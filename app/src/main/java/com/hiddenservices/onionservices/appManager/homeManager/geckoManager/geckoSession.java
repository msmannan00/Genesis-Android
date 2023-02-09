package com.hiddenservices.onionservices.appManager.homeManager.geckoManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import com.hiddenservices.onionservices.R;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.dataModel.geckoDataModel;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.autofillDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.contentDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.promptDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.historyDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.mediaDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.mediaSessionDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.scrollDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.selectionDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses.preferences;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses.errorHandler;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.dataManager.dataEnums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.libs.trueTime.trueTimeEncryption;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.WebExtension;
import org.mozilla.geckoview.WebRequestError;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_BADCERT_CACHED;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_BADCERT_CACHED_DARK;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_ERROR_CACHED;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_ERROR_CACHED_DARK;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_HELP_URL_CACHE;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_HELP_URL_CACHE_DARK;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_URL_CACHED;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_URL_CACHED_DARK;
import static com.hiddenservices.onionservices.constants.enums.etype.M_DEFAULT_BROWSER;
import static com.hiddenservices.onionservices.constants.enums.etype.M_RATE_COUNT;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManagerCallbacks.M_RATE_APPLICATION;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_DESKTOP;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_MOBILE;

public class  geckoSession extends GeckoSession implements GeckoSession.MediaDelegate, GeckoSession.ScrollDelegate, GeckoSession.PermissionDelegate, GeckoSession.ProgressDelegate, GeckoSession.NavigationDelegate {

    private eventObserver.eventListener mEvent;
    private geckoDataModel mGeckoDataModel;
    private mediaSessionDelegate mMediaSessionDelegate;
    private selectionDelegate mSelectionActionDelegate;
    private historyDelegate mHistoryDelegate;
    private promptDelegate mPromptDelegate;
    private contentDelegate mContentDelegate;
    private scrollDelegate mScrollDelegate;
    private autofillDelegate mAutofillDelegate;


    private boolean wasBackPressed = false;
    private boolean mCanGoBack = false;
    private boolean mCanGoForward = false;
    private boolean isPageLoading = false;
    private int mProgress = 0;
    private String mPrevURL = "about:blank";
    private Uri mUriPermission = null;
    private WeakReference<AppCompatActivity> mContext;
    private boolean mPreviousErrorPage = false;
    private boolean mRemovableFromBackPressed = false;
    private SecurityInformation securityInfo = null;

    /*Temp Variables*/
    private boolean mIsLoaded = false;
    public boolean isFirstPaintExecuted = false;
    private boolean mIsProgressBarChanging = false;
    private Handler mFindHandler;
    private boolean mClosed = false;
    public boolean mOnBackPressed = false;
    public SessionState mSessionState;
    public mediaDelegate mMediaDelegate;

    geckoSession(eventObserver.eventListener event, String pSessionID, AppCompatActivity mContext, GeckoView pGeckoView) {

        this.mContext = new WeakReference(mContext);
        this.mEvent = event;
        this.mGeckoDataModel = new geckoDataModel();
        this.mGeckoDataModel.mSessionID = pSessionID;

        this.mMediaDelegate = new mediaDelegate(this.mContext);
        this.mSelectionActionDelegate = new selectionDelegate(mContext, true);
        this.mMediaSessionDelegate = new mediaSessionDelegate(this.mContext, mGeckoDataModel, mMediaDelegate);
        this.mHistoryDelegate = new historyDelegate(this.mContext, mEvent, mGeckoDataModel, this);
        this.mPromptDelegate = new promptDelegate(mContext);
        this.mContentDelegate = new contentDelegate(this.mContext, mEvent, mGeckoDataModel, this);
        this.mScrollDelegate = new scrollDelegate(mEvent, mGeckoDataModel);
        this.mAutofillDelegate = new autofillDelegate(pGeckoView);

        onSessionReinit();
        setSelectionActionDelegate(this.mSelectionActionDelegate);
        setMediaSessionDelegate(this.mMediaSessionDelegate);
        setHistoryDelegate(this.mHistoryDelegate);
        setPromptDelegate(this.mPromptDelegate);
        setContentDelegate(this.mContentDelegate);
        setMediaDelegate(this.mMediaDelegate);
        setScrollDelegate(this.mScrollDelegate);
        setAutofillDelegate(this.mAutofillDelegate);

        setProgressDelegate(this);
        setNavigationDelegate(this);
        setPermissionDelegate(this);
    }


