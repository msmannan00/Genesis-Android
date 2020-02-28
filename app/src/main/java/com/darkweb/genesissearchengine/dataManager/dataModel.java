package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.tabManager.tabRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("CommitPrefEdits")
class dataModel
{
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEdit;

    private ArrayList<historyRowModel> mHistory = new ArrayList<>();
    private ArrayList<bookmarkRowModel> mBookmarks = new ArrayList<>();
    private ArrayList<tabRowModel> mTabs = new ArrayList<>();
    private ArrayList<historyRowModel> mSuggestions = new ArrayList<>();
    private Map<String, Boolean> mHistoryCache = new HashMap<>();
    private Map<String, historyRowModel> mSuggestionCache = new HashMap<>();

    private int mMaxHistoryId = 0;
    private int mHistorySize = 0;

    dataModel(AppCompatActivity app_context){
        mPrefs = PreferenceManager.getDefaultSharedPreferences(app_context);
        mEdit = mPrefs.edit();
    }
    void clearPrefs(){
        mEdit.clear();
        mEdit.apply();
    }

    /*Prefs Data Model*/

    void setString(String valueKey, String value){
        mEdit.putString(valueKey, value);
        mEdit.apply();
    }
    String getString(String valueKey, String valueDefault){
        return mPrefs.getString(valueKey, valueDefault);
    }
    void setBool(String valueKey, boolean value){
        mEdit.putBoolean(valueKey, value);
        mEdit.apply();
    }
    boolean getBool(String valueKey, boolean valueDefault){
        return mPrefs.getBoolean(valueKey, valueDefault);
    }
    void setInt(String valueKey, int value){
        mEdit.putInt(valueKey, value);
        mEdit.apply();
    }
    int getInt(String valueKey, int valueDefault){
        return mPrefs.getInt(valueKey, valueDefault);
    }
    void setFloat(String valueKey, int value){
        mEdit.putInt(valueKey, value);
        mEdit.apply();
    }
    int getFloat(String valueKey, int valueDefault){
        return mPrefs.getInt(valueKey, valueDefault);
    }


    /*List History*/

    void initializeHistory(ArrayList<historyRowModel> history){
        this.mHistory = history;
        if(!status.sHistoryStatus){
            initializeCache(history);
        }else {
            clearHistory();
        }
    }
    private void initializeCache(ArrayList<historyRowModel> history){
        for(int count=0;count<=history.size()-1;count++){

            mHistoryCache.put(history.get(count).getmHeader(),true);
            historyRowModel tempSuggestion = new historyRowModel(history.get(count).getTitle(),history.get(count).getmHeader(),-1);

            tempSuggestion.updateTitle(tempSuggestion.getmHeader());
            tempSuggestion.updateURL(history.get(count).getmHeader());
            addSuggenstions(tempSuggestion.getmHeader(),tempSuggestion.getTitle(),true);
            mSuggestionCache.put(helperMethod.removeLastSlash(history.get(count).getmHeader()),tempSuggestion);

        }
    }


