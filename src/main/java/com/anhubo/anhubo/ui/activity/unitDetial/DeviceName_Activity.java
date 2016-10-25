package com.anhubo.anhubo.ui.activity.unitDetial;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.DeviceNameAdapterOne;
import com.anhubo.anhubo.adapter.DeviceNameAdapterSecond;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.DeviceNameBean;
import com.anhubo.anhubo.protocol.RequestResultListener;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个类是用来设置设备名称的
 * Created by Administrator on 2016/9/18.
 */
public class DeviceName_Activity extends BaseActivity {

    private ListView mainlist;
    private ListView morelist;
    private List<DeviceNameBean.DataBean.DevicesBean> list;
    public static List<String> mainList = new ArrayList<>();
    private List<String> list2TypeName = new ArrayList<>();
    private List<DeviceNameBean.DataBean.DevicesBean.ChoiceBean> list2;
    private DeviceNameAdapterSecond adapterSecond;
    private String typeName;
    private String[][] data;


    @Override
    protected void initConfig() {
        getData();
    }


    @Override
    protected int getContentViewId() {
        return R.layout.add_device1_activity;
    }


    @Override
    protected void initViews() {


        // 找控件
        mainlist = (ListView) findViewById(R.id.lv_dev_name01);
        morelist = (ListView) findViewById(R.id.lv_dev_name02);
        // 设置状态栏显示的提示内容
        setTopBarDesc("设备名称");
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    protected void initEvents() {

    }


    @Override
    public void onClick(View v) {

    }


    private void getData() {

        String url = Urls.Url_GetDevName;
        MyRequestResultListener requestResultListener = new MyRequestResultListener();
        NetUtil.requestData(url, null, DeviceNameBean.class, requestResultListener, 0);
    }

    class MyRequestResultListener implements RequestResultListener<DeviceNameBean> {
        @Override
        public void onRequestFinish(DeviceNameBean bean, int what) {
            if (bean != null) {
                showData(bean);
                // 设置第一个适配器
                setAdapterOne();

            } else {
                System.out.println("DeviceName_Activity+++===没拿到bean对象");
            }
        }

        @Override
        public void showJsonStr(String str, int what) {
            //System.out.println("DeviceName_Activity+++==="+str);
        }
    }

    private void setAdapterOne() {
        // 拿到数据后调用构造把数据传过去
        final DeviceNameAdapterOne adapterOne = new DeviceNameAdapterOne(mActivity, mainList);
        mainlist.setAdapter(adapterOne);
        adapterOne.setSelectItem(0);
        mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                initAdapter(data[position]);
                adapterOne.setSelectItem(position);
                adapterOne.notifyDataSetChanged();
            }
        });
        // 设置第二个适配器
        setAdapterSecond();

    }

    private void setAdapterSecond() {
        mainlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // 一定要设置这个属性，否则ListView不会刷新
        initAdapter(data[0]);

        morelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapterSecond.setSelectItem(position);

                TextView tv = (TextView) view.findViewById(R.id.moreitem_txt);
                String str = tv.getText().toString().trim();
                adapterSecond.notifyDataSetChanged();
                // 返回增加界面
                returnAddActivity(str);
            }
        });
    }

    /**
     * 返回增加界面
     */
    private void returnAddActivity(String str) {
        Intent intent = new Intent();
        intent.putExtra(Keys.STR, str);
        setResult(2, intent);
        finish();
    }


    private void initAdapter(String[] array) {
        adapterSecond = new DeviceNameAdapterSecond(DeviceName_Activity.this, array);
        morelist.setAdapter(adapterSecond);
        adapterSecond.notifyDataSetChanged();
    }

    private void showData(DeviceNameBean deviceNameBean) {
        // 先获取第一列数据
        list = deviceNameBean.data.devices;
        // 每次遍历都清空一下集合
        mainList.clear();
        // 遍历list
        for (int i = 0; i < list.size(); i++) {
            DeviceNameBean.DataBean.DevicesBean devicesBean = list.get(i);
            String system_name = devicesBean.system_name;
            // 获取到第一列数据的集合
            mainList.add(system_name);

            // 获取到第二列数据
            list2 = devicesBean.choice;
            for (int m = 0; m < list2.size(); m++) {
                DeviceNameBean.DataBean.DevicesBean.ChoiceBean choiceBean = list2.get(m);
                // 拿到第二列的每一组的每个数据
                typeName = choiceBean.device_name;
                //添加进每组
                list2TypeName.add(typeName);
            }
            // 每次添加完后为了便于区分,添加一个*来用于分割集合
            list2TypeName.add("#");
        }

        //list2TypeName集合添加完之后用#切割得到新数组
        String[] arr = list2TypeName.toString().split("#");
        // 创建一个二维数组
        data = new String[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];
            String string = s.substring(1, s.length()).trim();
            String[] newArr = string.split(",");
            data[i] = newArr;
        }
    }
}




