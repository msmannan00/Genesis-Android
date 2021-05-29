package com.darkweb.genesissearchengine.appManager.settingManager.advanceManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class settingAdvanceController extends AppCompatActivity {

    /* Private Variables */

    private settingAdvanceModel mSettingAdvanceModel;
    private settingAdvanceViewController mSettingAdvanceViewController;
    private SwitchMaterial mRestoreTabs;
    private SwitchMaterial mShowWebFonts;
    private SwitchMaterial mToolbarTheme;

    private ArrayList<RadioButton> mImageOption = new ArrayList<>();
    private ArrayList<RadioButton> mTabLayoutOption = new ArrayList<>();
    private boolean mIsChanged = false;

    /* Initializations */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting_advance_view);

        viewsInitializations();
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

    public void viewsInitializations() {
        mRestoreTabs = findViewById(R.id.pRestoreTabs);
        mShowWebFonts = findViewById(R.id.pShowWebFonts);
        mToolbarTheme = findViewById(R.id.pToolbarTheme);
        mImageOption.add(findViewById(R.id.pAdvanceImageOption1));
        mImageOption.add(findViewById(R.id.pAdvanceImageOption2));
        mTabLayoutOption.add(findViewById(R.id.pAdvanceGridOption1));
        mTabLayoutOption.add(findViewById(R.id.pAdvanceGridOption2));

        activityContextManager.getInstance().onStack(this);
        mSettingAdvanceViewController = new settingAdvanceViewController(this, new settingAdvanceViewCallback(), mRestoreTabs, mShowWebFonts, mToolbarTheme, mImageOption, mTabLayoutOption);
        mSettingAdvanceModel = new settingAdvanceModel(new settingAdvanceModelCallback());
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    /*View Callbacks*/

    public class settingAdvanceViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /*Model Callbacks*/

    public class settingAdvanceModelCallback implements eventObserver.eventListener{

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

        int notificationStatus = (int)pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_GET_NOTIFICATION_STATUS);
        if(notificationStatus==0){
            pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_DISABLE_NOTIFICATION);
        } else if(notificationStatus==1){
            pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_ENABLE_NOTIFICATION);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
        activityContextManager.getInstance().onRemoveStack(this);
    }

    /*UI Redirection*/

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    public void onClose(View view){
        if(mIsChanged){
            pluginController.getInstance().onOrbotInvoke(Arrays.asList(status.sShowImages, status.sClearOnExit), pluginEnums.eOrbotManager.M_UPDATE_PRIVACY);
            activityContextManager.getInstance().getHomeController().initRuntimeSettings();
        }
        finish();
    }

    public void onRestoreTabs(View view) {
        mSettingAdvanceModel.onTrigger(settingAdvanceEnums.eAdvanceModel.M_RESTORE_TAB, Collections.singletonList(!mRestoreTabs.isChecked()));
        mRestoreTabs.toggle();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_RESTORE_TAB, status.sRestoreTabs));
    }

    public void onShowImages(View view) {
        mIsChanged = true;
        mSettingAdvanceViewController.onTrigger(settingAdvanceEnums.eAdvanceViewController.M_CLEAR_IMAGE, Collections.singletonList(null));
        mSettingAdvanceModel.onTrigger(settingAdvanceEnums.eAdvanceModel.M_SHOW_IMAGE, Collections.singletonList(view));
        mSettingAdvanceViewController.onTrigger(settingAdvanceEnums.eAdvanceViewController.M_SET_IMAGE, Collections.singletonList(view));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_SHOW_IMAGES,status.sShowImages));
    }

    public void onGridView(View view) {
        mSettingAdvanceViewController.onTrigger(settingAdvanceEnums.eAdvanceViewController.M_CLEAR_GRID, Collections.singletonList(null));
        mSettingAdvanceModel.onTrigger(settingAdvanceEnums.eAdvanceModel.M_SHOW_TAB_GRID, Collections.singletonList(view));
        mSettingAdvanceViewController.onTrigger(settingAdvanceEnums.eAdvanceViewController.M_SET_GRID, Collections.singletonList(view));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SHOW_TAB_GRID,status.sTabGridLayoutEnabled));
    }

    public void onShowWebFonts(View view) {
        mIsChanged = true;
        mSettingAdvanceModel.onTrigger(settingAdvanceEnums.eAdvanceModel.M_SHOW_WEB_FONTS, Collections.singletonList(!mShowWebFonts.isChecked()));
        mShowWebFonts.toggle();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SHOW_FONTS,status.sShowWebFonts));
    }

    public void onToolbarTheme(View view) {
        mSettingAdvanceModel.onTrigger(settingAdvanceEnums.eAdvanceModel.M_TOOLBAR_THEME, Collections.singletonList(!mToolbarTheme.isChecked()));
        mToolbarTheme.toggle();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_TOOLBAR_THEME,status.sToolbarTheme));
        activityContextManager.getInstance().getHomeController().onUpdateStatusBarTheme();
    }
}