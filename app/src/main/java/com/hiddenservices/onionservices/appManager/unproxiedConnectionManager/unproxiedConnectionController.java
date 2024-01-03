package com.hiddenservices.onionservices.appManager.unproxiedConnectionManager;

import static android.webkit.WebSettings.LOAD_NO_CACHE;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.hiddenservices.onionservices.R;
import com.hiddenservices.onionservices.eventObserver;
import java.util.Collections;
import java.util.List;

public class unproxiedConnectionController extends AppCompatActivity {

    WebView mWebView;
    ProgressBar mProgressBar;
    unproxiedConnectionViewController mUnproxiedConnectionViewController;
    TextView mHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adview_controller);

        onCrashInit(this);
        initializeViews();
        onInitializeController();

        onInitConnection();
    }

    private void initializeViews() {
        mWebView = findViewById(R.id.pWebView);
        mProgressBar = findViewById(R.id.mProgressBar);
        mHeader = findViewById(R.id.pHeader);
        mUnproxiedConnectionViewController = new unproxiedConnectionViewController(this, null, mProgressBar, mHeader);
        mUnproxiedConnectionViewController.onInit();
    }

    @SuppressLint("SetJavaScriptEnabled")
    void onInitConnection() {
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(LOAD_NO_CACHE);
        mWebView.clearCache(true);
    }

    public void onCrashInit(Context mContext){
    }

    private void onInitializeController() {
        String mURL = getIntent().getExtras().getString("m_url");

        if(getIntent().getExtras().containsKey("m_bridges")){
            boolean mBridge = getIntent().getExtras().getBoolean("m_bridges");
            if(mBridge){
                mUnproxiedConnectionViewController.onTrigger(unproxiedConnectionEnums.eAdvertViewController.M_UPDATE_HEADER, Collections.singletonList("Request Bridge"));
            }
        }
        System.gc();
        if(mURL.contains("play.google.com")){
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.loadUrl(mURL);
            finish();
        }else {
            mWebView.setWebViewClient(new unproxiedConnectionWebViewClient(new webivewViewCallback()));
            mWebView.loadUrl(mURL);
        }
    }

    /* Helper Methods */
    public void onClose(View view) {
        finish();
    }

    /* Callbacks */
    public class webivewViewCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> data, Object event_type) {
            if (event_type.equals(unproxiedConnectionEnums.eAdvertClientCallback.M_UPDATE_PROGRESSBAR)) {
                mUnproxiedConnectionViewController.onTrigger(unproxiedConnectionEnums.eAdvertViewController.M_UPDATE_PROGRESSBAR, Collections.singletonList(data.get(0)));
            }
            return null;
        }
    }

}