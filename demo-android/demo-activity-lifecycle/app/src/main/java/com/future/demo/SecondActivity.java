package com.future.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 *
 */
public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        // 获取从 MainActivity 传递过来的数据
        TextView myTextView = findViewById(R.id.myText);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("message")) {
            String message = intent.getStringExtra("message");
            myTextView.setText(message);
        }
    }
}
