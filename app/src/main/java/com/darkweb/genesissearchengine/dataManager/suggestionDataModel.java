package com.darkweb.genesissearchengine.dataManager;

import android.content.Context;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextServicesManager;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import java.util.ArrayList;
import java.util.List;

public class suggestionDataModel implements SpellCheckerSession.SpellCheckerSessionListener {

    /*Private Variables*/

    private SpellCheckerSession mSpellCheckerSession;
    private TextServicesManager mTextServicesManager;
    private ArrayList<historyRowModel> mHintListLocalCache;
    private ArrayList<historyRowModel> mCurrentList = new ArrayList<>();


    /*Initializations*/

    public suggestionDataModel(Context mContext){
        mTextServicesManager = (TextServicesManager) mContext.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        mSpellCheckerSession = mTextServicesManager.newSpellCheckerSession(null, null, this, true);
        mHintListLocalCache = initSuggestions();
    }

    /*Helper Methods*/



    private ArrayList<historyRowModel> getDefaultSuggestionsOnStart(String pQuery, int mSize, ArrayList<String> mDuplicationHandler, boolean pDefaultHostChaned){

        mCurrentList.clear();
        if(!pQuery.equals(strings.GENERIC_EMPTY_STR) && !pQuery.equals("about:blank") && !pQuery.contains("?") &&  !pQuery.contains("/")  && !pQuery.contains(" ") && !pQuery.contains("  ") && !pQuery.contains("\n")){
            mCurrentList.size();
            int sepPos = pQuery.indexOf(".");
            if (sepPos == -1) {
                mCurrentList.add( 0, new historyRowModel(pQuery+".com", strings.GENERIC_EMPTY_STR,-1));
                mCurrentList.add( 0, new historyRowModel(pQuery+".onion", strings.GENERIC_EMPTY_STR,-1));
            }else
            {
                if(!pQuery.equals(pQuery.substring(0,sepPos)+".com")){
                    mCurrentList.add( 0, new historyRowModel(pQuery.substring(0,sepPos)+".com", strings.GENERIC_EMPTY_STR,-1));
                }
                if(!pQuery.equals(pQuery.substring(0,sepPos)+".onion")){
                    mCurrentList.add( 0, new historyRowModel(pQuery.substring(0,sepPos)+".onion", strings.GENERIC_EMPTY_STR,-1));
                }
            }
        }

        mCurrentList.add( 0,new historyRowModel(pQuery, strings.GENERIC_EMPTY_STR,-1));
        return mCurrentList;
    }

    private ArrayList<historyRowModel> getDefaultSuggestions(String pQuery, int mSize, ArrayList<String> mDuplicationHandler, boolean pDefaultHostChaned){

        for(int count = 0; count<= mHintListLocalCache.size()-1 && mHintListLocalCache.size()<500; count++){
            if(mHintListLocalCache.get(count).getHeader().toLowerCase().contains(pQuery)){
                if(mHintListLocalCache.get(count).getHeader().toLowerCase().startsWith(pQuery)){
                    if(mDuplicationHandler!=null && !mDuplicationHandler.contains(mHintListLocalCache.get(count).getDescriptionParsed())) {
                        if(pDefaultHostChaned){
                            mCurrentList.add(1, new historyRowModel(mHintListLocalCache.get(count).getHeader(), mHintListLocalCache.get(count).getDescriptionParsed(), -1));
                        }else {
                            mCurrentList.add(0, new historyRowModel(mHintListLocalCache.get(count).getHeader(), mHintListLocalCache.get(count).getDescriptionParsed(), -1));
                        }
                    }
                    if(mCurrentList.size() + mSize > 6){
                        break;
                    }
                }else {
                    if(mCurrentList.size() + mSize <= 6){
                        mCurrentList.add(new historyRowModel(mHintListLocalCache.get(count).getHeader(),mHintListLocalCache.get(count).getDescriptionParsed(),-1));
                    }
                }
            }else if(mHintListLocalCache.get(count).getDescriptionParsed().toLowerCase().contains(pQuery)){
                if(mHintListLocalCache.get(count).getHeader().toLowerCase().startsWith(pQuery)){
                    if(mDuplicationHandler!=null && !mDuplicationHandler.contains(mHintListLocalCache.get(count).getDescriptionParsed())) {
                        if(pDefaultHostChaned){
                            mCurrentList.add(1, new historyRowModel(mHintListLocalCache.get(count).getHeader(), mHintListLocalCache.get(count).getDescriptionParsed(), -1));
                        }else {
                            mCurrentList.add(0, new historyRowModel(mHintListLocalCache.get(count).getHeader(), mHintListLocalCache.get(count).getDescriptionParsed(), -1));
                        }
                    }
                    if(mCurrentList.size() + mSize > 6){
                        break;
                    }
                }else {
                    if(mCurrentList.size() + mSize <= 6){
                        mCurrentList.add(new historyRowModel(mHintListLocalCache.get(count).getHeader(),mHintListLocalCache.get(count).getDescriptionParsed(),-1));
                    }
                }
            }
        }

        return mCurrentList;
    }

