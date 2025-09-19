package com.future.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ListViewListByUserIdAdapter extends BaseAdapter {
    JsonArray data;

    Context context;

    /**
     * @param context
     */
    public ListViewListByUserIdAdapter(Context context) {
        this.context = context;
        this.data = new JsonArray();
    }

    public void setData(JsonArray data) {
        if (data == null) {
            data = new JsonArray();
        }
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data.get(i).getAsJsonObject().get("id").getAsLong();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.list_view_list_by_user_id_item, null);

        JsonObject jsonObject = data.get(i).getAsJsonObject();
        long orderId = jsonObject.get("id").getAsLong();
        String createTime = jsonObject.get("createTime").getAsString();
        long merchantId = jsonObject.get("merchantId").getAsLong();
        JsonArray orderDetailList = jsonObject.getAsJsonArray("orderDetailList");
        String productList = "";
        for (int j = 0; j < orderDetailList.size(); j++) {
            String productName = orderDetailList.get(j).getAsJsonObject().get("productName").getAsString();
            productList = productList + productName + "，";
        }

        TextView textViewOrderId = view.findViewById(R.id.textViewListByUserIdOrderId);
        TextView textViewCreateTime = view.findViewById(R.id.textViewListByUserIdCreateTime);
        TextView textViewMerchantId = view.findViewById(R.id.textViewListByUserIdMerchantId);
        TextView textViewProductList = view.findViewById(R.id.textViewListByUserIdProductList);
        textViewOrderId.setText(String.valueOf(orderId));
        textViewCreateTime.setText(createTime);
        textViewMerchantId.setText(String.valueOf(merchantId));
        textViewProductList.setText(productList);

        return view;
    }
}
