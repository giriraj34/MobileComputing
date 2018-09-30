package com.example.aravind.group31;


/*
createAccDataTable - creates table with given name to store Activity data
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

    //Create the Data table to store accelerometer data
    public void createDataTable() {
        Log.d(TAG, "creating table");
        String queryStr = "";
        String[] colNames = {Constants.COLUMN_NAME_ACC_X, Constants.COLUMN_NAME_ACC_Y, Constants.COLUMN_NAME_ACC_Z};
        queryStr += "create table if not exists " + Constants.TABLE_NAME;
        queryStr += " ( id INTEGER PRIMARY KEY AUTOINCREMENT, ";
        for (int i = 1; i <= 50; i++) {
            for (int j = 0; j < 3; j++)
                queryStr += colNames[j] + "_" + i + " real, ";
        }
        queryStr += Constants.COLUMN_NAME_ACTIVITY + " text );";
        execute(queryStr);
        Log.d(TAG, "created table");
        try {
            ContentValues values = new ContentValues();
            values.put("id", 0);
            insertRowInTable(Constants.TABLE_NAME, values);
            Log.d(TAG,"created hist table");
        }catch (Exception e){
            Log.d(TAG,Constants.TABLE_NAME + " table already exists");
        }
    }

    public void execute(String queryStr) {
        getWritableDatabase().execSQL(queryStr);
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
