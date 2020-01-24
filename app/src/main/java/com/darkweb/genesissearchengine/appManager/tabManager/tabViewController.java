package com.darkweb.genesissearchengine.appManager.tabManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;

import java.util.Objects;

class tabViewController
{
    /*Private Variables*/
    private AppCompatActivity mContext;

    private ImageView mEmptyListNotifier;
    private RecyclerView mListView;
    private Button mClearButton;

    /*Initializations*/

    tabViewController(ImageView mEmptyListNotifier, RecyclerView mListView, Button mClearButton, AppCompatActivity mContext)
    {
        this.mContext = mContext;
        this.mEmptyListNotifier = mEmptyListNotifier;
        this.mListView = mListView;
        this.mClearButton = mClearButton;

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
        }
        else {
            mEmptyListNotifier.animate().setDuration(duration).alpha(1f);
            mClearButton.animate().setDuration(duration).alpha(0f);
        }
    }

    void removeFromList(int index)
    {
        Objects.requireNonNull(mListView.getAdapter()).notifyItemRemoved(index);
        mListView.getAdapter().notifyItemRangeChanged(index, mListView.getAdapter().getItemCount()-1);
    }

}
