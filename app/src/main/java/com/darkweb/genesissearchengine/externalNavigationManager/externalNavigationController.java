package com.darkweb.genesissearchengine.externalNavigationManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.R;

public class externalNavigationController extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(status.sSettingIsAppStarted){
            finish();
            Uri data = externalNavigationController.this.getIntent().getData();
            activityContextManager.getInstance().getHomeController().onOpenLinkNewTab(data.toString());


            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.darkweb.genesissearchengine");
                startActivity(launchIntent);
            }, 500);

            return;
        }

        Intent intent = new Intent(this.getIntent());
        intent.setClassName(this.getApplicationContext(), homeController.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Uri data = this.getIntent().getData();
        if(data!=null){
            if(activityContextManager.getInstance().getHomeController()!=null){
                activityContextManager.getInstance().getHomeController().onOpenLinkNewTab(data.toString());
            }else {
                status.sExternalWebsite = data.toString();
            }
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

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.darkweb.genesissearchengine");
        startActivity(launchIntent);

    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if(data!=null){
            activityContextManager.getInstance().getHomeController().onOpenLinkNewTab(data.toString());
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.darkweb.genesissearchengine");
            startActivity(launchIntent);
        }
    }

}
