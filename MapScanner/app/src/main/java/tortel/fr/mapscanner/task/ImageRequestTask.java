package tortel.fr.mapscanner.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import tortel.fr.mapscanner.listener.IPictureHandler;
import tortel.fr.mapscanner.manager.RequestManager;

public class ImageRequestTask extends AsyncTask<String, Void, Void> {

    private IPictureHandler callback;
    private Context context;

    public ImageRequestTask(IPictureHandler callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected Void doInBackground(final String[] urls) {

        ImageRequest imageRequest = new ImageRequest(urls[0],
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Bitmap compressedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, true);
                        Log.d("paull", "image IS CACHED: " +
                                RequestManager.getInstance(context).getImageLoader().isCached(urls[0], 0, 0));


                        callback.onPictureDownloaded(compressedBitmap);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        callback.onPictureDownloadFailed("Image download failed");
                        Log.e("error", "ERROR VOLLEY: " + error.getMessage());
                    }
                });

        RequestManager.getInstance(context).addToRequestQueue(imageRequest);
        return null;
    }
}
