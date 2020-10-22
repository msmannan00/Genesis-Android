package com.darkweb.genesissearchengine.appManager.settingManager.settingHomePage;

import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;

import java.util.Arrays;
import java.util.Collections;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

class settingModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    private String mSearchStatus = strings.GENERIC_EMPTY_STR;
    private int mCookieStatus = ACCEPT_FIRST_PARTY;
    private int mNotificationStatus = 0;
    private boolean mJavaStatus = false;
    private boolean mHistoryStatus = true;
    private boolean mFontAdjustable = true;
    private float mFontSize = 1;
    private int notificationStatusGlobal;

    /*Initializations*/

    settingModel(eventObserver.eventListener mEvent){
        init_status();
        this.mEvent = mEvent;
    }

    /*Helper Methods*/

    private void init_status()
    {
        mSearchStatus = status.sSettingSearchStatus;
        mHistoryStatus = status.sSettingHistoryStatus;
        mJavaStatus = status.sSettingJavaStatus;
        mCookieStatus = status.sSettingCookieStatus;
    }

    void initNotification(int notificationStatus){
        mNotificationStatus = notificationStatus;
        notificationStatusGlobal = notificationStatus;
    }

    /*Changed Status*/

    String getSearchStatus(){
        return mSearchStatus;
    }

    void setCookieStatus(int cookie_status, boolean isActivityLoading){
        this.mCookieStatus = cookie_status;
        if(!isActivityLoading){
            onSaveSettings();
        }
    }

    void setSearchStatus(String search_status, boolean isActivityLoading){
        this.mSearchStatus = search_status;
        if(!isActivityLoading){
            onSaveSettings();
        }
    }

    void setFontSize(float font_size, boolean isActivityLoading){
        this.mFontSize = font_size;
        if(!isActivityLoading){
            onSaveSettings();
        }
    }

    void setAdjustableStatus(boolean font_status, boolean isActivityLoading){
        this.mFontAdjustable = font_status;
        if(!isActivityLoading){
            onSaveSettings();
        }
    }


    void setJavaStatus(boolean java_status, boolean isActivityLoading){
        this.mJavaStatus = java_status;
        if(!isActivityLoading){
            onSaveSettings();
        }
    }

    void setmNotificationStatus(int notification_status, boolean isActivityLoading){
        this.mNotificationStatus = notification_status;
        if(!isActivityLoading){
            onSaveSettings();
        }
    }

    void setHistoryStatus(boolean history_status, boolean isActivityLoading){
        this.mHistoryStatus = history_status;
        if(!isActivityLoading){
            onSaveSettings();
        }
    }

    void onSaveSettings()
    {
        if(!status.sSettingSearchStatus.equals(mSearchStatus))
        {
            mEvent.invokeObserver(Collections.singletonList(mSearchStatus), enums.etype.update_searcn);
        }
        if(status.sSettingJavaStatus != mJavaStatus)
        {
            mEvent.invokeObserver(Collections.singletonList(mJavaStatus), enums.etype.update_javascript);
        }
        if(notificationStatusGlobal != mNotificationStatus)
        {
            mEvent.invokeObserver(Collections.singletonList(mNotificationStatus), enums.etype.update_notification);
        }
        if(status.sSettingHistoryStatus != mHistoryStatus)
        {
            status.sSettingHistoryStatus = mHistoryStatus;
            mEvent.invokeObserver(Collections.singletonList(mHistoryStatus), enums.etype.update_history);
        }
        if(status.sSettingFontAdjustable != mFontAdjustable)
        {
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FONT_ADJUSTABLE,mFontAdjustable));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_FONT_SIZE,100));

            status.sSettingFontAdjustable = mFontAdjustable;
            status.sSettingFontSize = 100;
            mFontSize = 100;

            mEvent.invokeObserver(Collections.singletonList(mFontSize), enums.etype.update_font_adjustable);
        }
        if(status.sSettingFontSize != mFontSize)
        {
            if(mFontSize <=0){
                mFontSize = 1;
            }

            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_FONT_SIZE,(int)mFontSize));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FONT_ADJUSTABLE,false));

            status.sSettingFontSize = mFontSize;
            status.sSettingFontAdjustable = false;
            mFontAdjustable = false;

            mEvent.invokeObserver(Collections.singletonList(mFontSize), enums.etype.update_font_size);
        }
        if(status.sSettingCookieStatus != mCookieStatus)
        {
            status.sSettingCookieStatus = mCookieStatus;
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_COOKIE_ADJUSTABLE,status.sSettingCookieStatus));
            mEvent.invokeObserver(Collections.singletonList(mCookieStatus), enums.etype.update_cookies);
        }
    }

}
