package com.future.demo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends MyBaseFragment {

    private static final String TAG = FragmentHome.class.getSimpleName();

    private SharedPreferencesSupport sharedPreferencesSupport;

    private LinearLayout navItemContainer;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);

        sharedPreferencesSupport = new SharedPreferencesSupport(getActivity());

        // 显示 userId、merchantId
        TextView textViewUserId = view.findViewById(R.id.textViewUserId);
        TextView textViewMerchantId = view.findViewById(R.id.textViewMerchantId);
        String userId = sharedPreferencesSupport.read(MainActivity.SharedPreferencesName, "userId");
        String merchantId = sharedPreferencesSupport.read(MainActivity.SharedPreferencesName, "merchantId");
        textViewUserId.setText(userId);
        textViewMerchantId.setText(merchantId);

        navItemContainer = view.findViewById(R.id.navItemContainer);
        viewPager = view.findViewById(R.id.viewPagerHome);

        // 设置 ViewPager
        List<MyBaseFragment> fragments = new ArrayList<>();
        MyBaseFragment fragment = new FragmentProductList();
        fragments.add(fragment);
        fragment = new FragmentListByUserId();
        fragments.add(fragment);
        fragment = new FragmentListByMerchantId();
        fragments.add(fragment);
        fragment = new FragmentCreateProduct();
        fragments.add(fragment);

        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager());
        fragmentPagerAdapter.setFragments(fragments);
        viewPager.setAdapter(fragmentPagerAdapter);

        // 获取导航 item 容器的所有直属子元素 navItem
        List<TextView> navItemList = new ArrayList<>();
        int childCount = navItemContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView navItem = (TextView) navItemContainer.getChildAt(i);
            navItemList.add(navItem);
        }

        // navItem 绑定点击事件
        navItemList.forEach(o -> {
            o.setOnClickListener((viewOnClicked) -> {
                setNavItemState(navItemList, (TextView) viewOnClicked);

                // 获取点击 navItem 索引
                int indexOfNavItem = navItemList.indexOf(viewOnClicked);
                if (indexOfNavItem < 0) {
                    Log.w(TAG, "意料之外，indexOfNavItem=" + indexOfNavItem);
                }

                if (indexOfNavItem == 0) {
                    // 首页

                    // 跳转到 LoginActivity
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);

                    // 销毁当前 Activity
                    getActivity().finish();
                } else if (indexOfNavItem == 1) {
                    // 商品列表
                    viewPager.setCurrentItem(0, true);
                } else if (indexOfNavItem == 2) {
                    // 用户订单
                    viewPager.setCurrentItem(1, true);
                } else if (indexOfNavItem == 3) {
                    // 商家订单
                    viewPager.setCurrentItem(2, true);
                } else if (indexOfNavItem == 4) {
                    // 新增商品
                    viewPager.setCurrentItem(3, true);
                }
            });
        });

        // 默认选中商品列表功能
        navItemList.get(1).performClick();

        return view;
    }

    /**
     * 设置 navItem 状态
     *
     * @param navItemList
     * @param viewOnClicked
     */
    void setNavItemState(List<TextView> navItemList, TextView viewOnClicked) {
        navItemList.forEach(o -> {
            if (o != viewOnClicked) {
                o.setTypeface(null, Typeface.NORMAL);
            } else {
                // 当前点中的 navItem
                o.setTypeface(null, Typeface.BOLD);
            }
        });
    }

    @Override
    protected String getTitle() {
        return "首页";
    }
}