    public void onDestroy() {
        close();
        setProgressDelegate(null);
        setHistoryDelegate(null);
        setNavigationDelegate(null);
        setContentDelegate(null);
        setPermissionDelegate(null);
        setScrollDelegate(null);
        setPromptDelegate(null);
        mEvent = null;
        mContext = null;
        mFindHandler = null;
    }

    public void onSetInitializeFromStartup() {
        mIsLoaded = true;
    }

    public boolean onGetInitializeFromStartup() {
        return mIsLoaded;
    }

    public boolean onValidateInitializeFromStartup() {
        if (!mIsLoaded) {
            mIsLoaded = true;
            initURL(mGeckoDataModel.mCurrentURL);
            return true;
        }
        return false;
    }

    void onFileUploadRequest(int resultCode, Intent data) {
        Objects.requireNonNull(mPromptDelegate).onFileCallbackResult(resultCode, data);
    }

    public void onSessionReinit() {
        if (mClosed) {
            return;
        }

        mContentDelegate.resetCrash();
        if (!isFirstPaintExecuted) {
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID), enums.etype.ON_SESSION_REINIT);
        } else {
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID), enums.etype.ON_FIRST_PAINT);
        }

        mFindHandler = new Handler();
        mFindHandler.postDelayed(() ->
        {
            if (mContext != null) {
                mContext.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);

            }
        }, 1500);

    }

    public historyDelegate getHistoryDelegate(){
        return mHistoryDelegate;
    }

    void initURL(String url) {
        if (mIsLoaded) {
            mContentDelegate.resetCrash();
            isPageLoading = true;
            mHistoryDelegate.setURL(url);
            mGeckoDataModel.mCurrentTitle = mGeckoDataModel.mCurrentURL;

            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle), enums.etype.on_update_suggestion);
            if (!url.equals("about:blank") && !url.equals("about:config")) {
                mProgress = 5;
                onProgressStart();
            }
            mGeckoDataModel.mCurrentURL_ID = -1;
        }
    }

    /*Autofill Delegate*/

    /*Progress Delegate*/

    @UiThread
    public void onSecurityChange(@NonNull final GeckoSession session, @NonNull final SecurityInformation securityInfo) {
        this.securityInfo = securityInfo;
    }

    @Override
    public void onPageStart(@NonNull GeckoSession var1, @NonNull String var2) {
        isFirstPaintExecuted = false;

        mEvent.invokeObserver(Arrays.asList(var2, mGeckoDataModel.mSessionID), enums.etype.search_update);
        if (mIsLoaded) {
            if (!mGeckoDataModel.mCurrentURL.equals("about:config") && !var2.equals("about:blank") && helperMethod.getHost(var2).endsWith(".onion")) {
                var2 = var2.replace("www.", "");
            }

            if(mGeckoDataModel.mCurrentURL.replace("http","https://").equals(var2)){
                mGeckoDataModel.mCurrentURL = var2;
                this.mGeckoDataModel.mCurrentURL = var2;
            }

            if (!mGeckoDataModel.mCurrentURL.equals("about:config") && !mGeckoDataModel.mCurrentURL.equals("about:blank") && !var2.equals("about:blank")) {
                //event.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.m_current_url_id, mGeckoDataModel.mTheme, this), enums.etype.ON_UPDATE_SEARCH_BAR);
                mContext.get().runOnUiThread(() -> mEvent.invokeObserver(Arrays.asList(5, mGeckoDataModel.mSessionID), enums.etype.progress_update));
                mHistoryDelegate.setURL(var2);
                //mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, this), enums.etype.ON_UPDATE_SEARCH_BAR);
            }
            if (!isPageLoading) {
                mGeckoDataModel.mCurrentTitle = "loading";
                mGeckoDataModel.mCurrentURL_ID = -1;
                mGeckoDataModel.mThemeChanged = false;
            }
            isPageLoading = true;
            if (!mGeckoDataModel.mCurrentURL.equals("about:config") && !mGeckoDataModel.mCurrentURL.equals("about:blank") && !mGeckoDataModel.mCurrentTitle.equals("loading")) {
                mProgress = 5;
                mContext.get().runOnUiThread(() -> mEvent.invokeObserver(Arrays.asList(5, mGeckoDataModel.mSessionID), enums.etype.progress_update));
                mGeckoDataModel.mThemeChanged = false;
            }
        }
    }

    @UiThread
    public void onPageStop(@NonNull GeckoSession var1, boolean var2) {
        if (var2) {
            if (mProgress >= 100) {
                mEvent.invokeObserver(Arrays.asList(null, mGeckoDataModel.mSessionID), enums.etype.on_page_loaded);

                if (!mGeckoDataModel.mThemeChanged) {
                    new Handler().postDelayed(() ->
                    {
                        if (!mGeckoDataModel.mThemeChanged) {
                            mGeckoDataModel.mTheme = null;
                            if (mEvent != null) {
                                mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), enums.etype.ON_UPDATE_THEME);
                            }
                        }
                    }, 500);
                }
            }
        }
    }


    @Override
    public void onProgressChange(@NonNull GeckoSession session, int progress) {
        if (!mGeckoDataModel.mFullScreenStatus) {
            mProgress = progress;

            if (progress <= 20) {
                mIsProgressBarChanging = false;
                mContext.get().runOnUiThread(() -> mEvent.invokeObserver(Arrays.asList(5, mGeckoDataModel.mSessionID), enums.etype.progress_update));
            } else {
                if (progress == 100) {
                    mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, this), enums.etype.ON_INVOKE_PARSER);
                    if (!mGeckoDataModel.mCurrentURL.startsWith(constants.CONST_GENESIS_DOMAIN_URL) && !mGeckoDataModel.mCurrentURL.contains("genesis") && !wasPreviousErrorPage()) {
                        checkApplicationRate();
                    }
                    if (!mIsProgressBarChanging) {
                        mIsProgressBarChanging = true;
                        mContext.get().runOnUiThread(() -> {
                            mEvent.invokeObserver(Arrays.asList(mProgress, mGeckoDataModel.mSessionID), enums.etype.progress_update);
                        });
                    }
                    mPreviousErrorPage = false;
                } else {
                    mIsProgressBarChanging = false;
                    mContext.get().runOnUiThread(() -> mEvent.invokeObserver(Arrays.asList(mProgress, mGeckoDataModel.mSessionID), enums.etype.progress_update));
                }
            }
        }

        if (progress >= 100) {
            isPageLoading = false;
        }
    }

    public String getSecurityInfo() {
        if (securityInfo != null && securityInfo.certificate != null) {

            String mAlternativeNames = strings.GENERIC_EMPTY_STR;
            try {
                for (List name : securityInfo.certificate.getSubjectAlternativeNames()) {
                    mAlternativeNames = mAlternativeNames + name.get(1) + "<br>";
                }

            } catch (Exception ignored) {
            }

            return "<br><b>Website</b><br>" + securityInfo.host + "<br><br>" +
                    "<b>Serial Number</b><br>" + securityInfo.certificate.getSerialNumber() + "<br><br>" +
                    "<b>Algorithm Name</b><br>" + securityInfo.certificate.getSigAlgName() + "<br><br>" +
                    "<b>Issued On</b><br>" + securityInfo.certificate.getNotBefore() + "<br><br>" +
                    "<b>Expires On</b><br>" + securityInfo.certificate.getNotAfter() + "<br><br>" +
                    "<b>Organization (O)</b><br>" + securityInfo.certificate.getSubjectDN().getName() + "<br><br>" +
                    "<b>Common Name (CN)</b><br>" + securityInfo.certificate.getIssuerDN().getName() + "<br><br>" +
                    "<b>Subject Alternative Names</b><br>" + mAlternativeNames;
        } else {
            if(status.sTorBrowsing){
                return "Tor Secured Connection";
            }else {
                return "Connection Not Secured";
            }
        }
    }

    public void onProgressStart() {
        if (!getCurrentURL().equals("about:config") && !getCurrentURL().equals("about:blank") && !getCurrentURL().contains("167.86.99.31") && !wasPreviousErrorPage() && !getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED) && !getCurrentURL().startsWith(CONST_GENESIS_URL_CACHED_DARK) && !getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE) && !getCurrentURL().startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)) {
            mContext.get().runOnUiThread(() -> mEvent.invokeObserver(Arrays.asList(5, mGeckoDataModel.mSessionID), enums.etype.progress_update));
        }
    }

    public void onRedrawPixel() {
        mEvent.invokeObserver(Arrays.asList("", mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, false), dataEnums.eTabCommands.M_UPDATE_PIXEL);
    }

    public boolean isLoaded() {
        return mProgress >= 100;
    }

    /*History Delegate*/

    @UiThread
    public void onSessionStateChange(@NonNull GeckoSession session, @NonNull SessionState sessionState) {
        mSessionState = sessionState;
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, sessionState.toString()), enums.etype.M_UPDATE_SESSION_STATE);


    }

    public boolean onRestoreState() {
        if (mSessionState != null) {
            restoreState(mSessionState);
            return true;
        } else {
            return false;
        }
    }

    @UiThread
    @Nullable
    public GeckoResult<GeckoSession> onNewSession(@NonNull final GeckoSession session, @NonNull final String uri) {
        initURL(uri);
        return null;
    }

    /*Navigation Delegate*/
    public void onLocationChange(@NonNull GeckoSession var1, @Nullable String var2) {

        if (!mIsLoaded) {
            return;
        }

        boolean mPastURLVerify = false;
        try {
            if (mHistoryDelegate.isHistoryEmpty()) {
                //mPastURLVerify = mHistoryList.get(mHistoryList.getCurrentIndex() - 1).getUri().equals(var2);
            }
        } catch (Exception ignored) {
        }

        if (wasBackPressed && mPastURLVerify) {
            if (var2.equals("http://167.86.99.31") || var2.startsWith(CONST_GENESIS_URL_CACHED) || var2.startsWith(CONST_GENESIS_URL_CACHED_DARK)) {
                if (var2.startsWith(CONST_GENESIS_URL_CACHED_DARK) && (status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(mContext.get()))) {
                    isPageLoading = false;
                    mEvent.invokeObserver(null, enums.etype.M_CHANGE_HOME_THEME);
                } else if (var2.startsWith(CONST_GENESIS_URL_CACHED) && (status.sTheme != enums.Theme.THEME_LIGHT && !helperMethod.isDayMode(mContext.get()))) {
                    isPageLoading = false;
                    mEvent.invokeObserver(null, enums.etype.M_CHANGE_HOME_THEME);
                }
            }
        }

        wasBackPressed = false;

        String newUrl = Objects.requireNonNull(var2).split("#")[0];
        if (newUrl.startsWith(CONST_GENESIS_URL_CACHED) || newUrl.startsWith(CONST_GENESIS_URL_CACHED_DARK)) {
            mHistoryDelegate.setURL(constants.CONST_GENESIS_DOMAIN_URL);
        } else if (newUrl.equals(constants.CONST_GENESIS_HELP_URL_CACHE)) {
            if (status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(mContext.get())) {
                mHistoryDelegate.setURL(constants.CONST_GENESIS_HELP_URL);
            } else {
                mHistoryDelegate.setURL(constants.CONST_GENESIS_HELP_URL_CACHE_DARK);
            }
        } else if (!newUrl.equals("about:blank")) {
            mHistoryDelegate.setURL(newUrl);
        }
    }

    private String setGenesisVerificationToken(String pString) {
        try {
            if (pString.contains("?")) {
                pString += "&" + constants.CONST_GENESIS_GMT_TIME_GET_KEY + "=" + trueTimeEncryption.getInstance().getSecretToken() + "&theme=" + status.sTheme;
            } else {
                pString += "?" + constants.CONST_GENESIS_GMT_TIME_GET_KEY + "=" + trueTimeEncryption.getInstance().getSecretToken() + "&theme=" + status.sTheme;
            }
            return pString;
        } catch (Exception ex) {
            return pString;
        }
    }

    public GeckoResult<AllowOrDeny> onLoadRequest(@NonNull GeckoSession var2, @NonNull GeckoSession.NavigationDelegate.LoadRequest var1) {
        if(mMediaSessionDelegate!=null){
            mMediaSessionDelegate.onTrigger(enums.MediaController.STOP);
            mMediaSessionDelegate.onTrigger(enums.MediaController.DESTROY);

            mMediaDelegate.onHideDefaultNotification();
        }
        if (var1.uri.contains("167.86.99.31")) {
            new preferences<>("network.proxy.type", 0).add();
        }else {
            new preferences<>("network.proxy.type", 1).add();
        }

        String m_url = var1.uri;
        if (helperMethod.getHost(m_url).endsWith(".onion")) {
            m_url = m_url.replace("www.", "");
        }

        if (m_url.endsWith("genesisconfigurenewidentity.com/")) {
            initURL(mPrevURL);
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), enums.etype.M_NEW_IDENTITY_MESSAGED);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        String mNormalizeURL = helperMethod.normalize(m_url);
        if (mNormalizeURL != null && mNormalizeURL.endsWith("167.86.99.31")) {
            initURL(constants.CONST_GENESIS_DOMAIN_URL);
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, false), enums.etype.M_LOAD_HOMEPAGE_GENESIS);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        if (!m_url.contains(constants.CONST_GENESIS_GMT_TIME_GET_KEY) && !m_url.startsWith(CONST_GENESIS_URL_CACHED) && !m_url.startsWith(CONST_GENESIS_URL_CACHED_DARK) && var1.uri.startsWith("http://167.86.99.31") && !var1.uri.contains(constants.CONST_GENESIS_LOCAL_TIME_GET_KEY) && !var1.uri.contains(constants.CONST_GENESIS_LOCAL_TIME_GET_KEY)) {

            String mVerificationURL = setGenesisVerificationToken(m_url);
            initURL(mVerificationURL);
            loadUri(mVerificationURL);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        } else if (m_url.startsWith("mailto")) {
            mEvent.invokeObserver(Arrays.asList(m_url, mGeckoDataModel.mSessionID), enums.etype.M_ON_MAIL);
            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        } else if (m_url.contains("167.86.99.31/advert__")) {
            mEvent.invokeObserver(Arrays.asList(m_url, mGeckoDataModel.mSessionID), enums.etype.on_playstore_load);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        } else if (m_url.equals(constants.CONST_GENESIS_DOMAIN_URL_SLASHED) || m_url.startsWith("http://167.86.99.31/?")) {
            initURL(constants.CONST_GENESIS_DOMAIN_URL);
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, false), enums.etype.M_LOAD_HOMEPAGE_GENESIS);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        } else if (m_url.equals("about:blank") && mIsLoaded) {
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), enums.etype.ON_EXPAND_TOP_BAR);
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, false), enums.etype.M_ON_BANNER_UPDATE);
            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        } else if (var1.target == 2) {
            mEvent.invokeObserver(Arrays.asList(m_url, mGeckoDataModel.mSessionID), enums.etype.open_new_tab);
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), enums.etype.ON_EXPAND_TOP_BAR);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        } else if (!m_url.equals("about:blank")) {
            if (mGeckoDataModel.mCurrentURL.startsWith(CONST_GENESIS_URL_CACHED) || mGeckoDataModel.mCurrentURL.startsWith(CONST_GENESIS_URL_CACHED_DARK)) {
                mHistoryDelegate.setURL(constants.CONST_GENESIS_DOMAIN_URL);
            } else if (mGeckoDataModel.mCurrentURL.equals(constants.CONST_GENESIS_HELP_URL_CACHE)) {
                if (status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(mContext.get())) {
                    mHistoryDelegate.setURL(constants.CONST_GENESIS_HELP_URL);
                } else {
                    mHistoryDelegate.setURL(constants.CONST_GENESIS_HELP_URL_CACHE_DARK);
                }
            } else if (!m_url.startsWith("resource://android/assets/homepage/")) {
                mHistoryDelegate.setURL(m_url);
            }

            mEvent.invokeObserver(Arrays.asList(m_url, mGeckoDataModel.mSessionID), enums.etype.start_proxy);

            /* Its Absence causes delay on first launch*/

            //event.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.m_current_url_id, mGeckoDataModel.mTheme, this), enums.etype.ON_UPDATE_SEARCH_BAR);

            if (!m_url.equals("about:config") && !mGeckoDataModel.mCurrentURL.contains("167.86.99.31")) {
                mProgress = 5;
                onProgressStart();
            }

            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        } else {
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
    }

    public void onUpdateBannerAdvert() {
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, false), enums.etype.M_ON_BANNER_UPDATE);
    }

    public void onClose() {
        stop();
        if (!mPrevURL.equals("about:blank")) {
            this.mGeckoDataModel.mCurrentURL = mPrevURL;
        } else {
            mPrevURL = mGeckoDataModel.mCurrentURL;
        }
        if (mMediaDelegate != null) {
            mMediaDelegate.onHideDefaultNotification();
        }
    }

    public GeckoResult<String> onLoadError(@NonNull GeckoSession var1, @Nullable String var2, @NonNull WebRequestError var3) {

        try {
            if(mMediaDelegate !=null){
                mMediaDelegate.onHideDefaultNotification();
            }
            if (helperMethod.getHost(var2).endsWith(".onion")) {
                var2 = var2.replace("www.", "");
            }

            if (var2.startsWith("https://trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd")) {
                var2 = var2.replace("https", "http");
                mGeckoDataModel.mCurrentURL = var2;
                this.mGeckoDataModel.mCurrentURL = var2;
            }
            if (mGeckoDataModel.mCurrentURL.contains("orion.onion")) {
                mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), enums.etype.M_NEW_IDENTITY);
            }
            if (status.sSettingIsAppStarted && orbotLocalConstants.mIsTorInitialized) {
                errorHandler handler = new errorHandler();
                mProgress = 0;
                mPreviousErrorPage = true;
                mEvent.invokeObserver(Arrays.asList(var2, mGeckoDataModel.mSessionID), enums.etype.on_load_error);
                mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), enums.etype.ON_UPDATE_THEME);

                InputStream mResourceURL = null;
                try {
                    if (var3.code == 50) {
                        if (status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(mContext.get())) {
                            mResourceURL = mContext.get().getResources().getAssets().open(CONST_GENESIS_BADCERT_CACHED);
                        } else {
                            mResourceURL = mContext.get().getResources().getAssets().open(CONST_GENESIS_BADCERT_CACHED_DARK);
                        }
                    } else {
                        if (status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(mContext.get())) {
                            mResourceURL = mContext.get().getResources().getAssets().open(CONST_GENESIS_ERROR_CACHED);
                        } else {
                            mResourceURL = mContext.get().getResources().getAssets().open(CONST_GENESIS_ERROR_CACHED_DARK);
                        }
                    }
                } catch (Exception ex) {
                    Log.i("asd", "asd : " + ex.getMessage());
                }

                return GeckoResult.fromValue("data:text/html," + handler.createErrorPage(var3.category, var3.code, mContext.get(), var2, mResourceURL));
            } else {
                mEvent.invokeObserver(Arrays.asList(var2, mGeckoDataModel.mSessionID), enums.etype.M_ORBOT_LOADING);
                mGeckoDataModel.mCurrentURL = mPrevURL;
                this.mGeckoDataModel.mCurrentURL = mPrevURL;
                //event.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.m_current_url_id, mGeckoDataModel.mTheme, this), enums.etype.ON_UPDATE_SEARCH_BAR);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    /*Content Delegate*/
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


    public void onFullScreenInvoke(boolean var2) {
        mSelectionActionDelegate.setFullScreen(mGeckoDataModel.mFullScreenStatus);
        this.mSelectionActionDelegate.enableExternalActions(!var2);
    }


    void downloadRequestedFile() {
        geckoDownloadManager mDownloadManager = mContentDelegate.getDownloadManager();

        if (mDownloadManager.getDownloadURL() != null && mDownloadManager.getDownloadFile() != null) {
            if (!createAndSaveFileFromBase64Url(mDownloadManager.getDownloadURL().toString())) {
                pluginController.getInstance().onDownloadInvoke(Arrays.asList(mDownloadManager.getDownloadURL(), mDownloadManager.getDownloadFile()), pluginEnums.eDownloadManager.M_WEB_DOWNLOAD_REQUEST);
            }
        }
    }

    void downloadRequestedFile(Uri downloadURL, String downloadFile) {
        if (downloadURL != null && downloadFile != null) {
            if (!createAndSaveFileFromBase64Url(downloadURL.toString())) {
                pluginController.getInstance().onDownloadInvoke(Arrays.asList(downloadURL, downloadFile), pluginEnums.eDownloadManager.M_WEB_DOWNLOAD_REQUEST);
            }
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

        try {
            if (!url.startsWith("data") && !url.startsWith("blob")) {
                return false;
            }

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String filetype;
            String filename;

            if (url.startsWith("blob")) {
                loadUri((String) pluginController.getInstance().onDownloadInvoke(Collections.singletonList(url), pluginEnums.eDownloadManager.M_BLOB_DOWNLOAD_REQUEST));
                return true;
            }

            filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"));
            filename = System.currentTimeMillis() + "." + filetype;

            File file = new File(path, filename);
            try {
                if (!path.exists())
                    path.mkdirs();
                if (!file.exists())
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
                Uri uri_temp = FileProvider.getUriForFile(mContext.get().getApplicationContext(), mContext.get().getString(R.string.GENERAL_FILE_PROVIDER_AUTHORITY), file);
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
        } catch (Exception ex) {
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

    public mediaSessionDelegate getMediaSessionDelegate(){
        return mMediaSessionDelegate;
    }

    public scrollDelegate getmScrollDelegate(){
        return mScrollDelegate;
    }

    public contentDelegate getContentDelegate(){
        return mContentDelegate;
    }

    /*Helper Methods*/

    void toogleUserAgent() {
        if (getSettings().getUserAgentMode() == USER_AGENT_MODE_DESKTOP) {
            getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE);
        } else {
            getSettings().setUserAgentMode(USER_AGENT_MODE_DESKTOP);
        }

    }

    int getUserAgentMode() {
        return getSettings().getUserAgentMode();
    }

    public String getCurrentURL() {
        return mGeckoDataModel.mCurrentURL;
    }

    public String getTitle() {
        return mGeckoDataModel.mCurrentTitle;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setTitle(String title) {
        mGeckoDataModel.mCurrentTitle = title;
    }

    public void setRemovableFromBackPressed(boolean pStatus) {
        mRemovableFromBackPressed = pStatus;
    }

    public boolean getRemovableFromBackPressed() {
        if (mGeckoDataModel.mCurrentURL.startsWith("data") || mGeckoDataModel.mCurrentURL.startsWith("blob")) {
            return true;
        } else {
            return mRemovableFromBackPressed;
        }
    }

    public void setTheme(String pTheme) {
        mGeckoDataModel.mTheme = pTheme;
    }

    public String getTheme() {
        return mGeckoDataModel.mTheme;
    }

    @Override
    public void onCanGoBack(@NonNull GeckoSession session, boolean var2) {
        mCanGoBack = var2;
    }

    @Override
    public void onCanGoForward(@NonNull GeckoSession session, boolean var2) {
        mCanGoForward = var2;
    }


    boolean canGoBack() {
        if (mHistoryDelegate.isHistoryEmpty()) {
            return false;
        }
        return mCanGoBack;
    }

    public boolean wasPreviousErrorPage() {
        return mPreviousErrorPage;
    }

    boolean canGoForward() {
        return mCanGoForward;
    }

    public String getSessionID() {
        return mGeckoDataModel.mSessionID;
    }

    public void setSessionID(String pSession) {
        mGeckoDataModel.mSessionID = pSession;
    }

    void exitScreen() {
        this.exitFullScreen();
    }

    public void closeSession() {
        mEvent.invokeObserver(Arrays.asList(null, mGeckoDataModel.mSessionID), enums.etype.on_close_sesson);
    }

    public void closeSessionInstant() {
        if (mMediaDelegate != null) {
            mMediaDelegate.onHideDefaultNotification();
        }
        mGeckoDataModel.mSessionID = "-1";
        new Handler().postDelayed(() ->
        {
            mClosed = true;
        }, 250);
        close();
    }

    public boolean isClosed() {
        return mClosed;
    }

    GeckoResult<FinderResult> mFinder = null;

    public void findInPage(String pQuery, int pDirection) {
        mFinder = null;
        mFinder = getFinder().find(pQuery, pDirection);
        new Thread() {
            public void run() {

                int mCounter = 0;
                while (mFinder == null) {
                    try {
                        mCounter += 1;
                        sleep(100);
                        if (mCounter > 100) {
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    FinderResult mResult = mFinder.poll(1000);
                    mEvent.invokeObserver(Arrays.asList(mResult.total, mResult.current), enums.etype.FINDER_RESULT_CALLBACK);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }.start();

    }

    void goBackSession() {
        if(mMediaSessionDelegate!=null){
            mMediaSessionDelegate.onTrigger(enums.MediaController.DESTROY);
            mMediaDelegate.onHideDefaultNotification();
        }
        wasBackPressed = true;
        mGeckoDataModel.mCurrentURL_ID = -1;
        goBack();
    }

    void goForwardSession() {
        if(mMediaSessionDelegate!=null){
            mMediaSessionDelegate.onTrigger(enums.MediaController.STOP);
            mMediaDelegate.onHideDefaultNotification();
        }

        goForward();
    }

    boolean isLoading() {
        return isPageLoading;
    }

    void setLoading(boolean status) {
        isPageLoading = status;
    }

    Uri getUriPermission() {
        return mUriPermission;
    }

    private void checkApplicationRate() {
        if (status.sSettingIsAppStarted) {
            if (status.sGlobalURLCount == 10) {
                mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), M_RATE_APPLICATION);
            } else if ( status.sGlobalURLCount == 20 || status.sGlobalURLCount == 80) {
                mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), M_DEFAULT_BROWSER);
            }

            status.sGlobalURLCount += 1;
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), M_RATE_COUNT);
        }
    }
}
