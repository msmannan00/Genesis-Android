package com.darkweb.genesissearchengine.appManager.database_manager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.darkweb.genesissearchengine.appManager.home_activity.home_model;
import com.darkweb.genesissearchengine.appManager.list_manager.list_row_model;
import com.darkweb.genesissearchengine.constants.constants;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class database_controller
{

    /*Private Variables*/

    private static final database_controller ourInstance = new database_controller();
    private SQLiteDatabase database_instance;

    public static database_controller getInstance()
    {
        return ourInstance;
    }

    private database_controller()
    {
    }

    /*Initializations*/

    public void initialize()
    {
        try
        {
            database_instance = home_model.getInstance().getHomeInstance().openOrCreateDatabase(constants.databae_name, MODE_PRIVATE, null);
            database_instance.execSQL("CREATE TABLE IF NOT EXISTS " + "history" + " (id INT(4),date VARCHAR,url VARCHAR);");
            database_instance.execSQL("CREATE TABLE IF NOT EXISTS " + "bookmark" + " (id INT(4),title VARCHAR,url VARCHAR);");

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    /*Helper Methods*/

    public void execSQL(String query)
    {
        database_instance.execSQL(query);
    }

    public ArrayList<list_row_model> selectHistory(){
        ArrayList<list_row_model> tempmodel = new ArrayList<>();
        Cursor c = database_instance.rawQuery("SELECT * FROM history ORDER BY id DESC ", null);
        if (c.moveToFirst()){
            do {
                tempmodel.add(new list_row_model(c.getString(2), c.getString(1),Integer.parseInt(c.getString(0))));
                home_model.getInstance().initSuggestions(c.getString(2));
            } while(c.moveToNext());
        }
        c.close();

        return  tempmodel;
    }

    public ArrayList<list_row_model> selectBookmark(){
        ArrayList<list_row_model> tempmodel = new ArrayList<>();
        Cursor c = database_instance.rawQuery("SELECT * FROM bookmark ORDER BY id DESC ", null);

        if (c.moveToFirst()){
            do {
                tempmodel.add(new list_row_model(c.getString(2), c.getString(1),Integer.parseInt(c.getString(0))));
            } while(c.moveToNext());
        }
        c.close();

        return  tempmodel;
    }

}
