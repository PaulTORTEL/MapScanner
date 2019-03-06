package tortel.fr.mapscanner.util;

import android.util.Log;

import tortel.fr.mapscanner.manager.RequestManager;

public final class RequestUtil {

    public static String getUri(final String type, final String action) {
        StringBuilder sb = new StringBuilder(RequestManager.baseUrl);
        sb.append(type);
        sb.append("/");
        sb.append(action);
        sb.append(formatCredentials());
        sb.append("&ll=51.897830,-8.476329");
        Log.d("paull", "url : " + sb.toString());
        return sb.toString();
    }

    private static String formatCredentials() {
        StringBuilder sb = new StringBuilder("?");
        sb.append("client_id=");
        sb.append(RequestManager.clientID);
        sb.append("&client_secret=");
        sb.append(RequestManager.clientSecret);
        sb.append("&v="); // Versioning is necessary
        sb.append("20180323");
        return sb.toString();
    }
}
