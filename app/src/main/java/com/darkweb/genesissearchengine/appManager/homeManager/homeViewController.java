package com.darkweb.genesissearchengine.appManager.homeManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.animatedColor;
import com.darkweb.genesissearchengine.helperManager.autoCompleteAdapter;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.widget.progressBar.AnimatedProgressBar;
import com.example.myapplication.R;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import org.mozilla.geckoview.GeckoView;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.Callable;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_DESKTOP;

class homeViewController
{
    /*ViewControllers*/
    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;

    /*ViewControllers*/
    private FrameLayout mWebviewContainer;
    private AnimatedProgressBar mProgressBar;
    private AutoCompleteTextView mSearchbar;
    private ConstraintLayout mSplashScreen;
    private ImageView mLoading;
    private TextView mLoadingText;
    private AdView mBannerAds = null;
    private Handler mUpdateUIHandler = null;
    private ImageButton mGatewaySplash;
    private LinearLayout mTopBar;
    private GeckoView mGeckoView;
    private ImageView mBackSplash;
    private Button mConnectButton;
    private Button mNewTab;
    private PopupWindow popupWindow = null;
    private View mFindBar;
    private View mSearchEngineBar;
    private EditText mFindText;
    private TextView mFindCount;
    private FrameLayout mTopLayout;
    private ImageButton mVoiceInput;
    private ImageButton mMenu;
    private FrameLayout mNestedScroll;
    private ImageView mBlocker;
    private ImageView mBlockerFullSceen;
    private TextView mCopyright;

    /*Local Variables*/
    private Callable<String> mLogs = null;
    private boolean isLandscape = false;

    void initialization(eventObserver.eventListener event, AppCompatActivity context, Button mNewTab, FrameLayout webviewContainer, TextView loadingText, AnimatedProgressBar progressBar, AutoCompleteTextView searchbar, ConstraintLayout splashScreen, ImageView loading, AdView banner_ads, ArrayList<historyRowModel> suggestions, ImageButton gateway_splash, LinearLayout top_bar, GeckoView gecko_view, ImageView backsplash, Button connect_button, View pFindBar, EditText pFindText, TextView pFindCount, FrameLayout pTopLayout, ImageButton pVoiceInput, ImageButton pMenu, FrameLayout pNestedScroll, ImageView pBlocker, ImageView pBlockerFullSceen, View mSearchEngineBar, TextView pCopyright){
        this.mContext = context;
        this.mProgressBar = progressBar;
        this.mSearchbar = searchbar;
        this.mSplashScreen = splashScreen;
        this.mLoading = loading;
        this.mLoadingText = loadingText;
        this.mWebviewContainer = webviewContainer;
        this.mBannerAds = banner_ads;
        this.mEvent = event;
        this.mGatewaySplash = gateway_splash;
        this.mTopBar = top_bar;
        this.mGeckoView = gecko_view;
        this.mBackSplash = backsplash;
        this.mConnectButton = connect_button;
        this.mNewTab = mNewTab;
        this.popupWindow = null;
        this.mFindBar = pFindBar;
        this.mFindText = pFindText;
        this.mFindCount = pFindCount;
        this.mTopLayout = pTopLayout;
        this.mVoiceInput = pVoiceInput;
        this.mMenu = pMenu;
        this.mNestedScroll = pNestedScroll;
        this.mBlocker = pBlocker;
        this.mBlockerFullSceen = pBlockerFullSceen;
        this.mSearchEngineBar = mSearchEngineBar;
        this.mCopyright = pCopyright;

        initSplashScreen();
        initializeSuggestionView(suggestions);
        createUpdateUiHandler();
        recreateStatusBar();
        initTopBarPadding();
    }

