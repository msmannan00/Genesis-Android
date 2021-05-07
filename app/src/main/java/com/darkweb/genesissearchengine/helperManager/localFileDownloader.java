package com.darkweb.genesissearchengine.helperManager;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.netcipher.client.StrongHttpsClient;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import org.mozilla.thirdparty.com.google.android.exoplayer2.util.Log;
import org.torproject.android.proxy.util.Prefs;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.conn.params.ConnRoutePNames;

import static com.darkweb.genesissearchengine.constants.enums.etype.M_DOWNLOAD_FAILURE;
import static java.lang.Thread.sleep;


public class localFileDownloader extends AsyncTask<String, Integer, String> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder build;
    private OutputStream output;
    private InputStream mStream;
    private Boolean mIsCanceled = false;

    private String PROXY_ADDRESS = "localhost";
    private int PROXY_PORT = 9050;

    private int mID;
    private String mFileName;
    private float mTotalByte;
    private float mDownloadByte;
    private String mURL;
    private eventObserver.eventListener mEvent;

    public localFileDownloader(Context pContext, String pURL, String pFileName, int pID, eventObserver.eventListener pEvent) {
        this.context = pContext;
        this.mFileName = pFileName;
        this.mURL = pURL;
        this.mID = pID;
        this.mEvent = pEvent;


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

        if(mFileName.length()>30){
            mFileName = "..." + mFileName.substring(mFileName.length()-30);
        }
        build.setContentTitle(mFileName)
                .setContentText("starting...")
                .setChannelId(mID + "")
                .setAutoCancel(false)
                .setDefaults(0)
                .setColor(Color.parseColor("#84989f"))
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_download, "Cancel",pendingIntent)
                .setSmallIcon(android.R.drawable.stat_sys_download);

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

    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[] { "B Downloaded", "KB ⇣", "MB ⇣", "GB ⇣", "TB ⇣" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    @Override
    protected String doInBackground(String... f_url) {
        int count;
        int mRequestCode = 0;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                URL url = new URL(f_url[0]);
                HttpURLConnection conection;
                Proxy proxy;
                if(helperMethod.getDomainName(f_url[0]).contains(".onion")){
                    proxy = new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(PROXY_ADDRESS, 9050));
                    conection = (HttpURLConnection) url.openConnection(proxy);
                }else {
                    conection = (HttpURLConnection) ProxySelector.openConnectionWithProxy(new URI(f_url[0]));;
                }

                conection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:60.0) Gecko/20100101 Firefox/60.0");
                conection.setRequestProperty("Accept","*/*");

                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                mRequestCode = conection.getResponseCode();
                mStream = conection.getInputStream();
                // Output stream
                output = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+mFileName));
                byte[] data = new byte[100000];

                long total = 0;

                mTotalByte = lenghtOfFile;
                while ((count = mStream.read(data)) != -1) {
                    total += count;
                    int cur = (int) ((total * 100) / lenghtOfFile);
                    if(lenghtOfFile<0){
                        cur = (int)total;
                        mDownloadByte = cur * -1;
                    }else {
                        mDownloadByte = cur;
                    }
                    publishProgress(Math.min(cur, 100));
                    if (Math.min(cur, 100) > 98) {
                        sleep(500);
                    }
                    Log.i("currentProgress", "currentProgress: " + Math.min(cur, 100) + "\n " + cur);

                    output.write(data, 0, count);

                }

                build.setContentText("saving file");
                build.setSmallIcon(android.R.drawable.stat_sys_download);
                mNotifyManager.notify(mID, build.build());

                output.flush();
                output.close();
                mStream.close();

            } catch (Exception ex) {
                Log.i("FIZZAHFUCK", ex.getMessage());
                if(mRequestCode!=200){
                    mEvent.invokeObserver(Collections.singletonList(mRequestCode), M_DOWNLOAD_FAILURE);
                    onCancel();
                }
            }
        }else {
            try {
                String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
                String urlEncoded = Uri.encode(f_url[0], ALLOWED_URI_CHARS);

                StrongHttpsClient httpclient = new StrongHttpsClient(context);

                if(helperMethod.getDomainName(f_url[0]).contains(".onion")){
                    httpclient.useProxy(true, "SOCKS", "127.0.0.1", 9050);
                }else {
                    httpclient.useProxy(true, "SOCKS", "127.0.0.1", 9050);
                }

                HttpGet httpget = new HttpGet(urlEncoded);
                HttpResponse response = httpclient.execute(httpget);

                StringBuffer sb = new StringBuffer();
                sb.append(response.getStatusLine()).append("\n\n");

                InputStream mStream = response.getEntity().getContent();

                mRequestCode = response.getStatusLine().getStatusCode();
                output = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+mFileName));
                byte[] data = new byte[100000];

                long total = 0;

                float lenghtOfFile = response.getEntity().getContentLength();
                mTotalByte = lenghtOfFile;
                int read;
                while ((read = mStream.read(data)) != -1) {
                    total += read;
                    int cur = (int) ((total * 100) / response.getEntity().getContentLength());
                    mDownloadByte = cur;
                    if(lenghtOfFile<0){
                        cur = (int)total;
                        mDownloadByte = total * -1;
                    }else {
                        mDownloadByte = cur;
                    }

                    publishProgress(Math.min(cur, 100));
                    if (Math.min(cur, 100) > 98) {
                        sleep(500);
                    }

                    Log.i("currentProgress", "currentProgress: " + Math.min(cur, 100) + "\n " + cur);
                    output.write(data, 0, read);
                }

                build.setContentText("saving file");
                build.setSmallIcon(android.R.drawable.stat_sys_download);
                mNotifyManager.notify(mID, build.build());

                output.flush();
                output.close();
                mStream.close();
            }catch (Exception ex){
                if(mRequestCode!=200){
                    pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(Collections.singletonList(mRequestCode), activityContextManager.getInstance().getHomeController()), pluginEnums.eMessageManager.M_DOWNLOAD_FAILURE);
                    onCancel();
                }
            }
        }
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        int mPercentage =  (int)(mDownloadByte);
        if(mPercentage<0){
            build.setProgress(100, progress[0], true);
            build.setContentText(getFileSize(mPercentage * -1));
        }else {
            build.setProgress(100, progress[0], false);
            build.setContentText(mPercentage+"%");
        }
        mNotifyManager.notify(mID, build.build());
        super.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(String file_url) {
        Intent snoozeIntentPost = new Intent(context, downloadNotification.class);
        snoozeIntentPost.setAction("Download_Open");
        snoozeIntentPost.putExtra("N_ID", mID);
        snoozeIntentPost.putExtra("N_COMMAND", 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, mID, snoozeIntentPost, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snoozeIntentPost1 = new Intent(context, downloadNotification.class);
        snoozeIntentPost1.setAction("Download_Cancelled");
        snoozeIntentPost1.putExtra("N_ID", mID);
        snoozeIntentPost1.putExtra("N_COMMAND", 2);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, mID, snoozeIntentPost1, PendingIntent.FLAG_UPDATE_CURRENT);

        build.addAction(0, null, null);
        build.setContentIntent(pendingIntent);
        build.setContentText("Download complete");
        build.setSmallIcon(R.drawable.ic_download_complete);
        build.setColor(Color.parseColor("#84989f"));
        build.setProgress(0, 0, false);
        build.setAutoCancel(true);
        build.setOngoing(false);
        build.addAction(android.R.drawable.stat_sys_download, "Open",pendingIntent);
        build.addAction(R.drawable.ic_download, "Cancel",pendingIntent1);
        build.setPriority(Notification.PRIORITY_LOW);
        mNotifyManager.notify(mID, build.build());

        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        String mPath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + mFileName).replace("File//","content://");
        File mFile = new File(mPath);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", mFile);

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.TITLE, mFileName);
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, mFileName);
            contentValues.put(MediaStore.Downloads.SIZE, mDownloadByte);
            contentValues.put(MediaStore.Downloads.MIME_TYPE, helperMethod.getMimeType(uri.toString(), context));

            contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + File.separator + "Temp");

            ContentResolver database = context.getContentResolver();
            database.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", mFile);
            String mime = helperMethod.getMimeType(uri.toString(), context);
            if(mime!=null){
                dm.addCompletedDownload(mFileName, mURL, false, mime, mFile.getAbsolutePath(), mFile.length(), false);
            }
        }

    }

    public void onCancel(){
        mIsCanceled = true;
        mNotifyManager.cancel(mID);
        cancel(true);
    }

    public void onTrigger(){
        if(!mIsCanceled){
            mNotifyManager.cancel(mID);
            String mPath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + mFileName).replace("File//","content://");
            File mFile = new File(mPath);

            new Handler().postDelayed(() -> helperMethod.openFile(mFile, context), 500);
        }
    }
}

