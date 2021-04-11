package com.darkweb.genesissearchengine.appManager.orbotManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bridgeManager.bridgeController;
import com.darkweb.genesissearchengine.appManager.helpManager.helpController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.SimpleGestureFilter;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.helperManager.theme;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
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
    private GestureDetector mSwipeDirectionDetector;

    /* INITIALIZATIONS */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        overridePendingTransition(R.anim.push_anim_in, R.anim.push_anim_out);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.orbot_settings_view);

        viewsInitializations();
        onInitListener();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onConfigurationChanged(newConfig);

        theme.getInstance().onConfigurationChanged(this);
    }

    public void viewsInitializations() {
        mBridgeSwitch = findViewById(R.id.pBridgeSwitch);
        mVpnSwitch = findViewById(R.id.pVpnSwitch);
        mCustomizableBridgeMenu = findViewById(R.id.pCustomizableBridgeMenu);

        mOrbotViewController = new com.darkweb.genesissearchengine.appManager.orbotManager.orbotViewController(mBridgeSwitch, mVpnSwitch, this, mCustomizableBridgeMenu);
        mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.M_INIT_UI, Arrays.asList(status.sVPNStatus,status.sBridgeStatus));
        mOrbotModel = new com.darkweb.genesissearchengine.appManager.orbotManager.orbotModel(new orbotModelCallback());
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    /* LISTENERS */

    private void onInitListener(){
        mSwipeDirectionDetector=new GestureDetector(this,new SimpleGestureFilter(){

            @Override
            public boolean onSwipe(Direction direction) {
                if (direction==Direction.left){
                    onClose(null);
                }
                return true;
            }
        });
    }

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
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_ENABLES,status.sBridgeStatus));
    }

    public void openBridgeSettings(View view){
        helperMethod.openActivity(bridgeController.class, constants.CONST_LIST_HISTORY, orbotController.this, true);
    }

    public void onVPNSwitch(View view){
        mOrbotModel.onTrigger(orbotEnums.eOrbotModelCommands.M_VPN_SWITCH,Collections.singletonList(!mVpnSwitch.isChecked()));
        mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.M_UPDATE_VPN,Collections.singletonList(status.sVPNStatus));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.VPN_ENABLED,status.sVPNStatus));
    }

    public void onClose(View view){
        finish();
        activityContextManager.getInstance().onRemoveStack(this);
        overridePendingTransition(R.anim.push_anim_out_reverse, R.anim.push_anim_in_reverse);
    }

    /* LOCAL OVERRIDES */

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        mOrbotViewController.onTrigger(orbotEnums.eOrbotViewCommands.M_INIT_POST_UI,null);

        activityContextManager.getInstance().onStack(this);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(inSignatureArea(event)){
            try{
                mSwipeDirectionDetector.onTouchEvent(event);
            }catch (Exception ignored){ }
        }
        return super.dispatchTouchEvent(event);
    }

    public boolean inSignatureArea(MotionEvent ev) {
        float mEventY = ev.getY();
        float mEventX = ev.getX();
        Log.i("FUCKSSSS", helperMethod.getScreenWidth(this)-ev.getX() + "----" + ev.getX());
        return mEventY>helperMethod.pxFromDp(500) || mEventX>helperMethod.getScreenWidth(this)-helperMethod.pxFromDp(80);
    }

}