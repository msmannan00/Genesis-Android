package com.hiddenservices.onionservices.appManager.homeManager.geckoManager;


import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.hiddenservices.onionservices.appManager.homeManager.geckoManager.geckoPromptView.LOGTAG;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.myapplication.R;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.pluginManager.pluginReciever.mediaNotificationReciever;
import org.mozilla.geckoview.GeckoSession;

public class mediaDelegate implements GeckoSession.MediaDelegate {
    private Integer mLastNotificationId = 100;
    private Integer mNotificationId;
    private final Activity mActivity;
    private Context mContext;
    private NotificationManager mNotificationManager;

    public mediaDelegate(Activity activity, Context pContext) {
        mActivity = activity;
        mContext = pContext;
        mNotificationManager = (NotificationManager) mContext.getSystemService( NOTIFICATION_SERVICE ) ;
    }

    @Override
    public void onRecordingStatusChanged(@NonNull GeckoSession session, RecordingDevice[] devices) {
        String message;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mActivity);
        RecordingDevice camera = null;
        RecordingDevice microphone = null;

        for (RecordingDevice device : devices) {
            if (device.type == RecordingDevice.Type.CAMERA) {
                camera = device;
            } else if (device.type == RecordingDevice.Type.MICROPHONE) {
                microphone = device;
            }
        }
        if (camera != null && microphone != null) {
            Log.d(LOGTAG, "ExampleDeviceDelegate:onRecordingDeviceEvent display alert_mic_camera");
            message = "Microphone and Camera is on";
        } else if (camera != null) {
            Log.d(LOGTAG, "ExampleDeviceDelegate:onRecordingDeviceEvent display alert_camera");
            message = "Camera is on";
        } else if (microphone != null) {
            Log.d(LOGTAG, "ExampleDeviceDelegate:onRecordingDeviceEvent display alert_mic");
            message = "Microphone is on";
        } else {
            Log.d(LOGTAG, "ExampleDeviceDelegate:onRecordingDeviceEvent dismiss any notifications");
            if (mNotificationId != null) {
                notificationManager.cancel(mNotificationId);
                mNotificationId = null;
            }
            return;
        }
        if (mNotificationId == null) {
            mNotificationId = ++mLastNotificationId;
        }

        Intent intent = new Intent(mActivity, mediaDelegate.class);
        PendingIntent pendingIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(mActivity.getApplicationContext(), 0, intent, FLAG_IMMUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(mActivity.getApplicationContext(), 0, intent, 0);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mActivity.getApplicationContext(), "GeckoChannel")
                        .setContentTitle("Orion Browser")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setOngoing(false)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(mNotificationId, notification);
    }

    public void onHideDefaultNotification(){
        if(mNotificationManager!=null){
            mNotificationManager.cancel(1030);
        }
        NotificationManagerCompat.from(mContext).cancel(1030);
    }

    public void showNotification(Context context, String title, String url, Bitmap mediaImage, boolean not_status) {

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

        if (not_status){
            PendingIntent pIntent = helperMethod.onCreateActionIntent(context, mediaNotificationReciever.class, 1030, "media_play", 0);
            contentView.setOnClickPendingIntent(R.id.trigger, pIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
                contentView.setImageViewResource(R.id.trigger, R.drawable.ic_baseline_play_arrow_no_tint);
            }else {
                contentView.setImageViewResource(R.id.trigger, R.drawable.ic_baseline_play_arrow);
            }
        }else {
            PendingIntent pIntent = helperMethod.onCreateActionIntent(context, mediaNotificationReciever.class, 1030, "media_pause", 1);
            contentView.setOnClickPendingIntent(R.id.trigger, pIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
                contentView.setImageViewResource(R.id.trigger, R.drawable.ic_baseline_pause_no_tint);
            }else {
                contentView.setImageViewResource(R.id.trigger, R.drawable.ic_baseline_pause);
            }
        }

        PendingIntent pIntentPrev = helperMethod.onCreateActionIntent(context, mediaNotificationReciever.class, 1030, "media_next", 2);
        contentView.setOnClickPendingIntent(R.id.next, pIntentPrev);

        if(android.os.Build.VERSION. SDK_INT > Build.VERSION_CODES.N){
            PendingIntent pIntentNext = helperMethod.onCreateActionIntent(context, mediaNotificationReciever.class, 1030, "media_back", 3);
            contentView.setOnClickPendingIntent(R.id.back, pIntentNext);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, "1030" ) ;

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
            NotificationChannel notificationChannel = new NotificationChannel( "1030" , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            notificationChannel.setSound(null, null);
            mBuilder.setChannelId("1030") ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        Notification notification = mBuilder.build();

        mBuilder.setContentIntent(PendingIntent.getActivity(context, 1030, new Intent(context, mediaDelegate.class), PendingIntent.FLAG_IMMUTABLE));
        PendingIntent dummyIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(context, 1030, new Intent(), PendingIntent.FLAG_IMMUTABLE);
        }else {
            PendingIntent.getActivity(context, 1030, new Intent(), 0);
        }
        notification.fullScreenIntent = dummyIntent;

        if(!not_status){
            notification.flags |= Notification.FLAG_NO_CLEAR;
        }

        notification.defaults = 0;
        mNotificationManager.notify(1030 , notification) ;

    }
}