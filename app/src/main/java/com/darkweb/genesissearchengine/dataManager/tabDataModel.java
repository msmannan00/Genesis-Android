package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Handler;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.tabManager.tabRowModel;
import org.mozilla.geckoview.GeckoResult;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@SuppressLint("CommitPrefEdits")
class tabDataModel
{
    private ArrayList<tabRowModel> mTabs = new ArrayList<>();
    ArrayList<tabRowModel> getTab(){
        return mTabs;
    }

    /*List Tabs*/

    void initializeTab(ArrayList<tabRowModel> pTabMdel){
        mTabs.addAll(pTabMdel);
    }

    geckoSession getHomePage(){
        if(mTabs.size()>0){
            return mTabs.get(0).getSession();
        }else {
            return null;
        }
    }

    void addTabs(geckoSession mSession,boolean pIsDataSavable){
        tabRowModel mTabModel = new tabRowModel(mSession);
        mTabs.add(0,mTabModel);

        if(mTabs.size()>20){
            closeTab(mTabs.get(mTabs.size()-1).getSession());
        }

        if(pIsDataSavable){
            String[] params = new String[3];
            params[0] = mTabModel.getSession().getTitle();
            params[1] = mTabModel.getSession().getCurrentURL();
            params[2] = mTabModel.getSession().getTheme();
            String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());

            databaseController.getInstance().execSQL("INSERT INTO tab(mid,date,title,url,theme) VALUES('"+ mTabModel.getmId() +"','" + m_date + "',?,?,?);",params);
        }
    }

    void clearTab() {
        int size = mTabs.size();
        for(int counter = 0; counter< size; counter++){
            if(mTabs.size()>0){
                mTabs.get(0).getSession().stop();
                mTabs.get(0).getSession().closeSession();
                mTabs.remove(0);
            }
        }
        if(mTabs.size()>0){
            mTabs.get(0).getSession().closeSession();
        }

        databaseController.getInstance().execSQL("DELETE FROM tab WHERE 1",null);

    }

    void closeTab(geckoSession mSession) {
        for(int counter = 0; counter< mTabs.size(); counter++){
            if(mTabs.get(counter).getSession().getSessionID().equals(mSession.getSessionID()))
            {
                databaseController.getInstance().execSQL("DELETE FROM tab WHERE mid='" + mTabs.get(counter).getmId() + "'",null);
                mTabs.remove(counter);
                break;
            }
        }
    }

    void moveTabToTop(geckoSession mSession) {

        for(int counter = 0; counter< mTabs.size(); counter++){

            if(mTabs.get(counter).getSession().getSessionID().equals(mSession.getSessionID()))
            {
                String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                databaseController.getInstance().execSQL("UPDATE tab SET date = '" + m_date + "' WHERE mid='"+mTabs.get(counter).getmId() + "'",null);
                mTabs.add(0,mTabs.remove(counter));
                break;
            }
        }
    }

    boolean updateTab(String mSessionID, geckoSession pSession) {

        boolean mSessionUpdated = false;
        for(int counter = 0; counter< mTabs.size(); counter++){

            if(mTabs.get(counter).getSession().getSessionID().equals(mSessionID))
            {
                String[] params = new String[3];
                params[0] = mTabs.get(counter).getSession().getTitle();
                params[1] = mTabs.get(counter).getSession().getCurrentURL();
                params[2] = mTabs.get(counter).getSession().getTheme();
                mSessionUpdated = true;

                String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                databaseController.getInstance().execSQL("UPDATE tab SET date = '" + m_date + "'  , url = ? , title = ?, theme = ?  WHERE mid='"+mTabs.get(counter).getmId() + "'",params);
                return true;
            }
        }
        if(!mSessionUpdated){
            addTabs(pSession, true);
        }
        return false;
    }


    tabRowModel getCurrentTab(){
        if(mTabs.size()>0){
            return mTabs.get(0);
        }
        else {
            return null;
        }
    }

    tabRowModel getLastTab(){
        if(mTabs.size()>0){
            return mTabs.get(mTabs.size()-1);
        }
        else {
            return null;
        }
    }

    public void updatePixels(String pSessionID, GeckoResult<Bitmap> pBitmapManager){
        for(int counter = 0; counter< mTabs.size(); counter++){
            if(mTabs.get(counter).getSession().getSessionID().equals(pSessionID))
            {
                final Handler handler = new Handler();
                int finalCounter = counter;
                int finalCounter1 = counter;
                handler.postDelayed(() ->
                {
                    try {
                        Bitmap mBitmap = pBitmapManager.poll(500);
                        mTabs.get(finalCounter).setmBitmap(mBitmap);


                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        byte[] mThumbnail = bos.toByteArray();

                        ContentValues mContentValues = new  ContentValues();
                        mContentValues.put("mThumbnail", mThumbnail);

                        databaseController.getInstance().execTab("tab",mContentValues, mTabs.get(finalCounter1).getmId());

                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }, 500);
            }
        }
    }

    public ArrayList<ArrayList<String>> getSuggestions(String pQuery){
        ArrayList<ArrayList<String>> mModel = new ArrayList<>();
        for(int count = 0; count<= mTabs.size()-1 && mTabs.size()<500; count++){
            if(mTabs.get(count).getSession().getTitle().toLowerCase().contains(pQuery) || mTabs.get(count).getSession().getCurrentURL().toLowerCase().contains(pQuery)){
                ArrayList<String> mTempModel = new ArrayList<>();
                mTempModel.add(mTabs.get(count).getSession().getTitle().toLowerCase());
                mTempModel.add(mTabs.get(count).getSession().getCurrentURL());
                mModel.add(mTempModel);
            }
        }
        return mModel;
    }

    int getTotalTabs(){
        return mTabs.size();
    }

    /*List Suggestion*/
    public Object onTrigger(dataEnums.eTabCommands pCommands, List<Object> pData){
        if(pCommands == dataEnums.eTabCommands.GET_TOTAL_TAB){
            return getTotalTabs();
        }
        else if(pCommands == dataEnums.eTabCommands.GET_CURRENT_TAB){
            return getCurrentTab();
        }
        else if(pCommands == dataEnums.eTabCommands.GET_LAST_TAB){
            return getLastTab();
        }
        else if(pCommands == dataEnums.eTabCommands.MOVE_TAB_TO_TOP){
            moveTabToTop((geckoSession)pData.get(0));
        }
        else if(pCommands == dataEnums.eTabCommands.CLOSE_TAB){
            closeTab((geckoSession)pData.get(0));
        }
        else if(pCommands == dataEnums.eTabCommands.M_CLEAR_TAB){
            clearTab();
        }
        else if(pCommands == dataEnums.eTabCommands.M_ADD_TAB){
            addTabs((geckoSession)pData.get(0), (boolean)pData.get(1));
        }
        else if(pCommands == dataEnums.eTabCommands.M_UPDATE_TAB){
            updateTab((String) pData.get(1), (geckoSession) pData.get(5));
        }
        else if(pCommands == dataEnums.eTabCommands.GET_TAB){
            return getTab();
        }
        else if(pCommands == dataEnums.eTabCommands.M_GET_SUGGESTIONS){
            return getSuggestions((String) pData.get(0));
        }
        else if(pCommands == dataEnums.eTabCommands.M_UPDATE_PIXEL){
            updatePixels((String)pData.get(0), (GeckoResult<Bitmap>)pData.get(1));
        }
        else if(pCommands == dataEnums.eTabCommands.M_HOME_PAGE){
            return getHomePage();
        }

        return null;
    }

}
