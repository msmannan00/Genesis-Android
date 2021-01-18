package com.darkweb.genesissearchengine.appManager.helpManager;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class helpController extends AppCompatActivity {

    /*Initializations*/
    private helpViewController mHelpViewController;
    private helpModel mHelpModel;
    private helpAdapter mHelpAdapter;
    private ProgressBar mProgressBar;
    private RecyclerView mRecycleView;
    private ConstraintLayout mRetryContainer;
    private Button mReloadButton;
    private editTextManager mSearchInput;

    /*Private Variables*/
    private Handler mSearchInvokedHandler = new Handler();
    private Runnable postToServerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view);

        initializeAppModel();
        initializeViews();
        initializeLocalEventHandlers();
    }

    private void initializeAppModel()
    {
        mHelpModel = new helpModel(this, new helpAdapterCallback());
        mHelpModel.onTrigger(helpEnums.eHelpModel.M_LOAD_HELP_DATA,null);
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

    public void initializeLocalEventHandlers(){

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

        postToServerRunnable = () -> {
            mHelpAdapter.onTrigger(helpEnums.eHelpAdapter.M_INIT_FILTER, Collections.singletonList(mSearchInput.getText().toString()));
        };

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

    public class edittextManagerCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {

            if(e_type.equals(enums.etype.ON_KEYBOARD_CLOSE)){
                mSearchInput.clearFocus();
                //helperMethod.hideKeyboard(helpController.this);
            }
            return null;
        }
    }

    public void onOpenHelp(View view) {
        helperMethod.sendIssueEmail(this);
    }

    /*LISTENERS CALLBACKS*/

    private class helpViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /*CALLBACKS HELPER FUNCTIONS*/

    private void onShowHelperManager(ArrayList<helpDataModel> pHelpListModel){
        mHelpAdapter = new helpAdapter(pHelpListModel, getApplicationContext());
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mRecycleView.setAdapter(mHelpAdapter);
        ((SimpleItemAnimator) Objects.requireNonNull(mRecycleView.getItemAnimator())).setSupportsChangeAnimations(false);
        mHelpViewController.onTrigger(helpEnums.eHelpViewController.M_DATA_LOADED, null);
        mSearchInput.setVisibility(View.VISIBLE);
        mSearchInput.animate().setDuration(300).alpha(1);
    }

    /*LISTENERS*/

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

    @Override
    public void onBackPressed() {
        if(mSearchInput.hasFocus()){
            mSearchInput.clearFocus();
        }else {
            finish();
        }
        super.onBackPressed();
    }

    public void onOpenHelpExternal(View view) {
        if(!status.sSettingIsAppStarted){
            activityContextManager.getInstance().getHomeController().onStartApplication(null);
        }
        activityContextManager.getInstance().getHomeController().onLoadURL(constants.CONST_GENESIS_HELP_URL_CACHE);
        finish();
        activityContextManager.getInstance().onClearStack();
    }

}
