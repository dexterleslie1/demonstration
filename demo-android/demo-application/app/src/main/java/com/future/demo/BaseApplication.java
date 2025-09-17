package com.future.demo;

import android.app.Application;
import android.util.Log;

/**
 *
 */
public class BaseApplication extends Application {
    private final static String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "回调 onCreate() 方法");
    }

    // onTerminate() 仅在模拟器有效，真机开发中需忽略此方法。
    // Android 强调应用应随时准备被系统销毁，因此不允许依赖“退出回调”来保存关键数据（需实时持久化）。
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "回调 onTerminate() 方法");
    }
}
