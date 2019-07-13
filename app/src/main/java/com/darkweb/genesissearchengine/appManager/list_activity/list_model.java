package com.darkweb.genesissearchengine.appManager.list_activity;

import java.util.ArrayList;

public class list_model
{
    private static final list_model ourInstance = new list_model();

    public static list_model getInstance()
    {
        return ourInstance;
    }

    private list_model()
    {
    }

    public ArrayList<list_row_model> list_model = new ArrayList<list_row_model>();
    public ArrayList<Integer> list_index = new ArrayList<Integer>();
    private ArrayList<list_row_model> main_model_ref = new ArrayList<list_row_model>();

    public int listType;

    public void setType(int listType)
    {
        this.listType = listType;
    }

    public int getType()
    {
        return listType;
    }

    public void setModel(ArrayList<list_row_model> list_model)
    {
        this.list_model = list_model;
    }

    public ArrayList<list_row_model> getModel()
    {
        return list_model;
    }

    public void setIndex(int index)
    {
        list_index.add(index);
    }

    public ArrayList<Integer> getIndex()
    {
        return list_index;
    }

    public void removeIndex(int index)
    {
        list_index.remove(index);
        for(int counter=index;counter<=list_index.size()-1;counter++)
        {
            list_index.set(counter,list_index.get(counter)-1);
        }
    }

    public void setMainList(ArrayList<list_row_model> model)
    {
        main_model_ref = model;
    }

    public ArrayList<list_row_model> getMainList()
    {
        return main_model_ref;
    }

    public void removeFromMainList(int index)
    {
        main_model_ref.remove(index);
    }

}