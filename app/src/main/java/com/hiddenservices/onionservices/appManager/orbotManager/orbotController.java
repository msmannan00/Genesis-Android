package com.hiddenservices.onionservices.appManager.orbotManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.bridgeManager.bridgeController;
import com.hiddenservices.onionservices.appManager.helpManager.helpController;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.status;
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

public class orbotController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private com.hiddenservices.onionservices.appManager.orbotManager.orbotModel mOrbotModel;
    private com.hiddenservices.onionservices.appManager.orbotManager.orbotViewController mOrbotViewController;

    private SwitchMaterial mOrbotSettingBridgeSwitch;
    private LinearLayout mOrbotSettingWarning;

    /* INITIALIZATIONS */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.orbot_settings_view);
        initializeStartupAnimation();

        if (!status.mThemeApplying) {
            activityContextManager.getInstance().onStack(this);
        }

        viewsInitializations();
        onInitListener();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);

        if (activityThemeManager.getInstance().onInitTheme(this)) {
            activityContextManager.getInstance().onResetTheme();
        }

        super.onConfigurationChanged(newConfig);
    }

    public void viewsInitializations() {
        mOrbotSettingBridgeSwitch = findViewById(R.id.pOrbotSettingBridgeSwitch);
        mOrbotSettingWarning = findViewById(R.id.pOrbotSettingWarning);

        mOrbotViewController = new orbotViewController(mOrbotSettingBridgeSwitch, this, mOrbotSettingWarning);
        mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.M_INIT_UI, Arrays.asList(status.sBridgeStatus, status.sSnowFlakesStatus));
        mOrbotViewController.onInit();

        mOrbotModel = new orbotModel(new orbotModelCallback());
        mOrbotModel.onInit();
    }

    /* LISTENERS */

    private void onInitListener() {
    }

    public class orbotModelCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> pData, Object pCommands) {
            return null;
        }
    }

    public void onUITriggered(View view) {
        if (view.getId() == R.id.pOrbotSettingBridge) {
            mOrbotModel.onTrigger(orbotEnums.eOrbotModelCommands.M_BRIDGE_SWITCH, Collections.singletonList(!mOrbotSettingBridgeSwitch.isChecked()));
            mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.M_UPDATE_BRIDGE_SETTINGS_VIEWS, Collections.singletonList(status.sBridgeStatus));
        } else if (view.getId() == R.id.pOrbotSettingWarning) {
            helperMethod.openActivity(bridgeController.class, constants.CONST_LIST_HISTORY, orbotController.this, true);
        }
    }

    public void onClose(View view) {
        finish();
        initializeStartupAnimation();
        activityContextManager.getInstance().onRemoveStack(this);
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this, true);
    }

    public void initializeStartupAnimation() {
        if (activityContextManager.getInstance().getHomeController()!=null && activityContextManager.getInstance().getHomeController().isSplashScreenLoading()) {
            overridePendingTransition(R.anim.translate_fade_left_reverse, R.anim.translate_fade_right_reverse);
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
        super.onBackPressed();
    }

}