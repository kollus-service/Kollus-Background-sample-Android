package se.kollus.com.player;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.kollus.sdk.media.KollusPlayerLMSListener;
import com.kollus.sdk.media.KollusStorage;
import com.kollus.sdk.media.MediaPlayer;
import com.kollus.sdk.media.content.KollusContent;

import se.kollus.com.utils.LogUtil;


public class CustomPlayer {
    private static final String TAG = CustomPlayer.class.getSimpleName();

    private Context mContext;
    private SurfaceView mSurfaceVew;
    private RelativeLayout mLayout;
    private VideoView mVideoView;
    private View mSurfaceView;
    private int mPlayType;
    private String mSourceUrl;
    private float mPlayingRate = 1.0f;
    private int mCurrentPlayAt = -1;

    public CustomPlayer(Context context) {
        LogUtil.d(TAG, "CustomPlayer()");
        this.mContext = context;
    }

    public CustomPlayer(Context context, SurfaceView surfaceView) {
        this.mContext = context;
        this.mSurfaceVew = surfaceView;

        initListener();
    }

    public CustomPlayer(Context context, RelativeLayout layout) {
        this.mContext = context;
        this.mLayout = layout;

        initPlayerLayer(layout);
        initListener();
    }

    private void initPlayerLayer(RelativeLayout layout) {
        mVideoView = new VideoView(mContext);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        mSurfaceView = new SurfaceView(mContext);
        mVideoView.addView(mSurfaceView, params);
        mVideoView.initVideoView((SurfaceView) mSurfaceView);

        layout.addView(mVideoView, params);
    }

    private void initListener() {
        mVideoView.setOnErrorListener(onErrorListener);
        mVideoView.setOnCompletionListener(onCompletionListener);
        mVideoView.setOnPreparedListener(onPreparedListener);
        mVideoView.setKollusPlayerLMSListener(kollusPlayerLMSListener);
    }

    public void setPlayerLayer(RelativeLayout layout) {
        LogUtil.d(TAG, "setPlayerLayer");
        initPlayerLayer(layout);
        initListener();
    }

    public void start() {
        LogUtil.d(TAG, "start");
        if (mVideoView != null) {
            mVideoView.start();
        }
    }

    public void prepareAsync() {
        if (mVideoView == null)
            return;

        if (TextUtils.isEmpty(mSourceUrl)) {
            LogUtil.d(TAG, "mSourceUrl is empty");
            return;
        }

        LogUtil.d(TAG, "mSourceUrl : " + mSourceUrl);

        if (mPlayType == 0) {
            mVideoView.setVideoURI(Uri.parse(mSourceUrl));
        } else {
            mVideoView.setVideoMCK(mSourceUrl);
        }
        mVideoView.stopPlayback();
        mVideoView.start();
    }

