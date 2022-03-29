package com.hiddenservices.onionservices.libs.views;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import java.util.HashMap;
import java.util.Objects;

public class KeyboardUtils implements ViewTreeObserver.OnGlobalLayoutListener
{
    private SoftKeyboardToggleListener mCallback;
    private View mRootView;

    private final static int sMagicNumber = 200;
    private static HashMap<SoftKeyboardToggleListener, KeyboardUtils> sListenerMap = new HashMap<>();

    private Boolean mPrevValue = null;
    private float mScreenDensity;

    public interface SoftKeyboardToggleListener
    {
        void onToggleSoftKeyboard(boolean isVisible);
    }

    @Override
    public void onGlobalLayout()
    {
        Rect r = new Rect();
        mRootView.getWindowVisibleDisplayFrame(r);

        int heightDiff = mRootView.getRootView().getHeight() - (r.bottom - r.top);
        float dp = heightDiff/ mScreenDensity;
        boolean isVisible = dp > sMagicNumber;

        if (mCallback != null && (mPrevValue == null || isVisible != mPrevValue)) {
            mPrevValue = isVisible;
            mCallback.onToggleSoftKeyboard(isVisible);
        }
    }

    public static void addKeyboardToggleListener(Activity act, SoftKeyboardToggleListener listener)
    {
        removeKeyboardToggleListener(listener);
        sListenerMap.put(listener, new KeyboardUtils(act, listener));
    }

    public static void removeKeyboardToggleListener(SoftKeyboardToggleListener listener)
    {
        if(sListenerMap.containsKey(listener))
        {
            KeyboardUtils k = sListenerMap.get(listener);
            assert k != null;
            k.removeListener();
            sListenerMap.remove(listener);
        }
    }

    public static void removeAllKeyboardToggleListeners()
    {
        for(SoftKeyboardToggleListener l : sListenerMap.keySet()){
            Objects.requireNonNull(sListenerMap.get(l)).removeListener();
        }

        sListenerMap.clear();
    }

    public static void toggleKeyboardVisibility(Context context)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null){
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void forceCloseKeyboard(View activeView)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) activeView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null){
            inputMethodManager.hideSoftInputFromWindow(activeView.getWindowToken(), 0);
        }
    }

    private void removeListener()
    {
        mCallback = null;
        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private KeyboardUtils(Activity act, SoftKeyboardToggleListener listener)
    {
        mCallback = listener;
        mRootView = ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mScreenDensity = act.getResources().getDisplayMetrics().density;
    }

}