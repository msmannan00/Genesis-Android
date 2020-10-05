package com.darkweb.genesissearchengine.appManager.bridgeManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import java.util.Arrays;
import java.util.Collections;

public class bridgeController extends AppCompatActivity {


    /*Private Variables*/

    private bridgeViewController m_bridge_view_controller;
    private RadioButton m_bridge_obfs;
    private RadioButton m_bridge_china;
    private RadioButton m_bridge_custom;
    private EditText m_custom_port;
    private Button m_bridge_button;
    private ImageView m_custom_bridge_blocker;
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
        m_bridge_view_controller = new bridgeViewController();
    }

    public void initializeConnections()
    {
        m_bridge_obfs = findViewById(R.id.p_bridge_obfs);
        m_bridge_china = findViewById(R.id.p_bridge_china);
        m_custom_port = findViewById(R.id.p_port_1);
        m_bridge_button = findViewById(R.id.p_bridge_button);
        m_bridge_custom = findViewById(R.id.p_custom_bridge);
        m_custom_bridge_blocker = findViewById(R.id.p_custom_bridge_blocker);

        m_bridge_view_controller.initialization(m_custom_port, m_bridge_button,this, m_bridge_obfs, m_bridge_china, m_bridge_custom, m_custom_bridge_blocker);
        m_bridge_view_controller.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sCustomBridge,0));
    }

    private void initializeLocalEventHandlers()
    {
        m_custom_port.addTextChangedListener(new TextWatcher() {
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
                status.sCustomBridge= m_custom_port.getText().toString();
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(dataController.getInstance()!=null){
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.CUSTOM_BRIDGE_1,status.sCustomBridge));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.GATEWAY_AUTO,status.sGatewayAuto));
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.GATEWAY_MANUAL,status.sGatewayManual));
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

    public void onCustomChecked(View view){
        status.sCustomBridge = strings.CUSTOM_BRIDGE_CUSTOM;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.CUSTOM_BRIDGE_1,status.sCustomBridge));
        m_bridge_view_controller.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sCustomBridge,250));

    }
    public void onMeekChecked(View view){
        status.sCustomBridge = strings.CUSTOM_BRIDGE_OBFS4;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.CUSTOM_BRIDGE_1,status.sCustomBridge));
        m_bridge_view_controller.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sCustomBridge,250));
    }
    public void onObfsChecked(View view){
        status.sCustomBridge = strings.CUSTOM_BRIDGE_MEEK;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.CUSTOM_BRIDGE_1,status.sCustomBridge));
        m_bridge_view_controller.onTrigger(bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS, Arrays.asList(status.sCustomBridge,250));
    }
}