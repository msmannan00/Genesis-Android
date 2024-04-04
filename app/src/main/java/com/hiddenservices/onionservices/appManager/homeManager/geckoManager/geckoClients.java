package com.hiddenservices.onionservices.appManager.homeManager.geckoManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.downloadManager.geckoDownloadManager;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.appManager.kotlinHelperLibraries.BrowserIconManager;
import com.hiddenservices.onionservices.constants.*;
import com.hiddenservices.onionservices.dataManager.dataController;
import com.hiddenservices.onionservices.dataManager.dataEnums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_URL_CACHED;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_URL_CACHED_DARK;
import static com.hiddenservices.onionservices.constants.constants.CONST_PRIVACY_POLICY_URL_NON_TOR;
import static com.hiddenservices.onionservices.constants.constants.CONST_REPORT_URL;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_LOAD_NEW_TAB;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_MAX_TAB_REACHED;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_MOBILE;
import static org.mozilla.geckoview.StorageController.ClearFlags.AUTH_SESSIONS;
import static org.mozilla.geckoview.StorageController.ClearFlags.COOKIES;
import static org.mozilla.geckoview.StorageController.ClearFlags.DOM_STORAGES;
import static org.mozilla.geckoview.StorageController.ClearFlags.IMAGE_CACHE;
import static org.mozilla.geckoview.StorageController.ClearFlags.NETWORK_CACHE;
import static org.mozilla.geckoview.StorageController.ClearFlags.PERMISSIONS;
import static org.mozilla.geckoview.StorageController.ClearFlags.SITE_DATA;
import static org.mozilla.geckoview.StorageController.ClearFlags.SITE_SETTINGS;
import org.mozilla.geckoview.ContentBlocking;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.WebResponse;

public class geckoClients {

    /*Gecko Variables*/

    private static GeckoRuntime mRuntime;
    private geckoSession mSession = null;
    private BrowserIconManager mIconManager;
    private eventObserver.eventListener mEvent;
    private String mSessionID;

    /*Local Initialization*/

    public void initializeSession(GeckoView pGeckoView, eventObserver.eventListener pEvent, AppCompatActivity pContext, ActivityResultLauncher<Intent> pUploadRequestLauncher) {
        if (pGeckoView.getSession() != null) {
            pGeckoView.releaseSession();
        }

        this.mEvent = pEvent;
        mSessionID = helperMethod.createRandomID();
        mSession = initSettings(pGeckoView, pEvent, pContext, mSessionID, pUploadRequestLauncher);
        initRuntimeSettings(pContext);
        mSession.open(mRuntime);
        pGeckoView.setSession(mSession);
        onUpdateFont();
    }

