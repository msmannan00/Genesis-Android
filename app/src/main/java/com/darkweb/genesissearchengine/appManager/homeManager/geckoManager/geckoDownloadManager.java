package com.darkweb.genesissearchengine.appManager.homeManager.geckoManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.eventObserver;

import org.mozilla.geckoview.WebResponse;

import java.util.Arrays;

import mozilla.components.support.utils.DownloadUtils;

class geckoDownloadManager
{
    private Uri downloadURL;
    private String downloadFile = strings.GENERIC_EMPTY_STR;

    geckoDownloadManager(){

    }

    void downloadFile(WebResponse response, geckoSession session, AppCompatActivity context, eventObserver.eventListener event) {
        session
                .getUserAgent()
                .accept(userAgent -> downloadFile(response, userAgent,context,session,event),
                        exception -> {
                            throw new IllegalStateException("Could not get UserAgent string.");
                        });
    }

    private void downloadFile(WebResponse response, String userAgent, AppCompatActivity context, geckoSession session, eventObserver.eventListener event) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    3);
            return;
        }

        try{
            String mFileName = DownloadUtils.guessFileName(response.headers.get("Content-Disposition"),"",response.uri,null);
            downloadURL = Uri.parse(response.uri);
            downloadFile = mFileName;
        }catch (Exception ex){
            ex.printStackTrace();
            Log.i("sadsad",ex.getMessage());
        }

        event.invokeObserver(Arrays.asList(0,session.getSessionID()), enums.etype.progress_update);
        event.invokeObserver(Arrays.asList(downloadFile.toString(),session.getSessionID(),downloadURL.toString()), enums.etype.download_file_popup);
    }

    Uri getDownloadURL(){
        return downloadURL;
    }

    String getDownloadFile(){
        return downloadFile;
    }

}
