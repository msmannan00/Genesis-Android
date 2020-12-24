package com.darkweb.genesissearchengine.appManager.tabManager;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
import com.darkweb.genesissearchengine.appManager.settingManager.settingHomePage.settingController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.recyclerview.animators.FadeInRightAnimator;

public class tabController extends AppCompatActivity
{
    /*Private Views*/
    private Button mTabs;
    private ImageView mRemoveSelection;
    private LinearLayout mTabsContainer;
    private ImageButton mMenuButton;
    private ImageButton mClearSelection;
    private View mPopupUndo;

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
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_view);
        initializeListModel();
        initializeViews();
        initializeList();
        initSwipe();
    }

    public void initializeListModel(){
        mListModel = new tabModel();
        mListModel.setList((ArrayList<tabRowModel>)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TAB, null));
        mContextManager = activityContextManager.getInstance();
        mHomeController = activityContextManager.getInstance().getHomeController();
        mContextManager.setTabController(this);
        pluginController.getInstance().logEvent(strings.EVENT_TAB_OPENED);
    }

    public void initializeViews(){
        mRecycleView = findViewById(R.id.pRecycleView);
        mTabs = findViewById(R.id.pTabs);
        mRemoveSelection = findViewById(R.id.pRemoveSelection);
        mTabsContainer = findViewById(R.id.pTabsContainer);
        mMenuButton = findViewById(R.id.pMenuButton);
        mClearSelection = findViewById(R.id.pClearSelection);
        mPopupUndo = findViewById(R.id.pPopupUndo);

        mtabViewController = new tabViewController(this, mTabs, mRemoveSelection, mTabsContainer, mMenuButton, mClearSelection, mPopupUndo);
    }

    public void initializeList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(tabController.this);
        tabAdapter adapter = new tabAdapter(mListModel.getList(),new adapterCallback());
        mTabAdapter = adapter;
        layoutManager.setReverseLayout(true);

        mRecycleView.setItemAnimator(new FadeInRightAnimator());
        Objects.requireNonNull(mRecycleView.getItemAnimator()).setAddDuration(200);
        mRecycleView.getItemAnimator().setRemoveDuration(200);
        mRecycleView.getItemAnimator().setMoveDuration(200);
        mRecycleView.getItemAnimator().setChangeDuration(450);

        mRecycleView.setAdapter(adapter);
        mRecycleView.setItemViewCacheSize(100);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setItemViewCacheSize(100);
        mRecycleView.setDrawingCacheEnabled(true);
        mRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecycleView.setLayoutManager(new LinearLayoutManager(tabController.this));
        mTabAdapter.notifyDataSetChanged();
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
                onClearTabBackup();
                onRemoveView(position);
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

    public void onBackPressedInvoked(View view) {
        onBackPressed();
    }

    public void onRemoveTab(int pIndex){
        mListModel.onRemoveTab(pIndex);
        initTabCount();
    }

    public void onClearTabBackup(){
        mListModel.onClearBackup();
    }

    public void onRemoveView(int pIndex){
        onRemoveTab(pIndex);
        mListModel.getList().remove(pIndex);
        mTabAdapter.notifyItemRemoved(pIndex);
        onShowUndoDialog();
    }

    public void initTabCount()
    {
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.INIT_TAB_COUNT, null);
        mHomeController.initTabCount();
    }

    public void onClose(){
        onClearTabBackup();
        if(mListModel.getList().size()<=0){
            mHomeController.onNewTab(false, false);
        }
        finish();
    }

    public void onNewTabInvoked(){
        mHomeController.onNewTab(true,false);
        onClose();
        overridePendingTransition(R.anim.popup_anim_in, R.anim.popup_anim_out);
    }

    public void onRestoreTab(View view){
        int mSize = mListModel.onLoadBackup();
        mTabAdapter.notifyItemRangeInserted(0, mSize);
        initTabCount();
    }

    public void onShowUndoDialog(){
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_SHOW_UNDO_DIALOG, null);
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
        mTabAdapter.onTrigger(tabEnums.eTabModelCommands.M_REMOVE_ALL_SELECTION, null);
        onShowUndoDialog();
    }

    public void onClearSelection(View view) {
        mTabAdapter.onTrigger(tabEnums.eTabModelCommands.M_CLEAR_ALL_SELECTION, null);
    }

    /*Tab Menu*/

    public void onMenuTrigger(View pView){
        if(pView.getId() == R.id.pNewTab){
            onNewTabInvoked();
        }
        else if(pView.getId() == R.id.pCloseTab){
            mtabViewController.onTrigger(tabEnums.eTabViewCommands.M_DISMISS_MENU, null);
            for(int mCounter=0;mCounter<mListModel.getList().size();mCounter++){
                onRemoveView(mCounter);
                mCounter-=1;
            }
        }
        else if(pView.getId() == R.id.pOpenSetting){
            mtabViewController.onTrigger(tabEnums.eTabViewCommands.M_DISMISS_MENU, null);
            helperMethod.openActivity(settingController.class, constants.CONST_LIST_HISTORY, this,true);
        }
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
        activityContextManager.getInstance().setCurrentActivity(this);
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
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        int mSize = (Integer) mTabAdapter.onTrigger(tabEnums.eTabModelCommands.M_SELECTED_LIST_SIZE, null);
        if(mSize>0){
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
                mtabViewController.onTrigger(tabEnums.eTabViewCommands.ON_SHOW_SELECTION, null);
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
                onClearTabBackup();
                onRemoveView((Integer) data.get(0));
            }
            return null;
        }
    }
}
