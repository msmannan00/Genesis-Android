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
        initializeCache(history);
        Log.i("","");
    }
    private void initializeCache(ArrayList<historyRowModel> history){
        for(int count=0;count<=history.size()-1;count++){
            mHistoryCache.put(history.get(count).getmHeader(),true);
            historyRowModel tempSuggestion = new historyRowModel(history.get(count).getTitle(),history.get(count).getmHeader(),-1);
            mSuggestions.add(tempSuggestion);
            mSuggestionCache.put(history.get(count).getmHeader(),tempSuggestion);
        }
    }
    void updateSuggestionURL(String url, String newURL){
        if(url.length()>1500){
            return;
        }
        historyRowModel model = mSuggestionCache.get(url);
        if(model!=null){
            mSuggestionCache.remove(url);
            model.updateURL(newURL);
            mSuggestionCache.put(newURL,model);
        }

    }
    void addSuggenstions(String url, String title){
        if(url.length()>1500){
            return;
        }

        historyRowModel tempModel = mSuggestionCache.get(url);

        if(tempModel==null){
            historyRowModel model = new historyRowModel(title,url,-1);
            mSuggestions.add(0,model);
            mSuggestionCache.put(url,model);
        }
        else {
            historyRowModel model = mSuggestionCache.get(url);
            if(model!=null){
                model.updateTitle(title);
            }
        }

        String[] params = new String[2];
        params[0] = title;
        params[1] = url;
        if(title.length()>0){
            databaseController.getInstance().execSQL("UPDATE history SET title = ? , date = DateTime('now') WHERE url = ?",params);
        }
    }
    void addHistory(String url) {

        if(url.length()>1500){
            return;
        }
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
                Log.i("ERERER3",mSuggestions.size()+"");
                mSuggestions.remove(model);
                mSuggestions.add(0,model);
                Log.i("ERERER4",mSuggestions.size()+"");
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
        Log.i("ERERER5",mSuggestions.size()+"");
        mSuggestions.clear();
        Log.i("ERERER6",mSuggestions.size()+"");
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
            Log.i("FUCKERRROR125:","_FERROR_");
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

        Log.i("ERERER9",mSuggestions.size()+"");
        mSuggestions.add(new historyRowModel("Youtube","https://youtube.com",-1));
        mSuggestions.add(new historyRowModel("Facebook","https://facebook.com",-1));
        mSuggestions.add(new historyRowModel("Twitter","https://twitter.com",-1));
        mSuggestions.add(new historyRowModel("Amazon","https://amazon.com",-1));
        mSuggestions.add(new historyRowModel("IMDB","https://imdb.com",-1));
        mSuggestions.add(new historyRowModel("Reddit","https://reddit.com",-1));
        mSuggestions.add(new historyRowModel("Pinterest","https://pinterest.com",-1));
        mSuggestions.add(new historyRowModel("EBay","https://ebay.com",-1));
        mSuggestions.add(new historyRowModel("Trip Advisor","https://tripadvisor.com",-1));
        mSuggestions.add(new historyRowModel("Craigslist","https://craigslist.org",-1));
        mSuggestions.add(new historyRowModel("Walmart","https://walmart.com",-1));
        mSuggestions.add(new historyRowModel("Instagram","https://instagram.com",-1));
        mSuggestions.add(new historyRowModel("Google","https://google.com",-1));
        mSuggestions.add(new historyRowModel("NY Times","https://nytimes.com",-1));
        mSuggestions.add(new historyRowModel("Apple","https://apple.com",-1));
        mSuggestions.add(new historyRowModel("Linkedin","https://linkedin.com",-1));
        mSuggestions.add(new historyRowModel("Indeed","https://indeed.com",-1));
        mSuggestions.add(new historyRowModel("Play.Google","https://play.google.com",-1));
        mSuggestions.add(new historyRowModel("ESPN","https://espn.com",-1));
        mSuggestions.add(new historyRowModel("Webmd","https://webmd.com",-1));
        mSuggestions.add(new historyRowModel("CNN","https://cnn.com",-1));
        mSuggestions.add(new historyRowModel("Homedepot","https://homedepot.com",-1));
        mSuggestions.add(new historyRowModel("ETSY","https://etsy.com",-1));
        mSuggestions.add(new historyRowModel("Netflix","https://netflix.com",-1));
        mSuggestions.add(new historyRowModel("Quora","https://quora.com",-1));
        mSuggestions.add(new historyRowModel("Microsoft","https://microsoft.com",-1));
        mSuggestions.add(new historyRowModel("Target","https://target.com",-1));
        mSuggestions.add(new historyRowModel("Merriam Webster","https://merriam-webster.com",-1));
        mSuggestions.add(new historyRowModel("Forbes","https://forbes.com",-1));
        mSuggestions.add(new historyRowModel("Mapquest","https://mapquest.com",-1));
        mSuggestions.add(new historyRowModel("NIH","https://nih.gov",-1));
        mSuggestions.add(new historyRowModel("Gamepedia","https://gamepedia.com",-1));
        mSuggestions.add(new historyRowModel("Yahoo","https://yahoo.com",-1));
        mSuggestions.add(new historyRowModel("Healthline","https://healthline.com",-1));
        mSuggestions.add(new historyRowModel("Foxnews","https://foxnews.com",-1));
        mSuggestions.add(new historyRowModel("All Recipes","https://allrecipes.com",-1));
        mSuggestions.add(new historyRowModel("Quizlet","https://quizlet.com",-1));
        mSuggestions.add(new historyRowModel("Weather","https://weather.com",-1));
        mSuggestions.add(new historyRowModel("Bestbuy","https://bestbuy.com",-1));
        mSuggestions.add(new historyRowModel("Urbandictionary","https://urbandictionary.com",-1));
        mSuggestions.add(new historyRowModel("Mayoclinic","https://mayoclinic.org",-1));
        mSuggestions.add(new historyRowModel("AOL","https://aol.com",-1));
        mSuggestions.add(new historyRowModel("Genius","https://genius.com",-1));
        mSuggestions.add(new historyRowModel("Zillow","https://zillow.com",-1));
        mSuggestions.add(new historyRowModel("Usatoday","https://usatoday.com",-1));
        mSuggestions.add(new historyRowModel("Glassdoor","https://glassdoor.com",-1));
        mSuggestions.add(new historyRowModel("MSN","https://msn.com",-1));
        mSuggestions.add(new historyRowModel("Rotten Tomatoes","https://rottentomatoes.com",-1));
        mSuggestions.add(new historyRowModel("Lowes","https://lowes.com",-1));
        mSuggestions.add(new historyRowModel("Dictionary","https://dictionary.com",-1));
        mSuggestions.add(new historyRowModel("Business Insider","https://businessinsider.com",-1));
        mSuggestions.add(new historyRowModel("US News","https://usnews.com",-1));
        mSuggestions.add(new historyRowModel("Medical News Today","https://medicalnewstoday.com",-1));
        mSuggestions.add(new historyRowModel("Britannica","https://britannica.com",-1));
        mSuggestions.add(new historyRowModel("Washington Post","https://washingtonpost.com",-1));
        mSuggestions.add(new historyRowModel("USPS","https://usps.com",-1));
        mSuggestions.add(new historyRowModel("Finance Yahoo","https://finance.yahoo.com",-1));
        mSuggestions.add(new historyRowModel("IRS","https://irs.gov",-1));
        mSuggestions.add(new historyRowModel("Yellow Pages","https://yellowpages.com",-1));
        mSuggestions.add(new historyRowModel("Chase","https://chase.com",-1));
        mSuggestions.add(new historyRowModel("Retail Menot","https://retailmenot.com",-1));
        mSuggestions.add(new historyRowModel("Accuweather","https://accuweather.com",-1));
        mSuggestions.add(new historyRowModel("Way Fair","https://wayfair.com",-1));
        mSuggestions.add(new historyRowModel("GO","https://go.com",-1));
        mSuggestions.add(new historyRowModel("Live","https://live.com",-1));
        mSuggestions.add(new historyRowModel("Login Yahoo","https://login.yahoo.com",-1));
        mSuggestions.add(new historyRowModel("Steam Community","https://steamcommunity.com",-1));
        mSuggestions.add(new historyRowModel("XFinity","https://xfinity.com",-1));
        mSuggestions.add(new historyRowModel("CNET","https://cnet.com",-1));
        mSuggestions.add(new historyRowModel("IGN","https://ign.com",-1));
        mSuggestions.add(new historyRowModel("Steam Powered","https://steampowered.com",-1));
        mSuggestions.add(new historyRowModel("Macys","https://macys.com",-1));
        mSuggestions.add(new historyRowModel("Wikihow","https://wikihow.com",-1));
        mSuggestions.add(new historyRowModel("Mail Yahoo","https://mail.yahoo.com",-1));
        mSuggestions.add(new historyRowModel("Wiktionary","wiktionary.org",-1));
        mSuggestions.add(new historyRowModel("Cbssports","https://cbssports.com",-1));
        mSuggestions.add(new historyRowModel("CNBC","https://cnbc.com",-1));
        mSuggestions.add(new historyRowModel("Bank Of America","https://bankofamerica.com",-1));
        mSuggestions.add(new historyRowModel("Expedia","https://expedia.com",-1));
        mSuggestions.add(new historyRowModel("Wellsfargo","https://wellsfargo.com",-1));
        mSuggestions.add(new historyRowModel("Groupon","https://groupon.com",-1));
        mSuggestions.add(new historyRowModel("Twitch","https://twitch.tv",-1));
        mSuggestions.add(new historyRowModel("Khan Academy","https://khanacademy.org",-1));
        mSuggestions.add(new historyRowModel("The Guardian","https://theguardian.com",-1));
        mSuggestions.add(new historyRowModel("Paypal","https://paypal.com",-1));
        mSuggestions.add(new historyRowModel("Spotify","https://spotify.com",-1));
        mSuggestions.add(new historyRowModel("ATT","https://att.com",-1));
        mSuggestions.add(new historyRowModel("NFL","https://nfl.com",-1));
        mSuggestions.add(new historyRowModel("Realtor","https://realtor.com",-1));
        mSuggestions.add(new historyRowModel("CA Gov","https://ca.gov",-1));
        mSuggestions.add(new historyRowModel("Good Reads","https://goodreads.com",-1));
        mSuggestions.add(new historyRowModel("Office","https://office.com",-1));
        mSuggestions.add(new historyRowModel("UFL","https://ufl.edu",-1));
        mSuggestions.add(new historyRowModel("MLB","https://mlb.com",-1));
        mSuggestions.add(new historyRowModel("Food Network","https://foodnetwork.com",-1));
        mSuggestions.add(new historyRowModel("BBC","https://bbc.com",-1));
        mSuggestions.add(new historyRowModel("Apartments","https://apartments.com",-1));
        mSuggestions.add(new historyRowModel("NPR","https://npr.org",-1));
        mSuggestions.add(new historyRowModel("Wow Head","https://wowhead.com",-1));
        mSuggestions.add(new historyRowModel("Duckduckgo","https://duckduckgo.com",-1));
        mSuggestions.add(new historyRowModel("Bing","https://bing.com",-1));
        mSuggestions.add(new historyRowModel("Google","https://google.com",-1));
        mSuggestions.add(new historyRowModel("Genesis Search","https://boogle.store",-1));
        Log.i("ERERER10",mSuggestions.size()+"");
    }

}
