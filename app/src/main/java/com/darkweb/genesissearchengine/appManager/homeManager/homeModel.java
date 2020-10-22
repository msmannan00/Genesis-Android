package com.darkweb.genesissearchengine.appManager.homeManager;

import android.util.Patterns;

import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

import java.net.URL;

class homeModel
{

    String getSearchEngine(){
        return status.sSettingSearchStatus;
    }

    String urlComplete(String url){
        try
        {
            String updateUrl = helperMethod.completeURL(url);
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

        return constants.CONST_BACKEND_GOOGLE_URL.replace("$s",url.replaceAll(" ","+"));
    }


}
