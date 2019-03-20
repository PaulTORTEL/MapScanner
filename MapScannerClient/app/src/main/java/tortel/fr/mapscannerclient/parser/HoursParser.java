package tortel.fr.mapscannerclient.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tortel.fr.mapscannerclient.bean.WeekHours;
import tortel.fr.mapscannerlib.ApiResponse;

public class HoursParser {

    public static WeekHours parse(ApiResponse response) {
        WeekHours weekHours = new WeekHours();

        try {
            JSONObject payload = new JSONObject(response.getPayload());

            // HOURS
           parseHours(weekHours, "hours", payload);

           // POPULAR HOURS
           parseHours(weekHours, "popular_hours", payload);

        } catch (JSONException e) {
            Log.e("error", e.toString());
        }

        return weekHours;
    }

    private static void parseHours(WeekHours weekHours, String type, JSONObject payload) throws JSONException {

        if (!payload.has(type)) {
            return;
        }

        JSONArray hours = payload.getJSONArray(type);

        for (int i = 0; i < hours.length(); i++) {

            JSONObject item = hours.getJSONObject(i);
            JSONArray day = item.getJSONArray("days");
            JSONArray open = item.getJSONArray("open");
            StringBuilder fullHourBuilder = new StringBuilder();

            for (int slice = 0; slice < open.length(); slice++) {
                JSONObject dayHours = open.getJSONObject(slice);

                StringBuilder startHours = new StringBuilder(dayHours.getString("start").replaceAll("\\+", ""));
                startHours.insert(2, ':');
                startHours.append("-");

                StringBuilder endHours = new StringBuilder(dayHours.getString("end").replaceAll("\\+", ""));
                endHours.insert(2, ':');

                if (slice + 1 < open.length()) {
                    endHours.append(",");
                    endHours.append("\n");
                }

                startHours.append(endHours.toString());
                fullHourBuilder.append(startHours.toString());
            }

            for (int j = 0; j < day.length(); j++) {

                if (type.equals("hours")) {
                    weekHours.getRegularHours().put(day.getInt(j), fullHourBuilder.toString());
                } else if (type.equals("popular_hours")) {
                    weekHours.getPopularHours().put(day.getInt(j), fullHourBuilder.toString());
                }
            }
        }
    }
}
