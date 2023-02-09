package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel;


import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.hiddenservices.onionservices.R;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.pluginManager.pluginReciever.mediaNotificationReciever;
import org.mozilla.geckoview.GeckoSession;

import java.lang.ref.WeakReference;

public class mediaDelegate implements GeckoSession.MediaDelegate {

    /*Private Variables*/

    private WeakReference<AppCompatActivity> mContext;
    private static int S_NOTIFICATION_ID = 1030;
    private static String S_NOTIFICATION_CHANNEL_ID = "1030";
    private static String S_NOTIFICATION_CHANNEL_NAME = "MEDIA_NOTIFICATION";

    /*Initializations*/

    public mediaDelegate(WeakReference<AppCompatActivity> pContext) {
        mContext = pContext;
    }

    public void onHideDefaultNotification(){
        NotificationManagerCompat.from(mContext.get()).cancel(S_NOTIFICATION_ID);
    }

    /*Triggers*/

    @SuppressLint("InlinedApi")
    public void showNotification(Context context, String title, String url, Bitmap mediaImage, boolean media_status) {
        NotificationManager mNotificationManager = (NotificationManager) mContext.get().getSystemService( NOTIFICATION_SERVICE ) ;
        if(title.length()<=0 || !status.sBackgroundMusic){
            return;
        }
        RemoteViews contentView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
            contentView = new RemoteViews(context.getPackageName() , R.layout. media_notification_no_background ) ;
        }else if (android.os.Build.VERSION. SDK_INT > Build.VERSION_CODES.N_MR1){
            contentView = new RemoteViews(context.getPackageName() , R.layout. media_notification_layout ) ;
        }else {
            contentView = new RemoteViews(context.getPackageName() , R.layout. media_notification_layout_small ) ;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ) {
            contentView.setInt(R.id.layout,"setBackgroundResource", R.color.c_tab_background);
        }
        contentView.setTextViewText(R.id.header, title);
        contentView.setTextViewText(R.id.body, "☍ " + url);

        try {
            contentView.setImageViewBitmap(R.id.logo, mediaImage);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (!media_status){
            PendingIntent pIntent = helperMethod.onCreateActionIntent(context, mediaNotificationReciever.class, S_NOTIFICATION_ID, "media_play", 0);
            contentView.setOnClickPendingIntent(R.id.trigger, pIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
                contentView.setImageViewResource(R.id.trigger, R.drawable.ic_baseline_play_arrow_no_tint);
            }else {
                contentView.setImageViewResource(R.id.trigger, R.drawable.ic_baseline_play_arrow);
            }
        }else {
            PendingIntent pIntent = helperMethod.onCreateActionIntent(context, mediaNotificationReciever.class, S_NOTIFICATION_ID, "media_pause", 1);
            contentView.setOnClickPendingIntent(R.id.trigger, pIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
                contentView.setImageViewResource(R.id.trigger, R.drawable.ic_baseline_pause_no_tint);
            }else {
                contentView.setImageViewResource(R.id.trigger, R.drawable.ic_baseline_pause);
            }
        }

        PendingIntent pIntentPrev = helperMethod.onCreateActionIntent(context, mediaNotificationReciever.class, S_NOTIFICATION_ID, "media_next", 2);
        contentView.setOnClickPendingIntent(R.id.next, pIntentPrev);

        if(android.os.Build.VERSION. SDK_INT > Build.VERSION_CODES.N){
            PendingIntent pIntentNext = helperMethod.onCreateActionIntent(context, mediaNotificationReciever.class, S_NOTIFICATION_ID, "media_back", 3);
            contentView.setOnClickPendingIntent(R.id.back, pIntentNext);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get(), S_NOTIFICATION_CHANNEL_ID ) ;

        mBuilder.setPriority(Notification.PRIORITY_LOW);
        mBuilder.setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            mBuilder.setCustomBigContentView(contentView);
            mBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        }else {
            mBuilder.setContent(contentView);
        }

        mBuilder.setSmallIcon(R.drawable.ic_baseline_media) ;
        mBuilder.setAutoCancel( true ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager.IMPORTANCE_LOW ;
            NotificationChannel notificationChannel = new NotificationChannel( S_NOTIFICATION_CHANNEL_ID , S_NOTIFICATION_CHANNEL_NAME , importance) ;
            notificationChannel.setSound(null, null);
            mBuilder.setChannelId(S_NOTIFICATION_CHANNEL_ID) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        Notification notification = mBuilder.build();

        mBuilder.setContentIntent(PendingIntent.getActivity(context, S_NOTIFICATION_ID, new Intent(context, mediaDelegate.class), PendingIntent.FLAG_IMMUTABLE));
        notification.fullScreenIntent = null;

        if(media_status){
            notification.flags |= Notification.FLAG_NO_CLEAR;
        }

        notification.defaults = 0;
        mNotificationManager.notify(S_NOTIFICATION_ID , notification) ;

    }
}