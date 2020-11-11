package com.darkweb.genesissearchengine.dataManager;

import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class suggestionDataModel {

    private ArrayList<historyRowModel> mSuggestions = new ArrayList<>();
    private Map<String, historyRowModel> mSuggestionCache = new HashMap<>();

    private void initSuggestions(){

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

    private void addSuggenstions(String url, String title,boolean isLoading){
        url = helperMethod.removeLastSlash(url);
        if(url.length()>1500 || title.equals("$TITLE") || title.equals("loading")){
            return;
        }

        url = helperMethod.removeLastSlash(url);
        url = helperMethod.urlWithoutPrefix(url);
        historyRowModel tempModel = mSuggestionCache.get(url);

        if(tempModel==null) {
            historyRowModel model = new historyRowModel(title, url, -1);
            mSuggestionCache.put(url, model);
            mSuggestions.add(0, mSuggestionCache.get(url));

        }
        else {
            updateSuggestionURL(url,title);
        }

    }

    private void clearSuggestion(){
        mSuggestions.clear();
        initSuggestions();
    }

    private void updateSuggestionURL(String url, String newURL){
        url = helperMethod.removeLastSlash(url);
        if(url.length()>1500){
            return;
        }
        url = helperMethod.removeLastSlash(url);
        url = helperMethod.urlWithoutPrefix(url);
        historyRowModel model = mSuggestionCache.get(url);
        if(model!=null){
            mSuggestionCache.remove(url);
            if(!newURL.equals("loading"))
                model.setHeader(newURL);
            mSuggestionCache.put(url,model);
        }
    }

    private ArrayList<historyRowModel> getmSuggestions(){
        return mSuggestions;
    }

    public Object onTrigger(dataEnums.eSuggestionCommands p_commands, List<Object> p_data){
        if(p_commands.equals(dataEnums.eSuggestionCommands.M_CLEAR_SUGGESTION)){
            clearSuggestion();
        }
        else if(p_commands.equals(dataEnums.eSuggestionCommands.M_INIT_SUGGESTION)){
            initSuggestions();
        }
        else if(p_commands.equals(dataEnums.eSuggestionCommands.M_GET_SUGGESTION)){
            return getmSuggestions();
        }
        else if(p_commands.equals(dataEnums.eSuggestionCommands.M_UPDATE_SUGGESTION)){
            updateSuggestionURL((String)p_data.get(0),(String) p_data.get(1));
        }
        else if(p_commands.equals(dataEnums.eSuggestionCommands.M_ADD_SUGGESTION)){
            addSuggenstions((String)p_data.get(0),(String) p_data.get(1),true);
        }
        return null;
    }

}
