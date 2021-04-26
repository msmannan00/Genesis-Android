package com.darkweb.genesissearchengine.appManager.homeManager.homeController;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.animatedColor;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.widget.progressBar.AnimatedProgressBar;
import com.example.myapplication.R;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import org.mozilla.geckoview.GeckoView;
import org.torproject.android.proxy.wrapper.orbotLocalConstants;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_DOMAIN_URL;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_HELP_URL;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_HELP_URL_CACHE;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_HELP_URL_CACHE_DARK;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_URL_CACHED;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_URL_CACHED_DARK;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_DESKTOP;

class homeViewController
{
    /*ViewControllers*/
    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;

    /*ViewControllers*/
    private com.google.android.material.appbar.AppBarLayout mAppBar;
    private ConstraintLayout mWebviewContainer;
    private AnimatedProgressBar mProgressBar;
    private editTextManager mSearchbar;
    private ConstraintLayout mSplashScreen;
    private TextView mLoadingText;
    private AdView mBannerAds = null;
    private Handler mUpdateUIHandler = null;
    private ImageButton mGatewaySplash;
    private LinearLayout mTopBar;
    private GeckoView mGeckoView;
    private Button mConnectButton;
    private Button mNewTab;
    private PopupWindow popupWindow = null;
    private View mFindBar;
    private View mSearchEngineBar;
    private EditText mFindText;
    private TextView mFindCount;
    private ConstraintLayout mTopLayout;
    private ImageButton mVoiceInput;
    private ImageButton mMenu;
    private ImageView mBlocker;
    private ImageView mBlockerFullSceen;
    private TextView mCopyright;
    private ImageButton mOrbotLogManager;
    private ConstraintLayout mInfoPortrait;
    private ConstraintLayout mInfoLandscape;
    private NestedScrollView mNestedScroll;
    private ProgressBar mProgressBarIndeterminate;
    private FragmentContainerView mTabFragment;
    private LinearLayout mTopBarContainer;
    private ImageView mSearchLock;
    private View mPopupLoadNewTab;
    private ImageView mTopBarHider;
    private ImageView mNewTabBlocker;
    private CoordinatorLayout mCoordinatorLayout;
    private ImageView mImageDivider;
    private ImageButton mPanicButton;
    private ImageView mGenesisLogo;

    /*Local Variables*/
    private Callable<String> mLogs = null;
    private boolean isLandscape = false;
    private boolean isFullScreen = false;
    private MovementMethod mSearchBarMovementMethod = null;
    private Handler mTabDialogHandler = null;
    private Runnable mTabDialogRunnable = null;

    void initialization(eventObserver.eventListener event, AppCompatActivity context, Button mNewTab, ConstraintLayout webviewContainer, TextView loadingText, AnimatedProgressBar progressBar, editTextManager searchbar, ConstraintLayout splashScreen, ImageView loading, AdView banner_ads, ImageButton gateway_splash, LinearLayout top_bar, GeckoView gecko_view, ImageView backsplash, Button connect_button, View pFindBar, EditText pFindText, TextView pFindCount, androidx.constraintlayout.widget.ConstraintLayout pTopLayout, ImageButton pVoiceInput, ImageButton pMenu, androidx.core.widget.NestedScrollView pNestedScroll, ImageView pBlocker, ImageView pBlockerFullSceen, View mSearchEngineBar, TextView pCopyright, RecyclerView pHistListView, com.google.android.material.appbar.AppBarLayout pAppBar, ImageButton pOrbotLogManager, ConstraintLayout pInfoLandscape, ConstraintLayout pInfoPortrait, ProgressBar pProgressBarIndeterminate, FragmentContainerView pTabFragment, LinearLayout pTopBarContainer, ImageView pSearchLock, View pPopupLoadNewTab, ImageView pTopBarHider, ImageView pNewTabBlocker, CoordinatorLayout mCoordinatorLayout, ImageView pImageDivider, ImageButton pPanicButton, ImageView pGenesisLogo){
        this.mContext = context;
        this.mProgressBar = progressBar;
        this.mSearchbar = searchbar;
        this.mSplashScreen = splashScreen;
        this.mLoadingText = loadingText;
        this.mWebviewContainer = webviewContainer;
        this.mBannerAds = banner_ads;
        this.mEvent = event;
        this.mGatewaySplash = gateway_splash;
        this.mTopBar = top_bar;
        this.mGeckoView = gecko_view;
        this.mConnectButton = connect_button;
        this.mNewTab = mNewTab;
        this.popupWindow = null;
        this.mFindBar = pFindBar;
        this.mFindText = pFindText;
        this.mFindCount = pFindCount;
        this.mTopLayout = pTopLayout;
        this.mVoiceInput = pVoiceInput;
        this.mMenu = pMenu;
        this.mBlocker = pBlocker;
        this.mBlockerFullSceen = pBlockerFullSceen;
        this.mSearchEngineBar = mSearchEngineBar;
        this.mCopyright = pCopyright;
        this.mAppBar = pAppBar;
        this.mOrbotLogManager = pOrbotLogManager;
        this.mInfoPortrait = pInfoPortrait;
        this.mInfoLandscape = pInfoLandscape;
        this.mNestedScroll = pNestedScroll;
        this.mProgressBarIndeterminate = pProgressBarIndeterminate;
        this.mTabFragment = pTabFragment;
        this.mTopBarContainer = pTopBarContainer;
        this.mSearchLock = pSearchLock;
        this.mPopupLoadNewTab = pPopupLoadNewTab;
        this.mTopBarHider = pTopBarHider;
        this.mNewTabBlocker = pNewTabBlocker;
        this.mCoordinatorLayout = mCoordinatorLayout;
        this.mImageDivider = pImageDivider;
        this.mPanicButton = pPanicButton;
        this.mGenesisLogo = pGenesisLogo;

        initSplashScreen();
        createUpdateUiHandler();
        recreateStatusBar();
        initTopBarPadding();
        initializeViews();
    }

