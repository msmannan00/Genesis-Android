package com.hiddenservices.onionservices.appManager.homeManager.geckoManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.dataModel.geckoDataModel;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.autofillDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.contentDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.navigationDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.permissionDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.progressDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.promptDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.historyDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.mediaDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.mediaSessionDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.scrollDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.selectionDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses.downloadHandler;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses.searchHandler;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.eventObserver;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_DESKTOP;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_MOBILE;

import android.content.Intent;

public class  geckoSession extends GeckoSession implements GeckoSession.ProgressDelegate {

    private WeakReference<AppCompatActivity> mContext;
    private eventObserver.eventListener mEvent;
    private geckoDataModel mGeckoDataModel;
    private mediaSessionDelegate mMediaSessionDelegate;
    private selectionDelegate mSelectionActionDelegate;
    private historyDelegate mHistoryDelegate;
    private promptDelegate mPromptDelegate;
    private contentDelegate mContentDelegate;
    private scrollDelegate mScrollDelegate;
    private autofillDelegate mAutofillDelegate;
    private mediaDelegate mMediaDelegate;
    private navigationDelegate mNavigationDelegate;
    private progressDelegate mProgressDelegate;

    private downloadHandler mDownloadHandler;
    private searchHandler mSearchHandler;

    private permissionDelegate mPermissionDelegate;

    private boolean mIsRemovableOnBackPressed = false;

    geckoSession(eventObserver.eventListener pEvent, String pSessionID, AppCompatActivity pContext, GeckoView pGeckoView, ActivityResultLauncher<Intent> pUploadRequestLauncher) {

        this.mContext = new WeakReference<>(pContext);
        this.mEvent = pEvent;
        this.mGeckoDataModel = new geckoDataModel();
        this.mGeckoDataModel.mSessionID = pSessionID;

        this.mSelectionActionDelegate = new selectionDelegate(pContext, true);
        this.mHistoryDelegate = new historyDelegate(this.mContext, mEvent, mGeckoDataModel, this);
        this.mPromptDelegate = new promptDelegate(this.mContext.get(), pUploadRequestLauncher);
        this.mContentDelegate = new contentDelegate(this.mContext, mEvent, mGeckoDataModel, this);
        this.mScrollDelegate = new scrollDelegate(mEvent, mGeckoDataModel);
        this.mAutofillDelegate = new autofillDelegate(pGeckoView);
        this.mDownloadHandler = new downloadHandler(this.mContext, this);
        this.mSearchHandler = new searchHandler(mEvent, this);
        this.mNavigationDelegate = new navigationDelegate(this.mContext, mEvent, mGeckoDataModel, this);
        this.mProgressDelegate = new progressDelegate(this.mContext, mEvent, mGeckoDataModel);
        this.mPermissionDelegate = new permissionDelegate(this.mContext, this);
        this.mMediaDelegate = new mediaDelegate(new WeakReference<>(pContext));

        setMediaDelegate(this.mMediaDelegate);
        setSelectionActionDelegate(this.mSelectionActionDelegate);
        setHistoryDelegate(this.mHistoryDelegate);
        setPromptDelegate(this.mPromptDelegate);
        setContentDelegate(this.mContentDelegate);
        setScrollDelegate(this.mScrollDelegate);
        setAutofillDelegate(this.mAutofillDelegate);
        setNavigationDelegate(this.mNavigationDelegate);
        setProgressDelegate(this.mProgressDelegate);
        setPermissionDelegate(this.mPermissionDelegate);
        setMediaSessionDelegate(null);
    }

    public void onInit(AppCompatActivity pContext){
        setMediaSessionDelegate(null);
        this.mMediaSessionDelegate = new mediaSessionDelegate(new WeakReference<>(pContext), mGeckoDataModel, mMediaDelegate);
        setMediaSessionDelegate(this.mMediaSessionDelegate);
    }

    public void initCallback(eventObserver.eventListener pEvent) {
        this.mEvent = pEvent;
    }

