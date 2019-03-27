package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import info.guardianproject.netcipher.proxy.OrbotHelper;
import org.mozilla.gecko.PrefsHelper;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;


import static java.lang.Thread.sleep;

public class application_controller extends AppCompatActivity
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
    private String version_code = "1.0";

    /*helper Variables*/
    Stack traceUrlList = new Stack<String>();

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_view);
        initializeProxy();
        initializeConnections();
        initializeOrbot();
        initializeWebViews();
        initializeView();
        initializeAds();
    }

    public void versionChecker()
    {
        String version = preference_manager.getInstance().getString("version","none",this);
        if(!version.equals(version_code) && !version.equals("none"))
        {
            message_manager.getInstance().versionWarning(this);
        }
        if(version.equals("none"))
        {
            webRequestHandler.getInstance().getVersion(this);
        }
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
        topbar = findViewById(R.id.topbar);
        webLoader = findViewById(R.id.webLoader);
    }

    public void initializeOrbot()
    {
        Intent orbot = OrbotHelper.getOrbotStartIntent(getApplicationContext());
        getApplicationContext().registerReceiver(orbot_manager.getInstance().orbotStatusReceiver,new IntentFilter(OrbotHelper.ACTION_STATUS));
        getApplicationContext().sendBroadcast(orbot);
    }

    public void initializeWebViews()
    {
        webRequestHandler.getInstance().initialization(webView1,webView2,progressBar,searchbar,requestFailure,this);
        webView1.bringToFront();
        progressBar.animate().alpha(0f);

        session1 = new GeckoSession();
        runtime1 = GeckoRuntime.create(this);
        session1.open(runtime1);
        webLoader.setSession(session1);
        session1.setProgressDelegate(new progressDelegate());
        webLoader.setVisibility(View.INVISIBLE);
    }

    /*Initialization*/
    public void initializeView()
    {
        webView1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView1.setBackgroundColor(Color.WHITE);
        webView1.setWebViewClient(loadWebViewClient());
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.getSettings().setUseWideViewPort(true);

        webView2.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView2.setBackgroundColor(Color.WHITE);
        webView2.setWebViewClient(loadWebViewClient());
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.getSettings().setUseWideViewPort(true);

        webView2.animate().setDuration(0).alpha(0f);
        progressBar.setVisibility(View.INVISIBLE);
        requestFailure.animate().setDuration(0).alpha(0.0f);
        progressBar.animate().alpha(0f);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        loadURLAnimate(constants.backendUrl);
        initializeViewClients();
    }

    private void initializeViewClients()
    {
        searchbar.setOnEditorActionListener((v, actionId, event) -> {
            return onEditorClicked(v, actionId, event);
        });
    }

    /*-------------------------------------------------------WEBVIEW LISTENERS-------------------------------------------------------*/

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

                    boolean init_status=orbot_manager.getInstance().reinitOrbot(application_controller.this);
                    if(!init_status)
                    {
                        session1.close();
                        session1 = new GeckoSession();
                        session1.open(runtime1);
                        session1.setProgressDelegate(new progressDelegate());
                        webLoader.releaseSession();
                        webLoader.setSession(session1);

                        session1.loadUri(url);
                    }
                    return true;
                }
                else
                {
                    if(traceUrlList.size()==0 || !status.currentURL.equals(traceUrlList.peek()))
                    {
                        traceUrlList.add(status.currentURL);
                        status.currentURL = url;
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
                webView2.animate().setDuration(250).alpha(1f).withEndAction((() -> {
                    datamodel.getInstance().setIsLoadingURL(false);
                    requestFailure.animate().alpha(0f).setDuration(300).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));;

                }));;

                progressBar.animate().alpha(0f).withEndAction((() -> progressBar.setVisibility(View.INVISIBLE)));;

                if(!status.hasApplicationLoaded)
                {
                    try
                    {
                        sleep(2000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    status.hasApplicationLoaded = true;
                    splashScreen.animate().alpha(0.0f).setDuration(300).setListener(null).withEndAction((() -> splashScreen.setVisibility(View.GONE)));
                    versionChecker();
                }

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchbar.getWindowToken(), 0);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                System.out.println("SUP2");
                requestFailure.setVisibility(View.VISIBLE);
                requestFailure.animate().alpha(1.0f);
                loadErrorPage();
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
            loadErrorPage();
        }

        return client;
    }

