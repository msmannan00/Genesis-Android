package com.hiddenservices.onionservices.appManager.tabManager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.R;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class tabViewController {
    /*Private Views*/
    private Fragment mContext;
    private PopupWindow mTabOptionMenu = null;
    private Button mTabs;
    private ImageView mRemoveSelection;
    private ImageButton mMenuButton;
    private ImageButton mClearSelection;
    private View mUndoLayout;
    private TextView mSelectionCount;
    private ImageView mBlocker;
    private NestedScrollView mNestedScrollView;
    private ImageButton mNewTab;

    /*Private Local Variables*/
    private Handler mDelayHandler = new Handler();
    private Paint mPainter = new Paint();

    /*Initializations*/

    public tabViewController(Fragment mContext, Button pTabs, ImageView pRemoveSelection, ImageButton pMenuButton, ImageButton pClearSelection, View pToastLayoutRoot, TextView pSelectionCount, ImageView pBlocker, RecyclerView pRecycleView, NestedScrollView pNestedScrollView, TextView pEmptyView, ImageButton pNewTab) {
        this.mContext = mContext;
        this.mTabs = pTabs;
        this.mRemoveSelection = pRemoveSelection;
        this.mMenuButton = pMenuButton;
        this.mClearSelection = pClearSelection;
        this.mUndoLayout = pToastLayoutRoot;
        this.mSelectionCount = pSelectionCount;
        this.mBlocker = pBlocker;
        this.mNestedScrollView = pNestedScrollView;
        this.mNewTab = pNewTab;

        initUI();
        initPostUI();
        onHoldInteraction();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onHoldInteraction() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> mBlocker.setVisibility(View.GONE), 250);
    }

    private void initTabCount(int pCount) {
        mTabs.setText(String.valueOf(pCount));
    }

    public void initUI() {
        mMenuButton.setVisibility(View.VISIBLE);

        if (status.sTabGridLayoutEnabled) {
            mNestedScrollView.setPadding(helperMethod.pxFromDp(7), helperMethod.pxFromDp(7), helperMethod.pxFromDp(7), helperMethod.pxFromDp(0));
        } else {
            mNestedScrollView.setPadding(0, 0, 0, 0);
        }

        onHideUndoDialogInit();
    }

    public void initExitUI() {
        //mMenuButton.setVisibility(View.GONE);
    }

    private void initPostUI() {
    }

    public void onOpenTabMenu(View view, int pSize) {
        onCloseTabMenu();
        LayoutInflater layoutInflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View mPopupView = layoutInflater.inflate(R.layout.tab_menu, null);
        mTabOptionMenu = new PopupWindow(mPopupView, ActionMenuView.LayoutParams.WRAP_CONTENT, ActionMenuView.LayoutParams.WRAP_CONTENT, true);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mTabOptionMenu.setOutsideTouchable(true);
        mTabOptionMenu.setFocusable(true);
        mTabOptionMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mTabOptionMenu.setAnimationStyle(R.style.popup_window_animation);
        mTabOptionMenu.setElevation(7);
        if (status.sSettingLanguageRegion.equals("Ur")) {
            mTabOptionMenu.showAsDropDown(view, helperMethod.pxFromDp(-45), helperMethod.pxFromDp(-55));
        } else {
            mTabOptionMenu.showAsDropDown(view, helperMethod.pxFromDp(-125), helperMethod.pxFromDp(-55));
        }
        if (pSize <= 0) {
            Button mTabClear = mPopupView.findViewById(R.id.pCloseTabTrigger);
            LinearLayout mCloseTab = mPopupView.findViewById(R.id.pPopupRateusClose);
            mTabClear.setAlpha(0.35f);
            mCloseTab.setEnabled(false);
        }
    }

    private void onCloseTabMenu() {
        if (mTabOptionMenu != null) {
            mTabOptionMenu.dismiss();
        }
    }

    private void onShowSelection() {
        if (mSelectionCount.getVisibility() == View.GONE) {
            String mText = 0 + " Selected";
            mSelectionCount.setText(mText);
        }

        mMenuButton.setVisibility(View.GONE);
        mSelectionCount.setVisibility(View.VISIBLE);
        mClearSelection.setVisibility(View.VISIBLE);

        mNewTab.setVisibility(View.GONE);
        mNewTab.animate().setDuration(250).alpha(0);
        mNewTab.setEnabled(false);
        mNewTab.setFocusable(false);
    }

    private void onShowSelectionMenu(boolean pStatus, int pCount) {
        if (pStatus || pCount > 0) {
            mRemoveSelection.setVisibility(View.VISIBLE);
        } else {
            mRemoveSelection.setVisibility(View.GONE);
        }

        String mText = pCount + " Selected";
        mSelectionCount.setText(mText);
        mTabs.setAlpha(0);
    }

    private void onHideSelectionMenu() {
        mSelectionCount.setVisibility(View.GONE);
        mRemoveSelection.setVisibility(View.GONE);
        mClearSelection.setVisibility(View.GONE);
        mMenuButton.setVisibility(View.VISIBLE);
        mTabs.animate().setStartDelay(0).setDuration(250).alpha(1);

        mNewTab.setVisibility(View.VISIBLE);
        mNewTab.animate().setDuration(250).alpha(1);
        mNewTab.setEnabled(true);
        mNewTab.setFocusable(true);
    }

    private void onHideUndoDialogForced() {
        if (mUndoLayout.getAlpha() < 1) {
            mUndoLayout.animate().cancel();
            mUndoLayout.setAlpha(0);
            mUndoLayout.setVisibility(View.GONE);
        } else {
            mUndoLayout.animate().setDuration(200).alpha(0).withEndAction(() -> mUndoLayout.setVisibility(View.GONE));
        }
        mDelayHandler.removeCallbacksAndMessages(null);
    }

    private void onShowUndoDialog() {
        if (mUndoLayout.getAlpha() == 0) {
            mUndoLayout.setAlpha(0);
            mUndoLayout.setVisibility(View.VISIBLE);
            mDelayHandler.removeCallbacksAndMessages(null);
            mUndoLayout.animate().setDuration(200).alpha(1).withEndAction(() -> {
                mDelayHandler.postDelayed(() -> {
                    mUndoLayout.animate().setDuration(200).alpha(0);
                }, 2000);
            });
        }
    }

    private void onHideUndoDialogInit() {
        mUndoLayout.animate().cancel();
        mUndoLayout.setAlpha(0);
        mUndoLayout.setVisibility(View.GONE);
    }

    public void blockUI(boolean pStatus) {
        if (pStatus) {
            mBlocker.setVisibility(View.VISIBLE);
        } else {
            mBlocker.setVisibility(View.GONE);
        }
    }

    private void onDrawSwipableBackground(Canvas pCanvas, RecyclerView.ViewHolder pViewHolder, float pDX, int pActionState) {

        Bitmap icon;
        if (pActionState == ItemTouchHelper.ANIMATION_TYPE_SWIPE_SUCCESS) {
            View itemView = pViewHolder.itemView;
            itemView.animate().alpha(0f);
        } else if (pActionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = pViewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            if (pDX > 0) {
                if (status.sTheme == enums.Theme.THEME_DARK) {
                    pCanvas.drawARGB(255, 28, 27, 33);
                } else {
                    pCanvas.drawARGB(255, 255, 255, 255);
                }
                if (!status.sTabGridLayoutEnabled) {
                    icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dustbin);
                    RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                    pCanvas.drawBitmap(icon, null, icon_dest, mPainter);
                }
            } else {
                if (status.sTheme == enums.Theme.THEME_DARK) {
                    pCanvas.drawARGB(255, 28, 27, 33);
                } else {
                    pCanvas.drawARGB(255, 255, 255, 255);
                }
                if (!status.sTabGridLayoutEnabled) {
                    icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dustbin);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    pCanvas.drawBitmap(icon, null, icon_dest, mPainter);
                }
            }
        }
    }

    public Object onTrigger(tabEnums.eTabViewCommands pCommands, List<Object> pData) {
        if (pCommands.equals(tabEnums.eTabViewCommands.M_SHOW_MENU)) {
            onOpenTabMenu((View) pData.get(0), (int) pData.get(1));
        } else if (pCommands.equals(tabEnums.eTabViewCommands.M_DISMISS_MENU)) {
            onCloseTabMenu();
        } else if (pCommands.equals(tabEnums.eTabViewCommands.INIT_TAB_COUNT)) {
            initTabCount((int) pData.get(0));
        } else if (pCommands.equals(tabEnums.eTabViewCommands.ON_HIDE_SELECTION)) {
            onHideSelectionMenu();
        } else if (pCommands.equals(tabEnums.eTabViewCommands.ON_SHOW_SELECTION_MENU)) {
            onShowSelectionMenu((boolean) pData.get(0), (int) pData.get(1));
        } else if (pCommands.equals(tabEnums.eTabViewCommands.ON_SHOW_SELECTION)) {
            onShowSelection();
        } else if (pCommands.equals(tabEnums.eTabViewCommands.ON_GENERATE_SWIPABLE_BACKGROUND)) {
            onDrawSwipableBackground((Canvas) pData.get(0), (RecyclerView.ViewHolder) pData.get(1), (float) pData.get(2), (int) pData.get(3));
        } else if (pCommands.equals(tabEnums.eTabViewCommands.ON_EXIT)) {
            initExitUI();
        } else if (pCommands.equals(tabEnums.eTabViewCommands.ON_HIDE_UNDO_DIALOG_INIT)) {
            onHideUndoDialogInit();
        } else if (pCommands.equals(tabEnums.eTabViewCommands.ON_HOLD_BLOCKER)) {
            blockUI(true);
        } else if (pCommands.equals(tabEnums.eTabViewCommands.ON_RELEASE_BLOCKER)) {
            blockUI(false);
        } else if (pCommands.equals(tabEnums.eTabViewCommands.ON_SHOW_UNDO_DIALOG)) {
            onShowUndoDialog();
        } else if (pCommands.equals(tabEnums.eTabViewCommands.ON_HIDE_UNDO_DIALOG)) {
            onHideUndoDialogForced();
        } else if (pCommands.equals(tabEnums.eTabViewCommands.ON_INIT_UI)) {
            initUI();
        }
        return null;
    }
}
