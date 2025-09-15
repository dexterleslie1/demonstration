package com.future.demo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tabLayout);

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
        // 设置 ViewPager 到 TabLayout
        tabLayout.setupWithViewPager(viewPager);

        for(int i=0; i<tabLayout.getTabCount(); i++) {
            if(i == 0) {
                tabLayout.getTabAt(i).setIcon(fragments.get(i).getIconActive());
            }else {
                tabLayout.getTabAt(i).setIcon(fragments.get(i).getIconInactive());
            }
        }

        // 点击 Tab Listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                MyBaseFragment fragment = fragments.get(position);
                tab.setIcon(fragment.getIconActive());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                MyBaseFragment fragment = fragments.get(position);
                tab.setIcon(fragment.getIconInactive());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                MyBaseFragment fragment = fragments.get(position);
                tab.setIcon(fragment.getIconActive());
            }
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
