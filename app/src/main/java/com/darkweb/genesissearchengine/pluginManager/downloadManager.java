package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.helperManager.localFileDownloader;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class downloadManager
{
    /*Private Variables*/

    private WeakReference<AppCompatActivity> mAppContext;
    private Map<Integer, localFileDownloader> mDownloads = new HashMap<>();
    private eventObserver.eventListener mEvent;

    /*Initializations*/

    downloadManager(WeakReference<AppCompatActivity> pAppContext, eventObserver.eventListener pEvent){
        this.mAppContext = pAppContext;
        this.mEvent = pEvent;

        initialize();
    }

    private void initialize()
    {
    }

    private void startDownload(String pPath,String pFile) {
        int mID = helperMethod.createNotificationID();
        localFileDownloader mFileDownloader = (localFileDownloader)new localFileDownloader(mAppContext.get().getApplicationContext(),pPath, pFile, mID, mEvent).execute(pPath);
        mDownloads.put(mID,mFileDownloader);
    }

    private void cancelDownload(int pID) {
        Objects.requireNonNull(mDownloads.get(pID)).onCancel();
    }

    private void onTriggerDownload(int pID) {
        Objects.requireNonNull(mDownloads.get(pID)).onTrigger();
    }

    /*External Triggers*/

    Object onTrigger(List<Object> pData, pluginEnums.eDownloadManager pEventType) {
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

        return null;
    }
}
