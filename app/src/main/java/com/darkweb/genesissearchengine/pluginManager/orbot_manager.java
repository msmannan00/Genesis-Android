package com.darkweb.genesissearchengine.pluginManager;

import com.darkweb.genesissearchengine.appManager.home_activity.app_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.toronionproxy.OnionProxyManager;
import org.mozilla.gecko.PrefsHelper;

public class orbot_manager {

    /*Private Variables*/
    private boolean isLoading = false;
    private int threadCounter = 100;
    /*Local Initialization*/
    private static final orbot_manager ourInstance = new orbot_manager();
    private OnionProxyManager onionProxyManager = null;

    public static orbot_manager getInstance()
    {
        return ourInstance;
    }

    private orbot_manager()
    {
    }

    /*Orbot Initialization*/
    public boolean initOrbot(String url)
    {
        if(!status.isTorInitialized)
        {
            fabricManager.getInstance().sendEvent("TOR NOT INITIALIZED : " + url);
            message_manager.getInstance().startingOrbotInfo(url);
            return false;
        }
        else
        {
            return true;
        }
    }

    public void reinitOrbot()
    {
        new Thread()
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        if(onionProxyManager!=null)
                        {
                            if(onionProxyManager.isRunning())
                            {
                                status.isTorInitialized = true;
                                threadCounter = 5000;
                            }
                        }
                        if(!isLoading && !status.isTorInitialized)
                        {
                            if(onionProxyManager == null)
                            {
                                onionProxyManager = new AndroidOnionProxyManager(app_model.getInstance().getAppContext(), strings.torfolder);
                            }
                            isLoading = false;
                            status.isTorInitialized = false;
                            initializeTorClient();
                        }
                        else
                        {
                            sleep(threadCounter);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    public String getLogs()
    {
        if(onionProxyManager!=null)
        {
            String Logs = onionProxyManager.getLastLog();
            if(Logs.equals(""))
            {
                return "Loading Please Wait";
            }
            Logs=Logs.replace("FAILED","Securing");
            return Logs;
        }
        return "Loading Please Wait";
    }

    public void initializeTorClient()
    {
        if(!isLoading)
        {
            new Thread()
            {
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            isLoading = true;

                            int totalSecondsPerTorStartup = 4 * 60;
                            int totalTriesPerTorStartup = 5;

                            boolean ok = onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup);

                            if (!ok)
                            {
                                continue;
                            }

                            app_model.getInstance().setPort(onionProxyManager.getIPv4LocalHostSocksPort());
                            initializeProxy();
                            status.isTorInitialized = true;
                            isLoading = false;
                            break;

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            continue;
                        }
                    }
                }

            }.start();
        }
    }

    /*Proxy Initialization*/
    public void initializeProxy()
    {
        PrefsHelper.setPref(keys.proxy_type, constants.proxy_type); //manual proxy settings
        PrefsHelper.setPref(keys.proxy_socks,constants.proxy_socks); //manual proxy settings
        PrefsHelper.setPref(keys.proxy_socks_port, app_model.getInstance().getPort()); //manual proxy settings
        PrefsHelper.setPref(keys.proxy_socks_version,constants.proxy_socks_version); //manual proxy settings
        PrefsHelper.setPref(keys.proxy_socks_remote_dns,constants.proxy_socks_remote_dns); //manual proxy settings
        PrefsHelper.setPref(keys.proxy_cache,constants.proxy_cache);
        PrefsHelper.setPref(keys.proxy_memory,constants.proxy_memory);
        PrefsHelper.setPref(keys.proxy_useragent_override, constants.proxy_useragent_override);
        PrefsHelper.setPref(keys.proxy_donottrackheader_enabled,constants.proxy_donottrackheader_enabled);
        PrefsHelper.setPref(keys.proxy_donottrackheader_value,constants.proxy_donottrackheader_value);
    }



}
