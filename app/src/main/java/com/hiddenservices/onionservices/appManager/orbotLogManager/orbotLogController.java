package com.hiddenservices.onionservices.appManager.orbotLogManager;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.settingManager.logManager.settingLogController;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.appManager.activityThemeManager;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import com.hiddenservices.onionservices.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.torproject.android.service.wrapper.logRowModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCallbackCommands.M_UPDATE_FLOATING_BUTTON;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCallbackCommands.M_UPDATE_LOGS;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCallbackCommands.M_UPDATE_RECYCLE_VIEW;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCommands.M_GET_LIST;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCommands.M_GET_LIST_SIZE;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogViewCommands.M_FLOAT_BUTTON_UPDATE;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogViewCommands.M_SCROLL_BOTTOM;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogViewCommands.M_SCROLL_TOP;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogViewCommands.M_SCROLL_TO_POSITION;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogViewCommands.M_SHOW_FLOATING_TOOLBAR;


public class orbotLogController extends AppCompatActivity implements ViewTreeObserver.OnScrollChangedListener, ViewTreeObserver.OnGlobalLayoutListener {

    /* PRIVATE VARIABLES */

    private orbotLogModel mOrbotModel;
    private orbotLogViewController mOrbotLogViewController;
    private orbotLogAdapter mOrbotAdapter;

    /* UI VARIABLES */

    private TextView mOrbotLogLoadingText;
    private RecyclerView mOrbotLogRecycleView;
    private NestedScrollView mOrbotLogNestedScroll;
    private FloatingActionButton mOrbotLogFloatingToolbar;
    private boolean mIsRecycleviewInteracting = false;

    /* INITIALIZATIONS */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        activityContextManager.getInstance().setOrbotLogController(this);
        activityContextManager.getInstance().onStack(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.orbot_log_view);
        initializeStartupAnimation();

