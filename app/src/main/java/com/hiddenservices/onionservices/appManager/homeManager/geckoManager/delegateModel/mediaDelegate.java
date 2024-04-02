package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.hiddenservices.onionservices.R;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeController;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.pluginManager.pluginReciever.mediaNotificationReceiver;
import org.mozilla.geckoview.GeckoSession;

import java.lang.ref.WeakReference;

public class mediaDelegate implements GeckoSession.MediaDelegate {

    /*Private Variables*/

    private final WeakReference<AppCompatActivity> mContext;
    private static final int S_NOTIFICATION_ID = 1030;
    private static final String S_NOTIFICATION_CHANNEL_ID = "1032";
    private static final String S_NOTIFICATION_CHANNEL_NAME = "MEDIA_NOTIFICATION";

    /*Initializations*/

    public mediaDelegate(WeakReference<AppCompatActivity> pContext) {
        mContext = pContext;
    }

    public void onHideDefaultNotification(){
        if(!status.mThemeApplying){
            NotificationManagerCompat.from(mContext.get()).cancel(S_NOTIFICATION_ID);
        }
    }

    /*Triggers*/

    @SuppressLint({"InlinedApi", "ObsoleteSdkInt"})
    public void showNotification(Context context, String title, String url, Bitmap mediaImage, boolean media_status) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (title.isEmpty() || !status.sBackgroundMusic) {
            return;
        }

        PendingIntent playPauseIntent;
        int playPauseIcon;
        if (!media_status) {
            playPauseIntent = helperMethod.onCreateActionIntent(context, mediaNotificationReceiver.class, S_NOTIFICATION_ID, "media_play", enums.MediaNotificationReceiver.PLAY);
            playPauseIcon = R.drawable.ic_baseline_play_arrow;
        } else {
            playPauseIntent = helperMethod.onCreateActionIntent(context, mediaNotificationReceiver.class, S_NOTIFICATION_ID, "media_pause", enums.MediaNotificationReceiver.PAUSE);
            playPauseIcon = R.drawable.ic_baseline_pause;
        }

        PendingIntent nextIntent = helperMethod.onCreateActionIntent(context, mediaNotificationReceiver.class, S_NOTIFICATION_ID, "media_next", enums.MediaNotificationReceiver.SKIP_FORWARD);
        PendingIntent prevIntent = helperMethod.onCreateActionIntent(context, mediaNotificationReceiver.class, S_NOTIFICATION_ID, "media_next", enums.MediaNotificationReceiver.SKIP_BACKWARD);

        PendingIntent actionIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            actionIntent = PendingIntent.getActivity(context, S_NOTIFICATION_ID, new Intent(context, homeController.class), PendingIntent.FLAG_IMMUTABLE);
        } else {
            actionIntent = PendingIntent.getActivity(context, S_NOTIFICATION_ID, new Intent(context, homeController.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, S_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(url)
                .setSmallIcon(R.drawable.ic_baseline_media)
                .setLargeIcon(mediaImage)
                .addAction(R.drawable.ic_baseline_skip_previous, "Previous", prevIntent)
                .addAction(playPauseIcon, "Play/Pause", playPauseIntent)
                .addAction(R.drawable.ic_baseline_skip_next, "Next", nextIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .setPriority(Notification.PRIORITY_LOW)
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(actionIntent);  // Set the PendingIntent to open homeController

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(S_NOTIFICATION_CHANNEL_ID, S_NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.setSound(null, null);
            mBuilder.setChannelId(S_NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification = mBuilder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
        } else {
            notification.flags |= Notification.FLAG_NO_CLEAR;
        }

        mNotificationManager.notify(S_NOTIFICATION_ID, notification);
    }

}
