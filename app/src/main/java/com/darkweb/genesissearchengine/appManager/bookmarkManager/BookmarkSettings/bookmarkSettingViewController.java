package com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkSettings;

import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.helperManager.sharedUIMethod;
import com.example.myapplication.R;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.List;

class bookmarkSettingViewController
{
    /* Private Variables */

    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;

    /* UI Variables */

    private EditText mBookmarName;
    private TextView mBookmarURL;

    /* Initializations */

    bookmarkSettingViewController(AppCompatActivity pContext, eventObserver.eventListener pEvent,EditText pBookmarName,TextView pBookmarURL)
    {
        this.mContext = pContext;
        this.mEvent = pEvent;

        this.mBookmarName = pBookmarName;
        this.mBookmarURL = pBookmarURL;

        initPostUI();
    }

    private void initPostUI(){
        sharedUIMethod.updateStatusBar(mContext);
    }

    private void initializeBookmark(String pBookmarkName, String pBookmarkURL){
        mBookmarName.setText(pBookmarkName);
        mBookmarURL.setText(pBookmarkURL);
    }

    private String getBookmarkName(){
        return mBookmarName.getText().toString();
    }

    private void mBookmarkNameValidationError(bookmarkSettingEnums.eBookmarkSettingViewCommands pCommands, boolean pStatus){
        try {
            if(pCommands.equals(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_BOOKMARK_NAME_VALIDATION_RESPONSE)){
                if(!pStatus){
                    mBookmarName.setBackground(helperMethod.getDrawableXML(mContext, R.xml.gx_generic_input_error));
                }else {
                    mBookmarName.setBackground(helperMethod.getDrawableXML(mContext, R.xml.bx_input_field));
                }
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void onClearForm(){
        try {
            mBookmarName.setBackground(helperMethod.getDrawableXML(mContext, R.xml.bx_input_field));
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void onClearFormFocus(){
        mBookmarName.clearFocus();
        helperMethod.hideKeyboard(mContext);
    }

    /* Event Observer */

    public Object onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands pCommands, List<Object> pData){
        if(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_INITIALIZE.equals(pCommands)){
            initializeBookmark((String)pData.get(0), (String) pData.get(1));
        }
        if(pCommands.equals(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_BOOKMARK_NAME_VALIDATION_RESPONSE)){
            mBookmarkNameValidationError(pCommands, (boolean)pData.get(0));
        }
        return null;
    }

    public Object onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands pCommands){
        if(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_GET_BOOKMARK_NAME.equals(pCommands)){
            return getBookmarkName();
        }
        else if(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_CLEAR_FORM.equals(pCommands)){
            onClearForm();
        }
        else if(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_CLEAR_FORM_FOCUS.equals(pCommands)){
            onClearFormFocus();
        }
        return null;
    }
}
