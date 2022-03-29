package com.hiddenservices.onionservices.appManager.settingManager.accessibilityManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

class settingAccessibilityViewController
{
    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;

    private SwitchMaterial mZoom;
    private SwitchMaterial mVoiceInput;
    private SeekBar mSeekBar;
    private TextView mSeekBarSample;
    private TextView mScalePercentage;

    /*Initializations*/

    settingAccessibilityViewController(settingAccessibilityController pContext, eventObserver.eventListener pEvent, SwitchMaterial pZoom, SwitchMaterial pVoiceInput, SeekBar pSeekBar, TextView mSeekBarSample, TextView pScalePercentage)
    {
        this.mEvent = pEvent;
        this.mContext = pContext;
        this.mZoom = pZoom;
        this.mVoiceInput = pVoiceInput;
        this.mSeekBar = pSeekBar;
        this.mSeekBarSample = mSeekBarSample;
        this.mScalePercentage = pScalePercentage;

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
        if(status.sSettingEnableZoom){
            mZoom.setChecked(true);
        }else {
            mZoom.setChecked(false);
        }
        if(status.sSettingEnableVoiceInput){
            mVoiceInput.setChecked(true);
        }else {
            mVoiceInput.setChecked(false);
        }

        mSeekBar.setProgress((int)status.sSettingFontSize/10-5);
        float percentage = status.sSettingFontSize;
        if(status.sSettingFontSize<100){
            mSeekBarSample.setTextSize((int)((12*percentage)/100));
        }else if(status.sSettingFontSize>100){
            mSeekBarSample.setTextSize((int)((12*percentage)/100));
        }

        mScalePercentage.setText(((int)percentage + constants.CONST_PERCENTAGE_SIGN));
        mSeekBar.setAlpha(1f);
        mScalePercentage.setAlpha(1f);
        mSeekBarSample.setAlpha(1f);
        mSeekBar.setEnabled(true);
    }

    private void updateSampleTextSize(int pText){
        mSeekBarSample.setTextSize(pText);
    }

    private void updatePercentage(String pText){
        mScalePercentage.setText(pText);
    }

    public Object onTrigger(settingAccessibilityEnums.eAccessibilityModel pCommands, List<Object> pData){
        if(pCommands.equals(settingAccessibilityEnums.eAccessibilityModel.M_UPDATE_SAMPLE_TEXT)){
            updateSampleTextSize((int)pData.get(0));
        }
        else if(pCommands.equals(settingAccessibilityEnums.eAccessibilityModel.M_UPDATE_PERCENTAGE)){
            updatePercentage((String)pData.get(0));
        }
        return null;
    }

}
