package com.darkweb.genesissearchengine.appManager.list_manager;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.database_manager.database_controller;
import com.darkweb.genesissearchengine.appManager.home_activity.home_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.pluginManager.message_manager;

import java.util.ArrayList;
import java.util.Objects;

class list_view_controller
{
    /*Private Variables*/

    private ImageView emptyListNotifier;
    private EditText searchBar;
    private RecyclerView listView;

    /*Initializations*/

    list_view_controller(ImageView emptyListNotifier, EditText searchBar,RecyclerView listView)
    {
        this.emptyListNotifier = emptyListNotifier;
        this.searchBar = searchBar;
        this.listView = listView;
    }

    /*Handlers*/

    void onClearHistory()
    {
        if(home_model.getInstance().getHistory().size()>0)
        {
            message_manager.getInstance().clearData();
        }
    }

    void onClearAll()
    {
        list_model.getInstance().getModel().clear();
        list_model.getInstance().getMainList().clear();
        home_model.getInstance().getSuggestions().clear();

        emptyListNotifier.setAlpha(0f);
        emptyListNotifier.setVisibility(View.VISIBLE);
        emptyListNotifier.animate().alpha(1);
        searchBar.setText(strings.emptyStr);

        String table = strings.bookmark_text;
        if(list_model.getInstance().getType()== constants.list_history)
        {
            table = strings.history_text;
        }
        database_controller.getInstance().execSQL("delete from "+table+" where 1");
        list_model.getInstance().getListInstance().initializeList();
    }

    void updateListStatus(boolean animate)
    {
        if(list_model.getInstance().getModel().size()<=0)
        {
            if(animate)
            {
                emptyListNotifier.setAlpha(0f);
                emptyListNotifier.setVisibility(View.VISIBLE);
                emptyListNotifier.animate().alpha(1);
            }
            else
            {
                emptyListNotifier.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            emptyListNotifier.setVisibility(View.GONE);
        }

    }

    void updateList()
    {
        String query = searchBar.getText().toString();
        ArrayList<list_row_model> temp_model = new ArrayList<>();
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
        listView.getAdapter().notifyDataSetChanged();
    }

    void onDatasetChanged(int index)
    {
        Objects.requireNonNull(listView.getAdapter()).notifyItemRemoved(index);
        listView.getAdapter().notifyDataSetChanged();
    }


    void closeView()
    {
        list_model.getInstance().getListInstance().finish();
    }

    public void onClearSearchBarCursor()
    {
        searchBar.clearFocus();
    }

    public String removeHttpRequest(String url)
    {

        if (url.startsWith("https://"))
        {
            return url.substring(8);
        }
        else if (url.startsWith("http://"))
        {
            return url.substring(7);
        }
        else
        {
            return url;
        }
        /*
        if (view == null)
        {
            return;
        }

        if (view.getText().toString().contains("https://"))
        {
            SpannableString ss = new SpannableString(view.getText());
            ss.setSpan(new ForegroundColorSpan(Color.argb(255, 0, 123, 43)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(ss);
        } else if (view.getText().toString().contains("http://"))
        {
            SpannableString ss = new SpannableString(view.getText());
            ss.setSpan(new ForegroundColorSpan(Color.argb(255, 0, 128, 43)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 4, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(ss);
        } else
        {
            SpannableString ss = new SpannableString(view.getText());
            ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, view.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(ss);
        }*/
    }


}
