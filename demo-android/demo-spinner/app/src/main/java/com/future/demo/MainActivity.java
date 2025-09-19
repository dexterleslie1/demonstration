package com.future.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        Spinner spinner = findViewById(R.id.spinner);
        List<SpinnerItem> spinnerItemList = Arrays.asList(
                new SpinnerItem("全部", ""),
                new SpinnerItem("未支付", "Unpay"),
                new SpinnerItem("未发货", "Undelivery")
        );
        // 定义 Spinner 的 Adapter
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return spinnerItemList.size();
            }

            @Override
            public Object getItem(int i) {
                // Spinner.getSelectedItem() 的返回值
                return spinnerItemList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View convertView, ViewGroup viewGroup) {
                LayoutInflater _LayoutInflater = LayoutInflater.from(MainActivity.this);
                convertView = _LayoutInflater.inflate(R.layout.spinner_item, null);
                if (convertView != null) {
                    ImageView imageView = convertView.findViewById(R.id.imageView);
                    imageView.setImageResource(R.mipmap.ic_launcher);
                    TextView _TextView1 = convertView.findViewById(R.id.textView3);
                    TextView _TextView2 = convertView.findViewById(R.id.textView4);
                    String text = spinnerItemList.get(i).getText();
                    String value = spinnerItemList.get(i).getValue();
                    _TextView1.setText(text);
                    _TextView2.setText(value);
                }
                return convertView;
            }
        };
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = spinnerItemList.get(i).getText();
                String value = spinnerItemList.get(i).getValue();
                String message = "选中 text=" + text + ",value=" + value;
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 查看 Spinner 选中的 item
                SpinnerItem spinnerItem = (SpinnerItem) spinner.getSelectedItem();
                String text = spinnerItem.getText();
                String value = spinnerItem.getValue();
                String message = "选中 text=" + text + ",value=" + value;
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}