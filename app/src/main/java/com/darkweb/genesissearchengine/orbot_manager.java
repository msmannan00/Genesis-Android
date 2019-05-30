package com.darkweb.genesissearchengine;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.toronionproxy.OnionProxyManager;
import org.mozilla.gecko.PrefsHelper;

import java.io.IOException;

public class orbot_manager {
    private static final orbot_manager ourInstance = new orbot_manager();
    boolean isOrbotRunning = false;
    boolean isLoading = false;
    OnionProxyManager onionProxyManager = null;
    Context applicationContext = null;
    WebView view1=null;
    WebView view2=null;

    public static orbot_manager getInstance() {
        return ourInstance;
    }

    private orbot_manager() {
    }

    public boolean reinitOrbot(Context application_context)
    {
        if(!status.isTorInitialized)
        {
            message_manager.getInstance().startingOrbotInfo(application_context);
            initializeTorClient(application_context,view1,view2);
            return false;
        }
        else
        {
            return true;
        }
    }

    public void restartOrbot(Context applicationContext)
    {
        this.applicationContext = applicationContext;
        isOrbotRunning = false;
        status.isTorInitialized = false;
        if(onionProxyManager!=null)
        {
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        onionProxyManager.stop();
                        //initializeTorClient(applicationContext);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void initializeTorClient(Context applicationContext,WebView view1,WebView view2)
    {
        if(view1==null)
        {
            this.view1 = view1;
            this.view2 = view2;
        }
        if(isLoading)
        {
           return;
        }
        new Thread()
        {
            public void run()
            {
                    while (true)
                    {
                        try
                        {
                            isLoading = true;
                            String fileStorageLocation = "torfiles";

                            if(onionProxyManager!=null && onionProxyManager.isRunning())
                            {
                                break;
                            }
                            onionProxyManager = new AndroidOnionProxyManager(applicationContext, fileStorageLocation);

                            int totalSecondsPerTorStartup = 4 * 60;
                            int totalTriesPerTorStartup = 5;
                                boolean ok = onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup);
                                if (!ok) {
                                    Log.i("TorTest", "Couldn't start Tor!");
                                    return;
                                } else {
                                    if (onionProxyManager.isRunning()) {
                                        Log.i("My App", "Tor initialized on port " + onionProxyManager.getIPv4LocalHostSocksPort());
                                    }
                                }

                            while (!onionProxyManager.isRunning()) {
                                sleep(1000);
                            }
                            if (onionProxyManager.isRunning()) {
                                Log.i("My App", "Tor initialized on port " + onionProxyManager.getIPv4LocalHostSocksPort());
                                status.port = onionProxyManager.getIPv4LocalHostSocksPort();
                                initializeProxy();
                                sleep(1500);
                                status.isTorInitialized = true;
                                break;
                            }
                            isLoading = false;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            continue;
                        }
                    }
                }

        }.start();
    }


    public void initializeProxy()
    {
        PrefsHelper.setPref("network.proxy.type",1); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks","127.0.0.1"); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks_port",status.port); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks_version",5); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks_remote_dns",true); //manual proxy settings
        PrefsHelper.setPref("browser.cache.disk.enable",false);
        PrefsHelper.setPref("browser.cache.memory.enable",false);
        PrefsHelper.setPref("general.useragent.override", "Mozilla/5.0 (Windows NT 6.1; rv:17.0) Gecko/20100101 Firefox/17.0");
        PrefsHelper.setPref("privacy.donottrackheader.enabled",false);
        PrefsHelper.setPref("privacy.donottrackheader.value",1);
    }



}
