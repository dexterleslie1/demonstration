package com.future.demo;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityProductPurchase extends AppCompatActivity {

    long productId;
    boolean flashSale;

    SharedPreferencesSupport sharedPreferencesSupport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_purchase);

        sharedPreferencesSupport = new SharedPreferencesSupport(this);

        Button buttonProductPurchase = findViewById(R.id.buttonProductPurchase);

        // 加载商品
        productId = getIntent().getLongExtra("productId", 0);
        Call<JsonObject> call = RetrofitClient.getApiService().getProductById(productId);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("加载中...");
        // 是否形成一个加载动画 true 表示不明确加载进度形成转圈动画 false 表示明确加载进度
        progressDialog.setIndeterminate(true);
        // 点击返回键或者 dialog 四周是否关闭 dialog true 表示可以关闭 false 表示不可关闭
        progressDialog.setCancelable(false);
        progressDialog.show();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                JsonObject data = jsonObject.getAsJsonObject("data");

                String name = data.get("name").getAsString();
                int stockAmount = data.get("stock").getAsInt();
                flashSale = data.get("flashSale").getAsBoolean();
                int toFlashSaleStartTimeRemainingSeconds = data.get("toFlashSaleStartTimeRemainingSeconds").getAsInt();
                int toFlashSaleEndTimeRemainingSeconds = data.get("toFlashSaleEndTimeRemainingSeconds").getAsInt();

                TextView textViewName = findViewById(R.id.textViewProductPurchaseName);
                TextView textViewStockAmount = findViewById(R.id.textViewProductPurchaseStockAmount);
                textViewName.setText(name);
                textViewStockAmount.setText(String.valueOf(stockAmount));

                LinearLayout linearLayoutStatus = findViewById(R.id.linearLayoutProductPurchaseStatus);
                if (!flashSale) {
                    buttonProductPurchase.setText("购买");
                    linearLayoutStatus.setVisibility(View.INVISIBLE);
                } else {
                    buttonProductPurchase.setText("秒杀");

                    TextView textViewStatus = findViewById(R.id.textViewProductPurchaseStatus);
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

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ActivityProductPurchase.this, t.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

        // 购买或者秒杀商品
        buttonProductPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long userId = Long.parseLong(sharedPreferencesSupport.read(MainActivity.SharedPreferencesName, "userId"));
                Call<JsonObject> call = null;
                if (flashSale) {
                    call = RetrofitClient.getApiService().createOrderFlashSale(userId, productId, false);
                } else {
                    call = RetrofitClient.getApiService().createOrderOrdinary(userId, productId, false);
                }
                ProgressDialog progressDialog = new ProgressDialog(ActivityProductPurchase.this);
                progressDialog.setIcon(R.mipmap.ic_launcher);
                progressDialog.setTitle("提示");
                progressDialog.setMessage("处理中...");
                // 是否形成一个加载动画 true 表示不明确加载进度形成转圈动画 false 表示明确加载进度
                progressDialog.setIndeterminate(true);
                // 点击返回键或者 dialog 四周是否关闭 dialog true 表示可以关闭 false 表示不可关闭
                progressDialog.setCancelable(false);
                progressDialog.show();
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                        JsonObject jsonObject = response.body();
                        String data = jsonObject.getAsJsonPrimitive("data").getAsString();
                        Toast.makeText(ActivityProductPurchase.this, data, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(ActivityProductPurchase.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }
}
