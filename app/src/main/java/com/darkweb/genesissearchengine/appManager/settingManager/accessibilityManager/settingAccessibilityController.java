package com.darkweb.genesissearchengine.appManager.settingManager.accessibilityManager;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.List;

public class settingAccessibilityController  extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private settingAccessibilityModel mSettingAccessibilityModel;
    private settingAccessibilityViewController mSettingAccessibilityViewController;
    private SwitchMaterial mZoom;
    private SwitchMaterial mVoiceInput;
    private SwitchMaterial mFontSizeAdjustable;
    private SeekBar mSeekBar;
    private TextView mSeekBarSample;
    private TextView mScalePercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_accessibility_view);

        viewsInitializations();
        initializeListeners();
    }

    public void viewsInitializations() {
        mZoom = findViewById(R.id.pZoom);
        mVoiceInput = findViewById(R.id.pVoiceInput);
        mFontSizeAdjustable = findViewById(R.id.pFontSizeAdjustable);
        mSeekBar = findViewById(R.id.pSeekBar);
        mSeekBarSample = findViewById(R.id.pSeekBarSample);
        mScalePercentage = findViewById(R.id.pScalePercentage);

        mSettingAccessibilityViewController = new settingAccessibilityViewController(this, new settingAccessibilityController.settingAccessibilityViewCallback(), mZoom, mVoiceInput, mFontSizeAdjustable, mSeekBar, mSeekBarSample, mScalePercentage);
        mSettingAccessibilityModel = new settingAccessibilityModel(new settingAccessibilityController.settingAccessibilityModelCallback());
    }

    /* LISTENERS */
    public class settingAccessibilityViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            return null;
        }
    }


    public class settingAccessibilityModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            return null;
        }
    }

    public void initializeListeners(){
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                int percentage = ((progress+5)*10);
                mSeekBarSample.setTextSize((int)((12.0*percentage)/100));
                mScalePercentage.setText(percentage+"%");
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_FONT_SIZE,percentage));
                status.sSettingFontSize = percentage;
                activityContextManager.getInstance().getHomeController().onLoadFont();
            }
        });
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

    public void onZoomSettingUpdate(View view){
        mSettingAccessibilityModel.onZoomSettingUpdate(!mZoom.isChecked());
        mZoom.toggle();
    }

    public void onVoiceInputSettingUpdate(View view){
        mSettingAccessibilityModel.onVoiceInputSettingUpdate(!mVoiceInput.isChecked());
        mVoiceInput.toggle();
    }

    public void onFontSizeAdjustableUpdate(View view){
        mSettingAccessibilityModel.onFontSizeAdjustableUpdate(!mFontSizeAdjustable.isChecked());
        mFontSizeAdjustable.toggle();

        if(!mFontSizeAdjustable.isChecked()){
            mSettingAccessibilityViewController.enableFontManual();
        }else {
            mSettingAccessibilityViewController.disableFontManual();
        }
        activityContextManager.getInstance().getHomeController().onLoadFont();
    }

}
