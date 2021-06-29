package com.darkweb.genesissearchengine.appManager.tabManager;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.appManager.settingManager.advanceManager.settingAdvanceController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.dataManager.models.tabRowModel;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.darkweb.genesissearchengine.appManager.tabManager.tabEnums.eTabViewCommands.ON_HIDE_UNDO_DIALOG_FORCED;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManager.M_LOAD_NEW_TAB;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManager.M_RESET;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManager.M_UNDO;
import static org.mozilla.gecko.util.ThreadUtils.runOnUiThread;

public class tabController extends Fragment
{
    /*Private Views*/
    private Button mTabs;
    private ImageView mRemoveSelection;
    private ImageButton mMenuButton;
    private ImageButton mClearSelection;
    private ImageButton mNewTab;
    private TextView mEmptyView;
    private View mPopupUndo;
    private TextView mSelectionCount;
    private ImageView mBlocker;
    private View mRootView;
    private NestedScrollView mNestedScrollView = null;

    /*Private Variables*/

    private tabModel mListModel;
    private homeController mHomeController;
    private activityContextManager mContextManager;
    private tabViewController mtabViewController;
    private RecyclerView mRecycleView;
    private tabAdapter mTabAdapter;
    private Handler mScrollHandler = null;
    private Runnable mScrollRunnable = null;
    private boolean mTabGridLayoutEnabled = status.sTabGridLayoutEnabled;
    private float minScroll = 0;
    private float maxScroll = 0;
    private float mScreenHeight;
    private float getmScreenWidth;
    private boolean mClosed = false;
    private boolean mClosedByNewTab = false;
    private boolean mFirstLaunch = true;
    boolean mScrolled = true;
    boolean mTouchable = true;

