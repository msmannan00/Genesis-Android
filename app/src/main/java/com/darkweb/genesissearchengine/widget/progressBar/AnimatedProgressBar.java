package com.darkweb.genesissearchengine.widget.progressBar;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

public class AnimatedProgressBar extends ProgressBar {

  private static final Interpolator DEFAULT_INTERPOLATER = new LinearInterpolator();

  private ValueAnimator animator;
  private boolean animate = true;

  public AnimatedProgressBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public AnimatedProgressBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AnimatedProgressBar(Context context) {
    super(context);
  }

  public boolean isAnimate() {
    return animate;
  }

  public void setAnimate(boolean animate) {
    this.animate = animate;
  }

  @Override
  public synchronized void setProgress(int progress) {
    if (animator != null){
      animator.cancel();
    }
    if (animator == null) {
      animator = ValueAnimator.ofInt(getProgress(), progress);
      animator.setInterpolator(DEFAULT_INTERPOLATER);
      animator.setDuration(100);
      animator.addUpdateListener(animation -> AnimatedProgressBar.super.setProgress((Integer) animation.getAnimatedValue()));
    } else{
      animator.setIntValues(getProgress(), progress);
    }
    animator.start();

  }

  @Override
  public synchronized void setSecondaryProgress(int secondaryProgress) {
    /*if (!animate) {
      super.setSecondaryProgress(secondaryProgress);
      return;
    }
    if (animatorSecondary != null)
      animatorSecondary.cancel();
    if (animatorSecondary == null) {
      animatorSecondary = ValueAnimator.ofInt(getProgress(), secondaryProgress);
      animatorSecondary.setInterpolator(DEFAULT_INTERPOLATER);
      animatorSecondary.addUpdateListener(new AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          AnimatedProgressBar.super.setSecondaryProgress((Integer) animation
                  .getAnimatedValue());
        }
      });
    } else
      animatorSecondary.setIntValues(getProgress(), secondaryProgress);
    animatorSecondary.start();*/
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    //if (animator != null)
      //animator.cancel();
    //if (animatorSecondary != null)
      //animatorSecondary.cancel();
  }

}