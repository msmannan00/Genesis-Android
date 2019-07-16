package com.darkweb.genesissearchengine.appManager.list_manager;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.home_activity.app_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.example.myapplication.R;
import java.util.Objects;

public class list_controller extends AppCompatActivity
{
    /*Private Variables*/

    private ImageView emptyListNotifier;
    private EditText searchBar;
    private RecyclerView listView;

    private list_view_controller viewController;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        initializeListInstance();
        initializeListModel();
        initializeViews();
        initializeList();
        initSearchBar();
        updatelist();
    }
    public void initializeListInstance(){
        list_model.getInstance().setListInstance(this);
        list_model.getInstance().setListContext(this);
    }
    public void initializeListModel(){
        Bundle bundle = getIntent().getExtras();
        if(Objects.requireNonNull(bundle).getInt(keys.list_type)==constants.list_bookmark)
        {
            list_model.getInstance().setType(constants.list_bookmark);
            list_model.getInstance().setMainList(app_model.getInstance().getBookmark());
        }
        else
        {
            list_model.getInstance().setType(constants.list_history);
            list_model.getInstance().setMainList(app_model.getInstance().getHistory());
        }
    }
    public void initializeViews(){
        emptyListNotifier = findViewById(R.id.empty_list);
        searchBar = findViewById(R.id.search);
        listView = findViewById(R.id.listview);
        viewController = new list_view_controller(emptyListNotifier,searchBar,listView);
    }
    public void initializeList(){
        list_adapter adapter = new list_adapter();
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
        updateListStatus(false);
    }

    /*View Handlers*/

    public void onClearHistory(View view)
    {
        viewController.onClearHistory();
    }
    public void onClearAll(){
        viewController.onClearAll();
        initializeViews();
    }
    public void updateListStatus(boolean animate)
    {
        viewController.updateListStatus(animate);
    }
    public void initSearchBar()
    {
        list_ehandler.getInstance().onEditorActionListener(searchBar);
    }
    public boolean updatelist(){
        viewController.updateList();
        updateListStatus(false);
        return true;
    }
    public void closeView()
    {
        viewController.closeView();
    }
    public void onDatasetChanged(int index)
    {
        viewController.onDatasetChanged(index);
    }
    public void onClearSearchBarCursor() {
        viewController.onClearSearchBarCursor();
    }
    public String removeHttpRequest(String view){
        return viewController.removeHttpRequest(view);
    }

    /*Event Handlers*/

    public void onBackPressed(View view)
    {
        list_ehandler.getInstance().onBackPressed();
    }
    public void onUrlClick(String url)
    {
        list_ehandler.getInstance().onUrlClick(url);
    }
    public void onClickListenersInitialize(View view,int index) {
        list_ehandler.getInstance().onClickListenersInitialize(view,index);
    }

}
