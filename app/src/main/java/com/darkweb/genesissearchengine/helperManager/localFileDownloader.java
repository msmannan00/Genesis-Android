package com.darkweb.genesissearchengine.helperManager;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import androidx.core.app.NotificationCompat;
import com.example.myapplication.R;
import org.mozilla.thirdparty.com.google.android.exoplayer2.util.Log;
import org.torproject.android.service.util.Prefs;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import static java.lang.Thread.sleep;


public class localFileDownloader extends AsyncTask<String, Integer, String> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder build;
    private OutputStream output;
    private InputStream mStream;

    private String PROXY_ADDRESS = "localhost";
    private int PROXY_PORT = 9050;

    private int mID = 123;
    private String mFileName="";
    private float mTotalByte;
    private float mDownloadByte;

    public localFileDownloader(Context pContext, String pURL, String pFileName, int pID) {
        this.context = pContext;
        this.mFileName = pFileName;
        this.mID = pID;


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mFileName = pFileName;
    }

    protected void onPreExecute() {
        super.onPreExecute();

        Intent snoozeIntent = new Intent(context, downloadNotification.class);
        snoozeIntent.setAction("Download_Cancelled");
        snoozeIntent.putExtra("N_ID", mID);
        snoozeIntent.putExtra("N_COMMAND", 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, mID, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        build = new NotificationCompat.Builder(context);

        build.setContentTitle(mFileName)
                .setContentText("starting...")
                .setChannelId(mID + "")
                .setAutoCancel(false)
                .setDefaults(0)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_download, "Cancel",pendingIntent)
                .setSmallIcon(R.drawable.ic_download);

        build.setOngoing(Prefs.persistNotifications());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(mID + "",
                    "Social Media Downloader",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("no sound");
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(false);
            mNotifyManager.createNotificationChannel(channel);

        }
        build.setProgress(100, 0, false);
        mNotifyManager.notify(mID, build.build());
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);

            Proxy proxy = new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(PROXY_ADDRESS, PROXY_PORT));


            URLConnection conection = url.openConnection(proxy);

            conection.connect();
            int lenghtOfFile = conection.getContentLength();

            mStream = conection.getInputStream();
            // Output stream
            output = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+mFileName));
            byte[] data = new byte[100000];

            long total = 0;

            mTotalByte = lenghtOfFile;
            while ((count = mStream.read(data)) != -1) {
                total += count;
                int cur = (int) ((total * 100) / lenghtOfFile);
                mDownloadByte = cur;
                publishProgress(Math.min(cur, 100));
                if (Math.min(cur, 100) > 98) {
                    sleep(500);
                }
                Log.i("currentProgress", "currentProgress: " + Math.min(cur, 100) + "\n " + cur);

                output.write(data, 0, count);

            }

            output.flush();
            output.close();

            mStream.close();

        } catch (Exception ex) {
            Log.i("ERROR", Objects.requireNonNull(ex.getMessage()));
        }

        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        build.setProgress(100, progress[0], false);
        int mPercentage =  (int)(mDownloadByte);
        if(mPercentage<0){
            mPercentage = 0;
        }
        build.setContentText(mPercentage+"%");
        mNotifyManager.notify(mID, build.build());
        super.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(String file_url) {
        Intent snoozeIntentPost = new Intent(context, downloadNotification.class);
        snoozeIntentPost.setAction("Download_Cancelled");
        snoozeIntentPost.putExtra("N_ID", mID);
        snoozeIntentPost.putExtra("N_COMMAND", 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, mID, snoozeIntentPost, PendingIntent.FLAG_UPDATE_CURRENT);

        build.setContentIntent(pendingIntent);
        build.addAction(R.drawable.ic_download, "Open",pendingIntent);
        build.setContentText("Download complete");
        build.setSmallIcon(R.drawable.ic_download_complete);
        build.setProgress(0, 0, false);
        build.setAutoCancel(true);
        build.setPriority(Notification.PRIORITY_LOW);
        mNotifyManager.notify(mID, build.build());
    }

    public void onCancel(){
        mNotifyManager.cancel(mID);
        cancel(true);
    }

    public void onTrigger(){
        mNotifyManager.cancel(mID);
        String mPath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + mFileName).replace("File//","content://");
        File mFile = new File(mPath);
        helperMethod.openFile(mFile, context);
    }
}

