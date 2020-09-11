package com.darkweb.genesissearchengine.appManager.languageManager;


import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("FieldCanBeLocal")
public class languageController extends AppCompatActivity {

    /*Initializations*/
    private languageViewController mLanguageViewController;
    private languageModel mLanguageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_view);

        initializeAppModel();
        initializeConnections();
    }

    public void initializeAppModel()
    {
        mLanguageViewController = new languageViewController();
        mLanguageModel = new languageModel();
    }

    public void initializeConnections()
    {
        mLanguageViewController.initialization(new languageViewCallback(),this);
    }

    public class languageViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> data, enums.etype e_type)
        {

        }
    }

    public void changeLanguage(Locale language){
        status.sLanguage = language.getLanguage();
        dataController.getInstance().setString(keys.LANGUAGE,language.getLanguage());
        pluginController.getInstance().setLanguage(this);
    }

    /*-------------------------------------------------------CALLBACKS-------------------------------------------------------*/

    public void onNavigationBackPressed(View view) {
        finish();
    }

    public void onMenuItemInvoked(View view) {
        int menuId = view.getId();
        if (menuId == R.id.langEnglish) {
            changeLanguage(Locale.ENGLISH);
        }
        else if (menuId == R.id.langGerman) {
            changeLanguage(new Locale("de"));
        }
        else if (menuId == R.id.langItalian) {
            changeLanguage(new Locale("it"));
        }
        else if (menuId == R.id.langPorteguse) {
            changeLanguage(new Locale("pt"));
        }
        else if (menuId == R.id.langRussian) {
            changeLanguage(new Locale("ru"));
        }
        else if (menuId == R.id.langUkarian) {
            changeLanguage(new Locale("uk"));
        }
        else if (menuId == R.id.langChinese) {
            changeLanguage(new Locale("zh"));
        }
        finish();
    }

}