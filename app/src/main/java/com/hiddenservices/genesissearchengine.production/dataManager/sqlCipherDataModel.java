package com.hiddenservices.genesissearchengine.production.dataManager;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.genesissearchengine.production.appManager.activityContextManager;
import com.hiddenservices.genesissearchengine.production.dataManager.models.bookmarkRowModel;
import com.hiddenservices.genesissearchengine.production.dataManager.models.historyRowModel;
import com.hiddenservices.genesissearchengine.production.appManager.homeManager.geckoManager.geckoSession;
import com.hiddenservices.genesissearchengine.production.dataManager.models.tabRowModel;
import com.hiddenservices.genesissearchengine.production.constants.constants;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteDatabase;
import org.mozilla.geckoview.GeckoSession;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.hiddenservices.genesissearchengine.production.constants.constants.CONST_DATABASE_NAME;


public class sqlCipherDataModel
{

    /*Private Variables*/

    private static SQLiteDatabase sDatabaseInstance;

    /*Initializations*/

    private void prepareDatabaseEnvironment(AppCompatActivity app_context) {
        File databaseFile = app_context.getDatabasePath(CONST_DATABASE_NAME + "_SECURE");

        if (!databaseFile.exists()) {
            Objects.requireNonNull(databaseFile.getParentFile()).mkdirs();
        }
    }

