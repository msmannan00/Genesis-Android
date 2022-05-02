package com.hiddenservices.onionservices.appManager.settingManager.advertSetttings;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.helpManager.helpController;
import com.hiddenservices.onionservices.appManager.settingManager.advertSetttings.advertResources.advert_constants;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.appManager.activityThemeManager;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class advertSettingController extends AppCompatActivity {

    /* UI Variables */

    /* Private Variables */

    private advertSettingModel mAdvertSettingModel;
    private advertSettingViewController mAdvertSettingViewController;
    private ArrayList<com.google.android.material.switchmaterial.SwitchMaterial> mSwitchButton = new ArrayList<>();

    /* Initializations */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        activityContextManager.getInstance().onStack(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_advert_view);

        initializeViews();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onConfigurationChanged(newConfig);

        if (newConfig.uiMode != getResources().getConfiguration().uiMode) {
            activityContextManager.getInstance().onResetTheme();
            activityThemeManager.getInstance().onConfigurationChanged(this);
        }
    }

    private void initializeViews() {

        mSwitchButton.add(findViewById(R.id.pAdOptionSwitch1));
        mSwitchButton.add(findViewById(R.id.pAdOptionSwitch2));
        mSwitchButton.add(findViewById(R.id.pAdOptionSwitch3));
        mSwitchButton.add(findViewById(R.id.pAdOptionSwitch4));
        mSwitchButton.add(findViewById(R.id.pAdOptionSwitch5));
        mSwitchButton.add(findViewById(R.id.pAdOptionSwitch6));
        mSwitchButton.add(findViewById(R.id.pAdOptionSwitch7));
        mSwitchButton.add(findViewById(R.id.pAdOptionSwitch8));
        mSwitchButton.add(findViewById(R.id.pAdOptionSwitch9));

        mAdvertSettingViewController = new advertSettingViewController(this, new settingLogViewCallback(), mSwitchButton);
        mAdvertSettingModel = new advertSettingModel(new settingLogModelCallback());
    }

    /*View Callbacks*/

    private class settingLogViewCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> pData, Object pCommands) {
            return null;
        }
    }

    /*Model Callbacks*/

    private class settingLogModelCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> pData, Object pCommands) {
            return null;
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume() {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
    }

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    /*UI Redirection*/

    public void onClose(View view) {
        finish();
    }

    public void onTriggerUI(View view) {
        if (view.getId() == R.id.pAdOption1) {
            mAdvertSettingViewController.onTrigger(advertSettingEnums.eAdvertSettingViewController.M_TOGGLE_SWITCH, Collections.singletonList(0));
            advert_constants.S_INSENSITIVE_CATEGORIES = mSwitchButton.get(0).isChecked();
        } else if (view.getId() == R.id.pAdOption2) {
            mAdvertSettingViewController.onTrigger(advertSettingEnums.eAdvertSettingViewController.M_TOGGLE_SWITCH, Collections.singletonList(1));
            advert_constants.S_APP_REDIRECTION = mSwitchButton.get(1).isChecked();
        } else if (view.getId() == R.id.pAdOption3) {
            mAdvertSettingViewController.onTrigger(advertSettingEnums.eAdvertSettingViewController.M_TOGGLE_SWITCH, Collections.singletonList(2));
            advert_constants.S_SEARCH_RESULTS = mSwitchButton.get(2).isChecked();
        } else if (view.getId() == R.id.pAdOption4) {
            mAdvertSettingViewController.onTrigger(advertSettingEnums.eAdvertSettingViewController.M_TOGGLE_SWITCH, Collections.singletonList(3));
            advert_constants.S_SENSOR_ADVERTISEMENT = mSwitchButton.get(3).isChecked();
        } else if (view.getId() == R.id.pAdOption5) {
            mAdvertSettingViewController.onTrigger(advertSettingEnums.eAdvertSettingViewController.M_TOGGLE_SWITCH, Collections.singletonList(4));
            advert_constants.S_NON_TRACKABLE_ADVERTISEMENT = mSwitchButton.get(4).isChecked();
        } else if (view.getId() == R.id.pAdOption6) {
            mAdvertSettingViewController.onTrigger(advertSettingEnums.eAdvertSettingViewController.M_TOGGLE_SWITCH, Collections.singletonList(5));
            advert_constants.S_VIDEO_ADVERTISEMENT = mSwitchButton.get(5).isChecked();
        } else if (view.getId() == R.id.pAdOption7) {
            mAdvertSettingViewController.onTrigger(advertSettingEnums.eAdvertSettingViewController.M_TOGGLE_SWITCH, Collections.singletonList(6));
            advert_constants.S_AUTHORIZED_ADVERTISERS = mSwitchButton.get(6).isChecked();
        } else if (view.getId() == R.id.pAdOption8) {
            mAdvertSettingViewController.onTrigger(advertSettingEnums.eAdvertSettingViewController.M_TOGGLE_SWITCH, Collections.singletonList(7));
            advert_constants.S_ERROR_REPORTING = mSwitchButton.get(7).isChecked();
        } else if (view.getId() == R.id.pAdOption9) {
            mAdvertSettingViewController.onTrigger(advertSettingEnums.eAdvertSettingViewController.M_TOGGLE_SWITCH, Collections.singletonList(8));
            advert_constants.S_NATIVE_ADVERTISERS = mSwitchButton.get(8).isChecked();
        }
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this, true);
    }

}
