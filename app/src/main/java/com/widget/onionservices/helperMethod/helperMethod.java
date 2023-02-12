package com.widget.onionservices.helperMethod;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.widget.onionservices.widgetManager.widgetController;

public class helperMethod {
    /*Helper Methods General*/
    public static PendingIntent onCreatePendingIntent(Context pContext, int pFlag, int pID, String pAction) {
        Intent intentUpdate = new Intent(pContext, widgetController.class);
        intentUpdate.setAction(pAction);
        PendingIntent pendingUpdate;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingUpdate = PendingIntent.getBroadcast(pContext, pID, intentUpdate, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingUpdate = PendingIntent.getBroadcast(pContext, pID, intentUpdate, pFlag);
        }
        return pendingUpdate;
    }

    public static void onStartApplication(Context pContext, String pPackageName) {
        Intent launchIntent = pContext.getPackageManager().getLaunchIntentForPackage(pPackageName);
        pContext.startActivity(launchIntent);
    }

}
