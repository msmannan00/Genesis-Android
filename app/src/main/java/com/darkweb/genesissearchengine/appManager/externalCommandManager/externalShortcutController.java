package com.darkweb.genesissearchengine.appManager.externalCommandManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import org.torproject.android.service.wrapper.orbotLocalConstants;

public class externalShortcutController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status.sSettingIsAppStarted = false;
        orbotLocalConstants.mIsTorInitialized = false;
        boolean mConnect = false;

        if (getIntent() != null && getIntent().getStringExtra("shortcut") != null) {
            String bundleString = getIntent().getStringExtra("shortcut");
            switch (bundleString) {
                case "erase":
                    setContentView(R.layout.popup_data_cleared_shortcut);
                    panicExitInvoked();
                    new Handler().postDelayed(this::finish, 3000);
                    return;
                case "erase_and_open":
                    panicExitInvoked();
                    mConnect = true;
                    break;
                case "Restart":
                    break;
            }
        }

        setContentView(R.layout.empty_view);

        if(mConnect){
            new Handler().postDelayed(() -> activityContextManager.getInstance().getHomeController().onStartApplication(null), 3000);
        }

        helperMethod.onDelayHandler(activityContextManager.getInstance().getHomeController(), 800, () -> {

            /* Start Required Activity */

            Intent intent = new Intent(this.getIntent());
            intent.setClassName(this.getApplicationContext(), homeController.class.getName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            this.startActivity(intent);
            overridePendingTransition(R.anim.fade_in_lang, R.anim.fade_out_lang);

            return null;
        });
    }

    public void panicExitInvoked() {
        dataController.getInstance().clearData(this);
    }
}