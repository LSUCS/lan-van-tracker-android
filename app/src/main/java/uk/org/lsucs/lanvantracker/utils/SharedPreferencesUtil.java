package uk.org.lsucs.lanvantracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Zack on 11/10/2017.
 */

public class SharedPreferencesUtil {

    public static final String STORAGE_NAME = "NYANVAN_PREFS";

    public static final String SERVICE_RUNNING = "serviceRunning";
    public static final String AUTH_KEY = "authKey";

    public static void addKeyValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void addKeyValue(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORAGE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getStringValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORAGE_NAME, 0);
        return sharedPreferences.getString(key, "");
    }

    public static boolean getBooleanValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORAGE_NAME, 0);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void deleteKeyValue(Context context, String key) {
        context.getSharedPreferences(STORAGE_NAME, 0).edit().remove(key).apply();
    }
}
