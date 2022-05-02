package com.hiddenservices.onionservices.appManager.orionAdvertManager;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.sharedUIMethod;

import java.util.List;

public class orionAdvertViewController extends AppCompatActivity {

    private WebView mWebView;
    private AppCompatActivity mContext;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adview_controller);
    }

    orionAdvertViewController(AppCompatActivity pContext, eventObserver.eventListener pEvent, WebView pGeckoView, ProgressBar pProgressBar) {
        this.mContext = pContext;
        this.mWebView = pGeckoView;
        this.mProgressBar = pProgressBar;

        initViews();
    }

    private void initViews() {
        sharedUIMethod.updateStatusBar(mContext);
    }

    public void onUpdateProgressBar(boolean status) {
        if (status) {
            mProgressBar.animate().setDuration(350).alpha(1);
        } else {
            mProgressBar.animate().setDuration(350).alpha(0);
        }
    }

    public void onTrigger(orionAdvertEnums.eOrionAdvertViewController pCommands, List<Object> pData) {
        if (pCommands.equals(orionAdvertEnums.eOrionAdvertViewController.M_UPDATE_PROGRESSBAR)) {
            onUpdateProgressBar((boolean) pData.get(0));
        }
    }

    public void onTrigger(orionAdvertEnums.eOrionAdvertViewController pCommands) {
    }

}

