package com.widget.onionservices.widgetManager;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import com.hiddenservices.onionservices.R;
import com.widget.onionservices.helperMethod.helperMethod;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class widgetController extends AppWidgetProvider {

    /*Model Declaration*/
    private widgetViewController mWidgetViewController;
    private widgetModelController mWidgetModelController;

    /* Local Variables */

    private int mCurrentWidth;
    private RemoteViews mViews;

    /* Navigator Initializations */

    private void initializeAppWidget(Context pContext, AppWidgetManager pAppWidgetManager, int pAppWidgetId) {
        initializeModel(pContext);
        initializeLocalEventHandler(pContext);
        pAppWidgetManager.updateAppWidget(pAppWidgetId, mViews);
    }

    private void initializeLocalEventHandler(Context pContext) {
        mViews.setOnClickPendingIntent(R.id.pTextInvoker, helperMethod.onCreatePendingIntent(pContext, PendingIntent.FLAG_IMMUTABLE, "mOpenApplication"));
        mViews.setOnClickPendingIntent(R.id.pVoiceInvoker, helperMethod.onCreatePendingIntent(pContext, PendingIntent.FLAG_IMMUTABLE, "mOpenVoice"));
    }

    private void initializeModel(Context pContext) {
        mViews = new RemoteViews(pContext.getPackageName(), R.layout.widget_search_controller);
        mWidgetViewController = new widgetViewController(pContext, new widgetViewCallback(), mViews);
        mWidgetModelController = new widgetModelController(new widgetModelCallback());
    }

    public void onReceive(Context context, Intent intent) {
        initializeModel(context);
        mWidgetModelController.onTrigger(widgetEnums.eModelViewController.M_ON_RECEIVE, Arrays.asList(context, intent));
    }

    /* Local Overrides */

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle widgetInfo) {
        mCurrentWidth = widgetInfo.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        onUpdate(context, appWidgetManager, new int[]{appWidgetId});
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        initializeModel(context);
        if (mCurrentWidth != 0) {
            int size = (int) (double) ((mCurrentWidth - 30) / 70);
            mWidgetViewController.onTrigger(widgetEnums.eWidgetViewController.M_INIT, Collections.singletonList(size));
            appWidgetManager.updateAppWidget(appWidgetIds, mViews);
        } else {
            initializeAppWidget(context, appWidgetManager, appWidgetIds[0]);
        }
    }

    /* Callbacks */

    public class widgetViewCallback implements com.hiddenservices.onionservices.eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> pData, Object pCommand) {
            return null;
        }
    }

    public class widgetModelCallback implements com.hiddenservices.onionservices.eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> pData, Object pCommand) {
            if (pCommand.equals(widgetEnums.eWidgetControllerCallback.M_UPDATE)) {
                onUpdate((Context) pData.get(0), (AppWidgetManager) pData.get(1), (int[]) pData.get(2));
            } else if (pCommand.equals(widgetEnums.eWidgetControllerCallback.M_OPTION_CHANGE)) {
                onAppWidgetOptionsChanged((Context) pData.get(0), (AppWidgetManager) pData.get(1), (int) pData.get(2), (Bundle) pData.get(3));
            } else if (pCommand.equals(widgetEnums.eWidgetControllerCallback.M_RESTORE)) {
                onRestored((Context) pData.get(0), (int[]) pData.get(1), (int[]) pData.get(2));
            } else if (pCommand.equals(widgetEnums.eWidgetControllerCallback.M_DELETE)) {
                onDeleted((Context) pData.get(0), (int[]) pData.get(1));
            }
            return null;
        }
    }
}