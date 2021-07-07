package com.darkweb.genesissearchengine.appManager.bookmarkManager.BookmarkSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.activityThemeManager;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.darkweb.genesissearchengine.constants.keys.M_ACTIVITY_RESPONSE;
import static com.darkweb.genesissearchengine.constants.responses.BOOKMARK_SETTING_CONTROLLER_SHOW_DELETE_ALERT;
import static com.darkweb.genesissearchengine.constants.responses.BOOKMARK_SETTING_CONTROLLER_SHOW_SUCCESS_ALERT;

public class bookmarkSettingController extends AppCompatActivity {

    /* Private Variables */

    private bookmarkSettingModel mBookmarkSettingModel;
    private bookmarkSettingViewController mBookmarkSettingViewController;

    /* UI Variables */

    private EditText mBookmarName;
    private TextView mBookmarURL;
    private ScrollView mScrollView;


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
        mScrollView = findViewById(R.id.pScrollView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeLocalEventHandlers() {

        mBookmarName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String mBookmarkName = (String) mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_GET_BOOKMARK_NAME);

                boolean mValidationStatus = (boolean)mBookmarkSettingModel.onTrigger(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_VALIDATE_FORM, Collections.singletonList(mBookmarkName));
                mBookmarkSettingModel.onTrigger(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_SET_BOOOKMARK_CHANGED_STATUS, Collections.singletonList(true));
                mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_BOOKMARK_NAME_VALIDATION_RESPONSE, Collections.singletonList(mValidationStatus));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {

            }
        });


        mBookmarName.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_CLEAR_FORM_FOCUS);
            }
        });

        mScrollView.setOnTouchListener((v, event) -> {
            mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_CLEAR_FORM_FOCUS);
            return false;
        });
    }

    private void initializeModels(){
        String mBookmarkName = getIntent().getStringExtra(keys.BOOKMARK_SETTING_NAME);
        String mBookmarkURL = getIntent().getStringExtra(keys.BOOKMARK_SETTING_URL);
        int mBookmarkID = getIntent().getIntExtra(keys.BOOKMARK_SETTING_ID, -1);

        mBookmarkSettingViewController = new bookmarkSettingViewController(this, new bookmarkSettingViewCallback(), mBookmarName, mBookmarURL);
        mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_INITIALIZE, Arrays.asList(mBookmarkName,mBookmarkURL));
        mBookmarkSettingModel = new bookmarkSettingModel(this, new bookmarkSettingModelCallback(), mBookmarkID, mBookmarkURL);
    }

    private void initCallableResponse(bookmarkSettingEnums.eActivityResponseCommands pResponse){
        Intent data = new Intent();
        if(pResponse.equals(bookmarkSettingEnums.eActivityResponseCommands.M_OPEN_UPDATE_ALERT)){
            data.putExtra(M_ACTIVITY_RESPONSE, BOOKMARK_SETTING_CONTROLLER_SHOW_SUCCESS_ALERT);
        }
        else if(pResponse.equals(bookmarkSettingEnums.eActivityResponseCommands.M_OPEN_DELETE_ALERT)){
            data.putExtra(M_ACTIVITY_RESPONSE, BOOKMARK_SETTING_CONTROLLER_SHOW_DELETE_ALERT);
        }
        setResult(RESULT_OK,data);
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
        onCloseTrigger(null);
    }

    /* UI Redirection */

    public void onCloseTrigger(View view){
        finish();
    }

    public void onUITrigger(View view){
        if(view.getId()==R.id.pBookmarkUpdate){
            mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_CLEAR_FORM);

            String mBookmarkName = (String) mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_GET_BOOKMARK_NAME);
            boolean mValid = (boolean) mBookmarkSettingModel.onTrigger(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_VALIDATE_FORM, Collections.singletonList(mBookmarkName));
            boolean mBookmarkChanged = (boolean) mBookmarkSettingModel.onTrigger(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_GET_UPDATE_STATUS);

            if(mValid){
                mBookmarkSettingModel.onTrigger(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_UPDATE_BOOKMARK, Collections.singletonList(mBookmarkName));
                mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_CLEAR_FORM_FOCUS);
                if(mBookmarkChanged){
                    initCallableResponse(bookmarkSettingEnums.eActivityResponseCommands.M_OPEN_UPDATE_ALERT);
                }
            }else {
                mBookmarkSettingViewController.onTrigger(bookmarkSettingEnums.eBookmarkSettingViewCommands.M_BOOKMARK_NAME_VALIDATION_RESPONSE, Collections.singletonList(false));
            }
            onCloseTrigger(null);
        }
        if(view.getId()==R.id.pRemoveBookmark){
            initCallableResponse(bookmarkSettingEnums.eActivityResponseCommands.M_OPEN_DELETE_ALERT);
            mBookmarkSettingModel.onTrigger(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_DELETE_BOOKMARK);
            onCloseTrigger(null);
        }
    }

    /* UI Callbacks */

    private class bookmarkSettingViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> pData, Object pType)
        {
            return null;
        }
    }

    private class bookmarkSettingModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> pData, Object pType)
        {
            return null;
        }
    }
}