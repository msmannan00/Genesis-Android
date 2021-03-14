package com.darkweb.genesissearchengine.appManager.tabManager;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class tabController extends Fragment
{
    /*Private Views*/
    private Button mTabs;
    private ImageView mRemoveSelection;
    private ImageButton mMenuButton;
    private ImageButton mClearSelection;
    private View mPopupUndo;
    private TextView mSelectionCount;
    private ImageView mBlocker;
    private View mRootView;

    /*Private Variables*/

    private tabModel mListModel;
    private homeController mHomeController;
    private activityContextManager mContextManager;
    private tabViewController mtabViewController;
    private RecyclerView mRecycleView;
    private tabAdapter mTabAdapter;
    private ColorDrawable mBackground;

    /*Initializations*/

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.tab_view, null);
        mRootView = root;

        super.onCreate(savedInstanceState);
        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activityContextManager.getInstance().setTabController(this);
    }

    public void onInit(){
        initializeActivity();
        initializeViews();
        initializeLocalEventHandlers();
        initializeList();
        initSwipe();
    }

    public void initializeActivity(){
        mListModel = new tabModel();
        mListModel.onTrigger(tabEnums.eModelCallback.M_SET_LIST, Collections.singletonList(dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TAB, null)));
        mContextManager = activityContextManager.getInstance();
        mHomeController = activityContextManager.getInstance().getHomeController();
        mContextManager.setTabController(this);
    }

    public void initializeViews(){
        mBackground = new ColorDrawable();
        mRecycleView = mRootView.findViewById(R.id.pRecycleView);
        mTabs = mRootView.findViewById(R.id.pTabs);
        mRemoveSelection = mRootView.findViewById(R.id.pRemoveSelection);
        mMenuButton = mRootView.findViewById(R.id.pMenuButton);
        mClearSelection = mRootView.findViewById(R.id.pClearSelection);
        mPopupUndo = mRootView.findViewById(R.id.pPopupUndo);
        mSelectionCount = mRootView.findViewById(R.id.pSelectionCount);
        mBlocker = mRootView.findViewById(R.id.pBlocker);

        mtabViewController = new tabViewController(this, mTabs, mRemoveSelection, mMenuButton, mClearSelection, mPopupUndo, mSelectionCount, mBlocker, mRecycleView);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initializeLocalEventHandlers(){
        mTabs.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                onBackPressedInvoked(null);
            }
            return false;
        });
    }
    public void initializeList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        tabAdapter adapter = new tabAdapter(mListModel.getList(),new adapterCallback());
        mTabAdapter = adapter;
        layoutManager.setReverseLayout(true);

        ((SimpleItemAnimator) Objects.requireNonNull(mRecycleView.getItemAnimator())).setSupportsChangeAnimations(false);

        mRecycleView.setAdapter(adapter);
        mRecycleView.setItemViewCacheSize(100);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setItemViewCacheSize(100);
        mRecycleView.setDrawingCacheEnabled(true);
        mRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        initTabCount();
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
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    if((Integer) viewHolder.itemView.getTag() >= mListModel.getList().size()){
                        return 0;
                    }
                    else {
                        final int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                        final int swipeFlags = 0;
                        return makeMovementFlags(swipeFlags, dragFlags);
                    }
                } else {
                    if((Integer) viewHolder.itemView.getTag() >= mListModel.getList().size()){
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
                boolean mStatus = onInitRemoveView(position, true);
                if(mStatus){
                    mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.NOTIFY_SWIPE, Collections.singletonList(position));
                }
                onShowUndoDialog();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
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
            mRecycleView.animate().setStartDelay(250).alpha(0);
        }
        initTabCount();
        mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.INIT_FIRST_ROW, null);
    }

    public boolean onInitRemoveView(int pIndex, boolean pCreateBackup){
        mListModel.onTrigger(tabEnums.eModelCallback.M_REMOVE_TAB,Collections.singletonList(pIndex));
        mListModel.getList().remove(pIndex);
        initTabCount();
        if(mListModel.getList().size()<1){
            mRecycleView.animate().setStartDelay(250).alpha(0).withEndAction(() -> mTabAdapter.notifyDataSetChanged());
            return false;
        }else{
            mTabAdapter.notifyItemRangeChanged(pIndex, mTabAdapter.getItemCount() - pIndex);
            return true;
        }
    }

    public void initTabCount()
    {
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.INIT_TAB_COUNT, Collections.singletonList(mListModel.getList().size()));

        ViewGroup.LayoutParams params = mRecycleView.getLayoutParams();
        params.height = helperMethod.pxFromDp(mTabAdapter.getItemCount() * 90);
        mRecycleView.setLayoutParams(params);
    }

    public void onClose(){
        onClearTabBackup();
    }

    public void onNewTabInvoked(){
        mHomeController.onBackPressed();
        int mBackupList = ((ArrayList<tabRowModel>)mListModel.onTrigger(tabEnums.eModelCallback.M_GET_BACKUP,null)).size();
        if(mListModel.getList().size()-mBackupList>=1){
            mHomeController.onNewTabBackground(true,false);
        }
        onClose();
    }

    public void onRestoreTab(View view){
        mPopupUndo.findViewById(R.id.pBlockerUndo).setVisibility(View.VISIBLE);
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_HIDE_UNDO_DIALOG, null);

        if(mRecycleView.getAlpha()==0){
            initializeList();
            mRecycleView.animate().cancel();
            mRecycleView.setVisibility(View.VISIBLE);
            mRecycleView.animate().setDuration(250).alpha(1);
        }

        ArrayList<tabRowModel> mBackup = (ArrayList<tabRowModel>)mListModel.onTrigger(tabEnums.eModelCallback.M_LOAD_BACKUP,null);
        mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.REINIT_DATA, Collections.singletonList(mBackup));
        mListModel.onTrigger(tabEnums.eModelCallback.M_CLEAR_BACKUP_RETAIN_DATABASE,null);
        initTabCount();
    }

    public void onShowUndoDialog(){
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_SHOW_UNDO_DIALOG, Collections.singletonList(mListModel.getList().size()));
    }

    public void onClearTabBackup(){
        ArrayList<tabRowModel> mBackupIndex = (ArrayList<tabRowModel>)mListModel.onTrigger(tabEnums.eModelCallback.M_GET_BACKUP,null);
        for(int mCounter=0;mCounter<mBackupIndex.size();mCounter++){
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.CLOSE_TAB, Arrays.asList(mBackupIndex.get(mCounter).getSession(), mBackupIndex.get(mCounter).getmId()));
            mBackupIndex.get(mCounter).getSession().closeSession();
        }
    }

    public void onTabRowChanged(String pId){
        if(mListModel!=null){
            for(int mCounter=0; mCounter<mListModel.getList().size();mCounter++){
                if(mListModel.getList().get(mCounter).getSession().getSessionID().equals(pId)){
                    mTabAdapter.notifyItemChanged(mCounter);
                }
            }
        }
    }

    /*UI Triggers*/

    public void onPostExit() {
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_EXIT, null);
    }

    public void openTabMenu(View view) {
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.M_SHOW_MENU, Collections.singletonList(view));
    }

    public void onRemoveSelection(View view) {
        int mSelectionSize = (int)mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.GET_SELECTION_SIZE,null);
        if(mSelectionSize >= mListModel.getList().size()){
            mRecycleView.animate().setStartDelay(250).alpha(0).withEndAction(() -> {
                mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.M_REMOVE_ALL_SELECTION, null);
                onShowUndoDialog();
            });
        }else {
            mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.M_REMOVE_ALL_SELECTION, null);
        }

        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_SHOW_SELECTION_MENU, Arrays.asList(false,0));
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_HIDE_SELECTION, null);
    }

    public void onClearSelection(View view) {
        mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.M_CLEAR_ALL_SELECTION, null);
    }

    public void onTriggerSelected(View view){
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_SHOW_SELECTION, null);
    }

    public void onBackPressedInvoked(View view) {
        mTabs.setPressed(true);
        onBackPressed();
    }

    /*Tab Menu*/

    public void onMenuTrigger(View pView){
        if(pView.getId() == R.id.pNewTab){
            onNewTabInvoked();
        }
        else if(pView.getId() == R.id.pCloseTab){
            mRecycleView.animate().setDuration(250).alpha(0).withEndAction(() -> {
                onClearTabBackup();
                mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.REMOVE_ALL, null);
                initTabCount();
            });
        }
        else if(pView.getId() == R.id.pOpenSetting){
            // helperMethod.openActivity(settingHomeController.class, constants.CONST_LIST_HISTORY, this,true);
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
        onBackPressed();
        super.onStop();
    }

    @Override
    public void onPause()
    {
        status.sSettingIsAppPaused = true;
        onBackPressed();
        super.onPause();
    }


    public void onBackPressed() {
        if(mTabAdapter!=null){
            boolean mStatus = (boolean) mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.M_SELECTION_MENU_SHOWING, null);
            onClearTabBackup();
            onClearSelection(null);
            if(!mStatus){
                mHomeController.onDisableTabViewController();
                onClose();
            }
        }
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
                initTabCount();
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_BACK_PRESSED)){
                onBackPressed();
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_LOAD_TAB)){
                mHomeController.onLoadTab((geckoSession)data.get(0),(boolean)data.get(1));
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW)){
                onInitRemoveView((Integer) data.get(0), true);
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW_RETAIN_BACKUP)){
                onInitRemoveView((Integer) data.get(0), false);
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_SHOW_UNDO_DIALOG)){
                mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_SHOW_UNDO_DIALOG, Collections.singletonList(mListModel.getList().size()));
            }
            else if(e_type.equals(tabEnums.eTabAdapterCallback.ON_SHOW_SELECTION_MENU)){
                mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_SHOW_SELECTION_MENU, data);
            }

            return null;
        }
    }
}
