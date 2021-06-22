package com.darkweb.genesissearchengine.appManager.bookmarkManager.BookmarkSettings;

import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.settingManager.settingHomeManager.settingHomeEnums;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.helperManager.sharedUIMethod;
import com.example.myapplication.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import static com.darkweb.genesissearchengine.appManager.bookmarkManager.BookmarkSettings.bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_BOOKMARK_NAME_VALIDATION_ERROR;
import static com.darkweb.genesissearchengine.appManager.bookmarkManager.BookmarkSettings.bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_BOOKMARK_URL_VALIDATION_ERROR;

class bookmarkSettingViewController
{
    /* Private Variables */

    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;

    /* UI Variables */

    private EditText mBookmarName;
    private EditText mBookmarURL;

    private TextView mBookmarkNameError;
    private TextView mBookmarkURLError;

    /* Initializations */

    bookmarkSettingViewController(AppCompatActivity pContext, eventObserver.eventListener pEvent,EditText pBookmarName,EditText pBookmarURL, TextView pBookmarkNameError, TextView pBookmarkURLError)
    {
        this.mContext = pContext;
        this.mEvent = pEvent;

        this.mBookmarName = pBookmarName;
        this.mBookmarURL = pBookmarURL;
        this.mBookmarkNameError = pBookmarkNameError;
        this.mBookmarkURLError = pBookmarkURLError;

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

    private String getBookmarkURL(){
        return mBookmarURL.getText().toString();
    }

    private void mBookmarkNameValidationError(bookmarkSettingEnums.eBookmarkSettingViewCommands pCommands, String pMessage, boolean pStatus){
        try {
            if(pCommands.equals(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_BOOKMARK_NAME_VALIDATION_ERROR)){
                if(!pStatus){
                    mBookmarName.setBackground(helperMethod.getDrawableXML(mContext, R.xml.gx_generic_input_error));
                    mBookmarkNameError.animate().setDuration(150).alpha(1);
                    mBookmarkNameError.setText(pMessage);
                }else {
                    mBookmarName.setBackground(helperMethod.getDrawableXML(mContext, R.xml.gx_generic_input));
                    mBookmarkNameError.animate().setDuration(150).alpha(0);
                    mBookmarkNameError.setText(pMessage);
                }
            }
            if(pCommands.equals(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_BOOKMARK_URL_VALIDATION_ERROR)){
                if(!pStatus){
                    mBookmarURL.setBackground(helperMethod.getDrawableXML(mContext, R.xml.gx_generic_input_error));
                    mBookmarkURLError.animate().setDuration(150).alpha(1);
                    mBookmarkURLError.setText(pMessage);
                }else {
                    mBookmarURL.setBackground(helperMethod.getDrawableXML(mContext, R.xml.gx_generic_input));
                    mBookmarkURLError.animate().setDuration(150).alpha(0);
                    mBookmarkURLError.setText(pMessage);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void onClearForm(){
        try {
            mBookmarName.setBackground(helperMethod.getDrawableXML(mContext, R.xml.gx_generic_input));
            mBookmarURL.setBackground(helperMethod.getDrawableXML(mContext, R.xml.gx_generic_input));

        } catch (Exception ignored) {
        }

    }

    /* Event Observer */

    public Object onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands pCommands, List<Object> pData){
        if(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_INITIALIZE.equals(pCommands)){
            initializeBookmark((String)pData.get(0), (String) pData.get(1));
        }
        if(pCommands.equals(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_BOOKMARK_NAME_VALIDATION_ERROR) || pCommands.equals(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_BOOKMARK_URL_VALIDATION_ERROR)){
            mBookmarkNameValidationError(pCommands, (String)pData.get(0), (boolean)pData.get(1));
        }
        return null;
    }

    public Object onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands pCommands){
        if(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_GET_BOOKMARK_NAME.equals(pCommands)){
            return getBookmarkName();
        }
        else if(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_GET_BOOKMARK_URL.equals(pCommands)){
            return getBookmarkURL();
        }
        else if(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_CLEAR_FORM.equals(pCommands)){
            onClearForm();
        }
        return null;
    }
}
