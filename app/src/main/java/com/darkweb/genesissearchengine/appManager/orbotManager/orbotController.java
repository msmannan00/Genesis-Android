package com.darkweb.genesissearchengine.appManager.orbotManager;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bridgeManager.bridgeController;
import com.darkweb.genesissearchengine.appManager.helpManager.helpController;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
import com.darkweb.genesissearchengine.appManager.orbotLogManager.orbotLogController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class orbotController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private com.darkweb.genesissearchengine.appManager.orbotManager.orbotModel mOrbotModel;
    private com.darkweb.genesissearchengine.appManager.orbotManager.orbotViewController mOrbotViewController;

    private SwitchMaterial mBridgeSwitch;
    private SwitchMaterial mVpnSwitch;
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

        mOrbotViewController = new com.darkweb.genesissearchengine.appManager.orbotManager.orbotViewController(mBridgeSwitch, mVpnSwitch, this, mCustomizableBridgeMenu);
        mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.M_INIT_UI, Arrays.asList(status.sBridgeVPNStatus,status.sBridgeStatus));
        mOrbotModel = new com.darkweb.genesissearchengine.appManager.orbotManager.orbotModel(new orbotModelCallback());
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    /* LISTENERS */

    public class orbotModelCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    public void onBridgeSwitch(View view){
        mOrbotModel.onTrigger(orbotEnums.eOrbotModelCommands.M_BRIDGE_SWITCH,Collections.singletonList(!mBridgeSwitch.isChecked()));
        mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.M_UPDATE_BRIDGE_SETTINGS_VIEWS, Collections.singletonList(status.sBridgeStatus));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_BRIDGE_ENABLES,status.sBridgeVPNStatus));
    }

    public void openBridgeSettings(View view){
        helperMethod.openActivity(bridgeController.class, constants.CONST_LIST_HISTORY, orbotController.this, true);
    }

    public void onVPNSwitch(View view){
        mOrbotModel.onTrigger(orbotEnums.eOrbotModelCommands.M_VPN_SWITCH,Collections.singletonList(!mVpnSwitch.isChecked()));
        mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.M_UPDATE_VPN,Collections.singletonList(status.sBridgeVPNStatus));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_VPN_ENABLED,status.sBridgeVPNStatus));
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume()
    {
        activityContextManager.getInstance().setCurrentActivity(this);
        mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.M_INIT_POST_UI,null);
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

    float oldTouchValue;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        int action=event.getAction();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                oldTouchValue = event.getX();
            case (MotionEvent.ACTION_MOVE):
                float currentX = event.getX();
                if (oldTouchValue < currentX-100)
                {
                    finish();
                }else {
                    return super.onTouchEvent(event);
                }
            default:
                return super.onTouchEvent(event);
                }

    }
}