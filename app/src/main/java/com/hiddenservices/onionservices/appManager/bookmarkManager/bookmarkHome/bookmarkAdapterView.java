package com.hiddenservices.onionservices.appManager.bookmarkManager.bookmarkHome;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.example.myapplication.R;
import java.util.List;

public class bookmarkAdapterView
{
    /*Private Variables*/
    private AppCompatActivity mContext;

    bookmarkAdapterView(AppCompatActivity pContext)
    {
        this.mContext = pContext;
    }

    public Object openMenu(PopupWindow pPopupWindow, View pView, View popupView){

        if(pPopupWindow !=null){
            pPopupWindow.dismiss();
        }

        pPopupWindow = new PopupWindow(
                popupView,
                ActionMenuView.LayoutParams.WRAP_CONTENT,
                ActionMenuView.LayoutParams.WRAP_CONTENT, true);

        pView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int xOffset = -(pView.getMeasuredWidth() - pView.getWidth());

        int[] location = new int[2];
        pView.getLocationOnScreen(location);
        int y = location[1];
        int height = helperMethod.getScreenHeight(mContext);
        int m_offset_height = 0;
        if(y + helperMethod.pxFromDp(300) >height){
            m_offset_height = helperMethod.pxFromDp(203);
        }
        else{
            m_offset_height = 0;
        }

        pPopupWindow.setOutsideTouchable(true);
        pPopupWindow.setFocusable(true);
        pPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pPopupWindow.setAnimationStyle(R.style.popup_window_animation);
        pPopupWindow.setElevation(7);
        if(status.sSettingLanguageRegion.equals("Ur")){
            pPopupWindow.showAsDropDown(pView,0, helperMethod.pxFromDp(-45));
        }else {
            pPopupWindow.showAsDropDown(pView,xOffset - 90, -m_offset_height-helperMethod.pxFromDp(50));
        }
        return pPopupWindow;
    }

    private void clearLongSelectedURL(ImageButton pPopupMenu, ImageView pLogoImage, View mItemView){
        mItemView.setPressed(false);
        pPopupMenu.setVisibility(View.VISIBLE);
        pPopupMenu.animate().setDuration(150).alpha(1);
        pPopupMenu.setClickable(true);
        pLogoImage.setAlpha(1f);
        pLogoImage.animate().cancel();
        pLogoImage.animate().setDuration(150).alpha(0).withEndAction(() -> pLogoImage.setVisibility(View.GONE));
    }

    public void onSelectView(View pItemView, View pMenuItem, ImageView pLogoImage, boolean pIsForced, boolean pVibrate){
        pItemView.setPressed(false);
        int speed = 150;
        if(pIsForced){
            speed=150;
        }
        if(pVibrate){
            helperMethod.vibrate(mContext);
        }


        pLogoImage.setAlpha(0f);
        pLogoImage.setVisibility(View.VISIBLE);
        pLogoImage.animate().cancel();
        pLogoImage.animate().setDuration(speed).alpha(0.95f);
    }

    public void onClearHighlight( View pItemView, View pMenuItem, ImageView pLogoImage, boolean pIsForced)
    {
        try {
            if(pLogoImage.getAlpha()>0){
                pItemView.setPressed(false);
                int speed = 150;
                if(pIsForced){
                    speed = 0;
                }
                pLogoImage.setAlpha(1f);
                pLogoImage.animate().cancel();
                pLogoImage.animate().setDuration(speed).alpha(0).withEndAction(() -> pLogoImage.setVisibility(View.GONE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object onTrigger(bookmarkEnums.eBookmarkViewAdapterCommands pCommands, List<Object> pData){
        if(pCommands == bookmarkEnums.eBookmarkViewAdapterCommands.M_OPEN_MENU){
            return openMenu((PopupWindow) pData.get(0), (View) pData.get(1), (View) pData.get(2));
        }
        if(pCommands == bookmarkEnums.eBookmarkViewAdapterCommands.M_CLEAR_LONG_SELECTED_VIEW){
            clearLongSelectedURL((ImageButton)pData.get(0), (ImageView)pData.get(1), (View)pData.get(2));
        }
        if(pCommands == bookmarkEnums.eBookmarkViewAdapterCommands.M_SELECT_VIEW){
            onSelectView((View)pData.get(0), (View)pData.get(1), (ImageView)pData.get(2), (Boolean) pData.get(3), (Boolean) pData.get(4));
        }
        if(pCommands == bookmarkEnums.eBookmarkViewAdapterCommands.M_CLEAR_HIGHLIGHT){
            onClearHighlight((View)pData.get(0), (View)pData.get(1), (ImageView)pData.get(2), (Boolean) pData.get(3));
        }
        return null;
    }

}
