package com.future.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ThirdActivity extends AppCompatActivity {

    private EditText inputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_activity);

        inputEditText = findViewById(R.id.input_edit_text);
        Button returnBtn = findViewById(R.id.return_btn);

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户输入
                String input = inputEditText.getText().toString();

                // 创建返回的 Intent 并设置结果
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", input);
                setResult(RESULT_OK, resultIntent);

                // 结束当前 Activity
                finish();
            }
        });
    }

}
