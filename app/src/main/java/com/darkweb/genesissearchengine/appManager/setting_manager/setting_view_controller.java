package com.darkweb.genesissearchengine.appManager.setting_manager;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.preference_manager;

import static com.darkweb.genesissearchengine.constants.status.history_status;
import static com.darkweb.genesissearchengine.constants.status.java_status;

class setting_view_controller
{
    /*Private Variables*/

    private Spinner search;
    private Spinner javascript;
    private Spinner history;

    /*Initializations*/

    setting_view_controller(Spinner search, Spinner javascript, Spinner history)
    {
        this.search = search;
        this.search = javascript;
        this.search = history;

        initJavascript();
        initHistory();
        initSearchEngine();
    }

    private void initJavascript()
    {
        if (java_status)
        {
            javascript.setSelection(0);
        }
        else
        {
            javascript.setSelection(1);
        }
    }

    private void initHistory()
    {
        if (history_status)
        {
            history.setSelection(0);
        }
        else
        {
            history.setSelection(1);
        }
    }

    @SuppressWarnings("unchecked")
    private void initSearchEngine()
    {
        String myString = preference_manager.getInstance().getString(keys.search_engine, strings.darkweb);
        ArrayAdapter myAdap = (ArrayAdapter) search.getAdapter();
        int spinnerPosition = myAdap.getPosition(myString);
        search.setSelection(spinnerPosition);
    }

    /*Helper Methods*/

    void closeView()
    {
        setting_model.getInstance().getSettingInstance().finish();
    }

}
