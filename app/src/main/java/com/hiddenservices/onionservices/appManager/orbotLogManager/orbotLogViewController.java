package com.hiddenservices.onionservices.appManager.orbotLogManager;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.sharedUIMethod;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

class orbotLogViewController {
    /*Private Variables*/

    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;

    private TextView mOrbotLogLoadingText;
    private RecyclerView mOrbotLogRecycleView;
    private NestedScrollView mOrbotLogNestedScroll;
    private FloatingActionButton mOrbotLogFloatingToolbar;

    /*Initializations*/

    orbotLogViewController(AppCompatActivity pContext, eventObserver.eventListener pEvent, TextView pOrbotLogLoadingText, RecyclerView pOrbotLogRecycleView, NestedScrollView pOrbotLogNestedScroll, FloatingActionButton pOrbotLogFloatingToolbar) {
        this.mContext = pContext;
        this.mEvent = pEvent;
        this.mOrbotLogLoadingText = pOrbotLogLoadingText;
        this.mOrbotLogRecycleView = pOrbotLogRecycleView;
        this.mOrbotLogNestedScroll = pOrbotLogNestedScroll;
        this.mOrbotLogFloatingToolbar = pOrbotLogFloatingToolbar;

        initPostUI();
    }

    private void initViews(boolean pLogThemeStyleAdvanced) {
        if (pLogThemeStyleAdvanced) {
            mOrbotLogRecycleView.setVisibility(View.VISIBLE);
            mOrbotLogLoadingText.setVisibility(View.GONE);
        } else {
            mOrbotLogRecycleView.setVisibility(View.GONE);
            mOrbotLogLoadingText.setVisibility(View.VISIBLE);
        }
    }

    /*Helper Methods*/

    private void initPostUI() {
        sharedUIMethod.updateStatusBar(mContext);
    }

    private void onUpdateLogs(String pLogs) {
        pLogs = "~ " + pLogs;
        mOrbotLogLoadingText.setText(String.format("%s%s", mOrbotLogLoadingText.getText(), "\n\n" + pLogs));
    }

    private void onFloatButtonUpdate() {
        if (mOrbotLogNestedScroll.canScrollVertically(enums.ScrollDirection.VERTICAL)) {
            mOrbotLogFloatingToolbar.setVisibility(View.VISIBLE);
            mOrbotLogFloatingToolbar.animate().setDuration(250).alpha(1);
        } else {
            mOrbotLogFloatingToolbar.animate().cancel();
            mOrbotLogFloatingToolbar.animate().setDuration(250).alpha(0).withEndAction(() -> mOrbotLogFloatingToolbar.setVisibility(View.GONE));
        }
    }

    private void onShowFloatingToolbar() {
        mOrbotLogFloatingToolbar.setVisibility(View.VISIBLE);
        mOrbotLogFloatingToolbar.animate().setDuration(250).alpha(1);
    }

    private void onScrollToTop() {
        mOrbotLogNestedScroll.stopNestedScroll();
        mOrbotLogNestedScroll.scrollTo(0, 0);
        mOrbotLogNestedScroll.smoothScrollTo(0, 0);
    }

    private void onScrollToBottom() {
        mOrbotLogNestedScroll.stopNestedScroll();
    }

    private void onScrollToSize(int pSize) {
        mOrbotLogNestedScroll.stopNestedScroll();
        mOrbotLogNestedScroll.scrollTo(0, pSize);
    }

    /*Triggers*/

    public void onTrigger(orbotLogEnums.eOrbotLogViewCommands pCommands, List<Object> pData) {
        if (pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS)) {
            onUpdateLogs((String) pData.get(0));
        } else if (pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_INIT_VIEWS)) {
            initViews((boolean) pData.get(0));
        } else if (pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_SCROLL_TO_POSITION)) {
            onScrollToSize((int) pData.get(0));
        }
    }

    public void onTrigger(orbotLogEnums.eOrbotLogViewCommands pCommands) {
        if (pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_SCROLL_TOP)) {
            onScrollToTop();
        } else if (pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_SCROLL_BOTTOM)) {
            onScrollToBottom();
        } else if (pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_FLOAT_BUTTON_UPDATE)) {
            onFloatButtonUpdate();
        } else if (pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_SHOW_FLOATING_TOOLBAR)) {
            onShowFloatingToolbar();
        }
    }
}
