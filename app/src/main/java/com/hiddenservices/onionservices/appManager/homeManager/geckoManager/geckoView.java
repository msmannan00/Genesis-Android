package com.hiddenservices.onionservices.appManager.homeManager.geckoManager;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import org.mozilla.geckoview.GeckoView;
import java.util.Collections;

public class geckoView extends GeckoView {
    private int mLastY;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedOffsetY;
    private NestedScrollingChildHelper mChildHelper;
    private eventObserver.eventListener mEvent;
    private boolean mScrollable = true;
    private int mSwipeDistance = 0;
    private boolean mPressed = false;
    private boolean mForcedScroll = false;
    private int mScrollOffsetRoot = -1;
    private int mBottomOffsetRange = 0;

    public void onSetHomeEvent(eventObserver.eventListener pEvent) {
        mEvent = pEvent;
    }

    public void onDestroy() {
        mEvent = null;
        mChildHelper = null;
    }

    public geckoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        setOverScrollMode(OVER_SCROLL_IF_CONTENT_SCROLLS);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final MotionEvent event = MotionEvent.obtain(ev);
        final int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsetY = 0;
        }


        final int eventY = (int) event.getY();
        event.offsetLocation(0, mNestedOffsetY);

        if (event.getPointerCount() > 1 && !status.sSettingEnableZoom) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE:

                boolean mBottomReached = false;
                Object mTemp = mEvent.invokeObserver(Collections.singletonList(null), homeEnums.eGeckoCallback.WAS_SCROLL_CHANGED);

                int deltaY = mLastY - eventY;
                mSwipeDistance += deltaY;

                if (mTemp != null && (int) mTemp == mScrollOffsetRoot) {
                    mBottomOffsetRange += 1;
                    if (mBottomOffsetRange > 5) {
                        mBottomReached = true;
                    }
                } else {
                    mBottomOffsetRange = 0;
                    mBottomReached = false;
                }

                if (mTemp != null) {
                    mScrollOffsetRoot = (int) mTemp;
                }

                if (mSwipeDistance >= 350 || mSwipeDistance <= -350) {
                    mScrollable = true;
                }

                final boolean allowScroll = status.sFullScreenBrowsing && (mScrollOffsetRoot > 0 && mScrollable || mForcedScroll || mBottomReached);


                if (allowScroll && dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    event.offsetLocation(0, -mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];

                }


                mLastY = eventY - mScrollOffset[1];

                if (allowScroll && dispatchNestedScroll(0, mScrollOffset[1], 0, deltaY, mScrollOffset)) {
                    mLastY -= mScrollOffset[1];
                    event.offsetLocation(0, mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                }

                break;

            case MotionEvent.ACTION_DOWN:
                mSwipeDistance = 0;
                mScrollable = false;
                mLastY = eventY;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                mEvent.invokeObserver(Collections.singletonList(null), homeEnums.eGeckoCallback.GECKO_SCROLL_DOWN);
                mPressed = true;

                new Handler().postDelayed(() ->
                {
                    if (!mPressed && status.sFullScreenBrowsing && !status.sDisableExpandTemp) {
                        if (mSwipeDistance > 75) {
                            mEvent.invokeObserver(Collections.singletonList(null), homeEnums.eGeckoCallback.GECKO_SCROLL_UP_MOVE);
                            mScrollable = true;
                        } else if (mSwipeDistance < -75) {
                            mEvent.invokeObserver(Collections.singletonList(null), homeEnums.eGeckoCallback.GECKO_SCROLL_DOWN_MOVE);
                            mScrollable = true;
                        }
                    }
                }, 100);

                break;

            case MotionEvent.ACTION_UP:
                mPressed = false;
                mEvent.invokeObserver(Collections.singletonList(null), homeEnums.eGeckoCallback.GECKO_SCROLL_UP_ALWAYS);
            case MotionEvent.ACTION_CANCEL:
                mPressed = false;
                stopNestedScroll();
                break;

            default:
                mPressed = false;
        }

        // Execute event handler from parent class in all cases
        boolean eventHandled = super.onTouchEvent(event);

        // Recycle previously obtained event
        event.recycle();

        return eventHandled;
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        if (mChildHelper != null) {
            mChildHelper.stopNestedScroll();
        }
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public int getMaxY() {
        return 1;
    }

    public boolean getPressedState() {
        return mPressed;
    }

    public void setForcedScroll(boolean pScroll) {
        mForcedScroll = pScroll;
    }

}