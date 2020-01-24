package com.darkweb.genesissearchengine.appManager.historyManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.constants.strings;
import com.example.myapplication.R;

import java.util.Objects;

class historyViewController
{
    /*Private Variables*/
    private AppCompatActivity mContext;

    private ImageView mEmptyListNotifier;
    private EditText mSearchBar;
    private RecyclerView mListView;
    private Button mClearButton;
    private ImageButton mMoreButton;

    /*Initializations*/

    historyViewController(ImageView mEmptyListNotifier, EditText mSearchBar, RecyclerView mListView, Button mClearButton,ImageButton mMoreButton,AppCompatActivity mContext)
    {
        this.mContext = mContext;
        this.mEmptyListNotifier = mEmptyListNotifier;
        this.mSearchBar = mSearchBar;
        this.mListView = mListView;
        this.mClearButton = mClearButton;
        this.mMoreButton = mMoreButton;

        initPostUI();
    }

    private void initPostUI(){
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

    void updateIfListEmpty(int size,int duration){
        if(size>0){
            mEmptyListNotifier.animate().setDuration(duration).alpha(0f);
            mClearButton.animate().setDuration(duration).alpha(1f);
            mMoreButton.animate().setDuration(duration).alpha(1f);
        }
        else {
            mEmptyListNotifier.animate().setDuration(duration).alpha(1f);
            mClearButton.animate().setDuration(duration).alpha(0f);
            mMoreButton.animate().setDuration(duration).alpha(0f);
        }
    }

    void updateList(){
        int index = Objects.requireNonNull(mListView.getAdapter()).getItemCount()-1;
        mListView.getAdapter().notifyDataSetChanged();
        mListView.scrollToPosition(index);
    }

    void removeFromList(int index)
    {
        Objects.requireNonNull(mListView.getAdapter()).notifyItemRemoved(index);
        mListView.getAdapter().notifyItemRangeChanged(index, mListView.getAdapter().getItemCount());
    }

    void clearList(){
        Objects.requireNonNull(mListView.getAdapter()).notifyDataSetChanged();
        updateIfListEmpty(mListView.getAdapter().getItemCount(),300);
        mSearchBar.clearFocus();
        mSearchBar.setText(strings.EMPTY_STR);
    }

}
