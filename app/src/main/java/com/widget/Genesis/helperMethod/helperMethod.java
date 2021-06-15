package com.widget.search.helperMethod;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.widget.search.searchWidgetManager;

public class helperMethod
{
    /*Helper Methods General*/
    public static PendingIntent onCreatePendingIntent(Context pContext,int pFlag, int pID, String pAction){
        Intent intentUpdate = new Intent(pContext, searchWidgetManager.class);
        intentUpdate.setAction(pAction);
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(pContext, pID, intentUpdate, pFlag);
        return pendingUpdate;
    }

}
