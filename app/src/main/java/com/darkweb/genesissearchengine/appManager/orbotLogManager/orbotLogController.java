package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.settingManager.logManager.settingLogController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.helperManager.theme;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.darkweb.genesissearchengine.appManager.orbotLogManager.orbotLogEnums.eOrbotLogViewCommands.M_SCROLL_THEME_UPDATE;


public class orbotLogController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private orbotLogModel mOrbotModel;
    private orbotLogViewController mOrbotLogViewController;
    private orbotLogAdapter mOrbotAdapter;
    private boolean mActivityClosed = false;
    private int mLogCounter = 0;
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
            theme.getInstance().onConfigurationChanged(this);
        }

        super.onConfigurationChanged(newConfig);
    }

    public void initializeViews() {
        mLogRecycleView = findViewById(R.id.pLogRecycleView);
        mLogs = findViewById(R.id.pLogs);
        mNestedScrollView = findViewById(R.id.pNestedScrollView);
        mFloatingScroller = findViewById(R.id.pFloatingScroller);

        mOrbotLogViewController = new orbotLogViewController(this, new orbotLogViewCallback() , mLogs, mLogRecycleView, mNestedScrollView, mFloatingScroller);
        mOrbotModel = new orbotLogModel();
    }

    public void initializeLogs(){
        mLogCounter = 0;
        if(status.sLogThemeStyleAdvanced){
            mLogCounter = orbotLocalConstants.mTorLogsHistory.size();
            mOrbotModel.setList(orbotLocalConstants.mTorLogsHistory);
            LinearLayoutManager layoutManager = new LinearLayoutManager(orbotLogController.this);
            orbotLogAdapter adapter = new orbotLogAdapter(mOrbotModel.getList(),new orbotLogController.orbotModelCallback());
            mOrbotAdapter = adapter;
            layoutManager.setReverseLayout(true);

            mLogRecycleView.setAdapter(adapter);
            Objects.requireNonNull(mLogRecycleView.getItemAnimator()).setAddDuration(250);

            mLogRecycleView.setNestedScrollingEnabled(false);
            mLogRecycleView.setLayoutManager(new LinearLayoutManager(orbotLogController.this));

            mOrbotAdapter.notifyDataSetChanged();

        }else {
            logToString();
        }
        mOrbotLogViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_INIT_VIEWS, Collections.singletonList(status.sLogThemeStyleAdvanced));
        mLogRecycleView.smoothScrollToPosition(mOrbotModel.getList().size());
    }


    /* LISTENERS */

    @SuppressLint("ClickableViewAccessibility")
    private void initListener(){

        mLogRecycleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi") @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if(orbotLogStatus.sScrollPosition!=-1){
                    mNestedScrollView.scrollTo(0, orbotLogStatus.sScrollPosition);
                }else if(mNestedScrollView.canScrollVertically(1)){
                    onScrollBottom(null);
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    mNestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        orbotLogStatus.sScrollPosition = scrollY;
                    });
                }
                mOrbotLogViewController.onTrigger(M_SCROLL_THEME_UPDATE);
                mLogRecycleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mNestedScrollView.setOnTouchListener((v, event) -> {
            if(mNestedScrollView.canScrollVertically(1)){
                orbotLogStatus.sUIInteracted = true;
            }

            if(event.getAction() == MotionEvent.ACTION_UP){
                mOrbotLogViewController.onTrigger(M_SCROLL_THEME_UPDATE);
            }

            if(event.getAction() == MotionEvent.ACTION_UP){
                mIsRecycleviewInteracting = false;
            }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                mIsRecycleviewInteracting = true;
            }

            return false;
        });

        mNestedScrollView.getViewTreeObserver() .addOnScrollChangedListener(() -> {
            if (mNestedScrollView.getChildAt(0).getBottom() <= (mNestedScrollView.getHeight() + mNestedScrollView.getScrollY())) {
                mOrbotLogViewController.onTrigger(M_SCROLL_THEME_UPDATE);

                if(!mIsRecycleviewInteracting){
                    orbotLogStatus.sUIInteracted = false;
                }
            }
        });
    }

    public void logToString(){
        for(int mCounter=0;mCounter<orbotLocalConstants.mTorLogsHistory.size();mCounter++){
            mOrbotLogViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS, Collections.singletonList(orbotLocalConstants.mTorLogsHistory.get(mCounter).getLog()));
            mLogCounter+=1;
        }
    }

    public void updateLogs(){
        new Thread(){
            public void run(){
                while (!mActivityClosed){
                    try {
                        if(status.sLogThemeStyleAdvanced){
                            sleep(800);
                        }else {
                            sleep(100);
                        }

                        if(orbotLocalConstants.mTorLogsHistory.size()>0){
                            runOnUiThread(() -> {
                                if(orbotLocalConstants.mTorLogsHistory.size()>mLogCounter){
                                    mOrbotModel.getList().add(orbotLocalConstants.mTorLogsHistory.get(mLogCounter));
                                    if(!status.sLogThemeStyleAdvanced){
                                        mOrbotLogViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS, Collections.singletonList(orbotLocalConstants.mTorLogsHistory.get(mLogCounter).getLog()));
                                    }else {
                                        if(mOrbotAdapter!=null){
                                            mOrbotAdapter.notifyItemInserted(mOrbotModel.getList().size()-1);
                                        }
                                    }
                                    mLogCounter+=1;
                                }
                            });
                        }
                        sleep(100);
                        if(!orbotLogStatus.sUIInteracted){
                            runOnUiThread(() -> {
                                if(orbotLocalConstants.mTorLogsHistory.size()>mLogCounter) {
                                    onScrollBottom(null);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /* View Callback */

    public void onScrollBottom(View view) {
        mNestedScrollView.fullScroll(View.FOCUS_DOWN);
        orbotLogStatus.sUIInteracted = false;
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
        orbotLogStatus.sScrollPosition = 0;
        orbotLogStatus.sUIInteracted = false;
        mIsRecycleviewInteracting = false;
        recreate();
    }

}