package com.hiddenservices.genesissearchengine.production.appManager.bridgeManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.genesissearchengine.production.appManager.activityContextManager;
import com.hiddenservices.genesissearchengine.production.appManager.helpManager.helpController;
import com.hiddenservices.genesissearchengine.production.constants.constants;
import com.hiddenservices.genesissearchengine.production.constants.keys;
import com.hiddenservices.genesissearchengine.production.constants.status;
import com.hiddenservices.genesissearchengine.production.dataManager.dataController;
import com.hiddenservices.genesissearchengine.production.dataManager.dataEnums;
import com.hiddenservices.genesissearchengine.production.eventObserver;
import com.hiddenservices.genesissearchengine.production.helperManager.helperMethod;
import com.hiddenservices.genesissearchengine.production.appManager.activityThemeManager;
import com.hiddenservices.genesissearchengine.production.pluginManager.pluginController;
import com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums;
import com.example.myapplication.R;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eMessageManager.M_BRIDGE_MAIL;

public class bridgeController extends AppCompatActivity implements View.OnFocusChangeListener, TextWatcher {


    /*Private Variables*/
    private bridgeModel mBridgeModel;
    private bridgeViewController mBridgeViewController;

    private RadioButton mBridgeSettingObfs;
    private RadioButton mBridgeSettingBridgeChina;
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
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        if(activityThemeManager.getInstance().onInitTheme(this)){
            activityContextManager.getInstance().onResetTheme();
        }

        super.onConfigurationChanged(newConfig);
    }

    public void initializeAppModel()
    {
        if(!status.mThemeApplying){
            activityContextManager.getInstance().onStack(this);
        }
        mBridgeViewController = new bridgeViewController();
    }

    public void initializeConnections()
    {
        mBridgeSettingObfs = findViewById(R.id.pBridgeSettingObfs);
        mBridgeSettingBridgeChina = findViewById(R.id.pBridgeSettingBridgeChina);
        mBridgeSettingCustomPort = findViewById(R.id.pBridgeSettingCustomPort);
        mBridgeSettingBridgeRequest = findViewById(R.id.pBridgeSettingBridgeRequest);
        mBridgeSettingBridgeCustom = findViewById(R.id.pBridgeSettingBridgeCustom);
        mBridgeSettingCustomBridgeBlocker = findViewById(R.id.pBridgeSettingCustomBridgeBlocker);

        mBridgeSettingCustomPort.setOnFocusChangeListener(this);
        mBridgeSettingCustomPort.addTextChangedListener(this);

        mBridgeViewController.initialization(mBridgeSettingCustomPort, mBridgeSettingBridgeRequest,this, mBridgeSettingObfs, mBridgeSettingBridgeChina, mBridgeSettingBridgeCustom, mBridgeSettingCustomBridgeBlocker);
        mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sBridgeCustomBridge,0,status.sBridgeCustomType));
        mBridgeModel = new bridgeModel(new bridgeController.bridgeModelCallback(), this);
    }


    /*Local Listeners*/

    @Override
    public void onFocusChange(View view, boolean b) {
        if(view.getId() == R.id.pBridgeSettingCustomPort){
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
        if(!status.sBridgeCustomBridge.equals("meek") && !status.sBridgeCustomBridge.equals("obfs4") && status.sBridgeCustomBridge.length()<=5){
            mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_OBFS_CHECK, null);
        }
    }

    /* VIEW LISTENERS */

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onClose(View view){
        finish();
    }

    public void onUITrigger(View view){
        if(view.getId() == R.id.pBridgeSettingCustomPort){
            pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), pluginEnums.eMessageManager.M_UPDATE_BRIDGES);
        }
        else if(view.getId() == R.id.pBridgeSettingCustomBridgeBlocker){
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_ENABLE_CUSTOM_BRIDGE,null);
        }
        else if(view.getId() == R.id.pBridgeSettingBridgeRequest){
            mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_REQUEST_BRIDGE, null);
            pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(constants.CONST_BACKEND_GOOGLE_URL, this), M_BRIDGE_MAIL);
        }
        else if(view.getId() == R.id.pBridgeSettingOption2){
            mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_MEEK_BRIDGE, null);
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sBridgeCustomBridge,250, status.sBridgeCustomType));
        }
        else if(view.getId() == R.id.pBridgeSettingOption1){
            mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_OBFS_CHECK, null);
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sBridgeCustomBridge,250, status.sBridgeCustomType));
        }
        else if(view.getId() == R.id.pBridgeSettingOption3){
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_ENABLE_CUSTOM_BRIDGE,null);
        }

    }

    /* EXTERNAL LISTENERS */

    public void onUpdateBridges(String pBridge, String pType) {
        if(pBridge.length()>5){
            mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_CUSTOM_BRIDGE, Arrays.asList(pBridge, pType));
            mBridgeViewController.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sBridgeCustomBridge,250));
        }else {
            if(status.sBridgeCustomBridge.equals("meek")){
                mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_MEEK_BRIDGE, null);
            }else {
                mBridgeModel.onTrigger(bridgeEnums.eBridgeModelCommands.M_OBFS_CHECK, null);
            }
        }
    }

    /* LISTENERS */

    public class bridgeModelCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> pData, Object pCommands)
        {
            return null;
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    protected void onPause()
    {
        super.onPause();
        if(dataController.getInstance()!=null){
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1,status.sBridgeCustomBridge));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY,status.sBridgeGatewayAuto));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL,status.sBridgeGatewayManual));
        }
    }

    @Override
    public void onResume()
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);

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

    @Override
    public void onBackPressed() {
        finish();
    }

}