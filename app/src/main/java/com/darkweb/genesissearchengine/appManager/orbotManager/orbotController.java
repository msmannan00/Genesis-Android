package com.darkweb.genesissearchengine.appManager.orbotManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.example.myapplication.R;

public class orbotController extends AppCompatActivity {

    private Switch mBridgeSwitch;
    private orbotViewController mOrbotViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orbot_settings_view);

        viewsInitializations();
        listenersInitializations();
    }

    @Override
    public void onResume()
    {
        activityContextManager.getInstance().setCurrentActivity(this);
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

        mBridgeSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            status.sGateway = isChecked;
            dataController.getInstance().setBool(keys.GATEWAY,isChecked);
        });
    }

}