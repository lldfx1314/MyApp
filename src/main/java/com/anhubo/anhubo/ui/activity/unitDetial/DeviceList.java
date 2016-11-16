package com.anhubo.anhubo.ui.activity.unitDetial;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.DeviceListAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.DeviceListBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/10/22.
 */
public class DeviceList extends BaseActivity {
    @InjectView(R.id.tv_device)
    TextView tvDevice;
    @InjectView(R.id.device_Listview)
    ListView deviceListview;
    private String businessid;
    private ArrayList<String> deviceIds;
    private ArrayList<String> deviceNames;

    @Override
    protected void initConfig() {
        super.initConfig();
        businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_device_list;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("设备列表");
    }

    @Override
    protected void onLoadDatas() {
        deviceIds = new ArrayList<>();
        deviceNames = new ArrayList<>();
        // 先获取网络数据
        progressBar.setVisibility(View.VISIBLE);
        String url = Urls.Device_List;
        Map<String, String> params = new HashMap<>();
        params.put("business_id", businessid);
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }
    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            progressBar.setVisibility(View.GONE);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(false).show();
        }

        @Override
        public void onResponse(String response) {

            //System.out.println("单位列表界面+++==="+response);
            DeviceListBean listBean = new Gson().fromJson(response, DeviceListBean.class);
            if(listBean!=null){
                progressBar.setVisibility(View.GONE);
                List<DeviceListBean.Data.Devices> devices = listBean.data.devices;
                for (int i = 0; i < devices.size(); i++) {
                    DeviceListBean.Data.Devices device = devices.get(i);
                    String deviceId = device.device_id;
                    deviceIds.add(deviceId);
                    String deviceName = device.device_name;
                    deviceNames.add(deviceName);
                }
                if(deviceIds!=null&&deviceNames!=null){
                    tvDevice.setText("设备总数："+deviceIds.size());
                    // 创建一个设备列表的适配器
                    //System.out.println("设备列表界面"+deviceIds);
                    //System.out.println("设备列表界面"+deviceNames);
                    DeviceListAdapter adapter = new DeviceListAdapter(mActivity,deviceIds,deviceNames);
                    deviceListview.setAdapter(adapter);
                }
            }


        }
    }
    @Override
    public void onClick(View v) {

    }
}
