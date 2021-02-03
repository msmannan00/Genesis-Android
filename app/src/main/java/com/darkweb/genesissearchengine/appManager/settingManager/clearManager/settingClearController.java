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
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.sql;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManager.M_DATA_CLEARED;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

public class settingClearController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private settingClearModel mSettingClearModel;
    private settingClearViewController mSettingClearViewController;
    private ArrayList<CheckBox> mCheckBoxList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_clear_view);

        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);

        viewsInitializations();
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

        activityContextManager.getInstance().onStack(this);
        mSettingClearViewController = new settingClearViewController(this, new settingClearController.settingClearViewCallback(), mCheckBoxList);
        mSettingClearModel = new settingClearModel(new settingClearController.settingClearModelCallback());
    }

    /*View Callbacks*/

    private class settingClearViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /*Model Callbacks*/

    private class settingClearModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    public void onClearData(View view){
        boolean mHomeInvoked = false;
        if(mCheckBoxList.get(0).isChecked()){
            mCheckBoxList.get(0).setChecked(false);
            mCheckBoxList.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
            mHomeInvoked = true;
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
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
            mCheckBoxList.get(4).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            activityContextManager.getInstance().getHomeController().onClearSiteData();
            mHomeInvoked = true;
        }
        if(mCheckBoxList.get(5).isChecked()){
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
            mCheckBoxList.get(5).setChecked(false);
            mCheckBoxList.get(5).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            activityContextManager.getInstance().getHomeController().onClearSession();
            mHomeInvoked = true;
        }
        if(mCheckBoxList.get(6).isChecked()){
            mCheckBoxList.get(6).setChecked(false);
            mCheckBoxList.get(6).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            activityContextManager.getInstance().getHomeController().onClearCookies();
        }
        if(mCheckBoxList.get(7).isChecked()){
            mCheckBoxList.get(7).setChecked(false);
            mCheckBoxList.get(7).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            onClearSettings();
            status.initStatus();
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
            mHomeInvoked = true;
        }

        activityContextManager.getInstance().getHomeController().initRuntimeSettings();
        pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_DATA_CLEARED);

        if(mHomeInvoked){
            activityContextManager.getInstance().getHomeController().onReDrawGeckoview();
            activityContextManager.getInstance().getHomeController().onHomeButton(null);
        }
    }

    private void onClearSettings(){
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_HISTORY,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_SUGGESTION,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_JAVA_SCRIPT,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_HISTORY_CLEAR,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.VPN_ENABLED,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_ENABLES,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FONT_ADJUSTABLE,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_ZOOM,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_VOICE_INPUT,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_TRACKING_PROTECTION,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_DONOT_TRACK,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_COOKIE_ADJUSTABLE,ACCEPT_FIRST_PARTY));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_FLOAT, Arrays.asList(keys.SETTING_FONT_SIZE,100));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE, strings.SETTING_DEFAULT_LANGUAGE));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE_REGION,strings.SETTING_DEFAULT_LANGUAGE_REGION));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE,constants.CONST_BACKEND_GENESIS_URL));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1,strings.BRIDGE_CUSTOM_BRIDGE_OBFS4));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS,0));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_RESTORE_TAB,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_CHARACTER_ENCODING,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_SHOW_IMAGES,0));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SHOW_FONTS,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_TOOLBAR_THEME,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FULL_SCREEN_BROWSIING,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_THEME, enums.Theme.THEME_DEFAULT));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_OPEN_URL_IN_NEW_TAB,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_LIST_VIEW,true));
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume()
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
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
        onClose(null);
    }

    /*UI Redirection*/

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onCheckBoxTriggered(View view){
        if(view!=null && view.getTag()!=null){
            mSettingClearViewController.onTrigger(settingClearEnums.eClearViewController.M_CHECK_INVOKE, Arrays.asList(view.getTag(),!mCheckBoxList.get(Integer.parseInt(view.getTag().toString())).isChecked()));
        }
    }

    public void onClose(View view){
        activityContextManager.getInstance().onRemoveStack(this);
        finish();
    }

}
