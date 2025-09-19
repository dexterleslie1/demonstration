package com.future.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FragmentProductListItem extends Fragment {

    long id;
    String name;
    int stockAmount;
    boolean flashSale;
    int toFlashSaleStartTimeRemainingSeconds;
    int toFlashSaleEndTimeRemainingSeconds;
    GridLayout.LayoutParams layoutParams;

    public FragmentProductListItem(
            long id,
            String name,
            int stockAmount,
            boolean flashSale,
            int toFlashSaleStartTimeRemainingSeconds,
            int toFlashSaleEndTimeRemainingSeconds,
            GridLayout.LayoutParams layoutParams) {
        this.id = id;
        this.name = name;
        this.stockAmount = stockAmount;
        this.flashSale = flashSale;
        this.toFlashSaleStartTimeRemainingSeconds = toFlashSaleStartTimeRemainingSeconds;
        this.toFlashSaleEndTimeRemainingSeconds = toFlashSaleEndTimeRemainingSeconds;
        this.layoutParams = layoutParams;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_info, container, false);

        TextView textViewName = view.findViewById(R.id.textViewProductInfoName);
        TextView textViewStockAmount = view.findViewById(R.id.textViewProductInfoStockAmount);
        textViewName.setText(name);
        textViewStockAmount.setText(String.valueOf(stockAmount));

        LinearLayout linearLayoutStatus = view.findViewById(R.id.linearLayoutProductInfoStatus);
        if (!flashSale) {
            linearLayoutStatus.setVisibility(View.INVISIBLE);
        } else {
            TextView textViewStatus = view.findViewById(R.id.textViewProductInfoStatus);
            if (toFlashSaleStartTimeRemainingSeconds > 0) {
                textViewStatus.setText("距离秒杀开始时间还有 " + toFlashSaleStartTimeRemainingSeconds + " 秒");
                textViewStatus.setTextColor(Color.parseColor("#FFA500"));
            } else if (toFlashSaleStartTimeRemainingSeconds <= 0 && toFlashSaleEndTimeRemainingSeconds > 0) {
                textViewStatus.setText("距离秒杀结束还有 " + toFlashSaleEndTimeRemainingSeconds + " 秒");
                textViewStatus.setTextColor(Color.parseColor("#9ACD32"));
            } else if (toFlashSaleStartTimeRemainingSeconds <= 0 && toFlashSaleEndTimeRemainingSeconds <= 0) {
                textViewStatus.setText("秒杀已结束");
                textViewStatus.setTextColor(Color.parseColor("#FF0000"));
            }
        }

        view.setLayoutParams(layoutParams);

        // 点击 fragment 跳转到购买商品 activity
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityProductPurchase.class);
                intent.putExtra("productId", id);
                startActivity(intent);
            }
        });

        return view;
    }
}