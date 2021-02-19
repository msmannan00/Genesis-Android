package com.darkweb.genesissearchengine.dataManager;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import java.util.List;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_REFERENCE_WEBSITES;

public class referenceWebsiteDataModel {

    private String mReferenceWebsiteData = "null";
    private boolean mLoading = false;

    public referenceWebsiteDataModel(){
        mReferenceWebsiteData = status.mReferenceWebsites;
    }

    private void onLoad(Context pContext){
        if(!mLoading){
            mLoading = true;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, CONST_GENESIS_REFERENCE_WEBSITES,
                    response -> {
                        mReferenceWebsiteData = response;
                        mLoading = false;
                    },
                    error -> {});

            RequestQueue requestQueue = Volley.newRequestQueue(pContext);
            requestQueue.add(stringRequest);
        }
    }

    private String onFetch(){
        try {
            return mReferenceWebsiteData;
        }catch (Exception ignored){}
        return strings.GENERIC_EMPTY_SPACE;
    }

    public Object onTrigger(dataEnums.eReferenceWebsiteCommands p_commands, List<Object> pData){
        if(p_commands == dataEnums.eReferenceWebsiteCommands.M_LOAD){
            onLoad((Context) pData.get(0));
        }
        if(p_commands == dataEnums.eReferenceWebsiteCommands.M_FETCH){
            return onFetch();
        }

        return null;
    }

}
