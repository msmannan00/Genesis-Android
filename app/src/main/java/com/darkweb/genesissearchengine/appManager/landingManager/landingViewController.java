package com.darkweb.genesissearchengine.appManager.landingManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.helperManager.animatedColor;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;

class landingViewController
{
    /*Private Variables*/
    private AppCompatActivity mContext;

    /*Initializations*/

    landingViewController(AppCompatActivity mContext, eventObserver.eventListener event){
        this.mContext = mContext;


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
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
            }
        }
    }
}
