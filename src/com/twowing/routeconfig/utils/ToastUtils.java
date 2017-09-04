
package com.twowing.routeconfig.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast mToast = null;

    public static void showToast(Context context, int resId, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, duration);
        } else {
            mToast.setText(context.getString(resId));
            mToast.setDuration(duration);
        }

        mToast.show();
    }

    public static void showToastText(Context context, String data, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, data, duration);
        } else {
            mToast.setText(data);
            mToast.setDuration(duration);
        }

        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
