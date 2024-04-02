package com.hiddenservices.onionservices.appManager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.eventObserver;

public class editTextManager extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    Context mContext;
    private eventObserver.eventListener mEvent = null;

    public editTextManager(@NonNull Context context) {
        super(context);
        mContext = context;
    }


    public editTextManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            id = android.R.id.pasteAsPlainText;
        }
        return super.onTextContextMenuItem(id);
    }

    public editTextManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setEventHandler(eventObserver.eventListener pEvent) {
        mEvent = pEvent;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEvent != null) {
                mEvent.invokeObserver(null, homeEnums.eEdittextCallbacks.ON_KEYBOARD_CLOSE);
            }
        }
        return false;
    }
}
