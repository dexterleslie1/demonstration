package com.future.demo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;

public class FragmentCreateProduct extends MyBaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_product, container, false);

        CheckBox checkBoxFlashSale = view.findViewById(R.id.checkBoxCreateProductFlashSale);
        checkBoxFlashSale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout linearLayout = view.findViewById(R.id.linearLayoutCreateProductFlashSaleTime);
                linearLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        Button buttonCreateProductCreate = view.findViewById(R.id.buttonCreateProductCreate);
        buttonCreateProductCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<JsonObject> call;
                if (!checkBoxFlashSale.isChecked()) {
                    call = RetrofitClient.getApiService().addOrdinaryProduct();
                } else {
                    call = RetrofitClient.getApiService().addFlashSaleProduct();
                }
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("提示");
                progressDialog.setMessage("创建中...");
                // 是否形成一个加载动画 true 表示不明确加载进度形成转圈动画 false 表示明确加载进度
                progressDialog.setIndeterminate(true);
                // 点击返回键或者 dialog 四周是否关闭 dialog true 表示可以关闭 false 表示不可关闭
                progressDialog.setCancelable(false);
                progressDialog.show();
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                        JsonObject jsonObject = response.body();

                        long productId = jsonObject.getAsJsonPrimitive("data").getAsLong();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setIcon(R.mipmap.ic_launcher).setTitle("提示")
                                .setMessage("成功新增普通商品，是否跳转到商品详情功能并下单呢？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // 跳转到商品购买/秒杀页面
                                        Intent intent = new Intent(getActivity(), ActivityProductPurchase.class);
                                        intent.putExtra("productId", productId);
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                        builder.create().show();

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });

        return view;
    }

    @Override
    protected String getTitle() {
        return "新增商品";
    }
}