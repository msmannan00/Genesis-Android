package com.darkweb.genesissearchengine.appManager.settingManager.privacyManager;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class settingPrivacyController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private settingPrivacyModel mSettingPrivacyModel;
    private settingPrivacyViewController mSettingPrivacyViewController;
    private SwitchMaterial mJavaScript;
    private SwitchMaterial mDoNotTrack;
    private SwitchMaterial mTrackingProtection;
    private SwitchMaterial mClearDataOnExit;
    private ArrayList<RadioButton> mCookie = new ArrayList<>();
    private boolean mSettingChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_privacy_view);

        viewsInitializations();
        initializeListeners();
    }

    public void viewsInitializations() {
        mJavaScript = findViewById(R.id.pJavascript);
        mDoNotTrack = findViewById(R.id.pDoNotTrack);
        mTrackingProtection = findViewById(R.id.pTrackingProtection);
        mClearDataOnExit = findViewById(R.id.pClearDataOnExit);
        mCookie.add(findViewById(R.id.pCookieRadioOption1));
        mCookie.add(findViewById(R.id.pCookieRadioOption2));
        mCookie.add(findViewById(R.id.pCookieRadioOption3));
        mCookie.add(findViewById(R.id.pCookieRadioOption4));

        mSettingPrivacyViewController = new settingPrivacyViewController(this, new settingPrivacyController.settingAccessibilityViewCallback(), mJavaScript, mDoNotTrack, mTrackingProtection, mClearDataOnExit, mCookie);
        mSettingPrivacyModel = new settingPrivacyModel(new settingPrivacyController.settingAccessibilityModelCallback());
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
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume()
    {
        if(mSettingChanged){
            activityContextManager.getInstance().setCurrentActivity(this);
        }
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(mSettingChanged){
            activityContextManager.getInstance().setCurrentActivity(this);
        }
        finish();
    }

    /*UI Redirection*/
    public void onClose(View view){
        activityContextManager.getInstance().getHomeController().initRuntimeSettings();
        finish();
    }

    public void onJavaScript(View view){
        mSettingChanged = true;
        mSettingPrivacyModel.onTrigger(settingPrivacyEnums.ePrivacyModel.M_SET_JAVASCRIPT, Collections.singletonList(!status.sSettingJavaStatus));
        mJavaScript.toggle();
    }

    public void onDoNotTrack(View view){
        mSettingChanged = true;
        mSettingPrivacyModel.onTrigger(settingPrivacyEnums.ePrivacyModel.SET_DONOT_TRACK, Collections.singletonList(!status.sStatusDoNotTrack));
        mDoNotTrack.toggle();
    }

    public void onTrackingProtection(View view){
        mSettingChanged = true;
        mSettingPrivacyModel.onTrigger(settingPrivacyEnums.ePrivacyModel.SET_TRACKING_PROTECTION, Collections.singletonList(!status.sSettingTrackingProtection));
        mTrackingProtection.toggle();
    }

    public void onCookies(View view){
        mSettingChanged = true;
        mSettingPrivacyViewController.onTrigger(settingPrivacyEnums.ePrivacyViewController.SET_COOKIE_STATUS, Collections.singletonList(view));
        mSettingPrivacyModel.onTrigger(settingPrivacyEnums.ePrivacyModel.SET_COOKIES, Collections.singletonList(view));
    }

    public void onClearPrivateData(View view){
        mSettingChanged = true;
        mSettingPrivacyModel.onTrigger(settingPrivacyEnums.ePrivacyModel.SET_CLEAR_PRIVATE_DATA, Collections.singletonList(!status.sClearOnExit));
        mClearDataOnExit.toggle();
    }

}
