package com.darkweb.genesissearchengine.appManager.settingManager.generalManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.helpManager.helpController;
import com.darkweb.genesissearchengine.appManager.languageManager.languageController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class settingGeneralController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private settingGeneralModel mSettingGeneralModel;
    private settingGeneralViewController mSettingGeneralViewController;
    private SwitchMaterial mFullScreenMode;
    private SwitchMaterial mOpenURLInNewTab;
    private RadioButton mThemeLight;
    private RadioButton mThemeDark;
    private RadioButton mThemeDefault;
    private TextView mHomePageText;

    /* PRIVATE LOCAL VARIABLES */

    private Boolean mIsThemeChanging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting_general_view);
        activityContextManager.getInstance().setSettingGeneralController(this);
        viewsInitializations();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
    }

    private void viewsInitializations() {

        mFullScreenMode = findViewById(R.id.pJSStatus);
        mThemeLight = findViewById(R.id.pThemeLight);
        mThemeDark = findViewById(R.id.pThemeDark);
        mThemeDefault = findViewById(R.id.pThemeDefault);
        mHomePageText = findViewById(R.id.pHomePageText);
        mOpenURLInNewTab = findViewById(R.id.pOpenURLInNewTab);

        activityContextManager.getInstance().onStack(this);
        mSettingGeneralViewController = new settingGeneralViewController(this, new settingGeneralViewCallback(), mFullScreenMode, mThemeLight, mThemeDark, mThemeDefault, mHomePageText, mOpenURLInNewTab);
        mSettingGeneralModel = new settingGeneralModel(new settingGeneralModelCallback());
    }

    private void onInitTheme(){

        if(status.sTheme == enums.Theme.THEME_DARK){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            activityContextManager.getInstance().getHomeController().recreate();
        }else if(status.sTheme == enums.Theme.THEME_LIGHT){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            activityContextManager.getInstance().getHomeController().recreate();
        }else {
            if(!status.sDefaultNightMode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                activityContextManager.getInstance().getHomeController().recreate();
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                activityContextManager.getInstance().getHomeController().recreate();
            }
        }
    }

    /*View Callbacks*/

    private class settingGeneralViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if(settingGeneralEnums.eGeneralViewCallback.M_RESET_THEME_INVOKED_BACK.equals(e_type))
            {
                boolean mIsThemeChangable = false;
                if(status.sTheme == enums.Theme.THEME_DARK){
                    if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        mIsThemeChangable = true;
                    }
                }
                else if(status.sTheme == enums.Theme.THEME_LIGHT){
                    if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        mIsThemeChangable = true;
                    }
                }else {
                    if(!status.sDefaultNightMode){
                        if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO){
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            mIsThemeChangable = true;
                        }
                    }else {
                        if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES){
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            mIsThemeChangable = true;
                        }
                    }
                }

                if(mIsThemeChangable){
                    status.mThemeApplying = true;
                    onInitTheme();
                    onBackPressed();
                    overridePendingTransition(R.anim.fade_in_lang, R.anim.fade_out_lang);
                    activityContextManager.getInstance().getSettingController().onReInitTheme();
                    activityContextManager.getInstance().getHomeController().onReInitTheme();
                    helperMethod.openActivity(settingGeneralController.class, constants.CONST_LIST_HISTORY, settingGeneralController.this,true);
                }
            }
            return null;
        }
    }

    /*Model Callbacks*/

    private class settingGeneralModelCallback implements eventObserver.eventListener{

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
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        activityContextManager.getInstance().onRemoveStack(this);
        finish();
    }

    /*External Redirection*/

    public void onLanguageChanged(){
        setContentView(R.layout.setting_general_view);
    }

    /*UI Redirection*/

    public void onClose(View view){
        finish();
    }

    public void onManageLanguage(View view) {
        helperMethod.openActivity(languageController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onFullScreenBrowsing(View view){
        mSettingGeneralModel.onTrigger(settingGeneralEnums.eGeneralModel.M_FULL_SCREEN_BROWSING, Collections.singletonList(!mFullScreenMode.isChecked()));
        mFullScreenMode.toggle();
        activityContextManager.getInstance().getHomeController().onFullScreenSettingChanged();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FULL_SCREEN_BROWSIING,status.sFullScreenBrowsing));
    }

    public void onSelectTheme(View view){
        if(!mIsThemeChanging){
            mIsThemeChanging = true;
            if(view.getId() == R.id.pOption1){
                if(status.sTheme != enums.Theme.THEME_DARK) {
                    mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_RESET_THEME, null);
                    mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_SET_THEME, Collections.singletonList(enums.Theme.THEME_DARK));
                    mSettingGeneralModel.onTrigger(settingGeneralEnums.eGeneralModel.M_SELECT_THEME, Collections.singletonList(enums.Theme.THEME_DARK));
                    dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_THEME,status.sTheme));
                    mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_UPDATE_THEME_BLOCKER, Collections.singletonList(enums.Theme.THEME_DARK));
                    mIsThemeChanging = false;
                }else {
                    mIsThemeChanging = false;
                }
            }else if(view.getId() == R.id.pOption2) {
                if(status.sTheme != enums.Theme.THEME_LIGHT) {
                    mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_RESET_THEME, null);
                    mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_SET_THEME, Collections.singletonList(enums.Theme.THEME_LIGHT));
                    mSettingGeneralModel.onTrigger(settingGeneralEnums.eGeneralModel.M_SELECT_THEME, Collections.singletonList(enums.Theme.THEME_LIGHT));
                    dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_THEME,status.sTheme));
                    mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_UPDATE_THEME_BLOCKER, Collections.singletonList(enums.Theme.THEME_LIGHT));
                    mIsThemeChanging = false;
                }else {
                    mIsThemeChanging = false;
                }
            }else {
                if(status.sTheme != enums.Theme.THEME_DEFAULT) {
                    mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_RESET_THEME, null);
                    mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_SET_THEME, Collections.singletonList(enums.Theme.THEME_DEFAULT));
                    mSettingGeneralModel.onTrigger(settingGeneralEnums.eGeneralModel.M_SELECT_THEME, Collections.singletonList(enums.Theme.THEME_DEFAULT));
                    dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_THEME,status.sTheme));
                    mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_UPDATE_THEME_BLOCKER, Collections.singletonList(enums.Theme.THEME_DEFAULT));
                    mIsThemeChanging = false;
                }else {
                    mIsThemeChanging = false;
                }
            }
        }
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onURLInNewTab(View view) {
        mSettingGeneralModel.onTrigger(settingGeneralEnums.eGeneralModel.M_URL_NEW_TAB, Collections.singletonList(!mOpenURLInNewTab.isChecked()));
        mOpenURLInNewTab.toggle();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_OPEN_URL_IN_NEW_TAB,status.sOpenURLInNewTab));
    }

}
