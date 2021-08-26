package com.darkweb.genesissearchengine.appManager.externalCommandManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import static com.darkweb.genesissearchengine.constants.constants.CONST_EXTERNAL_SHORTCUT_COMMAND_ERASE;
import static com.darkweb.genesissearchengine.constants.keys.EXTERNAL_SHORTCUT_COMMAND;

public class externalShortcutController extends AppCompatActivity {

    /* Initialize */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orbotLocalConstants.mIsTorInitialized = false;
        Intent mIntent = new Intent(this, homeController.class);

        if(getIntent() != null){
            String mShortcutCommands = getIntent().getStringExtra(EXTERNAL_SHORTCUT_COMMAND);
            status.sSettingIsAppStarted = false;
            if(mShortcutCommands!=null){
                mIntent.putExtra(EXTERNAL_SHORTCUT_COMMAND, mShortcutCommands);
                switch (mShortcutCommands) {
                    case CONST_EXTERNAL_SHORTCUT_COMMAND_ERASE:
                        setContentView(R.layout.popup_data_cleared_shortcut);
                        panicExitInvoked();
                        return;
                    case constants.CONST_EXTERNAL_SHORTCUT_COMMAND_ERASE_OPEN:
                        setContentView(R.layout.empty_view);
                        panicExitInvoked();
                        break;
                    case constants.CONST_EXTERNAL_SHORTCUT_COMMAND_RESTART:
                        break;
                }
            }
        }

        /* Start Required Activity */

        helperMethod.openIntent(mIntent, this, constants.CONST_LIST_EXTERNAL_SHORTCUT);
    }

    /* UI TRIGGERS */

    public void onUITrigger(View view){
        if(view.getId() == R.id.pDataClearedDismiss){
            finishAffinity();
        }
    }

    /* Helper Methods */

    public void panicExitInvoked() {
        dataController.getInstance().clearData(this);
        helperMethod.onDelayHandler(this, 3000, () -> {
            finish();
            return null;
        });
    }
}