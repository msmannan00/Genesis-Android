package com.hiddenservices.onionservices.appManager.bridgeManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_BRIDGE_MAIL;

public class bridgeController extends AppCompatActivity implements View.OnFocusChangeListener, TextWatcher {


    /*Private Variables*/
    private bridgeModel mBridgeModel;
    private bridgeViewController mBridgeViewController;

    private RadioButton mBridgeSettingObfs;
    private RadioButton mBridgeSettingBridgeChina;
    private RadioButton mBridgeSettingBridgeSnowflake1;
    private RadioButton mBridgeSettingBridgeSnowflake2;
    private RadioButton mBridgeSettingBridgeCustom;
    private EditText mBridgeSettingCustomPort;
    private Button mBridgeSettingBridgeRequest;
    private ImageView mBridgeSettingCustomBridgeBlocker;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityContextManager.getInstance().setBridgeController(this);
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bridge_settings_view);

        initializeAppModel();
        initializeConnections();
        onBack();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        if (activityThemeManager.getInstance().onInitTheme(this)) {
            activityContextManager.getInstance().onResetTheme();
        }

        super.onConfigurationChanged(newConfig);
    }

    public void initializeAppModel() {
        if (!status.mThemeApplying) {
            activityContextManager.getInstance().onStack(this);
        }
        mBridgeViewController = new bridgeViewController();
        mBridgeViewController.onInit();
    }

    public void initializeConnections() {
        mBridgeSettingObfs = findViewById(R.id.pBridgeSettingObfs);
        mBridgeSettingBridgeChina = findViewById(R.id.pBridgeSettingBridgeChina);
        mBridgeSettingBridgeSnowflake1 = findViewById(R.id.pBridgeSettingSnowFlakes1);
        mBridgeSettingBridgeSnowflake2 = findViewById(R.id.pBridgeSettingSnowFlakes2);
        mBridgeSettingCustomPort = findViewById(R.id.pBridgeSettingCustomPort);
        mBridgeSettingBridgeRequest = findViewById(R.id.pBridgeSettingBridgeRequest);
        mBridgeSettingBridgeCustom = findViewById(R.id.pBridgeSettingBridgeCustom);
        mBridgeSettingCustomBridgeBlocker = findViewById(R.id.pBridgeSettingCustomBridgeBlocker);

        mBridgeSettingCustomPort.setOnFocusChangeListener(this);
        mBridgeSettingCustomPort.addTextChangedListener(this);

        mBridgeViewController.initialization(mBridgeSettingBridgeSnowflake1, mBridgeSettingBridgeSnowflake2, mBridgeSettingCustomPort, mBridgeSettingBridgeRequest, this, mBridgeSettingObfs, mBridgeSettingBridgeChina, mBridgeSettingBridgeCustom, mBridgeSettingCustomBridgeBlocker);
        mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sBridgeCustomBridge, 0, status.sBridgeCustomType));
        mBridgeModel = new bridgeModel(new bridgeController.bridgeModelCallback(), this);
        mBridgeModel.onInit();
    }


    /*Local Listeners*/

    @Override
    public void onFocusChange(View view, boolean b) {
        if (view.getId() == R.id.pBridgeSettingCustomPort) {
            if (!b) {
                helperMethod.hideKeyboard(this);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        try {
            if (!status.sBridgeCustomBridge.equals("meek") && !status.sBridgeCustomBridge.equals("obfs4") && status.sBridgeCustomBridge.length() <= 5) {
                mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_OBFS_CHECK, null);
            }
        }catch (Exception ignored){}
    }

    /* VIEW LISTENERS */

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this, true);
    }

    public void onClose(View view) {
        finish();
    }

    public void onUITrigger(View view) {
        if (view.getId() == R.id.pBridgeSettingCustomPort) {
            pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), pluginEnums.eMessageManager.M_UPDATE_BRIDGES);
        } else if (view.getId() == R.id.pBridgeSettingCustomBridgeBlocker) {
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_ENABLE_CUSTOM_BRIDGE, null);
        } else if (view.getId() == R.id.pBridgeSettingBridgeRequest) {
            mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_REQUEST_BRIDGE, null);
            pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(constants.CONST_BACKEND_GOOGLE_URL, this), M_BRIDGE_MAIL);
        } else if (view.getId() == R.id.pSearchSettingOption2) {
            mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_MEEK_BRIDGE, null);
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sBridgeCustomBridge, 250, status.sBridgeCustomType));
        } else if (view.getId() == R.id.pBridgeSettingOption41) {
            mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_SNOWFLAKES_BRIDGE_1, null);
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sBridgeCustomBridge, 250, status.sBridgeCustomType));
        } else if (view.getId() == R.id.pBridgeSettingOption42) {
            mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_SNOWFLAKES_BRIDGE_2, null);
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sBridgeCustomBridge, 250, status.sBridgeCustomType));
        } else if (view.getId() == R.id.pSearchSettingOption1) {
            mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_OBFS_CHECK, null);
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sBridgeCustomBridge, 250, status.sBridgeCustomType));
        } else if (view.getId() == R.id.pBridgeSettingOption3) {
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_ENABLE_CUSTOM_BRIDGE, null);
        }

    }

    /* EXTERNAL LISTENERS */

    public void onUpdateBridges(String pBridge, String pType) {
        if (pBridge.length() > 5) {
            mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_CUSTOM_BRIDGE, Arrays.asList(pBridge, pType));
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sBridgeCustomBridge, 250));
        } else {
            if (status.sBridgeCustomBridge.equals("meek")) {
                mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_MEEK_BRIDGE, null);
            } else {
                mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_OBFS_CHECK, null);
            }
        }
    }

    @Override
    public void addMenuProvider(@NonNull MenuProvider provider, @NonNull LifecycleOwner owner, @NonNull Lifecycle.State state) {

    }

    /* LISTENERS */

    public class bridgeModelCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> pData, Object pCommands) {
            return null;
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    protected void onPause() {
        super.onPause();
        if (dataController.getInstance() != null) {
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1, status.sBridgeCustomBridge));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY, status.sBridgeGatewayAuto));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL, status.sBridgeGatewayManual));
        }
    }

    @Override
    public void onResume() {
        activityContextManager.getInstance().onPurgeStack();
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().setBridgeController(null);
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onBack() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
}