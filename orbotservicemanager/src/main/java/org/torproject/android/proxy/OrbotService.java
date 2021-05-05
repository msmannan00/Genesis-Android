/* Copyright (c) 2009-2011, Nathan Freitas, Orbot / The Guardian Project - https://guardianproject.info/apps/orbot */
/* See LICENSE for licensing information */
/*
 * Code for iptables binary management taken from DroidWall GPLv3
 * Copyright (C) 2009-2010  Rodrigo Zechin Rosauro
 */

package org.torproject.android.proxy;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jaredrummler.android.shell.CommandResult;

import net.freehaven.tor.control.ConfigEntry;
import net.freehaven.tor.control.TorControlCommands;
import net.freehaven.tor.control.TorControlConnection;

import org.apache.commons.io.FileUtils;
import org.torproject.android.service.R;
import org.torproject.android.service.wrapper.localHelperMethod;
import org.torproject.android.service.wrapper.logRowModel;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import org.torproject.android.service.util.CustomShell;
import org.torproject.android.service.util.CustomTorResourceInstaller;
import org.torproject.android.service.util.DummyActivity;
import org.torproject.android.service.util.Prefs;
import org.torproject.android.service.util.TorServiceUtils;
import org.torproject.android.service.util.Utils;
import org.torproject.android.service.vpn.OrbotVpnManager;
import org.torproject.android.service.vpn.VpnPrefs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import info.pluggabletransports.dispatch.util.TransportListener;
import info.pluggabletransports.dispatch.util.TransportManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class OrbotService extends VpnService implements TorServiceConstants, OrbotConstants {

    public final static String BINARY_TOR_VERSION = org.torproject.android.binary.TorServiceConstants.BINARY_TOR_VERSION;
    private final static int CONTROL_SOCKET_TIMEOUT = 60000;
    static final int NOTIFY_ID = 1;
    private static final int ERROR_NOTIFY_ID = 3;
    private static final int HS_NOTIFY_ID = 4;
    private static final Uri HS_CONTENT_URI = Uri.parse("content://org.torproject.android.ui.hiddenservices.providers/hs");
    private static final Uri COOKIE_CONTENT_URI = Uri.parse("content://org.torproject.android.ui.hiddenservices.providers.cookie/cookie");
    private final static String NOTIFICATION_CHANNEL_ID = "orbot_channel_1";
    private final static String RESET_STRING = "=\"\"";
    public static int mPortSOCKS = -1;
    public static int mPortHTTP = -1;
    public static int mPortDns = TOR_DNS_PORT_DEFAULT;
    public static int mPortTrans = TOR_TRANSPROXY_PORT_DEFAULT;
    public static File appBinHome;
    public static File appCacheHome;
    public static File fileTor;
    public static File fileObfsclient;
    public boolean isAppClosed = false;
    public static File fileTorRc;
    boolean mIsLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    TorEventHandler mEventHandler;
    OrbotVpnManager mVpnManager;
    Handler mHandler;
    //we should randomly sort alBridges so we don't have the same bridge order each time
    Random bridgeSelectRandom = new Random(System.nanoTime());
    ActionBroadcastReceiver mActionBroadcastReceiver;
    private String mCurrentStatus = STATUS_OFF;
    private TorControlConnection conn = null;
    private int mLastProcessId = -1;
    private ArrayList<String> configBuffer = null;
    private ArrayList<String> resetBuffer = null;
    private File fileControlPort, filePid;
    private NotificationManager mNotificationManager = null;
    private NotificationCompat.Builder mNotifyBuilder;
    private boolean mNotificationShowing = false;
    private final ExecutorService mExecutor = Executors.newFixedThreadPool(3);
    private File mHSBasePath;
    private ArrayList<Bridge> alBridges = null;
    private final String[] hsProjection = new String[]{
            HiddenService._ID,
            HiddenService.NAME,
            HiddenService.DOMAIN,
            HiddenService.PORT,
            HiddenService.AUTH_COOKIE,
            HiddenService.AUTH_COOKIE_VALUE,
            HiddenService.ONION_PORT,
            HiddenService.ENABLED};
    private final String[] cookieProjection = new String[]{
            ClientCookie._ID,
            ClientCookie.DOMAIN,
            ClientCookie.AUTH_COOKIE_VALUE,
            ClientCookie.ENABLED};

    /**
     * @param bridgeList bridges that were manually entered into Orbot settings
     * @return Array with each bridge as an element, no whitespace entries see issue #289...
     */
    private static String[] parseBridgesFromSettings(String bridgeList) {
        // this regex replaces lines that only contain whitespace with an empty String
        bridgeList = bridgeList.trim().replaceAll("(?m)^[ \t]*\r?\n", "");
        return bridgeList.split("\\n");
    }

    public void debug(String msg) {

        Log.d(OrbotConstants.TAG, msg);

        if (Prefs.useDebugLogging()) {
            sendCallbackLogMessage(msg);

        }
    }

    public int getNotifyId() {
        return NOTIFY_ID;
    }

    public void logException(String msg, Exception e) {
        if (Prefs.useDebugLogging()) {
            Log.e(OrbotConstants.TAG, msg, e);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));

            sendCallbackLogMessage(msg + '\n' + new String(baos.toByteArray()));

        } else
            sendCallbackLogMessage(msg);

    }

    private void showConnectedToTorNetworkNotification() {
        showToolbarNotification(getString(R.string.status_activated), NOTIFY_ID, R.drawable.ic_stat_tor);
    }

    private boolean findExistingTorDaemon() {
        try {
            mLastProcessId = initControlConnection(3, true);

            if (mLastProcessId != -1 && conn != null) {
                sendCallbackLogMessage(getString(R.string.found_existing_tor_process));
                sendCallbackStatus(STATUS_ON);
                showConnectedToTorNetworkNotification();
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /* (non-Javadoc)
     * @see android.app.Service#onLowMemory()
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();

        logNotice("Low Memory Warning!");

    }

    private void clearNotifications() {
        if (mNotificationManager != null)
            mNotificationManager.cancelAll();

        if (mEventHandler != null)
            mEventHandler.getNodes().clear();

        mNotificationShowing = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence name = getString(R.string.app_name); // The user-visible name of the channel.
        String description = getString(R.string.app_description); // The user-visible description of the channel.
        NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.enableLights(false);
        mChannel.enableVibration(false);
        mChannel.setShowBadge(false);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        mNotificationManager.createNotificationChannel(mChannel);
    }

    @SuppressLint({"NewApi", "RestrictedApi"})
    public void showToolbarNotification(String notifyMsg, int notifyType, int icon) {
        if(isAppClosed){
            return;
        }
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(getPackageName());
        PendingIntent pendIntent = PendingIntent.getActivity(OrbotService.this, 0, intent, 0);

        if (mNotifyBuilder == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_stat_tor)
                    .setContentIntent(pendIntent)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(Prefs.persistNotifications());
        }

        mNotifyBuilder.mActions.clear();
        if (conn != null) {
            Intent intentRefresh = new Intent(CMD_NEWNYM);
            PendingIntent pendingIntentNewNym = PendingIntent.getBroadcast(this, 0, intentRefresh, PendingIntent.FLAG_UPDATE_CURRENT);
            mNotifyBuilder.addAction(R.drawable.ic_stat_starting_tor_logo, getString(R.string.menu_new_identity), pendingIntentNewNym);
        }

        Intent intentRefresh = new Intent(CMD_SETTING);
        PendingIntent pendingIntentNewNym = PendingIntent.getBroadcast(this, 0, intentRefresh, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifyBuilder.addAction(0, "Notification Settings", pendingIntentNewNym);

        mNotifyBuilder.setContentText(notifyMsg)
                .setSmallIcon(icon)
                .setTicker(notifyType != NOTIFY_ID ? notifyMsg : null);

        if (!Prefs.persistNotifications())
            mNotifyBuilder.setPriority(Notification.PRIORITY_LOW);

        Notification notification = mNotifyBuilder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFY_ID, notification);
        } else if (Prefs.persistNotifications() && (!mNotificationShowing)) {
            startForeground(NOTIFY_ID, notification);
            logNotice("Set background service to FOREGROUND");
        } else {
            mNotificationManager.notify(NOTIFY_ID, notification);
        }

        mNotificationShowing = true;
    }

    /* (non-Javadoc)
     * @see android.app.Service#onStart(android.content.Intent, int)
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        self = this;

        showToolbarNotification("", NOTIFY_ID, R.drawable.ic_stat_tor);

        if (intent != null)
            exec(new IncomingIntentRouter(intent));
        else
            Log.d(OrbotConstants.TAG, "Got null onStartCommand() intent");

        return Service.START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(OrbotConstants.TAG, "task removed");
        Intent intent = new Intent(this, DummyActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        try {
            isAppClosed = true;
            unregisterReceiver(mNetworkStateReceiver);
            unregisterReceiver(mActionBroadcastReceiver);

            stopTor();
            clearNotifications();
            killAllDaemons();
        } catch (Exception ex) {
            Log.i("", "");
        }

        super.onDestroy();
    }


    private void stopTor() {
        new Thread(this::stopTorAsync).start();
    }

    private void stopTorAsync() {
        Log.i("OrbotService", "stopTor");
        try {
            sendCallbackStatus(STATUS_STOPPING);
            sendCallbackLogMessage(getString(R.string.status_shutting_down));

            killAllDaemons();

            //stop the foreground priority and make sure to remove the persistant notification
            stopForeground(true);

            sendCallbackLogMessage(getString(R.string.status_disabled));
        } catch (Exception e) {
            logNotice("An error occured stopping Tor: " + e.getMessage());
            sendCallbackLogMessage(getString(R.string.something_bad_happened));
        }
        clearNotifications();
        sendCallbackStatus(STATUS_OFF);


    }

    private void killAllDaemons() throws Exception {

        if (conn != null) {
            logNotice("Using control port to shutdown Tor");

            try {
                logNotice("sending HALT signal to Tor process");
                conn.shutdownTor("SHUTDOWN");

            } catch (IOException e) {
                Log.d(OrbotConstants.TAG, "error shutting down Tor via connection", e);
            }

            conn = null;
        }
    }

    private void requestTorRereadConfig() {
        try {
            if (conn != null)
                conn.signal("HUP");

        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         // if that fails, try again using native utils
         try {
         killProcess(fileTor, "-1"); // this is -HUP
         } catch (Exception e) {
         e.printStackTrace();
         }**/
    }

    public void logNotice(String msg) {
        if (msg != null && msg.trim().length() > 0) {
            if (Prefs.useDebugLogging())
                Log.d(OrbotConstants.TAG, msg);

            sendCallbackLogMessage(msg);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mHandler = new Handler();

            appBinHome = getFilesDir();//getDir(TorServiceConstants.DIRECTORY_TOR_BINARY, Application.MODE_PRIVATE);
            if (!appBinHome.exists())
                appBinHome.mkdirs();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                appCacheHome = new File(getDataDir(), DIRECTORY_TOR_DATA);
            } else {
                appCacheHome = getDir(DIRECTORY_TOR_DATA, Application.MODE_PRIVATE);
            }

            if (!appCacheHome.exists())
                appCacheHome.mkdirs();


            debug("listing files in DataDirectory: " + appCacheHome.getAbsolutePath());
            Iterator<File> files = FileUtils.iterateFiles(appCacheHome, null, true);
            while (files.hasNext()) {
                File fileNext = files.next();
                debug(fileNext.getAbsolutePath()
                        + " length=" + fileNext.length()
                        + " rw=" + fileNext.canRead() + "/" + fileNext.canWrite()
                        + " lastMod=" + new Date(fileNext.lastModified()).toLocaleString()

                );
            }

            fileTorRc = new File(appBinHome, TORRC_ASSET_KEY);
            fileControlPort = new File(getFilesDir(), TOR_CONTROL_PORT_FILE);
            filePid = new File(getFilesDir(), TOR_PID_FILE);

            mHSBasePath = new File(
                    getFilesDir().getAbsolutePath(),
                    TorServiceConstants.HIDDEN_SERVICES_DIR
            );

            if (!mHSBasePath.isDirectory())
                mHSBasePath.mkdirs();

            mEventHandler = new TorEventHandler(this);

            if (mNotificationManager == null) {
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }

            IntentFilter mNetworkStateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetworkStateReceiver , mNetworkStateFilter);

            IntentFilter filter = new IntentFilter();
            filter.addAction(CMD_NEWNYM);
            filter.addAction(CMD_SETTING);
            filter.addAction(CMD_ACTIVE);
            mActionBroadcastReceiver = new ActionBroadcastReceiver();
            registerReceiver(mActionBroadcastReceiver, filter);

            if (Build.VERSION.SDK_INT >= 26)
                createNotificationChannel();

            torUpgradeAndConfig();

            pluggableTransportInstall();

            new Thread(() -> {
                try {
                    findExistingTorDaemon();
                } catch (Exception e) {
                    Log.e(OrbotConstants.TAG, "error onBind", e);
                    logNotice("error finding exiting process: " + e.toString());
                }

            }).start();

            try {
                mVpnManager = new OrbotVpnManager(this);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            //what error here
            Log.e(OrbotConstants.TAG, "Error installing Orbot binaries", e);
            logNotice("There was an error installing Orbot binaries");
        }

        Log.i("OrbotService", "onCreate end");
    }

    public String getCurrentStatus() {
        return mCurrentStatus;
    }

    private boolean pluggableTransportInstall() {

        fileObfsclient = new TransportManager() {
            @Override
            public void startTransportSync(TransportListener transportListener) {

            }
        }.installTransport(this, OBFSCLIENT_ASSET_KEY);

        if (fileObfsclient != null && fileObfsclient.exists()) {

            fileObfsclient.setReadable(true);
            fileObfsclient.setExecutable(true);
            fileObfsclient.setWritable(false);
            fileObfsclient.setWritable(true, true);

            return fileObfsclient.canExecute();
        }

        return false;
    }

    private boolean torUpgradeAndConfig() throws IOException, TimeoutException {

        SharedPreferences prefs = Prefs.getSharedPrefs(getApplicationContext());
        String version = prefs.getString(PREF_BINARY_TOR_VERSION_INSTALLED, null);

        logNotice("checking binary version: " + version);

        CustomTorResourceInstaller installer = new CustomTorResourceInstaller(this, appBinHome);
        logNotice("upgrading binaries to latest version: " + BINARY_TOR_VERSION);

        fileTor = installer.installResources();

        if (fileTor != null && fileTor.canExecute()) {
            prefs.edit().putString(PREF_BINARY_TOR_VERSION_INSTALLED, BINARY_TOR_VERSION).apply();

            fileTorRc = new File(appBinHome, "torrc");//installer.getTorrcFile();
            return fileTorRc.exists();
        }

        return false;
    }

    private File updateTorrcCustomFile() throws IOException, TimeoutException {
        SharedPreferences prefs = Prefs.getSharedPrefs(getApplicationContext());

        StringBuffer extraLines = new StringBuffer();

        extraLines.append("\n");
        extraLines.append("ControlPortWriteToFile").append(' ').append(fileControlPort.getCanonicalPath()).append('\n');

        extraLines.append("PidFile").append(' ').append(filePid.getCanonicalPath()).append('\n');

        extraLines.append("RunAsDaemon 1").append('\n');
        extraLines.append("AvoidDiskWrites 1").append('\n');

        String socksPortPref = prefs.getString(OrbotConstants.PREF_SOCKS, (TorServiceConstants.SOCKS_PROXY_PORT_DEFAULT));

        if (socksPortPref.indexOf(':') != -1)
            socksPortPref = socksPortPref.split(":")[1];

        socksPortPref = checkPortOrAuto(socksPortPref);

        String httpPortPref = prefs.getString(OrbotConstants.PREF_HTTP, (TorServiceConstants.HTTP_PROXY_PORT_DEFAULT));

        if (httpPortPref.indexOf(':') != -1)
            httpPortPref = httpPortPref.split(":")[1];

        httpPortPref = checkPortOrAuto(httpPortPref);

        String isolate = "";
        if (prefs.getBoolean(OrbotConstants.PREF_ISOLATE_DEST, false)) {
            isolate += " IsolateDestAddr ";
        }

        String ipv6Pref = "";

        if (prefs.getBoolean(OrbotConstants.PREF_PREFER_IPV6, true)) {
            ipv6Pref += " IPv6Traffic PreferIPv6 ";
        }

        if (prefs.getBoolean(OrbotConstants.PREF_DISABLE_IPV4, false)) {
            ipv6Pref += " IPv6Traffic NoIPv4Traffic ";
        }

        extraLines.append("SOCKSPort ").append(socksPortPref).append(isolate).append(ipv6Pref).append('\n');
        extraLines.append("SafeSocks 0").append('\n');
        extraLines.append("TestSocks 0").append('\n');

        if (Prefs.openProxyOnAllInterfaces())
            extraLines.append("SocksListenAddress 0.0.0.0").append('\n');


        extraLines.append("HTTPTunnelPort ").append(httpPortPref).append('\n');

        if (prefs.getBoolean(OrbotConstants.PREF_CONNECTION_PADDING, false)) {
            extraLines.append("ConnectionPadding 1").append('\n');
        }

        if (prefs.getBoolean(OrbotConstants.PREF_REDUCED_CONNECTION_PADDING, true)) {
            extraLines.append("ReducedConnectionPadding 1").append('\n');
        }

        if (prefs.getBoolean(OrbotConstants.PREF_CIRCUIT_PADDING, true)) {
            extraLines.append("CircuitPadding 1").append('\n');
        } else {
            extraLines.append("CircuitPadding 0").append('\n');
        }

        if (prefs.getBoolean(OrbotConstants.PREF_REDUCED_CIRCUIT_PADDING, true)) {
            extraLines.append("ReducedCircuitPadding 1").append('\n');
        }

        String transPort = prefs.getString("pref_transport", TorServiceConstants.TOR_TRANSPROXY_PORT_DEFAULT + "");
        String dnsPort = prefs.getString("pref_dnsport", TorServiceConstants.TOR_DNS_PORT_DEFAULT + "");

        extraLines.append("TransPort ").append(checkPortOrAuto(transPort)).append('\n');
        extraLines.append("DNSPort ").append(checkPortOrAuto(dnsPort)).append('\n');

        extraLines.append("VirtualAddrNetwork 10.192.0.0/10").append('\n');
        extraLines.append("AutomapHostsOnResolve 1").append('\n');

        extraLines.append("DormantClientTimeout 10 minutes").append('\n');
        // extraLines.append("DormantOnFirstStartup 0").append('\n');
        extraLines.append("DormantCanceledByStartup 1").append('\n');

        extraLines.append("DisableNetwork 0").append('\n');

        if (Prefs.useDebugLogging()) {
            extraLines.append("Log debug syslog").append('\n');
            extraLines.append("SafeLogging 0").append('\n');
        }

        extraLines = processSettingsImpl(extraLines);

        if (extraLines == null)
            return null;

        extraLines.append('\n');
        extraLines.append(prefs.getString("pref_custom_torrc", "")).append('\n');

        logNotice("updating torrc custom configuration...");

        debug("torrc.custom=" + extraLines.toString());

        File fileTorRcCustom = new File(fileTorRc.getAbsolutePath() + ".custom");
        boolean success = updateTorConfigCustom(fileTorRcCustom, extraLines.toString());

        if (success && fileTorRcCustom.exists()) {
            logNotice("success.");
            return fileTorRcCustom;
        } else
            return null;

    }

    private String checkPortOrAuto(String portString) {
        if (!portString.equalsIgnoreCase("auto")) {
            boolean isPortUsed = true;
            int port = Integer.parseInt(portString);

            while (isPortUsed) {
                isPortUsed = TorServiceUtils.isPortOpen("127.0.0.1", port, 500);

                if (isPortUsed) //the specified port is not available, so let Tor find one instead
                    port++;
            }


            return port + "";
        }

        return portString;

    }

    public boolean updateTorConfigCustom(File fileTorRcCustom, String extraLines) throws IOException, TimeoutException {
        FileWriter fos = new FileWriter(fileTorRcCustom, false);
        PrintWriter ps = new PrintWriter(fos);
        ps.print(extraLines);
        ps.flush();
        ps.close();
        return true;
    }

    /**
     * Send Orbot's status in reply to an
     * {@link TorServiceConstants#ACTION_START} {@link Intent}, targeted only to
     * the app that sent the initial request. If the user has disabled auto-
     * starts, the reply {@code ACTION_START Intent} will include the extra
     * {@link TorServiceConstants#STATUS_STARTS_DISABLED}
     */
    private void replyWithStatus(Intent startRequest) {
        String packageName = startRequest.getStringExtra(EXTRA_PACKAGE_NAME);

        Intent reply = new Intent(ACTION_STATUS);
        reply.putExtra(EXTRA_STATUS, mCurrentStatus);
        reply.putExtra(EXTRA_SOCKS_PROXY, "socks://127.0.0.1:" + mPortSOCKS);
        reply.putExtra(EXTRA_SOCKS_PROXY_HOST, "127.0.0.1");
        reply.putExtra(EXTRA_SOCKS_PROXY_PORT, mPortSOCKS);
        reply.putExtra(EXTRA_HTTP_PROXY, "http://127.0.0.1:" + mPortHTTP);
        reply.putExtra(EXTRA_HTTP_PROXY_HOST, "127.0.0.1");
        reply.putExtra(EXTRA_HTTP_PROXY_PORT, mPortHTTP);

        if (packageName != null) {
            reply.setPackage(packageName);
            sendBroadcast(reply);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(reply);

        if (mPortSOCKS != -1 && mPortHTTP != -1)
            sendCallbackPorts(mPortSOCKS, mPortHTTP, mPortDns, mPortTrans);

    }

    /**
     * The entire process for starting tor and related services is run from this method.
     */
    private void startTor() {

        String torProcId = null;

        try {
            if (conn != null) torProcId = conn.getInfo("process/pid");
        } catch (Exception e) {
        }

        try {

            // STATUS_STARTING is set in onCreate()
            if (mCurrentStatus == STATUS_STOPPING) {
                // these states should probably be handled better
                sendCallbackLogMessage("Ignoring start request, currently " + mCurrentStatus);
                return;
            } else if (mCurrentStatus == STATUS_ON && (torProcId != null)) {
                showConnectedToTorNetworkNotification();
                sendCallbackLogMessage("Ignoring start request, already started.");
                // setTorNetworkEnabled (true);

                return;
            }


            // make sure there are no stray daemons running
            killAllDaemons();

            SharedPreferences prefs = Prefs.getSharedPrefs(getApplicationContext());
            String version = prefs.getString(PREF_BINARY_TOR_VERSION_INSTALLED, null);
            logNotice("checking binary version: " + version);

            sendCallbackStatus(STATUS_STARTING);
            showToolbarNotification(getString(R.string.status_starting_up), NOTIFY_ID, R.drawable.ic_stat_starting_tor_logo);
            //sendCallbackLogMessage(getString(R.string.status_starting_up));
            //logNotice(getString(R.string.status_starting_up));

            ArrayList<String> customEnv = new ArrayList<>();

            if (Prefs.bridgesEnabled())
                if (Prefs.useVpn() && !mIsLollipop) {
                    customEnv.add("TOR_PT_PROXY=socks5://" + OrbotVpnManager.sSocksProxyLocalhost + ":" + OrbotVpnManager.sSocksProxyServerPort);
                }

            boolean success = runTorShellCmd();

            if (success) {
                try {
                    updateOnionNames();
                } catch (SecurityException se) {
                    logNotice("unable to upload onion names");
                }
            }

        } catch (Exception e) {
            logException("Unable to start Tor: " + e.toString(), e);
            stopTor();
            showToolbarNotification(
                    getString(R.string.unable_to_start_tor) + ": " + e.getMessage(),
                    ERROR_NOTIFY_ID, R.drawable.ic_stat_notifyerr);

        }
    }

    private void updateOnionNames() throws SecurityException {
        // Tor is running, update new .onion names at db
        ContentResolver mCR = getApplicationContext().getContentResolver();
        Cursor hidden_services = mCR.query(HS_CONTENT_URI, hsProjection, null, null, null);
        if (hidden_services != null) {
            try {
                while (hidden_services.moveToNext()) {
                    String HSDomain = hidden_services.getString(hidden_services.getColumnIndex(HiddenService.DOMAIN));
                    Integer HSLocalPort = hidden_services.getInt(hidden_services.getColumnIndex(HiddenService.PORT));
                    Integer HSAuthCookie = hidden_services.getInt(hidden_services.getColumnIndex(HiddenService.AUTH_COOKIE));
                    String HSAuthCookieValue = hidden_services.getString(hidden_services.getColumnIndex(HiddenService.AUTH_COOKIE_VALUE));

                    // Update only new domains or restored from backup with auth cookie
                    if ((HSDomain == null || HSDomain.length() < 1) || (HSAuthCookie == 1 && (HSAuthCookieValue == null || HSAuthCookieValue.length() < 1))) {
                        String hsDirPath = new File(mHSBasePath.getAbsolutePath(), "hs" + HSLocalPort).getCanonicalPath();
                        File file = new File(hsDirPath, "hostname");

                        if (file.exists()) {
                            ContentValues fields = new ContentValues();

                            try {
                                String onionHostname = Utils.readString(new FileInputStream(file)).trim();
                                if (HSAuthCookie == 1) {
                                    String[] aux = onionHostname.split(" ");
                                    onionHostname = aux[0];
                                    fields.put(HiddenService.AUTH_COOKIE_VALUE, aux[1]);
                                }
                                fields.put(HiddenService.DOMAIN, onionHostname);
                                mCR.update(HS_CONTENT_URI, fields, "port=" + HSLocalPort, null);
                            } catch (FileNotFoundException e) {
                                logException("unable to read onion hostname file", e);
                                showToolbarNotification(getString(R.string.unable_to_read_hidden_service_name), HS_NOTIFY_ID, R.drawable.ic_stat_notifyerr);
                            }
                        } else {
                            showToolbarNotification(getString(R.string.unable_to_read_hidden_service_name), HS_NOTIFY_ID, R.drawable.ic_stat_notifyerr);

                        }
                    }
                }

            } catch (NumberFormatException e) {
                Log.e(OrbotConstants.TAG, "error parsing hsport", e);
            } catch (Exception e) {
                Log.e(OrbotConstants.TAG, "error starting share server", e);
            }

            hidden_services.close();
        }
    }

    private boolean runTorShellCmd() throws Exception {
        boolean result = true;

        File fileTorrcCustom = updateTorrcCustomFile();

        //make sure Tor exists and we can execute it
        if (fileTor == null || (!fileTor.exists()) || (!fileTor.canExecute()))
            return false;

        if ((!fileTorRc.exists()) || (!fileTorRc.canRead()))
            return false;

        if ((!fileTorrcCustom.exists()) || (!fileTorrcCustom.canRead()))
            return false;

        sendCallbackLogMessage(getString(R.string.status_starting_up));

        String torCmdString = fileTor.getAbsolutePath()
                + " DataDirectory " + appCacheHome.getAbsolutePath()
                + " --defaults-torrc " + fileTorRc.getAbsolutePath()
                + " -f " + fileTorrcCustom.getAbsolutePath();

        int exitCode = -1;

        try {
            exitCode = exec(torCmdString + " --verify-config", true);
        } catch (Exception e) {
            logNotice("Tor configuration did not verify: " + e.getMessage());
            return false;
        }

        if (exitCode == 0) {
            logNotice("Tor configuration VERIFIED.");
            try {
                exitCode = exec(torCmdString, false);
            } catch (Exception e) {
                logNotice("Tor was unable to start: " + e.getMessage());

                throw new Exception("Tor was unable to start: " + e.getMessage());

            }

            if (exitCode != 0) {
                logNotice("Tor did not start. Exit:" + exitCode);
                return false;
            }

            //now try to connect
            mLastProcessId = initControlConnection(10, false);

            if (mLastProcessId == -1) {
                logNotice(getString(R.string.couldn_t_start_tor_process_) + "; exit=" + exitCode);
                throw new Exception(getString(R.string.couldn_t_start_tor_process_) + "; exit=" + exitCode);
            } else {

                logNotice("Tor started; process id=" + mLastProcessId);
                result = true;
            }
        }

        return result;
    }

    public void exec(Runnable runn) {
        mExecutor.execute(runn);
    }

    private int exec(String cmd, boolean wait) throws Exception {
        HashMap<String, String> mapEnv = new HashMap<>();
        mapEnv.put("HOME", appBinHome.getAbsolutePath());

        CommandResult result = CustomShell.run("sh", wait, mapEnv, cmd);
        debug("executing: " + cmd);
        debug("stdout: " + result.getStdout());
        debug("stderr: " + result.getStderr());

        return result.exitCode;
    }

    private int initControlConnection(int maxTries, boolean isReconnect) throws Exception {
        int controlPort = -1;
        int attempt = 0;

        logNotice("Waiting for control port...");

        while (conn == null && attempt++ < maxTries && (mCurrentStatus != STATUS_OFF)) {
            try {

                controlPort = getControlPort();

                if (controlPort != -1) {
                    logNotice("Connecting to control port: " + controlPort);


                    break;
                }

            } catch (Exception ce) {
                conn = null;
                //    logException( "Error connecting to Tor local control port: " + ce.getMessage(),ce);

            }


            try {
                //    logNotice("waiting...");
                Thread.sleep(2000);
            } catch (Exception e) {
            }
        }


        if (controlPort != -1) {
            Socket torConnSocket = new Socket(IP_LOCALHOST, controlPort);
            torConnSocket.setSoTimeout(CONTROL_SOCKET_TIMEOUT);

            conn = new TorControlConnection(torConnSocket);

            conn.launchThread(true);//is daemon

        }

        if (conn != null) {

            logNotice("SUCCESS connected to Tor control port.");

            File fileCookie = new File(appCacheHome, TOR_CONTROL_COOKIE);

            if (fileCookie.exists()) {

                // We extend NullEventHandler so that we don't need to provide empty
                // implementations for all the events we don't care about.
                logNotice("adding control port event handler");

                conn.setEventHandler(mEventHandler);

                logNotice("SUCCESS added control port event handler");
                byte[] cookie = new byte[(int) fileCookie.length()];
                DataInputStream fis = new DataInputStream(new FileInputStream(fileCookie));
                fis.read(cookie);
                fis.close();
                conn.authenticate(cookie);

                logNotice("SUCCESS - authenticated to control port.");

                //       conn.setEvents(Arrays.asList(new String[]{"DEBUG","STATUS_CLIENT","STATUS_GENERAL","BW"}));

                if (Prefs.useDebugLogging())
                    conn.setEvents(Arrays.asList("CIRC", "STREAM", "ORCONN", "BW", "INFO", "NOTICE", "WARN", "DEBUG", "ERR", "NEWDESC", "ADDRMAP"));
                else
                    conn.setEvents(Arrays.asList("CIRC", "STREAM", "ORCONN", "BW", "NOTICE", "ERR", "NEWDESC", "ADDRMAP"));

                //  sendCallbackLogMessage(getString(R.string.tor_process_starting) + ' ' + getString(R.string.tor_process_complete));

                String torProcId = conn.getInfo("process/pid");

                String confSocks = conn.getInfo("net/listeners/socks");
                StringTokenizer st = new StringTokenizer(confSocks, " ");

                confSocks = st.nextToken().split(":")[1];
                confSocks = confSocks.substring(0, confSocks.length() - 1);
                mPortSOCKS = Integer.parseInt(confSocks);

                String confHttp = conn.getInfo("net/listeners/httptunnel");
                st = new StringTokenizer(confHttp, " ");

                confHttp = st.nextToken().split(":")[1];
                confHttp = confHttp.substring(0, confHttp.length() - 1);
                mPortHTTP = Integer.parseInt(confHttp);

                String confDns = conn.getInfo("net/listeners/dns");
                st = new StringTokenizer(confDns, " ");
                if (st.hasMoreTokens()) {
                    confDns = st.nextToken().split(":")[1];
                    confDns = confDns.substring(0, confDns.length() - 1);
                    mPortDns = Integer.parseInt(confDns);
                    Prefs.getSharedPrefs(getApplicationContext()).edit().putInt(VpnPrefs.PREFS_DNS_PORT, mPortDns).apply();
                }

                String confTrans = conn.getInfo("net/listeners/trans");
                st = new StringTokenizer(confTrans, " ");
                if (st.hasMoreTokens()) {
                    confTrans = st.nextToken().split(":")[1];
                    confTrans = confTrans.substring(0, confTrans.length() - 1);
                    mPortTrans = Integer.parseInt(confTrans);
                }

                sendCallbackPorts(mPortSOCKS, mPortHTTP, mPortDns, mPortTrans);

                setTorNetworkEnabled(true);

                return Integer.parseInt(torProcId);

            } else {
                logNotice("Tor authentication cookie does not exist yet");
                conn = null;

            }
        }

        throw new Exception("Tor control port could not be found");
    }

    private int getControlPort() {
        int result = -1;

        try {
            if (fileControlPort.exists()) {
                debug("Reading control port config file: " + fileControlPort.getCanonicalPath());
                BufferedReader bufferedReader = new BufferedReader(new FileReader(fileControlPort));
                String line = bufferedReader.readLine();

                if (line != null) {
                    String[] lineParts = line.split(":");
                    result = Integer.parseInt(lineParts[1]);
                }


                bufferedReader.close();

                //store last valid control port
                SharedPreferences prefs = Prefs.getSharedPrefs(getApplicationContext());
                prefs.edit().putInt("controlport", result).apply();
            } else {
                debug("Control Port config file does not yet exist (waiting for tor): " + fileControlPort.getCanonicalPath());
            }
        } catch (FileNotFoundException e) {
            debug("unable to get control port; file not found");
        } catch (Exception e) {
            debug("unable to read control port config file");
        }

        return result;
    }

    /**
     * Returns the port number that the HTTP proxy is running on
     */
    public int getHTTPPort() {
        return mPortHTTP;
    }

    /**
     * Returns the port number that the HTTP proxy is running on
     */
    public int getSOCKSPort() {
        return mPortSOCKS;
    }

    public String getInfo(String key) {
        try {
            if (conn != null) {
                return conn.getInfo(key);
            }
        } catch (Exception ioe) {
            //    Log.e(TAG,"Unable to get Tor information",ioe);
            logNotice("Unable to get Tor information" + ioe.getMessage());
        }
        return null;
    }

    public String getConfiguration(String name) {
        try {
            if (conn != null) {
                StringBuffer result = new StringBuffer();

                List<ConfigEntry> listCe = conn.getConf(name);

                Iterator<ConfigEntry> itCe = listCe.iterator();
                ConfigEntry ce;


                while (itCe.hasNext()) {
                    ce = itCe.next();

                    result.append(ce.key);
                    result.append(' ');
                    result.append(ce.value);
                    result.append('\n');
                }

                return result.toString();
            }
        } catch (Exception ioe) {

            logException("Unable to get Tor configuration: " + ioe.getMessage(), ioe);
        }

        return null;
    }

    /**
     * Set configuration
     **/
    public boolean updateConfiguration(String name, String value, boolean saveToDisk) {


        if (configBuffer == null)
            configBuffer = new ArrayList<>();

        if (resetBuffer == null)
            resetBuffer = new ArrayList<>();

        if (value == null || value.length() == 0) {
            resetBuffer.add(name + RESET_STRING);

        } else {

            String sbConf = name +
                    ' ' +
                    value;
            configBuffer.add(sbConf);
        }

        return false;
    }


    public void setTorNetworkEnabled(final boolean isEnabled) throws IOException {

        //it is possible to not have a connection yet, and someone might try to newnym
        if (conn != null) {
            new Thread() {
                public void run() {
                    try {

                        final String newValue = isEnabled ? "0" : "1";
                        conn.setConf("DisableNetwork", newValue);
                    } catch (Exception ioe) {
                        debug("error requesting newnym: " + ioe.getLocalizedMessage());
                    }
                }
            }.start();
        }

    }

    public void sendSignalActive() {
        if (conn != null && mCurrentStatus == STATUS_ON) {
            try {
                conn.signal("ACTIVE");
            } catch (IOException e) {
                debug("error send active: " + e.getLocalizedMessage());
            }
        }
    }

    public void newIdentity() {
        //it is possible to not have a connection yet, and someone might try to newnym
        if (conn != null) {
            new Thread() {
                public void run() {
                    try {
                        int iconId = R.drawable.ic_stat_tor;

                        if (conn != null && mCurrentStatus == STATUS_ON && Prefs.expandedNotifications())
                            showToolbarNotification(getString(R.string.newnym), NOTIFY_ID, R.drawable.ic_stat_starting_tor_logo);

                        conn.signal(TorControlCommands.SIGNAL_NEWNYM);

                    } catch (Exception ioe) {
                        debug("error requesting newnym: " + ioe.getLocalizedMessage());
                    }
                }
            }.start();
        }
    }

    public boolean saveConfiguration() {
        try {
            if (conn != null) {

                if (resetBuffer != null && resetBuffer.size() > 0) {
                    for (String value : configBuffer) {

                        //   debug("removing torrc conf: " + value);


                    }

                    // conn.resetConf(resetBuffer);
                    resetBuffer = null;
                }

                if (configBuffer != null && configBuffer.size() > 0) {

                    for (String value : configBuffer) {

                        debug("Setting torrc conf: " + value);


                    }

                    conn.setConf(configBuffer);

                    configBuffer = null;
                }

                // Flush the configuration to disk.
                //this is doing bad things right now NF 22/07/10
                //conn.saveConf();

                return true;
            }
        } catch (Exception ioe) {
            logException("Unable to update Tor configuration: " + ioe.getMessage(), ioe);
        }

        return false;
    }

    public void sendCallbackBandwidth(long upload, long download, long written, long read) {
        Intent intent = new Intent(LOCAL_ACTION_BANDWIDTH);

        intent.putExtra("up", upload);
        intent.putExtra("down", download);
        intent.putExtra("written", written);
        intent.putExtra("read", read);
        intent.putExtra(EXTRA_STATUS, mCurrentStatus);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendCallbackLogMessage(String logMessage)
    {

        if(logMessage.contains("Bootstrapped 100%")){
            orbotLocalConstants.mIsTorInitialized = true;
        }
        mHandler.post(() -> {
            Intent intent = new Intent(LOCAL_ACTION_LOG);
            intent.putExtra(LOCAL_EXTRA_LOG, logMessage);
            intent.putExtra(EXTRA_STATUS, mCurrentStatus);
            orbotLocalConstants.mTorLogsHistory.add(new logRowModel(logMessage, localHelperMethod.getCurrentTime()));

            if(!mConnectivity){
                orbotLocalConstants.mTorLogsStatus = "No internet connection";
            }else {
                orbotLocalConstants.mTorLogsStatus = logMessage;
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        });

    }

    private void sendCallbackPorts(int socksPort, int httpPort, int dnsPort, int transPort) {

        Intent intent = new Intent(LOCAL_ACTION_PORTS);
        // You can also include some extra data.
        intent.putExtra(EXTRA_SOCKS_PROXY_PORT, socksPort);
        intent.putExtra(EXTRA_HTTP_PROXY_PORT, httpPort);
        intent.putExtra(EXTRA_DNS_PORT, dnsPort);
        intent.putExtra(EXTRA_TRANS_PORT, transPort);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        if (Prefs.useVpn())
            mVpnManager.handleIntent(new Builder(), intent);

    }

    public void sendCallbackStatus(String currentStatus) {
        mCurrentStatus = currentStatus;
        Intent intent = getActionStatusIntent(currentStatus);
        sendBroadcastOnlyToOrbot(intent); // send for Orbot internals, using secure local broadcast
        sendBroadcast(intent); // send for any apps that are interested
    }

    /**
     * Send a secure broadcast only to Orbot itself
     *
     * @see {@link ContextWrapper#sendBroadcast(Intent)}
     * @see {@link LocalBroadcastManager}
     */
    private boolean sendBroadcastOnlyToOrbot(Intent intent) {
        return LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private Intent getActionStatusIntent(String currentStatus) {
        Intent intent = new Intent(ACTION_STATUS);
        intent.putExtra(EXTRA_STATUS, currentStatus);
        return intent;
    }

    /*
     *  Another way to do this would be to use the Observer pattern by defining the
     *  BroadcastReciever in the Android manifest.
     */

    private boolean mConnectivity = true;
    private int mNetworkType = -1;
    private final BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            SharedPreferences prefs = org.torproject.android.proxy.util.Prefs.getSharedPrefs(getApplicationContext());

            if(prefs==null){
            }

            boolean doNetworKSleep = prefs.getBoolean(OrbotConstants.PREF_DISABLE_NETWORK, true);

            final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo netInfo = cm.getActiveNetworkInfo();

            boolean newConnectivityState = false;
            int newNetType = -1;

            if (netInfo!=null)
                newNetType = netInfo.getType();

            if(netInfo != null && netInfo.isConnected()) {
                // WE ARE CONNECTED: DO SOMETHING
                newConnectivityState = true;
            }
            else {
                // WE ARE NOT: DO SOMETHING ELSE
                newConnectivityState = false;
            }

            mNetworkType = newNetType;

            if (newConnectivityState != mConnectivity) {
                mConnectivity = newConnectivityState;
                orbotLocalConstants.mNetworkState = mConnectivity;

                if (mConnectivity)
                    newIdentity();
            }


            if (doNetworKSleep)
            {
                //setTorNetworkEnabled (mConnectivity);
                if (!mConnectivity)
                {
                    //sendCallbackStatus(STATUS_OFF);
                    orbotLocalConstants.mTorLogsStatus = "No internet connection";
                    showToolbarNotification(getString(R.string.newnym), getNotifyId(), R.drawable.ic_stat_tor_off);
                    showToolbarNotification("Genesis is sleeping",NOTIFY_ID,R.drawable.ic_stat_tor_off);
                }
                else
                {
                    //sendCallbackStatus(STATUS_STARTING);
                    showToolbarNotification(getString(R.string.status_starting_up),NOTIFY_ID,R.drawable.ic_stat_starting_tor_logo);
                }

            }
        }
    };

    private StringBuffer processSettingsImpl(StringBuffer extraLines) throws IOException {
        logNotice(getString(R.string.updating_settings_in_tor_service));

        SharedPreferences prefs = Prefs.getSharedPrefs(getApplicationContext());

        boolean useBridges = Prefs.bridgesEnabled();

        boolean becomeRelay = prefs.getBoolean(OrbotConstants.PREF_OR, false);
        boolean ReachableAddresses = prefs.getBoolean(OrbotConstants.PREF_REACHABLE_ADDRESSES, false);

        boolean enableStrictNodes = prefs.getBoolean("pref_strict_nodes", false);
        String entranceNodes = prefs.getString("pref_entrance_nodes", "");
        String exitNodes = prefs.getString("pref_exit_nodes", "");
        String excludeNodes = prefs.getString("pref_exclude_nodes", "");

        if (!useBridges) {

            extraLines.append("UseBridges 0").append('\n');

            if (Prefs.useVpn()) //set the proxy here if we aren't using a bridge
            {

                if (!mIsLollipop) {
                    String proxyType = "socks5";
                    extraLines.append(proxyType + "Proxy" + ' ' + OrbotVpnManager.sSocksProxyLocalhost + ':' + OrbotVpnManager.sSocksProxyServerPort).append('\n');
                }

            } else {
                String proxyType = prefs.getString("pref_proxy_type", null);
                if (proxyType != null && proxyType.length() > 0) {
                    String proxyHost = prefs.getString("pref_proxy_host", null);
                    String proxyPort = prefs.getString("pref_proxy_port", null);
                    String proxyUser = prefs.getString("pref_proxy_username", null);
                    String proxyPass = prefs.getString("pref_proxy_password", null);

                    if ((proxyHost != null && proxyHost.length() > 0) && (proxyPort != null && proxyPort.length() > 0)) {
                        extraLines.append(proxyType + "Proxy" + ' ' + proxyHost + ':' + proxyPort).append('\n');

                        if (proxyUser != null && proxyPass != null) {
                            if (proxyType.equalsIgnoreCase("socks5")) {
                                extraLines.append("Socks5ProxyUsername" + ' ' + proxyUser).append('\n');
                                extraLines.append("Socks5ProxyPassword" + ' ' + proxyPass).append('\n');
                            } else
                                extraLines.append(proxyType + "ProxyAuthenticator" + ' ' + proxyUser + ':' + proxyPort).append('\n');

                        } else if (proxyPass != null)
                            extraLines.append(proxyType + "ProxyAuthenticator" + ' ' + proxyUser + ':' + proxyPort).append('\n');


                    }
                }
            }
        } else {
            if (fileObfsclient != null
                    && fileObfsclient.exists()
                    && fileObfsclient.canExecute()) {

                loadBridgeDefaults();

                extraLines.append("UseBridges 1").append('\n');
                //    extraLines.append("UpdateBridgesFromAuthority 1").append('\n');

                String bridgeList = Prefs.getBridgesList();
                boolean obfs3Bridges = bridgeList.contains("obfs3");
                boolean obfs4Bridges = bridgeList.contains("obfs4");
                boolean meekBridges = bridgeList.contains("meek");

                //check if any PT bridges are needed
                if (obfs3Bridges)
                    extraLines.append("ClientTransportPlugin obfs3 exec ")
                            .append(fileObfsclient.getAbsolutePath()).append('\n');

                if (obfs4Bridges)
                    extraLines.append("ClientTransportPlugin obfs4 exec ")
                            .append(fileObfsclient.getAbsolutePath()).append('\n');

                if (meekBridges)
                    extraLines.append("ClientTransportPlugin meek_lite exec " + fileObfsclient.getCanonicalPath()).append('\n');

                if (bridgeList != null && bridgeList.length() > 5) //longer then 1 = some real values here
                {
                    String[] bridgeListLines = parseBridgesFromSettings(bridgeList);
                    int bridgeIdx = (int) Math.floor(Math.random() * ((double) bridgeListLines.length));
                    String bridgeLine = bridgeListLines[bridgeIdx];
                    extraLines.append("Bridge ");
                    extraLines.append(bridgeLine);
                    extraLines.append("\n");
                    /**
                     for (String bridgeConfigLine : bridgeListLines) {
                     if (!TextUtils.isEmpty(bridgeConfigLine)) {
                     extraLines.append("Bridge ");
                     extraLines.append(bridgeConfigLine.trim());
                     extraLines.append("\n");
                     }

                     }**/
                } else {

                    String type = "obfs4";

                    if (meekBridges)
                        type = "meek_lite";

                    getBridges(type, extraLines);

                }
            } else {
                throw new IOException("Bridge binary does not exist: " + fileObfsclient.getCanonicalPath());
            }
        }


        //only apply GeoIP if you need it
        File fileGeoIP = new File(appBinHome, GEOIP_ASSET_KEY);
        File fileGeoIP6 = new File(appBinHome, GEOIP6_ASSET_KEY);

        if (fileGeoIP.exists()) {
            extraLines.append("GeoIPFile" + ' ' + fileGeoIP.getCanonicalPath()).append('\n');
            extraLines.append("GeoIPv6File" + ' ' + fileGeoIP6.getCanonicalPath()).append('\n');
        }

        if (!TextUtils.isEmpty(entranceNodes))
            extraLines.append("EntryNodes" + ' ' + entranceNodes).append('\n');

        if (!TextUtils.isEmpty(exitNodes))
            extraLines.append("ExitNodes" + ' ' + exitNodes).append('\n');

        if (!TextUtils.isEmpty(excludeNodes))
            extraLines.append("ExcludeNodes" + ' ' + excludeNodes).append('\n');

        extraLines.append("StrictNodes" + ' ' + (enableStrictNodes ? "1" : "0")).append('\n');

        try {
            if (ReachableAddresses) {
                String ReachableAddressesPorts =
                        prefs.getString(OrbotConstants.PREF_REACHABLE_ADDRESSES_PORTS, "*:80,*:443");

                extraLines.append("ReachableAddresses" + ' ' + ReachableAddressesPorts).append('\n');

            }

        } catch (Exception e) {
            showToolbarNotification(getString(R.string.your_reachableaddresses_settings_caused_an_exception_), ERROR_NOTIFY_ID, R.drawable.ic_stat_notifyerr);

            return null;
        }

        try {
            if (becomeRelay && (!useBridges) && (!ReachableAddresses)) {
                int ORPort = Integer.parseInt(prefs.getString(OrbotConstants.PREF_OR_PORT, "9001"));
                String nickname = prefs.getString(OrbotConstants.PREF_OR_NICKNAME, "Orbot");

                String dnsFile = writeDNSFile();

                extraLines.append("ServerDNSResolvConfFile" + ' ' + dnsFile).append('\n');
                extraLines.append("ORPort" + ' ' + ORPort).append('\n');
                extraLines.append("Nickname" + ' ' + nickname).append('\n');
                extraLines.append("ExitPolicy" + ' ' + "reject *:*").append('\n');

            }
        } catch (Exception e) {
            showToolbarNotification(getString(R.string.your_relay_settings_caused_an_exception_), ERROR_NOTIFY_ID, R.drawable.ic_stat_notifyerr);


            return null;
        }

        ContentResolver mCR = getApplicationContext().getContentResolver();

        try {
            /* ---- Hidden Services ---- */
            Cursor hidden_services = mCR.query(HS_CONTENT_URI, hsProjection, HiddenService.ENABLED + "=1", null, null);
            if (hidden_services != null) {
                try {
                    while (hidden_services.moveToNext()) {
                        String HSname = hidden_services.getString(hidden_services.getColumnIndex(HiddenService.NAME));
                        Integer HSLocalPort = hidden_services.getInt(hidden_services.getColumnIndex(HiddenService.PORT));
                        Integer HSOnionPort = hidden_services.getInt(hidden_services.getColumnIndex(HiddenService.ONION_PORT));
                        Integer HSAuthCookie = hidden_services.getInt(hidden_services.getColumnIndex(HiddenService.AUTH_COOKIE));
                        String hsDirPath = new File(mHSBasePath.getAbsolutePath(), "hs" + HSLocalPort).getCanonicalPath();

                        debug("Adding hidden service on port: " + HSLocalPort);

                        extraLines.append("HiddenServiceDir" + ' ' + hsDirPath).append('\n');
                        extraLines.append("HiddenServicePort" + ' ' + HSOnionPort + " 127.0.0.1:" + HSLocalPort).append('\n');
                        extraLines.append("HiddenServiceVersion 2").append('\n');

                        if (HSAuthCookie == 1)
                            extraLines.append("HiddenServiceAuthorizeClient stealth " + HSname).append('\n');
                    }
                } catch (NumberFormatException e) {
                    Log.e(OrbotConstants.TAG, "error parsing hsport", e);
                } catch (Exception e) {
                    Log.e(OrbotConstants.TAG, "error starting share server", e);
                }

                hidden_services.close();
            }
        } catch (SecurityException se) {
        }

        try {

            /* ---- Client Cookies ---- */
            Cursor client_cookies = mCR.query(COOKIE_CONTENT_URI, cookieProjection, ClientCookie.ENABLED + "=1", null, null);
            if (client_cookies != null) {
                try {
                    while (client_cookies.moveToNext()) {
                        String domain = client_cookies.getString(client_cookies.getColumnIndex(ClientCookie.DOMAIN));
                        String cookie = client_cookies.getString(client_cookies.getColumnIndex(ClientCookie.AUTH_COOKIE_VALUE));
                        extraLines.append("HidServAuth" + ' ' + domain + ' ' + cookie).append('\n');
                    }
                } catch (Exception e) {
                    Log.e(OrbotConstants.TAG, "error starting share server", e);
                }

                client_cookies.close();
            }
        } catch (SecurityException se) {
        }

        return extraLines;
    }

    //using Google DNS for now as the public DNS server
    private String writeDNSFile() throws IOException {
        File file = new File(appBinHome, "resolv.conf");

        PrintWriter bw = new PrintWriter(new FileWriter(file));
        bw.println("nameserver 8.8.8.8");
        bw.println("nameserver 8.8.4.4");
        bw.close();

        return file.getCanonicalPath();
    }

    @SuppressLint("NewApi")
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        switch (level) {
            case TRIM_MEMORY_BACKGROUND:
                debug("trim memory requested: app in the background");
                break;

            case TRIM_MEMORY_COMPLETE:
                debug("trim memory requested: cleanup all memory");
                break;

            case TRIM_MEMORY_MODERATE:
                debug("trim memory requested: clean up some memory");
                break;

            case TRIM_MEMORY_RUNNING_CRITICAL:
                debug("trim memory requested: memory on device is very low and critical");
                break;

            case TRIM_MEMORY_RUNNING_LOW:
                debug("trim memory requested: memory on device is running low");
                break;

            case TRIM_MEMORY_RUNNING_MODERATE:
                debug("trim memory requested: memory on device is moderate");
                break;

            case TRIM_MEMORY_UI_HIDDEN:
                debug("trim memory requested: app is not showing UI anymore");
                break;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        //do nothing here
        return super.onBind(intent); // invoking super class will call onRevoke() when appropriate
    }

    // system calls this method when VPN disconnects (either by the user or another VPN app)
    @Override
    public void onRevoke() {
        Prefs.putUseVpn(false);
        mVpnManager.handleIntent(new Builder(), new Intent(ACTION_STOP_VPN));
        // tell UI, if it's open, to update immediately (don't wait for onResume() in Activity...)
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_STOP_VPN));
    }

    private void setExitNode(String newExits) {
        SharedPreferences prefs = Prefs.getSharedPrefs(getApplicationContext());

        if (TextUtils.isEmpty(newExits)) {
            prefs.edit().remove("pref_exit_nodes").apply();

            if (conn != null) {
                try {
                    ArrayList<String> resetBuffer = new ArrayList<>();
                    resetBuffer.add("ExitNodes");
                    resetBuffer.add("StrictNodes");
                    conn.resetConf(resetBuffer);
                    conn.setConf("DisableNetwork", "1");
                    conn.setConf("DisableNetwork", "0");

                } catch (Exception ioe) {
                    Log.e(OrbotConstants.TAG, "Connection exception occured resetting exits", ioe);
                }
            }
        } else {
            prefs.edit().putString("pref_exit_nodes", newExits).apply();

            if (conn != null) {
                try {
                    File fileGeoIP = new File(appBinHome, GEOIP_ASSET_KEY);
                    File fileGeoIP6 = new File(appBinHome, GEOIP6_ASSET_KEY);

                    conn.setConf("GeoIPFile", fileGeoIP.getCanonicalPath());
                    conn.setConf("GeoIPv6File", fileGeoIP6.getCanonicalPath());

                    conn.setConf("ExitNodes", newExits);
                    conn.setConf("StrictNodes", "1");

                    conn.setConf("DisableNetwork", "1");
                    conn.setConf("DisableNetwork", "0");

                } catch (Exception ioe) {
                    Log.e(OrbotConstants.TAG, "Connection exception occured resetting exits", ioe);
                }
            }
        }

    }

    private void loadBridgeDefaults() {
        if (alBridges == null) {
            alBridges = new ArrayList<>();

            try {
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.bridges), "UTF-8"));
                String str;

                while ((str = in.readLine()) != null) {

                    StringTokenizer st = new StringTokenizer(str, " ");
                    Bridge b = new Bridge();
                    b.type = st.nextToken();

                    StringBuffer sbConfig = new StringBuffer();

                    while (st.hasMoreTokens())
                        sbConfig.append(st.nextToken()).append(' ');

                    b.config = sbConfig.toString().trim();

                    alBridges.add(b);

                }

                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void getBridges(String type, StringBuffer extraLines) {

        Collections.shuffle(alBridges, bridgeSelectRandom);

        int maxBridges = 2;
        int bridgeCount = 0;


        if(orbotLocalConstants.mIsManualBridge){
            List<String> mList = Arrays.asList(orbotLocalConstants.mBridges.split("\n "));
            alBridges.clear();

            for(int e=0;e<mList.size();e++){
                if(mList.get(e).length()<5){
                    continue;
                }
                List<String> mListTemp = Arrays.asList(mList.get(e).split(" "));
                int mIndex = 0;
                while(mListTemp.get(mIndex).length()<3){
                    mIndex+=1;
                }
                alBridges.add(new Bridge(mList.get(e).replace(mListTemp.get(mIndex),"") , orbotLocalConstants.mManualBridgeType));
            }
        }

        for (Bridge b : alBridges) {
            if (b.type.equals(type)) {
                extraLines.append("Bridge ");
                extraLines.append(b.type);
                extraLines.append(' ');
                extraLines.append(b.config);
                extraLines.append('\n');

                bridgeCount++;

                if (bridgeCount > maxBridges)
                    break;
            }
        }
    }

    public static final class HiddenService implements BaseColumns {
        public static final String NAME = "name";
        public static final String PORT = "port";
        public static final String ONION_PORT = "onion_port";
        public static final String DOMAIN = "domain";
        public static final String AUTH_COOKIE = "auth_cookie";
        public static final String AUTH_COOKIE_VALUE = "auth_cookie_value";
        public static final String CREATED_BY_USER = "created_by_user";
        public static final String ENABLED = "enabled";

        private HiddenService() {
        }
    }

    public static final class ClientCookie implements BaseColumns {
        public static final String DOMAIN = "domain";
        public static final String AUTH_COOKIE_VALUE = "auth_cookie_value";
        public static final String ENABLED = "enabled";

        private ClientCookie() {
        }
    }

    // for bridge loading from the assets default bridges.txt file
    static class Bridge {
        String type;
        String config;

        public Bridge(String pConfig,String pType){
            config = pConfig;
            type = pType;
        }

        public Bridge(){
        }
    }
    private class IncomingIntentRouter implements Runnable {
        Intent mIntent;

        public IncomingIntentRouter(Intent intent) {
            mIntent = intent;
        }

        public void run() {

            String action = mIntent.getAction();

            if (!TextUtils.isEmpty(action)) {
                if (action.equals(ACTION_START) || action.equals(ACTION_START_ON_BOOT)) {
                    startTor();
                    replyWithStatus(mIntent);

                    if (Prefs.useVpn()) {
                        if (mVpnManager != null
                                && (!mVpnManager.isStarted())) {
                            //start VPN here
                            Intent vpnIntent = VpnService.prepare(OrbotService.this);
                            if (vpnIntent == null) //then we can run the VPN
                            {
                                mVpnManager.handleIntent(new Builder(), mIntent);

                            }
                        }

                        if (mPortSOCKS != -1 && mPortHTTP != -1)
                            sendCallbackPorts(mPortSOCKS, mPortHTTP, mPortDns, mPortTrans);
                    }

                } else if (action.equals(ACTION_START_VPN)) {
                    if (mVpnManager != null && (!mVpnManager.isStarted())) {
                        //start VPN here
                        Intent vpnIntent = VpnService.prepare(OrbotService.this);
                        if (vpnIntent == null) //then we can run the VPN
                        {
                            mVpnManager.handleIntent(new Builder(), mIntent);


                        }
                    }

                    if (mPortSOCKS != -1 && mPortHTTP != -1)
                        sendCallbackPorts(mPortSOCKS, mPortHTTP, mPortDns, mPortTrans);


                } else if (action.equals(ACTION_STOP_VPN)) {
                    if (mVpnManager != null)
                        mVpnManager.handleIntent(new Builder(), mIntent);
                } else if (action.equals(ACTION_STATUS)) {
                    replyWithStatus(mIntent);
                } else if (action.equals(CMD_SIGNAL_HUP)) {
                    requestTorRereadConfig();
                } else if (action.equals(CMD_NEWNYM)) {
                    newIdentity();
                }
                else if (action.equals(CMD_SETTING)) {
                    onSettingRegister();
                }
                else if (action.equals(CMD_ACTIVE)) {
                    sendSignalActive();
                } else if (action.equals(CMD_SET_EXIT)) {

                    setExitNode(mIntent.getStringExtra("exit"));

                } else {
                    Log.w(OrbotConstants.TAG, "unhandled OrbotService Intent: " + action);
                }
            }
        }
    }

    public String getProxyStatus() {
        return mCurrentStatus;
    }

    private static OrbotService self = null;

    public static OrbotService getServiceObject(){
        return self;
    }

    public void onSettingRegister(){
        try {
            Intent intent = null;
            intent = new Intent(this, Class.forName("com.darkweb.genesissearchengine.appManager.settingManager.notificationManager.settingNotificationController"));
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void disableNotification(){
        if(mNotificationManager!=null){
            mNotificationManager.cancel(NOTIFY_ID);
            stopForeground(true);
            orbotLocalConstants.mNotificationStatus = 0;
        }
    }

    public void enableTorNotificationNoBandwidth(){
        orbotLocalConstants.mNotificationStatus = 1;
        showToolbarNotification("Connected to the Tor network", HS_NOTIFY_ID, R.mipmap.ic_stat_tor_logo);
    }

    public void enableNotification(){
        orbotLocalConstants.mNotificationStatus = 1;
        showToolbarNotification(0+"kbps ⇣ / " +0+"kbps ⇡", HS_NOTIFY_ID, R.mipmap.ic_stat_tor_logo);
    }

    private class ActionBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CMD_NEWNYM: {
                    newIdentity();
                    break;
                }
                case CMD_SETTING: {
                    onSettingRegister();
                    break;
                }
                case CMD_ACTIVE: {
                    sendSignalActive();
                    break;
                }
            }
        }
    }

}
