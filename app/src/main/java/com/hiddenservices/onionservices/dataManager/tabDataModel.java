package com.hiddenservices.onionservices.dataManager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.geckoSession;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.dataManager.models.tabRowModel;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.eventObserver;
import org.mozilla.geckoview.GeckoResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@SuppressLint("CommitPrefEdits")
class tabDataModel {

    /* Local Variables */

    private eventObserver.eventListener mExternalEvents;

    /* Initializations */

    public tabDataModel(eventObserver.eventListener pExternalEvents) {
        mExternalEvents = pExternalEvents;
    }

    private ArrayList<tabRowModel> mTabs = new ArrayList<>();

    void initializeTab(ArrayList<tabRowModel> pTabMdel) {
        for (int counter = 0; counter < mTabs.size(); counter++) {
            if (mTabs.get(counter).getSession() != null) {
                mTabs.get(counter).getSession().setActive(false);
                mTabs.get(counter).getSession().purgeHistory();
                mTabs.get(counter).getSession().close();
            }
        }
        for (int counter = 0; counter < pTabMdel.size(); counter++) {
            pTabMdel.get(counter).getSession().close();
        }
        mTabs.clear();
        mTabs.addAll(pTabMdel);
    }

    /* Helper Methods */

    ArrayList<tabRowModel> getTab() {
        return mTabs;
    }

    private void closeAllTabs() {
        for (int mCounter = 0; mCounter < mTabs.size(); mCounter++) {
            mTabs.get(mCounter).getSession().close();
        }
    }

    private void closeAllTabLowMemory() {
        for (int mCounter = 1; mCounter < mTabs.size(); mCounter++) {
            mTabs.get(mCounter).getSession().close();
        }
    }

    geckoSession getHomePage() {
        if (mTabs.size() > 0) {
            return mTabs.get(0).getSession();
        } else {
            return null;
        }
    }

    int addTabs(geckoSession mSession, boolean pIsDataSavable) {

        tabRowModel mTabModel = new tabRowModel(mSession);
        if(mTabs.size() > 0 && (status.sLowMemory == enums.MemoryStatus.LOW_MEMORY || status.sLowMemory == enums.MemoryStatus.CRITICAL_MEMORY)){
            getCurrentTab().getSession().close();
            getCurrentTab().getSession().stop();
        }

        if(mTabModel.getSession().getCurrentURL().equals("about:blank")){
            mTabs.add(0, mTabModel);
        }else {
            mTabs.add(mTabModel);
        }

        if (mTabs.size() > 20) {
            closeTab(mTabs.get(mTabs.size() - 1).getSession());
            return enums.AddTabCallback.TAB_FULL;
        }

        if (mTabs.size() > 2) {
            for(int counter=mTabs.size()-1;counter>1;counter--){
                mTabs.get(counter).resetBitmap();
                mTabs.get(counter).getSession().close();
                mTabs.get(counter).getSession().setActive(false);
            }
        }


        if (pIsDataSavable) {
            if (mTabModel.getSession().getTitle().equals("about:blank") || mTabModel.getSession().getTitle().equals("$TITLE") || mTabModel.getSession().getTitle().startsWith("http://loading") || mTabModel.getSession().getTitle().startsWith("loading")) {
                return enums.AddTabCallback.TAB_ADDED;
            }

            if (mTabModel.getSession().getCurrentURL().equals("about:blank") || mTabModel.getSession().getCurrentURL().equals("$TITLE") || mTabModel.getSession().getCurrentURL().startsWith("http://loading") || mTabModel.getSession().getCurrentURL().startsWith("loading")) {
                return enums.AddTabCallback.TAB_ADDED;
            }
        }
        return enums.AddTabCallback.TAB_ADDED;
    }

    void clearTab() {
        int size = mTabs.size();
        for (int counter = 0; counter < size; counter++) {
            if (mTabs.size() > 0) {
                mTabs.get(0).getSession().stop();
                mTabs.get(0).getSession().setActive(false);
                mTabs.get(0).getSession().purgeHistory();
                mTabs.get(0).getSession().close();
                mTabs.remove(0);
            }
        }
        if (mTabs.size() > 0) {
            mTabs.get(0).getSession().setActive(false);
            mTabs.get(0).getSession().purgeHistory();
            mTabs.get(0).getSession().close();
            mTabs.remove(0);
        }

        mExternalEvents.invokeObserver(Arrays.asList("DELETE FROM tab WHERE 1", null), dataEnums.eTabCallbackCommands.M_EXEC_SQL);

    }

