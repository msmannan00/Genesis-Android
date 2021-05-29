package com.widget.search;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RemoteViews;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.R;

import static com.darkweb.genesissearchengine.constants.constants.CONST_PACKAGE_NAME;
import static com.darkweb.genesissearchengine.constants.constants.CONST_WIDGET_NAME;

public class searchWidgetManager extends AppWidgetProvider {

    /* Local Variables */

    private static final String SHARED_PREF_FILE = CONST_WIDGET_NAME;
    private static final String COUNT_KEY = "count";
    private static int mCurrentWidth = -1;

    /* Navigator Initializations */

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
        switch (action) {
            case enums.WidgetCommands.OPEN_APPLICATION: {
                status.sWidgetResponse = enums.WidgetResponse.SEARCHBAR;
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(CONST_PACKAGE_NAME);
                launchIntent.putExtra(enums.WidgetCommands.OPEN_APPLICATION, true);
                context.startActivity(launchIntent);
                break;
            }
            case enums.WidgetCommands.OPEN_VOICE: {
                status.sWidgetResponse = enums.WidgetResponse.VOICE;
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(CONST_PACKAGE_NAME);
                launchIntent.putExtra(enums.WidgetCommands.OPEN_APPLICATION, true);
                context.startActivity(launchIntent);
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
                if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)
                        && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS)) {
                    int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                    Bundle widgetExtras = extras.getBundle(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS);
                    this.onAppWidgetOptionsChanged(context, AppWidgetManager.getInstance(context),
                            appWidgetId, widgetExtras);
                }
                break;
            }
            case AppWidgetManager.ACTION_APPWIDGET_ENABLED:
                this.onEnabled(context);
                break;
            case AppWidgetManager.ACTION_APPWIDGET_DISABLED:
                this.onDisabled(context);
                break;
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

    /* Local Overrides */

    @Override
    public void onAppWidgetOptionsChanged (Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle widgetInfo) {
        int width = widgetInfo.getInt (AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        mCurrentWidth = width;
        int[] appWidgetIds = new int[1];
        appWidgetIds[0] = appWidgetId;
        onUpdate(context, appWidgetManager, appWidgetIds);
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

    /* Helper Methods */

    private int getColsNum (int size) {
        return (int) Math.floor ((size - 30) / 70);
    }

}