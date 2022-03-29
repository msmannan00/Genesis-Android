package com.hiddenservices.onionservices.dataManager.models;

import android.graphics.Bitmap;

public class imageRowModel
{
    /*Private Variables*/

    private String mID;
    private String mCreationDate;
    private String mURL;
    private Bitmap mImage;

    /*Initializations*/
    public imageRowModel(String pID, String pCreationDate, String pURL, Bitmap pImage){
        mID = pID;
        mCreationDate = pCreationDate;
        mURL = pURL;
        mImage = pImage;
    }

    /*Getter Setters*/
    public Bitmap getImage(){
        return mImage;
    }
}
