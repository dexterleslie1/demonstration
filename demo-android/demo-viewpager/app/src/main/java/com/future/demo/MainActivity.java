package com.future.demo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化 ViewPager
        final List<MyBaseFragment> fragments = new ArrayList<>();
        MyBaseFragment fragment = new FragmentUnpay();
        fragments.add(fragment);
        fragment = new FragmentPaying();
        fragments.add(fragment);
        fragment = new FragmentPaymentRecord();
        fragments.add(fragment);

        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        fragmentPagerAdapter.setFragments(fragments);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);

        // 编程式切换 ViewPager
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        button1.setOnClickListener((view) -> {
            viewPager.setCurrentItem(0, true/* 平滑切换 */);
        });
        button2.setOnClickListener((view) -> {
            viewPager.setCurrentItem(1, true/* 平滑切换 */);
        });
        button3.setOnClickListener((view) -> {
            viewPager.setCurrentItem(2, true/* 平滑切换 */);
        });
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
