package com.darkweb.genesissearchengine.pluginManager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.userEngagementNotification;
import com.example.myapplication.R;

class notifictionManager
{
    /*Private Variables*/

    private AppCompatActivity mAppContext;
    private NotificationManager mNotifManager;

    /*Initializations*/

    notifictionManager(AppCompatActivity app_context, eventObserver.eventListener event){
        this.mAppContext = app_context;
        onNotificationClear();
    }

    public void onNotificationClear(){
        NotificationManager notificationManager = (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(status.mNotificationID);
    }

    public void onCreateUserEngagementNotification(int pDelay){
        onSchedule(getNotification() , pDelay ) ;
    }

    public void onSchedule(Notification notification , int delay){
        Intent notificationIntent = new Intent( mAppContext, userEngagementNotification.class) ;
        notificationIntent.putExtra("NOTIFICATION:INDENTIFIER:1001" , 1 ) ;
        notificationIntent.putExtra("NOTIFICATION:INDENTIFIER:1001" , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( mAppContext, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock. elapsedRealtime () + delay ;
        AlarmManager alarmManager = (AlarmManager) mAppContext.getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;
    }

    private Notification getNotification () {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mAppContext, "NOTIFICATION:INDENTIFIER:1001") ;
        builder.setContentTitle("Scheduled Notification") ;
        builder.setSmallIcon(R.drawable.notification_logo);
        builder.setAutoCancel(true) ;
        builder.setChannelId("NOTIFICATION:INDENTIFIER:1001") ;
        return builder.build() ;
    }
}
