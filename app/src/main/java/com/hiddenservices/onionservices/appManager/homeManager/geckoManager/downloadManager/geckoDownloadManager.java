package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.downloadManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.geckoSession;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.eventObserver;
import org.mozilla.geckoview.WebResponse;
import java.util.Arrays;
import mozilla.components.support.utils.DownloadUtils;

public class geckoDownloadManager {
    private Uri downloadURL;
    private String downloadFile = strings.GENERIC_EMPTY_STR;

    public void downloadFile(WebResponse response, geckoSession session, AppCompatActivity context, eventObserver.eventListener event) {
        session.getUserAgent().accept(userAgent -> downloadFile(response, context, session, event),
                exception -> {
                    throw new IllegalStateException("Could not get UserAgent string.");
                });
    }

    private void downloadFile(WebResponse response, AppCompatActivity context, geckoSession session, eventObserver.eventListener event) {
        if ( Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1 && ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    3);
            return;
        }

        try {
            String mFileName = DownloadUtils.guessFileName(response.headers.get("Content-Disposition"), "", response.uri, null);
            String murl = response.uri;
            if (!murl.startsWith("http")) {
                murl = "https://" + murl;
            }
            downloadURL = Uri.parse(murl);
            downloadFile = mFileName;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        event.invokeObserver(Arrays.asList(0, session.getSessionID()), homeEnums.eGeckoCallback.PROGRESS_UPDATE);
        event.invokeObserver(Arrays.asList(downloadFile, session.getSessionID(), downloadURL), homeEnums.eGeckoCallback.DOWNLOAD_FILE_POPUP);
    }

    public Uri getDownloadURL() {
        return downloadURL;
    }

    public String getDownloadFile() {
        return downloadFile;
    }

}
