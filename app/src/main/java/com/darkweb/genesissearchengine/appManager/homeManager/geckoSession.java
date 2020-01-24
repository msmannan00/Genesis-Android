package com.darkweb.genesissearchengine.appManager.homeManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.JavaScriptInterface;
import com.darkweb.genesissearchengine.helperManager.downloadFileService;
import com.darkweb.genesissearchengine.helperManager.errorHandler;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import org.mozilla.gecko.GeckoSystemStateListener;
import org.mozilla.gecko.GeckoThread;
import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.WebRequestError;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class geckoSession extends GeckoSession implements GeckoSession.PermissionDelegate,GeckoSession.ProgressDelegate, GeckoSession.HistoryDelegate,GeckoSession.NavigationDelegate,GeckoSession.ContentDelegate
{
    private eventObserver.eventListener event;

    private int mSessionID;
    private boolean mCanGoBack = false;
    private boolean mCanGoForward = false;
    private boolean mFullScreen = false;
    private boolean isPageLoading = false;
    private int mProgress = 0;
    private String mCurrentTitle = strings.EMPTY_STR;
    private String mCurrentURL = "about:blank";
    private Uri mUriPermission = null;
    private AppCompatActivity mContext;
    private geckoDownloadManager mDownloadManager;

    /*Temp Variables*/
    private GeckoSession.HistoryDelegate.HistoryList mHistoryList = null;
    private int rateCount=0;

    geckoSession(eventObserver.eventListener event,int mSessionID,AppCompatActivity mContext){

        this.mContext = mContext;
        this.mSessionID = mSessionID;
        setProgressDelegate(this);
        setHistoryDelegate(this);
        setNavigationDelegate(this);
        setContentDelegate(this);
        mDownloadManager = new geckoDownloadManager();
        setPromptDelegate(new geckoPromptView(mContext));

        this.event = event;
    }

    void onFileUploadRequest(int resultCode, Intent data){

        geckoPromptView mPromptDelegate = (geckoPromptView)getPromptDelegate();
        mPromptDelegate.onFileCallbackResult(resultCode,data);
    }

    void initURL(String url){
        isPageLoading = true;
        mCurrentURL = url;
        mCurrentTitle = mCurrentURL;

        event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle), enums.etype.on_update_suggestion);
        if(!url.equals("about:blank"))
        {
            mProgress = 5;
            event.invokeObserver(Arrays.asList(5, mSessionID), enums.etype.progress_update);
        }
    }

    /*Progress Delegate*/

    @Override
    public void onPageStart(@NonNull GeckoSession var1, @NonNull String var2) {
        if(!isPageLoading){
            mCurrentURL = "about:blank";
        }
        isPageLoading = true;
        if(!var2.equals("about:blank")){
            mProgress = 5;
        }

    }

    @AnyThread
    public void shutdown() {
        GeckoSystemStateListener.getInstance().shutdown();
        GeckoThread.forceQuit();
    }

    @UiThread
    public void onPageStop(@NonNull GeckoSession var1, boolean var2) {
    }

    @Override
    public void onProgressChange(@NonNull GeckoSession session, int progress)
    {
        if(!mFullScreen){
            mProgress = progress;
            event.invokeObserver(Arrays.asList(mProgress,mSessionID), enums.etype.progress_update);
        }

        if(progress>=100){
            isPageLoading = false;
        }
    }

    /*History Delegate*/
    @Override
    public GeckoResult<Boolean> onVisited(@NonNull GeckoSession var1, @NonNull String var2, @Nullable String var3, int var4) {
        if(var4==3 || var4==5 || var4==1){
            event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_url_load);
            event.invokeObserver(Arrays.asList(var2,mSessionID,mCurrentTitle), enums.etype.on_request_completed);
            isPageLoading = false;
        }
        return null;
    }

    @UiThread
    public void onHistoryStateChange(@NonNull GeckoSession var1, @NonNull GeckoSession.HistoryDelegate.HistoryList var2) {
        mHistoryList = var2;
    }

    /*Navigation Delegate*/
    public void onLocationChange(@NonNull GeckoSession var1, @Nullable String var2) {

        String newUrl = var2.split("#")[0];
        if(!mCurrentURL.equals("about:blank")){
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,newUrl), enums.etype.on_update_suggestion_url);
        }
        mCurrentURL = newUrl;

        if (var2 != null && !var2.equals("about:blank"))
        {
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID), enums.etype.start_proxy);
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID), enums.etype.search_update);
            event.invokeObserver(Arrays.asList(null,mSessionID), enums.etype.on_page_loaded);
            checkApplicationRate();
        }
    }

    @Override
    public void loadData(@NonNull byte[] var1, @Nullable String var2) {
    }

    public GeckoResult<AllowOrDeny> onLoadRequest(@NonNull GeckoSession var2, @NonNull GeckoSession.NavigationDelegate.LoadRequest var1) {
        if(var1.uri.contains("boogle.store/advert__")){
            event.invokeObserver(Arrays.asList(var1.uri,mSessionID), enums.etype.on_playstore_load);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        else if(var1.uri.equals("about:blank")){
            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        }
        else if(var1.target==2){
            event.invokeObserver(Arrays.asList(var1.uri,mSessionID), enums.etype.open_new_tab);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        else {
            event.invokeObserver(Arrays.asList(var1.uri,mSessionID), enums.etype.start_proxy);
            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        }
    }

    @Override
    public void onCanGoBack(@NonNull GeckoSession session, boolean var2)
    {
        mCanGoBack = var2;
    }

    @Override
    public void onCanGoForward(@NonNull GeckoSession session, boolean var2)
    {
        mCanGoForward = var2;
    }

    public GeckoResult<String> onLoadError(@NonNull GeckoSession var1, @Nullable String var2, WebRequestError var3) {
        errorHandler handler = new errorHandler();
        mProgress = 0;
        event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_load_error);
        event.invokeObserver(Arrays.asList(mProgress,mSessionID), enums.etype.progress_update);
        return GeckoResult.fromValue("data:text/html," + handler.createErrorPage(var3.category, var3.code,mContext,var2));
    }

    /*Content Delegate*/
    @Override
    public void onExternalResponse(@NonNull GeckoSession session, @NonNull GeckoSession.WebResponseInfo response) {
        try {
            event.invokeObserver(Arrays.asList(response,mSessionID), enums.etype.on_handle_external_intent);
        } catch (ActivityNotFoundException e) {
            mDownloadManager.downloadFile(response,this,mContext,event);
            stop();
        }
    }

    @UiThread
    public void onTitleChange(@NonNull GeckoSession var1, @Nullable String var2) {
        if(var2!=null && !var2.equals(strings.EMPTY_STR) && var2.length()>2 && !mCurrentURL.equals("about:blank")){
            mCurrentTitle = var2;
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle), enums.etype.on_update_suggestion);
        }
    }

    @Override
    public void onFullScreen(@NonNull GeckoSession var1, boolean var2) {
        mFullScreen = var2;
        event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_full_screen);
    }

    public void onContextMenu(@NonNull GeckoSession var1, int var2, int var3, @NonNull GeckoSession.ContentDelegate.ContextElement var4) {

        String title = strings.EMPTY_STR;
        if(var4.title!=null){
            title = var4.title;
        }
        if(var4.type!=0){
            if(var4.linkUri!=null){
                event.invokeObserver(Arrays.asList(var4.linkUri,mSessionID,var4.srcUri,title), enums.etype.on_long_press_with_link);
            }
            else {
                event.invokeObserver(Arrays.asList(var4.srcUri,mSessionID,title), enums.etype.on_long_press);
            }
        }
        else{
            event.invokeObserver(Arrays.asList(var4.linkUri,mSessionID,title), enums.etype.on_long_press_url);
        }
    }

    /*Download Manager*/

    void downloadRequestedFile()
    {
        if(mDownloadManager.getDownloadURL()!=null && mDownloadManager.getDownloadFile()!=null){
            if(!createAndSaveFileFromBase64Url(mDownloadManager.getDownloadURL().toString())){
                mContext.startService(downloadFileService.getDownloadService(mContext, mDownloadManager.getDownloadURL()+"__"+mDownloadManager.getDownloadFile(), Environment.DIRECTORY_DOWNLOADS));
            }
        }
    }

    void downloadRequestedFile(Uri downloadURL,String downloadFile)
    {
        if(downloadURL!=null && downloadFile!=null){
            if(!createAndSaveFileFromBase64Url(downloadURL.toString())){
                mContext.startService(downloadFileService.getDownloadService(mContext, downloadURL + "__" + downloadFile, Environment.DIRECTORY_DOWNLOADS));
            }
        }
    }

    public boolean downloadBlobFile(String url){

        try{
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String filetype = "";
            String filename = "";

            filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"));
            filename = System.currentTimeMillis() + "." + filetype;

            File file = new File(path, filename);
            try {
                if(!path.exists())
                    path.mkdirs();
                if(!file.exists())
                    file.createNewFile();

                String base64EncodedString = url.substring(url.indexOf(",") + 1);
                byte[] decodedBytes = Base64.decode(base64EncodedString, Base64.DEFAULT);
                OutputStream os = new FileOutputStream(file);
                os.write(decodedBytes);
                os.close();

                //Tell the media scanner about the new file so that it is immediately available to the user.
                MediaScannerConnection.scanFile(mContext,
                        new String[]{file.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });

                //Set notification after download complete and add "click to view" action to that
                String mimetype = url.substring(url.indexOf(":") + 1, url.indexOf("/"));
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), (mimetype + "/*"));
                PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

                Notification notification = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.xml.ic_download)
                        .setContentTitle(filename)
                        .setContentIntent(pIntent)
                        .build();

                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                int notificationId = 85851;
                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId, notification);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        catch (Exception ignored){

        }

        return true;
    }

    private boolean createAndSaveFileFromBase64Url(String url) {

        if(!url.startsWith("data") && !url.startsWith("blob")){
            return false;
        }
        else if(url.startsWith("blob")){
            Toast toast = Toast.makeText(mContext.getApplicationContext(),
                    "Unable to download urls that contain prefix blob. Not Supported",
                    Toast.LENGTH_SHORT);

            toast.show();
            return true;
        }

        try{
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String filetype = "";
            String filename = "";

            if(url.startsWith("blob")){
                loadUri(JavaScriptInterface.getBase64StringFromBlobUrl(url));
                return true;
            }

            filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"));
            filename = System.currentTimeMillis() + "." + filetype;

            File file = new File(path, filename);
            try {
                if(!path.exists())
                    path.mkdirs();
                if(!file.exists())
                    file.createNewFile();

                String base64EncodedString = url.substring(url.indexOf(",") + 1);
                byte[] decodedBytes = Base64.decode(base64EncodedString, Base64.DEFAULT);
                OutputStream os = new FileOutputStream(file);
                os.write(decodedBytes);
                os.close();

                //Tell the media scanner about the new file so that it is immediately available to the user.
                MediaScannerConnection.scanFile(mContext,
                        new String[]{file.toString()}, null,
                        (path1, uri) ->
                        {
                            Log.i("ExternalStorage", "Scanned " + path1 + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        });

                //Set notification after download complete and add "click to view" action to that
                String mimetype = url.substring(url.indexOf(":") + 1, url.indexOf("/"));
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), (mimetype + "/*"));
                } else {
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    Uri uri_temp = FileProvider.getUriForFile(mContext,mContext.getString(R.string.file_provider_authority), file);
                    intent.setDataAndType(uri_temp, (mimetype + "/*"));

                    List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        mUriPermission = uri_temp;
                        mContext.grantUriPermission(packageName, uri_temp, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
                PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Notification notification = new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.xml.ic_download)
                            .setContentTitle(filename)
                            .setContentIntent(pIntent)
                            .build();

                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    int notificationId = 85851;
                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(notificationId, notification);
                } else {

                    String channel_id = createNotificationChannel(mContext);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, channel_id)
                            .setSmallIcon(R.xml.ic_download)
                            .setContentTitle(filename)
                            .setContentIntent(pIntent);

                    notificationBuilder.setAutoCancel(true);

                    int notificationId = 85851;
                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(notificationId, notificationBuilder.build());
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return true;
    }

    private static String createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "Channel_id";
            CharSequence channelName = "Application_name";
            String channelDescription = "Application_name Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            boolean channelEnableVibrate = true;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
            return channelId;
        } else {
            return null;
        }
    }

    /*Helper Methods*/

    public String getCurrentURL(){
        return mCurrentURL;
    }

    public String getTitle(){
        return mCurrentTitle;
    }

    int getProgress(){
        return mProgress;
    }

    public void setTitle(String title){
        mCurrentTitle = title;
    }

    boolean canGoBack(){
        return mCanGoBack;
    }

    boolean canGoForward(){
        return mCanGoForward;
    }

    public int getSessionID(){
        return mSessionID;
    }

    void exitScreen(){
        this.exitFullScreen();
    }

    boolean getFullScreenStatus(){
        return !mFullScreen;
    }

    public void closeSession(){
        event.invokeObserver(Arrays.asList(null,mSessionID), enums.etype.on_close_sesson);
    }

    void goBackSession(){

        if(mHistoryList!=null){
            int index = mHistoryList.getCurrentIndex()-1;

            if(mHistoryList!=null && index>=0 && index<mHistoryList.size()){
                event.invokeObserver(Arrays.asList(mHistoryList.get(index).getUri(),mSessionID), enums.etype.start_proxy);

                new Handler().postDelayed(() ->
                {
                    goBack();
                }, 100);
            }
        }
        else {
            goBack();
        }
    }

    void goForwardSession(){

        if(mHistoryList!=null)
        {
            int index = mHistoryList.getCurrentIndex() + 1;

            if (mHistoryList != null && index >= 0 && index < mHistoryList.size())
            {

                event.invokeObserver(Arrays.asList(mHistoryList.get(index), mSessionID), enums.etype.start_proxy);

                new Handler().postDelayed(() ->
                {
                    goForward();
                }, 100);
            }
        }else {
            goForward();
        }
    }

    boolean isLoading(){
        return isPageLoading;
    }

    void setLoading(boolean status){
        isPageLoading = status;
    }

    Uri getUriPermission(){
        return mUriPermission;
    }

    private void checkApplicationRate(){
        if(rateCount==7){
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle), enums.etype.rate_application);
        }
        rateCount+=1;
   }

}
