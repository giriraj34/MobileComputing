package com.example.aravind.group31;

/*
Constructor - create the Database file on SD Card
addEntry - function to add entries to the table in database
createTable - creates a new table on the DB
*/
import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.example.aravind.group31.properties.Constants;

import java.io.File;

public class ActivityHelper {

    private DBModule dbModule;
    private String tableName;
    File apkStorage = null;
    String TAG = "HELPER";

    public ActivityHelper(final Context context) {
        // Pass the table name here and add it below
        if (isSDCardPresent()) {
            apkStorage = new File(Constants.UPLOAD_PATH);
        } else
            Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

        //If File is not present create directory
        if (!apkStorage.exists()) {
            apkStorage.mkdir();
            Log.i(TAG, "Directory Created." + apkStorage.getAbsolutePath());
        }

        // initialize Database Module
        Log.d(TAG,"in create helper");
        dbModule = new DBModule(context, apkStorage.getAbsolutePath() + File.separator + Constants.DB_NAME);
        Log.d("Helper","Created DB");
        dbModule.createHistTable();
    }

    public void createTable(String tblName) {
        this.tableName = tblName;
        dbModule.createAccDataTable(tblName);
        Log.i(TAG,"created table" + tableName);
        ContentValues values = new ContentValues();
        //values.put("id","null");
        values.put("table_name",tableName);
        dbModule.insertRowInTable("hist", values);
    }

    public boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public void addEntry(double xVal, double yVal, double zVal) {
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_NAME_ACC_X, xVal);
        values.put(Constants.COLUMN_NAME_ACC_Y, yVal);
        values.put(Constants.COLUMN_NAME_ACC_Z, zVal);
        //values.put(Constants.COLUMN_NAME_TIME_STAMP, timestamp); Takes the default value so not adding it here
        dbModule.insertRowInTable(tableName, values);
    }
}