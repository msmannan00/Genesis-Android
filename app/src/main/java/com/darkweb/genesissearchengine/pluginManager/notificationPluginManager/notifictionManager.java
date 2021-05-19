package com.darkweb.genesissearchengine.pluginManager.notificationPluginManager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import java.lang.ref.WeakReference;
import java.util.List;
import static com.darkweb.genesissearchengine.constants.constants.*;

public class notifictionManager
{
    /*Private Variables*/

    private WeakReference<AppCompatActivity> mAppContext;

    /*Initializations*/

    public notifictionManager(WeakReference<AppCompatActivity> pAppContext, eventObserver.eventListener pEvent){
        this.mAppContext = pAppContext;
        onNotificationClear();
    }

    private void onNotificationClear(){
        NotificationManager notificationManager = (NotificationManager) mAppContext.get().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(status.mNotificationID);
    }

    private void onCreateUserEngagementNotification(int pDelay){
        onSchedule(getNotification() , pDelay ) ;
    }

    private void onSchedule(Notification pNotification , int pDelay){
        Intent notificationIntent = new Intent( mAppContext.get().getApplicationContext(), localEngagementManager.class) ;
        notificationIntent.putExtra(CONST_NOTIFICATION_ID_NAME, CONST_NOTIFICATION_ID_VALUE) ;
        notificationIntent.putExtra(CONST_NOTIFICATION_ID_NAME, pNotification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( mAppContext.get().getApplicationContext(), CONST_NOTIFICATION_REQUEST_CODE, notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock. elapsedRealtime () + pDelay ;
        AlarmManager alarmManager = (AlarmManager) mAppContext.get().getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;
    }

    private Notification getNotification () {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mAppContext.get().getApplicationContext(), CONST_NOTIFICATION_ID_NAME) ;
        builder.setContentTitle(CONST_NOTIFICATION_TITLE) ;
        builder.setSmallIcon(R.mipmap.ic_stat_tor_logo);
        builder.setAutoCancel(true);
        builder.setColor(Color.parseColor("#84989f"));
        builder.setChannelId(CONST_NOTIFICATION_ID_NAME);
        return builder.build();
    }

    /*External Triggers*/

    public Object onTrigger(List<Object> pData, pluginEnums.eNotificationManager pEventType) {
        if(pEventType.equals(pluginEnums.eNotificationManager.M_CREATE_NOTIFICATION))
        {
            onCreateUserEngagementNotification((int)pData.get(0));
        }
        else if(pEventType.equals(pluginEnums.eNotificationManager.M_CLEAR_NOTIFICATION))
        {
            onNotificationClear();
        }
        return null;
    }

}
