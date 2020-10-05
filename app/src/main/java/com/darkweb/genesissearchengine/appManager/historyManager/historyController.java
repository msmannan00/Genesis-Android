package com.darkweb.genesissearchengine.appManager.historyManager;

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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
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

import static com.darkweb.genesissearchengine.appManager.historyManager.historyEnums.eHistoryViewCommands.M_VERTIFY_SELECTION_MENU;

public class historyController extends AppCompatActivity
{
    /*Private Variables*/

    private historyModel m_list_model;
    private homeController m_home_controller;
    private activityContextManager m_context_manager;

    /*Private Views*/

    private ImageView m_empty_list;
    private EditText m_search;
    private RecyclerView m_listview;
    private Button m_clearButton;
    private ImageButton m_menu_button;
    private ImageButton m_search_button;

    private historyViewController m_history_view_controller;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState){
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_view);
        initializeListModel();
        initializeViews();
        initializeList();
        onEditorInvoked();
    }

    public void initializeListModel(){
        m_list_model = new historyModel();
        m_context_manager = activityContextManager.getInstance();
        m_home_controller = activityContextManager.getInstance().getHomeController();
        m_context_manager.setHistoryController(this);
        activityContextManager.getInstance().setHistoryController(this);
        pluginController.getInstance().logEvent(strings.HISTORY_OPENED);
    }
    public void initializeViews(){
        m_empty_list = findViewById(R.id.p_empty_list);
        m_search = findViewById(R.id.p_search);
        m_listview = findViewById(R.id.p_listview);
        m_clearButton = findViewById(R.id.p_clearButton);
        m_menu_button = findViewById(R.id.p_menu_button);
        m_search_button = findViewById(R.id.p_search_button);
        m_history_view_controller = new historyViewController(m_empty_list, m_search, m_listview, m_clearButton,this, m_menu_button, m_search_button);
    }
    public void initializeList(){
        ArrayList<historyRowModel> model = (ArrayList<historyRowModel>) dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_GET_HISTORY ,null);
        m_list_model.setList(model);
        historyAdapter adapter = new historyAdapter(m_list_model.getList(),new adapterCallback(), this);
        adapter.invokeFilter(false);
        m_listview.setAdapter(adapter);
        m_listview.setLayoutManager(new LinearLayoutManager(this));
        m_history_view_controller.onTrigger(historyEnums.eHistoryViewCommands.M_UPDATE_LIST_IF_EMPTY, Arrays.asList(m_list_model.getList().size(),0));
    }

    /*View Handlers*/

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

    boolean m_is_recyclerview_loading = false;
    public void onEditorInvoked(){

        m_listview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if(m_search.getVisibility()==View.GONE){
                        LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                        int totalItemCount = layoutManager.getItemCount();
                        int lastVisible = layoutManager.findLastVisibleItemPosition();

                        boolean endHasBeenReached = lastVisible + 2 >= totalItemCount;
                        if (totalItemCount > 0 && endHasBeenReached && !m_is_recyclerview_loading) {
                            loadMore();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });

        m_clearButton.requestFocusFromTouch();
        m_clearButton.setOnClickListener(v -> pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHistoryController(), Collections.singletonList(strings.EMPTY_STR), enums.etype.clear_history));

        m_search.setOnEditorActionListener((v, actionId, event) ->{
            if (actionId == EditorInfo.IME_ACTION_NEXT)
            {
                helperMethod.hideKeyboard(this);
                return true;
            }
            return false;
        });

        m_search.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                m_search.clearFocus();
            }else {
                ((historyAdapter) Objects.requireNonNull(m_listview.getAdapter())).setFilter(m_search.getText().toString());
                ((historyAdapter) m_listview.getAdapter()).invokeFilter(true);
            }
        });

        m_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                ((historyAdapter) Objects.requireNonNull(m_listview.getAdapter())).setFilter(m_search.getText().toString());
                ((historyAdapter) m_listview.getAdapter()).invokeFilter(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(m_search.getVisibility() == View.VISIBLE){
            ((historyAdapter)m_listview.getAdapter()).onUpdateSearchStatus(m_history_view_controller.onHideSearch());
        }
        else if(m_menu_button.getVisibility() == View.VISIBLE){
            onClearMultipleSelection(null);
        }
        else {
            this.finish();
        }
    }

    public void onBackPressed(View view){
        if(m_search.getVisibility() == View.VISIBLE){
            ((historyAdapter)m_listview.getAdapter()).onUpdateSearchStatus(m_history_view_controller.onHideSearch());
        }
        else if(m_menu_button.getVisibility() == View.VISIBLE){
            onClearMultipleSelection(null);
        }
        else {
            this.finish();
        }
    }

    public void onclearDataTrigger(View view){
    }

    public void onclearData(){
        m_list_model.clearList();
        ((historyAdapter) Objects.requireNonNull(m_listview.getAdapter())).invokeFilter(true );
        m_history_view_controller.onTrigger(historyEnums.eHistoryViewCommands.M_CLEAR_LIST, null);
        databaseController.getInstance().execSQL(sql.H_CLEAR_DATA,null);
        finish();
    }

    boolean isLoadingMore = false;
    boolean is_page_loading = true;
    private boolean loadMore() {
        if(!isLoadingMore){
            if(is_page_loading){
                is_page_loading = false;
                return false;
            }
            isLoadingMore = true;
            new Thread(){
                public void run(){
                    if(!m_is_recyclerview_loading){
                        int m_size = m_list_model.getList().size();
                        m_is_recyclerview_loading = true;
                        ((historyAdapter) Objects.requireNonNull(m_listview.getAdapter())).onLoading();
                        boolean m_history = (boolean)dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_LOAD_MORE_HISTORY ,null);
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        activityContextManager.getInstance().getHistoryController().runOnUiThread(() -> new Thread(){
                            public void run(){
                                ((historyAdapter) Objects.requireNonNull(m_listview.getAdapter())).onLoadingClear();
                                isLoadingMore = false;
                                new Thread(){
                                    public void run(){
                                        try {
                                            sleep(200);
                                            m_is_recyclerview_loading = false;
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

                            }
                        }.start());
                    }
                }
            }.start();
        }
        return true;
    }

    @Override
    public void onTrimMemory(int level)
    {
        if(status.sIsAppPaused && (level==80 || level==15))
        {
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.LOW_MEMORY,true));
            finish();
        }
    }

    @Override
    public void onResume()
    {
        activityContextManager.getInstance().setCurrentActivity(this);
        status.sIsAppPaused = false;
        super.onResume();
    }

    @Override
    public void onPause()
    {
        status.sIsAppPaused = true;
        super.onPause();
    }

    public void onHideSearch(View view) {
        ((historyAdapter)m_listview.getAdapter()).onUpdateSearchStatus(m_history_view_controller.onHideSearch());
    }

    public void onLongPressMenu(View view) {
        m_history_view_controller.onLongPressMenu(view);
    }

    public void onOpenMultipleTabs(View view) {
        ArrayList<String> m_long_selected_urk = ((historyAdapter)m_listview.getAdapter()).getLongSelectedleURL();
        for(int m_counter=0;m_counter<m_long_selected_urk.size();m_counter++){
            m_home_controller.onOpenLinkNewTab(m_long_selected_urk.get(m_counter));
        }
        onBackPressed(null);
        ((historyAdapter)m_listview.getAdapter()).clearLongSelectedURL();
        m_history_view_controller.onCloseMenu();
    }

    public void onShareSelectedURL(View view) {
        String m_joined_url = ((historyAdapter)m_listview.getAdapter()).getSelectedURL();
        helperMethod.shareURL(this, m_joined_url);
        ((historyAdapter)m_listview.getAdapter()).clearLongSelectedURL();
        m_history_view_controller.onCloseMenu();
    }

    public void onClearMultipleSelection(View view) {
        ((historyAdapter)m_listview.getAdapter()).clearLongSelectedURL();
        m_history_view_controller.onCloseMenu();
    }

    public void onDeleteSelected(View view) {
        m_history_view_controller.onCloseMenu();
        ((historyAdapter)m_listview.getAdapter()).onDeleteSelected();
    }

    /*Event Observer*/

    public class adapterCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            if(e_type.equals(enums.etype.url_triggered)){
                String url_temp = helperMethod.completeURL(data.get(0).toString());
                pluginController.getInstance().logEvent(strings.HISTORY_TRIGGERED);
                m_home_controller.onLoadURL(url_temp);
                finish();
            }
            else if(e_type.equals(enums.etype.url_triggered_new_tab)){
                String url_temp = helperMethod.completeURL(data.get(0).toString());
                pluginController.getInstance().logEvent(strings.HISTORY_TRIGGERED);
                m_home_controller.onOpenLinkNewTab(url_temp);
                finish();
            }
            else if(e_type.equals(enums.etype.url_clear)){
                m_list_model.onManualClear((int)data.get(0));
            }
            else if(e_type.equals(enums.etype.url_clear_at)){
                dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_REMOVE_HISTORY ,data);
            }
            else if(e_type.equals(enums.etype.is_empty)){
                m_history_view_controller.onTrigger(historyEnums.eHistoryViewCommands.M_UPDATE_LIST_IF_EMPTY, Arrays.asList(m_list_model.getList().size(),300));
            }
            else if(e_type.equals(enums.etype.remove_from_database)){
                databaseController.getInstance().deleteFromList((int)data.get(0),strings.H_HISTORY_TITLE);
            }
            else if(e_type.equals(enums.etype.on_verify_selected_url_menu)){
                m_history_view_controller.onTrigger(M_VERTIFY_SELECTION_MENU, data);
            }
            return null;
        }
   }
}
