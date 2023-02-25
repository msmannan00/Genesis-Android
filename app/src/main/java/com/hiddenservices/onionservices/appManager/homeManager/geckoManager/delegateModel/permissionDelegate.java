package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.hiddenservices.onionservices.R;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.geckoSession;
import java.lang.ref.WeakReference;
import java.util.Locale;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoSession.PermissionDelegate;

public class permissionDelegate implements PermissionDelegate {

    public int androidPermissionRequestCode = 1;
    private WeakReference<AppCompatActivity> mContext;
    private geckoSession mGeckoSession;

    public permissionDelegate(WeakReference<AppCompatActivity> pContext, geckoSession pGeckoSession){
        this.mContext = pContext;
        this.mGeckoSession = pGeckoSession;
    }

    @Override
    public void onAndroidPermissionsRequest(final GeckoSession session, final String[] permissions, final Callback callback) {
        if (Build.VERSION.SDK_INT >= 23) {
            // requestPermissions was introduced in API 23.
            mContext.get().requestPermissions(permissions, androidPermissionRequestCode);
        } else {
            callback.grant();
        }
    }


    private String[] normalizeMediaName(final MediaSource[] sources) {
        if (sources == null) {
            return null;
        }

        String[] res = new String[sources.length];
        for (int i = 0; i < sources.length; i++) {
            final int mediaSource = sources[i].source;
            final String name = sources[i].name;
            if (MediaSource.SOURCE_CAMERA == mediaSource) {
                if (name.toLowerCase(Locale.ROOT).contains("front")) {
                    res[i] = "Front Camera";
                } else {
                    res[i] = "Back Camera";
                }
            } else if (!name.isEmpty()) {
                res[i] = name;
            } else if (MediaSource.SOURCE_MICROPHONE == mediaSource) {
                res[i] = "Microphone";
            } else {
                res[i] = "Other";
            }
        }

        return res;
    }

    @Override
    public void onMediaPermissionRequest(
            final GeckoSession session,
            final String uri,
            final MediaSource[] video,
            final MediaSource[] audio,
            final MediaCallback callback) {
        // If we don't have device permissions at this point, just automatically reject the request
        // as we will have already have requested device permissions before getting to this point
        // and if we've reached here and we don't have permissions then that means that the user
        // denied them.
        if ((audio != null
                && ContextCompat.checkSelfPermission(
                mContext.get(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
                || (video != null
                && ContextCompat.checkSelfPermission(
                mContext.get(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)) {
            callback.reject();
            return;
        }

        final String host = Uri.parse(uri).getAuthority();
        final String title;
        if (audio == null) {
            title = "Request Video";
        } else if (video == null) {
            title = "Request Audio";
        } else {
            title = "Request Media";
        }

        String[] videoNames = normalizeMediaName(video);
        String[] audioNames = normalizeMediaName(audio);

        final promptDelegate prompt =
                (promptDelegate) mGeckoSession.getPromptDelegate();
        prompt.onMediaPrompt(session, title, video, audio, videoNames, audioNames, callback);
    }
}