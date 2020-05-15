package se.kollus.com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String TAG = "Preferences";
    private static final String NAME = "kollus";

    private static final String SETTING_BACKGROUND_VIDEO = "setting_background_video";

    private static Context mContext = null;

    public static void init(Context context) {
        mContext = context;
    }

    private static SharedPreferences getPreferences() {
        if (mContext == null) {
            LogUtil.e(TAG, "error preferences not init");
            return null;
        }

        return mContext.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
    }

    private static void putString(String key, String value) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            LogUtil.e(TAG, "error Preferences not init putString key : " + key + " value : " + value);
            return;
        }

        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.apply();
    }

    private static void putBoolean(String key, boolean value) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            LogUtil.e(TAG, "error Preferences not init putString key : " + key + " value : " + value);
            return;
        }

        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);
        editor.apply();
    }

    private static String getString(String key) {
        return getString(key, "");
    }

    private static String getString(String key, String defValue) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            LogUtil.e(TAG, "error Preferences not init getString key : " + key);
            return defValue;
        }

        return pref.getString(key, defValue);
    }

    private static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    private static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            LogUtil.e(TAG, "error Preferences not init getString key : " + key);
            return defValue;
        }
        return pref.getBoolean(key, defValue);
    }

    public static void setBackgroundVideo(boolean value) {
        putBoolean(SETTING_BACKGROUND_VIDEO, value);
    }

    public static boolean isBackgroundVideo() {
        return getBoolean(SETTING_BACKGROUND_VIDEO, false);
    }
}
