package com.darkweb.genesissearchengine.appManager.main_activity;

import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.darkweb.genesissearchengine.*;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
import com.darkweb.genesissearchengine.pluginManager.*;

import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private AutoCompleteTextView searchbar;
    private FloatingActionButton floatingButton;
    private ImageView loadingIcon;
    private ImageView splashlogo;
    private TextView loadingText;

    /*Redirection Objects*/
    private geckoClients geckoclient = null;
    private webviewClient webviewclient = null;
    private eventHandler eventhandler = null;

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);

        if(helperMethod.isBuildValid())
        {
            initializeAppModel();
            preference_manager.getInstance().initialize();

            status.initStatus();
            initializeCrashlytics();
            initializeConnections();
            initializeWebView();
            initializeLocalEventHandlers();
            initAdManager();

            orbot_manager.getInstance().reinitOrbot();
            applicationViewController.getInstance().initialization(webView,loadingText,progressBar,searchbar,splashScreen,requestFailure,floatingButton, loadingIcon,splashlogo);
            firebase.getInstance().initialize();
            geckoclient.initialize(geckoView);
            app_model.getInstance().initialization();

            initSearchEngine();
        }
        else
        {
            setContentView(R.layout.invalid_setup);
            message_manager.getInstance().abiError(Build.SUPPORTED_ABIS[0]);
        }

    }

    public void initAdManager()
    {
        admanager.getInstance().initialize();
    }

    public void initSearchEngine()
    {
        fabricManager.getInstance().sendEvent("HOME PAGE LOADING : " );
        if(status.search_status.equals(enums.searchEngine.Google.toString()))
        {
            webView.loadUrl(constants.backendGoogle);
        }
        else if(status.search_status.equals(enums.searchEngine.Bing.toString()))
        {
            webView.loadUrl(constants.backendBing);
        }
        else
        {
            webView.loadUrl(constants.backendGenesis);
        }
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
        loadingText = findViewById(R.id.loadingText);

        webviewclient = new webviewClient();
        geckoclient = new geckoClients();
        eventhandler = new eventHandler();
    }

    public void initializeCrashlytics()
    {
        //fabricManager.getInstance().init();
    }

    public void initializeWebView()
    {
        setWebViewSettings(webView);
        webviewclient.loadWebViewClient(webView);
    }

    public void setWebViewSettings(WebView view)
    {
        view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        view.getSettings().setJavaScriptEnabled(false);
        view.getSettings().setUseWideViewPort(true);
    }

    /*------------------------------------------------------- Event Handler ----------------------------------------------------*/

    private void initializeLocalEventHandlers() {
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

    public void onMenuButtonPressed(View view)
    {
        eventhandler.onMenuButtonPressed(view);
    }

    /*-------------------------------------------------------Helper Method In UI Redirection----------------------------------------------------*/

    public void onloadURL(String url,boolean isHiddenWeb,boolean isUrlSavable) {
        if(isHiddenWeb)
        {
            geckoclient.loadGeckoURL(url,geckoView,isUrlSavable);
        }
        else
        {
            webView.loadUrl(url);
            onRequestTriggered(isHiddenWeb,url);
        }
    }

    public void onRequestTriggered(boolean isHiddenWeb,String url) {
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

    public void onInternetErrorView() {
        applicationViewController.getInstance().onInternetError();
        applicationViewController.getInstance().disableFloatingView();
    }

    public boolean onDisableInternetError()
    {
       return applicationViewController.getInstance().onDisableInternetError();
    }

    public void onProgressBarUpdateView(int progress) {
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

    public void onShowAds()
    {
        applicationViewController.getInstance().onShowAds();
    }

    public void openMenu(View view) {
        applicationViewController.getInstance().openMenu(view);
    }

    public void reInitializeSuggestion()   {
        applicationViewController.getInstance().reInitializeSuggestion();
    }

    /*-------------------------------------------------------Helper Method Out UI Redirection----------------------------------------------------*/

    public String getSearchBarUrl()
    {
         return applicationViewController.getInstance().getSearchBarUrl();
    }

    public void onReInitGeckoView() {
        geckoclient.initialize(geckoView);
        geckoclient.onReloadHiddenView();
    }

    public void onHiddenGoBack()
    {
        geckoclient.onHiddenGoBack(geckoView);
    }

    public void stopHiddenView() {
        geckoclient.stopHiddenView(geckoView);
        geckoclient.removeHistory();
    }

    public void onReloadHiddenView()
    {
        geckoclient.onReloadHiddenView();
    }

    public boolean isGeckoViewRunning()
    {
       return geckoclient.isGeckoViewRunning();
    }

    public boolean isInternetErrorOpened()
    {
        return requestFailure.getAlpha()==1;
    }

    /*-------------------------------------------------------Menu Handler----------------------------------------------------*/

    public boolean onMenuOptionSelected(MenuItem item) {

        eventhandler.onMenuPressed(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

}

