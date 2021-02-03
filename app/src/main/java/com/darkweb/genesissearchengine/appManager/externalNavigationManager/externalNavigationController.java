package com.darkweb.genesissearchengine.appManager.externalNavigationManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.example.myapplication.R;

public class externalNavigationController extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);
        Intent intent = new Intent(this.getIntent());
        intent.setClassName(this.getApplicationContext(), homeController.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Uri data = this.getIntent().getData();
        if(data!=null){
            activityContextManager.getInstance().getHomeController().onLoadURL(data.toString());
        }
        this.startActivity(intent);

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
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if(data!=null){
            activityContextManager.getInstance().getHomeController().onLoadURL("https://bbc.com");
        }
    }

}
