package com.hiddenservices.genesissearchengine.production.appManager.externalCommandManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.genesissearchengine.production.appManager.activityContextManager;
import com.hiddenservices.genesissearchengine.production.appManager.homeManager.homeController.homeController;
import com.hiddenservices.genesissearchengine.production.constants.constants;
import com.hiddenservices.genesissearchengine.production.constants.status;
import com.hiddenservices.genesissearchengine.production.constants.strings;
import com.hiddenservices.genesissearchengine.production.dataManager.dataController;
import com.hiddenservices.genesissearchengine.production.helperManager.helperMethod;
import com.example.myapplication.R;
import org.orbotproject.android.service.wrapper.orbotLocalConstants;
import static com.hiddenservices.genesissearchengine.production.constants.constants.CONST_EXTERNAL_SHORTCUT_COMMAND_ERASE;
import static com.hiddenservices.genesissearchengine.production.constants.keys.EXTERNAL_SHORTCUT_COMMAND;

public class externalShortcutController extends AppCompatActivity {

    /* Initialize */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        status.sExternalWebsite = strings.GENERIC_EMPTY_STR;
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
                        activityContextManager.getInstance().onClearStack();
                        break;
                }
            }
        }

        /* Start Required Activity */

        finish();
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