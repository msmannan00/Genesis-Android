package com.hiddenservices.onionservices.pluginManager.downloadPluginManager;

import android.app.NotificationManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import com.hiddenservices.onionservices.pluginManager.pluginReciever.downloadNotificationReciever;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.webrtc.ContextUtils.getApplicationContext;

public class downloadManager {
    /* Private Variables */

    private WeakReference<AppCompatActivity> mAppContext;
    private Map<Integer, downloadReciever> mDownloads = new HashMap<>();
    private eventObserver.eventListener mEvent;

    /* Initializations */

    public downloadManager(WeakReference<AppCompatActivity> pAppContext, eventObserver.eventListener pEvent) {
        this.mAppContext = pAppContext;
        this.mEvent = pEvent;
    }

    private void onStartDownload(String pPath, String pFile) {
        int mNotificationID = helperMethod.createUniqueNotificationID();
        downloadReciever mFileDownloader = (downloadReciever) new downloadReciever(mAppContext.get(), pPath, pFile, mNotificationID, mEvent, downloadNotificationReciever.class).execute(pPath);
        mDownloads.put(mNotificationID, mFileDownloader);
    }

    private void onCancelDownload(int pID) {
        if (mDownloads != null && mDownloads.containsKey(pID)) {
            downloadReciever mReciever = mDownloads.get(pID);
            if (mReciever != null) {
                mDownloads.get(pID).onCancel();
            } else {
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
            }
        }
    }

    private void onDownloadURLRequest(int pID) {
        if (mDownloads != null && mDownloads.get(pID) != null) {
            mDownloads.get(pID).onTrigger();
        }
    }

    private void onDownloadWebRequest(String mURL, String mPath) {
        mAppContext.get().startService(downloadService.getDownloadService(mAppContext.get(), mURL, mPath));
    }

    private String onDownloadBlobFile(String pURL) {
        return blobDownloader.getBase64StringFromBlobUrl(pURL);
    }

    /* External Triggers */

    public Object onTrigger(List<Object> pData, pluginEnums.eDownloadManager pEventType) {
        if (pEventType.equals(pluginEnums.eDownloadManager.M_START_DOWNLOAD)) {
            onStartDownload((String) pData.get(0), (String) pData.get(1));
        } else if (pEventType.equals(pluginEnums.eDownloadManager.M_CANCEL_DOWNLOAD)) {
            onCancelDownload((int) pData.get(0));
        } else if (pEventType.equals(pluginEnums.eDownloadManager.M_URL_DOWNLOAD_REQUEST)) {
            onDownloadURLRequest((int) pData.get(0));
        } else if (pEventType.equals(pluginEnums.eDownloadManager.M_WEB_DOWNLOAD_REQUEST)) {
            onDownloadWebRequest(pData.get(0).toString(), (String) pData.get(1));
        } else if (pEventType.equals(pluginEnums.eDownloadManager.M_BLOB_DOWNLOAD_REQUEST)) {
            return onDownloadBlobFile((String) pData.get(0));
        }

        return null;
    }
}
