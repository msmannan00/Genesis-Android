package com.darkweb.genesissearchengine.appManager.list_manager;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.darkweb.genesissearchengine.appManager.home_activity.app_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.orbot_manager;
import com.example.myapplication.R;

public class list_ehandler
{
    /*Initializations*/

    private static final list_ehandler ourInstance = new list_ehandler();
    public static list_ehandler getInstance()
    {
        return ourInstance;
    }
    private list_ehandler(){
    }

    /*Listener Initializations*/

    void onEditorActionListener(EditText view)
    {
        view.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId != EditorInfo.IME_ACTION_NEXT)
            {
                return false;
            }
            else
            {
                list_model.getInstance().getListInstance().onClearSearchBarCursor();
                InputMethodManager imm = (InputMethodManager) list_model.getInstance().getListInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return list_model.getInstance().getListInstance().updatelist();
            }
        });
    }

    void onClickListenersInitialize(View view,int index)
    {
        view.setOnClickListener(v ->
        {
            if(v.getId() == R.id.message_button)
            {
                onManualClear(index);
            }
        });
    }

    /*Listeners*/

    private void onManualClear(int index)
    {
        list_model.getInstance().deleteFromDatabase(list_model.getInstance().getModel().get(index).getId());
        list_model.getInstance().getModel().remove(index);
        list_model.getInstance().removeFromMainList(list_model.getInstance().getIndex().get(index));
        list_model.getInstance().removeIndex(index);
        list_model.getInstance().getListInstance().onDatasetChanged(index);
        list_model.getInstance().getListInstance().updateListStatus(true);
    }

    void onBackPressed()
    {
        list_model.getInstance().getListInstance().closeView();
    }

    void onUrlClick(String url)
    {
        String url_temp = helperMethod.completeURL(url);

        app_model.getInstance().addNavigation(url, enums.navigationType.onion);

        if(!url_temp.contains("boogle") && !url_temp.equals(constants.backendGoogle) && !url_temp.equals(constants.backendBing))
        {
            if(orbot_manager.getInstance().initOrbot(url_temp))
            {
                app_model.getInstance().getAppInstance().onloadURL(url_temp,true,false);
            }
        }
        else
        {
            app_model.getInstance().getAppInstance().onloadURL(url_temp,false,false);
        }
        list_model.getInstance().getListInstance().closeView();

    }

}