package com.darkweb.genesissearchengine.appManager.homeManager.geckoManager;

import android.content.Context;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import org.mozilla.geckoview.GeckoView;
import java.util.Collections;

import static com.darkweb.genesissearchengine.constants.enums.etype.GECKO_SCROLL_DOWN;
import static com.darkweb.genesissearchengine.constants.enums.etype.GECKO_SCROLL_UP_ALWAYS;

public class NestedGeckoView extends GeckoView {
    private int mLastY;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedOffsetY;
    private NestedScrollingChildHelper mChildHelper;
    private eventObserver.eventListener mEvent;


    public void onSetHomeEvent(eventObserver.eventListener pEvent){
        mEvent = pEvent;
    }

    public void onDestroy() {
        mEvent = null;
        mChildHelper = null;
    }

    public NestedGeckoView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);

        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final MotionEvent event = MotionEvent.obtain(ev);
        final int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsetY = 0;
        }

        final int eventY = (int) event.getY();
        event.offsetLocation(0, mNestedOffsetY);

        if(event.getPointerCount() > 1 && !status.sSettingEnableZoom) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                // mEvent.invokeObserver(Collections.singletonList(null), GECKO_SCROLL_FINISHED);
                final boolean allowScroll = status.sFullScreenBrowsing;
                int deltaY = mLastY - eventY;


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

                if(status.sFullScreenBrowsing){
                    Log.i("wow1", eventY + "");
                }

                break;

            case MotionEvent.ACTION_DOWN:
                mLastY = eventY;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                mEvent.invokeObserver(Collections.singletonList(null), GECKO_SCROLL_DOWN);
                break;

            case MotionEvent.ACTION_UP:
                mEvent.invokeObserver(Collections.singletonList(null), GECKO_SCROLL_UP_ALWAYS);
            case MotionEvent.ACTION_CANCEL:
                // mEvent.invokeObserver(Collections.singletonList(null), GECKO_SCROLL_FINISHED);
                stopNestedScroll();
                break;

            default:
                // mEvent.invokeObserver(Collections.singletonList(null), GECKO_SCROLL_FINISHED);
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
        mChildHelper.stopNestedScroll();
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

    public int getMaxY(){
        return 1;
    }

}