package com.darkweb.genesissearchengine.appManager.bridgeManager;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import java.util.List;

class bridgeViewController
{
    /*Private Variables*/
    private RadioButton mBridgeObfs;
    private RadioButton mBridgeChina;
    private RadioButton mBridgeCustom;
    private Button mBridgeButton;
    private EditText mCustomPort;
    private ImageView mCustomBridgeBlocker;

    /*ViewControllers*/
    private AppCompatActivity mContext;

    /*Initializations*/
    void initialization(EditText pCustomPort, Button pBridgeButton, AppCompatActivity pContext,RadioButton pBridgeObfs,RadioButton pBridgeChina,RadioButton pBridgeCustom, ImageView pCustomBridgeBlocker){
        this.mContext = pContext;
        this.mBridgeObfs = pBridgeObfs;
        this.mBridgeChina = pBridgeChina;
        this.mBridgeCustom = pBridgeCustom;
        this.mBridgeButton = pBridgeButton;
        this.mCustomPort = pCustomPort;
        this.mCustomBridgeBlocker = pCustomBridgeBlocker;

        initPostUI();
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.blue_dark));
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

    private void animateColor(TextView p_view, int p_from, int p_to, String p_command, int p_duration){
        ObjectAnimator colorAnim = ObjectAnimator.ofInt(p_view, p_command,p_from, p_to);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(p_duration);
        colorAnim.start();
    }

    private void resetRadioButtons(int p_duration){
        animateColor(mBridgeObfs, mBridgeObfs.getCurrentTextColor(), mContext.getResources().getColor(R.color.holo_dark_gray), "textColor", p_duration);
        animateColor(mBridgeCustom, mBridgeCustom.getCurrentTextColor(), mContext.getResources().getColor(R.color.holo_dark_gray), "textColor", p_duration);
        animateColor(mBridgeChina, mBridgeChina.getCurrentTextColor(), mContext.getResources().getColor(R.color.holo_dark_gray), "textColor", p_duration);

        mBridgeObfs.setHighlightColor(mContext.getResources().getColor(R.color.holo_dark_gray));
        mBridgeCustom.setHighlightColor(mContext.getResources().getColor(R.color.holo_dark_gray));
        mBridgeChina.setHighlightColor(mContext.getResources().getColor(R.color.holo_dark_gray));

        mBridgeObfs.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.holo_dark_gray)));
        mBridgeCustom.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.holo_dark_gray)));
        mBridgeChina.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.holo_dark_gray)));

        helperMethod.hideKeyboard(mContext);
        mCustomPort.clearFocus();
        mCustomPort.animate().setDuration(p_duration).alpha(0.35f);
        mBridgeButton.animate().setDuration(p_duration).alpha(0.35f);
        mCustomBridgeBlocker.setVisibility(View.VISIBLE);
    }

    private void onEnableCustomBridge(){
        animateColor(mBridgeCustom, mBridgeCustom.getCurrentTextColor(), mContext.getResources().getColor(R.color.c_text_v1), "textColor", 200);
        mBridgeCustom.setHighlightColor(Color.BLACK);
        mBridgeCustom.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
        mBridgeObfs.setChecked(false);
        mBridgeChina.setChecked(false);
        mBridgeCustom.setChecked(true);
        mCustomPort.animate().setDuration(200).alpha(1f);
        mBridgeButton.animate().setDuration(200).alpha(1f);
        mCustomBridgeBlocker.setVisibility(View.GONE);
    }

    private void initViews(String p_bridge, int p_duration, String pType){
        resetRadioButtons(p_duration);
        if(p_bridge.equals(strings.BRIDGE_CUSTOM_BRIDGE_OBFS4)){
            animateColor(mBridgeObfs, mBridgeObfs.getCurrentTextColor(), mContext.getResources().getColor(R.color.c_text_v1), "textColor", p_duration);
            mBridgeObfs.setHighlightColor(Color.BLACK);
            mBridgeObfs.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mBridgeObfs.setChecked(true);
            mBridgeChina.setChecked(false);
            mBridgeCustom.setChecked(false);
            mCustomPort.setText(strings.GENERIC_EMPTY_STR);
        }else if(p_bridge.equals(strings.BRIDGE_CUSTOM_BRIDGE_MEEK)){
            animateColor(mBridgeChina, mBridgeChina.getCurrentTextColor(), mContext.getResources().getColor(R.color.c_text_v1), "textColor", p_duration);
            mBridgeChina.setHighlightColor(Color.BLACK);
            mBridgeChina.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mBridgeObfs.setChecked(false);
            mBridgeChina.setChecked(true);
            mBridgeCustom.setChecked(false);
            mCustomPort.setText(strings.GENERIC_EMPTY_STR);
        }else {
            onEnableCustomBridge();
            mCustomPort.setText(("(Type) " + pType + " ➔ " + "(Config) "+p_bridge.replace("\n","")));
        }
    }

    public void onTrigger(bridgeEnums.eBridgeViewCommands p_commands, List<Object> p_data){
        if(p_commands == bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS){
            initViews((String) p_data.get(0), (int)p_data.get(1), (String) p_data.get(2));
        }
        if(p_commands == bridgeEnums.eBridgeViewCommands.M_ENABLE_CUSTOM_BRIDGE){
            onEnableCustomBridge();
        }
    }

}
