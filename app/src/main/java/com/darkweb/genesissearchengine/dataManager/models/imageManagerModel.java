package com.darkweb.genesissearchengine.dataManager.models;

import android.graphics.Bitmap;
import java.util.Date;

public class imageManagerModel
{
    /*Private Variables*/

    private String mID;
    private String mCreationDate;
    private String mURL;
    private Bitmap mImage;

    /*Initializations*/
    public imageManagerModel(String pID, String pCreationDate, String pURL, Bitmap pImage){
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
