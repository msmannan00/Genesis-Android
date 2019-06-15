package com.darkweb.genesissearchengine.pluginManager;

import com.darkweb.genesissearchengine.appManager.app_model;
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
    public boolean initOrbot()
    {
        if(!status.isTorInitialized)
        {
            message_manager.getInstance().startingOrbotInfo();
            initializeTorClient();
            return false;
        }
        else
        {
            return true;
        }
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
                            String fileStorageLocation = strings.torfolder;

                            if(onionProxyManager!=null && onionProxyManager.isRunning())
                            {
                                break;
                            }
                            onionProxyManager = new AndroidOnionProxyManager(app_model.getInstance().getAppContext(), fileStorageLocation);

                            int totalSecondsPerTorStartup = 4 * 60;
                            int totalTriesPerTorStartup = 5;
                            boolean ok = onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup);
                            if (!ok) {
                                return;
                            } else {
                                if (onionProxyManager.isRunning()) {
                                }
                            }

                            while (!onionProxyManager.isRunning()) {
                                sleep(1000);
                            }
                            if (onionProxyManager.isRunning()) {
                                app_model.getInstance().setPort(onionProxyManager.getIPv4LocalHostSocksPort());
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
    }

    /*Proxy Initialization*/
    public void initializeProxy()
    {
        PrefsHelper.setPref(keys.proxy_type, constants.proxy_type); //manual proxy settings
        PrefsHelper.setPref(keys.proxy_socks,constants.proxy_socks); //manual proxy settings
        PrefsHelper.setPref(keys.proxy_socks_port, app_model.getInstance().getPort()); //manual proxy settings
        PrefsHelper.setPref(keys.proxy_socks_version,constants.proxy_socks_version); //manual proxy settings
        PrefsHelper.setPref(keys.proxy_socks_remote_dns,constants.proxy_socks_remote_dns); //manual proxy settings
        //PrefsHelper.setPref(keys.proxy_cache,constants.proxy_cache);
        //PrefsHelper.setPref(keys.proxy_memory,constants.proxy_memory);
        PrefsHelper.setPref(keys.proxy_useragent_override, constants.proxy_useragent_override);
        PrefsHelper.setPref(keys.proxy_donottrackheader_enabled,constants.proxy_donottrackheader_enabled);
        PrefsHelper.setPref(keys.proxy_donottrackheader_value,constants.proxy_donottrackheader_value);
    }



}