    public void pause() {
        LogUtil.d(TAG, "pause()");
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    public boolean setPlayingRate(int mode) {
        boolean result = false;

        switch (mode) {
            case -1:
                mPlayingRate -= 0.1f;
                break;
            case 1:
                mPlayingRate += 0.1f;
                break;
            default:
                mPlayingRate = 1;
                break;
        }

        if (mPlayingRate <= 0.5f) {
            mPlayingRate = 0.5f;
        }

        if (mPlayingRate >= 2.0f) {
            mPlayingRate = 2.0f;
        }

        if (mVideoView != null) {
            LogUtil.d(TAG, "mPlayingRate : " + mPlayingRate);
            result = mVideoView.setPlayingRate(mPlayingRate);
        }

        return result;
    }

    public void setVolumeLevel(int level) {
        if (mVideoView != null)
            mVideoView.setVolumeLevel(level);
    }

    public void setMute(boolean mute) {
        if (mVideoView != null)
            mVideoView.setMute(mute);
    }

    public void setFF() {
        if (mVideoView != null) {
            LogUtil.d(TAG, "setFF() : getCurrentPosition : " + mVideoView.getCurrentPosition());
            int sec = mVideoView.getCurrentPosition() + 10000;
            mVideoView.seekToExact(sec);
        }
    }

    public void setRW() {
        if (mVideoView != null) {
            LogUtil.d(TAG, "setRW() : getCurrentPosition : " + mVideoView.getCurrentPosition());
            int sec = mVideoView.getCurrentPosition() - 10000;
            mVideoView.seekToExact(sec);
        }
    }

    public int getPlayAt() {
        if (mVideoView != null) {
            LogUtil.d(TAG, "getPlayAt() : getPlayAt : " + mVideoView.getPlayAt());
            return mVideoView.getPlayAt();
        }
        return -1;
    }

    public int getCurrentPlayAt() {
        return mCurrentPlayAt;
    }

    public void setCurrentPlayAt(int playAt) {
        mCurrentPlayAt = playAt;
    }

    public boolean isPlaying() {
        if (mVideoView != null) {
            return mVideoView.isPlaying();
        }
        return false;
    }

    public void release() {
        LogUtil.d(TAG, "release()");
        if (mVideoView != null) {
            mVideoView.suspend();
        }
    }

    public void finish() {
        LogUtil.d(TAG, "finish()");
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    public void backGroundPlay() {
        mVideoView.setBackgroundStatus();
    }

    public SurfaceView getSurfaceVew() {
        return mSurfaceVew;
    }

    public String getSourceUrl() {
        return mSourceUrl;
    }

    public float getPlayingRate() {
        return mPlayingRate;
    }

    public int getCurrentPosition() {
        if (mVideoView != null) {
            return mVideoView.getCurrentPosition();
        }
        return 0;
    }

    public String getKollusMediaTitle() {
        KollusContent content = new KollusContent();
        if (mVideoView != null && mVideoView.getKollusContent(content)) {
            KollusStorage storage = KollusStorage.getInstance(mContext);
            storage.getKollusContent(content, content.getMediaContentKey());

            String cource = content.getCourse();
            String subcource = content.getSubCourse();
            String title;
            if (cource != null && cource.length() > 0) {
                if (subcource != null && subcource.length() > 0) {
                    title = cource + "(" + subcource + ")";
                } else {
                    title = cource;
                }
            } else {
                title = subcource;
            }
            return title;
        }
        return "";
    }

    public String getKollusMediaThumbnailPath() {
        KollusContent content = new KollusContent();
        if (mVideoView != null && mVideoView.getKollusContent(content)) {
            return content.getThumbnailPath();
        }

        return "";
    }

    public void setDataSource(int type, String url) {
        mPlayType = type;
        mSourceUrl = url;

        mVideoView.setVideoURI(Uri.parse(mSourceUrl));
    }

    public void setSurfaceVew(SurfaceView surfaceView) {
        mSurfaceVew = surfaceView;
    }

    private void setSizeSurfaceView(MediaPlayer mediaPlayer) {
        if (mSurfaceVew == null) {
            return;
        }

        mSurfaceVew.getHolder().setFixedSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());

        LogUtil.d(TAG, "onPrepared() - getWidth : " + mSurfaceVew.getWidth());
        LogUtil.d(TAG, "onPrepared() - getHeight : " + mSurfaceVew.getHeight());
        LogUtil.d(TAG, "onPrepared() - left : " + mSurfaceVew.getLeft());
        LogUtil.d(TAG, "onPrepared() - right : " + mSurfaceVew.getRight());
        LogUtil.d(TAG, "onPrepared() - top : " + mSurfaceVew.getTop());
        LogUtil.d(TAG, "onPrepared() - bottom : " + mSurfaceVew.getBottom());

        int mVideoWidth = mediaPlayer.getVideoWidth();
        int mVideoHeight = mediaPlayer.getVideoHeight();
        int displayWidth = mSurfaceVew.getWidth();
        int displayHeight = mSurfaceVew.getHeight();

        int l = mSurfaceVew.getLeft();
        int r = mSurfaceVew.getRight();
        int t = mSurfaceVew.getTop();
        int b = mSurfaceVew.getBottom();

        if (mVideoWidth * displayHeight > displayWidth * mVideoHeight) {
            displayHeight = displayWidth * mVideoHeight / mVideoWidth;
        } else if (mVideoWidth * displayHeight < displayWidth * mVideoHeight) {
            displayWidth = displayHeight * mVideoWidth / mVideoHeight;
        }

        l = (r - l - displayWidth) / 2;
        r = l + displayWidth;
        t = (b - t - displayHeight) / 2;
        b = t + displayHeight;

        LogUtil.d(TAG, "onPrepared() - left : " + l);
        LogUtil.d(TAG, "onPrepared() - right : " + r);
        LogUtil.d(TAG, "onPrepared() - top : " + t);
        LogUtil.d(TAG, "onPrepared() - bottom : " + b);

        mSurfaceVew.layout(l, t, r, b);
    }


    public MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            LogUtil.d(TAG, "onPrepared()");

            if (mediaPlayer != null) {
                setSizeSurfaceView(mediaPlayer);
                LogUtil.d(TAG, "onPrepared() - getPlayAt() : " + mediaPlayer.getPlayAt());
                if (mCurrentPlayAt != -1) {
                    mediaPlayer.seekToExact(mCurrentPlayAt);
                    mCurrentPlayAt = -1;
                }
                mediaPlayer.start();
            }
        }
    };

    public MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            LogUtil.d(TAG, "onCompletion()");

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    };

    public MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
            LogUtil.d(TAG, "onError() what : " + what + " extra : " + extra);

            if (mediaPlayer != null) {
                LogUtil.d(TAG, "errorMsg : " + mediaPlayer.getErrorString(extra));
            }

            return false;
        }
    };

    public MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
            LogUtil.d(TAG, "onVideoSizeChanged() width : " + width + "/ height : " + height);

            LogUtil.d(TAG, String.format("onVideoSizeChanged (%d %d) dimension(%d %d)",
                    width, height, mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight()));
            if (mediaPlayer.getVideoWidth() != 0 && mediaPlayer.getVideoHeight() != 0) {
                mSurfaceVew.getHolder().setFixedSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
                mSurfaceVew.requestLayout();
            }
        }
    };

    private KollusPlayerLMSListener kollusPlayerLMSListener = new KollusPlayerLMSListener() {

        @Override
        public void onLMS(String request, String response) {
            LogUtil.d(TAG, "request : " + request + " / response : " + response);
        }
    };
}
