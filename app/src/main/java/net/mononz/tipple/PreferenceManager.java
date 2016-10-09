package net.mononz.tipple;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceManager {

    private SharedPreferences pref;

    private static final String Preferences = "Preferences";
    private static final String LastUpdated = "last_updated";

    PreferenceManager(Context context) {
        pref = context.getSharedPreferences(Preferences, Context.MODE_PRIVATE);
    }

    // LastUpdated

    public long getLastUpdated() {
        return pref.getLong(LastUpdated, 0);
    }

    public void setLastUpdated(long timestamp) {
        Editor editor = pref.edit();
        editor.putLong(LastUpdated, timestamp);
        editor.apply();
    }

}