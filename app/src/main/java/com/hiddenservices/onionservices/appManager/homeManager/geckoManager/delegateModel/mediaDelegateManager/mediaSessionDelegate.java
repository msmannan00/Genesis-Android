package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel.mediaDelegateManager;

import static com.hiddenservices.onionservices.constants.strings.GENERIC_EMPTY_STR;
import android.graphics.Bitmap;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.dataModel.geckoDataModel;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.MediaSession;
import java.lang.ref.WeakReference;

public class mediaSessionDelegate implements MediaSession.Delegate{

    /*Private Variables*/

    private MediaSession mMediaSession = null;
    private final mediaDelegate mMediaDelegate;
    private final WeakReference<AppCompatActivity> mContext;
    private final geckoDataModel mGeckoDataModel;
    private boolean mIsRunning = false;
    private boolean isPaused = false;

    private Bitmap mMediaImage;
    private String mMediaTitle = GENERIC_EMPTY_STR;

    // Handler for posting delayed tasks
    private final Handler mHandler = new Handler();

    /*Initializations*/
    public mediaSessionDelegate(WeakReference<AppCompatActivity> pContext, geckoDataModel pGeckoDataModel, mediaDelegate pMediaDelegate){
        this.mContext = pContext;
        this.mMediaDelegate = pMediaDelegate;
        this.mGeckoDataModel = pGeckoDataModel;
    }

    /*Local Listeners*/

    @Override
    public void onActivated(@NonNull GeckoSession session, @NonNull MediaSession mediaSession) {
        MediaSession.Delegate.super.onActivated(session, mediaSession);
        mIsRunning = true;
        mMediaSession = mediaSession;
    }

    @Override
    public void onDeactivated(@NonNull GeckoSession session, @NonNull MediaSession mediaSession) {
        MediaSession.Delegate.super.onPause(session, mediaSession);
        mMediaDelegate.onHideDefaultNotification();
    }

    @Override
    public void onMetadata(@NonNull GeckoSession session, @NonNull MediaSession mediaSession, @NonNull MediaSession.Metadata meta) {
        mMediaTitle = meta.title;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(() -> new Thread(() -> {
            try {
                assert meta.artwork != null;
                Bitmap bitmap = meta.artwork.getBitmap(250).poll(2500);
                if (bitmap != null) {
                    mHandler.post(() -> mMediaImage = bitmap);
                    mMediaDelegate.showNotification(mContext.get(), mMediaTitle, helperMethod.getHost(mGeckoDataModel.mCurrentURL), mMediaImage, !isPaused);
                    MediaSession.Delegate.super.onMetadata(session, mediaSession, meta);
                }
            } catch (Throwable ignored) {
            }
        }).start(), 2000);
    }

    @Override
    public void onFeatures(@NonNull GeckoSession session, @NonNull MediaSession mediaSession, long features) {
        MediaSession.Delegate.super.onFeatures(session, mediaSession, features);
    }

    @Override
    public void onPlay(@NonNull GeckoSession session, @NonNull MediaSession mediaSession) {
        isPaused = false;
        MediaSession.Delegate.super.onPlay(session, mediaSession);
        mMediaDelegate.showNotification(this.mContext.get(), mMediaTitle, helperMethod.getHost(mGeckoDataModel.mCurrentURL), mMediaImage, true);
    }

    @Override
    public void onPause(@NonNull GeckoSession session, @NonNull MediaSession mediaSession) {
        if(status.sSettingIsAppInBackground){
            mediaSession.play();
        }else {
            isPaused = true;
            MediaSession.Delegate.super.onPause(session, mediaSession);
            mMediaDelegate.showNotification(this.mContext.get(), mMediaTitle, helperMethod.getHost(mGeckoDataModel.mCurrentURL), mMediaImage, false);
        }
    }

    @Override
    public void onStop(@NonNull GeckoSession session, @NonNull MediaSession mediaSession) {
        MediaSession.Delegate.super.onStop(session, mediaSession);
        mMediaDelegate.showNotification(this.mContext.get(), mMediaTitle, helperMethod.getHost(mGeckoDataModel.mCurrentURL), mMediaImage, false);
    }

    @Override
    public void onPositionState(@NonNull GeckoSession session, @NonNull MediaSession mediaSession, @NonNull MediaSession.PositionState state) {
        MediaSession.Delegate.super.onPositionState(session, mediaSession, state);
    }

    @Override
    public void onFullscreen(@NonNull GeckoSession session, @NonNull MediaSession mediaSession, boolean enabled, @Nullable @org.jetbrains.annotations.Nullable MediaSession.ElementMetadata meta) {
        MediaSession.Delegate.super.onFullscreen(session, mediaSession, enabled, meta);
    }

    public void resetMediaImage() {
        mMediaImage = null;
    }

    /*Triggers*/

    public Object onTrigger(enums.MediaController pCommands) {
        if (mMediaSession != null) {
            if (pCommands.equals(enums.MediaController.PLAY)) {
                mMediaDelegate.showNotification(this.mContext.get(), mMediaTitle, helperMethod.getHost(mGeckoDataModel.mCurrentURL), mMediaImage, true);
                mMediaSession.play();
            } else if (pCommands.equals(enums.MediaController.PAUSE)) {
                mMediaSession.pause();
                if (mIsRunning) {
                    mMediaDelegate.showNotification(this.mContext.get(), mMediaTitle, helperMethod.getHost(mGeckoDataModel.mCurrentURL), mMediaImage, false);
                }
            } else if (pCommands.equals(enums.MediaController.STOP)) {
                mMediaSession.stop();
            } else if (pCommands.equals(enums.MediaController.DESTROY)) {
                mMediaSession.stop();
                mMediaDelegate.onHideDefaultNotification();
                mIsRunning = false;
            } else if (pCommands.equals(enums.MediaController.SKIP_BACKWARD)) {
                mMediaSession.previousTrack();
            } else if (pCommands.equals(enums.MediaController.SKIP_FORWARD)) {
                mMediaSession.nextTrack();
            } else if (pCommands.equals(enums.MediaController.IS_MEDIA_RUNNING)) {
                return mIsRunning;
            } else if (pCommands.equals(enums.MediaController.RESET_MEDIA_IMAGE)) {
                resetMediaImage();
            }
        } else {
            mMediaDelegate.onHideDefaultNotification();
        }
        return null;
    }

}
