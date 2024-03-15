package com.hiddenservices.onionservices.appManager.bridgeManager;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.helperManager.sharedUIMethod;
import com.hiddenservices.onionservices.R;

import java.util.List;

class bridgeViewController {
    /*Private Variables*/
    private RadioButton mBridgeSettingObfs;
    private RadioButton mBridgeSettingBridgeChina;
    private RadioButton mBridgeSettingCustomPort;
    private Button mBridgeSettingBridgeRequest;
    private EditText mBridgeSettingBridgeCustom;
    private ImageView mBridgeSettingCustomBridgeBlocker;
    private RadioButton mBridgeSettingBridgeSnowflake1;

    private RadioButton mBridgeSettingBridgeSnowflake2;
    /*ViewControllers*/
    private AppCompatActivity mContext;

    /*Initializations*/
    void initialization(RadioButton pBridgeSettingBridgeSnowflake1, RadioButton pBridgeSettingBridgeSnowflake2, EditText pBridgeSettingBridgeCustom, Button pBridgeSettingBridgeRequest, AppCompatActivity pContext, RadioButton pBridgeSettingObfs, RadioButton pBridgeSettingBridgeChina, RadioButton pBridgeSettingCustomPort, ImageView pBridgeSettingCustomBridgeBlocker) {
        this.mContext = pContext;
        this.mBridgeSettingObfs = pBridgeSettingObfs;
        this.mBridgeSettingBridgeChina = pBridgeSettingBridgeChina;
        this.mBridgeSettingCustomPort = pBridgeSettingCustomPort;
        this.mBridgeSettingBridgeRequest = pBridgeSettingBridgeRequest;
        this.mBridgeSettingBridgeCustom = pBridgeSettingBridgeCustom;
        this.mBridgeSettingCustomBridgeBlocker = pBridgeSettingCustomBridgeBlocker;
        this.mBridgeSettingBridgeSnowflake1 = pBridgeSettingBridgeSnowflake1;
        this.mBridgeSettingBridgeSnowflake2 = pBridgeSettingBridgeSnowflake2;
        initPostUI();
    }

    protected void onInit(){

    }

    private void initPostUI() {
        sharedUIMethod.updateStatusBar(mContext);
        mBridgeSettingBridgeCustom.setLongClickable(false);
        mBridgeSettingBridgeCustom.setOnLongClickListener(v -> false);
    }

    private void animateColor(TextView p_view, int p_from, int p_to, String p_command, int p_duration) {
        ObjectAnimator colorAnim = ObjectAnimator.ofInt(p_view, p_command, p_from, p_to);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(p_duration);
        colorAnim.start();
    }

