package com.darkweb.genesissearchengine.helperManager;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.R;

import java.util.Locale;

public class LocaleManager {
    private Locale currentLocale;

    public void onCreate(Activity activity) {
        currentLocale = getSelectedLocale(activity);
        setContextLocale(activity, currentLocale);
    }

    public void onResume(Activity activity) {
    }

    private static Locale getSelectedLocale(Context context) {
        String defaultLanguage = context.getString(R.string.SETTING_DEFAULT_LANGUAGE);
        String selectedLanguage = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.PREF_LANGUAGE),
                defaultLanguage
        );
        String language[] = TextUtils.split(selectedLanguage, "_");

        if (language[0].equals(defaultLanguage))
            return Locale.getDefault();
        else if (language.length == 2)
            return new Locale(language[0], language[1]);
        else
            return new Locale(language[0]);
    }

    private static void setContextLocale(Context context, Locale selectedLocale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.locale = new Locale(status.sSettingLanguage);
        context.getResources().updateConfiguration(
                configuration,
                context.getResources().getDisplayMetrics()
        );
    }
}
