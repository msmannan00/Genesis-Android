package com.darkweb.genesissearchengine.appManager.settingManager.generalManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.languageManager.languageController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        onInitTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_general_view);

        viewsInitializations();
        initializeListeners();
    }

    public void viewsInitializations() {

        mFullScreenMode = findViewById(R.id.pFullScreenMode);
        mThemeLight = findViewById(R.id.pThemeLight);
        mThemeDark = findViewById(R.id.pThemeDark);
        mThemeDefault = findViewById(R.id.pThemeDefault);
        mHomePageText = findViewById(R.id.pHomePageText);
        mOpenURLInNewTab = findViewById(R.id.pOpenURLInNewTab);

        mSettingGeneralViewController = new settingGeneralViewController(this, new settingGeneralViewCallback(), mFullScreenMode, mThemeLight, mThemeDark, mThemeDefault, mHomePageText, mOpenURLInNewTab);
        mSettingGeneralModel = new settingGeneralModel(new settingGeneralModelCallback());
    }

    public void onInitTheme(){

        if(status.sTheme == enums.Theme.THEME_DARK){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else if(status.sTheme == enums.Theme.THEME_LIGHT){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else {
            if(!status.sDefaultNightMode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
    }

    /* LISTENERS */
    public class settingGeneralViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            if(enums.etype.M_RESET_THEME_INVOKED_BACK.equals(e_type))
            {
                helperMethod.restartActivity(getIntent(), settingGeneralController.this);
                new Handler().postDelayed(() -> {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    activityContextManager.getInstance().getSettingController().applyTheme();
                }, 150);
            }
            return null;
        }
    }


    public class settingGeneralModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            return null;
        }
    }

    public void initializeListeners(){
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume()
    {
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
        finish();
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
    }

    public void onSelectTheme(View view){
        if(view.getId() == R.id.pOption1){
            if(status.sTheme != enums.Theme.THEME_DARK) {
                mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_RESET_THEME, null);
                mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_SET_THEME, Collections.singletonList(enums.Theme.THEME_DARK));
                mSettingGeneralModel.onTrigger(settingGeneralEnums.eGeneralModel.M_SELECT_THEME, Collections.singletonList(enums.Theme.THEME_DARK));
                mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_UPDATE_THEME_BLOCKER, Collections.singletonList(enums.Theme.THEME_DARK));
            }
        }else if(view.getId() == R.id.pOption2) {
            if(status.sTheme != enums.Theme.THEME_LIGHT) {
                mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_RESET_THEME, null);
                mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_SET_THEME, Collections.singletonList(enums.Theme.THEME_LIGHT));
                mSettingGeneralModel.onTrigger(settingGeneralEnums.eGeneralModel.M_SELECT_THEME, Collections.singletonList(enums.Theme.THEME_LIGHT));
                mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_UPDATE_THEME_BLOCKER, Collections.singletonList(enums.Theme.THEME_LIGHT));
            }
        }else {
            if(status.sTheme != enums.Theme.THEME_DEFAULT) {
                mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_RESET_THEME, null);
                mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_SET_THEME, Collections.singletonList(enums.Theme.THEME_DEFAULT));
                mSettingGeneralModel.onTrigger(settingGeneralEnums.eGeneralModel.M_SELECT_THEME, Collections.singletonList(enums.Theme.THEME_DEFAULT));
                mSettingGeneralViewController.onTrigger(settingGeneralEnums.eGeneralViewController.M_UPDATE_THEME_BLOCKER, Collections.singletonList(enums.Theme.THEME_DEFAULT));
            }
        }
    }

    public void onURLInNewTab(View view) {
        mSettingGeneralModel.onTrigger(settingGeneralEnums.eGeneralModel.M_URL_NEW_TAB, Collections.singletonList(!mOpenURLInNewTab.isChecked()));
        mOpenURLInNewTab.toggle();
    }

}
