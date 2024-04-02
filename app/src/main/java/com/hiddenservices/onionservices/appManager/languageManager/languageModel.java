package com.hiddenservices.onionservices.appManager.languageManager;

import com.hiddenservices.onionservices.constants.status;

import java.util.ArrayList;
import java.util.List;

class languageModel {
    private ArrayList<languageDataModel> mSupportedLanguage;

    public languageModel() {
        mSupportedLanguage = new ArrayList<>();
    }

    protected void onInit(){
        onInitLanguage();
    }

    private void onInitLanguage() {
        mSupportedLanguage.add(new languageDataModel("Follow Device Language", "Default Language", "default", "default"));
        mSupportedLanguage.add(new languageDataModel("English (United States)", "United States", "en", "Us"));
        mSupportedLanguage.add(new languageDataModel("Deutsche", "German", "de", "De"));
        mSupportedLanguage.add(new languageDataModel("Català", "Catalan", "ca", "Es"));
        mSupportedLanguage.add(new languageDataModel("中文（中国）", "Chinese (China)", "zh", "Cn"));
        mSupportedLanguage.add(new languageDataModel("čeština", "Czech", "ch", "Cz"));
        mSupportedLanguage.add(new languageDataModel("Dutch (Netherland)", "Dutch (Netherland)", "nl", "Nl"));
        mSupportedLanguage.add(new languageDataModel("France (francaise)", "French (France)", "fr", "Fr"));
        mSupportedLanguage.add(new languageDataModel("Ελληνικά", "Greek", "el", "Gr"));
        mSupportedLanguage.add(new languageDataModel("Magyar", "Hungarian", "hu", "Hu"));
        mSupportedLanguage.add(new languageDataModel("bahasa Indonesia", "Indonesian", "in", "Id"));
        mSupportedLanguage.add(new languageDataModel("Italiana", "Italian", "it", "It"));
        mSupportedLanguage.add(new languageDataModel("日本人", "Japanese", "ja", "Jp"));
        mSupportedLanguage.add(new languageDataModel("韓国語", "Korean", "ko", "Kr"));
        mSupportedLanguage.add(new languageDataModel("Português", "Portuguese (Portugal)", "pt", "Pt"));
        mSupportedLanguage.add(new languageDataModel("Română", "Romanian", "ro", "Ro"));
        mSupportedLanguage.add(new languageDataModel("русский", "Russian", "ru", "Ru"));
        mSupportedLanguage.add(new languageDataModel("ไทย", "Thai", "th", "Th"));
        mSupportedLanguage.add(new languageDataModel("Türk", "Turkish", "tr", "Tr"));
        mSupportedLanguage.add(new languageDataModel("عربى", "Arabic", "ar", "Ar"));
        mSupportedLanguage.add(new languageDataModel("Український", "Ukrainian", "uk", "Ua"));
        mSupportedLanguage.add(new languageDataModel("Tiếng Việt", "Vietnamese", "vi", "Vn"));
    }

    private int getActiveLanguageIndex() {
        for (int mCounter = 0; mCounter < mSupportedLanguage.size(); mCounter++) {
            if (mSupportedLanguage.get(mCounter).getTag().equals(status.sSettingLanguage)) {
                return mCounter;
            }
        }
        return -1;
    }

    public Object onTrigger(languageEnums.eLanguageModel pCommands, List<Object> ignoredPData) {
        if (pCommands.equals(languageEnums.eLanguageModel.M_SUPPORTED_LANGUAGE)) {
            return mSupportedLanguage;
        } else if (pCommands.equals(languageEnums.eLanguageModel.M_ACTIVE_LANGUAGE)) {
            return getActiveLanguageIndex();
        }
        return null;
    }
}
