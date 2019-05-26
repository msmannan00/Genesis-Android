package com.darkweb.genesissearchengine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import com.example.myapplication.R;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;


import static java.lang.Thread.sleep;

public class application_controller extends AppCompatActivity
{

    /*View Objects*/
    public WebView webView1;
    public WebView webView2;
    private GeckoView webLoader;
    private ProgressBar progressBar;
    private ConstraintLayout requestFailure;
    private ConstraintLayout splashScreen;
    private FloatingActionButton floatingButton;
    private Button reloadButton;
    private ImageButton homeButton;
    private EditText searchbar;
    private LinearLayout topbar;
    private GeckoSession session1;
    private GeckoRuntime runtime1;
    private String version_code = "3.0";
    private boolean wasBackPressed = false;
    private boolean isLoadedUrlSet = false;
    private boolean isGeckoURLLoadded = false;
    private boolean isOnnionUrlHalted = false;
    Handler handler = null;
    Runnable geckoRunnable = null;
    boolean isBlackPage = false;
    int startPage=0;
    Handler geckoHandler = null;
    Runnable geckoRunnableError = null;
    /*helper Variables*/
    Stack traceUrlList = new Stack<String>();
    boolean isTutorialPopupShown = false;

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //initializeCrashlytics();
        initializeBackgroundColor();
        //setOrientation();
        setContentView(R.layout.application_view);
        //orbot_manager.getInstance().initializeTorClient(this);
        initializeStatus();
        initializeRunnable();
        initializeConnections();
        initializeWebViews();
        initializeView();
        initializeAds();
        checkSSLTextColor();
    }

    public void setOrientation()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void initializeBackgroundColor()
    {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            getWindow().setBackgroundDrawableResource(R.drawable.backgradientwhite);
        } else{
            getWindow().setBackgroundDrawableResource(R.drawable.backgradientblack);
        }
    }

    public void initializeStatus()
    {
        helperMethod.setPlaystoreStatus(this);
    }

    public void initializeCrashlytics()
    {
        Fabric.with(this, new Crashlytics());
        analyticmanager.getInstance().setDeviceID(this);
        analyticmanager.getInstance().logUser();
    }

    public void versionChecker()
    {
        String version = preference_manager.getInstance().getString("version","none",this);
        if(!version.equals(version_code) && !version.equals("none"))
        {
            message_manager.getInstance().versionWarning(this);
        }
        webRequestHandler.getInstance().getVersion(this);
    }

    public void initializeRunnable()
    {
        handler = new Handler();
        handler.postDelayed(geckoRunnable,15000);

        Runnable my_runnable = new Runnable() {
            @Override
            public void run() {
                if(!isBlackPage || startPage>1)
                {
                    Log.i("PROBLEM24","");
                    progressBar.animate().setDuration(150).alpha(0f).withEndAction((() -> progressBar.setVisibility(View.INVISIBLE)));;
                    Log.i("SHIT1 : ",status.currentURL);
                    datamodel.getInstance().setIsLoadingURL(false);
                    message_manager.getInstance().URLNotFoundError(application_controller.this);
                }
            }
        };
    }

    public void initializeAds()
    {
        admanager.getInstance().initialize(this);
    }

    public void initializeConnections()
    {
        webView1 = findViewById(R.id.pageLoader1);
        webView2 = findViewById(R.id.pageLoader2);
        progressBar = findViewById(R.id.progressBar);
        requestFailure = findViewById(R.id.requestFailure);
        splashScreen = findViewById(R.id.splashScreen);
        reloadButton = findViewById(R.id.reloadButton);
        homeButton = findViewById(R.id.home);
        searchbar = findViewById(R.id.search);
        floatingButton = findViewById(R.id.floatingActionButton3);
        topbar = findViewById(R.id.topbar);
        webLoader = findViewById(R.id.webLoader);
    }

    public void initializeWebViews()
    {
        webRequestHandler.getInstance().initialization(webView1,webView2,progressBar,searchbar,requestFailure,this,splashScreen,this);
        webView1.bringToFront();
        Log.i("PROBLEM25","");
        progressBar.animate().setDuration(150).alpha(0f);
        //floatingButton.setAlpha(0);

        session1 = new GeckoSession();
        runtime1 = GeckoRuntime.getDefault(application_controller.this);
        session1.open(runtime1);
        if(session1.isOpen())
        {
            webLoader.releaseSession();
        }
        webLoader.setSession(session1);
        session1.setProgressDelegate(new progressDelegate());
        webLoader.setVisibility(View.INVISIBLE);

        Drawable img = getResources().getDrawable( R.drawable.lock );
        searchbar.measure(0, 0);
        img.setBounds( 0, (int)(searchbar.getMeasuredHeight()*0.00), (int)(searchbar.getMeasuredHeight()*1.10), (int)(searchbar.getMeasuredHeight()*0.69) );
        //img.setBounds( 0, 0, 50, 31 );
        searchbar.setCompoundDrawables( img, null, null, null );
    }

    /*Initialization*/
    public void initializeView()
    {
        webView1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView1.setBackgroundColor(Color.WHITE);
        webView1.setWebViewClient(loadWebViewClient());
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.getSettings().setUseWideViewPort(true);
        webView1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView2.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView2.setBackgroundColor(Color.WHITE);
        webView2.setWebViewClient(loadWebViewClient());
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.getSettings().setUseWideViewPort(true);
        webView2.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView2.animate().setDuration(0).alpha(0f);
        progressBar.setVisibility(View.INVISIBLE);
        requestFailure.animate().setDuration(0).alpha(0.0f);
        progressBar.animate().setDuration(150).alpha(0f);

        webView1.loadUrl(constants.backendUrl);
        try {Thread.sleep(100);} catch (Exception e) {}

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        loadURLAnimate(constants.backendUrl);
        initializeViewClients();
    }

    private void initializeViewClients()
    {
        searchbar.setOnEditorActionListener((v, actionId, event) -> {
            Log.i("1actionId : ",actionId+"");
            if (actionId == 5)
            {
                return onEditorClicked(v, actionId, event);
            }
            else
            {
                return false;
            }
        });

        floatingButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                message_manager.getInstance().reportURL(application_controller.this,searchbar.getText().toString());
            }
        });
    }

    /*-------------------------------------------------------WEBVIEW LISTENERS-------------------------------------------------------*/

    public void loadGeckoURL(String url)
    {

        boolean init_status=orbot_manager.getInstance().reinitOrbot(application_controller.this);
        if(init_status)
        {
            searchbar.setText(url.replaceAll("boogle.store","genesis.onion"));
            checkSSLTextColor();
            KeyboardUtils.hideKeyboard(application_controller.this);

            progressBar.setAlpha(0);
            progressBar.setVisibility(View.VISIBLE);
            Log.i("PROBLEM11","");
            progressBar.animate().setDuration(150).setDuration(300).alpha(1f);

            isOnnionUrlHalted = false;
            session1.stop();
            session1.close();
            webLoader.releaseSession();
            session1 = new GeckoSession();
            session1.open(runtime1);
            session1.setProgressDelegate(new progressDelegate());
            webLoader.setSession(session1);

            session1.loadUri(url);
        }
    }

    private WebViewClient loadWebViewClient()
    {
        WebViewClient client = new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url)
            {
                URL host = null;
                try
                {
                    host = new URL(url);
                    if(host.getHost().contains("play.google.com"))
                    {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.darkweb.genesissearchengine"));
                        startActivity(browserIntent);
                        return true;
                    }
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }

                if(url.equals(searchbar.getText().toString()))
                {
                    view.stopLoading();
                    return true;
                }

                if(!url.toString().contains("boogle"))
                {
                    try
                    {
                        if(orbot_manager.getInstance().reinitOrbot(application_controller.this))
                        {
                            loadGeckoURL(url);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    return true;
                }
                else
                {
                    session1.stop();
                    session1.close();

                    searchbar.setText(url.replaceAll("boogle.store","genesis.onion"));
                    checkSSLTextColor();
                    KeyboardUtils.hideKeyboard(application_controller.this);
                    if(traceUrlList.size()==0 || !status.currentURL.equals(traceUrlList.peek()))
                    {
                        traceUrlList.add(status.currentURL);
                        status.currentURL = url;
                    }
                    if(url.contains("?"))
                    {
                        url = url+"&savesearch=on";
                    }
                    loadURLAnimate(url);
                    return true;
                }
            }
            @Override
            public void onPageFinished(WebView  view, String  url)
            {
                super.onPageFinished(view, url);

                handler = new Handler();
                int delay = 800;
                if(startPage>2)
                {
                    delay = 0;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView1.stopLoading();
                        webView2.stopLoading();
                        if(webView1.getAlpha()==0)
                        {
                            webView1.animate().setDuration(250).alpha(1f);
                        }
                        else
                        {
                            webView2.animate().setDuration(250).alpha(1f);
                        }
                        //.withEndAction((() -> {

                        //}));;
                        datamodel.getInstance().setIsLoadingURL(false);
                        requestFailure.animate().alpha(0f).setDuration(300).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));;
                        Log.i("PROBLEM12","");

                        if(searchbar.getText().toString().contains("genesis.onion"))
                        {
                            progressBar.animate().setDuration(150).alpha(0f).withEndAction((() -> progressBar.setVisibility(View.INVISIBLE)));;
                        }

                        if(!status.hasApplicationLoaded)
                        {
                            status.hasApplicationLoaded = true;
                            handler = new Handler();

                            splashScreen.animate().alpha(0.0f).setStartDelay(500).setDuration(300).setListener(null).withEndAction((() -> splashScreen.setVisibility(View.GONE)));
                            versionChecker();


                        }

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchbar.getWindowToken(), 0);

                    }
                }, delay);



                Handler popuphandler = new Handler();

                popuphandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(status.hasApplicationLoaded && !isTutorialPopupShown)
                        {
                            message_manager.getInstance().welcomeMessage(application_controller.this,application_controller.this);
                            isTutorialPopupShown = true;
                        }
                    }
                }, 2000);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("PROBLEM13","");
                        progressBar.animate().setDuration(150).alpha(0f).withEndAction((() -> progressBar.setVisibility(View.INVISIBLE)));;
                        datamodel.getInstance().setIsLoadingURL(false);
                        splashScreen.animate().alpha(0);
                        requestFailure.setVisibility(View.VISIBLE);
                        requestFailure.animate().alpha(1.0f);
                        status.isTorInitialized = false;
                    }
                }, 3000);

                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        };

        if(!helperMethod.isNetworkAvailable(this))
        {
            status.hasApplicationLoaded = true;
            splashScreen.animate().setStartDelay(2000).alpha(0.0f).setDuration(300).setListener(null).withEndAction((new Runnable() {
                @Override
                public void run()
                {
                    splashScreen.setVisibility(View.GONE);
                }
            }));

            requestFailure.setVisibility(View.VISIBLE);
            requestFailure.animate().alpha(1.0f);
            status.isTorInitialized = false;
            loadErrorPage();
        }

        return client;
    }


