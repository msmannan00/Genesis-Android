package com.darkweb.genesissearchengine.appManager.bookmarkManager;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.bookmarkEnums;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.sql;
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

import static com.darkweb.genesissearchengine.appManager.historyManager.bookmarkEnums.eBookmarkViewCommands.M_VERTIFY_SELECTION_MENU;


public class bookmarkController extends AppCompatActivity
{
    /*Private Variables*/

    private bookmarkModel mbookmarkModel;
    private homeController mHomeController;
    private activityContextManager mContextManager;
    private bookmarkAdapter mbookmarkAdapter;

    /*Private Views*/

    private ImageView mEmptyListNotification;
    private EditText mSearchInput;
    private RecyclerView mRecycleView;
    private Button mClearButton;
    private ImageButton mMenuButton;
    private ImageButton mSearchButton;

    private bookmarkViewController mbookmarkViewController;
    private boolean isUpdatingRecyclerView = false;
    private boolean mIsScrollingUp;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState){
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_view);
        initializeListModel();
        initializeViews();
        initializeList();
        initCustomListeners();
    }

    public void initializeListModel(){
        mbookmarkModel = new bookmarkModel();
        mContextManager = activityContextManager.getInstance();
        mHomeController = activityContextManager.getInstance().getHomeController();
        mContextManager.setBookmarkController(this);
        pluginController.getInstance().logEvent(strings.EVENT_BOOKMARK_OPENED);
    }
    public void initializeViews(){
        mEmptyListNotification = findViewById(R.id.pEmptyListNotification);
        mSearchInput = findViewById(R.id.pSearchInput);
        mRecycleView = findViewById(R.id.pRecycleView);
        mClearButton = findViewById(R.id.pClearButton);
        mMenuButton = findViewById(R.id.pMenuButton);
        mSearchButton = findViewById(R.id.pSearchButton);

        mbookmarkViewController = new bookmarkViewController(mEmptyListNotification, mSearchInput, mRecycleView, mClearButton,this, mMenuButton, mSearchButton);
    }
    public void initializeList(){
        ArrayList<bookmarkRowModel> model = (ArrayList<bookmarkRowModel>) dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_GET_BOOKMARK ,null);
        mbookmarkModel.setList(model);
        bookmarkAdapter adapter = new bookmarkAdapter(mbookmarkModel.getList(),new adapterCallback(), this);
        mbookmarkAdapter = adapter;
        adapter.invokeFilter(false);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setHasFixedSize(true);

        mRecycleView.setAdapter(adapter);
        mRecycleView.setItemViewCacheSize(100);
        mRecycleView.setDrawingCacheEnabled(true);
        mRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_UPDATE_LIST_IF_EMPTY, Arrays.asList(mbookmarkModel.getList().size(),0));
    }

    public void initCustomListeners(){
        mClearButton.requestFocusFromTouch();
        mClearButton.setOnClickListener(v -> pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getBookmarkController(), Collections.singletonList(strings.GENERIC_EMPTY_STR), enums.etype.clear_bookmark));

        mSearchInput.setOnEditorActionListener((v, actionId, event) ->{
            if (actionId == EditorInfo.IME_ACTION_NEXT)
            {
                helperMethod.hideKeyboard(this);
                return true;
            }
            return false;
        });

        mSearchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                mSearchInput.clearFocus();
            }else {
                ((bookmarkAdapter) Objects.requireNonNull(mRecycleView.getAdapter())).setFilter(mSearchInput.getText().toString());
                ((bookmarkAdapter) mRecycleView.getAdapter()).invokeFilter(true);
            }
        });

        mSearchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                ((bookmarkAdapter) Objects.requireNonNull(mRecycleView.getAdapter())).setFilter(mSearchInput.getText().toString());
                ((bookmarkAdapter) mRecycleView.getAdapter()).invokeFilter(true);
            }
        });
    }

    /*View Custom Overrides*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onTrimMemory(int level)
    {
        if(status.sSettingIsAppPaused && (level==80 || level==15))
        {
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.HOME_LOW_MEMORY,true));
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
    public void onPause()
    {
        status.sSettingIsAppPaused = true;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(mSearchInput.getVisibility() == View.VISIBLE){
            onHideSearch(null);
        }else if(mbookmarkAdapter.isLongPressMenuActive()){
            onClearMultipleSelection(null);
        }else {
            onBackPressed(null);
        }
    }

    /*External XML Listeners*/

    public void onBackPressed(View view){
        this.finish();
    }

    public void onHideSearch(View view) {
        ((bookmarkAdapter) mRecycleView.getAdapter()).onUpdateSearchStatus((boolean) mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_HIDE_SEARCH, null));
    }

    public void onLongPressMenu(View view) {
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_LONG_PRESS_MENU, Collections.singletonList(view));
    }

    public void onOpenMultipleTabs(View view) {
        ArrayList<String> m_long_selected_urk = (ArrayList<String>) mbookmarkAdapter.onTrigger(bookmarkEnums.eBookmarkAdapterCommands.GET_LONG_SELECTED_URL, null);
        for(int m_counter=0;m_counter<m_long_selected_urk.size();m_counter++){
            mHomeController.onOpenLinkNewTab(m_long_selected_urk.get(m_counter));
        }
        onBackPressed(null);
        mbookmarkAdapter.onTrigger(bookmarkEnums.eBookmarkAdapterCommands.M_CLEAR_LONG_SELECTED_URL,null);
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_CLOSE_MENU, null);
    }

    public void onShareSelectedURL(View view) {
        String m_joined_url = (String) mbookmarkAdapter.onTrigger(bookmarkEnums.eBookmarkAdapterCommands.GET_SELECTED_URL, null);
        helperMethod.shareURL(this, m_joined_url);
        mbookmarkAdapter.onTrigger(bookmarkEnums.eBookmarkAdapterCommands.M_CLEAR_LONG_SELECTED_URL,null);
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_CLOSE_MENU, null);
    }

    public void onClearMultipleSelection(View view) {
        mbookmarkAdapter.onTrigger(bookmarkEnums.eBookmarkAdapterCommands.M_CLEAR_LONG_SELECTED_URL, null);
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_CLOSE_MENU, null);
        mbookmarkViewController.onTrigger(M_VERTIFY_SELECTION_MENU,Collections.singletonList(true));
    }

    public void onDeleteSelected(View view) {
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_CLOSE_MENU, null);
        mbookmarkAdapter.onDeleteSelected();
    }

    /*Helper Methods*/

    public void onclearData(){
        mbookmarkModel.clearList();
        ((bookmarkAdapter) Objects.requireNonNull(mRecycleView.getAdapter())).invokeFilter(true );
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_CLEAR_LIST, null);
        databaseController.getInstance().execSQL(sql.SQL_CLEAR_BOOKMARK,null);
        finish();
    }


    /*Event Observer*/

    public class adapterCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            if(e_type.equals(enums.etype.url_triggered)){
                String url_temp = helperMethod.completeURL(data.get(0).toString());
                pluginController.getInstance().logEvent(strings.EVENT_BOOKMARK_TRIGGERED);
                mHomeController.onLoadURL(url_temp);
                finish();
            }
            else if(e_type.equals(enums.etype.url_triggered_new_tab)){
                String url_temp = helperMethod.completeURL(data.get(0).toString());
                pluginController.getInstance().logEvent(strings.EVENT_BOOKMARK_TRIGGERED);
                mHomeController.onOpenLinkNewTab(url_temp);
                finish();
            }
            else if(e_type.equals(enums.etype.url_clear)){
                mbookmarkModel.onManualClear((int)data.get(0));
            }
            else if(e_type.equals(enums.etype.url_clear_at)){
                dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_DELETE_BOOKMARK ,data);
            }
            else if(e_type.equals(enums.etype.is_empty)){
                mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_UPDATE_LIST_IF_EMPTY, Arrays.asList(mbookmarkModel.getList().size(),300));
            }
            else if(e_type.equals(enums.etype.remove_from_database)){
                databaseController.getInstance().deleteFromList((int)data.get(0),strings.HISTORY_TITLE);
            }
            else if(e_type.equals(enums.etype.on_verify_selected_url_menu)){
                mbookmarkViewController.onTrigger(M_VERTIFY_SELECTION_MENU, data);
            }
            return null;
        }
    }
}
