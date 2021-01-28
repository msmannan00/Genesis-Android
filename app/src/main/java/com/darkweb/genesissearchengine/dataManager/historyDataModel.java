package com.darkweb.genesissearchengine.dataManager;

import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class historyDataModel {

    private int mMaxHistoryId = 0;
    private int mHistorySize = 0;
    private ArrayList<historyRowModel> mHistory;
    private Map<Integer, historyRowModel> mHistoryCache;

    public historyDataModel(){
        mHistory = new ArrayList<>();
        mHistoryCache = new HashMap<>();
    }

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

    private void initializeCache(ArrayList<historyRowModel> history){
        for(int count=0;count<=history.size()-1;count++){
            historyRowModel tempSuggestion = new historyRowModel(history.get(count).getHeader(),history.get(count).getHeader(),-1);
            tempSuggestion.setURL(history.get(count).getHeader());
            mHistoryCache.put(history.get(count).getID(),tempSuggestion);
        }
    }

    private ArrayList<historyRowModel> getHistory() {
        return mHistory;
    }

    private void removeDuplicateURLFromHistory(int p_id, String p_url){

        for (int m_count = 0; m_count < mHistory.size(); m_count++) {
            historyRowModel m_temp_model = mHistory.get(m_count);
            if(m_temp_model==null)
                continue;
            if (m_temp_model.getDescription().equals(p_url)) {
                if(m_temp_model.getID()==p_id){
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
                        databaseController.getInstance().execSQL("DELETE FROM history WHERE id = " + mHistory.get(m_count).getID(), null);
                        mHistoryCache.remove(mHistory.get(m_count).getID());
                        mHistory.remove(m_count);
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

        Object url_exists = mHistoryCache.get(p_id);
        if(url_exists!=null){
            mHistoryCache.get(p_id).setHeader(p_header);
            mHistoryCache.get(p_id).setURL(p_url);

            removeDuplicateURLFromHistory(p_id, p_url);

            String[] params = new String[2];
            params[0] = p_url;
            params[1] = p_header;
            String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            databaseController.getInstance().execSQL("UPDATE history SET date = '" + m_date + "' , url = ? , title = ? WHERE id="+p_id,params);
            return p_id;
        }else {
            if(mHistorySize > constants.CONST_MAX_LIST_DATA_SIZE)
            {
                databaseController.getInstance().execSQL("DELETE FROM history WHERE id IN (SELECT id FROM History ORDER BY id ASC LIMIT "+(constants.CONST_MAX_LIST_DATA_SIZE /2)+")",null);
            }

            if(p_header.equals("loading")){
                p_header = p_url;
            }

            String[] params = new String[2];
            params[0] = p_url;
            params[1] = p_header;

            mMaxHistoryId = mMaxHistoryId +1;
            mHistorySize += 1;

            String m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(Calendar.getInstance().getTime());

            if(!p_header.equals("loading")){
                databaseController.getInstance().execSQL("INSERT INTO history(id,date,url,title) VALUES("+ mMaxHistoryId +",'" + m_date + "',?,?);",params);
            }

            mHistory.add(0,new historyRowModel(p_header,p_url, mMaxHistoryId));
            mHistoryCache.put(mMaxHistoryId, mHistory.get(0));
            removeDuplicateURLFromHistory(mMaxHistoryId, p_url);
            return mMaxHistoryId;
        }
    }
    private void removeHistory(int p_id){
        databaseController.getInstance().execSQL("DELETE FROM history WHERE id = "+p_id,null);
        databaseController.getInstance().selectHistory(0,constants.CONST_FETCHABLE_LIST_SIZE);
        mHistoryCache.remove(p_id);
        mHistorySize -= 1;
    }
    private void clearHistory(){
        databaseController.getInstance().execSQL("DELETE FROM history WHERE 1 ",null);
        mHistory.clear();
        mHistoryCache.clear();
    }
    private boolean loadMoreHistory(ArrayList<historyRowModel> p_history){
        this.mHistory.addAll(p_history);
        for(int count=0;count<=p_history.size()-1;count++){
            mHistoryCache.put(p_history.get(count).getID(),p_history.get(0));
        }

        return p_history.size() > 0;
    }

    public ArrayList<historyRowModel> getSuggestions(String pQuery, ArrayList<historyRowModel> pList){

        String mQueryOriginal = pQuery;
        if(status.sSettingSearchHistory){
            pQuery = pQuery.toLowerCase();
            for(int count = 0; count<= mHistory.size()-1 && mHistory.size()<500; count++){
                historyRowModel mTempModel = null;
                if(mHistory.get(count).getHeader().toLowerCase().contains(pQuery)){
                    mTempModel = new historyRowModel(mHistory.get(count).getHeader(),mHistory.get(count).getDescription(),-1);
                    if(!pList.contains(mTempModel)){
                        pList.add(mTempModel);
                    }
                }else if(mHistory.get(count).getDescription().toLowerCase().contains(pQuery)){
                    mTempModel = new historyRowModel(mHistory.get(count).getHeader(),mHistory.get(count).getDescription(),-1);
                    if(!pList.contains(mTempModel)){
                        pList.add(mTempModel);
                    }
                }
            }
        }

        ArrayList<historyRowModel> mDefaultSuggestions = initSuggestions();
        for(int count = 0; count<= mDefaultSuggestions.size()-1 && mDefaultSuggestions.size()<500; count++){
            if(mDefaultSuggestions.get(count).getHeader().toLowerCase().contains(pQuery)){
                pList.add(new historyRowModel(mDefaultSuggestions.get(count).getHeader(),mDefaultSuggestions.get(count).getDescription(),-1));
            }else if(mDefaultSuggestions.get(count).getDescription().toLowerCase().contains(pQuery)){
                if(pList.size()==0){
                    pList.add(new historyRowModel(mDefaultSuggestions.get(count).getHeader(),mDefaultSuggestions.get(count).getDescription(),-1));
                }else {
                    pList.add(new historyRowModel(mDefaultSuggestions.get(count).getHeader(),mDefaultSuggestions.get(count).getDescription(),-1));
                }
            }
        }

        /*Duplicate handler*/
        ArrayList<String> mDuplicateHandler = new ArrayList<>();
        for(int mCounter=0;mCounter<pList.size();mCounter++){
            if(mDuplicateHandler.contains(pList.get(mCounter).getDescription())){
                pList.remove(mCounter);
                mCounter-=1;
            }else {
                mDuplicateHandler.add(0,pList.get(mCounter).getDescription());
            }
        }

        if(!pQuery.equals(strings.GENERIC_EMPTY_STR) && !pQuery.equals("about:blank") && !pQuery.contains("?") &&  !pQuery.contains("/")  && !pQuery.contains(" ") && !pQuery.contains("  ") && !pQuery.contains("\n")){
            if(pList.size()<3){
                int sepPos = pQuery.indexOf(".");
                if (sepPos == -1) {
                    pList.add( 0,new historyRowModel(mQueryOriginal+".com", strings.GENERIC_EMPTY_STR,-1));
                    pList.add( 0,new historyRowModel(mQueryOriginal+".onion", strings.GENERIC_EMPTY_STR,-1));
                }else
                {
                    if(!pQuery.equals(pQuery.substring(0,sepPos)+".com")){
                        pList.add( 0,new historyRowModel(pQuery.substring(0,sepPos)+".com", strings.GENERIC_EMPTY_STR,-1));
                    }
                    if(!pQuery.equals(pQuery.substring(0,sepPos)+".onion")){
                        pList.add( 0,new historyRowModel(pQuery.substring(0,sepPos)+".onion", strings.GENERIC_EMPTY_STR,-1));
                    }
                }
            }
            pList.add( 0,new historyRowModel(mQueryOriginal, strings.GENERIC_EMPTY_STR,-1));

        }

        return pList;
    }

    private ArrayList<historyRowModel> initSuggestions(){
        ArrayList<historyRowModel> mHintList = new ArrayList<>();

        mHintList.add(new historyRowModel("Duckduckgo","https://duckduckgo.com",-1 ));
        mHintList.add(new historyRowModel("BBC","https://bbc.com",-1 ));
        mHintList.add(new historyRowModel("Youtube","https://youtube.com",-1 ));
        mHintList.add(new historyRowModel("Facebook","https://facebook.com",-1 ));
        mHintList.add(new historyRowModel("Twitter","https://twitter.com",-1 ));
        mHintList.add(new historyRowModel("Amazon","https://amazon.com",-1 ));
        mHintList.add(new historyRowModel("IMDB","https://imdb.com",-1 ));
        mHintList.add(new historyRowModel("Reddit","https://reddit.com",-1 ));
        mHintList.add(new historyRowModel("Pinterest","https://pinterest.com",-1 ));
        mHintList.add(new historyRowModel("EBay","https://ebay.com",-1 ));
        mHintList.add(new historyRowModel("Trip Advisor","https://tripadvisor.com",-1 ));
        mHintList.add(new historyRowModel("Craigslist","https://craigslist.com",-1 ));
        mHintList.add(new historyRowModel("Walmart","https://walmart.com",-1 ));
        mHintList.add(new historyRowModel("Instagram","https://instagram.com",-1 ));
        mHintList.add(new historyRowModel("Google","https://google.com",-1 ));
        mHintList.add(new historyRowModel("NY Times","https://nytimes.com",-1 ));
        mHintList.add(new historyRowModel("Apple","https://apple.com",-1 ));
        mHintList.add(new historyRowModel("Linkedin","https://linkedin.com",-1 ));
        mHintList.add(new historyRowModel("Play.Google","https://play.google.com",-1 ));
        mHintList.add(new historyRowModel("ESPN","https://espn.com",-1 ));
        mHintList.add(new historyRowModel("Webmd","https://webmd.com",-1 ));
        mHintList.add(new historyRowModel("CNN","https://cnn.com",-1 ));
        mHintList.add(new historyRowModel("Homedepot","https://homedepot.com",-1 ));
        mHintList.add(new historyRowModel("ETSY","https://etsy.com",-1 ));
        mHintList.add(new historyRowModel("Netflix","https://netflix.com",-1 ));
        mHintList.add(new historyRowModel("Quora","https://quora.com",-1 ));
        mHintList.add(new historyRowModel("Microsoft","https://microsoft.com",-1 ));
        mHintList.add(new historyRowModel("Target","https://target.com",-1 ));
        mHintList.add(new historyRowModel("Merriam Webster","https://merriam-webster.com",-1 ));
        mHintList.add(new historyRowModel("Forbes","https://forbes.com",-1 ));
        mHintList.add(new historyRowModel("Mapquest","https://mapquest.com",-1 ));
        mHintList.add(new historyRowModel("NIH","https://nih.gov",-1 ));
        mHintList.add(new historyRowModel("Gamepedia","https://gamepedia.com",-1 ));
        mHintList.add(new historyRowModel("Yahoo","https://yahoo.com",-1 ));
        mHintList.add(new historyRowModel("Healthline","https://healthline.com",-1 ));
        mHintList.add(new historyRowModel("Foxnews","https://foxnews.com",-1 ));
        mHintList.add(new historyRowModel("All Recipes","https://allrecipes.com",-1 ));
        mHintList.add(new historyRowModel("Quizlet","https://quizlet.com",-1 ));
        mHintList.add(new historyRowModel("Weather","https://weather.com",-1 ));
        mHintList.add(new historyRowModel("Bestbuy","https://bestbuy.com",-1 ));
        mHintList.add(new historyRowModel("Urbandictionary","https://urbandictionary.com",-1 ));
        mHintList.add(new historyRowModel("Mayoclinic","https://mayoclinic.org",-1 ));
        mHintList.add(new historyRowModel("AOL","https://aol.com",-1 ));
        mHintList.add(new historyRowModel("Genius","https://genius.com",-1 ));
        mHintList.add(new historyRowModel("Zillow","https://zillow.com",-1 ));
        mHintList.add(new historyRowModel("Usatoday","https://usatoday.com",-1 ));
        mHintList.add(new historyRowModel("Glassdoor","https://glassdoor.com",-1 ));
        mHintList.add(new historyRowModel("MSN","https://msn.com",-1 ));
        mHintList.add(new historyRowModel("Rotten Tomatoes","https://rottentomatoes.com",-1 ));
        mHintList.add(new historyRowModel("Lowes","https://lowes.com",-1 ));
        mHintList.add(new historyRowModel("Dictionary","https://dictionary.com",-1 ));
        mHintList.add(new historyRowModel("Business Insider","https://businessinsider.com",-1 ));
        mHintList.add(new historyRowModel("US News","https://usnews.com",-1 ));
        mHintList.add(new historyRowModel("Medical News Today","https://medicalnewstoday.com",-1 ));
        mHintList.add(new historyRowModel("Britannica","https://britannica.com",-1 ));
        mHintList.add(new historyRowModel("Washington Post","https://washingtonpost.com",-1 ));
        mHintList.add(new historyRowModel("USPS","https://usps.com",-1 ));
        mHintList.add(new historyRowModel("Finance Yahoo","https://finance.yahoo.com",-1 ));
        mHintList.add(new historyRowModel("IRS","https://finance.irs.gov",-1 ));
        mHintList.add(new historyRowModel("Yellow Pages","https://yellowpages.com",-1 ));
        mHintList.add(new historyRowModel("Chase","https://chase.com",-1 ));
        mHintList.add(new historyRowModel("Retail Menot","https://retailmenot.com",-1 ));
        mHintList.add(new historyRowModel("Accuweather","https://accuweather.com",-1 ));
        mHintList.add(new historyRowModel("Way Fair","https://wayfair.com",-1 ));
        mHintList.add(new historyRowModel("GO","https://go.com",-1 ));
        mHintList.add(new historyRowModel("Live","https://live.com",-1 ));
        mHintList.add(new historyRowModel("Login Yahoo","https://login.yahoo.com",-1 ));
        mHintList.add(new historyRowModel("Steam Community","https://steamcommunity.com",-1 ));
        mHintList.add(new historyRowModel("XFinity","https://xfinity.com",-1 ));
        mHintList.add(new historyRowModel("CNET","https://cnet.com",-1 ));
        mHintList.add(new historyRowModel("IGN","https://ign.com",-1 ));
        mHintList.add(new historyRowModel("Steam Powered","https://steampowered.com",-1 ));
        mHintList.add(new historyRowModel("Macys","https://macys.com",-1 ));
        mHintList.add(new historyRowModel("Wikihow","https://wikihow.com",-1 ));
        mHintList.add(new historyRowModel("Mail Yahoo","https://mail.yahoo.com",-1 ));
        mHintList.add(new historyRowModel("Wiktionary","https://wiktionary.com",-1 ));
        mHintList.add(new historyRowModel("Cbssports","https://cbssports.com",-1 ));
        mHintList.add(new historyRowModel("CNBC","https://cnbc.com",-1 ));
        mHintList.add(new historyRowModel("Bank Of America","https://bankofamerica.com",-1 ));
        mHintList.add(new historyRowModel("Expedia","https://expedia.com",-1 ));
        mHintList.add(new historyRowModel("Wellsfargo","https://wellsfargo.com",-1 ));
        mHintList.add(new historyRowModel("Groupon","https://groupon.com",-1 ));
        mHintList.add(new historyRowModel("Twitch","https://twitch.com",-1 ));
        mHintList.add(new historyRowModel("Khan Academy","https://khanacademy.com",-1 ));
        mHintList.add(new historyRowModel("The Guardian","https://theguardian.com",-1 ));
        mHintList.add(new historyRowModel("Paypal","https://paypal.com",-1 ));
        mHintList.add(new historyRowModel("Spotify","https://spotify.com",-1 ));
        mHintList.add(new historyRowModel("ATT","https://att.com",-1 ));
        mHintList.add(new historyRowModel("NFL","https://nfl.com",-1 ));
        mHintList.add(new historyRowModel("Realtor","https://realtor.com",-1 ));
        mHintList.add(new historyRowModel("CA Gov","https://ca.gov",-1 ));
        mHintList.add(new historyRowModel("Good Reads","https://goodreads.com",-1 ));
        mHintList.add(new historyRowModel("Office","https://office.com",-1 ));
        mHintList.add(new historyRowModel("UFL","https://ufl.com",-1 ));
        mHintList.add(new historyRowModel("MLB","https://mlb.com",-1 ));
        mHintList.add(new historyRowModel("Food Network","https://foodnetwork.com",-1 ));
        mHintList.add(new historyRowModel("Apartments","https://apartments.com",-1 ));
        mHintList.add(new historyRowModel("NPR","https://npr.com",-1 ));
        mHintList.add(new historyRowModel("Wow Head","https://wowhead.com",-1 ));
        mHintList.add(new historyRowModel("Bing","https://bing.com",-1 ));
        mHintList.add(new historyRowModel("Google","https://google.com",-1 ));
        mHintList.add(new historyRowModel("Genesis Search","https://genesis.onion",-1 ));

        return mHintList;
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
        else if(p_commands == dataEnums.eHistoryCommands.M_GET_SUGGESTIONS){
            return getSuggestions((String)p_data.get(0), (ArrayList<historyRowModel>) p_data.get(1));
        }
        else if(p_commands == dataEnums.eHistoryCommands.M_LOAD_MORE_HISTORY){
            return loadMoreHistory((ArrayList<historyRowModel>) p_data.get(0));
        }
        else if(p_commands == dataEnums.eHistoryCommands.M_INITIALIZE_HISTORY){
            initializeHistory((ArrayList<historyRowModel>) p_data.get(0), (int)p_data.get(1), (int)p_data.get(2));
        }
        else if(p_commands == dataEnums.eHistoryCommands.M_HISTORY_SIZE){
            return mHistory.size();
        }

        return null;
    }

}
