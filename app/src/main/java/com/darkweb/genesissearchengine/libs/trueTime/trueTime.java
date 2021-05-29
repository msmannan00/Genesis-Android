package com.darkweb.genesissearchengine.libs.trueTime;

import com.darkweb.genesissearchengine.constants.strings;
import com.instacart.library.truetime.TrueTime;
import java.util.Date;

public class trueTime {

    private static trueTime ourInstance = new trueTime();
    public static trueTime getInstance()
    {
        return ourInstance;
    }

    public void initTime(){
        try{
            TrueTime.build().initialize();
        }catch (Exception ignored){ }
    }


    public String getGMT(){
        if (TrueTime.isInitialized()) {
            Date trueTime = TrueTime.now();
            return trueTime.getTime()+strings.GENERIC_EMPTY_STR;
        }else {
            return "null";
        }
    }

    public String getLTZ(){
        return System.currentTimeMillis()+strings.GENERIC_EMPTY_STR;
    }

}
