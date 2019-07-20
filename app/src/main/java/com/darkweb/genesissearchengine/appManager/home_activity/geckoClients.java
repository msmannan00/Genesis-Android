package com.darkweb.genesissearchengine.appManager.home_activity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
import com.darkweb.genesissearchengine.pluginManager.fabricManager;
import com.darkweb.genesissearchengine.pluginManager.message_manager;
import com.darkweb.genesissearchengine.pluginManager.orbot_manager;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.util.ArrayList;

class geckoClients
{
    private GeckoSession session1 = null;
    private GeckoRuntime runtime1 = null;
    private final Handler internetErrorHandler = new Handler();

    private boolean isRunning = false;
    private boolean isContentLoading = false;
    private String  navigatedURL = "";
    private boolean isFirstTimeLoad = true;

    private boolean loadingCompeleted = false;
    private boolean wasBackPressed = false;
    private boolean isUrlSavable = true;

    private int urlRequestCount = 0;
    private boolean isAppRated = false;

    geckoClients()
    {
        isAppRated = preference_manager.getInstance().getBool(keys.isAppRated,false);
    }

    void loadGeckoURL(String url,GeckoView geckoView,boolean isUrlSavable,boolean reinit)
    {
        boolean init_status = orbot_manager.getInstance().initOrbot(url);
        if (init_status)
        {
            if(reinit)
            {
                initialize(geckoView);
            }

            this.isUrlSavable = isUrlSavable;
            navigatedURL = "";
            loadingCompeleted = false;
            session1.loadUri(url);
            app_model.getInstance().getAppInstance().onRequestTriggered(true,url);
            app_model.getInstance().getAppInstance().onProgressBarUpdateView(4);
            isFirstTimeLoad = true;
            wasBackPressed = false;
            isContentLoading = false;
            isRunning = false;
        }
    }

    void initialize(GeckoView geckoView)
    {
        session1 = new GeckoSession();
        runtime1 = GeckoRuntime.getDefault(app_model.getInstance().getAppContext());
        runtime1.getSettings().setJavaScriptEnabled(status.java_status);
        session1.open(runtime1);
        geckoView.releaseSession();
        geckoView.setSession(session1);
        session1.setProgressDelegate(new progressDelegate());
        session1.setNavigationDelegate(new navigationDelegate());
        geckoView.setVisibility(View.VISIBLE);
        geckoView.setAlpha(1);
    }

    public void initializeDownloadManager()
    {
        //DownloadsFeature downloadsFeature = new DownloadsFeature(app_model.getInstance().getAppContext(),null,null,null,session1);
    }

    class navigationDelegate implements GeckoSession.NavigationDelegate
    {
        @Override
        public void onLocationChange(GeckoSession session, String url)
        {
            navigatedURL = url;
            if(isUrlSavable && app_model.getInstance().getNavigation().size()>0 && !url.equals("about:blank"))
            {
                app_model.getInstance().addHistory(navigatedURL);
                app_model.getInstance().addNavigation(navigatedURL,enums.navigationType.onion);
            }
        }

        @Override
        public GeckoResult<GeckoSession> onNewSession(GeckoSession session, String url)
        {
            Log.i("FUCK2",url);
            session1.loadUri(url);
            return null;
        }

    }

    class progressDelegate implements GeckoSession.ProgressDelegate
    {
        @Override
        public void onPageStart(GeckoSession session, String url)
        {
            wasBackPressed = false;
            isRunning = true;
            loadingCompeleted = false;
            fabricManager.getInstance().sendEvent("ONION GECKO_CLIENT URL REQEST : " + url + "--");

            app_model.getInstance().getAppInstance().onUpdateSearchBarView(url);
            isContentLoading = !navigatedURL.equals(url);

            navigatedURL = url;
        }

