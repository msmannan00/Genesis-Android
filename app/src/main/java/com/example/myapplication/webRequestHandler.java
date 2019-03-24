package com.example.myapplication;

import android.content.Context;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;

import android.os.Handler;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.conn.params.ConnRoutePNames;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.client.StrongBuilder;
import info.guardianproject.netcipher.proxy.OrbotHelper;

import javax.net.ssl.HttpsURLConnection;

public class webRequestHandler implements StrongBuilder.Callback<HttpClient>
{
    private static final webRequestHandler ourInstance = new webRequestHandler();
    private WebView[] view = new WebView[2];;
    private int viewIndex = 1;
    private int currentViewIndex = 0;
    private String html = "";
    private String baseURL = "";
    private Boolean isLoading = false;
    private ProgressBar progressBar;
    private EditText searchbar;
    private Thread clientThread = null;
    private ConstraintLayout requestFailure;
    HttpGet request = null;

    // test the local device proxy provided by Orbot/Tor
    private final static String PROXY_HOST = "127.0.0.1";
    private final static int PROXY_HTTP_PORT = 8118; // default for Orbot/Tor
    private final static int PROXY_SOCKS_PORT = 9050; // default for Orbot/Tor
    private Proxy.Type mProxyType = null;

    public static webRequestHandler getInstance() {
        return ourInstance;
    }

    private webRequestHandler()
    {
    }

    public void initialization(WebView view1, WebView view2, ProgressBar progressBar, EditText searchbar, ConstraintLayout requestFailure, Context applicationContext)
    {
        this.view[0] = view1;
        this.view[1] = view2;
        this.progressBar = progressBar;
        this.searchbar = searchbar;
        this.requestFailure = requestFailure;
        OrbotHelper.get(applicationContext).init();
        createUpdateUiHandler();
    }

    public boolean isReloadedUrl = false;
    public void loadURL(final String url)
    {
        Log.d("ME HERE 1 : ","SUPER WOW");
        try
        {
            if(!datamodel.getInstance().getIsLoadingURL())
            {
                datamodel.getInstance().setIsLoadingURL(true);
            }
            else
            {
                request.abort();
                isReloadedUrl = true;
                clientThread.stop();
                searchbar.setText(url.replace("http://boogle.store","http://genesis.onion"));
            }
            progressBar.animate().alpha(0f);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.animate().setDuration(300).alpha(1f);

        }
        catch (Exception ex)
        {
            Log.d("ERROR : ","SUPER WOW");
        }
        clientThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try
                {
                    if(url.contains("boogle.store"))
                    {
                        HttpClient client = new DefaultHttpClient();
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
                    else
                    {
                        proxyConnection(url);
                    }
                }
                catch (Exception e)
                {
                    if(!isReloadedUrl)
                    {
                        Message message = new Message();
                        message.what = INTERNET_ERROR;
                        updateUIHandler.sendMessage(message);
                        Log.d("ERROR : ","SUPER WOW");
                        e.printStackTrace();
                    }
                    isReloadedUrl = false;
                }
            }
        });
        clientThread.start();
    }

    public void proxyConnection(String url) throws Exception {
        NetCipher.useTor();
        HttpURLConnection connection = NetCipher.getHttpURLConnection(url);
        connection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
        connection.setRequestProperty("Accept","*/*");
        connection.connect();
        int status = connection.getResponseCode();

        int responseCode = connection.getResponseCode(); //can call this instead of con.connect()
        InputStream in;
        if (responseCode >= 400 && responseCode <= 499) {
            throw new Exception("Bad authentication status: " + responseCode); //provide a more meaningful exception message
        }
        else {
            in = connection.getInputStream();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        html = sb.toString();

        Message message = new Message();
        message.what = MESSAGE_UPDATE_TEXT_CHILD_THREAD;
        updateUIHandler.sendMessage(message);
    }

    private Handler updateUIHandler = null;
    private final static int MESSAGE_UPDATE_TEXT_CHILD_THREAD =1;
    private final static int INTERNET_ERROR =2;

    public WebView getView()
    {
        return view[currentViewIndex];
    }

    private void createUpdateUiHandler()
    {

        if(updateUIHandler == null)
        {
            updateUIHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    Log.i("APPLYING : ","SUCCESS : APPLYING");
                    if(msg.what == MESSAGE_UPDATE_TEXT_CHILD_THREAD)
                    {
                        view[viewIndex].animate().setDuration(0).alpha(0f);
                        view[viewIndex].bringToFront();
                        view[viewIndex].loadDataWithBaseURL(baseURL,html, "text/html", "utf-8", null);

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
                        view[currentViewIndex].animate().setDuration(0).alpha(0f).withEndAction((new Runnable() {
                            @Override
                            public void run()
                            {
                            }
                        }));
                    }
                    else if (msg.what == INTERNET_ERROR)
                    {
                        datamodel.getInstance().setIsLoadingURL(false);
                        progressBar.animate().alpha(0f);
                        requestFailure.setVisibility(View.VISIBLE);
                        requestFailure.animate().alpha(1f).setDuration(300).withEndAction((new Runnable() {
                            @Override
                            public void run()
                            {
                            }
                        }));;
                    }
                }
            };
        }
    }

    @Override
    public void onConnected(HttpClient httpClient) {

    }

    @Override
    public void onConnectionException(Exception e) {

    }

    @Override
    public void onTimeout() {
    }

    @Override
    public void onInvalid() {

    }
}
