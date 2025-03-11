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

public class SecurityCheck extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("check")) {
            try {
                Activity activity = cordova.getActivity();
                ContentResolver resolver = activity.getContentResolver();

                boolean isDebuggerAttached = Debug.isDebuggerConnected();
                boolean isUsbDebuggingEnabled = Settings.Secure.getInt(resolver, Settings.Global.ADB_ENABLED, 0) != 0;
                boolean isDebuggable = (activity.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
                boolean isDevOptionsEnabled = isDeveloperOptionsEnabled(activity);

                JSONObject result = new JSONObject();
                result.put("debuggerAttached", isDebuggerAttached);
                result.put("usbDebugging", isUsbDebuggingEnabled);
                result.put("debuggable", isDebuggable);
                result.put("devOptionsEnabled", isDevOptionsEnabled);

                callbackContext.success(result);
            } catch (Exception e) {
                callbackContext.error("Security check failed: " + e.getMessage());
            }
            return true;
        }
        return false;
    }

    private boolean isDeveloperOptionsEnabled(Activity activity) {
        try {
            int devOptions = Settings.Secure.getInt(activity.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
            return devOptions != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
