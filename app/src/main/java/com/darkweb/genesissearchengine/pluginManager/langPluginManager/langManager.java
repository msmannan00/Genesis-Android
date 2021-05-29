package com.darkweb.genesissearchengine.pluginManager.langPluginManager;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class langManager {

    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private Locale mLanguage;
    private Locale mSystemLocale;

    /*Initializations*/

    public langManager(AppCompatActivity pAppContext, eventObserver.eventListener pEvent, Locale pLanguage, Locale pSystemLocale, String pSettingLanguage, String pSettingRegionLanguage, boolean pThemeApplying) {
        this.mEvent = pEvent;
        this.mLanguage = pLanguage;
        this.mSystemLocale = pSystemLocale;

        onInitLanguage(pAppContext, pSettingLanguage, pSettingRegionLanguage, pThemeApplying);
    }

    private boolean initLocale(Boolean pThemeApplying){
        if(!pThemeApplying){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Locale mSystemLocale = Resources.getSystem().getConfiguration().getLocales().get(0);
                if(mSystemLocale!=mSystemLocale || !mSystemLocale.getLanguage().equals(mLanguage.getLanguage()) ){
                    mEvent.invokeObserver(Collections.singletonList(mSystemLocale), pluginEnums.eLangManager.M_UPDATE_LOCAL);
                }
            } else {
                Locale mSystemLocale = Resources.getSystem().getConfiguration().locale;
                if(mSystemLocale!=mSystemLocale  || !mSystemLocale.getLanguage().equals(mLanguage.getLanguage())){
                    mEvent.invokeObserver(Collections.singletonList(mSystemLocale), pluginEnums.eLangManager.M_UPDATE_LOCAL);
                }
            }
       }
        return false;
    }

    private void onInitLanguage(AppCompatActivity pAppContext, String pSettingLanguage, String pSettingRegionLanguage, Boolean pThemeApplying) {
        if(pSettingLanguage.equals("default")){
            if(!mLanguage.getLanguage().equals(Resources.getSystem().getConfiguration().locale.getLanguage()) || !mLanguage.getCountry().equals(Resources.getSystem().getConfiguration().locale.getCountry()))
            {
                if(mSystemLocale==null){
                    initLocale(pThemeApplying);
                }
                String mSystemLangugage = mSystemLocale.toString();
                if(mSystemLangugage.equals("en_US") || mSystemLangugage.equals("de_DE") || mSystemLangugage.equals("ur_UR") || mSystemLangugage.equals("ur_PK") || mSystemLangugage.equals("ca_ES") || mSystemLangugage.equals("zh_CN") || mSystemLangugage.equals("ch_CZ") || mSystemLangugage.equals("nl_NL") || mSystemLangugage.equals("fr_FR") || mSystemLangugage.equals("el_GR") || mSystemLangugage.equals("hu_HU") || mSystemLangugage.equals("in_ID") || mSystemLangugage.equals("it_IT") || mSystemLangugage.equals("ja_JP") || mSystemLangugage.equals("ko_KR") || mSystemLangugage.equals("pt_PT") || mSystemLangugage.equals("ro_RO") || mSystemLangugage.equals("ru_RU") || mSystemLangugage.equals("th_TH") || mSystemLangugage.equals("tr_TR") || mSystemLangugage.equals("uk_UA") || mSystemLangugage.equals("vi_VN")){
                    if(mSystemLangugage.equals("ur_PK")){
                        mLanguage = new Locale("ur", "Ur");
                    } else if(mSystemLangugage.equals("vi_VN")){
                        mLanguage = new Locale("ch", "Cz");
                    } else {
                        mLanguage = new Locale(mSystemLocale.getLanguage(), mSystemLocale.getCountry());
                    }
                }else {
                    mLanguage = new Locale("en", "Us");
                }
            }else {
                Locale mSystemLocale = Resources.getSystem().getConfiguration().locale;
                mEvent.invokeObserver(Collections.singletonList(mSystemLocale), pluginEnums.eLangManager.M_UPDATE_LOCAL);
                return;
            }
        }else {
            mLanguage = new Locale(pSettingLanguage, pSettingRegionLanguage);
        }

        Locale.setDefault(mLanguage);
        Resources resources = pAppContext.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(mLanguage);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    /*Helper Methods*/

    private void onCreate(AppCompatActivity pActivity, String pSettingLanguage, String pSettingRegionLanguage, Boolean pThemeApplying) {
        onInitLanguage(pActivity, pSettingLanguage, pSettingRegionLanguage, pThemeApplying);
    }

    private void onResume(AppCompatActivity pActivity, String pSettingLanguage, String pSettingRegionLanguage, Boolean pThemeApplying) {
        onInitLanguage(pActivity, pSettingLanguage, pSettingRegionLanguage, pThemeApplying);
    }

    private String getSupportedSystemLanguageInfo(String pSettingLanguage) {
        if(pSettingLanguage.equals("default")){
            Locale mSystemLocale = Resources.getSystem().getConfiguration().locale;
            String mSystemLangugage = mSystemLocale.toString();

            if(mSystemLangugage.equals("cs_CZ") || mSystemLangugage.equals("ur_PK") || mSystemLangugage.equals("en_US") || mSystemLangugage.equals("de_DE") || mSystemLangugage.equals("ca_ES") || mSystemLangugage.equals("zh_CN") || mSystemLangugage.equals("ch_CZ") || mSystemLangugage.equals("nl_NL") || mSystemLangugage.equals("fr_FR") || mSystemLangugage.equals("el_GR") || mSystemLangugage.equals("hu_HU") || mSystemLangugage.equals("in_ID") || mSystemLangugage.equals("it_IT") || mSystemLangugage.equals("ja_JP") || mSystemLangugage.equals("ko_KR") || mSystemLangugage.equals("pt_PT") || mSystemLangugage.equals("ro_RO") || mSystemLangugage.equals("ru_RU") || mSystemLangugage.equals("th_TH") || mSystemLangugage.equals("tr_TR") || mSystemLangugage.equals("uk_UA") || mSystemLangugage.equals("vi_VN")){
                return "Default | " + mSystemLocale.getDisplayName();
            }else {
                return mSystemLocale.getDisplayName() + " | is unsupported";
            }
        }else {
            return mLanguage.getDisplayName();
        }
    }

    /*External Triggers*/

    public Object onTrigger(List<Object> pData, pluginEnums.eLangManager pEventType) {
        if(pEventType.equals(pluginEnums.eLangManager.M_ACTIVITY_CREATED))
        {
            onCreate((AppCompatActivity) pData.get(0), (String)pData.get(1), (String)pData.get(2), (boolean)pData.get(3));
        }
        else if(pEventType.equals(pluginEnums.eLangManager.M_RESUME))
        {
            initLocale((boolean)pData.get(3));
            onResume((AppCompatActivity) pData.get(0), (String)pData.get(1), (String)pData.get(2), (boolean)pData.get(3));
        }
        else if(pEventType.equals(pluginEnums.eLangManager.M_SET_LANGUAGE))
        {
            onInitLanguage((AppCompatActivity) pData.get(0), (String) pData.get(1), (String) pData.get(2), (boolean)pData.get(3));
        }
        else if(pEventType.equals(pluginEnums.eLangManager.M_SUPPORTED_SYSTEM_LANGUAGE_INFO))
        {
            return getSupportedSystemLanguageInfo((String) pData.get(0));
        }
        return null;
    }

}