package com.example.aravind.group31;


/*
createHistTable - creates the hist table in DB to keep track of latest updated table
createAccDataTable - creates table with given name to store Acceleration data
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.util.Log;

import com.example.aravind.group31.properties.Constants;

public class DBModule extends SQLiteOpenHelper {

    String TAG = "DBModule";

    public DBModule(final Context ctx, String dbName) { super(ctx,  dbName, null, Constants.DB_VERSION);
        //SQLiteDatabase.openOrCreateDatabase(dbName,null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }
    public void createHistTable(){
        Log.d("","creating history table");
        String queryStrHist = String.format("create table if not exists %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s text);", "hist", "id", "table_name");
        execute(queryStrHist);
        try {
            ContentValues values = new ContentValues();
            values.put("id", 0);
            values.put("table_name", "null");
            insertRowInTable("hist", values);
            Log.d(TAG,"created hist table");
        }catch (Exception e) {
            Log.d(TAG,"Hist table already exists");
        }
    }

    public void execute(String queryStr) {
        getWritableDatabase().execSQL(queryStr);
    }

    public void createAccDataTable(String tableName){
        Log.d("","creating table");
        String queryStr = String.format(
                "create table if not exists %s (%s real, %s real , %s real, %s DATETIME DEFAULT " +
                        "(datetime('now','localtime')));",
                tableName,
                Constants.COLUMN_NAME_ACC_X,
                Constants.COLUMN_NAME_ACC_Y,
                Constants.COLUMN_NAME_ACC_Z,
                Constants.COLUMN_NAME_TIME_STAMP);
        execute(queryStr);
        Log.d("DB","created table");
    }

    public void deleteTable(String tableName){
        String queryStr = String.format("DROP TABLE IF EXISTS %s;",tableName);
        execute(queryStr);
    }

    public void insertRowInTable(String tableName, ContentValues contentValues) {
        getWritableDatabase().insert(tableName, null, contentValues);
    }

    public Cursor getEntry(String queryStr, String[] whereItems) {
        return getReadableDatabase().rawQuery(queryStr, whereItems);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
