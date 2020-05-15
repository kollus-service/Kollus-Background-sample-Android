package se.kollus.com.player;

import android.content.Context;

import com.kollus.sdk.media.util.Utils;

public class KollusConstant {
    public static final String KEY = "KOLLUS_KEY";
    public static final String EXPIRE_DATE = "KOLLUS_EXPIRE_DATE";
    public static final int ZAPPING_INTERVAL = 100;

    public static final int NETWORK_TIMEOUT_SEC = 10;
    public static final int NETWORK_RETRY_COUNT = 3;

    public static String getPlayerId(Context context) {
        String playerId;
        playerId = Utils.createUUIDSHA1(context);

        return playerId;
    }

    public static String getPlayerIdWithMD5(Context context) {
        String playerIdWidthMd5;
        playerIdWidthMd5 = Utils.createUUIDMD5(context);
        return playerIdWidthMd5;
    }
}
