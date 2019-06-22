package com.darkweb.genesissearchengine.appManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.darkweb.genesissearchengine.*;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
import com.darkweb.genesissearchengine.pluginManager.*;

import com.example.myapplication.R;
import org.mozilla.geckoview.GeckoView;


public class application_controller extends AppCompatActivity
{
    /*View Webviews*/
    private WebView webView;
    private GeckoView geckoView;

    /*View Objects*/
    private ProgressBar progressBar;
    private ConstraintLayout requestFailure;
    private ConstraintLayout splashScreen;
    private EditText searchbar;
    private FloatingActionButton floatingButton;
    private ImageView loadingIcon;
    private ImageView splashlogo;

    /*Redirection Objects*/
    private geckoClients geckoclient = null;
    private webviewClient webviewclient = null;
    private eventHandler eventhandler = null;

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_view);

        if(helperMethod.isBuildValid())
        {
            initializeAppModel();
            initializeCrashlytics();
            initializeConnections();
            initializeWebView();
            initializeLocalEventHandlers();

            preference_manager.getInstance().initialize();
            orbot_manager.getInstance().reinitOrbot();
            admanager.getInstance().initialize();
            applicationViewController.getInstance().initialization(webView,progressBar,searchbar,splashScreen,requestFailure,floatingButton, loadingIcon,splashlogo);
            geckoclient.initialize(geckoView);

            startApplication();
        }
        else
        {
            setContentView(R.layout.invalid_setup);
            message_manager.getInstance().abiError(Build.SUPPORTED_ABIS[0]);
        }

    }

    public void startApplication()
    {
        fabricManager.getInstance().sendEvent("HOME PAGE LOADING : " );
        webView.loadUrl(constants.backendUrl);
    }

    public void initializeAppModel()
    {
        app_model.getInstance().setAppContext(this);
        app_model.getInstance().setAppInstance(this);
    }

    public void initializeConnections()
    {
        webView = findViewById(R.id.pageLoader1);
        geckoView = findViewById(R.id.webLoader);

        progressBar = findViewById(R.id.progressBar);
        requestFailure = findViewById(R.id.requestFailure);
        splashScreen = findViewById(R.id.splashScreen);
        searchbar = findViewById(R.id.search);
        floatingButton = findViewById(R.id.floatingActionButton3);
        loadingIcon = findViewById(R.id.imageView_loading_back);
        splashlogo = findViewById(R.id.backsplash);
        geckoView = findViewById(R.id.webLoader);

        webviewclient = new webviewClient();
        geckoclient = new geckoClients();
        eventhandler = new eventHandler();
    }

    public void initializeCrashlytics()
    {
        fabricManager.getInstance().init();
    }

    public void initializeWebView()
    {
        setWebViewSettings(webView);
        webviewclient.loadWebViewClient(webView);
    }

    public void setWebViewSettings(WebView view)
    {
        view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        view.getSettings().setUseWideViewPort(true);
    }

    /*------------------------------------------------------- Event Handler ----------------------------------------------------*/

    private void initializeLocalEventHandlers()
    {
        searchbar.setOnEditorActionListener((v, actionId, event) ->
        {
            return eventhandler.onEditorClicked(v,actionId,event);
        });
    }

    public void onReloadButtonPressed(View view)
    {
       eventhandler.onReloadButtonPressed(view);
    }

    @Override
    public void onBackPressed()
    {
        eventhandler.onBackPressed();
    }

    public void onFloatingButtonPressed(View view)
    {
        eventhandler.onFloatingButtonPressed();
    }

    public void onHomeButtonPressed(View view)
    {
        eventhandler.onHomeButtonPressed();
    }

    /*-------------------------------------------------------Helper Method In UI Redirection----------------------------------------------------*/

    public void onloadURL(String url,boolean isHiddenWeb)
    {
        if(isHiddenWeb)
        {
            geckoclient.loadGeckoURL(url,geckoView);
        }
        else
        {
            webView.loadUrl(url);
            onRequestTriggered(isHiddenWeb,url);
        }
    }

    public void onRequestTriggered(boolean isHiddenWeb,String url)
    {
        applicationViewController.getInstance().onRequestTriggered(isHiddenWeb,url);
    }

    public void onClearSearchBarCursorView()
    {
        applicationViewController.getInstance().onClearSearchBarCursor();
    }

    public void onUpdateSearchBarView(String url)
    {
        applicationViewController.getInstance().onUpdateSearchBar(url);
    }

    public void onInternetErrorView()
    {
        applicationViewController.getInstance().onInternetError();
        applicationViewController.getInstance().disableFloatingView();
    }

    public void onDisableInternetError()
    {
        applicationViewController.getInstance().onDisableInternetError();
    }

    public void onProgressBarUpdateView(int progress)
    {
        applicationViewController.getInstance().onProgressBarUpdate(progress);
    }

    public void onBackPressedView()
    {
        applicationViewController.getInstance().onBackPressed();
    }

    public void onPageFinished(boolean isHidden)
    {
        applicationViewController.getInstance().onPageFinished(isHidden);
    }

    public void onUpdateView(boolean status)
    {
        applicationViewController.getInstance().onUpdateView(status);
    }

    public void onReload()
    {
        applicationViewController.getInstance().onReload();
    }


    /*-------------------------------------------------------Helper Method Out UI Redirection----------------------------------------------------*/

    public void onHiddenGoBack()
    {
        geckoclient.onHiddenGoBack(geckoView);
    }

    public void stopHiddenView()
    {
        geckoclient.stopHiddenView(geckoView);
    }

    public void onReloadHiddenView()
    {
        geckoclient.onReloadHiddenView();
    }

    public boolean isGeckoViewRunning()
    {
       return geckoclient.isGeckoViewRunning();
    }

    public void forcedCachePatch()
    {
    }

    public boolean isInternetErrorOpened()
    {
        return requestFailure.getAlpha()==1;
    }

}

