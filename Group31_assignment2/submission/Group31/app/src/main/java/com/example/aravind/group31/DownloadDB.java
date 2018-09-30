package com.example.aravind.group31;

/*
This module downloads the DB from server and plots last 10 entries on the graph
 */
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.aravind.group31.properties.Constants;


public class DownloadDB {
    private static final String TAG = "Download Task";
    private Context context;
    private DBModule dbModule;
    private String downloadUrl = Constants.URL + Constants.DB_NAME;
    public String finalTable;
    public List<AccValues> accValuesList;

    public DownloadDB(Context context){
        this.context = context;
        new DownloadingDB().execute();
    }

    public Object getActivity() {
        return context;
    }

    public class DownloadingDB extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Check if SD card is present
            if (isSDCardPresent()) {
                apkStorage = new File(Constants.DOWNLOAD_PATH);
            } else
                Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

            //If File is not present create directory
            if (!apkStorage.exists()) {
                apkStorage.mkdir();
                Log.e(TAG, "Directory Created." + apkStorage.getAbsolutePath());
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are getting data
                c.connect();//connect the URL Connection
                c.setReadTimeout(20000);
                c.setConnectTimeout(20000);

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode() + " " + c.getResponseMessage());
                }

                outputFile = new File(apkStorage, Constants.DB_NAME);
                Log.i(TAG, "output file "+outputFile.getName() + "  "+outputFile.exists());
                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.i(TAG, "File Created");
                }
                InputStream is = c.getInputStream();//Get InputStream for connection
                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location
                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Boolean origStatus = ((MainActivity)getActivity()).graphStatus;
            ((MainActivity)getActivity()).graphStatus = false;
            dbModule = new DBModule(context, outputFile.getAbsolutePath());
            String query = String.format("select * from hist order by id desc limit 1;");
            Cursor cursor = dbModule.getEntry(query, null);
            cursor.moveToFirst();
            if(cursor.getInt(cursor.getColumnIndex("id")) == 0)
                return;
            Log.d(TAG,"index value = "+cursor.getString(0));
            finalTable = cursor.getString(cursor.getColumnIndex("table_name"));
            Log.d(TAG,"finalTable = "+finalTable);
            //finalTable = "giriraj_1213350721_23_Male";
            query = String.format("select %s,%s,%s from %s order by %s desc limit 10",
                    Constants.COLUMN_NAME_ACC_X, Constants.COLUMN_NAME_ACC_Y, Constants.COLUMN_NAME_ACC_Z,
                    finalTable, Constants.COLUMN_NAME_TIME_STAMP);
            cursor = dbModule.getEntry(query, null);
            accValuesList = getAccValues(cursor);
            if(((MainActivity)getActivity()).lastX == 0) ((MainActivity)getActivity()).runGraph();
            ((MainActivity)getActivity()).populatePatientDetails(finalTable);
            ((MainActivity)getActivity()).plotDownloadedValues(accValuesList);
            ((MainActivity)getActivity()).graphStatus = origStatus;
        }

        public List<AccValues> getAccValues(Cursor cursor) {
            List<AccValues> accValuesList = new ArrayList<>();
            cursor.moveToFirst();
            Log.d(TAG,"Count of rows = "+ cursor.getCount());
            int index=0;
            while (!cursor.isAfterLast() && index<10) {
                double x = cursor.getDouble(cursor.getColumnIndex(Constants.COLUMN_NAME_ACC_X));
                double y = cursor.getDouble(cursor.getColumnIndex(Constants.COLUMN_NAME_ACC_Y));
                double z = cursor.getDouble(cursor.getColumnIndex(Constants.COLUMN_NAME_ACC_Z));
                AccValues accValues = new AccValues( x, y, z);
                accValuesList.add(accValues);
                Log.d(TAG,"x = "+ x +", y = "+ y +", z = "+ z);
                cursor.moveToNext();
                index++;
            }

            return accValuesList;
        }

        public boolean isSDCardPresent() {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) return true;
            return false;
        }

    }

}
