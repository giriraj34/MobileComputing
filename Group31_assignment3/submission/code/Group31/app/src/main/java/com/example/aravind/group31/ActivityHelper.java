package com.example.aravind.group31;

/*
Constructor - create the Database file on SD Card
addEntry - function to add entries to the table in database
createTable - creates a new table on the DB
*/

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.aravind.group31.properties.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivityHelper {

    private DBModule dbModule;
    private Context context;
    String TAG = "HELPER";
    int cnt = 0;

    public ActivityHelper(final Context context) {
        this.context = context;

        // initialize Database Module
        Log.d(TAG,"in create helper");
        dbModule = new DBModule(context, ContainerActivity.appFolderPath + Constants.DB_NAME);
        Log.d(TAG,"Created DB");
        dbModule.createDataTable();
    }

    public Object getActivity() {
        return context;
    }

    public void addEntry(ContentValues values) {
        dbModule.insertRowInTable(Constants.TABLE_NAME, values);
    }

    public List<List<AccValues>> getActivityData(String activity){
        String query = String.format("select * from %s where %s is %s;", Constants.TABLE_NAME, Constants.COLUMN_NAME_ACTIVITY, activity);
        Cursor cursor = dbModule.getEntry(query, null);
        List<List<AccValues>> accValuesList = getAccValues(cursor);
        return accValuesList;
    }

    public List<List<AccValues>> getAccValues(Cursor cursor) {
        List<List<AccValues>> accValuesList = new ArrayList<>();
        List<AccValues> val = new ArrayList<>();
        AccValues accValues;
        cursor.moveToFirst();
        Log.d(TAG,"Count of rows = "+ cursor.getCount());
        int index;
        cnt = 0;
        while (!cursor.isAfterLast() && cnt < 20){
            index = 1;
            while (index <= 50) {
                double x = cursor.getDouble(cursor.getColumnIndex(Constants.COLUMN_NAME_ACC_X + "_"+ Integer.toString(index)));
                double y = cursor.getDouble(cursor.getColumnIndex(Constants.COLUMN_NAME_ACC_Y + "_"+ Integer.toString(index)));
                double z = cursor.getDouble(cursor.getColumnIndex(Constants.COLUMN_NAME_ACC_Z + "_"+ Integer.toString(index)));
                accValues = new AccValues(x, y, z);
                val.add(accValues);
                Log.d(TAG, "x = " + x + ", y = " + y + ", z = " + z);
                index++;
            }
            Log.d(TAG,"index = " + Integer.toString(index));
            accValuesList.add(val);
            val = new ArrayList<>();
            cursor.moveToNext();
            cnt++;
        }
        return accValuesList;
    }
}