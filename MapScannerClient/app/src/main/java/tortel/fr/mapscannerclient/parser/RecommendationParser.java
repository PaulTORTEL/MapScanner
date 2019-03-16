package tortel.fr.mapscannerclient.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tortel.fr.mapscannerclient.bean.Place;
import tortel.fr.mapscannerlib.ApiResponse;

public class RecommendationParser {

    public static List<Place> parse(ApiResponse response) {
        List<Place> list = new ArrayList<>();

        try {
            JSONObject payload = new JSONObject(response.getPayload());
            JSONObject venues = payload.getJSONObject("venue_list");
            JSONArray items = venues.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                Place p = new Place();

                JSONObject item = items.getJSONObject(i);
                JSONObject venue = item.getJSONObject("venue");


                p.setName(venue.getString("name"));
                p.setId(venue.getString("id"));

                JSONObject location = venue.getJSONObject("location");

                p.setAddress(location.has("address") ? location.getString("address") : "");
                p.setCity(location.has("city") ? location.getString("city") : "");
                p.setDistance(location.has("distance") ? location.getInt("distance") : -1);
                p.setCountry(location.has("country") ? location.getString("country") : "");


                list.add(p);
            }
        } catch (JSONException e) {
            Log.e("error", e.toString());
        }

        return list;
    }
}
