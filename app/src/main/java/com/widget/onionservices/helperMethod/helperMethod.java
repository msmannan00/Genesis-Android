package com.widget.onionservices.helperMethod;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.widget.onionservices.widgetManager.widgetController;

public class helperMethod {
    /*Helper Methods General*/
    public static PendingIntent onCreatePendingIntent(Context pContext, int pID, String pAction) {
        Intent intentUpdate = new Intent(pContext, widgetController.class);
        intentUpdate.setAction(pAction);
        PendingIntent pendingUpdate;
        pendingUpdate = PendingIntent.getBroadcast(pContext, pID, intentUpdate, PendingIntent.FLAG_IMMUTABLE);
        return pendingUpdate;
    }

    public static void onStartApplication(Context pContext, String pPackageName) {
        Intent launchIntent = pContext.getPackageManager().getLaunchIntentForPackage(pPackageName);
        pContext.startActivity(launchIntent);
    }

}
