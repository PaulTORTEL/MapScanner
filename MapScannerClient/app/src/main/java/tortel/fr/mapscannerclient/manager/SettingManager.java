package tortel.fr.mapscannerclient.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.TreeSet;

import tortel.fr.mapscannerclient.bean.QueryFilter;

public class SettingManager {
    private static final SettingManager instance = new SettingManager();
    private boolean init = false;
    private final String PREFERENCES_FILE_NAME = "PREF_MAP_SCANNER_CLIENT";

    private QueryFilter queryFilter;

    public static SettingManager getInstance() {
        return instance;
    }

    private SettingManager() {
    }

    public boolean isInit() {
        return init;
    }

    public QueryFilter getQueryFilter() {
        return queryFilter;
    }

    public void init(Activity activity) {
        if (isInit()) {
            return;
        }

        init = true;
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        queryFilter = new QueryFilter();

        queryFilter.setType(sharedPref.getInt("type", 0));
        queryFilter.setTags(sharedPref.getStringSet("tags", new TreeSet<String>()));
        queryFilter.setRadius(sharedPref.getInt("radius", 250));
    }

    public void savePreferences(Activity activity, QueryFilter filter) {

        SharedPreferences sharedPref = activity.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        this.queryFilter = filter;

        editor.putInt("type", filter.getType());
        editor.putInt("radius", filter.getRadius());
        editor.putStringSet("tags", filter.getTags());
        editor.apply();
    }

}
