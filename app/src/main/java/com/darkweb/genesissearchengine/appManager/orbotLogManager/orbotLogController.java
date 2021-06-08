package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.settingManager.logManager.settingLogController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.appManager.activityThemeManager;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.torproject.android.service.wrapper.logRowModel;
import org.torproject.android.service.wrapper.orbotLocalConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.darkweb.genesissearchengine.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCommands.M_GET_LIST;
import static com.darkweb.genesissearchengine.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCommands.M_GET_LIST_SIZE;
import static com.darkweb.genesissearchengine.appManager.orbotLogManager.orbotLogEnums.eOrbotLogViewCommands.M_FLOAT_BUTTON_UPDATE;


public class orbotLogController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private orbotLogModel mOrbotModel;
    private orbotLogViewController mOrbotLogViewController;
    private orbotLogAdapter mOrbotAdapter;
    private boolean mActivityClosed = false;
    private int mLogCounter = 1;
    private boolean mIsRecycleviewInteracting = false;

    /* UI VARIABLES */

    private TextView mLogs;
    private RecyclerView mLogRecycleView;
    private NestedScrollView mNestedScrollView;
    private FloatingActionButton mFloatingScroller;

    /* INITIALIZATIONS */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        overridePendingTransition(R.anim.push_anim_out_reverse, R.anim.push_anim_in_reverse);
        activityContextManager.getInstance().setOrbotLogController(this);
        activityContextManager.getInstance().onStack(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.orbot_log_view);

        initializeViews();
        initializeLogs();
        initListener();
        updateLogs();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        if(newConfig.uiMode != getResources().getConfiguration().uiMode){
            activityContextManager.getInstance().onResetTheme();
            activityThemeManager.getInstance().onConfigurationChanged(this);
        }

        mNestedScrollView.stopNestedScroll();

        helperMethod.onDelayHandler(orbotLogController.this, 150, () -> {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if(!orbotLogStatus.sUIInteracted && ((int)mOrbotModel.onTrigger(M_GET_LIST_SIZE)>1)){
                    if(mNestedScrollView.canScrollVertically(enums.ScrollDirection.VERTICAL)){
                        mNestedScrollView.stopNestedScroll();
                        onScrollBottom();
                        mNestedScrollView.stopNestedScroll();
                        orbotLogStatus.sScrollPosition = -1;
                    }
                }else {
                    mNestedScrollView.stopNestedScroll();
                    mNestedScrollView.scrollTo(0,0);
                    mNestedScrollView.smoothScrollTo(0,0);
                    mNestedScrollView.stopNestedScroll();
                    orbotLogStatus.sScrollPosition = 0;
                }
            }

            return null;
        });

        super.onConfigurationChanged(newConfig);
    }

    private void initializeViews() {
        mLogRecycleView = findViewById(R.id.pLogRecycleView);
        mLogs = findViewById(R.id.pLogs);
        mNestedScrollView = findViewById(R.id.pNestedScrollView);
        mFloatingScroller = findViewById(R.id.pFloatingScroller);

        mOrbotLogViewController = new orbotLogViewController(this, new orbotLogViewCallback() , mLogs, mLogRecycleView, mNestedScrollView, mFloatingScroller);
        mOrbotModel = new orbotLogModel();
    }

    private void initializeLogs(){
        mLogCounter = 0;
        mOrbotModel.setList(orbotLocalConstants.mTorLogsHistory);
        if(status.sLogThemeStyleAdvanced){
            mLogCounter = (int)mOrbotModel.onTrigger(M_GET_LIST_SIZE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(orbotLogController.this);
            orbotLogAdapter adapter = new orbotLogAdapter(((ArrayList)mOrbotModel.onTrigger(M_GET_LIST)),new orbotLogController.orbotModelCallback());
            mOrbotAdapter = adapter;
            layoutManager.setReverseLayout(true);

            mLogRecycleView.setAdapter(adapter);
            Objects.requireNonNull(mLogRecycleView.getItemAnimator()).setAddDuration(350);

            mLogRecycleView.setNestedScrollingEnabled(false);
            mLogRecycleView.setLayoutManager(new LinearLayoutManager(orbotLogController.this));

            mOrbotAdapter.notifyDataSetChanged();

        }else {
            logToString();
        }
        mOrbotLogViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_INIT_VIEWS, Collections.singletonList(status.sLogThemeStyleAdvanced));
        mLogRecycleView.smoothScrollToPosition((int)mOrbotModel.onTrigger(M_GET_LIST_SIZE));
    }


    /* LISTENERS */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    private void initListener(){

        mLogRecycleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi") @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                if(orbotLogStatus.sOrientation==-1){
                    orbotLogStatus.sOrientation = getResources().getConfiguration().orientation;
                }

                if(orbotLogStatus.sOrientation != getResources().getConfiguration().orientation && orbotLogStatus.sScrollPosition!=-1 && orbotLogStatus.sScrollPosition!=0){
                    mNestedScrollView.stopNestedScroll();
                    orbotLogStatus.sScrollPosition = 0;
                    mNestedScrollView.scrollTo(0, orbotLogStatus.sScrollPosition);
                    orbotLogStatus.sOrientation = getResources().getConfiguration().orientation;
                }else {
                    if(orbotLogStatus.sScrollPosition!=-1 && orbotLogStatus.sUIInteracted){
                        mNestedScrollView.scrollTo(0, orbotLogStatus.sScrollPosition);
                    }else if(mNestedScrollView.canScrollVertically(enums.ScrollDirection.VERTICAL)){
                        onScrollBottom();
                    }
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    mNestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        orbotLogStatus.sScrollPosition = scrollY;
                    });
                }

                mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);
                mLogRecycleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                orbotLogStatus.sOrientation = getResources().getConfiguration().orientation;
            }
        });

        mLogRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);
                }
            }
        });

        mLogRecycleView.setOnTouchListener((v, event) -> onTouch(event));

        mNestedScrollView.setOnTouchListener((v, event) -> onTouch(event));

        mLogRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);
                }
            }
        });

        mNestedScrollView.getViewTreeObserver() .addOnScrollChangedListener(() -> {

            if (mNestedScrollView.getChildAt(0).getBottom() <= (mNestedScrollView.getHeight() + mNestedScrollView.getScrollY())) {
                mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);
                if(!mIsRecycleviewInteracting){
                    orbotLogStatus.sUIInteracted = false;
                }
            }
            if(mNestedScrollView.getScrollY() == 0){
                mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);
            }
        });
    }

    public boolean onTouch(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_UP){
            mIsRecycleviewInteracting = false;

            if(mNestedScrollView.canScrollVertically(enums.ScrollDirection.VERTICAL)){
                orbotLogStatus.sUIInteracted = true;
            }
        }else if(event.getAction() == MotionEvent.ACTION_DOWN){
            mIsRecycleviewInteracting = true;
        }

        mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);

        return false;
    }

    private void logToString(){
        if((int)mOrbotModel.onTrigger(M_GET_LIST_SIZE)>1){
            for(int mCounter=0;mCounter<(int)mOrbotModel.onTrigger(M_GET_LIST_SIZE);mCounter++){
                mOrbotLogViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS, Collections.singletonList(((ArrayList<logRowModel>)mOrbotModel.onTrigger(M_GET_LIST)).get(mCounter).getLog()));
                mLogCounter+=1;
            }
        }
    }

    private void updateLogs(){
        new Thread(){
            public void run(){
                try {
                sleep(1000);
                while (!mActivityClosed){
                        if(status.sLogThemeStyleAdvanced){
                            sleep(800);
                        }else {
                            sleep(100);
                        }

                        if(mLogCounter>0){
                            runOnUiThread(() -> {
                                if(orbotLocalConstants.mTorLogsHistory.size()>mLogCounter){
                                    ((ArrayList<logRowModel>)mOrbotModel.onTrigger(M_GET_LIST)).add(orbotLocalConstants.mTorLogsHistory.get(mLogCounter));
                                    if(!status.sLogThemeStyleAdvanced){
                                        mOrbotLogViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS, Collections.singletonList(((ArrayList<logRowModel>)mOrbotModel.onTrigger(M_GET_LIST)).get(mLogCounter).getLog()));
                                    }else {
                                        if(mOrbotAdapter!=null){
                                            mOrbotAdapter.notifyItemInserted((int)mOrbotModel.onTrigger(M_GET_LIST_SIZE)-1);
                                        }
                                    }

                                    if(!orbotLogStatus.sUIInteracted){
                                        helperMethod.onDelayHandler(orbotLogController.this, 150, () -> {
                                            Log.i("SUPFUCK4", orbotLogStatus.sUIInteracted + "");
                                            if(!orbotLogStatus.sUIInteracted){
                                                onScrollBottomAnimated(null);
                                            }
                                            return null;
                                        });
                                    }
                                    mLogCounter+=1;
                                }
                            });
                        }
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void onScrollBottom() {
        mNestedScrollView.scrollTo(0,helperMethod.pxFromDp((int)mOrbotModel.onTrigger(M_GET_LIST_SIZE)*100));
    }

    /* View Callback */

    public void onScrollBottomAnimated(View view) {
        mNestedScrollView.fullScroll(View.FOCUS_DOWN);
        orbotLogStatus.sUIInteracted = false;
        Log.i("SUPFUCK5", orbotLogStatus.sUIInteracted + "");

        if(view!=null){
            mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);
        }
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(settingLogController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onClose(View view){
        finish();
        activityContextManager.getInstance().onRemoveStack(this);
        overridePendingTransition(R.anim.push_anim_in, R.anim.push_anim_out);
        mActivityClosed = true;
    }

    /* View Callback */

    private class orbotLogViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /* Model Callback */

    public class orbotModelCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume()
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityContextManager.getInstance().onRemoveStack(this);
        activityContextManager.getInstance().setOrbotLogController(null);
        mActivityClosed = true;
    }

    /* External Calls */

    public void onRefreshLayoutTheme(){
        if(!orbotLogStatus.sUIInteracted){
            orbotLogStatus.sScrollPosition = -1;
        }else {
            orbotLogStatus.sScrollPosition = 0;
        }

        mIsRecycleviewInteracting = false;
        recreate();
    }

}