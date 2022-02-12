package com.hiddenservices.genesissearchengine.production.pluginManager.downloadPluginManager;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hiddenservices.genesissearchengine.production.pluginManager.pluginController;
import com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums;

import java.util.Arrays;

public class downloadService extends IntentService
{
    private static final String DOWNLOAD_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Download_path";
    private static final String DESTINATION_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Destination_path";

    public downloadService() {
        super("DownloadSongService");
    }
    @SuppressLint("StaticFieldLeak")
    static Context context;

    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath, final @NonNull String destinationPath) {
                downloadService.context = callingClassContext;
                return new Intent(callingClassContext, downloadService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        startDownload(downloadPath);
    }
    private void startDownload(String downloadPath) {
        String []fn = (downloadPath+"__"+"as").split("__");
        pluginController.getInstance().onDownloadInvoke(Arrays.asList(fn[0],fn[1]), pluginEnums.eDownloadManager.M_DOWNLOAD_FILE);
    }
}