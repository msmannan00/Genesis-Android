package com.hiddenservices.onionservices.helperManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import com.hiddenservices.onionservices.BuildConfig;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.kotlinHelperLibraries.DefaultBrowser;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.libs.trueTime.trueTimeEncryption;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.net.ssl.HttpsURLConnection;
import org.mozilla.geckoview.ContentBlocking;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static com.hiddenservices.onionservices.constants.constants.CONST_LIST_EXTERNAL_SHORTCUT;
import static com.hiddenservices.onionservices.constants.constants.CONST_PLAYSTORE_URL;
import static com.hiddenservices.onionservices.constants.keys.M_ACTIVITY_NAVIGATION_BUNDLE_KEY;
import static com.hiddenservices.onionservices.constants.keys.M_RESTART_APP_KEY;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_OPEN_ACTIVITY_FAILED;

public class helperMethod {
    /*Helper Methods General*/

    public static String getFileSizeBadge(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[]{"B Downloaded", "KB ⇣", "MB ⇣", "GB ⇣", "TB ⇣"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @SuppressLint({"UnspecifiedImmutableFlag", "LaunchActivityFromNotification"})
    public static PendingIntent onCreateActionIntent(Context pContext, Class<?> pBroadcastReciever, int pNotificationID, String pTitle, int pCommandID) {
        PendingIntent pendingIntent;
        Intent pendingIntentTrigger = new Intent(pContext, pBroadcastReciever);
        pendingIntentTrigger.setAction(pTitle);
        pendingIntentTrigger.putExtra("N_ID", pNotificationID);
        pendingIntentTrigger.putExtra("N_COMMAND", pCommandID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(pContext, pNotificationID, pendingIntentTrigger, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(pContext, pNotificationID, pendingIntentTrigger, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;
    }

    public static String getAssetsCacheFile(Context context, String fileName) {
        File cacheFile = new File(context.getCacheDir(), fileName);
        try {
            try (InputStream inputStream = context.getAssets().open(fileName)) {
                try (FileOutputStream outputStream = new FileOutputStream(cacheFile)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mYAML = helperMethod.readFromFile(cacheFile.getPath());
        if (status.sTorBrowsing) {
            mYAML = mYAML.replace("# network.proxy.socks:  \"127.0.0.1\"", "network.proxy.socks:  \"127.0.0.1\"");
            mYAML = mYAML.replace("# network.proxy.socks_port:  9050", "network.proxy.socks_port:  9050");
            mYAML = mYAML.replace("browser.cache.memory.enable: true", "browser.cache.memory.enable: false");
            mYAML = mYAML.replace("privacy.resistFingerprinting: true", "privacy.resistFingerprinting: false");

            StringBuilder buf = new StringBuilder(mYAML);
            int portIndex = mYAML.indexOf("network.proxy.socks_port");
            int breakIndex = mYAML.indexOf("\n", portIndex);
            mYAML = buf.replace(portIndex, breakIndex, "network.proxy.socks_port:  " + orbotLocalConstants.mSOCKSPort).toString();
        } else {
            mYAML = mYAML.replace("browser.cache.memory.enable: true", "browser.cache.memory.enable: false");
            if(status.sSettingTrackingProtection == ContentBlocking.AntiTracking.STRICT){
                mYAML = mYAML.replace("privacy.resistFingerprinting: false", "privacy.resistFingerprinting: true");
            }
        }
        if(status.sSettingTrackingProtection==ContentBlocking.AntiTracking.STRICT){
            mYAML = mYAML.replace("# browser.send_pings.require_same_host: true", "browser.send_pings.require_same_host: true");
            mYAML = mYAML.replace("# security.csp.experimentalEnabled: true", "security.csp.experimentalEnabled: true");
            mYAML = mYAML.replace("# network.http.referer.spoofSource: true", "network.http.referer.spoofSource: true");
            mYAML = mYAML.replace("# security.OCSP.require: true", "security.OCSP.require: true");
            mYAML = mYAML.replace("# security.ssl.require_safe_negotiation: true", "security.ssl.require_safe_negotiation: true");
            mYAML = mYAML.replace("# privacy.resistFingerprinting: true", "privacy.resistFingerprinting: true");
            mYAML = mYAML.replace("# security.mixed_content.block_active_content: true", "security.mixed_content.block_active_content: true");
            mYAML = mYAML.replace("# security.mixed_content.block_display_content: true", "security.mixed_content.block_display_content: true");
            mYAML = mYAML.replace("# javascript.enabled: false", "javascript.enabled: false");
            mYAML = mYAML.replace("# webgl.min_capability_mode: true", "webgl.min_capability_mode: true");
            mYAML = mYAML.replace("# webgl.disable-extensions: true", "webgl.disable-extensions: true");
            mYAML = mYAML.replace("# browser.display.use_document_fonts: 0", "browser.display.use_document_fonts: 0");
            mYAML = mYAML.replace("# media.autoplay.default: 5", "media.autoplay.default: 5");
            mYAML = mYAML.replace("# layers.acceleration.disabled: true", "layers.acceleration.disabled: true");
            mYAML = mYAML.replace("# media.peerconnection.enabled: false", "media.peerconnection.enabled: false");
            mYAML = mYAML.replace("# dom.battery.enabled: false", "dom.battery.enabled: false");
            mYAML = mYAML.replace("# dom.gamepad.enabled: false", "dom.gamepad.enabled: false");
            mYAML = mYAML.replace("# media.webspeech.synth.enabled: false", "media.webspeech.synth.enabled: false");
            mYAML = mYAML.replace("# dom.w3c_touch_events.enabled: 0", "dom.w3c_touch_events.enabled: 0");
            mYAML = mYAML.replace("# dom.event.clipboardevents.enabled: false", "dom.event.clipboardevents.enabled: false");
            mYAML = mYAML.replace("# dom.webnotifications.enabled: false", "dom.webnotifications.enabled: false");
            mYAML = mYAML.replace("# network.prefetch-next: false", "network.prefetch-next: false");
            mYAML = mYAML.replace("# network.dns.disablePrefetch: true", "network.dns.disablePrefetch: true");
            mYAML = mYAML.replace("# network.http.speculative-parallel-limit: 0", "network.http.speculative-parallel-limit: 0");
            mYAML = mYAML.replace("# browser.formfill.enable: false", "browser.formfill.enable: false");
            mYAML = mYAML.replace("# browser.download.folderList: 2", "browser.download.folderList: 2");
            mYAML = mYAML.replace("# webgl.enable-webgl2: false", "webgl.enable-webgl2: false");
        }
        if(status.sSettingTrackingProtection==ContentBlocking.AntiTracking.DEFAULT){
            mYAML = mYAML.replace("# javascript.enabled: true", "javascript.enabled: true");
            mYAML = mYAML.replace("# webgl.disabled: false", "webgl.disabled: false");
            mYAML = mYAML.replace("# webgl.min_capability_mode: false", "webgl.min_capability_mode: false");
            mYAML = mYAML.replace("# webgl.disable-extensions: false", "webgl.disable-extensions: false");
            mYAML = mYAML.replace("# layers.acceleration.disabled: false", "layers.acceleration.disabled: false");
            mYAML = mYAML.replace("# media.peerconnection.enabled: true", "media.peerconnection.enabled: true");
            mYAML = mYAML.replace("# media.autoplay.default: 0", "media.autoplay.default: 0");
            mYAML = mYAML.replace("# dom.w3c_touch_events.enabled: 1", "dom.w3c_touch_events.enabled: 1");
            mYAML = mYAML.replace("# dom.event.clipboardevents.enabled: true", "dom.event.clipboardevents.enabled: true");
            mYAML = mYAML.replace("# dom.webnotifications.enabled: true", "dom.webnotifications.enabled: true");
            mYAML = mYAML.replace("# network.prefetch-next: true", "network.prefetch-next: true");
            mYAML = mYAML.replace("# network.dns.disablePrefetch: false", "network.dns.disablePrefetch: false");
            mYAML = mYAML.replace("# network.http.speculative-parallel-limit: 6", "network.http.speculative-parallel-limit: 6");
            mYAML = mYAML.replace("# browser.formfill.enable: true", "browser.formfill.enable: true");
            mYAML = mYAML.replace("# browser.download.folderList: 1", "browser.download.folderList: 1");
            mYAML = mYAML.replace("# webgl.enable-webgl2: true", "webgl.enable-webgl2: true");
            mYAML = mYAML.replace("# dom.battery.enabled: true", "dom.battery.enabled: true");
            mYAML = mYAML.replace("# dom.gamepad.enabled: true", "dom.gamepad.enabled: true");
            mYAML = mYAML.replace("# media.webspeech.synth.enabled: true", "media.webspeech.synth.enabled: true");
        }
        helperMethod.writeToFile(cacheFile.getPath(), mYAML);
        return cacheFile.getAbsolutePath();
    }

    public static String setGenesisVerificationToken(String pString) {
        try {
            if (pString.contains("?")) {
                pString += "&" + constants.CONST_GENESIS_GMT_TIME_GET_KEY + "=" + trueTimeEncryption.getInstance().getSecretToken();
            } else {
                pString += "?" + constants.CONST_GENESIS_GMT_TIME_GET_KEY + "=" + trueTimeEncryption.getInstance().getSecretToken();
            }
            return pString;
        } catch (Exception ex) {
            return pString;
        }
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void onDelayHandler(AppCompatActivity pActivity, int pTime, Callable<Void> pMethodParam) {
        final Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            try {
                pMethodParam.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, pTime);
    }

    public static void writeToFile(String pFilePath, String content) {
        Writer writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pFilePath), StandardCharsets.UTF_8));
            writer.write(content);
            writer.close();
        } catch (Exception ex) {
        }
    }

    public static String readFromFile(String pFilePath) {
        File file = new File(pFilePath);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            return text.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static boolean availablePort(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

    public static String completeURL(String pURL) {
        if (pURL.contains("167.86.99.31") || pURL.equals("about:blank") || pURL.equals("about:config") || pURL.startsWith("resource://") || pURL.startsWith("data")) {
            return pURL;
        }
        URL weburl;
        try {
            weburl = new URL(pURL);
            URLConnection result = weburl.openConnection();

            if (result instanceof HttpsURLConnection) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!pURL.startsWith("http://") && !pURL.startsWith("https://")) {
            if (pURL.startsWith("www")) {
                pURL = "http://" + pURL;
            } else {
                pURL = "http://" + pURL;
            }
        } else {
            if (pURL.startsWith("https://")) {
                return pURL;
            }
            pURL = pURL.replace("https://", "").replace("http://", "");
            if (pURL.startsWith("www")) {
                pURL = "http://" + pURL;
            } else {
                pURL = "http://" + pURL;
            }
        }
        return pURL;
    }

    public static String createRandomID() {
        return UUID.randomUUID().toString();
    }

    public static int createUniqueNotificationID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }

    public static int getScreenHeight(AppCompatActivity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size.y;
    }

    public static int getScreenWidth(AppCompatActivity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size.x;
    }

    public static SpannableString urlDesigner(boolean protocol, String url, Context pContext, int pDefColor, int pTheme, boolean sTorBrowsing) {

        int mColor;
        int mTextColor;
        if (pTheme == enums.Theme.THEME_DARK) {
            mColor = Color.argb(255, 0, 204, 71);
            mTextColor = Color.WHITE;
        } else {
            mColor = Color.argb(255, 0, 153, 54);
            mTextColor = Color.BLACK;
        }
        if(url.equals("about:blank")){
            SpannableString span = new SpannableString(url);
            span.setSpan(new ForegroundColorSpan(mTextColor), 0, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return span;
        }

        if (url.startsWith("https://") || url.startsWith("http://")) {
            if(url.startsWith("https://")){
                SpannableString span = new SpannableString(url);
                span.setSpan(new ForegroundColorSpan(mColor), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(Color.GRAY), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                if(pTheme != enums.Theme.THEME_DARK){
                    span.setSpan(new ForegroundColorSpan(Color.BLACK), getHost(url).length()+8, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else {
                    span.setSpan(new ForegroundColorSpan(Color.WHITE), getHost(url).length()+8, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                return span;
            }else {
                SpannableString span = new SpannableString(url);
                if(sTorBrowsing){
                    span.setSpan(new ForegroundColorSpan(mColor), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else {
                    span.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                span.setSpan(new ForegroundColorSpan(Color.GRAY), 4, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(Color.BLACK), getHost(url).length()+7, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                if(pTheme != enums.Theme.THEME_DARK){
                    span.setSpan(new ForegroundColorSpan(Color.BLACK), getHost(url).length()+7, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else {
                    span.setSpan(new ForegroundColorSpan(Color.WHITE), getHost(url).length()+7, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                return span;
            }
        } else {
            SpannableString ss = new SpannableString(url);

            if(pTheme != enums.Theme.THEME_DARK){
                ss.setSpan(new ForegroundColorSpan(Color.LTGRAY), getHost("http://"+url).length(), url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else {
                ss.setSpan(new ForegroundColorSpan(Color.GRAY), getHost("http://"+url).length(), url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return ss;
        }
    }

    public static void sendIssueEmail(Context context) {
        Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
        selectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"orionhiddentechnologies@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Issue Title");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Write Message Here....");
        emailIntent.setSelector(selectorIntent);
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public static void sendCustomMail(Context context, String pURL) {
        String mail = pURL.replaceFirst("mailto:", "");

        Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
        selectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "get transport obfs4");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "get transport obfs4");
        emailIntent.setSelector(selectorIntent);
        context.startActivity(Intent.createChooser(emailIntent, "get transport obfs4"));
    }


    public static void hideKeyboard(AppCompatActivity context) {
        if (context != null) {
            View view = context.findViewById(android.R.id.content);
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void shareApp(AppCompatActivity context) {
        ShareCompat.IntentBuilder.from(context)
                .setType("text/plain")
                .setChooserTitle("Hi! Check out this Awesome App")
                .setSubject("Hi! Check out this Awesome App")
                .setText("Orion | Onion Search | " + CONST_PLAYSTORE_URL)
                .startChooser();
    }

    public static int invertedShadeColor(int pColor, float pPercent) {
        int mColor = pColor;
        if (ColorUtils.calculateLuminance(pColor) <= 0.5) {
            if (ColorUtils.calculateLuminance(pColor) == 1) {
                return Color.DKGRAY;
            }
            int a = Color.alpha(mColor);
            int r = Math.round(Color.red(mColor) / pPercent);
            int g = Math.round(Color.green(mColor) / pPercent);
            int b = Math.round(Color.blue(mColor) / pPercent);
            return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
        } else {
            pPercent = pPercent + 0.05f;
            int a = (pColor >> 24) & 0xFF;
            int r = (int) (((pColor >> 16) & 0xFF) * pPercent);
            int g = (int) (((pColor >> 8) & 0xFF) * pPercent);
            int b = (int) ((pColor & 0xFF) * pPercent);

            return (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    public static int invertedGrayColor(int pColor) {
        if (ColorUtils.calculateLuminance(pColor) <= 0.5) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    public static double getColorDensity(int pColor) {
        return ColorUtils.calculateLuminance(pColor);
    }

    public static void shareApp(AppCompatActivity context, String p_share, String p_title) {
        ShareCompat.IntentBuilder.from(context)
                .setType("text/plain")
                .setChooserTitle("Hi! Check out this Awesome URL | " + p_title)
                .setSubject("Hi! Check out this Awesome URL | " + p_title)
                .setText("Website URL | " + p_share)
                .startChooser();
    }

    public static void shareURL(AppCompatActivity context, String p_share) {
        ShareCompat.IntentBuilder.from(context)
                .setType("text/plain")
                .setChooserTitle("Hi! Check out these Awesome URLS")
                .setSubject("Hi! Check out these Awesome URL")
                .setText("Website URL | " + p_share)
                .startChooser();
    }

    public static void openDownloadFolder(AppCompatActivity context) {
        Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            helperMethod.showToastMessage("Download Folder Not Found", context);
        }
    }

    public static String normalize(String url) {
        try {
            String pathSeparator = "/";

            URI uri = new URI(url);
            uri = uri.normalize();

            String path = uri.getPath();

            if (!path.startsWith(pathSeparator)) {
                path = pathSeparator + path;
            }

            if (path.endsWith(pathSeparator)) {
                path = path.substring(0, path.length() - 1);
            }

            String urlStr = uri.getScheme() + "://" + uri.getHost();
            int port = uri.getPort();

            if (port != -1) {
                urlStr = urlStr + ":" + port;
            }

            urlStr = urlStr + path;

            return urlStr;
        } catch (Exception ex) {
            return null;
        }
    }

    static public String getHost(String link) {
        URL url;
        try {
            if(link.contains("167.86.99.31") || link.contains("about:blank") || link.startsWith("resource")){
                return "";
            }
            url = new URL(link);
            return url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String removeLastSlash(String url) {
        if (url.length() > 2) {
            if (url.charAt(url.length() - 1) == '/') {
                return url.substring(0, url.length() - 1);
            }
        }
        return url;
    }

    public static String urlWithoutPrefix(String url) {
        try {
            url = url.substring(url.indexOf(getHost(url))).replace("www.", "").replace("m.", "");
            return url;
        } catch (Exception ex) {
            return url;
        }
    }

    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the side
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        display.getRealSize(size);

        return size;
    }

    public static void openDefaultBrowser(AppCompatActivity mContext) {
        DefaultBrowser mIconManager = new DefaultBrowser();
        mIconManager.openSetDefaultBrowserOption(mContext);
    }

    public static boolean isDefaultBrowserSet(AppCompatActivity mContext) {
        DefaultBrowser mIconManager = new DefaultBrowser();
        return mIconManager.getabcEnabledValue(mContext);
    }

    public static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static void openActivity(Class<?> cls, int type, AppCompatActivity context, boolean animation) {
        Intent myIntent = new Intent(context, cls);
        myIntent.putExtra(keys.PROXY_LIST_TYPE, type);
        if (!animation) {
            myIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
        }
        context.startActivity(myIntent);
    }

    public static void openIntent(Intent pIntent, AppCompatActivity pContext, int pType) {
        if (pType == CONST_LIST_EXTERNAL_SHORTCUT) {
            pIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pIntent.putExtra(M_ACTIVITY_NAVIGATION_BUNDLE_KEY, pType);
            pContext.startActivity(pIntent);
            pContext.overridePendingTransition(R.anim.fade_in_lang, R.anim.fade_out_lang);
        }
    }

    public static void onMinimizeApp(AppCompatActivity context) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }

    public static void openNotification(AppCompatActivity pContext) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", pContext.getPackageName());
        pContext.startActivity(intent);
    }

    public static boolean isDayMode(AppCompatActivity pContext) {
        if (pContext.getResources().getString(R.string.mode).equals("Day")) {
            return true;
        } else {
            return false;
        }
    }

    public static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (Exception ex) {
            return url;
        }
    }

    public static void openPlayStore(String packageName, AppCompatActivity context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            helperMethod.showToastMessage("Playstore Not Found", context);
        }

    }

    public static int pxFromDp(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void setImageDrawableWithAnimation(ImageView imageView, Drawable drawable, int duration) {
        Drawable currentDrawable = imageView.getDrawable();
        if (currentDrawable == null) {
            imageView.setImageDrawable(drawable);
            return;
        }

        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{
                currentDrawable,
                drawable
        });
        imageView.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(duration);
    }

    public static String getMimeType(String url, Context pContext) {
        String mimeType;
        Uri myUri = Uri.parse(url);
        if (ContentResolver.SCHEME_CONTENT.equals(myUri.getScheme())) {
            ContentResolver cr = pContext.getContentResolver();
            mimeType = cr.getType(myUri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(myUri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static void openFile(File url, Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {

                Uri uri = FileProvider.getUriForFile(context, "com.hiddenservices.onionservices.provider", url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri, Uri.parse(url.toString()).getScheme());
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(context), M_OPEN_ACTIVITY_FAILED);
            }
        } else {
            try {
                Uri uri = FileProvider.getUriForFile(context, "com.hiddenservices.onionservices.provider", url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(url), getMimeType(uri.toString(), context));
                context.startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(context), M_OPEN_ACTIVITY_FAILED);
            }
        }
    }

    public static void copyURL(String url, Context context) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("link", url);
        clipboard.setPrimaryClip(clip);

    }

    public static void showToastMessage(String message, Context context) {
        if (context != null) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    public static boolean checkPermissions(AppCompatActivity context) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1){
            return true;
        }
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded.toArray(new String[0]), 1050);
            return false;
        }
        return true;
    }

    public static String getCurrentDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        sdf.applyPattern("E | MMM dd,yyyy");
        return sdf.format(date);
    }

    public static Drawable getDrawableXML(Context pContext, int pSrc) {
        try {
            Drawable mDrawable;
            Resources res = pContext.getResources();
            mDrawable = Drawable.createFromXml(res, res.getXml(pSrc));
            return mDrawable;
        } catch (Exception ex) {
            return null;
        }
    }


    public static String getCurrentTime() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);

        System.out.printf("%d-%02d-%02d %02d:%02d:%02d.%03d", year, month, day, hour, minute, second, millis);
        return day + "/" + month + "/" + year + " | " + hour + ":" + minute + ":" + second;
    }

    public static PopupWindow onCreateMenu(View p_view, int p_layout, String pSettingLanguageRegion) {
        PopupWindow popupWindow;

        LayoutInflater layoutInflater
                = (LayoutInflater) p_view.getContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View popupView = layoutInflater.inflate(p_layout, null);


        popupWindow = new PopupWindow(
                popupView,
                ActionMenuView.LayoutParams.WRAP_CONTENT,
                ActionMenuView.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.popup_window_animation);

        if (pSettingLanguageRegion.equals("Ur")) {
            popupWindow.showAtLocation(p_view, Gravity.TOP | Gravity.START, 0, 0);
        } else {
            popupWindow.showAtLocation(p_view, Gravity.TOP | Gravity.END, 0, 0);
        }

        popupWindow.setElevation(7);

        return popupWindow;
    }

    public static void restart(boolean pOpenOnRestart, Context pContext) {
        ActivityManager manager = (ActivityManager) pContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager
                .getRunningAppProcesses();
        if (runningProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (!BuildConfig.APPLICATION_ID.equalsIgnoreCase(processInfo.processName)) {
                    android.os.Process.killProcess(processInfo.pid);
                }
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                super.run();

                PackageManager packageManager = pContext.getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(pContext.getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                mainIntent.putExtra(M_RESTART_APP_KEY, pOpenOnRestart);
                pContext.startActivity(mainIntent);
            }
        });
        Runtime.getRuntime().exit(0);
        System.exit(1);
    }

    public static void restartAndOpen(boolean pOpenOnRestart) {
        ActivityManager manager = (ActivityManager) activityContextManager.getInstance().getHomeController().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        if (runningProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (!BuildConfig.APPLICATION_ID.equalsIgnoreCase(processInfo.processName)) {
                    android.os.Process.killProcess(processInfo.pid);
                }
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                super.run();

                PackageManager packageManager = activityContextManager.getInstance().getHomeController().getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(activityContextManager.getInstance().getHomeController().getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                mainIntent.putExtra(M_RESTART_APP_KEY, pOpenOnRestart);
                activityContextManager.getInstance().getHomeController().startActivity(mainIntent);
                activityContextManager.getInstance().getHomeController().overridePendingTransition(R.anim.popup_scale_in, R.anim.popup_scale_out);
            }
        });

        Runtime.getRuntime().exit(0);
        System.exit(1);
    }

}