    @SuppressLint("WrongConstant")
    public void initializeViews(){
        mSearchbar.setTag(R.id.msearchbarProcessing,false);
        mNestedScroll.setNestedScrollingEnabled(true);
        this.mBlockerFullSceen.setVisibility(View.GONE);
        mSearchBarMovementMethod = mSearchbar.getMovementMethod();
        mSearchbar.setMovementMethod(null);
        mTopBarContainer.getLayoutTransition().setDuration(200);

        final Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
        }, 1500);

        updateBannerAdvertStatus(false, false);
        expandTopBar();
        mBlockerFullSceen.setVisibility(View.GONE);
        mNewTab.setPressed(true);
        mNewTab.setPressed(false);


        View child = mAppBar.getChildAt(0);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) child.getLayoutParams();
        params.setScrollFlags(1);
        mAppBar.setExpanded(true,false);
        mAppBar.refreshDrawableState();
        mAppBar.invalidate();

        onClearSelections(false);
        mGeckoView.requestFocus();


        mContext.runOnUiThread(() -> {
            if(helperMethod.getScreenHeight(mContext)<1000){

                ConstraintLayout.LayoutParams newLayoutParams1 = (ConstraintLayout.LayoutParams) mImageDivider.getLayoutParams();
                newLayoutParams1.bottomMargin = helperMethod.pxFromDp(200);
                mImageDivider.setLayoutParams(newLayoutParams1);


                ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) mGenesisLogo.getLayoutParams();
                newLayoutParams.topMargin = helperMethod.pxFromDp(80);
                mGenesisLogo.setLayoutParams(newLayoutParams);
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void initTopBarPadding(){
        if(!status.sFullScreenBrowsing){
            int paddingDp = 110;
            if(isFullScreen){
                paddingDp = 60;
            }else {
                if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    paddingDp = 0;
                }else {
                    mGeckoView.setPadding(0,0,0,0);
                    View child = mAppBar.getChildAt(0);
                    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) child.getLayoutParams();
                    params.setScrollFlags(0);
                    return;
                }
            }
            float density = mContext.getResources().getDisplayMetrics().density;
            int paddingPixel = (int)(paddingDp * density);
            mGeckoView.setPadding(0,0,0,paddingPixel);

            View child = mAppBar.getChildAt(0);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) child.getLayoutParams();
            params.setScrollFlags(0);

        }else {
            int paddingDp = 0;
            if(isFullScreen){
                paddingDp = 60;
            }
            float density = mContext.getResources().getDisplayMetrics().density;
            int paddingPixel = (int)(paddingDp * density);
            mGeckoView.setPadding(0,0,0,paddingPixel);

            View child = mAppBar.getChildAt(0);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) child.getLayoutParams();
            params.setScrollFlags(1);
        }
    }

    public void initSplashOrientation(){
        if(isLandscape){
            this.mInfoPortrait.setVisibility(View.GONE);
            this.mInfoLandscape.setVisibility(View.VISIBLE);
            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue_splash));
        }else {
            this.mInfoPortrait.setVisibility(View.VISIBLE);
            this.mInfoLandscape.setVisibility(View.GONE);
            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
        }
    }

    public void onShowLoadTabDialog() {
        mPopupLoadNewTab.findViewById(R.id.pBlockerUndo).setVisibility(View.GONE);
        mPopupLoadNewTab.animate().cancel();
        mPopupLoadNewTab.setAlpha(0);
        mPopupLoadNewTab.setVisibility(View.VISIBLE);
        mPopupLoadNewTab.animate().setDuration(350).alpha(1);

        if(mTabDialogHandler!=null){
            mTabDialogHandler.removeCallbacksAndMessages(null);
        }

        mTabDialogHandler = new Handler();
        mTabDialogRunnable = this::onHideLoadTabDialog;
        mTabDialogHandler.postDelayed(mTabDialogRunnable, 7500);
    }

    public void onHideLoadTabDialog() {
        mPopupLoadNewTab.findViewById(R.id.pBlockerUndo).setVisibility(View.VISIBLE);
        mPopupLoadNewTab.animate().cancel();

        mPopupLoadNewTab.animate().setDuration(350).alpha(0).withEndAction(() -> {
            mPopupLoadNewTab.setVisibility(View.GONE);
        });
    }

    public void onShowTabContainer(){
        onHideLoadTabDialog();
        if(mTabFragment.getAlpha()==0 || mTabFragment.getAlpha()==1){

            onUpdateStatusBarTheme(null, false);
            mTabFragment.animate().cancel();
            mTabFragment.setAlpha(0);
            mTabFragment.setVisibility(View.VISIBLE);
            mTabFragment.animate().alpha(1).setStartDelay(100).setDuration(150).withEndAction(() -> mNewTab.setPressed(false));
        }
    }

    public void onHideTabContainer(){
        onHideLoadTabDialog();
        if(mTabFragment.getAlpha()>0 || mTabFragment.getVisibility()!=View.GONE){
            Log.i("SUPERFUCK4","SUPERFUCK");
            mNewTab.setPressed(false);
            new Handler().postDelayed(() ->
            {
                mTopBarHider.animate().alpha(0).setDuration(250).setStartDelay(0).withEndAction(() -> mTopBarHider.setVisibility(View.GONE));
                mEvent.invokeObserver(null, enums.etype.M_UPDATE_THEME);
            }, 250);

            mTabFragment.animate().cancel();
            mTabFragment.animate().setDuration(100).alpha(0f).withEndAction(() -> {
                mTabFragment.setVisibility(View.GONE);
                mEvent.invokeObserver(null, enums.etype.M_UPDATE_PIXEL_BACKGROUND);
            });
        }
    }

    public int getSearchLogo(){
        switch (status.sSettingSearchStatus) {
            case constants.CONST_BACKEND_GENESIS_URL:
                return R.drawable.genesis;
            case constants.CONST_BACKEND_GOOGLE_URL:
                return R.drawable.google;
            case constants.CONST_BACKEND_DUCK_DUCK_GO_URL:
                return R.drawable.duckduckgo;
            case constants.CONST_BACKEND_BING_URL:
                return R.drawable.bing;
            default:
                return R.drawable.wikipedia;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onUpdateSearchIcon(int mStatus){
        try {
            if(mStatus==0){
                mSearchLock.setColorFilter(null);
                mSearchLock.clearColorFilter();
                mSearchLock.setImageTintList(null);

                mSearchLock.setImageDrawable(mContext.getResources().getDrawable(getSearchLogo()));
            }
            else if(mStatus==1){
                if(!(boolean)mSearchLock.getTag(R.id.themed)){
                    mSearchLock.setColorFilter(ContextCompat.getColor(mContext, R.color.c_lock_tint));
                    mSearchLock.setImageDrawable(helperMethod.getDrawableXML(mContext,R.xml.ic_baseline_lock));
                }else {
                    mSearchLock.setImageDrawable(helperMethod.getDrawableXML(mContext,R.xml.ic_baseline_lock));
                }
            }
            else if(mStatus==2){
                mSearchLock.setColorFilter(ContextCompat.getColor(mContext, R.color.c_icon_tint));
                mSearchLock.setImageDrawable(helperMethod.getDrawableXML(mContext,R.xml.ic_baseline_browser));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void initSearchBarFocus(boolean pStatus, boolean mIsKeyboardOpened){
        if(!pStatus){
            this.mVoiceInput.animate().setDuration(0).alpha(0).withEndAction(() -> {

                mVoiceInput.setClickable(true);
                mVoiceInput.setFocusable(true);
                onUpdateSearchIcon(1);
                mVoiceInput.setVisibility(View.GONE);
                mNewTab.setVisibility(View.VISIBLE);
                mMenu.setVisibility(View.VISIBLE);
                mSearchbar.setFadingEdgeLength(helperMethod.pxFromDp(20));
                mSearchbar.setMovementMethod(null);

                if(status.sSettingLanguageRegion.equals("Ur")){
                    mSearchbar.setPadding(helperMethod.pxFromDp(17),0,mSearchbar.getPaddingRight(),0);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mSearchbar.getLayoutParams();
                    params.leftMargin = helperMethod.pxFromDp(5);
                }else {
                    mSearchbar.setPadding(mSearchbar.getPaddingLeft(),0,helperMethod.pxFromDp(5),0);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mSearchbar.getLayoutParams();
                    params.rightMargin = helperMethod.pxFromDp(10);
                }

            });
        }else {

            onUpdateSearchIcon(0);
            mSearchbar.setMovementMethod(mSearchBarMovementMethod);
            mSearchbar.setFadingEdgeLength(helperMethod.pxFromDp(0));

            final Handler handler = new Handler();
            handler.postDelayed(() ->
            {
                Drawable drawable;
                Resources res = mContext.getResources();
                try {
                    if(status.sSettingEnableVoiceInput){
                        drawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_keyboard_voice));
                    }else {
                        drawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_cancel));
                    }
                    mVoiceInput.setImageDrawable(drawable);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                mVoiceInput.setVisibility(View.VISIBLE);
                mVoiceInput.setClickable(true);
                mVoiceInput.setFocusable(true);
            }, 0);

            mNewTab.setVisibility(View.GONE);
            this.mMenu.setVisibility(View.GONE);

            if(status.sSettingLanguageRegion.equals("Ur")){
                mSearchbar.setPadding(helperMethod.pxFromDp(45),0,mSearchbar.getPaddingRight(),0);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mSearchbar.getLayoutParams();
                params.leftMargin = helperMethod.pxFromDp(17);
            }else {
                mSearchbar.setPadding(mSearchbar.getPaddingLeft(),0,helperMethod.pxFromDp(45),0);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mSearchbar.getLayoutParams();
                params.rightMargin = helperMethod.pxFromDp(17);
            }

            if(!mIsKeyboardOpened){
                mSearchbar.requestFocus();
                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mSearchbar, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    void initTab(int count, boolean pForced, enums.etype pEvent, List<Object> pData){
        if(!pForced){
            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(mNewTab,
                    PropertyValuesHolder.ofFloat("scaleX", 1, 0.70f, 1),
                    PropertyValuesHolder.ofFloat("scaleY", 1, 0.70f, 1));
            scaleDown.setDuration(250);
            scaleDown.start();

            scaleDown.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator arg0) {
                }

                @Override
                public void onAnimationRepeat(Animator arg0) {
                }

                @Override
                public void onAnimationEnd(Animator arg0) {
                    if(pEvent!=null){
                        mEvent.invokeObserver(pData, pEvent);
                    }
                }

                @Override
                public void onAnimationCancel(Animator arg0) {
                }
            });
        }

        if(count==0){
            count=1;
        }
        mNewTab.setText((count+strings.GENERIC_EMPTY_STR));

    }

    public void recreateStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
            }
            else {
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
            }
        }
    }

    public void initStatusBarColor(boolean mInstant) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            animatedColor oneToTwo = new animatedColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue), ContextCompat.getColor(mContext, R.color.green_dark_v2));

            int mDelay = 800;
            if(status.mThemeApplying || mInstant){
                mDelay = 0;
            }

            ValueAnimator animator = ObjectAnimator.ofFloat(0f, 1f);
            animator.setDuration(300).setStartDelay(mDelay);
            animator.addUpdateListener(animation ->
            {
                float v = (float) animation.getAnimatedValue();
                mContext.getWindow().setStatusBarColor(oneToTwo.with(v));
                mContext.getWindow().setStatusBarColor(oneToTwo.with(v));
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
                }
            });
            animator.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
                        mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }else {
                        View decorView = mContext.getWindow().getDecorView();
                        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                }
            });

            animator.start();
        }else {
            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
        }

    }

    public void initSplashLoading(){

        mLoadingText.setAlpha(0);
        mLoadingText.setVisibility(View.VISIBLE);
        mLoadingText.animate().setStartDelay(0).alpha(1);

        mProgressBarIndeterminate.setVisibility(View.VISIBLE);
        mProgressBarIndeterminate.animate().alpha(1);
        mConnectButton.setEnabled(false);
        mSplashScreen.setEnabled(false);
        mBlocker.setClickable(true);
        mBlocker.setFocusable(true);
    }

    void initHomePage(){
        mConnectButton.setEnabled(false);
        mSplashScreen.setEnabled(false);
        mOrbotLogManager.setEnabled(false);
        mPanicButton.setEnabled(false);

        final Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            mOrbotLogManager.setEnabled(true);
        }, 700);

        mConnectButton.animate().setDuration(350).alpha(0.4f).withEndAction(() -> {
            mCopyright.setVisibility(View.GONE);
            initSplashLoading();
        });
        mGatewaySplash.animate().setDuration(350).alpha(0.4f);
        mPanicButton.animate().setDuration(250).alpha(0f);
    }

    private void initSplashScreen(){

        mIsAnimating = false;
        helperMethod.hideKeyboard(mContext);
        mSearchLock.setTag(R.id.themed,false);
        mAppBar.setTag(R.id.expandableBar,true);

        if(!status.mThemeApplying){
            mSearchbar.setEnabled(false);
        }else {
            mSearchbar.setEnabled(true);
        }

        View root = mSearchbar.getRootView();
        root.setBackgroundColor(ContextCompat.getColor(mContext, R.color.c_background_keyboard));


    }

    void initProxyLoading(Callable<String> logs){
        this.mLogs = logs;

        if(mSplashScreen.getVisibility()==View.VISIBLE){
            new Thread(){
                public void run(){
                    AppCompatActivity temp_context = mContext;
                    while (!orbotLocalConstants.mIsTorInitialized || !orbotLocalConstants.mNetworkState){
                        try
                        {
                            sleep(1000);
                            mEvent.invokeObserver(Collections.singletonList(status.sSettingSearchStatus), enums.etype.recheck_orbot);
                            if(temp_context.isDestroyed()){
                                return;
                            }
                            startPostTask(messages.MESSAGE_UPDATE_LOADING_TEXT);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    if(!status.sSettingIsAppStarted){
                        startPostTask(messages.MESSAGE_ON_URL_LOAD);
                        mContext.runOnUiThread(() -> splashScreenDisable());
                    }else {
                        mContext.runOnUiThread(() -> mEvent.invokeObserver(null, enums.etype.ON_LOAD_TAB_ON_RESUME));
                    }
                }
            }.start();
        }
    }

    /*-------------------------------------------------------PAGE UI Methods-------------------------------------------------------*/

    void onPageFinished(){
        mSearchbar.setEnabled(true);
        mProgressBar.bringToFront();
        mSplashScreen.bringToFront();
        splashScreenDisable();
    }

    public void splashScreenDisableInstant() {
        mSplashScreen.setAlpha(0f);
        mSplashScreen.setVisibility(View.GONE);
        mSplashScreen.setVisibility(View.GONE);
        mBlocker.setEnabled(false);
    }
//C:\Users\MSi\AppData\Local\Android\ndk
    private boolean mIsAnimating = false;
    public void splashScreenDisable(){
        mTopBar.setAlpha(1);

        if(mSplashScreen.getAlpha()==1){
            if(!mIsAnimating){
                mIsAnimating = true;
                triggerPostUI();
                mProgressBar.setVisibility(View.GONE);
                mSplashScreen.animate().cancel();
                onClearSelections(false);
                mGeckoView.requestFocus();
                mProgressBarIndeterminate.animate().setStartDelay(300).setDuration(250).alpha(0).withEndAction(() -> mSplashScreen.animate().setDuration(350).setStartDelay(200).alpha(0).withEndAction(() -> {
                    mProgressBarIndeterminate.setVisibility(View.GONE);
                    mSplashScreen.setClickable(false);
                    mSplashScreen.setFocusable(false);
                    mProgressBarIndeterminate.setVisibility(View.GONE);
                    mSearchbar.setEnabled(true);
                    mBlocker.setEnabled(false);

                    mProgressBarIndeterminate.setVisibility(View.GONE);
                    mBlocker.setVisibility(View.GONE);
                    mGatewaySplash.setVisibility(View.GONE);
                    mConnectButton.setVisibility(View.GONE);
                    mPanicButton.setVisibility(View.GONE);

                    mEvent.invokeObserver(null, enums.etype.M_CACHE_UPDATE_TAB);
                    mEvent.invokeObserver(null, enums.etype.M_SPLASH_DISABLE);
                }));
                mEvent.invokeObserver(null, enums.etype.M_WELCOME_MESSAGE);
                mOrbotLogManager.setClickable(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    initStatusBarColor(false);
                }
                status.sSettingIsAppRestarting = true;
            }
        }
    }

    public void onUpdateToolbarTheme(){
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
    }

    private void triggerPostUI(){
        expandTopBar();
        if(mProgressBar.getProgress()>0 && mProgressBar.getProgress()<10000){
            mProgressBar.animate().setStartDelay(0).alpha(1);
        }
    }

    public void expandTopBar(){
        Object mTag = mAppBar.getTag(R.id.expandableBar);
        if(mTag!=null && (boolean) mTag){
            mAppBar.setExpanded(true,true);
        }
    }

    /*-------------------------------------------------------Helper Methods-------------------------------------------------------*/

    void onOpenMenu(View view, boolean canGoForward, boolean isLoading, int userAgent, String mURL){

        if(popupWindow!=null){
            popupWindow.dismiss();
        }

        LayoutInflater layoutInflater
                = (LayoutInflater) mContext
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View popupView = layoutInflater.inflate(R.layout.popup_side_menu, null);


        int height = helperMethod.getScreenHeight(mContext)*90 /100;

        popupWindow = new PopupWindow(
                popupView,
                ActionMenuView.LayoutParams.WRAP_CONTENT,
                ActionMenuView.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        View parent = view.getRootView();
        popupWindow.setAnimationStyle(R.style.popup_window_animation);
        if(isLandscape){
            helperMethod.hideKeyboard(mContext);
            popupWindow.setHeight(height);
        }

        if(status.sSettingLanguageRegion.equals("Ur") || status.sSettingLanguage.equals("default") && status.mSystemLocale.getLanguage().equals("ur")){
            popupWindow.showAtLocation(parent, Gravity.TOP|Gravity.START,0,0);
        }else {
            popupWindow.showAtLocation(parent, Gravity.TOP|Gravity.END,0,0);
        }

        ImageButton back = popupView.findViewById(R.id.menu22);
        ImageButton close = popupView.findViewById(R.id.menu20);
        ImageButton mRefresh = popupView.findViewById(R.id.menu21);
        ImageButton mDownload = popupView.findViewById(R.id.menuItem25);
        CheckBox desktop = popupView.findViewById(R.id.menu27);
        LinearLayout newTab = popupView.findViewById(R.id.menu11);
        desktop.setChecked(userAgent==USER_AGENT_MODE_DESKTOP);

        String mExtention = helperMethod.getMimeType(mURL, mContext);
        if(mExtention == null || mExtention.equals("text/html") || mExtention.equals("application/vnd.ms-htmlhelp") || mExtention.equals("application/vnd.sun.xml.writer") || mExtention.equals("application/vnd.sun.xml.writer.global") || mExtention.equals("application/vnd.sun.xml.writer.template") || mExtention.equals("application/xhtml+xml")){
            mDownload.setEnabled(false);
            mDownload.setColorFilter(Color.argb(255, 191, 191, 191));
        }

        if(!canGoForward){
           back.setEnabled(false);
           back.setColorFilter(Color.argb(255, 191, 191, 191));
        }
        if(!isLoading){
           close.setVisibility(View.GONE);
           mRefresh.setVisibility(View.VISIBLE);
        }else {
            close.setVisibility(View.VISIBLE);
            mRefresh.setVisibility(View.GONE);
        }

        newTab.setClickable(false);
        close.setClickable(false);
        new Handler().postDelayed(() ->
        {
            newTab.setClickable(true);
            close.setClickable(true);
        }, 300);

    }

    void downloadNotification(String message, enums.etype e_type){

        if(popupWindow!=null){
            popupWindow.dismiss();
        }

        LayoutInflater layoutInflater
                = (LayoutInflater) mContext
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("InflateParams") final View popupView = layoutInflater.inflate(R.layout.notification_menu, null);
        popupWindow = new PopupWindow(
                popupView,
                ActionMenuView.LayoutParams.MATCH_PARENT,
                ActionMenuView.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        View parent = mGeckoView.getRootView();
        popupWindow.setAnimationStyle(R.style.popup_window_animation);


        if(message.length()>30){
            message = message.substring(message.length()-20);
        }

        TextView notification_message = popupView.findViewById(R.id.notification_message);
        notification_message.setText(message);

        Button btn = popupView.findViewById(R.id.notification_event);
        btn.setOnClickListener(v ->
        {
            mEvent.invokeObserver(Collections.singletonList(status.sSettingSearchStatus), enums.etype.progress_update);
            popupWindow.dismiss();
        });

        popupWindow.showAtLocation(parent, Gravity.BOTTOM|Gravity.START,0,-10);
    }

    void closeMenu(){
        if(popupWindow!=null){
            popupWindow.dismiss();
        }
    }

    public void onMoveTopBar(int pPosition){
    }

    public void setOrientation(boolean status){
        isLandscape = status;
    }

    void onSetBannerAdMargin(boolean status,boolean isAdLoaded){
        if(isAdLoaded){
            if(status && !isLandscape && !isFullScreen){
                mBannerAds.setVisibility(View.VISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
                    mWebviewContainer.clearAnimation();
                    mProgressBar.bringToFront();
                }, 250);
            }else{
                mBannerAds.setVisibility(View.GONE);
            }
        }
    }

    void updateBannerAdvertStatus(boolean status, boolean pIsAdvertLoaded){
        if(status && pIsAdvertLoaded){
            if(mBannerAds.getAlpha()==0){
                mBannerAds.animate().cancel();
                mBannerAds.setAlpha(0);
                mBannerAds.animate().alpha(1);
                mBannerAds.setVisibility(View.VISIBLE);
            }
            onSetBannerAdMargin(true,true);
        }else{
            if(mBannerAds.getAlpha()==1){
                mBannerAds.animate().cancel();
                mBannerAds.animate().alpha(0).withEndAction(() -> mBannerAds.setVisibility(View.GONE));
            }
            onSetBannerAdMargin(false,true);
        }
    }

    private Handler searchBarUpdateHandler = new Handler();
    private String handlerLocalUrl = "";
    void onUpdateSearchBar(String url,boolean showProtocol, boolean pClearText, boolean pBypassFocus){

        if(url.startsWith(CONST_GENESIS_URL_CACHED) || url.startsWith(CONST_GENESIS_URL_CACHED_DARK)){
            mSearchbar.setTag(R.id.msearchbarProcessing,true);
            url = CONST_GENESIS_DOMAIN_URL;
        }
        else if(url.startsWith(CONST_GENESIS_HELP_URL_CACHE) || url.startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)){
            mSearchbar.setTag(R.id.msearchbarProcessing,true);
            url = CONST_GENESIS_HELP_URL;
        }
        else if(helperMethod.getHost(helperMethod.completeURL(url)).contains("genesis") || helperMethod.getHost(helperMethod.completeURL(url)).contains("boogle")){
            mSearchbar.setTag(R.id.msearchbarProcessing,true);
        }else {
            mSearchbar.setTag(R.id.msearchbarProcessing,false);
        }
        Log.i("FUCK::5",url);
        if(!mSearchbar.hasFocus() || pClearText || pBypassFocus){
            if(mSearchEngineBar.getVisibility() == View.GONE || pBypassFocus){
                int delay = 0;
                 handlerLocalUrl = url;

                if(searchBarUpdateHandler.hasMessages(100)){
                    return;
                }

                searchBarUpdateHandler.sendEmptyMessage(100);
                searchBarUpdateHandler.postDelayed(() ->
                {
                    searchBarUpdateHandler.removeMessages(100);
                    triggerUpdateSearchBar(handlerLocalUrl,showProtocol, pClearText);
                    mSearchbar.setTag(R.id.msearchbarProcessing,false);
                }, delay);
            }
        }
    }

    public void onUpdateFindBarCount(int index, int total)
    {
        if(total==0){
            mFindCount.setText("0/0");
            mFindCount.setTextColor(ContextCompat.getColor(mContext, R.color.dark_red_soft));
        }else {
            mFindCount.setText((total + "/" + index));
            mFindCount.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_v6));
        }
    }

    public void onUpdateStatusBarTheme(String pTheme, boolean mForced)
    {
        if(mSplashScreen.getAlpha()<=0 && (status.sTheme != enums.Theme.THEME_DARK && mTabFragment.getAlpha()<=0 || mForced)){
            if(status.sDefaultNightMode && status.sTheme == enums.Theme.THEME_DEFAULT){
                return;
            }
            int mColor;
            try{
                mColor = Color.parseColor(pTheme);
            }catch (Exception ex){
                mColor = -1;
            }

            if(!mSearchbar.isFocused() && pTheme!=null && status.sToolbarTheme && mColor!=-1 && helperMethod.getColorDensity(mColor)<0.80 && status.sTheme != enums.Theme.THEME_DARK){
                mTopBar.setBackgroundColor(mColor);
                mSearchbar.setTextColor(helperMethod.invertedGrayColor(mColor));
                mSearchbar.setHintTextColor(helperMethod.invertedGrayColor(mColor));
                mNewTab.setTextColor(helperMethod.invertedGrayColor(mColor));

                GradientDrawable mGradientDrawable = new GradientDrawable();
                mGradientDrawable.setColor(ColorUtils.blendARGB(helperMethod.invertedShadeColor(mColor,0.90f), Color.BLACK, 0.2f));
                mGradientDrawable.setCornerRadius(helperMethod.pxFromDp(7));

                GradientDrawable gradientDrawable1 = new GradientDrawable();
                gradientDrawable1.setColor(ColorUtils.blendARGB(helperMethod.invertedShadeColor(mColor,0.50f), Color.BLACK, 0.2f));
                gradientDrawable1.setCornerRadius(helperMethod.pxFromDp(4));

                GradientDrawable gradientDrawable3 = new GradientDrawable();
                gradientDrawable3.setColor(ColorUtils.blendARGB(helperMethod.invertedShadeColor(mColor,0.50f), Color.BLACK, 0.2f));
                gradientDrawable3.setCornerRadius(helperMethod.pxFromDp(4));
                gradientDrawable3.setStroke(helperMethod.pxFromDp(2), helperMethod.invertedGrayColor(mColor));

                GradientDrawable gradientDrawable2 = new GradientDrawable();
                gradientDrawable2.setColor(ColorUtils.blendARGB(helperMethod.invertedShadeColor(mColor,0.90f), Color.BLACK, 0.2f));
                gradientDrawable2.setCornerRadius(helperMethod.pxFromDp(4));
                gradientDrawable2.setStroke(helperMethod.pxFromDp(2), helperMethod.invertedGrayColor(mColor));

                StateListDrawable states = new StateListDrawable();
                InsetDrawable mInsetDrawable1 = new InsetDrawable(gradientDrawable3, helperMethod.pxFromDp(8), helperMethod.pxFromDp(8), helperMethod.pxFromDp(8), helperMethod.pxFromDp(8));
                InsetDrawable mInsetDrawable2 = new InsetDrawable(gradientDrawable2, helperMethod.pxFromDp(8), helperMethod.pxFromDp(8), helperMethod.pxFromDp(8), helperMethod.pxFromDp(8));

                states.addState(new int[] {android.R.attr.state_pressed}, mInsetDrawable1);
                states.addState(new int[] { }, mInsetDrawable2);

                mNewTab.setBackground(states);

                mMenu.setColorFilter(helperMethod.invertedGrayColor(mColor));
                mVoiceInput.setColorFilter(helperMethod.invertedGrayColor(mColor));
                mVoiceInput.setBackground(mGradientDrawable);
                mSearchLock.setColorFilter(helperMethod.invertedGrayColor(mColor));
                mSearchLock.setTag(R.id.themed,true);
                gradientDrawable1.setCornerRadius(helperMethod.pxFromDp(7));
                gradientDrawable1.setStroke(helperMethod.pxFromDp(2), mColor);
                gradientDrawable1.setColor(ColorUtils.blendARGB(helperMethod.invertedShadeColor(mColor,0.90f), Color.BLACK, 0.2f));
                mSearchbar.setBackground(gradientDrawable1);
                mSearchbar.setHintTextColor(ColorUtils.blendARGB(helperMethod.invertedShadeColor(mColor,0.10f), Color.BLACK, 0.2f));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mContext.getWindow().setStatusBarColor(Color.parseColor(pTheme));
                }else {
                    mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
                }

                if(helperMethod.isColorDark(mColor)){
                    mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }else {
                    View decorView = mContext.getWindow().getDecorView(); //set status background black
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                mTopBarHider.setBackgroundColor(mColor);
            }
            else{
                mSearchLock.setTag(R.id.themed,false);
                mTopBar.setBackground(ContextCompat.getDrawable(mContext, R.color.c_background));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
                }else {
                    mContext.getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
                    mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
                }

                GradientDrawable gradientDrawable1 = new GradientDrawable();
                gradientDrawable1.setColor(ContextCompat.getColor(mContext, R.color.c_edittext_background));
                gradientDrawable1.setCornerRadius(helperMethod.pxFromDp(7));
                gradientDrawable1.setStroke(helperMethod.pxFromDp(2), ContextCompat.getColor(mContext, R.color.c_edittext_background));
                mSearchbar.setBackground(gradientDrawable1);

                Drawable drawable;
                Resources res = mContext.getResources();
                try {
                    drawable = Drawable.createFromXml(res, res.getXml(R.xml.gx_generic_tab_button));
                    mNewTab.setBackground(drawable);

                    Drawable drawableTemp = Drawable.createFromXml(res, res.getXml(R.xml.gx_generic_input));
                    mVoiceInput.setBackground(drawableTemp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                mNewTab.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_v1));
                mMenu.setColorFilter(ContextCompat.getColor(mContext, R.color.c_navigation_tint));
                mVoiceInput.setColorFilter(ContextCompat.getColor(mContext, R.color.c_navigation_tint));
                mSearchbar.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_v1));
                mSearchbar.setHintTextColor(ContextCompat.getColor(mContext, R.color.c_text_v2));

                if(!mSearchbar.isFocused()){
                    onUpdateSearchIcon(1);
                }

                if(status.sTheme != enums.Theme.THEME_DARK || (status.sDefaultNightMode && status.sTheme != enums.Theme.THEME_DEFAULT)){
                    mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }else {
                    View decorView = mContext.getWindow().getDecorView();
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }

                mTopBarHider.setBackground(ContextCompat.getDrawable(mContext, R.color.c_background));
            }
        }
    }

    public void onUpdateFindBar(boolean pStatus)
    {
        mFindBar.animate().cancel();
        if(pStatus){
            mFindBar.setVisibility(View.VISIBLE);
            mFindBar.setAlpha(1);
            mFindText.requestFocus();
            mFindCount.setText("0/0");
            mFindCount.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_v6));
            final Handler handler = new Handler();
            handler.postDelayed(() ->
            {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
            }, 300);
        }else {
            mFindText.clearFocus();
            helperMethod.hideKeyboard(mContext);
            mFindBar.animate().alpha(0).withEndAction(() -> {
                mFindCount.setText(strings.GENERIC_EMPTY_STR);
                mFindText.setText(strings.GENERIC_EMPTY_STR);
                mFindBar.setVisibility(View.GONE);
            });
        }
    }

    @SuppressLint("WrongConstant")
    public void onUpdateSearchEngineBar(boolean pStatus, int delay)
    {
        if(pStatus){
            if(mSearchEngineBar.getAlpha() == 0 || mSearchEngineBar.getVisibility() == View.GONE && mSplashScreen.getAlpha()<=0){
                mSearchEngineBar.animate().cancel();
                mSearchEngineBar.setAlpha(0f);
                mSearchEngineBar.animate().setDuration(delay).alpha(1);
                mSearchEngineBar.setVisibility(View.VISIBLE);
                onUpdateStatusBarTheme(null, false);

                View child = mAppBar.getChildAt(0);
                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) child.getLayoutParams();
                params.setScrollFlags(0);
            }
        }else {
                View child = mAppBar.getChildAt(0);
                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) child.getLayoutParams();
                params.setScrollFlags(1);
                mAppBar.refreshDrawableState();
                mAppBar.invalidate();

                mSearchEngineBar.animate().setDuration(delay).alpha(0).withEndAction(() -> {
                mSearchEngineBar.animate().cancel();
                mSearchEngineBar.setAlpha(0f);
                mSearchEngineBar.setVisibility(View.GONE);
                mEvent.invokeObserver(null, enums.etype.M_UPDATE_THEME);
                mEvent.invokeObserver(null, enums.etype.M_RESET_SUGGESTION);
            });
        }

    }

    private void triggerUpdateSearchBar(String url, boolean showProtocol, boolean pClearText){
        if (mSearchbar == null || url==null)
        {
            return;
        }

        if(!showProtocol){
            url=url.replace("https://","");
            url=url.replace("http://","");
        }

        url = url.replace("boogle.store","genesis.onion");
        boolean isTextSelected = false;

        if(mSearchbar.isSelected()){
            isTextSelected = true;
        }


        if(url.length()<=300){
            url = removeEndingSlash(url);
            mSearchbar.setText(helperMethod.urlDesigner(url, mContext, mSearchbar.getCurrentTextColor()));
            mSearchbar.selectAll();

            if(isTextSelected){
                mSearchbar.selectAll();
            }

            mSearchbar.setSelection(0);
        }else {
            url = removeEndingSlash(url);
            mSearchbar.setText(url);
        }

        if(mSearchbar.isFocused()){
            mSearchbar.setSelection(mSearchbar.getText().length());
            mSearchbar.selectAll();
        }

        if(mSearchbar.getText().toString().equals("loading")){
            mSearchbar.setText("about:blank");
        }
    }

    private String removeEndingSlash(String url){
        return helperMethod.removeLastSlash(url);
    }

    void onNewTab(){
        if(!mSearchbar.isFocused()){
            mSearchbar.requestFocus();
            mSearchbar.selectAll();
        }
    }

    void onUpdateLogs(String log){
        mLoadingText.setText(log);
    }

    void progressBarReset(){
        mProgressBar.setProgress(5);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public void onFirstPaint(){
        mGeckoView.setForeground(ContextCompat.getDrawable(mContext, R.color.clear_alpha));
    }

    public void onSessionReinit(){
        mGeckoView.setForeground(ContextCompat.getDrawable(mContext, R.color.c_background));
    }

    void onProgressBarUpdate(int value, boolean mForced){
        if(mSearchbar.getText().toString().equals("genesis.onion") && !mForced || (boolean)mSearchbar.getTag(R.id.msearchbarProcessing)){
            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.animate().cancel();

        if(value != mProgressBar.getProgress()){
            if(value<=5 && value>0){
                mProgressBar.setProgress(5);
            }else {
                mProgressBar.setProgress(value);
            }
            if(value >= 100 || value<=0){
                mProgressBar.animate().alpha(0).withEndAction(() -> mProgressBar.setProgress(0));
            }else {
                mProgressBar.setAlpha(1);
            }
        }
    }

    public void onNewTabAnimation(List<Object> data, Object e_type){
        mGeckoView.setPivotX(0);
        mGeckoView.setPivotY(0);

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(mGeckoView,
                PropertyValuesHolder.ofFloat("scaleX", 1, 0.8f),
                PropertyValuesHolder.ofFloat("scaleY", 1, 0.8f));

        mNewTabBlocker.setVisibility(View.VISIBLE);
        ObjectAnimator alpha = ObjectAnimator.ofPropertyValuesHolder(mNewTabBlocker,
                PropertyValuesHolder.ofFloat("alpha", 0, 1f));

        scaleDown.setDuration(150);
        alpha.setDuration(150);

        scaleDown.start();
        alpha.start();

        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
            }

            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                mEvent.invokeObserver(data, e_type);
                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(mGeckoView,
                        PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1f),
                        PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1f));

                scaleDown.setDuration(150);
                scaleDown.setStartDelay(0);
                mNewTabBlocker.animate().setStartDelay(250).setDuration(150).alpha(0f).withEndAction(() -> {
                    mNewTabBlocker.setVisibility(View.GONE);
                });
                scaleDown.start();
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mEvent.invokeObserver(data, e_type);
                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(mGeckoView,
                        PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1f),
                        PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1f));

                scaleDown.setDuration(150);
                scaleDown.setStartDelay(0);
                mNewTabBlocker.animate().setStartDelay(250).setDuration(150).alpha(0f).withEndAction(() -> {
                    mNewTabBlocker.setVisibility(View.GONE);
                });
                scaleDown.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    void onClearSelections(boolean hideKeyboard){
        mSearchbar.setFocusable(false);
        mSearchbar.setFocusableInTouchMode(true);
        mSearchbar.setFocusable(true);
        if(hideKeyboard){
            helperMethod.hideKeyboard(mContext);
        }
    }

    private void disableEnableControls(boolean enable, ViewGroup vg){
        vg.setEnabled(enable); // the point that I was missing
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            child.setClickable(enable);
            if (child instanceof ViewGroup){
                disableEnableControls(enable, (ViewGroup)child);
            }
        }
    }

    private int defaultFlag = 0;
    void onFullScreenUpdate(boolean pStatus){
        int value = !pStatus ? 1 : 0;

        if(pStatus) {
            isFullScreen = true;
        }else {
            this.mBlockerFullSceen.setVisibility(View.VISIBLE);
            this.mBlockerFullSceen.setAlpha(1f);
            isFullScreen = false;
        }

        if(pStatus){
            onProgressBarUpdate(100, false);
            this.mBlockerFullSceen.setVisibility(View.VISIBLE);
            this.mBlockerFullSceen.setAlpha(0f);
            this.mBlockerFullSceen.animate().setStartDelay(0).setDuration(200).alpha(1).withEndAction(() -> {
                mTopBar.setClickable(!pStatus);
                disableEnableControls(!pStatus, mTopBar);
                mTopBar.setAlpha(value);
                mBannerAds.setVisibility(View.GONE);

                defaultFlag = mContext.getWindow().getDecorView().getSystemUiVisibility();
                final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                mContext.getWindow().getDecorView().setSystemUiVisibility(flags);
                mContext.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

                mProgressBar.setVisibility(View.GONE);
                mTopBar.setVisibility(View.GONE);
                mBannerAds.setVisibility(View.GONE);

                NestedScrollView.MarginLayoutParams params = (NestedScrollView.MarginLayoutParams) mNestedScroll.getLayoutParams();
                params.setMargins(0, 0, 0,helperMethod.pxFromDp(60)*-1);
                mNestedScroll.setLayoutParams(params);

                com.darkweb.genesissearchengine.constants.status.sFullScreenBrowsing = false;
                initTopBarPadding();

                this.mBlockerFullSceen.animate().setStartDelay(100).setDuration(200).alpha(0).withEndAction(() -> {
                    mBlockerFullSceen.setVisibility(View.GONE);
                });
            });
        }
        else {
            mTopBar.setClickable(!pStatus);
            disableEnableControls(!pStatus, mTopBar);
            mTopBar.setAlpha(value);
            mBannerAds.setVisibility(View.GONE);

            mProgressBar.setVisibility(View.VISIBLE);
            mTopBar.setVisibility(View.VISIBLE);
            mBannerAds.setVisibility(View.GONE);
            mEvent.invokeObserver(Collections.singletonList(!isLandscape), enums.etype.on_init_ads);

            NestedScrollView.MarginLayoutParams params = (NestedScrollView.MarginLayoutParams) mNestedScroll.getLayoutParams();
            params.setMargins(0, 0, 0,0);
            mNestedScroll.setLayoutParams(params);

            com.darkweb.genesissearchengine.constants.status.sFullScreenBrowsing = (boolean) dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_FULL_SCREEN_BROWSIING,true));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(status.sTheme == enums.Theme.THEME_DARK || status.sDefaultNightMode){
                    mContext.getWindow().getDecorView().setSystemUiVisibility(0);
                }else {
                    mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }else {
                mContext.getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
            }

            this.mBlockerFullSceen.animate().setStartDelay(0).setDuration(200).alpha(0).withEndAction(() -> mBlockerFullSceen.setVisibility(View.GONE));
            mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            initTopBarPadding();
            mAppBar.setExpanded(true,false);
            mAppBar.refreshDrawableState();
            mAppBar.invalidate();
        }

    }

    void onReDraw(){
        if(mWebviewContainer.getPaddingBottom()==0){
            mWebviewContainer.setPadding(0,0,0,1);
        }
        else {
            mWebviewContainer.setPadding(0,0,0,0);
        }
    }

    void onSessionChanged(){
    }

    void onUpdateLogo(){

        switch (status.sSettingSearchStatus)
        {
        }
    }

    /*-------------------------------------------------------POST UI TASK HANDLER-------------------------------------------------------*/

    private void startPostTask(int m_id) {
        Message message = new Message();
        message.what = m_id;
        mUpdateUIHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private void createUpdateUiHandler(){
        mUpdateUIHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == messages.MESSAGE_ON_URL_LOAD)
                {
                    if(mEvent.invokeObserver(null, enums.etype.M_HOME_PAGE)==null){
                        mEvent.invokeObserver(null, enums.etype.M_PRELOAD_URL);
                        if(status.sSettingRedirectStatus.equals(strings.GENERIC_EMPTY_STR)){
                            mEvent.invokeObserver(Collections.singletonList(helperMethod.getDomainName(status.sSettingSearchStatus)), enums.etype.on_url_load);
                        }else {
                            mEvent.invokeObserver(Collections.singletonList(helperMethod.getDomainName(status.sSettingRedirectStatus)), enums.etype.on_url_load);
                        }
                    }
                    if(!status.sExternalWebsite.equals(strings.GENERIC_EMPTY_STR) ){
                        mEvent.invokeObserver(Collections.singletonList(helperMethod.getDomainName(status.sExternalWebsite)), enums.etype.open_new_tab_instant);
                    }

                    status.sSettingIsAppStarted = true;
                }
                if(msg.what == messages.MESSAGE_UPDATE_LOADING_TEXT)
                {
                    if(mLogs !=null)
                    {
                        try
                        {
                            mLogs.call();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                if(msg.what == messages.MESSAGE_PROGRESSBAR_VALIDATE)
                {
                }
            }
        };
    }


}