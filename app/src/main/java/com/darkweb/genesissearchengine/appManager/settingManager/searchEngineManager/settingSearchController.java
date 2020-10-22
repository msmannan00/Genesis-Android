package com.darkweb.genesissearchengine.appManager.settingManager.searchEngineManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

import java.util.List;

public class settingSearchController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
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
        mSettingSearchViewController = new settingSearchViewController(this, new settingSearchViewCallback());

        mSettingSearchModel = new settingSearchModel(new settingSearchModelCallback());
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