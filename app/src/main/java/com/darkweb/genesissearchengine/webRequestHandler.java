package com.darkweb.genesissearchengine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import android.os.Handler;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.conn.ssl.NoopHostnameVerifier;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.TrustSelfSignedStrategy;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.ssl.SSLContexts;

public class webRequestHandler
{
    private static final webRequestHandler ourInstance = new webRequestHandler();
    private WebView[] view = new WebView[2];
    private ProgressBar progressBar;
    private EditText searchbar;
    private ConstraintLayout requestFailure;

    public boolean reloadError=false;
    public boolean isReloadedUrl = false;
    private int viewIndex = 1;
    private int currentViewIndex = 0;
    private String html = "";
    private String baseURL = "";
    public Thread clientThread = null;
    HttpGet request = null;
    private Handler updateUIHandler = null;
    public boolean isUrlStoped = false;
    private String currenturl = "";
    private ConstraintLayout splash;
    private final static int INTERNET_ERROR =2;
    private final static int MESSAGE_UPDATE_TEXT_CHILD_THREAD =1;
    private final static int RELOAD_ERROR =3;
    private application_controller controller;

    public static webRequestHandler getInstance() {
        return ourInstance;
    }

    private webRequestHandler()
    {
    }

    public void initialization(WebView view1, WebView view2, ProgressBar progressBar, EditText searchbar, ConstraintLayout requestFailure, Context applicationContext,ConstraintLayout splash,application_controller controller)
    {
        this.controller = controller;
        this.splash =  splash;
        this.view[0] = view1;
        this.view[1] = view2;
        this.progressBar = progressBar;
        this.searchbar = searchbar;
        this.requestFailure = requestFailure;
        createUpdateUiHandler();
    }

    public void loadURL(final String url)
    {

        Log.i("STEST : 1","1 : " + currenturl.equals(url) + " : " + isReloadedUrl + " : " + !reloadError);
        try
        {
            if(!currenturl.equals(url) || isReloadedUrl || !reloadError)
            {
                Log.i("STEST : 2","1");
                isReloadedUrl = false;
                preInitialization(url);
                currenturl = url;
            }
            else
            {
                Log.i("STEST : 3","1");
                Message message = new Message();
                message.what = RELOAD_ERROR;
                updateUIHandler.sendMessage(message);
                return;
            }
        }
        catch (Exception e)
        {
            Log.i("STEST : 4","1 : " + e.getMessage());
            e.printStackTrace();
        }

        clientThread = new Thread(() -> {
            try
            {
                Log.i("STEST : 5","1");
                currenturl = url;
                if(url.contains("boogle.store"))
                {
                    Log.i("STEST : 6","1");
                    nonProxyConnection(url);
                }
                else
                {
                    Log.i("STEST : 7","1");
                    proxyConnection(url);
                }
            }
            catch (Exception e)
            {
                Log.i("STEST : 8","1");
                if(!e.getMessage().contains("Socket closed") && !e.getMessage().contains("failed to respond") && e.getMessage().contains("Unable to resolve host \"boogle.store\""))
                {
                    Log.i("STEST99 : 9","1 : "+e.getMessage());
                    onError();
                    reloadError=true;
                    e.printStackTrace();
                }
            }
        });
        clientThread.start();
    }

    public void preInitialization(String url)
    {
        Log.i("WOW MAN 0","WOW MAN 2 : " + currenturl + "----" + url);
        progressBar.setAlpha(0);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate().setDuration(150).alpha(1f);
        Log.i("WOW MAN 1","WOW MAN 2 : " + currenturl + "----" + url);

        if(!datamodel.getInstance().getIsLoadingURL())
        {
            datamodel.getInstance().setIsLoadingURL(true);
        }
        else
        {
            request.abort();
            isReloadedUrl = true;
            if(clientThread!=null)
            clientThread.stop();
            clientThread = null;
            searchbar.setText(url.replace("http://boogle.store","http://genesis.onion"));
        }
    }

