package com.darkweb.genesissearchengine.appManager.settingManager.settingHomePage;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.settingManager.accessibilityManager.settingAccessibilityController;
import com.darkweb.genesissearchengine.appManager.settingManager.advanceManager.settingAdvanceController;
import com.darkweb.genesissearchengine.appManager.settingManager.clearManager.settingClearController;
import com.darkweb.genesissearchengine.appManager.settingManager.generalManager.settingGeneralController;
import com.darkweb.genesissearchengine.appManager.settingManager.notificationManager.settingNotificationController;
import com.darkweb.genesissearchengine.appManager.settingManager.privacyManager.settingPrivacyController;
import com.darkweb.genesissearchengine.appManager.settingManager.searchEngineManager.settingSearchController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import java.util.Arrays;
import java.util.List;
import static com.darkweb.genesissearchengine.constants.enums.etype.on_not_support;

public class settingController extends AppCompatActivity
{
    /*Private Observer Classes*/

    private settingViewController mSettingViewController;
    private settingModel mSettingModel;

    /*Initializations*/

    public settingController(){
        mSettingModel = new settingModel(new settingModelCallback());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        onInitTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        pluginController.getInstance().onCreate(this);
        viewsInitializations();
        listenersInitializations();
    }

    public void onInitTheme(){

        if(status.sTheme == enums.Theme.THEME_DARK){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else if(status.sTheme == enums.Theme.THEME_LIGHT){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else {
            if((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
    }

    public void applyTheme(){
        recreate();
    }

    public void viewsInitializations()
    {
        activityContextManager.getInstance().setSettingController(this);
        mSettingViewController = new settingViewController(this, new settingModelCallback());
    }

    public void listenersInitializations()
    {
        pluginController.getInstance().logEvent(strings.EVENT_SETTINGS_OPENED);
    }

    /*Local Overrides*/

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
        onInitTheme();
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }


    @Override
    public void onBackPressed(){
        finish();
    }

    /*UI Redirection*/

    public void onNavigationBackPressed(View view){
        finish();
    }

    public void onDefaultBrowser(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS));
        }else{
            pluginController.getInstance().MessageManagerHandler(this, null, on_not_support);
        }
    }

    public void onManageNotification(View view){
        helperMethod.openActivity(settingNotificationController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onManageSearchEngine(View view){
        helperMethod.openActivity(settingSearchController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onManageSearchAccessibility(View view) {
        helperMethod.openActivity(settingAccessibilityController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onManageGeneral(View view) {
        helperMethod.openActivity(settingGeneralController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onManageSearchClearData(View view) {
        helperMethod.openActivity(settingClearController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onManageSearchPrivacy(View view) {
        helperMethod.openActivity(settingPrivacyController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onManageSearchAdvanced(View view) {
        helperMethod.openActivity(settingAdvanceController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    /*Event Observer*/

    public class settingViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    public class settingModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

}