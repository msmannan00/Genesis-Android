package com.darkweb.genesissearchengine.appManager.landingManager;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.darkweb.genesissearchengine.appManager.landingManager.langingPageManager.landingPageController;
import com.darkweb.genesissearchengine.appManager.landingManager.langingPageManager.landingPageDataModel;
import com.darkweb.genesissearchengine.appManager.orbotManager.orbotController;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.github.paolorotolo.appintro.AppIntro;

import java.util.Collections;
import java.util.List;

import static com.darkweb.genesissearchengine.appManager.landingManager.landingEnums.eLandingPageControllerCallbackCommands.M_ON_LANDING_PAGE_FINISH_TRIGGERED;

public class landingController extends AppIntro {

    /* Private Variables */

    private landingViewController mLandingViewController;
    private landingModelController mLandingModel;

    /* UI Variables */

    private landingPageController mIntro_welcome;
    private landingPageController mIntro_intro;
    private landingPageController mIntro_start;

    /* Initializations */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);

        initializePages();
        initializeModels();
    }

    private void initializePages(){
        mIntro_welcome = new landingPageController();
        mIntro_intro = new landingPageController();
        mIntro_start = new landingPageController();

        mIntro_welcome.onInitialize(new landingPageCallback(), new landingPageDataModel(getString(R.string.LANDING_HELLO), getString(R.string.LANDING_WELCOME),null, enums.LandingPageTypes.M_LANDING_WELCOME));
        mIntro_intro.onInitialize(new landingPageCallback(), new landingPageDataModel(getString(R.string.LANDING_BROWSE_INFO), getString(R.string.LANDING_NO_TRACKING),null, enums.LandingPageTypes.M_LANDING_INTRO));
        mIntro_start.onInitialize(new landingPageCallback(), new landingPageDataModel(getString(R.string.LANDING_BRIDGES_INFO),null,getString(R.string.LANDING_MORE), enums.LandingPageTypes.M_LANDING_START));
    }

    private void initializeModels(){
        mLandingViewController = new landingViewController(this,new landingViewCallback(), mIntro_welcome, mIntro_intro, mIntro_start);
        mLandingModel = new landingModelController(this, new landingModelCallback());
    }

    /* Local Override */

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        mLandingModel.onTrigger(landingEnums.eLandingModelCommands.M_UPDATE_LANDING_PAGE_SHOWN_STATUS);
        onCloseTrigger();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /* Helper Methods */

    public void onCloseTrigger(){
        finish();
    }

    /* Callbacks */

    private class landingViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> pData, Object pType)
        {
            return null;
        }
    }

    private class landingModelCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> pData, Object pType)
        {
            return null;
        }
    }

    private class landingPageCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> pData, Object pType)
        {
            if(pType.equals(M_ON_LANDING_PAGE_FINISH_TRIGGERED)){
                if((int)pData.get(0) == enums.LandingPageTypes.M_LANDING_START){
                    startActivity(new Intent(landingController.this, orbotController.class));
                }
            }
            return null;
        }
    }

}