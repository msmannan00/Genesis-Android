package com.darkweb.genesissearchengine.appManager.externalCommandManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;

import static com.darkweb.genesissearchengine.constants.constants.CONST_PACKAGE_NAME;

public class externalURLNavigationContoller extends AppCompatActivity {
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri data = externalURLNavigationContoller.this.getIntent().getData();
        if(data == null || status.sSettingIsAppStarted){

            /* Close Activity */

            finish();
            activityContextManager.getInstance().onClearStack();

            /* Create Request Handler */

            if(status.sSettingIsAppStarted && data!=null){
                helperMethod.onDelayHandler(this, 250, () -> {
                    activityContextManager.getInstance().getHomeController().onOpenLinkNewTab(activityContextManager.getInstance().getHomeController().completeURL(data.toString()));
                    activityContextManager.getInstance().getHomeController().onClearSelectionTab();
                    return null;
                });
            }

            Intent bringToForegroundIntent = new Intent(activityContextManager.getInstance().getHomeController(), homeController.class);
            bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(bringToForegroundIntent);
            overridePendingTransition(R.anim.fade_in_instant, R.anim.fade_out_instant);
        }
        else if(status.sSettingIsAppRunning){

            /* Refresh Intent Data */

            finish();
            status.sExternalWebsite = data.toString();

        }else {

            /* Start Required Activity */

            Intent intent = new Intent(this.getIntent());
            intent.setClassName(this.getApplicationContext(), homeController.class.getName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if(activityContextManager.getInstance().getHomeController()!=null){
                activityContextManager.getInstance().getHomeController().onOpenLinkNewTab(data.toString());
            }else {
                status.sExternalWebsite = data.toString();
            }

            /* Bring Application To Front */

            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(CONST_PACKAGE_NAME);
            startActivity(launchIntent);
            overridePendingTransition(R.anim.fade_in_instant, R.anim.fade_out_instant);

            /* Close Activity */

            helperMethod.onDelayHandler(this, 1000, () -> {
                finish();
                return null;
            });
        }
    }
}
