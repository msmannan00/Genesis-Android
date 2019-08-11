package com.darkweb.genesissearchengine.httpManager;

import android.annotation.SuppressLint;
import android.os.Message;


import android.os.Handler;
import com.darkweb.genesissearchengine.appManager.home_activity.home_model;
import com.darkweb.genesissearchengine.constants.messages;

public class webRequestHandler
{
    /*Private Variables*/
    private String html = "";
    private Handler updateUIHandler = null;
    private httpclient client;
    private String requestedUrl = "";

    /*Initialization*/
    private static final webRequestHandler ourInstance = new webRequestHandler();

    public static webRequestHandler getInstance() {
        return ourInstance;
    }

    private webRequestHandler()
    {
        client = new httpclient();
        createUpdateUiHandler();
    }

    /*Helper Methods*/

    public void loadURL(final String url)
    {
        if(client.isRequestLoading())
        {
            client.stopRequest();
        }
        cachedURLSelector();
    }

    public void cachedURLSelector()
    {
        client = new httpclient();
        client.httpConnection(requestedUrl,updateUIHandler,false);
    }

    public void startPostTask(int m_id)
    {
        Message message = new Message();
        message.what = m_id;
        updateUIHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private void createUpdateUiHandler()
    {
        updateUIHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == messages.MESSAGE_UPDATE_TEXT_CHILD_THREAD)
                {
                    //home_model.getInstance().getHomeInstance().onloadURL(client.getHtmlResponse());
                }
                else if (msg.what ==  messages.INTERNET_ERROR)
                {
                    home_model.getInstance().getHomeInstance().onInternetErrorView();
                }
            }
        };
    }
}
