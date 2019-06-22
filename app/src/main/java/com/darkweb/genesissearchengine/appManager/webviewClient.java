package com.darkweb.genesissearchengine.appManager;

import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.pluginManager.fabricManager;
import com.darkweb.genesissearchengine.pluginManager.orbot_manager;

public class webviewClient
{
    boolean isGeckoView = false;

    public void loadWebViewClient(WebView webview)
    {
        WebViewClient client = new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url)
            {
                isGeckoView = false;
                if(url.equals(view.getUrl()) && !app_model.getInstance().getAppInstance().isInternetErrorOpened() || url.contains("https://boogle.store/search?q=&"))
                {
                    app_model.getInstance().getAppInstance().onProgressBarUpdateView(0);
                    return true;
                }
                if(!url.contains("boogle"))
                {
                    fabricManager.getInstance().sendEvent("BASE SIMPLE SEARCHED : " + url);
                    isGeckoView = true;
                    if(orbot_manager.getInstance().initOrbot(url))
                    {
                        app_model.getInstance().getAppInstance().onloadURL(url,true);
                    }
                    return true;
                }
                else
                {
                    fabricManager.getInstance().sendEvent("BASE ONION SEARCHED : " + url);
                    app_model.getInstance().getAppInstance().onRequestTriggered(false,url);
                    return false;
                }
            }
            @Override
            public void onPageFinished(WebView  view, String  url)
            {
                super.onPageFinished(view, url);
                app_model.getInstance().getAppInstance().onPageFinished(false);
                app_model.getInstance().getAppInstance().onUpdateSearchBarView(url);
                app_model.getInstance().getAppInstance().onProgressBarUpdateView(0);
                status.isApplicationLoaded = true;
                fabricManager.getInstance().sendEvent("BASE SUCCESSFULLY LOADED : " + url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                fabricManager.getInstance().sendEvent("BASE URL ERROR : " + failingUrl + "--" + description);
                app_model.getInstance().getAppInstance().onInternetErrorView();
            }
        };

        webview.setWebViewClient(client);

        webview.setWebChromeClient(new WebChromeClient()
        {
            public void onProgressChanged(WebView view, int newProgress)
            {
                if(!isGeckoView)
                {
                    if(newProgress<95 && newProgress>5)
                    {
                        app_model.getInstance().getAppInstance().onProgressBarUpdateView(newProgress);
                    }
                    else if(newProgress<=5)
                    {
                        app_model.getInstance().getAppInstance().onProgressBarUpdateView(4);
                    }
                }
            }
        });
    }
}
