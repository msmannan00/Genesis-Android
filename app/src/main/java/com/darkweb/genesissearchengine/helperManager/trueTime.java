package com.darkweb.genesissearchengine.helperManager;


import android.widget.Toast;

import com.darkweb.genesissearchengine.constants.strings;
import com.instacart.library.truetime.TrueTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
        Date deviceTime = new Date();

        return System.currentTimeMillis()+strings.GENERIC_EMPTY_STR;
    }

    private String _formatDate(Date date, String pattern, TimeZone timeZone) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        format.setTimeZone(timeZone);
        return format.format(date);
    }

}
