package com.future.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferencesSupport sharedPreferencesSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferencesSupport = new SharedPreferencesSupport(this);

        // 读取之前的 userId 和 merchantId
        String userIdStr = sharedPreferencesSupport.read(MainActivity.SharedPreferencesName, "userId");
        String merchantIdStr = sharedPreferencesSupport.read(MainActivity.SharedPreferencesName, "merchantId");

        // 把之前的 userId 和 merchantId 设置到界面中
        EditText editTextUserId = findViewById(R.id.editTextUserId);
        EditText editTextMerchantId = findViewById(R.id.editTextMerchantId);
        editTextUserId.setText(userIdStr);
        editTextMerchantId.setText(merchantIdStr);

        // 点击确定按钮
        Button buttonOk = findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存 userId 和 merchantId 到 sharedPreferences 中
                String userId = editTextUserId.getText().toString();
                String merchantId = editTextMerchantId.getText().toString();
                sharedPreferencesSupport.write(MainActivity.SharedPreferencesName, "userId", userId);
                sharedPreferencesSupport.write(MainActivity.SharedPreferencesName, "merchantId", merchantId);

                // 跳转到 MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
