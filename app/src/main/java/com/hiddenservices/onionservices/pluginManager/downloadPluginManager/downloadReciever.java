package com.hiddenservices.onionservices.pluginManager.downloadPluginManager;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.libs.netcipher.client.StrongHttpsClient;
import com.example.myapplication.R;
import org.orbotproject.android.service.wrapper.orbotLocalConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;

import static com.hiddenservices.onionservices.constants.enums.etype.M_DOWNLOAD_FAILURE;
import static java.lang.Thread.sleep;


public class downloadReciever extends AsyncTask<String, Integer, String> {


    private WeakReference<Context> mContext;
    private eventObserver.eventListener mEvent;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private Class<?> mBroadcastReciever;

    private int mNotificationID;
    private float mDownloadByte;
    private String mURL;
    private String mFileName;
    private Boolean mIsCanceled = false;

    public downloadReciever(Context pContext, String pURL, String pFileName, int pNotificationID, eventObserver.eventListener pEvent, Class<?> pBroadcastReciever) {
        this.mContext = new WeakReference(pContext);
        this.mEvent = pEvent;

        this.mFileName = pFileName;
        this.mURL = pURL;
        this.mNotificationID = pNotificationID;
        this.mBroadcastReciever = pBroadcastReciever;


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    protected void onPreExecute() {
        super.onPreExecute();

        /* Create Pending Intent */

        PendingIntent pendingIntentCancel = helperMethod.onCreateActionIntent(mContext.get(), mBroadcastReciever, mNotificationID, "Download_Cancelled",0);

        /* Create Notification */

        if(mFileName.length()>30){
            mFileName = "..." + mFileName.substring(mFileName.length()-30);
        }

        mNotifyManager = (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(mContext.get());
        mNotificationBuilder.setContentTitle(mFileName)
                .setContentText("starting...")
                .setChannelId(mNotificationID + "")
                .setAutoCancel(false)
                .setDefaults(0)
                .setColor(Color.parseColor("#84989f"))
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_download, "Cancel",pendingIntentCancel)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setDeleteIntent(pendingIntentCancel)
                .setOngoing(false);

        /* Notification Channel for Latest Androids */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(mNotificationID + "", "Social Media Downloader", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("no sound");
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(false);
            mNotifyManager.createNotificationChannel(channel);
        }
        mNotificationBuilder.setProgress(100, 0, false);
        mNotifyManager.notify(mNotificationID, mNotificationBuilder.build());
    }


    @Override
    protected String doInBackground(String... f_url) {
        int mRequestCode = 0;
        OutputStream mOutputStream;
        InputStream mInputStream;

        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                String fURL = f_url[0];
                URL url = new URL(fURL);
                HttpURLConnection conection;
                Proxy proxy;
                if(helperMethod.getDomainName(fURL).contains(".onion")){
                    proxy = new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved("localhost", orbotLocalConstants.mSOCKSPort));
                    conection = (HttpURLConnection) url.openConnection(proxy);
                }else {
                    Proxy mProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", orbotLocalConstants.mHTTPPort));
                    URLConnection mURLConnection = new URI(fURL).toURL().openConnection(mProxy);
                    conection = (HttpURLConnection) mURLConnection;
                }

                conection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:60.0) Gecko/20100101 Firefox/60.0");
                conection.setRequestProperty("Accept","*/*");
                conection.connect();
                mRequestCode = conection.getResponseCode();
                mInputStream = conection.getInputStream();
                mOutputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+mFileName);
                int lenghtOfFile = conection.getContentLength();

                readStream(mInputStream, mOutputStream, lenghtOfFile);
            }else {
                String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
                String urlEncoded = Uri.encode(f_url[0], ALLOWED_URI_CHARS);
                StrongHttpsClient httpclient = new StrongHttpsClient(mContext.get());

                if(helperMethod.getDomainName(f_url[0]).contains(".onion")){
                    httpclient.useProxy(true, "SOCKS", "127.0.0.1", orbotLocalConstants.mSOCKSPort);
                }else {
                    httpclient.useProxy(true, "SOCKS", "127.0.0.1", orbotLocalConstants.mSOCKSPort);
                }

                HttpGet httpget = new HttpGet(urlEncoded);
                HttpResponse response = httpclient.execute(httpget);
                mInputStream = response.getEntity().getContent();
                mRequestCode = response.getStatusLine().getStatusCode();
                mOutputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+mFileName);
                float lenghtOfFile = response.getEntity().getContentLength();
                readStream(mInputStream, mOutputStream, lenghtOfFile);
            }
        }catch (Exception ex){
            if(mRequestCode!=200 && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                onBackgroundException(mRequestCode);
            }
            else if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
                onBackgroundException(mRequestCode);
        }

        return null;
    }

    @SuppressLint({"UnspecifiedImmutableFlag", "LaunchActivityFromNotification"})
    @Override
    protected void onPostExecute(String file_url) {

        /* Create Pending Intent */

        PendingIntent pendingIntentOpen = helperMethod.onCreateActionIntent(mContext.get(), mBroadcastReciever, mNotificationID, "Download_Open",1);
        PendingIntent pendingIntentCancel = helperMethod.onCreateActionIntent(mContext.get(), mBroadcastReciever, mNotificationID, "Download_Cancelled",2);

        /* Create Notification */

        mNotificationBuilder.addAction(0, null, null);
        mNotificationBuilder.setContentIntent(pendingIntentOpen);
        mNotificationBuilder.setContentText("Download complete");
        mNotificationBuilder.setSmallIcon(R.drawable.ic_download_complete);
        mNotificationBuilder.setColor(Color.parseColor("#84989f"));
        mNotificationBuilder.setProgress(0, 0, false);
        mNotificationBuilder.setAutoCancel(true);
        mNotificationBuilder.setOngoing(false);
        mNotificationBuilder.addAction(android.R.drawable.stat_sys_download, "Open",pendingIntentOpen);
        mNotificationBuilder.addAction(R.drawable.ic_download, "Cancel",pendingIntentCancel);
        mNotificationBuilder.setOngoing(false);
        mNotificationBuilder.setPriority(Notification.PRIORITY_LOW);
        mNotifyManager.notify(mNotificationID, mNotificationBuilder.build());

        DownloadManager dm = (DownloadManager) mContext.get().getSystemService(Context.DOWNLOAD_SERVICE);
        String mPath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + mFileName).replace("File//","content://");
        File mFile = new File(mPath);

        /* Create Dwonload Complete Destination */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri uri = FileProvider.getUriForFile(mContext.get(), "com.hiddenservices.onionservices.provider", mFile);

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.TITLE, mFileName);
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, mFileName);
            contentValues.put(MediaStore.Downloads.SIZE, mDownloadByte);
            contentValues.put(MediaStore.Downloads.MIME_TYPE, helperMethod.getMimeType(uri.toString(), mContext.get()));
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + File.separator + mFileName + "_" + helperMethod.createRandomID().substring(0,5));
            ContentResolver database = mContext.get().getContentResolver();
            database.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            Uri uri = FileProvider.getUriForFile(mContext.get(), "com.hiddenservices.onionservices.provider", mFile);
            String mime = helperMethod.getMimeType(uri.toString(), mContext.get());
            if(mime!=null){
                dm.addCompletedDownload(mFileName, mURL, false, mime, mFile.getAbsolutePath(), mFile.length(), false);
            }
        }

    }

    /* UI TRIGGERS*/

    protected void onProgressUpdate(Integer... progress) {
        int mPercentage =  (int)(mDownloadByte);
        if(mPercentage<0){
            mNotificationBuilder.setProgress(100, progress[0], true);
            mNotificationBuilder.setContentText(helperMethod.getFileSizeBadge(mPercentage * -1));
        }else {
            mNotificationBuilder.setProgress(100, progress[0], false);
            mNotificationBuilder.setContentText(mPercentage+"%");
        }
        mNotifyManager.notify(mNotificationID, mNotificationBuilder.build());
        super.onProgressUpdate(progress);
    }

    public void onCancel(){
        mIsCanceled = true;
        mNotifyManager.cancel(mNotificationID);
        cancel(true);
    }

    public void onTrigger(){
        if(!mIsCanceled){
            mNotifyManager.cancel(mNotificationID);
            String mPath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + mFileName).replace("File//","content://");
            File mFile = new File(mPath);
            new Handler().postDelayed(() -> helperMethod.openFile(mFile, activityContextManager.getInstance().getHomeController()), 500);
        }
    }

    /* Helper Methods*/

    private boolean readStream(InputStream pInputStream,OutputStream pOutputStream, float pLengthOfFile) throws IOException, InterruptedException {

        byte[] mData = new byte[100000];
        long mTotalReadCount = 0;
        int mCurrentReadCount;

        while ((mCurrentReadCount = pInputStream.read(mData)) != -1) {
            if(!status.sSettingIsAppRunning){
                return false;
            }
            mTotalReadCount += mCurrentReadCount;
            int cur = (int) ((mTotalReadCount * 100) / pLengthOfFile);
            mDownloadByte = cur;
            if(pLengthOfFile<0){
                cur = (int)mTotalReadCount;
                mDownloadByte = mTotalReadCount * -1;
            }else {
                mDownloadByte = cur;
            }

            publishProgress(Math.min(cur, 100));
            if (Math.min(cur, 100) > 98) {
                sleep(500);
            }
            pOutputStream.write(mData, 0, mCurrentReadCount);
        }

        mNotificationBuilder.setContentText("saving file");
        mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        mNotifyManager.notify(mNotificationID, mNotificationBuilder.build());

        pOutputStream.flush();
        pOutputStream.close();
        pInputStream.close();
        return true;
    }

    private void onBackgroundException(int pRequestCode){
        String mRequestCodeResponse = String.valueOf(pRequestCode);
        if(mRequestCodeResponse == null || mRequestCodeResponse.equals("0")){
            mRequestCodeResponse = "\"Unknown\"";
        }

        String finalMRequestCodeResponse = mRequestCodeResponse;
        activityContextManager.getInstance().getHomeController().runOnUiThread(() -> mEvent.invokeObserver(Collections.singletonList("Request Error | " + finalMRequestCodeResponse), M_DOWNLOAD_FAILURE));
        onCancel();
    }

}

