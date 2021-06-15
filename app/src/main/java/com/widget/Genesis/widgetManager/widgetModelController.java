package com.widget.Genesis.widgetManager;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.eventObserver;
import com.example.myapplication.R;
import com.widget.Genesis.helperMethod.helperMethod;

import java.util.List;

import static com.darkweb.genesissearchengine.constants.constants.CONST_PACKAGE_NAME;

public class modelViewController extends AppWidgetProvider {

    /*Private Variables*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    modelViewController(eventObserver.eventListener pEvent)
    {
        this.mEvent = pEvent;
    }

    private void initialize(Context context, Intent intent){
        String action = intent.getAction();
        switch (action) {
            case enums.WidgetCommands.OPEN_APPLICATION: {
                status.sWidgetResponse = enums.WidgetResponse.SEARCHBAR;
                helperMethod.onStartApplication(context, CONST_PACKAGE_NAME);
                break;
            }
            case enums.WidgetCommands.OPEN_VOICE: {
                status.sWidgetResponse = enums.WidgetResponse.VOICE;
                helperMethod.onStartApplication(context, CONST_PACKAGE_NAME);
                break;
            }
            case AppWidgetManager.ACTION_APPWIDGET_UPDATE: {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                    if (appWidgetIds != null && appWidgetIds.length > 0) {
                        this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                    }
                }
                break;
            }
            case AppWidgetManager.ACTION_APPWIDGET_DELETED: {
                Bundle extras = intent.getExtras();
                if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                    final int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                    this.onDeleted(context, new int[]{appWidgetId});
                }
                break;
            }
            case AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED: {
                Bundle extras = intent.getExtras();
                if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)&& extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS)) {
                    int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                    Bundle widgetExtras = extras.getBundle(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS);
                    this.onAppWidgetOptionsChanged(context, AppWidgetManager.getInstance(context),appWidgetId, widgetExtras);
                }
                break;
            }

            case AppWidgetManager.ACTION_APPWIDGET_RESTORED: {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int[] oldIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_OLD_IDS);
                    int[] newIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                    if (oldIds != null && oldIds.length > 0) {
                        this.onRestored(context, oldIds, newIds);
                        this.onUpdate(context, AppWidgetManager.getInstance(context), newIds);
                    }
                }
                break;
            }
        }
    }

    public Object onTrigger(widgetEnums.eModelViewController pCommands, List<Object> pData){
        if(pCommands.equals(widgetEnums.eModelViewController.M_ON_RECIEVE)){
            initialize((Context)pData.get(0), (Intent) pData.get(1));
        }
        return null;
    }

}