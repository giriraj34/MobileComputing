package com.example.aravind.group31.properties;
import android.os.Environment;

public class Constants {
    public final static String DB_NAME = "group31.db";
    public final static String URL = "http://impact.asu.edu/CSE535Spring18Folder/";
    public final static int DB_VERSION = 1;
    public final static String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public final static String DOWNLOAD_PATH = sdCardPath + "/Android/data/CSE535_ASSIGNMENT2_DOWN/";
    public final static String UPLOAD_PATH =  sdCardPath + "/Android/data/CSE535_ASSIGNMENT2/";
    //public final static String DOWNLOAD_PATH =  "/extsdcard/Android/data/CSE535_ASSIGNMENT2_DOWN/";
    //public final static String UPLOAD_PATH =  "/extsdcard/Android/data/CSE535_ASSIGNMENT2/";
    public final static String COLUMN_NAME_ACC_X = "acc_x";
    public final static String COLUMN_NAME_ACC_Y = "acc_y";
    public final static String COLUMN_NAME_ACC_Z = "acc_z";
    public final static String COLUMN_NAME_TIME_STAMP = "time_stamp";
}
