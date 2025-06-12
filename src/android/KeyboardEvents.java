package com.example.keyboard;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class KeyboardEvents extends CordovaPlugin {

    private View rootView;
    private int previousHeight = 0;
    private ViewTreeObserver.OnGlobalLayoutListener layoutListener;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if ("init".equals(action)) {
            cordova.getActivity().runOnUiThread(() -> {
                FrameLayout content = (FrameLayout) cordova.getActivity().findViewById(android.R.id.content);
                rootView = content.getRootView();
                DisplayMetrics dm = new DisplayMetrics();
                cordova.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                final float density = dm.density;

                layoutListener = () -> {
                    Rect r = new Rect();
                    rootView.getWindowVisibleDisplayFrame(r);

                    int screenHeight;
                    if (Build.VERSION.SDK_INT >= 21) {
                        Display display = cordova.getActivity().getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        screenHeight = size.y;
                    } else {
                        screenHeight = rootView.getRootView().getHeight();
                    }

                    int heightDiff = screenHeight - r.bottom;
                    int heightDp = (int) (heightDiff / density);

                    if (heightDp > 100 && heightDiff != previousHeight) {
                        PluginResult result = new PluginResult(PluginResult.Status.OK, "S" + heightDp);
                        result.setKeepCallback(true);
                        callbackContext.sendPluginResult(result);
                    } else if (heightDiff < previousHeight - 100) {
                        PluginResult result = new PluginResult(PluginResult.Status.OK, "H");
                        result.setKeepCallback(true);
                        callbackContext.sendPluginResult(result);
                    }

                    previousHeight = heightDiff;
                };

                rootView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
                PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
            });

            return true;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        if (rootView != null && layoutListener != null) {
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
        }
    }
}
