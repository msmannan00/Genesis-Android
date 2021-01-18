package com.darkweb.genesissearchengine.appManager.settingManager.advanceManager;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.helpManager.helpController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class settingAdvanceController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private settingAdvanceModel mSettingAdvanceModel;
    private settingAdvanceViewController mSettingAdvanceViewController;
    private SwitchMaterial mRestoreTabs;
    private SwitchMaterial mShowWebFonts;
    private SwitchMaterial mToolbarTheme;
    private ArrayList<RadioButton> mImageOption = new ArrayList<>();
    private boolean mIsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_advance_view);

        viewsInitializations();
    }

    public void viewsInitializations() {
        mRestoreTabs = findViewById(R.id.pRestoreTabs);
        mShowWebFonts = findViewById(R.id.pShowWebFonts);
        mToolbarTheme = findViewById(R.id.pToolbarTheme);
        mImageOption.add(findViewById(R.id.pAdvanceImageOption1));
        mImageOption.add(findViewById(R.id.pAdvanceImageOption2));

        activityContextManager.getInstance().onStack(this);
        mSettingAdvanceViewController = new settingAdvanceViewController(this, new settingAdvanceViewCallback(), mRestoreTabs, mShowWebFonts, mToolbarTheme, mImageOption);
        mSettingAdvanceModel = new settingAdvanceModel(new settingAdvanceModelCallback());
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    /* LISTENERS */
    public class settingAdvanceViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }


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
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();

        int notificationStatus = pluginController.getInstance().getNotificationStatus();
        if(notificationStatus==0){
            pluginController.getInstance().disableTorNotification();
        } else if(notificationStatus==1){
            pluginController.getInstance().enableTorNotification();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(mIsChanged){
            pluginController.getInstance().updatePrivacy();
            activityContextManager.getInstance().getHomeController().initRuntimeSettings();
        }
        activityContextManager.getInstance().onRemoveStack(this);
        finish();
    }

    /*UI Redirection*/

    public void onClose(View view){
        if(mIsChanged){
            pluginController.getInstance().updatePrivacy();
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
        activityContextManager.getInstance().getHomeController().onUpdateToolbarTheme();
    }
}