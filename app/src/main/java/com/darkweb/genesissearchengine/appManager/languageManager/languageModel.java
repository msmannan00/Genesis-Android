package com.darkweb.genesissearchengine.appManager.languageManager;

import com.darkweb.genesissearchengine.constants.status;

import java.util.ArrayList;
import java.util.List;

class languageModel
{
    private ArrayList<languageDataModel> mSupportedLanaguage;

    public languageModel(){
        mSupportedLanaguage = new ArrayList<>();
        onInitLanguage();
    }

    private void onInitLanguage(){
        mSupportedLanaguage.add(new languageDataModel("Follow Device Language","Default Language", "default","default"));
        mSupportedLanaguage.add(new languageDataModel("English (United States)","United States", "en","Us"));
        mSupportedLanaguage.add(new languageDataModel("Deutsche","German", "de","De"));
        mSupportedLanaguage.add(new languageDataModel("Català","Catalan", "ca","Es"));
        mSupportedLanaguage.add(new languageDataModel("中文（中国）","Chinese (China)", "zh","Cn"));
        mSupportedLanaguage.add(new languageDataModel("čeština","Czech", "ch","Cz"));
        mSupportedLanaguage.add(new languageDataModel("Dutch (Netherland)","Dutch (Netherland)", "nl","Nl"));
        mSupportedLanaguage.add(new languageDataModel("France (francaise)","French (France)", "fr","Fr"));
        mSupportedLanaguage.add(new languageDataModel("Ελληνικά","Greek", "el","Gr"));
        mSupportedLanaguage.add(new languageDataModel("Magyar","Hungarian", "hu","Hu"));
        mSupportedLanaguage.add(new languageDataModel("bahasa Indonesia","Indonesian", "in","Id"));
        mSupportedLanaguage.add(new languageDataModel("Italiana","Italian", "it","It"));
        mSupportedLanaguage.add(new languageDataModel("日本人","Japanese", "ja","Jp"));
        mSupportedLanaguage.add(new languageDataModel("韓国語","Korean", "ko","Kr"));
        mSupportedLanaguage.add(new languageDataModel("Português","Portuguese (Portugal)", "pt","Pt"));
        mSupportedLanaguage.add(new languageDataModel("Română","Romanian", "ro","Ro"));
        mSupportedLanaguage.add(new languageDataModel("Urdu (اردو)","Urdu", "ur","Ur"));
        mSupportedLanaguage.add(new languageDataModel("русский","Russian", "ru","Ru"));
        mSupportedLanaguage.add(new languageDataModel("ไทย","Thai", "th","Th"));
        mSupportedLanaguage.add(new languageDataModel("Türk","Turkish", "tr","Tr"));
        mSupportedLanaguage.add(new languageDataModel("عربى","Arabic", "ar","Ar"));
        mSupportedLanaguage.add(new languageDataModel("Український","Ukrainian", "uk","Ua"));
        mSupportedLanaguage.add(new languageDataModel("Tiếng Việt","Vietnamese", "vi","Vn"));
    }

    private int getActiveLanguageIndex(){
        for(int mCounter=0;mCounter<mSupportedLanaguage.size();mCounter++){
            if(mSupportedLanaguage.get(mCounter).getTag().equals(status.sSettingLanguage)){
                return mCounter;
            }
        }
        return -1;
    }

    public Object onTrigger(languageEnums.eLanguageModel pCommands, List<Object> pData){
        if(pCommands.equals(languageEnums.eLanguageModel.M_SUPPORTED_LANGUAGE)){
            return mSupportedLanaguage;
        }
        else if(pCommands.equals(languageEnums.eLanguageModel.M_ACTIVE_LANGUAGE)){
            return getActiveLanguageIndex();
        }
        return null;
    }
}
