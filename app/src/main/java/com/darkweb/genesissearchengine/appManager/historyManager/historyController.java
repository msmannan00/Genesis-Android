package com.darkweb.genesissearchengine.appManager.historyManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class historyController extends AppCompatActivity
{
    /*Private Variables*/

    private historyModel mListModel;
    private homeController mHomeController;
    private activityContextManager mContextManager;

    private ImageView mEmptyListNotifier;
    private EditText mSearchBar;
    private RecyclerView mListView;
    private Button mClearButton;
    private ImageButton mMoreButton;

    private historyViewController mHistoryViewController;

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
        mListModel = new historyModel();
        mContextManager = activityContextManager.getInstance();
        mHomeController = activityContextManager.getInstance().getHomeController();
        mContextManager.setHistoryController(this);
        activityContextManager.getInstance().setHistoryController(this);
        pluginController.getInstance().logEvent(strings.HISTORY_OPENED);
    }
    public void initializeViews(){
        mEmptyListNotifier = findViewById(R.id.empty_list);
        mSearchBar = findViewById(R.id.search);
        mListView = findViewById(R.id.listview);
        mClearButton = findViewById(R.id.clearButton);
        mMoreButton = findViewById(R.id.load_more);
        mHistoryViewController = new historyViewController(mEmptyListNotifier, mSearchBar, mListView, mClearButton, mMoreButton,this);
    }
    public void initializeList(){
        ArrayList<historyRowModel> model = dataController.getInstance().getHistory();
        mListModel.setList(model);
        historyAdapter adapter = new historyAdapter(mListModel.getList(),new adapterCallback());
        adapter.invokeFilter(false);
        mListView.setAdapter(adapter);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mHistoryViewController.updateIfListEmpty(mListModel.getList().size(),0);
    }

    /*View Handlers*/

    public void onEditorInvoked(){

        mSearchBar.setOnEditorActionListener((v, actionId, event) ->{
            if (actionId == EditorInfo.IME_ACTION_NEXT)
            {
                helperMethod.hideKeyboard(this);
                return true;
            }
            return false;
        });

        mSearchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                ((historyAdapter) Objects.requireNonNull(mListView.getAdapter())).setFilter(mSearchBar.getText().toString());
                ((historyAdapter) mListView.getAdapter()).invokeFilter(true);
            }
        });
    }

    public void onBackPressed(View view){
        this.finish();
    }
    public void onclearDataTrigger(View view){
        pluginController.getInstance().MessageManagerHandler(this, Collections.singletonList(strings.EMPTY_STR),enums.etype.clear_history);
    }
    public void onclearData(){
        mListModel.clearList();
        ((historyAdapter) Objects.requireNonNull(mListView.getAdapter())).invokeFilter(true );
        mHistoryViewController.clearList();
        databaseController.getInstance().execSQL("delete from history where 1",null);
        finish();
    }

    public void onLoadMoreHostory(View view)
    {
        dataController.getInstance().loadMoreHistory();
    }

    public void updateHistory(){
        initializeList();
        mHistoryViewController.updateList();
    }

    @Override
    public void onTrimMemory(int level)
    {
        if(status.sIsAppPaused && (level==80 || level==15))
        {
            dataController.getInstance().setBool(keys.LOW_MEMORY,true);
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

    /*Event Observer*/

    public class adapterCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> data, enums.etype e_type)
        {
            if(e_type.equals(enums.etype.url_triggered)){
                String url_temp = helperMethod.completeURL(data.get(0).toString());
                pluginController.getInstance().logEvent(strings.HISTORY_TRIGGERED);
                mHomeController.onLoadURL(url_temp);
                finish();
            }
            else if(e_type.equals(enums.etype.url_clear)){
                mListModel.onManualClear((int)data.get(0));
            }
            else if(e_type.equals(enums.etype.url_clear_at)){
                dataController.getInstance().removeHistory(data.toString());
            }
            else if(e_type.equals(enums.etype.is_empty)){
                mHistoryViewController.removeFromList((int)data.get(0));
                mHistoryViewController.updateIfListEmpty(mListModel.getList().size(),300);
            }
            else if(e_type.equals(enums.etype.remove_from_database)){
                databaseController.getInstance().deleteFromList((int)data.get(0),"history");
            }
        }

    }

}
