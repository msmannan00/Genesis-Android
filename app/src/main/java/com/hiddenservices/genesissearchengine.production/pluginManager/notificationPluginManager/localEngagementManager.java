package com.hiddenservices.genesissearchengine.production.pluginManager.notificationPluginManager;

import android.app.Notification ;
import android.app.NotificationChannel ;
import android.app.NotificationManager ;
import android.app.PendingIntent;
import android.content.BroadcastReceiver ;
import android.content.Context ;
import android.content.Intent ;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.hiddenservices.genesissearchengine.production.appManager.activityContextManager;
import com.hiddenservices.genesissearchengine.production.constants.strings;
import com.example.myapplication.R;
import java.util.Random;
import static com.hiddenservices.genesissearchengine.production.constants.constants.mUserEngagementNotificationID;

public class localEngagementManager extends BroadcastReceiver {


    public void onReceive (Context context , Intent intent) {
        try{
            final int NOTIFY_ID = mUserEngagementNotificationID;
            String pTitle = strings.NOTIFICATION_TITLE;
            String pBody = strings.NOTIFICATION_BODY[new Random().nextInt(strings.NOTIFICATION_BODY.length)];

            String name = "NOTIFICATION:INDENTIFIER:1001";
            String id = "NOTIFICATION:INDENTIFIER:1001";
            String description = "NOTIFICATION:INDENTIFIER:1001";


            NotificationCompat.Builder builder;
            NotificationManager mNotifManager = (NotificationManager)context.getSystemService(Context. NOTIFICATION_SERVICE ) ;

            if (mNotifManager == null) {
                mNotifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = mNotifManager.getNotificationChannel(id);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(id, name, importance);
                    mChannel.setDescription(description);
                    mChannel.enableVibration(true);
                    mChannel.setLightColor(Color.GREEN);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mNotifManager.createNotificationChannel(mChannel);
                }
                builder = new NotificationCompat.Builder(context, id);

                Intent mIntent = activityContextManager.getInstance().getHomeController().getIntent();
                PendingIntent pendIntent = PendingIntent.getActivity(activityContextManager.getInstance().getCurrentActivity(), 0, mIntent, PendingIntent.FLAG_ONE_SHOT);

                builder.setContentTitle(pTitle)  // required
                        .setSmallIcon(R.mipmap.ic_stat_tor_logo) // required
                        .setContentText(pBody)  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setContentIntent(pendIntent)
                        .setTicker(pTitle)
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            } else {

                builder = new NotificationCompat.Builder(context);
                Intent mIntent = activityContextManager.getInstance().getHomeController().getIntent();
                PendingIntent pendIntent = PendingIntent.getActivity(activityContextManager.getInstance().getCurrentActivity(), 0, mIntent, PendingIntent. FLAG_ONE_SHOT);

                builder.setContentTitle(pTitle)                           // required
                        .setSmallIcon(R.mipmap.ic_stat_tor_logo) // required
                        .setContentText(pBody)  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendIntent)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setTicker(pTitle)
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                        .setPriority(Notification.PRIORITY_HIGH);
            }

            Notification notification = builder.build();
            mNotifManager.notify(NOTIFY_ID, notification);
        }catch (Exception ex){
            Log.i("","");
        }
    }
}