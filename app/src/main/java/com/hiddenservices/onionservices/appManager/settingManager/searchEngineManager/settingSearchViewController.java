package com.hiddenservices.onionservices.appManager.settingManager.searchEngineManager;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.List;

class settingSearchViewController {
    /*Private Variables*/

    private AppCompatActivity mContext;
    private ArrayList<RadioButton> mSearchEngines;
    private SwitchMaterial mSearchHistory;
    private SwitchMaterial mSearchSuggestions;
    private LinearLayout mSearchSettingOption1;

    /*Initializations*/

    settingSearchViewController(settingSearchController pContext, eventObserver.eventListener ignoredPEvent, ArrayList<RadioButton> pSearchEngines, SwitchMaterial pSearchHistory, SwitchMaterial pSearchSuggestions, LinearLayout pSearchSettingOption1) {
        this.mContext = pContext;
        this.mSearchEngines = pSearchEngines;
        this.mSearchHistory = pSearchHistory;
        this.mSearchSuggestions = pSearchSuggestions;
        this.mSearchSettingOption1 = pSearchSettingOption1;
    }

    protected void onInit(){
        initViews();
        initPostUI();
    }

    private void initViews() {
        initSearchEngine();
        initSearchViews();
    }

    private void initPostUI() {
        Window window = mContext.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.blue_dark));
            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
        } else {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
        }

        if (!status.sTorBrowsing) {
            mSearchSettingOption1.setAlpha(0.3f);
        }

    }

    private void initSearchViews() {
        if (status.sSettingSearchHistory) {
            mSearchHistory.setChecked(true);
        }
        if (status.sSearchSuggestionStatus) {
            mSearchSuggestions.setChecked(true);
        } else {
            mSearchHistory.setChecked(false);
        }
    }

    private void initSearchEngine() {
        resetSearchEngine();
        switch (status.sSettingDefaultSearchEngine) {
            case constants.CONST_BACKEND_GOOGLE_URL:
                mSearchEngines.get(2).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
                mSearchEngines.get(2).setChecked(true);
                break;
            case constants.CONST_BACKEND_GENESIS_URL:
                mSearchEngines.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
                mSearchEngines.get(0).setChecked(true);
                break;
            case constants.CONST_BACKEND_DUCK_DUCK_GO_URL:
                mSearchEngines.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
                mSearchEngines.get(1).setChecked(true);
                break;
            case constants.CONST_BACKEND_BING_URL:
                mSearchEngines.get(3).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
                mSearchEngines.get(3).setChecked(true);
                break;
            case constants.CONST_BACKEND_WIKI_URL:
                mSearchEngines.get(4).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
                mSearchEngines.get(4).setChecked(true);
                break;
        }
    }

    private void resetSearchEngine() {
        for (int mCounter = 0; mCounter < mSearchEngines.size(); mCounter++) {
            mSearchEngines.get(mCounter).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint_default)));
            mSearchEngines.get(mCounter).setChecked(false);
        }
    }

    public Object onTrigger(settingSearchEnums.eSearchViewController pCommands, List<Object> ignoredPData) {
        if (pCommands.equals(settingSearchEnums.eSearchViewController.M_INIT_SEARCH_ENGINE)) {
            initSearchEngine();
        } else if (pCommands.equals(settingSearchEnums.eSearchViewController.M_RESET_SEARCH_ENGINE)) {
            resetSearchEngine();
        }
        return null;
    }

}
