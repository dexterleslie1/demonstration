package com.future.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

        Button buttonShortToast = findViewById(R.id.buttonShortToast);
        Button buttonLongToast = findViewById(R.id.buttonLongToast);
        buttonShortToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出短时间 Toast
                Toast.makeText(getApplication(), "短Toast", Toast.LENGTH_SHORT).show();
            }
        });
        buttonLongToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出长时间 Toast
                Toast.makeText(getApplication(), "长Toast", Toast.LENGTH_LONG).show();
            }
        });
    }
}