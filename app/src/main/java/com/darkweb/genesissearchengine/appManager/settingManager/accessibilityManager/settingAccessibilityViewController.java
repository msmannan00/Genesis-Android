package com.darkweb.genesissearchengine.appManager.settingManager.accessibilityManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

class settingAccessibilityViewController
{
    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;

    private SwitchMaterial mZoom;
    private SwitchMaterial mVoiceInput;
    private SwitchMaterial mFontSizeAdjustable;
    private SeekBar mSeekBar;
    private TextView mSeekBarSample;
    private TextView mScalePercentage;

    /*Initializations*/

    settingAccessibilityViewController(settingAccessibilityController pContext, eventObserver.eventListener pEvent, SwitchMaterial pZoom, SwitchMaterial pVoiceInput, SwitchMaterial pFontSizeAdjustable, SeekBar pSeekBar, TextView mSeekBarSample, TextView pScalePercentage)
    {
        this.mEvent = pEvent;
        this.mContext = pContext;
        this.mZoom = pZoom;
        this.mVoiceInput = pVoiceInput;
        this.mFontSizeAdjustable = pFontSizeAdjustable;
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
            }
            else {
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.white));
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
        if(status.sSettingFontAdjustable){
            mFontSizeAdjustable.setChecked(true);
            mSeekBar.setProgress(5);
            mScalePercentage.setText("100%");
            mSeekBar.setEnabled(false);
            mSeekBar.setAlpha(0.5f);
            mScalePercentage.setAlpha(0.5f);
            mSeekBarSample.setAlpha(0.3f);
            disableFontManual();
        }else {
            mFontSizeAdjustable.setChecked(false);
            mSeekBar.setProgress((int)status.sSettingFontSize/10-5);
            int percentage = (int)status.sSettingFontSize;
            if(status.sSettingFontSize<100){
                mSeekBarSample.setTextSize((int)(12.0*percentage)/100);
            }else if(status.sSettingFontSize>100){
                mSeekBarSample.setTextSize((int)(12.0*percentage)/100);
            }

            mScalePercentage.setText(percentage+"%");
            mSeekBar.setAlpha(1f);
            mScalePercentage.setAlpha(1f);
            mSeekBarSample.setAlpha(1f);
            mSeekBar.setEnabled(true);
        }
    }

    public void disableFontManual(){
        mSeekBar.setProgress(5);
        mSeekBarSample.setTextSize(12);
        mScalePercentage.setText("100%");
        mSeekBar.setEnabled(false);
        mSeekBar.animate().setDuration(250).alpha(0.5f);
        mScalePercentage.animate().setDuration(250).alpha(0.5f);
        mSeekBarSample.animate().setDuration(250).alpha(0.3f);
    }

    public void enableFontManual(){
        mSeekBar.setProgress(5);
        mSeekBarSample.setTextSize(12);
        mScalePercentage.setText("100%");
        mSeekBar.setEnabled(true);
        mSeekBar.animate().setDuration(250).alpha(1f);
        mScalePercentage.animate().setDuration(250).alpha(1f);
        mSeekBarSample.animate().setDuration(250).alpha(1f);
    }
}
