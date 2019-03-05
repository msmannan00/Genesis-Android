package com.example.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;

import static java.lang.Thread.sleep;

public class applicationController extends AppCompatActivity
{

    /*View Objects*/
    private WebView webView1;
    private WebView webView2;
    private ProgressBar progressBar;
    private ConstraintLayout requestFailure;
    private ConstraintLayout splashScreen;
    private Button reloadButton;
    private ImageButton homeButton;
    private EditText searchbar;
    private LinearLayout topbar;

    /*helper Variables*/
    String currentURL = "http://boogle.store/";
    boolean isRequestError = false;
    boolean hasApplicationLoaded = false;
    Stack urlList = new Stack<String>();
    int scroll1y=0;
    int scroll2y=0;

    /*Initialization*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_controller);
        initializeView();
        initializeProxy();
        initializeAds();
    }

    public void initializeProxy()
    {
        //torProxyServer.getInstance().initialize(this,this);
    }

    public void initializeAds()
    {

        admanager.getInstance().initialize(this);
    }

    /*Initialization*/
    public void initializeView()
    {
        webView1 = (WebView) findViewById(R.id.pageLoader1);
        webView2 = (WebView) findViewById(R.id.pageLoader2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        requestFailure = (ConstraintLayout) findViewById(R.id.requestFailure);
        splashScreen = (ConstraintLayout) findViewById(R.id.splashScreen);
        reloadButton = (Button) findViewById(R.id.reloadButton);
        homeButton = (ImageButton) findViewById(R.id.home);
        searchbar = (EditText) findViewById(R.id.search);
        topbar = (LinearLayout) findViewById(R.id.topbar);
        webRequestHandler.getInstance().initialization(webView1,webView2,progressBar,searchbar,requestFailure);
        webView1.bringToFront();

        webView2.animate().setDuration(0).alpha(0f);
        progressBar.setVisibility(View.INVISIBLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        webView1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView1.setBackgroundColor(Color.WHITE);
        webView1.setWebViewClient(loadWebViewClient());
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.loadUrl("http://boogle.store/");

        webView2.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView2.setBackgroundColor(Color.WHITE);
        webView2.setWebViewClient(loadWebViewClient());
        webView2.getSettings().setJavaScriptEnabled(true);

        requestFailure.animate().setDuration(0).alpha(0.0f);
        initializeViewClients();

    }

    private void initializeViewClients()
    {
        homeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                    webRequestHandler.getInstance().loadURL("http://boogle.store/");
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
                    String url = v.getText().toString();
                    if(!url.startsWith("www.")&& !url.startsWith("http://")){
                        url = "www."+url;
                    }
                    if(!url.startsWith("http://")){
                        url = "http://"+url;
                    }


                    boolean isUrlValid = Patterns.WEB_URL.matcher(url).matches();

                    URL host = new URL(url);
                    Log.i("SUPER WOW",host.getHost());
                    if(isUrlValid && host.getHost().replace("www.","").contains("."))
                    {
                        Log.i("WOW1","WOW1");
                        if(host.getHost().contains(".onion")||host.getHost().contains("boogle.store")||host.getHost().contains("genesis.store"))
                        {
                            webRequestHandler.getInstance().loadURL(v.getText().toString());
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Only onion urls allowed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        webRequestHandler.getInstance().loadURL("http://boogle.store/search?q="+v.getText().toString().replaceAll(" ","+")+"&p_num=1&s_type=all");
                    }
                }
                catch (IOException e)
                {
                    webRequestHandler.getInstance().loadURL("http://boogle.store/search?q="+v.getText().toString().replaceAll(" ","+")+"&p_num=1&s_type=all");
                    e.printStackTrace();
                }
                return false;
            }
        });

        Log.i("HELL1","HELL1");
        reloadButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                Log.i("HELL2","HELL2");
                webRequestHandler.getInstance().loadURL(currentURL);
                isRequestError = false;
            }
        });

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

    @Override
    public void onBackPressed()
    {
        if(urlList.size()>0)
        {
            boolean crul = !currentURL.equals("http://boogle.store/");
            boolean peek = !urlList.peek().equals("http://boogle.store/");

            Log.i("WOW",urlList.size() + "---" + urlList.peek() + "---" + currentURL + "---" + peek);
            currentURL = urlList.peek().toString();

            if(!currentURL.equals(urlList.peek()) && crul)
            {
                webRequestHandler.getInstance().loadURL("http://boogle.store/");
            }
            else
            {
                webRequestHandler.getInstance().loadURL(urlList.pop().toString());
            }
        }
    }

    private WebViewClient loadWebViewClient()
    {
        WebViewClient client = new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url)
            {
                Log.i("OVERRIDING URL 1 : ","SUCCESS : " + view);
                System.out.println(url);
                if(!currentURL.equals(url))
                {
                    urlList.add(currentURL);
                    currentURL = url;
                }
                if(!url.toString().contains("boogle"))
                {
                    Log.i("OVERRIDING URL 2 : ","SUCCESS : " + url);
                    //admanager.getInstance().showAd();
                    //StrongBuild.getInstance().loadURL("https://stackoverflow.com/questions/4543349/load-local-html-in-webview",view,getApplicationContext());
                }
                else
                {
                    Log.i("OVERRIDING URL 3 : ","SUCCESS : " + url);
                    webRequestHandler.getInstance().loadURL(url);
                    return true;
                }
                return false;
            }
            @Override
            public void onPageFinished(WebView  view, String  url)
            {
                Log.i("APPLIED : ","SUCCESS : APPLIED");
                super.onPageFinished(view, url);
                webView1.animate().setDuration(250).alpha(1f);
                webView2.animate().setDuration(250).alpha(1f);

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
                    splashScreen.animate().alpha(0.0f).setDuration(500).setListener(null).withEndAction((new Runnable() {
                        @Override
                        public void run()
                        {
                            splashScreen.setVisibility(View.GONE);
                        }
                    }));
                }
                if(!isRequestError)
                {
                        requestFailure.animate().alpha(0.0f).setDuration(500).setListener(null).withEndAction((new Runnable() {
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
                Log.i("SUPME3 : ","ERROR");
                //reloadButton.setEnabled(true);
                System.out.println("SUP2");
                requestFailure.setVisibility(View.VISIBLE);
                requestFailure.animate().alpha(1.0f);
                isRequestError = true;

                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        };

        return client;
    }

}
