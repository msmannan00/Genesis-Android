package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel;


import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManagerCallbacks.M_RATE_APPLICATION;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.dataModel.geckoDataModel;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses.intentHandler;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import org.mozilla.geckoview.GeckoSession;
import java.lang.ref.WeakReference;
import java.util.Arrays;

public class progressDelegate implements GeckoSession.ProgressDelegate {

    /*Private Variables*/

    private WeakReference<AppCompatActivity> mContext;
    private eventObserver.eventListener mEvent;
    private geckoDataModel mGeckoDataModel;
    private SecurityInformation securityInfo = null;
    private GeckoSession.SessionState mSessionState;
    private int mProgress = 5;

    /*Initializations*/

    public progressDelegate(WeakReference<AppCompatActivity> pContext, eventObserver.eventListener pEvent, geckoDataModel pGeckoDataModel) {
        this.mContext = pContext;
        this.mEvent = pEvent;
        this.mGeckoDataModel = pGeckoDataModel;

    }

    @UiThread
    public void onSecurityChange(@NonNull final GeckoSession session, @NonNull final SecurityInformation securityInfo) {
        this.securityInfo = securityInfo;
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, securityInfo.isSecure, this), homeEnums.eGeckoCallback.M_UPDATE_SSL);
    }

    @Override
    public void onProgressChange(@NonNull GeckoSession session, int progress) {
        if (!mGeckoDataModel.mFullScreenStatus) {
            mProgress = progress;

            if (progress <= 20) {
                mContext.get().runOnUiThread(() -> mEvent.invokeObserver(Arrays.asList(5, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.PROGRESS_UPDATE));
            } else {
                if (progress == 100) {
                    mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, this), homeEnums.eGeckoCallback.ON_INVOKE_PARSER);
                    if (!mGeckoDataModel.mCurrentURL.startsWith(constants.CONST_GENESIS_DOMAIN_URL) && !mGeckoDataModel.mCurrentURL.contains("genesis")) {
                        checkApplicationRate();
                    }
                }
                mContext.get().runOnUiThread(() -> mEvent.invokeObserver(Arrays.asList(mProgress, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.PROGRESS_UPDATE));
            }
        }
    }

    @UiThread
    public void onSessionStateChange(@NonNull GeckoSession session, @NonNull GeckoSession.SessionState sessionState) {
        mSessionState = sessionState;
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, sessionState.toString()), homeEnums.eGeckoCallback.M_UPDATE_SESSION_STATE);
    }

    @Override
    public void onPageStart(@NonNull GeckoSession var1, @NonNull String var2) {
        if(var2.startsWith("tel:") || var2.startsWith("intent")){
            intentHandler.actionIntent(var2, mContext);
            return;
        }
        securityInfo = null;
        mGeckoDataModel.mTheme = null;
        mProgress = 5;
        mEvent.invokeObserver(Arrays.asList(5, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.PROGRESS_UPDATE);

        if(var2.startsWith("jar:file")){
            return;
        }
        if(!var2.contains("about:blank")){
            mEvent.invokeObserver(Arrays.asList(var2, mGeckoDataModel.mSessionID, var2, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, null), homeEnums.eGeckoCallback.ON_UPDATE_SEARCH_BAR);
        }

        if (!var2.startsWith("intent")) {
            if (!mGeckoDataModel.mCurrentURL.equals("about:config") && !var2.equals("about:blank") && !var2.startsWith("jar:file")) {
                if(helperMethod.getHost(var2).endsWith(".onion")){
                    var2 = var2.replace("www.", "");
                }
                this.mGeckoDataModel.mCurrentURL = var2;
            }

            if(mGeckoDataModel.mCurrentURL.replace("http","https://").equals(var2)){
                this.mGeckoDataModel.mCurrentURL = var2;
            }

            if (!mGeckoDataModel.mCurrentURL.equals("about:config") && !mGeckoDataModel.mCurrentURL.equals("about:blank") && !var2.equals("about:blank")) {
                mContext.get().runOnUiThread(() -> mEvent.invokeObserver(Arrays.asList(5, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.PROGRESS_UPDATE));
            }
            if (!mGeckoDataModel.mCurrentURL.equals("about:config") && !mGeckoDataModel.mCurrentURL.equals("about:blank") && !mGeckoDataModel.mCurrentTitle.equals("loading")) {
                mProgress = 5;
                mContext.get().runOnUiThread(() -> mEvent.invokeObserver(Arrays.asList(5, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.PROGRESS_UPDATE));
                if(!var2.equals("about:blank")){
                    mEvent.invokeObserver(Arrays.asList(var2, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.SEARCH_UPDATE);
                }
            }
        }
    }

    @UiThread
    public void onPageStop(@NonNull GeckoSession var1, boolean var2) {
        if (var2) {
            if (mProgress >= 100) {
                mEvent.invokeObserver(Arrays.asList(null, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.ON_PAGE_LOADED);
            }
        }
    }

    /*Local Listeners*/
    public GeckoSession.SessionState getSessionState() {
        return mSessionState;
    }

    public String getSecurityInfo() {
        return intentHandler.getSecurityInfo(this.securityInfo);
    }

    private void checkApplicationRate() {
        if (status.sSettingIsAppStarted) {
            if (status.sGlobalURLCount == 10) {
                mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), M_RATE_APPLICATION);
            } else if ( status.sGlobalURLCount == 20 || status.sGlobalURLCount == 80) {
                mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.M_DEFAULT_BROWSER);
            }

            status.sGlobalURLCount += 1;
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.M_RATE_COUNT);
        }
    }

    public void setSessionState(GeckoSession.SessionState pSessionState) {
        mSessionState = pSessionState;
    }

    public int getProgress(){
        return mProgress;
    }

    public void resetProgress() {
        mProgress = 5;
    }

    public boolean getSecurityState(){
        if(mGeckoDataModel.mCurrentURL.equals("about:blank") || mGeckoDataModel.mCurrentURL.startsWith("resource://android/assets/policy")){
            return true;
        }
        if(securityInfo==null){
            return false;
        }else {
            return securityInfo.isSecure;
        }
    }

}