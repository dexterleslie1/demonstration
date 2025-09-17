package com.future.demo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 */
public class SharedPreferencesSupport {
    private Context context;

    /**
     *
     */
    public SharedPreferencesSupport(Context context) {
        this.context = context;
    }

    /**
     *
     * @param sharedPreferencesName
     * @param storeKey
     * @param storeValue
     */
    public void write(String sharedPreferencesName, String storeKey, String storeValue) {
        SharedPreferences sharedPreferences =
                this.context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(storeKey, storeValue);
        editor.apply();
    }

    /**
     *
     * @param sharedPreferencesName
     * @param storeKey
     * @return
     */
    public String read(String sharedPreferencesName, String storeKey) {
        SharedPreferences sharedPreferences =
                this.context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        String storeValue = sharedPreferences.getString(storeKey, null);
        return storeValue;
    }
}
