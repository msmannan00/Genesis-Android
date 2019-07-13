package com.darkweb.genesissearchengine.appManager.list_activity;

public class list_row_model
{
    private int id;
    private String header;
    private String description;

    public list_row_model(String header, String description,int id) {
        this.id = id;
        this.header = header;
        this.description = description;
    }

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
