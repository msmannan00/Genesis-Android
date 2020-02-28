package com.darkweb.genesissearchengine.appManager.bridgeManager;

import android.animation.ValueAnimator;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.R;

class bridgeViewController
{
    /*Private Variables*/

    private Switch mAutoSwitch;
    private Switch mManualSwitch;
    private EditText mCustomBridgeInput1;
    private RadioButton mObs4Proxy;
    private RadioButton mMeekProxy;
    private Button mBridgeRequestButton;

    private LinearLayout mTopPanel;
    private LinearLayout mBottomPanel;

    /*ViewControllers*/
    private int mPanelHeight = 0;

    private AppCompatActivity mContext;

    /*Initializations*/

    void initialization(Switch mAutoSwitch,Switch mManualSwitch,EditText mCustomBridgeInput1,AppCompatActivity mContext,RadioButton mObs4Proxy,RadioButton mMeekProxy,LinearLayout mTopPanel,LinearLayout mBottomPanel,Button mBridgeRequestButton){
        this.mContext = mContext;
        this.mAutoSwitch = mAutoSwitch;
        this.mManualSwitch = mManualSwitch;
        this.mCustomBridgeInput1 = mCustomBridgeInput1;
        this.mObs4Proxy = mObs4Proxy;
        this.mMeekProxy = mMeekProxy;
        this.mTopPanel = mTopPanel;
        this.mBottomPanel = mBottomPanel;
        this.mBridgeRequestButton = mBridgeRequestButton;

        initPostUI();
        initViews();
        initPanels();
    }

    private void initPostUI(){
        mPanelHeight = 1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
            }
            else {
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.white));
            }
        }
    }

    private void initViews(){
        mAutoSwitch.setChecked(status.sGatewayAuto);
        mManualSwitch.setChecked(status.sGatewayManual);

        mMeekProxy.clearFocus();
        mObs4Proxy.clearFocus();

        if(status.sGatewayAuto){
            if(status.sCustomBridge.contains("obfs")){
                mObs4Proxy.setChecked(true);
            }else {
                mMeekProxy.setChecked(true);
            }
            setBridgeState(false,true);
        }
        else if(status.sGatewayManual){
            mCustomBridgeInput1.setText(status.sCustomBridge);
            setBridgeState(true,false);
        }
        else {
            setBridgeState(false,false);
        }
    }

    void setBridgeState(boolean manual, boolean auto){
        mAutoSwitch.setChecked(auto);
        mManualSwitch.setChecked(manual);
        if(auto){
            mTopPanel.animate().setDuration(300).alpha(0.55f).withEndAction((() -> mTopPanel.setClickable(true)));
            mBottomPanel.animate().setDuration(300).alpha(0.55f).withEndAction((() -> mBottomPanel.setClickable(false)));
            mCustomBridgeInput1.setEnabled(false);
            mMeekProxy.setEnabled(true);
            mObs4Proxy.setEnabled(true);
            mBridgeRequestButton.setEnabled(false);
        }
        if(manual){
            mTopPanel.animate().setDuration(300).alpha(0.55f).withEndAction((() -> mTopPanel.setClickable(false)));
            mBottomPanel.animate().setDuration(300).alpha(1).withEndAction((() -> mBottomPanel.setClickable(true)));
            mCustomBridgeInput1.setEnabled(true);
            mMeekProxy.setEnabled(false);
            mObs4Proxy.setEnabled(false);
            mBridgeRequestButton.setEnabled(true);
        }
        if(!auto && !manual) {
            mTopPanel.animate().setDuration(300).alpha(0.55f).withEndAction((() -> mTopPanel.setClickable(false)));
            mBottomPanel.animate().setDuration(300).alpha(0.55f).withEndAction((() -> mBottomPanel.setClickable(false)));
            mCustomBridgeInput1.setEnabled(false);
            mMeekProxy.setEnabled(false);
            mObs4Proxy.setEnabled(false);
            mBridgeRequestButton.setEnabled(false);
        }
    }

    private void initPanels(){
    }

}
