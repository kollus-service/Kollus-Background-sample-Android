package se.kollus.com.ui.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import se.kollus.com.R;
import se.kollus.com.player.BackgroundVideoService;
import se.kollus.com.ui.avtivity.IntroActivity;

public class NotificationPlayer {
    private final static int NOTIFICATION_PLAYER_ID = 0x123456;
    private BackgroundVideoService mService;
    private NotificationManager mNotificationManager;
    private NotificationManagerBuilder mNotificationManagerBuilder;
    private boolean isForeground;

    public NotificationPlayer(BackgroundVideoService service) {
        mService = service;
        mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void updateNotificationPlayer() {
        cancel();
        mNotificationManagerBuilder = new NotificationManagerBuilder();
        mNotificationManagerBuilder.execute();
    }

    public void removeNotificationPlayer() {
        cancel();
        mService.stopForeground(true);
        //mService.stopSelf();
        isForeground = false;
    }

    private void cancel() {
        if (mNotificationManagerBuilder != null) {
            mNotificationManagerBuilder.cancel(true);
            mNotificationManagerBuilder = null;
        }
    }

    private class NotificationManagerBuilder extends AsyncTask<Void, Void, Notification> {
        private RemoteViews mRemoteViews;
        private NotificationCompat.Builder mNotificationBuilder;
        private PendingIntent mMainPendingIntent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent intent = new Intent(mService, IntroActivity.class);
            mMainPendingIntent = PendingIntent.getActivity(mService, 0, intent, 0);
            mRemoteViews = createRemoteView(R.layout.notification_player);

            if (Build.VERSION.SDK_INT >= 26) {
                String CHANNEL_ID = "kollus_channel_id";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "kollus_channel_id", NotificationManager.IMPORTANCE_LOW);
                channel.enableVibration(true);
                channel.setSound(null, null);
                ((NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                mNotificationBuilder = new NotificationCompat.Builder(mService, CHANNEL_ID);
            } else {
                mNotificationBuilder = new NotificationCompat.Builder(mService);
            }
            mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContent(mRemoteViews)
                    .setContentIntent(mMainPendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            Notification notification = mNotificationBuilder.build();
            notification.priority = Notification.PRIORITY_MAX;
            notification.contentIntent = mMainPendingIntent;
            if (!isForeground) {
                isForeground = true;
                mService.startForeground(NOTIFICATION_PLAYER_ID, notification);
            }
        }

        @Override
        protected Notification doInBackground(Void... params) {
            mNotificationBuilder.setContent(mRemoteViews);
            mNotificationBuilder.setContentIntent(mMainPendingIntent);
            mNotificationBuilder.setPriority(Notification.PRIORITY_MAX);
            Notification notification = mNotificationBuilder.build();
            updateRemoteView(mRemoteViews, notification);
            return notification;
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);
            try {
                mNotificationManager.notify(NOTIFICATION_PLAYER_ID, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private RemoteViews createRemoteView(int layoutId) {
            RemoteViews remoteView = new RemoteViews(mService.getPackageName(), layoutId);
            Intent actionTogglePlay = new Intent(CommandActions.TOGGLE_PLAY);
            Intent actionForward = new Intent(CommandActions.FORWARD);
            Intent actionRewind = new Intent(CommandActions.REWIND);
            Intent actionClose = new Intent(CommandActions.CLOSE);
            PendingIntent togglePlay = PendingIntent.getService(mService, 0, actionTogglePlay, 0);
            PendingIntent forward = PendingIntent.getService(mService, 0, actionForward, 0);
            PendingIntent rewind = PendingIntent.getService(mService, 0, actionRewind, 0);
            PendingIntent close = PendingIntent.getService(mService, 0, actionClose, 0);

            remoteView.setOnClickPendingIntent(R.id.btn_play_pause, togglePlay);
            remoteView.setOnClickPendingIntent(R.id.btn_forward, forward);
            remoteView.setOnClickPendingIntent(R.id.btn_rewind, rewind);
            remoteView.setOnClickPendingIntent(R.id.btn_close, close);
            return remoteView;
        }

        private void updateRemoteView(final RemoteViews remoteViews, final Notification notification) {
            if (mService.getPlayer() != null && mService.getPlayer().isPlaying()) {
                remoteViews.setImageViewResource(R.id.btn_play_pause, R.drawable.pause);
            } else {
                remoteViews.setImageViewResource(R.id.btn_play_pause, R.drawable.play);
            }

            if (mService.getPlayer() != null) {
                String title = mService.getPlayer().getKollusMediaTitle();
                final String path = mService.getPlayer().getKollusMediaThumbnailPath();
                remoteViews.setTextViewText(R.id.txt_title, title);

                Bitmap thumbnailBitmap = BitmapFactory.decodeFile(path);
                if (thumbnailBitmap != null) {
                    Bitmap proxy = Bitmap.createBitmap(thumbnailBitmap.getWidth(), thumbnailBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(proxy);
                    c.drawBitmap(thumbnailBitmap, new Matrix(), null);
                    remoteViews.setImageViewBitmap(R.id.img_albumart, proxy);
                }
            }
//            Picasso.with(mService).load(path).error(R.drawable.ic_launcher_background).into(remoteViews, R.id.img_albumart, NOTIFICATION_PLAYER_ID, notification);
        }
    }

    public class CommandActions {
        public final static String BACK_GROUND_OFF = "BACK_GROUND_OFF";
        public final static String BACK_GROUND_ON = "BACK_GROUND_ON";
        public final static String REWIND = "REWIND";
        public final static String TOGGLE_PLAY = "TOGGLE_PLAY";
        public final static String FORWARD = "FORWARD";
        public final static String CLOSE = "CLOSE";
    }

}
