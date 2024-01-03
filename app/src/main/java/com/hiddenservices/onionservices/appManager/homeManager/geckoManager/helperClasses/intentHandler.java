package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import org.mozilla.geckoview.GeckoSession;
import java.lang.ref.WeakReference;
import java.util.List;

public class intentHandler {

    public static void actionDial(String pIntentHander, WeakReference<AppCompatActivity> mContext) {
        if(pIntentHander.startsWith("tel:")){
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(pIntentHander));
            mContext.get().startActivity(intent);
        }
    }

    public static String getSecurityInfo(GeckoSession.ProgressDelegate.SecurityInformation securityInfo) {
        if (securityInfo != null && securityInfo.certificate != null) {

            String mAlternativeNames = strings.GENERIC_EMPTY_STR;
            try {
                for (List name : securityInfo.certificate.getSubjectAlternativeNames()) {
                    mAlternativeNames = mAlternativeNames + name.get(1) + "<br>";
                }

            } catch (Exception ignored) {
            }

            return "<br><b>Website</b><br>" + securityInfo.host + "<br><br>" +
                    "<b>Serial Number</b><br>" + securityInfo.certificate.getSerialNumber() + "<br><br>" +
                    "<b>Algorithm Name</b><br>" + securityInfo.certificate.getSigAlgName() + "<br><br>" +
                    "<b>Issued On</b><br>" + securityInfo.certificate.getNotBefore() + "<br><br>" +
                    "<b>Expires On</b><br>" + securityInfo.certificate.getNotAfter() + "<br><br>" +
                    "<b>Organization (O)</b><br>" + securityInfo.certificate.getSubjectDN().getName() + "<br><br>" +
                    "<b>Common Name (CN)</b><br>" + securityInfo.certificate.getIssuerDN().getName() + "<br><br>" +
                    "<b>Subject Alternative Names</b><br>" + mAlternativeNames;
        } else {
            if(status.sTorBrowsing){
                return "Tor Secured Connection";
            }else {
                return "Connection Not Secured";
            }
        }
    }

}
