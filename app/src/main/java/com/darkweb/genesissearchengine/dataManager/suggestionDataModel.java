package com.darkweb.genesissearchengine.dataManager;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.SuggestionSpan;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;

import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class suggestionDataModel implements SpellCheckerSession.SpellCheckerSessionListener {

    /*Private Variables*/

    private SpellCheckerSession mSpellCheckerSession;
    private TextServicesManager mTextServicesManager;
    private ArrayList<historyRowModel> mHintListLocalCache;

    /*Initializations*/

    public suggestionDataModel(Context mContext){
        mTextServicesManager = (TextServicesManager) mContext.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        mSpellCheckerSession = mTextServicesManager.newSpellCheckerSession(null, null, this, true);
        mHintListLocalCache = initSuggestions();
    }

    /*Helper Methods*/

    private ArrayList<historyRowModel> getSuggestions(String pQuery, ArrayList<historyRowModel> pHistory, ArrayList<bookmarkRowModel> pBookmarks){
        ArrayList<historyRowModel> mHistory = pHistory;
        ArrayList<bookmarkRowModel> mBookmarks = pBookmarks;

        pQuery = pQuery.toLowerCase();
        ArrayList<historyRowModel> mList = new ArrayList<>();

        if(status.sSettingSearchHistory) {
            for (int count = 0; count <= mBookmarks.size() - 1 && mBookmarks.size() < 500; count++) {
                if (mBookmarks.get(count).getHeader().toLowerCase().contains(pQuery)) {
                    mList.add(0, new historyRowModel(mBookmarks.get(count).getHeader(), mBookmarks.get(count).getDescription(), -1));
                } else if (mList.size() > 0 && mBookmarks.get(count).getDescription().toLowerCase().contains(pQuery)) {
                    mList.add(mList.size() - 1, new historyRowModel(mBookmarks.get(count).getHeader(), mBookmarks.get(count).getDescription(), -1));
                }
            }
        }

        String mQueryOriginal = pQuery;
        if(status.sSettingSearchHistory){
            pQuery = pQuery.toLowerCase();
            for(int count = 0; count<= mHistory.size()-1 && mHistory.size()<500; count++){
                historyRowModel mTempModel;
                if(mHistory.get(count).getHeader().toLowerCase().contains(pQuery)){
                    mTempModel = new historyRowModel(mHistory.get(count).getHeader(),mHistory.get(count).getDescription(),-1);
                    if(!mList.contains(mTempModel)){
                        mList.add(mTempModel);
                    }
                }else if(mHistory.get(count).getDescription().toLowerCase().contains(pQuery)){
                    mTempModel = new historyRowModel(mHistory.get(count).getHeader(),mHistory.get(count).getDescription(),-1);
                    if(!mList.contains(mTempModel)){
                        mList.add(mTempModel);
                    }
                }
            }
        }

        for(int count = 0; count<= mHintListLocalCache.size()-1 && mHintListLocalCache.size()<500; count++){
            if(mHintListLocalCache.get(count).getHeader().toLowerCase().contains(pQuery)){
                mList.add(new historyRowModel(mHintListLocalCache.get(count).getHeader(),mHintListLocalCache.get(count).getDescription(),-1));
            }else if(mHintListLocalCache.get(count).getDescription().toLowerCase().contains(pQuery)){
                if(mList.size()==0){
                    mList.add(new historyRowModel(mHintListLocalCache.get(count).getHeader(),mHintListLocalCache.get(count).getDescription(),-1));
                }else {
                    mList.add(new historyRowModel(mHintListLocalCache.get(count).getHeader(),mHintListLocalCache.get(count).getDescription(),-1));
                }
            }
        }

        /*Duplicate handler*/
        ArrayList<String> mDuplicateHandler = new ArrayList<>();
        for(int mCounter=0;mCounter<mList.size();mCounter++){
            if(mDuplicateHandler.contains(mList.get(mCounter).getDescription())){
                mList.remove(mCounter);
                mCounter-=1;
            }else {
                mDuplicateHandler.add(0,mList.get(mCounter).getDescription());
            }
        }

        if(!pQuery.equals(strings.GENERIC_EMPTY_STR) && !pQuery.equals("about:blank") && !pQuery.contains("?") &&  !pQuery.contains("/")  && !pQuery.contains(" ") && !pQuery.contains("  ") && !pQuery.contains("\n")){
            if(mList.size()<3){
                int sepPos = pQuery.indexOf(".");
                if (sepPos == -1) {
                    mList.add( 0,new historyRowModel(mQueryOriginal+".com", strings.GENERIC_EMPTY_STR,-1));
                    mList.add( 0,new historyRowModel(mQueryOriginal+".onion", strings.GENERIC_EMPTY_STR,-1));
                }else
                {
                    if(!pQuery.equals(pQuery.substring(0,sepPos)+".com")){
                        mList.add( 0,new historyRowModel(pQuery.substring(0,sepPos)+".com", strings.GENERIC_EMPTY_STR,-1));
                    }
                    if(!pQuery.equals(pQuery.substring(0,sepPos)+".onion")){
                        mList.add( 0,new historyRowModel(pQuery.substring(0,sepPos)+".onion", strings.GENERIC_EMPTY_STR,-1));
                    }
                }
            }
            mList.add( 0,new historyRowModel(mQueryOriginal, strings.GENERIC_EMPTY_STR,-1));
        }

        if (Pattern.matches("[a-zA-Z]+",pQuery)){
            Spannable str = new SpannableString(pQuery);
            SuggestionSpan[] spanned = str.getSpans(0, pQuery.length(), SuggestionSpan.class);
            for (SuggestionSpan suggestionSpan : spanned) {
                mList.add(0, new historyRowModel(suggestionSpan.toString(), strings.GENERIC_EMPTY_STR, -1));
            }
        }

        return mList;
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

    /*External Redirections*/

    public Object onTrigger(dataEnums.eSuggestionCommands pCommands, List<Object> pData){

        if(pCommands == dataEnums.eSuggestionCommands.M_GET_SUGGESTIONS)
        {
           return getSuggestions((String) pData.get(0), (ArrayList<historyRowModel>)pData.get(1), (ArrayList<bookmarkRowModel>)pData.get(2));
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
