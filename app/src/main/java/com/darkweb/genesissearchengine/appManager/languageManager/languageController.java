package com.darkweb.genesissearchengine.appManager.languageManager;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.helpManager.helpController;
import com.darkweb.genesissearchengine.appManager.tabManager.tabController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.helperManager.theme;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.darkweb.genesissearchengine.appManager.languageManager.languageEnums.eLanguageModel.M_ACTIVE_LANGUAGE;
import static com.darkweb.genesissearchengine.constants.constants.CONST_LANGUAGE_DEFAULT_LANG;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eLangManager.M_SUPPORTED_SYSTEM_LANGUAGE_INFO;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManager.M_LANGUAGE_SUPPORT_FAILURE;

public class languageController extends AppCompatActivity {

    /*Private Variables*/

    private languageViewController mLanguageViewController;
    private languageModel mLanguageModel;
    private ImageView mBlocker;

    private RecyclerView mRecycleView;
    private languageAdapter mLanguageAdapter;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_view);

        initializeAppModel();
        initializeConnections();
        initializeAdapter();
        onInitScroll();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);

        theme.getInstance().onConfigurationChanged(this);
    }

    private void initializeAppModel()
    {
        mLanguageViewController = new languageViewController();
        mLanguageModel = new languageModel();
    }

    private void initializeConnections()
    {
        activityContextManager.getInstance().onStack(this);
        mRecycleView = findViewById(R.id.pRecycleView);
        mBlocker = findViewById(R.id.pBlocker);

        mLanguageViewController.initialization(new languageViewCallback(),this, mBlocker);
    }

    private void initializeAdapter(){
        mLanguageAdapter = new languageAdapter((ArrayList<languageDataModel>)mLanguageModel.onTrigger(languageEnums.eLanguageModel.M_SUPPORTED_LANGUAGE,null), this, status.sSettingLanguage, new languageAdapterCallback());
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

    private void onInitScroll(){
        int mPosition = (int)mLanguageModel.onTrigger(M_ACTIVE_LANGUAGE,null);
        if(!getIntent().getExtras().containsKey("activity_restarted")){
            if(mPosition>0){
                mPosition -= 1;
            }
            mRecycleView.scrollToPosition(mPosition);
        }else {
            int mPositionOffset = getIntent().getExtras().getInt("activity_restarted");
            Objects.requireNonNull(mRecycleView.getLayoutManager()).scrollToPosition(mPositionOffset);
        }
    }

    /*UI Redirections*/

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public void onClose(View view) {
        activityContextManager.getInstance().onRemoveStack(this);
        finish();
    }

    /*Helper Methods*/

    private boolean changeLanguage(String pLanguageCode, String pLanguageRegion){
        boolean mDefaultLanguageNotSupported = false;
        if(pLanguageCode.equals(CONST_LANGUAGE_DEFAULT_LANG)){
            Locale mSystemLocale = Resources.getSystem().getConfiguration().locale;
            String mSystemLangugage = mSystemLocale.toString();
            status.sSettingLanguage = CONST_LANGUAGE_DEFAULT_LANG;
            status.sSettingLanguageRegion = CONST_LANGUAGE_DEFAULT_LANG;
            if(!mSystemLangugage.equals("en_US") && !mSystemLangugage.equals("de_DE") && !mSystemLangugage.equals("ca_ES") && !mSystemLangugage.equals("zh_CN") && !mSystemLangugage.equals("ch_CZ") && !mSystemLangugage.equals("nl_NL") && !mSystemLangugage.equals("fr_FR") && !mSystemLangugage.equals("el_GR") && !mSystemLangugage.equals("hu_HU") && !mSystemLangugage.equals("in_ID") && !mSystemLangugage.equals("it_IT") && !mSystemLangugage.equals("ja_JP") && !mSystemLangugage.equals("ko_KR") && !mSystemLangugage.equals("pt_PT") && !mSystemLangugage.equals("ro_RO") && !mSystemLangugage.equals("ru_RU") && !mSystemLangugage.equals("th_TH") && !mSystemLangugage.equals("tr_TR") && !mSystemLangugage.equals("uk_UA") && !mSystemLangugage.equals("vi_VN")){
                mDefaultLanguageNotSupported = true;
            }
        }else {
            if(status.sSettingLanguage.equals(pLanguageCode)){
                return true;
            }
            status.sSettingLanguage = pLanguageCode;
            status.sSettingLanguageRegion = pLanguageRegion;
        }
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE,status.sSettingLanguage));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE_REGION,status.sSettingLanguageRegion));
        finish();

        getIntent().putExtra("activity_restarted",((LinearLayoutManager) Objects.requireNonNull(mRecycleView.getLayoutManager())).findFirstCompletelyVisibleItemPosition());
        helperMethod.restartActivity(getIntent(), this);
        overridePendingTransition(R.anim.fade_in_lang, R.anim.fade_out_lang);
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_SET_LANGUAGE);

        if(activityContextManager.getInstance().getSettingController()!=null && !activityContextManager.getInstance().getSettingController().isDestroyed()){
            activityContextManager.getInstance().getSettingController().onRedrawXML();
        }
        if(activityContextManager.getInstance().getSettingGeneralController()!=null && !activityContextManager.getInstance().getSettingGeneralController().isDestroyed()){
            activityContextManager.getInstance().getSettingGeneralController().onLanguageChanged();
        }

        if(mDefaultLanguageNotSupported){
            pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(Resources.getSystem().getConfiguration().locale.getDisplayName(), this),M_LANGUAGE_SUPPORT_FAILURE);
        }

        status.mThemeApplying = true;
        activityContextManager.getInstance().getHomeController().recreate();

        return true;
    }

    /*View Callbacks*/

    private class languageViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /*Adapter Callbacks*/

    private class languageAdapterCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            if(e_type.equals(languageEnums.eLanguageAdapterCallback.M_UPDATE_LANGUAGE)){
                return changeLanguage((String)data.get(0), (String)data.get(1));
            }
            else if(e_type.equals(languageEnums.eLanguageAdapterCallback.M_DISABLE_VIEW_CLICK)){
                mLanguageViewController.onTrigger(languageEnums.eLanguagevViewController.M_UPDATE_BLOCKER, Collections.singletonList(false));
            }
            else if(e_type.equals(languageEnums.eLanguageAdapterCallback.M_ENABLE_VIEW_CLICK)){
                mLanguageViewController.onTrigger(languageEnums.eLanguagevViewController.M_UPDATE_BLOCKER, Collections.singletonList(true));
            }
            else if(e_type.equals(languageEnums.eLanguageAdapterCallback.M_SYSTEM_LANGUAGE_SUPPORT_INFO)){
                return pluginController.getInstance().onLanguageInvoke(null, M_SUPPORTED_SYSTEM_LANGUAGE_INFO);
            }
            return null;
        }
    }

    /*Override Methods*/

    @Override
    protected void onResume() {
        if(mLanguageAdapter!=null){
            mLanguageAdapter.notifyDataSetChanged();
        }
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
        super.onBackPressed();
    }
}