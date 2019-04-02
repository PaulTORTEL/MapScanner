package tortel.fr.mapscanner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import tortel.fr.mapscanner.bean.HoursVenue;
import tortel.fr.mapscanner.bean.ImageVenue;
import tortel.fr.mapscanner.handler.PhotosHandler;
import tortel.fr.mapscanner.handler.VenuesHandler;
import tortel.fr.mapscanner.listener.IHoursDatabaseListener;
import tortel.fr.mapscanner.listener.IImageDatabaseListener;
import tortel.fr.mapscanner.manager.ClientManager;
import tortel.fr.mapscanner.manager.DatabaseManager;
import tortel.fr.mapscanner.task.DataRequestTask;
import tortel.fr.mapscanner.task.ImageRequestTask;
import tortel.fr.mapscannerlib.ApiResponse;
import tortel.fr.mapscannerlib.Filter;
import tortel.fr.mapscannerlib.MessageUtils;

public class MapScannerService extends Service implements IImageDatabaseListener, IHoursDatabaseListener {

    private Messenger serviceMessenger;

    public MapScannerService() {
    }

    static class IncomingHandler extends Handler {
        private Context applicationContext;
        private MapScannerService mapScannerService;

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
            mapScannerService = (MapScannerService) context;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageUtils.REGISTER_CLIENT_MSG:
                    try {
                        ClientManager.getInstance().addClient(msg.replyTo);
                    } catch (ClientManager.ClientException e) {
                        Log.e("error", e.getMessage());
                    }

                    break;
                case MessageUtils.UNREGISTER_CLIENT_MSG:
                    try {
                        ClientManager.getInstance().removeClient(msg.replyTo);
                    } catch (ClientManager.ClientException e) {
                        Log.e("error", e.getMessage());
                    }

                    break;
                case MessageUtils.VENUES_MSG:
                    Bundle bundle = msg.getData();
                    Filter f = (Filter) bundle.getSerializable("filter");
                    if (f.getEndpoint().equals("hours")) {
                        DatabaseManager.getInstance().getHoursVenue(mapScannerService, applicationContext, f, msg);
                    } else {
                        mapScannerService.performDataRequest(f, msg.replyTo);
                    }
                    break;
                case MessageUtils.PHOTOS_MSG:
                    Bundle imgBundle = msg.getData();
                    Filter photoFilter = (Filter) imgBundle.getSerializable("filter");

                    DatabaseManager.getInstance().getImageVenue(mapScannerService, applicationContext, photoFilter, msg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        serviceMessenger = new Messenger(new IncomingHandler(this));
        return serviceMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private boolean isDataObsolete(long timestamp) {
        Calendar now = Calendar.getInstance();
        // Obsolete when > 24 hours
        return (now.getTimeInMillis() > (timestamp + (1000 * 60 * 60 * 24)));
    }

    private void performDataRequest(Filter filter, Messenger replyTo) {
        DataRequestTask task = new DataRequestTask(new VenuesHandler(replyTo, filter.getEndpoint(), filter.getGroupId(), getApplicationContext()), getApplicationContext());
        task.execute(filter);
    }

    private void perfomPhotoRequest(Filter photoFilter, Messenger replyTo) {
        DataRequestTask imgTask = new DataRequestTask(new PhotosHandler(replyTo, getApplicationContext(), photoFilter.getGroupId()), getApplicationContext());
        imgTask.execute(photoFilter);
    }

    @Override
    public void onImageVenueFetched(ImageVenue imageVenue, Filter filter, Messenger replyTo) {
        if (imageVenue != null) {
            if (isDataObsolete(imageVenue.getTimestamp())) {
                // Obsolete data
                DatabaseManager.getInstance().deleteImageVenue(this, getApplicationContext(), imageVenue);
                perfomPhotoRequest(filter, replyTo);
            } else {
                // Fetched data from the database
                new PhotosHandler(replyTo, getApplicationContext(), filter.getGroupId()).startImageRequestTask(imageVenue.getUrl());
            }
        } else {
            // No data
            perfomPhotoRequest(filter, replyTo);
        }
    }

    @Override
    public void onImageVenueSaved() {
        // Nothing to do yet
    }

    @Override
    public void onImageVenueDeleted() {
        // Nothing to do yet
    }

    @Override
    public void onImageVenueUpdated() {
        // Nothing to do yet
    }


    @Override
    public void onHoursVenueFetched(HoursVenue hoursVenue, Filter filter, Messenger replyTo) {
        if (hoursVenue != null) {
            if (isDataObsolete(hoursVenue.getTimestamp())) {
                // Obsolete data
                DatabaseManager.getInstance().deleteHoursVenue(this, getApplicationContext(), hoursVenue);
                performDataRequest(filter, replyTo);
            } else {
                // Fetched data from the database
                ApiResponse apiResponse = new ApiResponse();
                apiResponse.setRequestId("N/A");
                apiResponse.setEndpoint(filter.getEndpoint());
                apiResponse.setCode(200);
                JSONObject payload = new JSONObject();
                try {
                    if (!hoursVenue.getHours().isEmpty()) {
                        JSONObject hours = new JSONObject(hoursVenue.getHours());
                        payload.put("hours", hours.getJSONArray("timeframes"));
                    }
                    if (!hoursVenue.getPopularHours().isEmpty()) {
                        JSONObject popularHours = new JSONObject(hoursVenue.getPopularHours());
                        payload.put("popular_hours", popularHours.getJSONArray("timeframes"));
                    }

                    apiResponse.setPayload(payload.toString());
                    new VenuesHandler(replyTo, filter.getEndpoint(), filter.getGroupId(), getApplicationContext()).sendToClient("venues", apiResponse, MessageUtils.VENUES_MSG);

                } catch (JSONException e) {
                    Log.e("error", e.getMessage());
                }
            }
        } else {
            // No data
            performDataRequest(filter, replyTo);
        }
    }

    @Override
    public void onHoursVenueSaved() {
        // Nothing to do yet
    }

    @Override
    public void onHoursVenueDeleted() {
        // Nothing to do yet
    }

    @Override
    public void onHoursVenueUpdated() {
        // Nothing to do yet
    }
}
