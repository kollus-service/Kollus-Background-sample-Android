package se.kollus.com.ui.avtivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

import se.kollus.com.R;
import se.kollus.com.player.PlayerManager;
import se.kollus.com.ui.fragment.PlayVideoFragment;
import se.kollus.com.utils.LogUtil;
import se.kollus.com.utils.Preferences;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.main_activity);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggle_btn);
        toggle.setChecked(Preferences.isBackgroundVideo());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new PlayVideoFragment());
        transaction.commit();
    }

    public void onToggleClicked(View v) {
        boolean on = ((ToggleButton) v).isChecked();
        Preferences.setBackgroundVideo(on);
        if (on) {
            Toast.makeText(this, "on clicked", Toast.LENGTH_SHORT).show();
            PlayerManager.getInstance().startBackgroundService();
        } else {
            Toast.makeText(this, "off clicked", Toast.LENGTH_SHORT).show();
            PlayerManager.getInstance().stopBackgroundService();
        }
    }

    @Override
    protected void onResume() {
        LogUtil.d(TAG, "onResume");
        if (Preferences.isBackgroundVideo() && PlayerManager.getInstance().isStarting()) {
            PlayerManager.getInstance().stopBackgroundService();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.d(TAG, "onPause");
        if (Preferences.isBackgroundVideo()) {
            PlayerManager.getInstance().startBackgroundService();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        super.onDestroy();
        if (!Preferences.isBackgroundVideo()) {
            PlayerManager.getInstance().terminate();
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof OnBackPressedListener) {
                    ((OnBackPressedListener) fragment).onBackPressed();
                }
            }
        }
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }
}
