package com.hiddenservices.onionservices.dataManager;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.dataManager.models.crawlerRowModel;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import org.apache.commons.text.StringEscapeUtils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
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
        if (mHost != null){
            if(!helperMethod.isValidURL(pURL)){
                return;
            }
            pURL = helperMethod.normalize(pURL);
            if(mDuplicate.size()<30 && mHost.contains(".onion") && !mHost.contains("genesis") && !mHost.contains("trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd") && !mDuplicate.contains(pURL)){
                mHTML.add(new crawlerRowModel(pURL, pHtml));
                mDuplicate.add(pURL);
            }
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
                }, error -> {
                    Log.i("ad","");
                }) {
                    protected Map<String, String> getParams() {
                        mHtml = StringEscapeUtils.escapeXml11(mHtml);
                        mHtml = mHtml.replace("\n"," ");

                        Map<String, String> MyData = new HashMap<>();
                        MyData.put("m_html", mHtml);
                        MyData.put("m_url", mURL);
                        return MyData;
                    }
                };
                mRequestData.setRetryPolicy(new DefaultRetryPolicy(
                        30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                            if (mHTML.size()<100){
                                onSendRequest();
                            }
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
