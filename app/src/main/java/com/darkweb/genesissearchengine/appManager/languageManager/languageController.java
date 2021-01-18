package com.darkweb.genesissearchengine.appManager.languageManager;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.helpManager.helpController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import java.util.Arrays;
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
        activityContextManager.getInstance().onStack(this);
        mLanguageViewController.initialization(new languageViewCallback(),this);
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(helpController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public class languageViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {

            return null;
        }
    }

    public void changeLanguage(Locale language){
        status.sSettingLanguage = language.getLanguage();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE,language.getLanguage()));
        pluginController.getInstance().setLanguage();
    }

    /*-------------------------------------------------------CALLBACKS-------------------------------------------------------*/

    public void onClose(View view) {
        activityContextManager.getInstance().onRemoveStack(this);
        finish();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onMenuItemInvoked(View view) {
        int menuId = view.getId();
        if (menuId == R.id.langEnglish) {
            changeLanguage(Locale.ENGLISH);
        }
        else if (menuId == R.id.pLangGerman) {
            changeLanguage(new Locale("de"));
        }
        else if (menuId == R.id.pLangItalian) {
            changeLanguage(new Locale("it"));
        }
        else if (menuId == R.id.pLangPorteguse) {
            changeLanguage(new Locale("pt"));
        }
        else if (menuId == R.id.pLangRussian) {
            changeLanguage(new Locale("ru"));
        }
        else if (menuId == R.id.pLangUkarian) {
            changeLanguage(new Locale("uk"));
        }
        else if (menuId == R.id.pLangChinese) {
            changeLanguage(new Locale("zh"));
        }
        finish();
    }

}