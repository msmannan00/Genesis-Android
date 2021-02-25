package com.darkweb.genesissearchengine.appManager.tabManager;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.appManager.settingManager.settingHomePage.settingHomeController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class tabController extends AppCompatActivity
{
    /*Private Views*/
    private Button mTabs;
    private ImageView mRemoveSelection;
    private ImageButton mMenuButton;
    private ImageButton mClearSelection;
    private View mPopupUndo;
    private TextView mSelectionCount;
    private ImageView mBlocker;

    /*Private Variables*/

    private tabModel mListModel;
    private homeController mHomeController;
    private activityContextManager mContextManager;
    private tabViewController mtabViewController;
    private RecyclerView mRecycleView;
    private tabAdapter mTabAdapter;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState){
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeActivity();
        initializeViews();
        initializeList();
        initSwipe();
        initializeLocalEventHandlers();
    }

    public void initializeActivity(){
        mListModel = new tabModel();
        mListModel.onTrigger(tabEnums.eModelCallback.M_SET_LIST, Collections.singletonList(dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TAB, null)));
        mContextManager = activityContextManager.getInstance();
        mHomeController = activityContextManager.getInstance().getHomeController();
        mContextManager.setTabController(this);
    }

    public void initializeViews(){
        mRecycleView = findViewById(R.id.pRecycleView);
        mTabs = findViewById(R.id.pTabs);
        mRemoveSelection = findViewById(R.id.pRemoveSelection);
        mMenuButton = findViewById(R.id.pMenuButton);
        mClearSelection = findViewById(R.id.pClearSelection);
        mPopupUndo = findViewById(R.id.pPopupUndo);
        mSelectionCount = findViewById(R.id.pSelectionCount);
        mBlocker = findViewById(R.id.pBlocker);

        mtabViewController = new tabViewController(this, mTabs, mRemoveSelection, mMenuButton, mClearSelection, mPopupUndo, mSelectionCount, mBlocker);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(tabController.this);
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
        mRecycleView.setLayoutManager(new LinearLayoutManager(tabController.this));
        initTabCount();
    }

    /*Listeners*/

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mListModel.onTrigger(tabEnums.eModelCallback.M_CLEAR_BACKUP_WITHOUT_CLOSE,null);
                boolean mStatus = onInitRemoveView(position, true);
                if(mStatus){
                    mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.NOTIFY_SWIPE, Collections.singletonList(position));
                }
                onShowUndoDialog();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Canvas mCanvas = (Canvas) mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_GENERATE_SWIPABLE_BACKGROUND, Arrays.asList(c, viewHolder, dX, actionState));
                super.onChildDraw(mCanvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecycleView);
    }
    /*View Handlers*/

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
        mHomeController.initTabCount();
    }

    public void onClose(){
        onClearTabBackup();
        finish();
    }

    public void onNewTabInvoked(){
        mHomeController.onNewTabBackground(true,false);
        onClose();
        overridePendingTransition(R.anim.popup_anim_in, R.anim.popup_anim_out);
    }

    public void onRestoreTab(View view){
        Log.i("FUCKSSS","FUCKSSS1 : " + (mPopupUndo.findViewById(R.id.pBlockerUndo).getVisibility()==View.VISIBLE));
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
        mListModel.onTrigger(tabEnums.eModelCallback.M_CLEAR_BACKUP_WITHOUT_CLOSE,null);
        initTabCount();
    }

    public void onShowUndoDialog(){
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_SHOW_UNDO_DIALOG, Collections.singletonList(mListModel.getList().size()));
    }

    public void onClearTabBackup(){
        ArrayList<tabRowModel> mBackupIndex = (ArrayList<tabRowModel>)mListModel.onTrigger(tabEnums.eModelCallback.M_GET_BACKUP,null);
        for(int mCounter=0;mCounter<mBackupIndex.size();mCounter++){
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.CLOSE_TAB, Collections.singletonList(mBackupIndex.get(mCounter).getSession()));
            mBackupIndex.get(mCounter).getSession().closeSession();
        }
    }

    public void onTabRowChanged(String pId){
        for(int mCounter=0; mCounter<mListModel.getList().size();mCounter++){
            if(mListModel.getList().get(mCounter).getSession().getSessionID().equals(pId)){
                mTabAdapter.notifyItemChanged(mCounter);
            }
        }
    }

    /*UI Triggers*/

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
        activityContextManager.getInstance().onRemoveStack(this);
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
            helperMethod.openActivity(settingHomeController.class, constants.CONST_LIST_HISTORY, this,true);
        }
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.M_DISMISS_MENU, null);
    }

    @Override
    public void onTrimMemory(int level)
    {
        if(status.sSettingIsAppPaused && (level==80 || level==15))
        {
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.HOME_LOW_MEMORY,true));
            onClose();
        }
    }

    @Override
    public void onResume()
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        status.sSettingIsAppPaused = false;
        activityContextManager.getInstance().onStack(this);
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

    @Override
    public void onBackPressed() {
        boolean mStatus = (boolean) mTabAdapter.onTrigger(tabEnums.eTabAdapterCommands.M_SELECTION_MENU_SHOWING, null);
        if(mStatus){
            onClearTabBackup();
            onClearSelection(null);
        }else {
            super.onBackPressed();
            onClose();
            overridePendingTransition(R.anim.popup_anim_in, R.anim.popup_anim_out);
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
                mListModel.onTrigger(tabEnums.eModelCallback.M_CLEAR_BACKUP_WITHOUT_CLOSE,null);
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