        initializeViews();
        initializeLogs();
        initListener();
    }

    public void initializeStartupAnimation() {
        if (activityContextManager.getInstance().getHomeController() == null || activityContextManager.getInstance().getHomeController().isSplashScreenLoading()) {
            overridePendingTransition(R.anim.translate_fade_left, R.anim.translate_fade_right);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        if (newConfig.uiMode != getResources().getConfiguration().uiMode) {
            activityContextManager.getInstance().onResetTheme();
            activityThemeManager.getInstance().onConfigurationChanged(this);
        }

        mOrbotLogNestedScroll.stopNestedScroll();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            initScrollPositionOnConfigurationChanged();
        }

        super.onConfigurationChanged(newConfig);
    }

    private void initScrollPositionOnConfigurationChanged() {
        helperMethod.onDelayHandler(orbotLogController.this, 150, () -> {
            if (!orbotLogStatus.sUIInteracted && ((int) mOrbotModel.onTrigger(M_GET_LIST_SIZE) > 1)) {
                if (mOrbotLogNestedScroll.canScrollVertically(enums.ScrollDirection.VERTICAL)) {
                    mOrbotLogViewController.onTrigger(M_SCROLL_BOTTOM);
                    orbotLogStatus.sScrollPosition = -1;
                    onScrollBottom();
                }
            } else {
                mOrbotLogViewController.onTrigger(M_SCROLL_TOP);
                orbotLogStatus.sScrollPosition = 0;
            }
            return null;
        });
    }

    private void initializeViews() {
        mOrbotLogRecycleView = findViewById(R.id.pOrbotLogRecycleView);
        mOrbotLogLoadingText = findViewById(R.id.pOrbotLogLoadingText);
        mOrbotLogNestedScroll = findViewById(R.id.pOrbotLogNestedScroll);
        mOrbotLogFloatingToolbar = findViewById(R.id.pOrbotLogFloatingToolbar);

        mOrbotLogViewController = new orbotLogViewController(this, new orbotLogViewCallback(), mOrbotLogLoadingText, mOrbotLogRecycleView, mOrbotLogNestedScroll, mOrbotLogFloatingToolbar);
        mOrbotLogViewController.onInit();

        mOrbotModel = new orbotLogModel(this, new orbotModelCallback());
        mOrbotModel.onInit();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeLogs() {
        if (status.sLogThemeStyleAdvanced) {
            orbotLogAdapter adapter = new orbotLogAdapter(((ArrayList) mOrbotModel.onTrigger(M_GET_LIST)), new orbotLogController.orbotAdapterCallback());
            mOrbotAdapter = adapter;

            Objects.requireNonNull(mOrbotLogRecycleView.getItemAnimator()).setAddDuration(350);
            mOrbotLogRecycleView.setAdapter(adapter);
            mOrbotLogRecycleView.setNestedScrollingEnabled(false);
            mOrbotLogRecycleView.setLayoutManager(new LinearLayoutManager(orbotLogController.this));
            mOrbotAdapter.notifyDataSetChanged();

        } else {
            logToString();
        }
        mOrbotLogViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_INIT_VIEWS, Collections.singletonList(status.sLogThemeStyleAdvanced));
        mOrbotLogRecycleView.smoothScrollToPosition((int) mOrbotModel.onTrigger(M_GET_LIST_SIZE));
    }


    /* LISTENERS */

    @Override
    public void onGlobalLayout() {
        if (orbotLogStatus.sOrientation == -1) {
            orbotLogStatus.sOrientation = getResources().getConfiguration().orientation;
        }

        if (orbotLogStatus.sOrientation != getResources().getConfiguration().orientation && orbotLogStatus.sScrollPosition != -1 && orbotLogStatus.sScrollPosition != 0) {
            orbotLogStatus.sScrollPosition = 0;
            orbotLogStatus.sOrientation = getResources().getConfiguration().orientation;

            mOrbotLogNestedScroll.stopNestedScroll();
            mOrbotLogViewController.onTrigger(M_SCROLL_TO_POSITION, Collections.singletonList(orbotLogStatus.sScrollPosition));
        } else {
            if (orbotLogStatus.sScrollPosition != -1 && orbotLogStatus.sUIInteracted) {
                mOrbotLogViewController.onTrigger(M_SCROLL_TO_POSITION, Collections.singletonList(orbotLogStatus.sScrollPosition));
            } else if (mOrbotLogNestedScroll.canScrollVertically(enums.ScrollDirection.VERTICAL)) {
                if (orbotLogStatus.sScrollPosition == -1) {
                    onScrollBottom();
                } else {
                    mOrbotLogViewController.onTrigger(M_SCROLL_TO_POSITION, Collections.singletonList((int) (mOrbotModel.onTrigger(M_GET_LIST_SIZE)) * 100));
                }
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mOrbotLogNestedScroll.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                orbotLogStatus.sScrollPosition = scrollY;
                if (!mOrbotLogNestedScroll.canScrollVertically(1)) {
                    orbotLogStatus.sScrollPosition = -1;
                }
            });
        }

        mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);
        mOrbotLogRecycleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        orbotLogStatus.sOrientation = getResources().getConfiguration().orientation;
    }

    @Override
    public void onScrollChanged() {
        if (mOrbotLogNestedScroll.getChildAt(0).getBottom() <= (mOrbotLogNestedScroll.getHeight() + mOrbotLogNestedScroll.getScrollY())) {
            mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);
            if (!mIsRecycleviewInteracting) {
                orbotLogStatus.sUIInteracted = false;
            }
        } else if (mOrbotLogNestedScroll.getScrollY() == 0) {
            helperMethod.onDelayHandler(orbotLogController.this, 300, () -> {
                if (mOrbotLogNestedScroll.getScrollY() == 0) {
                    mOrbotLogViewController.onTrigger(M_SHOW_FLOATING_TOOLBAR);
                }
                return null;
            });
        }
    }


    public void onScrollStateChange(RecyclerView recyclerView, int newState) {
        if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
            mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {

        mOrbotLogRecycleView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mOrbotLogNestedScroll.getViewTreeObserver().addOnScrollChangedListener(this);

        mOrbotLogRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                onScrollStateChange(recyclerView, newState);
            }
        });

        mOrbotLogRecycleView.setOnTouchListener((v, event) -> onTouch(event));

        mOrbotLogNestedScroll.setOnTouchListener((v, event) -> onTouch(event));

    }

    /* Helper Methods */

    public boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mIsRecycleviewInteracting = false;

            if (mOrbotLogNestedScroll.canScrollVertically(enums.ScrollDirection.VERTICAL)) {
                orbotLogStatus.sUIInteracted = true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            mIsRecycleviewInteracting = true;
        }

        mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);

        return false;
    }


    private void logToString() {
        if ((int) mOrbotModel.onTrigger(M_GET_LIST_SIZE) > 1) {
            for (int mCounter = 0; mCounter < (int) mOrbotModel.onTrigger(M_GET_LIST_SIZE); mCounter++) {
                mOrbotLogViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS, Collections.singletonList(((ArrayList<logRowModel>) mOrbotModel.onTrigger(M_GET_LIST)).get(mCounter).getLog()));
            }
        }
    }

    private void onScrollBottom() {
        mOrbotLogNestedScroll.scrollTo(0, helperMethod.pxFromDp((int) mOrbotModel.onTrigger(M_GET_LIST_SIZE) * 100));
    }

    /* View Callback */

    public void onUITriggered(View view) {
        if (view.getId() == R.id.pOrbotLogFloatingToolbar) {
            if (!orbotLogStatus.sUIInteracted || view != null) {
                onScrollDownByFloatingToolabar();
            }
            if (view != null) {
                mOrbotLogViewController.onTrigger(M_FLOAT_BUTTON_UPDATE);
            }
        } else if (view.getId() == R.id.pOrbotLogSettings) {
            helperMethod.openActivity(settingLogController.class, constants.CONST_LIST_HISTORY, this, true);
        }
    }

    public void onScrollDownByFloatingToolabar() {
        if (!mIsRecycleviewInteracting) {
            orbotLogStatus.sUIInteracted = false;
            mOrbotLogNestedScroll.fullScroll(View.FOCUS_DOWN);
        }
    }

    public void onCloseTriggered(View view) {
        finish();
        activityContextManager.getInstance().onRemoveStack(this);
        initializeStartupAnimation();
    }

    /* View Callback */

    private class orbotLogViewCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> pData, Object pType) {
            return null;
        }
    }

    /* Adapter Callback*/

    public class orbotAdapterCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> pData, Object pType) {
            if (pType.equals(orbotLogEnums.eOrbotLogAdapterCommands.M_CLOSE)) {
                helperMethod.onDelayHandler(orbotLogController.this, 500, () -> {
                    finish();
                    return null;
                });
            }
            return null;
        }
    }

    /* Model Callback */

    public class orbotModelCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> pData, Object pType) {
            if (pType.equals(M_UPDATE_FLOATING_BUTTON)) {
                if (!orbotLogStatus.sUIInteracted) {
                    onScrollDownByFloatingToolabar();
                }
            } else if (pType.equals(M_UPDATE_LOGS)) {
                mOrbotLogViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS, Collections.singletonList(((ArrayList<logRowModel>) mOrbotModel.onTrigger(M_GET_LIST)).get((int) pData.get(0)).getLog()));
            } else if (pType.equals(M_UPDATE_RECYCLE_VIEW)) {
                if (mOrbotAdapter != null) {
                    mOrbotAdapter.notifyItemInserted((int) pData.get(0));
                }
            }
            return null;
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume() {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        onCloseTriggered(null);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityContextManager.getInstance().onRemoveStack(this);
        activityContextManager.getInstance().setOrbotLogController(null);
    }

    /* External Calls */

    public void onRefreshLayoutTheme() {
        if (!orbotLogStatus.sUIInteracted) {
            orbotLogStatus.sScrollPosition = -1;
        } else {
            orbotLogStatus.sScrollPosition = 0;
        }

        mIsRecycleviewInteracting = false;
        recreate();
    }

}