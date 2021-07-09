package com.darkweb.genesissearchengine.appManager.landingManager;

import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.appManager.landingManager.langingPageManager.landingPageController;
import com.darkweb.genesissearchengine.eventObserver;
import com.example.myapplication.R;
import com.github.paolorotolo.appintro.AppIntro;

class landingViewController
{
    /*Private Variables*/

    private AppIntro mContext;
    private eventObserver.eventListener mEvent;

    /*UI Variables*/

    private landingPageController mIntroWelcome;
    private landingPageController mIntroIntro;
    private landingPageController mIntroStart;

    /*Initializations*/

    landingViewController(AppIntro pContext, eventObserver.eventListener pEvent, landingPageController pIntroWelcome, landingPageController pIntroIntro, landingPageController pIntroStart){
        this.mContext = pContext;
        this.mEvent = pEvent;
        this.mIntroWelcome = pIntroWelcome;
        this.mIntroIntro = pIntroIntro;
        this.mIntroStart = pIntroStart;

        setUIData();
        initPostUI();
    }

    private void setUIData(){
        mContext.addSlide(mIntroWelcome);
        mContext.addSlide(mIntroIntro);
        mContext.addSlide(mIntroStart);

        mContext.setBarColor(mContext.getResources().getColor(R.color.landing_ease_blue));
        mContext.setSeparatorColor(mContext.getResources().getColor(R.color.headerblack));

        mContext.showSkipButton(false);
        mContext.setProgressButtonEnabled(true);
    }

    private void initPostUI(){
        mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
    }

    public Object onTrigger(landingEnums.eLandingViewCommands pCommands){
        return null;
    }
}
