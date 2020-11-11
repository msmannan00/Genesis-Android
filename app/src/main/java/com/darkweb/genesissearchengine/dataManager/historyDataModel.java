package com.darkweb.genesissearchengine.dataManager;

import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class historyDataModel {

    private int m_max_history_Id = 0;
    private int m_history_size = 0;
    private ArrayList<historyRowModel> m_history;
    private Map<Integer, historyRowModel> m_history_cache;

    public historyDataModel(){
        m_history = new ArrayList<>();
        m_history_cache = new HashMap<>();
    }

    void initializeHistory(ArrayList<historyRowModel> history, int pMaxHistoryId, int pHistorySize){
        m_max_history_Id = pMaxHistoryId;
        m_history_size = pHistorySize;
        this.m_history = history;
        if(!status.sClearOnExit){
            initializeCache(history);
        }else {
            clearHistory();
        }
    }

    private void initializeCache(ArrayList<historyRowModel> history){
        for(int count=0;count<=history.size()-1;count++){
            historyRowModel tempSuggestion = new historyRowModel(history.get(count).getHeader(),history.get(count).getHeader(),-1);
            tempSuggestion.setURL(history.get(count).getHeader());
            m_history_cache.put(history.get(count).getID(),tempSuggestion);
        }
    }

    private ArrayList<historyRowModel> getHistory() {
        return m_history;
    }

    private void removeDuplicateURLFromHistory(int p_id, String p_url){

        for (int m_count = 0; m_count < m_history.size(); m_count++) {
            historyRowModel m_temp_model = m_history.get(m_count);
            if(m_temp_model==null)
                continue;
            if (m_temp_model.getDescription().equals(p_url)) {
                if(m_temp_model.getID()==p_id){
                    if(m_count>0){
                        m_history.remove(m_count);
                        m_history.add(0, m_temp_model);
                    }
                }else {
                    Calendar calendar = Calendar.getInstance();
                    int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(m_history.get(m_count).getDate());

                    if (dayOfYear == cal.get(Calendar.DAY_OF_YEAR)) {
                        databaseController.getInstance().execSQL("DELETE FROM history WHERE id = " + m_history.get(m_count).getID(), null);
                        m_history_cache.remove(m_history.get(m_count).getID());
                        m_history.remove(m_count);
                        m_count = m_count-1;
                    }
                }
            }
        }
    }

    private int addHistory(String p_url,String p_header, int p_id) {
        if(p_url.length()>1500 || p_url.equals("about:blank") || p_header.equals("$TITLE")){
            return p_id;
        }

        p_url = helperMethod.removeLastSlash(p_url);
        p_url = helperMethod.urlWithoutPrefix(p_url);

        Object url_exists = m_history_cache.get(p_id);
        if(url_exists!=null){
            m_history_cache.get(p_id).setHeader(p_header);
            m_history_cache.get(p_id).setURL(p_url);

            removeDuplicateURLFromHistory(p_id, p_url);

            String[] params = new String[2];
            params[0] = p_url;
            params[1] = p_header;
            String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            databaseController.getInstance().execSQL("UPDATE history SET date = '" + m_date + "' , url = ? , title = ? WHERE id="+p_id,params);
            return p_id;
        }else {
            if(m_history_size > constants.CONST_MAX_LIST_DATA_SIZE)
            {
                databaseController.getInstance().execSQL("DELETE FROM history WHERE id IN (SELECT id FROM History ORDER BY id ASC LIMIT "+(constants.CONST_MAX_LIST_DATA_SIZE /2)+")",null);
            }

            if(p_header.equals("loading")){
                p_header = p_url;
            }

            String[] params = new String[2];
            params[0] = p_url;
            params[1] = p_header;

            m_max_history_Id = m_max_history_Id +1;
            m_history_size += 1;

            String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());

            if(!p_header.equals("loading")){
                databaseController.getInstance().execSQL("INSERT INTO history(id,date,url,title) VALUES("+ m_max_history_Id +",'" + m_date + "',?,?);",params);
            }

            m_history.add(0,new historyRowModel(p_header,p_url, m_max_history_Id));
            m_history_cache.put(m_max_history_Id,m_history.get(0));
            removeDuplicateURLFromHistory(m_max_history_Id, p_url);
            return m_max_history_Id;
        }
    }
    private void removeHistory(int p_id){
        databaseController.getInstance().execSQL("DELETE FROM history WHERE id = "+p_id,null);
        databaseController.getInstance().selectHistory(0,constants.CONST_FETCHABLE_LIST_SIZE);
        m_history_cache.remove(p_id);
        m_history_size -= 1;
    }
    private void clearHistory(){
        databaseController.getInstance().execSQL("DELETE FROM history WHERE 1 ",null);
        m_history.clear();
        m_history_cache.clear();
    }
    private boolean loadMoreHistory(ArrayList<historyRowModel> p_history){
        this.m_history.addAll(p_history);
        for(int count=0;count<=p_history.size()-1;count++){
            m_history_cache.put(p_history.get(count).getID(),p_history.get(0));
        }

        return p_history.size() > 0;
    }

    public Object onTrigger(dataEnums.eHistoryCommands p_commands, List<Object> p_data){
        if(p_commands == dataEnums.eHistoryCommands.M_GET_HISTORY){
            return getHistory();
        }
        else if(p_commands == dataEnums.eHistoryCommands.M_ADD_HISTORY){
            return addHistory((String) p_data.get(0),(String) p_data.get(2), (int)p_data.get(3));
        }
        else if(p_commands == dataEnums.eHistoryCommands.M_REMOVE_HISTORY){
            removeHistory((int) p_data.get(0));
        }
        else if(p_commands == dataEnums.eHistoryCommands.M_CLEAR_HISTORY){
            clearHistory();
        }
        else if(p_commands == dataEnums.eHistoryCommands.M_LOAD_MORE_HISTORY){
            return loadMoreHistory((ArrayList<historyRowModel>) p_data.get(0));
        }
        else if(p_commands == dataEnums.eHistoryCommands.M_INITIALIZE_HISTORY){
            initializeHistory((ArrayList<historyRowModel>) p_data.get(0), (int)p_data.get(1), (int)p_data.get(2));
        }
        else if(p_commands == dataEnums.eHistoryCommands.M_HISTORY_SIZE){
            return m_history.size();
        }

        return null;
    }

}
