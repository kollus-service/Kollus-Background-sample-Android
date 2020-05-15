package se.kollus.com;

import android.app.Application;
import android.content.Context;

import se.kollus.com.player.PlayerManager;
import se.kollus.com.utils.Preferences;

public class KollusSampleApplication extends Application {

    private static Context mApplicationContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = getApplicationContext();
        PlayerManager.getInstance().init();
        Preferences.init(mApplicationContext);
    }

    public static Context getAppContext() {
        return mApplicationContext;
    }

}
