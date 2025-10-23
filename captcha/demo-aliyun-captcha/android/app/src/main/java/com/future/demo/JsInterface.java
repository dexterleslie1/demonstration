package com.future.demo;

import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 用于协助测试 JS 调用 Android 的接口
 */
public class JsInterface {

    private AppCompatActivity activity;

    public JsInterface(AppCompatActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void getCaptchaVerifyParam(String captchaVerifyParam) {
        Toast.makeText(this.activity, captchaVerifyParam, Toast.LENGTH_SHORT).show();
    }
}
