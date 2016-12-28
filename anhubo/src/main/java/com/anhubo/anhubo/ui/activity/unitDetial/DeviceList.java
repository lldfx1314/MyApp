package com.anhubo.anhubo.ui.activity.unitDetial;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.DeviceListAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.DeleteDeviceBean;
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

import okhttp3.Call;

/**
 * Created by LUOLI on 2016/10/22.
 */
public class DeviceList extends BaseActivity implements AdapterView.OnItemClickListener, DeviceListAdapter.InterClick {
    private TextView tvDevice;
    private ListView listview;
    private String businessid;
    private ArrayList<String> deviceIds;
    private ArrayList<String> deviceNames;
    private ArrayList<String> deviceJudges;
    private DeviceListAdapter adapter;
    private int mPosition;
    private int newDevices;

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
    protected void initTitleBar() {
        super.initTitleBar();

        setTopBarDesc("设备列表");
    }

    @Override
    protected void initViews() {
        tvDevice = (TextView) findViewById(R.id.tv_device);
        listview = (ListView) findViewById(R.id.listview_device);
        // 显示初始值
        tvDevice.setText("拥有设备总数: " + 0);
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        deviceIds = new ArrayList<>();
        deviceNames = new ArrayList<>();
        deviceJudges = new ArrayList<>();

        listview.setOnItemClickListener(this);
    }


    @Override
    protected void onLoadDatas() {

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

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

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
            // System.out.println("单位列表界面+++===" + response);

            progressBar.setVisibility(View.GONE);
            DeviceListBean listBean = new Gson().fromJson(response, DeviceListBean.class);
            if (listBean != null) {
                List<DeviceListBean.Data.Devices> devices = listBean.data.devices;
                if (devices != null && !devices.isEmpty()) {
                    for (int i = 0; i < devices.size(); i++) {
                        DeviceListBean.Data.Devices device = devices.get(i);

                        String deviceId = device.device_id;
                        deviceIds.add(deviceId);

                        String deviceName = device.device_name;
                        deviceNames.add(deviceName);

                        String judge = device.judge;
                        deviceJudges.add(judge);
                    }
                }

                if (deviceIds != null) {
                    newDevices = deviceIds.size();
                    tvDevice.setText("拥有设备总数: " + newDevices);
                }
                if (deviceNames != null && deviceJudges != null) {

                    adapter = new DeviceListAdapter(mActivity, deviceIds,deviceNames, deviceJudges, DeviceList.this);
                    listview.setAdapter(adapter);
                }
            }
        }
    }

    /**
     * listView条目的点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        //System.out.println("点击了条目+"+position);

    }

    /**
     * listView条目里button的点击事件
     */
    @Override
    public void onBtnClick(View v) {
        // 弹窗提示用户删除设备
        mPosition = (int) v.getTag();

        dialog(mPosition);
    }

    /**
     * Dialog对话框提示用户去可以删除当前设备
     */
    protected void dialog(final int position) {

        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("要删除该设备吗？")
                .setCancelable(false)
                .setPositiveButton("删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 删除设备
                        deleteDevice(position);
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    /**
     * 删除设备
     */
    private void deleteDevice(int position) {


        String url = Urls.Delete_Device;
        Map<String, String> params = new HashMap<>();
        params.put("device_id", deviceIds.get(position));
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());
    }

    /**
     * 删除设备
     */
    class MyStringCallback1 extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(false).show();
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("删除设备+" + response);
            DeleteDeviceBean bean = new Gson().fromJson(response, DeleteDeviceBean.class);

            if (bean != null) {
                int code = bean.code;
                if (code == 0) {
                    newDevices = newDevices - 1;
                    tvDevice.setText("拥有设备总数: " + newDevices);
                    // 删除集合里面对应的数据

                    deviceIds.remove(mPosition);
                    deviceNames.remove(mPosition);
                    deviceJudges.remove(mPosition);
                    // 刷新适配器
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}
