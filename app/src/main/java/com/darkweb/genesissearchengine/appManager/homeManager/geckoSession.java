package com.darkweb.genesissearchengine.appManager.homeManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.JavaScriptInterface;
import com.darkweb.genesissearchengine.helperManager.downloadFileService;
import com.darkweb.genesissearchengine.helperManager.errorHandler;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import org.mozilla.gecko.util.ThreadUtils;
import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.Autofill;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.WebRequestError;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_DESKTOP;
import static org.mozilla.geckoview.GeckoSessionSettings.USER_AGENT_MODE_MOBILE;

public class geckoSession extends GeckoSession implements GeckoSession.MediaDelegate,GeckoSession.ScrollDelegate,GeckoSession.PermissionDelegate,GeckoSession.ProgressDelegate, GeckoSession.HistoryDelegate,GeckoSession.NavigationDelegate,GeckoSession.ContentDelegate
{
    private eventObserver.eventListener event;

    private String mSessionID;
    private boolean mCanGoBack = false;
    private boolean mCanGoForward = false;
    private boolean mFullScreen = false;
    private boolean isPageLoading = false;
    private int mProgress = 0;
    private String mCurrentTitle = "loading";
    private String mCurrentURL = "about:blank";
    private Uri mUriPermission = null;
    private AppCompatActivity mContext;
    private geckoDownloadManager mDownloadManager;

    /*Temp Variables*/
    private GeckoSession.HistoryDelegate.HistoryList mHistoryList = null;
    private int rateCount=0;
    private int m_current_url_id = -1;
    private GeckoView mGeckoView;
    private boolean mIsLoaded = false;

    geckoSession(eventObserver.eventListener event,String mSessionID,AppCompatActivity mContext, GeckoView pGeckoView){

        this.mGeckoView = pGeckoView;
        this.mContext = mContext;
        this.mSessionID = mSessionID;

        setProgressDelegate(this);
        setHistoryDelegate(this);
        setNavigationDelegate(this);
        setContentDelegate(this);
        setAutoFillDelegate();
        setPermissionDelegate(this);
        mDownloadManager = new geckoDownloadManager();
        setPromptDelegate(new geckoPromptView(mContext));
        this.event = event;
    }

    public void onSetInitializeFromStartup(){
        mIsLoaded = true;
    }

    public boolean onGetInitializeFromStartup(){
        return mIsLoaded;
    }

    public void onValidateInitializeFromStartup(){
        if(!mIsLoaded){
            mIsLoaded = true;
            initURL(mCurrentURL);
            loadUri(mCurrentURL);
        }
    }

    void onFileUploadRequest(int resultCode, Intent data){

        geckoPromptView mPromptDelegate = (geckoPromptView)getPromptDelegate();
        Objects.requireNonNull(mPromptDelegate).onFileCallbackResult(resultCode,data);
    }

