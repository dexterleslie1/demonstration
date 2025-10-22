package com.future.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

/**
 *
 */
public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        // 启用 ActionBar 并显示返回按钮，需要继承 AppCompatActivity 才有 ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 获取从 MainActivity 传递过来的数据
        TextView myTextView = findViewById(R.id.myText);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("message")) {
            String message = intent.getStringExtra("message");
            myTextView.setText(message);
        }
    }

    // 处理返回按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 或者 finish()
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
