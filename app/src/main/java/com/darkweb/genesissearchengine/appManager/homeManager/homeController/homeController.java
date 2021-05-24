package com.darkweb.genesissearchengine.appManager.homeManager.homeController;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.dataManager.models.historyRowModel;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.NestedGeckoView;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.*;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.homeManager.hintManager.hintAdapter;
import com.darkweb.genesissearchengine.appManager.landingManager.landingController;
import com.darkweb.genesissearchengine.appManager.languageManager.languageController;
import com.darkweb.genesissearchengine.appManager.orbotLogManager.orbotLogController;
import com.darkweb.genesissearchengine.appManager.orbotManager.orbotController;
import com.darkweb.genesissearchengine.appManager.settingManager.searchEngineManager.settingSearchController;
import com.darkweb.genesissearchengine.appManager.settingManager.settingHomeManager.settingHomeController;
import com.darkweb.genesissearchengine.dataManager.models.tabRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.libs.views.KeyboardUtils;
import com.darkweb.genesissearchengine.appManager.activityStateManager;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.appManager.activityThemeManager;
import com.darkweb.genesissearchengine.libs.trueTime.trueTime;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.google.android.gms.ads.AdView;
import org.mozilla.geckoview.ContentBlocking;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import org.torproject.android.proxy.OrbotService;
import org.torproject.android.proxy.util.Prefs;
import org.torproject.android.proxy.wrapper.LocaleHelper;
import org.torproject.android.proxy.wrapper.orbotLocalConstants;

import java.io.StringWriter;
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
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_HELP_URL_CACHE;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_HELP_URL_CACHE_DARK;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_URL_CACHED;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_URL_CACHED_DARK;
import static com.darkweb.genesissearchengine.constants.enums.etype.GECKO_SCROLL_DOWN;
import static com.darkweb.genesissearchengine.constants.enums.etype.M_INITIALIZE_TAB_LINK;
import static com.darkweb.genesissearchengine.constants.enums.etype.M_INITIALIZE_TAB_SINGLE;
import static com.darkweb.genesissearchengine.constants.enums.etype.M_NEW_LINK_IN_NEW_TAB;
import static com.darkweb.genesissearchengine.constants.enums.etype.open_new_tab;
import static com.darkweb.genesissearchengine.constants.sql.SQL_CLEAR_HISTORY;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManager.*;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManagerCallbacks.M_RATE_APPLICATION;
import static java.lang.Character.isLetter;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

public class homeController extends AppCompatActivity implements ComponentCallbacks2
{
    /*Model Declaration*/
    private homeViewController mHomeViewController;
    private homeModel mHomeModel;
    private geckoClients mGeckoClient = null;

    /*View Webviews*/
    private NestedGeckoView mGeckoView = null;
    private androidx.constraintlayout.widget.ConstraintLayout mTopLayout;
    private ConstraintLayout mWebViewContainer;

    /*View Objects*/
    private ProgressBar mProgressBar;
    private ConstraintLayout mSplashScreen;
    private editTextManager mSearchbar;
    private ImageView mLoadingIcon;
    private ImageView mBlocker;
    private TextView mLoadingText;
    private AdView mBannerAds = null;
    private ImageButton mGatewaySplash;
    private ImageButton mPanicButton;
    private ImageButton mPanicButtonLandscape;
    private LinearLayout mTopBar;
    private ImageView mBackSplash;
    private Button mConnectButton;
    private Button mNewTab;
    private View mFindBar;
    private View mSearchEngineBar;
    private ImageView mGenesisLogo;
    private EditText mFindText;
    private TextView mFindCount;
    private ImageButton mVoiceInput;
    private ImageButton mMenu;
    private NestedScrollView mNestedScroll;
    private ImageView mBlockerFullSceen;
    private ImageView mNewTabBlocker;
    private TextView mCopyright;
    private RecyclerView mHintListView = null;
    private ImageView mSearchLock;
    private ImageButton mOrbotLogManager;
    private ConstraintLayout mInfoPortrait;
    private ConstraintLayout mInfoLandscape;
    private com.google.android.material.appbar.AppBarLayout mAppBar;
    private ProgressBar mProgressBarIndeterminate;
    private FragmentContainerView mTabFragment;
    private LinearLayout mTopBarContainer;
    private View mPopupLoadNewTab;
    private ImageView mTopBarHider;
    private CoordinatorLayout mCoordinatorLayout;
    private ImageView mImageDivider;

