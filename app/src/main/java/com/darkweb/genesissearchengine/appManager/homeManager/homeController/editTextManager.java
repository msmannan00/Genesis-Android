package com.darkweb.genesissearchengine.appManager.homeManager.homeController;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.eventObserver;

public class editTextManager  extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                id = android.R.id.pasteAsPlainText;
            } else {
                onInterceptClipDataToPlainText();
            }
        }
        return super.onTextContextMenuItem(id);
    }

    private void onInterceptClipDataToPlainText() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null) {
            for (int i = 0; i < clip.getItemCount(); i++) {
                final CharSequence paste;
                // Get an item as text and remove all spans by toString().
                final CharSequence text = clip.getItemAt(i).coerceToText(getContext());
                paste = (text instanceof Spanned) ? text.toString() : text;
                if (paste != null) {
                    copyToClipBoard(getContext(), paste);
                }
            }
        }
    }

    public void copyToClipBoard(@NonNull Context context, @NonNull CharSequence text) {
        ClipData clipData = ClipData.newPlainText("rebase_copy", text);
        ClipboardManager manager = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        manager.setPrimaryClip(clipData);
    }

    public editTextManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
