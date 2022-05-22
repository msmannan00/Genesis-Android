package com.hiddenservices.onionservices.appManager.settingManager.notificationManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

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
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class settingNotificationController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private SwitchMaterial mNotificationManual;
    private settingNotificationModel mSettingNotificationModel;
    private settingNotificationViewController mSettingNotificationViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_notification_view);

        viewsInitializations();
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

    private void viewsInitializations() {
        activityContextManager.getInstance().onStack(this);
        mNotificationManual = findViewById(R.id.pNotificationManual);
        mSettingNotificationViewController = new settingNotificationViewController(this, new settingNotificationViewCallback(), mNotificationManual);

        mSettingNotificationModel = new settingNotificationModel(new settingNotificationModelCallback());
    }

    /*View Callbacks*/

    private class settingNotificationViewCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            return null;
        }
    }

    /*Model Callbacks*/

    private class settingNotificationModelCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            return null;
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume() {
        activityContextManager.getInstance().onCheckPurgeStack();
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();

        int notificationStatus = status.sBridgeNotificationManual;
        if (notificationStatus == 0) {
            pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_DISABLE_NOTIFICATION);
        } else {
            pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_ENABLE_NOTIFICATION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    /*UI Redirection*/

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this, true);
    }

    public void onClose(View view) {
        finish();
    }

    public void onOpenNotificationSettings(View view) {
        helperMethod.openNotification(this);
    }

    public void onSaveLocalNotificationSettings(View view) {
        mSettingNotificationModel.onTrigger(settingNotificationEnums.eNotificationModel.M_UPDATE_LOCAL_NOTIFICATION, Collections.singletonList(!mNotificationManual.isChecked()));
        mNotificationManual.toggle();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS, status.sBridgeNotificationManual));
    }
}