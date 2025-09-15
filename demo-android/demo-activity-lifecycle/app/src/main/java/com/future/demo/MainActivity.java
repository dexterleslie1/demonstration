package com.future.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_THIRD_ACTIVITY = 1;
    // 打印日志时的 tag
    private final static String TAG = MainActivity.class.getSimpleName();

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        resultTextView = findViewById(R.id.result_text_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到 SecondActivity 并传递数据
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("message", "Hello from MainActivity!");
                startActivity(intent);
            }
        });


        // 跳转到 ThirdActivity 并期待返回结果
        Button toThirdActivityBtn = findViewById(R.id.button2);
        toThirdActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
                startActivityForResult(intent, REQUEST_CODE_THIRD_ACTIVITY);
            }
        });

        Log.i(TAG, "Activity lifecycle onCreate() is invoked");
    }

    // 处理从 ThirdActivity 返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_THIRD_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                resultTextView.setText("Received from ThirdActivity: " + result);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG, "Activity lifecycle onStart() is invoked");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i(TAG, "Activity lifecycle onRestart() is invoked");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i(TAG, "Activity lifecycle onStop() is invoked");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "Activity lifecycle onDestroy() is invoked");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(TAG, "Activity lifecycle onPause() is invoked");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Activity lifecycle onResume() is invoked");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
