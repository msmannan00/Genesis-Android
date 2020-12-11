package com.darkweb.genesissearchengine.appManager.helpManager;


import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("FieldCanBeLocal")
public class helpController extends AppCompatActivity {

    /*Initializations*/
    private helpViewController mHelpViewController;
    private helpModel mHelpModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view);

        initializeAppModel();
        initializeConnections();
    }

    public void initializeAppModel()
    {
        mHelpViewController = new helpViewController();
        mHelpModel = new helpModel();
    }

    public void initializeConnections()
    {
        mHelpViewController.initialization(new helpViewCallback(),this);
    }

    public class helpViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /*UI Redirection*/

    public void onClose(View view) {
        finish();
    }

}