package com.darkweb.genesissearchengine.appManager.main_activity;

import android.content.Context;
import android.net.Uri;
import com.darkweb.genesissearchengine.appManager.data_helper.database_controller;
import com.darkweb.genesissearchengine.appManager.list_activity.list_controller;
import com.darkweb.genesissearchengine.appManager.list_activity.list_model;
import com.darkweb.genesissearchengine.appManager.list_activity.list_row_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperMethod;

import java.text.SimpleDateFormat;
import java.util.*;

public class app_model
{
    /*Data Objects*/
    private ArrayList<list_row_model> history = new ArrayList<list_row_model>();
    private ArrayList<list_row_model> bookmarks = new ArrayList<list_row_model>();
    private HashSet<String> suggestions = new HashSet<String>();
    private static int port = 9150;

    private Context appContext;
    private application_controller appInstance;

    private Context listContext;
    private list_controller listInstance;

    /*Initializations*/
    public void initialization()
    {
        database_controller.getInstance().initialize();
        initializeHistory();
        initializeBookmarks();
    }

    /*Setter Getter Initializations*/
    private static final app_model ourInstance = new app_model();
    public static app_model getInstance()
    {
        return ourInstance;
    }


    /*Getters Setters*/
    public int getPort()
    {
        return port;
    }
    public void setPort(int port)
    {
        this.port = port;
    }


    public void setAppContext(Context appContext)
    {
        this.appContext = appContext;
    }
    public Context getAppContext()
    {
        return appContext;
    }


    public application_controller getAppInstance()
    {
        return appInstance;
    }
    public void setAppInstance(application_controller appInstance)
    {
        this.appInstance = appInstance;
    }


    public void initializeHistory(){
        if(!status.history_status)
        {
            history = database_controller.getInstance().selectHistory();
        }
        else
        {
            database_controller.getInstance().execSQL("delete from history where 1");
        }
        app_model.getInstance().getAppInstance().reInitializeSuggestion();
    }
    public void addHistory(String url) {

        if(history.size()> constants.max_history_size)
        {
            database_controller.getInstance().execSQL("delete from history where id="+history.get(history.size()-1).getId());
            history.remove(history.size()-1);
        }

        int autoval = 0;
        if(history.size()>0)
        {
            autoval = history.get(0).getId()+1;
        }

        addSuggestions(url);
        SimpleDateFormat d_form = new SimpleDateFormat("yyyy-MM-dd | HH-mm-ss");
        String date = d_form.format(new Date());
        database_controller.getInstance().execSQL("INSERT INTO history(id,date,url) VALUES("+autoval+",'"+date+"','"+url+"');");
        history.add(0,new list_row_model(url,date,autoval));
    }
    public ArrayList<list_row_model> getHistory() {
        return history;
    }


    public void initializeBookmarks(){
        bookmarks = database_controller.getInstance().selectBookmark();
    }
    public void addBookmark(String url,String title){
        int autoval = 0;
        if(bookmarks.size()> constants.max_bookmark_size)
        {
            database_controller.getInstance().execSQL("delete from bookmark where id="+bookmarks.get(bookmarks.size()-1).getId());
            bookmarks.remove(history.size()-1);
        }

        if(bookmarks.size()>0)
        {
            autoval = bookmarks.get(0).getId()+1;
        }

        if(title.equals(""))
        {
            title = "New_Bookmark"+autoval;
        }
        database_controller.getInstance().execSQL("INSERT INTO bookmark(id,title,url) VALUES("+autoval+",'"+title+"','"+url+"');");
        bookmarks.add(0,new list_row_model(url,title,autoval));
    }
    public ArrayList<list_row_model> getBookmark(){
        return bookmarks;
    }


    public void initSuggestions(String url) {
        suggestions.add(url);
    }
    public void addSuggestions(String url) {
        if(url.contains("boogle.store"))
        {
            Uri uri = Uri.parse(url);
            String actual_url = uri.getQueryParameter("q");
            suggestions.add(actual_url);
        }
        suggestions.add(url);
        app_model.getInstance().getAppInstance().reInitializeSuggestion();
    }
    public ArrayList<String> getSuggestions() {
        return new ArrayList<String>(suggestions);
    }


    public void setListInstance(list_controller listInstance){
        this.listInstance = listInstance;
    }
    public list_controller getListInstance(){
        return listInstance;
    }


    public void setListContext(Context listContext) {
        this.listContext = listContext;
    }
    public Context getListContext(){
        return listContext;
    }

}