class progressDelegate implements GeckoSession.ProgressDelegate
{
    @Override
    public void onPageStart(GeckoSession session, String url)
    {
        isGeckoURLLoadded = false;
        isBlackPage = url.equals("about:blank");
        if(!isBlackPage && !orbot_manager.getInstance().reinitOrbot(application_controller.this))
        {
            session1.stop();
            session1.close();
            return;
        }

        if(isOnnionUrlHalted)
        {
            return;
        }

        isLoadedUrlSet = false;
        KeyboardUtils.hideKeyboard(application_controller.this);
        try
        {
            URL host = new URL(url);
            if(!host.getHost().contains("onion"))
            {
                session1.stop();
                //session1.close();
                //session1.stop();
                message_manager.getInstance().baseURLError(application_controller.this);
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        datamodel.getInstance().setIsLoadingURL(true);
        if(!isBlackPage && !wasBackPressed)
        {
            searchbar.setText(url);
            checkSSLTextColor();
        }
        if(!isBlackPage && progressBar.getVisibility() == View.INVISIBLE)
        {
            progressBar.setAlpha(0);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.animate().setDuration(150).alpha(1f);
        }
    }
    @Override
    public void onPageStop(GeckoSession session, boolean success)
    {
        if(!success)
        {
            initGeckoFailureHandler(isGeckoURLLoadded);
            isGeckoURLLoadded = false;
        }
        if(success)
        {
            isGeckoURLLoadded = false;
        }
    }

    @Override
    public void onProgressChange(GeckoSession session, int progress)
    {
        if(geckoHandler!=null)
        {
            geckoHandler.removeCallbacks(geckoRunnableError);
            geckoHandler = null;
        }
        if(progress>=100)
        {
            isGeckoURLLoadded = true;
            floatingButton.animate().alpha(1);

            if(!isLoadedUrlSet &&!isOnnionUrlHalted)
            {
                webLoader.bringToFront();
                webLoader.animate().setDuration(100).alpha(1);
                webLoader.setVisibility(View.VISIBLE);
                requestFailure.animate().alpha(0f).setDuration(300).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));;
                String url = searchbar.getText().toString();
                boolean isBlackPage = url.equals("about:blank");
                if(!isBlackPage && !wasBackPressed)
                {
                    checkSSLTextColor();
                    traceUrlList.add(status.currentURL);
                    if(status.currentURL.contains("boogle.store"))
                    {
                        admanager.getInstance().showAd(true);
                    }
                    else
                    {
                        admanager.getInstance().showAd(false);
                    }
                    status.currentURL = url;
                }
                wasBackPressed=false;
            }
            progressBar.animate().setDuration(150).alpha(0f).withEndAction((() -> progressBar.setVisibility(View.INVISIBLE)));;
            isLoadedUrlSet = true;
        }
        if(progress>=100)
        {
            datamodel.getInstance().setIsLoadingURL(false);
        }
    }

