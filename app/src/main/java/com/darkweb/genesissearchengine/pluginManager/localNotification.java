package com.darkweb.genesissearchengine.pluginManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;
import com.darkweb.genesissearchengine.appManager.home_activity.home_model;
import com.example.myapplication.R;

public class localNotification
{
    private static final localNotification ourInstance = new localNotification();

    public static localNotification getInstance()
    {
        return ourInstance;
    }

    private localNotification()
    {
        mContext = home_model.getInstance().getAppContext();
    }

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    /**
     * Create and push the notification
     */
    public void createNotification(String title, String message)
    {
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(mContext , home_model.getInstance().getHomeInstance().getClass());
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.xml.ic_icon_download_notification);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;

        int oneTimeID = (int) SystemClock.uptimeMillis();
        mNotificationManager.notify(oneTimeID, mBuilder.build());

        mNotificationManager.notify(oneTimeID /* Request Code */, mBuilder.build());

        new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(5000);
                    mNotificationManager.cancel(oneTimeID);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
