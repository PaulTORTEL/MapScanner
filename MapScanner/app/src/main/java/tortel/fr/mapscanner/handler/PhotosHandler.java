package tortel.fr.mapscanner.handler;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Messenger;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import tortel.fr.mapscanner.bean.ImageVenue;
import tortel.fr.mapscanner.listener.IPictureHandler;
import tortel.fr.mapscanner.manager.DatabaseManager;
import tortel.fr.mapscanner.task.ImageRequestTask;
import tortel.fr.mapscannerlib.ApiResponse;
import tortel.fr.mapscannerlib.MessageUtils;

public class PhotosHandler extends DataHandler implements IPictureHandler {

    private Context context;
    private String placeId;

    public PhotosHandler(Messenger clientMessenger, Context context, String placeId) {
        super(clientMessenger);
        this.context = context;
        this.placeId = placeId;
    }

    @Override
    public void onRequestSuccessful(JSONObject rawData) {
        ApiResponse response = trimPayload(rawData);

        try {
            JSONObject payload = new JSONObject(response.getPayload());
            int width = payload.getInt("width");
            int height = payload.getInt("height");

            String url = payload.getString("prefix") + width + "x" + height + payload.getString("suffix");

            startImageRequestTask(url);
            ImageVenue imageVenue = new ImageVenue();
            imageVenue.setVenueId(placeId);
            imageVenue.setTimestamp(Calendar.getInstance().getTimeInMillis());
            imageVenue.setUrl(url);
            DatabaseManager.getInstance().insertImageVenue(null, context, imageVenue);

        } catch (JSONException e) {
            Log.e("error", "Error to create the JSON object based on the payload");
        }
    }

    public void startImageRequestTask(String url) {
        ImageRequestTask task = new ImageRequestTask(this, context);
        task.execute(url);
    }

    @Override
    public void onRequestFailed(JSONObject rawData) {
        ApiResponse response = trimError(rawData);
        sendToClient("photos", response, MessageUtils.PHOTOS_MSG);
    }

    @Override
    public void onPictureDownloaded(Bitmap bitmap) {
        ApiResponse response = new ApiResponse();
        response.setRequestId(placeId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        response.setBitmap(byteArray);

        sendToClient("photos", response, MessageUtils.PHOTOS_MSG);
    }

    @Override
    public void onPictureDownloadFailed(String error) {
        ApiResponse response = new ApiResponse();
        response.setErrorDetail("The photos has not been downloaded properly. An error has occured.");
        response.setCode(400);
        sendToClient("photos", response, MessageUtils.PHOTOS_MSG);
    }

    public ApiResponse trimPayload(JSONObject rawData) {
        ApiResponse response = new ApiResponse();
        JSONObject payload = new JSONObject();

        try {
            JSONObject meta = rawData.getJSONObject("meta");
            response.setCode(meta.getInt("code"));
            response.setRequestId(meta.getString("requestId"));

            JSONObject resp = rawData.getJSONObject("response");
            JSONObject photos = resp.getJSONObject("photos");


            JSONArray photoArray = photos.getJSONArray("items");
            JSONObject item = photoArray.getJSONObject(0);
            payload.put("id", item.getString("id"));
            payload.put("prefix", item.getString("prefix"));
            payload.put("suffix", item.getString("suffix"));
            payload.put("width", item.getInt("width"));
            payload.put("height", item.getInt("height"));
            payload.put("tip", item.getJSONObject("tip"));

        } catch (JSONException e) {
            Log.e("error", e.getMessage());
        }
        response.setPayload(payload.toString());

        return response;
    }
}
