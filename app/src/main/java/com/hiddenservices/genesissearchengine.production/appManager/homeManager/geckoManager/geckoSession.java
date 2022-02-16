package com.hiddenservices.genesissearchengine.production.appManager.homeManager.geckoManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.hiddenservices.genesissearchengine.production.constants.constants;
import com.hiddenservices.genesissearchengine.production.constants.enums;
import com.hiddenservices.genesissearchengine.production.constants.keys;
import com.hiddenservices.genesissearchengine.production.constants.status;
import com.hiddenservices.genesissearchengine.production.constants.strings;
import com.hiddenservices.genesissearchengine.production.dataManager.dataEnums;
import com.hiddenservices.genesissearchengine.production.eventObserver;
import com.hiddenservices.genesissearchengine.production.helperManager.helperMethod;
import com.hiddenservices.genesissearchengine.production.libs.trueTime.trueTimeEncryption;
import com.hiddenservices.genesissearchengine.production.pluginManager.pluginController;
import com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums;
import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.gecko.PrefsHelper;
import org.mozilla.gecko.util.ThreadUtils;
import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.Autofill;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.SlowScriptResponse;
import org.mozilla.geckoview.WebExtension;
import org.mozilla.geckoview.WebRequestError;
import org.mozilla.geckoview.WebResponse;
import org.orbotproject.android.service.wrapper.orbotLocalConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.hiddenservices.genesissearchengine.production.constants.constants.CONST_GENESIS_BADCERT_CACHED;
import static com.hiddenservices.genesissearchengine.production.constants.constants.CONST_GENESIS_BADCERT_CACHED_DARK;
import static com.hiddenservices.genesissearchengine.production.constants.constants.CONST_GENESIS_ERROR_CACHED;
import static com.hiddenservices.genesissearchengine.production.constants.constants.CONST_GENESIS_ERROR_CACHED_DARK;
import static com.hiddenservices.genesissearchengine.production.constants.constants.CONST_GENESIS_HELP_URL_CACHE;
import static com.hiddenservices.genesissearchengine.production.constants.constants.CONST_GENESIS_HELP_URL_CACHE_DARK;
import static com.hiddenservices.genesissearchengine.production.constants.constants.CONST_GENESIS_URL_CACHED;
import static com.hiddenservices.genesissearchengine.production.constants.constants.CONST_GENESIS_URL_CACHED_DARK;
import static com.hiddenservices.genesissearchengine.production.constants.enums.etype.M_DEFAULT_BROWSER;
import static com.hiddenservices.genesissearchengine.production.constants.enums.etype.M_RATE_COUNT;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eMessageManager.M_LONG_PRESS_URL;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eMessageManager.M_LONG_PRESS_WITH_LINK;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eMessageManagerCallbacks.M_RATE_APPLICATION;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_DESKTOP;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_MOBILE;

