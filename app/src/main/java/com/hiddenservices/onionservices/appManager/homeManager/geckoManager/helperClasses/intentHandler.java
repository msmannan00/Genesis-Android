package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import java.lang.ref.WeakReference;

public class intentHandler {

    public static void actionDial(String pIntentHander, WeakReference<AppCompatActivity> mContext) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(pIntentHander));
        mContext.get().startActivity(intent);
    }
}
