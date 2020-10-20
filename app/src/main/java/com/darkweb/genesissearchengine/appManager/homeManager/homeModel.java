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

        if(status.sSettingSearchStatus.equals(constants.CONST_BACKEND_GOOGLE_URL)){
            return getSearchEngine()+"search?q="+url.replaceAll(" ","+");
        }
        else if(status.sSettingSearchStatus.equals(constants.CONST_BACKEND_GENESIS_URL)){
            return getSearchEngine()+"/search?s_type=all&p_num=1&q="+url.replaceAll(" ","+");
        }
        else{
            return getSearchEngine()+"?q="+url.replaceAll(" ","+");
        }
    }


}
