package tortel.fr.mapscanner.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import tortel.fr.mapscanner.listener.IDataHandler;
import tortel.fr.mapscanner.manager.RequestManager;
import tortel.fr.mapscanner.util.RequestUtil;
import tortel.fr.mapscannerlib.Filter;

public class DataRequestTask extends AsyncTask<Filter, Void, Void> {

    private IDataHandler callback;
    private Context context;

    public DataRequestTask(IDataHandler callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Filter... filters) {

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, RequestUtil.getUri(filters[0]), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getJSONObject("meta").getInt("code") == 200) {
                                callback.onRequestSuccessful(response);
                            } else {
                                callback.onRequestFailed(response);
                            }
                        } catch (JSONException e) {
                            Log.e("error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "ERROR VOLLEY: " + error.getMessage());
                    }
                });

        RequestManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
        return null;
    }
}
