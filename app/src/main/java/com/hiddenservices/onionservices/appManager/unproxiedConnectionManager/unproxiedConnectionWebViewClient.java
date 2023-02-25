package com.hiddenservices.onionservices.appManager.unproxiedConnectionManager;

import android.graphics.Bitmap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.hiddenservices.onionservices.eventObserver;

import java.util.Collections;

public class unproxiedConnectionWebViewClient extends android.webkit.WebViewClient {

    private eventObserver.eventListener mEvent;

    public unproxiedConnectionWebViewClient(eventObserver.eventListener pEvent) {
        mEvent = pEvent;
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url){
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        mEvent.invokeObserver(Collections.singletonList(true), unproxiedConnectionEnums.eAdvertClientCallback.M_UPDATE_PROGRESSBAR);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        mEvent.invokeObserver(Collections.singletonList(false), unproxiedConnectionEnums.eAdvertClientCallback.M_UPDATE_PROGRESSBAR);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        mEvent.invokeObserver(Collections.singletonList(false), unproxiedConnectionEnums.eAdvertClientCallback.M_UPDATE_PROGRESSBAR);
    }

}
