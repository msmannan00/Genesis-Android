package com.hiddenservices.genesissearchengine.production.appManager.helpManager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hiddenservices.genesissearchengine.production.constants.enums;
import com.hiddenservices.genesissearchengine.production.eventObserver;

public class editViewController extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    Context mContext;
    private eventObserver.eventListener mEvent = null;

    public editViewController(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public editViewController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public editViewController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setEventHandler(eventObserver.eventListener pEvent){
        mEvent = pEvent;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mEvent!=null){
                mEvent.invokeObserver(null, enums.etype.ON_KEYBOARD_CLOSE);
            }
        }
        return false;
    }
}
