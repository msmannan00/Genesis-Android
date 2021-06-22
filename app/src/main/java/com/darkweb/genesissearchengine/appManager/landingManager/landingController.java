package com.darkweb.genesissearchengine.appManager.landingManager;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.darkweb.genesissearchengine.appManager.orbotManager.orbotController;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.github.paolorotolo.appintro.AppIntro;

import java.util.Arrays;
import java.util.Collections;

public class landingController extends AppIntro {

    private landingViewController mLandingViewController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomSlideBigText welcome = CustomSlideBigText.newInstance(R.layout.landing_view);
        welcome.setTitle(getString(R.string.LANDING_HELLO));
        welcome.setSubTitle(getString(R.string.LANDING_WELCOME));
        addSlide(welcome);

        CustomSlideBigText intro2 = CustomSlideBigText.newInstance(R.layout.landing_view);
        intro2.setTitle(getString(R.string.LANDING_BROWSE_INFO));
        intro2.setSubTitle(getString(R.string.LANDING_NO_TRACKING));
        addSlide(intro2);

        CustomSlideBigText cs2 = CustomSlideBigText.newInstance(R.layout.landing_view);
        cs2.setTitle(getString(R.string.LANDING_BRIDGES_INFO));
        cs2.showButton(getString(R.string.LANDING_MORE), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(landingController.this, orbotController.class));
            }
        });
        addSlide(cs2);

        setBarColor(getResources().getColor(R.color.landing_ease_blue));
        setSeparatorColor(getResources().getColor(R.color.headerblack));

        showSkipButton(false);
        setProgressButtonEnabled(true);
        initConnections();
    }

    private void initConnections(){
        mLandingViewController = new landingViewController(this,null);
    }



    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        status.sSettingFirstStart = false;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FIRST_INSTALLED,false));
        finish();
    }

    @Override
    protected void onResume() {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        super.onResume();
    }
}