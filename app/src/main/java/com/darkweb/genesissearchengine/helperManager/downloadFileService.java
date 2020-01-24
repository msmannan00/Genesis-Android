package com.darkweb.genesissearchengine.helperManager;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public class downloadFileService extends IntentService
{
    private static final String DOWNLOAD_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Download_path";
    private static final String DESTINATION_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Destination_path";
    public downloadFileService() {
        super("DownloadSongService");
    }
    @SuppressLint("StaticFieldLeak")
    static Context context;

    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath, final @NonNull String destinationPath) {
                downloadFileService.context = callingClassContext;
                return new Intent(callingClassContext, downloadFileService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        startDownload(downloadPath);
    }
    private void startDownload(String downloadPath) {
        String []fn = downloadPath.split("__");

        try {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            Uri mDestinationUri = Uri.withAppendedPath(Uri.fromFile(file), fn[1]);

            File myFile = new File(mDestinationUri.getPath());
            if(myFile.exists())
                myFile.delete();

            Uri uri = Uri.parse(fn[0]); // Path where you want to download file.
            DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request req = new DownloadManager.Request(uri);
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fn[1]);
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            manager.enqueue(req);
        } catch ( ActivityNotFoundException e ) {
            e.printStackTrace();
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);
        }
    }
}