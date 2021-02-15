package com.darkweb.genesissearchengine.appManager.homeManager.geckoManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.kotlinHelperLibraries.BrowserIconManager;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

import java.io.File;
import java.util.List;
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
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class geckoClients
{
    /*Gecko Variables*/

    private geckoSession mSession = null;
    private GeckoRuntime mRuntime = null;
    private BrowserIconManager mIconManager;
    private eventObserver.eventListener event;
    private AppCompatActivity context;

    /*Local Variable*/

    private String mSessionID;

    public void initialize(GeckoView geckoView, eventObserver.eventListener event, AppCompatActivity context, boolean isForced)
    {
        this.context = context;
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

    public void onValidateInitializeFromStartup(){
        mSession.onValidateInitializeFromStartup();
    }

    public boolean onGetInitializeFromStartup(){
        return mSession.onGetInitializeFromStartup();
    }

    public geckoSession initFreeSession(GeckoView pGeckoView, AppCompatActivity pcontext, eventObserver.eventListener event){
        this.event = event;
        initRuntimeSettings(pcontext);
        geckoSession mTempSession = new geckoSession(new geckoViewClientCallback(),mSessionID,context, pGeckoView);
        mTempSession.open(mRuntime);
        mTempSession.getSettings().setUseTrackingProtection(status.sStatusDoNotTrack);
        mTempSession.getSettings().setFullAccessibilityTree(true);
        mTempSession.getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE);
        mTempSession.getSettings().setAllowJavascript(status.sSettingJavaStatus);
        return mTempSession;
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
            mRuntime = GeckoRuntime.getDefault(context);
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

    public void onGetFavIcon(ImageView pImageView, String pURL){
        pURL = helperMethod.completeURL(helperMethod.getDomainName(pURL));
        mIconManager.onLoadIconIntoView(context,mRuntime, pImageView, pURL);
    }

    public void onLoadFavIcon(){
        BrowserIconManager mIconManager = new BrowserIconManager();
        mIconManager.onLoadIcon(context, mRuntime);
    }

    private int getCookiesBehaviour(){
        return status.sSettingCookieStatus;
    }

    public void updateSetting(){
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
        onReload();
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

    public void loadURL(String url) {
        if(mSession.onGetInitializeFromStartup()){
            mSession.initURL(url);
            if(url.startsWith("https://boogle.store?pG") || url.endsWith("boogle.store") || url.endsWith(constants.CONST_GENESIS_DOMAIN_URL_SLASHED)){
                try{
                    mSession.initURL(constants.CONST_GENESIS_DOMAIN_URL);
                    mSession.loadUri(constants.CONST_GENESIS_URL_CACHED);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else if(url.contains(constants.CONST_GENESIS_HELP_URL_SUB)){
                try{
                    mSession.initURL(constants.CONST_GENESIS_HELP_URL);
                    mSession.loadUri(constants.CONST_GENESIS_HELP_URL_CACHE);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else {
                mSession.loadUri(url);
            }
        }
    }

    public void onRedrawPixel(){
        mSession.onRedrawPixel();
        onLoadFavIcon();
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

    public void onBackPressed(boolean isFinishAllowed){
        mSession.goBackSession();
        if(mSession.canGoBack()){
            mSession.goBackSession();
        }
        else if(isFinishAllowed){
            event.invokeObserver(null, enums.etype.back_list_empty);
        }
    }

    public boolean canGoBack(){
        return mSession.canGoBack();
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


    public void onStop(){
        mSession.stop();
    }

    public void onReload(){
        mSession.stop();
        loadURL(mSession.getCurrentURL());
    }

    public void onReloadStatic(){
        mSession.loadUri(mSession.getCurrentURL());
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

    public void downloadFile()
    {
        if(helperMethod.checkPermissions(context)){
            mSession.downloadRequestedFile();
        }
    }

    /*Session Updates*/

    public void onUpdateFont(){
        float font = (status.sSettingFontSize -100)/3+100;
        mRuntime.getSettings().setFontSizeFactor(font/100);
    }


    public class geckoViewClientCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if (mSessionID!=null && mSessionID.equals(data.get(1)) || e_type.equals(enums.etype.FINDER_RESULT_CALLBACK) || e_type.equals(enums.etype.ON_UPDATE_TAB_TITLE) || e_type.equals(enums.etype.on_update_favicon) ||e_type.equals(enums.etype.on_update_history) || e_type.equals(enums.etype.on_request_completed) || e_type.equals(enums.etype.on_update_suggestion) || e_type.equals(enums.etype.on_update_suggestion_url))
            {
                if (e_type.equals(on_handle_external_intent))
                {
                    GeckoSession.WebResponseInfo responseInfo = (GeckoSession.WebResponseInfo)data.get(0);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndTypeAndNormalize(Uri.parse(responseInfo.uri), responseInfo.contentType);
                    context.startActivity(intent);
                } else
                {
                    return event.invokeObserver(data, e_type);
                }
            }
            return null;
        }
    }
}
