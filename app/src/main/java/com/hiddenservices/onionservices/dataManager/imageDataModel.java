package com.hiddenservices.onionservices.dataManager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hiddenservices.onionservices.dataManager.models.imageRowModel;
import com.hiddenservices.onionservices.helperManager.helperMethod;

import org.torproject.android.service.wrapper.orbotLocalConstants;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hiddenservices.onionservices.constants.constants.*;
import static com.hiddenservices.onionservices.constants.enums.ImageQueueStatus.*;

@SuppressLint("CommitPrefEdits")
class imageDataModel {

    /* Local Variables */

    private Map<String, imageRowModel> mImageCache;
    private Map<String, Integer> mParsedQueues;
    private ArrayList<String> mRequestQueue;

    /* Initializations */

    public imageDataModel() {
        mImageCache = new HashMap<>();
        mParsedQueues = new HashMap<>();
        mRequestQueue = new ArrayList<>();

        mBackgroundThread();
    }

    /* Helper Methods */

    private void onRequestImage(String mURL) {
        String mCraftedURL = helperMethod.completeURL(helperMethod.getDomainName(mURL)) + "/favicon.ico";
        if (!mParsedQueues.containsKey(mCraftedURL)) {
            mRequestQueue.add(mCraftedURL);
        }
    }

    private Bitmap getImage(String mURL) {
        String mCraftedURL = helperMethod.completeURL(helperMethod.getDomainName(mURL)) + "/favicon.ico";
        if (!mParsedQueues.containsKey(mCraftedURL) || mParsedQueues.get(mCraftedURL) == M_IMAGE_LOADING_FAILED) {
            return null;
        } else {
            return mImageCache.get(mCraftedURL).getImage();
        }
    }

    public void mBackgroundThread() {
        /*new Thread(){
            public void run(){
                while (true) {
                    try {
                        sleep(50);
                        if(mRequestQueue.size()>0){
                            mParsedQueues.put(mRequestQueue.get(0), enums.ImageQueueStatus.M_IMAGE_LOADING);
                            Bitmap mBitmap = getBitmapFromURL(mRequestQueue.get(0));
                            if(mBitmap==null){
                                mParsedQueues.put(mRequestQueue.get(0), M_IMAGE_LOADING_FAILED);
                            }else {
                                mParsedQueues.put(mRequestQueue.get(0), M_IMAGE_LOADED_SUCCESSFULLY);
                                mImageCache.put(mRequestQueue.get(0), new imageRowModel(helperMethod.createRandomID(), "",mRequestQueue.get(0),mBitmap));
                            }
                            mRequestQueue.remove(0);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();*/
    }

    /* External Triggers */

    public Object onTrigger(dataEnums.eImageCommands pCommands, List<Object> pData) {


        return null;
    }

}
