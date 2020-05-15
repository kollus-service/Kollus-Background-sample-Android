package se.kollus.com.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import se.kollus.com.KollusSampleApplication;
import se.kollus.com.ui.notification.NotificationPlayer;
import se.kollus.com.utils.CommonUtils;
import se.kollus.com.utils.LogUtil;

public class PlayerManager {

    private static final String TAG = PlayerManager.class.getSimpleName();

    private static PlayerManager mInstance;
    private BackgroundVideoService mVideoService;
    private Context mContext;

    private boolean isBind;

    public static PlayerManager getInstance() {
        if (mInstance == null) {
            mInstance = new PlayerManager();
        }
        return mInstance;
    }

    public PlayerManager() {
        mContext = KollusSampleApplication.getAppContext();
    }

    public boolean init() {
        if (mContext != null && (!CommonUtils.isRunningService(mContext, BackgroundVideoService.class) || !isBind)) {
            LogUtil.d(TAG, "PlayerManager init");
            mContext.startService(new Intent(mContext, BackgroundVideoService.class));
            return mContext.bindService(new Intent(mContext, BackgroundVideoService.class), mServiceConn, Context.BIND_ADJUST_WITH_ACTIVITY);
        }
        return false;
    }

    public void terminate() {
        if (mContext != null && CommonUtils.isRunningService(mContext, BackgroundVideoService.class)) {
            isBind = false;
            LogUtil.d(TAG, "PlayerManager terminate : " + mContext.stopService(new Intent(mContext, BackgroundVideoService.class)));
        }
        mInstance = null;
    }

    public void startBackgroundService() {
        LogUtil.d(TAG, "startBackgroundService");
        Intent intent = new Intent(mContext, BackgroundVideoService.class);
        intent.setAction(NotificationPlayer.CommandActions.BACK_GROUND_ON);
        mContext.startService(intent);
    }

    public void stopBackgroundService() {
        LogUtil.d(TAG, "stopBackgroundService");
        Intent intent = new Intent(mContext, BackgroundVideoService.class);
        intent.setAction(NotificationPlayer.CommandActions.BACK_GROUND_OFF);
        mContext.startService(intent);
    }

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            LogUtil.d(TAG, "ServiceConnection");
            mVideoService = ((BackgroundVideoService.ServiceBinder) binder).getService();
            isBind = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            LogUtil.d(TAG, "onServiceDisconnected");
            mVideoService = null;
            isBind = false;
        }
    };

    public boolean isServiceBind() {
        return isBind;
    }

    public CustomPlayer getPlayer() {
        if (mVideoService != null) {
            LogUtil.d(TAG, "getPlayer");
            return mVideoService.getPlayer();
        }
        return null;
    }

    public boolean isStarting() {
        if (mVideoService != null && mVideoService.getPlayer() != null) {
            LogUtil.d(TAG, "isStarting");
            return mVideoService.getPlayer().isPlaying();
        }
        return false;
    }
}
