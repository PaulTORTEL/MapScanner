package tortel.fr.mapscannerclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import tortel.fr.mapscannerlib.ApiResponse;
import tortel.fr.mapscannerlib.Filter;
import tortel.fr.mapscannerlib.MessageUtils;

public class MainActivity extends AppCompatActivity {

    private Messenger mapScannerService = null;
    private boolean bound;

    private Messenger clientMessenger;

    private TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clientMessenger = new Messenger(new IncomingHandler(this));
        test = this.findViewById(R.id.test);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent("map.scanner.service.intent");
        intent.setComponent(new ComponentName("tortel.fr.mapscanner", "tortel.fr.mapscanner.MapScannerService"));
        bindService(intent, mapScannerConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (bound) {
            if (mapScannerService != null) {
                Message msg = Message.obtain(null, MessageUtils.UNREGISTER_CLIENT_MSG);
                msg.replyTo = clientMessenger;

                try {
                    mapScannerService.send(msg);
                } catch (RemoteException e) {
                    Log.e("error", e.getMessage());
                }
            }
            unbindService(mapScannerConnection);
            bound = false;
        }
    }


    private ServiceConnection mapScannerConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mapScannerService = new Messenger(service);
            bound = true;
            Message msg = Message.obtain(null, MessageUtils.REGISTER_CLIENT_MSG);

            /*TODO: TO REMOVE */
            Message msg2 = Message.obtain(null, MessageUtils.VENUES_MSG);
            Bundle b = new Bundle();
            Filter f = new Filter();
            f.setGroup("venues");
            f.setEndpoint("explore");

            HashMap<String, String> map = new HashMap<>();
            map.put("limit", "20");
            map.put("ll", "51.903614,-8.468399");
            map.put("query", "coffee");

            f.setParams(map);

            b.putSerializable("filter", f);
            msg2.setData(b);


            Message msg3 = Message.obtain(null, MessageUtils.PHOTOS_MSG);
            Bundle b2 = new Bundle();
            Filter f2 = new Filter();
            f2.setGroup("venues");
            f2.setGroupId("4b4a936cf964a520f18a26e3");
            f2.setEndpoint("photos");

            b2.putSerializable("filter", f2);
            msg3.setData(b2);

            Message msg4 = Message.obtain(null, MessageUtils.VENUES_MSG);
            Bundle b3 = new Bundle();
            Filter f3 = new Filter();
            f3.setGroup("venues");
            f3.setGroupId("4b4a936cf964a520f18a26e3");
            f3.setEndpoint("hours");

            b3.putSerializable("filter", f3);
            msg4.setData(b3);
            /***/

            msg.replyTo = clientMessenger;
            msg2.replyTo = clientMessenger;
            msg3.replyTo = clientMessenger;
            msg4.replyTo = clientMessenger;
            try {
                mapScannerService.send(msg);
                mapScannerService.send(msg2);
                mapScannerService.send(msg3);
                mapScannerService.send(msg4);
            } catch (RemoteException e) {
                Log.e("error", e.getMessage());
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mapScannerService = null;
            bound = false;
        }
    };


    static class IncomingHandler extends Handler {
        private MainActivity mainActivity;

        IncomingHandler(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageUtils.VENUES_MSG:
                    TextView test = mainActivity.findViewById(R.id.test);
                    TextView testHours = mainActivity.findViewById(R.id.testHours);
                    Bundle b = msg.getData();
                    ApiResponse resp = (ApiResponse) b.getSerializable("venues");

                    StringBuilder sb = new StringBuilder("" + resp.getCode());
                    sb.append("\n");

                    try {

                        if (resp.getEndpoint().equals("explore")) {
                            JSONObject payload = new JSONObject(resp.getPayload());
                            JSONObject venues = payload.getJSONObject("venue_list");
                            JSONArray items = venues.getJSONArray("items");

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                JSONObject venue = item.getJSONObject("venue");
                                sb.append(venue.getString("name"));
                                sb.append("\n");
                            }
                            test.setText(sb.toString());
                        } else if (resp.getEndpoint().equals("hours")) {
                            JSONObject payload = new JSONObject(resp.getPayload());
                            JSONArray hours = payload.getJSONArray("hours");
                            StringBuilder sb2 = new StringBuilder();
                            for (int i = 0; i < hours.length(); i++) {
                                JSONObject item = hours.getJSONObject(i);
                                JSONArray day = item.getJSONArray("days");
                                JSONArray open = item.getJSONArray("open");
                                JSONObject dayHours = open.getJSONObject(0);

                                sb2.append("Day: ");
                                sb2.append(day.getInt(0));
                                sb2.append(" || Hours: ");
                                sb2.append(dayHours.getString("start"));
                                sb2.append(" -> ");
                                sb2.append(dayHours.getString("end"));
                                sb2.append("\n");
                            }
                            testHours.setText(sb2.toString());
                        }

                    } catch (JSONException e) {
                        Log.e("error", e.toString());
                    }

                    break;

                case MessageUtils.PHOTOS_MSG:
                    ImageView img = mainActivity.findViewById(R.id.testImg);
                    Bundle b2 = msg.getData();
                    ApiResponse resp2 = (ApiResponse) b2.getSerializable("photos");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(resp2.getBitmap(), 0, resp2.getBitmap().length);
                    img.setImageBitmap(bitmap);
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
