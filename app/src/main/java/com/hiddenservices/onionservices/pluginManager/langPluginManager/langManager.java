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

    public langManager(AppCompatActivity pAppContext, eventObserver.eventListener pEvent, Locale pLanguage, Locale pSystemLocale, String pSettingLanguage, String pSettingRegionLanguage) {
        this.mEvent = pEvent;
        this.mLanguage = pLanguage;
        this.mSystemLocale = pSystemLocale;

        onInitLanguage(pAppContext, pSettingLanguage, pSettingRegionLanguage);
    }

    private void initLocale() {
        Locale mSystemLocaleTemp;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mSystemLocaleTemp = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            mSystemLocaleTemp = Resources.getSystem().getConfiguration().locale;
        }
        mEvent.invokeObserver(Collections.singletonList(mSystemLocaleTemp), pluginEnums.eLangManager.M_UPDATE_LOCAL);
        mSystemLocale = mSystemLocaleTemp;
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
                String mSystemLanguage = mSystemLocale.toString();
                if (mSystemLanguage.equals("cs_CZ") || mSystemLanguage.equals("en_US") || mSystemLanguage.equals("de_DE") || mSystemLanguage.equals("ur_UR") || mSystemLanguage.equals("ur_PK") || mSystemLanguage.equals("ca_ES") || mSystemLanguage.equals("zh_CN") || mSystemLanguage.equals("ch_CZ") || mSystemLanguage.equals("nl_NL") || mSystemLanguage.equals("fr_FR") || mSystemLanguage.equals("el_GR") || mSystemLanguage.equals("hu_HU") || mSystemLanguage.equals("in_ID") || mSystemLanguage.equals("it_IT") || mSystemLanguage.equals("ja_JP") || mSystemLanguage.equals("ko_KR") || mSystemLanguage.equals("pt_PT") || mSystemLanguage.equals("ro_RO") || mSystemLanguage.equals("ru_RU") || mSystemLanguage.equals("th_TH") || mSystemLanguage.equals("ar_AR") || mSystemLanguage.equals("tr_TR") || mSystemLanguage.equals("uk_UA") || mSystemLanguage.equals("vi_VN")) {
                    if (mSystemLanguage.equals("ur_PK")) {
                        mLanguage = new Locale("ur", "Ur");
                    } else if (mSystemLanguage.equals("vi_VN") || mSystemLanguage.equals("cs_CZ")) {
                        mLanguage = new Locale("ch", "Cz");
                    } else {
                        mLanguage = new Locale(mSystemLocale.getCountry(), mSystemLocale.getCountry());
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
            String mSystemLanguage = mSystemLocale.toString();

            if (mSystemLanguage.equals("cs_CZ") || mSystemLanguage.equals("ar_AR") || mSystemLanguage.equals("ur_PK") || mSystemLanguage.equals("en_GB") || mSystemLanguage.equals("en_US") || mSystemLanguage.equals("de_DE") || mSystemLanguage.equals("ca_ES") || mSystemLanguage.equals("zh_CN") || mSystemLanguage.equals("ch_CZ") || mSystemLanguage.equals("nl_NL") || mSystemLanguage.equals("fr_FR") || mSystemLanguage.equals("el_GR") || mSystemLanguage.equals("hu_HU") || mSystemLanguage.equals("in_ID") || mSystemLanguage.equals("it_IT") || mSystemLanguage.equals("ja_JP") || mSystemLanguage.equals("ko_KR") || mSystemLanguage.equals("pt_PT") || mSystemLanguage.equals("ro_RO") || mSystemLanguage.equals("ru_RU") || mSystemLanguage.equals("th_TH") || mSystemLanguage.equals("tr_TR") || mSystemLanguage.equals("uk_UA") || mSystemLanguage.equals("vi_VN")) {
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