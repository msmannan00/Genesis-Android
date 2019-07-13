package com.darkweb.genesissearchengine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import androidx.core.app.ShareCompat;
import com.darkweb.genesissearchengine.appManager.list_activity.list_controller;
import com.darkweb.genesissearchengine.appManager.main_activity.app_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
import com.example.myapplication.BuildConfig;

import java.net.MalformedURLException;
import java.net.URL;

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


    public static String completeURL(String url)
    {
        if(!url.startsWith("www.")&& !url.startsWith("http://")&& !url.startsWith("https://")){
            url = "www."+url;
        }
        if(!url.startsWith("http://")&&!url.startsWith("https://")){
            url = "http://"+url;
        }
        return url;
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

    public static int screenHeight(boolean hasSoftKeys)
    {
        if(!hasSoftKeys)
        {
            return (int)(Resources.getSystem().getDisplayMetrics().heightPixels)-(helperMethod.getNavigationBarHeight());
        }
        else
        {
            return (int)(Resources.getSystem().getDisplayMetrics().heightPixels);
        }
    }

    public static int screenWidth()
    {
        return (int)(Resources.getSystem().getDisplayMetrics().widthPixels);
    }

    public static int getNavigationBarHeight() {
        Resources resources = app_model.getInstance().getAppContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static RotateAnimation getRotationAnimation()
    {
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        return rotate;
    }

    public static ViewGroup.MarginLayoutParams getCenterScreenPoint(ViewGroup.LayoutParams itemLayoutParams)
    {
        double heightloader = Resources.getSystem().getDisplayMetrics().heightPixels*0.78;
        ViewGroup.MarginLayoutParams params_loading = (ViewGroup.MarginLayoutParams) itemLayoutParams;
        params_loading.topMargin = (int)(heightloader);

        return params_loading;
    }

    public static boolean hasSoftKeys(WindowManager windowManager)
    {
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    public static boolean isBuildValid ()
    {
        if(BuildConfig.FLAVOR.equals("aarch64")&& Build.SUPPORTED_ABIS[0].equals("arm64-v8a") || BuildConfig.FLAVOR.equals("arm")&&Build.SUPPORTED_ABIS[0].equals("armeabi-v7a") || BuildConfig.FLAVOR.equals("x86")&&Build.SUPPORTED_ABIS[0].equals("x86") || BuildConfig.FLAVOR.equals("x86_64")&&Build.SUPPORTED_ABIS[0].equals("x86_64"))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    public static void rateApp()
    {
        preference_manager.getInstance().setBool(keys.isAppRated,true);
        app_model.getInstance().getAppInstance().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.darkweb.genesissearchengine")));
    }

    public static void shareApp()
    {
        ShareCompat.IntentBuilder.from(app_model.getInstance().getAppInstance())
                .setType("text/plain")
                .setChooserTitle("Genesis | Onion Search")
                .setText("http://play.google.com/store/apps/details?id=" + app_model.getInstance().getAppInstance().getPackageName())
                .startChooser();
    }

    public static String getHost(String link)
    {
        URL url = null;
        try
        {
            url = new URL(link);
            String host = url.getHost();
            return host;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return "";
        }

    }

    public static void openActivity( Class<?> cls,int type)
    {
        Intent myIntent = new Intent(app_model.getInstance().getAppInstance(), cls);
        myIntent.putExtra(keys.list_type, type);
        app_model.getInstance().getAppInstance().startActivity(myIntent);
    }

}
