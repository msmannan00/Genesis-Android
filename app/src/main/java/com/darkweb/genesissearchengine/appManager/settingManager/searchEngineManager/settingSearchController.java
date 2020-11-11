package com.darkweb.genesissearchengine.appManager.settingManager.searchEngineManager;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
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
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_search_view);

        viewsInitializations();
    }

    public void viewsInitializations() {
        mSearchEngines.add(findViewById(R.id.mRadioSearch_1));
        mSearchEngines.add(findViewById(R.id.mRadioSearch_2));
        mSearchEngines.add(findViewById(R.id.mRadioSearch_3));
        mSearchEngines.add(findViewById(R.id.mRadioSearch_4));
        mSearchEngines.add(findViewById(R.id.mRadioSearch_5));
        mSearchHistory = findViewById(R.id.pSearchHistory);
        mSearchSuggestions = findViewById(R.id.pSearchSuggestions);

        mSettingSearchViewController = new settingSearchViewController(this, new settingSearchViewCallback(), mSearchEngines, mSearchHistory, mSearchSuggestions);
        mSettingSearchModel = new settingSearchModel(new settingSearchModelCallback());
    }

    public void onSelectSearchEngine(View view) {
        if(view.getId() == R.id.pOption1){
            mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE, Collections.singletonList(constants.CONST_BACKEND_GENESIS_URL));
        }
        else if(view.getId() == R.id.pOption2){
            mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE, Collections.singletonList(constants.CONST_BACKEND_DUCK_DUCK_GO_URL));
        }
        else if(view.getId() == R.id.pOption3){
            mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE, Collections.singletonList(constants.CONST_BACKEND_GOOGLE_URL));
        }
        else if(view.getId() == R.id.pOption4){
            mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE, Collections.singletonList(constants.CONST_BACKEND_BING_URL));
        }
        else if(view.getId() == R.id.pOption5){
            mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE, Collections.singletonList(constants.CONST_BACKEND_WIKI_URL));
        }

        mSettingSearchViewController.onTrigger(settingSearchEnums.eSearchViewController.M_RESET_SEARCH_ENGINE, Collections.singletonList(null));
        mSettingSearchViewController.onTrigger(settingSearchEnums.eSearchViewController.M_INIT_SEARCH_ENGINE, Collections.singletonList(null));
    }

    public void setSearchHistory(View view){
        mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_HISTORY, Collections.singletonList(!mSearchHistory.isChecked()));
        mSearchHistory.toggle();
    }

    public void setSearchStatus(View view){
        mSettingSearchModel.onTrigger(settingSearchEnums.eSearchModel.M_SET_SEARCH_HISTORY, Collections.singletonList(!mSearchSuggestions.isChecked()));
        mSearchSuggestions.toggle();
    }

    /* LISTENERS */
    public class settingSearchViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            return null;
        }
    }


    public class settingSearchModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
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

}