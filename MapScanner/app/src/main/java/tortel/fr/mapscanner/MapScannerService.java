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

import tortel.fr.mapscanner.handler.VenuesHandler;
import tortel.fr.mapscanner.manager.ClientManager;
import tortel.fr.mapscanner.task.DataRequestTask;
import tortel.fr.mapscannerlib.Filter;
import tortel.fr.mapscannerlib.MessageUtils;

public class MapScannerService extends Service {

    private Messenger serviceMessenger;

    public MapScannerService() {
    }

    static class IncomingHandler extends Handler {
        private Context applicationContext;

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageUtils.REGISTER_CLIENT_MSG:
                    try {
                        ClientManager.getInstance().addClient(msg.replyTo);
                        DataRequestTask task = new DataRequestTask(new VenuesHandler(msg.replyTo), applicationContext);
                        Bundle bundle = msg.getData();
                        task.execute((Filter) bundle.getSerializable("filter"));

                    } catch (ClientManager.ClientException e) {
                        Log.d("error", e.getMessage());
                    }

                    break;
                case MessageUtils.UNREGISTER_CLIENT_MSG:
                    try {
                        ClientManager.getInstance().removeClient(msg.replyTo);
                    } catch (ClientManager.ClientException e) {
                        Log.d("error", e.getMessage());
                    }

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
}