    public geckoSession initializeSessionInBackground(GeckoView pGeckoView, eventObserver.eventListener pEvent, AppCompatActivity pContext, String pURL, ActivityResultLauncher<Intent> pUploadRequestLauncher) {
        geckoSession mSessionHidden = initSettings(pGeckoView, pEvent, pContext, helperMethod.createRandomID(), pUploadRequestLauncher);
        mSessionHidden.open(mRuntime);
        mSessionHidden.loadUri(pURL);

        pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(pContext), M_LOAD_NEW_TAB);
        dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_ADD_TAB, Arrays.asList(mSessionHidden, true));
        return mSessionHidden;
    }

    private geckoSession initSettings(GeckoView pGeckoView, eventObserver.eventListener ignoredPEvent, AppCompatActivity pContext, String pSessionID, ActivityResultLauncher<Intent> pUploadRequestLauncher){
        geckoSession mSessionInitializer = new geckoSession(new geckoViewClientCallback(), pSessionID, pContext, pGeckoView, pUploadRequestLauncher);
        mSessionInitializer.getSettings().setUseTrackingProtection(status.sStatusDoNotTrack);
        mSessionInitializer.getSettings().setFullAccessibilityTree(true);
        mSessionInitializer.getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE);
        mSessionInitializer.getSettings().setAllowJavascript(status.sSettingJavaStatus && status.sSettingTrackingProtection!=ContentBlocking.AntiTracking.STRICT);
        return mSessionInitializer;
    }

    public void initializeIcon(Context pcontext) {
        if (mIconManager == null) {
            mIconManager = new BrowserIconManager();
            mIconManager.init(pcontext, mRuntime);
        }
    }

    public void onSaveCurrentTab(boolean isHardCopy, AppCompatActivity pContext) {
        int mStatus = (Integer) dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_ADD_TAB, Arrays.asList(mSession, isHardCopy));
        if (mStatus == enums.AddTabCallback.TAB_FULL) {
            pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(pContext), M_MAX_TAB_REACHED);
        }
    }

    public void initRestore(geckoView mNestedGeckoView, AppCompatActivity pContext) {
        if(mSession==null){
            return;
        }
        boolean mState = mSession.onRestoreState();
        if (!mState) {
            mSession.stop();
            loadURL(mSession.getCurrentURL(), pContext);
        } else {
            String mURL = mSession.getCurrentURL();
            if (mURL.equals("http://167.86.99.31") || mURL.startsWith(CONST_GENESIS_URL_CACHED) || mURL.startsWith(CONST_GENESIS_URL_CACHED_DARK)) {
                if (!mSession.getNavigationDelegate().canGoBack()) {
                    mNestedGeckoView.releaseSession();
                    mSession.close();
                    mSession.open(mRuntime);
                    mNestedGeckoView.setSession(mSession);
                } else {
                    mSession.goBack();
                }
                loadURL("http://167.86.99.31", pContext);
            }
        }
    }

    @SuppressLint("WrongConstant")
    public void initRuntimeSettings(AppCompatActivity context) {
        if (mRuntime == null) {
            GeckoRuntimeSettings.Builder mSettings = new GeckoRuntimeSettings.Builder();
            if (status.sShowImages == 2) {
                mSettings.configFilePath(helperMethod.getAssetsCacheFile(context, "geckoview-config-noimage.yaml"));
            } else {
                mSettings.configFilePath(helperMethod.getAssetsCacheFile(context, "geckoview-config.yaml"));
            }
            mSettings.build();

            mRuntime = GeckoRuntime.create(context, mSettings.build());
            mRuntime.getSettings().setAboutConfigEnabled(true);
            mRuntime.getSettings().setAutomaticFontSizeAdjustment(false);
            mRuntime.getSettings().setWebFontsEnabled(status.sShowWebFonts);
            mRuntime.getSettings().setForceUserScalableEnabled(status.sSettingEnableZoom);
            mRuntime.getSettings().getContentBlocking().setCookieBehavior(getCookiesBehaviour());
            mRuntime.getSettings().getContentBlocking().setSafeBrowsing(ContentBlocking.SafeBrowsing.DEFAULT);

            if (status.sSettingTrackingProtection == ContentBlocking.AntiTracking.DEFAULT) {
                mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.DEFAULT);
            } else if (status.sSettingTrackingProtection == ContentBlocking.AntiTracking.STRICT) {
                mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.STRICT);
            }

            mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.NONE);
            mRuntime.getSettings().getContentBlocking().setSafeBrowsing(ContentBlocking.SafeBrowsing.NONE);

            dataController.getInstance().initializeListData();
            onClearAll();
        }
    }

    public void onReload(AppCompatActivity pContext, boolean isThemeCall, boolean isDelayed) {
        int mDelay = 1000;
        if(!isDelayed){
            mDelay = 0;
        }
        new Handler().postDelayed(() ->
        {   if(mSession != null){
            mSession.stop();
            String url = mSession.getCurrentURL();
            if (url.startsWith("http://167.86.99.31/?pG") || url.startsWith("https://167.86.99.31?pG") || url.endsWith("167.86.99.31") || url.contains(constants.CONST_GENESIS_HELP_URL_SUB) || url.contains(constants.CONST_GENESIS_HELP_URL_CACHE) || url.contains(constants.CONST_GENESIS_HELP_URL_CACHE_DARK)) {
                loadURL(mSession.getCurrentURL(), pContext);
            } else if (!isThemeCall) {
                mSession.reload();
            }
        }
        }, mDelay);
    }

    public void onLoadTab(geckoSession pSession, GeckoView geckoView) {
        mSession = pSession;
        geckoView.releaseSession();
        geckoView.setSession(pSession);
        mSession.initCallback(new geckoViewClientCallback());
        this.mSessionID = mSession.getSessionID();
    }

    @SuppressLint("WrongConstant")
    public void initStaticSettings(){
        mSession.getSettings().setUseTrackingProtection(status.sStatusDoNotTrack);
        mSession.getSettings().setFullAccessibilityTree(true);
        mSession.getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE);
        mSession.getSettings().setAllowJavascript(status.sSettingJavaStatus && status.sSettingTrackingProtection!=ContentBlocking.AntiTracking.STRICT);
        mRuntime.getSettings().setAboutConfigEnabled(true);
        mRuntime.getSettings().setAutomaticFontSizeAdjustment(false);
        mRuntime.getSettings().setWebFontsEnabled(status.sShowWebFonts);
        mRuntime.getSettings().setForceUserScalableEnabled(status.sSettingEnableZoom);
        mRuntime.getSettings().getContentBlocking().setCookieBehavior(status.sSettingCookieStatus);
        mRuntime.getSettings().getContentBlocking().setSafeBrowsing(ContentBlocking.SafeBrowsing.DEFAULT);

    }

    public void loadURL(String url, AppCompatActivity pContext) {

        if (url.startsWith("https://orion.onion/privacy")) {
            url = CONST_PRIVACY_POLICY_URL_NON_TOR;
        }
        if (!status.sTorBrowsing && url.equals("https://167.86.99.31/privacy")) {
            url = CONST_PRIVACY_POLICY_URL_NON_TOR;
        }

        url = helperMethod.completeURL(url);
        mSession.initURL(url);
        initStaticSettings();
        if (!url.startsWith(CONST_REPORT_URL) && (url.startsWith("resource://android/assets/homepage/") || url.startsWith("http://167.86.99.31/?pG") || url.startsWith("https://167.86.99.31?pG") || url.endsWith("167.86.99.31") || url.endsWith(constants.CONST_GENESIS_DOMAIN_URL_SLASHED))) {
            try {
                mSession.initURL(constants.CONST_GENESIS_DOMAIN_URL);
                if (status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(pContext)) {
                    String mURL = constants.CONST_GENESIS_URL_CACHED + "?pData=" + dataController.getInstance().invokeReferenceWebsite(dataEnums.eReferenceWebsiteCommands.M_FETCH);
                    mSession.getSettings().setAllowJavascript(true);
                    mSession.initURL(mURL);

                    mSession.stop();
                    mSession.loadUri(mURL);
                } else {
                    String mURL = constants.CONST_GENESIS_URL_CACHED_DARK + "?pData=" + dataController.getInstance().invokeReferenceWebsite(dataEnums.eReferenceWebsiteCommands.M_FETCH);
                    mSession.getSettings().setAllowJavascript(true);
                    mSession.loadUri(mURL);
                }
            } catch (Exception ignored) {
            }
        } else if (url.contains(constants.CONST_GENESIS_HELP_URL_SUB) || url.contains(constants.CONST_GENESIS_HELP_URL_CACHE) || url.contains(constants.CONST_GENESIS_HELP_URL_CACHE_DARK)) {
            try {
                mSession.initURL(constants.CONST_GENESIS_HELP_URL);

                if (status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(pContext)) {
                    mSession.getSettings().setAllowJavascript(true);
                    mSession.loadUri(constants.CONST_GENESIS_HELP_URL_CACHE);
                } else {
                    mSession.getSettings().setAllowJavascript(true);
                    mSession.loadUri(constants.CONST_GENESIS_HELP_URL_CACHE_DARK);
                }
            } catch (Exception ignored) {
            }
        } else {
            mSession.getSettings().setAllowJavascript(status.sSettingJavaStatus && status.sSettingTrackingProtection!=ContentBlocking.AntiTracking.STRICT);
            mSession.loadUri(url);
        }
        mSession.initURL(url);
    }

    public void initHomeTheme() {
        String mURLFinal;
        mSession.initURL(constants.CONST_GENESIS_DOMAIN_URL);
        if (status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(activityContextManager.getInstance().getHomeController())) {
            String mURL = constants.CONST_GENESIS_URL_CACHED + "?pData=" + dataController.getInstance().invokeReferenceWebsite(dataEnums.eReferenceWebsiteCommands.M_FETCH);
            mSession.getSettings().setAllowJavascript(true);
            mSession.initURL(mURL);
            mURLFinal = mURL;
        } else {
            String mURL = constants.CONST_GENESIS_URL_CACHED_DARK + "?pData=" + dataController.getInstance().invokeReferenceWebsite(dataEnums.eReferenceWebsiteCommands.M_FETCH);
            mSession.getSettings().setAllowJavascript(true);
            mSession.initURL(mURL);
            mURLFinal = mURL;
        }

        if (!mSession.getNavigationDelegate().canGoBack()) {
            activityContextManager.getInstance().getHomeController().getGeckoView().releaseSession();
            mSession.close();
            mSession.open(mRuntime);
            activityContextManager.getInstance().getHomeController().getGeckoView().setSession(mSession);
        } else {
            mSession.goBack();
        }

        new Handler().postDelayed(() ->
        {
            if (!mSession.getNavigationDelegate().canGoBack()) {
                mSession.close();
                activityContextManager.getInstance().getHomeController().getGeckoView().releaseSession();
                mSession.open(mRuntime);
                activityContextManager.getInstance().getHomeController().getGeckoView().setSession(mSession);
            }

            mSession.loadUri(mURLFinal);

        }, 10);

    }

    /*Helper Classes*/


    public void toggleUserAgent() {
        mSession.toggleUserAgent();
    }

    public int getUserAgent() {
        return mSession.getUserAgentMode();
    }

    public void onGetFavIcon(ImageView pImageView, String pURL, AppCompatActivity pContext) {
        if(status.sLowMemory != enums.MemoryStatus.CRITICAL_MEMORY && status.sLowMemory != enums.MemoryStatus.LOW_MEMORY){
            initializeIcon(pContext);
            pURL = helperMethod.completeURL(helperMethod.getDomainName(pURL));
            mIconManager.onLoadIconIntoView(pImageView, pURL);
        }
    }

    private int getCookiesBehaviour() {
        return status.sSettingCookieStatus;
    }

    public void resetSession() {
        mSessionID = strings.GENERIC_EMPTY_STR;
    }

    public Object onMediaInvoke(enums.MediaController mController){
        if(mSession!=null && mSession.getMediaSessionDelegate()!=null){
            return mSession.getMediaSessionDelegate().onTrigger(mController);
        }
        return null;
    }

    public String getTheme() {
        try {
            if (mSessionID.equals(strings.GENERIC_EMPTY_STR)) {
                return null;
            } else if (mSession != null && mSession.getTheme() != null) {
                return mSession.getTheme();
            } else {
                return null;
            }
        }catch (Exception ex){
            return null;
        }
    }

    public void onClearAll() {
        if (mRuntime != null) {
            if(status.sClearOnExit){
                mRuntime.getStorageController().clearData(NETWORK_CACHE);
                mRuntime.getStorageController().clearData(IMAGE_CACHE);
                mRuntime.getStorageController().clearData(DOM_STORAGES);
                mRuntime.getStorageController().clearData(COOKIES);
                mRuntime.getStorageController().clearData(SITE_SETTINGS);
                mRuntime.getStorageController().clearData(SITE_DATA);
            }
        }
    }

    public void onClearSiteData() {
        if (mRuntime != null) {
            mRuntime.getStorageController().clearData(SITE_SETTINGS);
            mRuntime.getStorageController().clearData(SITE_DATA);
        }
    }
    public void onClearSession() {
        if (mRuntime != null) {
            mRuntime.getStorageController().clearData(AUTH_SESSIONS);
            mRuntime.getStorageController().clearData(PERMISSIONS);
        }
    }

    public void onClearCache() {
        if (mRuntime != null) {
            mRuntime.getStorageController().clearData(NETWORK_CACHE);
            mRuntime.getStorageController().clearData(IMAGE_CACHE);
            mRuntime.getStorageController().clearData(DOM_STORAGES);
        }
    }

    public void onClearCookies() {
        if (mRuntime != null) {
            mRuntime.getStorageController().clearData(COOKIES);
        }
    }

    public void onBackPressed(boolean isFinishAllowed, int mTabSize) {
        if (mSession.getNavigationDelegate().canGoBack()) {
            mSession.goBackSession();
        } else if (isFinishAllowed) {
            if (mSession.isRemovableFromBackPressed() && mTabSize > 1) {
                mSession.stop();
                mEvent.invokeObserver(null, homeEnums.eGeckoCallback.M_CLOSE_TAB_BACK);
            } else {
                mEvent.invokeObserver(null, homeEnums.eGeckoCallback.BACK_LIST_EMPTY);
            }
        }
    }

    public String getSecurityInfo() {
        return mSession.getProgressDelegate().getSecurityInfo();
    }

    public void onUploadRequest(int resultCode, Intent data) {
        mSession.getDownloadHandler().onFileUploadRequest(resultCode, data, mSession.getPromptDelegate());
    }

    public boolean canGoForward() {
        return mSession.getNavigationDelegate().canGoForward();
    }

    public Uri getUriPermission() {
        return mSession.getDownloadHandler().getUriPermission();
    }

    public boolean getFullScreenStatus() {
        if(mSession==null){
            return true;
        }
        return !mSession.getContentDelegate().getFullScreenStatus();
    }

    public void onExitFullScreen() {
        if(mSession!=null){
            mSession.exitScreen();
        }
    }

    public void onForwardPressed() {
        if (mSession.getNavigationDelegate().canGoForward()) {
            mSession.goForwardSession();
        }
    }

    public void setRemovableFromBackPressed(boolean pStatus) {
        mSession.setRemovableFromBackPressed(pStatus);
    }

    public void onUpdateFont() {
        float font = (status.sSettingFontSize - 100) / 3 + 100;
        mRuntime.getSettings().setFontSizeFactor(font / 117);
    }

    public int getScrollOffset() {
        return mSession.getmScrollDelegate().getScrollOffset();
    }

    public void manualDownloadWithName(String url, String file, AppCompatActivity context) {
        Uri downloadURL = Uri.parse(url);
        if (helperMethod.checkPermissions(context)) {
            mSession.getDownloadHandler().downloadRequestedFile(downloadURL, file);
        }
    }

    public void downloadFile(AppCompatActivity pContext) {
        if (helperMethod.checkPermissions(pContext)) {
            geckoDownloadManager mDownloadManager = mSession.getContentDelegate().getDownloadManager();
            mSession.getDownloadHandler().downloadRequestedFile(mDownloadManager);
        }
    }

    public void manual_download(String url, AppCompatActivity context) {
        Uri downloadURL = Uri.parse(url);
        File f = new File(url);
        f.getName();
        String downloadFile = f.getName();

        if (helperMethod.checkPermissions(context)) {
            mSession.getDownloadHandler().downloadRequestedFile(downloadURL, downloadFile);
        }
    }

    public void onClose() {
        onMediaInvoke(enums.MediaController.DESTROY);
        mSession.onClose();
    }

    /*Private Return*/

    public GeckoRuntime getRuntime() {
        return mRuntime;
    }

    public geckoSession getSession() {
        return mSession;
    }

    /*Session Updates*/

    public class geckoViewClientCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            mSessionID = activityContextManager.getInstance().getHomeController().getSessionID();
            if (e_type.equals(homeEnums.eGeckoCallback.ON_FULL_SCREEN)) {
                mSession.onFullScreenInvoke((boolean)data.get(0));
            }
            if (e_type.equals(homeEnums.eGeckoCallback.ON_DESTROY_MEDIA)) {
                if(mSession.getMediaSessionDelegate()!=null){
                    mSession.getMediaSessionDelegate().onTrigger(enums.MediaController.DESTROY);
                }
            }
            if (e_type.equals(homeEnums.eGeckoCallback.M_CHANGE_HOME_THEME)) {
                initHomeTheme();
            }

            else if (mSession != null) {
                if (e_type.equals(homeEnums.eGeckoCallback.SESSION_ID)) {
                    return mSession.getSessionID();
                } else if (mSessionID != null && mSessionID.equals(data.get(1)) || e_type.equals(homeEnums.eGeckoCallback.M_UPDATE_SSL) || e_type.equals(homeEnums.eGeckoCallback.ON_INVOKE_PARSER) || e_type.equals(homeEnums.eGeckoCallback.M_RATE_COUNT) || e_type.equals(homeEnums.eGeckoCallback.FINDER_RESULT_CALLBACK) || e_type.equals(homeEnums.eGeckoCallback.ON_UPDATE_TAB_TITLE) || e_type.equals(homeEnums.eGeckoCallback.ON_UPDATE_FAVICON) || e_type.equals(homeEnums.eGeckoCallback.ON_UPDATE_HISTORY) || e_type.equals(homeEnums.eGeckoCallback.ON_REQUEST_COMPLETED) || e_type.equals(homeEnums.eGeckoCallback.ON_UPDATE_SUGGESTION) || e_type.equals(homeEnums.eGeckoCallback.ON_UPDATE_SUGGESTION_URL)) {
                    if (mSession.getContentDelegate().isClosed()) {
                        return null;
                    }
                    if (e_type.equals(homeEnums.eGeckoCallback.ON_HANDLE_EXTERNAL_INTENT)) {
                        try {
                            WebResponse responseInfo = (WebResponse) data.get(0);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndTypeAndNormalize(Uri.parse(responseInfo.uri), responseInfo.headers.get("Content-Type"));
                            activityContextManager.getInstance().getHomeController().startActivity(intent);
                        } catch (Exception ignored) {
                        }
                    } else {
                        return mEvent.invokeObserver(data, e_type);
                    }
                }
            }
            return null;
        }
    }
}
