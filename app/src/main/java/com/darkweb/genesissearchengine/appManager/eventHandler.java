package com.darkweb.genesissearchengine.appManager;

import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.fabricManager;
import com.darkweb.genesissearchengine.pluginManager.message_manager;

import java.io.IOException;
import java.net.URL;

public class eventHandler
{
    application_controller appContoller;

    public eventHandler()
    {
        appContoller = app_model.getInstance().getAppInstance();
    }

    public boolean onEditorClicked(TextView v, int actionId, KeyEvent event)
    {

        if (actionId != EditorInfo.IME_ACTION_NEXT)
        {
            return false;
        }

        helperMethod.hideKeyboard();
        String url = helperMethod.completeURL(v.getText().toString());
        try
        {
            URL host = new URL(url);
            boolean isUrlValid = Patterns.WEB_URL.matcher(url).matches();

            if(isUrlValid && host.getHost().replace("www.","").contains("."))
            {
                if(host.getHost().contains(constants.backendUrlHost) || host.getHost().contains(constants.frontEndUrlHost) || host.getHost().contains(constants.frontEndUrlHost_v1))
                {
                    fabricManager.getInstance().sendEvent("EDITOR BASE URL REQUEST : " + url);
                    appContoller.onloadURL(url.replace(constants.frontEndUrlHost_v1,constants.backendUrlHost),false);
                    return true;
                }
                else /*if(host.getHost().contains(constants.allowedHost))*/
                {
                    fabricManager.getInstance().sendEvent("EDITOR ONION URL REQUEST : " + url);
                    app_model.getInstance().getAppInstance().onloadURL(url,true);
                    return true;
                }/*
                else
                {
                    message_manager.getInstance().baseURLError();
                    return true;
                }*/
            }
        }

        catch (IOException e){}

        String editedURL = "https://boogle.store/search?q="+v.getText().toString().replaceAll(" ","+")+"&p_num=1&s_type=all&savesearch=on";
        appContoller.onloadURL(editedURL,false);
        appContoller.onClearSearchBarCursorView();
        fabricManager.getInstance().sendEvent("EDITOR SEARCHED : " + editedURL);

        return true;
    }

    public void onReloadButtonPressed(View view)
    {
        fabricManager.getInstance().sendEvent("RELOAD BUTTON PRESSSED : ");
        appContoller.onReload();
    }

    public void onHomeButtonPressed()
    {
        fabricManager.getInstance().sendEvent("HOME BUTTON PRESSSED : ");
        applicationViewController.getInstance().checkSSLTextColor();
        appContoller.onloadURL(constants.backendUrlSlashed,false);
        helperMethod.hideKeyboard();
        appContoller.onUpdateSearchBarView(constants.frontUrlSlashed);
        appContoller.stopHiddenView();
    }

    public void onFloatingButtonPressed()
    {
        fabricManager.getInstance().sendEvent("FLOATING BUTTON PRESSSED : ");
        message_manager.getInstance().reportURL();
    }

    public void onBackPressed()
    {
        fabricManager.getInstance().sendEvent("BACK BUTTON PRESSSED : ");
        appContoller.onBackPressedView();
    }

}
