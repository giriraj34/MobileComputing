package com.example.aravind.group31;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.aravind.group31.properties.Constants;
import com.google.gson.Gson;
import java.util.List;

public class DataFragment extends Fragment implements SensorEventListener {


    public ActivityHelper activityHelper;
    Spinner activity;
    int cnt;
    String strX,strY,strZ;
    String TAG = "Scale Fragment";

    protected int lastX = 0;
    protected boolean status = false;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    public float last_x, last_y, last_z;
    ContentValues contentValues;
    ProgressDialog progressDialog;
    Button collectDataButton;
    Button plotButton;
    Context context;
    WebView myWebView;
    View view;
    Gson gson;

    public DataFragment() {
        // Required empty public constructor
    }
    @SuppressLint("ValidFragment")
    public DataFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHelper = new ActivityHelper(context);
        senSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        contentValues = new ContentValues();
    }
    private void addEntry() {
        //Log.d(TAG, "collecting data");
        if (status) {
            //Log.d(TAG, "true status");
            lastX++;
            cnt++;
            strX = Constants.COLUMN_NAME_ACC_X + "_" + Integer.toString(cnt);
            contentValues.put(strX, last_x);
            strY = Constants.COLUMN_NAME_ACC_Y + "_" + Integer.toString(cnt);
            contentValues.put(strY, last_y);
            strZ = Constants.COLUMN_NAME_ACC_Z + "_" + Integer.toString(cnt);
            contentValues.put(strZ, last_z);
            if(cnt == 50) {
                contentValues.put(Constants.COLUMN_NAME_ACTIVITY,activity.getSelectedItem().toString());
                activityHelper.addEntry(contentValues);
                cnt = 0;
                contentValues = new ContentValues();
            }
        }
    }
    public void addActivityDropDown(){
        ArrayAdapter<String> actAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.activity_items));
        actAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activity.setAdapter(actAdapter);
        activity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    cnt = 0;
                    contentValues = new ContentValues();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    protected void runThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    addEntry();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    public void createButtons() {
        collectDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastX == 0) {
                    runThread();
                }
                if (status == false) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    status = true;
                    collectDataButton.setText("Stop Collection");
                }else{
                    status = false;
                    collectDataButton.setText("Collect Data");
                }
                cnt = 0;
                contentValues = new ContentValues();
            }
        });

        plotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plotGraph();
            }
        });
    }

    @SuppressLint("JavascriptInterface")
    private void plotGraph() {
        WebSettings webSettings = myWebView.getSettings();
        //Enable Javascript
        webSettings.setJavaScriptEnabled(true);
        List<List<AccValues>> walkValues = activityHelper.getActivityData("'Walking'");
        List<List<AccValues>> runValues = activityHelper.getActivityData("'Running'");
        List<List<AccValues>> jumpValues = activityHelper.getActivityData("'Jumping'");
        final String jumpJson = gson.toJson(jumpValues);
        final String runJson = gson.toJson(runValues);
        final String walkJson = gson.toJson(walkValues);
        Log.d(TAG, "num of lines per activity = " + Integer.toString(jumpValues.size()));
        //Log.d(TAG, "num of points per line = " + Integer.toString(jumpValues.get(0).size()));
        Log.d(TAG,jumpJson);

        //URL
        myWebView.loadUrl("file:///android_asset/" + "html/plot.html");
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:plotGraph("+runJson+", "+jumpJson+", "+walkJson+")");
            }
        });

        myWebView.addJavascriptInterface(new WebAppInterface(context), "Android");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_data, container, false);
        progressDialog = new ProgressDialog(getContext());
        activity = view.findViewById(R.id.activity_spinner);
        collectDataButton = (Button) view.findViewById(R.id.collect_data_btn);
        plotButton = (Button) view.findViewById(R.id.plot_btn);
        myWebView = (WebView) view.findViewById(R.id.webview);
        gson = new Gson();
        addActivityDropDown();
        createButtons();
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            last_x = sensorEvent.values[0];
            last_y = sensorEvent.values[1];
            last_z = sensorEvent.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    public void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public class WebAppInterface {
        Context context;
        /** Instantiate the interface and set the context */
        WebAppInterface(Context context) {
            this.context = context;
        }
    }
}