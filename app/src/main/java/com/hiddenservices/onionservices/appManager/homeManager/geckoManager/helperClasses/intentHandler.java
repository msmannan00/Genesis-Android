package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import org.mozilla.geckoview.GeckoSession;
import java.lang.ref.WeakReference;
import java.util.List;

public class intentHandler {

    public static boolean actionIntent(String pIntentHandler, WeakReference<AppCompatActivity> mContext) {
        if(pIntentHandler.startsWith("tel:")){
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(pIntentHandler));
            mContext.get().startActivity(intent);
            return false;
        }
        if(pIntentHandler.startsWith("intent:")){
            try {
                if(status.sSettingTrackingProtection == 0){
                    Uri data = Uri.parse(pIntentHandler);
                    Intent intent = new Intent(Intent.ACTION_VIEW, data);
                    String packageName = data.getQueryParameter("package");
                    if (packageName != null && !packageName.isEmpty()) {
                        intent.setPackage(packageName);
                    }
                    mContext.get().startActivity(intent);
                }else {
                    helperMethod.showToastMessage("Tracking protection blocking request", mContext.get());
                }
            } catch (ActivityNotFoundException e) {
                helperMethod.showToastMessage("Requested application not found on device", mContext.get());
            }
        }
        return true;
    }

    public static String getSecurityInfo(GeckoSession.ProgressDelegate.SecurityInformation securityInfo) {
        if (securityInfo != null && securityInfo.certificate != null) {

            StringBuilder mAlternativeNames = new StringBuilder(strings.GENERIC_EMPTY_STR);
            try {
                for (List<?> name : securityInfo.certificate.getSubjectAlternativeNames()) {
                    mAlternativeNames.append(name.get(1)).append("<br>");
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
