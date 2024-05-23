package com.example.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {
    private Context context;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private OnShakeListener shakeListener;
    private long lastShakeTime;
    private static final long SHAKE_COOLDOWN_TIME_MS = 1000; // 1 second cooldown

    // Constructor to initialize the ShakeDetector
    public ShakeDetector(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
        lastShakeTime = System.currentTimeMillis(); // Initialize last shake time
    }

    // Define the interface for shake detection listener
    public interface OnShakeListener {
        void onShake(); // Callback method for shake detection
    }

    // Set the shake detection listener
    public void setOnShakeListener(OnShakeListener listener) {
        shakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Get accelerometer values
            float xAxis = event.values[0];
            float yAxis = event.values[1];
            float zAxis = event.values[2];

            // Set shake threshold values
            float thresholdX = 10.0f;
            float thresholdY = 10.0f;
            float thresholdZ = 10.0f;

            // Check if shake threshold is exceeded
            if (Math.abs(xAxis) > thresholdX || Math.abs(yAxis) > thresholdY || Math.abs(zAxis) > thresholdZ) {
                long currentTime = System.currentTimeMillis();
                // Check if cooldown time has passed since last shake
                if (currentTime - lastShakeTime > SHAKE_COOLDOWN_TIME_MS) {
                    // Trigger the shake listener callback
                    shakeListener.onShake();
                    lastShakeTime = currentTime; // Update last shake time
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle changes in sensor accuracy if needed
    }

    // Unregister the sensor listener to release resources
    public void unregisterListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
