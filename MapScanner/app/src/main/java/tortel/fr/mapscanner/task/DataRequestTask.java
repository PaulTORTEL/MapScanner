package tortel.fr.mapscanner.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import tortel.fr.mapscanner.listener.IDataRequester;
import tortel.fr.mapscanner.manager.RequestManager;
import tortel.fr.mapscanner.util.RequestUtil;

public class DataRequestTask extends AsyncTask<Void, Void, Void> {

    private IDataRequester callback;
    private Context context;

    public DataRequestTask(IDataRequester callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, RequestUtil.getUri("venues", "search"), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("paull", response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("paull", "error : " + error.getMessage());
                       // callback.onRequestFailure("ERROR VOLLEY: " + error.getMessage() + " " + error.toString(), requestParam.getCategory());
                    }
                });

        RequestManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
        return null;
    }
}
