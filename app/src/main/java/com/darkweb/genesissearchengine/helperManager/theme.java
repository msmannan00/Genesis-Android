package com.darkweb.genesissearchengine.helperManager;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;

public class theme {

    private boolean mLocalThemeChanged;
    private static theme ourInstance = new theme();
    int mode = -1;

    public static theme getInstance()
    {
        return ourInstance;
    }

    public void onConfigurationChanged(AppCompatActivity pContext){
        boolean sDefaultNightMode = (pContext.getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        if(status.sDefaultNightMode != sDefaultNightMode){
            setupThemeLocal(pContext, sDefaultNightMode);
            pContext.recreate();
        }
    }

    public void setupThemeLocal(Context context, boolean sDefaultNightMode) {
        Resources res = context.getResources();
        mode = res.getConfiguration().uiMode;

        if(status.sTheme == enums.Theme.THEME_DARK){
            if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                mode = Configuration.UI_MODE_NIGHT_YES;
                mLocalThemeChanged = true;
            }
        }else if(status.sTheme == enums.Theme.THEME_LIGHT){
            if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                mode = Configuration.UI_MODE_NIGHT_NO;
                mLocalThemeChanged = true;
            }
        }else {
            if(!sDefaultNightMode){
                if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    mode = Configuration.UI_MODE_NIGHT_NO;
                    mLocalThemeChanged = true;
                }
            }else {
                if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    mode = Configuration.UI_MODE_NIGHT_YES;
                    mLocalThemeChanged = true;
                }
            }
        }
    }

    public Context setupTheme(Context context) {

        Resources res = context.getResources();

        if(mode==-1){
            mode = res.getConfiguration().uiMode;

            if(status.sTheme == enums.Theme.THEME_DARK){
                if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    mode = Configuration.UI_MODE_NIGHT_YES;
                }
            }else if(status.sTheme == enums.Theme.THEME_LIGHT){
                if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    mode = Configuration.UI_MODE_NIGHT_NO;
                }
            }else {
                if(!status.sDefaultNightMode){
                    if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        mode = Configuration.UI_MODE_NIGHT_NO;
                    }
                }else {
                    if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        mode = Configuration.UI_MODE_NIGHT_YES;
                    }
                }
            }
        }

        Configuration config = new Configuration(res.getConfiguration());
        config.uiMode = mode;
        context = context.createConfigurationContext(config);
        return context;
    }

    public Context initTheme(Context pContext){
        boolean sDefaultNightMode = (pContext.getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        boolean sDefaultNightModeCurrent = (pContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        if(status.sSettingIsAppStarted){
            status.mThemeApplying = true;
        }

        status.sDefaultNightMode = sDefaultNightMode;
        pContext = setupTheme(pContext);

        mLocalThemeChanged = false;
        mode = -1;

        return pContext;
    }

}