    void updateSuggestionURL(String url, String newURL,boolean isLoading){
        if(url.length()>1500){
            return;
        }
        url = helperMethod.removeLastSlash(url);
        url = helperMethod.urlWithoutPrefix(url);
        historyRowModel model = mSuggestionCache.get(url);
        if(model!=null){
            mSuggestionCache.remove(url);
            if(!newURL.equals("loading"))
                model.updateHeader(newURL);
            model.updateTitle(model.getmHeader());
            mSuggestionCache.put(url,model);
        }

        String[] params = new String[2];
        params[0] = newURL;
        params[1] = url;
        if(newURL.length()>0 && !isLoading){
            databaseController.getInstance().execSQL("UPDATE history SET title = ? , date = DateTime('now') WHERE url = ?",params);
        }
    }
    void addSuggenstions(String url, String title,boolean isLoading){
        if(url.length()>1500 || title.equals("$TITLE") || title.equals("loading")){
            return;
        }

        url = helperMethod.removeLastSlash(url);
        url = helperMethod.urlWithoutPrefix(url);
        historyRowModel tempModel = mSuggestionCache.get(url);

        if(tempModel==null){
            historyRowModel model = new historyRowModel(title,url,-1);
            mSuggestionCache.put(url,model);
            mSuggestions.add(0,mSuggestionCache.get(url));
        }
        else {
            updateSuggestionURL(url,title,isLoading);
        }

        String[] params = new String[2];
        params[0] = title;
        params[1] = url;
        if(title.length()>0 && !isLoading){
            databaseController.getInstance().execSQL("UPDATE history SET title = ? , date = DateTime('now') WHERE url = ?",params);
        }
    }
    void addHistory(String url) {

        if(url.length()>1500){
            return;
        }
        url = helperMethod.removeLastSlash(url);
        url = helperMethod.urlWithoutPrefix(url);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat d_form = new SimpleDateFormat("dd MMMM | hh:mm a");
        String date = d_form.format(new Date());

        Object url_exists = mHistoryCache.get(url);
        if(url_exists!=null){
            for(int count = 0; count< mHistory.size(); count++){
                historyRowModel model = mHistory.get(count);
                if(model.getmHeader().equals(url)){
                    mHistory.remove(count);
                    mHistory.add(0,model);
                    databaseController.getInstance().execSQL("UPDATE history SET date = '"+date+"' WHERE id="+model.getmId(),null);
                    break;
                }
            }

            historyRowModel model = mSuggestionCache.get(url);
            if(model!=null){
                for(int e=0;e<mSuggestions.size();e++){
                    String temp_url = helperMethod.removeLastSlash(model.getmHeader());
                    temp_url = helperMethod.urlWithoutPrefix(temp_url);

                    if(temp_url.equals(mSuggestions.get(e).getmDescription())){
                        mSuggestions.remove(e);
                    }
                }
                mSuggestions.add(0,model);
            }

            return;
        }

        if(mHistorySize > constants.MAX_LIST_DATA_SIZE)
        {
            databaseController.getInstance().execSQL("DELETE FROM history WHERE id IN (SELECT id FROM History ORDER BY id ASC LIMIT "+(constants.MAX_LIST_DATA_SIZE /2)+")",null);
        }

        String[] params = new String[1];
        params[0] = url;

        mMaxHistoryId = mMaxHistoryId +1;
        mHistorySize += 1;

        databaseController.getInstance().execSQL("INSERT INTO history(id,date,url,title) VALUES("+ mMaxHistoryId +",DateTime('now'),?,'');",params);
        mHistory.add(0,new historyRowModel(url,date, mMaxHistoryId));
        mHistoryCache.put(url,true);
    }
    ArrayList<historyRowModel> getmHistory() {
        return mHistory;
    }
    void setMaxHistoryID(int max_history_id){
        this.mMaxHistoryId = max_history_id;
    }
    void setHistorySize(int history_size){
        this.mHistorySize = history_size;
    }
    void removeHistory(String url) {
        mHistoryCache.remove(url);
        mHistorySize -= 1;
    }
    void clearHistory() {
        mHistory.clear();
        mHistoryCache.clear();
        mSuggestionCache.clear();
        mSuggestions.clear();
        initSuggestions();
    }
    void loadMoreHistory(ArrayList<historyRowModel> history){
        this.mHistory.addAll(history);
        for(int count=0;count<=history.size()-1;count++){
            mHistoryCache.put(history.get(count).getmHeader(),true);
        }
    }

    /*List Bookmark*/

    void initializeBookmarks(){
        mBookmarks = databaseController.getInstance().selectBookmark();
    }
    void addBookmark(String url, String title){
        if(url.length()>1500){
            return;
        }
        int autoval = 0;
        if(mBookmarks.size()> constants.MAX_LIST_SIZE)
        {
            databaseController.getInstance().execSQL("delete from bookmark where id="+ mBookmarks.get(mBookmarks.size()-1).getmId(),null);
            mBookmarks.remove(mHistory.size()-1);
        }

        if(mBookmarks.size()>0)
        {
            autoval = mBookmarks.get(0).getmId()+1;
        }

        if(title.equals(""))
        {
            title = "New_Bookmark"+autoval;
        }

        String[] params = new String[2];
        params[0] = title;
        params[1] = url;

        databaseController.getInstance().execSQL("INSERT INTO bookmark(id,title,url) VALUES("+autoval+",?,?);",params);
        mBookmarks.add(0,new bookmarkRowModel(url,title,autoval));
    }
    ArrayList<bookmarkRowModel> getBookmark(){
        return mBookmarks;
    }
    void clearBookmark() {
        mBookmarks.clear();
    }

    /*List Tabs*/

