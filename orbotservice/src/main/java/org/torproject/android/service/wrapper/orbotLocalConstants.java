package org.torproject.android.service.wrapper;

import android.content.Context;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class orbotLocalConstants
{
    public static ArrayList<String> mTorLogsHistory = new ArrayList<>();
    public static String mTorLogsStatus = "Loading...";
    public static boolean mIsTorInitialized = false;
    public static int mNotificationStatus = 0;
    public static WeakReference<Context> mHomeContext;
    public static String mBridges = "";
    public static boolean mIsManualBridge = false;
    public static boolean mNetworkState = true;
}
