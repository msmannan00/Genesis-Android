package com.darkweb.genesissearchengine.appManager.bridgeManager;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

import java.util.Collections;

public class bridgeController extends AppCompatActivity {


    /*Private Variables*/

    private bridgeViewController mBridgeViewController;
    private Switch mAutoSwitch;
    private Switch mManualSwitch;
    private EditText mCustomBridgeInput1;
    private RadioButton mObs4Proxy;
    private RadioButton mMeekProxy;
    private LinearLayout mTopPanel;
    private LinearLayout mBottomPanel;
    private Button mBridgeRequestButton;
    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bridge_settings_view);

        initializeAppModel();
        initializeConnections();
        initializeLocalEventHandlers();
    }

    public void initializeAppModel()
    {
        mBridgeViewController = new bridgeViewController();
    }

    public void initializeConnections()
    {
        mAutoSwitch = findViewById(R.id.bridgeSwitchAuto);
        mManualSwitch = findViewById(R.id.bridgeSwitchManual);
        mObs4Proxy = findViewById(R.id.bridge_obfs);
        mMeekProxy = findViewById(R.id.bridge_china);
        mTopPanel = findViewById(R.id.topPanel);
        mBottomPanel = findViewById(R.id.bottomPanel);
        mCustomBridgeInput1 = findViewById(R.id.port1);
        mBridgeRequestButton = findViewById(R.id.bridgeButton);

        mBridgeViewController.initialization(mAutoSwitch,mManualSwitch,mCustomBridgeInput1,this,mObs4Proxy,mMeekProxy,mTopPanel,mBottomPanel,mBridgeRequestButton);
    }

    private void initializeLocalEventHandlers()
    {
        mAutoSwitch.setOnClickListener(view ->
        {
            boolean isChecked = mAutoSwitch.isChecked();
            status.sGatewayAuto = isChecked;
            dataController.getInstance().setBool(keys.GATEWAY_AUTO, isChecked);

            dataController.getInstance().setBool(keys.GATEWAY_MANUAL, !isChecked);
            if (isChecked)
            {
                mBridgeViewController.setBridgeState(false, isChecked);
                status.sGatewayManual = false;
            } else
            {
                mBridgeViewController.setBridgeState(false, false);
            }

        });

        mManualSwitch.setOnClickListener(view ->
        {
            boolean isChecked = mManualSwitch.isChecked();
            status.sGatewayManual = isChecked;
            dataController.getInstance().setBool(keys.GATEWAY_MANUAL, isChecked);

            dataController.getInstance().setBool(keys.GATEWAY_AUTO, !isChecked);
            if (isChecked)
            {
                mBridgeViewController.setBridgeState(isChecked, false);
                status.sGatewayAuto = false;
            } else
            {
                mBridgeViewController.setBridgeState(false, false);
            }
        });
        mCustomBridgeInput1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                status.sCustomBridge=mCustomBridgeInput1.getText().toString();
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(dataController.getInstance()!=null){
            dataController.getInstance().setString(keys.CUSTOM_BRIDGE_1,status.sCustomBridge);
            dataController.getInstance().setBool(keys.GATEWAY_AUTO,status.sGatewayAuto);
            dataController.getInstance().setBool(keys.GATEWAY_MANUAL,status.sGatewayManual);
        }
    }

    @Override
    public void onResume()
    {
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onClose(View view){
        finish();
    }

    /*Helper Method*/

    public void requestBridges(View view){
        pluginController.getInstance().MessageManagerHandler(this, Collections.singletonList(constants.BACKEND_GOOGLE_URL), enums.etype.on_bridge_mail);
    }

    public void onMeekChecked(View view){
        status.sCustomBridge = "meek";
        dataController.getInstance().setString(keys.CUSTOM_BRIDGE_1,status.sCustomBridge);
    }
    public void onObfsChecked(View view){
        status.sCustomBridge = "obfs4";
        dataController.getInstance().setString(keys.CUSTOM_BRIDGE_1,status.sCustomBridge);
    }
}