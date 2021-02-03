package com.darkweb.genesissearchengine.appManager.homeManager.geckoManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import org.mozilla.geckoview.GeckoSession;
import java.util.Arrays;

class geckoDownloadManager
{
    private Uri downloadURL;
    private String downloadFile = strings.GENERIC_EMPTY_STR;

    geckoDownloadManager(){

    }

    void downloadFile(GeckoSession.WebResponseInfo response, geckoSession session, AppCompatActivity context, eventObserver.eventListener event) {
        session
                .getUserAgent()
                .accept(userAgent -> downloadFile(response, userAgent,context,session,event),
                        exception -> {
                            throw new IllegalStateException("Could not get UserAgent string.");
                        });
    }

    private void downloadFile(GeckoSession.WebResponseInfo response, String userAgent, AppCompatActivity context, geckoSession session, eventObserver.eventListener event) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    3);
            return;
        }


        downloadURL = Uri.parse(response.uri);
        downloadFile = response.filename != null ? response.filename : downloadURL.getLastPathSegment();

        event.invokeObserver(Arrays.asList(0,session.getSessionID()), enums.etype.progress_update);
        event.invokeObserver(Arrays.asList(downloadFile,session.getSessionID(),downloadURL), enums.etype.download_file_popup);
    }

    Uri getDownloadURL(){
        return downloadURL;
    }

    String getDownloadFile(){
        return downloadFile;
    }

}
