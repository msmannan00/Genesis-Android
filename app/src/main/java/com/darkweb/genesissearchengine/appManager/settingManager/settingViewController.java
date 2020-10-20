package com.darkweb.genesissearchengine.appManager.settingManager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;
import static com.darkweb.genesissearchengine.constants.status.sSettingHistoryStatus;
import static com.darkweb.genesissearchengine.constants.status.sSettingJavaStatus;

class settingViewController
{
    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;

    private Spinner mSearch;
    private Spinner mJavaScript;
    private Spinner mNotification;
    private Spinner mHistory;
    private Spinner mCookies;
    private Spinner mFontAdjustable;
    private SeekBar mFontSize;
    private TextView mFontSizePercentage;

    /*Initializations*/

    settingViewController(Spinner mSearch, Spinner mJavaScript, Spinner mHistory, SeekBar mFontSize, Spinner mFontAdjustable, TextView mFontSizePercentage, settingController mContext, eventObserver.eventListener mEvent, Spinner mCookies, Spinner mNotification, int notificationStatus)
    {
        this.mFontSizePercentage = mFontSizePercentage;
        this.mSearch = mSearch;
        this.mJavaScript = mJavaScript;
        this.mNotification = mNotification;
        this.mHistory = mHistory;
        this.mFontAdjustable = mFontAdjustable;
        this.mFontSize = mFontSize;
        this.mCookies = mCookies;

        this.mEvent = mEvent;
        this.mContext = mContext;

        initNotification(notificationStatus);
        initViews();
        initJavascript();
        initHistory();
        initSearchEngine();
        initFontAdjustable();
        initCookies();
        initFontSize();
        initPostUI();
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
            }
            else {
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.white));
            }
        }
    }

    private void initViews()
    {
        mSearch.setDropDownVerticalOffset(15);
        mSearch.setDropDownHorizontalOffset(-15);
        mJavaScript.setDropDownVerticalOffset(15);
        mJavaScript.setDropDownHorizontalOffset(-15);
        mNotification.setDropDownVerticalOffset(15);
        mNotification.setDropDownHorizontalOffset(-15);
        mHistory.setDropDownVerticalOffset(15);
        mHistory.setDropDownHorizontalOffset(-15);
        mCookies.setDropDownVerticalOffset(15);
        mCookies.setDropDownHorizontalOffset(-15);
        mFontAdjustable.setDropDownHorizontalOffset(-15);
        mFontAdjustable.setDropDownVerticalOffset(15);
    }

    private void initJavascript()
    {
        if (sSettingJavaStatus)
        {
            mJavaScript.setSelection(0);
        }
        else
        {
            mJavaScript.setSelection(1);
        }
    }

    private void initNotification(int status)
    {
        mNotification.setSelection(status);
    }

    private void initHistory()
    {
        if (sSettingHistoryStatus)
        {
            mHistory.setSelection(0);
        }
        else
        {
            mHistory.setSelection(1);
        }
    }

    private void initCookies()
    {
        mCookies.setSelection(status.sSettingCookieStatus);
    }

    private void initFontAdjustable()
    {
        if (status.sSettingFontAdjustable)
        {
            mFontAdjustable.setSelection(0);
        }
        else
        {
            mFontAdjustable.setSelection(1);
        }
    }

    private void initFontSize()
    {
        mFontSize.setProgress((int)status.sSettingFontSize);
        updatePercentage(mFontSize.getProgress());
    }

    void setFontSize(boolean status){
        if(status){
            mFontAdjustable.setSelection(1);
        }
    }

    void setFontSizeAdjustable(boolean status){
        if(status){
            mFontSize.setProgress(100);
        }
    }

    private void initSearchEngine()
    {
        mSearch.setSelection(getEngineIndex());
    }

    /*External Helper Methods*/

    private int getEngineIndex(){
        if(status.sSettingSearchStatus.equals(constants.CONST_BACKEND_GENESIS_URL)){
            return 0;
        }
        else if(status.sSettingSearchStatus.equals(constants.CONST_BACKEND_GOOGLE_URL)){
            return 1;
        }
        else
            return 2;
    }

    @SuppressLint("SetTextI18n")
    void updatePercentage(int font_size){

        mFontSizePercentage.setText(strings.SETTING_CUSTOM_FONTS + strings.GENERIC_EMPTY_SPACE + font_size +strings.SETTING_PERCENT_SIGN);
    }
}
