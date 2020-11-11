package com.darkweb.genesissearchengine.appManager.tabManager;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import java.util.List;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class tabViewController
{
    /*Private Variables*/
    private AppCompatActivity mContext;
    private PopupWindow mTabOptionMenu = null;
    private Button mTabs;

    /*Initializations*/

    tabViewController(AppCompatActivity mContext, Button pTabs)
    {
        this.mContext = mContext;
        this.mTabs = pTabs;

        initPostUI();
        initUI();
    }

    private void initUI(){
        initTabCount();
    }

    private void initTabCount(){
        mTabs.setText((((int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null))+ strings.GENERIC_EMPTY_STR));
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_text_v3));
            }
            else {
                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
                    mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
            }
        }
    }

    private void onOpenTabMenu(View view) {
        onCloseTabMenu();
        LayoutInflater layoutInflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View mPopupView = layoutInflater.inflate(R.layout.tab_menu, null);
        mTabOptionMenu = new PopupWindow( mPopupView, ActionMenuView.LayoutParams.WRAP_CONTENT, ActionMenuView.LayoutParams.WRAP_CONTENT, true);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mTabOptionMenu.setOutsideTouchable(true);
        mTabOptionMenu.setFocusable(true);
        mTabOptionMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mTabOptionMenu.setAnimationStyle(R.style.popup_window_animation);
        mTabOptionMenu.setElevation(7);
        mTabOptionMenu.showAsDropDown(view,0, helperMethod.pxFromDp(-45));
    }

    private void onCloseTabMenu() {
        if(mTabOptionMenu!=null){
            mTabOptionMenu.dismiss();
        }
    }

    public void onTrigger(tabEnums.eTabViewCommands pCommands, List<Object> pData){
        if(pCommands.equals(tabEnums.eTabViewCommands.M_SHOW_MENU)){
            onOpenTabMenu((View) pData.get(0));
        }else if(pCommands.equals(tabEnums.eTabViewCommands.M_DISMISS_MENU)){
            onCloseTabMenu();
        }else if(pCommands.equals(tabEnums.eTabViewCommands.INIT_TAB_COUNT)){
            initTabCount();
        }
    }

}
