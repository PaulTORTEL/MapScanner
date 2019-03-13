package tortel.fr.mapscanner.handler;

import android.os.Messenger;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tortel.fr.mapscannerlib.ApiResponse;
import tortel.fr.mapscannerlib.MessageUtils;

public class VenuesHandler extends DataHandler {

    public VenuesHandler(Messenger clientMessenger) {
        super(clientMessenger);
    }

    @Override
    public void onRequestSuccessful(JSONObject rawData) {
        ApiResponse response = trimPayload(rawData);
        sendToClient("venues", response, MessageUtils.VENUES_MSG);
    }

    @Override
    public void onRequestFailed(JSONObject rawData) {
        ApiResponse response = trimError(rawData);
        sendToClient("venues", response, MessageUtils.VENUES_MSG);
    }

    ApiResponse trimPayload(JSONObject rawData) {
        ApiResponse response = new ApiResponse();
        JSONObject payload = new JSONObject();

        try {
            JSONObject meta = rawData.getJSONObject("meta");
            response.setCode(meta.getInt("code"));
            response.setRequestId(meta.getString("requestId"));

            JSONObject resp = rawData.getJSONObject("response");

            payload.put("location", resp.get("headerFullLocation"));
            payload.put("suggestedRadius", resp.getInt("suggestedRadius"));
            payload.put("totalResults", resp.getInt("totalResults"));

            JSONArray groups = resp.getJSONArray("groups");
            payload.put("venue_list", groups.getJSONObject(0));

            response.setPayload(payload.toString());
        } catch (JSONException e) {
            Log.e("error", e.getMessage());
        }

        return response;
    }
}
