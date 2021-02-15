package com.darkweb.genesissearchengine.appManager.homeManager.homeController;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkController;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.NestedGeckoView;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.*;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.homeManager.hintManager.hintAdapter;
import com.darkweb.genesissearchengine.appManager.landingManager.landingController;
import com.darkweb.genesissearchengine.appManager.languageManager.languageController;
import com.darkweb.genesissearchengine.appManager.orbotLogManager.orbotLogController;
import com.darkweb.genesissearchengine.appManager.orbotManager.orbotController;
import com.darkweb.genesissearchengine.appManager.settingManager.searchEngineManager.settingSearchController;
import com.darkweb.genesissearchengine.appManager.settingManager.settingHomePage.settingHomeController;
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
import com.darkweb.genesissearchengine.helperManager.OnClearFromRecentService;
import com.darkweb.genesissearchengine.helperManager.SimpleGestureFilter;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.helperManager.trueTime;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.darkweb.genesissearchengine.widget.progressBar.AnimatedProgressBar;
import com.example.myapplication.R;
import com.google.android.gms.ads.AdView;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import org.torproject.android.service.OrbotService;
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
import static com.darkweb.genesissearchengine.constants.enums.etype.M_INITIALIZE_TAB_LINK;
import static com.darkweb.genesissearchengine.constants.enums.etype.M_INITIALIZE_TAB_SINGLE;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManager.*;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManagerCallbacks.M_RATE_APPLICATION;

public class homeController extends AppCompatActivity implements ComponentCallbacks2
{
    /*Model Declaration*/
    private homeViewController mHomeViewController;
    private homeModel mHomeModel;
    private geckoClients mGeckoClient = null;
    private GestureDetector mSwipeDirectionDetector;

    /*View Webviews*/
    private NestedGeckoView mGeckoView = null;
    private androidx.constraintlayout.widget.ConstraintLayout mTopLayout;
    private ConstraintLayout mWebViewContainer;

    /*View Objects*/
    private AnimatedProgressBar mProgressBar;
    private ConstraintLayout mSplashScreen;
    private editTextManager mSearchbar;
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
    private View mSearchEngineBar;
    private EditText mFindText;
    private TextView mFindCount;
    private ImageButton mVoiceInput;
    private ImageButton mMenu;
    private NestedScrollView mNestedScroll;
    private ImageView mBlockerFullSceen;
    private TextView mCopyright;
    private RecyclerView mHintListView;
    private ImageView mSearchLock;
    private ImageButton mOrbotLogManager;
    private ConstraintLayout mInfoPortrait;
    private ConstraintLayout mInfoLandscape;
    private com.google.android.material.appbar.AppBarLayout mAppBar;