    void addTabs(geckoSession mSession,boolean isHardCopy){
        if(!isHardCopy){
            mTabs.add(0,new tabRowModel(mSession,mTabs.size()));
        }
        else {
            mTabs.add(0,new tabRowModel(mSession,mTabs.size()));
        }
    }
    ArrayList<tabRowModel> getTab(){
        return mTabs;
    }
    void clearTab() {
        int size = mTabs.size();
        for(int counter = 0; counter< size; counter++){
            mTabs.get(0).getSession().stop();
            mTabs.remove(0);
        }
        if(mTabs.size()>0){
            //Log.i("FUCKERRROR125:","_FERROR_");
            mTabs.get(0).getSession().closeSession();
        }
    }
    void closeTab(geckoSession mSession) {

        for(int counter = 0; counter< mTabs.size(); counter++){
            if(mTabs.get(counter).getSession().getSessionID()==mSession.getSessionID())
            {
                mTabs.remove(counter);
                break;
            }
            else {
                mTabs.get(counter).setId(mTabs.get(counter).getmId()+1);
            }
        }
    }
    void moveTabToTop(geckoSession mSession) {

        for(int counter = 0; counter< mTabs.size(); counter++){

            if(mTabs.get(counter).getSession().getSessionID()==mSession.getSessionID())
            {
                /***BIG PROBLEM***/
                mTabs.remove(counter);
                mTabs.add(0,new tabRowModel(mSession,0));
                break;
            }else {
                mTabs.get(counter).setId(mTabs.get(counter).getmId()+1);
            }
        }
    }
    tabRowModel getCurrentTab(){
        if(mTabs.size()>0){
            return mTabs.get(0);
        }
        else {
            return null;
        }
    }
    int getTotalTabs(){
        return mTabs.size();
    }

    /*List Suggestion*/

