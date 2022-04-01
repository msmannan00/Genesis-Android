package com.hiddenservices.onionservices.pluginManager.downloadPluginManager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import java.util.Arrays;

public class downloadService extends IntentService
{
    public downloadService() {
        super("DOWNLOAD_SERVICE");
    }

    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath, final @NonNull String destinationPath) {
        Intent mIntent = new Intent(callingClassContext, downloadService.class);
        mIntent.putExtra("DOWNLOAD_PATH", downloadPath);
        mIntent.putExtra("DESTINATION_PATH", destinationPath);
        return mIntent;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String mDownloadPath = intent.getStringExtra("DOWNLOAD_PATH");
        String mDestinationPath = intent.getStringExtra("DESTINATION_PATH");

        pluginController.getInstance().onDownloadInvoke(Arrays.asList(mDownloadPath, mDestinationPath), pluginEnums.eDownloadManager.M_START_DOWNLOAD);
    }
}