    void initURL(String url){
        if(mIsLoaded){
            isPageLoading = true;
            mCurrentURL = url;
            mCurrentTitle = mCurrentURL;

            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle), enums.etype.on_update_suggestion);
            if(!url.equals("about:blank") && !url.equals("about:config"))
            {
                mProgress = 5;
                event.invokeObserver(Arrays.asList(5, mSessionID), enums.etype.progress_update);
            }
            m_current_url_id = -1;
        }
    }

    /*Scroll Delegate*/

    /*Autofill Delegate*/

    public void setAutoFillDelegate(){
        this.setAutofillDelegate(new AndroidAutofillDelegate());
    }

    private class AndroidAutofillDelegate implements Autofill.Delegate {

        private Rect displayRectForId(@NonNull final GeckoSession session,
                                      @NonNull final Autofill.Node node) {
            final Matrix matrix = new Matrix();
            final RectF rectF = new RectF(node.getDimensions());
            session.getPageToScreenMatrix(matrix);
            matrix.mapRect(rectF);

            final Rect screenRect = new Rect();
            rectF.roundOut(screenRect);
            return screenRect;
        }

        @Override
        public void onAutofill(@NonNull final GeckoSession session,
                               final int notification,
                               final Autofill.Node node) {
            ThreadUtils.assertOnUiThread();
            if (Build.VERSION.SDK_INT < 26) {
                return;
            }

            final AutofillManager manager =
                    mContext.getSystemService(AutofillManager.class);
            if (manager == null) {
                return;
            }

            switch (notification) {
                case Autofill.Notify.SESSION_STARTED:
                case Autofill.Notify.SESSION_CANCELED:
                    manager.cancel();
                    break;
                case Autofill.Notify.SESSION_COMMITTED:
                    manager.commit();
                    break;
                case Autofill.Notify.NODE_FOCUSED:
                    manager.notifyViewEntered(
                            mGeckoView, node.getId(),
                            displayRectForId(session, node));
                    break;
                case Autofill.Notify.NODE_BLURRED:
                    manager.notifyViewExited(mGeckoView, node.getId());
                    break;
                case Autofill.Notify.NODE_UPDATED:
                    manager.notifyValueChanged(
                            mGeckoView,
                            node.getId(),
                            AutofillValue.forText(node.getValue()));
                    break;
                case Autofill.Notify.NODE_ADDED:
                case Autofill.Notify.NODE_REMOVED:
                    break;
            }
        }
    }
    /*Progress Delegate*/

    @Override
    public void onPageStart(@NonNull GeckoSession var1, @NonNull String var2) {
        mCanGoBack = true;
        if(mIsLoaded){
            if(!isPageLoading){
                mCurrentTitle = "loading";
                m_current_url_id = -1;
            }
            isPageLoading = true;
            if(!var2.equals("about:blank") && !mCurrentTitle.equals("loading")){
                mProgress = 5;
            }
        }
    }

    @UiThread
    public void onPageStop(@NonNull GeckoSession var1, boolean var2) {
        if(var2){
            event.invokeObserver(Arrays.asList("",mSessionID,mCurrentTitle, m_current_url_id), dataEnums.eTabCommands.M_UPDATE_PIXEL);
        }
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

    public void onRedrawPixel(){
        event.invokeObserver(Arrays.asList("",mSessionID,mCurrentTitle, m_current_url_id), dataEnums.eTabCommands.M_UPDATE_PIXEL);
    }

    /*History Delegate*/
    @Override
    public GeckoResult<Boolean> onVisited(@NonNull GeckoSession var1, @NonNull String var2, @Nullable String var3, int var4) {
        if(var4==3 || var4==5 || var4==1){
            event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_url_load);
            m_current_url_id = (int)event.invokeObserver(Arrays.asList(var2,mSessionID,mCurrentTitle, m_current_url_id), enums.etype.on_update_history);
            event.invokeObserver(Arrays.asList(var2,mSessionID,mCurrentTitle, m_current_url_id), dataEnums.eTabCommands.M_UPDATE_PIXEL);
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

        if(!mIsLoaded){
            return;
        }

        String newUrl = Objects.requireNonNull(var2).split("#")[0];
        if(!mCurrentTitle.equals("loading")){
            m_current_url_id = (int)event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id), enums.etype.on_update_history);
            event.invokeObserver(Arrays.asList(var2,mSessionID,mCurrentTitle, m_current_url_id), dataEnums.eTabCommands.M_UPDATE_PIXEL);
        }
        mCurrentURL = newUrl;

        if (!var2.equals("about:blank"))
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
        else if(var1.uri.equals("about:blank") && mIsLoaded){
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
        if(var2!=null && !var2.equals(strings.GENERIC_EMPTY_STR) && var2.length()>2 && !var2.equals("about:blank") && mIsLoaded){
            mCurrentTitle = var2;
            m_current_url_id = (int)event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle, m_current_url_id), enums.etype.on_update_history);
            event.invokeObserver(Arrays.asList(var2,mSessionID,mCurrentTitle, m_current_url_id), dataEnums.eTabCommands.M_UPDATE_PIXEL);
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle), enums.etype.ON_UPDATE_TAB_TITLE);
        }
    }

    @Override
    public void onFullScreen(@NonNull GeckoSession var1, boolean var2) {
        mFullScreen = var2;
        event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_full_screen);
    }

    public void onContextMenu(@NonNull GeckoSession var1, int var2, int var3, @NonNull GeckoSession.ContentDelegate.ContextElement var4) {

        String title = strings.GENERIC_EMPTY_STR;
        if(var4.title!=null){
            title = var4.title;
        }
        if(var4.type!=0){
            if(var4.linkUri!=null){
                event.invokeObserver(Arrays.asList(var4.linkUri,mSessionID,var4.srcUri,title), enums.eMessageEnums.M_LONG_PRESS_WITH_LINK);
            }
            else {
                event.invokeObserver(Arrays.asList(var4.srcUri,mSessionID,title), enums.etype.on_long_press);
            }
        }
        else{
            event.invokeObserver(Arrays.asList(var4.linkUri,mSessionID,title), enums.eMessageEnums.M_LONG_PRESS_URL);
        }
    }

    /*Permission Delegate*/

    @Override
    public void onContentPermissionRequest(final GeckoSession session, final String uri,
                                           final int type, final Callback callback) {
        if (PERMISSION_AUTOPLAY_AUDIBLE == type || PERMISSION_AUTOPLAY_INAUDIBLE == type) {
            if (!status.sAutoPlay) {
                callback.reject();
            } else {
                callback.grant();
            }
        }else {
            callback.reject();
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

            String filetype;
            String filename;

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
                intent.setAction(android.content.Intent.ACTION_VIEW);
                Uri uri_temp = FileProvider.getUriForFile(mContext,mContext.getString(R.string.GENERAL_FILE_PROVIDER_AUTHORITY), file);
                intent.setDataAndType(uri_temp, (mimetype + "/*"));

                List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    mUriPermission = uri_temp;
                    mContext.grantUriPermission(packageName, uri_temp, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

                String channel_id = createNotificationChannel(mContext);
                assert channel_id != null;
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, channel_id)
                        .setSmallIcon(R.xml.ic_download)
                        .setContentTitle(filename)
                        .setContentIntent(pIntent);

                notificationBuilder.setAutoCancel(true);

                int notificationId = 85851;
                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId, notificationBuilder.build());

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

    /*Helper Methods*/

    void toogleUserAgent(){
        if(getSettings().getUserAgentMode()==USER_AGENT_MODE_DESKTOP){
            getSettings().setUserAgentMode(USER_AGENT_MODE_MOBILE);
        }else {
            getSettings().setUserAgentMode(USER_AGENT_MODE_DESKTOP);
        }

    }

    int getUserAgentMode(){
        return getSettings().getUserAgentMode();
    }

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

    public void setURL(String pURL){
        mCurrentURL = pURL;
    }

    boolean canGoBack(){
        return mCanGoBack;
    }

    boolean canGoForward(){
        return mCanGoForward;
    }

    public String getSessionID(){
        return mSessionID;
    }

    public void setSessionID(String pSession){
        mSessionID = pSession;
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

    public void findInPage(String pQuery, int pDirection){
        new Thread(){
            public void run(){
                try {
                    FinderResult mFinder = getFinder().find(pQuery, pDirection).poll(600);
                    if(mFinder!=null){
                        event.invokeObserver(Arrays.asList(mFinder.total, mFinder.current), enums.etype.FINDER_RESULT_CALLBACK);
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }.start();
    }

    void goBackSession(){

        if(mHistoryList!=null){
            int index = mHistoryList.getCurrentIndex()-1;

            if(mHistoryList!=null && index>=0 && index<mHistoryList.size()){
                event.invokeObserver(Arrays.asList(mHistoryList.get(index).getUri(),mSessionID), enums.etype.start_proxy);
                new Handler().postDelayed(this::goBack, 100);
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
                new Handler().postDelayed(this::goForward, 100);
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
            event.invokeObserver(Arrays.asList(mCurrentURL,mSessionID,mCurrentTitle), enums.eMessageEnums.M_RATE_APPLICATION);
        }
        rateCount+=1;
   }

}
