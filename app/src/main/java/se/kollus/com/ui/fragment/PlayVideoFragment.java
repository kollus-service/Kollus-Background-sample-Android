package se.kollus.com.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import se.kollus.com.R;
import se.kollus.com.player.CustomPlayer;
import se.kollus.com.player.PlayerManager;
import se.kollus.com.utils.CommonUtils;
import se.kollus.com.utils.LogUtil;
import se.kollus.com.utils.Preferences;

import static com.kollus.sdk.media.KollusStorage.TYPE_CACHE;
import static se.kollus.com.utils.Config.MODE_MAKE_JWT;


public class PlayVideoFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = PlayVideoFragment.class.getSimpleName();

    private RelativeLayout mPlayerLayout = null;
    private CustomPlayer mPlayer = null;
    private TextView mLogTextView = null;

    public int playType = TYPE_CACHE;
    public String jwtUrl = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LogUtil.d(TAG, "onCreateView");

        if (MODE_MAKE_JWT) {
            String mckey = "KOLLUS_MEDIA_CONTENT_KEY";
            String cuid = "CLIENT_USER";
            try {
                jwtUrl = CommonUtils.createUrl(cuid, mckey, true);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        View root = inflater.inflate(R.layout.fragment_playvideo, container, false);

        mPlayerLayout = (RelativeLayout) root.findViewById(R.id.surface_view);

        mPlayer = PlayerManager.getInstance().getPlayer();
        if (mPlayer != null) {
            LogUtil.d(TAG, "mPlayer not null");
            mPlayer.setCurrentPlayAt(mPlayer.getCurrentPosition());
            mPlayer.release();
            mPlayer.setPlayerLayer(mPlayerLayout);
            mPlayer.setDataSource(playType, jwtUrl);
        } else {
            LogUtil.d(TAG, "mPlayer null");
        }

        mLogTextView = (TextView) root.findViewById(R.id.control_log);

        root.findViewById(R.id.play).setOnClickListener(this);
        root.findViewById(R.id.pause).setOnClickListener(this);
        root.findViewById(R.id.rate_up).setOnClickListener(this);
        root.findViewById(R.id.rate_down).setOnClickListener(this);
        root.findViewById(R.id.volume_up).setOnClickListener(this);
        root.findViewById(R.id.volume_down).setOnClickListener(this);
        root.findViewById(R.id.mute).setOnClickListener(this);
        root.findViewById(R.id.un_mute).setOnClickListener(this);
        root.findViewById(R.id.ff).setOnClickListener(this);
        root.findViewById(R.id.rw).setOnClickListener(this);
        root.findViewById(R.id.restart).setOnClickListener(this);
        root.findViewById(R.id.callApp).setOnClickListener(this);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        LogUtil.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
        if (!Preferences.isBackgroundVideo()) {
            if (mPlayer != null) {
                mPlayer.pause();
            }
        } else {
            if (mPlayer != null) {
                mPlayer.backGroundPlay();
            }
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        super.onDestroy();
        if (!Preferences.isBackgroundVideo()) {
            if (mPlayer != null) {
                mPlayer.finish();
            }
        } else {
            if (mPlayer != null) {
                mPlayer.backGroundPlay();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                setLogText("play");
                mPlayer.start();
                break;
            case R.id.pause:
                setLogText("pause");
                mPlayer.pause();
                break;
            case R.id.rate_up:
                mPlayer.setPlayingRate(1);
                setLogText("Rate Up : " + String.format("%.1f", mPlayer.getPlayingRate()));
                break;
            case R.id.rate_down:
                mPlayer.setPlayingRate(-1);
                setLogText("Rate Down : " + String.format("%.1f", mPlayer.getPlayingRate()));
                break;
            case R.id.volume_up:
                CommonUtils.setStreamVolume(getContext(), true);
                mPlayer.setVolumeLevel(CommonUtils.getStreamVolume(getContext()));
                setLogText("Volume up : " + CommonUtils.getStreamVolume(getContext()));
                break;
            case R.id.volume_down:
                CommonUtils.setStreamVolume(getContext(), false);
                mPlayer.setVolumeLevel(CommonUtils.getStreamVolume(getContext()));
                setLogText("Volume down : " + CommonUtils.getStreamVolume(getContext()));
                break;
            case R.id.ff:
                mPlayer.setFF();
                setLogText("setFF(10) : " + mPlayer.getCurrentPosition() + "ms");
                break;
            case R.id.rw:
                mPlayer.setRW();
                setLogText("setRW(10) : " + mPlayer.getCurrentPosition() + "ms");
                break;
            case R.id.mute:
                mPlayer.setMute(true);
                setLogText("set Mute");
                break;
            case R.id.un_mute:
                mPlayer.setMute(false);
                setLogText("set unMute");
                break;
            case R.id.restart:
                mPlayer.prepareAsync();
                setLogText("re start");
                break;
            case R.id.callApp:
                setLogText("call Kollus App");
                CommonUtils.startKollusApp(getContext(), jwtUrl);
                mPlayer.pause();
                break;
            default:
                break;
        }
    }

    public void setLogText(String log) {
        if (log != null && mLogTextView != null) {
            mLogTextView.setText(log);
        }
    }
}
