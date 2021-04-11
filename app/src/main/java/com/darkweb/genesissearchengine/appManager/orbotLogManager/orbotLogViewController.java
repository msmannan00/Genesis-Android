package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.R;
import java.util.List;

class orbotLogViewController
{
    /*Private Variables*/

    private AppCompatActivity mContext;
    private TextView mLogs;
    private RecyclerView mRecycleView;

    /*Initializations*/

    orbotLogViewController(AppCompatActivity pContext, TextView pLogs, RecyclerView pRecycleView)
    {
        this.mContext = pContext;
        this.mLogs = pLogs;
        this.mRecycleView = pRecycleView;

        initViews();
        initPostUI();
    }

    private void initViews(){
        if(status.sLogListView){
            mRecycleView.setVisibility(View.VISIBLE);
            mLogs.setVisibility(View.GONE);
        }else {
            mRecycleView.setVisibility(View.GONE);
            mLogs.setVisibility(View.VISIBLE);
        }
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
            }
            else {
                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
                    mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
            }
        }
    }


    private void onUpdateLogs(String pLogs){
        pLogs = "~ " + pLogs;
        mLogs.setText(String.format("%s%s",mLogs.getText() ,pLogs + "\n\n"));
    }

    public void onTrigger(orbotLogEnums.eOrbotLogViewCommands pCommands, List<Object> pData){
        if(pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS)){
            onUpdateLogs((String) pData.get(0));
        }
        else if(pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_INIT_VIEWS)){
            initViews();
        }
    }
}
