package com.hiddenservices.genesissearchengine.production.appManager.homeManager.homeController;

import android.util.Patterns;

import com.hiddenservices.genesissearchengine.production.constants.status;
import com.hiddenservices.genesissearchengine.production.helperManager.helperMethod;

import java.net.URL;

class homeModel
{

    String getSearchEngine(){
        return status.sSettingDefaultSearchEngine;
    }

    String urlComplete(String pURL, String pSearchEngine){
        if(pURL.startsWith("data")){
            return pURL;
        }
        if(pURL.equals("about:blank")){
            return helperMethod.completeURL("trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion");
        }
        else if(pURL.equals("about:config")){
            return pURL;
        }else {
            try
            {
                String updateUrl = helperMethod.completeURL(pURL);
                URL host = new URL(updateUrl);
                boolean isUrlValid = Patterns.WEB_URL.matcher(updateUrl).matches();
                if(isUrlValid && host.getHost().replace("www.","").contains("."))
                {
                    return pURL;
                }
            }

            catch (Exception ex){
                ex.printStackTrace();
            }

            String mURL = pSearchEngine.replace("$s",pURL.replaceAll(" ","+"));
            return mURL;
        }
    }
}