    public void initURL(String url) {
        mContentDelegate.resetCrash();
        mContentDelegate.resetCrash();
        mHistoryDelegate.setURL(url);
        mGeckoDataModel.mCurrentTitle = mGeckoDataModel.mCurrentURL;
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle), homeEnums.eGeckoCallback.ON_UPDATE_SUGGESTION);
    }

    /*Delegate Handler*/

    public mediaSessionDelegate getMediaSessionDelegate(){
        return mMediaSessionDelegate;
    }

    public promptDelegate getPromptDelegate(){
        return mPromptDelegate;
    }

    public progressDelegate getProgressDelegate(){
        return mProgressDelegate;
    }

    public scrollDelegate getmScrollDelegate(){
        return mScrollDelegate;
    }

    public contentDelegate getContentDelegate(){
        return mContentDelegate;
    }

    public navigationDelegate getNavigationDelegate(){
        return mNavigationDelegate;
    }

    public downloadHandler getDownloadHandler(){
        return mDownloadHandler;
    }

    public historyDelegate getHistoryDelegate(){
        return mHistoryDelegate;
    }

    /*Helper Methods*/

    public void onClose() {
        stop();
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, this), homeEnums.eGeckoCallback.ON_DESTROY_MEDIA);
    }

    public boolean onRestoreState() {
        GeckoSession.SessionState mSessionState = this.mProgressDelegate.getSessionState();
        if (mSessionState != null) {
            restoreState(mSessionState);
            return true;
        } else {
            return false;
        }
    }

    public void setSessionState(GeckoSession.SessionState pSessionState) {
        mProgressDelegate.setSessionState(pSessionState);
    }

    public void findInPage(String pQuery, int pDirection) {
        mSearchHandler.findInPage(pQuery, pDirection);
    }

    public void onFullScreenInvoke(boolean var2) {
        mSelectionActionDelegate.setFullScreen(mGeckoDataModel.mFullScreenStatus);
        this.mSelectionActionDelegate.enableExternalActions(!var2);
    }

    public void toggleUserAgent() {
        if (getSettings().getUserAgentMode() == USER_AGENT_MODE_DESKTOP) {
            getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE);
        } else {
            getSettings().setUserAgentMode(USER_AGENT_MODE_DESKTOP);
        }
    }

    public int getUserAgentMode() {
        return getSettings().getUserAgentMode();
    }

    public void setRemovableFromBackPressed(boolean pStatus) {
        mIsRemovableOnBackPressed = pStatus;
    }

    public boolean isRemovableFromBackPressed() {
        if (mGeckoDataModel.mCurrentURL.startsWith("data") || mGeckoDataModel.mCurrentURL.startsWith("blob")) {
            return true;
        } else {
            return mIsRemovableOnBackPressed;
        }
    }

    public void exitScreen() {
        this.exitFullScreen();
    }

    public void goBackSession() {
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, this), homeEnums.eGeckoCallback.ON_DESTROY_MEDIA);
        goBack();
    }

    public void goForwardSession() {
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, this), homeEnums.eGeckoCallback.ON_DESTROY_MEDIA);
        goForward();
    }

    /*Properties Getter Setter*/
    public void resetProgress() {
        mProgressDelegate.resetProgress();
    }
    public void setTheme(String pTheme) {
        mGeckoDataModel.mTheme = pTheme;
    }

    public String getTheme() {
        return mGeckoDataModel.mTheme;
    }

    public String getSessionID() {
        return mGeckoDataModel.mSessionID;
    }

    public void setSessionID(String pSessionID) {
        mGeckoDataModel.mSessionID = pSessionID;
    }

    public String getCurrentURL() {
        return mGeckoDataModel.mCurrentURL;
    }

    public String getTitle() {
        return mGeckoDataModel.mCurrentTitle;
    }

    public void setTitle(String title) {
        mGeckoDataModel.mCurrentTitle = title;
    }

    public int getProgress() {
        return mProgressDelegate.getProgress();
    }

    public boolean getSSL() {
        return mProgressDelegate.getSecurityState();
    }

}
