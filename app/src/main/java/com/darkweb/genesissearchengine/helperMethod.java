package com.darkweb.genesissearchengine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.List;
import java.util.UUID;

public class helperMethod
{
    public static boolean isNetworkAvailable(Context application_context)
    {
        ConnectivityManager cm = (ConnectivityManager)  application_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    public static boolean readPrefs(String valueKey,Context applicationContext) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(applicationContext);
        return prefs.getBoolean(valueKey,false);
    }

    public static void savePrefs(String valueKey, boolean value,Context applicationContext) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(valueKey, value);
        edit.commit();
    }

    public static void setPlaystoreStatus(Context context) {

        String GooglePlayStorePackageNameOld = "com.google.market";
        String GooglePlayStorePackageNameNew = "com.android.vending";

        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

        for (PackageInfo packageInfo : packages)
        {
            if (packageInfo.packageName.equals(GooglePlayStorePackageNameOld) ||
                    packageInfo.packageName.equals(GooglePlayStorePackageNameNew)) {
                status.isPlayStoreInstalled = true;
                break;
            }
        }

    }

}
