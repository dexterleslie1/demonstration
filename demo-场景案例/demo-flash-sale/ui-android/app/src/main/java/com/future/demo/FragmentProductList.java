package com.future.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;

;

public class FragmentProductList extends MyBaseFragment {

    private GridLayout productListContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_list, null);

        productListContainer = view.findViewById(R.id.productListContainer);

        // 点击刷新商品列表
        Button buttonRefreshProductList = view.findViewById(R.id.buttonRefreshProductList);
        buttonRefreshProductList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshProductList();
            }
        });

        refreshProductList();

        return view;
    }

    void refreshProductList() {
        // 加载商品列表
        Call<JsonObject> call = RetrofitClient.getApiService().listProductByIds();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        /*progressDialog.setIcon(R.mipmap.ic_launcher);*/
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
                productListContainer.removeAllViews();

                JsonObject jsonObject = response.body();
                // 商品列表数据
                JsonArray data = jsonObject.getAsJsonArray("data");

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                for (int i = 0; i < data.size(); i++) {
                    GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                    layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1.0f);
                    layoutParams.width = 0;
                    float density = getResources().getDisplayMetrics().density;
                    // 400dp 转 px
                    layoutParams.height = (int) (400 * density);
                    String name = data.get(i).getAsJsonObject().get("name").getAsString();
                    int stockAmount = data.get(i).getAsJsonObject().get("stock").getAsInt();
                    boolean flashSale = data.get(i).getAsJsonObject().get("flashSale").getAsBoolean();
                    int toFlashSaleStartTimeRemainingSeconds = data.get(i).getAsJsonObject().get("toFlashSaleStartTimeRemainingSeconds").getAsInt();
                    int toFlashSaleEndTimeRemainingSeconds = data.get(i).getAsJsonObject().get("toFlashSaleEndTimeRemainingSeconds").getAsInt();
                    FragmentProductListItem productListItem = new FragmentProductListItem(name, stockAmount, flashSale, toFlashSaleStartTimeRemainingSeconds, toFlashSaleEndTimeRemainingSeconds, layoutParams);
                    transaction.add(R.id.productListContainer, productListItem, FragmentProductListItem.class.getSimpleName());
                }
                transaction.commit();

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected String getTitle() {
        return "商品列表";
    }
}