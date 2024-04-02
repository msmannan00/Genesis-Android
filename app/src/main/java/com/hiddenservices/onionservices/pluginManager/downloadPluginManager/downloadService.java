package com.hiddenservices.onionservices.pluginManager.downloadPluginManager;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Data;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;

import java.util.Arrays;

// Other imports as necessary

public class downloadService extends Worker {
    private static final String S_DOWNLOAD_PATH = "DOWNLOAD_PATH";
    private static final String S_DOWNLOAD_DESTINATION_PATH = "DESTINATION_PATH";

    public downloadService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String mDownloadPath = getInputData().getString(S_DOWNLOAD_PATH);
        String mDestinationPath = getInputData().getString(S_DOWNLOAD_DESTINATION_PATH);
        pluginController.getInstance().onDownloadInvoke(Arrays.asList(mDownloadPath, mDestinationPath), pluginEnums.eDownloadManager.M_START_DOWNLOAD);

        return Result.success();
    }

    public static void enqueueDownload(@NonNull Context context, @NonNull String downloadPath, @NonNull String destinationPath) {
        Data inputData = new Data.Builder()
                .putString(S_DOWNLOAD_PATH, downloadPath)
                .putString(S_DOWNLOAD_DESTINATION_PATH, destinationPath)
                .build();

        OneTimeWorkRequest downloadWorkRequest = new OneTimeWorkRequest.Builder(downloadService.class)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context).enqueue(downloadWorkRequest);
    }
}
