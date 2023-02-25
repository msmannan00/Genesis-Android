package com.hiddenservices.onionservices.appManager.unproxiedConnectionManager;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.R;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.sharedUIMethod;

import java.util.List;

public class unproxiedConnectionViewController extends AppCompatActivity {

    private AppCompatActivity mContext;
    private ProgressBar mProgressBar;
    private TextView mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adview_controller);
    }

    unproxiedConnectionViewController(AppCompatActivity pContext, eventObserver.eventListener pEvent, ProgressBar pProgressBar, TextView p4Header) {
        this.mContext = pContext;
        this.mProgressBar = pProgressBar;
        this.mHeader = p4Header;

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

    public void onUpdateHeader(String pHeader) {
        mHeader.setText(pHeader);
    }

    public void onTrigger(unproxiedConnectionEnums.eAdvertViewController pCommands, List<Object> pData) {
        if (pCommands.equals(unproxiedConnectionEnums.eAdvertViewController.M_UPDATE_PROGRESSBAR)) {
            onUpdateProgressBar((boolean) pData.get(0));
        }
        if (pCommands.equals(unproxiedConnectionEnums.eAdvertViewController.M_UPDATE_HEADER)) {
            onUpdateHeader((String) pData.get(0));
        }
    }

    public void onTrigger(unproxiedConnectionEnums.eAdvertViewController pCommands) {
    }

}

