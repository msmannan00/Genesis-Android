package org.torproject.android.service.wrapper;

public class logRowModel {
    /*Private Variables*/

    private String mLog;
    private String mDate;

    /*Initializations*/

    public logRowModel(String pLog, String pDate) {
        this.mLog = pLog;
        this.mDate = pDate;
    }

    /*Variable Setters*/

    public void setLog(String pLog){
        this.mLog = pLog;
    }
    public void setDate(String pDate) {
        mDate = pDate;
    }

    /*Variable Getters*/

    public String getLog() {
        return mLog;
    }
    public String getDate() {
        return mDate;
    }
}
