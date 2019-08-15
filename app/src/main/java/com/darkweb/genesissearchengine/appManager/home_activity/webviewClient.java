package com.darkweb.genesissearchengine.appManager.home_activity;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperMethod;
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
                if(url.contains("advert"))
                {
                    home_model.getInstance().getHomeInstance().onProgressBarUpdateView(0);
                    helperMethod.openPlayStore(url.split("__")[1]);
                    return true;
                }

                isGeckoView = false;
                if(home_model.getInstance().isUrlRepeatable(url,view.getUrl()))
                {
                    home_model.getInstance().getHomeInstance().onProgressBarUpdateView(0);
                    return true;
                }
                if(!url.contains("boogle"))
                {
                    home_model.getInstance().getHomeInstance().stopHiddenView(false,false);
                    fabricManager.getInstance().sendEvent("BASE SIMPLE SEARCHED : " + url);
                    isGeckoView = true;
                    if(orbot_manager.getInstance().initOrbot(url))
                    {
                        home_model.getInstance().getHomeInstance().onloadURL(url,true,true);
                    }
                    return true;
                }
                else
                {
                    /* if(url.startsWith("https://boogle.store/search?"))
                    {
                        url = url.replace("https://boogle.store/search?q=random&p_num=1&s_type=image","https://duckduckgo.com/?q=onion+links+websites&iar=images&iax=images&ia=images");
                        url = url.replace("boogle.store/search?","duckduckgo.com/?");
                        url = url.replace("q=","q=onion+links+");
                        url = url.replace("&s_type=image","&ia=images&iax=images");

                        home_model.getInstance().getHomeInstance().stopHiddenView(false,false);
                        fabricManager.getInstance().sendEvent("BASE SIMPLE SEARCHED : " + url);
                        isGeckoView = true;
                        if(orbot_manager.getInstance().initOrbot(url))
                        {
                            home_model.getInstance().getHomeInstance().onloadURL(url,true,true);
                        }
                        return true;
                    }
                    else
                    {
                        home_model.getInstance().addNavigation(url,enums.navigationType.base);
                        home_model.getInstance().addHistory(url);
                        fabricManager.getInstance().sendEvent("BASE ONION SEARCHED : " + url);
                        home_model.getInstance().getHomeInstance().onRequestTriggered(false,url);
                    } */
                    home_model.getInstance().addNavigation(url,enums.navigationType.base);
                    home_model.getInstance().addHistory(url);
                    fabricManager.getInstance().sendEvent("BASE ONION SEARCHED : " + url);
                    home_model.getInstance().getHomeInstance().onRequestTriggered(false,url);
                    return false;
                }
            }
            @Override
            public void onPageFinished(WebView  view, String  url)
            {
                super.onPageFinished(view, url);
                home_model.getInstance().getHomeInstance().onPageFinished(false);
                home_model.getInstance().getHomeInstance().onUpdateSearchBarView(url);
                home_model.getInstance().getHomeInstance().onProgressBarUpdateView(0);
                status.isApplicationLoaded = true;
                fabricManager.getInstance().sendEvent("BASE SUCCESSFULLY LOADED : " + url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                fabricManager.getInstance().sendEvent("BASE URL ERROR : " + failingUrl + "--" + description);
                home_model.getInstance().getHomeInstance().onInternetErrorView();
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
                        home_model.getInstance().getHomeInstance().onProgressBarUpdateView(newProgress);
                    }
                    else if(newProgress<=5)
                    {
                        home_model.getInstance().getHomeInstance().onProgressBarUpdateView(4);
                    }
                }
            }
        });
    }
}
