package tortel.fr.mapscanner.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class RequestManager {

    private static RequestManager instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context context;

    public static final String clientID = "OCPQVLEMFICIKXWZAK2LX3KBOOSD2JPWCTGKTZFZDWUNGR33";
    public static final String clientSecret = "ACJGAL4JDM5SCJ3IRTXNEEL53QLUSRUQRKRDM3VGCSWH5J2G";
    public static final String baseUrl = "https://api.foursquare.com/v2/";

    private RequestManager(Context context) {
        this.context = context;

        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
            new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap>
                        cache = new LruCache<String, Bitmap>(20);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });
    }

    public static synchronized RequestManager getInstance(Context context) {

        if (instance == null) {
            instance = new RequestManager(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }


}