    @Override
    public void onSecurityChange(GeckoSession session, SecurityInformation securityInfo)
    {
    }
}
    public void initGeckoFailureHandler(boolean hasURLLoaded)
    {
        int delay = 15000;
        if(!helperMethod.isNetworkAvailable(application_controller.this) || hasURLLoaded)
        {
            delay=0;
        }

        if(geckoHandler == null)
        {
            geckoHandler = new Handler();
            geckoRunnableError = new Runnable() {
                @Override
                public void run() {
                    floatingButton.animate().alpha(0);
                    Log.i("PROBLEM15","");
                    datamodel.getInstance().setIsLoadingURL(false);
                    requestFailure.setAlpha(0.0f);
                    requestFailure.setVisibility(View.VISIBLE);
                    requestFailure.animate().alpha(1.0f);
                    Log.i("PROBLEM16","");
                    progressBar.animate().setDuration(150).alpha(0f).withEndAction((() -> progressBar.setVisibility(View.INVISIBLE)));;
                    geckoHandler = null;

                    if(!helperMethod.isNetworkAvailable(application_controller.this))
                    {
                        orbot_manager.getInstance().restartOrbot(application_controller.this);
                    }
                    wasBackPressed=false;

                }
            };
            geckoHandler.postDelayed(geckoRunnableError, delay);
        }
    }

    /*-------------------------------------------------------Helper Method-------------------------------------------------------*/

    public void loadErrorPage()
    {
        requestFailure.animate().alpha(0.0f).setDuration(300).setListener(null).withEndAction((() -> {
            requestFailure.setVisibility(View.GONE);
        }));
    }

    public void loadURLAnimate(String url)
    {
        floatingButton.animate().alpha(0);
        webRequestHandler.getInstance().loadURL(url);
    }

    /*-------------------------------------------------------EVENT LISTENERS-------------------------------------------------------*/

    public void initializePopupView(String url)
    {
        searchbar.setText(url.replaceAll("boogle.store","genesis.onion"));
        checkSSLTextColor();
        traceUrlList.add(status.currentURL);
        status.currentURL=url;
        progressBar.setAlpha(0f);
        progressBar.setVisibility(View.VISIBLE);
        Log.i("PROBLEM17","");
        progressBar.animate().setDuration(150).alpha(1f);
    }

    public void onHomeButtonPressed(View view)
    {
        webRequestHandler.getInstance().isUrlStoped=true;
        searchbar.setText("https://genesis.onion");
        checkSSLTextColor();
        status.currentURL="https://boogle.store";
        traceUrlList.clear();
        progressBar.setAlpha(0f);
        progressBar.setVisibility(View.VISIBLE);
        Log.i("PROBLEM18","");
        progressBar.animate().setDuration(150).alpha(1f);
        loadURLAnimate("https://boogle.store");
        webView1.stopLoading();
        webView2.stopLoading();
        session1.close();
        isOnnionUrlHalted = true;
        wasBackPressed = false;
        KeyboardUtils.hideKeyboard(application_controller.this);
        webRequestHandler.getInstance().isUrlStoped=false;
    }

    public void onReloadButtonPressed(View view)
    {
        webRequestHandler.getInstance().isReloadedUrl = true;
        Log.i("PROBLEM19","");
        progressBar.animate().setDuration(150).alpha(0f);
        progressBar.setVisibility(View.VISIBLE);
        if(searchbar.getText().toString().contains("://genesis.onion"))
        {
            Log.i("PROBLEM20","");
            progressBar.animate().setDuration(150).alpha(1f);
            loadURLAnimate(status.currentURL);
        }
        else
        {
            if(status.isTorInitialized)
            {
                Log.i("PROBLEM21","");
                progressBar.animate().setDuration(150).alpha(1f);
                wasBackPressed = true;
                loadGeckoURL(searchbar.getText().toString());
            }
            else
            {
                orbot_manager.getInstance().reinitOrbot(application_controller.this);
            }
        }
    }

    public void checkSSLTextColor()
    {
        if(searchbar==null)
        {
            return;
        }

        if(searchbar.getText().toString().contains("https://"))
        {
            SpannableString ss = new SpannableString(searchbar.getText());
            ss.setSpan(new ForegroundColorSpan(Color.argb(255,0,123,43)),0,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY),5,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchbar.setText(ss);
        }
        else if(searchbar.getText().toString().contains("http://"))
        {
            SpannableString ss = new SpannableString(searchbar.getText());
            ss.setSpan(new ForegroundColorSpan(Color.argb(255,0,128,43)),0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY),4,7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchbar.setText(ss);
        }
        else
        {
            SpannableString ss = new SpannableString(searchbar.getText());
            ss.setSpan(new ForegroundColorSpan(Color.BLACK),0,searchbar.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchbar.setText(ss);
        }
    }

    public void backPressed()
    {
        if(traceUrlList.size()>0)
        {
            if(traceUrlList.peek().toString().contains("boogle.store"))
            {
                session1.stop();
                session1.close();
                if(!status.currentURL.contains("boogle.store"))
                {
                    searchbar.setText(traceUrlList.peek().toString().replaceAll("boogle.store","genesis.onion"));
                    isOnnionUrlHalted=true;
                    session1.stop();
                    session1.close();
                    webLoader.releaseSession();
                    webLoader.releaseSession();
                    status.currentURL = traceUrlList.pop().toString();
                    Log.i("PROBLEM22","");
                    webLoader.animate().alpha(0f).withEndAction((() -> webLoader.setVisibility(View.INVISIBLE)));;
                    //isOnnionUrlHalted = true;
                    KeyboardUtils.hideKeyboard(application_controller.this);
                    if(requestFailure.getAlpha()==1)
                    {
                        requestFailure.animate().alpha(0f).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));;
                    }
                    else
                    {
                        progressBar.animate().alpha(0f).withEndAction((() -> progressBar.setVisibility(View.INVISIBLE)));;
                    }
                    floatingButton.animate().alpha(0);
                }
                else
                {
                    //if(progressBar.getAlpha()==0)
                    //{
                    //progressBar.animate().setDuration(150).setDuration(300).alpha(1f);
                    Log.i("1LOG3","LOG1");
                    searchbar.setText(traceUrlList.peek().toString().replaceAll("boogle.store","genesis.onion"));
                    session1.close();
                    loadURLAnimate(traceUrlList.pop().toString());
                    if(traceUrlList.size()<=0)
                    {
                        status.currentURL = "https://boogle.store";
                    }
                    else
                    {
                        status.currentURL = traceUrlList.peek().toString();
                    }
                    floatingButton.animate().alpha(0);
                    //}
                }
            }
            else
            {
                if(traceUrlList.size()<=0 || traceUrlList.peek().toString().contains("boogle.store"))
                {
                    session1.stop();
                    session1.close();

                    searchbar.setText(traceUrlList.peek().toString().replaceAll("boogle.store","genesis.onion"));
                    status.currentURL = "https://boogle.store";
                    webLoader.animate().setDuration(250).alpha(0);
                    traceUrlList.pop();
                    wasBackPressed = false;
                    session1.close();
                    floatingButton.animate().alpha(0);
                }
                else
                {
                    if(orbot_manager.getInstance().reinitOrbot(this) && progressBar.getAlpha()==0)
                    {
                        status.currentURL = traceUrlList.peek().toString();
                        traceUrlList.pop().toString();
                        boolean init_status=orbot_manager.getInstance().reinitOrbot(application_controller.this);
                        if(init_status)
                        {
                            searchbar.setText(status.currentURL.replaceAll("boogle.store","genesis.onion"));
                            KeyboardUtils.hideKeyboard(application_controller.this);
                            progressBar.animate().alpha(1);
                            progressBar.setVisibility(View.VISIBLE);
                            wasBackPressed = true;
                            requestFailure.animate().alpha(0);
                            session1.loadUri(status.currentURL);
                        }
                    }
                }
            }
            if(traceUrlList.size()==0)
            {
                searchbar.setText("https://genesis.onion/");
            }
            checkSSLTextColor();
        }
    }

    @Override
    public void onBackPressed()
    {
        backPressed();
    }

    public boolean onEditorClicked(TextView v, int actionId, KeyEvent event)
    {
        KeyboardUtils.hideKeyboard(application_controller.this);
        Log.i("FUCKOFF : ",actionId+"");
        try
        {
            session1.stop();
            webView1.stopLoading();
            webView2.stopLoading();
            //session1.close();
            String url = v.getText().toString();
            if(!url.startsWith("www.")&& !url.startsWith("http://")&& !url.startsWith("https://")){
                url = "www."+url;
            }
            if(!url.startsWith("http://")&&!url.startsWith("https://")){
                url = "http://"+url;
            }


            boolean isUrlValid = Patterns.WEB_URL.matcher(url).matches();

            url = url.replace("genesis.onion","boogle.store");
            URL host = new URL(url);
            if(isUrlValid && host.getHost().replace("www.","").contains("."))
            {
                if(host.getHost().contains(constants.backendUrlHost)||host.getHost().contains(constants.frontEndUrlHost))
                {
                    floatingButton.animate().alpha(0);
                    loadURLAnimate(url);
                    return true;
                }
                else if(host.getHost().contains(constants.allowedHost))
                {
                    if(!url.contains("://boogle.store"))
                    {
                        orbot_manager.getInstance().reinitOrbot(this);
                    }

                    if(status.isTorInitialized)
                    {
                        session1.stop();
                        session1.loadUri(url);
                    }
                    else
                    {
                        orbot_manager.getInstance().reinitOrbot(application_controller.this);
                        message_manager.getInstance().startingOrbotInfo(this);
                    }

                    return true;
                }
                else
                {
                    message_manager.getInstance().baseURLError(this);
                    return true;
                }
            }
            else
            {
                loadURLAnimate("https://boogle.store/search?q="+v.getText().toString().replaceAll(" ","+")+"&p_num=1&s_type=all");
            }

        }
        catch (IOException e)
        {
            loadURLAnimate("https://boogle.store/search?q="+v.getText().toString().replaceAll(" ","+")+"&p_num=1&s_type=all");
            e.printStackTrace();
        }
        return false;
    }



}

