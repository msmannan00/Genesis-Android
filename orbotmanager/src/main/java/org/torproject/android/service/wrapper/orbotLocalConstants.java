package org.torproject.android.service.wrapper;

import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class orbotLocalConstants
{
    public static ArrayList<logRowModel> mTorLogsHistory = new ArrayList<>();
    public static String mTorLogsStatus = "Loading...";
    public static boolean mIsTorInitialized = false;
    public static boolean mForcedQuit = false;
    public static int mNotificationStatus = 0;
    public static WeakReference<Context> mHomeContext;
    public static Intent mHomeIntent = null;
    public static String mBridges = "";
    public static boolean mIsManualBridge = false;
    public static String mManualBridgeType = "";
    public static boolean mNetworkState = true;
    public static boolean mAppStarted = false;
    public static String mBridgesDefault = "";
    public static boolean mStartTriggered = false;

    public static int mHTTPPort = 8118;
    public static int mSOCKSPort = -1;
}
