package com.darkweb.genesissearchengine.appManager;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.darkweb.genesissearchengine.constants.constants;

public class applicationViewController
{
    /*ViewControllers*/
    private WebView webView1;
    private WebView webView2;
    private ProgressBar progressBar;
    private EditText searchbar;
    private ConstraintLayout splashScreen;
    private ConstraintLayout requestFailure;

    /*Initializations*/
    private static final applicationViewController ourInstance = new applicationViewController();
    public static applicationViewController getInstance()
    {
        return ourInstance;
    }

    private applicationViewController()
    {
    }

    public void initialization(WebView webView1,WebView webView2,ProgressBar progressBar,EditText searchbar,ConstraintLayout splashScreen,ConstraintLayout requestFailure)
    {
        this.webView1 = webView1;
        this.webView2 = webView2;
        this.progressBar = progressBar;
        this.searchbar = searchbar;
        this.splashScreen = splashScreen;
        this.requestFailure = requestFailure;
    }

    public void onRequestURL()
    {
        webView1.stopLoading();
        webView2.stopLoading();

        progressBar.setProgress(5);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate().setDuration(150).alpha(1f);
    }

    /*Helper Methods*/
    public void onInternetError()
    {
        splashScreen.animate().setStartDelay(2000).alpha(0);
        progressBar.animate().alpha(0);

        requestFailure.setVisibility(View.VISIBLE);
        requestFailure.animate().alpha(1f).setDuration(300);
    }

    public void loadUrlOnWebview(String html)
    {
        WebView webView = getCurrentView();
        webView.bringToFront();
        webView.loadDataWithBaseURL(app_model.getInstance().getCurrentURL(),html, "text/html", "utf-8", null);
    }

    public WebView getCurrentView()
    {
        WebView tempView;
        if(webView1.getAlpha()==0)
        {
            tempView = webView1;
        }
        else
        {
            tempView = webView2;
        }
        return tempView;
    }

}
