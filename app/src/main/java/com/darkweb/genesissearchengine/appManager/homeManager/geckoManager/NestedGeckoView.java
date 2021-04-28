package com.darkweb.genesissearchengine.appManager.homeManager.geckoManager;

import android.content.Context;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.PanZoomController;

import java.util.Collections;
import static com.darkweb.genesissearchengine.constants.enums.etype.GECKO_SCROLL_CHANGED;

public class NestedGeckoView extends GeckoView {
    private int mLastY = 0;
    private int deltaY = 0;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedOffsetY;
    private NestedScrollingChildHelper mChildHelper;
    private eventObserver.eventListener mEvent;
    private int mInputResult = PanZoomController.INPUT_RESULT_UNHANDLED;

    public void onSetHomeEvent(eventObserver.eventListener pEvent){
        mEvent = pEvent;
    }

    public void onDestroy() {
        mEvent = null;
        mChildHelper = null;
    }

    public NestedGeckoView(final Context context) {
        this(context, null);
    }

    public NestedGeckoView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        MotionEvent event = MotionEvent.obtain(ev);
        final int action = event.getActionMasked();
        int eventY = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final boolean allowScroll = !shouldPinOnScreen() &&
                        mInputResult == PanZoomController.INPUT_RESULT_HANDLED;

                if(!status.sSettingEnableZoom){
                    mInputResult = PanZoomController.INPUT_RESULT_UNHANDLED;
                }
                int deltaY = mLastY - eventY;

                if (allowScroll && dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    event.offsetLocation(0f, -mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                }

                mLastY = eventY - mScrollOffset[1];

                if (allowScroll && dispatchNestedScroll(0, mScrollOffset[1], 0, deltaY, mScrollOffset)) {
                    mLastY -= mScrollOffset[1];
                    event.offsetLocation(0f, mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                }
                break;

            case MotionEvent.ACTION_DOWN:
                // A new gesture started. Reset handled status and ask GV if it can handle this.
                mInputResult = PanZoomController.INPUT_RESULT_UNHANDLED;

                updateInputResult(event);

                mNestedOffsetY = 0;
                mLastY = eventY;

                // The event should be handled either by onTouchEvent,
                // either by onTouchEventForResult, never by both.
                // Early return if we sent it to updateInputResult(..) which calls onTouchEventForResult.
                event.recycle();
                return true;

            // We don't care about other touch events
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                break;
        }

        // Execute event handler from parent class in all cases
        final boolean eventHandled = callSuperOnTouchEvent(event);

        // Recycle previously obtained event
        event.recycle();

        return eventHandled;
    }

    private boolean callSuperOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void updateInputResult(MotionEvent event) {
        if(status.sSettingEnableZoom){
            super.onTouchEventForResult(event).accept(inputResult -> {
                mInputResult = inputResult;
                mInputResult = PanZoomController.INPUT_RESULT_UNHANDLED;

                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
            });
        }
    }

    public int getInputResult() {
        return mInputResult;
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
    public boolean dispatchNestedScroll(int dxConsumed,
                                        int dyConsumed,
                                        int dxUnconsumed,
                                        int dyUnconsumed,
                                        int[] offsetInWindow) {
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

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}