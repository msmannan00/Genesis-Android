package com.darkweb.genesissearchengine.externalNavigationManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.constants.status;

import static com.darkweb.genesissearchengine.constants.constants.CONST_PACKAGE_NAME;

public class externalNavigationController extends AppCompatActivity {
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri data = externalNavigationController.this.getIntent().getData();
        if(data == null){
            finish();
            activityContextManager.getInstance().onClearStack();
            Intent bringToForegroundIntent = new Intent(activityContextManager.getInstance().getHomeController(), homeController.class);
            bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(bringToForegroundIntent);
            return;
        }

        if(status.sSettingIsAppStarted){
            finish();
            activityContextManager.getInstance().onClearStack();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                activityContextManager.getInstance().getHomeController().onOpenLinkNewTab(activityContextManager.getInstance().getHomeController().completeURL(data.toString()));
                activityContextManager.getInstance().getHomeController().onClearSelectionTab();
            }, 3000);

            Intent bringToForegroundIntent = new Intent(activityContextManager.getInstance().getHomeController(), homeController.class);
            bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(bringToForegroundIntent);

            return;
        }else if(status.sSettingIsAppRunning){
            finish();
            status.sExternalWebsite = data.toString();
            return;
        }

        Intent intent = new Intent(this.getIntent());
        intent.setClassName(this.getApplicationContext(), homeController.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if(activityContextManager.getInstance().getHomeController()!=null){
            activityContextManager.getInstance().getHomeController().onOpenLinkNewTab(data.toString());
        }else {
            status.sExternalWebsite = data.toString();
        }

        this.startActivity(intent);
        this.overridePendingTransition(0, 0);

        new Thread(){
            public void run(){
                try {
                    sleep(1000);
                    externalNavigationController.this.runOnUiThread(() -> finish());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(CONST_PACKAGE_NAME);
        startActivity(launchIntent);

    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if(data!=null){
            activityContextManager.getInstance().getHomeController().onOpenLinkNewTab(data.toString());
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(CONST_PACKAGE_NAME);
            startActivity(launchIntent);
        }
    }
}