    private void initialize(AppCompatActivity app_context)
    {
        try
        {
            SQLiteDatabase.loadLibs(app_context);
            prepareDatabaseEnvironment(app_context);
            sDatabaseInstance = SQLiteDatabase.openOrCreateDatabase(app_context.getDatabasePath(CONST_DATABASE_NAME + "_SECURE_V1"), constants.CONST_ENCRYPTION_KEY_DATABASE,null, wrapHook(null));

            sDatabaseInstance.execSQL("CREATE TABLE IF NOT EXISTS " + "history" + " (id  INT(4) PRIMARY KEY,date DATETIME,url VARCHAR,title VARCHAR);");
            sDatabaseInstance.execSQL("CREATE TABLE IF NOT EXISTS " + "bookmark" + " (id INT(4) PRIMARY KEY,title VARCHAR,url VARCHAR);");
            sDatabaseInstance.execSQL("CREATE TABLE IF NOT EXISTS " + "tab" + " (mid INT(4) PRIMARY KEY,date,title VARCHAR,url VARCHAR,mThumbnail BLOB, theme VARCHAR, session VARCHAR);");

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    /*Helper Methods*/

    private SQLiteDatabaseHook wrapHook(final SQLiteDatabaseHook hook) {
        if (hook == null)
        {
            return keyHook;
        }
        return new SQLiteDatabaseHook() {
            @Override
            public void preKey(net.sqlcipher.database.SQLiteDatabase sqLiteDatabase) {
                keyHook.preKey(sqLiteDatabase);
                hook.preKey(sqLiteDatabase);
            }

            @Override
            public void postKey(net.sqlcipher.database.SQLiteDatabase sqLiteDatabase) {
                keyHook.postKey(sqLiteDatabase);
                hook.preKey(sqLiteDatabase);
            }
        };
    }

    SQLiteDatabaseHook keyHook = new SQLiteDatabaseHook() {
        @Override
        public void preKey(net.sqlcipher.database.SQLiteDatabase sqLiteDatabase) {

        }

        @Override
        public void postKey(net.sqlcipher.database.SQLiteDatabase sqLiteDatabase) {

        }
    };

    private void execSQL(String query,Object params,boolean pContentValues)
    {
        try {
            if(params==null)
            {
                sDatabaseInstance.execSQL(query);
            }
            else
            {
                sDatabaseInstance.execSQL(query,(String[])params);
            }
        }catch (Exception ex){
            Log.i("Memory Error", "Memory Full");
        }
    }

    private void execSQL(String query,Object params,boolean pContentValues,String whereClause, String[] whereArgs)
    {
        try {
            if(params==null)
            {
                sDatabaseInstance.execSQL(query);
            }
            else if(pContentValues){
                sDatabaseInstance.update(query, (ContentValues)params, whereClause, whereArgs);
            }
            else
            {
                sDatabaseInstance.execSQL(query,(String[])params);
            }
        }catch (Exception ex){
            Log.i("Memory Error", "Memory Full");
        }
    }

    private ArrayList<historyRowModel> selectHistory(int pStartIndex,int pEndIndex){
        ArrayList<historyRowModel> tempmodel = new ArrayList<>();

        Cursor c = sDatabaseInstance.rawQuery("SELECT * FROM history ORDER BY date DESC LIMIT " + pEndIndex + " OFFSET "+pStartIndex, null);
        if (c.moveToFirst()){
            do {
                historyRowModel model = new historyRowModel(c.getString(3), c.getString(2),Integer.parseInt(c.getString(0)));
                try {
                    Date m_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).parse(c.getString(1));
                    model.setDate(m_date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(Calendar.getInstance().getTime().getTime() < model.getDate().getTime()){
                    tempmodel.add(model);
                }else {
                    tempmodel.add(model);
                }
            } while(c.moveToNext());
        }
        c.close();

        return  tempmodel;
    }

    private ArrayList<tabRowModel> selectTabs(){
        ArrayList<tabRowModel> mTempListModel = new ArrayList<>();

        Cursor c = sDatabaseInstance.rawQuery("SELECT * FROM tab ORDER BY date ASC", null);
        if (c.moveToFirst()){
            do {
                geckoSession mSession =  activityContextManager.getInstance().getHomeController().onNewTabInit();
                tabRowModel model = new tabRowModel(c.getString(0), c.getString(1),c.getBlob(4));
                GeckoSession.SessionState session = null;
                try {
                        session = GeckoSession.SessionState.fromString(c.getString(6));
                        model.setSession(mSession, c.getString(3),c.getString(2), c.getString(5), session);
                        model.getSession().setSessionID(model.getmId());
                        if(session != null){
                            mTempListModel.add(0, model);
                        }else {
                            mTempListModel.add(model);
                        }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } while(c.moveToNext());
        }
        c.close();

        return  mTempListModel;
    }

    private int getLargestHistoryID(){
        int id = 0;
        Cursor c = sDatabaseInstance.rawQuery("SELECT max(id) FROM history", null);

        if (c.moveToFirst()){
            do {
                if(c.getString(0)==null){
                    break;
                }else {
                    id = Integer.parseInt(c.getString(0));
                }
            } while(c.moveToNext());
        }
        c.close();

        return  id;
    }

    private ArrayList<bookmarkRowModel> selectBookmark(){
        ArrayList<bookmarkRowModel> tempmodel = new ArrayList<>();
        Cursor c = sDatabaseInstance.rawQuery("SELECT * FROM bookmark ORDER BY id DESC ", null);

        if (c.moveToFirst()){
            do {
                tempmodel.add(new bookmarkRowModel(c.getString(1), c.getString(2),Integer.parseInt(c.getString(0))));
            } while(c.moveToNext());
        }
        c.close();

        return  tempmodel;
    }

    private void deleteFromList(int index,String table) {
        execSQL("delete from "+table+" where id="+index,null, false);
    }

    /* External Triggers */

    public Object onTrigger(dataEnums.eSqlCipherCommands pCommands, List<Object> pData) {
        if(pCommands == dataEnums.eSqlCipherCommands.M_INIT){
            initialize((AppCompatActivity)pData.get(0));
        }
        else if(pCommands == dataEnums.eSqlCipherCommands.M_EXEC_SQL){
            execSQL((String)pData.get(0), pData.get(1), false);
        }
        else if(pCommands == dataEnums.eSqlCipherCommands.M_EXEC_SQL_USING_CONTENT){
            execSQL((String)pData.get(0), pData.get(1), true, (String)pData.get(2), (String[])pData.get(3));
        }
        else if(pCommands == dataEnums.eSqlCipherCommands.M_SELECT_BOOKMARK){
            return selectBookmark();
        }
        else if(pCommands == dataEnums.eSqlCipherCommands.M_SELECT_HISTORY){
            return selectHistory((int)pData.get(0), (int)pData.get(1));
        }
        else if(pCommands == dataEnums.eSqlCipherCommands.M_SELECT_TABS){
            return selectTabs();
        }
        else if(pCommands == dataEnums.eSqlCipherCommands.M_HISTORY_ID){
            return getLargestHistoryID();
        }
        else if(pCommands == dataEnums.eSqlCipherCommands.M_DELETE_FROM_HISTORY){
            deleteFromList((int)pData.get(0), (String)pData.get(1));
        }

        return null;
    }

}
