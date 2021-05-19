package com.darkweb.genesissearchengine.pluginManager.downloadPluginManager;

import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.darkweb.genesissearchengine.pluginManager.pluginReciever.downloadNotificationReciever;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class downloadManager
{
    /* Private Variables */

    private WeakReference<AppCompatActivity> mAppContext;
    private Map<Integer, downloadReciever> mDownloads = new HashMap<>();
    private eventObserver.eventListener mEvent;

    /* Initializations */

    public downloadManager(WeakReference<AppCompatActivity> pAppContext, eventObserver.eventListener pEvent){
        this.mAppContext = pAppContext;
        this.mEvent = pEvent;

        initialize();
    }

    private void initialize()
    {
    }

    private void startDownload(String pPath,String pFile) {
        int mID = helperMethod.createNotificationID();
        downloadReciever mFileDownloader = (downloadReciever)new downloadReciever(mAppContext.get().getApplicationContext(),pPath, pFile, mID, mEvent, downloadNotificationReciever.class).execute(pPath);
        mDownloads.put(mID,mFileDownloader);
    }

    private void cancelDownload(int pID) {
        Objects.requireNonNull(mDownloads.get(pID)).onCancel();
    }

    private void onTriggerDownload(int pID) {
        if(mDownloads!=null && mDownloads.get(pID)!=null){
            mDownloads.get(pID).onTrigger();
        }
    }

    private void  onStartSercvice(String mURL, String mPath){
        mAppContext.get().startService(downloadService.getDownloadService(mAppContext.get().getApplicationContext(), mURL + "__" + mPath, Environment.DIRECTORY_DOWNLOADS));
    }

    private String downloadBlob(String pURL){
        return blobDownloader.getBase64StringFromBlobUrl(pURL);
    }

    /* External Triggers */

    public Object onTrigger(List<Object> pData, pluginEnums.eDownloadManager pEventType) {
        if(pEventType.equals(pluginEnums.eDownloadManager.M_DOWNLOAD_FILE))
        {
            startDownload((String) pData.get(0),(String)pData.get(1));
        }
        else if(pEventType.equals(pluginEnums.eDownloadManager.M_CANCEL))
        {
            cancelDownload((int) pData.get(0));
        }
        else if(pEventType.equals(pluginEnums.eDownloadManager.M_TRIGGER))
        {
            onTriggerDownload((int) pData.get(0));
        }
        else if(pEventType.equals(pluginEnums.eDownloadManager.M_START_SERVICE))
        {
            onStartSercvice((String) pData.get(0), (String) pData.get(1));
        }
        else if(pEventType.equals(pluginEnums.eDownloadManager.M_DOWNLOAD_BLOB))
        {
            return downloadBlob((String) pData.get(0));
        }

        return null;
    }
}
