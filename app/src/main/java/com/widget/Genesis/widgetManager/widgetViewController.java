package com.widget.Genesis.widgetManager;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;
import com.darkweb.genesissearchengine.eventObserver;
import com.example.myapplication.R;

import java.util.List;

public class widgetViewController extends AppWidgetProvider {

    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private Context mContext;
    private RemoteViews mViews;

    /*Initializations*/

    widgetViewController(Context pContext, eventObserver.eventListener pEvent, RemoteViews pViews)
    {
        this.mEvent = pEvent;
        this.mContext = pContext;
        this.mViews = pViews;
    }

    private void initialize(int pSize){
        if(pSize<=3){
            mViews.setViewVisibility(R.id.pVoiceInput, View.GONE);
            mViews.setViewVisibility(R.id.pSearchInputWidget, View.GONE);
        }else {
            mViews.setViewVisibility(R.id.pVoiceInput, View.VISIBLE);
            mViews.setViewVisibility(R.id.pSearchInputWidget, View.VISIBLE);
        }
    }

    public Object onTrigger(widgetEnums.eWidgetViewController pCommands, List<Object> pData){
        if(pCommands.equals(widgetEnums.eWidgetViewController.M_INIT)){
            initialize((int)pData.get(0));
        }
        return null;
    }

}