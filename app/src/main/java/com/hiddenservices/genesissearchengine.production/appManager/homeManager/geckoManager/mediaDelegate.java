package com.hiddenservices.genesissearchengine.production.appManager.homeManager.geckoManager;


import static com.hiddenservices.genesissearchengine.production.appManager.homeManager.geckoManager.geckoPromptView.LOGTAG;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;

import org.mozilla.geckoview.GeckoSession;

class mediaDelegate implements GeckoSession.MediaDelegate {
    private Integer mLastNotificationId = 100;
    private Integer mNotificationId;
    private final Activity mActivity;
    private Context mContext;

    public mediaDelegate(Activity activity, Context pContext) {
        mActivity = activity;
        mContext = pContext;
    }

    @Override
    public void onRecordingStatusChanged(@NonNull GeckoSession session, RecordingDevice[] devices) {
        String message;
        int icon;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mActivity);
        RecordingDevice camera = null;
        RecordingDevice microphone = null;

        for (RecordingDevice device : devices) {
            if (device.type == RecordingDevice.Type.CAMERA) {
                camera = device;
            } else if (device.type == RecordingDevice.Type.MICROPHONE) {
                microphone = device;
            }
        }
        if (camera != null && microphone != null) {
            Log.d(LOGTAG, "ExampleDeviceDelegate:onRecordingDeviceEvent display alert_mic_camera");
            message = "Microphone and Camera is on";
        } else if (camera != null) {
            Log.d(LOGTAG, "ExampleDeviceDelegate:onRecordingDeviceEvent display alert_camera");
            message = "Camera is on";
        } else if (microphone != null) {
            Log.d(LOGTAG, "ExampleDeviceDelegate:onRecordingDeviceEvent display alert_mic");
            message = "Microphone is on";
        } else {
            Log.d(LOGTAG, "ExampleDeviceDelegate:onRecordingDeviceEvent dismiss any notifications");
            if (mNotificationId != null) {
                notificationManager.cancel(mNotificationId);
                mNotificationId = null;
            }
            return;
        }
        if (mNotificationId == null) {
            mNotificationId = ++mLastNotificationId;
        }

        Intent intent = new Intent(mActivity, mediaDelegate.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(mActivity.getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mActivity.getApplicationContext(), "GeckoChannel")
                        .setContentTitle("Genesis Browser")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE);

        notificationManager.notify(mNotificationId, builder.build());
    }
}