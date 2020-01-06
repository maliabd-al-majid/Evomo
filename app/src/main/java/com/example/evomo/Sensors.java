package com.example.evomo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sensors extends Activity {
    protected static DBFile file;
    public static boolean IsStarted = false;
    private int Index = 0;
    SensorManager sensorManager;
    private float[] accelerationValues;
    private float[] rotationValues;
    private float[] gravityValues;
    private float[] quaternionValues;
    private float[] userAccelerationValues;
    private long timestamp;
    private List<double[]> RecordingData;
    private long lastUpdate;// used to control time between every record of Sensor

    public Sensors(Context context, LinearLayout linearLayout) {
        initializeSensor(context, linearLayout);
        setRecordstoView(context, linearLayout);

    }

    public void initializeSensor(final Context context, final LinearLayout linearLayout) {
        // initialize Sensor values and listeners
        lastUpdate = System.currentTimeMillis();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerationValues = new float[3];
        rotationValues = new float[3];
        gravityValues = new float[3];
        quaternionValues = new float[4];
        userAccelerationValues = new float[3];
        RecordingData = new ArrayList<double[]>();
        file = new DBFile();
        final SensorEventListener mEventListener = new SensorEventListener() {
            public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
            }

            public void onSensorChanged(SensorEvent event) {
                // run when Sensor's Values Change
                switch (event.sensor.getType()) {
                    case android.hardware.Sensor.TYPE_ACCELEROMETER:
                        System.arraycopy(event.values, 0, accelerationValues, 0, 3);
                        break;
                    case Sensor.TYPE_GRAVITY:
                        System.arraycopy(event.values, 0, gravityValues, 0, 3);
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        System.arraycopy(event.values, 0, rotationValues, 0, 3);
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        System.arraycopy(event.values, 0, quaternionValues, 0, 4);
                        break;
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        System.arraycopy(event.values, 0, userAccelerationValues, 0, 3);
                        break;
                }

                if (Sensors.IsStarted) {
                    timestamp = event.timestamp;
                    if (timestamp - lastUpdate < 1000000000) {// time between records and Button is in Start Status ( Can be edited based on accuracy we need to apply but File and List will contain huge data)
                        return;
                    }
                    lastUpdate = timestamp;
                    startrecording(context, linearLayout);
                }
            }
        };
        setListeners(sensorManager, mEventListener);
    }

    public void startrecording(Context context, LinearLayout linearLayout) {
        //Start adding Elments to List and File

            getRecordingData();
            TextView RecordRow = new TextView(context);
            RecordRow.setBackgroundColor(2);
            RecordRow.append(Arrays.toString(RecordingData.get(RecordingData.size() - 1)));
            linearLayout.addView(RecordRow);


    }

    public void setListeners(SensorManager sensorManager, SensorEventListener mEventListener) {  // initailize all listeners

        sensorManager.registerListener(mEventListener,
                sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(mEventListener,
                sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mEventListener,
                sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mEventListener,
                sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    private float[] getAccelerationValues() {

        return accelerationValues;
    }

    private float[] getRotationValues() {
        return rotationValues;
    }

    private float[] getGravityValues() {
        return gravityValues;
    }

    private float[] getQuartnionValues() {
        return quaternionValues;
    }

    private float[] getUserAccelerationValues() {
        return userAccelerationValues;
    }

    private long getTimeStamp() {
        return timestamp;
    }

    private void getRecordingData() { // add new Record Row to List
        double[] recordRow = new double[17];

        recordRow[0] = getAccelerationValues()[0];
        recordRow[1] = getAccelerationValues()[1];
        recordRow[2] = getAccelerationValues()[2];
        recordRow[3] = getRotationValues()[0];
        recordRow[4] = getRotationValues()[1];
        recordRow[5] = getRotationValues()[2];
        recordRow[6] = getUserAccelerationValues()[0];
        recordRow[7] = getUserAccelerationValues()[1];
        recordRow[8] = getUserAccelerationValues()[2];
        recordRow[9] = getGravityValues()[0];
        recordRow[10] = getGravityValues()[1];
        recordRow[11] = getGravityValues()[2];
        recordRow[12] = getQuartnionValues()[0];
        recordRow[13] = getQuartnionValues()[1];
        recordRow[14] = getQuartnionValues()[2];
        recordRow[15] = getQuartnionValues()[3];
        recordRow[16] = getTimeStamp();
        RecordingData.add(recordRow);
        //  saveRecordstoFile(context);
    }

    private void setRecordstoView(Context context, LinearLayout linearLayout) {
        getRecordsFromFile(context);
        for (int i = 0; i < RecordingData.size(); i++) {
            TextView RecordRow = new TextView(context);
            RecordRow.setBackgroundColor(2);
            RecordRow.append(Arrays.toString(RecordingData.get(i)));
            linearLayout.addView(RecordRow);
        }
    }

    public void saveRecordstoFile(Context context) {//Save to File

        for (; Index < RecordingData.size(); Index++) {
            file.Save(context, Arrays.toString(RecordingData.get(Index)));
        }
    }

    private void getRecordsFromFile(Context context) { // Read from file into List of doubles
        RecordingData = new ArrayList<double[]>();
        String textdata = file.Load(context);
        String[] recordstext = textdata.replace("[", "").split("]");
        if (recordstext.length - 1 > 0)
            for (int i = 0; i < recordstext.length - 1; i++) {
                double[] recordRow = new double[17];
                String[] rowtext = recordstext[i].split(",");
                for (int j = 0; j < recordRow.length; j++) {
                    recordRow[j] = Double.parseDouble(rowtext[j]);

                }
                RecordingData.add(recordRow);
                Index++;
            }
        //  textdata.split()

    }

    public void clearRecords(Context context) { // Clear File and Clear List
        RecordingData = new ArrayList<double[]>();
        file.Clear(context);
        Index = 0;
        ///
    }


}



