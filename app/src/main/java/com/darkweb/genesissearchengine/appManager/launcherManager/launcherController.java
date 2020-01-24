package com.darkweb.genesissearchengine.appManager.launcherManager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import java.util.List;

/*Not Yet Registered Belongs To Previous Builds
* Application Error Fallback Restart Instead Of Crash
**/

public class launcherController extends AppCompatActivity
{
    /*Private Variables*/
    private launcherViewController mLauncherViewController;
    private boolean mIsStarted = false;

    /*Initializations*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invalid_setup_view);

        initViews();
        helperMethod.openActivity(homeController.class, constants.LIST_HISTORY, this,false);
    }

    private void initViews(){
        mLauncherViewController = new launcherViewController(this,new launcherViewCallback());
    }

    /*Activity State Manager*/
    @Override
    public void onResume()
    {
        activityContextManager.getInstance().setCurrentActivity(this);
        if(mIsStarted){
            helperMethod.openActivity(homeController.class, constants.LIST_HISTORY, this,false);
            pluginController.getInstance().logEvent(strings.APP_RESTARTED);
        }
        else {
            mIsStarted = true;
        }
        super.onResume();
    }

    /*Initializations Callbacks*/

    public class launcherViewCallback implements eventObserver.eventListener
    {
        @Override
        public void invokeObserver(List<Object> data, enums.etype e_type)
        {

        }
    }

}
