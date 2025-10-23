package com.future.demo;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WebView webView = findViewById(R.id.webView);
        // WebView 设置
        WebSettings webSettings = webView.getSettings();
        // 设置开启 Chrome 调试
        WebView.setWebContentsDebuggingEnabled(true);
        // 设置屏幕自适应
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        // 禁用缓存，每次加载页面时都会从网络或服务器重新请求资源，而不会使用本地缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置 WebView 组件支持加载 JavaScript
        webSettings.setJavaScriptEnabled(true);
        // 允许网页使用 localStorage（持久化存储）和 sessionStorage（会话级存储）
        webSettings.setDomStorageEnabled(true);

        // 设置不使用默认浏览器，而直接使用 WebView 组件加载页面
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // 建立JavaScript调用Java接口的桥梁
        webView.addJavascriptInterface(new JsInterface(this), "AndroidBridge");
        // 加载本地或者远程资源
        // 例如：加载本地 assets 目录中的 index.html 文件，file:///android_asset/index.html
        // 例如：加载远程资源，http://x.x.x.x:5500/index.html
        webView.loadUrl("file:///android_asset/index.html");
    }
}