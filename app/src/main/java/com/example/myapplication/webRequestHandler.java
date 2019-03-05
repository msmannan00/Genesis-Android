package com.example.myapplication;

import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.os.Handler;

public class webRequestHandler
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
    private HttpClient client = null;
    private Thread clientThread = null;
    private Boolean isRendering = false;
    private ConstraintLayout requestFailure;

    public static webRequestHandler getInstance() {
        return ourInstance;
    }

    private webRequestHandler()
    {
    }

    public void initialization(WebView view1,WebView view2,ProgressBar progressBar,EditText searchbar,ConstraintLayout requestFailure)
    {
        this.view[0] = view1;
        this.view[1] = view2;
        this.progressBar = progressBar;
        this.searchbar = searchbar;
        this.requestFailure = requestFailure;
        createUpdateUiHandler();
    }

    public void loadURL(final String url)
    {
        try
        {
            progressBar.setVisibility(View.VISIBLE);
            if(isRendering)
            {
                return;
            }

            if(!isLoading)
            {
                searchbar.setText(url.replace("http://boogle.store","http://genesis.onion"));
            }
            else if(client!=null)
            {
                client.getConnectionManager().shutdown();
                isLoading = false;
            }
            if(clientThread!=null)
            {
                clientThread.interrupt();
            }
            client = new DefaultHttpClient();
        }
        catch (Exception ex)
        {
            Log.d("SUPER WOW1","SUPER WOW");
        }
        clientThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try
                {
                        isLoading = true;
                        HttpGet request = new HttpGet(url);
                        baseURL = url;
                        HttpResponse response = client.execute(request);
                        InputStream in = response.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder str = new StringBuilder();
                        String line = null;

                        while((line = reader.readLine()) != null)
                        {
                            if(!isLoading)
                            {
                                return;
                            }
                            str.append(line);
                        }
                        in.close();
                        html = str.toString();
                        Message message = new Message();
                        message.what = MESSAGE_UPDATE_TEXT_CHILD_THREAD;
                        updateUIHandler.sendMessage(message);
                }
                catch (Exception e)
                {
                    Message message = new Message();
                    message.what = INTERNET_ERROR;
                    updateUIHandler.sendMessage(message);
                    Log.d("SUPER WOW2","SUPER WOW");
                    e.printStackTrace();
                }
            }
        });
        clientThread.start();
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
                    if(msg.what == MESSAGE_UPDATE_TEXT_CHILD_THREAD && isLoading &&!isRendering)
                    {
                        isRendering = true;
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
                                isLoading = false;
                                progressBar.setVisibility(View.INVISIBLE);
                                isRendering = false;
                            }
                        }));
                    }
                    else if (msg.what == INTERNET_ERROR)
                    {
                        isRendering = false;
                        isLoading = false;
                        progressBar.setVisibility(View.INVISIBLE);
                        requestFailure.setVisibility(View.VISIBLE);
                        requestFailure.animate().alpha(1f).setDuration(500);
                    }
                }
            };
        }
    }
}
