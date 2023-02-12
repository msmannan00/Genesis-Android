package com.hiddenservices.onionservices.pluginManager.downloadPluginManager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;

import java.util.Arrays;

public class downloadService extends IntentService {

    private static String S_DOWNLOAD_SERVICE_NAME = "DOWNLOAD_SERVICE";
    private static String S_DOWNLOAD_PATH = "DOWNLOAD_PATH";
    private static String S_DOWNLOAD_DESTINATION_PATH = "DESTINATION_PATH";

    public downloadService() {
        super(S_DOWNLOAD_SERVICE_NAME);
    }

    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath, final @NonNull String destinationPath) {
        Intent mIntent = new Intent(callingClassContext, downloadService.class);
        mIntent.putExtra(S_DOWNLOAD_PATH, downloadPath);
        mIntent.putExtra(S_DOWNLOAD_DESTINATION_PATH, destinationPath);
        return mIntent;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String mDownloadPath = intent.getStringExtra(S_DOWNLOAD_PATH);
        String mDestinationPath = intent.getStringExtra(S_DOWNLOAD_DESTINATION_PATH);

        pluginController.getInstance().onDownloadInvoke(Arrays.asList(mDownloadPath, mDestinationPath), pluginEnums.eDownloadManager.M_START_DOWNLOAD);
    }
}