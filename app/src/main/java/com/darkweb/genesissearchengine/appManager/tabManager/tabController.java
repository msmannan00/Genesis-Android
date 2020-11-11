package com.darkweb.genesissearchengine.appManager.tabManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
import com.darkweb.genesissearchengine.appManager.settingManager.settingHomePage.settingController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
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

public class tabController extends AppCompatActivity
{
    /*Private Views*/
    private Button mTabs;

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
        onCustomeListeners();
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

        mtabViewController = new tabViewController(this, mTabs);
    }
    public void initializeList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(tabController.this);
        tabAdapter adapter = new tabAdapter(mListModel.getList(),new adapterCallback(), mHomeController.getmGeckoView(), ((tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB,null)).getmId());
        mTabAdapter = adapter;
        layoutManager.setReverseLayout(true);

        mRecycleView.setAdapter(adapter);
        mRecycleView.setItemViewCacheSize(100);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setAdapter(adapter);
        mRecycleView.setItemViewCacheSize(100);
        mRecycleView.setDrawingCacheEnabled(true);
        mRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecycleView.setLayoutManager(new LinearLayoutManager(tabController.this));
    }

    /*View Handlers*/

    public void onCustomeListeners(){

    }

    public void onReleaseDisplay(){
        mTabAdapter.onClose();
        mHomeController.onAcquireDisplay();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.popup_anim_in, R.anim.popup_anim_out);
    }

    public void onBackPressed(View view) {
        onReleaseDisplay();
        finish();
        overridePendingTransition(R.anim.popup_anim_in, R.anim.popup_anim_out);
    }

    public void onRemoveView(int pId){
        for(int mCounter=0; mCounter<mListModel.getList().size();mCounter++){
            if(mListModel.getList().get(mCounter).getSession().getSessionID() == pId){
                mListModel.getList().get(mCounter).releaseGeckoView();
                mListModel.getList().remove(mCounter);
                mTabAdapter.notifyDataSetChanged();
                //mTabAdapter.notifyItemRemoved(mCounter);
                //mTabAdapter.notifyItemRangeChanged(mCounter-1, mListModel.getList().size());
            }
        }
    }

    public void initTabCount()
    {
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.INIT_TAB_COUNT, null);
    }

    public void onNewTabInvoked(){
        mHomeController.onNewTab(true,false);
        onReleaseDisplay();
        finish();
        overridePendingTransition(R.anim.popup_anim_in, R.anim.popup_anim_out);
    }

    /*UI Triggers*/

    public void openTabMenu(View view) {
        mtabViewController.onTrigger(tabEnums.eTabViewCommands.M_SHOW_MENU, Collections.singletonList(view));
    }

    /*Tab Menu*/

    public void onMenuTrigger(View pView){
        if(pView.getId() == R.id.pNewTab){
            onNewTabInvoked();
        }
        else if(pView.getId() == R.id.pCloseTab){

        }
        else if(pView.getId() == R.id.pOpenSetting){
            mtabViewController.onTrigger(tabEnums.eTabViewCommands.M_DISMISS_MENU, null);
            helperMethod.openActivity(settingController.class, constants.CONST_LIST_HISTORY, this,true);
        }
    }

    public void onTabRowChanged(int pId){
        for(int mCounter=0; mCounter<mListModel.getList().size();mCounter++){
            if(mListModel.getList().get(mCounter).getSession().getSessionID() == pId){
                mListModel.getList().get(mCounter).releaseGeckoView();
                mTabAdapter.notifyItemChanged(mCounter);
            }
        }
    }

    @Override
    public void onTrimMemory(int level)
    {
        if(status.sSettingIsAppPaused && (level==80 || level==15))
        {
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.HOME_LOW_MEMORY,true));
            onReleaseDisplay();
            finish();
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

    /*Event Observer*/

    public class adapterCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            return null;
        }
    }
}
