package com.future.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class FragmentListByMerchantId extends MyBaseFragment {

    SharedPreferencesSupport sharedPreferencesSupport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_by_merchant_id, container, false);

        sharedPreferencesSupport = new SharedPreferencesSupport(getActivity());

        ListViewListByMerchantIdAdapter listViewAdapter = new ListViewListByMerchantIdAdapter(getActivity());
        ListView listView = view.findViewById(R.id.listViewListByMerchantId);
        listView.setAdapter(listViewAdapter);

        // 初始化状态 Spinner
        Spinner spinner = view.findViewById(R.id.spinnerListByMerchantId);
        List<SpinnerItem> spinnerItemList = Arrays.asList(
                new SpinnerItem("全部", ""),
                new SpinnerItem("未支付", "Unpay"),
                new SpinnerItem("未发货", "Undelivery"),
                new SpinnerItem("未收货", "Unreceive"),
                new SpinnerItem("已签收", "Received"),
                new SpinnerItem("买家取消", "Canceled")
        );
        // 定义 Spinner 的 Adapter
        BaseAdapter spinnerAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return spinnerItemList.size();
            }

            @Override
            public Object getItem(int i) {
                // Spinner.getSelectedItem() 的返回值
                return spinnerItemList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View convertView, ViewGroup viewGroup) {
                LayoutInflater _LayoutInflater = LayoutInflater.from(getActivity());
                convertView = _LayoutInflater.inflate(R.layout.spinner_item, null);
                if (convertView != null) {
                    TextView textViewSpinnerItemText = convertView.findViewById(R.id.textViewSpinnerItemText);
                    String text = spinnerItemList.get(i).getText();
                    String value = spinnerItemList.get(i).getValue();
                    textViewSpinnerItemText.setText(text);
                }
                return convertView;
            }
        };
        spinner.setAdapter(spinnerAdapter);

        Button buttonListByUserIdQuery = view.findViewById(R.id.buttonListByMerchantIdQuery);
        buttonListByUserIdQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long merchantId = Long.parseLong(sharedPreferencesSupport.read(MainActivity.SharedPreferencesName, "merchantId"));
                // 获取查询状态值
                SpinnerItem spinnerItem = (SpinnerItem) spinner.getSelectedItem();
                String status = spinnerItem.getValue();
                Call<JsonObject> call;
                if (TextUtils.isEmpty(status)) {
                    call = RetrofitClient.getApiService().listByMerchantIdAndWithoutStatus(merchantId, true);
                } else {
                    call = RetrofitClient.getApiService().listByMerchantIdAndStatus(merchantId, true, status);
                }
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
                        JsonArray data = jsonObject.getAsJsonArray("data");
                        listViewAdapter.setData(data);
                        listViewAdapter.notifyDataSetChanged();
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
        return "商家订单";
    }
}