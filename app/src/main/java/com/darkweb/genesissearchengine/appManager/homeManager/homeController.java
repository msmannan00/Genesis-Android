package com.darkweb.genesissearchengine.appManager.homeManager;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkController;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.appManager.landingManager.landingController;
import com.darkweb.genesissearchengine.appManager.languageManager.languageController;
import com.darkweb.genesissearchengine.appManager.orbotLogManager.orbotLogController;
import com.darkweb.genesissearchengine.appManager.orbotManager.orbotController;
import com.darkweb.genesissearchengine.appManager.settingManager.settingHomePage.settingController;
import com.darkweb.genesissearchengine.appManager.tabManager.tabController;
import com.darkweb.genesissearchengine.appManager.tabManager.tabRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.KeyboardUtils;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.widget.progressBar.AnimatedProgressBar;
import com.example.myapplication.R;
import com.google.android.gms.ads.AdView;
import org.mozilla.geckoview.GeckoSession;
import org.torproject.android.service.util.Prefs;
import org.torproject.android.service.wrapper.LocaleHelper;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
import static com.darkweb.genesissearchengine.constants.enums.etype.GECKO_SCROLL_CHANGED;

public class homeController extends AppCompatActivity implements ComponentCallbacks2
{
    /*Model Declaration*/
    private homeViewController mHomeViewController;
    private homeModel mHomeModel;
    private geckoClients mGeckoClient = null;

    /*View Webviews*/
    private NestedGeckoView mGeckoView = null;
    private FrameLayout mTopLayout;
    private FrameLayout mWebViewContainer;

    /*View Objects*/
    private AnimatedProgressBar mProgressBar;
    private ConstraintLayout mSplashScreen;
    private AutoCompleteTextView mSearchbar;
    private ImageView mLoadingIcon;
    private ImageView mBlocker;
    private TextView mLoadingText;
    private AdView mBannerAds = null;
    private ImageButton mGatewaySplash;
    private LinearLayout mTopBar;
    private ImageView mBackSplash;
    private Button mConnectButton;
    private Button mNewTab;
    private View mFindBar;
    private EditText mFindText;
    private TextView mFindCount;
    private ImageButton mVoiceInput;
    private ImageButton mMenu;
    private FrameLayout mNestedScroll;