    /*Redirection Objects*/
    private GeckoResult<Bitmap> mRenderedBitmap = null;
    private boolean mPageClosed = false;
    private boolean isKeyboardOpened = false;
    private boolean isSuggestionChanged = false;
    private boolean isSuggestionSearchOpened = false;
    private boolean isFocusChanging = false;
    private boolean mAppRestarted = false;
    private boolean mSearchBarLoading = false;
    private boolean mSearchBarLoadingOpening = false;
    private boolean mSearchBarWasBackButtonPressed = false;
    private String mSearchBarPreviousText = strings.GENERIC_EMPTY_STR;
    private Handler mScrollHandler = null;
    private Runnable mScrollRunnable = null;

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
            onInitTheme();
            onInitBooleans();
            orbotLocalConstants.mHomeIntent = getIntent();

            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            pluginController.getInstance().preInitialize(this);
            dataController.getInstance().initialize(this);
            status.initStatus(this);
            dataController.getInstance().invokeSQLCipher(dataEnums.eSqlCipherCommands.M_INIT, Collections.singletonList(this));
            trueTime.getInstance().initTime();

            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_view);

            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                status.sSettingIsAppStarted = false;
                finishAndRemoveTask();

                Intent intent = new Intent(this, homeController.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("crash",true);
                this.startActivity(intent);
                this.finish();

                Runtime.getRuntime().exit(0);
            });

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
            initSuggestions();
            initAdmob();
            initWidget();
            initSuggestionView(new ArrayList<>(), strings.GENERIC_EMPTY_STR);
            status.sSettingIsAppRunning = true;
            org.torproject.android.service.util.Prefs.setContext(activityContextManager.getInstance().getHomeController());
    }


    public void initWidget(){
        if(status.sWidgetResponse == enums.WidgetResponse.SEARCHBAR){
            if(!status.sSettingIsAppStarted){
                if(mSplashScreen.getAlpha()==1 && mConnectButton.isEnabled()){
                    onStartApplication(null);
                }
            }else {
                mHomeViewController.initSearchBarFocus(false, isKeyboardOpened);
                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
                    if(mSearchEngineBar.getVisibility() != View.VISIBLE){
                        mHomeViewController.initSearchBarFocus(true, isKeyboardOpened);
                        status.sWidgetResponse = enums.WidgetResponse.NONE;
                    }
                }, 500);
            }
        }
        else if(status.sWidgetResponse == enums.WidgetResponse.VOICE){
            if(!status.sSettingIsAppStarted){
                if(mSplashScreen.getAlpha()==1 && mConnectButton.isEnabled()){
                    onStartApplication(null);
                }
            }else {
                if(mSearchEngineBar.getVisibility() != View.VISIBLE){
                    onVoiceClick(null);
                    status.sWidgetResponse = enums.WidgetResponse.NONE;
                }
            }
        }
    }

    public void initAdmob(){
        pluginController.getInstance().onAdsInvoke(Collections.singletonList(this), pluginEnums.eAdManager.M_INITIALIZE_BANNER_ADS);
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

    public void initSuggestions(){
        mSuggestions = (ArrayList<historyRowModel>)dataController.getInstance().invokeSuggestions(dataEnums.eSuggestionCommands.M_GET_SUGGESTIONS, Collections.singletonList(mSearchbar.getText().toString()));
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
        initSuggestionView(new ArrayList<>(), strings.GENERIC_EMPTY_STR);
    }


    public void onLoadTabFromTabController(){

            Object mTempModel = dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
            if(mTempModel!=null){
                tabRowModel model = (tabRowModel)mTempModel;
                if(model.getSession().onGetInitializeFromStartup()){
                    mGeckoClient.getmRuntime();
                    mGeckoClient.initSession(model.getSession());
                    mGeckoView.releaseSession();
                    mGeckoView.setSession(model.getSession());

                    if(!model.getSession().isOpen()){
                        model.getSession().open(mGeckoClient.getmRuntime());
                        onLoadURL(model.getSession().getCurrentURL());
                    }

                    mGeckoClient.onValidateInitializeFromStartup(mGeckoView, homeController.this);
                    mGeckoClient.onSessionReinit();
                }

                if(mGeckoClient.getSession().getCurrentURL().equals("about:blank") || mGeckoClient.getSession().getCurrentURL().contains("genesishiddentechnologies.com") || mGeckoClient.wasPreviousErrorPage() || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED_DARK) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)){
                    mHomeViewController.updateBannerAdvertStatus(false, (boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
                    if(model.getSession().onGetInitializeFromStartup()) {
                        mHomeViewController.progressBarReset();
                    }
                }else {
                    mHomeViewController.updateBannerAdvertStatus(true, (boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
                }
                mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false,false,false);
            }else {
                if(status.sSettingSearchStatus.equals(constants.CONST_BACKEND_GENESIS_URL)){
                    mHomeViewController.updateBannerAdvertStatus(false, (boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
                }
            }
    }

    public void onLoadTabOnResume(){
        Object mTempModel = dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
        if(mTempModel!=null){
            tabRowModel model = (tabRowModel)mTempModel;
            if(!status.mThemeApplying){
                mHomeViewController.onUpdateSearchBar(model.getSession().getCurrentURL(), false, false, false);
            }
            onLoadTab(model.getSession(),false,true);
        }else {
            onNewIntent(getIntent());
            onOpenLinkNewTab(helperMethod.getDomainName(mHomeModel.getSearchEngine()));
        }
        initTabCountForced();
        if(!status.mThemeApplying){
            mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(), false, false, false);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initSuggestionView(ArrayList<historyRowModel> pList, String pSearch){
        if(mHintListView.getAdapter() == null){
            hintAdapter mAdapter = new hintAdapter(pList,new hintViewCallback(), this, pSearch);
            mHintListView.setAdapter(mAdapter);
            mHintListView.setLayoutManager(new LinearLayoutManager(this));
            mHintListView.setHasFixedSize(true);
            mHintListView.setItemViewCacheSize(10);
            mHintListView.setItemViewCacheSize(10);
            mHintListView.setDrawingCacheEnabled(true);
            mHintListView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            Objects.requireNonNull(mHintListView.getItemAnimator()).setChangeDuration(0);
            Objects.requireNonNull(mHintListView.getItemAnimator()).setAddDuration(0);

            findViewById(R.id.pSuggestionScroll).setOnTouchListener((v, event) -> {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    helperMethod.hideKeyboard(this);
                }
                return false;
            });
        }else {
            onUpdateSuggestionList(mSuggestions);
        }
    }

    public void onUpdateSuggestionList(ArrayList<historyRowModel> pList){
        ((hintAdapter) Objects.requireNonNull(mHintListView.getAdapter())).onUpdateAdapter(pList, mSearchbar.getText().toString());
    }

    private void initLocalLanguage() {
        pluginController.getInstance().onLanguageInvoke(Arrays.asList(this, status.sSettingLanguage, status.sSettingLanguageRegion, status.mThemeApplying),pluginEnums.eLangManager.M_SET_LANGUAGE);
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

    public void initializeAppModel()
    {
        mHomeViewController = new homeViewController();
        mHomeModel = new homeModel();
    }

    public void initializeConnections()
    {
        mGeckoView = findViewById(R.id.pWebView);
        mTopBarContainer = findViewById(R.id.pTopBarContainer);
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
        mSearchLock = findViewById(R.id.pSearchLogo);
        mOrbotLogManager = findViewById(R.id.pOrbotLogManager);
        mFindBar = findViewById(R.id.pFindBar);
        mInfoPortrait = findViewById(R.id.pInfoPortrait);
        mInfoLandscape = findViewById(R.id.pInfoLandscape);
        mProgressBarIndeterminate = findViewById(R.id.pProgressBarIndeterminate);
        mTabFragment = findViewById(R.id.mTabFragment);
        mPopupLoadNewTab = findViewById(R.id.pPopupLoadNewTab);
        mTopBarHider = findViewById(R.id.pTopBarHider);
        mNewTabBlocker = findViewById(R.id.pNewTabBlocker);
        mCoordinatorLayout = findViewById(R.id.pCoordinatorLayout);
        mImageDivider = findViewById(R.id.pImageDivider);
        mPanicButton = findViewById(R.id.pPanicButton);
        mPanicButtonLandscape = findViewById(R.id.pPanicButtonLandscape);
        mGenesisLogo = findViewById(R.id.pGenesisLogo);

        mGeckoView.setSaveEnabled(false);
        mGeckoView.setSaveFromParentEnabled(false);
        mGeckoView.setAutofillEnabled(true);

        mGeckoClient = new geckoClients();
        mHomeViewController.initialization(new homeViewCallback(),this,mNewTab, mWebViewContainer, mLoadingText, mProgressBar, mSearchbar, mSplashScreen, mLoadingIcon, mBannerAds, mGatewaySplash, mTopBar, mGeckoView, mBackSplash, mConnectButton, mFindBar, mFindText, mFindCount, mTopLayout, mVoiceInput, mMenu, mNestedScroll, mBlocker, mBlockerFullSceen, mSearchEngineBar, mCopyright, mHintListView, mAppBar, mOrbotLogManager, mInfoLandscape, mInfoPortrait, mProgressBarIndeterminate, mTabFragment, mTopBarContainer, mSearchLock, mPopupLoadNewTab, mTopBarHider, mNewTabBlocker, mCoordinatorLayout, mImageDivider, mPanicButton, mGenesisLogo, mPanicButtonLandscape);
        mGeckoView.onSetHomeEvent(new nestedGeckoViewCallback());
        mGeckoClient.initialize(mGeckoView, new geckoViewCallback(), this,false);
        mGeckoClient.onValidateInitializeFromStartup(mGeckoView, homeController.this);
        dataController.getInstance().initializeListData();
    }

    public void onUpdateStatusBarTheme(){
        mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(), false);
    }

    public boolean inSignatureArea(MotionEvent ev) {
        float mEventY = ev.getY();
        return mEventY>mTopBar.getY()+mTopBar.getHeight() && mEventY<mConnectButton.getY();
    }

    public void initPreFixes() {
        try {
            if(getIntent().getBooleanExtra("crash", false)){
                new Handler().postDelayed(() ->
                {
                    pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(homeController.this), M_APPLICATION_CRASH);
                }, 2000);
            }

            if(!status.mThemeApplying){
                orbotLocalConstants.mTorLogsStatus = strings.GENERIC_EMPTY_STR;
            }

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
        initTabCountForced();
    }

    public void initTab(boolean isKeyboardOpened){
        postNewTabAnimation(isKeyboardOpened, false);
        initTabCountForced();
    }

    @Override
    protected void attachBaseContext(Context base) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(base);
        status.sTheme = mPrefs.getInt(keys.SETTING_THEME,enums.Theme.THEME_DEFAULT);
        Prefs.setContext(base);
        orbotLocalConstants.mHomeContext = new WeakReference<>(base);

        Context mContext = activityThemeManager.getInstance().initTheme(base);
        activityContextManager.getInstance().setApplicationContext(mContext.getApplicationContext());
        super.attachBaseContext(LocaleHelper.onAttach(mContext, Prefs.getDefaultLocale()));
    }

    /*-------------------------------------------------------Helper Methods-------------------------------------------------------*/

    public void onGetFavIcon(ImageView pImageView, String pURL){
        mGeckoClient.onGetFavIcon(pImageView, pURL, homeController.this);
    }

    public void onGetThumbnail(ImageView pImageView,boolean pLoadTabView){
        try{
            mRenderedBitmap = mGeckoView.capturePixels();
        }catch (Exception ignored){}
        if(mScrollHandler!=null){
            mScrollHandler.removeCallbacksAndMessages(null);
        }
        new Handler().postDelayed(() ->
        {
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(mGeckoClient.getSession().getSessionID(), mRenderedBitmap, pImageView, mGeckoView, pLoadTabView));
        }, 100);
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
        mGeckoClient.updateSetting(mGeckoView, homeController.this);
    }

    public void onReDrawGeckoview(){
        mGeckoClient.getSession().close();
        mGeckoClient.initialize(mGeckoView, new geckoViewCallback(), this,false);
    }


    public void onLoadURL(String url){
        if(mGeckoView.getSession()!=null && !mGeckoView.getSession().isOpen()){
            mGeckoView.getSession().open(mGeckoClient.getmRuntime());
        }

        mAppBar.animate().cancel();
        Objects.requireNonNull(mGeckoView.getSession()).stop();
        mGeckoClient.loadURL(url.replace("genesis.onion","genesishiddentechnologies.com"),mGeckoView, homeController.this);
    }

    public void onLoadTab(geckoSession mTempSession, boolean isSessionClosed, boolean pExpandAppBar){

        if(!isSessionClosed){
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.MOVE_TAB_TO_TOP, Collections.singletonList(mTempSession));
        }

        if(mTempSession.isOpen()){
            if(mTempSession.getSessionID().equals(mGeckoClient.getSession().getSessionID())){
                return;
            }
        }

        mGeckoClient.getmRuntime();
        mGeckoClient.initSession(mTempSession);
        mGeckoView.releaseSession();
        mGeckoView.setSession(mTempSession);

        if(!mTempSession.isOpen()){
            mTempSession.open(mGeckoClient.getmRuntime());
            onLoadURL(mTempSession.getCurrentURL());
        }

        mHomeViewController.onClearSelections(false);
        mHomeViewController.onUpdateSearchBar(mTempSession.getCurrentURL(),false,true, false);
        if(mTempSession.getProgress()>0 && mTempSession.getProgress()<100){
            mHomeViewController.onProgressBarUpdate(mTempSession.getProgress(), false);
        }else {
            mHomeViewController.progressBarReset();
        }

        mGeckoClient.onValidateInitializeFromStartup(mGeckoView, homeController.this);
        mGeckoClient.onSessionReinit();
        mHomeViewController.onUpdateStatusBarTheme(mTempSession.getTheme(), false);
        mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(), false, false, false);
        mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(),false);

        try{
            mRenderedBitmap = mGeckoView.capturePixels();
        }catch (Exception ignored){}

        if(pExpandAppBar){
            mHomeViewController.expandTopBar(false, mGeckoView.getMaxY());
        }

        if(mGeckoClient.getSession().getCurrentURL().equals("about:blank") || mGeckoClient.getSession().getCurrentURL().contains("genesishiddentechnologies.com") || mGeckoClient.wasPreviousErrorPage() || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED_DARK) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)){
            mHomeViewController.updateBannerAdvertStatus(false, (boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
        }else {
            mHomeViewController.updateBannerAdvertStatus(true, (boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
        }

        mHomeViewController.onProgressBarUpdate(mGeckoClient.getSession().getProgress(),true);
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(mGeckoClient.getSession().getSessionID(), mRenderedBitmap, null, mGeckoView, false));


        TouchView(mGeckoView);
        TouchView(mNestedScroll);

    }

    public void TouchView(View view)
    {
        long downTime = 3000;
        long eventTime = 3000;
        float x = 0f;
        float y = 100f;

        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_SCROLL,
                x,
                y,
                metaState
        );

        view.dispatchTouchEvent(motionEvent);

        view.dispatchTouchEvent(MotionEvent.obtain(0,0,MotionEvent.ACTION_DOWN, -1,-1,0.5f,5,0,1,1,0,0));
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onDestroy() {
        if(!status.sSettingIsAppStarted){
            super.onDestroy();
            return;
        }
        pluginController.getInstance().onOrbotInvoke(Collections.singletonList(status.mThemeApplying), pluginEnums.eOrbotManager.M_DESTROY);
        mBackSplash.setImageDrawable(null);
        mBackSplash.setBackground(null);

        if(!status.mThemeApplying){
            if(!status.sSettingIsAppStarted){
                Intent intent = new Intent(getApplicationContext(), OrbotService.class);
                stopService(intent);
            }else {
                NotificationManagerCompat.from(this).cancelAll();
            }
        }

        KeyboardUtils.removeAllKeyboardToggleListeners();
        mGatewaySplash.setOnTouchListener(null);

        ((ConstraintLayout)mGatewaySplash.getParent()).removeView(mGatewaySplash);
        ((ConstraintLayout)mGeckoView.getParent()).removeView(mGeckoView);
        ((ConstraintLayout)mTabFragment.getParent()).removeView(mTabFragment);
        ((LinearLayout)mSearchbar.getParent()).removeView(mSearchbar);

        mTabFragment = null;
        mNestedScroll = null;
        mGeckoView.setOnFocusChangeListener(null);
        mGeckoView.setOnTouchListener(null);
        mGeckoView.destroyDrawingCache();
        mGeckoView.releaseSession();
        mGeckoClient.onDestroy();

        mGeckoView.onDestroy();
        mGeckoClient=null;
        mHomeViewController = null;
        activityContextManager.getInstance().setHomeController(null);
        mGeckoView.releaseSession();
        mGeckoView = null;

        activityContextManager.getInstance().setHomeController(null);
        activityContextManager.getInstance().setCurrentActivity(null);
        pluginController.getInstance().onRemoveInstances();

        unregisterReceiver(downloadStatus);

        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeLocalEventHandlers() {

        startService(new Intent(getBaseContext(), activityStateManager.class));

        registerReceiver(downloadStatus,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        mNewTab.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                mTopBarContainer.getLayoutTransition().setDuration(200);
                onOpenTabViewBoundary(null);
                mNewTab.setPressed(true);
            }
            return true;
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
                if(!mSearchbar.isFocused()){
                    if(mFindText.getText().length()==0 && mGeckoClient!=null){
                            mGeckoClient.getSession().getFinder().clear();
                            mHomeViewController.onUpdateFindBarCount(0,0);
                        }else if(mGeckoClient!=null){
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
                mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),true,true, false);
                mHomeViewController.onClearSelections(true);
                mGeckoClient.setLoading(true);
                final Handler handler = new Handler();
                handler.postDelayed(() -> mHomeViewController.onClearSelections(false), 500);
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
            mHomeViewController.onClearSelections(true);
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                mGatewaySplash.setElevation(9);
                status.sUIInteracted = true;
                mHomeViewController.onUpdateFindBar(false);
            }

            return false;
        });

        mGeckoView.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus)
            {
                pluginController.getInstance().onMessageManagerInvoke(null, M_RESET);
                if (!mGeckoClient.getSession().getCurrentURL().equals("about:blank")){
                    mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(), false, true, false);
                }

                final Handler handler = new Handler();
                handler.postDelayed(() -> getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE), 300);

            }else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            }
        });


        mSearchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Handler().postDelayed(() ->
                {
                    String mText = mSearchbar.getText().toString();
                    if(status.sSearchSuggestionStatus && isSuggestionChanged){
                        String mURL = mSearchbar.getText().toString();

                        if(!mURL.equals(strings.GENERIC_EMPTY_STR) && isLetter(mSearchbar.getText().toString().charAt(0)) && mSearchbar.getText().toString().contains(".")){
                            mHomeViewController.onUpdateSearchIcon(2);
                        }else{
                            mHomeViewController.onUpdateSearchIcon(0);
                        }
                        if(mSearchEngineBar.getVisibility() == View.GONE){
                            mSearchBarLoadingOpening = true;
                        }
                        if(mSearchBarLoadingOpening){
                            mSuggestions = (ArrayList<historyRowModel>)dataController.getInstance().invokeSuggestions(dataEnums.eSuggestionCommands.M_GET_DEFAULT_SUGGESTION, Collections.singletonList(mText));
                            mHomeViewController.onUpdateSearchEngineBar(true, 0);
                            onUpdateSuggestionList(mSuggestions);
                            mEdittextChanged.postDelayed(postToServerRunnable, 0);
                            mSearchBarLoadingOpening = true;
                            mSearchBarLoading = false;

                            mEdittextChanged.removeCallbacks(postToServerRunnable);
                            mSuggestions = (ArrayList<historyRowModel>)dataController.getInstance().invokeSuggestions(dataEnums.eSuggestionCommands.M_GET_SUGGESTIONS, Collections.singletonList(mText));
                            mEdittextChanged.postDelayed(postToServerRunnable, 150);
                            return;
                        }
                        if(mSuggestions.size()>0){
                            mSuggestions = (ArrayList<historyRowModel>)dataController.getInstance().invokeSuggestions(dataEnums.eSuggestionCommands.M_GET_SUGGESTIONS, Collections.singletonList(mText));
                            if(mHintListView.getAdapter()==null){
                                initSuggestionView(mSuggestions, mText.toString());
                            }else if(!mSearchBarLoadingOpening){
                                mEdittextChanged.removeCallbacks(postToServerRunnable);
                                if(!mSearchBarLoading){
                                    mSearchBarLoading = true;
                                    mEdittextChanged.postDelayed(postToServerRunnable, 0);
                                }else{
                                    mEdittextChanged.postDelayed(postToServerRunnable, 150);
                                }
                            }
                        }
                    }

                }, 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

        });

        mSearchbar.setEventHandler(new edittextManagerCallback());

        mSearchbar.setOnFocusChangeListener((v, hasFocus) -> {
            status.sUIInteracted = true;
            if(!hasFocus)
            {
                mSearchBarWasBackButtonPressed = true;
                new Handler().postDelayed(() ->
                {
                    mSearchBarWasBackButtonPressed = false;
                }, 100);

                if(!isSuggestionSearchOpened){
                    if(isSuggestionChanged){
                        isSuggestionChanged = false;
                        if(mHintListView!=null && mHintListView.getAdapter()!=null && mHintListView.getAdapter().getItemCount()>0){
                            mHomeViewController.onUpdateSearchEngineBar(false, 150);
                            ((hintAdapter) Objects.requireNonNull(mHintListView.getAdapter())).onClearAdapter();
                        }
                        mHomeViewController.initSearchBarFocus(false, isKeyboardOpened);
                        if(!mGeckoClient.isLoading()){
                            mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false,true, false);
                        }
                        helperMethod.hideKeyboard(homeController.this);
                    }
                }
                mSearchbar.setSelection(0);
            }else {
                mSearchBarWasBackButtonPressed = false;
                if(!isFocusChanging){
                    if(!status.mThemeApplying){
                        mHomeViewController.initSearchBarFocus(true, isKeyboardOpened);
                    }
                    isSuggestionChanged = true;
                    isSuggestionSearchOpened = false;
                }
            }
        });

        KeyboardUtils.addKeyboardToggleListener(this, isVisible -> isKeyboardOpened = isVisible);

        mNestedScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&  scrollY > oldScrollY) {
                }
            }
            onInvokePixelGenerator();
        });
    }

    private ArrayList<historyRowModel> mSuggestions;
    private Handler mEdittextChanged = new Handler();
    private Runnable postToServerRunnable = () -> {
        mSearchBarLoadingOpening = false;
        onUpdateSuggestionList(mSuggestions);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            mSearchBarLoading = false;
        }, 150);
    };

    public void onSearchBarInvoked(View view){
        String url = ((EditText)view).getText().toString();
        String validated_url = mHomeModel.urlComplete(url, mHomeModel.getSearchEngine());
        url = validated_url;

        mHomeViewController.onUpdateSearchBar(url,false,true, false);
        onLoadURL(url);
    }

    public void onSearchString(String pString){
        String validated_url = mHomeModel.urlComplete(pString, mHomeModel.getSearchEngine());
        pString = validated_url;

        mHomeViewController.onUpdateSearchBar(pString,false,true, false);
        onLoadURL(pString);
    }

    public void onSuggestionInvoked(View view){
        String mVal = ((TextView)view.findViewById(R.id.pURL)).getText().toString();
        if(mVal.equals(strings.GENERIC_EMPTY_STR)){
            mVal = ((TextView)view.findViewById(R.id.pHeaderSingle)).getText().toString();
        }
        String pURL = mHomeModel.urlComplete(mVal, status.sSettingSearchStatus);

        mHomeViewController.onClearSelections(true);
        mHomeViewController.onUpdateSearchBar(pURL,false,true, true);

        String finalPURL = pURL;
        new Handler().postDelayed(() ->
        {
            onLoadURL(finalPURL);
        }, 250);
    }

    public void onSuggestionMove(View view){
        String val = view.getTag().toString();
        mHomeViewController.onUpdateSearchBar(val,false,false, true);
    }

    public void onHomeButton(View view){

        mGeckoClient.getSession().setTheme(null);
        mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(), true);
        onLoadURL(helperMethod.getDomainName(mHomeModel.getSearchEngine()));
        mHomeViewController.onUpdateLogo();
    }

    /*TAB CONTROLLER EVENTS*/
    public void onMenuTrigger(View pView){
        activityContextManager.getInstance().getTabController().onMenuTrigger(pView);
    }

    public void openTabMenu(View view) {
        activityContextManager.getInstance().getTabController().openTabMenu(view);
    }

    public void onRemoveSelection(View view) {
        activityContextManager.getInstance().getTabController().onRemoveSelection(view);
    }

    public void onLoadRecentTab(View view){
        mHomeViewController.onHideLoadTabDialog();
        tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_RECENT_TAB, null);
        if(model!=null && !mGeckoClient.getSession().getSessionID().equals(model.getSession().getSessionID())){
            Log.i("FUCK::1",model.getSession().getCurrentURL());
            mHomeViewController.onUpdateSearchBar(model.getSession().getCurrentURL(),false,false,true);
            Log.i("FUCK::2",model.getSession().getCurrentURL());
            onLoadTab(model.getSession(), false,true);
            Log.i("FUCK::3",model.getSession().getCurrentURL());
        }
    }

    public void onRestoreTab(View view){
        activityContextManager.getInstance().getTabController().onRestoreTab(view);
    }

    public void onClearSelection(View view){
        activityContextManager.getInstance().getTabController().onClearSelection(view);
    }

    public geckoSession onNewTabInit(){
        return mGeckoClient.initFreeSession(mGeckoView, this, new geckoViewCallback());
    }

    public void postNewTabAnimation(boolean isKeyboardOpenedTemp, boolean isKeyboardOpened){
        initializeGeckoView(true, true);
        if(status.sOpenURLInNewTab){
            if(mGeckoView.getSession()!=null && !mGeckoView.getSession().isOpen()){
               mGeckoView.getSession().open(mGeckoClient.getmRuntime());
            }

            mHomeViewController. onUpdateSearchBar(helperMethod.getDomainName(status.sSettingSearchStatus),false,true, false);
            onLoadURL(helperMethod.getDomainName(status.sSettingSearchStatus));
            mGeckoView.getSession().setActive(true);

        }else {
            onLoadURL("about:blank");
            mHomeViewController. onUpdateSearchBar(strings.HOME_BLANK_PAGE,false,true, false);
            mHomeViewController.onNewTab();
        }
        mHomeViewController.progressBarReset();
        mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(), false);
    }

    public void postNewLinkTabAnimation(String url,boolean isRemovable){
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(mGeckoClient.getSession().getSessionID(), mRenderedBitmap, null, mGeckoView,false));
        initializeGeckoView(true, true);
        mHomeViewController.progressBarReset();
        mHomeViewController.onUpdateSearchBar(url,false,true, false);
        mGeckoClient.loadURL(url, mGeckoView, homeController.this);

        if(isRemovable){
            mGeckoClient.setRemovableFromBackPressed(true);
        }
    }

    public void postNewLinkTabAnimationInBackgroundTrigger(String url){
        String mExtention = helperMethod.getMimeType(url, this);
        if(mExtention == null || mExtention.equals("text/html") || mExtention.equals("application/vnd.ms-htmlhelp") || mExtention.equals("application/vnd.sun.xml.writer") || mExtention.equals("application/vnd.sun.xml.writer.global") || mExtention.equals("application/vnd.sun.xml.writer.template") || mExtention.equals("application/xhtml+xml")){
            initTabCount(M_NEW_LINK_IN_NEW_TAB, Collections.singletonList(url));
        }else {
            postNewLinkTabAnimation(url, true);
        }
    }

    public void postNewLinkTabAnimationInBackground(String url){
        mAppBar.setTag(R.id.expandableBar,false);
        geckoSession mSession = mGeckoClient.getSession();
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(mGeckoClient.getSession().getSessionID(), mRenderedBitmap, null, mGeckoView,false));
        mGeckoClient.initialize(mGeckoView, new geckoViewCallback(), this,true);
        mHomeViewController.progressBarReset();
        mGeckoClient.initURL(url);
        mGeckoClient.loadURL(url, mGeckoView, homeController.this);
        mGeckoClient.getSession().setURL(url);
        onSaveCurrentTab(mGeckoClient.getSession(),false);
        onLoadTab(mSession,false,false);


        mAppBar.setTag(R.id.expandableBar,true);
        initTabCountForced();
        mHomeViewController.onUpdateSearchBar(mSession.getCurrentURL(),false,true, false);
    }

    public void onNewTab(boolean isKeyboardOpenedTemp, boolean isKeyboardOpened){

        try{
            mRenderedBitmap = mGeckoView.capturePixels();
        }catch (Exception ignored){}

        new Handler().postDelayed(() ->
        {
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(mGeckoClient.getSession().getSessionID(), mRenderedBitmap, null, mGeckoView, false));
            if(status.sSettingSearchStatus.startsWith("https://genesishiddentechnologies.com") || !status.sOpenURLInNewTab || mGeckoClient.getSession().getCurrentURL().equals("about:blank") || mGeckoClient.getSession().getCurrentURL().contains("genesishiddentechnologies.com") || mGeckoClient.wasPreviousErrorPage() || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED_DARK) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)){
                mHomeViewController.updateBannerAdvertStatus(false, (boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
            }

            if(status.sOpenURLInNewTab){
                mGeckoClient.resetSession();
                mHomeViewController.onUpdateSearchBar(helperMethod.getDomainName(mHomeModel.getSearchEngine()), false, false,false);

                if(status.sSettingSearchStatus.startsWith("https://genesishiddentechnologies.com")){
                    mHomeViewController.progressBarReset();
                }else {
                    mHomeViewController.onProgressBarUpdate(5, true);
                }
                mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(), false);
            }
            mHomeViewController.onNewTabAnimation(Arrays.asList(isKeyboardOpenedTemp, isKeyboardOpened), M_INITIALIZE_TAB_SINGLE);
        }, 100);
    }

    public void onUpdateScreenPixel(){

    }

    public void onNewTabBackground(boolean isKeyboardOpenedTemp, boolean isKeyboardOpened){
        if(status.sSettingSearchStatus.startsWith("https://genesishiddentechnologies.com") || !status.sOpenURLInNewTab || mGeckoClient.getSession().getCurrentURL().equals("about:blank") || mGeckoClient.getSession().getCurrentURL().contains("genesishiddentechnologies.com") || mGeckoClient.wasPreviousErrorPage() || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED_DARK) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)){
            mHomeViewController.updateBannerAdvertStatus(false, (boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
        }

        if(status.sOpenURLInNewTab){
            mGeckoClient.resetSession();
            mHomeViewController.onUpdateSearchBar(helperMethod.getDomainName(mHomeModel.getSearchEngine()), false, false,false);

            if(status.sSettingSearchStatus.startsWith("https://genesishiddentechnologies.com")){
                mHomeViewController.progressBarReset();
            }else {
                mHomeViewController.onProgressBarUpdate(5, true);
            }
            mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(), true);
        }

        mHomeViewController.onNewTabAnimation(Arrays.asList(isKeyboardOpenedTemp, isKeyboardOpened), M_INITIALIZE_TAB_SINGLE);
    }

    public String completeURL(String pURL){
        return mHomeModel.urlComplete(pURL, mHomeModel.getSearchEngine());
    }

    public void onOpenLinkNewTab(String url){
        mNewTab.setPressed(true);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            onGetThumbnail(null, false);
            mHomeViewController.expandTopBar(false,mGeckoView.getMaxY());
            if(status.sSettingSearchStatus.startsWith("https://genesishiddentechnologies.com") || !status.sOpenURLInNewTab || mGeckoClient.getSession().getCurrentURL().equals("about:blank") || mGeckoClient.getSession().getCurrentURL().contains("genesishiddentechnologies.com") || mGeckoClient.wasPreviousErrorPage() || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED_DARK) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)){
                mHomeViewController.updateBannerAdvertStatus(false, (boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
            }
            mHomeViewController.onNewTabAnimation(Collections.singletonList(url), M_INITIALIZE_TAB_LINK);
        }, 100);
    }

    public void onClearSelectionTab(){
        mNewTab.setPressed(false);
    }

    public void onOpenTabViewBoundary(View view){
        onInvokePixelGenerator();
        if(mScrollHandler!=null){
            mScrollHandler.removeCallbacksAndMessages(null);
        }
        onInvokePixelGenerator();
        mNewTab.setPressed(true);
        onOpenTabReady();
    }

    public void onOpenTabReady(){
        if(!status.mThemeApplying){
            runOnUiThread(() -> {
                mHomeViewController.onShowTabContainer();
                pluginController.getInstance().onMessageManagerInvoke(null, M_RESET);
                activityContextManager.getInstance().getTabController().onInitInvoked();
            });
        }
    }

    public void onLoadFirstElement(){
        activityContextManager.getInstance().getTabController().onInitFirstElement();
    }

    public void onLockSecure(View view){
        pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(mGeckoClient.getSession().getCurrentURL(), status.sSettingJavaStatus, status.sStatusDoNotTrack, status.sSettingTrackingProtection, status.sSettingCookieStatus, this), M_SECURE_CONNECTION);
    }

    public void onNotificationInvoked(String message,enums.etype e_type){
        mHomeViewController.downloadNotification(message,e_type);
    }

    public void onOpenMenuItem(View view){
        status.sUIInteracted = true;
        pluginController.getInstance().onMessageManagerInvoke(null, M_RESET);
        initLocalLanguage();

        helperMethod.hideKeyboard(this);
        mHomeViewController.onOpenMenu(view,mGeckoClient.canGoForward(),!(mProgressBar.getAlpha()<=0 || mProgressBar.getVisibility() == View.INVISIBLE || mGeckoClient.isLoaded()),mGeckoClient.getUserAgent(), mGeckoClient.getSession().getCurrentURL());

        view.setClickable(false);
        new Handler().postDelayed(() ->
        {
            view.setClickable(true);
        }, 500);

    }

    public void onFullScreenSettingChanged(){
        mHomeViewController.initTopBarPadding();
    }

    public void onDisableTabViewController(){
        onResumeDump();
        mHomeViewController.onHideTabContainer();
        activityContextManager.getInstance().getTabController().onExitAndClearBackup();
        activityContextManager.getInstance().getTabController().onPostExit();
    }

    @Override
    public void onBackPressed(){
        if(mTabFragment.getVisibility()==View.VISIBLE){
            if(activityContextManager.getInstance().getTabController().isSelectionOpened()){
                activityContextManager.getInstance().getTabController().onClearSelection(null);
            }else {
                boolean mStatus = activityContextManager.getInstance().getTabController().onBackPressed();
                onResumeDump();
                activityContextManager.getInstance().getTabController().onPostExit();
                if(!mStatus){
                    mHomeViewController.onHideTabContainer();
                }
            }
            return;
        }

        if(mFindBar!=null && mFindBar.getVisibility() == View.VISIBLE){
            mHomeViewController.onUpdateFindBar(false);
        }
        else if(mSearchEngineBar.getVisibility() == View.VISIBLE){
            mHomeViewController.onUpdateSearchEngineBar(false, 150);
            ((hintAdapter) Objects.requireNonNull(mHintListView.getAdapter())).onClearAdapter();
        }
        else if(!mGeckoClient.getFullScreenStatus()){
            mGeckoClient.onExitFullScreen();
            mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(), false);
        }
        else if(mSearchbar.isFocused() || isKeyboardOpened){
            mHomeViewController.onClearSelections(true);
        }
        else if(!mSearchBarWasBackButtonPressed){
            int mTabSize = (int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null);
            mGeckoClient.onBackPressed(true, mTabSize, mGeckoView, this);
        }
    }

    /*Activity States*/

    public NestedGeckoView getGeckoView(){
        return mGeckoView;
    }

    public void onReload(View view){
        onLoadURL(mSearchbar.getText().toString());
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
            handler.postDelayed(() -> mGeckoClient.onRedrawPixel(homeController.this), 300);
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

                if(mGeckoClient.getSession().getCurrentURL().equals("about:blank") || mGeckoClient.getSession().getCurrentURL().contains("genesishiddentechnologies.com") || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED_DARK) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE) || mGeckoClient.getSession().getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)){
                    mHomeViewController.updateBannerAdvertStatus(false, (boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
                }else {
                    mHomeViewController.updateBannerAdvertStatus(true, (boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
                }
            }
            if(mSplashScreen.getAlpha()>0){
                mHomeViewController.initSplashOrientation();
            }
            if(mSearchEngineBar.getVisibility() == View.VISIBLE){
                mHomeViewController.onClearSelections(true);
            }
            pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
            mHomeViewController.onClearSelections(true);
            mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false,true, true);

            mHomeViewController.initTopBarPadding();
            mHomeViewController.onSetBannerAdMargin(!mGeckoClient.isLoading(),(boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED)&&!!mGeckoClient.isLoading());

            mHomeViewController.expandTopBar(false, mGeckoView.getMaxY());
            status.sUIInteracted = true;
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mHomeViewController!=null){
            mHomeViewController.closeMenu();
            helperMethod.hideKeyboard(this);
        }
        if(mTabFragment.getVisibility()==View.VISIBLE){
            onResumeDump();
            mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(), false);
            mHomeViewController.onHideTabContainer();
            activityContextManager.getInstance().getTabController().onPostExit();
            activityContextManager.getInstance().getTabController().onBackPressed();
            return;
        }

        mHomeViewController.onUpdateSearchEngineBar(false, 0);
        mGeckoClient.onExitFullScreen();
        mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(), false);
        pluginController.getInstance().onMessageManagerInvoke(null, M_RESET);
        pluginController.getInstance().onNotificationInvoke(Collections.singletonList(172800000)  , pluginEnums.eNotificationManager.M_CREATE_NOTIFICATION);
        mSearchBarWasBackButtonPressed = false;
        if(status.sSettingIsAppStarted){
            status.sUIInteracted = true;
        }
        mHomeViewController.onUpdateFindBar(false);
        mHomeViewController.onClearSelections(isKeyboardOpened);
        mTopBarContainer.getLayoutTransition().setDuration(0);
    }

    @Override
    public void onResume()
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);

        if (mGeckoClient.getSession()!=null && mGeckoClient!=null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mGeckoClient.getUriPermission()!=null) {
            this.revokeUriPermission(mGeckoClient.getUriPermission(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            TouchView(mGeckoView);
            TouchView(mNestedScroll);
        }


        if(isSuggestionSearchOpened){
            isSuggestionChanged = true;
            isFocusChanging = false;
            isSuggestionSearchOpened = false;
            mSearchbar.requestFocus();
            mSearchbar.setText(helperMethod.urlDesigner(mSearchBarPreviousText, this, mSearchbar.getCurrentTextColor(), status.sTheme));
            mSearchbar.selectAll();
            mHomeViewController.initSearchBarFocus(true, isKeyboardOpened);
        }

        if(mGeckoView!=null){
            tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
            if(model!=null){
                if(mGeckoView!=null && mGeckoView.getSession()!=null && !mGeckoView.getSession().isOpen()){
                    onReDrawGeckoview();
                    onLoadURL(model.getSession().getCurrentURL());
                }
            }
        }

        if(status.sSettingIsAppStarted){
            mHomeViewController.onClearSelections(isKeyboardOpened);
        }

        if(mAppBar!=null){
            mHomeViewController.expandTopBar(false, mGeckoView.getMaxY());

            mAppBar.refreshDrawableState();
            mAppBar.invalidate();
        }
        mAppRestarted = true;
        pluginController.getInstance().onNotificationInvoke(null, pluginEnums.eNotificationManager.M_CLEAR_NOTIFICATION );

        if(!status.mThemeApplying){
            //activityContextManager.getInstance().onClearStack();
        }
        initWidget();
        initTabCountForced();

        if(status.sSettingIsAppStarted && !status.mThemeApplying){
            pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_INIT_LOCALE);
            mHomeViewController.onUpdateSearchEngineBar(false, 0);
            mNewTab.setPressed(false);
            pluginController.getInstance().onMessageManagerInvoke(null, M_RESET);
        }

        if(status.sSettingIsAppStarted && !status.mThemeApplying){
            if(mGeckoClient.getSession().wasPreviousErrorPage()){
                pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_NEW_CIRCUIT);
                mGeckoClient.onReload(mGeckoView, this);
            }
        }

        status.mThemeApplying = false;

        super.onResume();
    }

    public void onResumeDump()
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        if (mGeckoClient.getSession()!=null && mGeckoClient!=null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mGeckoClient.getUriPermission()!=null) {
            this.revokeUriPermission(mGeckoClient.getUriPermission(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if(isSuggestionSearchOpened){
            tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
            if(model==null){
                onOpenLinkNewTab(status.sSettingSearchStatus);
            }
            isSuggestionChanged = true;
            isFocusChanging = false;
            isSuggestionSearchOpened = false;
            mSearchbar.requestFocus();
            mSearchbar.setText(helperMethod.urlDesigner(mSearchBarPreviousText, this, mSearchbar.getCurrentTextColor(), status.sTheme));
            mSearchbar.selectAll();
            mHomeViewController.initSearchBarFocus(true, isKeyboardOpened);
        }

        if(status.sSettingIsAppStarted && mAppRestarted) {
            activityContextManager.getInstance().onClearStack();

            tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
            if(model==null){
                mHomeViewController.onProgressBarUpdate(5, false);
                initializeGeckoView(false,false);
                onLoadURL(helperMethod.getDomainName(status.sSettingSearchStatus));
            }else {
                if(model.getSession().getSessionID() != mGeckoClient.getSession().getSessionID()){
                    onLoadTab(model.getSession(),false,true);
                }
            }
        }

        if(mAppBar!=null){
            mHomeViewController.expandTopBar(false, mGeckoView.getMaxY());

            mAppBar.refreshDrawableState();
            mAppBar.invalidate();
        }
        mAppRestarted = true;
        pluginController.getInstance().onNotificationInvoke(null, pluginEnums.eNotificationManager.M_CLEAR_NOTIFICATION );
        initTabCountForced();
        initWidget();
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
                handler.postDelayed(() ->  mGeckoView.clearFocus(), 500);
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
        if(status.sSettingEnableVoiceInput || status.sWidgetResponse == enums.WidgetResponse.VOICE){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice2Text \n Say Something!!");
            try {
                startActivityForResult(intent, 100);
            } catch (ActivityNotFoundException ignored) {

            }
        }else {
            mSearchbar.clearFocus();
            new Handler().postDelayed(() ->
            {
                mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false,true, false);
            }, 170);
        }
    }
    /*-------------------------------------------------------External Callback Methods-------------------------------------------------------*/

    public String onGetCurrentURL(){
        return mGeckoClient.getSession().getCurrentURL();
    }

    public void onStartApplication(View view){
        pluginController.getInstance().onOrbotInvoke(Arrays.asList(status.sBridgeCustomBridge, status.sBridgeGatewayManual, status.sBridgeCustomType, status.sBridgeStatus, status.sShowImages, status.sClearOnExit, (String)dataController.getInstance().invokeBridges(dataEnums.eBridgeWebsiteCommands.M_FETCH, null)), pluginEnums.eOrbotManager.M_START_ORBOT);
        onInvokeProxyLoading();
        mHomeViewController.initHomePage();
    }

    public void onDownloadFile(){
        mGeckoClient.downloadFile(homeController.this);
    }

    public void onManualDownload(String url){

        /*EXTERNAL STORAGE REQUEST*/
        mGeckoClient.manual_download(url,this);
    }

    public void onManualDownloadFileName(String pURL, String pPath){

        /*EXTERNAL STORAGE REQUEST*/
        mGeckoClient.manualDownloadWithName(pURL,pPath,this);
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
        int mStatus = (Integer) dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_ADD_TAB, Arrays.asList(session,isHardCopy));
        if(mStatus == enums.AddTabCallback.TAB_FULL){
            pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_MAX_TAB_REACHED);
        }
    }

    public boolean onCloseCurrentTab(geckoSession session){
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.CLOSE_TAB, Arrays.asList(session, session));
        tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);

        session.stop();
        session.close();
        initTabCountForced();

        if(model!=null){
            if(mTabFragment.getVisibility()!=View.VISIBLE){
                onLoadTab(model.getSession(),true, true);
            }
            return true;
        }
        else {
            if(mTabFragment.getVisibility()!=View.VISIBLE){
                return false;
            }else {
                return true;
            }
        }
    }

    public void initTabCount(enums.etype pEvent, List<Object> pData){
        mHomeViewController.initTab((int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null), false, pEvent, pData);
    }

    public void initTabCountForced(){
        mHomeViewController.initTab((int)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_TOTAL_TAB, null), true, null, null);
    }

    /*-------------------------------------------------------CALLBACKS-------------------------------------------------------*/


    public void onHideFindBar(View view){
        mHomeViewController.onUpdateFindBar(false);
        mGeckoClient.getSession().getFinder().clear();
    }

    public void onClearSearchBar(View view){
        mHomeViewController.onUpdateSearchBar(strings.GENERIC_EMPTY_STR, false, true, false);
    }

    public void onFindNext(View view){
        mGeckoClient.getSession().findInPage(mFindText.getText().toString(), GeckoSession.FINDER_FIND_MATCH_CASE & GeckoSession.FINDER_DISPLAY_HIGHLIGHT_ALL);
    }

    public void onOpenSearchEngine(View view){
        mSearchBarPreviousText = mSearchbar.getText().toString();
        isSuggestionSearchOpened = true;
        helperMethod.openActivity(settingSearchController.class,constants.CONST_LIST_HISTORY, homeController.this,true);
    }

    public void onFindPrev(View view){
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
        else if (menuId == R.id.menuItem25) {
            pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(mGeckoClient.getSession().getCurrentURL(), this), M_DOWNLOAD_SINGLE);
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
                mGeckoClient.onRedrawPixel(homeController.this);
                mHomeViewController.onShowTabContainer();
                pluginController.getInstance().onMessageManagerInvoke(null, M_RESET);
                Log.i("I AM FUCKED,","I AM FUCKED : 1");
                activityContextManager.getInstance().getTabController().onInit();
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
            else if (menuId == R.id.pMenuOpenRecentTab)
            {
                onOpenTabViewBoundary(null);
                mNewTab.setPressed(true);
            }
            else if (menuId == R.id.pMenuOpenNewTab)
            {
                helperMethod.hideKeyboard(this);
                helperMethod.openActivity(bookmarkController.class,constants.CONST_LIST_BOOKMARK, homeController.this,true);
            }
            else if (menuId == R.id.menu28)
            {
                pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_NEW_CIRCUIT);
                pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_NEW_IDENTITY);
                mGeckoClient.onReload(mGeckoView, this);
            }
            else if (menuId == R.id.pMenuOpenCurrentTab)
            {
                helperMethod.hideKeyboard(this);
                pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_REPORT_URL);
            }
            else if (menuId == R.id.pMenuQuit)
            {
                pluginController.getInstance().onOrbotInvoke(Collections.singletonList(status.mThemeApplying), pluginEnums.eOrbotManager.M_DESTROY);

                new Handler().postDelayed(() ->
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
                }, 100);
            }
            else if (menuId == R.id.pMenuFind)
            {
                helperMethod.hideKeyboard(this);
                mHomeViewController.onUpdateFindBar(true);
            }
            if (menuId == R.id.menu20) {
                mGeckoClient.onClose();
                mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false,true, true);
                helperMethod.hideKeyboard(this);
            }
            if (menuId == R.id.menu21) {
                helperMethod.hideKeyboard(this);
                String mUrl = mGeckoClient.getSession().getCurrentURL();

                if(mUrl.equals(constants.CONST_GENESIS_HELP_URL_CACHE_DARK) && status.sTheme != enums.Theme.THEME_DARK){
                    onLoadURL(constants.CONST_GENESIS_HELP_URL_CACHE);
                }
                else if(mUrl.equals(constants.CONST_GENESIS_HELP_URL_CACHE) && status.sTheme != enums.Theme.THEME_LIGHT){
                    onLoadURL(constants.CONST_GENESIS_HELP_URL_CACHE_DARK);
                }
                else if(mUrl.equals(CONST_GENESIS_URL_CACHED_DARK) && status.sTheme != enums.Theme.THEME_DARK){
                    onLoadURL(CONST_GENESIS_URL_CACHED);
                }
                else if(mUrl.equals(constants.CONST_GENESIS_URL_CACHED) && status.sTheme != enums.Theme.THEME_LIGHT){
                    onLoadURL(CONST_GENESIS_URL_CACHED_DARK);
                }else {
                    onLoadURL(mGeckoClient.getSession().getCurrentURL());
                }

            }
            if (menuId == R.id.menu22) {
                helperMethod.hideKeyboard(this);
                mHomeViewController.onProgressBarUpdate(5,true);
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
            if (menuId == R.id.menu26 || menuId == R.id.menu27) {
                helperMethod.hideKeyboard(this);
                mGeckoClient.toogleUserAgent();
                mGeckoClient.onReload(mGeckoView, homeController.this);
            }
            if(menuId == R.id.menu25){
                helperMethod.hideKeyboard(this);
                helperMethod.openActivity(languageController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
            }
        }
        mHomeViewController.closeMenu();
    }

    private void onInitTheme(){

        if(status.mThemeApplying){
            if(status.sTheme == enums.Theme.THEME_DARK){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else if(status.sTheme == enums.Theme.THEME_LIGHT){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }else {
                if(!status.sDefaultNightMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        }
    }

    public void onReInitTheme(){
        recreate();
        mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(), true);
    }

    public void onOrbotLog(View view) {
        mHomeViewController.closeMenu();
        helperMethod.openActivity(orbotLogController.class, constants.CONST_LIST_HISTORY, homeController.this,true);
    }

    public void panicExit(View view) {
        pluginController.getInstance().onMessageManagerInvoke(null, M_RESET);
        pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(homeController.this), M_PANIC_RESET);
    }

    public void panicExitInvoked() {
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
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE,constants.CONST_BACKEND_GENESIS_URL));
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
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_ENABLES,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL,false));


        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
        dataController.getInstance().invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL, Arrays.asList(SQL_CLEAR_HISTORY,null));
        dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_CLEAR_HISTORY ,null);
        activityContextManager.getInstance().getHomeController().onClearCache();
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
        activityContextManager.getInstance().getHomeController().onClearSiteData();
        activityContextManager.getInstance().getHomeController().onClearSession();
        activityContextManager.getInstance().getHomeController().onClearCookies();
        onClearSettings();
        status.initStatus(activityContextManager.getInstance().getHomeController());
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
        activityContextManager.getInstance().getHomeController().initRuntimeSettings();
        pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_DATA_CLEARED);
        activityContextManager.getInstance().getHomeController().onClearSettings();

        status.sSettingIsAppStarted = false;
        finishAndRemoveTask();

        Intent intent = new Intent(this, homeController.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent);
        overridePendingTransition(R.anim.popup_scale_in, R.anim.popup_scale_out);
        this.finish();

        Runtime.getRuntime().exit(0);

    }

    public class nestedGeckoViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if(status.sFullScreenBrowsing){
                if(e_type.equals(GECKO_SCROLL_DOWN)){
                    mTopBarContainer.getLayoutTransition().setDuration(0);
                    mHomeViewController.onClearSelections(isKeyboardOpened);
                    mSearchbar.clearFocus();
                }
                else {
                    int[] locatiosn = new int[2];
                    mSearchbar.getLocationOnScreen(locatiosn);
                    int ys = locatiosn[1];
                    if(ys!=0){
                        if(ys<=50){
                            mHomeViewController.shrinkTopBar(true, mGeckoView.getMaxY());
                        }else {
                            mHomeViewController.expandTopBar(true, mGeckoView.getMaxY());
                        }
                    }
                }
            }
            return null;
        }
    }

    public class homeViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
           if(e_type.equals(enums.etype.M_INIT_TAB_COUNT_FORCED))
           {
               initTabCountForced();
           }
           else if(e_type.equals(enums.etype.M_NEW_LINK_IN_NEW_TAB))
           {
               postNewLinkTabAnimationInBackground(dataToStr(data.get(0)));
               mHomeViewController.onShowLoadTabDialog();
           }
           else if(e_type.equals(enums.etype.M_RESET_SUGGESTION))
           {
           }
           else if(e_type.equals(open_new_tab))
           {
               if(status.sSettingPopupStatus){
                   pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(homeController.this), M_POPUP_BLOCKED);
               }else {
                   postNewLinkTabAnimation(dataToStr(data.get(0)), true);
               }
           }
           else if(e_type.equals(enums.etype.M_CACHE_UPDATE_TAB)){
           }
           else if(e_type.equals(enums.etype.M_UPDATE_PIXEL_BACKGROUND)){
               onInvokePixelGenerator();
           }
           else if(e_type.equals(enums.etype.ON_NEW_TAB_ANIMATION)){
               postNewTabAnimation((boolean)data.get(0),(boolean)data.get(1));
           }
           else if(e_type.equals(enums.etype.ON_OPEN_TAB_VIEW)){
               onOpenTabViewBoundary(null);
           }
           else if(e_type.equals(enums.etype.download_folder))
           {
               onOpenDownloadFolder(null);
           }
           else if(e_type.equals(enums.etype.M_UPDATE_THEME))
           {
               if(mGeckoClient!=null) {
                   mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(), true);
               }
           }
           else if(e_type.equals(enums.etype.ON_UPDATE_THEME)){
               mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(),false);
           }
           else if(e_type.equals(M_INITIALIZE_TAB_SINGLE)){
               initTabCount(enums.etype.ON_NEW_TAB_ANIMATION,data);
           }
           else if(e_type.equals(M_INITIALIZE_TAB_LINK)){
               postNewLinkTabAnimation((String)data.get(0),false);
           }
           else if(e_type.equals(enums.etype.on_init_ads))
           {
               mHomeViewController.onSetBannerAdMargin((boolean)data.get(0),(boolean)pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED));
           }
           else if(e_type.equals(enums.etype.M_SPLASH_DISABLE))
           {
               initWidget();
           }
           else if(e_type.equals(enums.etype.M_WELCOME_MESSAGE)){
               if(status.sSettingIsWelcomeEnabled){
                   final Handler handler = new Handler();
                   Runnable runnable = () -> {
                       if(!status.sUIInteracted){
                           if(mHomeViewController!=null){
                               mHomeViewController.closeMenu();
                               mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false,true, true);
                               mSearchbar.clearFocus();
                               pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(strings.GENERIC_EMPTY_STR, homeController.this), M_WELCOME);
                               status.sUIInteracted = true;
                           }
                       }
                   };
                   handler.postDelayed(runnable, 2500);
               }
           }
           else if(e_type.equals(enums.etype.on_url_load)){
               if(status.sSettingIsAppRedirected){
                   mHomeViewController.onPageFinished();
                   mGeckoClient.onRedrawPixel(homeController.this);

                   status.sSettingIsAppRedirected = false;
                   onLoadURL(status.sSettingRedirectStatus);
                   status.sSettingRedirectStatus = strings.GENERIC_EMPTY_STR;
               }else {
                   if(status.mThemeApplying){
                       mHomeViewController.onUpdateSearchBar(data.get(0).toString(),false, false, false);
                       mHomeViewController.splashScreenDisableInstant();
                       onLoadTabOnResume();
                   }
                   onLoadURL(data.get(0).toString());
                   mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false,true, false);
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
                       mGeckoClient.onRedrawPixel(homeController.this);
                       mHomeViewController.onProgressBarUpdate(5, false);
                       onLoadTabOnResume();
                   }
               }
           }
           else if(e_type.equals(enums.etype.open_new_tab_instant)){
               postNewLinkTabAnimation(dataToStr(data.get(0)), true);
           }
           else if(e_type.equals(enums.etype.M_HOME_PAGE)){
               geckoSession mSession = (geckoSession)dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_HOME_PAGE, null);
               if(mSession!=null){
                   onLoadTab(mSession, false,true);
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
                        ((hintAdapter) Objects.requireNonNull(mHintListView.getAdapter())).onClearAdapter();
                    }
                    mHomeViewController.initSearchBarFocus(false, isKeyboardOpened);
                    mHomeViewController.onUpdateSearchBar(mGeckoClient.getSession().getCurrentURL(),false,true, true);
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
                mGeckoClient.onGetFavIcon((ImageView) data.get(0), (String) data.get(1), homeController.this);
            }
            return null;
        }
    }

    public void onDisableAdvert(){
        mHomeViewController.updateBannerAdvertStatus(false, true);
    }

    public void onClearSettings(){
        mHomeViewController.updateBannerAdvertStatus(false, true);
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
        activityContextManager.getInstance().getTabController().onCloseAllTabs();
        onLoadTabFromTabController();
        initTabCountForced();
    }

    public void onClearAllTabs(){
        mHomeViewController.updateBannerAdvertStatus(false, true);
    }

    public void onInvokePixelGenerator(){

        if(mTabFragment==null || mTabFragment.getVisibility()==View.VISIBLE){
            return;
        }

        if(mScrollHandler!=null){
            mScrollHandler.removeCallbacksAndMessages(null);
        }

        mScrollHandler = new Handler();
        mScrollRunnable = () -> {

            try{
                mRenderedBitmap = mGeckoView.capturePixels();
            }catch (Exception ignored){}
            new Handler().postDelayed(() ->
            {
                if(mTabFragment!=null){
                    if(mTabFragment.getVisibility()!=View.VISIBLE){
                        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(mGeckoClient.getSession().getSessionID(), mRenderedBitmap, null, mGeckoView, true));
                    }else {
                        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_PIXEL, Arrays.asList(mGeckoClient.getSession().getSessionID(), mRenderedBitmap, null, mGeckoView, true));
                    }
                }
            }, 100);

        };
        mScrollHandler.postDelayed(mScrollRunnable, 450);
    }

    public class geckoViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {

            if(e_type.equals(enums.etype.ON_EXPAND_TOP_BAR)){
                mHomeViewController.expandTopBar(false, mGeckoView.getMaxY());
            }
            else if(e_type.equals(enums.etype.M_ON_BANNER_UPDATE)){
                Object mAdvertResponse = pluginController.getInstance().onAdsInvoke(null, pluginEnums.eAdManager.M_IS_ADVERT_LOADED);
                if(mAdvertResponse != null){
                    mHomeViewController.updateBannerAdvertStatus((boolean)data.get(3), (boolean)mAdvertResponse);
                }
            }
            else if(e_type.equals(enums.etype.progress_update)){
                mHomeViewController.onProgressBarUpdate((int)data.get(0), false);
            }
            else if(e_type.equals(enums.etype.M_LOAD_HOMEPAGE_GENESIS)){
                onLoadURL(helperMethod.getDomainName(constants.CONST_BACKEND_GENESIS_URL));
            }
            else if(e_type.equals(enums.etype.progress_update_forced)){
                Log.i("SUPPPP7:",(String)data.get(2));
                mHomeViewController.onUpdateSearchBar((String) data.get(2), false, true, false);
                mHomeViewController.onProgressBarUpdate((int)data.get(0), true);
            }
            else if(e_type.equals(enums.etype.ON_UPDATE_SEARCH_BAR)){
                mHomeViewController.onUpdateSearchBar((String)data.get(0), false, false, false);
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
                helperMethod.onMinimizeApp(homeController.this);
            }
            else if(e_type.equals(enums.etype.M_CLOSE_TAB)){
                onCloseCurrentTab(mGeckoClient.getSession());
            }
            else if(e_type.equals(enums.etype.M_ADMOB_BANNER_RECHECK)){
                if(data.get(2).toString().startsWith(CONST_GENESIS_URL_CACHED) || data.get(2).toString().startsWith(CONST_GENESIS_URL_CACHED_DARK) || helperMethod.getHost(data.get(2).toString()).contains("genesishiddentechnologies.com")  || data.get(2).toString().startsWith(CONST_GENESIS_HELP_URL_CACHE) || data.get(2).toString().startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)){
                    mHomeViewController.updateBannerAdvertStatus(false,true);
                }
            }
            else if(e_type.equals(enums.etype.ON_UPDATE_THEME)){
                mHomeViewController.onUpdateStatusBarTheme(mGeckoClient.getTheme(),false);
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
                mGeckoClient.onRedrawPixel(homeController.this);
            }
            else if(e_type.equals(M_RATE_APPLICATION)){
                if(!status.sSettingIsAppRated){
                    dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,true));
                    status.sSettingIsAppRated = true;
                    pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(strings.GENERIC_EMPTY_STR, homeController.this), M_RATE_APP);
                }
            }
            else if(e_type.equals(enums.etype.on_load_error)){
                pluginController.getInstance().onLanguageInvoke(Arrays.asList(homeController.this, status.sSettingLanguage, status.sSettingLanguageRegion, status.mThemeApplying), pluginEnums.eLangManager.M_SET_LANGUAGE);
                initLocalLanguage();
                mHomeViewController.onPageFinished();
                mGeckoClient.onRedrawPixel(homeController.this);
                mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false,true, false);
            }
            else if(e_type.equals(enums.etype.search_update)){
                mHomeViewController.onUpdateSearchBar(dataToStr(data.get(0),mGeckoClient.getSession().getCurrentURL()),false, true, false);
            }
            else if(e_type.equals(enums.etype.download_file_popup)){
                List<Object> mData = new ArrayList<>();
                mData.addAll(data);
                mData.add(homeController.this);
                pluginController.getInstance().onMessageManagerInvoke(mData, M_DOWNLOAD_SINGLE);
            }
            else if(e_type.equals(enums.etype.on_full_screen)){
                boolean status = (Boolean)data.get(0);
                mHomeViewController.onFullScreenUpdate(status);

                mHomeViewController.onUpdateSearchEngineBar(false, 0);
            }
            else if(e_type.equals(enums.etype.on_update_favicon)){
                dataController.getInstance().invokeImage(dataEnums.eImageCommands.M_REQUEST_IMAGE_URL,Collections.singletonList(data.get(0)));
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
            else if(e_type.equals(open_new_tab)){
                initTabCount(open_new_tab, data);
            }
            else if(e_type.equals(enums.etype.on_close_sesson)){
                if(!onCloseCurrentTab(mGeckoClient.getSession())){
                    postNewTabAnimation(true,true);
                }
            }
            else if(e_type.equals(enums.etype.on_playstore_load)){
                helperMethod.openPlayStore(dataToStr(data.get(0)).split("__")[1],homeController.this);
            }
            else if(e_type.equals(enums.etype.ON_UPDATE_TAB_TITLE)){
                if(activityContextManager.getInstance().getTabController()!=null && mTabFragment.getVisibility()==View.VISIBLE)
                    activityContextManager.getInstance().getTabController().onTabRowChanged((String) data.get(1));
            }
            else if(e_type.equals(enums.etype.M_RELOAD)){
                onReload(null);
            }
            else if(e_type.equals(enums.etype.M_UPDATE_SESSION_STATE)){
                dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_UPDATE_SESSION_STATE, data);
            }
            else if(e_type.equals(enums.etype.FINDER_RESULT_CALLBACK)){
                mHomeViewController.onUpdateFindBarCount((int)data.get(0),(int)data.get(1));
            }
            else if(e_type.equals(enums.etype.M_ON_MAIL)){
                helperMethod.sendCustomMail(homeController.this, (String)data.get(0));
            }
            else if(e_type.equals(enums.etype.M_OPEN_SESSION)){
                tabRowModel model = (tabRowModel)dataController.getInstance().invokeTab(dataEnums.eTabCommands.GET_CURRENT_TAB, null);
                if(model!=null){
                    onLoadTab(model.getSession(),true,true);
                    onLoadURL(model.getSession().getCurrentURL());
                }
            }
            else if(e_type.equals(enums.etype.M_UPDATE_PIXEL_BACKGROUND)){
                onInvokePixelGenerator();
            }
            else if(e_type.equals(enums.etype.M_INIT_PADDING)){
                mHomeViewController.initTopBarPadding();
            }
            else if(e_type.equals(enums.etype.M_RATE_COUNT)){
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_RATE_COUNT, status.sRateCount));
            }

            return null;
        }
    }
}

