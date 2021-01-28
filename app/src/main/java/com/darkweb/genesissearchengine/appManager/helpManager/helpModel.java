package com.darkweb.genesissearchengine.appManager.helpManager;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.darkweb.genesissearchengine.constants.constants.*;

class helpModel
{
    private eventObserver.eventListener mEvent;
    private String mJsonPath = CONST_SERVER;
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
                                    obj.getString(CONST_HELP_MODEL_HEADER),
                                    obj.getString(CONST_HELP_MODEL_DESCRIPTION),
                                    obj.getString(CONST_HELP_MODEL_ICON));
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
