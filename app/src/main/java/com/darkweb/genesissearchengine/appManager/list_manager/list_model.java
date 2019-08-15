package com.darkweb.genesissearchengine.appManager.list_manager;

import android.content.Context;
import com.darkweb.genesissearchengine.appManager.database_manager.database_controller;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.strings;

import java.util.ArrayList;

public class list_model
{
    /*Private Variables*/

    private ArrayList<list_row_model> list_model = new ArrayList<>();
    private ArrayList<Integer> list_index;
    private ArrayList<list_row_model> main_model_ref = new ArrayList<>();
    private list_controller listInstance;
    private Context listContext;
    private int listType;

    /*Initializations*/

    private static final list_model ourInstance = new list_model();
    public static list_model getInstance()
    {
        return ourInstance;
    }
    private list_model(){
        list_index = new ArrayList<>();
    }

    /*Helper Methods*/

    void setType(int listType)
    {
        this.listType = listType;
    }
    int getType()
    {
        return listType;
    }

    void setModel(ArrayList<list_row_model> list_model)
    {
        this.list_model = list_model;
    }
    ArrayList<list_row_model> getModel()
    {
        return list_model;
    }

    void setIndex(int index){
        list_index.add(index);
    }
    void removeIndex(int index){
        list_index.remove(index);
        for(int counter=index;counter<=list_index.size()-1;counter++)
        {
            list_index.set(counter,list_index.get(counter)-1);
        }
    }
    ArrayList<Integer> getIndex(){
        return list_index;
    }

    void setMainList(ArrayList<list_row_model> model)
    {
        main_model_ref = model;
    }
    ArrayList<list_row_model> getMainList()
    {
        return main_model_ref;
    }
    void removeFromMainList(int index)
    {
        main_model_ref.remove(index);
    }

    public list_controller getListInstance()
    {
        return listInstance;
    }
    void setListInstance(list_controller listInstance)
    {
        this.listInstance = listInstance;
    }

    public Context getListContext(){
        return listContext;
    }
    void setListContext(Context listContext)
    {
        this.listContext = listContext;
    }

    /*Database Handler*/

    void deleteFromDatabase(int index) {
        String table = strings.bookmark_text;
        if(getType()== constants.list_history)
        {
            table = strings.history_text;
        }
        database_controller.getInstance().execSQL("delete from "+table+" where id="+index,null);
    }


}