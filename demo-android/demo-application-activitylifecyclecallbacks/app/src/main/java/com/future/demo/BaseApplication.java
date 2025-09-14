package com.future.demo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 *
 */
public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks{
    private final static String TAG = BaseApplication.class.getSimpleName();

    // 用于判断应用是否在前台，=1时为前台，=0使为后台
    private int activityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        this.activityCount++;
        boolean changingConfigurations = activity.isChangingConfigurations();
        String message = String.format("changingConfigurations %s onActivityStarted activity count: %d", changingConfigurations, activityCount);
        Log.i(TAG, message);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        this.activityCount--;
        boolean changingConfigurations = activity.isChangingConfigurations();
        String message = String.format("changingConfigurations %s onActivityStopped activity count: %d", changingConfigurations, activityCount);
        Log.i(TAG, message);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
