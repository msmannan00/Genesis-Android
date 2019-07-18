package com.darkweb.genesissearchengine.appManager.home_activity;

import com.darkweb.genesissearchengine.constants.enums;

public class navigation_model
{
    private String url;
    private enums.navigationType type;

    /*Initializations*/

    public navigation_model(String url, enums.navigationType type) {
        this.url = url;
        this.type = type;
    }

    /*Variable Getters*/

    public String getURL() {
        return url;
    }
    public enums.navigationType type() {
        return type;
    }

}
