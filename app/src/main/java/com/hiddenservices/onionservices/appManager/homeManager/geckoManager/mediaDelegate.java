package com.hiddenservices.onionservices.appManager.homeManager.geckoManager;


import static android.content.Context.NOTIFICATION_SERVICE;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.myapplication.R;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.pluginManager.pluginReciever.mediaNotificationReciever;
import org.mozilla.geckoview.GeckoSession;

public class mediaDelegate implements GeckoSession.MediaDelegate {
    private Context mContext;

    public mediaDelegate(Activity activity, Context pContext) {
        mContext = pContext;
    }

    public void onHideDefaultNotification(){
        NotificationManagerCompat.from(mContext).cancel(1030);
    }

    public void showNotification(Context context, String title, String url, Bitmap mediaImage, boolean not_status) {
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService( NOTIFICATION_SERVICE ) ;
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