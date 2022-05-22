package com.hiddenservices.onionservices.appManager.settingManager.clearManager;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.helpManager.helpController;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.dataManager.dataController;
import com.hiddenservices.onionservices.dataManager.dataEnums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.appManager.activityThemeManager;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import com.example.myapplication.R;

import org.mozilla.geckoview.ContentBlocking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hiddenservices.onionservices.constants.sql.SQL_CLEAR_BOOKMARK;
import static com.hiddenservices.onionservices.constants.sql.SQL_CLEAR_HISTORY;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_DATA_CLEARED;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

public class settingClearController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private settingClearModel mSettingClearModel;
    private settingClearViewController mSettingClearViewController;
    private ArrayList<CheckBox> mCheckBoxList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_clear_view);

        viewsInitializations();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onConfigurationChanged(newConfig);
        if (newConfig.uiMode != getResources().getConfiguration().uiMode) {
            activityContextManager.getInstance().onResetTheme();
            activityThemeManager.getInstance().onConfigurationChanged(this);
        }

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

    private class settingClearViewCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            return null;
        }
    }

    /*Model Callbacks*/

    private class settingClearModelCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            return null;
        }
    }

    public void onClearData(View view) {
        boolean mHomeInvoked = false;
        boolean mClearInvoked = false;

        if (mCheckBoxList.get(0).isChecked()) {
            mCheckBoxList.get(0).setChecked(false);
            mCheckBoxList.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
            mHomeInvoked = true;
            mClearInvoked = true;
        }
        if (mCheckBoxList.get(1).isChecked()) {
            mCheckBoxList.get(1).setChecked(false);
            mCheckBoxList.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            dataController.getInstance().invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL, Arrays.asList(SQL_CLEAR_HISTORY, null));
            dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_CLEAR_HISTORY, null);
            mClearInvoked = true;
        }
        if (mCheckBoxList.get(2).isChecked()) {
            mCheckBoxList.get(2).setChecked(false);
            mCheckBoxList.get(2).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            dataController.getInstance().invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL, Arrays.asList(SQL_CLEAR_BOOKMARK, null));
            dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_CLEAR_BOOKMARK, null);
            mClearInvoked = true;
        }
        if (mCheckBoxList.get(3).isChecked()) {
            mCheckBoxList.get(3).setChecked(false);
            mCheckBoxList.get(3).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            activityContextManager.getInstance().getHomeController().onClearCache();
            mClearInvoked = true;
        }
        if (mCheckBoxList.get(4).isChecked()) {
            mCheckBoxList.get(4).setChecked(false);
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
            mCheckBoxList.get(4).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            activityContextManager.getInstance().getHomeController().onClearSiteData();
            mHomeInvoked = true;
            mClearInvoked = true;
        }
        if (mCheckBoxList.get(5).isChecked()) {
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
            mCheckBoxList.get(5).setChecked(false);
            mCheckBoxList.get(5).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            activityContextManager.getInstance().getHomeController().onClearSession();
            mHomeInvoked = true;
            mClearInvoked = true;
        }
        if (mCheckBoxList.get(6).isChecked()) {
            mCheckBoxList.get(6).setChecked(false);
            mCheckBoxList.get(6).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            activityContextManager.getInstance().getHomeController().onClearCookies();
            mClearInvoked = true;
        }
        if (mCheckBoxList.get(7).isChecked()) {
            mCheckBoxList.get(7).setChecked(false);
            mCheckBoxList.get(7).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_checkbox_tint_default)));
            onClearSettings();
            status.initStatus(activityContextManager.getInstance().getHomeController());
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
            mHomeInvoked = true;
            mClearInvoked = true;
        }

        if (mClearInvoked) {
            activityContextManager.getInstance().getHomeController().initRuntimeSettings();
            pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_DATA_CLEARED);

            if (mHomeInvoked) {
                activityContextManager.getInstance().getHomeController().onClearSettings();
            }
        }

    }

    private void onClearSettings() {

        boolean mIsThemeChangable = false;
        if (!status.sDefaultNightMode) {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                mIsThemeChangable = true;
            }
        } else {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                mIsThemeChangable = true;
            }
        }

        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_HISTORY, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_SUGGESTION, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_JAVA_SCRIPT, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_HISTORY_CLEAR, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL, false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED, false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_VPN_ENABLED, false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_ENABLES, false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FONT_ADJUSTABLE, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_ZOOM, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_VOICE_INPUT, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_TRACKING_PROTECTION, ContentBlocking.AntiTracking.DEFAULT));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_DONOT_TRACK, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_COOKIE_ADJUSTABLE, ACCEPT_FIRST_PARTY));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_FLOAT, Arrays.asList(keys.SETTING_FONT_SIZE, 100));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE, strings.SETTING_DEFAULT_LANGUAGE));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE_REGION, strings.SETTING_DEFAULT_LANGUAGE_REGION));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE, constants.CONST_BACKEND_GENESIS_URL));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1, strings.BRIDGE_CUSTOM_BRIDGE_OBFS4));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS, 1));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_RESTORE_TAB, false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_CHARACTER_ENCODING, false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_SHOW_IMAGES, 0));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SHOW_FONTS, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_TOOLBAR_THEME, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FULL_SCREEN_BROWSIING, false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_THEME, enums.Theme.THEME_DEFAULT));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_LIST_VIEW, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SHOW_TAB_GRID, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_OPEN_URL_IN_NEW_TAB, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_POPUP, true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_TYPE, strings.BRIDGE_CUSTOM_BRIDGE_OBFS4));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_INSTALLED, false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_ENABLES, false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL, false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_TOR_BROWSING, true));

        if (mIsThemeChangable) {
            status.mThemeApplying = true;
            onBackPressed();
            overridePendingTransition(R.anim.fade_in_lang, R.anim.fade_out_lang);
            activityContextManager.getInstance().getHomeController().onResetData();
            activityContextManager.getInstance().getHomeController().onReInitTheme();
            activityContextManager.getInstance().getSettingController().onReInitTheme();
            helperMethod.openActivity(settingClearController.class, constants.CONST_LIST_HISTORY, settingClearController.this, true);
        }
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume() {
        activityContextManager.getInstance().onCheckPurgeStack();
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
    }

    /*UI Redirection*/

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this, true);
    }

    public void onCheckBoxTriggered(View view) {
        if (view != null && view.getTag() != null) {
            mSettingClearViewController.onTrigger(settingClearEnums.eClearViewController.M_CHECK_INVOKE, Arrays.asList(view.getTag(), !mCheckBoxList.get(Integer.parseInt(view.getTag().toString())).isChecked()));
        }
    }

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    public void onClose(View view) {
        activityContextManager.getInstance().onRemoveStack(this);
        finish();
    }

}