    private ArrayList<historyRowModel> getSuggestions(String pQuery, ArrayList<historyRowModel> pHistory, ArrayList<bookmarkRowModel> pBookmarks){

        mCurrentList = new ArrayList<>();
        mCurrentList.clear();
        String mQueryOriginal = pQuery;
        pQuery = pQuery.replace("+","%").replace(" ","+");
        ArrayList<historyRowModel> mHistory = pHistory;
        ArrayList<bookmarkRowModel> mBookmarks = pBookmarks;

        pQuery = pQuery.toLowerCase();
        ArrayList<String> mDuplicationHandler = new ArrayList<>();

        if(status.sSettingSearchHistory){
            pQuery = pQuery.toLowerCase();
            for(int count = 0; count<= mHistory.size()-1 && mHistory.size()<500; count++){
                historyRowModel mTempModel;
                if(!mDuplicationHandler.contains(mHistory.get(count).getDescription())){
                    if(mHistory.get(count).getHeader().toLowerCase().contains(pQuery)){
                        mTempModel = new historyRowModel(mHistory.get(count).getHeader(),mHistory.get(count).getDescription(),-1);
                        if(!mCurrentList.contains(mTempModel)){
                            mDuplicationHandler.add(mTempModel.getDescription());
                            mCurrentList.add(mTempModel);
                        }
                    }else if(mHistory.get(count).getDescription().toLowerCase().contains(pQuery)){
                        mTempModel = new historyRowModel(mHistory.get(count).getHeader(),mHistory.get(count).getDescription(),-1);
                        if(!mCurrentList.contains(mTempModel)){
                            mDuplicationHandler.add(mTempModel.getDescription());
                            mCurrentList.add(mTempModel);
                        }
                    }
                    if(mCurrentList.size()>6){
                        break;
                    }
                }
            }
        }

        if(status.sSettingSearchHistory && mCurrentList.size()>4) {
            for (int count = 0; count <= mBookmarks.size() - 1 && mBookmarks.size() < 500; count++) {
                if(!mDuplicationHandler.contains(mBookmarks.get(count).getDescription())){
                    if (mBookmarks.get(count).getHeader().toLowerCase().contains(pQuery)) {
                        mDuplicationHandler.add(mBookmarks.get(count).getDescription());
                        mCurrentList.add(mCurrentList.size() - 1,new historyRowModel(mBookmarks.get(count).getHeader(), mBookmarks.get(count).getDescription(), -1));
                    } else if (mCurrentList.size() > 0 && mBookmarks.get(count).getDescription().toLowerCase().contains(pQuery)) {
                        mDuplicationHandler.add(mBookmarks.get(count).getDescription());
                        mCurrentList.add(mCurrentList.size() - 1, new historyRowModel(mBookmarks.get(count).getHeader(), mBookmarks.get(count).getDescription(), -1));
                    }
                    if(mCurrentList.size()>6){
                        break;
                    }
                }
            }
        }

        boolean mDefaultHostChaned = false;
        if(mCurrentList.size()>3){
            String mHost1 = helperMethod.getHost(helperMethod.completeURL(mCurrentList.get(0).getDescription()));
            String mHost2 = helperMethod.getHost(helperMethod.completeURL(mCurrentList.get(1).getDescription()));
            String mHost3 = helperMethod.getHost(helperMethod.completeURL(mCurrentList.get(2).getDescription()));

            String mHostReal = mHost1.replace("www.","");
            if(mHost1.equals(mHost2) && mHost1.equals(mHost3) && !mDuplicationHandler.contains(mHostReal)){
                mCurrentList.add( 0,new historyRowModel(mHostReal, strings.GENERIC_EMPTY_STR,-1));
                mDuplicationHandler.add(mHostReal);
            }
            mDefaultHostChaned = true;
        }

        getDefaultSuggestions(pQuery, mCurrentList.size(), mDuplicationHandler, mDefaultHostChaned);

        boolean mHostAppend = false;
        if(mCurrentList.size()>1 && helperMethod.getHost(helperMethod.completeURL(mCurrentList.get(0).getDescription())).equals(helperMethod.completeURL(mCurrentList.get(1).getDescription()))){
            mHostAppend = true;
        }

        int mSize = mCurrentList.size();
        if(mCurrentList.size()<3){

            if(!mQueryOriginal.equals(strings.GENERIC_EMPTY_STR) && !mQueryOriginal.equals("about:blank") && !mQueryOriginal.contains("?") &&  !mQueryOriginal.contains("/")  && !mQueryOriginal.contains(" ") && !mQueryOriginal.contains("  ") && !mQueryOriginal.contains("\n")){
                mCurrentList.size();
                int sepPos = pQuery.indexOf(".");
                if (sepPos == -1) {
                    mCurrentList.add( mSize, new historyRowModel(mQueryOriginal+".com", strings.GENERIC_EMPTY_STR,-1));
                    mCurrentList.add( mSize, new historyRowModel(mQueryOriginal+".onion", strings.GENERIC_EMPTY_STR,-1));
                }else
                {
                    if(!pQuery.equals(pQuery.substring(0,sepPos)+".com")){
                        mCurrentList.add( mSize, new historyRowModel(pQuery.substring(0,sepPos)+".com", strings.GENERIC_EMPTY_STR,-1));
                    }
                    if(!pQuery.equals(pQuery.substring(0,sepPos)+".onion")){
                        mCurrentList.add( mSize, new historyRowModel(pQuery.substring(0,sepPos)+".onion", strings.GENERIC_EMPTY_STR,-1));
                    }
                }
            }
        }

        if(mHostAppend){
            if(mCurrentList.get(0).getDescription().startsWith(pQuery)){
                mCurrentList.add( mSize,new historyRowModel(mCurrentList.get(0).getHeader(), mCurrentList.get(0).getDescription(),-1));
            }
        }

        if(pQuery.length()>0){
            if(!pQuery.equals("about:blank")){
                mCurrentList.add( mSize,new historyRowModel(pQuery, strings.GENERIC_EMPTY_STR,-1));
            }
        }
        if(mCurrentList.size()<=0) {
            mCurrentList.add( mSize,new historyRowModel("Genesis Search", "genesis.onion",-1));
        }

        return mCurrentList;
    }

