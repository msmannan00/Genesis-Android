package com.darkweb.genesissearchengine.appManager.bookmarkManager.BookmarkSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.activityThemeManager;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;

import org.mozilla.geckoview.GeckoSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class bookmarkSettingController extends AppCompatActivity {

    /* Private Variables */

    private bookmarkSettingModel mBookmarkSettingModel;
    private bookmarkSettingViewController mBookmarkSettingViewController;

    /* UI Variables */

    private EditText mBookmarName;
    private EditText mBookmarURL;

    private TextView mBookmarkNameError;
    private TextView mBookmarkURLError;

    /* Initializations */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        activityContextManager.getInstance().onStack(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_setting_view);

        initializeViews();
        initializeModels();
        initializeLocalEventHandlers();
    }

    private void initializeViews() {
        mBookmarName = findViewById(R.id.pBookmarkName);
        mBookmarURL = findViewById(R.id.pBookmarkURL);
        mBookmarkNameError = findViewById(R.id.pBookmarkNameError);
        mBookmarkURLError = findViewById(R.id.pBookmarkURLError);
    }

    private void initializeLocalEventHandlers() {


        TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String mBookmarkName = (String) mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_GET_BOOKMARK_NAME);
                String mBookmarkURL = (String) mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_GET_BOOKMARK_URL);
                mBookmarkSettingModel.onTrigger(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_VALIDATE_FORM, Arrays.asList(mBookmarkName, mBookmarkURL));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {

            }
        };

        mBookmarName.addTextChangedListener(mTextWatcher);
        mBookmarURL.addTextChangedListener(mTextWatcher);
    }

    public void initializeModels(){
        String mBookmarkName = getIntent().getStringExtra(keys.BOOKMARK_SETTING_NAME);
        String mBookmarkURL = getIntent().getStringExtra(keys.BOOKMARK_SETTING_URL);
        int mBookmarkID = getIntent().getIntExtra(keys.BOOKMARK_SETTING_ID, -1);

        mBookmarkSettingViewController = new bookmarkSettingViewController(this, new bookmarkSettingViewCallback(), mBookmarName, mBookmarURL, mBookmarkNameError, mBookmarkURLError);
        mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_INITIALIZE, Arrays.asList(mBookmarkName,mBookmarkURL));
        mBookmarkSettingModel = new bookmarkSettingModel(this, new bookmarkSettingModelCallback(), mBookmarkID);
    }

    /* Local Override */

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onConfigurationChanged(newConfig);

        if(newConfig.uiMode != getResources().getConfiguration().uiMode){
            activityContextManager.getInstance().onResetTheme();
            activityThemeManager.getInstance().onConfigurationChanged(this);
        }
    }

    @Override
    public void onResume()
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
    }

    /* UI Redirection */

    public void onClose(View view){
        finish();
    }

    public void onUpdateBookmark(View view) {
        String mBookmarkName = (String) mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_GET_BOOKMARK_NAME);
        String mBookmarkURL = (String) mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_GET_BOOKMARK_URL);
        mBookmarkSettingModel.onTrigger(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_UPDATE_BOOKMARK, Arrays.asList(mBookmarkName, mBookmarkURL));
    }

    /* UI Callbacks */

    private class bookmarkSettingViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    private class bookmarkSettingModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> pData, Object pType)
        {
            if(pType.equals(bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_BOOKMARK_NAME_VALIDATION_ERROR)){
                mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_BOOKMARK_NAME_VALIDATION_ERROR, pData);
            }
            else if(pType.equals(bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_BOOKMARK_URL_VALIDATION_ERROR)){
                mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_BOOKMARK_URL_VALIDATION_ERROR, pData);
            }
            else if(pType.equals(bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_CLEAR_FORM)){
                mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_CLEAR_FORM);
            }
            else if(pType.equals(bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_CLOSE)){
                onClose(null);
            }
            return null;
        }
    }
}