package com.hiddenservices.onionservices.appManager.homeManager.homeController;

import static com.hiddenservices.onionservices.constants.constants.CONST_PRIVACY_POLICY_URL_NON_TOR;
import static com.hiddenservices.onionservices.constants.constants.CONST_REPORT_URL;

import android.util.Patterns;

import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.helperManager.helperMethod;

import java.net.URL;

public class homeModel {

    public String getSearchEngine() {
        return status.sSettingDefaultSearchEngine;
    }

    public String urlComplete(String pURL, String pSearchEngine) {
        if (!pURL.startsWith(CONST_REPORT_URL) && (pURL.startsWith("resource://android/assets/homepage/") || pURL.startsWith("http://167.86.99.31/?pG") || pURL.startsWith("https://167.86.99.31?pG") || pURL.endsWith("167.86.99.31") || pURL.endsWith(constants.CONST_GENESIS_DOMAIN_URL_SLASHED))) {
            return pURL;
        }
        if (pURL.startsWith(CONST_PRIVACY_POLICY_URL_NON_TOR)) {
            return CONST_PRIVACY_POLICY_URL_NON_TOR;
        }
        if (pURL.startsWith("data") || pURL.startsWith("http://data") || pURL.startsWith("https://data")) {
            return pURL;
        }
        if (pURL.equals("about:blank")) {
            return helperMethod.completeURL("167.86.99.31");
        } else if (pURL.equals("about:config")) {
            return pURL;
        } else {
            try {
                String updateUrl = helperMethod.completeURL(pURL);
                URL host = new URL(updateUrl);
                boolean isUrlValid = Patterns.WEB_URL.matcher(updateUrl).matches();
                if (isUrlValid && host.getHost().replace("www.", "").contains(".")) {
                    return pURL;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            String mURL = pSearchEngine.replace("$s", pURL.replaceAll(" ", "+"));
            mURL = mURL.replace("167.86.99.31","juhanurmihxlp77nkq76byazcldy2hlmovfu2epvl5ankdibsot4csyd.onion");
            return mURL;
        }
    }
}
