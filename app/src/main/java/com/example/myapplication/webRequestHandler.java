package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
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

import android.os.Handler;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.client.StrongBuilder;
import info.guardianproject.netcipher.proxy.OrbotHelper;

public class webRequestHandler implements StrongBuilder.Callback<HttpClient>
{
    private static final webRequestHandler ourInstance = new webRequestHandler();
    private WebView[] view = new WebView[2];
    private ProgressBar progressBar;
    private EditText searchbar;
    private ConstraintLayout requestFailure;

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

    public static webRequestHandler getInstance() {
        return ourInstance;
    }

    private webRequestHandler()
    {
    }

    public void initialization(WebView view1, WebView view2, ProgressBar progressBar, EditText searchbar, ConstraintLayout requestFailure, Context applicationContext,ConstraintLayout splash)
    {
        this.splash =  splash;
        this.view[0] = view1;
        this.view[1] = view2;
        this.progressBar = progressBar;
        this.searchbar = searchbar;
        this.requestFailure = requestFailure;
        OrbotHelper.get(applicationContext).init();
        createUpdateUiHandler();
    }

    public void loadURL(final String url)
    {

        try
        {
            Log.i("WOW MAN 0","WOW MAN 2 : " + currenturl + "----" + url);
            if(!currenturl.equals(url) || isReloadedUrl)
            {
                Log.i("WOW MAN 1","WOW MAN 2");
                isReloadedUrl = false;
                currenturl = url;
                preInitialization(url);
            }
            else
            {
                Message message = new Message();
                message.what = RELOAD_ERROR;
                updateUIHandler.sendMessage(message);
                return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        clientThread = new Thread(() -> {
            try
            {
                currenturl = url;
                if(url.contains("boogle.store"))
                {
                    nonProxyConnection(url);
                }
                else
                {
                    proxyConnection(url);
                }
            }
            catch (Exception e)
            {
                if(!e.getMessage().contains("failed to respond"))
                {
                    Log.i("SUP3",e.getMessage()+"");
                    onError();
                    e.printStackTrace();
                }
            }
        });
        clientThread.start();
    }

    public void preInitialization(String url)
    {
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
        progressBar.animate().setDuration(150).alpha(0f);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate().setDuration(150).alpha(1f);

    }

    public void nonProxyConnection(String url) throws IOException {
        url = url.replace("http://boogle","https://boogle");

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

    public void proxyConnection(String url) throws Exception {
        NetCipher.useTor();
        HttpURLConnection connection = NetCipher.getHttpURLConnection(url);
        connection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
        connection.setRequestProperty("Accept","*/*");
        connection.connect();

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

    public void onError()
    {
        if(!isReloadedUrl)
        {
            Message message = new Message();
            message.what = INTERNET_ERROR;
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
                        view[currentViewIndex].animate().setDuration(0).alpha(0f).withEndAction((() -> {
                        }));
                    }
                    else if (msg.what == INTERNET_ERROR)
                    {
                        splash.animate().setStartDelay(2000).alpha(0);
                        datamodel.getInstance().setIsLoadingURL(false);
                        progressBar.animate().setDuration(150).alpha(0f);
                        requestFailure.setVisibility(View.VISIBLE);
                        requestFailure.animate().alpha(1f).setDuration(300).withEndAction((() -> {
                        }));
                    }
                    else if (msg.what == RELOAD_ERROR)
                    {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.animate().setDuration(150).alpha(0f);
                            }
                        }, 1000);

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
}
