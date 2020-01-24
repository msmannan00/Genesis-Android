package com.darkweb.genesissearchengine.appManager.settingManager;

import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import java.util.Collections;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

class settingModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    private String mSearchStatus = strings.EMPTY_STR;
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
        mSearchStatus = status.sSearchStatus;
        mHistoryStatus = status.sHistoryStatus;
        mJavaStatus = status.sJavaStatus;
        mCookieStatus = status.sCookieStatus;
    }

    void initNotification(int notificationStatus){
        mNotificationStatus = notificationStatus;
        notificationStatusGlobal = notificationStatus;
    }

    /*Changed Status*/

    String getSearchStatus(){
        return mSearchStatus;
    }

    void setCookieStatus(int cookie_status){
        this.mCookieStatus = cookie_status;
    }

    void setSearchStatus(String search_status){
        this.mSearchStatus = search_status;
    }

    void setFontSize(float font_size){
        this.mFontSize = font_size;
    }

    void setAdjustableStatus(boolean font_status){
        this.mFontAdjustable = font_status;
    }


    void setJavaStatus(boolean java_status){
        this.mJavaStatus = java_status;
    }

    void setmNotificationStatus(int notification_status){
        this.mNotificationStatus = notification_status;
    }

    void setHistoryStatus(boolean history_status){
        this.mHistoryStatus = history_status;
    }

    void onCloseView()
    {
        if(!status.sSearchStatus.equals(mSearchStatus))
        {
            mEvent.invokeObserver(Collections.singletonList(mSearchStatus), enums.etype.update_searcn);
        }
        if(status.sJavaStatus != mJavaStatus)
        {
            mEvent.invokeObserver(Collections.singletonList(mJavaStatus), enums.etype.update_javascript);
        }
        if(notificationStatusGlobal != mNotificationStatus)
        {
            mEvent.invokeObserver(Collections.singletonList(mNotificationStatus), enums.etype.update_notification);
        }
        if(status.sHistoryStatus != mHistoryStatus)
        {
            status.sHistoryStatus = mHistoryStatus;
            mEvent.invokeObserver(Collections.singletonList(mHistoryStatus), enums.etype.update_history);
        }
        if(status.sFontAdjustable != mFontAdjustable)
        {
            dataController.getInstance().setBool(keys.FONT_ADJUSTABLE, mFontAdjustable);
            dataController.getInstance().setInt(keys.FONT_SIZE,100);

            status.sFontAdjustable = mFontAdjustable;
            status.sFontSize = 100;
            mFontSize = 100;

            mEvent.invokeObserver(Collections.singletonList(mFontSize), enums.etype.update_font_adjustable);
        }
        if(status.sFontSize != mFontSize)
        {
            if(mFontSize <=0){
                mFontSize = 1;
            }

            dataController.getInstance().setInt(keys.FONT_SIZE,(int) mFontSize);
            dataController.getInstance().setBool(keys.FONT_ADJUSTABLE,false);

            status.sFontSize = mFontSize;
            status.sFontAdjustable = false;
            mFontAdjustable = false;

            mEvent.invokeObserver(Collections.singletonList(mFontSize), enums.etype.update_font_size);
        }
        if(status.sCookieStatus != mCookieStatus)
        {
            status.sCookieStatus = mCookieStatus;
            dataController.getInstance().setInt(keys.COOKIE_ADJUSTABLE,status.sCookieStatus);
            mEvent.invokeObserver(Collections.singletonList(mCookieStatus), enums.etype.update_cookies);
        }
        mEvent.invokeObserver(Collections.singletonList(mHistoryStatus), enums.etype.close_view);
    }

}
