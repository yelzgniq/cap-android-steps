package com.kingsleymichael.capandroidsteps;

import android.content.pm.ActivityInfo;
import com.getcapacitor.Bridge;
import android.util.Log;

public class CapAndroidSteps {

    private Bridge bridge;

    CapAndroidSteps(Bridge bridge) {
        this.bridge = bridge;
    }

    /**
     * Inverts the provided string.
     * This method was just an initial test to see if the plugin was working.
     * @param input The string to invert
     * @return The inverted string
     */
    public String invertString(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder reversed = new StringBuilder(input);
        Log.d("CapAndroidSteps", "Inverting string: " + input);
        return reversed.reverse().toString();
    }
}
