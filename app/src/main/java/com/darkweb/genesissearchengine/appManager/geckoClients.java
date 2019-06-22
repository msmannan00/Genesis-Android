package com.darkweb.genesissearchengine.appManager;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.fabricManager;
import com.darkweb.genesissearchengine.pluginManager.orbot_manager;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class geckoClients
{
    private GeckoSession session1 = null;
    private GeckoRuntime runtime1;
    private final Handler internetErrorHandler = new Handler();

    boolean canSessionGoBack = false;
    boolean isRunning = false;
    boolean isContentLoading = false;
    String  navigatedURL = "";

    private boolean loadingCompeleted = false;
    private String currentURL = "";

    public void loadGeckoURL(String url,GeckoView geckoView)
    {
        boolean init_status = orbot_manager.getInstance().initOrbot(url);
        if (init_status)
        {
            loadingCompeleted = false;
            initialize(geckoView);
            session1.loadUri(url);
            app_model.getInstance().getAppInstance().onRequestTriggered(true,url);
            app_model.getInstance().getAppInstance().onProgressBarUpdateView(4);
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
            Log.i("SHITJ1",""+url);
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
            isRunning = true;
            loadingCompeleted = false;
            currentURL = url;
            fabricManager.getInstance().sendEvent("ONION GECKO_CLIENT URL REQEST : " + url + "--");

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
                            app_model.getInstance().getAppInstance().onInternetErrorView();
                            fabricManager.getInstance().sendEvent("ONION GECKO_CLIENT URL ERROR : " + success + "--" + isContentLoading);
                        }
                        else if(success)
                        {
                            app_model.getInstance().getAppInstance().onDisableInternetError();
                            app_model.getInstance().getAppInstance().onProgressBarUpdateView(0);
                            fabricManager.getInstance().sendEvent("ONION GECKO_CLIENT URL SUCCESS : " + success + "--" + isContentLoading);
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
                app_model.getInstance().getAppInstance().onPageFinished(true);
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
        boolean init_status = orbot_manager.getInstance().initOrbot("");
        if (init_status)
        {
            if(canSessionGoBack)
            {
                app_model.getInstance().getAppInstance().onDisableInternetError();
                session1.goBack();
                fabricManager.getInstance().sendEvent("ONION LOCAL RETURNED : ");
            }
            else if(isRunning)
            {
                fabricManager.getInstance().sendEvent("ONION RETURNED TO GENESIS : ");
                geckoView.releaseSession();
                stopHiddenView(geckoView);
                app_model.getInstance().getAppInstance().onUpdateView(true);
            }
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
    }

    public boolean isGeckoViewRunning()
    {
        return isRunning;
    }

    public void onReloadHiddenView()
    {
        boolean init_status = orbot_manager.getInstance().initOrbot("");
        if (init_status)
        {
            if(session1!=null)
            {
                loadingCompeleted = false;
                session1.stop();
                session1.reload();
            }
        }
    }
}
