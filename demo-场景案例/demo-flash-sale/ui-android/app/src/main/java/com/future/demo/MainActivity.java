package com.future.demo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
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

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        final List<MyBaseFragment> fragments = new ArrayList<>();
        MyBaseFragment fragment = new FragmentHome();
        fragments.add(fragment);
        fragment = new FragmentMe();
        fragments.add(fragment);

        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        fragmentPagerAdapter.setFragments(fragments);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);
        // 设置 ViewPager 到 TabLayout
        tabLayout.setupWithViewPager(viewPager);

//        // 点击 Tab Listener
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                int position = tab.getPosition();
//                MyBaseFragment fragment = fragments.get(position);
//                tab.setIcon(fragment.getIconActive());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                int position = tab.getPosition();
//                MyBaseFragment fragment = fragments.get(position);
//                tab.setIcon(fragment.getIconInactive());
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                int position = tab.getPosition();
//                MyBaseFragment fragment = fragments.get(position);
//                tab.setIcon(fragment.getIconActive());
//            }
//        });
    }
}