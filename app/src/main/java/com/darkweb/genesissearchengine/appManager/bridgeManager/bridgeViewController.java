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
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import java.util.List;

class bridgeViewController
{
    /*Private Variables*/
    private RadioButton m_bridge_obfs;
    private RadioButton m_bridge_china;
    private RadioButton m_bridge_custom;
    private Button m_bridge_button;
    private EditText m_custom_port;
    private ImageView m_custom_bridge_blocker;

    /*ViewControllers*/
    private AppCompatActivity m_context;

    /*Initializations*/
    void initialization(EditText p_custom_port, Button p_bridge_button, AppCompatActivity p_context,RadioButton p_bridge_obfs,RadioButton p_bridge_china,RadioButton p_bridge_custom, ImageView p_custom_bridge_blocker){
        this.m_context = p_context;
        this.m_bridge_obfs = p_bridge_obfs;
        this.m_bridge_china = p_bridge_china;
        this.m_bridge_custom = p_bridge_custom;
        this.m_bridge_button = p_bridge_button;
        this.m_custom_port = p_custom_port;
        this.m_custom_bridge_blocker = p_custom_bridge_blocker;

        initPostUI();
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = m_context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(m_context.getResources().getColor(R.color.blue_dark));
            }
            else {
                m_context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                m_context.getWindow().setStatusBarColor(ContextCompat.getColor(m_context, R.color.white));
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
        animateColor(m_bridge_obfs, m_bridge_obfs.getCurrentTextColor(), m_context.getResources().getColor(R.color.float_white), "textColor", p_duration);
        animateColor(m_bridge_custom, m_bridge_custom.getCurrentTextColor(), m_context.getResources().getColor(R.color.float_white), "textColor", p_duration);
        animateColor(m_bridge_china, m_bridge_china.getCurrentTextColor(), m_context.getResources().getColor(R.color.float_white), "textColor", p_duration);

        m_bridge_obfs.setHighlightColor(m_context.getResources().getColor(R.color.float_white));
        m_bridge_custom.setHighlightColor(m_context.getResources().getColor(R.color.float_white));
        m_bridge_china.setHighlightColor(m_context.getResources().getColor(R.color.float_white));

        m_bridge_obfs.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(m_context, R.color.float_white)));
        m_bridge_custom.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(m_context, R.color.float_white)));
        m_bridge_china.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(m_context, R.color.float_white)));

        helperMethod.hideKeyboard(m_context);
        m_custom_port.clearFocus();
        m_custom_port.animate().setDuration(p_duration).alpha(0.2f);
        m_bridge_button.animate().setDuration(p_duration).alpha(0.2f);
        m_custom_bridge_blocker.setVisibility(View.VISIBLE);
    }

    private void initViews(String p_bridge, int p_duration){
        resetRadioButtons(p_duration);
        switch (p_bridge) {
            case strings.CUSTOM_BRIDGE_OBFS4:

                animateColor(m_bridge_china, m_bridge_china.getCurrentTextColor(), m_context.getResources().getColor(R.color.black), "textColor", p_duration);
                m_bridge_china.setHighlightColor(Color.BLACK);
                m_bridge_china.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(m_context, R.color.cursor_blue)));
                m_bridge_china.setChecked(true);
                break;
            case strings.CUSTOM_BRIDGE_MEEK:
                animateColor(m_bridge_obfs, m_bridge_obfs.getCurrentTextColor(), m_context.getResources().getColor(R.color.black), "textColor", p_duration);
                m_bridge_obfs.setHighlightColor(Color.BLACK);
                m_bridge_obfs.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(m_context, R.color.cursor_blue)));
                m_bridge_obfs.setChecked(true);
                break;
            case strings.CUSTOM_BRIDGE_CUSTOM:
                animateColor(m_bridge_custom, m_bridge_custom.getCurrentTextColor(), m_context.getResources().getColor(R.color.black), "textColor", p_duration);
                m_bridge_custom.setHighlightColor(Color.BLACK);
                m_bridge_custom.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(m_context, R.color.cursor_blue)));
                m_bridge_custom.setChecked(true);

                m_custom_port.animate().setDuration(p_duration).alpha(1f);
                m_bridge_button.animate().setDuration(p_duration).alpha(1f);
                m_custom_bridge_blocker.setVisibility(View.GONE);
                break;
        }
    }

    public void onTrigger(bridgeEnums.eBridgeViewCommands p_commands, List<Object> p_data){
        if(p_commands == bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS){
            initViews((String) p_data.get(0), (int)p_data.get(1));
        }
    }

}