    void closeTab(geckoSession mSession) {
        mSession.stop();
        mSession.setActive(false);
        mSession.purgeHistory();
        mSession.close();

        try {
            String mID = strings.GENERIC_EMPTY_STR;
            for (int counter = 0; counter < mTabs.size(); counter++) {
                if (mTabs.get(counter).getSession().getSessionID().equals(mSession.getSessionID())) {
                    mTabs.get(counter).getSession().setActive(false);
                    mTabs.get(counter).getSession().purgeHistory();
                    mTabs.get(counter).getSession().stop();
                    mTabs.get(counter).getSession().close();
                    mID = mTabs.get(counter).getmId();
                    mTabs.remove(counter);
                    break;
                }
            }

            mExternalEvents.invokeObserver(Arrays.asList("DELETE FROM tab WHERE mid='" + mID + "'", null), dataEnums.eTabCallbackCommands.M_EXEC_SQL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void moveTabToTop(geckoSession mSession) {
        for (int counter = 0; counter < mTabs.size(); counter++) {

            try {
                if (mTabs.get(counter).getSession().getSessionID().equals(mSession.getSessionID())) {
                    String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());

                    mExternalEvents.invokeObserver(Arrays.asList("UPDATE tab SET date = '" + m_date + "' WHERE mid='" + mTabs.get(counter).getmId() + "'", null), dataEnums.eTabCallbackCommands.M_EXEC_SQL);
                    mTabs.add(0, mTabs.remove(counter));
                    mTabs.get(0).getSession().setActive(true);
                    break;
                }
            } catch (Exception ex) {
                Log.i(ex.getMessage(), ex.getMessage());
            }
        }
    }

    void updateSession(String mSessionState, String mSessionID) {
        for (int counter = 0; counter < mTabs.size(); counter++) {

            try {
                if (mTabs.get(counter).getSession().getSessionID().equals(mSessionID)) {
                    String[] params = new String[1];
                    params[0] = mSessionState;

                    mExternalEvents.invokeObserver(Arrays.asList("UPDATE tab SET session = ? WHERE mid='" + mTabs.get(counter).getmId() + "'", params), dataEnums.eTabCallbackCommands.M_EXEC_SQL);
                    mTabs.add(0, mTabs.remove(counter));
                    break;
                }
            } catch (Exception ex) {
                Log.i(ex.getMessage(), ex.getMessage());
            }
        }
    }

    boolean updateTab(String mSessionID, geckoSession pSession) {
        for (int counter = 0; counter < mTabs.size(); counter++) {

            if (mTabs.get(counter).getSession().getSessionID().equals(mSessionID)) {
                String[] params = new String[3];
                params[0] = mTabs.get(counter).getSession().getTitle();
                params[1] = mTabs.get(counter).getSession().getCurrentURL();
                params[2] = mTabs.get(counter).getSession().getTheme();
                String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());

                if(status.sLowMemory != enums.MemoryStatus.STABLE){
                    mTabs.get(counter).decodeByteArraysetmBitmap(null);
                }

                if (mTabs.get(counter).getSession().getTitle().equals("about:blank") || mTabs.get(counter).getSession().getTitle().equals("$TITLE") || mTabs.get(counter).getSession().getTitle().startsWith("http://loading") || mTabs.get(counter).getSession().getTitle().startsWith("loading")) {
                    return false;
                }

                if (mTabs.get(counter).getSession().getCurrentURL().equals("about:blank") || mTabs.get(counter).getSession().getCurrentURL().equals("$TITLE") || mTabs.get(counter).getSession().getCurrentURL().startsWith("http://loading") || mTabs.get(counter).getSession().getCurrentURL().startsWith("loading")) {
                    return false;
                }

                if (mTabs.get(counter).getmId() != null) {
                    mExternalEvents.invokeObserver(Arrays.asList("REPLACE INTO tab(mid,date,title,url,theme) VALUES('" + mTabs.get(counter).getmId() + "','" + m_date + "',?,?,?);", params), dataEnums.eTabCallbackCommands.M_EXEC_SQL);
                }
                return true;
            }
        }
        addTabs(pSession, true);
        return false;
    }


    tabRowModel getCurrentTab() {
        if (mTabs.size() > 0) {
            return mTabs.get(0);
        } else {
            return null;
        }
    }

    tabRowModel getRecentTab() {
        if (mTabs.size() > 1) {
            return mTabs.get(1);
        } else {
            return null;
        }
    }

    tabRowModel getLastTab() {
        if (mTabs.size() > 0) {
            return mTabs.get(mTabs.size() - 1);
        } else {
            return null;
        }
    }

    updatePixelThread mThread = new updatePixelThread();
    public void updatePixels(String pSessionID, GeckoResult<Bitmap> pBitmapManager, ImageView pImageView, boolean pOpenTabView) {
        if(status.sLowMemory == enums.MemoryStatus.STABLE){
            mThread.cancel(true);
            mThread = new updatePixelThread();
            mThread.pBitmapManager = pBitmapManager;
            mThread.pImageView = pImageView;
            mThread.pOpenTabView = pOpenTabView;
            mThread.pSessionID = pSessionID;
            mThread.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class updatePixelThread extends AsyncTask<String, String, String> {

        public String pSessionID;
        public GeckoResult<Bitmap> pBitmapManager;
        public ImageView pImageView;
        public boolean pOpenTabView;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                for (int counter = 0; counter < mTabs.size(); counter++) {
                    int finalCounter = counter;
                    if (mTabs.get(counter).getSession().getSessionID().equals(pSessionID) && pBitmapManager!=null) {
                        Bitmap mBitmap = pBitmapManager.poll(10000);

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 25, out);

                        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                        Bitmap emptyBitmap = Bitmap.createBitmap(decoded.getWidth(), decoded.getHeight(), decoded.getConfig());
                        if (!decoded.sameAs(emptyBitmap)) {
                            mTabs.get(finalCounter).decodeByteArraysetmBitmap(decoded);
                        }
                        break;
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            if (pOpenTabView) {
                activityContextManager.getInstance().getHomeController().onLoadFirstElement();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String bitmap) {
            pImageView = null;
            pBitmapManager = null;
        }
    }

    public ArrayList<ArrayList<String>> getSuggestions(String pQuery) {
        ArrayList<ArrayList<String>> mModel = new ArrayList<>();
        for (int count = 0; count <= mTabs.size() - 1 && mTabs.size() < 500; count++) {
            if (mTabs.get(count).getSession().getTitle().toLowerCase().contains(pQuery) || mTabs.get(count).getSession().getCurrentURL().toLowerCase().contains(pQuery)) {
                ArrayList<String> mTempModel = new ArrayList<>();
                mTempModel.add(mTabs.get(count).getSession().getTitle().toLowerCase());
                mTempModel.add(mTabs.get(count).getSession().getCurrentURL());
                mModel.add(mTempModel);
            }
        }
        return mModel;
    }

    int getTotalTabs() {
        return mTabs.size();
    }

    /*List Suggestion*/
    public Object onTrigger(dataEnums.eTabCommands pCommands, List<Object> pData) {
        if (pCommands == dataEnums.eTabCommands.GET_TOTAL_TAB) {
            return getTotalTabs();
        }
        if (pCommands == dataEnums.eTabCommands.CLOSE_ALL_TABS) {
            closeAllTabs();
        } else if (pCommands == dataEnums.eTabCommands.GET_CURRENT_TAB) {
            return getCurrentTab();
        } else if (pCommands == dataEnums.eTabCommands.GET_RECENT_TAB) {
            return getRecentTab();
        } else if (pCommands == dataEnums.eTabCommands.GET_LAST_TAB) {
            return getLastTab();
        } else if (pCommands == dataEnums.eTabCommands.MOVE_TAB_TO_TOP) {
            moveTabToTop((geckoSession) pData.get(0));
        } else if (pCommands == dataEnums.eTabCommands.CLOSE_TAB) {
            closeTab((geckoSession) pData.get(0));
            activityContextManager.getInstance().getHomeController().initTabCountForced();
        } else if (pCommands == dataEnums.eTabCommands.M_CLEAR_TAB) {
            clearTab();
            activityContextManager.getInstance().getHomeController().initTabCountForced();
        } else if (pCommands == dataEnums.eTabCommands.M_ADD_TAB) {
            int mTabs = addTabs((geckoSession) pData.get(0), (boolean) pData.get(1));
            activityContextManager.getInstance().getHomeController().initTabCountForced();
            return mTabs;
        } else if (pCommands == dataEnums.eTabCommands.M_UPDATE_SESSION_STATE) {
            updateSession((String) pData.get(5), (String) pData.get(1));
        } else if (pCommands == dataEnums.eTabCommands.M_UPDATE_TAB) {
            updateTab((String) pData.get(1), (geckoSession) pData.get(5));
            activityContextManager.getInstance().getHomeController().initTabCountForced();
        } else if (pCommands == dataEnums.eTabCommands.GET_TAB) {
            return getTab();
        } else if (pCommands == dataEnums.eTabCommands.M_GET_SUGGESTIONS) {
            return getSuggestions((String) pData.get(0));
        } else if (pCommands == dataEnums.eTabCommands.M_UPDATE_PIXEL) {
            updatePixels((String) pData.get(0), (GeckoResult<Bitmap>) pData.get(1), (ImageView) pData.get(2), (Boolean) pData.get(4));
        } else if (pCommands == dataEnums.eTabCommands.M_HOME_PAGE) {
            return getHomePage();
        } else if (pCommands == dataEnums.eTabCommands.M_CLOSE_TAB_LOW_MEMORY) {
            closeAllTabLowMemory();
        }

        return null;
    }
}