    private ArrayList<historyRowModel> initSuggestions(){
        mHintListLocalCache = new ArrayList<>();

        mHintListLocalCache.add(new historyRowModel("Duckduckgo","https://duckduckgo.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("BBC","https://bbc.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Youtube","https://youtube.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Facebook","https://facebook.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Twitter","https://twitter.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Amazon","https://amazon.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("IMDB","https://imdb.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Reddit","https://reddit.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Pinterest","https://pinterest.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("EBay","https://ebay.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Trip Advisor","https://tripadvisor.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Craigslist","https://craigslist.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Walmart","https://walmart.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Instagram","https://instagram.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Google","https://google.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("NY Times","https://nytimes.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Apple","https://apple.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Linkedin","https://linkedin.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Play.Google","https://play.google.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("ESPN","https://espn.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Webmd","https://webmd.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("CNN","https://cnn.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Homedepot","https://homedepot.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("ETSY","https://etsy.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Netflix","https://netflix.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Quora","https://quora.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Microsoft","https://microsoft.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Target","https://target.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Merriam Webster","https://merriam-webster.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Forbes","https://forbes.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Mapquest","https://mapquest.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("NIH","https://nih.gov",-1 ));
        mHintListLocalCache.add(new historyRowModel("Gamepedia","https://gamepedia.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Yahoo","https://yahoo.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Healthline","https://healthline.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Foxnews","https://foxnews.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("All Recipes","https://allrecipes.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Quizlet","https://quizlet.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Weather","https://weather.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Bestbuy","https://bestbuy.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Urbandictionary","https://urbandictionary.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Mayoclinic","https://mayoclinic.org",-1 ));
        mHintListLocalCache.add(new historyRowModel("AOL","https://aol.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Genius","https://genius.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Zillow","https://zillow.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Usatoday","https://usatoday.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Glassdoor","https://glassdoor.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("MSN","https://msn.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Rotten Tomatoes","https://rottentomatoes.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Lowes","https://lowes.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Dictionary","https://dictionary.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Business Insider","https://businessinsider.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("US News","https://usnews.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Medical News Today","https://medicalnewstoday.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Britannica","https://britannica.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Washington Post","https://washingtonpost.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("USPS","https://usps.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Finance Yahoo","https://finance.yahoo.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("IRS","https://finance.irs.gov",-1 ));
        mHintListLocalCache.add(new historyRowModel("Yellow Pages","https://yellowpages.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Chase","https://chase.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Retail Menot","https://retailmenot.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Accuweather","https://accuweather.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Way Fair","https://wayfair.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("GO","https://go.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Live","https://live.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Login Yahoo","https://login.yahoo.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Steam Community","https://steamcommunity.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("XFinity","https://xfinity.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("CNET","https://cnet.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("IGN","https://ign.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Steam Powered","https://steampowered.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Macys","https://macys.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Wikihow","https://wikihow.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Wikipedia","https://en.wikipedia.org/",-1 ));
        mHintListLocalCache.add(new historyRowModel("Mail Yahoo","https://mail.yahoo.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Wiktionary","https://wiktionary.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Cbssports","https://cbssports.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("CNBC","https://cnbc.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Bank Of America","https://bankofamerica.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Expedia","https://expedia.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Wellsfargo","https://wellsfargo.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Groupon","https://groupon.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Twitch","https://twitch.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Khan Academy","https://khanacademy.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("The Guardian","https://theguardian.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Paypal","https://paypal.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Spotify","https://spotify.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("ATT","https://att.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("NFL","https://nfl.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Realtor","https://realtor.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("CA Gov","https://ca.gov",-1 ));
        mHintListLocalCache.add(new historyRowModel("Good Reads","https://goodreads.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Office","https://office.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("UFL","https://ufl.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("MLB","https://mlb.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Food Network","https://foodnetwork.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Apartments","https://apartments.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("NPR","https://npr.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Wow Head","https://wowhead.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Bing","https://bing.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Google","https://google.com",-1 ));
        mHintListLocalCache.add(new historyRowModel("Genesis Search","https://genesis.onion",-1 ));

        return mHintListLocalCache;
    }

    /*External Redirections*/

    public Object onTrigger(dataEnums.eSuggestionCommands pCommands, List<Object> pData){

        if(pCommands == dataEnums.eSuggestionCommands.M_GET_SUGGESTIONS)
        {
            return getSuggestions((String) pData.get(0), (ArrayList<historyRowModel>)pData.get(1), (ArrayList<bookmarkRowModel>)pData.get(2));
        }
        else if(pCommands == dataEnums.eSuggestionCommands.M_GET_DEFAULT_SUGGESTION)
        {
            return getDefaultSuggestionsOnStart((String) pData.get(0),0, null, false);
        }

        return null;
    }

    /*Local Overrides*/

    @Override
    public void onGetSuggestions(SuggestionsInfo[] results) {

    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {

    }
}
