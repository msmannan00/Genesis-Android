package com.darkweb.genesissearchengine.pluginManager;

import android.app.Activity;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.helperManager.LocaleManager;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import java.util.Locale;

class langManager
{
    /*Private Variables*/

    private AppCompatActivity mAppContext;
    private eventObserver.eventListener mEvent;
    private Locale language;
    private LocaleManager mLocaleManguage;

    /*Initializations*/

    langManager(AppCompatActivity mAppContext, eventObserver.eventListener mEvent){
        this.mAppContext = mAppContext;
        this.mEvent = mEvent;
        initialize();
    }

    private void initialize(){
        mLocaleManguage = new LocaleManager();
    }

    void setDefaultLanguage(Locale language){
        this.language = language;
        onSaveLanguage();
    }

    private void onSaveLanguage(){
        Configuration configuration = mAppContext.getResources().getConfiguration();
        configuration.locale = this.language;
        mAppContext.getResources().updateConfiguration(
                configuration,
                mAppContext.getResources().getDisplayMetrics()
        );
    }

    /*External Locale Requrest Manager*/

    public void onCreate(Activity activity) {
        mLocaleManguage.onCreate(activity);
    }

    public void onResume(Activity activity) {
        mLocaleManguage.onResume(activity);
    }

}
