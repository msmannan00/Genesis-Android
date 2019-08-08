package com.darkweb.genesissearchengine.appManager.home_activity;

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
    private home_ehandler eventhandler = null;

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(helperMethod.isBuildValid())
        {
            setContentView(R.layout.home_view);
            initializeAppModel();
            preference_manager.getInstance().initialize();

            status.initStatus();
            initializeCrashlytics();
            initializeConnections();
            initializeWebView();
            initializeLocalEventHandlers();
            initAdManager();

            orbot_manager.getInstance().reinitOrbot();
            viewController.getInstance().initialization(webView,loadingText,progressBar,searchbar,splashScreen,requestFailure,floatingButton, loadingIcon,splashlogo);
            firebase.getInstance().initialize();
            geckoclient.initialize(geckoView);
            app_model.getInstance().initialization();
            initBoogle();
            downloadManager.getInstance().initialize();
        }
        else
        {
            initializeAppModel();
            setContentView(R.layout.invalid_setup_view);
            message_manager.getInstance().abiError(Build.SUPPORTED_ABIS[0]);
        }

    }

    public void initAdManager()
    {
        admanager.getInstance().initialize();
    }

    public void initBoogle()
    {
        onloadURL(constants.backendGenesis,false,false);
    }

    public Boolean initSearchEngine()
    {
        if(status.search_status.equals(enums.searchEngine.Bing.toString()))
        {
            webView.stopLoading();
            onloadURL(constants.backendBing,true,false);
            if(app_model.getInstance().getNavigation().size()!=1)
            {
                app_model.getInstance().addNavigation(constants.backendBing,enums.navigationType.onion);
            }
            if(app_model.getInstance().getNavigation().size()>0)
            {
                app_model.getInstance().getNavigation().set(0,new navigation_model(constants.backendBing,enums.navigationType.onion));
            }
            return false;
        }
        else if(status.search_status.equals(enums.searchEngine.Google.toString()))
        {
            webView.stopLoading();
            onloadURL(constants.backendGoogle,true,false);
            if(app_model.getInstance().getNavigation().size()!=1)
            {
                app_model.getInstance().addNavigation(constants.backendGoogle,enums.navigationType.onion);
            }
            if(app_model.getInstance().getNavigation().size()>0)
            {
                app_model.getInstance().getNavigation().set(0,new navigation_model(constants.backendGoogle,enums.navigationType.onion));
            }
            return false;
        }
        else
        {
            onloadURL(constants.backendGenesis,false,false);
            if(app_model.getInstance().getNavigation().size()!=1)
            {
                app_model.getInstance().addNavigation(constants.backendGenesis,enums.navigationType.base);
            }
            if(app_model.getInstance().getNavigation().size()>0)
            {
                app_model.getInstance().getNavigation().set(0,new navigation_model(constants.backendGenesis,enums.navigationType.base));
            }
            return true;
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


        geckoView.Browser browser = mGeckoView.getCurrentBrowser();
        if (browser == null) {
            browser = mGeckoView.addBrowser(text.getText().toString());
        } else {
            browser.loadUrl(text.getText().toString());
        }


        progressBar = findViewById(R.id.progressBar);
        requestFailure = findViewById(R.id.requestFailure);
        splashScreen = findViewById(R.id.splashScreen);
        searchbar = findViewById(R.id.search);
        floatingButton = findViewById(R.id.floatingActionButton3);
        loadingIcon = findViewById(R.id.imageView_loading_back);
        splashlogo = findViewById(R.id.backsplash);
        loadingText = findViewById(R.id.loadingText);

        webviewclient = new webviewClient();
        geckoclient = new geckoClients();
        eventhandler = new home_ehandler();
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
        view.getSettings().setJavaScriptEnabled(status.java_status);
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
            geckoclient.loadGeckoURL(url,geckoView,isUrlSavable,webView.getVisibility()==View.VISIBLE);
        }
        else if(!app_model.getInstance().isUrlRepeatable(url,webView.getUrl()) || webView.getVisibility() == View.GONE)
        {
            webView.loadUrl(url);
            onRequestTriggered(isHiddenWeb,url);
        }
    }

    public void onRequestTriggered(boolean isHiddenWeb,String url) {
        viewController.getInstance().onRequestTriggered(isHiddenWeb,url);
    }

    public void onClearSearchBarCursorView()
    {
        viewController.getInstance().onClearSearchBarCursor();
    }

    public void onUpdateSearchBarView(String url)
    {
        viewController.getInstance().onUpdateSearchBar(url);
    }

    public void onInternetErrorView() {
        viewController.getInstance().onInternetError();
        viewController.getInstance().disableFloatingView();
    }

    public boolean onDisableInternetError()
    {
       return viewController.getInstance().onDisableInternetError();
    }

    public void onProgressBarUpdateView(int progress) {
        viewController.getInstance().onProgressBarUpdate(progress);
    }

    public void onBackPressedView()
    {
        viewController.getInstance().onBackPressed();
    }

    public void onPageFinished(boolean isHidden)
    {
        viewController.getInstance().onPageFinished(isHidden);
    }

    public void onUpdateView(boolean status)
    {
        viewController.getInstance().onUpdateView(status);
    }

    public void onReload()
    {
        viewController.getInstance().onReload();
    }

    public void onShowAd(enums.adID id)
    {
        admanager.getInstance().showAd(id);
    }

    public void openMenu(View view) {

        viewController.getInstance().openMenu(view);
    }

    public void reInitializeSuggestion()   {
        viewController.getInstance().reInitializeSuggestion();
    }

    public void hideSplashScreen(){
        viewController.getInstance().hideSplashScreen();
    }

    /*-------------------------------------------------------Helper Method Out UI Redirection----------------------------------------------------*/

    public String getSearchBarUrl()
    {
         return viewController.getInstance().getSearchBarUrl();
    }

    public void onReInitGeckoView() {
        geckoclient.initialize(geckoView);
        if(webView.getVisibility() != View.VISIBLE)
        {
            geckoclient.onReloadHiddenView();
        }
    }

    public void onHiddenGoBack()
    {
        geckoclient.onHiddenGoBack(geckoView);
    }

    public void releaseSession()
    {
        geckoclient.releaseSession(geckoView);
    }

    public void stopHiddenView(boolean releaseView,boolean backPressed) {
        geckoclient.stopHiddenView(geckoView,releaseView,backPressed);
        //geckoclient.removeHistory();
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

