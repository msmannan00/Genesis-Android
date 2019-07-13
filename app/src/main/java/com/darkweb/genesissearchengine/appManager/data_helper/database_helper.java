package com.darkweb.genesissearchengine.appManager.data_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.darkweb.genesissearchengine.appManager.list_activity.list_model;
import com.darkweb.genesissearchengine.appManager.list_activity.list_row_model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class database_helper extends SQLiteOpenHelper
{
    private Context        mContext;
    private String         mDbPath;
    private String         mDbName;
    private int            mDbVersion;

    public  SQLiteDatabase db;

    public database_helper(Context context, String dbName, int version) {
        super(context, dbName, null, version);
        mContext   = context;
        mDbPath    = context.getApplicationInfo().dataDir + "/databases/";
        mDbName    = dbName;
        mDbVersion = version;
    }

    public boolean exists() {
        SQLiteDatabase db = null;

        try {
            db = SQLiteDatabase.openDatabase(mDbPath + mDbName, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e) {
            //database does not exist yet.
        }

        if (db != null) {
            db.close();
            return true;
        } else {
            return false;
        }
    }

    public void openDatabase(int flag) throws SQLiteException, IOException {
        if (!exists()) {
            if (flag == SQLiteDatabase.OPEN_READONLY) {
                this.getReadableDatabase();
            } else if (flag == SQLiteDatabase.OPEN_READWRITE) {
                this.getWritableDatabase();
            }
            InputStream  iStream = null;
            OutputStream oStream = null;
            try {
                iStream = mContext.getAssets().open(mDbName);
                oStream = new FileOutputStream(mDbPath + mDbName);
                byte[]       buffer  = new byte[1024];
                int          length;

                while ((length = iStream.read(buffer)) > 0) {
                    oStream.write(buffer, 0, length);
                }
            } catch (IOException e) {
                throw e;
            } finally {
                if (iStream != null) {
                    iStream.close();
                }

                if (oStream != null) {
                    oStream.flush();
                    oStream.close();
                }
            }
        }

        try {
            if (flag == SQLiteDatabase.OPEN_READONLY) {
                db = SQLiteDatabase.openDatabase(mDbPath + mDbName, null,
                        SQLiteDatabase.OPEN_READONLY);
            } else if (flag == SQLiteDatabase.OPEN_READWRITE) {
                db = SQLiteDatabase.openDatabase(mDbPath + mDbName, null,
                        SQLiteDatabase.OPEN_READWRITE);
            }
        } catch (SQLiteException e) {
            throw e;
        }
    }

    @Override
    public synchronized void close() {
        if (db != null) {
            db.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void runQuery(String query)
    {
        db.execSQL(query);
    }

    public ArrayList<list_row_model> selectHistory()
    {
        ArrayList<list_row_model> tempmodel = new ArrayList<list_row_model>();
        Cursor c = db.rawQuery("SELECT * FROM history ", null);
        if (c.moveToFirst()){
            do {
                tempmodel.add(new list_row_model(c.getString(0),c.getString(1),Integer.parseInt(c.getString(0))));
            } while(c.moveToNext());
        }
        c.close();
        ///db.close();

        return  tempmodel;
    }
}
