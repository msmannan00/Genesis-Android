package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.dataManager.models.imageManagerModel;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.darkweb.genesissearchengine.constants.constants.CONST_PROXY_SOCKS;
import static com.darkweb.genesissearchengine.constants.enums.ImageQueueStatus.M_IMAGE_LOADED_SUCCESSFULLY;
import static com.darkweb.genesissearchengine.constants.enums.ImageQueueStatus.M_IMAGE_LOADING_FAILED;

@SuppressLint("CommitPrefEdits")
class imageDataModel
{
    private Map<String, imageManagerModel> mImageCache;
    private Map<String, Integer> mParsedQueues;
    private ArrayList<String> mRequestQueue;

    public imageDataModel(){
        mImageCache = new HashMap<>();
        mParsedQueues = new HashMap<>();
        mRequestQueue = new ArrayList<>();







        mBackgroundThread();
    }


    private void onRequestImage(String mURL){
        String mCraftedURL = helperMethod.completeURL(helperMethod.getDomainName(mURL))+"/favicon.ico";
        if(!mParsedQueues.containsKey(mCraftedURL)){
            mRequestQueue.add(mCraftedURL);
        }
    }

    private Bitmap getImage(String mURL){
        String mCraftedURL = helperMethod.completeURL(helperMethod.getDomainName(mURL))+"/favicon.ico";
        if(!mParsedQueues.containsKey(mCraftedURL) || mParsedQueues.get(mCraftedURL) == M_IMAGE_LOADING_FAILED){
            return null;
        }else{
            return mImageCache.get(mCraftedURL).getImage();
        }
    }

    public void mBackgroundThread(){
        new Thread(){
            public void run(){
                while (true) {
                    try {
                        sleep(1000);
                        if(mRequestQueue.size()>0){
                            mParsedQueues.put(mRequestQueue.get(0), enums.ImageQueueStatus.M_IMAGE_LOADING);
                            Bitmap mBitmap = getBitmapFromURL(mRequestQueue.get(0));
                            if(mBitmap==null){
                                mParsedQueues.put(mRequestQueue.get(0), enums.ImageQueueStatus.M_IMAGE_LOADING_FAILED);
                            }else {
                                mParsedQueues.put(mRequestQueue.get(0), M_IMAGE_LOADED_SUCCESSFULLY);
                                mImageCache.put(mRequestQueue.get(0), new imageManagerModel(helperMethod.createRandomID(), "",mRequestQueue.get(0),mBitmap));
                            }
                            mRequestQueue.remove(0);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(CONST_PROXY_SOCKS, 9050));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setConnectTimeout(10000);
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*List Suggestion*/
    public Object onTrigger(dataEnums.eImageCommands pCommands, List<Object> pData){

        if(pCommands.equals(dataEnums.eImageCommands.M_REQUEST_IMAGE_URL)){
            onRequestImage((String) pData.get(0));
        }
        else if(pCommands.equals(dataEnums.eImageCommands.M_GET_IMAGE)){
            return getImage((String) pData.get(0));
        }

        return null;
    }

}
