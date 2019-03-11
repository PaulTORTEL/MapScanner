package tortel.fr.mapscannerclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
            msg.setData(b);
            /***/

            msg.replyTo = clientMessenger;
            try {
                mapScannerService.send(msg);
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
                case 0:
                    TextView test = mainActivity.findViewById(R.id.test);
                    Bundle b = msg.getData();
                    ApiResponse resp = (ApiResponse) b.getSerializable("venues");

                    StringBuilder sb = new StringBuilder("" + resp.getCode());
                    sb.append("\n");

                    try {
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
                    } catch (JSONException e) {
                        Log.e("error", e.toString());
                    }

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
