package com.darkweb.genesissearchengine.appManager.externalCommandManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import static com.darkweb.genesissearchengine.constants.constants.CONST_PACKAGE_NAME;
import static com.darkweb.genesissearchengine.constants.keys.EXTERNAL_SHORTCUT_COMMAND_NAVIGATE;

public class externalURLNavigationContoller extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        status.sExternalWebsite = strings.GENERIC_EMPTY_STR;
        Uri mData = externalURLNavigationContoller.this.getIntent().getData();
        if(mData == null){
            mData = Uri.parse(constants.CONST_BACKEND_GENESIS_URL);
        }

        if(activityContextManager.getInstance().getHomeController()==null){
            Intent mIntent = new Intent(this, homeController.class);
            mIntent.putExtra(EXTERNAL_SHORTCUT_COMMAND_NAVIGATE, mData.toString());
            helperMethod.openIntent(mIntent, this, constants.CONST_LIST_EXTERNAL_SHORTCUT);

            Uri finalMData = mData;
            helperMethod.onDelayHandler(this, 3000, () -> {
                activityContextManager.getInstance().getHomeController().onStartApplication(null);
                activityContextManager.getInstance().getHomeController().onExternalURLInvoke(finalMData.toString());
                return null;
            });

        }
        else {
            activityContextManager.getInstance().getHomeController().onExternalURLInvoke(mData.toString());
        }

        finish();
    }
}