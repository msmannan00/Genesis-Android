package com.darkweb.genesissearchengine.appManager.historyManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import java.util.List;
import java.util.Objects;

class historyViewController
{
    /*Private Variables*/
    private AppCompatActivity mContext;
    private ImageView mEmptyListNotification;
    private EditText mSearchInput;
    private RecyclerView mRecycleView;
    private Button mClearButton;
    private ImageButton mMenuButton;
    private ImageButton mSearchButton;
    private PopupWindow mPopupWindow = null;

    /*Private Local Variables*/
    private Paint mPainter = new Paint();

    /*Initializations*/

    historyViewController(ImageView pEmptyListNotification, EditText pSearchInput, RecyclerView pRecycleView, Button pClearButton,AppCompatActivity pContext,ImageButton pMenuButton,ImageButton pSearchButton)
    {
        this.mEmptyListNotification = pEmptyListNotification;
        this.mSearchInput = pSearchInput;
        this.mRecycleView = pRecycleView;
        this.mClearButton = pClearButton;
        this.mContext = pContext;
        this.mMenuButton = pMenuButton;
        this.mSearchButton = pSearchButton;

        initPostUI();
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

    private void updateIfListEmpty(int pSize,int pDuration){
        if(pSize>0){
            mClearButton.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_setting_heading));
            mEmptyListNotification.animate().setDuration(pDuration).alpha(0f);
            mClearButton.setText(strings.HISTORY_CLEAR_HISTORY);
            mClearButton.setClickable(true);
        }
        else {
            mClearButton.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_v3));
            mEmptyListNotification.animate().setDuration(pDuration).alpha(1f);

            mClearButton.animate().setDuration(pDuration).alpha(0.4f);
            mSearchButton.animate().setDuration(pDuration).alpha(0f);
            mMenuButton.animate().setDuration(pDuration).alpha(0f);

            mClearButton.setEnabled(false);
            mSearchButton.setClickable(false);
            mMenuButton.setClickable(false);
            mSearchInput.setVisibility(View.GONE);
            mClearButton.animate().setDuration(300).alpha(1);

            mClearButton.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_v3));
            mClearButton.setText(strings.HISTORY_NO_HISTORY_FOUND);
            mClearButton.setClickable(false);
        }
    }

    private void onCloseMenu(){
        if(mPopupWindow !=null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
        onSelectionMenu(true);
    }

    private void onSelectionMenu(boolean pStatus){
        if(!pStatus){
            mClearButton.setClickable(false);
            mClearButton.animate().cancel();
            mClearButton.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_v3));
            mClearButton.animate().setDuration(200).alpha(0.4f);
            mMenuButton.setVisibility(View.VISIBLE);
            mSearchButton.setVisibility(View.GONE);
            if (mSearchInput.getVisibility() == View.VISIBLE){
                onHideSearch();
            }
        }else {
            if (mSearchInput.getVisibility() != View.VISIBLE) {
                mClearButton.setClickable(true);
                mClearButton.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_setting_heading));
                mClearButton.animate().cancel();
                mClearButton.animate().setDuration(200).alpha(1);
            }
            mMenuButton.setVisibility(View.GONE);
            mSearchButton.setVisibility(View.VISIBLE);

        }

    }

    private void updateList(){
        mRecycleView.getAdapter().notifyDataSetChanged();
    }

    private void removeFromList(int pIndex)
    {
        Objects.requireNonNull(mRecycleView.getAdapter()).notifyItemRemoved(pIndex);
        mRecycleView.getAdapter().notifyItemRangeChanged(pIndex, mRecycleView.getAdapter().getItemCount());
    }

    private void clearList(){
        Objects.requireNonNull(mRecycleView.getAdapter()).notifyDataSetChanged();
        updateIfListEmpty(mRecycleView.getAdapter().getItemCount(),300);
        mSearchInput.clearFocus();
        mSearchInput.setText(strings.GENERIC_EMPTY_STR);
        mClearButton.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_v3));
    }

    private boolean onHideSearch() {
        if(mSearchInput.getVisibility() == View.VISIBLE){
            mSearchInput.animate().cancel();
            mSearchInput.animate().alpha(0).setDuration(150).withEndAction(() -> {
                mSearchInput.getText().clear();
                mSearchInput.setVisibility(View.GONE);
                mSearchInput.setText(strings.GENERIC_EMPTY_STR);
            });
            mSearchInput.setText(strings.GENERIC_EMPTY_STR);
            mSearchInput.setClickable(false);
            mClearButton.setClickable(true);
            mClearButton.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_setting_heading));
            mClearButton.animate().cancel();
            mClearButton.animate().setDuration(150).alpha(1f);
            return false;
        }else {
            mSearchInput.animate().cancel();
            mSearchInput.setAlpha(0f);
            mSearchInput.animate().setDuration(150).alpha(1);
            mSearchInput.setVisibility(View.VISIBLE);
            mSearchInput.setClickable(true);
            mClearButton.setClickable(false);
            mSearchInput.requestFocus();
            mClearButton.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_v3));
            mClearButton.animate().cancel();
            mClearButton.animate().setDuration(150).alpha(0.4f);
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return true;
        }
    }

    private void onLongPressMenu(View pView) {
        mPopupWindow = helperMethod.onCreateMenu(pView, R.layout.recyclerview__menu);
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

    public Object onTrigger(historyEnums.eHistoryViewCommands pCommands, List<Object> pData){
        if(pCommands == historyEnums.eHistoryViewCommands.M_UPDATE_LIST_IF_EMPTY){
            updateIfListEmpty((int)pData.get(0), (int)pData.get(1));
        }
        else if(pCommands == historyEnums.eHistoryViewCommands.M_UPDATE_LIST){
            updateList();
        }
        else if(pCommands == historyEnums.eHistoryViewCommands.M_REMOVE_FROM_LIST){
            removeFromList((int)pData.get(0));
        }
        else if(pCommands == historyEnums.eHistoryViewCommands.M_CLEAR_LIST){
            clearList();
        }
        else if(pCommands == historyEnums.eHistoryViewCommands.M_VERTIFY_SELECTION_MENU){
            onSelectionMenu((boolean)pData.get(0));
        }
        else if(pCommands == historyEnums.eHistoryViewCommands.M_HIDE_SEARCH){
            return onHideSearch();
        }
        else if(pCommands == historyEnums.eHistoryViewCommands.M_CLOSE_MENU){
            onCloseMenu();
        }
        else if(pCommands == historyEnums.eHistoryViewCommands.M_LONG_PRESS_MENU){
            onLongPressMenu((View) pData.get(0));
        }else if(pCommands.equals(historyEnums.eHistoryViewCommands.ON_GENERATE_SWIPABLE_BACKGROUND)){
            onDrawSwipableBackground((Canvas)pData.get(0), (RecyclerView.ViewHolder)pData.get(1), (float)pData.get(2), (int)pData.get(3));
        }
        return null;
    }

}
