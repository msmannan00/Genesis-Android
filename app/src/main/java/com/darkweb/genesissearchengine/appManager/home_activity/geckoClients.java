package com.darkweb.genesissearchengine.appManager.home_activity;

import android.os.Handler;
import android.view.View;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
import com.darkweb.genesissearchengine.helperMethod;
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
    private final Handler internetErrorHandler = new Handler();
    private ArrayList<String> urlList = new ArrayList<>();

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

    void loadGeckoURL(String url,GeckoView geckoView,boolean isUrlSavable)
    {
        boolean init_status = orbot_manager.getInstance().initOrbot(url);
        if (init_status)
        {
            this.isUrlSavable = isUrlSavable;
            navigatedURL = "";
            loadingCompeleted = false;
            initialize(geckoView,false);
            session1.loadUri(url);
            app_model.getInstance().getAppInstance().onRequestTriggered(true,url);
            app_model.getInstance().getAppInstance().onProgressBarUpdateView(4);
            isFirstTimeLoad = true;
            if(isUrlSavable)
            {
                urlList.clear();
            }
        }
    }

    void initialize(GeckoView geckoView,boolean release)
    {
        if(urlList.size()<=0 || release)
        {
            session1 = new GeckoSession();
            GeckoRuntime runtime1 = GeckoRuntime.getDefault(app_model.getInstance().getAppContext());
            runtime1.getSettings().setJavaScriptEnabled(status.java_status);
            session1.open(runtime1);
            geckoView.releaseSession();
            geckoView.setSession(session1);
            session1.setProgressDelegate(new progressDelegate());
            session1.setNavigationDelegate(new navigationDelegate());
            geckoView.setVisibility(View.VISIBLE);
            geckoView.setAlpha(1);
        }
    }

    class navigationDelegate implements GeckoSession.NavigationDelegate
    {
        @Override
        public void onLocationChange(GeckoSession session, String url)
        {
            navigatedURL = url;
        }

        @Override
        public GeckoResult<GeckoSession> onNewSession(GeckoSession session, String uri)
        {
            session1.loadUri(uri);
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
                        if(!success && !isContentLoading)
                        {
                            app_model.getInstance().getAppInstance().onPageFinished(true);
                            app_model.getInstance().getAppInstance().onInternetErrorView();
                            fabricManager.getInstance().sendEvent("ONION GECKO_CLIENT URL ERROR : " + success + "--" + isContentLoading);
                        }
                        else if(success)
                        {
                            if((urlList.size() == 0 || !urlList.get(urlList.size() - 1).equals(navigatedURL)) && !wasBackPressed)
                            {
                                urlList.add(navigatedURL);
                            }

                            urlRequestCount++;
                                if(urlRequestCount==5)
                            {
                                if(!isAppRated)
                                {
                                    isAppRated = true;
                                    message_manager.getInstance().rateApp();
                                }
                            }
                            else if(isAppRated)
                            {
                                if(isFirstTimeLoad)
                                {
                                    app_model.getInstance().getAppInstance().onShowAd(enums.adID.hidden);
                                }
                                else
                                {
                                    app_model.getInstance().getAppInstance().onShowAd(enums.adID.internal);
                                }
                            }

                            if(isUrlSavable && !urlList.get(urlList.size()-1).equals("navigatedURL"))
                            {
                                app_model.getInstance().addHistory(navigatedURL);
                            }
                            isUrlSavable = true;
                            app_model.getInstance().getAppInstance().onDisableInternetError();
                            app_model.getInstance().getAppInstance().onProgressBarUpdateView(0);
                            fabricManager.getInstance().sendEvent("ONION GECKO_CLIENT URL SUCCESS : " + success + "--" + isContentLoading);
                            app_model.getInstance().getAppInstance().onPageFinished(true);
                        }
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
            if(urlList.size()>1)
            {
                 urlList.remove(urlList.size() - 1);
                 wasBackPressed = true;
                 session1.stop();
                 session1.loadUri(urlList.get(urlList.size() - 1));
                 app_model.getInstance().getAppInstance().onUpdateSearchBarView(urlList.get(urlList.size()-1));
            }
            else if(isRunning)
            {
                 if(navigatedURL.equals(constants.backendGoogle) && status.search_status.equals(strings.google_text) || navigatedURL.equals(constants.backendBing) && status.search_status.equals(strings.bing_text))
                 {
                     helperMethod.onMinimizeApp();
                 }
                 else
                 {
                     if(urlList.size()>0)
                     {
                         urlList.remove(urlList.size()-1);
                     }
                     stopHiddenView(geckoView,false);
                     app_model.getInstance().getAppInstance().onUpdateView(true);
                     app_model.getInstance().getAppInstance().onDisableInternetError();
                 }
            }
    }

    void stopHiddenView(GeckoView geckoView,boolean releaseView)
    {
        if(session1!=null)
        {
            isRunning = false;
            loadingCompeleted = false;

            session1.stop();
            if(!releaseView)
            {
                session1.close();
            }
        }
    }

    int getHiddenQueueLength()
    {
        return urlList.size();
    }

    void setRootEngine(String url)
    {
        if(urlList.size()>1)
        {
            urlList.set(0,url);
        }
    }

    boolean isGeckoViewRunning()
    {
        return isRunning;
    }

    void onReloadHiddenView()
    {
        if(urlList.size()>0)
        {
            wasBackPressed = true;
            session1.stop();
            session1.loadUri(urlList.get(urlList.size()-1));
        }
    }
}
