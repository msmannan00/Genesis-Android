package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.util.Collections;
import java.util.List;

public class orbotLogController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private orbotLogModel mOrbotModel;
    private orbotLogViewController mOrbotViewController;

    private TextView mLogs;
    private int mLogCounter=0;
    private boolean mActivityClosed = false;

    /* INITIALIZATIONS */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orbot_log_view);

        viewsInitializations();
        onUpdateLogs();
    }

    public void viewsInitializations() {
        mLogs = findViewById(R.id.pLogs);

        activityContextManager.getInstance().setOrbotLogController(this);
        mOrbotViewController = new orbotLogViewController(this, mLogs);
        mOrbotModel = new orbotLogModel(new orbotModelCallback());
    }

    /* LISTENERS */

    public void onUpdateLogs(){
        new Thread(){
            public void run(){
                while (true){
                    try {
                        if(mActivityClosed){
                            break;
                        }
                        if(orbotLocalConstants.mTorLogsHistory.size()>mLogCounter){
                            sleep(0);
                        }else {
                            sleep(400);
                        }

                        if(orbotLocalConstants.mTorLogsHistory.size()>0){
                            runOnUiThread(() -> {
                                if(orbotLocalConstants.mTorLogsHistory.size()>mLogCounter){
                                    mOrbotViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS, Collections.singletonList(orbotLocalConstants.mTorLogsHistory.get(mLogCounter)));
                                    mLogCounter+=1;
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public class orbotModelCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            return null;
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume()
    {
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
        mActivityClosed = true;
    }

    public void onClose(View view){
        finish();
        mActivityClosed = true;
    }
}