package tortel.fr.mapscanner.handler;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import tortel.fr.mapscanner.listener.IDataHandler;
import tortel.fr.mapscannerlib.ApiResponse;

public abstract class DataHandler implements IDataHandler {

    private Messenger clientMessenger;

    public DataHandler(Messenger clientMessenger) {
        this.clientMessenger = clientMessenger;
    }

    public ApiResponse trimError(JSONObject rawData) {
        ApiResponse response = new ApiResponse();

        try {
            JSONObject meta = rawData.getJSONObject("meta");
            response.setCode(meta.getInt("code"));
            response.setRequestId(meta.getString("requestId"));
            response.setErrorDetail(meta.getString("errorDetail"));

        } catch (JSONException e) {
            Log.e("error", e.getMessage());
        }

        return response;
    }

    public abstract ApiResponse trimPayload(JSONObject rawData);

    public void sendToClient(final String key, final ApiResponse response, final int what) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, response);
        Message message = new Message();

        message.what = what;
        message.setData(bundle);

        try {
            clientMessenger.send(message);
        } catch (RemoteException e) {
            Log.e("error", e.getMessage());
        }
    }
}
