package com.darkweb.genesissearchengine.appManager.homeManager;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
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
import com.example.myapplication.R;
import com.google.android.gms.ads.AdView;

import org.mozilla.geckoview.GeckoView;
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

public class homeController extends AppCompatActivity implements ComponentCallbacks2
{
    /*Model Declaration*/
    private homeViewController mHomeViewController;
    private homeModel mHomeModel;
    private geckoClients mGeckoClient = null;

    /*View Webviews*/
    private GeckoView mGeckoView = null;
    private FrameLayout mWebViewContainer;

    /*View Objects*/
    private com.darkweb.genesissearchengine.widget.AnimatedProgressBar mProgressBar;
    private ConstraintLayout mSplashScreen;
    private AutoCompleteTextView mSearchbar;
    private ImageView mLoadingIcon;
    private TextView mLoadingText;
    private AdView mBannerAds = null;
    private ImageButton mGatewaySplash;
    private LinearLayout mTopBar;
    private ImageView mBackSplash;
    private Button mConnectButton;
    private Button mNewTab;

    /*Redirection Objects*/
    private boolean mPageClosed = false;
    private boolean isKeyboardOpened = false;
    private boolean isSuggestionChanged = false;
    private boolean isTabMenuOpened = false;

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
            dataController.getInstance().initializeListData();
            initializeAppModel();
            initializeAppModel();
            initializeConnections();
            pluginController.getInstance().initialize();
            initializeGeckoView(savedInstanceState == null);
            initializeLocalEventHandlers();
            initLandingPage();
            onNewIntent(getIntent());
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
    }

    public void initializeAppModel()
    {
        mHomeViewController = new homeViewController();
        mHomeModel = new homeModel();
    }

    public void initializeConnections()
    {
        mGeckoView = findViewById(R.id.pWebView);

        mProgressBar = findViewById(R.id.progressBar);
        mSplashScreen = findViewById(R.id.splashScreen);
        mSearchbar = findViewById(R.id.pSearchInput);
        mLoadingIcon = findViewById(R.id.imageView_loading_back);
        mLoadingText = findViewById(R.id.loadingText);
        mWebViewContainer = findViewById(R.id.webviewContainer);
        mBannerAds = findViewById(R.id.adView);
        mGatewaySplash = findViewById(R.id.gateway_splash);
        mTopBar = findViewById(R.id.topbar);
        mBackSplash = findViewById(R.id.backsplash);
        mConnectButton = findViewById(R.id.Connect);
        mNewTab = findViewById(R.id.newButtonInvoke);

        mGeckoView.setSaveEnabled(false);
        mGeckoView.setSaveFromParentEnabled(false);

        mGeckoClient = new geckoClients();
        mHomeViewController.initialization(new homeViewCallback(),this,mNewTab, mWebViewContainer, mLoadingText, mProgressBar, mSearchbar, mSplashScreen, mLoadingIcon, mBannerAds,(ArrayList<historyRowModel>)dataController.getInstance().invokeSuggestion(dataEnums.eSuggestionCommands.M_GET_SUGGESTION, null), mGatewaySplash, mTopBar, mGeckoView, mBackSplash, mConnectButton);
    }

    public void onChangeTheme(){
        if(!status.sSettingIsAppStarted){
            status.sDefaultNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        }
    }

    public void initPreFixes() {
        try {
            Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");

            Method method = clazz.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);

            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);

            method.invoke(field.get(null));
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void initializeGeckoView(boolean isForced){
        mGeckoClient.initialize(mGeckoView, new geckoViewCallback(), this,isForced);
        //mGeckoClient.loadURL("about:blank");
        onSaveCurrentTab(mGeckoClient.getSession(),false);
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

    public GeckoView getmGeckoView(){
        return mGeckoView;
    }

    public void onLoadTab(geckoSession mTempSession,boolean isSessionClosed){
        if(!isSessionClosed){
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.MOVE_TAB_TO_TOP, Collections.singletonList(mTempSession));
        }

        mGeckoView.releaseSession();
        mGeckoClient.initSession(mTempSession);
        mGeckoView.setSession(mTempSession);

        mHomeViewController.onClearSelections(false);
        mHomeViewController.onUpdateSearchBar(mTempSession.getCurrentURL(),false);
        if(mTempSession.getProgress()>0 && mTempSession.getProgress()<100){
            mHomeViewController.onProgressBarUpdate(mTempSession.getProgress());
        }else {
            mHomeViewController.progressBarReset();
        }
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

        mSearchbar.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE)
            {
                helperMethod.hideKeyboard(homeController.this);
                mGeckoClient.setLoading(true);
                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
                    pluginController.getInstance().logEvent(strings.EVENT_SEARCH_INVOKED);
                    onSearchBarInvoked(v);
                    mGeckoView.clearFocus();
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
                if(!mGeckoClient.isLoading()){
                    mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false);
                }
                if(isSuggestionChanged){
                    isSuggestionChanged = false;
                    mHomeViewController.initializeSuggestionView((ArrayList<historyRowModel>)dataController.getInstance().invokeSuggestion(dataEnums.eSuggestionCommands.M_GET_SUGGESTION, null));
                }
            }else {
                mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),true);
            }
        });
        pluginController.getInstance().logEvent(strings.EVENT_APP_STARTED);

        KeyboardUtils.addKeyboardToggleListener(this, isVisible -> isKeyboardOpened = isVisible);
    }

    void onSearchBarInvoked(View view){
        String url = ((EditText)view).getText().toString();
        String validated_url = mHomeModel.urlComplete(url);
        if(validated_url!=null){
            url = validated_url;
        }
        mHomeViewController.onUpdateSearchBar(url,false);
        onLoadURL(url);
    }

    public void onSuggestionInvoked(View view){
        String val = ((TextView)view.findViewById(R.id.hintCompletionUrl)).getText().toString();
        mSearchbar.clearFocus();
        mHomeViewController.onUpdateSearchBar(val,false);
        onLoadURL(val);
    }

    public void onHomeButton(View view){
        pluginController.getInstance().logEvent(strings.EVENT_HOME_INVOKED);
        onLoadURL(mHomeModel.getSearchEngine());
        mHomeViewController.onUpdateLogo();
    }

    public void onNewTab(boolean isKeyboardOpenedTemp, boolean isKeyboardOpened){
        initializeGeckoView(true);
        if(status.sOpenURLInNewTab){
            onLoadURL(helperMethod.getDomainName(status.sSettingSearchStatus));
            mHomeViewController. onUpdateSearchBar(helperMethod.getDomainName(status.sSettingSearchStatus),false);
        }else {
            mGeckoView.releaseSession();
            onLoadURL("about:blank");
            mHomeViewController. onUpdateSearchBar(strings.HOME_BLANK_PAGE,false);
        }
        mHomeViewController.progressBarReset();
        mHomeViewController.onNewTab(isKeyboardOpened,isKeyboardOpenedTemp);
        mHomeViewController.onSessionChanged();
    }

    public void onOpenTabViewBoundary(View view){
        mNewTab.setPressed(true);
        isTabMenuOpened = true;
        helperMethod.openActivity(tabController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
        overridePendingTransition(R.anim.popup_anim_in, R.anim.popup_anim_out);
    }

    public void onNotificationInvoked(String message,enums.etype e_type){
        mHomeViewController.downloadNotification(message,e_type);
    }

    public void onOpenMenuItem(View view){
        pluginController.getInstance().setLanguage(this);
        pluginController.getInstance().logEvent(strings.EVENT_MENU_INVOKED);
        status.sSettingIsAppStarted = true;
        pluginController.getInstance().onResetMessage();
        initLocalLanguage();
        pluginController.getInstance().onCreate(this);


        mHomeViewController.onOpenMenu(view,mGeckoClient.canGoBack(),mGeckoClient.canGoForward(),!(mProgressBar.getAlpha()<=0 || mProgressBar.getVisibility() ==View.INVISIBLE),mGeckoClient.getUserAgent());
    }

    @Override
    public void onBackPressed(){
        pluginController.getInstance().logEvent(strings.EVENT_ON_BACK);
        mSearchbar.clearFocus();
        if(mGeckoClient.getFullScreenStatus()){
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
        if (mGeckoClient!=null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mGeckoClient.getUriPermission()!=null) {
            this.revokeUriPermission(mGeckoClient.getUriPermission(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if(status.sSettingIsAppStarted){
            onStartApplication(null);
        }
        super.onResume();
    }

    public void onAcquireDisplay(){
        isTabMenuOpened = false;
        tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
        if(model==null){
            onLoadTab(model.getSession(),true);
        }else {
            mGeckoView.releaseSession();
            mGeckoView.setSession(model.getSession());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==1){
            mGeckoClient.onUploadRequest(resultCode,data);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onSetBannerAdMargin(){
        mHomeViewController.onSetBannerAdMargin(true,pluginController.getInstance().isAdvertLoaded());
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
        initializeGeckoView(true);
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

    public void releaseSession(){
        mGeckoView.releaseSession();
    }

    public void loadExistingTab(){
        tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
        if (model != null)
        {
            onLoadTab(model.getSession(), true);
        }
    }

    /*-------------------------------------------------------CALLBACKS-------------------------------------------------------*/

    public void onOpenDownloadFolder(View view){
        helperMethod.openDownloadFolder(homeController.this);
    }

    public void onMenuItemInvoked(View view){
        int menuId = view.getId();
        if (menuId == R.id.menu11) {
            onNewTab(true, isKeyboardOpened);
        }
        else if (menuId == R.id.menu10) {
            if(!onCloseCurrentTab(mGeckoClient.getSession())){
                onNewTab(true, isKeyboardOpened);
            }
        }
        else {
            mSearchbar.clearFocus();
            if (menuId == R.id.menu12) {
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
                pluginController.getInstance().MessageManagerHandler(homeController.this, Collections.singletonList(mGeckoClient.getSession().getCurrentURL()),enums.etype.bookmark);
            }
            else if (menuId == R.id.pMenuOpenNewTab)
            {
                helperMethod.hideKeyboard(this);
                helperMethod.openActivity(bookmarkController.class,constants.CONST_LIST_BOOKMARK, homeController.this,true);
            }
            else if (menuId == R.id.pMenuOpenCurrentTab)
            {
                helperMethod.hideKeyboard(this);
                pluginController.getInstance().MessageManagerHandler(homeController.this,null,enums.etype.report_url);
            }
            else if (menuId == R.id.pMenuShare)
            {
                helperMethod.hideKeyboard(this);
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,true));
                status.sSettingIsAppRated = true;
                pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), Collections.singletonList(strings.GENERIC_EMPTY_STR), enums.etype.rate_app);
            }
            else if (menuId == R.id.pMenuCopy)
            {
                helperMethod.hideKeyboard(this);
                helperMethod.shareApp(homeController.this);
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
                pluginController.getInstance().MessageManagerHandler(homeController.this, Collections.singletonList(mGeckoClient.getSession().getCurrentURL()),enums.etype.bookmark);
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
        helperMethod.openActivity(orbotLogController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
    }

    public class homeViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, enums.etype e_type)
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
        public Object invokeObserver(List<Object> data, enums.etype e_type)
        {
            if(e_type.equals(enums.etype.progress_update)){
                mHomeViewController.onProgressBarUpdate((int)data.get(0));
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
                    activityContextManager.getInstance().getTabController().onTabRowChanged((int)data.get(1));
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
                            pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), Collections.singletonList(strings.GENERIC_EMPTY_STR), enums.etype.welcome);
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
            else if(e_type.equals(enums.etype.rate_application)){
                if(!status.sSettingIsAppRated){
                    dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,true));
                    status.sSettingIsAppRated = true;
                    pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), Collections.singletonList(strings.GENERIC_EMPTY_STR), enums.etype.rate_app);
                }
            }
            else if(e_type.equals(enums.etype.on_load_error)){
                pluginController.getInstance().setLanguage(homeController.this);
                initLocalLanguage();
                mHomeViewController.onPageFinished();
                mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false);
            }
            else if(e_type.equals(enums.etype.search_update)){
                mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false);
            }
            else if(e_type.equals(enums.etype.download_file_popup)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,Collections.singletonList(dataToStr(data.get(0))),enums.etype.download_file);
            }
            else if(e_type.equals(enums.etype.on_full_screen)){
                boolean status = (Boolean)data.get(0);
                mHomeViewController.onFullScreenUpdate(status);
            }
            else if(e_type.equals(enums.etype.on_update_favicon)){
                dataController.getInstance().invokeImageCache(dataEnums.eImageCacheCommands.M_SET_IMAGE ,data);
            }
            else if(e_type.equals(enums.etype.on_long_press_with_link)){
                 pluginController.getInstance().MessageManagerHandler(homeController.this, Arrays.asList(dataToStr(data.get(0)),dataToStr(data.get(2)),dataToStr(data.get(3))),enums.etype.on_long_press_with_link);
            }
            else if(e_type.equals(enums.etype.on_long_press)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,Arrays.asList(dataToStr(data.get(0)),dataToStr(data.get(2))),enums.etype.download_file_long_press);
            }
            else if(e_type.equals(enums.etype.on_long_press_url)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,Arrays.asList(dataToStr(data.get(0)),dataToStr(data.get(2))),enums.etype.on_long_press_url);
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
            return null;
        }
    }
}

