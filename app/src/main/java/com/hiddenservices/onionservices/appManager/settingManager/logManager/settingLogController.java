package com.hiddenservices.onionservices.appManager.settingManager.logManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.helpManager.helpController;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.dataManager.dataController;
import com.hiddenservices.onionservices.dataManager.dataEnums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.appManager.activityThemeManager;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import com.hiddenservices.onionservices.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hiddenservices.onionservices.appManager.settingManager.logManager.settingLogEnums.eLogViewController.M_toggle_LOG_VIEW;

public class settingLogController extends AppCompatActivity {

    /* UI Variables */

    private SwitchMaterial mSettingLogStatusSwitch;

    /* Private Variables */

    private settingLogModel mSettingLogModel;
    private settingLogViewController mSettingLogViewController;

    /* Initializations */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        activityContextManager.getInstance().onStack(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_log_view);

        initializeViews();
        onBack();
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
        mSettingLogStatusSwitch = findViewById(R.id.pSettingLogStatusSwitch);

        mSettingLogViewController = new settingLogViewController(this, new settingLogViewCallback(), mSettingLogStatusSwitch);
        mSettingLogViewController.onTrigger(settingLogEnums.eLogViewController.M_INIT_VIEW, Collections.singletonList(status.sLogThemeStyleAdvanced));
        mSettingLogViewController.onInit();

        mSettingLogModel = new settingLogModel(new settingLogModelCallback());
        mSettingLogModel.onInit();
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
        activityContextManager.getInstance().onPurgeStack();
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity();
        super.onResume();
    }

    public void onBack() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onClose(null);
            }
        });
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
        if (view.getId() == R.id.pSettingLogStatus) {
            mSettingLogModel.onTrigger(settingLogEnums.eLogModel.M_SWITCH_LOG_VIEW, Collections.singletonList(!mSettingLogStatusSwitch.isChecked()));
            mSettingLogViewController.onTrigger(M_toggle_LOG_VIEW);
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_LIST_VIEW, status.sLogThemeStyleAdvanced));

            helperMethod.onDelayHandler(250, () -> {
                activityContextManager.getInstance().getOrbotLogController().onRefreshLayoutTheme();
                return null;
            });
        }
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this, true);
    }

}
