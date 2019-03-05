package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import javax.net.ssl.TrustManager;

import android.webkit.WebView;
import info.guardianproject.netcipher.client.StrongBuilder;
import info.guardianproject.netcipher.client.StrongOkHttpClientBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StrongBuild implements StrongBuilder.Callback<OkHttpClient> {

    private static final StrongBuild ourInstance = new StrongBuild();

    public static StrongBuild getInstance() {
        return ourInstance;
    }

    public String url;
    public String htmlCode;
    public WebView view;

    public void loadURL(String url, WebView view, Context applicationContext){
        try{
            this.url = url;
            this.view = view;
            StrongOkHttpClientBuilder.
                    forMaxSecurity(applicationContext).
                    withTorValidation().
                    withBestProxy().
                    build(StrongBuild.this);

        }catch(Exception e){
            e.printStackTrace();
            Log.e("info", "ERROR");
        }
    }

    @Override
    public void onConnected(final OkHttpClient okHttpClient){
        Log.e("info" , "CONNECTED Strong Builder");

        view.loadDataWithBaseURL(null, "ASD", "text/html", "utf-8", null);
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    Request request = new Request.Builder().url(url).build();
                    Response response = okHttpClient.newCall(request).execute();

                    Log.e("info", "RESPONSE: "+response.toString());
                    Log.e("info", response.body().string());
                    htmlCode = response.body().string();

                    Log.d("LOADING : " ,"LOADING");
                    view.loadDataWithBaseURL(null, htmlCode, "text/html", "utf-8", null);

                }catch(Exception e){
                    e.printStackTrace();
                    Log.e("info", "ERROR - ATTEMPTING CONNECTION TO ONION DOMAIN");
                }
            }
        }).start();
    }

    @Override
    public void onConnectionException(Exception e){
        Log.e("info" , "Exception");
    }

    @Override
    public void onTimeout(){
        Log.e("info" , "Timeout");
    }

    @Override
    public void onInvalid(){
        Log.e("info" , "Invalid");
    }
}