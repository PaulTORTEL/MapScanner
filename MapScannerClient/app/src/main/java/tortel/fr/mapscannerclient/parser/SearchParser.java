package tortel.fr.mapscannerclient.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tortel.fr.mapscannerclient.bean.Place;
import tortel.fr.mapscannerlib.ApiResponse;

public class SearchParser {
    public static List<Place> parse(ApiResponse response) {
        List<Place> list = new ArrayList<>();

        try {
            JSONObject payload = new JSONObject(response.getPayload());
            JSONArray venues = payload.getJSONArray("venues");

            for (int i = 0; i < venues.length(); i++) {
                Place p = new Place();

                JSONObject venue = venues.getJSONObject(i);

                p.setName(venue.getString("name"));
                p.setId(venue.getString("id"));

                JSONObject location = venue.getJSONObject("location");

                p.setLat(location.getDouble("lat"));
                p.setLng(location.getDouble("lng"));
                p.setAddress(location.has("address") ? location.getString("address") : "");
                p.setCity(location.has("city") ? location.getString("city") : "");
                p.setDistance(location.has("distance") ? location.getInt("distance") : -1);
                p.setCountry(location.has("country") ? location.getString("country") : "");

                JSONArray categories = venue.getJSONArray("categories");
                p.setCategory(categories.getJSONObject(0).getString("name"));

                list.add(p);
            }
        } catch (JSONException e) {
            Log.e("error", e.toString());
        }

        return list;
    }
}
