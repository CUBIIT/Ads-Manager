package mediation.helper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceUtil {
    public static final String REVIEW = "review";

    public static void putSharedPreference(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void putSharedPreference(Context context, String key, long value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static Boolean getSharedPreference(Context context, String key, boolean _default) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, _default);
    }

    public static long getSharedPreference(Context context, String key, long _default) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(key, _default);
    }

}