    public void nonProxyConnection(String url) throws IOException {
        url = url.replace("http://boogle","https://boogle");
        HttpClient client=new DefaultHttpClient();;
        try {
                    SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
                    SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
                    NoopHostnameVerifier.INSTANCE);
                    client = HttpClients.custom().setSSLSocketFactory(scsf).build();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        request = new HttpGet(url);
        baseURL = url;
        HttpResponse response = client.execute(request);
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder str = new StringBuilder();
        String line = null;

        while((line = reader.readLine()) != null)
        {
            str.append(line);
        }
        in.close();

        html = str.toString();
        Message message = new Message();
        message.what = MESSAGE_UPDATE_TEXT_CHILD_THREAD;
        updateUIHandler.sendMessage(message);

    }

    public void reportURL(String url)
    {
        try
        {
            HttpGet reportrequest = new HttpGet(url);
            HttpClient client=new DefaultHttpClient();;
            HttpResponse response = client.execute(reportrequest);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void proxyConnection(String url) throws Exception {
    }

    public void onError()
    {
        Log.i("WOW222","WOW222 : " + isReloadedUrl);
        reloadError = true;
        if(!isReloadedUrl)
        {
            Message message = new Message();
            message.what = INTERNET_ERROR;
            updateUIHandler.sendMessage(message);
            Log.i("WOW222","WOW333 : " + isReloadedUrl);
        }
        else
        {
            Log.i("SUSHIT5","5");
            Message message = new Message();
            message.what = RELOAD_ERROR;
            updateUIHandler.sendMessage(message);
        }
        isReloadedUrl = false;
    }

    @SuppressLint("HandlerLeak")
    private void createUpdateUiHandler()
    {

        if(updateUIHandler == null)
        {
            updateUIHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    if(msg.what == MESSAGE_UPDATE_TEXT_CHILD_THREAD)
                    {
                        reloadError = false;
                        view[viewIndex].setAlpha(0);
                        view[viewIndex].bringToFront();
                        view[viewIndex].loadDataWithBaseURL(baseURL,html, "text/html", "utf-8", null);
                        //view[currentViewIndex].animate().alpha(1);

                        if(viewIndex==1)
                        {
                            viewIndex = 0;
                            currentViewIndex =1;
                        }
                        else
                        {
                            viewIndex = 1;
                            currentViewIndex=0;
                        }
                        //view[currentViewIndex].animate().setDuration(0).alpha(0f).withEndAction((() -> {
                        //}));
                    }
                    else if (msg.what == INTERNET_ERROR)
                    {
                        splash.animate().setStartDelay(2000).alpha(0);
                        datamodel.getInstance().setIsLoadingURL(false);
                        Log.i("PROBLEM28","");
                        progressBar.animate().setDuration(150).alpha(0f);
                        requestFailure.setVisibility(View.VISIBLE);
                        requestFailure.animate().alpha(1f).setDuration(300).withEndAction((() -> {
                        }));

                        if(!helperMethod.isNetworkAvailable(controller))
                        {
                            orbot_manager.getInstance().restartOrbot(controller);
                        }

                        Log.i("SUSHIT2","2");
                    }
                    else if (msg.what == RELOAD_ERROR)
                    {
                        Log.i("SUSHIT1","1");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("PROBLEM29","");
                                progressBar.animate().setDuration(150).alpha(0f);
                            }
                        }, 1000);

                    }
                }
            };
        }
    }

    public void getVersion(Context applicationContext)
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    String webPage = "https://boogle.store/version";
                    URL url = new URL(webPage);
                    URLConnection urlConnection = null;
                    urlConnection = url.openConnection();
                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    int numCharsRead;
                    char[] charArray = new char[1024];
                    StringBuffer sb = new StringBuffer();
                    while ((numCharsRead = isr.read(charArray)) > 0) {
                        sb.append(charArray, 0, numCharsRead);
                    }
                    String result = sb.toString();
                    preference_manager.getInstance().saveString("version",result,applicationContext);

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    /*****--------------------ASYNC TASK--------------------******/



}
