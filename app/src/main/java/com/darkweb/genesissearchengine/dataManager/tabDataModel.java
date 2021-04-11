package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.NestedGeckoView;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.tabManager.tabRowModel;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoView;

import java.io.ByteArrayInputStream;
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

    int addTabs(geckoSession mSession,boolean pIsDataSavable){
        tabRowModel mTabModel = new tabRowModel(mSession);
        mTabs.add(0,mTabModel);

        if(mTabs.size()>20){
            closeTab(mTabs.get(mTabs.size()-1).getSession(), mTabs.get(mTabs.size()-1).getmId());
            return enums.AddTabCallback.TAB_FULL;
        }

        if(pIsDataSavable){
            String[] params = new String[3];
            params[0] = mTabModel.getSession().getTitle();
            params[1] = mTabModel.getSession().getCurrentURL();
            params[2] = mTabModel.getSession().getTheme();
            String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());

            databaseController.getInstance().execSQL("REPLACE INTO tab(mid,date,title,url,theme) VALUES('"+ mTabModel.getmId() +"','" + m_date + "',?,?,?);",params);
        }
        return enums.AddTabCallback.TAB_ADDED;
    }

    void clearTab() {
        int size = mTabs.size();
        for(int counter = 0; counter< size; counter++){
            if(mTabs.size()>0){
                mTabs.get(0).getSession().stop();
                mTabs.get(0).getSession().close();
                mTabs.remove(0);
            }
        }
        if(mTabs.size()>0){
            mTabs.get(0).getSession().close();
            mTabs.remove(0);
        }

        databaseController.getInstance().execSQL("DELETE FROM tab WHERE 1",null);

    }

    void closeTab(geckoSession mSession,Object pID) {
        mSession.stop();
        mSession.close();
        if(pID == null){
            String mID = strings.GENERIC_EMPTY_STR;
            for(int counter = 0; counter< mTabs.size(); counter++){
                if(mTabs.get(counter).getSession().getSessionID().equals(mSession.getSessionID()))
                {
                    mTabs.get(counter).getSession().stop();
                    mTabs.get(counter).getSession().close();
                    mTabs.remove(counter);
                    mID = mTabs.get(counter).getmId();
                    break;
                }
            }
            databaseController.getInstance().execSQL("DELETE FROM tab WHERE mid='" + mID + "'",null);
        }else {
            for(int counter = 0; counter< mTabs.size(); counter++){
                if(mTabs.get(counter).getSession().getSessionID().equals(mSession.getSessionID()))
                {
                    mTabs.get(counter).getSession().stop();
                    mTabs.get(counter).getSession().close();
                    mTabs.remove(counter);
                    break;
                }
            }
            databaseController.getInstance().execSQL("DELETE FROM tab WHERE mid='" + pID + "'",null);
        }
    }

    void moveTabToTop(geckoSession mSession) {
        for(int counter = 0; counter< mTabs.size(); counter++){

            try{
                if(mTabs.get(counter).getSession().getSessionID().equals(mSession.getSessionID()))
                {
                    String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                    databaseController.getInstance().execSQL("UPDATE tab SET date = '" + m_date + "' WHERE mid='"+mTabs.get(counter).getmId() + "'",null);
                    mTabs.add(0,mTabs.remove(counter));
                    break;
                }
            }catch (Exception ex){
                Log.i(ex.getMessage(), ex.getMessage());
            }
        }
    }

    boolean updateTab(String mSessionID, geckoSession pSession) {

        for(int counter = 0; counter< mTabs.size(); counter++){

            if(mTabs.get(counter).getSession().getSessionID().equals(mSessionID))
            {
                String[] params = new String[3];
                params[0] = mTabs.get(counter).getSession().getTitle();
                params[1] = mTabs.get(counter).getSession().getCurrentURL();
                params[2] = mTabs.get(counter).getSession().getTheme();
                String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                databaseController.getInstance().execSQL("UPDATE tab SET date = '" + m_date + "'  , url = ? , title = ?, theme = ?  WHERE mid='"+mTabs.get(counter).getmId() + "'",params);
                return true;
            }
        }
        addTabs(pSession, true);
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

    tabRowModel getRecentTab(){
        if(mTabs.size()>1){
            return mTabs.get(1);
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

    @SuppressLint("HandlerLeak")
    static Handler handler = new Handler()
    {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
        }

        @NonNull
        @Override
        public String getMessageName(@NonNull Message message) {
            return super.getMessageName(message);
        }

        @Override
        public boolean sendMessageAtTime(@NonNull Message msg, long uptimeMillis) {
            return super.sendMessageAtTime(msg, uptimeMillis);
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public void handleMessage(Message msg)
        {
            Log.i("FUCK","FUCK");
        }
    };

    // int isLoading = 0;
    public void updatePixels(String pSessionID, GeckoResult<Bitmap> pBitmapManager, ImageView pImageView, GeckoView pGeckoView, boolean pOpenTabView){

        new Thread(){
            public void run(){
                try {
                    for(int counter = 0; counter< mTabs.size(); counter++) {
                        int finalCounter = counter;
                        if (mTabs.get(counter).getSession().getSessionID().equals(pSessionID)) {
                            GeckoResult<Bitmap> mResult = pBitmapManager.withHandler(handler);
                            Bitmap mBitmap = pBitmapManager.poll(0);

                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            mBitmap.compress(Bitmap.CompressFormat.PNG, 20, out);
                            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));


                            Bitmap emptyBitmap = Bitmap.createBitmap(decoded.getWidth(), decoded.getHeight(), decoded.getConfig());
                            if (!decoded.sameAs(emptyBitmap)) {

                                mTabs.get(finalCounter).setmBitmap(decoded);

                                if(pImageView!=null){
                                    activityContextManager.getInstance().getHomeController().runOnUiThread(() -> {
                                        pImageView.setImageBitmap(mBitmap);
                                    });
                                }

                                byte[] mThumbnail = out.toByteArray();
                                ContentValues mContentValues = new  ContentValues();
                                mContentValues.put("mThumbnail", mThumbnail);
                                databaseController.getInstance().execTab("tab",mContentValues, mTabs.get(finalCounter).getmId());
                            }
                        }
                    }

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                if(pOpenTabView){
                    activityContextManager.getInstance().getHomeController().onLoadFirstElement();
                }

            }
        }.start();
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
        else if(pCommands == dataEnums.eTabCommands.GET_RECENT_TAB){
            return getRecentTab();
        }
        else if(pCommands == dataEnums.eTabCommands.GET_LAST_TAB){
            return getLastTab();
        }
        else if(pCommands == dataEnums.eTabCommands.MOVE_TAB_TO_TOP){
            moveTabToTop((geckoSession)pData.get(0));
        }
        else if(pCommands == dataEnums.eTabCommands.CLOSE_TAB){
            closeTab((geckoSession)pData.get(0), pData.get(1));
            activityContextManager.getInstance().getHomeController().initTabCountForced();
        }
        else if(pCommands == dataEnums.eTabCommands.M_CLEAR_TAB){
            clearTab();
            activityContextManager.getInstance().getHomeController().initTabCountForced();
        }
        else if(pCommands == dataEnums.eTabCommands.M_ADD_TAB){
            int mTabs = addTabs((geckoSession)pData.get(0), (boolean)pData.get(1));
            activityContextManager.getInstance().getHomeController().initTabCountForced();

            return mTabs;
        }
        else if(pCommands == dataEnums.eTabCommands.M_UPDATE_TAB){
            updateTab((String) pData.get(1), (geckoSession) pData.get(5));
            activityContextManager.getInstance().getHomeController().initTabCountForced();
        }
        else if(pCommands == dataEnums.eTabCommands.GET_TAB){
            return getTab();
        }
        else if(pCommands == dataEnums.eTabCommands.M_GET_SUGGESTIONS){
            return getSuggestions((String) pData.get(0));
        }
        else if(pCommands == dataEnums.eTabCommands.M_UPDATE_PIXEL){
            updatePixels((String)pData.get(0), (GeckoResult<Bitmap>)pData.get(1),  (ImageView) pData.get(2), (GeckoView) pData.get(3), (Boolean) pData.get(4));
        }
        else if(pCommands == dataEnums.eTabCommands.M_HOME_PAGE){
            return getHomePage();
        }

        return null;
    }

}
