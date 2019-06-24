package com.darkweb.genesissearchengine.appManager;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.messages;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.httpManager.serverRequestManager;
import com.darkweb.genesissearchengine.pluginManager.message_manager;
import com.darkweb.genesissearchengine.pluginManager.orbot_manager;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class applicationViewController
{
    /*ViewControllers*/
    private WebView webView;
    private ProgressBar progressBar;
    private EditText searchbar;
    private ConstraintLayout splashScreen;
    private ConstraintLayout requestFailure;
    private FloatingActionButton floatingButton;
    private ImageView loading;
    private ImageView splashlogo;
    private TextView loadingText;

    /*Private Variables*/
    private boolean pageLoadedSuccessfully = true;
    private boolean isSplashLoading = false;
    private Handler updateUIHandler = null;

    /*ProgressBar Delayed Updater*/
    Handler progressBarHandler = null;

    /*Initializations*/
    private static final applicationViewController ourInstance = new applicationViewController();

    public static applicationViewController getInstance()
    {
        return ourInstance;
    }

    private applicationViewController()
    {
    }

    public void initialization(WebView webView1, TextView loadingText, ProgressBar progressBar, EditText searchbar, ConstraintLayout splashScreen, ConstraintLayout requestFailure, FloatingActionButton floatingButton, ImageView loading, ImageView splashlogo)
    {
        this.webView = webView1;
        this.progressBar = progressBar;
        this.searchbar = searchbar;
        this.splashScreen = splashScreen;
        this.requestFailure = requestFailure;
        this.floatingButton = floatingButton;
        this.loading = loading;
        this.splashlogo = splashlogo;
        this.loadingText = loadingText;

        app_model.getInstance().getAppInstance().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        checkSSLTextColor();
        initSplashScreen();
        initLock();
        initViews();
        createUpdateUiHandler();
    }

    public boolean isHiddenView()
    {
        return app_model.getInstance().getAppInstance().isGeckoViewRunning();
    }

    public void initViews()
    {
        floatingButton.setVisibility(View.INVISIBLE);
    }

    public void initLock()
    {
        Drawable img = app_model.getInstance().getAppInstance().getResources().getDrawable( R.drawable.icon_lock);
        searchbar.measure(0, 0);
        img.setBounds( 0, (int)(searchbar.getMeasuredHeight()*0.00), (int)(searchbar.getMeasuredHeight()*1.10), (int)(searchbar.getMeasuredHeight()*0.69) );
        searchbar.setCompoundDrawables( img, null, null, null );
    }

    public void onRequestTriggered(boolean isHiddenWeb,String url)
    {
        onProgressBarUpdate(4);
        helperMethod.hideKeyboard();
        pageLoadedSuccessfully = true;
        onUpdateSearchBar(url);
        checkSSLTextColor();
        onClearSearchBarCursor();

        searchbar.setFocusableInTouchMode(false);
        searchbar.setFocusable(false);
        searchbar.setFocusableInTouchMode(true);
        searchbar.setFocusable(true);
    }

    /*Helper Methods*/
    public void onInternetError()
    {
        disableSplashScreen();
        requestFailure.setVisibility(View.VISIBLE);
        webView.setAlpha(0);
        requestFailure.animate().alpha(1f).setDuration(150);
        pageLoadedSuccessfully = false;
        onClearSearchBarCursor();
        onProgressBarUpdate(0);
        disableFloatingView();
    }

    public void disableSplashScreen()
    {
        if(!isSplashLoading)
        {
            isSplashLoading = true;
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        boolean isFirstInstall = preference_manager.getInstance().getBool(keys.hasOrbotInstalled,true);
                        while (!status.isTorInitialized && isFirstInstall)
                        {
                            startPostTask(messages.UPDATE_LOADING_TEXT);
                            sleep(100);
                        }
                        preference_manager.getInstance().setBool(keys.hasOrbotInstalled,false);
                        startPostTask(messages.DISABLE_SPLASH_SCREEN);
                    }
                    catch (Exception ex)
                    {
                        Log.i("Fizza",ex.getMessage());
                    }
                }
            }.start();
        }
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
                if(msg.what == messages.UPDATE_LOADING_TEXT)
                {
                    loadingText.setText(orbot_manager.getInstance().getLogs());
                }
                if(msg.what == messages.DISABLE_SPLASH_SCREEN)
                {
                    status.isApplicationLoaded = true;
                    splashScreen.animate().alpha(0.0f).setDuration(200).setListener(null).withEndAction((() -> splashScreen.setVisibility(View.GONE)));
                    onWelcomeMessageCheck();
                }
            }
        };
    }

    public boolean onDisableInternetError()
    {
        if(requestFailure.getAlpha()==1)
        {
            requestFailure.animate().alpha(0f).setDuration(150).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));;
            return true;
        }
        else
        {
            return false;
        }
    }

    public void onPageFinished(boolean status)
    {
        helperMethod.hideKeyboard();
        progressBar.setProgress(100);

        if(!status)
        {
            if(pageLoadedSuccessfully)
            {
                requestFailure.animate().alpha(0f).setStartDelay(200).setDuration(200).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));
                onUpdateView(true);
            }
            //onUpdateSearchBar(webView.getUrl());
            disableSplashScreen();
            floatingButton.animate().alpha(0).withEndAction((() -> floatingButton.setVisibility(View.GONE)));;

            app_model.getInstance().getAppInstance().stopHiddenView();
        }
        else
        {
            onUpdateView(false);
            floatingButton.animate().alpha(1).withEndAction((() -> floatingButton.setVisibility(View.VISIBLE)));;
        }
    }

    public void checkSSLTextColor()
    {
        if (searchbar == null)
        {
            return;
        }

        if (searchbar.getText().toString().contains("https://"))
        {
            SpannableString ss = new SpannableString(searchbar.getText());
            ss.setSpan(new ForegroundColorSpan(Color.argb(255, 0, 123, 43)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchbar.setText(ss);
        } else if (searchbar.getText().toString().contains("http://"))
        {
            SpannableString ss = new SpannableString(searchbar.getText());
            ss.setSpan(new ForegroundColorSpan(Color.argb(255, 0, 128, 43)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 4, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchbar.setText(ss);
        } else
        {
            SpannableString ss = new SpannableString(searchbar.getText());
            ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, searchbar.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchbar.setText(ss);
        }
    }

    public void onClearSearchBarCursor()
    {
        searchbar.clearFocus();
    }

    public void disableFloatingView()
    {
        floatingButton.animate().alpha(0).withEndAction((() -> floatingButton.setVisibility(View.GONE)));;

    }

    public void onUpdateSearchBar(String url)
    {
        searchbar.setText(url.replace(constants.backendUrlHost,constants.frontEndUrlHost_v1));
        checkSSLTextColor();
    }

    public void initSplashScreen()
    {
        boolean hasSoftKey = helperMethod.hasSoftKeys(app_model.getInstance().getAppInstance().getWindowManager());
        int height = helperMethod.screenHeight(hasSoftKey);

        splashlogo.getLayoutParams().height = height;
        loading.setAnimation(helperMethod.getRotationAnimation());
        loading.setLayoutParams(helperMethod.getCenterScreenPoint(loading.getLayoutParams()));
    }

    public void onProgressBarUpdate(int progress)
    {
        progressBar.setVisibility(View.VISIBLE);
        if(progress==0)
        {
            progressBar.animate().alpha(0).withEndAction((() -> progressBar.setProgress(progress)));
        }
        else
        {
            progressBar.setProgress(progress);
            progressBar.setAlpha(1);
        }
    }

    public void onBackPressed()
    {
        if(!isHiddenView())
        {
            webView.goBack();
            webView.bringToFront();
            webView.setAlpha(1);
            webView.setVisibility(View.VISIBLE);
            requestFailure.animate().alpha(0f).setDuration(200).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));
            onUpdateSearchBar(webView.getUrl());
        }
        else
        {
            app_model.getInstance().getAppInstance().onHiddenGoBack();
        }
    }

    public void onUpdateView(boolean status)
    {
        if(status)
        {
            floatingButton.animate().alpha(0).withEndAction((() -> floatingButton.setVisibility(View.GONE)));;
            webView.setAlpha(1);
            webView.setVisibility(View.VISIBLE);
            webView.bringToFront();
            onProgressBarUpdate(0);
            onUpdateSearchBar(webView.getUrl());
        }
        else
        {
            webView.animate().alpha(0).setDuration(150).withEndAction((() -> webView.setVisibility(View.GONE)));
        }
    }

    public void onWelcomeMessageCheck()
    {
        if(!preference_manager.getInstance().getBool("FirstTimeLoaded",false))
        {
            message_manager.getInstance().welcomeMessage();
        }
        serverRequestManager.getInstance().versionChecker();
    }

    public void onReload()
    {
        if(!isHiddenView())
        {
            onRequestTriggered(false,webView.getUrl());
            webView.reload();
        }
        else
        {
            app_model.getInstance().getAppInstance().onReloadHiddenView();
        }
    }


}