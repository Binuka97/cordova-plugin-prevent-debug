package com.plugin.security;

import android.content.ContentResolver;
import android.provider.Settings;
import android.os.Debug;
import android.util.Log;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import org.apache.cordova.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class SecurityCheck extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("check")) {
            try {
                Activity activity = cordova.getActivity();
                ContentResolver resolver = activity.getContentResolver();

                boolean isDebuggerAttached = Debug.isDebuggerConnected();
                boolean isUsbDebuggingEnabled = Settings.Secure.getInt(resolver, Settings.Secure.ADB_ENABLED, 0) != 0;
                boolean isDebuggable = (activity.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
                boolean isDevOptionsEnabled = isDeveloperOptionsEnabled(activity);
                boolean isHooked = isAppHooked();

                JSONObject result = new JSONObject();
                result.put("debuggerAttached", isDebuggerAttached);
                result.put("usbDebugging", isUsbDebuggingEnabled);
                result.put("debuggable", isDebuggable);
                result.put("devOptionsEnabled", isDevOptionsEnabled);
                result.put("appHooked", isHooked);

                callbackContext.success(result);
            } catch (Exception e) {
                Log.e("SecurityCheck", "Security check failed: " + e.getMessage());
                callbackContext.error("Security check failed: " + e.getMessage());
            }
            return true;
        }
        return false;
    }

    private boolean isDeveloperOptionsEnabled(Activity activity) {
        try {
            int devOptions;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
                devOptions = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED, 0);
            } else {
                devOptions = Settings.Global.getInt(activity.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
            }
            return devOptions != 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAppHooked() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().contains("frida") || element.getClassName().contains("xposed")) {
                return true;
            }
        }
        return false;
    }
}
    
    // The plugin is very simple. It checks for the following security issues: 
    
    // Debugger attached 
    // USB debugging enabled 
    // App is debuggable 
    // Developer options enabled 
    // App is hooked by Frida or Xposed 
    
    // The plugin is written in Java and uses the Android SDK to check for these issues. 
    // Step 3: Add the plugin to the Cordova project 
    // To add the plugin to the Cordova project, run the following command: 
    // cordova plugin add /path/to/SecurityCheck 
    // Step 4: Use the plugin in the Cordova project 
    // To use the plugin in the Cordova project, add the following code to the JavaScript file: