package com.darkweb.genesissearchengine.appManager.homeManager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import java.io.File;
import java.util.List;
import static com.darkweb.genesissearchengine.constants.enums.etype.on_handle_external_intent;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_DESKTOP;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_MOBILE;
import static org.mozilla.geckoview.StorageController.ClearFlags.ALL;

import org.mozilla.geckoview.ContentBlocking;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoSessionSettings;
import org.mozilla.geckoview.GeckoView;


class geckoClients
{
    /*Gecko Variables*/

    private geckoSession mSession = null;
    private GeckoRuntime mRuntime = null;
    private int mSessionID=0;
    private int mGlobalSessionCounter=0;

    private eventObserver.eventListener event;
    private AppCompatActivity context;

    void initialize(GeckoView geckoView, eventObserver.eventListener event, AppCompatActivity context, boolean isForced)
    {
        this.context = context;
        this.event = event;
        mGlobalSessionCounter+=1;
        mSessionID = mGlobalSessionCounter;

        runtimeSettings(context);

        if(!isForced && geckoView.getSession()!=null && geckoView.getSession().isOpen()){
            mSession = (geckoSession) geckoView.getSession();
        }
        else {
            geckoView.releaseSession();
            Log.i("GCHECKS:","GCHECKS:"+mGlobalSessionCounter);
            mSession = new geckoSession(new geckoViewClientCallback(),mGlobalSessionCounter,context);
            mSession.open(mRuntime);
            mSession.getSettings().setUseTrackingProtection(true);
            mSession.getSettings().setFullAccessibilityTree(true);
            mSession.getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE );
            mSession.getSettings().setAllowJavascript(status.sJavaStatus);
            geckoView.releaseSession();
            geckoView.setSession(mSession);

        }
        onUpdateFont();
    }

    void toogleUserAgent(){
        mSession.toogleUserAgent();
    }

    int getUserAgent(){
        return mSession.getUserAgentMode();
    }

    private void runtimeSettings(AppCompatActivity context){
        if(mRuntime==null){
            mRuntime = GeckoRuntime.getDefault(context);
            mRuntime.getSettings().getContentBlocking().setCookieBehavior(getCookiesBehaviour());
            mRuntime.getSettings().setAutomaticFontSizeAdjustment(status.sFontAdjustable);
            mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.AD);
            mRuntime.getSettings().getContentBlocking().setAntiTracking(ContentBlocking.AntiTracking.FINGERPRINTING);
        }
    }

    private int getCookiesBehaviour(){
        return status.sCookieStatus;
    }

    void updateCookies(){
        mRuntime.getSettings().getContentBlocking().setCookieBehavior(status.sCookieStatus);
        onReload();
    }

    void onClose(){
    }

    void initSession(geckoSession mSession){
        mSessionID = mSession.getSessionID();
        this.mSession = mSession;
    }

    geckoSession getSession(){
        return mSession;
    }

    void updateJavascript(){
        mSession.getSettings().setAllowJavascript(status.sJavaStatus);
        if(status.sJavaStatus){
            mSession.reload();
        }
    }

    void onUploadRequest(int resultCode,Intent data){
        mSession.onFileUploadRequest(resultCode,data);
    }

    void setLoading(boolean status){
        mSession.setLoading(status);
    }

    void loadURL(String url){
        mSession.initURL(url);
        mSession.loadUri(url);
    }

    void onClearSession(){
        mRuntime.getStorageController().clearData(ALL);
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

    boolean canGoForward(){
        return mSession.canGoForward();
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

    void manual_download(String url, AppCompatActivity context){
        Uri downloadURL = Uri.parse(url);
        File f = new File(url);
        String downloadFile = f.getName() != null ? f.getName() : downloadURL.getLastPathSegment();

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

    void onUpdateSettings(){
        mSession.reload();
    }

    void onUpdateFont(){
        float font = (status.sFontSize -100)/4+100;
        mRuntime.getSettings().setAutomaticFontSizeAdjustment(status.sFontAdjustable);
        if(!mRuntime.getSettings().getAutomaticFontSizeAdjustment()){
            mRuntime.getSettings().setFontSizeFactor(font/100);
        }
    }


    public class geckoViewClientCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(List<Object> data, enums.etype e_type)
        {
            if (mSessionID == (int)data.get(1) || e_type.equals(enums.etype.on_request_completed) || e_type.equals(enums.etype.on_update_suggestion) || e_type.equals(enums.etype.on_update_suggestion_url))
            {
                if (e_type.equals(on_handle_external_intent))
                {
                    GeckoSession.WebResponseInfo responseInfo = (GeckoSession.WebResponseInfo)data.get(0);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndTypeAndNormalize(Uri.parse(responseInfo.uri), responseInfo.contentType);
                    context.startActivity(intent);
                } else
                {
                    event.invokeObserver(data, e_type);
                }
            }
        }
    }
}
