package com.darkweb.genesissearchengine.appManager.proxyStatusManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.orbotLogManager.orbotLogController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.appManager.activityThemeManager;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class proxyStatusController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private proxyStatusModel mProxyStatusModel;
    private proxyStatusViewController mProxyStatusViewController;

    /* INITIALIZATIONS */
    private TextView mOrbotStatus;
    private SwitchMaterial mVpnStatus;
    private SwitchMaterial mBridgeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        overridePendingTransition(R.anim.popup_scale_in, R.anim.popup_scale_out);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxy_status_view);

        viewsInitializations();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onConfigurationChanged(newConfig);
        if(newConfig.uiMode != getResources().getConfiguration().uiMode){
            activityContextManager.getInstance().onResetTheme();
            activityThemeManager.getInstance().onConfigurationChanged(this);
        }
    }

    public void viewsInitializations() {
        mOrbotStatus = findViewById(R.id.pOrbotStatus);
        mVpnStatus = findViewById(R.id.pVpnStatus);
        mBridgeStatus = findViewById(R.id.pBridgeStatus);

        activityContextManager.getInstance().onStack(this);
        mProxyStatusViewController = new proxyStatusViewController(this, mOrbotStatus, mVpnStatus, mBridgeStatus);
        mProxyStatusViewController.onTrigger(proxyStatusEnums.eProxyStatusViewCommands.M_INIT_VIEWS, Arrays.asList(pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_GET_ORBOT_STATUS), status.sVPNStatus,status.sBridgeStatus));
        mProxyStatusModel = new proxyStatusModel(new proxyStatusModelCallback());
    }

    public void orbotLog(View view) {
        helperMethod.openActivity(orbotLogController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void refreshOrbotStatus(View view) {
        mProxyStatusViewController.onTrigger(proxyStatusEnums.eProxyStatusViewCommands.M_INIT_VIEWS, Arrays.asList(pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_GET_ORBOT_STATUS), status.sVPNStatus,status.sBridgeStatus));
    }

    /* LISTENERS */

    public class proxyStatusModelCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    public void onClose(View view){
        finish();
        activityContextManager.getInstance().onRemoveStack(this);
        overridePendingTransition(R.anim.popup_scale_in, R.anim.popup_scale_out);
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
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}