package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import com.hiddenservices.onionservices.R;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.dataModel.geckoDataModel;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.promptDelegate;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.downloadManager.geckoDownloadManager;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.geckoSession;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class downloadHandler {

    private static String S_CHANNEL_ID = "DOWNLOAD_CHANNEL";
    private static String S_CHANNEL_NAME = "ORION DOWNLOADER";
    private static String S_CHANNEL_DESCRIPTION = "ORION DOWNLOADER";

    private WeakReference<AppCompatActivity> mContext;
    private geckoSession mGeckoSession;
    private Uri mUriPermission = null;

    /*Initializations*/

    public downloadHandler(WeakReference<AppCompatActivity> pContext, geckoDataModel pGeckoDataModel, geckoSession pGeckoSession) {
        this.mContext = pContext;
        this.mGeckoSession = pGeckoSession;
    }

    public void downloadRequestedFile(geckoDownloadManager mDownloadManager) {
        if (mDownloadManager.getDownloadURL() != null && mDownloadManager.getDownloadFile() != null) {
            if (!createAndSaveFileFromBase64Url(mDownloadManager.getDownloadURL().toString())) {
                pluginController.getInstance().onDownloadInvoke(Arrays.asList(mDownloadManager.getDownloadURL(), mDownloadManager.getDownloadFile()), pluginEnums.eDownloadManager.M_WEB_DOWNLOAD_REQUEST);
            }
        }
    }

    public void downloadRequestedFile(Uri downloadURL, String downloadFile) {
        if (downloadURL != null && downloadFile != null) {
            if (!createAndSaveFileFromBase64Url(downloadURL.toString())) {
                pluginController.getInstance().onDownloadInvoke(Arrays.asList(downloadURL, downloadFile), pluginEnums.eDownloadManager.M_WEB_DOWNLOAD_REQUEST);
            }
        }
    }

    public void onFileUploadRequest(int resultCode, Intent data, promptDelegate mPromptDelegate) {
        Objects.requireNonNull(mPromptDelegate).onFileCallbackResult(resultCode, data);
    }

    private boolean createAndSaveFileFromBase64Url(String url) {

        try {
            if (!url.startsWith("data") && !url.startsWith("blob")) {
                return false;
            }

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String filetype;
            String filename;

            if (url.startsWith("blob")) {
                mGeckoSession.loadUri((String) pluginController.getInstance().onDownloadInvoke(Collections.singletonList(url), pluginEnums.eDownloadManager.M_BLOB_DOWNLOAD_REQUEST));
                return true;
            }

            filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"));
            filename = System.currentTimeMillis() + "." + filetype;

            File file = new File(path, filename);
            try {
                if (!path.exists())
                    path.mkdirs();
                if (!file.exists())
                    file.createNewFile();

                String base64EncodedString = url.substring(url.indexOf(",") + 1);
                byte[] decodedBytes = Base64.decode(base64EncodedString, Base64.DEFAULT);
                OutputStream os = new FileOutputStream(file);
                os.write(decodedBytes);
                os.close();

                //Tell the media scanner about the new file so that it is immediately available to the user.
                MediaScannerConnection.scanFile(mContext.get().getApplicationContext(),
                        new String[]{file.toString()}, null,
                        (path1, uri) ->
                        {
                        });

                String mimetype = url.substring(url.indexOf(":") + 1, url.indexOf("/"));
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                Uri uri_temp = FileProvider.getUriForFile(mContext.get().getApplicationContext(), mContext.get().getString(R.string.GENERAL_FILE_PROVIDER_AUTHORITY), file);
                intent.setDataAndType(uri_temp, (mimetype + "/*"));

                List<ResolveInfo> resInfoList = mContext.get().getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    mUriPermission = uri_temp;
                    mContext.get().getApplicationContext().grantUriPermission(packageName, uri_temp, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                PendingIntent pIntent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    pIntent = PendingIntent.getActivity(mContext.get().getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
                }

                String channel_id = createNotificationChannel(mContext.get().getApplicationContext());

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext.get().getApplicationContext(), channel_id)
                        .setSmallIcon(R.drawable.ic_download)
                        .setContentTitle(filename)
                        .setContentIntent(pIntent);

                notificationBuilder.setAutoCancel(true);

                int notificationId = 85851;
                NotificationManager notificationManager = (NotificationManager) mContext.get().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId, notificationBuilder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }

    private static String createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = S_CHANNEL_ID;
            CharSequence channelName = S_CHANNEL_NAME;
            String channelDescription = S_CHANNEL_DESCRIPTION;
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(true);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
            return channelId;
        } else {
            return null;
        }
    }

    public Uri getUriPermission() {
        return mUriPermission;
    }

}
