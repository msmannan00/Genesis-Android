package com.hiddenservices.onionservices.appManager.settingManager.proxyStatusManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogController;
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

public class proxyStatusController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private proxyStatusModel mProxyStatusModel;
    private proxyStatusViewController mProxyStatusViewController;

    /* INITIALIZATIONS */
    private TextView mOrbotStatus;
    private SwitchMaterial mBridgeStatus;
    private SwitchMaterial mSnowflakeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxy_status_view);

        viewsInitializations();
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

    public void viewsInitializations() {
        mOrbotStatus = findViewById(R.id.pOrbotStatus);
        mBridgeStatus = findViewById(R.id.pBridgeStatus);
        mSnowflakeStatus = findViewById(R.id.pSnowflakeStatus);

        activityContextManager.getInstance().onStack(this);
        mProxyStatusViewController = new proxyStatusViewController(this, mOrbotStatus, mBridgeStatus, mSnowflakeStatus);
        mProxyStatusViewController.onInit();

        Object mProxy = pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_GET_ORBOT_STATUS);
        if (mProxy == null) {
            mProxyStatusViewController.onTrigger(proxyStatusEnums.eProxyStatusViewCommands.M_INIT_VIEWS, Arrays.asList("loading...", status.sBridgeStatus, status.sSnowFlakesStatus));
        } else {
            mProxyStatusViewController.onTrigger(proxyStatusEnums.eProxyStatusViewCommands.M_INIT_VIEWS, Arrays.asList(mProxy, status.sBridgeStatus, status.sSnowFlakesStatus));
        }
        mProxyStatusModel = new proxyStatusModel(new proxyStatusModelCallback());
        mProxyStatusModel.onInit();
    }

    public void orbotLog(View view) {
        helperMethod.openActivity(orbotLogController.class, constants.CONST_LIST_HISTORY, this, true);
    }

    public void refreshOrbotStatus(View view) {
        mProxyStatusViewController.onTrigger(proxyStatusEnums.eProxyStatusViewCommands.M_INIT_VIEWS, Arrays.asList(pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_GET_ORBOT_STATUS), status.sBridgeStatus, status.sSnowFlakesStatus));
    }

    /* LISTENERS */

    public class proxyStatusModelCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            return null;
        }
    }

    public void onClose(View view) {
        finish();
        activityContextManager.getInstance().onRemoveStack(this);
    }

    /* LOCAL OVERRIDES */

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        activityContextManager.getInstance().onPurgeStack();
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
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

    @Override
    protected void onStop() {
        super.onStop();
    }
}