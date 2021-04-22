package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.settingManager.logManager.settingLogController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.SimpleGestureFilter;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.helperManager.theme;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class orbotLogController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private orbotLogModel mOrbotModel;
    private orbotLogViewController mOrbotViewController;
    private RecyclerView mRecycleView;
    private orbotLogAdapter mOrbotAdapter;
    private NestedScrollView mMainScroll;
    private FloatingActionButton mFloatingScroller;

    private TextView mLogs;
    private boolean mActivityClosed = false;
    private int mLogCounter = 0;
    private GestureDetector mSwipeDirectionDetector;
    private boolean mIsLayoutChanging = false;

    /* INITIALIZATIONS */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        overridePendingTransition(R.anim.push_anim_out_reverse, R.anim.push_anim_in_reverse);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.orbot_log_view);

        viewsInitializations();
        onUpdateLogs();
        initializeLogs();
        onInitListener();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        if(newConfig.uiMode != getResources().getConfiguration().uiMode){
            activityContextManager.getInstance().onResetTheme();
            theme.getInstance().onConfigurationChanged(this);
        }

        super.onConfigurationChanged(newConfig);
    }

    public void viewsInitializations() {
        mRecycleView = findViewById(R.id.pLogRecycleView);
        mLogs = findViewById(R.id.pLogs);
        mMainScroll = findViewById(R.id.mMainScroll);
        mFloatingScroller = findViewById(R.id.pFloatingScroller);

        activityContextManager.getInstance().setOrbotLogController(this);
        activityContextManager.getInstance().onStack(this);
        mOrbotViewController = new orbotLogViewController(this, mLogs, mRecycleView);
        mOrbotModel = new orbotLogModel();

    }

    public void initializeLogs(){
        mLogCounter = 0;
        if(status.sLogListView){
            mLogCounter = orbotLocalConstants.mTorLogsHistory.size();
            mOrbotModel.setList(orbotLocalConstants.mTorLogsHistory);
            LinearLayoutManager layoutManager = new LinearLayoutManager(orbotLogController.this);
            orbotLogAdapter adapter = new orbotLogAdapter(mOrbotModel.getList(),new orbotLogController.orbotModelCallback());
            mOrbotAdapter = adapter;
            layoutManager.setReverseLayout(true);


            mRecycleView.setAdapter(new AlphaInAnimationAdapter(adapter));
            mRecycleView.getItemAnimator().setAddDuration(250);
            mRecycleView.getItemAnimator().setChangeDuration(250);
            mRecycleView.getItemAnimator().setMoveDuration(250);
            mRecycleView.getItemAnimator().setRemoveDuration(250);
            mRecycleView.setNestedScrollingEnabled(false);
            mRecycleView.setLayoutManager(new LinearLayoutManager(orbotLogController.this));
            mOrbotAdapter.notifyDataSetChanged();
        }else {
            logToString();
        }
        mOrbotViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_INIT_VIEWS, null);
        mRecycleView.smoothScrollToPosition(mOrbotModel.getList().size());
    }

    public void logToString(){
        for(int mCounter=0;mCounter<orbotLocalConstants.mTorLogsHistory.size();mCounter++){
            mOrbotViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS, Collections.singletonList(orbotLocalConstants.mTorLogsHistory.get(mCounter).getLog()));
            mLogCounter+=1;
        }
    }

    /* LISTENERS */

    private void onInitListener(){

        mRecycleView.getViewTreeObserver().addOnGlobalLayoutListener(() -> mIsLayoutChanging = false);

        mMainScroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if(status.sLogListView){
                if(mMainScroll.canScrollVertically(1)){
                    if(mFloatingScroller.getAlpha()==0){
                        mFloatingScroller.setVisibility(View.VISIBLE);
                        mFloatingScroller.animate().cancel();
                        mFloatingScroller.animate().alpha(1);
                    }
                }else {
                    mFloatingScroller.animate().cancel();
                    mFloatingScroller.animate().alpha(0).withEndAction(() -> mFloatingScroller.setVisibility(View.GONE));
                }
            }
        });

        mSwipeDirectionDetector=new GestureDetector(this,new SimpleGestureFilter(){

            @Override
            public boolean onSwipe(Direction direction) {
                if (direction==Direction.right){
                    finish();
                    overridePendingTransition(R.anim.push_anim_in, R.anim.push_anim_out);
                }
                return true;
            }
        });
    }

    public void onUpdateLogs(){

        new Thread(){
            public void run(){
                while (true){
                    try {
                        if(mActivityClosed){
                            break;
                        }
                        boolean mLayoutChangeTemp = mIsLayoutChanging;
                        if(mIsLayoutChanging){
                            if(status.sLogListView){
                                sleep(1000);
                            }else {
                                sleep(50);
                            }
                            continue;
                        }else {
                            if(status.sLogListView){
                                sleep(500);
                            }else {
                                sleep(50);
                            }
                            if(mLayoutChangeTemp != mIsLayoutChanging){
                                continue;
                            }
                        }
                        if(orbotLocalConstants.mTorLogsHistory.size()>0){
                            mIsLayoutChanging = true;
                            runOnUiThread(() -> {
                                if(orbotLocalConstants.mTorLogsHistory.size()>mLogCounter){
                                    mOrbotModel.getList().add(orbotLocalConstants.mTorLogsHistory.get(mLogCounter));
                                    if(!status.sLogListView){
                                        mOrbotViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS, Collections.singletonList(orbotLocalConstants.mTorLogsHistory.get(mLogCounter).getLog()));
                                        mIsLayoutChanging = false;
                                        onScrollBottom(null);
                                    }else {
                                        if(mOrbotAdapter!=null){
                                            mOrbotAdapter.notifyItemInserted(mOrbotModel.getList().size()-1);
                                        }
                                    }
                                }else {
                                    mIsLayoutChanging = false;
                                }
                                mLogCounter+=1;
                            });
                            sleep(500);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void onScrollBottom(View view) {
        mMainScroll.fullScroll(View.FOCUS_DOWN);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityContextManager.getInstance().onRemoveStack(this);
        activityContextManager.getInstance().setOrbotLogController(null);
        mActivityClosed = true;
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onResume()
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(inSignatureArea(event)){
            try{
                mSwipeDirectionDetector.onTouchEvent(event);
            }catch (Exception ignored){ }
        }
        return super.dispatchTouchEvent(event);
    }

    public boolean inSignatureArea(MotionEvent ev) {
        float mEventY = ev.getY();
        return mEventY>helperMethod.pxFromDp(300) || ev.getX()<helperMethod.pxFromDp(80);
    }


}