package com.darkweb.genesissearchengine.appManager.helpManager;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.darkweb.genesissearchengine.helperManager.ProxiedHurlStack;
import com.darkweb.genesissearchengine.helperManager.eventObserver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class helpModel
{
    private eventObserver.eventListener mEvent;
    private String mJsonPath = "https://drive.google.com/uc?export=download&id=1es7XOAWCktGGfSnJu_o8W4_LZuudjR-T";
    private AppCompatActivity mContext;
    private ArrayList<helpDataModel> mHelpListModel;

    public helpModel(AppCompatActivity pContext, eventObserver.eventListener pEvent){
        this.mContext = pContext;
        this.mEvent = pEvent;
        this.mHelpListModel = new ArrayList<>();
    }

    private void getHelpJSON(){
        mHelpListModel.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mJsonPath,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            helpDataModel hero = new helpDataModel(
                                    obj.getString("mHeader"),
                                    obj.getString("mDescription"),
                                    obj.getString("mIcon"));
                            mHelpListModel.add(hero);
                        }
                        mEvent.invokeObserver(Collections.singletonList(mHelpListModel),helpEnums.eHelpModelCallback.M_LOAD_JSON_RESPONSE_SUCCESS);
                    } catch (JSONException e) {
                        mEvent.invokeObserver(Collections.singletonList(mHelpListModel),helpEnums.eHelpModelCallback.M_LOAD_JSON_RESPONSE_FAILURE);
                        e.printStackTrace();
                    }
                },
                error -> {
                    mEvent.invokeObserver(Collections.singletonList(mHelpListModel),helpEnums.eHelpModelCallback.M_LOAD_JSON_RESPONSE_FAILURE);
                });

        RequestQueue requestQueue = Volley.newRequestQueue(mContext/*, new ProxiedHurlStack()*/);
        requestQueue.add(stringRequest);
    }

    public Object onTrigger(helpEnums.eHelpModel pCommands, List<Object> pData){
        if(pCommands.equals(helpEnums.eHelpModel.M_LOAD_HELP_DATA)){
            getHelpJSON();
        }
        return null;
    }
}
