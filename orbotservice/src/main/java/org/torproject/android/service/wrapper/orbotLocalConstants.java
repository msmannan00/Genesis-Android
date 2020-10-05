package org.torproject.android.service.wrapper;

import android.content.Context;
import java.lang.ref.WeakReference;

public class orbotLocalConstants
{
    public static String tor_logs_status = "Loading...";
    public static boolean sIsTorInitialized = true;
    public static int sNotificationStatus = 0;
    public static WeakReference<Context> sHomeContext;
    public static String bridges = "";
    public static boolean sIsManualBridge = false;
    public static boolean sNetworkState = true;
}
