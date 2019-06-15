package com.darkweb.genesissearchengine.httpManager;

import android.os.Handler;
import android.os.Message;
import com.darkweb.genesissearchengine.appManager.app_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.messages;
import com.darkweb.genesissearchengine.helperMethod;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.conn.ssl.NoopHostnameVerifier;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.TrustSelfSignedStrategy;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.ssl.SSLContexts;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class httpclient
{
    private HttpGet request = null;
    private String html = "";

    public void httpConnection(String url,Handler updateUIHandler,boolean isCachedRequest)
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    String c_url = url.replace("http://boogle","https://boogle");

                    HttpClient client=new DefaultHttpClient();;
                    SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
                            SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
                            NoopHostnameVerifier.INSTANCE);
                    client = HttpClients.custom().setSSLSocketFactory(scsf).build();

                    request = new HttpGet(c_url);
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
                    if(c_url.equals(constants.backendUrlSlashed))
                    {
                        html = html.replace("/privacy","https://boogle.store/privacy").replace("/about","https://boogle.store/about").replace("/reportus","https://boogle.store/reportus").replace("\"search\"","https://boogle.store/search").replace("/create","https://boogle.store/create");
                        helperMethod.setHomepageHTML(html, app_model.getInstance().getAppContext());
                    }
                    if(!isCachedRequest)
                    {
                        startPostTask(messages.MESSAGE_UPDATE_TEXT_CHILD_THREAD,updateUIHandler);
                    }
                }
                catch (Exception e)
                {
                    if(!request.isAborted() && !isCachedRequest)
                    {
                        startPostTask(messages.INTERNET_ERROR,updateUIHandler);
                    }
                }
                request = null;
            }
        }.start();
    }

    public void startPostTask(int m_id,Handler updateUIHandler)
    {
        Message message = new Message();
        message.what = m_id;
        updateUIHandler.sendMessage(message);
    }

    public String getHtmlResponse()
    {
        return html;
    }

    public boolean isRequestLoading()
    {
        return request!=null;
    }

    public void stopRequest()
    {
        new Thread()
        {
            public void run()
            {
                request.abort();
            }
        }.start();
    }
}
