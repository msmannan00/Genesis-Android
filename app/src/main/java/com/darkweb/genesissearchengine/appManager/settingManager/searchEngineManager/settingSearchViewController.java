package com.darkweb.genesissearchengine.appManager.settingManager.searchEngineManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

class settingSearchViewController
{
    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;
    private ArrayList<RadioButton> mSearchEngines;
    private SwitchMaterial mSearchHistory;
    private SwitchMaterial mSearchSuggestions;

    /*Initializations*/

    settingSearchViewController(settingSearchController pContext, eventObserver.eventListener pEvent, ArrayList<RadioButton> pSearchEngines, SwitchMaterial pSearchHistory, SwitchMaterial pSearchSuggestions)
    {
        this.mEvent = pEvent;
        this.mContext = pContext;
        this.mSearchEngines = pSearchEngines;
        this.mSearchHistory = pSearchHistory;
        this.mSearchSuggestions = pSearchSuggestions;

        initViews();
        initPostUI();
    }

    private void initViews()
    {
        initSearchEngine();
        initSearchViews();
    }

    public void initSearchViews(){
        if(status.sSettingSearchHistory){
            mSearchHistory.setChecked(true);
        }
        if(status.getsSettingSearchSuggestion){
            mSearchSuggestions.setChecked(true);
        }
    }

    public void initSearchEngine(){
        switch (status.sSettingSearchStatus) {
            case constants.CONST_BACKEND_GOOGLE_URL:
                mSearchEngines.get(2).setChecked(true);
                break;
            case constants.CONST_BACKEND_GENESIS_URL:
                mSearchEngines.get(0).setChecked(true);
                break;
            case constants.CONST_BACKEND_DUCK_DUCK_GO_URL:
                mSearchEngines.get(1).setChecked(true);
                break;
            case constants.CONST_BACKEND_BING_URL:
                mSearchEngines.get(3).setChecked(true);
                break;
            case constants.CONST_BACKEND_WIKI_URL:
                mSearchEngines.get(4).setChecked(true);
                break;
        }
    }

    public void resetSearchEngine(){
        for(int mCounter=0;mCounter<mSearchEngines.size();mCounter++){
            mSearchEngines.get(mCounter).setChecked(false);
        }
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
            }
            else {
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.white));
            }
        }
    }

}
