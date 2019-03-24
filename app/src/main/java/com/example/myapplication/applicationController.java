package com.example.myapplication;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import info.guardianproject.netcipher.proxy.OrbotHelper;
import org.mozilla.gecko.PrefsHelper;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import static java.lang.Thread.sleep;

public class applicationController extends AppCompatActivity
{

    /*View Objects*/
    private WebView webView1;
    private WebView webView2;
    private GeckoView webLoader;
    private ProgressBar progressBar;
    private ConstraintLayout requestFailure;
    private ConstraintLayout splashScreen;
    private Button reloadButton;
    private ImageButton homeButton;
    private EditText searchbar;
    private LinearLayout topbar;
    private GeckoSession session1;
    private GeckoRuntime runtime1;

    /*helper Variables*/
    String currentURL = "http://boogle.store/";
    boolean isRequestError = false;
    boolean hasApplicationLoaded = false;
    Stack urlList = new Stack<String>();
    boolean wasBackPressed = false;
    boolean isFirstLaunch = true;

    /*Initialization*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applicationView);
        initializeProxy();
        initializeView();
        initializeAds();
    }

    public void initializeAds()
    {

        admanager.getInstance().initialize(this);
    }

    public void initializeProxy()
    {
        PrefsHelper.setPref("network.proxy.type",1); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks","127.0.0.1"); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks_port",9050); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks_version",5); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks_remote_dns",true); //manual proxy settings
        PrefsHelper.setPref("browser.cache.disk.enable",false);
        PrefsHelper.setPref("browser.cache.memory.enable",true);
        PrefsHelper.setPref("browser.cache.disk.capacity",0);
        PrefsHelper.setPref("general.useragent.override", "Mozilla/5.0 (Windows NT 6.1; rv:17.0) Gecko/20100101 Firefox/17.0");
        PrefsHelper.setPref("privacy.donottrackheader.enabled",false);
        PrefsHelper.setPref("privacy.donottrackheader.value",1);
    }

    /*Initialization*/
    public void initializeView()
    {
        /*if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
            StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

        webView1 = (WebView) findViewById(R.id.pageLoader1);
        webView2 = (WebView) findViewById(R.id.pageLoader2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        requestFailure = (ConstraintLayout) findViewById(R.id.requestFailure);
        splashScreen = (ConstraintLayout) findViewById(R.id.splashScreen);
        reloadButton = (Button) findViewById(R.id.reloadButton);
        homeButton = (ImageButton) findViewById(R.id.home);
        searchbar = (EditText) findViewById(R.id.search);
        topbar = (LinearLayout) findViewById(R.id.topbar);
        webRequestHandler.getInstance().initialization(webView1,webView2,progressBar,searchbar,requestFailure,this);
        webView1.bringToFront();
        progressBar.animate().alpha(0f);

        Intent orbot = OrbotHelper.getOrbotStartIntent(getApplicationContext());
        getApplicationContext().registerReceiver(orbotStatusReceiver,new IntentFilter(OrbotHelper.ACTION_STATUS));
        getApplicationContext().sendBroadcast(orbot);

        session1 = new GeckoSession();
        webLoader = (GeckoView) findViewById(R.id.webLoader);
        runtime1 = GeckoRuntime.create(this);
        session1.open(runtime1);
        webLoader.setSession(session1);

        webView2.animate().setDuration(0).alpha(0f);
        progressBar.setVisibility(View.INVISIBLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        webView1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView1.setBackgroundColor(Color.WHITE);
        webView1.setWebViewClient(loadWebViewClient());
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.getSettings().setUseWideViewPort(true);

        loadURLAnimate("http://boogle.store/");

        webView2.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView2.setBackgroundColor(Color.WHITE);
        webView2.setWebViewClient(loadWebViewClient());
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.getSettings().setUseWideViewPort(true);

        requestFailure.animate().setDuration(0).alpha(0.0f);
        initializeViewClients();
        progressBar.animate().alpha(0f);

    }

    public void openSession()
    {
        session1 = new GeckoSession();
        webLoader = (GeckoView) findViewById(R.id.webLoader);
        GeckoRuntime runtime1 = GeckoRuntime.create(this);
        session1.open(runtime1);
        webLoader.setSession(session1);
    }

    private void initializeViewClients()
    {
        homeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                session1.stop();
                session1.close();
                session1.stop();
                progressBar.animate().alpha(0f);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.animate().setDuration(300).alpha(1f);
                loadURLAnimate("http://boogle.store/");
                isRequestError = false;
            }
        });

        searchbar.setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                try
                {
                    session1.stop();
                    session1.close();
                    String url = v.getText().toString();
                    if(!url.startsWith("www.")&& !url.startsWith("http://")&& !url.startsWith("https://")){
                        url = "www."+url;
                    }
                    if(!url.startsWith("http://")&&!url.startsWith("https://")){
                        url = "http://"+url;
                    }


                    boolean isUrlValid = Patterns.WEB_URL.matcher(url).matches();

                    URL host = new URL(url);
                    if(isUrlValid && host.getHost().replace("www.","").contains("."))
                    {
                        if(host.getHost().contains("boogle.store")||host.getHost().contains("genesis.store"))
                        {
                            loadURLAnimate(v.getText().toString());
                            return true;
                        }
                        else if(host.getHost().contains(".onion"))
                        {
                            if(!reinitOrbot())
                            {
                                session1.close();
                                session1 = new GeckoSession();
                                session1.open(runtime1);
                                session1.setProgressDelegate(new ExamplesProgressDelegate());
                                webLoader.releaseSession();
                                webLoader.setSession(session1);

                                session1.loadUri(url);
                            }
                            return true;
                        }
                        else
                        {
                            baseURLError();
                            return true;
                        }
                    }
                    else
                    {
                        loadURLAnimate("http://boogle.store/search?q="+v.getText().toString().replaceAll(" ","+")+"&p_num=1&s_type=all");
                    }
                    reinitOrbot();

                }
                catch (IOException e)
                {
                    loadURLAnimate("http://boogle.store/search?q="+v.getText().toString().replaceAll(" ","+")+"&p_num=1&s_type=all");
                    e.printStackTrace();
                }
                return false;
            }
        });

        reloadButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                progressBar.animate().alpha(0f);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.animate().setDuration(300).alpha(1f);
                loadURLAnimate(currentURL);
                isRequestError = false;
            }
        });

        session1.setProgressDelegate(new ExamplesProgressDelegate());

    }

    public static String getDomainName(String url) throws URISyntaxException
    {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void onChromeClientProgressChanged(int newProgress)
    {

    }

    public void baseURLError()
    {
        new LovelyInfoDialog(this)
                .setTopColorRes(R.color.header)
                .setIcon(R.drawable.logo)
                .setTitle("Surface Web URL Not Allowed")
                .setMessage("This software can only be used to search hidden web such as \"Onion\" and \"I2P\" for searching in Surface Web use \"Google\" or \"Bing\"")
                .show();
    }

    public void startingOrbotInfo()
    {
        new LovelyInfoDialog(this)
                .setTopColorRes(R.color.header)
                .setIcon(R.drawable.logo)
                .setTitle("Orbot is Starting")
                .setMessage("Looks Like Orbot is Installed but not Running. Please wait while we Start Orbot for you")
                .show();
    }

    public boolean reinitOrbot()
    {
        if(!OrbotHelper.isOrbotInstalled(this))
        {
            OrbotHelper.getOrbotInstallIntent(this);
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.header)
                    .setIcon(R.drawable.logo)
                    .setTitle("Orbot Proxy Not Installed")
                    .setMessage("Hidden Web can only be access by Special Proxies. Please Install Orbot Proxy from Playstore")
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String appPackageName = "org.torproject.android"; // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }
        if(!isOrbotRunning)
        {
            OrbotHelper.get(this).init();
            startingOrbotInfo();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        reinitOrbot();
        if(urlList.size()>0)
        {
            searchbar.setText(urlList.peek().toString().replaceAll("boogle.store","genesis.onion"));
            if(urlList.peek().toString().contains("boogle.store"))
            {
                if(!currentURL.contains("boogle.store"))
                {
                    currentURL = urlList.pop().toString();
                    progressBar.animate().alpha(0f);
                    webLoader.animate().setDuration(250).alpha(0);
                    webLoader.setVisibility(View.INVISIBLE);
                }
                else
                {
                    loadURLAnimate(urlList.pop().toString());
                    if(urlList.size()<=0)
                    {
                        currentURL = "http://boogle.store/";
                    }
                    else
                    {
                        currentURL = urlList.peek().toString();
                    }
                }
            }
            else
            {
                wasBackPressed = true;
                if(urlList.size()<=0 || urlList.peek().toString().contains("boogle.store"))
                {
                    currentURL = "http://boogle.store/";
                    webLoader.animate().setDuration(250).alpha(0);
                }
                else
                {
                    webLoader.animate().setDuration(250).alpha(1);
                    currentURL = urlList.peek().toString();
                }
                urlList.pop();
                session1.stop();
                session1.close();
                session1.stop();
                session1.goBack();
            }
            if(urlList.size()==0)
            {
                searchbar.setText("http://genesis.onion/");
            }
        }
    }

    private boolean isAnimating = false;
    private WebViewClient loadWebViewClient()
    {
        WebViewClient client = new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url)
            {
                searchbar.setText(url.replaceAll("boogle.store","genesis.onion"));

                if(!url.toString().contains("boogle"))
                {
                    //admanager.getInstance().showAd();

                    boolean init_status=reinitOrbot();
                    if(!init_status)
                    {
                        session1.close();
                        session1 = new GeckoSession();
                        session1.open(runtime1);
                        session1.setProgressDelegate(new ExamplesProgressDelegate());
                        webLoader.releaseSession();
                        webLoader.setSession(session1);

                        session1.loadUri(url);
                    }
                    return true;
                }
                else
                {
                    if(urlList.size()==0 || !currentURL.equals(urlList.peek()))
                    {
                        urlList.add(currentURL);
                        currentURL = url;
                    }

                    loadURLAnimate(url);
                    return true;
                }
            }
            @Override
            public void onPageFinished(WebView  view, String  url)
            {
                super.onPageFinished(view, url);

                webView1.animate().setDuration(250).alpha(1f);
                webView2.animate().setDuration(250).alpha(1f).withEndAction((new Runnable() {
                    @Override
                    public void run()
                    {
                        datamodel.getInstance().setIsLoadingURL(false);
                        requestFailure.animate().alpha(0f).setDuration(300).withEndAction((new Runnable() {
                            @Override
                            public void run()
                            {
                                requestFailure.setVisibility(View.INVISIBLE);
                            }
                        }));;

                    }
                }));;

                progressBar.animate().alpha(0f).withEndAction((new Runnable() {
                    @Override
                    public void run()
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }));;

                if(!hasApplicationLoaded)
                {
                    try
                    {
                        sleep(2000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    hasApplicationLoaded = true;
                    splashScreen.animate().alpha(0.0f).setDuration(300).setListener(null).withEndAction((new Runnable() {
                        @Override
                        public void run()
                        {
                            splashScreen.setVisibility(View.GONE);
                        }
                    }));
                }
                if(!isRequestError)
                {
                        requestFailure.animate().alpha(0.0f).setDuration(300).setListener(null).withEndAction((new Runnable() {
                        @Override
                        public void run()
                        {
                            requestFailure.setVisibility(View.GONE);
                            //reloadButton.setEnabled(false);
                        }
                    }));
                }
                isRequestError = false;

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchbar.getWindowToken(), 0);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                //reloadButton.setEnabled(true);
                System.out.println("SUP2");
                requestFailure.setVisibility(View.VISIBLE);
                requestFailure.animate().alpha(1.0f);
                isRequestError = true;

                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        };

        if(!isNetworkAvailable())
        {
            hasApplicationLoaded = true;
            splashScreen.animate().setStartDelay(2000).alpha(0.0f).setDuration(300).setListener(null).withEndAction((new Runnable() {
                @Override
                public void run()
                {
                    splashScreen.setVisibility(View.GONE);
                }
            }));

            requestFailure.setVisibility(View.VISIBLE);
            requestFailure.animate().alpha(1.0f);
            isRequestError = true;
        }

        return client;
    }

    public void loadURLAnimate(String url)
    {
        if(!isAnimating)
        {
            webRequestHandler.getInstance().loadURL(url);
        }
    }
boolean isPageLoaded = false;
class ExamplesProgressDelegate implements GeckoSession.ProgressDelegate
{
    @Override
    public void onPageStart(GeckoSession session, String url)
    {
        try
        {
            URL host = new URL(url);
            if(!host.getHost().contains("onion"))
            {
                session1.stop();
                session1.close();
                session1.stop();
                baseURLError();
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        isPageLoaded = false;
        datamodel.getInstance().setIsLoadingURL(true);
        boolean isBlackPage = url.equals("about:blank");
        if(!isBlackPage &&!wasBackPressed)
        {
            urlList.add(currentURL);
            currentURL = url;
        }
        wasBackPressed = false;
        if(!isBlackPage)
        {
            progressBar.setAlpha(0);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.animate().setDuration(300).alpha(1f);
        }
    }
    @Override
    public void onPageStop(GeckoSession session, boolean success)
    {
        progressBar.animate().alpha(0f);
        datamodel.getInstance().setIsLoadingURL(false);
    }

    @Override
    public void onProgressChange(GeckoSession session, int progress)
    {
        if(progress>=50 && !isPageLoaded)
        {
            isPageLoaded = true;
            webLoader.bringToFront();
            webLoader.animate().setDuration(100).alpha(1);
            webLoader.setVisibility(View.VISIBLE);

            requestFailure.animate().alpha(0f).setDuration(300).withEndAction((new Runnable() {
                @Override
                public void run()
                {
                    requestFailure.setVisibility(View.INVISIBLE);
                }
            }));;
        }
    }

    @Override
    public void onSecurityChange(GeckoSession session, SecurityInformation securityInfo)
    {
    }
}

    public boolean isNetworkAvailable()
    {
        ConnectivityManager cm = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    boolean isOrbotRunning = false;
    private BroadcastReceiver orbotStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(),
                    OrbotHelper.ACTION_STATUS)) {
                String status = intent.getStringExtra(OrbotHelper.EXTRA_STATUS);
                if (status.equals(OrbotHelper.STATUS_ON))
                {
                    isOrbotRunning = true;
                }
                else if (status.equals(OrbotHelper.STATUS_OFF))
                {
                    isOrbotRunning = false;
                }
                else if (status.equals(OrbotHelper.STATUS_STARTING))
                {
                }
                else if (status.equals(OrbotHelper.STATUS_STOPPING))
                {
                }
            }
        }
    };
}
