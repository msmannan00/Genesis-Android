package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.sharedUIMethod;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

class orbotLogViewController
{
    /*Private Variables*/

    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;

    private TextView mLogs;
    private RecyclerView mLogRecycleView;
    private NestedScrollView mNestedScrollView;
    private FloatingActionButton mFloatingScroller;

    /*Initializations*/

    orbotLogViewController(AppCompatActivity pContext, eventObserver.eventListener pEvent, TextView pLogs, RecyclerView pLogRecycleView, NestedScrollView pNestedScrollView, FloatingActionButton pFloatingScroller)
    {
        this.mContext = pContext;
        this.mLogs = pLogs;
        this.mLogRecycleView = pLogRecycleView;
        this.mNestedScrollView = pNestedScrollView;
        this.mFloatingScroller = pFloatingScroller;
        this.mEvent = pEvent;

        initPostUI();
    }

    private void initViews(boolean pLogThemeStyleAdvanced){
        if(pLogThemeStyleAdvanced){
            mLogRecycleView.setVisibility(View.VISIBLE);
            mLogs.setVisibility(View.GONE);
        }else {
            mLogRecycleView.setVisibility(View.GONE);
            mLogs.setVisibility(View.VISIBLE);
        }
    }

    /*Helper Methods*/

    private void initPostUI(){
        sharedUIMethod.updateStatusBar(mContext);
    }

    private void onUpdateLogs(String pLogs){
        pLogs = "~ " + pLogs;
        mLogs.setText(String.format("%s%s",mLogs.getText() ,pLogs + "\n\n"));
    }

    private void onScrollThemeUpdate(){
        if(mNestedScrollView.canScrollVertically(1)){
            if(mFloatingScroller.getAlpha()==0){
                mFloatingScroller.setVisibility(View.VISIBLE);
                mFloatingScroller.animate().cancel();
                mFloatingScroller.animate().alpha(1);
            }
        }else {
            mFloatingScroller.animate().cancel();
            mFloatingScroller.animate().alpha(0).withEndAction(() -> mFloatingScroller.setVisibility(View.GONE));
        }
    }


    /*Triggers*/

    public void onTrigger(orbotLogEnums.eOrbotLogViewCommands pCommands, List<Object> pData){
        if(pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS)){
            onUpdateLogs((String) pData.get(0));
        }
        else if(pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_INIT_VIEWS)){
            initViews((boolean)pData.get(0));
        }
        else if(pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_SCROLL_THEME_UPDATE)){
            onScrollThemeUpdate();
        }
    }

    public void onTrigger(orbotLogEnums.eOrbotLogViewCommands pCommands){
        onTrigger(pCommands, null);
    }
}
