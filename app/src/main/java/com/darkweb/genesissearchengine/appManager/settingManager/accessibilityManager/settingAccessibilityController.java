package com.darkweb.genesissearchengine.appManager.settingManager.accessibilityManager;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.constants;
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
import java.util.Collections;
import java.util.List;

public class settingAccessibilityController  extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private settingAccessibilityModel mSettingAccessibilityModel;
    private settingAccessibilityViewController mSettingAccessibilityViewController;
    private SwitchMaterial mZoom;
    private SwitchMaterial mVoiceInput;
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
        mSeekBar = findViewById(R.id.pSeekBar);
        mSeekBarSample = findViewById(R.id.pSeekBarSample);
        mScalePercentage = findViewById(R.id.pScalePercentage);

        mSettingAccessibilityViewController = new settingAccessibilityViewController(this, new settingAccessibilityController.settingAccessibilityViewCallback(), mZoom, mVoiceInput, mSeekBar, mSeekBarSample, mScalePercentage);
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
                mSettingAccessibilityViewController.onTrigger(settingAccessibilityEnums.eAccessibilityModel.M_UPDATE_SAMPLE_TEXT, Collections.singletonList((int)((12.0*percentage)/100)));
                mSettingAccessibilityViewController.onTrigger(settingAccessibilityEnums.eAccessibilityModel.M_UPDATE_PERCENTAGE, Collections.singletonList((percentage+ constants.CONST_PERCENTAGE_SIGN)));

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
        mSettingAccessibilityModel.onTrigger(settingAccessibilityEnums.eAccessibilityViewController.M_ZOOM_SETTING, Collections.singletonList(!mZoom.isChecked()));
        mZoom.toggle();
    }

    public void onVoiceInputSettingUpdate(View view){
        mSettingAccessibilityModel.onTrigger(settingAccessibilityEnums.eAccessibilityViewController.M_VOICE_INPUT_SETTING, Collections.singletonList(!mVoiceInput.isChecked()));
        mVoiceInput.toggle();
    }

}
