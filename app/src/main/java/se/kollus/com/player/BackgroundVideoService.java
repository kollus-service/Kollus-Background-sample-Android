package se.kollus.com.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.kollus.sdk.media.KollusStorage;
import com.kollus.sdk.media.util.Utils;

import se.kollus.com.KollusSampleApplication;
import se.kollus.com.ui.notification.NotificationPlayer;
import se.kollus.com.utils.LogUtil;

public class BackgroundVideoService extends Service {

    private static final String TAG = BackgroundVideoService.class.getSimpleName();

    private final IBinder mBinder = new ServiceBinder();
    private CustomPlayer mPlayer;
    private KollusStorage mStorage = null;
    private NotificationPlayer mNotificationPlayer = null;
    private Context mContext = null;


    @Override
    public void onCreate() {
        LogUtil.d(TAG, "onCreate");
        super.onCreate();
        mContext = KollusSampleApplication.getAppContext();
        init();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        finalizeService();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        initStorage();
        mNotificationPlayer = new NotificationPlayer(this);
    }

    private void initStorage() {
        LogUtil.d(TAG, "initStorage");
        mStorage = KollusStorage.getInstance(mContext);
        LogUtil.d(TAG, mStorage.getVersion());

        if (!mStorage.isReady()) {
            int errorCode = mStorage.initialize(KollusConstant.KEY, KollusConstant.EXPIRE_DATE, mContext.getPackageName());
            int nRet = mStorage.setDevice(Utils.getStoragePath(mContext), Utils.createUUIDSHA1(mContext),
                    Utils.createUUIDMD5(mContext), Utils.isTablet(mContext));

            LogUtil.d(TAG, "KollusStorage version : " + mStorage.getVersion());
            LogUtil.d(TAG, "KollusStorage init errorCode : " + errorCode);
            LogUtil.d(TAG, "KollusStorage setDevice : " + nRet);
        }

        mStorage.setNetworkTimeout(KollusConstant.NETWORK_TIMEOUT_SEC, KollusConstant.NETWORK_RETRY_COUNT);
        mPlayer = new CustomPlayer(mContext);
    }

    private void finalizeService() {
        mStorage = null;
        mPlayer = null;
    }

    private void updateNotificationPlayer() {
        LogUtil.d(TAG, "updateNotificationPlayer");
        if (mNotificationPlayer != null) {
            mNotificationPlayer.updateNotificationPlayer();
        }
    }

    private void removeNotificationPlayer() {
        LogUtil.d(TAG, "updateNotificationPlayer");
        if (mNotificationPlayer != null) {
            mNotificationPlayer.removeNotificationPlayer();
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;

        String action = intent.getAction();
        if (NotificationPlayer.CommandActions.TOGGLE_PLAY.equals(action)) {
            if (mPlayer.isPlaying()) {
                pauseVideo();
            } else {
                playVideo();
            }
            updateNotificationPlayer();
        } else if (NotificationPlayer.CommandActions.REWIND.equals(action)) {
            //playPrevious();
        } else if (NotificationPlayer.CommandActions.FORWARD.equals(action)) {
            //playNext();
        } else if (NotificationPlayer.CommandActions.CLOSE.equals(action)) {
            stopPlayer();
            removeNotificationPlayer();
        } else if (NotificationPlayer.CommandActions.BACK_GROUND_ON.equals(action)) {
            //isStarting = true;
            updateNotificationPlayer();
        } else if (NotificationPlayer.CommandActions.BACK_GROUND_OFF.equals(action)) {
            removeNotificationPlayer();
        }
    }

    private void playVideo() {
        if (mPlayer != null) {
            mPlayer.start();
            //isStarting = true;
        }
    }

    private void pauseVideo() {
        if (mPlayer != null) {
            mPlayer.pause();
            //isStarting = false;
        }
    }

    private void resumeVideo() {
        if (mPlayer != null) {
            mPlayer.start();
        }
    }

    private void playNext() {
        // Need implementation
    }

    private void playPrevious() {
        // Need implementation
    }

    private void seekVideo(int seekTo) {
        // Need implementation
    }

    private void stopPlayer() {
        if (mPlayer != null) {
            mPlayer.pause();
            mPlayer.release();
        }
        mPlayer = null;
    }

    public CustomPlayer getPlayer() {
        return mPlayer;
    }

    public class ServiceBinder extends Binder {
        public BackgroundVideoService getService() {
            return BackgroundVideoService.this;
        }
    }
}
