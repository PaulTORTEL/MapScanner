package tortel.fr.mapscanner.handler;

import android.content.Context;
import android.os.Messenger;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import tortel.fr.mapscanner.bean.HoursVenue;
import tortel.fr.mapscanner.manager.DatabaseManager;
import tortel.fr.mapscannerlib.ApiResponse;
import tortel.fr.mapscannerlib.MessageUtils;

public class VenuesHandler extends DataHandler {

    private String endpoint;
    private Context context;
    private String placeId;

    public VenuesHandler(Messenger clientMessenger, final String endpoint, String placeId, Context context) {
        super(clientMessenger);
        this.endpoint = endpoint;
        this.context = context;
        this.placeId = placeId;
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

    public ApiResponse trimPayload(JSONObject rawData) {
        ApiResponse response = new ApiResponse();
        JSONObject payload = new JSONObject();

        try {
            JSONObject meta = rawData.getJSONObject("meta");
            response.setCode(meta.getInt("code"));
            response.setRequestId(meta.getString("requestId"));

            JSONObject resp = rawData.getJSONObject("response");

            if (endpoint.equals("explore")) {
                payload.put("location", resp.getString("headerFullLocation"));
                if (resp.has("suggestedRadius")) {
                    payload.put("suggestedRadius", resp.getInt("suggestedRadius"));
                }

                payload.put("totalResults", resp.getInt("totalResults"));

                JSONArray groups = resp.getJSONArray("groups");
                payload.put("venue_list", groups.getJSONObject(0));
            } else if (endpoint.equals("hours")) {
                HoursVenue hoursVenue = new HoursVenue();
                hoursVenue.setHours("");
                hoursVenue.setPopularHours("");
                hoursVenue.setTimestamp(Calendar.getInstance().getTimeInMillis());
                hoursVenue.setVenueId(placeId);
                if (resp.has("hours")) {
                    JSONObject hours = resp.getJSONObject("hours");
                    if (hours.has("timesframes")) {
                        payload.put("hours", hours.getJSONArray("timeframes"));
                        hoursVenue.setHours(hours.toString());
                    }
                }

                if (resp.has("popular")) {
                    JSONObject popular = resp.getJSONObject("popular"); // ranking of the hours of frequentation
                    if (popular.has("timeframes")) {
                        payload.put("popular_hours", popular.getJSONArray("timeframes"));
                        hoursVenue.setPopularHours(popular.toString());
                    }
                }

                if (!hoursVenue.getHours().isEmpty() || !hoursVenue.getPopularHours().isEmpty()) {
                    DatabaseManager.getInstance().insertHoursVenue(null, context, hoursVenue);
                }
            } else if (endpoint.equals("search")) {
                payload.put("venues", resp.getJSONArray("venues"));
            }

            response.setPayload(payload.toString());
            response.setEndpoint(endpoint);
        } catch (JSONException e) {
            Log.e("error", e.getMessage());
        }

        return response;
    }
}
