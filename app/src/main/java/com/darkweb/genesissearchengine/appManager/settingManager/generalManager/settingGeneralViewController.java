package com.darkweb.genesissearchengine.appManager.settingManager.generalManager;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.Collections;
import java.util.List;

import static com.darkweb.genesissearchengine.appManager.settingManager.generalManager.settingGeneralEnums.eGeneralViewCallback.M_RESET_THEME_INVOKED_BACK;

class settingGeneralViewController
{
    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;
    private SwitchMaterial mFullScreenMode;
    private SwitchMaterial mOpenURLInNewTab;
    private RadioButton mThemeLight;
    private RadioButton mThemeDark;
    private RadioButton mThemeDefault;
    private TextView mHomePageText;

    /*Initializations*/


    settingGeneralViewController(settingGeneralController pContext, eventObserver.eventListener pEvent, SwitchMaterial pFullScreenMode, RadioButton pThemeLight, RadioButton pThemeDark, RadioButton pThemeDefault, TextView pHomePageText, SwitchMaterial pOpenURLInNewTab)
    {
        this.mEvent = pEvent;
        this.mContext = pContext;
        this.mFullScreenMode = pFullScreenMode;
        this.mThemeLight = pThemeLight;
        this.mThemeDark = pThemeDark;
        this.mThemeDefault = pThemeDefault;
        this.mHomePageText = pHomePageText;
        this.mOpenURLInNewTab = pOpenURLInNewTab;

        initViews();
        initPostUI();
    }


    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
            }
            else {
                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
                    mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
            }
        }
    }

    private void initViews()
    {
        resetThemeSelection();

        if(status.sTheme == enums.Theme.THEME_LIGHT){
            mThemeLight.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mThemeLight.setChecked(true);
        }else if(status.sTheme == enums.Theme.THEME_DARK){
            mThemeDark.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mThemeDark.setChecked(true);
        }else{
            mThemeDefault.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mThemeDefault.setChecked(true);
        }

        if(status.sFullScreenBrowsing){
            mFullScreenMode.setChecked(true);
        }else {
            mFullScreenMode.setChecked(false);
        }

        if(status.sOpenURLInNewTab){
            mOpenURLInNewTab.setChecked(true);
        }else {
            mOpenURLInNewTab.setChecked(false);
        }

        mHomePageText.setText(helperMethod.getDomainName(status.sSettingSearchStatus.replace("boogle.store","genesis.onion")));
    }

    private void updateThemeChanger(){
        mEvent.invokeObserver(Collections.singletonList(status.sTheme), M_RESET_THEME_INVOKED_BACK);
    }

    private void resetThemeSelection(){
        mThemeDark.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint_default)));
        mThemeLight.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint_default)));
        mThemeDefault.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint_default)));

        mThemeDark.setChecked(false);
        mThemeLight.setChecked(false);
        mThemeDefault.setChecked(false);
    }

    private void setTheme(int pTheme){

        if(pTheme == enums.Theme.THEME_LIGHT){
            mThemeLight.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mThemeLight.setChecked(true);
        }else if(pTheme == enums.Theme.THEME_DARK){
            mThemeDark.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mThemeDark.setChecked(true);
        }else{
            mThemeDefault.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mThemeDefault.setChecked(true);
        }
    }

    public Object onTrigger(settingGeneralEnums.eGeneralViewController pCommands, List<Object> pData){
        if(pCommands.equals(settingGeneralEnums.eGeneralViewController.M_SET_THEME)){
            setTheme((int)pData.get(0));
        }
        else if(pCommands.equals(settingGeneralEnums.eGeneralViewController.M_RESET_THEME)){
            resetThemeSelection();
        }
        else if(pCommands.equals(settingGeneralEnums.eGeneralViewController.M_UPDATE_THEME_BLOCKER)){
            updateThemeChanger();
        }
        return null;
    }

}
