package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel;


import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_BADCERT_CACHED;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_BADCERT_CACHED_DARK;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_ERROR_CACHED;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_ERROR_CACHED_DARK;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_URL_CACHED;
import static com.hiddenservices.onionservices.constants.constants.CONST_GENESIS_URL_CACHED_DARK;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.dataModel.geckoDataModel;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.geckoSession;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses.errorHandler;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses.preferencesHandler;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.libs.trueTime.trueTimeEncryption;
import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.WebRequestError;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;

public class navigationDelegate implements GeckoSession.NavigationDelegate {

    /*Private Variables*/

    private WeakReference<AppCompatActivity> mContext;
    private eventObserver.eventListener mEvent;
    private geckoDataModel mGeckoDataModel;
    private geckoSession mGeckoSession;

    private boolean mCanGoBack = false;
    private boolean mCanGoForward = false;

    /*Initializations*/

    public navigationDelegate(WeakReference<AppCompatActivity> pContext, eventObserver.eventListener pEvent, geckoDataModel pGeckoDataModel, geckoSession pGeckoSession) {
        this.mContext = pContext;
        this.mEvent = pEvent;
        this.mGeckoDataModel = pGeckoDataModel;
        this.mGeckoSession = pGeckoSession;
    }

    @Override
    public void onCanGoBack(@NonNull GeckoSession session, boolean var2) {
        mCanGoBack = var2;
    }

    @Override
    public void onCanGoForward(@NonNull GeckoSession session, boolean var2) {
        mCanGoForward = var2;
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
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, mGeckoSession), homeEnums.eGeckoCallback.ON_DESTROY_MEDIA);
        if (var1.uri.contains("167.86.99.31")) {
            new preferencesHandler<>("network.proxy.type", 0).add();
        }else {
            new preferencesHandler<>("network.proxy.type", 1).add();
        }

        String m_url = var1.uri;
        if(m_url.startsWith("tel:")){
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        if (helperMethod.getHost(m_url).endsWith(".onion")) {
            m_url = m_url.replace("www.", "");
        }

        if (m_url.endsWith("genesisconfigurenewidentity.com/")) {
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.M_NEW_IDENTITY_MESSAGED);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        String mNormalizeURL = helperMethod.normalize(m_url);
        if (mNormalizeURL != null && mNormalizeURL.endsWith("167.86.99.31")) {
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, false), homeEnums.eGeckoCallback.M_LOAD_HOMEPAGE_GENESIS);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        if (m_url.startsWith("mailto")) {
            mEvent.invokeObserver(Arrays.asList(m_url, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.M_ON_MAIL);
            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        } else if (m_url.contains("167.86.99.31/advert__")) {
            mEvent.invokeObserver(Arrays.asList(m_url, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.ON_PLAYSTORE_LOAD);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        } else if (m_url.equals(constants.CONST_GENESIS_DOMAIN_URL_SLASHED) || m_url.startsWith("http://167.86.99.31/?")) {
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, false), homeEnums.eGeckoCallback.M_LOAD_HOMEPAGE_GENESIS);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        } else if (var1.target == 2) {
            mEvent.invokeObserver(Arrays.asList(m_url, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.OPEN_NEW_TAB);
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.ON_EXPAND_TOP_BAR);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        } else if (!m_url.equals("about:blank")) {
            if (mGeckoDataModel.mCurrentURL.startsWith(CONST_GENESIS_URL_CACHED) || mGeckoDataModel.mCurrentURL.startsWith(CONST_GENESIS_URL_CACHED_DARK)) {
                this.mGeckoDataModel.mCurrentURL = constants.CONST_GENESIS_DOMAIN_URL;
            } else if (mGeckoDataModel.mCurrentURL.equals(constants.CONST_GENESIS_HELP_URL_CACHE)) {
                if (status.sTheme == enums.Theme.THEME_LIGHT || helperMethod.isDayMode(mContext.get())) {
                    this.mGeckoDataModel.mCurrentURL = constants.CONST_GENESIS_HELP_URL;
                } else {
                    this.mGeckoDataModel.mCurrentURL = constants.CONST_GENESIS_HELP_URL_CACHE_DARK;
                }
            } else if (!m_url.startsWith("resource://android/assets/homepage/")) {
                this.mGeckoDataModel.mCurrentURL = m_url;
            }

            mEvent.invokeObserver(Arrays.asList(m_url, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.START_PROXY);

            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        } else {
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
    }

    public GeckoResult<String> onLoadError(@NonNull GeckoSession var1, @Nullable String var2, @NonNull WebRequestError var3) {

        try {
            if(var2==null){
                var2 = this.mGeckoDataModel.mCurrentURL;
            }
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, mGeckoSession), homeEnums.eGeckoCallback.ON_DESTROY_MEDIA);
            if (helperMethod.getHost(var2).endsWith(".onion")) {
                var2 = var2.replace("www.", "");
            }

            if (var2.startsWith("https://trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd")) {
                var2 = var2.replace("https", "http");
                this.mGeckoDataModel.mCurrentURL = var2;
            }
            if (mGeckoDataModel.mCurrentURL.contains("orion.onion")) {
                mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.M_NEW_IDENTITY);
            }
            if (status.sSettingIsAppStarted && orbotLocalConstants.mIsTorInitialized) {
                errorHandler handler = new errorHandler();
                mEvent.invokeObserver(Arrays.asList(var2, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.ON_LOAD_ERROR);
                //mGeckoDataModel.mTheme = null;
                mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.ON_UPDATE_THEME);

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
                }

                return GeckoResult.fromValue("data:text/html," + handler.createErrorPage(var3.category, var3.code, mContext.get(), var2, mResourceURL));
            } else {
                mEvent.invokeObserver(Arrays.asList(var2, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.M_ORBOT_LOADING);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean canGoBack() {
        return mCanGoBack;
    }

    public boolean canGoForward() {
        return mCanGoForward;
    }

}