    ArrayList<historyRowModel> getmSuggestions(){
        return mSuggestions;
    }
    void clearSuggestion(){
        mSuggestions.clear();
        initSuggestions();
    }
    void initSuggestions(){

        addSuggenstions("https://duckduckgo.com","Duckduckgo",true);
        addSuggenstions("https://bbc.com","BBC",true);
        addSuggenstions("https://youtube.com","Youtube",true);
        addSuggenstions("https://facebook.com","Facebook",true);
        addSuggenstions("https://twitter.com","Twitter",true);
        addSuggenstions("https://amazon.com","Amazon",true);
        addSuggenstions("https://imdb.com","IMDB",true);
        addSuggenstions("https://reddit.com","Reddit",true);
        addSuggenstions("https://pinterest.com","Pinterest",true);
        addSuggenstions("https://ebay.com","EBay",true);
        addSuggenstions("https://tripadvisor.com","Trip Advisor",true);
        addSuggenstions("https://craigslist.org","Craigslist",true);
        addSuggenstions("https://walmart.com","Walmart",true);
        addSuggenstions("https://instagram.com","Instagram",true);
        addSuggenstions("https://google.com","Google",true);
        addSuggenstions("https://nytimes.com","NY Times",true);
        addSuggenstions("https://apple.com","Apple",true);
        addSuggenstions("https://linkedin.com","Linkedin",true);
        addSuggenstions("https://indeed.com","Indeed",true);
        addSuggenstions("https://play.google.com","Play.Google",true);
        addSuggenstions("https://espn.com","ESPN",true);
        addSuggenstions("https://webmd.com","Webmd",true);
        addSuggenstions("https://cnn.com","CNN",true);
        addSuggenstions("https://homedepot.com","Homedepot",true);
        addSuggenstions("https://etsy.com","ETSY",true);
        addSuggenstions("https://netflix.com","Netflix",true);
        addSuggenstions("https://quora.com","Quora",true);
        addSuggenstions("https://microsoft.com","Microsoft",true);
        addSuggenstions("https://target.com","Target",true);
        addSuggenstions("https://merriam-webster.com","Merriam Webster",true);
        addSuggenstions("https://forbes.com","Forbes",true);
        addSuggenstions("https://mapquest.com","Mapquest",true);
        addSuggenstions("https://nih.gov","NIH",true);
        addSuggenstions("https://gamepedia.com","Gamepedia",true);
        addSuggenstions("https://yahoo.com","Yahoo",true);
        addSuggenstions("https://healthline.com","Healthline",true);
        addSuggenstions("https://foxnews.com","Foxnews",true);
        addSuggenstions("https://allrecipes.com","All Recipes",true);
        addSuggenstions("https://quizlet.com","Quizlet",true);
        addSuggenstions("https://weather.com","Weather",true);
        addSuggenstions("https://bestbuy.com","Bestbuy",true);
        addSuggenstions("https://urbandictionary.com","Urbandictionary",true);
        addSuggenstions("https://mayoclinic.org","Mayoclinic",true);
        addSuggenstions("https://aol.com" ,"AOL",true);
        addSuggenstions("https://genius.com","Genius",true);
        addSuggenstions("https://zillow.com","Zillow",true);
        addSuggenstions("https://usatoday.com","Usatoday",true);
        addSuggenstions("https://glassdoor.com","Glassdoor",true);
        addSuggenstions("https://msn.com","MSN",true);
        addSuggenstions("https://rottentomatoes.com","Rotten Tomatoes",true);
        addSuggenstions("https://lowes.com","Lowes",true);
        addSuggenstions("https://dictionary.com","Dictionary",true);
        addSuggenstions("https://businessinsider.com","Business Insider",true);
        addSuggenstions("https://usnews.com","US News",true);
        addSuggenstions("https://medicalnewstoday.com","Medical News Today",true);
        addSuggenstions("https://britannica.com","Britannica",true);
        addSuggenstions("https://washingtonpost.com","Washington Post",true);
        addSuggenstions("https://usps.com","USPS",true);
        addSuggenstions("https://finance.yahoo.com","Finance Yahoo",true);
        addSuggenstions("https://irs.gov","IRS",true);
        addSuggenstions("https://yellowpages.com","Yellow Pages",true);
        addSuggenstions("https://chase.com","Chase",true);
        addSuggenstions("https://retailmenot.com","Retail Menot",true);
        addSuggenstions("https://accuweather.com","Accuweather",true);
        addSuggenstions("https://wayfair.com","Way Fair",true);
        addSuggenstions("https://go.com","GO",true);
        addSuggenstions("https://live.com","Live",true);
        addSuggenstions("https://login.yahoo.com","Login Yahoo",true);
        addSuggenstions("https://steamcommunity.com","Steam Community",true);
        addSuggenstions("https://xfinity.com","XFinity",true);
        addSuggenstions("https://cnet.com","CNET",true);
        addSuggenstions("https://ign.com","IGN",true);
        addSuggenstions("https://steampowered.com","Steam Powered",true);
        addSuggenstions("https://macys.com","Macys",true);
        addSuggenstions("https://wikihow.com","Wikihow",true);
        addSuggenstions("https://mail.yahoo.com","Mail Yahoo",true);
        addSuggenstions("wiktionary.org","Wiktionary",true);
        addSuggenstions("https://cbssports.com","Cbssports",true);
        addSuggenstions("https://cnbc.com","CNBC",true);
        addSuggenstions("https://bankofamerica.com","Bank Of America",true);
        addSuggenstions("https://expedia.com","Expedia",true);
        addSuggenstions("https://wellsfargo.com","Wellsfargo",true);
        addSuggenstions("https://groupon.com","Groupon",true);
        addSuggenstions("https://twitch.tv","Twitch",true);
        addSuggenstions("https://khanacademy.org","Khan Academy",true);
        addSuggenstions("https://theguardian.com","The Guardian",true);
        addSuggenstions("https://paypal.com","Paypal",true);
        addSuggenstions("https://spotify.com","Spotify",true);
        addSuggenstions("https://att.com","ATT",true);
        addSuggenstions("https://nfl.com","NFL",true);
        addSuggenstions("https://realtor.com","Realtor",true);
        addSuggenstions("https://ca.gov","CA Gov",true);
        addSuggenstions("https://goodreads.com","Good Reads",true);
        addSuggenstions("https://office.com","Office",true);
        addSuggenstions("https://ufl.edu","UFL",true);
        addSuggenstions("https://mlb.com","MLB",true);
        addSuggenstions("https://foodnetwork.com","Food Network",true);
        addSuggenstions("https://apartments.com","Apartments",true);
        addSuggenstions("https://npr.org","NPR",true);
        addSuggenstions("https://wowhead.com","Wow Head",true);
        addSuggenstions("https://bing.com","Bing",true);
        addSuggenstions("https://google.com","Google",true);
        addSuggenstions("https://boogle.store","Genesis Search",true);
    }

}
