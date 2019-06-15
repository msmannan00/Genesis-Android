package com.darkweb.genesissearchengine.appManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import com.crashlytics.android.Crashlytics;
import com.darkweb.genesissearchengine.*;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.httpManager.webRequestHandler;
import com.darkweb.genesissearchengine.pluginManager.*;
import com.example.myapplication.BuildConfig;
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
    private ProgressBar progressBar;
    private ConstraintLayout requestFailure;
    private ConstraintLayout splashScreen;
    private EditText searchbar;

    private GeckoView webLoader;
    private FloatingActionButton floatingButton;
    private Button reloadButton;
    private ImageButton homeButton;
    private LinearLayout topbar;
    private GeckoSession session1;
    private GeckoRuntime runtime1;
    private String version_code = "5.0";
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
        setContentView(R.layout.application_view);
        if(BuildConfig.FLAVOR.equals("aarch64")&&Build.SUPPORTED_ABIS[0].equals("arm64-v8a") || BuildConfig.FLAVOR.equals("arm")&&Build.SUPPORTED_ABIS[0].equals("armeabi-v7a") || BuildConfig.FLAVOR.equals("x86")&&Build.SUPPORTED_ABIS[0].equals("x86") || BuildConfig.FLAVOR.equals("x86_64")&&Build.SUPPORTED_ABIS[0].equals("x86_64"))
        {
            //initializeCrashlytics();
            initializeBackgroundColor();
            setContentView(R.layout.application_view);
            app_model.getInstance().setAppContext(this);
            app_model.getInstance().setAppInstance(this);

            preference_manager.getInstance().initialize(this);
            orbot_manager.getInstance().initializeTorClient();
            initializeStatus();
            initializeRunnable();
            initializeConnections();
            initializeWebViews();
            initializeView();
            initializeAds();
            checkSSLTextColor();
            initSplashScreen();
        }
        else
        {
            setContentView(R.layout.invalid_setup);
            message_manager.getInstance().abiError(Build.SUPPORTED_ABIS[0]);
        }

    }

    public void setOrientation()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void initSplashScreen()
    {
        ImageView view = findViewById(R.id.imageView_loading_back);
        RotateAnimation rotate = new RotateAnimation(0, 360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);

        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        view.setAnimation(rotate);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        ImageView splashlogo = findViewById(R.id.backsplash);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) splashlogo.getLayoutParams();
        int height = screenHeight();
        splashlogo.getLayoutParams().height = height;

        ImageView loading = findViewById(R.id.imageView_loading_back);
        double heightloader = Resources.getSystem().getDisplayMetrics().heightPixels*0.78;
        ViewGroup.MarginLayoutParams params_loading = (ViewGroup.MarginLayoutParams) loading.getLayoutParams();
        params_loading.topMargin = (int)(heightloader);
        loading.setLayoutParams(params_loading);
    }

    public int screenHeight()
    {
        if(!hasSoftKeys(this.getWindowManager()))
        {
            return (int)(Resources.getSystem().getDisplayMetrics().heightPixels)-(getNavigationBarHeight(this));
        }
        else
        {
            return (int)(Resources.getSystem().getDisplayMetrics().heightPixels);
        }
    }

    public boolean hasSoftKeys(WindowManager windowManager)
    {
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    public int getStatusBarHeight(Context c) {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getNavigationBarHeight(Context c) {
        Resources resources = c.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }


    public void initializeBackgroundColor()
    {
        /*
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            getWindow().setBackgroundDrawableResource(R.xml.splash_wall_white);
        } else{
            getWindow().setBackgroundDrawableResource(R.xml.splash_wall_black);
        }*/
    }

    public void initializeStatus()
    {
        //helperMethod.setPlaystoreStatus(this);
    }

    public void initializeCrashlytics()
    {
        Fabric.with(this, new Crashlytics());
        analyticmanager.getInstance().initialize(this);
        analyticmanager.getInstance().logUser();
    }

    public void versionChecker()
    {
        /*String version = preference_manager.getInstance().getString("version","none",this);
        if(!version.equals(version_code) && !version.equals("none"))
        {
            message_manager.getInstance().versionWarning(this,Build.SUPPORTED_ABIS[0]);
        }
        webRequestHandler.getInstance().getVersion(this);*/
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
                    Log.i("SHIT1 : ", app_model.getInstance().getCurrentURL());
                    //app_model.getInstance().setIsLoadingURL(false);
                    message_manager.getInstance().baseURLError();
                }
            }
        };
    }

    public void initializeAds()
    {
        admanager.getInstance().initialize();
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
        applicationViewController.getInstance().initialization(webView1,webView2,progressBar,searchbar, splashScreen, requestFailure);
        webView1.bringToFront();
        progressBar.animate().setDuration(150).alpha(0f);

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

        Drawable img = getResources().getDrawable( R.drawable.icon_lock);
        searchbar.measure(0, 0);
        img.setBounds( 0, (int)(searchbar.getMeasuredHeight()*0.00), (int)(searchbar.getMeasuredHeight()*1.10), (int)(searchbar.getMeasuredHeight()*0.69) );
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
        requestFailure.animate().setDuration(0).alpha(0.0f);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        loadURLAnimate(constants.backendUrl);
        webView2.setAlpha(0);
        webView1.setAlpha(0);
        initializeViewClients();
    }

    private void initializeViewClients()
    {

        webView1.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int newProgress){
                if(newProgress<95 && newProgress>5 && view.equals(applicationViewController.getInstance().getCurrentView()))
                {
                    Log.i("SHIT","SHIT1 : " + newProgress);
                    progressBar.setProgress(newProgress);
                }
            }
        });
        webView2.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int newProgress){
                if(newProgress<95 && newProgress>5 && view.equals(applicationViewController.getInstance().getCurrentView()))
                {
                    Log.i("SHIT","SHIT2 : " + newProgress);
                    progressBar.setProgress(newProgress);
                }
            }
        });

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
                message_manager.getInstance().reportURL(searchbar.getText().toString());
            }
        });
    }

    /*-------------------------------------------------------WEBVIEW LISTENERS-------------------------------------------------------*/

    public void loadGeckoURL(String url)
    {

        boolean init_status=orbot_manager.getInstance().initOrbot();
        if(init_status)
        {
            searchbar.setText(url.replaceAll("boogle.store","genesis.onion"));
            checkSSLTextColor();
            helperMethod.hideKeyboard();

            Log.i("PROBLEM11","");

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

                if(url.equals(searchbar.getText().toString()))
                {
                    view.stopLoading();
                    return true;
                }

                if(!url.toString().contains("boogle"))
                {
                    try
                    {
                        if(orbot_manager.getInstance().initOrbot())
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
                    helperMethod.hideKeyboard();
                    if(traceUrlList.size()==0 || !app_model.getInstance().getCurrentURL().equals(traceUrlList.peek()))
                    {
                        traceUrlList.add(app_model.getInstance().getCurrentURL());
                    }
                    if(url.contains("?"))
                    {
                        url = url+"&savesearch=on";
                    }
                    if(!app_model.getInstance().getCurrentURL().equals(url))
                    {

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
                int delay = 150;
                if(startPage>2)
                {
                    delay = 0;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView1.stopLoading();
                        webView2.stopLoading();

                        int duration = 200;
                        if(requestFailure.getAlpha()==1)
                            duration=1;

                        progressBar.setProgress(100);
                        requestFailure.animate().alpha(0f).setDuration(200).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));;

                        if(webView1.getAlpha()==0)
                        {
                            webView1.animate().setDuration(duration).alpha(1f).withEndAction((() -> webView2.setAlpha(0)));
                        }
                        else
                        {
                            webView2.animate().setDuration(duration).alpha(1f).withEndAction((() -> webView1.setAlpha(0)));
                        }
                        progressBar.animate().alpha(0).withEndAction((() -> progressBar.setVisibility(View.INVISIBLE)));

                        //.withEndAction((() -> {

                        //}));;
                        //app_model.getInstance().setIsLoadingURL(false);
                        //requestFailure.animate().alpha(0f).setDuration(300).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));;
                        Log.i("PROBLEM12","");


                        splashScreen.animate().alpha(0.0f).setStartDelay(150).setDuration(200).setListener(null).withEndAction((() -> splashScreen.setVisibility(View.GONE)));
                        if(!status.isApplicationLoaded)
                        {
                            status.isApplicationLoaded = true;
                            handler = new Handler();

                            Handler popuphandler = new Handler();

                            popuphandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(status.isApplicationLoaded && !isTutorialPopupShown)
                                    {
                                        if(!preference_manager.getInstance().getBool("FirstTimeLoaded",false)) {
                                            message_manager.getInstance().welcomeMessage();
                                            isTutorialPopupShown = true;
                                        }
                                        else if(constants.build_type.equals("local"))
                                        {
                                            versionChecker();
                                        }
                                    }
                                }
                            }, 1000);

                        }

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchbar.getWindowToken(), 0);

                    }
                }, delay);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        };

        if(!helperMethod.isNetworkAvailable())
        {
            status.isApplicationLoaded = true;
            splashScreen.animate().setStartDelay(2000).alpha(0.0f).setDuration(300).setListener(null).withEndAction((new Runnable() {
                @Override
                public void run()
                {
                    splashScreen.setVisibility(View.GONE);
                }
            }));

            //requestFailure.setVisibility(View.VISIBLE);
            //requestFailure.animate().alpha(1.0f);
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
        if(!isBlackPage && !orbot_manager.getInstance().initOrbot())
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
        helperMethod.hideKeyboard();
        try
        {
            URL host = new URL(url);
            if(!host.getHost().contains("onion"))
            {
                session1.stop();
                //session1.close();
                //session1.stop();
                message_manager.getInstance().baseURLError();
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        //app_model.getInstance().setIsLoadingURL(true);
        if(!isBlackPage && !wasBackPressed)
        {
            searchbar.setText(url);
            checkSSLTextColor();
        }
        if(!isBlackPage && progressBar.getVisibility() == View.INVISIBLE)
        {
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
                //requestFailure.animate().alpha(0f).setDuration(300).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));;
                String url = searchbar.getText().toString();
                boolean isBlackPage = url.equals("about:blank");
                if(!isBlackPage && !wasBackPressed)
                {
                    checkSSLTextColor();
                    traceUrlList.add(app_model.getInstance().getCurrentURL());
                    if(app_model.getInstance().getCurrentURL().contains("boogle.store"))
                    {
                        admanager.getInstance().showAd(true);
                    }
                    else
                    {
                        admanager.getInstance().showAd(false);
                    }
                    //app_model.getInstance().setCurrentURL(url);
                }
                wasBackPressed=false;
            }
            isLoadedUrlSet = true;
        }
        if(progress>=100)
        {
            //app_model.getInstance().setIsLoadingURL(false);
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
        if(!helperMethod.isNetworkAvailable() || hasURLLoaded)
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
                    //app_model.getInstance().setIsLoadingURL(false);
                    //requestFailure.setAlpha(0.0f);
                    //requestFailure.setVisibility(View.VISIBLE);
                    //requestFailure.animate().alpha(1.0f);
                    Log.i("PROBLEM16","");
                    geckoHandler = null;

                    if(!helperMethod.isNetworkAvailable())
                    {
                        //orbot_manager.getInstance().restartOrbot();
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
        //requestFailure.animate().alpha(0.0f).setDuration(300).setListener(null).withEndAction((() -> {
        //    requestFailure.setVisibility(View.GONE);
        //}));
    }

    public void loadURLAnimate(String url)
    {
        if(!app_model.getInstance().getCurrentURL().equals(url))
        {
            loadURL(url);
        }
    }
    public void loadURL(String url)
    {
        app_model.getInstance().setCurrentURL(url);
        applicationViewController.getInstance().onRequestURL();
        floatingButton.animate().alpha(0);
        webRequestHandler.getInstance().loadURL(url);
    }

    /*-------------------------------------------------------EVENT LISTENERS-------------------------------------------------------*/

    public void initializePopupView(String url)
    {
        searchbar.setText(url.replaceAll("boogle.store","genesis.onion"));
        checkSSLTextColor();
        traceUrlList.add(app_model.getInstance().getCurrentURL());
        //app_model.getInstance().setCurrentURL(url);
        Log.i("PROBLEM17","");
    }

    public void onHomeButtonPressed(View view)
    {
        searchbar.setText(constants.frontUrlSlashed);
        checkSSLTextColor();
        traceUrlList.clear();
        loadURLAnimate(constants.backendUrlSlashed);
        helperMethod.hideKeyboard();

        session1.close();
        isOnnionUrlHalted = true;
        wasBackPressed = false;
    }

    public void onReloadButtonPressed(View view)
    {
        if(searchbar.getText().toString().contains("://genesis.onion"))
        {
            loadURL(app_model.getInstance().getCurrentURL());
        }
        else
        {
            if(status.isTorInitialized)
            {
                wasBackPressed = true;
                loadGeckoURL(searchbar.getText().toString());
            }
            else
            {
                orbot_manager.getInstance().initOrbot();
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
                if(!app_model.getInstance().getCurrentURL().contains("boogle.store"))
                {
                    searchbar.setText(traceUrlList.peek().toString().replaceAll("boogle.store","genesis.onion"));
                    isOnnionUrlHalted=true;
                    session1.stop();
                    session1.close();
                    webLoader.releaseSession();
                    webLoader.releaseSession();
                    //app_model.getInstance().setCurrentURL(traceUrlList.pop().toString());
                    Log.i("PROBLEM22","");
                    webLoader.animate().alpha(0f).withEndAction((() -> webLoader.setVisibility(View.INVISIBLE)));;
                    //isOnnionUrlHalted = true;
                    helperMethod.hideKeyboard();
                    if(requestFailure.getAlpha()==1)
                    {
                        //requestFailure.animate().alpha(0f).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));;
                    }
                    else
                    {
                    }
                    floatingButton.animate().alpha(0);
                }
                else
                {
                    Log.i("1LOG3","LOG1");
                    searchbar.setText(traceUrlList.peek().toString().replaceAll("boogle.store","genesis.onion"));
                    session1.close();
                    loadURLAnimate(traceUrlList.pop().toString());
                    if(traceUrlList.size()<=0)
                    {
                        //app_model.getInstance().setCurrentURL("https://boogle.store");
                    }
                    else
                    {
                        //app_model.getInstance().setCurrentURL(traceUrlList.peek().toString());
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
                    //app_model.getInstance().setCurrentURL("https://boogle.store");
                    webLoader.animate().setDuration(250).alpha(0);
                    traceUrlList.pop();
                    wasBackPressed = false;
                    session1.close();
                    floatingButton.animate().alpha(0);
                }
                else
                {
                    if(orbot_manager.getInstance().initOrbot() && progressBar.getAlpha()==0)
                    {
                        //app_model.getInstance().setCurrentURL(traceUrlList.peek().toString());
                        traceUrlList.pop().toString();
                        boolean init_status=orbot_manager.getInstance().initOrbot();
                        if(init_status)
                        {
                            searchbar.setText(app_model.getInstance().getCurrentURL().replaceAll("boogle.store","genesis.onion"));
                            helperMethod.hideKeyboard();
                            wasBackPressed = true;
                            //requestFailure.animate().alpha(0);
                            session1.loadUri(app_model.getInstance().getCurrentURL());
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
        helperMethod.hideKeyboard();
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
                        orbot_manager.getInstance().initOrbot();
                    }

                    if(status.isTorInitialized)
                    {
                        session1.stop();
                        session1.loadUri(url);
                    }
                    else
                    {
                        orbot_manager.getInstance().initOrbot();
                        message_manager.getInstance().startingOrbotInfo();
                    }

                    return true;
                }
                else
                {
                    message_manager.getInstance().baseURLError();
                    return true;
                }
            }
            else
            {
                String editedURL = "https://boogle.store/search?q="+v.getText().toString().replaceAll(" ","+")+"&p_num=1&s_type=all&savesearch=on";
                //app_model.getInstance().setCurrentURL(editedURL);
                searchbar.setText(editedURL.replace("boogle.store","genesis.onion"));
                searchbar.clearFocus();
                loadURLAnimate(editedURL);
            }

        }
        catch (IOException e)
        {
            String editedURL = "https://boogle.store/search?q="+v.getText().toString().replaceAll(" ","+")+"&p_num=1&s_type=all&savesearch=on";
            //app_model.getInstance().setCurrentURL(editedURL);
            searchbar.clearFocus();
            searchbar.setText(editedURL.replace("boogle.store","genesis.onion"));
            loadURLAnimate(editedURL);
            e.printStackTrace();
        }
        return true;
    }

























    public void onInternetError()
    {
        applicationViewController.getInstance().onInternetError();
    }

    public void loadUrlOnWebview(String html)
    {
        applicationViewController.getInstance().loadUrlOnWebview(html);
    }
}

