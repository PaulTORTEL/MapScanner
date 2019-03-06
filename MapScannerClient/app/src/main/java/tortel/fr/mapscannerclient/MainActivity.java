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
import android.widget.Toast;

import tortel.fr.mapscannerlib.MessageUtils;

public class MainActivity extends AppCompatActivity {

    private Messenger mapScannerService = null;
    private boolean bound;

    private Messenger clientMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clientMessenger = new Messenger(new IncomingHandler(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent("map.scanner.service.intent");
        intent.setComponent(new ComponentName("tortel.fr.mapscanner", "tortel.fr.mapscanner.MSService"));
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
        private Context applicationContext;

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(applicationContext, "hello!", Toast.LENGTH_SHORT).show();
                    //  msg.replyTo.send();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }




}
