package com.kingsleymichael.capandroidsteps;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

@CapacitorPlugin(name = "CapAndroidSteps")
public class CapAndroidStepsPlugin extends Plugin implements SensorEventListener {

    private static final int ACTIVITY_RECOGNITION_REQUEST_CODE = 100;
    private static final String TAG = "CapAndroidStepsPlugin";
    
    private static class StepData {
        public long timestamp;
        public float count;
        
        public StepData(long timestamp, float count) {
            this.timestamp = timestamp;
            this.count = count;
        }
    }

    private CapAndroidSteps implementation;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private Map<Integer, PluginCall> savedCalls = new HashMap<>();
    
    // For tracking step data
    private List<StepData> stepHistory = new ArrayList<>();
    private float initialStepCount = -1;
    private float lastStepCount = 0;

    // Add a field to store the latest sensor values
    private float[] latestSensorValues = null;

    @Override
    public void load() {
        implementation = new CapAndroidSteps(getBridge());
        // Initialize sensor manager
        sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        if (sensorManager != null) {
            Log.d(TAG, "Sensor manager initialized");
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        // Permission check and request (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String permission = Manifest.permission.ACTIVITY_RECOGNITION;
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "ACTIVITY_RECOGNITION permission not granted. Requesting permission.");
                ActivityCompat.requestPermissions(getActivity(), new String[] { permission }, ACTIVITY_RECOGNITION_REQUEST_CODE);
                return;  // Important: Exit load() if permission is not granted yet.  `handlePermissionsRequest` will resume.
            } else {
                Log.d(TAG, "ACTIVITY_RECOGNITION permission already granted.");
            }
        }

        // Register the listener (only if sensor and permission are OK)
        if (stepSensor != null) {
            Log.d(TAG, "Registering step sensor listener");
            Log.d(TAG, "stepSensor: " + stepSensor.toString()); //log sensor info
            Log.d(TAG, "sensorManager: " + sensorManager.toString()); //log manager info
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            Log.d(TAG, "Step sensor listener registered.");
        } else {
            Log.w(TAG, "Step sensor is null.  Cannot register listener.");
        }
    }

    public void handlePermissionsRequest(int requestCode, String[] permissions, int[] grantResults) {
        PluginCall savedCall = savedCalls.get(requestCode);
        if (savedCall == null) {
            Log.d(TAG, "No stored plugin call for request code " + requestCode);
            return;
        }
        
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, restart sensor listening
                if (stepSensor != null) {
                    sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
                    savedCall.resolve(buildPermissionResult(true));
                } else {
                    savedCall.reject("Step sensor not available on this device");
                }
            } else {
                savedCall.resolve(buildPermissionResult(false));
            }
            
            savedCalls.remove(requestCode);
        }
    }

    @PluginMethod
    public void requestActivityRecognitionPermission(PluginCall call) {
        // Starting with Android 10 (API 29), apps must be granted the ACTIVITY_RECOGNITION permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String permission = Manifest.permission.ACTIVITY_RECOGNITION;
            if (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                // We already have the permission
                call.resolve(buildPermissionResult(true));
            } else {
                // Save the call for handling in onRequestPermissionsResult
                savedCalls.put(ACTIVITY_RECOGNITION_REQUEST_CODE, call);
                
                // Request the permission
                ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[] { permission },
                    ACTIVITY_RECOGNITION_REQUEST_CODE
                );
                
                // Don't resolve the call immediately - will be resolved in onRequestPermissionsResult
                call.setKeepAlive(true);
            }
        } else {
            // For Android versions below 10, this permission is not required
            call.resolve(buildPermissionResult(true));
        }
    }
    
    private JSObject buildPermissionResult(boolean granted) {
        JSObject result = new JSObject();
        result.put("granted", granted);
        return result;
    }

    @PluginMethod
    public void invertString(PluginCall call) {
        String value = call.getString("value");
        if (value == null) {
            call.reject("Must provide a string value");
            return;
        }
        
        String inverted = implementation.invertString(value);
        JSObject ret = new JSObject();
        ret.put("value", inverted);
        call.resolve(ret);
    }

    @PluginMethod
    public void getStepsForPeriod(PluginCall call) {
        String period = call.getString("period", "day");
        
        // First check if we have permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String permission = Manifest.permission.ACTIVITY_RECOGNITION;
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                call.reject("Activity recognition permission not granted");
                return;
            }
        }
        
        // Special case for "all" period - return all steps since boot from the raw sensor value
        if ("all".equals(period)) {
            // Check if we have sensor access
            if (stepSensor == null) {
                call.reject("Step sensor not available on this device");
                return;
            }
            
            // Check if we have at least one reading (meaning the sensor has been initialized)
            if (initialStepCount == -1) {
                // show message but also raw sensor values
                Log.w(TAG, "lastStepCount value: " + lastStepCount); // added this line
                call.reject("No step data available yet. Try again after walking a few steps.");
                return;
            }
            
            // Return the raw step count from the sensor, which represents total steps since boot
            int totalSteps = (int) lastStepCount;
            
            JSObject ret = new JSObject();
            ret.put("count", totalSteps);
            ret.put("period", period);
            call.resolve(ret);
            return;
        }
        
        // For hour and day periods, we need step history
        if (stepHistory.isEmpty()) {
            call.reject("No step data available. Make sure step counting is active.");
            return;
        }
        
        // Calculate steps based on the requested period
        long periodStartTime;
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        
        if ("hour".equals(period)) {
            cal.add(Calendar.HOUR, -1);
        } else { // Default to "day"
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        periodStartTime = cal.getTimeInMillis();
        
        // Calculate steps in the period
        int stepsInPeriod = 0;
        float firstStepInPeriod = -1;
        
        // Find the first step count reading in the period
        for (StepData data : stepHistory) {
            if (data.timestamp >= periodStartTime) {
                if (firstStepInPeriod < 0) {
                    firstStepInPeriod = data.count;
                }
            }
        }
        
        // If we don't have any readings in the period, return 0
        if (firstStepInPeriod < 0) {
            JSObject ret = new JSObject();
            ret.put("count", 0);
            ret.put("period", period);
            call.resolve(ret);
            return;
        }
        
        // Get the latest reading
        float latestStepCount = stepHistory.get(stepHistory.size() - 1).count;
        
        // Calculate the difference
        stepsInPeriod = (int) (latestStepCount - firstStepInPeriod);
        
        // Ensure we don't return negative values (could happen if sensor was reset)
        if (stepsInPeriod < 0) {
            stepsInPeriod = 0;
        }
        
        JSObject ret = new JSObject();
        ret.put("count", stepsInPeriod);
        ret.put("period", period);
        call.resolve(ret);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: " + event.sensor.getType());
        // Handle step sensor data
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            float steps = event.values[0];
            long currentTime = System.currentTimeMillis();

            Log.d(TAG, "onSensorChanged: steps = " + steps + ", currentTime = " + currentTime);  //Log current time
            
            // Store the raw sensor values
            latestSensorValues = new float[event.values.length];
            System.arraycopy(event.values, 0, latestSensorValues, 0, event.values.length);
            
            // Initialize step count if this is the first reading
            if (initialStepCount < 0) {
                initialStepCount = steps;
                lastStepCount = steps;
            }
            
            // Store step data to history
            stepHistory.add(new StepData(currentTime, steps));
            
            // Keep history size reasonable (last 24 hours should be enough)
            // Remove readings older than 24 hours
            long oneDayAgo = currentTime - (24 * 60 * 60 * 1000);
            while (!stepHistory.isEmpty() && stepHistory.get(0).timestamp < oneDayAgo) {
                stepHistory.remove(0);
            }
            
            // Calculate steps since last reading
            float stepsSinceLastReading = steps - lastStepCount;
            lastStepCount = steps;
            
            Log.d(TAG, "Steps counted: " + steps + " (+" + stepsSinceLastReading + " since last reading)");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Required by SensorEventListener interface
        // Can be left empty if not needed

        Log.w(TAG, "Sensor accuracy changed: sensor = " + sensor.getName() + ", accuracy = " + accuracy);

        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                Log.w(TAG, "Sensor data is unreliable.");
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                Log.w(TAG, "Sensor accuracy is low.");
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                Log.d(TAG, "Sensor accuracy is medium.");
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                Log.d(TAG, "Sensor accuracy is high.");
                break;
        }
    }

    @PluginMethod
    public void getRawSensorValues(PluginCall call) {
        // First check if we have permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String permission = Manifest.permission.ACTIVITY_RECOGNITION;
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                call.reject("Activity recognition permission not granted");
                return;
            }
        }
        
        // Check if we have sensor access
        if (stepSensor == null) {
            call.reject("Step sensor not available on this device");
            return;
        }
        
        // Check if we have any sensor data
        if (latestSensorValues == null) {
            call.reject("No sensor data available yet. Try again after walking a few steps.");
            return;
        }
        
        // Create response with the raw sensor values
        JSObject ret = new JSObject();
        
        // Add all sensor values as an array
        JSObject valuesObject = new JSObject();
        for (int i = 0; i < latestSensorValues.length; i++) {
            valuesObject.put(String.valueOf(i), latestSensorValues[i]);
        }
        ret.put("values", valuesObject);
        
        // Also include the step count as convenience
        ret.put("stepCount", latestSensorValues[0]);
        
        // Include sensor information
        ret.put("sensorName", stepSensor.getName());
        ret.put("sensorVendor", stepSensor.getVendor());
        ret.put("sensorVersion", stepSensor.getVersion());
        
        call.resolve(ret);
    }
}
