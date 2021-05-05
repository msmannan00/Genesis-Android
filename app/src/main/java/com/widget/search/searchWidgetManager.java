package com.widget.search;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;

public class searchWidgetManager extends AppWidgetProvider {

    private static final String SHARED_PREF_FILE = "com.example.android.appwidgetsample";
    private static final String COUNT_KEY = "count";
    private static int mCurrentWidth = -1;

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_FILE, 0);
        int count = prefs.getInt(COUNT_KEY + appWidgetId, 0);
        count++;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_search_controller);

        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putInt(COUNT_KEY + appWidgetId, count);
        prefEditor.apply();

        int[] idArray = new int[]{appWidgetId};
        Intent intentUpdate = new Intent(context, searchWidgetManager.class);
        intentUpdate.setAction("mOpenApplication");
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(context, appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent mintentUpdate = new Intent(context, searchWidgetManager.class);
        mintentUpdate.setAction("mOpenVoice");
        mintentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
        PendingIntent mpintentUpdate = PendingIntent.getBroadcast(context, appWidgetId, mintentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.pSearchLogo, pendingUpdate);
        views.setOnClickPendingIntent(R.id.pTopBarContainer, pendingUpdate);
        views.setOnClickPendingIntent(R.id.pSearchInputWidget, pendingUpdate);
        views.setOnClickPendingIntent(R.id.pVoiceInput, mpintentUpdate);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(action.equals("mOpenApplication")){
            status.sWidgetResponse = enums.WidgetResponse.SEARCHBAR;
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.darkweb.genesissearchengine");
            launchIntent.putExtra("mOpenApplication",true);
            context.startActivity(launchIntent);
        }
        else if(action.equals("mOpenVoice")){
            status.sWidgetResponse = enums.WidgetResponse.VOICE;
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.darkweb.genesissearchengine");
            launchIntent.putExtra("mOpenApplication",true);
            context.startActivity(launchIntent);
        }
        else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                final int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                this.onDeleted(context, new int[] { appWidgetId });
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)
                    && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS)) {
                int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                Bundle widgetExtras = extras.getBundle(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS);
                this.onAppWidgetOptionsChanged(context, AppWidgetManager.getInstance(context),
                        appWidgetId, widgetExtras);
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)) {
            this.onEnabled(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {
            this.onDisabled(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_RESTORED.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int[] oldIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_OLD_IDS);
                int[] newIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (oldIds != null && oldIds.length > 0) {
                    this.onRestored(context, oldIds, newIds);
                    this.onUpdate(context, AppWidgetManager.getInstance(context), newIds);
                }
            }
        }
    }

    @Override
    public void onAppWidgetOptionsChanged (Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle widgetInfo) {
        int width = widgetInfo.getInt (AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        mCurrentWidth = width;
        int[] appWidgetIds = new int[1];
        appWidgetIds[0] = appWidgetId;
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private int getColsNum (int size) {
        return (int) Math.floor ((size - 30) / 70);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            int size = getColsNum(mCurrentWidth);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_search_controller);
            if(mCurrentWidth!=-1){
                if(size<=2){
                    views.setTextColor(R.id.pSearchInputWidget, Color.WHITE);
                }else {
                    views.setTextColor(R.id.pSearchInputWidget, Color.GRAY);
                }
                appWidgetManager.updateAppWidget(appWidgetIds, views);
            }
        }
    }
}