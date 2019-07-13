package com.darkweb.genesissearchengine.appManager.list_activity;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.data_helper.database_controller;
import com.darkweb.genesissearchengine.appManager.main_activity.app_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.pluginManager.message_manager;
import com.example.myapplication.R;
import java.util.ArrayList;

public class list_controller extends AppCompatActivity
{
    private ImageView empty_list;
    private EditText search_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list_manager);

        initializeListInstance();
        initializeListModel();
        initializeListController();
        initSearchBar();
        updatelist();
    }

    public void initializeListInstance()
    {
        app_model.getInstance().setListInstance(this);
        app_model.getInstance().setListContext(this);
    }


    public void initializeListModel()
    {
        Bundle bundle = getIntent().getExtras();
        if(bundle.getInt(keys.list_type)==constants.list_bookmark)
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

    public void initializeListController()
    {
        empty_list = findViewById(R.id.empty_list);
        search_bar = findViewById(R.id.search);
        RecyclerView list_view = findViewById(R.id.listview);

        list_adapter adapter = new list_adapter();
        adapter.initialize(empty_list,this);
        list_view.setAdapter(adapter);
        list_view.setLayoutManager(new LinearLayoutManager(this));
        isListEmpty();
    }

    public void clearHistory(View view)
    {
        if(app_model.getInstance().getHistory().size()>0)
        {
            message_manager.getInstance().clearData();
        }
    }

    public void clearAll()
    {
        list_model.getInstance().getModel().clear();
        list_model.getInstance().getMainList().clear();
        initializeListController();

        empty_list.setAlpha(0f);
        empty_list.setVisibility(View.VISIBLE);
        empty_list.animate().alpha(1);
        search_bar.setText("");

        String table="bookmark";
        if(list_model.getInstance().getType()==constants.list_history)
        {
            table = "history";
        }
        database_controller.getInstance().execSQL("delete from "+table+" where 1");

    }

    public void isListEmpty()
    {
        if(list_model.getInstance().getModel().size()<=0)
        {
            empty_list.setVisibility(View.VISIBLE);
        }
        else
        {
            empty_list.setVisibility(View.GONE);
        }

    }

    public void initSearchBar()
    {
        search_bar.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId != EditorInfo.IME_ACTION_NEXT)
            {
                return false;
            }
            else
            {
                return updatelist();
            }
        });
    }

    public boolean updatelist()
    {
        String query = search_bar.getText().toString();
        ArrayList<list_row_model> temp_model = new ArrayList<list_row_model>();
        list_model.getInstance().getIndex().clear();
        for(int counter=0;counter<=list_model.getInstance().getMainList().size()-1;counter++)
        {
            if(list_model.getInstance().getMainList().get(counter).getHeader().contains(query) || list_model.getInstance().getMainList().get(counter).getDescription().contains(query))
            {
                temp_model.add(list_model.getInstance().getMainList().get(counter));
                list_model.getInstance().setIndex(counter);
            }
        }
        list_model.getInstance().setModel(temp_model);
        initializeListController();

        return true;
    }

    public void onBackPressed(View view)
    {
        finish();
    }

}
