package com.darkweb.genesissearchengine.appManager.homeManager.homeController;

import android.util.Patterns;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

import java.net.URL;

class homeModel
{

    String getSearchEngine(){
        return status.sSettingSearchStatus;
    }

    String urlComplete(String pURL, String pSearchEngine){
        if(pURL.equals("about:config")){
            return pURL;
        }else {
            try
            {
                String updateUrl = helperMethod.completeURL(pURL);
                URL host = new URL(updateUrl);
                boolean isUrlValid = Patterns.WEB_URL.matcher(updateUrl).matches();
                if(isUrlValid && host.getHost().replace("www.","").contains("."))
                {
                    return null;
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