public class
geckoSession extends GeckoSession implements GeckoSession.MediaDelegate,GeckoSession.ScrollDelegate,GeckoSession.PermissionDelegate,GeckoSession.ProgressDelegate, GeckoSession.HistoryDelegate,GeckoSession.NavigationDelegate,GeckoSession.ContentDelegate
{
    private eventObserver.eventListener event;

    private boolean wasBackPressed = false;
    private String mSessionID ;
    private boolean mCanGoBack = false;
    private boolean mCanGoForward = false;
    private boolean mFullScreen = false;
    private boolean isPageLoading = false;
    private int mProgress = 0;
    private String mPrevURL = "about:blank";
    private String mCurrentTitle = "loading";
    private String mCurrentURL = "about:blank";
    private Uri mUriPermission = null;
    private WeakReference<AppCompatActivity> mContext;
    private geckoDownloadManager mDownloadManager;
    private String mTheme = null;
    private boolean mPreviousErrorPage = false;
    private boolean mRemovableFromBackPressed = false;
    private boolean mThemeChanged = false;
    private int mScollOffset = 0;
    private selectionActionDelegate mSelectionActionDelegate;
    private SecurityInformation securityInfo = null;

    /*Temp Variables*/
    private GeckoSession.HistoryDelegate.HistoryList mHistoryList = null;
    private int rateCount=0;
    private int m_current_url_id = -1;
    private GeckoView mGeckoView;
    private boolean mIsLoaded = false;
    public boolean isFirstPaintExecuted = false;
    private boolean mIsProgressBarChanging = false;
    private Handler mFindHandler;
    private boolean mClosed = false;
    public boolean mCloseRequested = false;
    public boolean mOnBackPressed = false;
    public SessionState mSessionState;

    geckoSession(eventObserver.eventListener event,String mSessionID,AppCompatActivity mContext, GeckoView pGeckoView){

        this.mGeckoView = pGeckoView;
        this.mContext = new WeakReference(mContext);
        this.mSessionID = mSessionID;
        this.event = event;

        onSessionReinit();
        setProgressDelegate(this);
        setHistoryDelegate(this);
        setNavigationDelegate(this);
        setContentDelegate(this);
        setAutoFillDelegate();
        setPermissionDelegate(this);
        setScrollDelegate(this);
        mDownloadManager = new geckoDownloadManager();
        mSelectionActionDelegate = new selectionActionDelegate(mContext, true);
        setPromptDelegate(new geckoPromptView(mContext));
        setSelectionActionDelegate(mSelectionActionDelegate);
    }

    public void onDestroy(){
        close();
        setProgressDelegate(null);
        setHistoryDelegate(null);
        setNavigationDelegate(null);
        setContentDelegate(null);
        setAutoFillDelegate();
        setPermissionDelegate(null);
        setScrollDelegate(null);
        mDownloadManager = null;
        setPromptDelegate(null);
        event = null;
        mContext = null;
        mDownloadManager = null;
        mHistoryList = null;
        mFindHandler = null;
        mGeckoView = null;
    }

    public void onSetInitializeFromStartup(){
        mIsLoaded = true;
    }

    public boolean onGetInitializeFromStartup(){
        return mIsLoaded;
    }

    public boolean onValidateInitializeFromStartup(){
        if(!mIsLoaded){
            mIsLoaded = true;
            initURL(mCurrentURL);
            return true;
        }
        return false;
    }

    void onFileUploadRequest(int resultCode, Intent data){

        geckoPromptView mPromptDelegate = (geckoPromptView)getPromptDelegate();
        Objects.requireNonNull(mPromptDelegate).onFileCallbackResult(resultCode,data);
    }

    public void onSessionReinit(){
        if(mClosed){
            return;
        }

        mCrashCount = 0;
        if(!isFirstPaintExecuted){
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id), enums.etype.ON_SESSION_REINIT);
        }else {
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id), enums.etype.ON_FIRST_PAINT);
        }

        mFindHandler = new Handler();
        mFindHandler.postDelayed(() ->
        {
            if(mContext!=null){
                mContext.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);

            }
        }, 1500);

    }

    void initURL(String url){
        if(mIsLoaded){
            mCrashCount = 0;
            isPageLoading = true;
            setURL(url);
            mCurrentTitle = mCurrentURL;

            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle), enums.etype.on_update_suggestion);
            if(!url.equals("about:blank") && !url.equals("about:config"))
            {
                mProgress = 5;
                onProgressStart();
            }
            m_current_url_id = -1;
        }
    }

    /*Scroll Delegate*/
    @UiThread
    public void onScrollChanged(@NonNull GeckoSession session, int scrollX, int scrollY) {
        mScollOffset = scrollY;
        event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme), enums.etype.M_UPDATE_PIXEL_BACKGROUND);
        if(scrollY<=3){
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme), enums.etype.M_ON_SCROLL_TOP_BOUNDARIES);
        }else if(scrollY<=helperMethod.pxFromDp(30)){
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme), enums.etype.M_ON_SCROLL_BOUNDARIES);
        }else {
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme), enums.etype.M_ON_SCROLL_NO_BOUNDARIES);
        }
    }

    public int getScrollOffset(){
        return mScollOffset;
    }

    /*Autofill Delegate*/

    public void setAutoFillDelegate(){
        this.setAutofillDelegate(new AndroidAutofillDelegate());
    }

    private class AndroidAutofillDelegate implements Autofill.Delegate {

        private Rect displayRectForId(@NonNull final GeckoSession session,
                                      @NonNull final Autofill.Node node) {
            final Matrix matrix = new Matrix();
            final RectF rectF = new RectF(node.getDimensions());
            session.getPageToScreenMatrix(matrix);
            matrix.mapRect(rectF);

            final Rect screenRect = new Rect();
            rectF.roundOut(screenRect);
            return screenRect;
        }

        @Override
        public void onAutofill(@NonNull final GeckoSession session,
                               final int notification,
                               final Autofill.Node node) {
            ThreadUtils.assertOnUiThread();
            if (Build.VERSION.SDK_INT < 26) {
                return;
            }

            final AutofillManager manager =
                    mContext.get().getApplicationContext().getSystemService(AutofillManager.class);
            if (manager == null) {
                return;
            }

            switch (notification) {
                case Autofill.Notify.SESSION_STARTED:
                case Autofill.Notify.SESSION_CANCELED:
                    manager.cancel();
                    break;
                case Autofill.Notify.SESSION_COMMITTED:
                    manager.commit();
                    break;
                case Autofill.Notify.NODE_FOCUSED:
                    manager.notifyViewEntered(
                            mGeckoView, node.getId(),
                            displayRectForId(session, node));
                    break;
                case Autofill.Notify.NODE_BLURRED:
                    manager.notifyViewExited(mGeckoView, node.getId());
                    break;
                case Autofill.Notify.NODE_UPDATED:
                    manager.notifyValueChanged(
                            mGeckoView,
                            node.getId(),
                            AutofillValue.forText(node.getValue()));
                    break;
                case Autofill.Notify.NODE_ADDED:
                case Autofill.Notify.NODE_REMOVED:
                    break;
            }
        }
    }
    /*Progress Delegate*/

    @UiThread
    public void onSecurityChange(@NonNull final GeckoSession session,@NonNull final SecurityInformation securityInfo) {
        this.securityInfo = securityInfo;
    }

    @Override
    public void onPageStart(@NonNull GeckoSession var1, @NonNull String var2) {
        mCloseRequested = false;
        isFirstPaintExecuted = false;

        PrefsHelper.setPref(keys.PROXY_TYPE, 1);
        PrefsHelper.setPref(keys.PROXY_SOCKS,"127.0.0.1");
        PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, orbotLocalConstants.mSOCKSPort);
        PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,5);
        PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,true);

        if(mIsLoaded){
            if(helperMethod.getHost(var2).endsWith(".onion")){
                var2 = var2.replace("www.","");
            }

            mCurrentURL = var2;

            if(!mCurrentURL.equals("about:blank")){
                event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme, this), enums.etype.ON_UPDATE_SEARCH_BAR);
                mContext.get().runOnUiThread(() -> event.invokeObserver(Arrays.asList(5,mSessionID), enums.etype.progress_update));
            }
            if(!isPageLoading){
                mCurrentTitle = "loading";
                m_current_url_id = -1;
                mThemeChanged = false;
            }
            isPageLoading = true;
            if(!mCurrentURL.equals("about:blank") && !mCurrentTitle.equals("loading")){
                mProgress = 5;
                mContext.get().runOnUiThread(() -> event.invokeObserver(Arrays.asList(5,mSessionID), enums.etype.progress_update));
                mThemeChanged = false;
            }
        }
    }

    @UiThread
    public  @Nullable GeckoResult<SlowScriptResponse> onSlowScript(@NonNull final GeckoSession geckoSession, @NonNull final String scriptFileName) {
        return null;
    }

    @UiThread
    public void onPageStop(@NonNull GeckoSession var1, boolean var2) {
        mCloseRequested = !var2;
        if(var2){
            if(mProgress>=100){
                event.invokeObserver(Arrays.asList(null,mSessionID), enums.etype.on_page_loaded);

                if(!mThemeChanged){
                    new Handler().postDelayed(() ->
                    {
                        if(!mThemeChanged){
                            mTheme = null;
                            if(event!=null) {
                                event.invokeObserver(Arrays.asList(mCurrentURL, mSessionID, mCurrentTitle, mTheme), enums.etype.ON_UPDATE_THEME);
                            }
                        }
                    }, 500);
                }
            }
        }
    }


    @Override
    public void onProgressChange(@NonNull GeckoSession session, int progress)
    {
        if(!mFullScreen){
            mProgress = progress;

            if(progress<=20) {
                mIsProgressBarChanging = false;
                mContext.get().runOnUiThread(() -> event.invokeObserver(Arrays.asList(5,mSessionID), enums.etype.progress_update));
            }else {
                if(progress==100){
                    event.invokeObserver(Arrays.asList(mSessionID,mCurrentTitle, m_current_url_id, mTheme, this), enums.etype.ON_INVOKE_PARSER);
                    if(!mCurrentURL.contains("genesis") && !wasPreviousErrorPage()){
                        checkApplicationRate();
                    }
                    if(!mIsProgressBarChanging){
                        mIsProgressBarChanging = true;
                        mContext.get().runOnUiThread(() -> {
                            event.invokeObserver(Arrays.asList(mProgress, mSessionID), enums.etype.progress_update);
                        });
                        event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme), enums.etype.M_UPDATE_PIXEL_BACKGROUND);
                    }
                    mPreviousErrorPage = false;
                }else {
                    mIsProgressBarChanging = false;
                    mContext.get().runOnUiThread(() -> event.invokeObserver(Arrays.asList(mProgress,mSessionID), enums.etype.progress_update));
                }
            }
        }

        if(progress>=100){
            isPageLoading = false;
        }
    }

    public String getSecurityInfo(){
        if(securityInfo!=null && securityInfo.certificate!=null){

            String mAlternativeNames = strings.GENERIC_EMPTY_STR;
            try{
                for (List name : securityInfo.certificate.getSubjectAlternativeNames()){
                    mAlternativeNames = mAlternativeNames + name.get(1) + "<br>";
                }

            }catch (Exception ignored) {}

            return "<br><b>Website</b><br>"+securityInfo.host + "<br><br>" +
                    "<b>Serial Number</b><br>" + securityInfo.certificate.getSerialNumber() + "<br><br>" +
                    "<b>Algorithm Name</b><br>" + securityInfo.certificate.getSigAlgName() + "<br><br>" +
                    "<b>Issued On</b><br>" + securityInfo.certificate.getNotBefore() + "<br><br>" +
                    "<b>Expires On</b><br>" + securityInfo.certificate.getNotAfter() + "<br><br>" +
                    "<b>Organization (O)</b><br>" + securityInfo.certificate.getSubjectDN().getName() + "<br><br>" +
                    "<b>Common Name (CN)</b><br>" + securityInfo.certificate.getIssuerDN().getName() + "<br><br>" +
                    "<b>Subject Alternative Names</b><br>" + mAlternativeNames;
        }else {
            return "Onion Secured Connection";
        }
    }

    public void onProgressStart(){
        if(!getCurrentURL().equals("about:blank") && !getCurrentURL().contains("trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion") && !wasPreviousErrorPage() && !getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED) && !getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED_DARK) && !getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE) && !getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)){
            mContext.get().runOnUiThread(() -> event.invokeObserver(Arrays.asList(5,mSessionID), enums.etype.progress_update));
        }
    }

    public void onRedrawPixel(){
        event.invokeObserver(Arrays.asList("",mSessionID,mCurrentTitle, m_current_url_id, mTheme, false), dataEnums.eTabCommands.M_UPDATE_PIXEL);
    }

    public boolean isLoaded(){
        return mProgress>=100;
    }

    /*History Delegate*/
    @Override
    public GeckoResult<Boolean> onVisited(@NonNull GeckoSession var1, @NonNull String var2, @Nullable String var3, int var4) {
        if(var4==3 || var4==5 || var4==1){
            event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_url_load);
            Object mID = event.invokeObserver(Arrays.asList(var2,mSessionID,mCurrentTitle, m_current_url_id, mTheme, this), enums.etype.on_update_history);
            if(mID!=null){
                m_current_url_id = (int)mID;
            }
            isPageLoading = false;
        }
        return null;
    }

    @UiThread
    public void onHistoryStateChange(@NonNull GeckoSession var1, @NonNull GeckoSession.HistoryDelegate.HistoryList var2) {
        mHistoryList = var2;
    }

    @UiThread
    public void onSessionStateChange(@NonNull GeckoSession session, @NonNull SessionState sessionState) {
        try{
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme, sessionState.toString()), enums.etype.M_UPDATE_SESSION_STATE);
            mSessionState = sessionState;
        }catch (Exception ignored){}
    }

    public boolean onRestoreState(){
        if(mSessionState!=null){
            restoreState(mSessionState);
            return true;
        }else {
            return false;
        }
    }

    /*Navigation Delegate*/
    public void onLocationChange(@NonNull GeckoSession var1, @Nullable String var2) {

        if(!mIsLoaded){
            return;
        }

        boolean mPastURLVerify = false;
        try {
            if(mHistoryList.getCurrentIndex()-1>=0 && mHistoryList.getCurrentIndex()-1<mHistoryList.size()){
                mPastURLVerify = mHistoryList.get(mHistoryList.getCurrentIndex()-1).getUri().equals(var2);
            }
        }catch (Exception ignored){}

        if(wasBackPressed && mPastURLVerify){
            if(var2.equals("http://trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion") || var2.startsWith(CONST_GENESIS_URL_CACHED) || var2.startsWith(CONST_GENESIS_URL_CACHED_DARK)){
                if(var2.startsWith(CONST_GENESIS_URL_CACHED_DARK) && (status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(mContext.get()))){
                    isPageLoading = false;
                    event.invokeObserver(null, enums.etype.M_CHANGE_HOME_THEME);
                }
                else if(var2.startsWith(CONST_GENESIS_URL_CACHED) && (status.sTheme != enums.Theme.THEME_LIGHT && !helperMethod.isDayMode(mContext.get()))){
                    isPageLoading = false;
                    event.invokeObserver(null, enums.etype.M_CHANGE_HOME_THEME);
                }
            }
        }
        wasBackPressed = false;

        String newUrl = Objects.requireNonNull(var2).split("#")[0];
        if(!mCurrentTitle.equals("loading")){
            Object mURL = event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme, this), enums.etype.on_update_history);
            if(mURL!=null){
                m_current_url_id = (int)mURL;
            }
        }
        if(newUrl.startsWith(CONST_GENESIS_URL_CACHED) || newUrl.startsWith(CONST_GENESIS_URL_CACHED_DARK)){
            setURL(constants.CONST_GENESIS_DOMAIN_URL);
        }
        else if(newUrl.equals(constants.CONST_GENESIS_HELP_URL_CACHE)){
            if(status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(mContext.get())){
                setURL(constants.CONST_GENESIS_HELP_URL);
            }else {
                setURL(constants.CONST_GENESIS_HELP_URL_CACHE_DARK);
            }
        }else if(!newUrl.equals("about:blank")){
            setURL(newUrl);
        }
        if(!mCurrentURL.equals("about:blank")){
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme, this), enums.etype.ON_UPDATE_SEARCH_BAR);
        }
    }

    private String setGenesisVerificationToken(String pString){
        try{
            Uri built = Uri.parse(pString).buildUpon()
                    .appendQueryParameter(constants.CONST_GENESIS_GMT_TIME_GET_KEY, trueTimeEncryption.getInstance().getSecretToken())
                    .build();
            return built.toString();
        }catch (Exception ex){
            return pString;
        }
    }

    public GeckoResult<AllowOrDeny> onLoadRequest(@NonNull GeckoSession var2, @NonNull GeckoSession.NavigationDelegate.LoadRequest var1) {

        String m_url = var1.uri;
        if(helperMethod.getHost(m_url).endsWith(".onion")){
            m_url = m_url.replace("www.","");
        }

        if(m_url.endsWith("genesisconfigurenewidentity.com/")){
            initURL(mPrevURL);
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), enums.etype.M_NEW_IDENTITY_MESSAGED);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        String mNormalizeURL = helperMethod.normalize(m_url);
        if(mNormalizeURL!=null && mNormalizeURL.endsWith("trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion")){
            initURL(constants.CONST_GENESIS_DOMAIN_URL);
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, false), enums.etype.M_LOAD_HOMEPAGE_GENESIS);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        if(!m_url.contains(constants.CONST_GENESIS_GMT_TIME_GET_KEY) && !m_url.startsWith(CONST_GENESIS_URL_CACHED) && !m_url.startsWith(CONST_GENESIS_URL_CACHED_DARK) && var1.uri.startsWith("http://trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion") && !var1.uri.contains(constants.CONST_GENESIS_LOCAL_TIME_GET_KEY) && !var1.uri.contains(constants.CONST_GENESIS_LOCAL_TIME_GET_KEY)){

            String mVerificationURL = setGenesisVerificationToken(m_url);
            initURL(mVerificationURL);
            loadUri(mVerificationURL);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        else if(m_url.startsWith("mailto")){
            event.invokeObserver(Arrays.asList(m_url,mSessionID), enums.etype.M_ON_MAIL);
            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        }
        else if(m_url.contains("trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion/advert__")){
            event.invokeObserver(Arrays.asList(m_url,mSessionID), enums.etype.on_playstore_load);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        else if(m_url.equals(constants.CONST_GENESIS_DOMAIN_URL_SLASHED) || m_url.startsWith("http://trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion/?")){
            initURL(constants.CONST_GENESIS_DOMAIN_URL);
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, false), enums.etype.M_LOAD_HOMEPAGE_GENESIS);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        else if(m_url.equals("about:blank") && mIsLoaded){
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), enums.etype.ON_EXPAND_TOP_BAR);
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, false), enums.etype.M_ON_BANNER_UPDATE);
            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        }
        else if(var1.target==2){
            event.invokeObserver(Arrays.asList(m_url,mSessionID), enums.etype.open_new_tab);
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), enums.etype.ON_EXPAND_TOP_BAR);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        else if(!m_url.equals("about:blank")){
            if(mCurrentURL.startsWith(CONST_GENESIS_URL_CACHED) || mCurrentURL.startsWith(CONST_GENESIS_URL_CACHED_DARK)){
                setURL(constants.CONST_GENESIS_DOMAIN_URL);
            }else if(mCurrentURL.equals(constants.CONST_GENESIS_HELP_URL_CACHE)){
                if(status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(mContext.get())){
                    setURL(constants.CONST_GENESIS_HELP_URL);
                }else {
                    setURL(constants.CONST_GENESIS_HELP_URL_CACHE_DARK);
                }
            }else if(!m_url.startsWith("resource://android/assets/homepage/")){
                setURL(m_url);
            }

            event.invokeObserver(Arrays.asList(m_url,mSessionID), enums.etype.start_proxy);
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID), enums.etype.search_update);

            /* Its Absence causes delay on first launch*/

            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme, this), enums.etype.ON_UPDATE_SEARCH_BAR);

            if(!mCurrentURL.contains("trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion")){
                mProgress = 5;
                onProgressStart();
            }

            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        }else {
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
    }

    public void onUpdateBannerAdvert(){
        event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, false), enums.etype.M_ON_BANNER_UPDATE);
    }

    public void onClose(){
        stop();
        mCurrentURL = mPrevURL;
        //mIsLoaded = false;
        //isPageLoading = false;
    }

    @Override
    public void onCanGoBack(@NonNull GeckoSession session, boolean var2)
    {
        mCanGoBack = var2;
    }

    @Override
    public void onCanGoForward(@NonNull GeckoSession session, boolean var2)
    {
        mCanGoForward = var2;
    }


    public GeckoResult<String> onLoadError(@NonNull GeckoSession var1, @Nullable String var2, WebRequestError var3) {

        if(helperMethod.getHost(var2).endsWith(".onion")){
            var2 = var2.replace("www.","");
        }

        if(var2.startsWith("https://trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd")){
            var2 = var2.replace("https","http");
            mCurrentURL = var2;
        }
        if(mCurrentURL.contains("genesis.onion")){
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), enums.etype.M_NEW_IDENTITY);
        }
        if(status.sSettingIsAppStarted && orbotLocalConstants.mIsTorInitialized){
            errorHandler handler = new errorHandler();
            mProgress = 0;
            mPreviousErrorPage = true;
            event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_load_error);
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), enums.etype.ON_UPDATE_THEME);

            InputStream mResourceURL = null;
            try {
                if(var3.code==50){
                    if(status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(mContext.get())){
                        mResourceURL = mContext.get().getResources().getAssets().open(CONST_GENESIS_BADCERT_CACHED);
                    }else {
                        mResourceURL = mContext.get().getResources().getAssets().open(CONST_GENESIS_BADCERT_CACHED_DARK);
                    }
                }
                else {
                    if(status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(mContext.get())){
                        mResourceURL = mContext.get().getResources().getAssets().open(CONST_GENESIS_ERROR_CACHED);
                    }else {
                        mResourceURL = mContext.get().getResources().getAssets().open(CONST_GENESIS_ERROR_CACHED_DARK);
                    }
                }
            }catch (Exception ex){
                Log.i("asd","asd : " + ex.getMessage());
            }

            return GeckoResult.fromValue("data:text/html," + handler.createErrorPage(var3.category, var3.code,mContext.get(),var2, mResourceURL));
        }else {
            event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.M_ORBOT_LOADING);
            mCurrentURL = mPrevURL;
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme, this), enums.etype.ON_UPDATE_SEARCH_BAR);
        }
        return null;
    }

    /*Content Delegate*/
    @UiThread
    @Override
    public void onExternalResponse(@NonNull GeckoSession session, @NonNull WebResponse response) {
        try {
            if(response.headers.containsKey("Content-Disposition")){
                mDownloadManager.downloadFile(response,this,mContext.get(),event);
            }else if(response.headers.containsKey("Content-Type")){
                mDownloadManager.downloadFile(response,this,mContext.get(),event);
            }
        } catch (ActivityNotFoundException e) {
            event.invokeObserver(Arrays.asList(response,mSessionID), enums.etype.on_handle_external_intent);
            stop();
        }
    }

    @UiThread
    public void onExternalResponse(@NonNull  GeckoSession session, @NonNull GeckoSession.WebResponseInfo response){

    }

    @UiThread
    public void onFirstContentfulPaint(@NonNull GeckoSession var1) {

        isFirstPaintExecuted = true;
        if(mPreviousErrorPage || mCurrentURL.contains("trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion") || mCurrentURL.startsWith(CONST_GENESIS_URL_CACHED) || mCurrentURL.startsWith(CONST_GENESIS_URL_CACHED_DARK) || mCurrentURL.startsWith(CONST_GENESIS_HELP_URL_CACHE) || mCurrentURL.toString().startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)){
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, false), enums.etype.M_ON_BANNER_UPDATE);
        }else {
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, true), enums.etype.M_ON_BANNER_UPDATE);
        }

        if(!mCurrentURL.equals("about:blank")){
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id,mTheme), enums.etype.ON_FIRST_PAINT);
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id,mTheme), enums.etype.ON_LOAD_REQUEST);
        }else {
            onSessionReinit();
        }

        event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme), enums.etype.M_UPDATE_PIXEL_BACKGROUND);
        event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), enums.etype.ON_EXPAND_TOP_BAR);
        mPrevURL = mCurrentURL;
    }

    @UiThread
    public void onWebAppManifest(@NonNull GeckoSession var1, @NonNull JSONObject var2) {
        try {
            mThemeChanged = true;
            mTheme = var2.getString("theme_color");
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), enums.etype.ON_UPDATE_THEME);
            // event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme), enums.etype.M_INDEX_WEBSITE);
        } catch (Exception ex) {
            mTheme = null;
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), enums.etype.ON_UPDATE_THEME);
            ex.printStackTrace();
        }
        event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme), enums.etype.ON_UPDATE_TAB_TITLE);
    }

    @Nullable
    public GeckoResult<Object> onMessage(final @NonNull String nativeApp, final @NonNull Object message, final @NonNull WebExtension.MessageSender sender) {
        if (message instanceof JSONObject) {
            JSONObject json = (JSONObject) message;
            try {
                if (json.has("type") && "WPAManifest".equals(json.getString("type"))) {
                    JSONObject manifest = json.getJSONObject("manifest");
                    Log.d("MessageDelegate", "Found WPA manifest: " + manifest);
                }
            } catch (JSONException ex) {
                Log.e("MessageDelegate", "Invalid manifest", ex);
            }
        }
        return null;
    }

    @UiThread
    public void onTitleChange(@NonNull GeckoSession var1, @Nullable String var2) {
        if(var2!=null && !var2.equals(strings.GENERIC_EMPTY_STR) && var2.length()>2 && !var2.equals("about:blank") && mIsLoaded){
            mCurrentTitle = var2;
            Object mID = event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme, this), enums.etype.on_update_history);
            if(mID!=null){
                m_current_url_id = (int)mID;
            }

            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), enums.etype.ON_UPDATE_TAB_TITLE);
        }
    }

    @UiThread
    public void onCloseRequest(@NonNull GeckoSession var1) {
        if(!canGoBack() && !mClosed){
            mCloseRequested = true;
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle), enums.etype.back_list_empty);
        }
    }

    int mCrashCount = 0;
    @UiThread
    public void onCrash(@NonNull GeckoSession session) {
        mCloseRequested = true;
        if(!mClosed && status.sSettingIsAppStarted){
            if(event==null){
                return;
            }
            Object mSessionObject = event.invokeObserver(null, enums.etype.SESSION_ID);
            if(mSessionObject==null || getSessionID()==null){
                return;
            }
            String mSessionID = (String) mSessionObject;
            if(mSessionID.equals(getSessionID())){
                if(mCrashCount<=5){
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        try {
                            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme, this), enums.etype.M_OPEN_SESSION);
                        }catch (Exception ignored){}
                    }, mCrashCount*500);
                }
                mCrashCount+=1;
            }
        }
    }

    @UiThread
    public void onKill(@NonNull GeckoSession session) {
        mCloseRequested = true;
        if(!mClosed && status.sSettingIsAppStarted){
            if(event==null){
                return;
            }
            Object mSessionObject = event.invokeObserver(null, enums.etype.SESSION_ID);
            if(mSessionObject==null || getSessionID()==null){
                return;
            }
            String mSessionID = (String) mSessionObject;


            if(mSessionID.equals(getSessionID())){
                if(mCrashCount<=5){
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        if(status.sSettingIsAppStarted && !session.isOpen()){
                            try {
                                event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id, mTheme, this), enums.etype.M_OPEN_SESSION);
                            }catch (Exception ignored){}
                        }
                    }, mCrashCount*500);
                }
                mCrashCount+=1;
            }
        }
    }

    @Override
    public void onFullScreen(@NonNull GeckoSession var1, boolean var2) {
        mFullScreen = var2;
        mSelectionActionDelegate.setFullScreen(mFullScreen);
        event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_full_screen);
    }

    @UiThread
    @Override
    public void onContextMenu(@NonNull GeckoSession var1, int var2, int var3, @NonNull GeckoSession.ContentDelegate.ContextElement var4) {

        String title = strings.GENERIC_EMPTY_STR;
        if(var4.title!=null){
            title = var4.title;
        }
        if(var4.type!=0 && var4.srcUri!=null){
            if(var4.linkUri!=null){
                event.invokeObserver(Arrays.asList(var4.linkUri,mSessionID,var4.srcUri,title, mTheme, var4.altText, this, mContext.get()), M_LONG_PRESS_WITH_LINK);
            }
            else {
                try{
                    String mTitle = var4.title;
                    if(mTitle==null || mTitle.length()<=0)
                    {
                        mTitle = helperMethod.getDomainName(mCurrentURL) + "\n" + var4.srcUri;
                    }
                    event.invokeObserver(Arrays.asList(var4.srcUri,mSessionID,mTitle, mTheme, this, mContext.get()), enums.etype.on_long_press);
                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.i("","");
                }
            }
        }
        else if(var4.linkUri!=null){
            event.invokeObserver(Arrays.asList(var4.linkUri,mSessionID,title, mTheme, this, mContext.get()), M_LONG_PRESS_URL);
        }
    }

    /*Permission Delegate*/

    @Override
    public void onContentPermissionRequest(final GeckoSession session, final String uri,
                                           final int type, final Callback callback) {
        if (PERMISSION_AUTOPLAY_AUDIBLE == type || PERMISSION_AUTOPLAY_INAUDIBLE == type) {
            if (!status.sToolbarTheme) {
                callback.reject();
            } else {
                callback.grant();
            }
        }else {
            callback.reject();
        }
    }
    /*Download Manager*/

    void downloadRequestedFile()
    {
        if(mDownloadManager.getDownloadURL()!=null && mDownloadManager.getDownloadFile()!=null){
            if(!createAndSaveFileFromBase64Url(mDownloadManager.getDownloadURL().toString())){
                pluginController.getInstance().onDownloadInvoke(Arrays.asList(mDownloadManager.getDownloadURL()+"__"+mDownloadManager.getDownloadFile(), Environment.DIRECTORY_DOWNLOADS), pluginEnums.eDownloadManager.M_START_SERVICE);
            }
        }
    }

    void downloadRequestedFile(Uri downloadURL,String downloadFile)
    {
        if(downloadURL!=null && downloadFile!=null){
            if(!createAndSaveFileFromBase64Url(downloadURL.toString())){
                pluginController.getInstance().onDownloadInvoke(Arrays.asList(downloadURL + "__" + downloadFile, Environment.DIRECTORY_DOWNLOADS), pluginEnums.eDownloadManager.M_START_SERVICE);
            }
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Shutta_"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private boolean createAndSaveFileFromBase64Url(String url) {

        try{
            if(!url.startsWith("data") && !url.startsWith("blob")){
                return false;
            }

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String filetype;
            String filename;

            if(url.startsWith("blob")){
                loadUri((String) pluginController.getInstance().onDownloadInvoke(Collections.singletonList(url), pluginEnums.eDownloadManager.M_DOWNLOAD_BLOB));
                return true;
            }

            filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"));
            filename = System.currentTimeMillis() + "." + filetype;

            File file = new File(path, filename);
            try {
                if(!path.exists())
                    path.mkdirs();
                if(!file.exists())
                    file.createNewFile();

                String base64EncodedString = url.substring(url.indexOf(",") + 1);
                byte[] decodedBytes = Base64.decode(base64EncodedString, Base64.DEFAULT);
                OutputStream os = new FileOutputStream(file);
                os.write(decodedBytes);
                os.close();

                //Tell the media scanner about the new file so that it is immediately available to the user.
                MediaScannerConnection.scanFile(mContext.get().getApplicationContext(),
                        new String[]{file.toString()}, null,
                        (path1, uri) ->
                        {
                            Log.i("ExternalStorage", "Scanned " + path1 + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        });

                //Set notification after download complete and add "click to view" action to that
                String mimetype = url.substring(url.indexOf(":") + 1, url.indexOf("/"));
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                Uri uri_temp = FileProvider.getUriForFile(mContext.get().getApplicationContext(),mContext.get().getString(R.string.GENERAL_FILE_PROVIDER_AUTHORITY), file);
                intent.setDataAndType(uri_temp, (mimetype + "/*"));

                List<ResolveInfo> resInfoList = mContext.get().getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    mUriPermission = uri_temp;
                    mContext.get().getApplicationContext().grantUriPermission(packageName, uri_temp, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                PendingIntent pIntent = PendingIntent.getActivity(mContext.get().getApplicationContext(), 0, intent, 0);

                String channel_id = createNotificationChannel(mContext.get().getApplicationContext());

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext.get().getApplicationContext(), channel_id)
                        .setSmallIcon(R.drawable.ic_download)
                        .setContentTitle(filename)
                        .setContentIntent(pIntent);

                notificationBuilder.setAutoCancel(true);

                int notificationId = 85851;
                NotificationManager notificationManager = (NotificationManager) mContext.get().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId, notificationBuilder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return true;
    }

    private static String createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "Channel_id";
            CharSequence channelName = "Application_name";
            String channelDescription = "Application_name Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(true);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
            return channelId;
        } else {
            return null;
        }
    }

    /*Helper Methods*/

    void toogleUserAgent(){
        if(getSettings().getUserAgentMode()==USER_AGENT_MODE_DESKTOP){
            getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE);
        }else {
            getSettings().setUserAgentMode(USER_AGENT_MODE_DESKTOP);
        }

    }

    int getUserAgentMode(){
        return getSettings().getUserAgentMode();
    }

    public String getCurrentURL(){
        return mCurrentURL;
    }

    public String getTitle(){
        return mCurrentTitle;
    }

    public int getProgress(){
        return mProgress;
    }

    public void setTitle(String title){
        mCurrentTitle = title;
    }

    public void setURL(String pURL){
        if(helperMethod.getHost(pURL).endsWith(".onion")){
            pURL = pURL.replace("www.","");
        }
        mCurrentURL = pURL;
    }

    public void setRemovableFromBackPressed(boolean pStatus){
        mRemovableFromBackPressed = pStatus;
    }

    public boolean getRemovableFromBackPressed(){
        if(mCurrentURL.startsWith("data") || mCurrentURL.startsWith("blob")){
            return true;
        }else {
            return mRemovableFromBackPressed;
        }
    }

    public void setTheme(String pTheme){
        mTheme = pTheme;
    }

    public String getTheme(){
        return mTheme;
    }
    boolean canGoBack(){
        if(mHistoryList==null || mHistoryList.size()==0){
            return false;
        }
        return mCanGoBack;
    }

    public boolean wasPreviousErrorPage(){
        return mPreviousErrorPage;
    }

    boolean canGoForward(){
        return mCanGoForward;
    }

    public String getSessionID(){
        return mSessionID;
    }

    public void setSessionID(String pSession){
        mSessionID = pSession;
    }

    void exitScreen(){
        this.exitFullScreen();
    }

    boolean getFullScreenStatus(){
        return !mFullScreen;
    }

    public void closeSession(){
        event.invokeObserver(Arrays.asList(null,mSessionID), enums.etype.on_close_sesson);
    }

    public void closeSessionInstant(){
        mSessionID = "-1";
        new Handler().postDelayed(() ->
        {
            mClosed = true;
        }, 250);
        close();
    }

    public boolean isClosed(){
        return mClosed;
    }

    GeckoResult<FinderResult> mFinder = null;
    public void findInPage(String pQuery, int pDirection){
        mFinder = null;
        mFinder = getFinder().find(pQuery, pDirection);
        new Thread(){
            public void run(){

                int mCounter=0;
                while (mFinder==null){
                    try {
                        mCounter+=1;
                        sleep(100);
                        if(mCounter>100){
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    FinderResult mResult = mFinder.poll(1000);
                    event.invokeObserver(Arrays.asList(mResult.total, mResult.current), enums.etype.FINDER_RESULT_CALLBACK);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }.start();

    }

    void goBackSession(){
        wasBackPressed = true;
        mOnBackPressed = true;
        goBack();

        try {
            if (mHistoryList!=null && mHistoryList.size()>=1){
                if(mHistoryList.getCurrentIndex()-1>=0 && mHistoryList.getCurrentIndex()-1<mHistoryList.size()){
                    String mURL = mHistoryList.get(mHistoryList.getCurrentIndex()-1).getUri();
                    if(mURL.startsWith("resource://")){
                        new Handler().postDelayed(() ->
                        {
                            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, false), enums.etype.M_ON_BANNER_UPDATE);
                        }, 0);
                    }
                }
                event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id), enums.etype.ON_FIRST_PAINT);
            }
        }catch (Exception ignored){}
    }

    void goForwardSession(){

        if(mHistoryList!=null)
        {
            stop();
            int index = mHistoryList.getCurrentIndex() + 1;
            initURL(mHistoryList.get(index).getUri());
            if(mHistoryList.size()>index){
                event.invokeObserver(Arrays.asList(mHistoryList.get(index).getUri(), mSessionID), enums.etype.start_proxy);
            }
            final Handler handler = new Handler();
            handler.postDelayed(this::goForward, 100);
            mProgress = 5;
            event.invokeObserver(Arrays.asList(5, mSessionID, mCurrentURL), enums.etype.progress_update_forced);
            event.invokeObserver(Arrays.asList(5, mSessionID, mCurrentURL), enums.etype.M_ADMOB_BANNER_RECHECK);
        }else {
            final Handler handler = new Handler();
            handler.postDelayed(this::goForward, 100);
            mProgress = 5;
            event.invokeObserver(Arrays.asList(5, mSessionID, mCurrentURL), enums.etype.progress_update_forced);
            event.invokeObserver(Arrays.asList(5, mSessionID, mCurrentURL), enums.etype.M_ADMOB_BANNER_RECHECK);
        }
    }

    boolean isLoading(){
        return isPageLoading;
    }

    void setLoading(boolean status){
        isPageLoading = status;
    }

    Uri getUriPermission(){
        return mUriPermission;
    }

    private void checkApplicationRate(){
        if(status.sSettingIsAppStarted){
            if(status.sGlobalURLCount == 10){
                event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), M_RATE_APPLICATION);
            }

            else if(status.sGlobalURLCount == 20 || status.sGlobalURLCount == 80){
                event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), M_DEFAULT_BROWSER);
            }

            status.sGlobalURLCount +=1;
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, mTheme), M_RATE_COUNT);
        }
    }
}
