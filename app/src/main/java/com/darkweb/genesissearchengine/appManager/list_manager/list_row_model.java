package com.darkweb.genesissearchengine.appManager.list_manager;

public class list_row_model
{
    /*Private Variables*/

    private int id;
    private String header;
    private String description;

    /*Initializations*/

    public list_row_model(String header, String description,int id) {
        this.id = id;
        this.header = header;
        this.description = description;
    }

    /*Variable Getters*/

    public String getHeader() {
        return header;
    }
    public String getDescription() {
        return description;
    }
    public int getId() {
        return id;
    }
}
