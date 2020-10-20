package com.darkweb.genesissearchengine.appManager.orbotManager;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bridgeManager.bridgeController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.Collections;

public class orbotController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private SwitchMaterial mBridgeSwitch;
    private SwitchMaterial mVpnSwitch;
    private orbotViewController mOrbotViewController;
    private LinearLayout mCustomizableBridgeMenu;

    /* INITIALIZATIONS */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orbot_settings_view);

        viewsInitializations();
    }

    public void viewsInitializations() {
        mBridgeSwitch = findViewById(R.id.pBridgeSwitch);
        mVpnSwitch = findViewById(R.id.pVpnSwitch);
        mCustomizableBridgeMenu = findViewById(R.id.pCustomizableBridgeMenu);

        mOrbotViewController = new orbotViewController(mBridgeSwitch, mVpnSwitch, this, mCustomizableBridgeMenu);
        mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.S_INIT_UI, Arrays.asList(status.sBridgeVPNStatus,status.sBridgeStatus));
    }

    /* LISTENERS */

    public void onBridgeSwitch(View view){
        status.sBridgeStatus = !mBridgeSwitch.isChecked();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_BRIDGE_ENABLES,status.sBridgeStatus));
        pluginController.getInstance().updateBridges(status.sBridgeStatus);
        mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.S_UPDATE_BRIDGE_SETTINGS_VIEWS, Collections.singletonList(status.sBridgeStatus));
    }

    public void openBridgeSettings(View view){
        helperMethod.openActivity(bridgeController.class, constants.CONST_LIST_HISTORY, orbotController.this, true);
    }

    public void onVPNSwitch(View view){
        status.sBridgeVPNStatus = !mVpnSwitch.isChecked();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_VPN_ENABLED,status.sBridgeVPNStatus));
        mOrbotViewController.updateVPN(status.sBridgeVPNStatus);
        pluginController.getInstance().updateVPN(status.sBridgeVPNStatus);
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume()
    {
        activityContextManager.getInstance().setCurrentActivity(this);
        mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.S_INIT_POST_UI,null);
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

    public void onClose(View view){
        finish();
    }
}