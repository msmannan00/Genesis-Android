package com.darkweb.genesissearchengine.httpManager;

import android.annotation.SuppressLint;
import android.os.Message;


import android.os.Handler;
import com.darkweb.genesissearchengine.appManager.app_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.messages;
import com.darkweb.genesissearchengine.helperMethod;

import static com.darkweb.genesissearchengine.constants.enums.webRequestStatus.*;
import static java.lang.Thread.sleep;

public class webRequestHandler
{
    /*Private Variables*/
    private String html = "";
    private Handler updateUIHandler = null;
    private httpclient client;


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
        if(app_model.getInstance().getCurrentURL().equals(constants.backendUrlSlashed))
        {
            String html_local = helperMethod.readHomepageHTML(app_model.getInstance().getAppContext());
            if(html_local.length()>1)
            {
                html = html_local;
                client = new httpclient();
                client.httpConnection(app_model.getInstance().getCurrentURL(),updateUIHandler,true);
                startPostTask(messages.LOCAL_HOMEPAGE_CACHED);
                return;
            }
        }

        client = new httpclient();
        client.httpConnection(app_model.getInstance().getCurrentURL(),updateUIHandler,false);
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
                if(msg.what == messages.LOCAL_HOMEPAGE_CACHED)
                {
                    app_model.getInstance().getAppInstance().loadUrlOnWebview(html);
                }
                if(msg.what == messages.MESSAGE_UPDATE_TEXT_CHILD_THREAD)
                {
                    app_model.getInstance().getAppInstance().loadUrlOnWebview(client.getHtmlResponse());
                }
                else if (msg.what ==  messages.INTERNET_ERROR)
                {
                    app_model.getInstance().getAppInstance().onInternetError();
                }
            }
        };
    }
}
