package com.darkweb.genesissearchengine.appManager.landingManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.darkweb.genesissearchengine.appManager.bridgeManager.bridgeController;
import com.example.myapplication.R;
import com.github.paolorotolo.appintro.AppIntro;

public class landingController extends AppIntro {

    private landingViewController mLauncherViewController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        CustomSlideBigText welcome = CustomSlideBigText.newInstance(R.layout.custom_slide_big_text);
        welcome.setTitle(getString(R.string.hello));
        welcome.setSubTitle(getString(R.string.welcome));
        addSlide(welcome);

        CustomSlideBigText intro2 = CustomSlideBigText.newInstance(R.layout.custom_slide_big_text);
        intro2.setTitle(getString(R.string.browser_the_internet));
        intro2.setSubTitle(getString(R.string.no_tracking));
        addSlide(intro2);

        CustomSlideBigText cs2 = CustomSlideBigText.newInstance(R.layout.custom_slide_big_text);
        cs2.setTitle(getString(R.string.bridges_sometimes));
        cs2.showButton(getString(R.string.action_more), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(landingController.this,bridgeController.class));
            }
        });
        addSlide(cs2);


        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(getResources().getColor(R.color.landing_ease_blue));
        setSeparatorColor(getResources().getColor(R.color.panel_background_main));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);
        initConnections();
    }

    private void initConnections(){
        mLauncherViewController = new landingViewController(this,null);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

}