package tortel.fr.mapscanner.manager;

import android.os.Messenger;

import java.util.ArrayList;

public class ClientManager {
    private static final ClientManager instance = new ClientManager();

    private ArrayList<Messenger> clients = new ArrayList<>();

    public static synchronized ClientManager getInstance() {
        return instance;
    }

    private ClientManager() {
    }

    public ArrayList<Messenger> getClients() {
        return clients;
    }

    public void addClient(Messenger client) throws ClientException {
        operationClient(client, true);
    }

    public void removeClient(Messenger client) throws ClientException {
        operationClient(client, false);
    }

    private void operationClient(Messenger client, final boolean add) throws ClientException {
        if (client == null) {
            throw new ClientException("Client messenger cannot be null");
        }
        if (add) {
            this.getClients().add(client);
        } else {
            this.getClients().remove(client);
        }
    }


    /** EXCEPTIONS **/
    public class ClientException extends Exception {
        public ClientException(String errorMessage) {
            super(errorMessage);
        }
    }
}
