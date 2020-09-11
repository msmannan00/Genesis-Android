package com.darkweb.genesissearchengine.appManager.orbotManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bridgeManager.bridgeController;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
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

public class orbotController extends AppCompatActivity {

    private Switch mBridgeSwitch;
    private orbotViewController mOrbotViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orbot_settings_view);

        viewsInitializations();
        listenersInitializations();
    }

    @Override
    public void onResume()
    {
        activityContextManager.getInstance().setCurrentActivity(this);
        mOrbotViewController.initViews();
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

    public void viewsInitializations() {
        mBridgeSwitch = findViewById(R.id.bridgeSwitch);

        mOrbotViewController = new orbotViewController(mBridgeSwitch,this);
    }

    public void listenersInitializations() {

        mBridgeSwitch.setOnClickListener(view ->
        {
            Switch switch_view = (Switch)view;
            switch_view.setChecked(!switch_view.isChecked());
            helperMethod.openActivity(bridgeController.class, constants.LIST_HISTORY, orbotController.this,true);
        });
    }

    public void onClearCache(View view){

        helperMethod.clearAppData(this);
        ((Switch)view).setChecked(false);
    }

}