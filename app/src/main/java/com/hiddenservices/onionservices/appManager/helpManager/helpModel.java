package com.hiddenservices.onionservices.appManager.helpManager;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.dataManager.dataController;
import com.hiddenservices.onionservices.dataManager.dataEnums;
import com.hiddenservices.onionservices.eventObserver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hiddenservices.onionservices.constants.constants.*;

class helpModel {
    private eventObserver.eventListener mEvent;
    private String mJsonPath;
    private AppCompatActivity mContext;
    private ArrayList<helpDataModel> mHelpListModel;
    private boolean mIsLoaded = false;

    public helpModel(AppCompatActivity pContext, eventObserver.eventListener pEvent) {
        this.mContext = pContext;
        this.mEvent = pEvent;
        this.mHelpListModel = new ArrayList<>();

        if (status.sDeveloperBuild) {
            this.mJsonPath = CONST_SERVER_DEV;
        } else {
            this.mJsonPath = CONST_SERVER;
        }
    }

    protected void onInit(){

    }

    private void getHelpJSON() {

        ArrayList<helpDataModel> mTempModel = (ArrayList<helpDataModel>) dataController.getInstance().invokeHelp(dataEnums.eHelpCommands.M_GET_HELP, null);

        mHelpListModel.clear();
        if (!mTempModel.isEmpty()) {
            mIsLoaded = true;
            mHelpListModel.addAll(mTempModel);
            mEvent.invokeObserver(Collections.singletonList(mHelpListModel), helpEnums.eHelpModelCallback.M_LOAD_JSON_RESPONSE_SUCCESS);
        } else {
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
                                dataController.getInstance().invokeHelp(dataEnums.eHelpCommands.M_SET_HELP, Collections.singletonList(mHelpListModel));
                            }
                            mEvent.invokeObserver(Collections.singletonList(mHelpListModel), helpEnums.eHelpModelCallback.M_LOAD_JSON_RESPONSE_SUCCESS);
                        } catch (JSONException e) {
                            mEvent.invokeObserver(Collections.singletonList(mHelpListModel), helpEnums.eHelpModelCallback.M_LOAD_JSON_RESPONSE_FAILURE);
                        }
                    },
                    error -> mEvent.invokeObserver(Collections.singletonList(mHelpListModel), helpEnums.eHelpModelCallback.M_LOAD_JSON_RESPONSE_FAILURE));

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(stringRequest);
        }
    }

    private boolean IsLoaded() {
        return mIsLoaded;
    }

    public Object onTrigger(helpEnums.eHelpModel pCommands, List<Object> ignoredPData) {
        if (pCommands.equals(helpEnums.eHelpModel.M_LOAD_HELP_DATA)) {
            getHelpJSON();
        } else if (pCommands.equals(helpEnums.eHelpModel.M_IS_LOADED)) {
            return IsLoaded();
        }
        return null;
    }
}