    /*Redirection Objects*/
    private boolean mPageClosed = false;
    private boolean isKeyboardOpened = false;
    private boolean isSuggestionChanged = false;

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
            pluginController.getInstance().preInitialize(this);
            databaseController.getInstance().initialize(this);
            dataController.getInstance().initialize(this);
            onChangeTheme();
            status.initStatus();
            pluginController.getInstance().onCreate(this);
            onInitTheme();

            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_view);

            initPreFixes();
            activityContextManager.getInstance().setHomeController(this);
            pluginController.getInstance().initializeAllServices(this);
            initializeAppModel();
            initializeConnections();
            pluginController.getInstance().initialize();
            initializeLocalEventHandlers();
            initLandingPage();
            initLocalLanguage();
    }

    public void onInitTheme(){

        if(status.sTheme == enums.Theme.THEME_DARK){
            if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }else if(status.sTheme == enums.Theme.THEME_LIGHT){
            if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }else {
            if((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO){
                if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }else {
                if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        }
    }

    private void initLocalLanguage() {

        String lang = Resources.getSystem().getConfiguration().locale.getLanguage();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= 24) {
            config.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        } else {
            config.locale = locale;
            getBaseContext().getApplicationContext().createConfigurationContext(config);
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if(data!=null){
            status.sSettingRedirectStatus = data.toString();
            if(status.sSettingIsAppStarted){
                onLoadURL(status.sSettingRedirectStatus);
            }
        }
    }

    public void initLandingPage(){
        if(status.sSettingFirstStart){
            helperMethod.openActivity(landingController.class, constants.CONST_LIST_HISTORY, homeController.this,false);
            status.sSettingFirstStart = false;
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FIRST_INSTALLED,false));
        }

        if(status.sSettingIsAppStarted){
            mHomeViewController.onPageFinished();
            mSplashScreen.setAlpha(0);
            mHomeViewController.onProgressBarUpdate(100);
        }
    }

    public void initializeAppModel()
    {
        mHomeViewController = new homeViewController();
        mHomeModel = new homeModel();
    }

    public void initializeConnections()
    {
        mGeckoView = findViewById(R.id.pWebView);

        mProgressBar = findViewById(R.id.pProgressBar);
        mSplashScreen = findViewById(R.id.pSplashScreen);
        mSearchbar = findViewById(R.id.pSearchInput);
        mLoadingText = findViewById(R.id.pOrbotLogs);
        mWebViewContainer = findViewById(R.id.pWebLayoutView);
        mTopLayout = findViewById(R.id.pTopLayout);
        mLoadingIcon = findViewById(R.id.pLoadingIcon);
        mBannerAds = findViewById(R.id.pAdView);
        mGatewaySplash = findViewById(R.id.pSettings);
        mTopBar = findViewById(R.id.pTopbar);
        mBackSplash = findViewById(R.id.pTopImage);
        mConnectButton = findViewById(R.id.Connect);
        mNewTab = findViewById(R.id.pTabCounter);
        mFindBar = findViewById(R.id.pFindBar);
        mFindText = findViewById(R.id.pFindText);
        mFindCount = findViewById(R.id.pFindCount);
        mVoiceInput = findViewById(R.id.pVoiceInput);
        mMenu = findViewById(R.id.pMenu);
        mBlocker = findViewById(R.id.pBlocker);
        mNestedScroll = findViewById(R.id.pNestedScroll);

        mGeckoView.setSaveEnabled(false);
        mGeckoView.setSaveFromParentEnabled(false);

        mGeckoClient = new geckoClients();
        mHomeViewController.initialization(new homeViewCallback(),this,mNewTab, mWebViewContainer, mLoadingText, mProgressBar, mSearchbar, mSplashScreen, mLoadingIcon, mBannerAds,(ArrayList<historyRowModel>)dataController.getInstance().invokeSuggestion(dataEnums.eSuggestionCommands.M_GET_SUGGESTION, null), mGatewaySplash, mTopBar, mGeckoView, mBackSplash, mConnectButton, mFindBar, mFindText, mFindCount, mTopLayout, mVoiceInput, mMenu, mNestedScroll, mBlocker);
        mGeckoView.onSetHomeEvent(new nestedGeckoViewCallback());
        mGeckoClient.initialize(mGeckoView, new geckoViewCallback(), this,false);
        mGeckoClient.onValidateInitializeFromStartup();
        dataController.getInstance().initializeListData();

        if(status.sSettingIsAppStarted){
            Object mTempModel = dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
            if(mTempModel!=null){
                tabRowModel model = (tabRowModel)mTempModel;
                onLoadTab(model.getSession(),false);
                onLoadURL(mGeckoClient.getSession().getCurrentURL());
                mGeckoClient.initSession(model.getSession());
            }else {
                onNewIntent(getIntent());
            }
        }
    }

    public void onChangeTheme(){
        if(!status.sSettingIsAppStarted){
            status.sDefaultNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        }
    }

    public void initPreFixes() {
        try {
            Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");

            Method method = Objects.requireNonNull(clazz.getSuperclass()).getDeclaredMethod("stop");
            method.setAccessible(true);

            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);

            method.invoke(field.get(null));
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void initializeGeckoView(boolean isForced, boolean pDatabaseSavable){
        mGeckoClient.initialize(mGeckoView, new geckoViewCallback(), this,isForced);
        onSaveCurrentTab(mGeckoClient.getSession(),pDatabaseSavable);
        mHomeViewController.initTab((int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null));
    }

    public void initTab(boolean isKeyboardOpened){
        onNewTab(isKeyboardOpened, false);
        mHomeViewController.initTab((int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null));
    }

    @Override
    protected void attachBaseContext(Context base) {
        Prefs.setContext(base);
        orbotLocalConstants.mHomeContext = new WeakReference<>(base);

        super.attachBaseContext(LocaleHelper.onAttach(base, Prefs.getDefaultLocale()));
    }

    /*-------------------------------------------------------Helper Methods-------------------------------------------------------*/

    public void onLoadFont(){
        mGeckoClient.onUpdateFont();
        mHomeViewController.onReDraw();
    }

    public void onLoadProxy(View view){
        if(pluginController.getInstance().isInitialized() && !mPageClosed){
            pluginController.getInstance().logEvent(strings.EVENT_GATEWAY_OPENED);
            helperMethod.openActivity(orbotController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
        }
    }


    public void initRuntimeSettings()
    {
        mGeckoClient.updateSetting();
    }

    public void onLoadURL(String url){
        mHomeViewController.onClearSelections(true);
        mGeckoClient.loadURL(url.replace("genesis.onion","boogle.store"));
    }

    public void onLoadTab(geckoSession mTempSession,boolean isSessionClosed){
        if(!isSessionClosed){
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.MOVE_TAB_TO_TOP, Collections.singletonList(mTempSession));
        }

        mGeckoClient.initSession(mTempSession);
        mGeckoView.releaseSession();
        mGeckoView.setSession(mTempSession);

        mHomeViewController.onClearSelections(false);
        mHomeViewController.onUpdateSearchBar(mTempSession.getCurrentURL(),false);
        if(mTempSession.getProgress()>0 && mTempSession.getProgress()<100){
            mHomeViewController.onProgressBarUpdate(mTempSession.getProgress());
        }else {
            mHomeViewController.progressBarReset();
        }
        if(mGeckoClient.getSession().onGetInitializeFromStartup()){
            mGeckoClient.onRedrawPixel();
        }
        mGeckoClient.onValidateInitializeFromStartup();
    }

    /*-------------------------------------------------------USER EVENTS-------------------------------------------------------*/

    private BroadcastReceiver downloadStatus = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            DownloadManager dMgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            Cursor c= Objects.requireNonNull(dMgr).query(new DownloadManager.Query().setFilterById(id));

            if(c.moveToFirst()){
                String url = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));
                onNotificationInvoked(URLUtil.guessFileName(url, null, null), enums.etype.download_folder);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(downloadStatus);
        }catch (Exception ignored){}
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeLocalEventHandlers() {

        registerReceiver(downloadStatus,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        mFindText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                if(mFindText.getText().length()==0 && mGeckoClient!=null){
                        mGeckoClient.getSession().getFinder().clear();
                        mHomeViewController.onUpdateFindBarCount(0,0);
                    }else {
                        mGeckoClient.getSession().findInPage(mFindText.getText().toString(), GeckoSession.FINDER_FIND_MATCH_CASE & GeckoSession.FINDER_DISPLAY_HIGHLIGHT_ALL);
                }
            }
        });

        mSearchbar.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE)
            {
                onSearchBarInvoked(v);
                mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),true);
                mHomeViewController.onClearSelections(true);
                mGeckoClient.setLoading(true);
                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
                    pluginController.getInstance().logEvent(strings.EVENT_SEARCH_INVOKED);
                    mHomeViewController.onClearSelections(false);
                }, 500);
            }
            return true;
        });

        mGatewaySplash.setOnTouchListener((v, event) ->
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                mGatewaySplash.setElevation(9);
            else if (event.getAction() == MotionEvent.ACTION_UP)
                mGatewaySplash.setElevation(2);
                return false;
        });

        mGeckoView.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus)
            {
                helperMethod.hideKeyboard(homeController.this);
                status.sSettingIsAppStarted = true;
                pluginController.getInstance().onResetMessage();
                mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false);
            }
        });

        mSearchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
            }
        });

        mSearchbar.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus)
            {
                mHomeViewController.initSearchBarFocus(false);
                if(!mGeckoClient.isLoading()){
                    mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false);
                }
                if(isSuggestionChanged){
                    isSuggestionChanged = false;
                    mHomeViewController.initializeSuggestionView((ArrayList<historyRowModel>)dataController.getInstance().invokeSuggestion(dataEnums.eSuggestionCommands.M_GET_SUGGESTION, null));
                }
            }else {
                mHomeViewController.initSearchBarFocus(true);
                mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),true);
            }
        });

        pluginController.getInstance().logEvent(strings.EVENT_APP_STARTED);
        KeyboardUtils.addKeyboardToggleListener(this, isVisible -> isKeyboardOpened = isVisible);
    }

    void onSearchBarInvoked(View view){
        String url = ((EditText)view).getText().toString();
        String validated_url = mHomeModel.urlComplete(url, mHomeModel.getSearchEngine());
        if(validated_url!=null){
            url = validated_url;
        }
        mHomeViewController.onUpdateSearchBar(url,false);
        onLoadURL(url);
    }

    public void onSuggestionInvoked(View view){
        String val = ((TextView)view.findViewById(R.id.hintCompletionUrl)).getText().toString();
        onLoadURL(val);
        mSearchbar.setSelection(0);
        mHomeViewController.onUpdateSearchBar(val,false);
    }

    public void applyTheme(){
        status.mCurrentReloadURL = mSearchbar.getText().toString();
        recreate();
    }

    public void onHomeButton(View view){
        pluginController.getInstance().logEvent(strings.EVENT_HOME_INVOKED);
        onLoadURL(helperMethod.getDomainName(mHomeModel.getSearchEngine()));
        mHomeViewController.onUpdateLogo();
    }

    public geckoSession onNewTabInit(){
        return mGeckoClient.initFreeSession(mGeckoView, this, new geckoViewCallback());
    }

    public void onNewTab(boolean isKeyboardOpenedTemp, boolean isKeyboardOpened){
        mGeckoClient.onRedrawPixel();
        initializeGeckoView(true, true);
        if(status.sOpenURLInNewTab){
            onLoadURL(helperMethod.getDomainName(status.sSettingSearchStatus));
            mHomeViewController. onUpdateSearchBar(helperMethod.getDomainName(status.sSettingSearchStatus),false);
        }else {
            onLoadURL("about:blank");
            mHomeViewController. onUpdateSearchBar(strings.HOME_BLANK_PAGE,false);
        }
        mHomeViewController.progressBarReset();
        mHomeViewController.onNewTab(isKeyboardOpened,isKeyboardOpenedTemp);
        mHomeViewController.onSessionChanged();
    }

    public void onOpenTabViewBoundary(View view){
        mGeckoClient.onRedrawPixel();
        final Handler handler = new Handler();
        mNewTab.setPressed(true);
        handler.postDelayed(() ->
        {
            helperMethod.openActivity(tabController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
            overridePendingTransition(R.anim.popup_anim_in, R.anim.popup_anim_out);
        }, 100);
    }

    public void onNotificationInvoked(String message,enums.etype e_type){
        mHomeViewController.downloadNotification(message,e_type);
    }

    public void onOpenMenuItem(View view){
        pluginController.getInstance().setLanguage();
        pluginController.getInstance().logEvent(strings.EVENT_MENU_INVOKED);
        status.sSettingIsAppStarted = true;
        pluginController.getInstance().onResetMessage();
        initLocalLanguage();
        pluginController.getInstance().onCreate(this);


        mHomeViewController.onOpenMenu(view,mGeckoClient.canGoBack(),!(mProgressBar.getAlpha()<=0 || mProgressBar.getVisibility() ==View.INVISIBLE),mGeckoClient.getUserAgent());
    }

    public void onFullScreenSettingChanged(){
        mHomeViewController.initTopBarPadding();
    }

    @Override
    public void onBackPressed(){
        pluginController.getInstance().logEvent(strings.EVENT_ON_BACK);
        mSearchbar.clearFocus();
        if(mFindBar.getVisibility() == View.VISIBLE){
            mHomeViewController.onUpdateFindBar(false);
        }
        else if(mGeckoClient.getFullScreenStatus()){
            mGeckoClient.onBackPressed(true);
            mHomeViewController.onClearSelections(true);
        }
        else {
            mGeckoClient.onExitFullScreen();
        }
    }

    /*Activity States*/

    public void onReload(View view){
        mHomeViewController.onUpdateLogo();
    }

    public void onClearSession(){
        mGeckoClient.onClearSession();
    }

    public void onClearSiteData(){
        mGeckoClient.onClearSiteData();
    }

    public void onClearCache(){
        mGeckoClient.onClearCache();
    }

    public void onClearCookies(){
        mGeckoClient.onClearCookies();
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        pluginController.getInstance().onResetMessage();
        mHomeViewController.closeMenu();

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mHomeViewController.setOrientation(true);
                if(mGeckoClient.getFullScreenStatus())
                {
                    mHomeViewController.onSetBannerAdMargin(false, true);
                }
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                mHomeViewController.setOrientation(false);
                if(mGeckoClient.getFullScreenStatus())
                {
                    mHomeViewController.onSetBannerAdMargin(true,pluginController.getInstance().isAdvertLoaded());
                }
            }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mHomeViewController!=null){
            mHomeViewController.closeMenu();
            helperMethod.hideKeyboard(this);
        }

        mGeckoClient.onExitFullScreen();
        pluginController.getInstance().onResetMessage();
    }

    @Override
    public void onResume()
    {
        pluginController.getInstance().onResume(this);
        activityContextManager.getInstance().setCurrentActivity(this);
        if (mGeckoClient.getSession()!=null && mGeckoClient!=null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mGeckoClient.getUriPermission()!=null) {
            this.revokeUriPermission(mGeckoClient.getUriPermission(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if(status.sSettingIsAppStarted){
            onStartApplication(null);
        }
        if((int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null)<=0){
            //onNewTab(false, false);
        }

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==100){
            if(resultCode == RESULT_OK && null != data){
                onSearchBarInvoked(mSearchbar);
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mHomeViewController.onUpdateSearchBar(result.get(0),false);
                helperMethod.hideKeyboard(homeController.this);
                mGeckoClient.setLoading(true);
                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
                    pluginController.getInstance().logEvent(strings.EVENT_SEARCH_INVOKED);
                    mGeckoView.clearFocus();
                }, 500);
            }
        }
        else if(requestCode==1){
            mGeckoClient.onUploadRequest(resultCode,data);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onSetBannerAdMargin(){
        mHomeViewController.onSetBannerAdMargin(true,pluginController.getInstance().isAdvertLoaded());
    }

    public void onVoiceClick(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Voice2Text \n Say Something!!");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException ignored) {

        }
    }
    /*-------------------------------------------------------External Callback Methods-------------------------------------------------------*/

    public void onSuggestionUpdate(){
        if(!mSearchbar.isFocused()){
            mHomeViewController.initializeSuggestionView((ArrayList<historyRowModel>)dataController.getInstance().invokeSuggestion(dataEnums.eSuggestionCommands.M_GET_SUGGESTION, null));
        }else {
            isSuggestionChanged = true;
        }
    }

    public void onStartApplication(View view){
        pluginController.getInstance().initializeOrbot();
        onInvokeProxyLoading();
        mHomeViewController.initHomePage();
    }

    public void onDownloadFile(){
        mGeckoClient.downloadFile();
    }

    public void onManualDownload(String url){

        /*EXTERNAL STORAGE REQUEST*/
        mGeckoClient.manual_download(url,this);
    }

    public AdView getBannerAd()
    {
        return mBannerAds;
    }

    public void onInvokeProxyLoading(){

         Callable<String> callable = () -> {
            mHomeViewController.onUpdateLogs(pluginController.getInstance().orbotLogs());
            return strings.GENERIC_EMPTY_STR;
        };

        mHomeViewController.initProxyLoading(callable);
    }

    public void onOpenLinkNewTab(String url){
        mGeckoClient.onRedrawPixel();
        initializeGeckoView(true, true);
        mHomeViewController.progressBarReset();
        mHomeViewController.onNewTab(false,isKeyboardOpened);
        mHomeViewController.onUpdateSearchBar(url,false);
        mGeckoClient.loadURL(url);
    }

    public void onSaveCurrentTab(geckoSession session,boolean isHardCopy){
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_ADD_TAB, Arrays.asList(session,isHardCopy));
    }

    public boolean onCloseCurrentTab(geckoSession session){
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.CLOSE_TAB, Collections.singletonList(session));
        tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);

        session.stop();
        session.close();
        initTabCount();

        if(model!=null){
            if(activityContextManager.getInstance().getTabController()==null || activityContextManager.getInstance().getTabController()!=null && (activityContextManager.getInstance().getTabController().isDestroyed())){
                onLoadTab(model.getSession(),true);
            }
            return true;
        }
        else {
            if(activityContextManager.getInstance().getTabController()==null || activityContextManager.getInstance().getTabController()!=null && (activityContextManager.getInstance().getTabController().isDestroyed())){
                return false;
            }else {
                return true;
            }
        }
    }

    public void initTabCount(){
        mHomeViewController.initTab((int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null));
    }

    /*-------------------------------------------------------CALLBACKS-------------------------------------------------------*/

    public void onHideFindBar(View view){
        mHomeViewController.onUpdateFindBar(false);
        mGeckoClient.getSession().getFinder().clear();
    }

    public void onFindNext(View view){
        mFindText.setText("0/0");
        mGeckoClient.getSession().findInPage(mFindText.getText().toString(), GeckoSession.FINDER_FIND_MATCH_CASE & GeckoSession.FINDER_DISPLAY_HIGHLIGHT_ALL);
    }

    public void onFindPrev(View view){
        mFindText.setText("0/0");
        mGeckoClient.getSession().findInPage(mFindText.getText().toString(), GeckoSession.FINDER_FIND_BACKWARDS & GeckoSession.FINDER_DISPLAY_HIGHLIGHT_ALL);
    }

    public void onOpenDownloadFolder(View view){
        helperMethod.openDownloadFolder(homeController.this);
    }

    public void onMenuItemInvoked(View view){
        int menuId = view.getId();
        if (menuId == R.id.menu11) {
            onNewTab(isKeyboardOpened, true);
        }
        else if (menuId == R.id.menu10) {
            if(!onCloseCurrentTab(mGeckoClient.getSession())){
                onNewTab(isKeyboardOpened, true);
            }
        }
        else {
            mSearchbar.clearFocus();
            if (menuId == R.id.menu12) {
                mHomeViewController.closeMenu();
                helperMethod.hideKeyboard(this);
                helperMethod.openActivity(orbotLogController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
            }
            else if (menuId == R.id.menu9) {
                helperMethod.hideKeyboard(this);
                helperMethod.openActivity(tabController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
            }
            else if (menuId == R.id.menu8) {
                helperMethod.hideKeyboard(this);
                onOpenDownloadFolder(null);
            }
            else if (menuId == R.id.menu7) {
                helperMethod.hideKeyboard(this);
                helperMethod.openActivity(historyController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
            }
            else if (menuId == R.id.menu6)
            {
                helperMethod.hideKeyboard(this);
                helperMethod.openActivity(settingController.class,constants.CONST_LIST_HISTORY, homeController.this,true);
            }
            else if (menuId == R.id.pMenuDelete)
            {
                pluginController.getInstance().MessageManagerHandler(homeController.this, Collections.singletonList(mGeckoClient.getSession().getCurrentURL()), enums.eMessageEnums.M_BOOKMARK);
            }
            else if (menuId == R.id.pMenuOpenNewTab)
            {
                helperMethod.hideKeyboard(this);
                helperMethod.openActivity(bookmarkController.class,constants.CONST_LIST_BOOKMARK, homeController.this,true);
            }
            else if (menuId == R.id.pMenuOpenCurrentTab)
            {
                helperMethod.hideKeyboard(this);
                pluginController.getInstance().MessageManagerHandler(homeController.this,null,enums.eMessageEnums.M_REPORT_URL);
            }
            else if (menuId == R.id.pMenuShare)
            {
                helperMethod.hideKeyboard(this);
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,true));
                status.sSettingIsAppRated = true;
                pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), Collections.singletonList(strings.GENERIC_EMPTY_STR), enums.eMessageEnums.M_RATE_APP);
            }
            else if (menuId == R.id.pMenuCopy)
            {
                helperMethod.hideKeyboard(this);
                helperMethod.shareApp(homeController.this);
            }
            else if (menuId == R.id.pMenuFind)
            {
                helperMethod.hideKeyboard(this);
                mHomeViewController.onUpdateFindBar(true);
            }
            if (menuId == R.id.menu20) {
                helperMethod.hideKeyboard(this);
                mGeckoClient.onStop();
            }
            if (menuId == R.id.menu21) {
                helperMethod.hideKeyboard(this);
                mGeckoClient.onReload();
            }
            if (menuId == R.id.menu22) {
                helperMethod.hideKeyboard(this);
                mGeckoClient.onForwardPressed();
            }
            if (menuId == R.id.menu23) {
                helperMethod.hideKeyboard(this);
                pluginController.getInstance().MessageManagerHandler(homeController.this, Collections.singletonList(mGeckoClient.getSession().getCurrentURL()),enums.eMessageEnums.M_BOOKMARK);
            }
            if (menuId == R.id.menu24) {
                helperMethod.hideKeyboard(this);
                onHomeButton(view);
            }
            if (menuId == R.id.menu26 || menuId == R.id.menu27 || menuId == R.id.menu28) {
                helperMethod.hideKeyboard(this);
                mGeckoClient.toogleUserAgent();
                mGeckoClient.onReload();
            }
            if(menuId == R.id.menu25){
                helperMethod.hideKeyboard(this);
                helperMethod.openActivity(languageController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
            }
        }
        mHomeViewController.closeMenu();
    }

    public void onOrbotLog(View view) {
        mHomeViewController.closeMenu();
        helperMethod.openActivity(orbotLogController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
    }

    public class nestedGeckoViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if(e_type.equals(GECKO_SCROLL_CHANGED)){
                mHomeViewController.onMoveTopBar((int)data.get(0));
            }
            return null;
        }
    }

    public class homeViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
           if(e_type.equals(enums.etype.download_folder))
           {
               onOpenDownloadFolder(null);
           }
           if(e_type.equals(enums.etype.on_init_ads))
           {
               mHomeViewController.onSetBannerAdMargin((boolean)data.get(0),pluginController.getInstance().isAdvertLoaded());
           }
           else if(e_type.equals(enums.etype.on_url_load)){
               mHomeViewController.onUpdateLogs("Starting | Genesis Search");
               onLoadURL(data.get(0).toString());
           }
           else if(e_type.equals(enums.etype.recheck_orbot)){
               pluginController.getInstance().isOrbotRunning();
           }
           else if(e_type.equals(enums.etype.M_HOME_PAGE)){
               geckoSession mSession = (geckoSession)dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_HOME_PAGE, null);
               if(mSession!=null){
                   onLoadTab(mSession, false);
               }
               return dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_HOME_PAGE, null);
           }
           return null;
        }
    }

    String dataToStr(Object data,String defaultVal){
        if(data==null){
            return defaultVal;
        }
        else {
            return data.toString();
        }
    }

    String dataToStr(Object data){
        if(data==null){
            return strings.GENERIC_EMPTY_STR;
        }
        else {
            return data.toString();
        }
    }


    public class geckoViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if(e_type.equals(enums.etype.progress_update)){
                mHomeViewController.onProgressBarUpdate((int)data.get(0));
            }
            else if(e_type.equals(enums.etype.ON_UPDATE_TITLE_BAR)){
                mHomeViewController.onUpdateTitleBar((boolean)data.get(0));
            }
            else if(e_type.equals(enums.etype.on_url_load)){
                mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false);
            }
            else if(e_type.equals(enums.etype.back_list_empty)){
                if((int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null)>1){
                    if(!onCloseCurrentTab(mGeckoClient.getSession())){
                        onNewTab(true,false);
                    }
                }else {
                    helperMethod.onMinimizeApp(homeController.this);
                }
            }
            else if(e_type.equals(enums.etype.start_proxy)){
                pluginController.getInstance().setProxy(dataToStr(data.get(0)));
            }
            else if(e_type.equals(enums.etype.on_update_history)){
                if(activityContextManager.getInstance().getTabController()!=null && !activityContextManager.getInstance().getTabController().isDestroyed() && !activityContextManager.getInstance().getTabController().isFinishing()){
                    dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(data.get(1), mGeckoView.capturePixels()));
                }
                return dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_ADD_HISTORY ,data);
            }
            else if(e_type.equals(enums.etype.on_update_suggestion)){
                dataController.getInstance().invokeSuggestion(dataEnums.eSuggestionCommands.M_ADD_SUGGESTION, Arrays.asList(data.get(0).toString(),data.get(2).toString()));
            }
            else if(e_type.equals(enums.etype.on_page_loaded)){
                pluginController.getInstance().logEvent(strings.EVENT_PAGE_OPENED_SUCCESS);
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_IS_BOOTSTRAPPED,true));
                mHomeViewController.onPageFinished();
                if(status.sSettingIsWelcomeEnabled && !status.sSettingIsAppStarted){
                    final Handler handler = new Handler();
                    Runnable runnable = () ->
                    {
                        if(!status.sSettingIsAppStarted){
                            pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), Collections.singletonList(strings.GENERIC_EMPTY_STR), enums.eMessageEnums.M_WELCOME);
                        }
                        status.sSettingIsAppStarted = true;
                    };
                    handler.postDelayed(runnable, 1300);
                }else {
                    final Handler handler = new Handler();
                    Runnable runnable = () -> pluginController.getInstance().initializeBannerAds();
                    handler.postDelayed(runnable, 2000);
                }
            }
            else if(e_type.equals(enums.eMessageEnums.M_RATE_APPLICATION)){
                if(!status.sSettingIsAppRated){
                    dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,true));
                    status.sSettingIsAppRated = true;
                    pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), Collections.singletonList(strings.GENERIC_EMPTY_STR), enums.eMessageEnums.M_RATE_APP);
                }
            }
            else if(e_type.equals(enums.etype.on_load_error)){
                pluginController.getInstance().setLanguage();
                initLocalLanguage();
                mHomeViewController.onPageFinished();
                mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false);
            }
            else if(e_type.equals(enums.etype.search_update)){
                mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false);
            }
            else if(e_type.equals(enums.etype.download_file_popup)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,Collections.singletonList(dataToStr(data.get(0))),enums.eMessageEnums.M_DOWNLOAD_FILE);
            }
            else if(e_type.equals(enums.etype.on_full_screen)){
                boolean status = (Boolean)data.get(0);
                mHomeViewController.onFullScreenUpdate(status);
            }
            else if(e_type.equals(enums.etype.on_update_favicon)){
                dataController.getInstance().invokeImageCache(dataEnums.eImageCacheCommands.M_SET_IMAGE ,data);
            }
            else if(e_type.equals(enums.eMessageEnums.M_LONG_PRESS_WITH_LINK)){
                 pluginController.getInstance().MessageManagerHandler(homeController.this, Arrays.asList(dataToStr(data.get(0)),dataToStr(data.get(2)),dataToStr(data.get(3))),enums.eMessageEnums.M_LONG_PRESS_WITH_LINK);
            }
            else if(e_type.equals(enums.etype.on_long_press)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,Arrays.asList(dataToStr(data.get(0)),dataToStr(data.get(2))),enums.eMessageEnums.M_LONG_PRESS_DOWNLOAD);
            }
            else if(e_type.equals(enums.eMessageEnums.M_LONG_PRESS_URL)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,Arrays.asList(dataToStr(data.get(0)),dataToStr(data.get(2))),enums.eMessageEnums.M_LONG_PRESS_URL);
            }
            else if(e_type.equals(enums.etype.open_new_tab)){
                onOpenLinkNewTab(dataToStr(data.get(0)));
            }
            else if(e_type.equals(enums.etype.on_close_sesson)){
                if(!onCloseCurrentTab(mGeckoClient.getSession())){
                    onNewTab(true,false);
                }
            }
            else if(e_type.equals(enums.etype.on_playstore_load)){
                helperMethod.openPlayStore(dataToStr(data.get(0)).split("__")[1],homeController.this);
            }
            else if(e_type.equals(enums.etype.on_update_suggestion_url)){
                dataController.getInstance().invokeSuggestion(dataEnums.eSuggestionCommands.M_UPDATE_SUGGESTION, Arrays.asList(dataToStr(data.get(0)),dataToStr(data.get(2))));
            }
            else if(e_type.equals(enums.etype.ON_UPDATE_TAB_TITLE)){
                if(activityContextManager.getInstance().getTabController()!=null && !activityContextManager.getInstance().getTabController().isDestroyed())
                activityContextManager.getInstance().getTabController().onTabRowChanged((String) data.get(1));
            }
            else if(e_type.equals(dataEnums.eTabCommands.M_UPDATE_PIXEL)){
                try{

                    dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(data.get(1), mGeckoView.capturePixels()));
                }catch (Exception ignored){

                }
            }
            else if(e_type.equals(enums.etype.FINDER_RESULT_CALLBACK)){
                mHomeViewController.onUpdateFindBarCount((int)data.get(0),(int)data.get(1));
            }

            return null;
        }
    }
}

