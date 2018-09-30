package com.example.aravind.group31;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aravind.group31.properties.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import umich.cse.yctung.androidlibsvm.LibSVM;

public class TrainFragment extends Fragment implements SensorEventListener {

    static TrainFragment trainFragment;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    public float last_x, last_y, last_z;
    public String testStr = "0";
    Button trainButton;
    Button testButton;
    File file;
    PrintWriter writer;
    TextView accuracy;
    TextView prediction;
    View view;
    String parameters = "";
    Spinner kernelSpinner;
    int cnt = 1;
    Context context;
    Spinner parameterSpinner;
    Button predictButton;
    List<String> commands;
    String TAG = "TRAIN FRAGMENT";
    StringBuilder log;
    private static String[] words;

    public TrainFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public TrainFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trainFragment = this;
        senSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_train, container, false);
        trainButton = (Button) view.findViewById(R.id.train_btn);
        predictButton = (Button) view.findViewById(R.id.predict_btn);
        testButton = (Button) view.findViewById(R.id.test_btn);
        accuracy = (TextView) view.findViewById(R.id.accuracyValue);
        prediction = (TextView) view.findViewById(R.id.pred_activity);

        String[] kernelItems = view.getResources().getStringArray(R.array.kernel_items);
        kernelSpinner = (Spinner) view.findViewById(R.id.kernel_spinner);
        ArrayAdapter<String> kernelAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_list_item_1, kernelItems);
        kernelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kernelSpinner.setAdapter(kernelAdapter);


        String[] parameterItems = view.getResources().getStringArray(R.array.parameters_items);
        parameterSpinner = (Spinner) view.findViewById(R.id.parameter_spinner);
        ArrayAdapter<String> parameterAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_list_item_1, parameterItems);
        parameterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parameterSpinner.setAdapter(parameterAdapter);

        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parameters += " -t " + Integer.toString(kernelSpinner.getSelectedItemPosition());
                String outputModelPath = ContainerActivity.appFolderPath+Constants.MODEL_FILE;
                parameters += " -v " + Integer.toString(parameterSpinner.getSelectedItemPosition()+3);
                Log.d(TAG,parameters);
                String dataFilePath = ContainerActivity.appFolderPath+Constants.TRAIN_DATA;
                new AsyncTrainTask().execute(new String[]{parameters, dataFilePath, outputModelPath});
            }
        });

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "do the activity for 5 seconds to predict", Toast.LENGTH_SHORT).show();
                runThread();
                cnt = 1;
                testStr = "0";
            }
        });

        testButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                commands = new ArrayList<>();
                commands.add(ContainerActivity.appFolderPath + Constants.TEST_POWER);
                commands.add(ContainerActivity.appFolderPath + Constants.MODEL_FILE);
                commands.add(ContainerActivity.appFolderPath + Constants.PREDICT_DATA);
                new AsyncPredictTask().execute(commands.toArray(new String[0]));
            }
        });

        return view;
    }

    public void readLogcat(){
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            log = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if(line.contains(ContainerActivity.processId)) {
                    while((line = bufferedReader.readLine()) != null){
                        if( line.contains("Accuracy")) {
                            words = line.split(":");
                        }
                    }
                    break;
                }
            }
            log.append(words[words.length-1]);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(ContainerActivity.TAG, "readLogcat: failed to read from logcat logger.");
        }
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

    private class AsyncTrainTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(ContainerActivity.TAG, "==================\nStart of SVM TRAIN\n==================");
        }

        @Override
        protected Void doInBackground(String... params) {
            LibSVM.getInstance().train(TextUtils.join(" ", params));
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getContext(), "SVM Train has executed successfully!", Toast.LENGTH_LONG).show();
            Log.d(ContainerActivity.TAG, "==================\nEnd of SVM TRAIN\n==================");
            readLogcat();
            accuracy.setText(log.toString());
        }
    }

    private class AsyncPredictTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(ContainerActivity.TAG, "==================\nStart of SVM PREDICT\n==================");
        }

        @Override
        protected Void doInBackground(String... params) {
            LibSVM.getInstance().predict(TextUtils.join(" ", params));
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getContext(), "SVM Predict has executed successfully!", Toast.LENGTH_LONG).show();
            Log.d(ContainerActivity.TAG, "==================\nEnd of SVM PREDICT\n==================");
            try {
            file = new File(ContainerActivity.appFolderPath,Constants.PREDICT_DATA);
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                String predictedVal;
                while ((line = br.readLine()) != null) {
                    Log.d(TAG,"predicted val = " + line);
                    predictedVal = "RUNNING";
                    if(line.equalsIgnoreCase("1"))
                        predictedVal = "WALKING";
                    if(line.equalsIgnoreCase("2"))
                        predictedVal = "JUMPING";
                    Log.d(TAG,"predicted text = " + predictedVal);
                    prediction.setText(predictedVal);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void addEntry() {
        //Log.d(TAG, "collecting data");
            //Log.d(TAG, "true status");
        if(cnt<150)
            testStr += " " + cnt++ + ":" + last_x + " " + cnt++ + ":" + last_y + " " + cnt++ + ":" + last_z;
            if(cnt >= 150) {
                Log.d(TAG,"count= "+cnt);
                file = new File(ContainerActivity.appFolderPath,Constants.TEST_DATA);
                try {
                    file.createNewFile();
                    writer = new PrintWriter(file);
                    writer.print(testStr);
                    writer.flush();
                    writer.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                commands = new ArrayList<>();
                commands.add(ContainerActivity.appFolderPath + Constants.TEST_DATA);
                commands.add(ContainerActivity.appFolderPath + Constants.MODEL_FILE);
                commands.add(ContainerActivity.appFolderPath + Constants.PREDICT_DATA);
                new AsyncPredictTask().execute(commands.toArray(new String[0]));
            }
    }
    protected void runThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(cnt < 150) {
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
}
