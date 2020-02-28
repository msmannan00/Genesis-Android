package com.darkweb.genesissearchengine.appManager.landingManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
                window.setStatusBarColor(mContext.getResources().getColor(R.color.landing_ease_blue));
            }
            else {
                initStatusBarColor();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initStatusBarColor() {
        animatedColor oneToTwo = new animatedColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue), ContextCompat.getColor(mContext, R.color.landing_ease_blue));
        animatedColor twoToThree = new animatedColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue), ContextCompat.getColor(mContext, R.color.landing_ease_blue));
        animatedColor ThreeToFour = new animatedColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue), ContextCompat.getColor(mContext, R.color.landing_ease_blue));

        ValueAnimator animator = ObjectAnimator.ofFloat(0f, 1f).setDuration(0);
        animator.addUpdateListener(animation ->
        {
            float v = (float) animation.getAnimatedValue();
            mContext.getWindow().setStatusBarColor(oneToTwo.with(v));
            mContext.getWindow().setStatusBarColor(oneToTwo.with(v));
        });
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                final ValueAnimator animator2 = ObjectAnimator.ofFloat(0f, 1f).setDuration(217);
                animator2.addUpdateListener(animation1 ->
                {
                    float v = (float) animation1.getAnimatedValue();
                    mContext.getWindow().setStatusBarColor(twoToThree.with(v));
                });
                animator2.start();

                animator2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        final ValueAnimator animator3 = ObjectAnimator.ofFloat(0f, 1f).setDuration(0);
                        animator3.addUpdateListener(animation1 ->
                        {
                            float v = (float) animation1.getAnimatedValue();
                            mContext.getWindow().setStatusBarColor(ThreeToFour.with(v));

                        });
                        animator3.start();
                    }
                });
                animator2.start();

            }
        });
        animator.start();

    }



}
