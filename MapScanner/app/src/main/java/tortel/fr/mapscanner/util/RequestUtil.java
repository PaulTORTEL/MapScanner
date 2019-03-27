package tortel.fr.mapscanner.util;

import java.util.Map;

import tortel.fr.mapscanner.manager.RequestManager;
import tortel.fr.mapscannerlib.Filter;

public final class RequestUtil {

    public static String getUri(Filter filter) {
        StringBuilder sb = new StringBuilder(RequestManager.baseUrl);
        sb.append(filter.getGroup());
        sb.append("/");

        if (filter.getGroupId() != null && !filter.getGroupId().isEmpty()) {
            sb.append(filter.getGroupId());
            sb.append("/");
        }

        sb.append(filter.getEndpoint());
        sb.append(formatCredentials());

        if (filter.getParams() != null) {
            for (Map.Entry<String, String> entry : filter.getParams().entrySet()) {
                sb.append("&");
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
            }
        }

        return sb.toString();
    }

    private static String formatCredentials() {
        StringBuilder sb = new StringBuilder("?");
        sb.append("client_id=");
        sb.append(CredentialsUtil.clientID);
        sb.append("&client_secret=");
        sb.append(CredentialsUtil.clientSecret);
        sb.append("&v="); // Versioning is necessary
        sb.append("20180323");
        return sb.toString();
    }
}
