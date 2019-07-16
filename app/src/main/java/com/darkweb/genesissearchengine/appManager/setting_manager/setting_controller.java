package com.darkweb.genesissearchengine.appManager.setting_manager;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

public class setting_controller extends AppCompatActivity
{

    /*Private Variables*/

    private setting_view_controller viewController;
    private Spinner search;
    private Spinner javascript;
    private Spinner history;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);

        setting_model.getInstance().init_status();
        viewsInitializations();
        listenersInitializations();
        initializeModel();
    }

    public void viewsInitializations()
    {
        search = findViewById(R.id.search_manager);
        javascript = findViewById(R.id.javascript_manager);
        history = findViewById(R.id.history_manager);

        viewController = new setting_view_controller(search,javascript,history);
    }

    public void listenersInitializations()
    {
        initializeItemSelectedListener(search);
        initializeItemSelectedListener(javascript);
        initializeItemSelectedListener(history);
    }

    public void initializeModel()
    {
        setting_model.getInstance().setSettingInstance(this);
    }

    /*Event Handlers*/

    public void initializeItemSelectedListener(Spinner view)
    {
        settings_ehandler.getInstance().onItemListnerInitialization(view);
    }

    public void onBackPressed(View view)
    {
        settings_ehandler.getInstance().onBackPressed();
    }

    /*View Handlers*/

    @Override
    public void onBackPressed(){
        closeView();
    }

    public void closeView()
    {
        viewController.closeView();
    }

}