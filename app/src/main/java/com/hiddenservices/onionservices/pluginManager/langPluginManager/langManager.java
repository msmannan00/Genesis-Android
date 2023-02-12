package com.hiddenservices.onionservices.pluginManager.langPluginManager;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;

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

        onInitLanguage(pAppContext, pSettingLanguage, pSettingRegionLanguage);
    }

    private boolean initLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale mSystemLocaleTemp = Resources.getSystem().getConfiguration().getLocales().get(0);
            mEvent.invokeObserver(Collections.singletonList(mSystemLocaleTemp), pluginEnums.eLangManager.M_UPDATE_LOCAL);
            mSystemLocale = mSystemLocaleTemp;
        } else {
            Locale mSystemLocaleTemp = Resources.getSystem().getConfiguration().locale;
            mEvent.invokeObserver(Collections.singletonList(mSystemLocaleTemp), pluginEnums.eLangManager.M_UPDATE_LOCAL);
            mSystemLocale = mSystemLocaleTemp;
        }
        return false;
    }

    private void onInitLanguage(AppCompatActivity pAppContext, String pSettingLanguage, String pSettingRegionLanguage) {
        if (pSettingLanguage.equals("default")) {
            Locale mSystemLocaleTemp;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mSystemLocaleTemp = Resources.getSystem().getConfiguration().getLocales().get(0);
            } else {
                mSystemLocaleTemp = Resources.getSystem().getConfiguration().locale;
            }

            if (!mLanguage.toString().equals(mSystemLocaleTemp.toString())) {
                if (mSystemLocale == null) {
                    initLocale();
                }
                mSystemLocale = mSystemLocaleTemp;
                String mSystemLangugage = mSystemLocale.toString();
                if (mSystemLangugage.equals("cs_CZ") || mSystemLangugage.equals("en_US") || mSystemLangugage.equals("de_DE") || mSystemLangugage.equals("ur_UR") || mSystemLangugage.equals("ur_PK") || mSystemLangugage.equals("ca_ES") || mSystemLangugage.equals("zh_CN") || mSystemLangugage.equals("ch_CZ") || mSystemLangugage.equals("nl_NL") || mSystemLangugage.equals("fr_FR") || mSystemLangugage.equals("el_GR") || mSystemLangugage.equals("hu_HU") || mSystemLangugage.equals("in_ID") || mSystemLangugage.equals("it_IT") || mSystemLangugage.equals("ja_JP") || mSystemLangugage.equals("ko_KR") || mSystemLangugage.equals("pt_PT") || mSystemLangugage.equals("ro_RO") || mSystemLangugage.equals("ru_RU") || mSystemLangugage.equals("th_TH") || mSystemLangugage.equals("ar_AR") || mSystemLangugage.equals("tr_TR") || mSystemLangugage.equals("uk_UA") || mSystemLangugage.equals("vi_VN")) {
                    if (mSystemLangugage.equals("ur_PK")) {
                        mLanguage = new Locale("ur", "Ur");
                    } else if (mSystemLangugage.equals("vi_VN") || mSystemLangugage.equals("cs_CZ")) {
                        mLanguage = new Locale("ch", "Cz");
                    } else {
                        mLanguage = new Locale(mSystemLocale.getLanguage(), mSystemLocale.getCountry());
                    }
                } else {
                    mLanguage = new Locale("en", "Us");
                }
                helperMethod.updateResources(pAppContext, status.mSystemLocale.getLanguage());
            } else {
                helperMethod.updateResources(pAppContext, status.mSystemLocale.getLanguage());
                return;
            }

        } else {
            mLanguage = new Locale(pSettingLanguage, pSettingRegionLanguage);
        }


        status.mSystemLocale = mLanguage;
        Locale.setDefault(mLanguage);
        Resources resources = pAppContext.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(mLanguage);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    /*Helper Methods*/

    private void onCreate(AppCompatActivity pActivity, String pSettingLanguage, String pSettingRegionLanguage) {
        onInitLanguage(pActivity, pSettingLanguage, pSettingRegionLanguage);
    }

    private void onResume(AppCompatActivity pActivity, String pSettingLanguage, String pSettingRegionLanguage) {
        onInitLanguage(pActivity, pSettingLanguage, pSettingRegionLanguage);
    }

    private String getSupportedSystemLanguageInfo(String pSettingLanguage) {
        if (pSettingLanguage.equals("default")) {
            Locale mSystemLocale = Resources.getSystem().getConfiguration().locale;
            String mSystemLangugage = mSystemLocale.toString();

            if (mSystemLangugage.equals("cs_CZ") || mSystemLangugage.equals("ar_AR") || mSystemLangugage.equals("ur_PK") || mSystemLangugage.equals("en_GB") || mSystemLangugage.equals("en_US") || mSystemLangugage.equals("de_DE") || mSystemLangugage.equals("ca_ES") || mSystemLangugage.equals("zh_CN") || mSystemLangugage.equals("ch_CZ") || mSystemLangugage.equals("nl_NL") || mSystemLangugage.equals("fr_FR") || mSystemLangugage.equals("el_GR") || mSystemLangugage.equals("hu_HU") || mSystemLangugage.equals("in_ID") || mSystemLangugage.equals("it_IT") || mSystemLangugage.equals("ja_JP") || mSystemLangugage.equals("ko_KR") || mSystemLangugage.equals("pt_PT") || mSystemLangugage.equals("ro_RO") || mSystemLangugage.equals("ru_RU") || mSystemLangugage.equals("th_TH") || mSystemLangugage.equals("tr_TR") || mSystemLangugage.equals("uk_UA") || mSystemLangugage.equals("vi_VN")) {
                return "Default | " + mSystemLocale.getDisplayName();
            } else {
                return mSystemLocale.getDisplayName() + " | is unsupported";
            }
        } else {
            return mLanguage.getDisplayName();
        }
    }

    /*External Triggers*/

    public Object onTrigger(List<Object> pData, pluginEnums.eLangManager pEventType) {
        if (pEventType.equals(pluginEnums.eLangManager.M_ACTIVITY_CREATED)) {
            onCreate((AppCompatActivity) pData.get(0), (String) pData.get(1), (String) pData.get(2));
        } else if (pEventType.equals(pluginEnums.eLangManager.M_RESUME)) {
            initLocale();
            onResume((AppCompatActivity) pData.get(0), (String) pData.get(1), (String) pData.get(2));
        } else if (pEventType.equals(pluginEnums.eLangManager.M_SET_LANGUAGE)) {
            onInitLanguage((AppCompatActivity) pData.get(0), (String) pData.get(1), (String) pData.get(2));
        } else if (pEventType.equals(pluginEnums.eLangManager.M_SUPPORTED_SYSTEM_LANGUAGE_INFO)) {
            return getSupportedSystemLanguageInfo((String) pData.get(0));
        }
        return null;
    }

}