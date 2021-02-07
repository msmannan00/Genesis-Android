package com.darkweb.genesissearchengine.appManager.tabManager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import java.util.List;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class tabViewController
{
    /*Private Views*/
    private AppCompatActivity mContext;
    private PopupWindow mTabOptionMenu = null;
    private Button mTabs;
    private ImageView mRemoveSelection;
    private ImageButton mMenuButton;
    private ImageButton mClearSelection;
    private View mToastLayoutRoot;
    private TextView mSelectionCount;
    private ImageView mBlocker;

    /*Private Local Variables*/
    private Handler mDelayHandler = new Handler();
    private Paint mPainter = new Paint();

    /*Initializations*/

    tabViewController(AppCompatActivity mContext, Button pTabs, ImageView pRemoveSelection, ImageButton pMenuButton, ImageButton pClearSelection, View pToastLayoutRoot, TextView pSelectionCount, ImageView pBlocker)
    {
        this.mContext = mContext;
        this.mTabs = pTabs;
        this.mRemoveSelection = pRemoveSelection;
        this.mMenuButton = pMenuButton;
        this.mClearSelection = pClearSelection;
        this.mToastLayoutRoot = pToastLayoutRoot;
        this.mSelectionCount = pSelectionCount;
        this.mBlocker = pBlocker;

        initPostUI();
        onHoldInteraction();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onHoldInteraction(){
        final Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            mBlocker.setVisibility(View.GONE);
        }, 350);
    }

    private void initTabCount(int pCount){
        mTabs.setText(String.valueOf(pCount));
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

    public void onOpenTabMenu(View view) {
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
        mTabOptionMenu.showAsDropDown(view,helperMethod.pxFromDp(-125), helperMethod.pxFromDp(-45));
    }

    private void onCloseTabMenu() {
        if(mTabOptionMenu!=null){
            mTabOptionMenu.dismiss();
        }
    }

    private void onShowSelection() {
        if(mSelectionCount.getVisibility() == View.GONE){
            mSelectionCount.setText(0 + " Selected");
        }

        mMenuButton.setVisibility(View.GONE);
        mSelectionCount.setVisibility(View.VISIBLE);
        mClearSelection.setVisibility(View.VISIBLE);
    }

    private void onShowSelectionMenu(boolean pStatus, int pCount) {
        if(pStatus || pCount>0){
            mRemoveSelection.setVisibility(View.VISIBLE);
        }else {
            mRemoveSelection.setVisibility(View.GONE);
        }
        mSelectionCount.setText(pCount + " Selected");
        mTabs.setAlpha(0);
    }

    private void onHideSelectionMenu() {
        mSelectionCount.setVisibility(View.GONE);
        mRemoveSelection.setVisibility(View.GONE);
        mClearSelection.setVisibility(View.GONE);
        mMenuButton.setVisibility(View.VISIBLE);
        mTabs.animate().setStartDelay(250).setDuration(200).alpha(1);
    }

    private void onShowUndoDialog(int pTabCount) {
        mToastLayoutRoot.findViewById(R.id.pBlockerUndo).setVisibility(View.GONE);
        mToastLayoutRoot.animate().cancel();
        mToastLayoutRoot.setVisibility(View.VISIBLE);
        mToastLayoutRoot.setAlpha(0);
        mToastLayoutRoot.animate().alpha(1);

        initTabCount(pTabCount);
        mDelayHandler.removeCallbacksAndMessages(null);
        mDelayHandler.postDelayed(() -> mToastLayoutRoot.animate().alpha(0).withEndAction(() -> {
            mToastLayoutRoot.setVisibility(View.GONE);
        }), 3000);
    }

    private void onHideUndoDialog() {
        mToastLayoutRoot.animate().cancel();
        mToastLayoutRoot.animate().alpha(0).withEndAction(() -> mToastLayoutRoot.setVisibility(View.GONE));
    }

    private void onDrawSwipableBackground(Canvas pCanvas, RecyclerView.ViewHolder pViewHolder, float pDX, int pActionState) {

        Bitmap icon;
        if(pActionState == ItemTouchHelper.ANIMATION_TYPE_SWIPE_SUCCESS){
            View itemView = pViewHolder.itemView;
            itemView.animate().alpha(0f);
        }
        else if(pActionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            View itemView = pViewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            if(pDX > 0){
                mPainter.setColor(ContextCompat.getColor(mContext, R.color.c_list_item_current));
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), pDX,(float) itemView.getBottom());
                pCanvas.drawRect(background, mPainter);
                icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dustbin);
                RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                pCanvas.drawBitmap(icon,null,icon_dest, mPainter);
            } else {
                mPainter.setColor(ContextCompat.getColor(mContext, R.color.c_list_item_current));
                RectF background = new RectF((float) itemView.getRight() + pDX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                pCanvas.drawRect(background, mPainter);
                icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dustbin);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                pCanvas.drawBitmap(icon,null,icon_dest, mPainter);
            }
        }
    }

    public Object onTrigger(tabEnums.eTabViewCommands pCommands, List<Object> pData){
        if(pCommands.equals(tabEnums.eTabViewCommands.M_SHOW_MENU)){
            onOpenTabMenu((View) pData.get(0));
        }else if(pCommands.equals(tabEnums.eTabViewCommands.M_DISMISS_MENU)){
            onCloseTabMenu();
        }else if(pCommands.equals(tabEnums.eTabViewCommands.INIT_TAB_COUNT)){
            initTabCount((int)pData.get(0));
        }else if(pCommands.equals(tabEnums.eTabViewCommands.ON_HIDE_SELECTION)){
            onHideSelectionMenu();
        }
        else if(pCommands.equals(tabEnums.eTabViewCommands.ON_SHOW_SELECTION_MENU)){
            onShowSelectionMenu((boolean) pData.get(0), (int) pData.get(1));
        }
        else if(pCommands.equals(tabEnums.eTabViewCommands.ON_SHOW_SELECTION)){
            onShowSelection();
        }else if(pCommands.equals(tabEnums.eTabViewCommands.ON_SHOW_UNDO_DIALOG)){
            onShowUndoDialog((int)pData.get(0));
        }else if(pCommands.equals(tabEnums.eTabViewCommands.ON_HIDE_UNDO_DIALOG)){
            onHideUndoDialog();
        }else if(pCommands.equals(tabEnums.eTabViewCommands.ON_GENERATE_SWIPABLE_BACKGROUND)){
            onDrawSwipableBackground((Canvas)pData.get(0), (RecyclerView.ViewHolder)pData.get(1), (float)pData.get(2), (int)pData.get(3));
        }
        return null;
    }
}
