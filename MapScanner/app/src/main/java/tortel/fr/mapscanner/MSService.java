package tortel.fr.mapscanner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import tortel.fr.mapscanner.manager.ClientManager;
import tortel.fr.mapscanner.task.DataRequestTask;
import tortel.fr.mapscannerlib.MessageUtils;

public class MSService extends Service {

    private Messenger serviceMessenger;

    public MSService() {
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
                        Toast.makeText(applicationContext, "There are " + ClientManager.getInstance().getClients().size() + " clients", Toast.LENGTH_SHORT).show();
                        msg.replyTo.send(Message.obtain(null, 0));

                        DataRequestTask task = new DataRequestTask(null, applicationContext);
                        task.execute();

                    } catch (ClientManager.ClientException | RemoteException e) {
                        Log.d("error", e.getMessage());
                    }

                    break;
                case MessageUtils.UNREGISTER_CLIENT_MSG:
                    try {
                        ClientManager.getInstance().removeClient(msg.replyTo);
                        Toast.makeText(applicationContext, "There are " + ClientManager.getInstance().getClients().size() + " clients", Toast.LENGTH_SHORT).show();
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
