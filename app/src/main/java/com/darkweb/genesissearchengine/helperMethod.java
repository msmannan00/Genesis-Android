package com.darkweb.genesissearchengine;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.darkweb.genesissearchengine.appManager.app_model;
import com.darkweb.genesissearchengine.constants.keys;

public class helperMethod
{
    /*Helper Methods*/
    public static boolean isNetworkAvailable()
    {
        ConnectivityManager cm = (ConnectivityManager)  app_model.getInstance().getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    public static String readHomepageHTML(Context applicationContext)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        return prefs.getString(keys.homepage_html_key,"");
    }

    public static void setHomepageHTML(String html, Context applicationContext)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(keys.homepage_html_key, html);
        edit.commit();

    }

    public static void hideKeyboard()
    {
        View view = app_model.getInstance().getAppInstance().findViewById(android.R.id.content);
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) app_model.getInstance().getAppInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
