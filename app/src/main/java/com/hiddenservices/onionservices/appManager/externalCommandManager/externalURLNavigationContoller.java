package com.hiddenservices.onionservices.appManager.externalCommandManager;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.advertManager.advertController;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeController;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.helperManager.helperMethod;

import static com.hiddenservices.onionservices.constants.keys.EXTERNAL_SHORTCUT_COMMAND_NAVIGATE;

public class externalURLNavigationContoller extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status.sExternalWebsite = strings.GENERIC_EMPTY_STR;
        Uri mData = externalURLNavigationContoller.this.getIntent().getData();
        if(status.sExternalWebsiteLoading && !status.sSettingIsAppStarted){
            Intent intent = new Intent(this, homeController.class);
            intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return;
        }
        status.sExternalWebsiteLoading = true;
        if(mData.toString().contains("applovin")){
            Intent myIntent = new Intent(this, advertController.class);
            myIntent.putExtra(keys.ADVERT_URL, mData.toString());
            myIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(myIntent);
        }else {
            if(mData == null){
                mData = Uri.parse(constants.CONST_BACKEND_GENESIS_URL);
            }
            if(activityContextManager.getInstance().getHomeController()==null){
                Intent mIntent = new Intent(this, homeController.class);
                mIntent.putExtra(EXTERNAL_SHORTCUT_COMMAND_NAVIGATE, mData.toString());
                helperMethod.openIntent(mIntent, this, constants.CONST_LIST_EXTERNAL_SHORTCUT);
                Uri finalMData = mData;
                status.sExternalWebsite = finalMData.toString();
                helperMethod.onDelayHandler(this, 1500, () -> {
                    activityContextManager.getInstance().getHomeController().onStartApplication(null);
                    activityContextManager.getInstance().getHomeController().onExternalURLInvoke(finalMData.toString());
                    return null;
                });

            }
            else {
                Intent intent = new Intent(this, homeController.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

                activityContextManager.getInstance().getHomeController().onExternalURLInvoke(mData.toString());
            }
        }

        helperMethod.onDelayHandler(this, 2500, () -> {
            finish();
            return null;
        });
    }
}