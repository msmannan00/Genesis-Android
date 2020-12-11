package com.darkweb.genesissearchengine.appManager.settingManager.clearManager;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.helpManager.helpController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.sql;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class settingClearController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private settingClearModel mSettingClearModel;
    private settingClearViewController mSettingClearViewController;
    private ArrayList<CheckBox> mCheckBoxList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_clear_view);

        pluginController.getInstance().onCreate(this);
        viewsInitializations();
        initializeListeners();
    }

    public void viewsInitializations() {
        mCheckBoxList.add(findViewById(R.id.pClearChecked_1));
        mCheckBoxList.add(findViewById(R.id.pClearChecked_2));
        mCheckBoxList.add(findViewById(R.id.pClearChecked_3));
        mCheckBoxList.add(findViewById(R.id.pClearChecked_4));
        mCheckBoxList.add(findViewById(R.id.pClearChecked_5));
        mCheckBoxList.add(findViewById(R.id.pClearChecked_6));
        mCheckBoxList.add(findViewById(R.id.pClearChecked_7));
        mCheckBoxList.add(findViewById(R.id.pClearChecked_8));

        mSettingClearViewController = new settingClearViewController(this, new settingClearController.settingClearViewCallback(), mCheckBoxList);
        mSettingClearModel = new settingClearModel(new settingClearController.settingClearModelCallback());
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    /* LISTENERS */
    public class settingClearViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }


    public class settingClearModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    public void initializeListeners(){
    }

    public void onCheckBoxTriggered(View view){
        mSettingClearViewController.onTrigger(settingClearEnums.eClearViewController.M_CHECK_INVOKE, Arrays.asList(view.getTag(),!mCheckBoxList.get(Integer.parseInt(view.getTag().toString())).isChecked()));
    }

    public void onClearData(View view){
        try{
            if(mCheckBoxList.get(0).isChecked()){
                mCheckBoxList.get(0).setChecked(false);
                mCheckBoxList.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
                activityContextManager.getInstance().getHomeController().onClearSession();
                dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
                activityContextManager.getInstance().getHomeController().initTab(false);
            }
            if(mCheckBoxList.get(1).isChecked()){
                mCheckBoxList.get(1).setChecked(false);
                mCheckBoxList.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
                databaseController.getInstance().execSQL(sql.SQL_CLEAR_HISTORY,null);
                dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_CLEAR_HISTORY ,null);
            }
            if(mCheckBoxList.get(2).isChecked()){
                mCheckBoxList.get(2).setChecked(false);
                mCheckBoxList.get(2).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
                databaseController.getInstance().execSQL(sql.SQL_CLEAR_BOOKMARK,null);
                dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_CLEAR_BOOKMARK ,null);
            }
            if(mCheckBoxList.get(3).isChecked()){
                mCheckBoxList.get(3).setChecked(false);
                mCheckBoxList.get(3).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
                activityContextManager.getInstance().getHomeController().onClearCache();
            }
            if(mCheckBoxList.get(4).isChecked()){
                mCheckBoxList.get(4).setChecked(false);
                mCheckBoxList.get(4).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
                dataController.getInstance().invokeSuggestion(dataEnums.eSuggestionCommands.M_CLEAR_SUGGESTION ,null);
            }
            if(mCheckBoxList.get(5).isChecked()){
                mCheckBoxList.get(5).setChecked(false);
                mCheckBoxList.get(5).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
                activityContextManager.getInstance().getHomeController().onClearSiteData();
            }
            if(mCheckBoxList.get(6).isChecked()){
                mCheckBoxList.get(6).setChecked(false);
                mCheckBoxList.get(6).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
                activityContextManager.getInstance().getHomeController().onClearSession();
            }
            if(mCheckBoxList.get(7).isChecked()){
                mCheckBoxList.get(7).setChecked(false);
                mCheckBoxList.get(7).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
                activityContextManager.getInstance().getHomeController().onClearCookies();
            }

            activityContextManager.getInstance().getHomeController().initRuntimeSettings();
            pluginController.getInstance().MessageManagerHandler(this, null, enums.eMessageEnums.M_DATA_CLEARED);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume()
    {
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /*UI Redirection*/
    public void onClose(View view){
        finish();
    }

}
