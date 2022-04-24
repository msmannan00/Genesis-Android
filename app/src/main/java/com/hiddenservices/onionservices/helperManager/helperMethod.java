package com.hiddenservices.onionservices.helperManager;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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

import com.example.myapplication.BuildConfig;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.geckoSession;
import com.hiddenservices.onionservices.appManager.kotlinHelperLibraries.defaultBrowser;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.libs.trueTime.trueTimeEncryption;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.example.myapplication.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.Key;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.net.ssl.HttpsURLConnection;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static com.hiddenservices.onionservices.constants.constants.CONST_LIST_EXTERNAL_SHORTCUT;
import static com.hiddenservices.onionservices.constants.constants.CONST_PACKAGE_NAME;
import static com.hiddenservices.onionservices.constants.constants.CONST_PLAYSTORE_URL;
import static com.hiddenservices.onionservices.constants.keys.M_ACTIVITY_NAVIGATION_BUNDLE_KEY;
import static com.hiddenservices.onionservices.constants.keys.M_RESTART_APP_KEY;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_OPEN_ACTIVITY_FAILED;

public class helperMethod
{
    /*Helper Methods General*/

    public static String getFileSizeBadge(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[] { "B Downloaded", "KB ⇣", "MB ⇣", "GB ⇣", "TB ⇣" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @SuppressLint({"UnspecifiedImmutableFlag", "LaunchActivityFromNotification"})
    public static PendingIntent onCreateActionIntent(Context pContext, Class<?> pBroadcastReciever, int pNotificationID, String pTitle, int pCommandID){
        PendingIntent pendingIntent;
        Intent pendingIntentTrigger = new Intent(pContext, pBroadcastReciever);
        pendingIntentTrigger.setAction(pTitle);
        pendingIntentTrigger.putExtra("N_ID", pNotificationID);
        pendingIntentTrigger.putExtra("N_COMMAND", pCommandID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(pContext, pNotificationID, pendingIntentTrigger, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }else{
            pendingIntent = PendingIntent.getBroadcast(pContext, pNotificationID, pendingIntentTrigger, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;
    }

    public static String setGenesisVerificationToken(String pString){
        try{
            if (pString.contains("?")){
                pString += "&"+ constants.CONST_GENESIS_GMT_TIME_GET_KEY+"="+ trueTimeEncryption.getInstance().getSecretToken();
            }else {
                pString += "?"+constants.CONST_GENESIS_GMT_TIME_GET_KEY+"="+trueTimeEncryption.getInstance().getSecretToken();
            }
            return pString;
        }catch (Exception ex){
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

    public static int getStatusBarHeight(AppCompatActivity pContext) {
        int result = 0;
        int resourceId = pContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = pContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static byte[] convertToBytes(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        bos.close();
        byte[] data = bos.toByteArray();
        return data;
    }



    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    public static String caesarCipherEncrypt(String pMessage, Key pSecretKey) {
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pSecretKey);
            byte[] cipherText = cipher.doFinal((pMessage + "__" + createRandomID()).getBytes());
            return new String(cipherText);
        }catch (Exception ex){
            return pMessage;
        }
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
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

    public static void onOpenHelpExternal(AppCompatActivity context, String pURL){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pURL));
        context.startActivity(browserIntent);
    }

    public static geckoSession deepCopy(geckoSession object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
            outputStrm.writeObject(object);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            return (geckoSession)objInputStream.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeToFile(String pFilePath, String content){
        Writer writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pFilePath), StandardCharsets.UTF_8));
            writer.write(content);
            writer.close();
        } catch (Exception ex) {
        }
    }

    public static String readFromFile(String pFilePath){
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
        }
        catch (IOException e) {
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

    public static String completeURL(String pURL){
        if(pURL.equals("about:blank") || pURL.equals("about:config") || pURL.startsWith("resource://")){
            return pURL;
        }
        URL weburl;
        try
        {
            weburl = new URL(pURL);
            URLConnection result = weburl.openConnection();

            if (result instanceof HttpsURLConnection) {

            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if(!pURL.startsWith("www.")&& !pURL.startsWith("http://")&& !pURL.startsWith("https://")){
            pURL = ""+pURL;
        }
        if(!pURL.startsWith("http://")&&!pURL.startsWith("https://")){
            if(pURL.startsWith("www")){
                pURL = "http://"+pURL;
            }else {
                pURL = "http://"+pURL;
            }
        }else {
            if(pURL.startsWith("https://")){
                return pURL;
            }
            pURL = pURL.replace("https://","").replace("http://","");
            if(pURL.startsWith("www")){
                pURL = "http://"+pURL;
            }else {
                pURL = "http://"+pURL;
            }
        }
        return pURL;
    }

    public static String createRandomID(){
        return UUID.randomUUID().toString();
    }

    public static int createUniqueNotificationID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
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

    public static SpannableString urlDesigner(String url, Context pContext, int pDefColor, int pTheme){

        int mColor = 0;
        if(pTheme == enums.Theme.THEME_DARK){
            mColor = Color.argb(255, 0, 204, 71);
        }else {
            mColor = Color.argb(255, 0, 153, 54);
        }

        if (url.startsWith("https://"))
        {
            SpannableString ss = new SpannableString(url);
            ss.setSpan(new ForegroundColorSpan(mColor), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ss;
        } else if (url.startsWith("http://"))
        {
            SpannableString ss = new SpannableString(url);
            ss.setSpan(new ForegroundColorSpan(mColor), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 4, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ss;
        } else
        {
            SpannableString ss = new SpannableString(url);
            return ss;
        }
    }

    public static String getDefaultBrowser(AppCompatActivity context){
        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(browserIntent,PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    public static void openURLInCustomBrowser(String pData, AppCompatActivity pContext){
        String mBrowser = helperMethod.getSystemBrowser(pContext);
        if(!mBrowser.equals(strings.GENERIC_EMPTY_STR)){
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(pData));
            intent.setPackage(mBrowser);
            pContext.startActivity(intent);
        }
    }

    public static String getSystemBrowser(AppCompatActivity context){
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.google.com"));
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        String mBrowser = strings.GENERIC_EMPTY_STR;
        for (ResolveInfo info : list) {
            if(!info.activityInfo.packageName.contains(CONST_PACKAGE_NAME)){
                mBrowser = info.activityInfo.packageName;
                if(info.activityInfo.packageName.contains("chrome") || info.activityInfo.packageName.contains("google") || info.activityInfo.packageName.contains("firefox")){
                    return mBrowser;
                }
            }
        }
        return mBrowser;
    }

    public static void sendIssueEmail(Context context){
        Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
        selectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"orionhiddentechnologies@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Issue Report");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Write Message Here....");
        emailIntent.setSelector( selectorIntent );
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public static void sendCustomMail(Context context, String pURL){
        String mail = pURL.replaceFirst("mailto:", "");

        Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
        selectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "get transport obfs4");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "get transport obfs4");
        emailIntent.setSelector( selectorIntent );
        context.startActivity(Intent.createChooser(emailIntent, "get transport obfs4"));
    }

    public static void sendBridgeEmail(Context context){
        Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
        selectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"bridges@torproject.org"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "get transport obfs4");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "get transport obfs4");
        emailIntent.setSelector( selectorIntent );
        context.startActivity(Intent.createChooser(emailIntent, "get transport obfs4"));
    }

    public static void onRevealView(View pView) {
        int cx = pView.getWidth();
        int cy = pView.getHeight();
        float finalRadius = (float) Math.hypot(cx, cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(pView, 100, 100, 0, finalRadius);
        pView.setVisibility(View.VISIBLE);
        anim.start();
    }
    public static void onHideView(View pView) {
        int cx = pView.getWidth();
        int cy = pView.getHeight();
        float initialRadius = (float) Math.hypot(cx, cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(pView, 100, 100, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                pView.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if(drawable==null){
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
    }

    public static void hideKeyboard(AppCompatActivity context) {
        if(context!=null){
            View view = context.findViewById(android.R.id.content);
            if (view != null)
            {
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
        if(ColorUtils.calculateLuminance(pColor) <= 0.5){
            if(ColorUtils.calculateLuminance(pColor)==1){
                return Color.DKGRAY;
            }
            int a = Color.alpha(mColor);
            int r = Math.round(Color.red(mColor) / pPercent);
            int g = Math.round(Color.green(mColor) / pPercent);
            int b = Math.round(Color.blue(mColor) / pPercent);
            return Color.argb(a,Math.min(r,255), Math.min(g,255), Math.min(b,255));
        }else{
            pPercent = pPercent + 0.05f;
            int a = (pColor >> 24) & 0xFF;
            int r = (int) (((pColor >> 16) & 0xFF) * pPercent);
            int g = (int) (((pColor >> 8) & 0xFF) * pPercent);
            int b = (int) ((pColor & 0xFF) * pPercent);

            return (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    public static int invertedGrayColor(int pColor) {
        if(ColorUtils.calculateLuminance(pColor) <= 0.5){
            return Color.WHITE;
        }else{
            return Color.BLACK;
        }
    }

    public static boolean isColorDark(int pColor) {
        if(ColorUtils.calculateLuminance(pColor) > 0.5){
            return true;
        }else{
            return false;
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

    public static void vibrate(AppCompatActivity context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(50);
    }

    public static void shareURL(AppCompatActivity context, String p_share) {
        ShareCompat.IntentBuilder.from(context)
                .setType("text/plain")
                .setChooserTitle("Hi! Check out these Awesome URLS")
                .setSubject("Hi! Check out these Awesome URL")
                .setText("Website URL | " + p_share)
                .startChooser();
    }

    public static void openDownloadFolder(AppCompatActivity context)
    {
        Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        if(intent.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivity(intent);
        }else {
            helperMethod.showToastMessage("Download Folder Not Found",context);
        }
    }

    public static String normalize(String url) {
        try{
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
        }catch (Exception ex){
            return null;
        }
    }

    static public String getHost(String link){
        URL url;
        try
        {
            url = new URL(link);
            return url.getHost();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return "";
        }

    }

    public static String langaugeWithoutTranslation(String pLangauge) {
        switch (pLangauge) {
            case "en_US":
                return "English (United States)";
            case "de_DE":
                return "German (Deutsche)";
            case "ca_ES":
                return "Catalan (Català)";
            case "zh_CN":
                return "Chinese （中文-中国）";
            case "ch_CZ":
                return "Czech （čeština）";
            case "nl_NL":
                return "Dutch （Netherland）";
            case "fr_FR":
                return "French （francaise）";
            case "el_GR":
                return "Greek （Ελληνικά）";
            case "hu_HU":
                return "Hungarian （Magyar）";
            case "in_ID":
                return "Indonesian （bahasa）";
            case "it_IT":
                return "Italian （Italiana）";
            case "ja_JP":
                return "Japanese （日本人）";
            case "ko_KR":
                return "Korean （韓国語）";
            case "pt_PT":
                return "Portuguese （Português）";
            case "ro_RO":
                return "Romanian （Română）";
            case "ru_RU":
                return "Russian （русский）";
            case "th_TH":
                return "Thai （ไทย）";
            case "tr_TR":
                return "Turkish （Türk）";
            case "uk_UA":
                return "Ukrainian （Український）";
            case "vi_VN":
                return "Vietnamese （Tiếng Việt）";
        }

        return "Not Defined";
    }
    public static Boolean isValidURL(String url)
    {
        Pattern p = Pattern.compile("((http|https)://)(www.)?[a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)");
        Matcher m;
        m=p.matcher(url);
        return m.matches();
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static String removeLastSlash(String url){
        if(url.length()>2){
            if(url.charAt(url.length()-1)=='/'){
                return url.substring(0,url.length()-1);
            }
        }
        return url;
    }

    public static String urlWithoutPrefix(String url){
        try{
            url = url.substring(url.indexOf(getHost(url))).replace("www.","").replace("m.","");
            return url;
        }catch (Exception ex){
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

    public static void openDefaultBrowser(AppCompatActivity mContext){
        defaultBrowser mIconManager = new defaultBrowser();
        mIconManager.openSetDefaultBrowserOption(mContext);
    }

    public static boolean isDefaultBrowserSet(AppCompatActivity mContext){
        defaultBrowser mIconManager = new defaultBrowser();
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

    public static void openActivity( Class<?> cls,int type,AppCompatActivity context,boolean animation){
        Intent myIntent = new Intent(context, cls);
        myIntent.putExtra(keys.PROXY_LIST_TYPE, type);
        if(!animation){
            myIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
        }
        context.startActivity(myIntent);
    }

    public static void openIntent(Intent pIntent,AppCompatActivity pContext, int pType){
        if(pType == CONST_LIST_EXTERNAL_SHORTCUT){
            pIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pIntent.putExtra(M_ACTIVITY_NAVIGATION_BUNDLE_KEY, pType);
            pContext.startActivity(pIntent);
            pContext.overridePendingTransition(R.anim.fade_in_lang, R.anim.fade_out_lang);
        }
    }

    public static void openActivityReverse( Class<?> cls,int type,AppCompatActivity context,boolean animation){
        Intent myIntent = new Intent(context, cls);
        myIntent.putExtra(keys.PROXY_LIST_TYPE, type);
        if(!animation){
            myIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
        }

        context.startActivity(myIntent);
    }

    public static void restartActivity( Intent pIntent, AppCompatActivity pContext){
        pContext.finish();
        pContext.startActivity(pIntent);
    }

    public static void onMinimizeApp(AppCompatActivity context){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }

    public static int screenWidth()
    {
        return (Resources.getSystem().getDisplayMetrics().widthPixels);
    }

    public static RotateAnimation getRotationAnimation(){
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        return rotate;
    }

    public static void openNotification(AppCompatActivity pContext)
    {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", pContext.getPackageName());
        pContext.startActivity(intent);
    }

    public static boolean isDayMode(AppCompatActivity pContext)
    {
        if(pContext.getResources().getString(R.string.mode).equals("Day")){
            return true;
        }else {
            return false;
        }
    }

    public static String getDomainName(String url)
    {
        try{
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        }catch (Exception ex){
            return url;
        }
    }

    public static void openPlayStore(String packageName,AppCompatActivity context)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id="+packageName));

        if(intent.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivity(intent);
        }else {
            helperMethod.showToastMessage("Playstore Not Found",context);
        }

    }

    public static int dpFromPx(final Context context, final float px) {
        return (int)(px / context.getResources().getDisplayMetrics().density);
    }

    public static int pxFromDp(float dp){
        return   (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void setImageDrawableWithAnimation(ImageView imageView, Drawable drawable, int duration) {
        Drawable currentDrawable = imageView.getDrawable();
        if (currentDrawable == null) {
            imageView.setImageDrawable(drawable);
            return;
        }

        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[] {
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            try {

                Uri uri = FileProvider.getUriForFile(context, "com.hiddenservices.onionservices.provider", url);
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url.toString()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri, Uri.parse(url.toString()).getScheme());
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(context), M_OPEN_ACTIVITY_FAILED);
            }
        } else{
            try {
                Uri uri = FileProvider.getUriForFile(context, "com.hiddenservices.onionservices.provider", url);
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url.toString()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(url), getMimeType(uri.toString(),context));
                context.startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(context), M_OPEN_ACTIVITY_FAILED);
            }
        }
    }
    public static void copyURL(String url,Context context){

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("link", url);
        clipboard.setPrimaryClip(clip);

    }

    public static void showToastMessage(String message,Context context){
        if(context!=null){
            Toast toast=Toast.makeText(context.getApplicationContext(),message,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    public static boolean checkPermissions(AppCompatActivity context) {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
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
            ActivityCompat.requestPermissions(context, listPermissionsNeeded.toArray(new String[0]), 100);
            return false;
        }
        return true;
    }

    public static String getCurrentDate(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        sdf.applyPattern("E | MMM dd,yyyy");
        return sdf.format(date);
    }

    public static Drawable getDrawableXML(Context pContext, int pSrc)  {
        try {
            Drawable mDrawable;
            Resources res = pContext.getResources();
            mDrawable = Drawable.createFromXml(res, res.getXml(pSrc));
            return mDrawable;
        }catch (Exception ex){
            return null;
        }
    }



    public static String getCurrentTime(){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);

        System.out.printf("%d-%02d-%02d %02d:%02d:%02d.%03d", year, month, day, hour, minute, second, millis);
        return month + "/" + year + " | " + hour + ":" + minute + ":" + second;
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

        if(pSettingLanguageRegion.equals("Ur")){
            popupWindow.showAtLocation(p_view, Gravity.TOP|Gravity.START,0,0);
        }else {
            popupWindow.showAtLocation(p_view, Gravity.TOP|Gravity.END,0,0);
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
                pContext.getApplicationContext().startActivity(mainIntent);
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
                activityContextManager.getInstance().getHomeController().getApplicationContext().startActivity(mainIntent);
                activityContextManager.getInstance().getHomeController().overridePendingTransition(R.anim.popup_scale_in, R.anim.popup_scale_out);
            }
        });
        Runtime.getRuntime().exit(0);
        System.exit(1);
    }

}
