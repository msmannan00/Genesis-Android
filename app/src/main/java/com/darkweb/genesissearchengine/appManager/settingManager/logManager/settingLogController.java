package com.darkweb.genesissearchengine.appManager.settingManager.logManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.helpManager.helpController;
import com.darkweb.genesissearchengine.constants.constants;
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
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static com.darkweb.genesissearchengine.appManager.settingManager.logManager.settingLogEnums.eLogViewController.M_TOOGLE_LOG_VIEW;

public class settingLogController extends AppCompatActivity {

    /* UI Variables */

    private SwitchMaterial mLogThemeToggle;

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
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onConfigurationChanged(newConfig);

        if(newConfig.uiMode != getResources().getConfiguration().uiMode){
            activityContextManager.getInstance().onResetTheme();
            activityThemeManager.getInstance().onConfigurationChanged(this);
        }
    }

    private void initializeViews() {
        mLogThemeToggle = findViewById(R.id.pLogThemeToggle);

        mSettingLogViewController = new settingLogViewController(this, new settingLogViewCallback(), mLogThemeToggle);
        mSettingLogViewController.onTrigger(settingLogEnums.eLogViewController.M_INIT_VIEW, Collections.singletonList(status.sLogThemeStyleAdvanced));
        mSettingLogModel = new settingLogModel(new settingLogModelCallback());
    }

    /*View Callbacks*/

    private class settingLogViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /*Model Callbacks*/

    private class settingLogModelCallback implements eventObserver.eventListener{

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

    public void onClose(View view){
        finish();
    }

    public void onSwitchLogUIMode(View view){
        mSettingLogModel.onTrigger(settingLogEnums.eLogModel.M_SWITCH_LOG_VIEW, Collections.singletonList(!mLogThemeToggle.isChecked()));
        mSettingLogViewController.onTrigger(M_TOOGLE_LOG_VIEW);
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_LIST_VIEW, status.sLogThemeStyleAdvanced));

        helperMethod.onDelayHandler(this, 250, () -> {
            activityContextManager.getInstance().getOrbotLogController().onRefreshLayoutTheme();
            return null;
        });
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

}