    /*Initializations*/

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.tab_view, null);
        mRootView = root;
        mScreenHeight = helperMethod.pxFromDp(helperMethod.getScreenHeight(activityContextManager.getInstance().getHomeController()));
        getmScreenWidth = helperMethod.pxFromDp(helperMethod.getScreenWidth(activityContextManager.getInstance().getHomeController()));

        super.onCreate(savedInstanceState);
        return root;
    }

    @Override
    public void onDestroy() {
        mListModel = null;
        mHomeController = null;
        mContextManager = null;
        mtabViewController = null;
        mRecycleView = null;
        mTabAdapter = null;
        mScrollHandler = null;
        mScrollRunnable = null;
        mScrolled = false;
        mFirstLaunch = false;
        super.onDestroy();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activityContextManager.getInstance().setTabController(this);
        mTabGridLayoutEnabled = status.sTabGridLayoutEnabled;

        onInit();
    }


    public void onInit(){
        initializeViews();
        initializeActivity();
        initializeLocalEventHandlers();
        initializeList();
        initSwipe();

        mClosed = false;
        mClosedByNewTab = false;
        mTabGridLayoutEnabled = status.sTabGridLayoutEnabled;
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_RELEASE_BLOCKER, null);
        mNestedScrollView.scrollTo(0,0);
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_HIDE_UNDO_DIALOG_INIT, null);
        mRecycleView.animate().setDuration(150).alpha(1);
        mRecycleView.setAlpha(1);
    }

    public void onInitInvoked(){
        if(mFirstLaunch){
            mRecycleView.setAlpha(0);
            mFirstLaunch = false;
            mTouchable = true;

            ObjectAnimator alpha = ObjectAnimator.ofPropertyValuesHolder(mRecycleView, PropertyValuesHolder.ofFloat("alpha", 0, 1f));
            alpha.setDuration(300);
            alpha.setStartDelay(500);
            alpha.start();
            mEmptyView.setAlpha(0);


            initializeList();
            mClosed = false;
            mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_RELEASE_BLOCKER, null);
            mNestedScrollView.scrollTo(0,0);
            mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_HIDE_UNDO_DIALOG_INIT, null);
            mTabAdapter.notifyDataSetChanged();
            mEmptyView.animate().setStartDelay(1200).setDuration(0).alpha(1);
        }else {
            mTouchable = true;
            initializeList();
            mRecycleView.setAlpha(1);
            mEmptyView.setAlpha(1);
            mClosed = false;
            mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_RELEASE_BLOCKER, null);
            mNestedScrollView.scrollTo(0,0);
            mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_HIDE_UNDO_DIALOG_INIT, null);
            mTabAdapter.notifyDataSetChanged();
        }
    }

    public void onInitFirstElement(){
        runOnUiThread(() -> {
            if(mTabAdapter!=null){
                if(mBlocker.getVisibility() != View.VISIBLE){
                    mTabAdapter.notifyItemChanged(0);
                }
            }
        });
    }

    public void initializeActivity(){
        mListModel = new tabModel();
        mListModel.onTrigger(tabEnums.eModelCallback.M_SET_LIST, Collections.singletonList(dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TAB, null)));
        mContextManager = activityContextManager.getInstance();
        mHomeController = activityContextManager.getInstance().getHomeController();
        mContextManager.setTabController(this);
    }

    public void initializeViews(){
        mRecycleView = mRootView.findViewById(R.id.pRecycleView);
        mTabs = mRootView.findViewById(R.id.pTabs);
        mEmptyView = mRootView.findViewById(R.id.pEmptyView);
        mRemoveSelection = mRootView.findViewById(R.id.pRemoveSelection);
        mMenuButton = mRootView.findViewById(R.id.pMenuButton);
        mClearSelection = mRootView.findViewById(R.id.pClearSelection);
        mPopupUndo = mRootView.findViewById(R.id.pPopupUndo);
        mSelectionCount = mRootView.findViewById(R.id.pSelectionCount);
        mBlocker = mRootView.findViewById(R.id.pBlocker);
        mNestedScrollView = mRootView.findViewById(R.id.pNestedScroll);
        mNewTab = mRootView.findViewById(R.id.pNewTab);

        mtabViewController = new tabViewController(this, mTabs, mRemoveSelection, mMenuButton, mClearSelection, mPopupUndo, mSelectionCount, mBlocker, mRecycleView, mNestedScrollView, mEmptyView, mNewTab);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initializeLocalEventHandlers(){

        mTabs.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                onBackPressedInvoked(null);
            }
            return false;
        });

        mNestedScrollView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                onSwipeBounce(0);
            }

            return false;
        });

        mRecycleView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
                return !mTouchable;
            }
        });

        mNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            float scrollY = mNestedScrollView.getScrollY();

            minScroll = scrollY;
            int orientation = this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                maxScroll = mRecycleView.computeVerticalScrollRange() - mScreenHeight*0.350f + helperMethod.pxFromDp(helperMethod.getNavigationBarSize(getContext()).y);
            } else {
                maxScroll = mRecycleView.computeVerticalScrollRange() - getmScreenWidth*0.20f + helperMethod.pxFromDp(helperMethod.getNavigationBarSize(getContext()).y);
            }

            if(!mScrolled){
                onSwipeBounce(300);
            }
        });

        mNestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            mScrolled = false;
            if (mRecycleView.getChildAt(mRecycleView.getChildCount() - 1) != null) {
                if ((scrollY >= (mRecycleView.getChildAt(mRecycleView.getChildCount() - 1).getMeasuredHeight() - mRecycleView.getMeasuredHeight())) && scrollY > oldScrollY) {
                    Log.i("FUCK2:::::::",scrollY+"");
                    onSwipeBounce(0);
                }
            }
        });
    }

    public void onSwipeBounce(int mDuration){
        if(!mClosed){
            if(minScroll > maxScroll){
                if(mScrollHandler!=null){
                    mScrollHandler.removeCallbacksAndMessages(null);
                }

                mScrollHandler = new Handler();
                mScrollRunnable = () -> {
                    mNestedScrollView.clearFocus();
                    mRecycleView.clearFocus();
                    mNestedScrollView.smoothScrollTo(0, (int)maxScroll);
                };
                mScrollHandler.postDelayed(mScrollRunnable, mDuration);
            }else if(mScrollHandler!=null){
                mScrollHandler.removeCallbacksAndMessages(null);
            }
        }
    }

    public void initializeList(){
        tabAdapter adapter = new tabAdapter(mListModel.getList(),new adapterCallback());
        mTabAdapter = adapter;

        ((SimpleItemAnimator) Objects.requireNonNull(mRecycleView.getItemAnimator())).setSupportsChangeAnimations(false);

        mRecycleView.setAdapter(adapter);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setHasFixedSize(false);

        if(status.sTabGridLayoutEnabled){
            mRecycleView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        }else {
            mRecycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        }

        initTabCount(0);
    }

    /*Listeners*/

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                boolean mStatus = (boolean) mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.M_SELECTION_MENU_SHOWING, null);
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    if((Integer) viewHolder.itemView.getTag() >= mListModel.getList().size() || mStatus){
                        return 0;
                    }
                    else {
                        final int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                        final int swipeFlags = 0;
                        return makeMovementFlags(swipeFlags, dragFlags);
                    }
                } else {
                    if((Integer) viewHolder.itemView.getTag() >= mListModel.getList().size() || mStatus){
                        return 0;
                    }
                    else {
                        final int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                        final int swipeFlags = 0;
                        return makeMovementFlags(swipeFlags, dragFlags);
                    }
                }
            }

            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                onClearTabBackup();
                mListModel.onTrigger(tabEnums.eModelCallback.M_CLEAR_BACKUP_RETAIN_DATABASE,null);
                boolean mStatus = onInitRemoveView(position, true, true);
                if(mStatus){
                    mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.NOTIFY_SWIPE, Collections.singletonList(position));
                }
                viewHolder.itemView.animate().alpha(0);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    float alpha = 1 - (Math.abs(dX) / recyclerView.getWidth())*1.75f;
                    if(alpha <0.35){
                        alpha = 0.35f;
                    }
                    viewHolder.itemView.setAlpha(alpha);

                    Canvas mCanvas = (Canvas) mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_GENERATE_SWIPABLE_BACKGROUND, Arrays.asList(c, viewHolder, dX, actionState));
                    super.onChildDraw(mCanvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecycleView);
    }
    /*View Handlers*/

    public void onExitAndClearBackup(){
        mListModel.onTrigger(tabEnums.eModelCallback.M_CLEAR_BACKUP_WITHOUT_CLOSE,null);
    }

    public void onRemoveTab(int pIndex){
        mListModel.onTrigger(tabEnums.eModelCallback.M_REMOVE_TAB,Collections.singletonList(pIndex));
        if(mListModel.getList().size()<1){
            mRecycleView.animate().setDuration(200).alpha(0);
            onClearSelection(null);
        }
        initTabCount(400);
        new Handler().postDelayed(() -> {
            activityContextManager.getInstance().getHomeController().onLoadTabFromTabController();
        }, 500);
        mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.INIT_FIRST_ROW, null);
        onSwipeBounce(0);
    }

    public boolean onInitRemoveView(int pIndex, boolean pCreateBackup, boolean pShowPopupOnClearAll){
        if(mListModel.getList().size()<=pIndex){
            return false;
        }
        mListModel.onTrigger(tabEnums.eModelCallback.M_REMOVE_TAB,Collections.singletonList(pIndex));
        mListModel.getList().remove(pIndex);
        if(mListModel.getList().size()<1){
            mRecycleView.animate().setDuration(200).alpha(0).withEndAction(() -> {
                if(pShowPopupOnClearAll){
                    onShowUndoDialog();
                }
                mTabAdapter.notifyDataSetChanged();
                onClearSelection(null);
            });
            new Handler().postDelayed(() -> {
                activityContextManager.getInstance().getHomeController().onLoadTabFromTabController();
                onSwipeBounce(0);
            }, 500);
            return true;
        }else{
            if(pShowPopupOnClearAll){
                onShowUndoDialog();
            }
            mTabAdapter.notifyItemRangeChanged(pIndex, mTabAdapter.getItemCount() - pIndex);
            mTabAdapter.notifyItemChanged(0);
            new Handler().postDelayed(() -> {
                activityContextManager.getInstance().getHomeController().onLoadTabFromTabController();
                onSwipeBounce(0);
            }, 500);
            return true;
        }
    }

    public void initTabCount(int pDelay)
    {
        activityContextManager.getInstance().getHomeController().initTabCountForced();
    }

    public void onClose(){
        onClearTabBackup();
    }

    public void onNewTabInvoked(){
        ArrayList<tabRowModel> mBackup = (ArrayList<tabRowModel>)mListModel.onTrigger(tabEnums.eModelCallback.M_GET_BACKUP,null);
        if(mListModel.getList().size()>=1 && mBackup.size()!=1){
            mHomeController.onNewTabBackground(true,false);

            mClosedByNewTab = false;
            onPostExit();
            //onBackPressed();
            onClose();
        }
        else {
            onExitAndClearBackup();
        }
    }

    public void onRestoreTab(View view){

        mTouchable = true;
        final Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            ArrayList<tabRowModel> mBackup = (ArrayList<tabRowModel>)mListModel.onTrigger(tabEnums.eModelCallback.M_LOAD_BACKUP,null);
            mListModel.onTrigger(tabEnums.eModelCallback.M_CLEAR_BACKUP_RETAIN_DATABASE,null);

            if(mRecycleView.getAlpha()==0){
                mRecycleView.animate().cancel();
                mRecycleView.setVisibility(View.VISIBLE);
                mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.REINIT_DATA, Collections.singletonList(mBackup));
                mTabAdapter.notifyDataSetChanged();
                mRecycleView.animate().setDuration(200).alpha(1).withEndAction(() -> mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_HIDE_UNDO_DIALOG, null));
            }else {
                mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_HIDE_UNDO_DIALOG, null);
                mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.REINIT_DATA, Collections.singletonList(mBackup));
            }

            new Handler().postDelayed(() -> {
                activityContextManager.getInstance().getHomeController().onLoadTabFromTabController();
            }, 500);

        }, 100);
    }

    public void onShowUndoDialog(){
        pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(mHomeController), M_UNDO);
    }

    public void onClearTabBackup(){
        ArrayList<tabRowModel> mBackupIndex = (ArrayList<tabRowModel>)mListModel.onTrigger(tabEnums.eModelCallback.M_GET_BACKUP,null);
        for(int mCounter=0;mCounter<mBackupIndex.size();mCounter++){
            mBackupIndex.get(mCounter).getSession().closeSessionInstant();
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.CLOSE_TAB, Arrays.asList(mBackupIndex.get(mCounter).getSession(), mBackupIndex.get(mCounter).getmId()));
        }
    }

    public void onTabRowChanged(String pId){
        if(mListModel!=null){
            for(int mCounter=0; mCounter<mListModel.getList().size();mCounter++){
                if(mListModel.getList().get(mCounter)!=null && mListModel.getList().get(mCounter).getSession()!=null && mListModel.getList().get(mCounter).getSession().getSessionID()!=null && mListModel.getList().get(mCounter).getSession().getSessionID().equals(pId)){
                    if(mBlocker.getVisibility() != View.VISIBLE){
                        mTabAdapter.notifyItemChanged(mCounter);
                    }
                }
            }
        }
    }

    /*UI Triggers*/

    public void onPostExit() {
        if(mtabViewController!=null && !mClosedByNewTab){
            mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_EXIT, null);
        }
    }

    public void openTabMenu(View view) {
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.M_SHOW_MENU, Collections.singletonList(view));
    }

    public int getSelectionCount(){
        return (int)mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.GET_SELECTION_SIZE,null);
    }

    public boolean isSelectionOpened(){
        return mClearSelection.getVisibility() == View.VISIBLE;
    }

    public void onRemoveSelection(View view) {
        int mSelectionSize = (int)mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.GET_SELECTION_SIZE,null);
        if(mSelectionSize >= mListModel.getList().size()){
            mRecycleView.animate().setDuration(200).alpha(0).withEndAction(() -> {
                mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.M_REMOVE_ALL_SELECTION, null);
                //onShowUndoDialog();
                onClearSelection(null);
            });
        }else {
            mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.M_REMOVE_ALL_SELECTION, null);
        }

        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_SHOW_SELECTION_MENU, Arrays.asList(false,0));
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_HIDE_SELECTION, null);
        new Handler().postDelayed(() -> {
            activityContextManager.getInstance().getHomeController().onLoadTabFromTabController();
        }, 500);

        // mTabAdapter.notifyDataSetChanged();
        initTabCount(400);

    }

    public void onClearSelection(View view) {
        mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.M_CLEAR_ALL_SELECTION, null);
        if(!mClosed){
            mNestedScrollView.smoothScrollTo(0,0);
        }
    }

    public void onTriggerSelected(View view){
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_SHOW_SELECTION, null);
    }

    public void onBackPressedInvoked(View view) {
        mTabs.setPressed(true);
        onBackPressed();
    }

    /*Tab Menu*/

    public void onCloseAllTabs(){
        if(mTabAdapter!=null){
            mTabAdapter.notifyDataSetChanged();
        }
    }

    public void onMenuTrigger(View pView){
        if(pView.getId() == R.id.pNewTab){
            if(!isSelectionOpened()){
                onClearTabBackup();
                mListModel.onTrigger(tabEnums.eModelCallback.M_CLEAR_BACKUP_RETAIN_DATABASE,null);
                onNewTabInvoked();
                mClosedByNewTab = true;
            }
            mHomeController.onBackPressed();
        }
        if(pView.getId() == R.id.pNewTabMenu){
            onClearTabBackup();
            mListModel.onTrigger(tabEnums.eModelCallback.M_CLEAR_BACKUP_RETAIN_DATABASE,null);
            onNewTabInvoked();
            mHomeController.onBackPressed();
            mClosedByNewTab = true;
        }
        else if(pView.getId() == R.id.pCloseTab){
            mTouchable = false;
            onClearTabBackup();
            mListModel.onTrigger(tabEnums.eModelCallback.M_CLEAR_BACKUP_RETAIN_DATABASE,null);
            mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.REMOVE_ALL, null);
            onClearSelection(null);
        }
        else if(pView.getId() == R.id.pOpenSetting){
            activityContextManager.getInstance().getHomeController().onBackPressed();
            new Handler().postDelayed(() ->
            {
                helperMethod.openActivity(settingAdvanceController.class, constants.CONST_LIST_HISTORY, activityContextManager.getInstance().getHomeController(),true);
            }, 250);
        }
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.M_DISMISS_MENU, null);
    }

    @Override
    public void onResume()
    {
        status.sSettingIsAppPaused = false;
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause()
    {
        status.sSettingIsAppPaused = true;
        pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_RESET);
        super.onPause();
    }


    public boolean onBackPressed() {
        mtabViewController.onTrigger(ON_HIDE_UNDO_DIALOG_FORCED, null);

        if(mTabAdapter!=null){
            boolean mStatus = (boolean) mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.M_SELECTION_MENU_SHOWING, null);
            onClearTabBackup();
            onClearSelection(null);
            if(!mStatus){
                mHomeController.onDisableTabViewController();
                onClose();
                return true;
            }
        }
        return false;
    }

    /*Event Observer*/

    public class adapterCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if(e_type.equals(tabEnums.eTabAdapterCallback.ON_HIDE_SELECTION)){
                mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_HIDE_SELECTION, null);
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_SHOW_SELECTION)){
                onTriggerSelected(null);
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_CLEAR_TAB_BACKUP)){
                onClearTabBackup();
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_REMOVE_TAB)){
                onRemoveTab((Integer) data.get(0));
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_INIT_TAB_COUNT)){
                initTabCount(400);
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_BACK_PRESSED)){
                onBackPressed();
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_LOAD_TAB)){
                mClosed = true;
                mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_HOLD_BLOCKER, null);
                mHomeController.onLoadTab((geckoSession)data.get(0),(boolean)data.get(1),true, true);
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW)){
                onInitRemoveView((Integer) data.get(0), true, true);
                initTabCount(400);
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW_RETAIN_BACKUP)){
                onInitRemoveView((Integer) data.get(0), false, (boolean)data.get(1));
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_SHOW_UNDO_POPUP)){
                onShowUndoDialog();
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.M_CLEAR_BACKUP)){
                onExitAndClearBackup();
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_SHOW_SELECTION_MENU)){
                mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_SHOW_SELECTION_MENU, data);
            }
            return null;
        }
    }
}
