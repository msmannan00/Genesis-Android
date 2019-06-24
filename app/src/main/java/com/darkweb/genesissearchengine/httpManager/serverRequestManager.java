package com.darkweb.genesissearchengine.httpManager;

import android.content.Context;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class serverRequestManager
{
    private static final serverRequestManager ourInstance = new serverRequestManager();

    public static serverRequestManager getInstance()
    {
        return ourInstance;
    }

    private serverRequestManager()
    {
    }

    public void reportURL(String url)
    {
        try
        {
            HttpGet reportrequest = new HttpGet(url);
            HttpClient client=new DefaultHttpClient();;
            client.execute(reportrequest);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
                    preference_manager.getInstance().setString("version",result);

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    public void versionChecker()
    {
        /*String version = preference_manager.getInstance().getString("version","none",this);
        if(!version.equals(version_code) && !version.equals("none"))
        {
            message_manager.getInstance().versionWarning(this,Build.SUPPORTED_ABIS[0]);
        }
        webRequestHandler.getInstance().getVersion(this);*/
    }

}
