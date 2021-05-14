package com.darkweb.genesissearchengine.appManager.homeManager.geckoManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.kotlinHelperLibraries.BrowserIconManager;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

import java.io.File;
import java.util.List;

import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_URL_CACHED;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_URL_CACHED_DARK;
import static com.darkweb.genesissearchengine.constants.constants.CONST_REPORT_URL;
import static com.darkweb.genesissearchengine.constants.enums.etype.on_handle_external_intent;
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
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.WebResponse;

public class geckoClients
{
    /*Gecko Variables*/

    private geckoSession mSession = null;
    private GeckoRuntime mRuntime = null;
    private BrowserIconManager mIconManager;
    private eventObserver.eventListener event;

    /*Local Variable*/

    private String mSessionID;

    public void initialize(GeckoView geckoView, eventObserver.eventListener event, AppCompatActivity context, boolean isForced)
    {
        this.event = event;
        mSessionID = helperMethod.createRandomID();
        initRuntimeSettings(context);

        if(!isForced && geckoView.getSession()!=null && geckoView.getSession().isOpen()){
            mSession = (geckoSession) geckoView.getSession();
        }
        else {
            geckoView.releaseSession();
            mSession = new geckoSession(new geckoViewClientCallback(),mSessionID,context, geckoView);
            mSession.open(mRuntime);
            mSession.getSettings().setUseTrackingProtection(status.sStatusDoNotTrack);
            mSession.getSettings().setFullAccessibilityTree(true);
            mSession.getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE);
            mSession.getSettings().setAllowJavascript(status.sSettingJavaStatus);
            geckoView.releaseSession();
            geckoView.setSession(mSession);
        }
        mSession.onSetInitializeFromStartup();
        onUpdateFont();
    }

    public void onValidateInitializeFromStartup(NestedGeckoView mNestedGeckoView, AppCompatActivity pcontext){
        boolean mStatus = mSession.onValidateInitializeFromStartup();
        if(mStatus){
            boolean mState = mSession.onRestoreState();
            if(!mState){
                new Handler().postDelayed(() ->
                {
                    mSession.stop();
                    mSession.loadUri(mSession.getCurrentURL());
                }, 500);
            }else {
                String mURL = mSession.getCurrentURL();
                if(mURL.equals("https://genesishiddentechnologies.com") || mURL.startsWith(CONST_GENESIS_URL_CACHED) || mURL.startsWith(CONST_GENESIS_URL_CACHED_DARK)){
                    if(!mSession.canGoBack()){
                        mNestedGeckoView.releaseSession();
                        mSession.close();
                        mSession.open(mRuntime);
                        mNestedGeckoView.setSession(mSession);
                    }else {
                        mSession.goBack();
                    }
                    loadURL("genesishiddentechnologies.com", mNestedGeckoView, pcontext);
                }
            }
        }
    }

    public geckoSession initFreeSession(GeckoView pGeckoView, AppCompatActivity pcontext, eventObserver.eventListener event){
        this.event = event;
        initRuntimeSettings(pcontext);
        geckoSession mTempSession = new geckoSession(new geckoViewClientCallback(),mSessionID,pcontext, pGeckoView);
        mTempSession.open(mRuntime);
        mTempSession.getSettings().setUseTrackingProtection(status.sStatusDoNotTrack);
        mTempSession.getSettings().setFullAccessibilityTree(true);
        mTempSession.getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE);
        mTempSession.getSettings().setAllowJavascript(status.sSettingJavaStatus);
        return mTempSession;
    }

    public void onDestroy(){
        mSession.onDestroy();
        mSession = null;
        mRuntime = null;
        mIconManager = null;
        event = null;
    }

    public GeckoRuntime getmRuntime(){
        return mRuntime;
    }

    public void onSessionReinit(){
        mSession.onSessionReinit();
    }

    public void toogleUserAgent(){
        mSession.toogleUserAgent();
    }

    public int getUserAgent(){
        return mSession.getUserAgentMode();
    }

    @SuppressLint("WrongConstant")
    public void initRuntimeSettings(AppCompatActivity context){
        if(mRuntime==null){
            mRuntime = GeckoRuntime.getDefault(context.getApplicationContext());
            mRuntime.getSettings().setAboutConfigEnabled(true);
            mRuntime.getSettings().setAutomaticFontSizeAdjustment(false);
            mRuntime.getSettings().setWebFontsEnabled(status.sShowWebFonts);
            mRuntime.getSettings().setForceUserScalableEnabled(status.sSettingEnableZoom);
            mRuntime.getSettings().getContentBlocking().setCookieBehavior(getCookiesBehaviour());
            mRuntime.getSettings().getContentBlocking().setSafeBrowsing(ContentBlocking.SafeBrowsing.DEFAULT);
            mIconManager = new BrowserIconManager();

            if(status.sSettingTrackingProtection == 1){
                mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.DEFAULT);
            }else if(status.sSettingTrackingProtection == 2){
                mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.STRICT);
            }
        }
    }

    public void onGetFavIcon(ImageView pImageView, String pURL, AppCompatActivity pcontext){
        pURL = helperMethod.completeURL(helperMethod.getDomainName(pURL));
        mIconManager.onLoadIconIntoView(pcontext,mRuntime, pImageView, pURL);
    }

    public void onLoadFavIcon(AppCompatActivity pcontext){
        BrowserIconManager mIconManager = new BrowserIconManager();
        mIconManager.onLoadIcon(pcontext.getApplicationContext(), mRuntime);
    }

    private int getCookiesBehaviour(){
        return status.sSettingCookieStatus;
    }

    @SuppressLint("WrongConstant")
    public void updateSetting(NestedGeckoView mNestedGeckoView, AppCompatActivity pcontext){
        mRuntime.getSettings().setRemoteDebuggingEnabled(false);
        mRuntime.getSettings().setWebFontsEnabled(status.sShowWebFonts);
        mRuntime.getSettings().getContentBlocking().setCookieBehavior(getCookiesBehaviour());
        mRuntime.getSettings().setAutomaticFontSizeAdjustment(false);
        mRuntime.getSettings().getContentBlocking().setSafeBrowsing(ContentBlocking.SafeBrowsing.DEFAULT);
        mRuntime.getSettings().setWebFontsEnabled(status.sShowWebFonts);
        mRuntime.getSettings().setForceUserScalableEnabled(status.sSettingEnableZoom);
        mIconManager = new BrowserIconManager();

        if(status.sSettingTrackingProtection == 1){
            mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.DEFAULT);
        }else if(status.sSettingTrackingProtection == 2){
            mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.STRICT);
        }

        mSession.getSettings().setUseTrackingProtection(status.sStatusDoNotTrack);
        mSession.getSettings().setFullAccessibilityTree(true);
        mSession.getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE );
        mSession.getSettings().setAllowJavascript(status.sSettingJavaStatus);
        onUpdateFont();
        onReload(mNestedGeckoView, pcontext);
    }

    public void resetSession(){
        mSessionID = strings.GENERIC_EMPTY_STR;
    }

    public String getTheme(){
        if(mSessionID.equals(strings.GENERIC_EMPTY_STR)){
            return null;
        }else {
            return mSession.getTheme();
        }
    }

    public void initSession(geckoSession mSession){
        mSessionID = mSession.getSessionID();
        this.mSession = mSession;
    }

    public geckoSession getSession(){
        return mSession;
    }

    public void onUploadRequest(int resultCode,Intent data){
        mSession.onFileUploadRequest(resultCode,data);
    }
    public void setLoading(boolean status){
        mSession.setLoading(status);
    }


    public void initURL(String url) {
        mSession.initURL(url);
    }

    public void loadURL(String url, NestedGeckoView mNestedGeckoView, AppCompatActivity pcontext) {
        mSession = (geckoSession)mNestedGeckoView.getSession();
        if(mSession==null){
            return;
        }
        if(mSession.onGetInitializeFromStartup()){
            mSession.initURL(url);
            if(!url.startsWith(CONST_REPORT_URL) && (url.startsWith("https://genesishiddentechnologies.com/?pG") || url.startsWith("https://genesishiddentechnologies.com?pG") || url.endsWith("genesishiddentechnologies.com") || url.endsWith(constants.CONST_GENESIS_DOMAIN_URL_SLASHED))){
                try{
                    mSession.initURL(constants.CONST_GENESIS_DOMAIN_URL);
                    if(status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(pcontext)){
                        String mURL = constants.CONST_GENESIS_URL_CACHED + "?pData="+ dataController.getInstance().invokeReferenceWebsite(dataEnums.eReferenceWebsiteCommands.M_FETCH,null);
                        mSession.getSettings().setAllowJavascript(true);
                        mSession.initURL(mURL);
                        mSession.loadUri(mURL);
                    }else {
                        String mURL = constants.CONST_GENESIS_URL_CACHED_DARK + "?pData="+ dataController.getInstance().invokeReferenceWebsite(dataEnums.eReferenceWebsiteCommands.M_FETCH,null);
                        mSession.getSettings().setAllowJavascript(true);
                        mSession.loadUri(mURL);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else if(url.contains(constants.CONST_GENESIS_HELP_URL_SUB) || url.contains(constants.CONST_GENESIS_HELP_URL_CACHE) || url.contains(constants.CONST_GENESIS_HELP_URL_CACHE_DARK)){
                try{
                    mSession.initURL(constants.CONST_GENESIS_HELP_URL);

                    if(status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(pcontext)){
                        mSession.getSettings().setAllowJavascript(true);
                        mSession.loadUri(constants.CONST_GENESIS_HELP_URL_CACHE);
                    }else {
                        mSession.getSettings().setAllowJavascript(true);
                        mSession.loadUri(constants.CONST_GENESIS_HELP_URL_CACHE_DARK);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else {
                mSession.getSettings().setAllowJavascript(status.sSettingJavaStatus);
                mSession.loadUri(url);
            }
        }
    }

    public void onRedrawPixel(AppCompatActivity pcontext){
        mSession.onRedrawPixel();
        onLoadFavIcon(pcontext);
    }

    public boolean isLoaded(){
       return mSession.isLoaded();
    }

    public void onClearSiteData(){
        mRuntime.getStorageController().clearData(SITE_SETTINGS);
        mRuntime.getStorageController().clearData(SITE_DATA);
    }

    public void onClearSession(){
        mRuntime.getStorageController().clearData(AUTH_SESSIONS);
        mRuntime.getStorageController().clearData(PERMISSIONS);
    }

    public void onClearCache(){
        mRuntime.getStorageController().clearData(NETWORK_CACHE);
        mRuntime.getStorageController().clearData(IMAGE_CACHE);
        mRuntime.getStorageController().clearData(DOM_STORAGES);
    }

    public void onClearCookies(){
        mRuntime.getStorageController().clearData(COOKIES);
    }

    public void onBackPressed(boolean isFinishAllowed, int mTabSize, NestedGeckoView mNestedGeckoView, AppCompatActivity pcontext){
        if(mSession.canGoBack()){
            mSession.goBackSession();
            mSession.onUpdateBannerAdvert();
        }
        else if(isFinishAllowed){
            if(mSession.getRemovableFromBackPressed() && mTabSize>1){
                event.invokeObserver(null, enums.etype.M_CLOSE_TAB);
            }else {
                event.invokeObserver(null, enums.etype.back_list_empty);
            }
        }
    }

    public boolean wasPreviousErrorPage(){
        return mSession.wasPreviousErrorPage();
    }

    public boolean canGoForward(){
        return mSession.canGoForward();
    }

    public boolean isLoading(){
        return mSession.isLoading();
    }

    public Uri getUriPermission(){
        return mSession.getUriPermission();
    }

    public boolean getFullScreenStatus(){
        return mSession.getFullScreenStatus();
    }

    public void onExitFullScreen(){
        mSession.exitScreen();
    }

    public void onForwardPressed(){
        if(mSession.canGoForward()){
            mSession.goForwardSession();
        }
    }

    public void onClose(){
        mSession.onClose();
    }

    public void setRemovableFromBackPressed(boolean pStatus){
        mSession.setRemovableFromBackPressed(pStatus);
    }


    public void onStop(){
        mSession.stop();
    }

    public void onReload(NestedGeckoView mNestedGeckoView, AppCompatActivity pcontext){
        mSession.stop();
        String url = mSession.getCurrentURL();
        if(url.startsWith("https://genesishiddentechnologies.com/?pG") || url.startsWith("https://genesishiddentechnologies.com?pG") || url.endsWith("genesishiddentechnologies.com") ||  url.contains(constants.CONST_GENESIS_HELP_URL_SUB) || url.contains(constants.CONST_GENESIS_HELP_URL_CACHE) || url.contains(constants.CONST_GENESIS_HELP_URL_CACHE_DARK)){
            loadURL(mSession.getCurrentURL(), mNestedGeckoView, pcontext);
        }else{
            mSession.reload();
        }
    }

    public void onReloadStatic(NestedGeckoView mNestedGeckoView, AppCompatActivity pcontext){
        ///mSession.stop();
        loadURL(mSession.getCurrentURL(), mNestedGeckoView, pcontext);
    }

    public void manual_download(String url, AppCompatActivity context){
        Uri downloadURL = Uri.parse(url);
        File f = new File(url);
        f.getName();
        String downloadFile = f.getName();

        /*EXTERNAL STORAGE REQUEST*/
        if(helperMethod.checkPermissions(context)){
            mSession.downloadRequestedFile(downloadURL,downloadFile);
        }
    }

    public void manualDownloadWithName(String url, String file, AppCompatActivity context){
        Uri downloadURL = Uri.parse(url);
        /*EXTERNAL STORAGE REQUEST*/
        if(helperMethod.checkPermissions(context)){
            mSession.downloadRequestedFile(downloadURL,file);
        }
    }

    public void downloadFile(AppCompatActivity pcontext)
    {
        if(helperMethod.checkPermissions(pcontext)){
            mSession.downloadRequestedFile();
        }
    }

    public void downloadFile(String mURL, AppCompatActivity pcontext)
    {
        if(helperMethod.checkPermissions(pcontext)){
            mSession.downloadRequestedFile();
        }
    }

    /*Session Updates*/

    public void onUpdateFont(){
        float font = (status.sSettingFontSize -100)/3+100;
        mRuntime.getSettings().setFontSizeFactor(font/100);
    }

    public void reinitHomeTheme(){
        String mURLFinal;
        mSession.initURL(constants.CONST_GENESIS_DOMAIN_URL);
        if(status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(activityContextManager.getInstance().getHomeController())){
            String mURL = constants.CONST_GENESIS_URL_CACHED + "?pData="+ dataController.getInstance().invokeReferenceWebsite(dataEnums.eReferenceWebsiteCommands.M_FETCH,null);
            mSession.getSettings().setAllowJavascript(true);
            mSession.initURL(mURL);
            mURLFinal = mURL;
        }else {
            String mURL = constants.CONST_GENESIS_URL_CACHED_DARK + "?pData="+ dataController.getInstance().invokeReferenceWebsite(dataEnums.eReferenceWebsiteCommands.M_FETCH,null);
            mSession.getSettings().setAllowJavascript(true);
            mSession.initURL(mURL);
            mURLFinal = mURL;
        }

        if(!mSession.canGoBack()){
            activityContextManager.getInstance().getHomeController().getGeckoView().releaseSession();
            mSession.close();
            mSession.open(mRuntime);
            activityContextManager.getInstance().getHomeController().getGeckoView().setSession(mSession);
        }else {
            mSession.goBack();
        }

        new Handler().postDelayed(() ->
        {
            if(!mSession.canGoBack()){
                mSession.close();
                activityContextManager.getInstance().getHomeController().getGeckoView().releaseSession();
                mSession.open(mRuntime);
                activityContextManager.getInstance().getHomeController().getGeckoView().setSession(mSession);
            }

            mSession.loadUri(mURLFinal);

        }, 10);

    }

    public class geckoViewClientCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if(e_type.equals(enums.etype.M_CHANGE_HOME_THEME)){
                reinitHomeTheme();
            }
            else if(mSession!=null && mSession.isClosed()){
                return null;
            }else if(mSession!=null) {
                if(e_type.equals(enums.etype.SESSION_ID)){
                    return mSession.getSessionID();
                }
                else if (mSessionID!=null && mSessionID.equals(data.get(1)) || e_type.equals(enums.etype.M_RATE_COUNT) || e_type.equals(enums.etype.FINDER_RESULT_CALLBACK) || e_type.equals(enums.etype.ON_UPDATE_TAB_TITLE) || e_type.equals(enums.etype.on_update_favicon) ||e_type.equals(enums.etype.on_update_history) || e_type.equals(enums.etype.on_request_completed) || e_type.equals(enums.etype.on_update_suggestion) || e_type.equals(enums.etype.on_update_suggestion_url))
                {
                    if (e_type.equals(on_handle_external_intent))
                    {
                        try {
                            WebResponse responseInfo = (WebResponse)data.get(0);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndTypeAndNormalize(Uri.parse(responseInfo.uri), responseInfo.headers.get("Content-Type"));
                            activityContextManager.getInstance().getHomeController().startActivity(intent);
                        }catch (Exception ex){
                            Log.i("ex","ex");
                        }
                    } else
                    {
                        return event.invokeObserver(data, e_type);
                    }
                }
            }
            return null;
        }
    }
}