class progressDelegate implements GeckoSession.ProgressDelegate
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
                message_manager.getInstance().baseURLError(application_controller.this);
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        datamodel.getInstance().setIsLoadingURL(true);
        boolean isBlackPage = url.equals("about:blank");
        if(!isBlackPage)
        {
            traceUrlList.add(status.currentURL);
            status.currentURL = url;
        }
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
        if(progress>=50 && webLoader.getVisibility()==View.INVISIBLE)
        {
            webLoader.bringToFront();
            webLoader.animate().setDuration(100).alpha(1);
            webLoader.setVisibility(View.VISIBLE);

            requestFailure.animate().alpha(0f).setDuration(300).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));;
        }
    }

    @Override
    public void onSecurityChange(GeckoSession session, SecurityInformation securityInfo)
    {
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
        webRequestHandler.getInstance().loadURL(url);
    }

    /*-------------------------------------------------------EVENT LISTENERS-------------------------------------------------------*/

    public void onHomeButtonPressed(View view)
    {
        session1.stop();
        session1.close();
        session1.stop();
        progressBar.animate().alpha(0f);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate().setDuration(300).alpha(1f);
        loadURLAnimate("http://boogle.store/");
    }

    public void onReloadButtonPressed(View view)
    {
        progressBar.animate().alpha(0f);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate().setDuration(300).alpha(1f);
        loadURLAnimate(status.currentURL);
    }

    @Override
    public void onBackPressed()
    {
        orbot_manager.getInstance().reinitOrbot(this);
        if(traceUrlList.size()>0)
        {
            searchbar.setText(traceUrlList.peek().toString().replaceAll("boogle.store","genesis.onion"));
            if(traceUrlList.peek().toString().contains("boogle.store"))
            {
                if(!status.currentURL.contains("boogle.store"))
                {
                    status.currentURL = traceUrlList.pop().toString();
                    progressBar.animate().alpha(0f);
                    webLoader.animate().setDuration(250).alpha(0);
                    webLoader.setVisibility(View.INVISIBLE);
                }
                else
                {
                    loadURLAnimate(traceUrlList.pop().toString());
                    if(traceUrlList.size()<=0)
                    {
                        status.currentURL = "http://boogle.store/";
                    }
                    else
                    {
                        status.currentURL = traceUrlList.peek().toString();
                    }
                }
            }
            else
            {
                if(traceUrlList.size()<=0 || traceUrlList.peek().toString().contains("boogle.store"))
                {
                    status.currentURL = "http://boogle.store/";
                    webLoader.animate().setDuration(250).alpha(0);
                }
                else
                {
                    webLoader.animate().setDuration(250).alpha(1);
                    status.currentURL = traceUrlList.peek().toString();
                }
                traceUrlList.pop();
                session1.stop();
                session1.close();
                session1.stop();
                session1.goBack();
            }
            if(traceUrlList.size()==0)
            {
                searchbar.setText("http://genesis.onion/");
            }
        }
    }

    public boolean onEditorClicked(TextView v, int actionId, KeyEvent event)
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
                if(host.getHost().contains(constants.backendUrlHost)||host.getHost().contains(constants.frontEndUrlHost))
                {
                    loadURLAnimate(v.getText().toString());
                    return true;
                }
                else if(host.getHost().contains(constants.allowedHost))
                {
                    if(!orbot_manager.getInstance().reinitOrbot(this.getApplicationContext()))
                    {
                        session1.close();
                        session1 = new GeckoSession();
                        session1.open(runtime1);
                        session1.setProgressDelegate(new application_controller.progressDelegate());
                        webLoader.releaseSession();
                        webLoader.setSession(session1);

                        session1.loadUri(url);
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
                loadURLAnimate("http://boogle.store/search?q="+v.getText().toString().replaceAll(" ","+")+"&p_num=1&s_type=all");
            }
            orbot_manager.getInstance().reinitOrbot(this.getApplicationContext());

        }
        catch (IOException e)
        {
            loadURLAnimate("http://boogle.store/search?q="+v.getText().toString().replaceAll(" ","+")+"&p_num=1&s_type=all");
            e.printStackTrace();
        }
        return false;
    }

}
