package org.torproject.android.service.wrapper;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class orbotLocalConstants
{
    public static String tor_logs_status = "Loading...";
    public static boolean sIsTorInitialized = false;
    public static int sNotificationStatus = 0;
    public static WeakReference<Context> sHomeContext;
}
