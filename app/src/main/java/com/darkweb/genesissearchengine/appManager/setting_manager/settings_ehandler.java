package com.darkweb.genesissearchengine.appManager.setting_manager;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.darkweb.genesissearchengine.appManager.main_activity.app_model;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
import com.example.myapplication.R;

import static com.darkweb.genesissearchengine.constants.status.*;
import static com.darkweb.genesissearchengine.constants.status.search_status;

public class settings_ehandler
{
    /*Initializations*/

    private static final settings_ehandler ourInstance = new settings_ehandler();

    public static settings_ehandler getInstance()
    {
        return ourInstance;
    }

    private settings_ehandler()
    {
    }

    /*Listeners*/

    @SuppressWarnings("ConstantConditions")
    private void onJavaScriptListener(int position)
    {
        if(position==1 && java_status)
        {
            java_status = false;
            preference_manager.getInstance().setBool(keys.java_script, java_status);
            app_model.getInstance().getAppInstance().onReInitGeckoView();
        }
        else if(!java_status)
        {
            java_status = true;
            preference_manager.getInstance().setBool(keys.java_script, java_status);
            app_model.getInstance().getAppInstance().onReInitGeckoView();
        }
    }

    private void onSearchListner(AdapterView<?> parentView,int position)
    {
        if(!search_status.equals(parentView.getItemAtPosition(position).toString()))
        {
            search_status = parentView.getItemAtPosition(position).toString();
            preference_manager.getInstance().setString(keys.search_engine, search_status);
            app_model.getInstance().getAppInstance().initSearchEngine();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void onHistoryListener(int position)
    {
        if(position==1 && history_status)
        {
            history_status = false;
            preference_manager.getInstance().setBool(keys.history_clear, history_status);
        }
        else if(!java_status)
        {
            history_status = true;
            preference_manager.getInstance().setBool(keys.history_clear, history_status);
        }
    }

    void onBackPressed()
    {
        setting_model.getInstance().getSettingInstance().closeView();
    }

    /*Listener Initializations*/

    void onItemListnerInitialization(Spinner view)
    {
        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(selectedItemView.getId()== R.id.search_manager)
                {
                    settings_ehandler.getInstance().onJavaScriptListener(position);
                }
                else if(selectedItemView.getId()== R.id.javascript_manager)
                {
                    settings_ehandler.getInstance().onHistoryListener(position);
                }
                else if(selectedItemView.getId()== R.id.history_manager)
                {
                    settings_ehandler.getInstance().onSearchListner(parentView,position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

}