    /*Redirection Objects*/
    private GeckoResult<Bitmap> mRenderedBitmap = null;
    private boolean mPageClosed = false;
    private boolean isKeyboardOpened = false;
    private boolean isSuggestionChanged = false;
    private boolean isSuggestionSearchOpened = false;
    private boolean isFocusChanging = false;
    private boolean mAppRestarted = false;
    private boolean mSearchBarLoading = false;
    private String mSearchBarPreviousText = strings.GENERIC_EMPTY_STR;

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
            onInitBooleans();
            orbotLocalConstants.mHomeIntent = getIntent();

            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            pluginController.getInstance().preInitialize(this);
            databaseController.getInstance().initialize(this);
            dataController.getInstance().initialize(this);
            onChangeTheme();
            status.initStatus();
            pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
            onInitTheme();
            trueTime.getInstance().initTime();

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
            onInitResume(false);
    }

    public void onInitBooleans(){
        mPageClosed = false;
        isKeyboardOpened = false;
        isSuggestionChanged = false;
        isSuggestionSearchOpened = false;
        isFocusChanging = false;
        mAppRestarted = false;
        mSearchBarLoading = false;
    }

    public void onInitResume(boolean pStatus){
        if(status.mThemeApplying){
            mSplashScreen.setAlpha(0);
            mSplashScreen.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mHomeViewController.initStatusBarColor(false);
            }else {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.c_text_v3));
            }
            mHomeViewController.splashScreenDisableInstant();
            onLoadTabOnResume();
            mSearchLock.setColorFilter(ContextCompat.getColor(this, R.color.c_lock_tint));
        }
        initSuggestionView(new ArrayList<>());
    }

    public void onLoadTabOnResume(){
        Object mTempModel = dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
        if(mTempModel!=null){
            tabRowModel model = (tabRowModel)mTempModel;
            if(!status.mThemeApplying){
                mHomeViewController.onUpdateSearchBar(model.getSession().getCurrentURL(), false, false);
            }
            onLoadTab(model.getSession(),false);
            onLoadURL(model.getSession().getCurrentURL());
        }else {
            onNewIntent(getIntent());
            onOpenLinkNewTab(helperMethod.getDomainName(mHomeModel.getSearchEngine()));
        }
        initTabCount();
        if(!status.mThemeApplying){
            mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(), false, false);
        }
        status.mThemeApplying = false;
    }

    public void onInitDefaultTab(){
        tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
        if(model!=null){
            if(!mGeckoClient.getSession().getSessionID().equals(model.getSession().getSessionID())){
                onLoadTab(model.getSession(),true);
            }
        }else {
            postNewTabAnimation(false, false);
        }
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
            if(!status.sDefaultNightMode){
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

    @SuppressLint("ClickableViewAccessibility")
    private void initSuggestionView(ArrayList<historyRowModel> pList){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        hintAdapter mAdapter = new hintAdapter(pList,new hintViewCallback(), this);
        layoutManager.setReverseLayout(true);

        mHintListView.setAdapter(mAdapter);
        mHintListView.setNestedScrollingEnabled(false);
        mHintListView.setHasFixedSize(true);
        mHintListView.setItemViewCacheSize(10);
        mHintListView.setDrawingCacheEnabled(true);
        mHintListView.setDrawingCacheEnabled(true);
        mHintListView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mHintListView.setLayoutManager(new LinearLayoutManager(this));
        Objects.requireNonNull(mHintListView.getItemAnimator()).setChangeDuration(0);

        findViewById(R.id.pSuggestionScroll).setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                helperMethod.hideKeyboard(this);
            }
            return false;
        });
    }

    public void onUpdateSuggestionList(ArrayList<historyRowModel> pList){
        ((hintAdapter) Objects.requireNonNull(mHintListView.getAdapter())).onUpdateAdapter(pList);
    }

    private void initLocalLanguage() {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this),pluginEnums.eLangManager.M_SET_LANGUAGE);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if(data!=null){
            status.sSettingIsAppRedirected = true;
            status.sSettingRedirectStatus = data.toString();
            if(status.sSettingIsAppStarted){
                mSplashScreen.setAlpha(0);
                mSplashScreen.setVisibility(View.GONE);
            }
            onStartApplication(null);
        }
    }

    public void initLandingPage(){
        if(status.sSettingFirstStart){
            helperMethod.openActivity(landingController.class, constants.CONST_LIST_HISTORY, homeController.this,false);
        }
    }

    public void onRedrawXML(){
        setContentView(R.layout.home_view);
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
        mNewTab = findViewById(R.id.pNewTab);
        mSearchEngineBar = findViewById(R.id.pSearchEngineBar);
        mFindText = findViewById(R.id.pFindText);
        mFindCount = findViewById(R.id.pFindCount);
        mVoiceInput = findViewById(R.id.pVoiceInput);
        mMenu = findViewById(R.id.pMenu);
        mBlocker = findViewById(R.id.pBlocker);
        mNestedScroll = findViewById(R.id.pNestedScroll);
        mBlockerFullSceen = findViewById(R.id.pBlockerFullSceen);
        mCopyright = findViewById(R.id.pCopyright);
        mHintListView = mSearchEngineBar.findViewById(R.id.pHistListView);
        mAppBar = findViewById(R.id.pAppbar);
        mSearchLock = findViewById(R.id.pSearchLock);
        mOrbotLogManager = findViewById(R.id.pOrbotLogManager);
        mFindBar = findViewById(R.id.pFindBar);
        mInfoPortrait = findViewById(R.id.pInfoPortrait);
        mInfoLandscape = findViewById(R.id.pInfoLandscape);

        mGeckoView.setSaveEnabled(false);
        mGeckoView.setSaveFromParentEnabled(false);
        mGeckoView.setAutofillEnabled(true);

        mGeckoClient = new geckoClients();
        mHomeViewController.initialization(new homeViewCallback(),this,mNewTab, mWebViewContainer, mLoadingText, mProgressBar, mSearchbar, mSplashScreen, mLoadingIcon, mBannerAds, mGatewaySplash, mTopBar, mGeckoView, mBackSplash, mConnectButton, mFindBar, mFindText, mFindCount, mTopLayout, mVoiceInput, mMenu, mNestedScroll, mBlocker, mBlockerFullSceen, mSearchEngineBar, mCopyright, mHintListView, mAppBar, mOrbotLogManager, mInfoLandscape, mInfoPortrait);
        mGeckoView.onSetHomeEvent(new nestedGeckoViewCallback());
        mGeckoClient.initialize(mGeckoView, new geckoViewCallback(), this,false);
        mGeckoClient.onValidateInitializeFromStartup();
        dataController.getInstance().initializeListData();
    }

    public void onUpdateStatusBarTheme(){
        mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getSession().getTheme(), false);
    }

    public void onUpdateToolbarTheme(){
        mHomeViewController.onUpdateToolbarTheme();
    }

    public void onChangeTheme(){
        if(!status.sSettingIsAppStarted){
            status.sDefaultNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(inSignatureArea(event)){
            try{
                mSwipeDirectionDetector.onTouchEvent(event);
            }catch (Exception ignored){ }
        }
        return super.dispatchTouchEvent(event);
    }

    public boolean inSignatureArea(MotionEvent ev) {
        float mEventY = ev.getY();
        return mEventY>mTopBar.getY()+mTopBar.getHeight() && mEventY<mConnectButton.getY();
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
        postNewTabAnimation(isKeyboardOpened, false);
        mHomeViewController.initTab((int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null));
    }

    @Override
    protected void attachBaseContext(Context base) {
        Prefs.setContext(base);
        orbotLocalConstants.mHomeContext = new WeakReference<>(base);
        super.attachBaseContext(LocaleHelper.onAttach(base, Prefs.getDefaultLocale()));
    }

    /*-------------------------------------------------------Helper Methods-------------------------------------------------------*/

    public void onGetFavIcon(ImageView pImageView, String pURL){
    }

    public void onGetThumbnail(ImageView pImageView){
        mRenderedBitmap = mGeckoView.capturePixels();
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(mGeckoClient.getSession().getSessionID(), mRenderedBitmap, pImageView, mGeckoView));
    }


    public void onLoadFont(){
        mGeckoClient.onUpdateFont();
        mHomeViewController.onReDraw();
    }

    public void onLoadProxy(View view){
        if(pluginController.getInstance().isInitialized() && !mPageClosed){
            helperMethod.openActivity(orbotController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
        }
    }


    public void initRuntimeSettings()
    {
        mGeckoClient.updateSetting();
    }

    public void onReDrawGeckoview(){
        mGeckoClient.getSession().closeSession();
        mGeckoClient.getSession().close();
        mGeckoClient.initialize(mGeckoView, new geckoViewCallback(), this,false);
    }

    public void onLoadURL(String url){
        mAppBar.animate().cancel();
        mHomeViewController.onClearSelections(true);
        mGeckoView.getSession().stop();
        mGeckoClient.loadURL(url.replace("genesis.onion","boogle.store"));
    }

    public void onLoadTab(geckoSession mTempSession, boolean isSessionClosed){
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(mGeckoClient.getSession().getSessionID(), mRenderedBitmap, null, mGeckoView));

        if(!isSessionClosed){
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.MOVE_TAB_TO_TOP, Collections.singletonList(mTempSession));
        }

        mGeckoClient.initSession(mTempSession);
        mGeckoView.releaseSession();
        mGeckoView.setSession(mTempSession);

        mHomeViewController.onClearSelections(false);
        mHomeViewController.onUpdateSearchBar(mTempSession.getCurrentURL(),false,true);
        if(mTempSession.getProgress()>0 && mTempSession.getProgress()<100){
            mHomeViewController.onProgressBarUpdate(mTempSession.getProgress());
        }else {
            mHomeViewController.progressBarReset();
        }

        mGeckoClient.onValidateInitializeFromStartup();
        mGeckoClient.onSessionReinit();
        mHomeViewController.onUpdateStatusBarTheme(mTempSession.getTheme(), false);
        mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(), false, false);
        mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getSession().getTheme(),true);
        mAppBar.setExpanded(true,true);
        mRenderedBitmap = mGeckoView.capturePixels();
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

    @SuppressLint("NewApi")
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        switch (level) {
            case TRIM_MEMORY_BACKGROUND:
                Log.i("wow : ", "trim memory requested: app in the background");
                break;

            case TRIM_MEMORY_COMPLETE:
                Log.i("wow : ", "trim memory requested: cleanup all memory");
                break;

            case TRIM_MEMORY_MODERATE:
                Log.i("wow : ", "trim memory requested: clean up some memory");
                break;

            case TRIM_MEMORY_RUNNING_CRITICAL:
                Log.i("wow : ", "trim memory requested: memory on device is very low and critical");
                break;

            case TRIM_MEMORY_RUNNING_LOW:
                Log.i("wow : ", "trim memory requested: memory on device is running low");
                break;

            case TRIM_MEMORY_RUNNING_MODERATE:
                Log.i("wow : ", "trim memory requested: memory on device is moderate");
                break;

            case TRIM_MEMORY_UI_HIDDEN:
                Log.i("wow : ", "trim memory requested: app is not showing UI anymore");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(!status.sSettingIsAppStarted){
            Intent intent = new Intent(getApplicationContext(), OrbotService.class);
            stopService(intent);
        }
        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeLocalEventHandlers() {

        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

        registerReceiver(downloadStatus,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        mNewTab.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                onOpenTabViewBoundary(null);
            }
            return false;
        });


        mFindText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                if(mSearchbar.isFocused()){
                    if(mFindText.getText().length()==0 && mGeckoClient!=null){
                            mGeckoClient.getSession().getFinder().clear();
                            mHomeViewController.onUpdateFindBarCount(0,0);
                        }else {
                        assert mGeckoClient != null;
                        mGeckoClient.getSession().findInPage(mFindText.getText().toString(), GeckoSession.FINDER_FIND_MATCH_CASE & GeckoSession.FINDER_DISPLAY_HIGHLIGHT_ALL);
                    }
                }
            }
        });

        mSearchbar.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE)
            {
                onSearchBarInvoked(v);
                mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),true,true);
                mHomeViewController.onClearSelections(true);
                mGeckoClient.setLoading(true);
                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
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

        mGeckoView.setOnTouchListener((v, event) -> {
            helperMethod.hideKeyboard(homeController.this);
            return false;
        });

        mGeckoView.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus)
            {
                pluginController.getInstance().onMessageManagerInvoke(null, M_RESET);
                mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false,true);

                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }, 300);

            }else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            }
        });


        mSearchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if(status.sSearchSuggestionStatus && isSuggestionChanged){
                    mSuggestions = (ArrayList<historyRowModel>)dataController.getInstance().invokeSuggestions(dataEnums.eSuggestionCommands.M_GET_SUGGESTIONS, Collections.singletonList(mSearchbar.getText().toString()));
                    if(mSuggestions.size()>0){
                        if(!isSuggestionSearchOpened && mSearchbar.isFocused()){
                            if(Objects.requireNonNull(mHintListView.getAdapter()).getItemCount()>0){
                                mHomeViewController.onUpdateSearchEngineBar(true, 0);
                            }
                        }
                        if(mHintListView.getAdapter()==null){
                            initSuggestionView(mSuggestions);
                            mSearchEngineBar.setVisibility(View.VISIBLE);
                            mSearchEngineBar.setAlpha(1);
                        }else {
                            mSearchEngineBar.setVisibility(View.VISIBLE);
                            mSearchEngineBar.setAlpha(1);
                            mEdittextChanged.removeCallbacks(postToServerRunnable);
                            if(!mSearchBarLoading){
                                mSearchBarLoading = true;
                                mEdittextChanged.postDelayed(postToServerRunnable, 0);
                            }else{
                                mEdittextChanged.postDelayed(postToServerRunnable, 300);
                            }
                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
            }
        });

        mSearchbar.setEventHandler(new edittextManagerCallback());

        mSearchbar.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus)
            {
                if(!isSuggestionSearchOpened){
                    if(isSuggestionChanged){
                        isSuggestionChanged = false;
                        if(mHintListView!=null && mHintListView.getAdapter()!=null && mHintListView.getAdapter().getItemCount()>0){
                            mHomeViewController.onUpdateSearchEngineBar(false, 150);
                        }
                        mHomeViewController.initSearchBarFocus(false);
                        if(!mGeckoClient.isLoading()){
                            mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false,true);
                        }
                        helperMethod.hideKeyboard(homeController.this);
                    }
                }
            }else {
                if(!isFocusChanging){
                    if(!status.mThemeApplying){
                        mHomeViewController.initSearchBarFocus(true);
                    }
                    isSuggestionChanged = true;
                    isSuggestionSearchOpened = false;
                }
            }
        });

        mSwipeDirectionDetector=new GestureDetector(this,new SimpleGestureFilter(){

            @Override
            public boolean onSwipe(Direction direction) {
                if(mSplashScreen.getAlpha()>=1 && mSplashScreen.getVisibility()==View.VISIBLE){
                    if (direction==Direction.left){
                        helperMethod.openActivity(orbotLogController.class, constants.CONST_LIST_HISTORY, homeController.this, true);
                    }
                    else if (direction==Direction.right){
                        helperMethod.openActivity(orbotController.class, constants.CONST_LIST_HISTORY, homeController.this, true);
                    }
                }
                return true;
            }
        });

        KeyboardUtils.addKeyboardToggleListener(this, isVisible -> isKeyboardOpened = isVisible);
    }

    private ArrayList<historyRowModel> mSuggestions;
    private Handler mEdittextChanged = new Handler();
    private Runnable postToServerRunnable = () -> {
        onUpdateSuggestionList(mSuggestions);
        final Handler handler = new Handler();
        handler.postDelayed(() -> mSearchBarLoading = false, 110);
    };

    public void onSearchBarInvoked(View view){
        String url = ((EditText)view).getText().toString();
        String validated_url = mHomeModel.urlComplete(url, mHomeModel.getSearchEngine());
        if(validated_url!=null){
            url = validated_url;
        }
        mHomeViewController.onUpdateSearchBar(url,false,true);
        onLoadURL(url);
    }

    public void onSuggestionInvoked(View view){
        String mVal = ((TextView)view.findViewById(R.id.pURL)).getText().toString();
        if(mVal.equals(strings.GENERIC_EMPTY_STR)){
            mVal = ((TextView)view.findViewById(R.id.pHeaderSingle)).getText().toString();
        }
        String pURL = mHomeModel.urlComplete(mVal, status.sSettingSearchStatus);
        if(pURL==null){
            pURL = mVal;
        }

        onLoadURL(pURL);
        mHomeViewController.onUpdateSearchBar(pURL,false,true);
    }

    public void onSuggestionMove(View view){
        String val = view.getTag().toString();
        mHomeViewController.onUpdateSearchBar(val,false,false);
    }

    public void applyTheme(){
        status.mCurrentReloadURL = mSearchbar.getText().toString();
        onResume();
        recreate();
    }

    public void onHomeButton(View view){
        onLoadURL(helperMethod.getDomainName(mHomeModel.getSearchEngine()));
        mHomeViewController.onUpdateLogo();
    }

    public geckoSession onNewTabInit(){
        return mGeckoClient.initFreeSession(mGeckoView, this, new geckoViewCallback());
    }

    public void postNewTabAnimation(boolean isKeyboardOpenedTemp, boolean isKeyboardOpened){
        initializeGeckoView(true, true);
        if(status.sOpenURLInNewTab){
            onLoadURL(helperMethod.getDomainName(status.sSettingSearchStatus));
            mHomeViewController. onUpdateSearchBar(helperMethod.getDomainName(status.sSettingSearchStatus),false,true);
        }else {
            onLoadURL("about:blank");
            mHomeViewController. onUpdateSearchBar(strings.HOME_BLANK_PAGE,false,true);
            mHomeViewController.onNewTab();
        }
        mHomeViewController.progressBarReset();
        mHomeViewController.onSessionChanged();
        mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getSession().getTheme(), false);
    }

    public void postNewLinkTabAnimation(String url){
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(mGeckoClient.getSession().getSessionID(), mRenderedBitmap, null, mGeckoView));
        initializeGeckoView(true, true);
        mHomeViewController.progressBarReset();
        mHomeViewController.onUpdateSearchBar(url,false,true);
        mGeckoClient.loadURL(url);
    }


    public void onNewTab(boolean isKeyboardOpenedTemp, boolean isKeyboardOpened){
        try {
            onGetThumbnail(null);
        }catch (Exception ignored){}

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            mHomeViewController.onNewTabAnimation(Arrays.asList(isKeyboardOpenedTemp, isKeyboardOpened), M_INITIALIZE_TAB_SINGLE);
        }, 100);
    }

    public void onOpenLinkNewTab(String url){
        onGetThumbnail(null);

        final Handler handler = new Handler();
        handler.postDelayed(() -> mHomeViewController.onNewTabAnimation(Collections.singletonList(url),M_INITIALIZE_TAB_LINK), 100);
    }

    public void onOpenTabViewBoundary(View view){
        onGetThumbnail(null);
        mGeckoClient.onRedrawPixel();
        mNewTab.setPressed(true);
        helperMethod.openActivity(tabController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
        overridePendingTransition(R.anim.popup_anim_in, R.anim.popup_anim_out);
    }

    public void onLockSecure(View view){
        pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(mGeckoClient.getSession().getCurrentURL(), status.sSettingJavaStatus, status.sStatusDoNotTrack, status.sSettingTrackingProtection, status.sSettingCookieStatus, this), M_SECURE_CONNECTION);
    }

    public void onNotificationInvoked(String message,enums.etype e_type){
        mHomeViewController.downloadNotification(message,e_type);
    }

    public void onOpenMenuItem(View view){
        pluginController.getInstance().onMessageManagerInvoke(null, M_RESET);
        initLocalLanguage();
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);

        helperMethod.hideKeyboard(this);
        mHomeViewController.onOpenMenu(view,mGeckoClient.canGoBack(),!(mProgressBar.getAlpha()<=0 || mProgressBar.getVisibility() ==View.INVISIBLE),mGeckoClient.getUserAgent());
    }

    public void onFullScreenSettingChanged(){
        mHomeViewController.initTopBarPadding();
    }

    @Override
    public void onBackPressed(){

        mSearchbar.clearFocus();
        if(mFindBar!=null && mFindBar.getVisibility() == View.VISIBLE){
            mHomeViewController.onUpdateFindBar(false);
        }
        else if(mSearchEngineBar.getVisibility() == View.VISIBLE){
            mHomeViewController.onUpdateSearchEngineBar(false, 150);
        }
        else if(!mGeckoClient.getFullScreenStatus()){
            mGeckoClient.onExitFullScreen();
            mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getSession().getTheme(), true);
        }
        else if((int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null)>0){
            mGeckoClient.onBackPressed(true);
        }
        else {
            mGeckoClient.onBackPressed(true);
        }
    }

    /*Activity States*/

    public void onReload(View view){
        mHomeViewController.onUpdateLogo();
    }

    public void onClearSession(){
        onHomeButton(null);
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

        pluginController.getInstance().onMessageManagerInvoke(null, M_RESET);
        mHomeViewController.closeMenu();

            final Handler handler = new Handler();
            handler.postDelayed(() -> mGeckoClient.onRedrawPixel(), 300);
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
                    mHomeViewController.onSetBannerAdMargin(true,(boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
                }
            }
            if(mSplashScreen.getAlpha()>0){
                mHomeViewController.initSplashOrientation();
            }
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mHomeViewController!=null){
            mHomeViewController.closeMenu();
            helperMethod.hideKeyboard(this);
        }

        mGeckoClient.onExitFullScreen();
        mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getSession().getTheme(), true);
        pluginController.getInstance().onMessageManagerInvoke(null, M_RESET);
        pluginController.getInstance().onNotificationInvoke(Collections.singletonList(1296000000) /* Every 15 Days */ , pluginEnums.eNotificationManager.M_CREATE_NOTIFICATION);
    }

    @Override
    public void onResume()
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        if (mGeckoClient.getSession()!=null && mGeckoClient!=null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mGeckoClient.getUriPermission()!=null) {
            this.revokeUriPermission(mGeckoClient.getUriPermission(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if(isSuggestionSearchOpened){
            isSuggestionChanged = true;
            isFocusChanging = false;
            isSuggestionSearchOpened = false;
            mSearchbar.requestFocus();
            mSearchbar.setText(helperMethod.urlDesigner(mSearchBarPreviousText, this, mSearchbar.getCurrentTextColor()));
            mSearchbar.selectAll();
            mHomeViewController.initSearchBarFocus(true);
        }
        if(status.sSettingIsAppStarted && mAppRestarted){
            activityContextManager.getInstance().onClearStack();
            tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
            if(model==null || !mGeckoClient.getSession().getSessionID().equals(model.getSession().getSessionID())){
                if(model==null){
                    onInitDefaultTab();
                }else {
                    onLoadTab(model.getSession(), false);
                }
            }
        }
        if(mAppBar!=null){
            mAppBar.setExpanded(true,true);

            mAppBar.refreshDrawableState();
            mAppBar.invalidate();
        }
        mAppRestarted = true;
        pluginController.getInstance().onNotificationInvoke(null, pluginEnums.eNotificationManager.M_CLEAR_NOTIFICATION );

        if(mGeckoView!=null){
            tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
            if(model!=null){
                if(!mGeckoView.getSession().isOpen()){
                    onReDrawGeckoview();
                    onLoadURL(model.getSession().getCurrentURL());
                }else {
                    mGeckoView.releaseSession();
                    mGeckoView.requestFocus();
                    mGeckoView.setSession(model.getSession());
                }
            }
        }

        super.onResume();
    }

    public void OnClearSuggestion(View view){
        mSearchbar.clearFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==100){
            if(resultCode == RESULT_OK && null != data){
                mSearchbar.clearFocus();
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mSearchbar.setText(result.get(0).toLowerCase());
                helperMethod.hideKeyboard(homeController.this);
                mGeckoClient.setLoading(true);
                onSearchBarInvoked(mSearchbar);
                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
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
        mHomeViewController.onSetBannerAdMargin(true,(boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
    }

    public void onVoiceClick(View view) {
        if(status.sSettingEnableVoiceInput){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice2Text \n Say Something!!");
            try {
                startActivityForResult(intent, 100);
            } catch (ActivityNotFoundException ignored) {

            }
        }else {
            onSearchBarInvoked(mSearchbar);
        }
    }
    /*-------------------------------------------------------External Callback Methods-------------------------------------------------------*/

    public String onGetCurrentURL(){
        return mGeckoClient.getSession().getCurrentURL();
    }

    public void onStartApplication(View view){
        pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_START_ORBOT);
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
            String mLog = (String) pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_GET_LOGS);
            mHomeViewController.onUpdateLogs(mLog);
            return strings.GENERIC_EMPTY_STR;
        };

        mHomeViewController.initProxyLoading(callable);
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

    public void onClearSearchBar(View view){
        mHomeViewController.onUpdateSearchBar(strings.GENERIC_EMPTY_STR, false, true);
    }

    public void onFindNext(View view){
        mFindCount.setText("0/0");
        mGeckoClient.getSession().findInPage(mFindText.getText().toString(), GeckoSession.FINDER_FIND_MATCH_CASE & GeckoSession.FINDER_DISPLAY_HIGHLIGHT_ALL);
    }

    public void onOpenSearchEngine(View view){
        mSearchBarPreviousText = mSearchbar.getText().toString();
        isSuggestionSearchOpened = true;
        helperMethod.openActivity(settingSearchController.class,constants.CONST_LIST_HISTORY, homeController.this,true);
    }

    public void onFindPrev(View view){
        mFindCount.setText("0/0");
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
                mGeckoClient.onRedrawPixel();
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
                helperMethod.openActivity(settingHomeController.class,constants.CONST_LIST_HISTORY, homeController.this,true);
            }
            else if (menuId == R.id.pMenuDelete)
            {
                pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(this, mGeckoClient.getSession().getCurrentURL()), M_BOOKMARK);
            }
            else if (menuId == R.id.pMenuOpenNewTab)
            {
                helperMethod.hideKeyboard(this);
                helperMethod.openActivity(bookmarkController.class,constants.CONST_LIST_BOOKMARK, homeController.this,true);
            }
            else if (menuId == R.id.pMenuOpenCurrentTab)
            {
                helperMethod.hideKeyboard(this);
                pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_REPORT_URL);
            }
            else if (menuId == R.id.pMenuQuit)
            {
                status.sSettingIsAppStarted = false;
                finishAndRemoveTask();

                new Thread(){
                    public void run(){
                        try {
                            sleep(1000);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
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
                pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(mGeckoClient.getSession().getCurrentURL(), this), M_BOOKMARK);
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

    public void onReInitTheme(){
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
           else if(e_type.equals(enums.etype.ON_UPDATE_THEME)){
               mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getSession().getTheme(),false);
           }
           else if(e_type.equals(M_INITIALIZE_TAB_SINGLE)){
               postNewTabAnimation((boolean)data.get(0),(boolean)data.get(1));
           }
           else if(e_type.equals(M_INITIALIZE_TAB_LINK)){
               postNewLinkTabAnimation((String)data.get(0));
           }
           else if(e_type.equals(enums.etype.on_init_ads))
           {
               mHomeViewController.onSetBannerAdMargin((boolean)data.get(0),(boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
           }
           else if(e_type.equals(enums.etype.M_WELCOME_MESSAGE)){
               if(status.sSettingIsWelcomeEnabled){
                   final Handler handler = new Handler();
                   Runnable runnable = () ->
                   {
                       pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(strings.GENERIC_EMPTY_STR, homeController.this), M_WELCOME);
                   };
                   handler.postDelayed(runnable, 1300);
               }
           }
           else if(e_type.equals(enums.etype.on_url_load)){
               if(status.sSettingIsAppRedirected){
                   mHomeViewController.onPageFinished();
                   mGeckoClient.onRedrawPixel();

                   status.sSettingIsAppRedirected = false;
                   onLoadURL(status.sSettingRedirectStatus);
                   status.sSettingRedirectStatus = strings.GENERIC_EMPTY_STR;
               }else {
                   if(status.mThemeApplying){
                       mHomeViewController.onUpdateSearchBar(data.get(0).toString(),false, false);
                       mHomeViewController.splashScreenDisableInstant();
                       onLoadTabOnResume();
                   }
                   onLoadURL(data.get(0).toString());
                   mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false,true);
               }
           }
           else if(e_type.equals(enums.etype.ON_LOAD_TAB_ON_RESUME)){
               if(status.sSettingIsAppRedirected){
                   status.sSettingIsAppRedirected = false;
                   onLoadURL(status.sSettingRedirectStatus);
                   status.sSettingRedirectStatus = strings.GENERIC_EMPTY_STR;
               }else {
                   if(status.mThemeApplying){
                       mHomeViewController.splashScreenDisableInstant();
                       onLoadTabOnResume();
                   }
                   else if(status.sSettingIsAppStarted){
                       mHomeViewController.onPageFinished();
                       mGeckoClient.onRedrawPixel();
                       mHomeViewController.onProgressBarUpdate(5);
                       onLoadTabOnResume();
                   }
               }
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

    public class edittextManagerCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {

            if(e_type.equals(enums.etype.ON_KEYBOARD_CLOSE)){
                isSuggestionChanged = false;
                isFocusChanging = true;
                mSearchbar.setSelection(0);
                mHomeViewController.onClearSelections(false);
                if(!isSuggestionSearchOpened){
                    if(mHintListView!=null && mHintListView.getAdapter()!=null && mHintListView.getAdapter().getItemCount()>0){
                        mHomeViewController.onUpdateSearchEngineBar(false, 150);
                    }
                    mHomeViewController.initSearchBarFocus(false);
                    if(!mGeckoClient.isLoading()){
                        mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false,true);
                    }
                    helperMethod.hideKeyboard(homeController.this);
                }
                isFocusChanging = false;
            }
            return null;
        }
    }


    public class hintViewCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            if(e_type.equals(enums.etype.fetch_favicon)){
                mGeckoClient.onGetFavIcon((ImageView) data.get(0), (String) data.get(1));
            }
            return null;
        }
    }

    public class geckoViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if(e_type.equals(enums.etype.ON_EXPAND_TOP_BAR)){
                mAppBar.setExpanded(true,true);
            }
            else if(e_type.equals(enums.etype.progress_update)){
                mHomeViewController.onProgressBarUpdate((int)data.get(0));
            }
            else if(e_type.equals(enums.etype.ON_UPDATE_SEARCH_BAR)){
                mHomeViewController.onUpdateSearchBar((String)data.get(0), false, false);
            }
            else if(e_type.equals(enums.etype.ON_FIRST_PAINT)){
                mHomeViewController.onFirstPaint();
            }
            else if(e_type.equals(enums.etype.ON_SESSION_REINIT)){
                mHomeViewController.onSessionReinit();
            }
            else if(e_type.equals(enums.etype.on_url_load)){
                //mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false,true);
            }
            else if(e_type.equals(enums.etype.back_list_empty)){
                if((int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null)>1){
                    if(!onCloseCurrentTab(mGeckoClient.getSession())){
                        postNewTabAnimation(true,false);
                    }
                }else {
                    helperMethod.onMinimizeApp(homeController.this);
                }
            }
            else if(e_type.equals(enums.etype.ON_UPDATE_THEME)){
                mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getSession().getTheme(),false);
            }
            else if(e_type.equals(enums.etype.start_proxy)){
                pluginController.getInstance().onOrbotInvoke(data, pluginEnums.eOrbotManager.M_SET_PROXY);
            }
            else if(e_type.equals(enums.etype.on_update_history)){
                return dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_ADD_HISTORY ,data);
            }
            else if(e_type.equals(enums.etype.on_page_loaded)){
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_IS_BOOTSTRAPPED,true));
                mHomeViewController.onPageFinished();
                mGeckoClient.onRedrawPixel();

                final Handler handler = new Handler();
                Runnable runnable = () -> pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_INITIALIZE_BANNER_ADS);
                handler.postDelayed(runnable, 2000);
            }
            else if(e_type.equals(M_RATE_APPLICATION)){
                if(!status.sSettingIsAppRated){
                    dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,true));
                    status.sSettingIsAppRated = true;
                    pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(strings.GENERIC_EMPTY_STR, homeController.this), M_RATE_APP);
                }
            }
            else if(e_type.equals(enums.etype.on_load_error)){
                pluginController.getInstance().onLanguageInvoke(Collections.singletonList(homeController.this), pluginEnums.eLangManager.M_SET_LANGUAGE);
                initLocalLanguage();
                mHomeViewController.onPageFinished();
                mGeckoClient.onRedrawPixel();
                mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false,true);
            }
            else if(e_type.equals(enums.etype.search_update)){
                mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false, true);
            }
            else if(e_type.equals(enums.etype.download_file_popup)){
                pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(dataToStr(data.get(0)), homeController.this), M_DOWNLOAD_FILE);
            }
            else if(e_type.equals(enums.etype.on_full_screen)){
                boolean status = (Boolean)data.get(0);
                mHomeViewController.onFullScreenUpdate(status);
                mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getSession().getTheme(),true);
            }
            else if(e_type.equals(enums.etype.on_update_favicon)){
                dataController.getInstance().invokeImage(dataEnums.eImageCommands.M_REQUEST_IMAGE_URL,Collections.singletonList((String)data.get(0)));
            }
            else if(e_type.equals(M_LONG_PRESS_WITH_LINK)){
                pluginController.getInstance().onMessageManagerInvoke(data, M_LONG_PRESS_WITH_LINK);
            }
            else if(e_type.equals(enums.etype.on_long_press)){
                pluginController.getInstance().onMessageManagerInvoke(data, M_LONG_PRESS_DOWNLOAD);
            }
            else if(e_type.equals(M_LONG_PRESS_URL)){
                pluginController.getInstance().onMessageManagerInvoke(data, M_LONG_PRESS_URL);
            }
            else if(e_type.equals(enums.etype.open_new_tab)){
                onOpenLinkNewTab(dataToStr(data.get(0)));
            }
            else if(e_type.equals(enums.etype.on_close_sesson)){
                if(!onCloseCurrentTab(mGeckoClient.getSession())){
                    postNewTabAnimation(true,false);
                }
            }
            else if(e_type.equals(enums.etype.on_playstore_load)){
                helperMethod.openPlayStore(dataToStr(data.get(0)).split("__")[1],homeController.this);
            }
            else if(e_type.equals(enums.etype.ON_UPDATE_TAB_TITLE)){
                if(activityContextManager.getInstance().getTabController()!=null && !activityContextManager.getInstance().getTabController().isDestroyed())
                activityContextManager.getInstance().getTabController().onTabRowChanged((String) data.get(1));
            }
            else if(e_type.equals(enums.etype.FINDER_RESULT_CALLBACK)){
                mHomeViewController.onUpdateFindBarCount((int)data.get(0),(int)data.get(1));
            }

            return null;
        }
    }
}

