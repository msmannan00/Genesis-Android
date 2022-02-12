package com.widget.Genesis.helperMethod;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.widget.Genesis.widgetManager.widgetController;

public class helperMethod
{
    /*Helper Methods General*/
    public static PendingIntent onCreatePendingIntent(Context pContext,int pFlag, int pID, String pAction){
        Intent intentUpdate = new Intent(pContext, widgetController.class);
        intentUpdate.setAction(pAction);
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(pContext, pID, intentUpdate, pFlag);
        return pendingUpdate;
    }

    public static void onStartApplication(Context pContext,String pPackageName){
        Intent launchIntent = pContext.getPackageManager().getLaunchIntentForPackage(pPackageName);
        pContext.startActivity(launchIntent);
    }

}
