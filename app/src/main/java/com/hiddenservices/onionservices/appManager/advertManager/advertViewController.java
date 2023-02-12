package com.hiddenservices.onionservices.appManager.advertManager;

import android.os.Bundle;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.R;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.sharedUIMethod;

import java.util.List;

public class advertViewController extends AppCompatActivity {

    private AppCompatActivity mContext;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adview_controller);
    }

    advertViewController(AppCompatActivity pContext, eventObserver.eventListener pEvent, ProgressBar pProgressBar) {
        this.mContext = pContext;
        this.mProgressBar = pProgressBar;

        initViews();
    }

    private void initViews() {
        sharedUIMethod.updateStatusBar(mContext);
    }

    public void onUpdateProgressBar(boolean status) {
        if (status) {
            mProgressBar.animate().setDuration(350).alpha(1);
        } else {
            mProgressBar.animate().setDuration(350).alpha(0);
        }
    }

    public void onTrigger(advertEnums.eAdvertViewController pCommands, List<Object> pData) {
        if (pCommands.equals(advertEnums.eAdvertViewController.M_UPDATE_PROGRESSBAR)) {
            onUpdateProgressBar((boolean) pData.get(0));
        }
    }

    public void onTrigger(advertEnums.eAdvertViewController pCommands) {
    }

}

