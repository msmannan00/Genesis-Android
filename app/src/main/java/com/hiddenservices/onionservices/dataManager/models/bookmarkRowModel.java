package com.hiddenservices.onionservices.dataManager.models;

import java.util.Calendar;
import java.util.Date;

public class bookmarkRowModel {
    /*Private Variables*/

    private int m_id;
    private String m_header;
    private String m_description;
    private Date m_date;

    /*Initializations*/

    public bookmarkRowModel(String p_header, String p_description, int p_id) {
        this.m_id = p_id;
        this.m_header = p_header;
        this.m_description = p_description;
        m_date = Calendar.getInstance().getTime();
    }

    /*Variable Setters*/

    public void setHeader(String p_header) {
        this.m_header = p_header;
    }

    public void setURL(String p_url) {
        this.m_description = p_url;
    }

    public void setDate(Date p_date) {
        m_date = p_date;
    }

    /*Variable Getters*/

    public String getHeader() {
        return m_header;
    }

    public String getDescription() {
        if (m_description != null && m_description.equals("167.86.99.31")) {
            return "orion.onion";
        }
        return m_description;
    }

    public int getID() {
        return m_id;
    }

    public Date getDate() {
        return m_date;
    }
}
