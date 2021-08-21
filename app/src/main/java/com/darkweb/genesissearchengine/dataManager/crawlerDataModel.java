package com.darkweb.genesissearchengine.dataManager;

import static java.lang.Thread.sleep;
import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.models.crawlerRowModel;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.thirdparty.com.google.android.exoplayer2.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

@SuppressLint("CommitPrefEdits")
class crawlerDataModel
{
    /* Initializations */
    private Set<String> mDuplicate;
    private ArrayList<crawlerRowModel> mHTML;
    private AppCompatActivity mContext;

    crawlerDataModel(AppCompatActivity pContext){
        mDuplicate = new HashSet<>();
        mHTML = new ArrayList<>();
        mContext = pContext;
        onCrawlHandler();
    }

    private void onParseHTML(String pHtml, String pURL){
        String mHost = helperMethod.getHost(pURL);

        if(mHost.contains(".onion") && !mHost.contains("genesis") && !mDuplicate.contains(pURL)){
            mHTML.add(new crawlerRowModel(pURL, pHtml));
            mDuplicate.add(pURL);
        }
    }

    /* Helper Methods */

    private void onCrawlHandler(){
        new Thread(){
            public void run(){
                while (true){
                    try {
                        sleep(1000);
                        if(mHTML.size()>0){
                            crawlerRowModel mModel = mHTML.remove(0);
                            String mHtml = mModel.getHTML();
                            String mURL = mModel.getURL();
                            String mTitle = strings.GENERIC_EMPTY_STR;
                            String mDescription = strings.GENERIC_EMPTY_STR;
                            String mKeywords = strings.GENERIC_EMPTY_STR;
                            String mtype = strings.CRAWLER_GENERIC_TYPE;
                            String mRank = strings.CRAWLER_RANK_UNVERIFIED;
                            Document doc = Jsoup.parse(mHtml);

                            Map<String, String> metas = new HashMap<>();
                            Elements metaTags = doc.getElementsByTag("meta");

                            for (Element metaTag : metaTags) {
                                String content = metaTag.attr("content");
                                String name = metaTag.attr("name");
                                metas.put(name, content);
                            }

                            Element abbrElement = doc.select("abbr").first();

                            mDescription = metas.get("description");
                            mTitle = doc.title();
                            mKeywords = metas.get("keywords");

                            if(mDescription==null){
                                mDescription = strings.GENERIC_EMPTY_null;
                            }
                            if(mTitle==null){
                                mTitle = strings.GENERIC_EMPTY_null;
                            }
                            if(mKeywords==null){
                                mKeywords = strings.GENERIC_EMPTY_null;
                            }


                            if(mDescription.length()<200){
                                Elements p= doc.getElementsByTag("h1");
                                for (Element x: p) {
                                    mDescription+= " " + x.text();
                                }
                            }
                            if(mDescription.length()<200){
                                Elements p= doc.getElementsByTag("p");
                                for (Element x: p) {
                                    mDescription+= " " + x.text();
                                }
                            }
                            mDescription = mDescription.trim().replaceAll(" +", " ");
                            mDescription = mDescription.trim().replaceAll("\n", "");

                            if(mTitle!=null && mTitle.length()>500){
                                mTitle = mTitle.substring(0,500);
                            }
                            if(mDescription!=null && mDescription.length()>1000){
                                mDescription = mDescription.substring(0,1000);
                            }
                            if(mKeywords!=null && mKeywords.length()>500){
                                mKeywords = mKeywords.substring(0,500);
                            }
                            if(mDescription.length()<=10){
                                return;
                            }

                            if(mURL==null){
                                mURL = strings.GENERIC_EMPTY_STR;
                            }
                            mURL = URLEncoder.encode(mURL);
                            if(mTitle==null){
                                mTitle = strings.GENERIC_EMPTY_STR;
                            }
                            mTitle = URLEncoder.encode(mTitle);
                            if(mDescription==null){
                                mDescription = strings.GENERIC_EMPTY_STR;
                            }
                            mDescription = URLEncoder.encode(mDescription);
                            if(mKeywords==null){
                                mKeywords = strings.GENERIC_EMPTY_STR;
                            }
                            mKeywords = URLEncoder.encode(mKeywords);

                            mDescription = "Sad";
                            mTitle = "asd";
                            mKeywords = "asd";
                            String mURL_POST = "https://www.genesishiddentechnologies.com/update_cache?url="+mURL+"&key_word="+mKeywords+"&desc="+mDescription+"&title="+mTitle+"&s_type="+mtype;

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, mURL_POST,
                                    response -> {
                                        Log.i("as","as");
                                    },
                                    error -> {
                                        Log.i("as","as");
                                    });

                            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                            requestQueue.add(stringRequest);

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }.start();
    }


    /* External Triggers */

    public Object onTrigger(dataEnums.eCrawlerCommands pCommands, List<Object> pData){
        if(pCommands.equals(dataEnums.eCrawlerCommands.M_INDEX_URL)){
            onParseHTML(pData.get(0).toString(), pData.get(1).toString());
        }
        return null;
    }

}
