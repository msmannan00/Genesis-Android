package com.darkweb.genesissearchengine.appManager;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.darkweb.genesissearchengine.pluginManager.admanager;
import com.darkweb.genesissearchengine.pluginManager.fabricManager;
import com.darkweb.genesissearchengine.pluginManager.orbot_manager;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.util.ArrayList;

public class geckoClients
{
    private GeckoSession session1 = null;
    private GeckoRuntime runtime1;
    private final Handler internetErrorHandler = new Handler();

    boolean canSessionGoBack = false;
    boolean isRunning = false;
    boolean isContentLoading = false;
    String  navigatedURL = "";
    boolean isFirstTimeLoad = true;
    ArrayList<String> urlList = new ArrayList<>();

    private boolean loadingCompeleted = false;
    private String currentURL = "";
    private boolean wasBackPressed = false;

    public void loadGeckoURL(String url,GeckoView geckoView)
    {
        boolean init_status = orbot_manager.getInstance().initOrbot(url);
        if (init_status)
        {
            urlList.clear();
            navigatedURL = "";
            loadingCompeleted = false;
            initialize(geckoView);
            session1.loadUri(url);
            app_model.getInstance().getAppInstance().onRequestTriggered(true,url);
            app_model.getInstance().getAppInstance().onProgressBarUpdateView(4);
            isFirstTimeLoad = true;
        }
    }

    public void initialize(GeckoView geckoView)
    {
        session1 = new GeckoSession();
        runtime1 = GeckoRuntime.getDefault(app_model.getInstance().getAppContext());
        session1.open(runtime1);
        geckoView.releaseSession();
        geckoView.setSession(session1);
        session1.setProgressDelegate(new progressDelegate());
        session1.setNavigationDelegate(new navigationDelegate());
        geckoView.setVisibility(View.VISIBLE);
        geckoView.setAlpha(1);
    }

    class navigationDelegate implements GeckoSession.NavigationDelegate
    {
        @Override
        public void onLocationChange(GeckoSession session, String url)
        {
            navigatedURL = url;
        }

        @Override
        public void onCanGoBack(GeckoSession session, boolean canGoBack)
        {
            canSessionGoBack = canGoBack;
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
            currentURL = url;
            fabricManager.getInstance().sendEvent("ONION GECKO_CLIENT URL REQEST : " + url + "--");

            app_model.getInstance().getAppInstance().onUpdateSearchBarView(url);
            if(navigatedURL.equals(currentURL))
            {
                isContentLoading = false;
            }
            else
            {
                isContentLoading = true;
            }

            Log.i("SHITJ2",""+navigatedURL + "--" + currentURL + "--" + isContentLoading);
            navigatedURL = url;
        }

        @Override
        public void onPageStop(GeckoSession session, boolean success)
        {
            internetErrorHandler.removeCallbacksAndMessages(null);

            internetErrorHandler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    if(loadingCompeleted)
                    {
                        if(!success && !isContentLoading)
                        {
                            app_model.getInstance().getAppInstance().onPageFinished(true);
                            app_model.getInstance().getAppInstance().onInternetErrorView();
                            fabricManager.getInstance().sendEvent("ONION GECKO_CLIENT URL ERROR : " + success + "--" + isContentLoading);
                        }
                        else if(success)
                        {
                            if(isFirstTimeLoad)
                            {
                                admanager.getInstance().showAd(true);
                                isFirstTimeLoad = false;
                            }

                            if((urlList.size()==0 || urlList.size()>0 && !urlList.get(urlList.size()-1).equals(navigatedURL))&&!wasBackPressed)
                            {
                                urlList.add(navigatedURL);
                            }

                            if(urlList.size()>0)
                            {
                                admanager.getInstance().showAd(false);
                            }
                            app_model.getInstance().getAppInstance().onDisableInternetError();
                            app_model.getInstance().getAppInstance().onProgressBarUpdateView(0);
                            fabricManager.getInstance().sendEvent("ONION GECKO_CLIENT URL SUCCESS : " + success + "--" + isContentLoading);
                            app_model.getInstance().getAppInstance().onPageFinished(true);
                        }
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

            if(progress>=5)
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

    public void onHiddenGoBack(GeckoView geckoView)
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
                 if(urlList.size()>0)
                 {
                     urlList.remove(urlList.size()-1);
                 }
                 stopHiddenView(geckoView);
                 app_model.getInstance().getAppInstance().onUpdateView(true);
                 app_model.getInstance().getAppInstance().onDisableInternetError();
            }


    }

    public void stopHiddenView(GeckoView geckoView)
    {
        if(session1!=null)
        {
            isRunning = false;
            loadingCompeleted = false;

            session1.stop();
            session1.close();
        }

        geckoView.releaseSession();

    }

    public boolean isGeckoViewRunning()
    {
        return isRunning;
    }

    public void onReloadHiddenView()
    {
        wasBackPressed = true;
        session1.stop();
        session1.loadUri(navigatedURL);
    }
}
