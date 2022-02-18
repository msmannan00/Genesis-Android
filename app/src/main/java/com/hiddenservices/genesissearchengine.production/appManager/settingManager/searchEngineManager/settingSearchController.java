package com.hiddenservices.genesissearchengine.production.appManager.settingManager.searchEngineManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.genesissearchengine.production.appManager.activityContextManager;
import com.hiddenservices.genesissearchengine.production.appManager.helpManager.helpController;
import com.hiddenservices.genesissearchengine.production.constants.constants;
import com.hiddenservices.genesissearchengine.production.constants.keys;
import com.hiddenservices.genesissearchengine.production.constants.status;
import com.hiddenservices.genesissearchengine.production.dataManager.dataController;
import com.hiddenservices.genesissearchengine.production.dataManager.dataEnums;
import com.hiddenservices.genesissearchengine.production.eventObserver;
import com.hiddenservices.genesissearchengine.production.helperManager.helperMethod;
import com.hiddenservices.genesissearchengine.production.appManager.activityThemeManager;
import com.hiddenservices.genesissearchengine.production.pluginManager.pluginController;
import com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class settingSearchController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private ArrayList<RadioButton> mSearchEngines = new ArrayList<>();
    private SwitchMaterial mSearchHistory;
    private SwitchMaterial mSearchSuggestions;
    private settingSearchModel mSettingSearchModel;
    private settingSearchViewController mSettingSearchViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_search_view);

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

    private void viewsInitializations() {
        mSearchEngines.add(findViewById(R.id.mRadioSearch_1));
        mSearchEngines.add(findViewById(R.id.mRadioSearch_2));
        mSearchEngines.add(findViewById(R.id.mRadioSearch_3));
        mSearchEngines.add(findViewById(R.id.mRadioSearch_4));
        mSearchEngines.add(findViewById(R.id.mRadioSearch_5));
        mSearchHistory = findViewById(R.id.pSearchHistory);
        mSearchSuggestions = findViewById(R.id.pSearchSuggestions);

        activityContextManager.getInstance().onStack(this);
        mSettingSearchViewController = new settingSearchViewController(this, new settingSearchViewCallback(), mSearchEngines, mSearchHistory, mSearchSuggestions);
        mSettingSearchModel = new settingSearchModel(new settingSearchModelCallback());
    }

    /*View Callbacks*/

    private class settingSearchViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /*Model Callbacks*/

    private class settingSearchModelCallback implements eventObserver.eventListener{

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

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    /*UI Redirection*/

    public void onClose(View view){
        finish();
    }

    public void onSelectSearchEngine(View view) {
        if(view.getId() == R.id.pBridgeSettingOption1){
            mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE, Collections.singletonList(constants.CONST_BACKEND_GENESIS_URL));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE, status.sSettingDefaultSearchEngine));
        }
        else if(view.getId() == R.id.pBridgeSettingOption2){
            mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE, Collections.singletonList(constants.CONST_BACKEND_DUCK_DUCK_GO_URL));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE, status.sSettingDefaultSearchEngine));
        }
        else if(view.getId() == R.id.pOption3){
            mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE, Collections.singletonList(constants.CONST_BACKEND_GOOGLE_URL));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE, status.sSettingDefaultSearchEngine));
        }
        else if(view.getId() == R.id.pOption4){
            mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE, Collections.singletonList(constants.CONST_BACKEND_BING_URL));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE, status.sSettingDefaultSearchEngine));
        }
        else if(view.getId() == R.id.pOption5){
            mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE, Collections.singletonList(constants.CONST_BACKEND_WIKI_URL));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE, status.sSettingDefaultSearchEngine));
        }

        mSettingSearchViewController.onTrigger(settingSearchEnums.eSearchViewController.M_RESET_SEARCH_ENGINE, Collections.singletonList(null));
        mSettingSearchViewController.onTrigger(settingSearchEnums.eSearchViewController.M_INIT_SEARCH_ENGINE, Collections.singletonList(null));
    }

    public void setSearchHistory(View view){
        mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_HISTORY, Collections.singletonList(!mSearchHistory.isChecked()));
        mSearchHistory.toggle();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_HISTORY, status.sSettingSearchHistory));
    }

    public void setSuggestionStatus(View view){
        mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_SUGGESTION_STATUS, Collections.singletonList(!mSearchSuggestions.isChecked()));
        mSearchSuggestions.toggle();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_SUGGESTION, status.sSearchSuggestionStatus));
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

}