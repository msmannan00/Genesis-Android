package com.hiddenservices.onionservices.dataManager;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;

import java.util.Arrays;
import java.util.List;

import static com.hiddenservices.onionservices.constants.constants.*;

public class bridgesDataModel {

    /* Local Variables */

    private String mBridges;
    private boolean mLoading = false;

    /* Initializations */

    public bridgesDataModel() {
    }

    /* Helper Methods */

    private void onLoad(Context pContext) {
        if (!mLoading) {
            mLoading = true;

            String mRefURL;
            if (status.sDeveloperBuild) {
                mRefURL = CONST_GENESIS_BRIDGE_WEBSITES_DEV;
            } else {
                mRefURL = CONST_GENESIS_BRIDGE_WEBSITES;
            }

            StringRequest stringRequest = new StringRequest(Request.Method.GET, mRefURL,
                    response -> {
                        if (response.length() > 10) {
                            mBridges = response;
                            status.sBridgesDefault = response;
                            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_DEFAULT, mBridges));
                            String xx = status.sBridgesDefault = (String) dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.BRIDGE_DEFAULT, strings.BRIDGES_DEFAULT));
                            mLoading = false;
                        } else {
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

    private String onFetch(Context pContext) {
        try {
            return status.sBridgesDefault;
        } catch (Exception ignored) {
        }
        return strings.GENERIC_EMPTY_SPACE;
    }

    /* External Triggers */

    public Object onTrigger(dataEnums.eBridgeWebsiteCommands p_commands, List<Object> pData) {
        if (p_commands == dataEnums.eBridgeWebsiteCommands.M_LOAD) {
            onLoad((Context) pData.get(0));
        }
        if (p_commands == dataEnums.eBridgeWebsiteCommands.M_FETCH) {
            return onFetch((Context) pData.get(0));
        }

        return null;
    }

}
