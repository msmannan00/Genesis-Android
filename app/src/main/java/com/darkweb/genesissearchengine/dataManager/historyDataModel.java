package com.darkweb.genesissearchengine.dataManager;

import com.darkweb.genesissearchengine.dataManager.models.historyRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class historyDataModel {

    /* Local Variables */

    private eventObserver.eventListener mExternalEvents;

    private int mMaxHistoryId = 0;
    private int mHistorySize = 0;
    private ArrayList<historyRowModel> mHistory;
    private Map<Integer, historyRowModel> mHistoryCache;

    public historyDataModel(eventObserver.eventListener pExternalEvents){
        mHistory = new ArrayList<>();
        mHistoryCache = new HashMap<>();

        mExternalEvents = pExternalEvents;
    }

    /* Initializations */

    void initializeHistory(ArrayList<historyRowModel> history, int pMaxHistoryId, int pHistorySize){
        mMaxHistoryId = pMaxHistoryId;
        mHistorySize = pHistorySize;
        this.mHistory = history;
        if(!status.sClearOnExit){
            initializeCache(history);
        }else {
            clearHistory();
        }
    }

    private void initializeCache(ArrayList<historyRowModel> pHistory){
        for(int count=0;count<=pHistory.size()-1;count++){
            historyRowModel tempSuggestion = new historyRowModel(pHistory.get(count).getHeader(),pHistory.get(count).getHeader(),-1);
            tempSuggestion.setURL(pHistory.get(count).getHeader());
            mHistoryCache.put(pHistory.get(count).getID(),tempSuggestion);
        }
    }

    /* Helper Methods */

    private ArrayList<historyRowModel> getHistory() {
        return mHistory;
    }

    private void removeDuplicateURLFromHistory(int pID, String pUrl){

        for (int m_count = 0; m_count < mHistory.size(); m_count++) {
            historyRowModel m_temp_model = mHistory.get(m_count);
            if(m_temp_model==null)
                continue;
            if (m_temp_model.getDescription().equals(pUrl)) {
                if(m_temp_model.getID()==pID){
                    if(m_count>0){
                        mHistory.remove(m_count);
                        mHistory.add(0, m_temp_model);
                    }
                }else {
                    Calendar calendar = Calendar.getInstance();
                    int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(mHistory.get(m_count).getDate());

                    if (dayOfYear == cal.get(Calendar.DAY_OF_YEAR)) {
                        mExternalEvents.invokeObserver(Arrays.asList("DELETE FROM history WHERE id = " + mHistory.get(m_count).getID(), null), dataEnums.eHistoryCallbackCommands.M_EXEC_SQL);
                        mHistoryCache.remove(mHistory.get(m_count).getID());
                        mHistory.remove(m_count);
                        m_count = m_count-1;
                    }
                }
            }
        }
    }

    private int addHistory(String pUrl,String pHeader, int pID) {
        if(pUrl.length()>1500 || pUrl.equals("about:blank") || pHeader.equals("$TITLE")){
            return pID;
        }

        pUrl = helperMethod.removeLastSlash(pUrl);
        pUrl = helperMethod.urlWithoutPrefix(pUrl);

        Object url_exists = mHistoryCache.get(pID);
        if(url_exists!=null){
            mHistoryCache.get(pID).setHeader(pHeader);
            mHistoryCache.get(pID).setURL(pUrl);

            removeDuplicateURLFromHistory(pID, pUrl);

            String[] params = new String[2];
            params[0] = pUrl;
            params[1] = pHeader;
            String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            mExternalEvents.invokeObserver(Arrays.asList("UPDATE history SET date = '" + m_date + "' , url = ? , title = ? WHERE id="+pID,params), dataEnums.eHistoryCallbackCommands.M_EXEC_SQL);
            return pID;
        }else {
            if(mHistorySize > constants.CONST_MAX_LIST_DATA_SIZE)
            {
                mExternalEvents.invokeObserver(Arrays.asList("DELETE FROM history WHERE id IN (SELECT id FROM History ORDER BY id ASC LIMIT "+(constants.CONST_MAX_LIST_DATA_SIZE /2)+")",null), dataEnums.eHistoryCallbackCommands.M_EXEC_SQL);
            }

            if(pHeader.equals("loading")){
                pHeader = pUrl;
            }

            String[] params = new String[2];
            params[0] = pUrl;
            params[1] = pHeader;

            mMaxHistoryId = mMaxHistoryId +1;
            mHistorySize += 1;

            String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());

            if(!pHeader.equals("loading")){
                mExternalEvents.invokeObserver(Arrays.asList("REPLACE INTO history(id,date,url,title) VALUES("+ mMaxHistoryId +",'" + m_date + "',?,?);",params), dataEnums.eHistoryCallbackCommands.M_EXEC_SQL);
            }

            mHistory.add(0,new historyRowModel(pHeader,pUrl, mMaxHistoryId));
            mHistoryCache.put(mMaxHistoryId, mHistory.get(0));
            removeDuplicateURLFromHistory(mMaxHistoryId, pUrl);
            return mMaxHistoryId;
        }
    }
    private void removeHistory(int pID){
        mExternalEvents.invokeObserver(Arrays.asList("DELETE FROM history WHERE id = "+pID,null), dataEnums.eHistoryCallbackCommands.M_EXEC_SQL);
        mHistoryCache.remove(pID);
        mHistorySize -= 1;
    }
    private void clearHistory(){
        mExternalEvents.invokeObserver(Arrays.asList("DELETE FROM history WHERE 1 ",null), dataEnums.eHistoryCallbackCommands.M_EXEC_SQL);
        mHistory.clear();
        mHistoryCache.clear();
    }
    private boolean loadMoreHistory(ArrayList<historyRowModel> pHistory){
        this.mHistory.addAll(pHistory);
        for(int count=0;count<=pHistory.size()-1;count++){
            mHistoryCache.put(pHistory.get(count).getID(),pHistory.get(0));
        }

        return pHistory.size() > 0;
    }

    /* External Triggers */

    public Object onTrigger(dataEnums.eHistoryCommands pCommands, List<Object> pData){
        if(pCommands == dataEnums.eHistoryCommands.M_GET_HISTORY){
            return getHistory();
        }
        else if(pCommands == dataEnums.eHistoryCommands.M_ADD_HISTORY){
            return addHistory((String) pData.get(0),(String) pData.get(2), (int)pData.get(3));
        }
        else if(pCommands == dataEnums.eHistoryCommands.M_REMOVE_HISTORY){
            removeHistory((int) pData.get(0));
        }
        else if(pCommands == dataEnums.eHistoryCommands.M_CLEAR_HISTORY){
            clearHistory();
        }
        else if(pCommands == dataEnums.eHistoryCommands.M_LOAD_MORE_HISTORY){
            return loadMoreHistory((ArrayList<historyRowModel>) pData.get(0));
        }
        else if(pCommands == dataEnums.eHistoryCommands.M_INITIALIZE_HISTORY){
            initializeHistory((ArrayList<historyRowModel>) pData.get(0), (int)pData.get(1), (int)pData.get(2));
        }
        else if(pCommands == dataEnums.eHistoryCommands.M_HISTORY_SIZE){
            return mHistory.size();
        }

        return null;
    }

}
