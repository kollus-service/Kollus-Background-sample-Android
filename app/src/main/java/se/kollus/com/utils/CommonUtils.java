package se.kollus.com.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();

    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    public static int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }

    public static void setStreamVolume(Context context, boolean isVolumeUp) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (isVolumeUp) {
            manager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE, AudioManager.RINGER_MODE_SILENT);
        } else {
            manager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER, AudioManager.RINGER_MODE_SILENT);
        }
    }

    public static int getStreamVolume(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static boolean isRunningService(Context context, Class<?> cls) {
        boolean isRunning = false;

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (info != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : info) {
                ComponentName compName = serviceInfo.service;
                String className = compName.getClassName();
                if (className.equals(cls.getName())) {
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }

    public static String createUrl(String cuid, String mck, boolean isSiType) throws NoSuchAlgorithmException, InvalidKeyException, JSONException {
        String playUri = "";
        String strToken = "";

        JSONObject mcObject = new JSONObject();
        mcObject.put("mckey", mck);

        JSONArray mcArray = new JSONArray();
        mcArray.put(mcObject);

        JSONObject payload = new JSONObject();
        payload.put("expt", System.currentTimeMillis() + 3600);
        payload.put("cuid", cuid);
        payload.put("mc", mcArray);

        JwtUtil jwtUtil = new JwtUtil();
        strToken = jwtUtil.createJwt(payload.toString(), Config.SECURITY_KEY);

        if (isSiType) {
            playUri = String.format("https://v.kr.kollus.com/si?jwt=%s&custom_key=%s&purge_cache", strToken, Config.CUSTOM_KEY);
        } else {
            playUri = String.format("https://v.kr.kollus.com/s?jwt=%s&custom_key=%s&purge_cache", strToken, Config.CUSTOM_KEY);
        }
        return playUri;
    }

    public static void startKollusApp(Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.kollus.media");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startKollusApp(Context context, String url) {
        try {
            String scheme = "kollus://path" + "?url=" + url;
            LogUtil.d(TAG, scheme);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.kollus.media")));
        }
    }
}
