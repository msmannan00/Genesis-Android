package com.hiddenservices.onionservices.appManager.externalCommandManager;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.R;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.unproxiedConnectionManager.unproxiedConnectionController;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeController;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import static com.hiddenservices.onionservices.constants.constants.CONST_PACKAGE_NAME;
import static com.hiddenservices.onionservices.constants.keys.EXTERNAL_SHORTCUT_COMMAND_NAVIGATE;

public class externalURLNavigationController extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.widget.onionservices.helperMethod.helperMethod.onStartApplication(this, CONST_PACKAGE_NAME);
        status.sExternalWebsite = strings.GENERIC_EMPTY_STR;
        final Uri[] mData = {externalURLNavigationController.this.getIntent().getData()};
        status.sExternalWebsiteLoading = true;
        new Handler().postDelayed(() ->
        {
            if (mData[0] !=null && status.sIsBackgroundAdvertCheck) {
                status.sIsBackgroundAdvertCheck = false;
                Intent myIntent = new Intent(activityContextManager.getInstance().getHomeController(), unproxiedConnectionController.class);
                myIntent.putExtra(keys.ADVERT_URL, mData[0].toString());
                myIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activityContextManager.getInstance().getHomeController().onAdvertClickPauseSession();
                activityContextManager.getInstance().getHomeController().startActivity(myIntent);

            } else {
                if (mData[0] == null) {
                    mData[0] = Uri.parse(constants.CONST_BACKEND_GENESIS_URL);
                }
                if (activityContextManager.getInstance().getHomeController() == null) {
                    Intent mIntent = new Intent(this, homeController.class);
                    mIntent.putExtra(EXTERNAL_SHORTCUT_COMMAND_NAVIGATE, mData[0].toString());
                    helperMethod.openIntent(mIntent, this, constants.CONST_LIST_EXTERNAL_SHORTCUT);
                    Uri finalMData = mData[0];
                    status.sExternalWebsite = finalMData.toString();
                } else {

                    Uri finalMData1 = mData[0];
                    helperMethod.onDelayHandler(100, () -> {
                        activityContextManager.getInstance().getHomeController().onExternalURLInvoke(finalMData1.toString());
                        return null;
                    });

                    helperMethod.onDelayHandler(100, () -> {
                        com.widget.onionservices.helperMethod.helperMethod.onStartApplication(this, CONST_PACKAGE_NAME);
                        return null;
                    });

                    return;
                }
            }
            status.sIsBackgroundAdvertCheck = false;
        }, 100);

        finish();
        overridePendingTransition(R.anim.fade_out   , R.anim.fade_out);
    }
}