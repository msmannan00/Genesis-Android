package com.hiddenservices.onionservices.appManager.advertManager;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.hiddenservices.onionservices.eventObserver;

import java.util.List;

public class advertViewController extends AppCompatActivity {

    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adview_controller);
    }

    advertViewController(AppCompatActivity pContext, eventObserver.eventListener pEvent, WebView pGeckoView, ProgressBar pProgressBar)
    {
        this.mWebView = pGeckoView;
        this.mProgressBar = pProgressBar;

        initViews();
    }

    private void initViews(){
    }

    public void onUpdateProgressBar(boolean status){
        if(status){
            mProgressBar.animate().setDuration(350).alpha(1);
        }else {
            mProgressBar.animate().setDuration(350).alpha(0);
        }
    }

    public void onTrigger(advertEnums.eAdvertViewController pCommands, List<Object> pData){
        if(pCommands.equals(advertEnums.eAdvertViewController.M_UPDATE_PROGRESSBAR)){
            onUpdateProgressBar((boolean)pData.get(0));
        }
    }

    public void onTrigger(advertEnums.eAdvertViewController pCommands){
    }

}