        @Override
        public void onPageStop(GeckoSession session, boolean success)
        {
            internetErrorHandler.removeCallbacksAndMessages(null);

            internetErrorHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if(loadingCompeleted)
                    {
                        if(isFirstTimeLoad)
                        {
                            app_model.getInstance().getAppInstance().hideSplashScreen();
                        }
                        if(!success && !isContentLoading && !wasBackPressed)
                        {
                            app_model.getInstance().getAppInstance().onPageFinished(true);
                            app_model.getInstance().getAppInstance().onInternetErrorView();
                            fabricManager.getInstance().sendEvent("ONION GECKO_CLIENT URL ERROR : " + success + "--" + isContentLoading);
                        }
                        else if(success)
                        {
                            urlRequestCount++;
                            if(urlRequestCount==5)
                            {
                                if(!isAppRated)
                                {
                                    isAppRated = true;
                                    message_manager.getInstance().rateApp();
                                }
                            }
                            else if(isAppRated || 1==1)
                            {
                                if(isFirstTimeLoad && navigatedURL.contains(".onion"))
                                {
                                    app_model.getInstance().getAppInstance().onShowAd(enums.adID.hidden_onion_start);
                                }
                                else if(!isFirstTimeLoad && navigatedURL.contains(".onion"))
                                {
                                    app_model.getInstance().getAppInstance().onShowAd(enums.adID.hidden_onion);
                                }
                                else if(!isFirstTimeLoad && !navigatedURL.contains(".onion"))
                                {
                                    app_model.getInstance().getAppInstance().onShowAd(enums.adID.hidden_base);
                                }
                            }

                            app_model.getInstance().getAppInstance().onDisableInternetError();
                            app_model.getInstance().getAppInstance().onProgressBarUpdateView(0);
                            fabricManager.getInstance().sendEvent("ONION GECKO_CLIENT URL SUCCESS : " + success + "--" + isContentLoading);
                            app_model.getInstance().getAppInstance().onPageFinished(true);
                        }

                        isUrlSavable = true;
                        isFirstTimeLoad = false;

                    }
                }
            }, 500);
        }



        @Override
        public void onProgressChange(GeckoSession session, int progress)
        {
            if(progress>=100)
            {
                loadingCompeleted = true;
                isContentLoading = false;
            }
            else if(progress>=5)
            {
                app_model.getInstance().getAppInstance().onProgressBarUpdateView(progress);
            }
            else
            {
                app_model.getInstance().getAppInstance().onProgressBarUpdateView(4);
            }
        }

        @Override
        public void onSecurityChange(GeckoSession session, SecurityInformation securityInfo)
        {
        }

    }

    void onHiddenGoBack(GeckoView geckoView)
    {
        isRunning = false;
        loadingCompeleted = false;
        isUrlSavable = false;

        wasBackPressed = true;
        session1.stop();

        if(app_model.getInstance().getAppInstance().isInternetErrorOpened())
        {
            initialize(geckoView);
        }

        session1.loadUri(app_model.getInstance().getNavigation().get(app_model.getInstance().getNavigation().size()-1).getURL());
    }

    void stopHiddenView(GeckoView geckoView,boolean releaseView,boolean backPressed)
    {
        if(session1!=null)
        {
            isRunning = false;
            loadingCompeleted = false;
            wasBackPressed = backPressed;

            session1.stop();
            if(!releaseView)
            {
                //session1.close();
            }
        }
    }

    public void releaseSession(GeckoView geckoView)
    {
        geckoView.releaseSession();
    }

    void setRootEngine(String url)
    {
    }

    boolean isGeckoViewRunning()
    {
        return isRunning;
    }

    void onReloadHiddenView()
    {
        if(app_model.getInstance().getNavigation().get(app_model.getInstance().getNavigation().size()-1).type().equals(enums.navigationType.onion))
        {
            isRunning = false;
            loadingCompeleted = false;
            isUrlSavable = false;

            wasBackPressed = true;
            session1.stop();

            session1.loadUri(app_model.getInstance().getNavigation().get(app_model.getInstance().getNavigation().size()-1).getURL());
        }
    }
}
