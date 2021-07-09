package com.darkweb.genesissearchengine.appManager.landingManager.langingPageManager;

import android.view.View;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.eventObserver;
import com.example.myapplication.R;

public class landingPageViewController {

    private Fragment mContext;
    private TextView mHeader;
    private TextView mSubHeader;
    private TextView mSubmit;
    private landingPageDataModel mLandingModel;

    public landingPageViewController(Fragment pContext, eventObserver.eventListener pEvent, TextView pHeader, TextView pSubHeader, TextView pSubmit, landingPageDataModel pLandingModel){
        this.mHeader = pHeader;
        this.mSubHeader = pSubHeader;
        this.mSubmit = pSubmit;
        this.mContext = pContext;
        this.mLandingModel = pLandingModel;

        onInitializeView();
    }

    private void onInitializeView(){
        if(this.mLandingModel.getPageType() == enums.LandingPageTypes.M_LANDING_WELCOME){
            mHeader.setText(mLandingModel.getHeader());
            mSubHeader.setText(mLandingModel.getSubHeader());
            mSubHeader.setVisibility(View.VISIBLE);
        }
        else if(this.mLandingModel.getPageType() == enums.LandingPageTypes.M_LANDING_INTRO){
            mHeader.setText(mLandingModel.getHeader());
            mSubHeader.setText(mLandingModel.getSubHeader());
            mSubHeader.setVisibility(View.VISIBLE);
        }
        else if(this.mLandingModel.getPageType() == enums.LandingPageTypes.M_LANDING_START){
            mHeader.setText(mLandingModel.getHeader());
            mSubHeader.setText(mLandingModel.getSubHeader());
            mSubmit.setText(mLandingModel.getNextButtonText());
            mSubHeader.setVisibility(View.VISIBLE);
            mSubmit.setVisibility(View.VISIBLE);
        }
    }
}