    public void initTopBarPadding(){
        if(!status.sFullScreenBrowsing){
            int paddingDp = 60;
            float density = mContext.getResources().getDisplayMetrics().density;
            int paddingPixel = (int)(paddingDp * density);
            mGeckoView.setPadding(0,0,0,paddingPixel);
        }else {
            int paddingDp = 0;
            float density = mContext.getResources().getDisplayMetrics().density;
            int paddingPixel = (int)(paddingDp * density);
            mGeckoView.setPadding(0,0,0,paddingPixel);
        }
    }

    public void initSearchBarFocus(boolean pStatus){
        if(!pStatus){
            this.mVoiceInput.animate().setDuration(0).alpha(0).withEndAction(() -> {
                mVoiceInput.setVisibility(View.GONE);
                ((LinearLayout)mNewTab.getParent()).setVisibility(View.VISIBLE);
                mMenu.setVisibility(View.VISIBLE);

                mSearchbar.setPadding(mSearchbar.getPaddingLeft(),0,helperMethod.pxFromDp(15),0);
            });
        }else {
            Drawable drawable;
            Resources res = mContext.getResources();
            try {
                if(status.sSettingEnableVoiceInput){
                    drawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_keyboard_voice));
                }else {
                    drawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_search_small));
                }
                mVoiceInput.setImageDrawable(drawable);
            } catch (Exception ignored) {
            }

            final Handler handler = new Handler();
            handler.postDelayed(() ->
            {
                mVoiceInput.setVisibility(View.VISIBLE);
            }, 0);

            ((LinearLayout)this.mNewTab.getParent()).setVisibility(View.GONE);
            this.mMenu.setVisibility(View.GONE);

            mSearchbar.setPadding(mSearchbar.getPaddingLeft(),0,helperMethod.pxFromDp(40),0);

            final Handler handler_keyboard = new Handler();
            handler_keyboard.postDelayed(() ->
            {
                InputMethodManager imm = (InputMethodManager)   mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, 0);
            }, 250);
        }
    }

    void initTab(int count){
        mNewTab.setText((count+strings.GENERIC_EMPTY_STR));
    }

    public void recreateStatusBar(){
        if(status.sSettingIsAppStarted){
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
            }
            else {
                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
                    mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }else {
                    mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.black));
                    View decorView = mContext.getWindow().getDecorView();
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
            }
        }else {
            onProgressBarUpdate(100);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initStatusBarColor() {
        animatedColor oneToTwo = new animatedColor(ContextCompat.getColor(mContext, R.color.secondary), ContextCompat.getColor(mContext, R.color.secondary));
        animatedColor twoToThree = new animatedColor(ContextCompat.getColor(mContext, R.color.secondary), ContextCompat.getColor(mContext, R.color.c_background));
        animatedColor ThreeToFour = new animatedColor(ContextCompat.getColor(mContext, R.color.c_background), ContextCompat.getColor(mContext, R.color.c_background));

        ValueAnimator animator = ObjectAnimator.ofFloat(0f, 1f).setDuration(0);
        animator.setStartDelay(1200);
        animator.addUpdateListener(animation ->
        {
            float v = (float) animation.getAnimatedValue();
            mContext.getWindow().setStatusBarColor(oneToTwo.with(v));
            mContext.getWindow().setStatusBarColor(oneToTwo.with(v));
            mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        });
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                final ValueAnimator animator2 = ObjectAnimator.ofFloat(0f, 1f).setDuration(217);
                animator2.addUpdateListener(animation1 ->
                {
                    float v = (float) animation1.getAnimatedValue();
                    mContext.getWindow().setStatusBarColor(twoToThree.with(v));
                    if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
                        mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }else {
                        mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.black));
                        View decorView = mContext.getWindow().getDecorView();
                        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                    mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
                });
                animator2.start();

                animator2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        final ValueAnimator animator3 = ObjectAnimator.ofFloat(0f, 1f).setDuration(0);
                        animator3.addUpdateListener(animation1 ->
                        {
                            float v = (float) animation1.getAnimatedValue();
                        });
                        animator3.start();
                    }
                });
                animator2.start();

            }
        });
        animator.start();
    }

    void initializeSuggestionView(ArrayList<historyRowModel> suggestions){

        if(mSearchbar.isFocused()){
            return;
        }

        autoCompleteAdapter suggestionAdapter = new autoCompleteAdapter(mContext, R.layout.hint_view, (ArrayList<historyRowModel>) suggestions.clone());

        int width = Math.round(helperMethod.screenWidth());
        mSearchbar.setThreshold(2);
        mSearchbar.setAdapter(suggestionAdapter);
        mSearchbar.setDropDownVerticalOffset(helperMethod.pxFromDp(0));
        mSearchbar.setDropDownWidth(width);
        mSearchbar.setDropDownBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.c_background)));
        mSearchbar.setDropDownHeight(helperMethod.getScreenHeight(mContext)*75/100);
        mSearchbar.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    public void initSplashLoading(){

        mLoading.setAnimation(helperMethod.getRotationAnimation());
        mLoading.setAnimation(helperMethod.getRotationAnimation());
        mLoadingText.setAlpha(0);
        mLoadingText.setVisibility(View.VISIBLE);
        mLoadingText.animate().setStartDelay(0).alpha(1);

        mConnectButton.setClickable(false);
        mGatewaySplash.setClickable(false);
        mBlocker.setClickable(true);
        mBlocker.setFocusable(true);
    }

    void initHomePage(){
        mConnectButton.setClickable(false);
        mGatewaySplash.setClickable(false);

        mConnectButton.animate().setDuration(200).alpha(0.4f).withEndAction(() -> {
            mCopyright.setVisibility(View.GONE);
            initSplashLoading();
        });
        mGatewaySplash.animate().setDuration(200).alpha(0.4f);
    }

    private void initSplashScreen(){
        mSearchbar.setEnabled(false);
        helperMethod.hideKeyboard(mContext);

        //mBackSplash.getLayoutParams().height = (int)(helperMethod.getScreenHeight(mContext) - helperMethod.getStatusBarHeight(mContext)*1.5f);
        mSearchbar.setEnabled(false);

        View root = mSearchbar.getRootView();
        root.setBackgroundColor(ContextCompat.getColor(mContext, R.color.c_background_keyboard));
        mGeckoView.setBackgroundResource( R.color.dark_purple);

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
                    startPostTask(messages.MESSAGE_ON_URL_LOAD);
                }
            }.start();
        }
    }

    /*-------------------------------------------------------PAGE UI Methods-------------------------------------------------------*/

    void onPageFinished(){
        mSearchbar.setEnabled(true);
        mProgressBar.bringToFront();
        mSplashScreen.bringToFront();
        if(mSplashScreen.getVisibility()!=View.GONE){
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);//Set Portrait
        }
        splashScreenDisable();
    }
    private void splashScreenDisable(){
        mTopBar.setAlpha(1);
        if(mSplashScreen.getVisibility()==View.VISIBLE)
        {
            mSplashScreen.animate().setStartDelay(1000).setDuration(500).alpha(0).withEndAction((this::triggerPostUI));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                initStatusBarColor();
            }
        }
    }

    private void triggerPostUI(){
        if(mSplashScreen.getVisibility()!=View.GONE){
            mSplashScreen.setVisibility(View.GONE);
            mSplashScreen.invalidate();
        }

        if(mProgressBar.getProgress()>0 && mProgressBar.getProgress()<10000){
            mProgressBar.animate().setStartDelay(0).alpha(1);
        }
    }

    /*-------------------------------------------------------Helper Methods-------------------------------------------------------*/

    void onOpenMenu(View view, boolean canGoBack, boolean isLoading, int userAgent){

        if(popupWindow!=null){
            popupWindow.dismiss();
        }

        LayoutInflater layoutInflater
                = (LayoutInflater) mContext
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View popupView = layoutInflater.inflate(R.layout.popup_menu, null);


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
        popupWindow.showAtLocation(parent, Gravity.TOP|Gravity.END,0,0);

        if(!status.sCharacterEncoding){
            popupView.findViewById(R.id.menu30).setVisibility(View.GONE);
        }

        ImageButton back = popupView.findViewById(R.id.menu22);
        ImageButton close = popupView.findViewById(R.id.menu20);
        CheckBox desktop = popupView.findViewById(R.id.menu27);
        desktop.setChecked(userAgent==USER_AGENT_MODE_DESKTOP);

        if(!canGoBack){
           back.setEnabled(false);
           back.setColorFilter(Color.argb(255, 191, 191, 191));
        }
        if(!isLoading){
           close.setEnabled(false);
           close.setColorFilter(Color.argb(255, 191, 191, 191));
        }

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
        if(mTopLayout.getY() - pPosition > -mTopLayout.getHeight() && mTopLayout.getY() - pPosition < 0){
            // mTopLayout.setTranslationY(mTopLayout.getTranslationY() - pPosition);
            // mWebviewContainer.setTranslationY(mWebviewContainer.getTranslationY() - pPosition);
        }
    }

    public void setOrientation(boolean status){
        isLandscape = status;
    }

    void onSetBannerAdMargin(boolean status,boolean isAdLoaded){
        if(isAdLoaded){
            if(status && !isLandscape){
                mBannerAds.setVisibility(View.VISIBLE);
                mBannerAds.setAlpha(1f);

                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
                    mWebviewContainer.clearAnimation();
                    mWebviewContainer.setPadding(0,AdSize.SMART_BANNER.getHeightInPixels(mContext)+1,0,0);
                    mProgressBar.bringToFront();
                }, 250);
            }else{
                mWebviewContainer.setPadding(0,0,0,0);
                mBannerAds.setVisibility(View.GONE);
            }
        }
    }

    private Handler searchBarUpdateHandler = new Handler();
    private String handlerLocalUrl = "";
    void onUpdateSearchBar(String url,boolean showProtocol, boolean pClearText){
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

        }, delay);
    }

    public void onUpdateFindBarCount(int index, int total)
    {
        if(total==0){
            mFindCount.setText(strings.GENERIC_EMPTY_STR);
        }else {
            mFindCount.setText((total + "/" + index));
        }
    }

    public void onUpdateFindBar(boolean pStatus)
    {
        mFindBar.animate().cancel();
        if(pStatus){
            mFindBar.setVisibility(View.VISIBLE);
            mFindBar.setAlpha(1);
            mFindBar.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.popup_anim_in));
            mFindText.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }else {
            mFindText.clearFocus();
            mFindCount.setText(strings.GENERIC_EMPTY_STR);
            mFindBar.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.popup_anim_out));
            helperMethod.hideKeyboard(mContext);
            mFindBar.animate().alpha(0).withEndAction(() -> mFindBar.setVisibility(View.GONE));
            mFindText.setText(strings.GENERIC_EMPTY_STR);
        }
    }

    public void onUpdateSearchEngineBar(boolean pStatus)
    {
        mSearchEngineBar.animate().cancel();
        if(pStatus){
            mSearchEngineBar.setAlpha(0f);
            mSearchEngineBar.animate().setDuration(300).alpha(1);
            mSearchEngineBar.setVisibility(View.VISIBLE);
        }else {
            mSearchEngineBar.animate().setDuration(300).alpha(0).withEndAction(() -> {
                mSearchEngineBar.setAlpha(0f);
                mSearchEngineBar.setVisibility(View.GONE);
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
            mSearchbar.setText(helperMethod.urlDesigner(url, mContext));
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
    }

    private String removeEndingSlash(String url){
        return helperMethod.removeLastSlash(url);
    }

    void onNewTab(boolean keyboard,boolean isKeyboardOpen){
        if(keyboard){
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

    void onProgressBarUpdate(int value){

        mProgressBar.setAlpha(1f);
        mProgressBar.setVisibility(View.VISIBLE);

        ProgressBarAnimation mProgressAnimation = new ProgressBarAnimation(mProgressBar, 1000);
        mProgressAnimation.setProgress(value*100);
    }

    public class ProgressBarAnimation extends Animation {
        private ProgressBar mProgressBar;
        private int mTo;
        private int mFrom;
        private long mStepDuration;

        /**
         * @param fullDuration - time required to fill progress from 0% to 100%
         */
        public ProgressBarAnimation(ProgressBar progressBar, long fullDuration) {
            super();
            mProgressBar = progressBar;
            mStepDuration = fullDuration / progressBar.getMax();
        }


        public void setProgress(int progress) {
            if(progress == 10000){
                mProgressBar.animate().setStartDelay(100).alpha(0);
            }else {
                mProgressBar.animate().cancel();
                mProgressBar.setAlpha(1);
            }

            if (progress < 0) {
                progress = 0;
            }

            if (progress > mProgressBar.getMax()) {
                progress = mProgressBar.getMax();
            }

            mTo = progress;

            mFrom = mProgressBar.getProgress();
            setDuration(Math.abs(mTo - mFrom) * mStepDuration);
            mProgressBar.startAnimation(this);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float value = mFrom + (mTo - mFrom) * interpolatedTime;
            mProgressBar.setProgress((int) value);
        }
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
    void onFullScreenUpdate(boolean status){
        int value = !status ? 1 : 0;

        if(status) {
        }else {
            this.mBlockerFullSceen.setVisibility(View.VISIBLE);
            this.mBlockerFullSceen.setAlpha(1f);
        }

        if(status){
            this.mBlockerFullSceen.setVisibility(View.VISIBLE);
            this.mBlockerFullSceen.setAlpha(0f);
            this.mBlockerFullSceen.animate().setStartDelay(0).setDuration(200).alpha(1).withEndAction(() -> {
                mTopBar.setClickable(!status);
                disableEnableControls(!status, mTopBar);
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

                mProgressBar.setVisibility(View.GONE);
                mTopBar.setVisibility(View.GONE);
                mBannerAds.setVisibility(View.GONE);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mWebviewContainer.getLayoutParams();
                params.setMargins(0, helperMethod.pxFromDp(0), 0, 0);
                mWebviewContainer.setLayoutParams(params);

                ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) mWebviewContainer.getLayoutParams();
                params1.setMargins(0, 0, 0,0);
                mGeckoView.setLayoutParams(params1);

                com.darkweb.genesissearchengine.constants.status.sFullScreenBrowsing = false;
                initTopBarPadding();

                this.mBlockerFullSceen.animate().setStartDelay(100).setDuration(200).alpha(0).withEndAction(() -> {
                    mBlockerFullSceen.setVisibility(View.GONE);
                });
            });
        }
        else {
            mTopBar.setClickable(!status);
            disableEnableControls(!status, mTopBar);
            mTopBar.setAlpha(value);
            mBannerAds.setVisibility(View.GONE);

            mProgressBar.setVisibility(View.VISIBLE);
            mTopBar.setVisibility(View.VISIBLE);
            mBannerAds.setVisibility(View.GONE);
            mEvent.invokeObserver(Collections.singletonList(!isLandscape), enums.etype.on_init_ads);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mWebviewContainer.getLayoutParams();
            params.setMargins(0, 0, 0,0);
            mWebviewContainer.setLayoutParams(params);

            ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) mWebviewContainer.getLayoutParams();
            params1.setMargins(0, 0, 0,helperMethod.pxFromDp(0));
            mGeckoView.setLayoutParams(params1);

            com.darkweb.genesissearchengine.constants.status.sFullScreenBrowsing = (boolean) dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_FULL_SCREEN_BROWSIING,true));
            initTopBarPadding();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else {
                mContext.getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
            }

            this.mBlockerFullSceen.animate().setStartDelay(0).setDuration(200).alpha(0).withEndAction(() -> {
                mBlockerFullSceen.setVisibility(View.GONE);
            });
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