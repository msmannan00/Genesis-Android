package com.hiddenservices.onionservices.appManager.advertManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.example.myapplication.R;
import com.hiddenservices.onionservices.eventObserver;

import java.util.Collections;
import java.util.List;

public class advertController extends AppCompatActivity {

    WebView mWebView;
    ProgressBar mProgressBar;
    advertViewController mAdvertViewController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adview_controller);

        initializeViews();
        onInitializeAdvertisements();
        onInitAdvert();
    }

    private void initializeViews() {
        mWebView = findViewById(R.id.pWebView);
        mProgressBar = findViewById(R.id.mProgressBar);
        mAdvertViewController = new advertViewController(this, null, mWebView, mProgressBar);
    }

    void onInitAdvert() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.setWebViewClient(new advertWebViewClient(new webivewViewCallback()));
    }

    private void onInitializeAdvertisements() {
        String mURL = getIntent().getExtras().getString("m_url");
        mWebView.loadUrl(mURL);
    }

    /* Helper Methods */
    public void onClose(View view) {
        finish();
    }

    /* Callbacks */
    public class webivewViewCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> data, Object event_type) {
            if (event_type.equals(advertEnums.eAdvertClientCallback.M_UPDATE_PROGRESSBAR)) {
                mAdvertViewController.onTrigger(advertEnums.eAdvertViewController.M_UPDATE_PROGRESSBAR, Collections.singletonList(data.get(0)));
            }
            return null;
        }
    }

}