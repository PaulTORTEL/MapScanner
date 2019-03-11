package tortel.fr.mapscanner.listener;

import org.json.JSONObject;

public interface IDataHandler {

    void onRequestSuccessful(JSONObject rawData);
    void onRequestFailed(JSONObject rawData);
}
