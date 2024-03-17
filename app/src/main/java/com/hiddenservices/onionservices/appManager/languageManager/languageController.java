package com.hiddenservices.onionservices.appManager.languageManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.helpManager.helpController;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.dataManager.dataController;
import com.hiddenservices.onionservices.dataManager.dataEnums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.appManager.activityThemeManager;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import com.hiddenservices.onionservices.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import static com.hiddenservices.onionservices.appManager.languageManager.languageEnums.eLanguageModel.M_ACTIVE_LANGUAGE;
import static com.hiddenservices.onionservices.constants.constants.CONST_LANGUAGE_DEFAULT_LANG;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eLangManager.M_SUPPORTED_SYSTEM_LANGUAGE_INFO;

@SuppressLint("NotifyDataSetChanged")
public class languageController extends AppCompatActivity {

    /*Private Variables*/

    private languageViewController mLanguageViewController;
    private languageModel mLanguageModel;
    private ImageView mBlocker;
    private boolean mThemeApplied = false;
    private Locale mLanguagePrevious = status.mSystemLocale;

    private RecyclerView mRecycleView;
    private languageAdapter mLanguageAdapter;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_view);

        initializeConnections();
        initializeAppModel();
        initializeAdapter();
        onInitScroll();
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

    private void initializeAppModel() {
        mLanguageViewController.onInit();
        mLanguageModel = new languageModel();
        mLanguageModel.onInit();
    }

    private void initializeConnections() {
        activityContextManager.getInstance().onStack(this);
        mRecycleView = findViewById(R.id.pRecycleView);
        mBlocker = findViewById(R.id.pSecureRootBlocker);

        mLanguageViewController = new languageViewController();
        mLanguageViewController.initialization(new languageViewCallback(), this, mBlocker);
    }

    private void initializeAdapter() {
        mLanguageAdapter = new languageAdapter((ArrayList<languageDataModel>) mLanguageModel.onTrigger(languageEnums.eLanguageModel.M_SUPPORTED_LANGUAGE, null), this, status.sSettingLanguage, new languageAdapterCallback());
        ((SimpleItemAnimator) Objects.requireNonNull(mRecycleView.getItemAnimator())).setSupportsChangeAnimations(false);

        mRecycleView.setAdapter(mLanguageAdapter);
        mRecycleView.setItemViewCacheSize(100);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setItemViewCacheSize(100);
        mRecycleView.setDrawingCacheEnabled(true);
        mRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mRecycleView.setAdapter(mLanguageAdapter);
    }

    private void onInitScroll() {
        int mPosition = (int) mLanguageModel.onTrigger(M_ACTIVE_LANGUAGE, null);
        if (!getIntent().getExtras().containsKey("activity_restarted")) {
            if (mPosition > 0) {
                mPosition -= 1;
            }
            mRecycleView.scrollToPosition(mPosition);
        } else {
            int mPositionOffset = getIntent().getExtras().getInt("activity_restarted");
            mThemeApplied = true;
            Objects.requireNonNull(mRecycleView.getLayoutManager()).scrollToPosition(mPositionOffset);
        }
    }

    /*UI Redirections*/

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this, true);
    }

    public void onClose(View view) {
        activityContextManager.getInstance().onRemoveStack(this);
        finish();
    }

    /*Helper Methods*/

    private void changeLanguage(String pLanguageCode, String pLanguageRegion) {
        if (pLanguageCode.equals(CONST_LANGUAGE_DEFAULT_LANG)) {
            status.sSettingLanguage = CONST_LANGUAGE_DEFAULT_LANG;
            status.sSettingLanguageRegion = CONST_LANGUAGE_DEFAULT_LANG;
        } else {
            status.sSettingLanguage = pLanguageCode;
            status.sSettingLanguageRegion = pLanguageRegion;
        }
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE, status.sSettingLanguage));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE_REGION, status.sSettingLanguageRegion));

        if (activityContextManager.getInstance().getSettingController() != null && !activityContextManager.getInstance().getSettingController().isDestroyed()) {
            activityContextManager.getInstance().getSettingController().onRedrawXML();
        }
        if (activityContextManager.getInstance().getSettingGeneralController() != null && !activityContextManager.getInstance().getSettingGeneralController().isDestroyed()) {
            activityContextManager.getInstance().getSettingGeneralController().onLanguageChanged();
        }

        status.mThemeApplying = true;
        mThemeApplied = true;

        Intent intent = new Intent(this, languageController.class);
        intent.putExtra("activity_restarted", ((LinearLayoutManager) Objects.requireNonNull(mRecycleView.getLayoutManager())).findFirstCompletelyVisibleItemPosition());
        this.startActivity(intent);

        overridePendingTransition(R.anim.fade_in_lang, R.anim.fade_out_lang);
        this.finish();
    }

    /*View Callbacks*/

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        super.onDestroy();
    }

    private class languageViewCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            return null;
        }
    }

    /*Adapter Callbacks*/

    private class languageAdapterCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            if (e_type.equals(languageEnums.eLanguageAdapterCallback.M_UPDATE_LANGUAGE)) {
                changeLanguage((String) data.get(0), (String) data.get(1));
                return true;
            } else if (e_type.equals(languageEnums.eLanguageAdapterCallback.M_DISABLE_VIEW_CLICK)) {
                mLanguageViewController.onTrigger(languageEnums.eLanguagevViewController.M_UPDATE_BLOCKER, Collections.singletonList(false));
            } else if (e_type.equals(languageEnums.eLanguageAdapterCallback.M_ENABLE_VIEW_CLICK)) {
                mLanguageViewController.onTrigger(languageEnums.eLanguagevViewController.M_UPDATE_BLOCKER, Collections.singletonList(true));
            } else if (e_type.equals(languageEnums.eLanguageAdapterCallback.M_SYSTEM_LANGUAGE_SUPPORT_INFO)) {
                return pluginController.getInstance().onLanguageInvoke(Collections.singletonList(status.sSettingLanguage), M_SUPPORTED_SYSTEM_LANGUAGE_INFO);
            }
            return null;
        }
    }

    /*Override Methods*/

    @Override
    protected void onResume() {
        activityContextManager.getInstance().onPurgeStack();
        if (mLanguageAdapter != null) {
            mLanguageAdapter.notifyDataSetChanged();
        }
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mThemeApplied && !status.mThemeApplying) {
            helperMethod.updateResources(activityContextManager.getInstance().getHomeController(), status.mSystemLocale.getLanguage());
            activityContextManager.getInstance().onResetTheme();

            String mSystemLangugage = status.mSystemLocale.toString();
            if (mSystemLangugage.equals("ur_PK") || mSystemLangugage.equals("ur_UR") || mLanguagePrevious.toString().equals("ur_PK") || mLanguagePrevious.toString().equals("ur_UR")) {
                activityContextManager.getInstance().getHomeController().recreate();
            }
            mLanguagePrevious = status.mSystemLocale;
        }
        status.mThemeApplying = false;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
        super.onBackPressed();
    }
}