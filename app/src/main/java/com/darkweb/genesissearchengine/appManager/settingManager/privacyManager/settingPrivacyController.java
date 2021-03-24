package com.darkweb.genesissearchengine.appManager.settingManager.privacyManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.helpManager.helpController;
import com.darkweb.genesissearchengine.appManager.settingManager.trackingManager.settingTrackingController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.helperManager.theme;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class settingPrivacyController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private settingPrivacyModel mSettingPrivacyModel;
    private settingPrivacyViewController mSettingPrivacyViewController;
    private SwitchMaterial mJavaScript;
    private SwitchMaterial mPopup;
    private SwitchMaterial mDoNotTrack;
    private SwitchMaterial mClearDataOnExit;
    private ArrayList<RadioButton> mCookie = new ArrayList<>();
    private boolean mSettingChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting_privacy_view);

        viewsInitializations();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);

        theme.getInstance().onConfigurationChanged(this);
    }

    private void viewsInitializations() {
        mJavaScript = findViewById(R.id.pJavascript);
        mDoNotTrack = findViewById(R.id.pDoNotTrack);
        mClearDataOnExit = findViewById(R.id.pClearDataOnExit);
        mPopup = findViewById(R.id.pPopup);
        mCookie.add(findViewById(R.id.pCookieRadioOption1));
        mCookie.add(findViewById(R.id.pCookieRadioOption2));
        mCookie.add(findViewById(R.id.pCookieRadioOption3));
        mCookie.add(findViewById(R.id.pCookieRadioOption4));

        activityContextManager.getInstance().onStack(this);
        mSettingPrivacyViewController = new settingPrivacyViewController(this, new settingPrivacyController.settingAccessibilityViewCallback(), mJavaScript, mDoNotTrack, mClearDataOnExit, mCookie, mPopup);
        mSettingPrivacyModel = new settingPrivacyModel(new settingPrivacyController.settingAccessibilityModelCallback());
    }

    /*View Callbacks*/

    private class settingAccessibilityViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /*Model Callbacks*/

    private class settingAccessibilityModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume()
    {
        if(mSettingChanged){
            activityContextManager.getInstance().setCurrentActivity(this);
        }
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(mSettingChanged){
            activityContextManager.getInstance().setCurrentActivity(this);
            activityContextManager.getInstance().getHomeController().initRuntimeSettings();
        }
        activityContextManager.getInstance().onRemoveStack(this);
        finish();
    }

    /*UI Redirection*/

    public void onClose(View view){
        onBackPressed();
    }

    public void onJavaScript(View view){
        mSettingChanged = true;
        mSettingPrivacyModel.onTrigger(settingPrivacyEnums.ePrivacyModel.M_SET_JAVASCRIPT, Collections.singletonList(!status.sSettingJavaStatus));
        mJavaScript.toggle();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_JAVA_SCRIPT,status.sSettingJavaStatus));
    }

    public void onPopup(View view){
        mSettingPrivacyModel.onTrigger(settingPrivacyEnums.ePrivacyModel.M_SET_POPUP, Collections.singletonList(!status.sSettingPopupStatus));
        mPopup.toggle();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_POPUP,status.sSettingPopupStatus));
    }

    public void onDoNotTrack(View view){
        mSettingChanged = true;
        mSettingPrivacyModel.onTrigger(settingPrivacyEnums.ePrivacyModel.M_SET_DONOT_TRACK, Collections.singletonList(!status.sStatusDoNotTrack));
        mDoNotTrack.toggle();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_DONOT_TRACK,status.sStatusDoNotTrack));
    }

    public void onCookies(View view){
        mSettingChanged = true;
        mSettingPrivacyViewController.onTrigger(settingPrivacyEnums.ePrivacyViewController.M_SET_COOKIE_STATUS, Collections.singletonList(view));
        mSettingPrivacyModel.onTrigger(settingPrivacyEnums.ePrivacyModel.M_SET_COOKIES, Collections.singletonList(view));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_COOKIE_ADJUSTABLE,status.sSettingCookieStatus));
    }

    public void onClearPrivateData(View view){
        mSettingChanged = true;
        mSettingPrivacyModel.onTrigger(settingPrivacyEnums.ePrivacyModel.M_SET_CLEAR_PRIVATE_DATA, Collections.singletonList(!status.sClearOnExit));
        mClearDataOnExit.toggle();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_HISTORY_CLEAR,status.sClearOnExit));
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onManageTracking(View view) {
        helperMethod.openActivity(settingTrackingController.class, constants.CONST_LIST_HISTORY, this,true);
    }

}
