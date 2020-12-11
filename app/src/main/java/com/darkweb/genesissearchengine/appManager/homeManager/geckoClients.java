package com.darkweb.genesissearchengine.appManager.homeManager;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
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

class geckoClients
{
    /*Gecko Variables*/

    private geckoSession mSession = null;
    private GeckoRuntime mRuntime = null;
    private String mSessionID;

    private eventObserver.eventListener event;
    private AppCompatActivity context;

    void initialize(GeckoView geckoView, eventObserver.eventListener event, AppCompatActivity context, boolean isForced)
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

    void toogleUserAgent(){
        mSession.toogleUserAgent();
    }

    int getUserAgent(){
        return mSession.getUserAgentMode();
    }

    public void initRuntimeSettings(AppCompatActivity context){
        if(mRuntime==null){
            mRuntime = GeckoRuntime.getDefault(context);
            mRuntime.getSettings().setAboutConfigEnabled(true);
            mRuntime.getSettings().setWebFontsEnabled(status.sShowWebFonts);
            mRuntime.getSettings().setRemoteDebuggingEnabled(false);
            mRuntime.getSettings().getContentBlocking().setCookieBehavior(getCookiesBehaviour());
            mRuntime.getSettings().getContentBlocking().setSafeBrowsing(ContentBlocking.SafeBrowsing.DEFAULT);
            mRuntime.getSettings().setAutomaticFontSizeAdjustment(status.sSettingFontAdjustable);
            if(status.sSettingTrackingProtection){
                mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.AD);
                mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.FINGERPRINTING);
            }else {
                mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.STRICT);
            }
        }
    }

    private int getCookiesBehaviour(){
        return status.sSettingCookieStatus;
    }

    void updateSetting(){
        mRuntime.getSettings().setRemoteDebuggingEnabled(false);
        mRuntime.getSettings().setWebFontsEnabled(status.sShowWebFonts);
        mRuntime.getSettings().getContentBlocking().setCookieBehavior(getCookiesBehaviour());
        mRuntime.getSettings().setAutomaticFontSizeAdjustment(status.sSettingFontAdjustable);
        mRuntime.getSettings().getContentBlocking().setSafeBrowsing(ContentBlocking.SafeBrowsing.DEFAULT);
        if(status.sSettingTrackingProtection){
            mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.AD);
            mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.FINGERPRINTING);
        }else {
            mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.STRICT);
        }


        mSession.getSettings().setUseTrackingProtection(status.sStatusDoNotTrack);
        mSession.getSettings().setFullAccessibilityTree(true);
        mSession.getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE );
        mSession.getSettings().setAllowJavascript(status.sSettingJavaStatus);
        onReload();
    }

    void initSession(geckoSession mSession){
        mSessionID = mSession.getSessionID();
        this.mSession = mSession;
    }

    geckoSession getSession(){
        return mSession;
    }

    void onUploadRequest(int resultCode,Intent data){
        mSession.onFileUploadRequest(resultCode,data);
    }

    void setLoading(boolean status){
        mSession.setLoading(status);
    }

    void loadURL(String url){
        if(mSession.onGetInitializeFromStartup()){
            mSession.initURL(url);
            mSession.loadUri(url);
        }
    }

    public void onRedrawPixel(){
        mSession.onRedrawPixel();
    }

    void onClearSiteData(){
        mRuntime.getStorageController().clearData(SITE_SETTINGS);
        mRuntime.getStorageController().clearData(SITE_DATA);
    }

    void onClearSession(){
        mRuntime.getStorageController().clearData(AUTH_SESSIONS);
        mRuntime.getStorageController().clearData(PERMISSIONS);
    }

    void onClearCache(){
        mRuntime.getStorageController().clearData(NETWORK_CACHE);
        mRuntime.getStorageController().clearData(IMAGE_CACHE);
        mRuntime.getStorageController().clearData(DOM_STORAGES);
    }

    void onClearCookies(){
        mRuntime.getStorageController().clearData(COOKIES);
    }

    void onBackPressed(boolean isFinishAllowed){
        if(mSession.canGoBack()){
            mSession.goBackSession();
        }
        else if(isFinishAllowed){
            event.invokeObserver(null, enums.etype.back_list_empty);
        }
    }

    boolean canGoBack(){
        return mSession.canGoBack();
    }

    boolean isLoading(){
        return mSession.isLoading();
    }

    Uri getUriPermission(){
        return mSession.getUriPermission();
    }

    boolean getFullScreenStatus(){
        return mSession.getFullScreenStatus();
    }

    void onExitFullScreen(){
        mSession.exitScreen();
    }

    void onForwardPressed(){
        if(mSession.canGoForward()){
            mSession.goForwardSession();
        }
    }


    void onStop(){
        mSession.stop();
    }

    void onReload(){
        mSession.reload();
    }

    void onReloadStatic(){
        mSession.loadUri(mSession.getCurrentURL());
    }

    void manual_download(String url, AppCompatActivity context){
        Uri downloadURL = Uri.parse(url);
        File f = new File(url);
        f.getName();
        String downloadFile = f.getName();

        /*EXTERNAL STORAGE REQUEST*/
        if(helperMethod.checkPermissions(context)){
            mSession.downloadRequestedFile(downloadURL,downloadFile);
        }
    }

    void downloadFile()
    {
        if(helperMethod.checkPermissions(context)){
            mSession.downloadRequestedFile();
        }
    }

    /*Session Updates*/

    void onUpdateFont(){
        float font = (status.sSettingFontSize -100)/3+100;
        mRuntime.getSettings().setAutomaticFontSizeAdjustment(status.sSettingFontAdjustable);
        if(!mRuntime.getSettings().getAutomaticFontSizeAdjustment()){
            mRuntime.getSettings().setFontSizeFactor(font/100);
        }
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
