package com.darkweb.genesissearchengine.dataManager;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;

import java.util.Arrays;
import java.util.List;

import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_BRIDGE_WEBSITES;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_REFERENCE_WEBSITES;

public class bridgesDataModel {

    private String mBridges = "null";
    private boolean mLoading = false;

    public bridgesDataModel(){
        mBridges = status.sBridgesDefault;
    }

    private void onLoad(Context pContext){
        if(!mLoading){
            mLoading = true;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, CONST_GENESIS_BRIDGE_WEBSITES,
                    response -> {
                        if(response.length()>10){
                            mBridges = response;
                            status.sBridgesDefault = response;
                            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_DEFAULT,strings.BRIDGES_DEFAULT));
                            mLoading = false;
                        }else {
                            mBridges = status.sReferenceWebsites;
                        }
                    },
                    error -> {
                        mBridges = status.sReferenceWebsites;
                        mLoading = false;
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(pContext);
            requestQueue.add(stringRequest);
        }
    }

    private String onFetch(){
        try {
            return mBridges;
        }catch (Exception ignored){}
        return strings.GENERIC_EMPTY_SPACE;
    }

    public Object onTrigger(dataEnums.eBridgeWebsiteCommands p_commands, List<Object> pData){
        if(p_commands == dataEnums.eBridgeWebsiteCommands.M_LOAD){
            onLoad((Context) pData.get(0));
        }
        if(p_commands == dataEnums.eBridgeWebsiteCommands.M_FETCH){
            return onFetch();
        }

        return null;
    }

}
