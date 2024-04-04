package com.hiddenservices.onionservices.appManager.helpManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.R;

import java.util.List;

class helpViewController {
    /*ViewControllers*/
    private AppCompatActivity mContext;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ConstraintLayout mRetryContainer;
    private Button mReloadButton;

    void initialization(eventObserver.eventListener ignoredEvent, AppCompatActivity context, ProgressBar pProgressBar, RecyclerView pRecyclerView, ConstraintLayout pRetryContainer, Button pReloadButton) {
        this.mContext = context;
        this.mProgressBar = pProgressBar;
        this.mRecyclerView = pRecyclerView;
        this.mRetryContainer = pRetryContainer;
        this.mReloadButton = pReloadButton;
    }

    protected void onInit(){

    }

    private void initPostUI() {
        Window window = mContext.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.blue_dark));
            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
        } else {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
        }
    }

    private void onDataLoaded() {
        mProgressBar.animate().cancel();
        mRetryContainer.animate().cancel();
        mRecyclerView.animate().cancel();

        mRecyclerView.animate().setDuration(300).alpha(1);
        mProgressBar.animate().setDuration(300).alpha(0);
    }

    private void onLoadError() {
        mRecyclerView.animate().setDuration(300).alpha(0);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setAlpha(1);

        mRetryContainer.bringToFront();
        mProgressBar.animate().setDuration(300).setStartDelay(200).alpha(0).withEndAction(() -> {
            mRetryContainer.setAlpha(0);
            mRetryContainer.animate().setDuration(300).alpha(1).withEndAction(() -> mReloadButton.setClickable(true));
            mRetryContainer.setVisibility(View.VISIBLE);
        });
    }

    private void onReloadData() {
        mRecyclerView.animate().setDuration(300).alpha(0);
        mReloadButton.setClickable(false);
        mRetryContainer.animate().cancel();
        mProgressBar.animate().cancel();
        mProgressBar.animate().setDuration(300).alpha(1);
        mRetryContainer.animate().setDuration(200).alpha(0).withEndAction(() -> mRetryContainer.setVisibility(View.GONE));
    }

    public void onTrigger(helpEnums.eHelpViewController pCommands, List<Object> ignoredPData) {
        if (pCommands.equals(helpEnums.eHelpViewController.M_INIT_VIEWS)) {
            initPostUI();
        } else if (pCommands.equals(helpEnums.eHelpViewController.M_DATA_LOADED)) {
            onDataLoaded();
        } else if (pCommands.equals(helpEnums.eHelpViewController.M_LOAD_ERROR)) {
            onLoadError();
        } else if (pCommands.equals(helpEnums.eHelpViewController.M_RELOAD_DATA)) {
            onReloadData();
        }
    }

}
