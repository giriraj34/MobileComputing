package com.example.aravind.group31.properties;
import android.os.Environment;

public class Constants {
    public final static String TABLE_NAME = "accelerometer_data";
    public final static String DB_NAME = "group31.db";
    public final static String MODEL_FILE = "modelFile.txt";
    public final static String TRAIN_DATA = "trainData.txt";
    public final static String PREDICT_DATA = "predData.txt";
    public final static String TEST_DATA = "testData.txt";
    public final static String TEST_POWER = "testPower.txt";
    //public final static String URL = "http://impact.asu.edu/CSE535Spring18Folder/";
    public final static int DB_VERSION = 1;
    //public final static String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    //public final static String DB_PATH =  sdCardPath + "/Android/data/CSE535_ASSIGNMENT3/";
    public final static String APP_FOLDER = "Android/data/CSE535_ASSIGNMENT3/";
    public final static String COLUMN_NAME_ACC_X = "acc_x";
    public final static String COLUMN_NAME_ACC_Y = "acc_y";
    public final static String COLUMN_NAME_ACC_Z = "acc_z";
    public final static String COLUMN_NAME_ACTIVITY = "activity";
    //public final static String COLUMN_NAME_TIME_STAMP = "time_stamp";
}
