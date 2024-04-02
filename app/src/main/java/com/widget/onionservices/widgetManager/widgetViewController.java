package com.widget.onionservices.widgetManager;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.R;

import java.util.List;

public class widgetViewController extends AppWidgetProvider {

    /*Private Variables*/

    private RemoteViews mViews;

    /*Initializations*/

    widgetViewController(Context ignoredPContext, eventObserver.eventListener ignoredPEvent, RemoteViews pViews) {
        this.mViews = pViews;
    }

    private void initialize() {
        mViews.setViewVisibility(R.id.pVoiceInput, View.VISIBLE);
        mViews.setViewVisibility(R.id.pSearchInputWidget, View.VISIBLE);
    }

    public Object onTrigger(widgetEnums.eWidgetViewController pCommands, List<Object> pData) {
        if (pCommands.equals(widgetEnums.eWidgetViewController.M_INIT)) {
            initialize();
        }
        return null;
    }

}