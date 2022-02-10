package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.models.crawlerRowModel;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.thirdparty.com.google.android.exoplayer2.util.Log;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    }

    private void onInit(){
        if(!status.sCrawlerStatusStarted){
            status.sCrawlerStatusStarted = true;
            onExecute();
        }
    }

    private void onParseHTML(String pHtml, String pURL){
        String mHost = helperMethod.getHost(pURL);

        if(mHost.contains(".onion") && !mHost.contains("genesis") && !mDuplicate.contains(pURL)){
            mHTML.add(new crawlerRowModel(pURL, pHtml));
            mDuplicate.add(pURL);
        }
    }

    /* Helper Methods */

    public void onExecute(){
        new Thread(){

            String mHtml = strings.GENERIC_EMPTY_STR;
            String mURL = strings.GENERIC_EMPTY_STR;
            String mTitle = strings.GENERIC_EMPTY_STR;
            String mDescription = strings.GENERIC_EMPTY_STR;
            String mKeywords = strings.GENERIC_EMPTY_STR;
            String mtype = strings.CRAWLER_GENERIC_TYPE;

            private void onParseHTML(){
                Document pDoc = Jsoup.parse(mHtml);
                Map<String, String> metas = new HashMap<>();
                Elements metaTags = pDoc.getElementsByTag("meta");
                for (Element metaTag : metaTags) {
                    String content = metaTag.attr("content");
                    String name = metaTag.attr("name");
                    metas.put(name, content);
                }
                if(metas.containsKey("description")){
                    mDescription = metas.get("description");
                }
                if(metas.containsKey("keywords")){
                    mKeywords = metas.get("keywords");
                }
                mTitle = pDoc.title();

                if(mDescription.length()<200){
                    Elements p= pDoc.getElementsByTag("h1");
                    for (Element x: p) {
                        mDescription+= " " + x.text();
                    }
                }
                if(mDescription.length()<200){
                    Elements p= pDoc.getElementsByTag("p");
                    for (Element x: p) {
                        mDescription+= " " + x.text();
                    }
                }
            }

            private void onCleanData(){
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
            }

            private void onSendRequest(){
                String mURL_POST = "https://www.trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion/update_cache?url="+mURL+"&key_word="+mKeywords+"&desc="+mDescription+"&title="+mTitle+"&s_type="+mtype;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, mURL_POST,
                        response -> {
                            Log.i("adsa","asd");
                        },
                        error -> {
                            Log.i("adsa","asd");
                        });

                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                requestQueue.add(stringRequest);
            }

            private void onEncodeData(){
                mURL = URLEncoder.encode(mURL);
                mTitle = URLEncoder.encode(mTitle);
                mDescription = URLEncoder.encode(mDescription);
                mKeywords = URLEncoder.encode(mKeywords);
            }

            public void run(){
                while (true){
                    try {
                        sleep(1000);
                        if(mHTML.size()>0){
                            crawlerRowModel mModel = mHTML.remove(0);
                            mHtml = mModel.getHTML();
                            mURL = mModel.getURL();

                            onParseHTML();
                            onCleanData();
                            onEncodeData();
                            onSendRequest();

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
        if(pCommands.equals(dataEnums.eCrawlerCommands.M_INIT)){
            onInit();
        }
        return null;
    }

}
