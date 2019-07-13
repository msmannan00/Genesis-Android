package com.darkweb.genesissearchengine.appManager.setting_manager;

public class setting_model
{
    private static final setting_model ourInstance = new setting_model();
    private setting_model(){
    }
    public static setting_model getInstance()
    {
        return ourInstance;
    }

    private setting_controller settingInstance;

    setting_controller getSettingInstance()
    {
        return settingInstance;
    }
    void setSettingInstance(setting_controller settingInstance)
    {
        this.settingInstance = settingInstance;
    }
}
