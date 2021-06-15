package com.widget.search;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.R;
import com.widget.search.helperMethod.helperMethod;
import static com.darkweb.genesissearchengine.constants.constants.CONST_PACKAGE_NAME;

public class searchWidgetManager extends AppWidgetProvider {

    /* Local Variables */

    int mCurrentWidth;

    /* Navigator Initializations */

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_search_controller);

        views.setOnClickPendingIntent(R.id.pTextInvoker, helperMethod.onCreatePendingIntent(context, appWidgetId, PendingIntent.FLAG_UPDATE_CURRENT, "mOpenApplication"));
        views.setOnClickPendingIntent(R.id.pVoiceInvoker, helperMethod.onCreatePendingIntent(context, appWidgetId, PendingIntent.FLAG_UPDATE_CURRENT, "mOpenVoice"));
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
    /* Local Overrides */

    @Override
    public void onAppWidgetOptionsChanged (Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle widgetInfo) {
        mCurrentWidth = widgetInfo.getInt (AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        onUpdate(context, appWidgetManager, new int[]{appWidgetId});
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if(mCurrentWidth!=0){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_search_controller);
            int size = (int) Math.floor ((mCurrentWidth - 30) / 70);;

            if(size<=3){
                views.setViewVisibility(R.id.pVoiceInput, View.GONE);
                views.setViewVisibility(R.id.pSearchInputWidget, View.GONE);
            }else {
                views.setViewVisibility(R.id.pVoiceInput, View.VISIBLE);
                views.setViewVisibility(R.id.pSearchInputWidget, View.VISIBLE);
            }
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }else {
            updateAppWidget(context, appWidgetManager, appWidgetIds[0]);
        }
    }
}