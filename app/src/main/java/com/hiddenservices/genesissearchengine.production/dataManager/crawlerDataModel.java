package com.hiddenservices.genesissearchengine.production.dataManager;

import android.annotation.SuppressLint;
import android.text.Html;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hiddenservices.genesissearchengine.production.constants.status;
import com.hiddenservices.genesissearchengine.production.constants.strings;
import com.hiddenservices.genesissearchengine.production.dataManager.models.crawlerRowModel;
import com.hiddenservices.genesissearchengine.production.helperManager.helperMethod;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.thirdparty.com.google.android.exoplayer2.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.methods.CloseableHttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.client.methods.RequestBuilder;
import ch.boye.httpclientandroidlib.impl.client.CloseableHttpClient;
import ch.boye.httpclientandroidlib.impl.client.HttpClients;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.util.EntityUtils;

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
        try {
            pURL = helperMethod.normalize(pURL);
            if(mDuplicate.size()<30 && mHost.contains(".onion") && !mHost.contains("genesis") && !mHost.contains("trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd") && !mDuplicate.contains(pURL)){
                mHTML.add(new crawlerRowModel(pURL, pHtml));
                mDuplicate.add(pURL);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public class ProxiedHurlStack extends HurlStack {

        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException {

            Proxy mProxy = new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved("127.0.0.1", 9050));//the proxy server(Can be your laptop ip or company proxy)
            HttpURLConnection mConnection = (HttpURLConnection) url.openConnection(mProxy);

            return mConnection;
        }
    }

    public void onExecute(){
        new Thread(){

            String mHtml = strings.GENERIC_EMPTY_STR;
            String mURL = strings.GENERIC_EMPTY_STR;

            private void onSendRequest(){
                RequestQueue mRequestQueue = Volley.newRequestQueue(mContext, new ProxiedHurlStack());
                String url = "http://trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion/user_index/";
                StringRequest mRequestData = new StringRequest(Request.Method.POST, url, response -> {
                    Log.d("",response);
                }, error -> {
                    Log.d("",error.toString());
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> MyData = new HashMap<>();
                        MyData.put("m_html", mHtml);
                        MyData.put("m_url", mURL);
                        return MyData;
                    }
                };
                mRequestQueue.add(mRequestData);
            }

            public void run(){
                while (true){
                    try {
                        sleep(1000);
                        if(mHTML.size()>0){
                            crawlerRowModel mModel = mHTML.remove(0);
                            mHtml = mModel.getHTML();
                            mURL = mModel.getURL();

                            onParseHTML(mHtml, mURL);
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
