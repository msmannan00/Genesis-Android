package com.darkweb.genesissearchengine.appManager.home_activity;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DownloadFileService extends IntentService
{
    private static final String DOWNLOAD_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Download_path";
    private static final String DESTINATION_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Destination_path";
    public DownloadFileService() {
        super("DownloadSongService");
    }
    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath, final @NonNull String destinationPath) {
        return new Intent(callingClassContext, DownloadFileService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        String destinationPath = intent.getStringExtra(DESTINATION_PATH);
        startDownload(downloadPath, destinationPath);
    }
    private void startDownload(String downloadPath, String destinationPath) {
        String []fn = downloadPath.split("__");

        Uri uri = Uri.parse(fn[0]); // Path where you want to download file.
        DownloadManager manager = (DownloadManager) home_model.getInstance().getHomeInstance().getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request req = new DownloadManager.Request(uri);
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fn[1]);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        manager.enqueue(req);
    }

}