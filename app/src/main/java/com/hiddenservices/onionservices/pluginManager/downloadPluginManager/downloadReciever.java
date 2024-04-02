package com.hiddenservices.onionservices.pluginManager.downloadPluginManager;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import com.hiddenservices.onionservices.R;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import org.torproject.android.service.wrapper.orbotLocalConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class downloadReciever {

    private WeakReference<Context> mContext;
    private eventObserver.eventListener mEvent;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private Class<?> mBroadcastReceiver;
    private int mNotificationID;
    private float mDownloadByte;
    private String mFileName;
    private boolean mIsCanceled = false;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler;

    public downloadReciever(Context context, String fileName, int notificationID, eventObserver.eventListener event, Class<?> broadcastReceiver) {
        handler = new Handler(Looper.getMainLooper());
        this.mContext = new WeakReference<>(context);
        this.mEvent = event;
        this.mFileName = helperMethod.createRandomID().substring(0, 5) + sanitizeFileName(fileName);
        this.mNotificationID = notificationID;
        this.mBroadcastReceiver = broadcastReceiver;
    }

    private String sanitizeFileName(String fileName) {
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }
        if (fileName.contains("?")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("?"));
        }
        return fileName;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void execute(String... f_url) {
        onPreExecute();
        executorService.submit(() -> {
            doInBackground(f_url);
            handler.post(this::onPostExecute);
        });
    }

    protected void onPreExecute() {
        PendingIntent pendingIntentCancel = helperMethod.onCreateActionIntent(mContext.get(), mBroadcastReceiver, mNotificationID, "Download_Cancelled", enums.DownloadNotificationReceiver.DOWNLOAD_CANCEL);

        if (mFileName.length() > 30) {
            mFileName = "..." + mFileName.substring(mFileName.length() - 30);
        }

        mNotifyManager = (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(mNotificationID), "Social Media Downloader", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("no sound");
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(false);
            mNotifyManager.createNotificationChannel(channel);
        }

        mNotificationBuilder = new NotificationCompat.Builder(mContext.get(), String.valueOf(mNotificationID))
                .setContentTitle(mFileName)
                .setContentText("starting...")
                .setAutoCancel(false)
                .setDefaults(0)
                .setColor(Color.parseColor("#84989f"))
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_download, "Cancel", pendingIntentCancel)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setDeleteIntent(pendingIntentCancel)
                .setOngoing(false)
                .setProgress(100, 0, false);

        mNotifyManager.notify(mNotificationID, mNotificationBuilder.build());
    }


    public void onTrigger() {
        if (!mIsCanceled) {
            mNotifyManager.cancel(mNotificationID);
            String mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + mFileName;
            File mFile = new File(mPath);
            new Handler().postDelayed(() -> helperMethod.openFile(mFile, activityContextManager.getInstance().getHomeController()), 500);
        }
    }
    protected void doInBackground(String... f_url) {
        OutputStream mOutputStream;
        InputStream mInputStream;

        try {
            String fURL = f_url[0];
            URL url = new URL(fURL);
            HttpURLConnection connection;

            if (helperMethod.getDomainName(fURL).contains(".onion")) {
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved("localhost", orbotLocalConstants.mSOCKSPort));
                connection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:60.0) Gecko/20100101 Firefox/60.0");
            connection.setRequestProperty("Accept", "*/*");
            connection.connect();

            mInputStream = connection.getInputStream();
            mOutputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + mFileName);
            int lengthOfFile = connection.getContentLength();

            readStream(mInputStream, mOutputStream, lengthOfFile);

        } catch (Exception e) {
            onBackgroundException();
        }

    }

    protected void onPostExecute() {
        if(!mIsCanceled){
            PendingIntent pendingIntentOpen = helperMethod.onCreateActionIntent(mContext.get(), mBroadcastReceiver, mNotificationID, "Download_Open", enums.DownloadNotificationReceiver.DOWNLOAD_OPEN);
            PendingIntent pendingIntentCancel = helperMethod.onCreateActionIntent(mContext.get(), mBroadcastReceiver, mNotificationID, "Download_Cancelled", enums.DownloadNotificationReceiver.DOWNLOAD_CANCEL);

            mNotificationBuilder.addAction(0, null, null);
            mNotificationBuilder.setContentIntent(pendingIntentOpen);
            mNotificationBuilder.setContentText("Download complete");
            mNotificationBuilder.setSmallIcon(R.drawable.ic_download_complete);
            mNotificationBuilder.setColor(Color.parseColor("#84989f"));
            mNotificationBuilder.setProgress(0, 0, false);
            mNotificationBuilder.setAutoCancel(true);
            mNotificationBuilder.setOngoing(false);
            mNotificationBuilder.addAction(android.R.drawable.stat_sys_download, "Open", pendingIntentOpen);
            mNotificationBuilder.addAction(R.drawable.ic_download, "Cancel", pendingIntentCancel);
            mNotificationBuilder.setOngoing(false);
            mNotificationBuilder.setPriority(Notification.PRIORITY_LOW);
            mNotifyManager.notify(mNotificationID, mNotificationBuilder.build());
        }else {
            mNotifyManager.cancel(mNotificationID);
        }
    }

    protected void onProgressUpdate(Integer... progress) {
        int mPercentage = (int) (mDownloadByte);
        if (mPercentage < 0) {
            mNotificationBuilder.setProgress(100, progress[0], true);
            mNotificationBuilder.setContentText(helperMethod.getFileSizeBadge(mPercentage * -1));
        } else {
            mNotificationBuilder.setProgress(100, progress[0], false);
            mNotificationBuilder.setContentText(mPercentage + "%");
        }
        mNotifyManager.notify(mNotificationID, mNotificationBuilder.build());
    }

    public void onCancel() {
        mIsCanceled = true;
        mNotifyManager.cancel(mNotificationID);
        executorService.shutdownNow();
    }

    private void readStream(InputStream pInputStream, OutputStream pOutputStream, float pLengthOfFile) throws IOException, InterruptedException {
        byte[] mData = new byte[100000];
        long mTotalReadCount = 0;
        int mCurrentReadCount;

        while ((mCurrentReadCount = pInputStream.read(mData)) != -1) {
            if (!status.sSettingIsAppRunning) {
                return;
            }
            mTotalReadCount += mCurrentReadCount;
            int cur = (int) ((mTotalReadCount * 100) / pLengthOfFile);
            if (pLengthOfFile < 0) {
                cur = (int) mTotalReadCount;
                mDownloadByte = mTotalReadCount * -1;
            } else {
                mDownloadByte = cur;
            }

            publishProgress(Math.min(cur, 100));
            if (Math.min(cur, 100) > 98) {
                sleep(500);
            }
            pOutputStream.write(mData, 0, mCurrentReadCount);
        }

        mNotifyManager.cancel(mNotificationID);
        onPostExecute();
    }

    private void publishProgress(int progress) {
        onProgressUpdate(progress);
    }

    private void onBackgroundException() {
        onCancel();
    }
}
