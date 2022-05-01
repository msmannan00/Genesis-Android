package com.hiddenservices.onionservices.appManager.orionAdvertManager;

import android.graphics.Bitmap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.hiddenservices.onionservices.eventObserver;

import java.util.Collections;

public class orionAdvertWebViewClient extends android.webkit.WebViewClient {

    private eventObserver.eventListener mEvent;
    public orionAdvertWebViewClient(eventObserver.eventListener pEvent){
        mEvent = pEvent;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        mEvent.invokeObserver(Collections.singletonList(true), orionAdvertEnums.eOrionAdvertClientCallback.M_UPDATE_PROGRESSBAR);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
        mEvent.invokeObserver(Collections.singletonList(false), orionAdvertEnums.eOrionAdvertClientCallback.M_UPDATE_PROGRESSBAR);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        mEvent.invokeObserver(Collections.singletonList(false), orionAdvertEnums.eOrionAdvertClientCallback.M_UPDATE_PROGRESSBAR);
    }

}
