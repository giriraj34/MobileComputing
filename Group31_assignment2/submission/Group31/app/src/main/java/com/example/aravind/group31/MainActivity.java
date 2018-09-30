package com.example.aravind.group31;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.aravind.group31.properties.Constants;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public ActivityHelper activityHelper;
    static MainActivity mainActivity;
    private GraphView graph;
    EditText name;
    EditText age;
    EditText id;
    Spinner sex;

    String tableName;

    private LineGraphSeries<DataPoint> series1;
    private LineGraphSeries<DataPoint> series2;
    private LineGraphSeries<DataPoint> series3;
    protected int lastX = 0;
    protected boolean graphStatus = false;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    public float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity=this;

        // initialize the ActivityHelper Class
        activityHelper = new ActivityHelper(mainActivity);

        // Add Sex Dropdown
        addSexDropDown();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        // initialize the graph
        createGraph();

        // create Run and Stop Buttons
        createButtons();

        createPatientObj();
    }

    public void addSexDropDown(){
        Spinner sexSpinner =(Spinner) findViewById(R.id.sex_spinner);
        //MainActivity.this
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(mainActivity,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sex_items));
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(sexAdapter);
    }

    public void createPatientObj(){
        name = findViewById(R.id.name_edit_view);
        age = findViewById(R.id.age_edit_view);
        id = findViewById(R.id.id_edit_view);
        sex = findViewById(R.id.sex_spinner);

        View.OnKeyListener inputTextWatcher= new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (graphStatus && (event.getAction() == KeyEvent.ACTION_DOWN && keyCode != KeyEvent.KEYCODE_DEL)) {
                    tableName = getTableName();
                    activityHelper.createTable(tableName);
                }
                return false;
            }
        };
        name.setOnKeyListener(inputTextWatcher);
        id.setOnKeyListener(inputTextWatcher);
        age.setOnKeyListener(inputTextWatcher);

        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(graphStatus) {
                    tableName = getTableName();
                    activityHelper.createTable(tableName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
    }

    public void createGraph(){
        graph = (GraphView) findViewById(R.id.graph);
        series1 = new LineGraphSeries<DataPoint>();
        series1.setColor(Color.BLUE);
        series2 = new LineGraphSeries<DataPoint>();
        series2.setColor(Color.BLACK);
        series3 = new LineGraphSeries<DataPoint>();
        series3.setColor(Color.RED);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-10);
        graph.getViewport().setMaxY(10);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(4);
        graph.getViewport().setMaxX(80);

        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time(sec)");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Acc (m/s2)");

        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling

        series1.setTitle("x - axis");
        series2.setTitle("y - axis");
        series3.setTitle("z - axis");
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        graph.getLegendRenderer().setSpacing(10);
        graph.getLegendRenderer().setTextSize(30);

    }

    public void createButtons(){
        Button button1 = (Button) findViewById(R.id.run_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(graphStatus == false) {
                    graph.addSeries(series1);
                    graph.addSeries(series2);
                    graph.addSeries(series3);
                    graph.getLegendRenderer().setVisible(true);
                }
                tableName = getTableName();
                activityHelper.createTable(tableName);
                graphStatus = true;
                if(lastX == 0){
                    runGraph();
                }
            }
        });

        Button button2 = (Button) findViewById(R.id.stop_button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(graphStatus && (lastX != 0)) {
                    graph.removeAllSeries();
                    graph.getLegendRenderer().setVisible(false);
                }
                graphStatus = false;
            }
        });
        createUpDownButtons();
    }

    public void createUpDownButtons(){

        ImageButton imageButton1 = findViewById(R.id.uploadButton);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String path = Constants.UPLOAD_PATH + Constants.DB_NAME;
                new UploadDB(path).execute();
                Toast.makeText(MainActivity.this,"Upload DB successful",Toast.LENGTH_SHORT).show();
            }

        });

        ImageButton imageButton2 = findViewById(R.id.downloadButton);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new DownloadDB(mainActivity);
                Toast.makeText(MainActivity.this,"Download DB successful",Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void populatePatientDetails(String tableName){
        String patient[] = tableName.split("_");
        name.setText(patient[0]);
        id.setText(patient[1]);
        age.setText(patient[2]);
        if(patient[3].equalsIgnoreCase("male"))
            sex.setSelection(0);
        else
            sex.setSelection(1);
    }

    protected void plotDownloadedValues(List<AccValues> accValuesList){
        for(int i = 0; i< accValuesList.size(); i++) {
            series1.appendData(new DataPoint(lastX, accValuesList.get(i).x), true, 1000); //  Blue Graph
            series2.appendData(new DataPoint(lastX, accValuesList.get(i).y), true, 1000); //  Black Graph
            series3.appendData(new DataPoint(lastX, accValuesList.get(i).z), true, 1000); //  Red Graph
            lastX++;
        }
    }

    protected void runGraph() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    private void addEntry() {
        if (graphStatus) {
            series1.appendData(new DataPoint(lastX, last_x), true, 1000); //  Blue Graph
            series2.appendData(new DataPoint(lastX, last_y), true, 1000); //  Black Graph
            series3.appendData(new DataPoint(lastX, last_z), true, 1000); //  Red Graph
            lastX++;
            activityHelper.addEntry(last_x, last_y, last_z);
        }
    }

    public String getTableName(){
        return name.getText() + "_" + id.getText() + "_" + age.getText() + "_" + sex.getSelectedItem();
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

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
