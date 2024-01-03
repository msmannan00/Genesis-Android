package com.hiddenservices.onionservices.appManager.settingManager.accessibilityManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.helpManager.helpController;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.dataManager.dataController;
import com.hiddenservices.onionservices.dataManager.dataEnums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.appManager.activityThemeManager;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import com.hiddenservices.onionservices.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class settingAccessibilityController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private settingAccessibilityModel mSettingAccessibilityModel;
    private settingAccessibilityViewController mSettingAccessibilityViewController;
    private SwitchMaterial mZoom;
    private SwitchMaterial mVoiceInput;
    private SeekBar mSeekBar;
    private TextView mSeekBarSample;
    private TextView mScalePercentage;

    /* PRIVATE LOCAL VARIABLES */

    private boolean mIsSettingChanged = false;
    private float mDefaultFontSize = status.sSettingFontSize;

    /* Initializations */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting_accessibility_view);

        viewsInitializations();
        initializeListeners();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onConfigurationChanged(newConfig);
        if (newConfig.uiMode != getResources().getConfiguration().uiMode) {
            activityContextManager.getInstance().onResetTheme();
            activityThemeManager.getInstance().onConfigurationChanged(this);
        }
    }

    private void viewsInitializations() {
        mZoom = findViewById(R.id.pZoom);
        mVoiceInput = findViewById(R.id.pVoiceInput);
        mSeekBar = findViewById(R.id.pSeekBar);
        mSeekBarSample = findViewById(R.id.pSeekBarSample);
        mScalePercentage = findViewById(R.id.pScalePercentage);

        activityContextManager.getInstance().onStack(this);
        mSettingAccessibilityViewController = new settingAccessibilityViewController(this, new settingAccessibilityController.settingAccessibilityViewCallback(), mZoom, mVoiceInput, mSeekBar, mSeekBarSample, mScalePercentage);
        mSettingAccessibilityViewController.onInit();

        mSettingAccessibilityModel = new settingAccessibilityModel(new settingAccessibilityController.settingAccessibilityModelCallback());
        mSettingAccessibilityModel.onInit();
    }

    private void initializeListeners() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mIsSettingChanged = true;
                int percentage = ((progress + 5) * 10);
                mSettingAccessibilityViewController.onTrigger(settingAccessibilityEnums.eAccessibilityModel.M_UPDATE_SAMPLE_TEXT, Collections.singletonList((int) ((12.0 * percentage) / 100)));
                mSettingAccessibilityViewController.onTrigger(settingAccessibilityEnums.eAccessibilityModel.M_UPDATE_PERCENTAGE, Collections.singletonList((percentage + constants.CONST_PERCENTAGE_SIGN)));

                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_FONT_SIZE, percentage));
                status.sSettingFontSize = percentage;
                activityContextManager.getInstance().getHomeController().onLoadFont();
            }
        });
    }

    /*View Callbacks*/

    private class settingAccessibilityViewCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            return null;
        }
    }

    /*Model Callbacks*/

    private class settingAccessibilityModelCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            return null;
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume() {
        activityContextManager.getInstance().onCheckPurgeStack();
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
    }

    /*UI Redirection*/

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this, true);
    }

    public void onClose(View view) {
        activityContextManager.getInstance().onRemoveStack(this);
        finish();

        if (mIsSettingChanged && mDefaultFontSize != status.sSettingFontSize) {
            activityContextManager.getInstance().getHomeController().initRuntimeSettings();
            activityContextManager.getInstance().getHomeController().onReload(null);
        }
    }

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    public void onZoomSettingUpdate(View view) {
        mSettingAccessibilityModel.onTrigger(settingAccessibilityEnums.eAccessibilityViewController.M_ZOOM_SETTING, Collections.singletonList(!mZoom.isChecked()));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_ZOOM, status.sSettingEnableZoom));
        mZoom.toggle();

        new Handler().postDelayed(() ->
        {
            activityContextManager.getInstance().getHomeController().onReload(null);
        }, 300);

    }

    public void onVoiceInputSettingUpdate(View view) {
        mSettingAccessibilityModel.onTrigger(settingAccessibilityEnums.eAccessibilityViewController.M_VOICE_INPUT_SETTING, Collections.singletonList(!mVoiceInput.isChecked()));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_VOICE_INPUT, status.sSettingEnableVoiceInput));
        mVoiceInput.toggle();
    }

}
