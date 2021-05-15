package com.darkweb.genesissearchengine.appManager.externalNavigationManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.sql;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.databaseManager.databaseController;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import org.mozilla.geckoview.ContentBlocking;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.util.Arrays;

import static com.darkweb.genesissearchengine.constants.constants.CONST_PACKAGE_NAME;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

public class externalShortcuts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status.sSettingIsAppStarted = false;
        orbotLocalConstants.mIsTorInitialized = false;
        boolean mConnect = false;

        if (getIntent() != null && getIntent().getStringExtra("shortcut") != null) {
            String bundleString = getIntent().getStringExtra("shortcut");
            switch (bundleString) {
                case "erase":
                    setContentView(R.layout.popup_data_cleared_shortcut);
                    panicExitInvoked();
                    new Handler().postDelayed(this::finish, 3000);
                    return;
                case "erase_and_open":
                    panicExitInvoked();
                    mConnect = true;
                    break;
                case "Restart":
                    break;
            }
        }

        setContentView(R.layout.empty_view);

        if(mConnect){
            new Handler().postDelayed(() -> activityContextManager.getInstance().getHomeController().onStartApplication(null), 2000);
        }

        new Handler().postDelayed(() ->
        {
            /* Start Required Activity */

            Intent intent = new Intent(this.getIntent());
            intent.setClassName(this.getApplicationContext(), homeController.class.getName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            this.startActivity(intent);
            overridePendingTransition(R.anim.fade_in_instant, R.anim.fade_out_instant);

        }, 800);
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if(data!=null){
            activityContextManager.getInstance().getHomeController().onOpenLinkNewTab(data.toString());
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.darkweb.genesissearchengine");
            startActivity(launchIntent);
            overridePendingTransition(R.anim.fade_in_instant, R.anim.fade_out_instant);
        }
    }

    public void panicExitInvoked() {
        status.sSettingIsAppStarted = false;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_HISTORY,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_SUGGESTION,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_JAVA_SCRIPT,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_HISTORY_CLEAR,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.VPN_ENABLED,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_ENABLES,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FONT_ADJUSTABLE,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_ZOOM,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_VOICE_INPUT,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_TRACKING_PROTECTION, ContentBlocking.AntiTracking.DEFAULT));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_DONOT_TRACK,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_COOKIE_ADJUSTABLE,ACCEPT_FIRST_PARTY));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_FLOAT, Arrays.asList(keys.SETTING_FONT_SIZE,100));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE, strings.SETTING_DEFAULT_LANGUAGE));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE_REGION,strings.SETTING_DEFAULT_LANGUAGE_REGION));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE, constants.CONST_BACKEND_GENESIS_URL));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1,strings.BRIDGE_CUSTOM_BRIDGE_OBFS4));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS,1));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_RESTORE_TAB,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_CHARACTER_ENCODING,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_SHOW_IMAGES,0));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SHOW_FONTS,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_TOOLBAR_THEME,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FULL_SCREEN_BROWSIING,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_THEME, enums.Theme.THEME_DEFAULT));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_LIST_VIEW,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SHOW_TAB_GRID,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_OPEN_URL_IN_NEW_TAB,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_POPUP,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_TYPE,strings.BRIDGE_CUSTOM_BRIDGE_OBFS4));

        databaseController.getInstance().initialize(this);
        databaseController.getInstance().execSQL(sql.SQL_CLEAR_HISTORY,null);
        databaseController.getInstance().execSQL(sql.SQL_CLEAR_BOOKMARK,null);
        databaseController.getInstance().execSQL(sql.SQL_CLEAR_TAB,null);
        status.initStatus(activityContextManager.getInstance().getHomeController());

    }
}