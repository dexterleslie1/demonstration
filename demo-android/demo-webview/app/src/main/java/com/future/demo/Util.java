package com.future.demo;

import android.net.Uri;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Util {

    /**
     * WebView 加载远程资源的 ip 地址
     */
    public final static String RemoteHost = "192.168.235.167";
    /**
     * WebView 加载远程资源的端口
     */
    public final static int RemotePort = 5500;

    /**
     * 初始化 WebView
     *
     * @param webView
     */
    public static void setupWebView(WebView webView,
                                    String url) {
        // WebView 设置
        WebSettings webSettings = webView.getSettings();
        // 设置开启 Chrome 调试
        WebView.setWebContentsDebuggingEnabled(true);
        // 设置屏幕自适应
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        // 禁用缓存，每次加载页面时都会从网络或服务器重新请求资源，而不会使用本地缓存
        // webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置 WebView 组件支持加载 JavaScript
        webSettings.setJavaScriptEnabled(true);
        // 允许网页使用 localStorage（持久化存储）和 sessionStorage（会话级存储）
        webSettings.setDomStorageEnabled(true);

        // 设置不使用默认浏览器，而直接使用 WebView 组件加载页面
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // URL 加载拦截
                Uri uri = Uri.parse(url);
                if (uri.getScheme().equals("wx") && uri.getAuthority().equals("myUrl")) {
                    String message = "URL 加载拦截：" + uri.getScheme() + "://" + uri.getAuthority();
                    // 拼接 url 参数
                    String query = "";
                    for (String name : uri.getQueryParameterNames()) {
                        query += "&" + name + "=" + uri.getQueryParameter(name);
                    }
                    query = query.substring(1);
                    message += "?" + query;
                    Toast.makeText(webView.getContext(), message, Toast.LENGTH_LONG).show();

                    // 表示已经处理了该 URL
                    return true;
                }

                view.loadUrl(url);
                return true;
            }
        });

        // 加载本地或者远程资源
        // 例如：加载本地 assets 目录中的 index.html 文件，file:///android_asset/index.html
        // 例如：加载远程资源，http://x.x.x.x:5500/index.html
        webView.loadUrl(url);
    }
}
