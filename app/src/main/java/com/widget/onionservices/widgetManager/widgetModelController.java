package com.widget.onionservices.widgetManager;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.widget.onionservices.helperMethod.helperMethod;
import java.util.Arrays;
import java.util.List;
import static com.hiddenservices.onionservices.constants.constants.CONST_PACKAGE_NAME;

public class widgetModelController {

    /*Private Variables*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    widgetModelController(eventObserver.eventListener pEvent)
    {
        this.mEvent = pEvent;
    }

    private void initialize(Context context, Intent intent){
        String action = intent.getAction();
        Log.i("22adsasdasddas","22asdasdsadasdadsasd");
        switch (action) {
            case enums.WidgetCommands.OPEN_APPLICATION: {
                Log.i("33adsasdasddas","33asdasdsadasdadsasd");
                status.sWidgetResponse = enums.WidgetResponse.SEARCHBAR;
                helperMethod.onStartApplication(context, CONST_PACKAGE_NAME);
                break;
            }
            case enums.WidgetCommands.OPEN_VOICE: {
                Log.i("44adsasdasddas","44asdasdsadasdadsasd");
                status.sWidgetResponse = enums.WidgetResponse.VOICE;
                helperMethod.onStartApplication(context, CONST_PACKAGE_NAME);
                break;
            }
            case AppWidgetManager.ACTION_APPWIDGET_UPDATE: {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                    if (appWidgetIds != null && appWidgetIds.length > 0) {
                        mEvent.invokeObserver(Arrays.asList(context, AppWidgetManager.getInstance(context), appWidgetIds), widgetEnums.eWidgetControllerCallback.M_UPDATE);
                    }
                }
                break;
            }
            case AppWidgetManager.ACTION_APPWIDGET_DELETED: {
                Bundle extras = intent.getExtras();
                if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                    final int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                    mEvent.invokeObserver(Arrays.asList(context, new int[]{appWidgetId}), widgetEnums.eWidgetControllerCallback.M_DELETE);
                }
                break;
            }
            case AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED: {
                Bundle extras = intent.getExtras();
                if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)&& extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS)) {
                    int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                    Bundle widgetExtras = extras.getBundle(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS);
                    mEvent.invokeObserver(Arrays.asList(context, AppWidgetManager.getInstance(context),appWidgetId, widgetExtras), widgetEnums.eWidgetControllerCallback.M_OPTION_CHANGE);
                }
                break;
            }

            case AppWidgetManager.ACTION_APPWIDGET_RESTORED: {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int[] oldIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_OLD_IDS);
                    int[] newIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                    if (oldIds != null && oldIds.length > 0) {
                        mEvent.invokeObserver(Arrays.asList(context, oldIds, newIds), widgetEnums.eWidgetControllerCallback.M_RESTORE);
                        mEvent.invokeObserver(Arrays.asList(context, AppWidgetManager.getInstance(context), newIds), widgetEnums.eWidgetControllerCallback.M_UPDATE);
                    }
                }
                break;
            }
        }
    }

    public Object onTrigger(widgetEnums.eModelViewController pCommands, List<Object> pData){
        if(pCommands.equals(widgetEnums.eModelViewController.M_ON_RECIEVE)){
            Log.i("11adsasdasddas","11asdasdsadasdadsasd");
            initialize((Context)pData.get(0), (Intent) pData.get(1));
        }
        return null;
    }

}