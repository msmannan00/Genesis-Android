package com.hiddenservices.onionservices.appManager.orionAdvertManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.hiddenservices.onionservices.appManager.settingManager.advertSetttings.advertSettingController;
import com.hiddenservices.onionservices.eventObserver;

import java.util.Collections;
import java.util.List;

public class orionAdvertController extends AppCompatActivity {

    WebView mWebView;
    ProgressBar mProgressBar;
    orionAdvertViewController mAdvertViewController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orion_adview_controller);

        initializeViews();
        onInitializeAdvertisements();
        onInitAdvert();
    }

    private void initializeViews() {
        mWebView = findViewById(R.id.pWebView);
        mProgressBar = findViewById(R.id.mProgressBar);
        mAdvertViewController = new orionAdvertViewController(this, null, mWebView, mProgressBar);
    }

    void onInitAdvert() {
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.setWebViewClient(new orionAdvertWebViewClient(new webivewViewCallback()));
    }

    private void onInitializeAdvertisements() {
        mWebView.loadUrl("https://360wise.com");
    }

    /* Helper Methods */
    public void onClose(View view) {
        finish();
    }

    public void onSettings(View view) {
        Intent myIntent = new Intent(this, advertSettingController.class);
        startActivity(myIntent);
    }


    /* Callbacks */
    public class webivewViewCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> data, Object event_type) {
            if (event_type.equals(orionAdvertEnums.eOrionAdvertClientCallback.M_UPDATE_PROGRESSBAR)) {
                mAdvertViewController.onTrigger(orionAdvertEnums.eOrionAdvertViewController.M_UPDATE_PROGRESSBAR, Collections.singletonList(data.get(0)));
            }
            return null;
        }
    }

}