    private void resetRadioButtons(int p_duration) {
        animateColor(mBridgeSettingObfs, mBridgeSettingObfs.getCurrentTextColor(), mContext.getResources().getColor(R.color.holo_dark_gray), "textColor", p_duration);
        animateColor(mBridgeSettingBridgeSnowflake1, mBridgeSettingBridgeSnowflake1.getCurrentTextColor(), mContext.getResources().getColor(R.color.holo_dark_gray), "textColor", p_duration);
        animateColor(mBridgeSettingBridgeSnowflake2, mBridgeSettingBridgeSnowflake2.getCurrentTextColor(), mContext.getResources().getColor(R.color.holo_dark_gray), "textColor", p_duration);
        animateColor(mBridgeSettingCustomPort, mBridgeSettingCustomPort.getCurrentTextColor(), mContext.getResources().getColor(R.color.holo_dark_gray), "textColor", p_duration);
        animateColor(mBridgeSettingBridgeChina, mBridgeSettingBridgeChina.getCurrentTextColor(), mContext.getResources().getColor(R.color.holo_dark_gray), "textColor", p_duration);

        mBridgeSettingObfs.setHighlightColor(mContext.getResources().getColor(R.color.holo_dark_gray));
        mBridgeSettingBridgeSnowflake1.setHighlightColor(mContext.getResources().getColor(R.color.holo_dark_gray));
        mBridgeSettingBridgeSnowflake2.setHighlightColor(mContext.getResources().getColor(R.color.holo_dark_gray));
        mBridgeSettingCustomPort.setHighlightColor(mContext.getResources().getColor(R.color.holo_dark_gray));
        mBridgeSettingBridgeChina.setHighlightColor(mContext.getResources().getColor(R.color.holo_dark_gray));

        mBridgeSettingObfs.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.holo_dark_gray)));
        mBridgeSettingBridgeSnowflake1.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.holo_dark_gray)));
        mBridgeSettingBridgeSnowflake2.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.holo_dark_gray)));
        mBridgeSettingCustomPort.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.holo_dark_gray)));
        mBridgeSettingBridgeChina.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.holo_dark_gray)));

        helperMethod.hideKeyboard(mContext);
        mBridgeSettingBridgeCustom.clearFocus();
        mBridgeSettingBridgeCustom.animate().setDuration(p_duration).alpha(0.35f);
        mBridgeSettingBridgeRequest.animate().setDuration(p_duration).alpha(0.35f);
        mBridgeSettingCustomBridgeBlocker.setVisibility(View.VISIBLE);
    }

    private void onEnableCustomBridge() {
        mBridgeSettingObfs.setChecked(false);
        mBridgeSettingBridgeChina.setChecked(false);
        mBridgeSettingCustomPort.setChecked(false);
        mBridgeSettingBridgeSnowflake1.setChecked(false);
        mBridgeSettingBridgeSnowflake2.setChecked(false);

        mBridgeSettingObfs.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_text_v6)));
        mBridgeSettingBridgeChina.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_text_v6)));
        mBridgeSettingBridgeSnowflake1.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_text_v6)));
        mBridgeSettingBridgeSnowflake2.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_text_v6)));

        animateColor(mBridgeSettingCustomPort, mBridgeSettingCustomPort.getCurrentTextColor(), mContext.getResources().getColor(R.color.c_text_v1), "textColor", 200);
        mBridgeSettingCustomPort.setHighlightColor(Color.BLACK);
        mBridgeSettingCustomPort.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
        mBridgeSettingObfs.setChecked(false);
        mBridgeSettingBridgeChina.setChecked(false);
        mBridgeSettingCustomPort.setChecked(true);
        mBridgeSettingBridgeSnowflake1.setChecked(false);
        mBridgeSettingBridgeSnowflake2.setChecked(false);
        mBridgeSettingBridgeCustom.animate().setDuration(200).alpha(1f);
        mBridgeSettingBridgeRequest.animate().setDuration(200).alpha(1f);
        mBridgeSettingCustomBridgeBlocker.setVisibility(View.GONE);
    }

    private void initViews(String p_bridge, int p_duration) {
        resetRadioButtons(p_duration);
        switch (p_bridge) {
            case strings.BRIDGE_CUSTOM_BRIDGE_OBFS4:
                animateColor(mBridgeSettingObfs, mBridgeSettingObfs.getCurrentTextColor(), mContext.getResources().getColor(R.color.c_text_v1), "textColor", p_duration);
                mBridgeSettingObfs.setHighlightColor(Color.BLACK);
                mBridgeSettingObfs.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
                mBridgeSettingObfs.setChecked(true);
                mBridgeSettingBridgeChina.setChecked(false);
                mBridgeSettingCustomPort.setChecked(false);
                mBridgeSettingBridgeSnowflake1.setChecked(false);
                mBridgeSettingBridgeSnowflake2.setChecked(false);
                mBridgeSettingBridgeCustom.setText(strings.GENERIC_EMPTY_STR);
                break;
            case strings.BRIDGE_CUSTOM_BRIDGE_MEEK:
                animateColor(mBridgeSettingBridgeChina, mBridgeSettingBridgeChina.getCurrentTextColor(), mContext.getResources().getColor(R.color.c_text_v1), "textColor", p_duration);
                mBridgeSettingBridgeChina.setHighlightColor(Color.BLACK);
                mBridgeSettingBridgeChina.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
                mBridgeSettingObfs.setChecked(false);
                mBridgeSettingBridgeSnowflake1.setChecked(false);
                mBridgeSettingBridgeSnowflake2.setChecked(false);
                mBridgeSettingBridgeChina.setChecked(true);
                mBridgeSettingCustomPort.setChecked(false);
                mBridgeSettingBridgeCustom.setText(strings.GENERIC_EMPTY_STR);
                break;
            case strings.BRIDGE_CUSTOM_BRIDGE_SNOWFLAKES_1:
                animateColor(mBridgeSettingBridgeChina, mBridgeSettingBridgeChina.getCurrentTextColor(), mContext.getResources().getColor(R.color.c_text_v1), "textColor", p_duration);
                mBridgeSettingBridgeChina.setHighlightColor(Color.BLACK);
                mBridgeSettingObfs.setChecked(false);
                mBridgeSettingBridgeSnowflake1.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
                mBridgeSettingBridgeSnowflake1.setChecked(true);
                mBridgeSettingBridgeSnowflake2.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_text_v6)));
                mBridgeSettingBridgeSnowflake2.setChecked(false);

                mBridgeSettingBridgeChina.setChecked(false);
                mBridgeSettingCustomPort.setChecked(false);
                mBridgeSettingBridgeCustom.setText(strings.GENERIC_EMPTY_STR);
                break;
            case strings.BRIDGE_CUSTOM_BRIDGE_SNOWFLAKES_2:
                animateColor(mBridgeSettingBridgeChina, mBridgeSettingBridgeChina.getCurrentTextColor(), mContext.getResources().getColor(R.color.c_text_v1), "textColor", p_duration);
                mBridgeSettingBridgeChina.setHighlightColor(Color.BLACK);
                mBridgeSettingObfs.setChecked(false);
                mBridgeSettingBridgeSnowflake2.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
                mBridgeSettingBridgeSnowflake2.setChecked(true);
                mBridgeSettingBridgeSnowflake1.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_text_v6)));
                mBridgeSettingBridgeSnowflake1.setChecked(false);

                mBridgeSettingBridgeChina.setChecked(false);
                mBridgeSettingCustomPort.setChecked(false);
                mBridgeSettingBridgeCustom.setText(strings.GENERIC_EMPTY_STR);
                break;
            default:
                onEnableCustomBridge();
                String mText = ("(Config) ➔ " + p_bridge.replace("\n", ""));
                mBridgeSettingBridgeCustom.setText(mText);
                break;
        }
    }

    public void onTrigger(bridgeEnums.eBridgeViewCommands pCommands, List<Object> pData) {
        if (pCommands == bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS) {
            initViews((String) pData.get(0), (int) pData.get(1));
        }
        if (pCommands == bridgeEnums.eBridgeViewCommands.M_ENABLE_CUSTOM_BRIDGE) {
            onEnableCustomBridge();
        }
    }
}