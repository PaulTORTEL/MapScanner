package tortel.fr.mapscannerclient.util;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

import java.util.Map;

import tortel.fr.mapscannerlib.Filter;

public class MessageUtil {

    public static Message makeMessage(int type, String group, String endpoint, String groupId, Messenger clientMessenger, Map<String, String> params) {
        Message msg = Message.obtain(null, type);
        Bundle bundle = new Bundle();
        Filter filter = new Filter();
        filter.setGroup(group);
        filter.setGroupId(groupId);
        filter.setEndpoint(endpoint);
        filter.setParams(params);

        bundle.putSerializable("filter", filter);
        msg.setData(bundle);
        msg.replyTo = clientMessenger;

        return msg;
    }
}
