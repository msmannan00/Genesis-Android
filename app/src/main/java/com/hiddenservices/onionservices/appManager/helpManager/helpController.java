package com.hiddenservices.onionservices.appManager.helpManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.appManager.activityThemeManager;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.hiddenservices.onionservices.appManager.helpManager.helpEnums.eHelpModel.M_IS_LOADED;

public class helpController extends AppCompatActivity {

    /*Initializations*/
    private helpViewController mHelpViewController;
    private helpModel mHelpModel;
    private helpAdapter mHelpAdapter;
    private ProgressBar mProgressBar;
    private RecyclerView mRecycleView;
    private ConstraintLayout mRetryContainer;
    private Button mReloadButton;
    private editViewController mSearchInput;

    /*Private Variables*/
    private Handler mSearchInvokedHandler = new Handler();
    private Runnable postToServerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view);

        initializeViews();
        initializeAppModel();
        initializeLocalEventHandlers();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);

        if(newConfig.uiMode != getResources().getConfiguration().uiMode){
            activityContextManager.getInstance().onResetTheme();
            activityThemeManager.getInstance().onConfigurationChanged(this);
        }

        super.onConfigurationChanged(newConfig);
    }

    private void initializeAppModel()
    {
        mHelpModel = new helpModel(this, new helpAdapterCallback());
        mHelpModel.onTrigger(helpEnums.eHelpModel.M_LOAD_HELP_DATA,null);
        if((boolean)mHelpModel.onTrigger(M_IS_LOADED,null)){
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void initializeViews(){
        mHelpViewController = new helpViewController();
        mProgressBar = findViewById(R.id.pProgressBar);
        mRecycleView = findViewById(R.id.pRecycleView);
        mRetryContainer = findViewById(R.id.pRetryContainer);
        mReloadButton = findViewById(R.id.pReloadButton);
        mSearchInput = findViewById(R.id.pSearchInput);

        mHelpViewController.initialization(new helpViewCallback(),this, mProgressBar, mRecycleView, mRetryContainer, mReloadButton);
        mHelpViewController.onTrigger(helpEnums.eHelpViewController.M_INIT_VIEWS, null);

    }

    private void initializeLocalEventHandlers(){

        mRecycleView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (child != null) {
                    return false;
                } else {
                    mSearchInput.clearFocus();
                    helperMethod.hideKeyboard(helpController.this);
                    return true;
                }
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        mSearchInput.setEventHandler(new edittextManagerCallback());

        if(mHelpAdapter!=null){
            postToServerRunnable = () -> mHelpAdapter.onTrigger(helpEnums.eHelpAdapter.M_INIT_FILTER, Collections.singletonList(mSearchInput.getText().toString()));
        }

        mSearchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                mSearchInvokedHandler.removeCallbacks(postToServerRunnable);
                mSearchInvokedHandler.postDelayed(postToServerRunnable, 50);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
            }
        });
    }



    /*HELPER FUNCTIONS*/

    private void onShowHelperManager(ArrayList<helpDataModel> pHelpListModel){
        mHelpAdapter = new helpAdapter(pHelpListModel, getApplicationContext());
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mRecycleView.setAdapter(mHelpAdapter);
        ((SimpleItemAnimator) Objects.requireNonNull(mRecycleView.getItemAnimator())).setSupportsChangeAnimations(false);
        mHelpViewController.onTrigger(helpEnums.eHelpViewController.M_DATA_LOADED, null);
        mSearchInput.setVisibility(View.VISIBLE);
        mSearchInput.animate().setDuration(300).alpha(1);
    }

    /*Ediitext Callback*/

    private class edittextManagerCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {

            if(e_type.equals(enums.etype.ON_KEYBOARD_CLOSE)){
                mSearchInput.clearFocus();
                //helperMethod.hideKeyboard(helpController.this);
            }
            return null;
        }
    }

    /*Helper View Callback*/

    private class helpViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }


    /*Adapter Callbacks*/

    private class helpAdapterCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if(helpEnums.eHelpModelCallback.M_LOAD_JSON_RESPONSE_SUCCESS.equals(e_type))
            {
                onShowHelperManager((ArrayList<helpDataModel>) data.get(0));
            }
            else if(helpEnums.eHelpModelCallback.M_LOAD_JSON_RESPONSE_FAILURE.equals(e_type))
            {
                mHelpViewController.onTrigger(helpEnums.eHelpViewController.M_LOAD_ERROR, null);
            }
            return null;
        }
    }

    /*UI Redirection*/

    public void onClose(View view) {
        finish();
    }

    public void onReloadData(View view) {
        mHelpViewController.onTrigger(helpEnums.eHelpViewController.M_RELOAD_DATA, null);
        mHelpModel.onTrigger(helpEnums.eHelpModel.M_LOAD_HELP_DATA,null);
    }

    public void onOpenHelp(View view) {
        helperMethod.sendIssueEmail(this);
    }

    public void onOpenHelpExternal(View view) {
        if(!status.sSettingIsAppStarted){
            activityContextManager.getInstance().getHomeController().onStartApplication(null);
        }

        if(status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(this)){
            activityContextManager.getInstance().getHomeController().onDisableAdvert();
            activityContextManager.getInstance().getHomeController().onLoadURL(constants.CONST_GENESIS_HELP_URL_CACHE);
        }else {
            activityContextManager.getInstance().getHomeController().onDisableAdvert();
            activityContextManager.getInstance().getHomeController().onLoadURL(constants.CONST_GENESIS_HELP_URL_CACHE_DARK);
        }
        finish();
        activityContextManager.getInstance().onClearStack();
    }

    /*Local Overrides*/

    @Override
    protected void onResume() {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(mSearchInput.hasFocus()){
            mSearchInput.clearFocus();
        }else {
            finish();
        }
        super.onBackPressed();
    }


}
