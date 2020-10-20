package com.darkweb.genesissearchengine.appManager.settingManager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

import java.util.Arrays;
import java.util.List;
import static com.darkweb.genesissearchengine.constants.status.sSettingCookieStatus;
import static com.darkweb.genesissearchengine.constants.status.sSettingHistoryStatus;
import static com.darkweb.genesissearchengine.constants.status.sSettingJavaStatus;

public class settingController extends AppCompatActivity
{
    /*Private Observer Classes*/

    private homeController mHomeController;
    private settingViewController mSettingViewController;
    private settingModel mSettingModel;

    /*Private Variables*/

    private Spinner mSearch;
    private Spinner mJavascript;
    private Spinner mHistory;
    private Spinner mCookies;
    private Spinner mFontAdjustable;
    private Spinner mNotification;
    private SeekBar mFontSize;
    private TextView mFontSizePercentage;

    /*Initializations*/

    public settingController(){
        mHomeController = activityContextManager.getInstance().getHomeController();
        mSettingModel = new settingModel(new settingModelCallback());
        mSettingModel.initNotification(pluginController.getInstance().getNotificationStatus());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);

        viewsInitializations();
        modelInitialization();
        listenersInitializations();
        initializeFontSizeListener();
    }

    public void modelInitialization(){
        mSettingModel.setJavaStatus(sSettingJavaStatus);
        mSettingModel.setHistoryStatus(sSettingHistoryStatus);
        mSettingModel.setSearchStatus(status.sSettingSearchStatus);
        mSettingModel.setAdjustableStatus(status.sSettingFontAdjustable);
        mSettingModel.setFontSize(status.sSettingFontSize);
    }

    public void viewsInitializations()
    {
        mSearch = findViewById(R.id.search_manager);
        mJavascript = findViewById(R.id.javascript_manager);
        mHistory = findViewById(R.id.history_manager);
        mFontSize = findViewById(R.id.font_size);
        mFontAdjustable = findViewById(R.id.font_adjustable);
        mFontSizePercentage = findViewById(R.id.font_size_percentage);
        mCookies = findViewById(R.id.cookies_manager);
        mNotification = findViewById(R.id.notification_manager);

        mSettingViewController = new settingViewController(mSearch, mJavascript, mHistory, mFontSize, mFontAdjustable, mFontSizePercentage,this, new settingModelCallback(), mCookies,mNotification,pluginController.getInstance().getNotificationStatus());
    }

    public void listenersInitializations()
    {
        initializeItemSelectedListener(mSearch);
        initializeItemSelectedListener(mJavascript);
        initializeItemSelectedListener(mNotification);
        initializeItemSelectedListener(mHistory);
        initializeItemSelectedListener(mFontAdjustable);
        initializeItemSelectedListener(mCookies);
        pluginController.getInstance().logEvent(strings.EVENT_SETTINGS_OPENED);
    }

    /*Event Handlers*/

    @Override
    public void onTrimMemory(int level)
    {
        if(status.sSettingIsAppPaused && (level==80 || level==15))
        {
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.HOME_LOW_MEMORY,true));
            finish();
        }
    }

    @Override
    public void onResume()
    {
        activityContextManager.getInstance().setCurrentActivity(this);
        status.sSettingIsAppPaused = false;
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }


    @Override
    public void onBackPressed(){
        mSettingModel.onCloseView();
    }

    public void initializeItemSelectedListener(Spinner view){
        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(parentView.getId()== R.id.search_manager)
                {
                    mSettingModel.setSearchStatus(getEngineURL(position));
                }
                else if(parentView.getId()== R.id.javascript_manager)
                {
                    mSettingModel.setJavaStatus(position==0);
                }
                else if(parentView.getId()== R.id.history_manager)
                {
                    mSettingModel.setHistoryStatus(position==0);
                }
                else if(parentView.getId()== R.id.font_adjustable)
                {
                    if(position==0){
                        mSettingModel.setFontSize(100);
                    }
                    mSettingViewController.setFontSizeAdjustable(position==0);
                    mSettingModel.setAdjustableStatus(position==0);
                }
                else if(parentView.getId()== R.id.cookies_manager)
                {
                    mSettingModel.setCookieStatus(position);
                }
                else if(parentView.getId()== R.id.notification_manager)
                {
                    mSettingModel.setmNotificationStatus(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public void initializeFontSizeListener(){

        mFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float cur_progress = seekBar.getProgress();
                mSettingModel.setFontSize((cur_progress));
                mSettingViewController.updatePercentage(mFontSize.getProgress());
                mSettingViewController.setFontSize(b);
                mSettingModel.setAdjustableStatus(b);
                if(cur_progress<1){
                    mFontSize.setProgress(1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void onNavigationBackPressed(View view){
        mSettingModel.onCloseView();
    }

    /*Event Observer*/

    public class settingViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            return null;
        }
    }

    public class settingModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            if(e_type == enums.etype.update_searcn){
                status.sSettingSearchStatus = (String)data.get(0);
                mHomeController.onHomeButton(null);
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE, mSettingModel.getSearchStatus()));
            }
            else if(e_type == enums.etype.update_javascript){
                status.sSettingJavaStatus = (boolean)data.get(0);
                mHomeController.onUpdateJavascript();
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_JAVA_SCRIPT, status.sSettingJavaStatus));
            }
            else if(e_type == enums.etype.update_history){
                sSettingHistoryStatus = (boolean)data.get(0);
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_HISTORY_CLEAR, sSettingHistoryStatus));
            }
            else if(e_type == enums.etype.update_notification){
                pluginController.getInstance().setNotificationStatus((int)data.get(0));
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS, pluginController.getInstance().getNotificationStatus()));

                int notificationStatus = pluginController.getInstance().getNotificationStatus();
                if(notificationStatus==0){
                    pluginController.getInstance().enableTorNotification();
                } else if(notificationStatus==1){
                    pluginController.getInstance().disableTorNotification();
                }else {
                    pluginController.getInstance().enableTorNotificationNoBandwidth();
                }

            }
            else if(e_type == enums.etype.update_font_adjustable || e_type == enums.etype.update_font_size){
                mHomeController.onLoadFont();
            }
            else if(e_type == enums.etype.update_cookies){
                sSettingCookieStatus = (int)data.get(0);
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_COOKIE_ADJUSTABLE, sSettingCookieStatus));
                mHomeController.onUpdateCookies();
                pluginController.getInstance().updateCookiesStatus();
            }
            else if(e_type == enums.etype.close_view){
                finish();

            }
            return null;
        }
    }

    /*Helper Methods*/

    public String getEngineURL(int index){

        if (index == 0)
        {
            return constants.CONST_BACKEND_GENESIS_URL;
        }
        else if (index == 1)
        {
            return constants.CONST_BACKEND_GOOGLE_URL;
        }
        else
        {
            return constants.CONST_BACKEND_DUCK_DUCK_GO_URL;
        }
    }

}