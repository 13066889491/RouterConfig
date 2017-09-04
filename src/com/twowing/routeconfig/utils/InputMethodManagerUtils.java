
package com.twowing.routeconfig.utils;

import java.lang.reflect.Field;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class InputMethodManagerUtils {
    private InputMethodManager mInputMethodManager;
    private static InputMethodManagerUtils mInputMethodUtils;
    private Context mContext;

    private InputMethodManagerUtils(Context context) {
        mContext = context;
        mInputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static InputMethodManagerUtils getInstance(Context context) {
        synchronized (InputMethodManagerUtils.class) {
            if (mInputMethodUtils == null) {
                mInputMethodUtils = new InputMethodManagerUtils(context);
            }
        }
        return mInputMethodUtils;
    }

    // 防止内存泄露
    public void fixInputMethodManagerLeak() {
        if (mContext == null || mInputMethodManager == null) {
            return;
        }
        try {
            Object object = null;
            Field mCurRootView = mInputMethodManager.getClass()
                    .getDeclaredField("mCurRootView");
            Field mServedView = mInputMethodManager.getClass()
                    .getDeclaredField("mServedView");
            Field mNextServedView = mInputMethodManager.getClass()
                    .getDeclaredField("mNextServedView");

            if (mCurRootView.isAccessible() == false) {
                mCurRootView.setAccessible(true);
            }
            object = mCurRootView.get(mInputMethodManager);
            if (object != null) { // 不为null则置为空
                mCurRootView.set(mInputMethodManager, null);
            }

            if (mServedView.isAccessible() == false) {
                mServedView.setAccessible(true);
            }
            object = mServedView.get(mInputMethodManager);
            if (object != null) { // 不为null则置为空
                mServedView.set(mInputMethodManager, null);
            }

            if (mNextServedView.isAccessible() == false) {
                mNextServedView.setAccessible(true);
            }
            object = mNextServedView.get(mInputMethodManager);
            if (object != null) { // 不为null则置为空
                mNextServedView.set(mInputMethodManager, null);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // 1.调用显示系统默认的输入法
    public void showSoftInput(View v) {
        if (mInputMethodManager != null) {
            mInputMethodManager.showSoftInput(v, 0);
        }
    }

    // 2..调用隐藏系统默认的输入法
    public void hideKeyboard(View v) {
        if (mInputMethodManager.isActive() && mInputMethodManager != null) {
            mInputMethodManager.hideSoftInputFromWindow(
                    v.getApplicationWindowToken(), 0);
        }
    }
}
