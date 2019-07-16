package com.darkweb.genesissearchengine.appManager.setting_manager;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;

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

    public void init_status()
    {
        setting_model.getInstance().search_status = status.search_status;
        setting_model.getInstance().history_status = status.history_status;
        setting_model.getInstance().java_status = status.java_status;
    }

    /*Changed Status*/
    public String search_status = strings.emptyStr;
    public boolean java_status = false;
    public boolean history_status = true;

}
