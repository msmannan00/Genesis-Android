package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel;


import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_LONG_PRESS_URL;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_LONG_PRESS_WITH_LINK;
import android.content.ActivityNotFoundException;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.dataModel.geckoDataModel;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.downloadManager.geckoDownloadManager;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.geckoSession;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses.permissionHandler;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import org.json.JSONObject;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.WebResponse;
import java.lang.ref.WeakReference;
import java.util.Arrays;

public class contentDelegate implements GeckoSession.ContentDelegate {

    /*Private Variables*/

    private WeakReference<AppCompatActivity> mContext;
    private eventObserver.eventListener mEvent;
    private geckoDataModel mGeckoDataModel;
    private geckoSession mGeckoSession;

    private geckoDownloadManager mDownloadManager;
    private boolean mClosed = false;
    private int mCrashCount = 0;

    /*Initializations*/

    public contentDelegate(WeakReference<AppCompatActivity> pContext, eventObserver.eventListener pEvent, geckoDataModel pGeckoDataModel, geckoSession pGeckoSession) {
        this.mContext = pContext;
        this.mEvent = pEvent;
        this.mGeckoDataModel = pGeckoDataModel;
        this.mGeckoSession = pGeckoSession;

        mDownloadManager = new geckoDownloadManager();
    }

    /*Local Listeners*/

    @UiThread
    public void onTitleChange(@NonNull GeckoSession var1, @Nullable String var2) {
        if (var2 != null && !var2.equals(strings.GENERIC_EMPTY_STR) && var2.length() > 2 && !var2.equals("about:blank")) {
            mGeckoDataModel.mCurrentTitle = var2;
        }
    }

    @Override
    public void onFullScreen(@NonNull GeckoSession var1, boolean var2) {
        mGeckoDataModel.mFullScreenStatus = var2;
        mEvent.invokeObserver(Arrays.asList(var2, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.ON_FULL_SCREEN);
    }

    @UiThread
    @Override
    public void onContextMenu(@NonNull GeckoSession var1, int var2, int var3, @NonNull GeckoSession.ContentDelegate.ContextElement var4) {

        String title = strings.GENERIC_EMPTY_STR;
        if (var4.title != null) {
            title = var4.title;
        }
        if (var4.type != 0 && var4.srcUri != null) {
            if (var4.linkUri != null) {
                mEvent.invokeObserver(Arrays.asList(var4.linkUri, mGeckoDataModel.mSessionID, var4.srcUri, title, mGeckoDataModel.mTheme, var4.altText, mGeckoSession, mContext.get()), M_LONG_PRESS_WITH_LINK);
            } else {
                try {
                    String mTitle = var4.title;
                    if (mTitle == null || mTitle.isEmpty()) {
                        mTitle = helperMethod.getDomainName(mGeckoDataModel.mCurrentURL) + "\n" + var4.srcUri;
                    }
                    mEvent.invokeObserver(Arrays.asList(var4.srcUri, mGeckoDataModel.mSessionID, mTitle, mGeckoDataModel.mTheme, mGeckoSession, mContext.get()), homeEnums.eGeckoCallback.ON_LONG_PRESS);
                } catch (Exception ignored) {
                }
            }
        } else if (var4.linkUri != null) {
            mEvent.invokeObserver(Arrays.asList(var4.linkUri, mGeckoDataModel.mSessionID, title, mGeckoDataModel.mTheme, mGeckoSession, mContext.get()), M_LONG_PRESS_URL);
        }
    }

    @UiThread
    @Override
    public void onExternalResponse(@NonNull GeckoSession session, @NonNull WebResponse response) {
        permissionHandler.getInstance().checkPermission(() -> {
            try {
                if (response.headers.containsKey("Content-Disposition")) {
                    mDownloadManager.downloadFile(response, mGeckoSession, mContext.get(), mEvent);
                } else if (response.headers.containsKey("Content-Type")) {
                    mDownloadManager.downloadFile(response, mGeckoSession, mContext.get(), mEvent);
                }
            } catch (ActivityNotFoundException e) {
                mEvent.invokeObserver(Arrays.asList(response, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.ON_HANDLE_EXTERNAL_INTENT);
                mGeckoSession.stop();
            }
            return null;
        });
    }

    @UiThread
    public void onFirstContentfulPaint(@NonNull GeckoSession var1) {
        if (!mGeckoDataModel.mCurrentURL.equals("about:blank")) {
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.ON_UPDATE_THEME);
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.ON_LOAD_REQUEST);
        }

        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.ON_EXPAND_TOP_BAR);
    }

    @UiThread
    public void onWebAppManifest(@NonNull GeckoSession var1, @NonNull JSONObject var2) {
        try {
            mGeckoDataModel.mTheme = var2.getString("theme_color");
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.ON_UPDATE_THEME);
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL,mGeckoDataModel.mSessionID,mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.M_INDEX_WEBSITE);
        } catch (Exception ignored) {
        }
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.ON_UPDATE_TAB_TITLE);
    }

    @UiThread
    public void onCrash(@NonNull GeckoSession session) {
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, mGeckoSession), homeEnums.eGeckoCallback.ON_DESTROY_MEDIA);

        if (!mClosed && status.sSettingIsAppStarted) {
            if (mEvent == null) {
                return;
            }
            Object mSessionObject = mEvent.invokeObserver(null, homeEnums.eGeckoCallback.SESSION_ID);
            if (mSessionObject == null || mGeckoDataModel.mSessionID == null) {
                return;
            }
            String mSessionID = (String) mSessionObject;
            if (mSessionID.equals(mGeckoDataModel.mSessionID)) {
                if (mCrashCount <= 5) {
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        try {
                            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, this), homeEnums.eGeckoCallback.M_OPEN_SESSION);
                        } catch (Exception ignored) {
                        }
                    }, mCrashCount * 500L);
                }
                mCrashCount += 1;
            }
        }
    }

    @UiThread
    public void onKill(@NonNull GeckoSession session) {
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, mGeckoSession), homeEnums.eGeckoCallback.ON_DESTROY_MEDIA);

        if (!mClosed && status.sSettingIsAppStarted) {
            if (mEvent == null) {
                return;
            }
            Object mSessionObject = mEvent.invokeObserver(null, homeEnums.eGeckoCallback.SESSION_ID);
            if (mSessionObject == null || mGeckoDataModel.mSessionID == null) {
                return;
            }
            String mSessionID = (String) mSessionObject;


            if (mSessionID.equals(mGeckoDataModel.mSessionID)) {
                if (mCrashCount <= 5) {
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        if (status.sSettingIsAppStarted && !session.isOpen()) {
                            try {
                                mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, this), homeEnums.eGeckoCallback.M_OPEN_SESSION);
                            } catch (Exception ignored) {
                            }
                        }
                    }, mCrashCount * 500L);
                }
                mCrashCount += 1;
            }
        }
    }

    @UiThread
    public void onCloseRequest(@NonNull final GeckoSession session) {
        mClosed = true;
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, this), homeEnums.eGeckoCallback.ON_DESTROY_MEDIA);
        mGeckoDataModel.mSessionID = "-1";
    }

    public boolean isClosed() {
        return mClosed;
    }

    public boolean getFullScreenStatus() {
        return !mGeckoDataModel.mFullScreenStatus;
    }

    public void resetCrash() {
        mCrashCount = 0;
    }

    public geckoDownloadManager getDownloadManager(){
        return this.mDownloadManager;
    }


}