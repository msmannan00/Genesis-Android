package com.darkweb.genesissearchengine.appManager.settingManager.settingHomeManager;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.helpManager.helpController;
import com.darkweb.genesissearchengine.appManager.proxyStatusManager.proxyStatusController;
import com.darkweb.genesissearchengine.appManager.settingManager.accessibilityManager.settingAccessibilityController;
import com.darkweb.genesissearchengine.appManager.settingManager.advanceManager.settingAdvanceController;
import com.darkweb.genesissearchengine.appManager.settingManager.clearManager.settingClearController;
import com.darkweb.genesissearchengine.appManager.settingManager.generalManager.settingGeneralController;
import com.darkweb.genesissearchengine.appManager.settingManager.notificationManager.settingNotificationController;
import com.darkweb.genesissearchengine.appManager.settingManager.privacyManager.settingPrivacyController;
import com.darkweb.genesissearchengine.appManager.settingManager.searchEngineManager.settingSearchController;
import com.darkweb.genesissearchengine.appManager.settingManager.trackingManager.settingTrackingController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.appManager.activityThemeManager;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManager.*;

public class settingHomeController extends AppCompatActivity
{
    /*Private Observer Classes*/

    private settingHomeViewController mSettingViewController;
    private settingHomeModel mSettingModel;

    /*Initializations*/

    public settingHomeController(){
        mSettingModel = new settingHomeModel(new settingModelCallback());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);
        if(!status.mThemeApplying){
            activityContextManager.getInstance().onStack(this);
        }

        setContentView(R.layout.setting);
        viewsInitializations();
        listenersInitializations();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        if(activityThemeManager.getInstance().onInitTheme(this) && !status.mThemeApplying){
            activityContextManager.getInstance().onResetTheme();
        }

        helperMethod.updateResources(this, status.mSystemLocale.getDisplayName());
        activityContextManager.getInstance().getHomeController().updateResources(this, status.mSystemLocale.getDisplayName());
        super.onConfigurationChanged(newConfig);
    }

    private void onInitTheme(){

        if(status.mThemeApplying){
            if(status.sTheme == enums.Theme.THEME_DARK){
                setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else if(status.sTheme == enums.Theme.THEME_LIGHT){
                setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }else {
                if(!status.sDefaultNightMode){
                    setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }else {
                    setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        }
    }

    private void viewsInitializations()
    {
        activityContextManager.getInstance().setSettingController(this);
        mSettingViewController = new settingHomeViewController(this, new settingViewCallback());
    }

    private void listenersInitializations()
    {
    }

    public void cicadaClipboard(View view) {
        pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_OPEN_CICADA);
    }

    /*View Callbacks*/

    private class settingViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /*Model Callbacks*/

    private class settingModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
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
        if(status.mThemeApplying){
            // activityContextManager.getInstance().onStack(this);
        }

        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
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
        finish();
    }

    @Override
    protected void onDestroy() {
        if(!status.mThemeApplying){
            activityContextManager.getInstance().onRemoveStack(this);
        }
        activityContextManager.getInstance().setSettingController(null);
        super.onDestroy();
    }

    /*External Redirection*/

    public void onRedrawXML(){
        setContentView(R.layout.setting);
    }

    public void onReInitTheme(){
        recreate();
    }

    /*UI Redirection*/

    public void onNavigationBackPressed(View view){
        finish();
    }

    public void onDefaultBrowser(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            helperMethod.openDefaultBrowser(this);
        }else{
            pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this),  M_NOT_SUPPORTED);
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

    public void onManageTracking(View view) {
        helperMethod.openActivity(settingTrackingController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onReportWebsite(View view) {
        try {
            finish();
            activityContextManager.getInstance().getHomeController().onLoadURL(constants.CONST_REPORT_URL + URLEncoder.encode(activityContextManager.getInstance().getHomeController().onGetCurrentURL(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void onSitemap(View view) {
        finish();
        activityContextManager.getInstance().getHomeController().onLoadURL(constants.CONST_SITEMAP);
    }

    public void onPrivacyPolicy(View view) {
        finish();
        activityContextManager.getInstance().getHomeController().onLoadURL(constants.CONST_PRIVACY_POLICY_URL);
    }

    public void onRateApplication(View view) {
        pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(activityContextManager.getInstance().getHomeController().onGetCurrentURL(), this),  M_RATE_APP);
        status.sSettingIsAppRated = true;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,true));
    }

    public void onShareApplication(View view) {
        helperMethod.shareApp(this);
    }

    public void onOpenProxyStatus(View view) {
        helperMethod.openActivity(proxyStatusController.class, constants.CONST_LIST_HISTORY, this,true);
    }

}