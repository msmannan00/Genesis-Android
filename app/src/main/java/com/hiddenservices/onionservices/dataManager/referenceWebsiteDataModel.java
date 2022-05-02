package com.hiddenservices.onionservices.dataManager;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;

import java.util.Arrays;
import java.util.List;

import static com.hiddenservices.onionservices.constants.constants.*;

public class referenceWebsiteDataModel {

    /* Local Variables */

    private boolean mLoading = false;

    /* Initializations */

    public referenceWebsiteDataModel() {
    }

    /* Helper Methods */

    private void onLoad(Context pContext) {
        if (!mLoading) {
            mLoading = true;

            String mRefURL;
            if (status.sDeveloperBuild) {
                mRefURL = CONST_GENESIS_REFERENCE_WEBSITES_DEV;
            } else {
                mRefURL = CONST_GENESIS_REFERENCE_WEBSITES;
            }

            StringRequest stringRequest = new StringRequest(Request.Method.GET, mRefURL,
                    response -> {
                        if (response.length() > 10) {
                            status.sReferenceWebsites = response;
                            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.HOME_REFERENCE_WEBSITES, status.sReferenceWebsites));
                            mLoading = false;
                        }
                    },
                    error -> {
                        mLoading = false;
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(pContext);
            requestQueue.add(stringRequest);
        }
    }

    private String onFetch() {
        return status.sReferenceWebsites;
    }

    /* External Triggers */

    public Object onTrigger(dataEnums.eReferenceWebsiteCommands p_commands, List<Object> pData) {
        if (p_commands == dataEnums.eReferenceWebsiteCommands.M_LOAD) {
            onLoad((Context) pData.get(0));
        }
        if (p_commands == dataEnums.eReferenceWebsiteCommands.M_FETCH) {
            return onFetch();
        }

        return null;
    }

}
