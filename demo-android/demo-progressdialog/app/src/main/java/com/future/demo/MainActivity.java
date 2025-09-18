package com.future.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.TimeUnit;

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

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 圆圈加载进度的 dialog
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setIcon(R.mipmap.ic_launcher);
                progressDialog.setTitle("加载dialog");
                progressDialog.setMessage("加载中...");
                // 是否形成一个加载动画 true 表示不明确加载进度形成转圈动画 false 表示明确加载进度
                progressDialog.setIndeterminate(true);
                // 点击返回键或者 dialog 四周是否关闭 dialog true 表示可以关闭 false 表示不可关闭
                progressDialog.setCancelable(true);
                progressDialog.show();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 带有进度的 dialog
                final int MAX_VALUE = 100;
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setProgress(0);
                progressDialog.setTitle("带有加载进度dialog");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(MAX_VALUE);
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = 0;
                        while (progress < MAX_VALUE) {
                            try {
                                Thread.sleep(100);
                                progress++;
                                progressDialog.setProgress(progress);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        //加载完毕自动关闭dialog
                        progressDialog.cancel();
                    }
                }).start();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 圆圈加载进度的 dialog
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setIcon(R.mipmap.ic_launcher);
                progressDialog.setTitle("加载dialog");
                progressDialog.setMessage("加载中...");
                // 是否形成一个加载动画 true 表示不明确加载进度形成转圈动画 false 表示明确加载进度
                progressDialog.setIndeterminate(true);
                // 点击返回键或者 dialog 四周是否关闭 dialog true 表示可以关闭 false 表示不可关闭
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Thread(()->{
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        Log.e(MainActivity.class.getSimpleName(), e.getMessage(), e);
                        throw new RuntimeException(e);
                    }

                    // 主动关闭 ProgressDialog
                    progressDialog.dismiss();
                }).start();
            }
        });
    }
}