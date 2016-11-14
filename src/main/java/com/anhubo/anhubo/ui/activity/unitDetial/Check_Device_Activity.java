package com.anhubo.anhubo.ui.activity.unitDetial;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.DeviceDetailsAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.CheckComplete_Bean;
import com.anhubo.anhubo.bean.ScanBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by Administrator on 2016/9/14.
 */
public class Check_Device_Activity extends BaseActivity {

    private ListView ck_ListView;
    private TextView deviceName;
    public static final String TAG = "Check_Device_Activity.class";
    private String device_type_name;
    private DeviceDetailsAdapter adapter;
    /*返回的问题集合*/
    private List<String> require_list;
    /*用来记录是否获取到信息的变量*/
    private boolean isGetDeviceInfo = false;
    private String deviceId;
    // 更多按钮
    private Button btnCheckMore;
    //完成按钮
    private Button btnCheckComplete;
    // 选择完成后的有问题的集合
    private ArrayList<Integer> completeList;
    /* 用来记录存在问题的选项，0表示没问题，1表示有问题*/
    private int isProblem = 0;
    /*用来记录对应的position位是否被点击*/
    private Map<Integer,Boolean> map = new HashMap<>();
    private int dateFlag;
    private String uid;
    private String businessid;
    private String scanType;

    @Override
    protected void initConfig() {
        super.initConfig();
        // 设置对话框以外的界面点击无效果
        setFinishOnTouchOutside(false);
        // 获取uid
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);

        Intent intent = getIntent();
        ScanBean deviceInfo = (ScanBean) intent.getSerializableExtra(Keys.DEVICEINFO);
        scanType = intent.getStringExtra(Keys.SCAN_TYPE);
        // 拿到序列化对象就可以获取里面数据进行展示了
        if (deviceInfo != null) {
            // 获取到数据置为true
            isGetDeviceInfo = true;
            showInfo(deviceInfo);
        } else {
            ToastUtils.showToast(mActivity, "请扫二维码或NFC进入本页面");
        }
        // 初始化completeList
        completeList = new ArrayList<>();
        // 创建一个数组，五个数全是0
        int[] arr = new int[]{0,0,0,0,0};
        for (int i = 0; i < arr.length; i++) {

            completeList.add(arr[i]);
        }
        // 为map集合设置默认的值，每个item默认没被点击过
        map.put(0,false);
        map.put(1,false);
        map.put(2,false);
        map.put(3,false);
        map.put(4,false);
        //System.out.println(map.toString());
    }

    /**
     * 拿到传过来的信息获取设备的相关信息
     */
    private void showInfo(ScanBean deviceInfo) {
        // 获取到设备名
        device_type_name = deviceInfo.data.device_type_name;
        // 获取到设备Id
        deviceId = deviceInfo.data.device_id;
        // 获取到时间标记
        dateFlag = deviceInfo.data.require_date_flag;
        // 拿到问题集合
        require_list = deviceInfo.data.require_list;
        //System.out.println("哈哈哈哈哈哈哈+++==="+require_list);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_check_device;
    }

    @Override
    protected void initViews() {
        // 找控件
        ck_ListView = (ListView) findViewById(R.id.check_listView);
        deviceName = (TextView) findViewById(R.id.tv_check_device);
        btnCheckMore = (Button) findViewById(R.id.check_more);// 更多
        btnCheckComplete = (Button) findViewById(R.id.check_complete);//检查完成

        // 获取适配器对象
        adapter = new DeviceDetailsAdapter(this, require_list);

    }


    /**事件的处理*/
    @Override
    protected void initEvents() {
        super.initEvents();
        // 显示 设备名称
        if (isGetDeviceInfo) {
            deviceName.setText(device_type_name);
        } else {
            deviceName.setText("没有数据");
            isGetDeviceInfo = false;
        }
        // 给listView设置适配器对象
        ck_ListView.setAdapter(adapter);
        // 设置listView的监听
        ck_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_check_device);
                // 获取当前position对应位置的点击记录
                Boolean isClick = map.get(position);
                if(!isClick) {
                    // 表示这个问题存在
                    imageView.setImageResource(R.drawable.fuxuan_input01);
                    // 存在问题就设置为1;
                    isProblem = 1;
                }else{
                    // 这个问题不存在
                    imageView.setImageResource(R.drawable.fuxuan_input02);
                    isProblem = 0;
                }

                // 将对应position位置改为对应的boolean值和值
                map.put(position, !isClick);
                completeList.set(position,isProblem);
            }

        });

        // 设置按钮的点击事件
        btnCheckMore.setOnClickListener(this);
        btnCheckComplete.setOnClickListener(this);
    }

    /**
     * 网络请求
     */
    @Override
    protected void onLoadDatas() {

    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {

        // 设置按钮的点击事件
        switch (v.getId()){
            case R.id.check_more:
            // 更多的点击事件
                Intent intent = new Intent(mActivity, FeedbackActivity.class);
                intent.putExtra(Keys.DeviceId,deviceId);
                startActivity(intent);
            break;
            case R.id.check_complete:
                // 检查完成的点击事件
                checkComplete();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 失去焦点后将集合清空
        completeList.clear();
    }

    /**检查完成的点击事件，请求网络*/
    private void checkComplete() {
        // 这里是完成的点击事件
        String url = Urls.Url_Check_Complete;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid",uid); //这是uid,登录后改成真正的用户
        params.put("device_id",deviceId);
        params.put("device_result",completeList.toString());//选择后的集合
        params.put("business_id",businessid);//这是business_id,登录后改成真正的business_id

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }
    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("Check_Device_Activity+++===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            CheckComplete_Bean bean = new Gson().fromJson(response, CheckComplete_Bean.class);
            if(bean!=null){
                ToastUtils.showToast(mActivity,"检查完成");

                if(TextUtils.equals(scanType,"nfcscan")){
                    // 把数据传给nfc界面
                    Intent intentNfC = new Intent();
                    intentNfC.putExtra(Keys.CHECKCOMPLETE_BEAN, bean);
                    setResult(1, intentNfC);
                    finish();
                }else if(TextUtils.equals(scanType,"qrscan")){
                    // 把数据传给Qr界面
                    Intent intentScan = new Intent();
                    intentScan.putExtra(Keys.CHECKCOMPLETE_BEAN, bean);
                    setResult(1, intentScan);
                    finish();
                }



            }else {
                System.out.println("Check_Device_Activity+++===没获取到bean对象");
            }
        }
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 获取到屏幕的的宽度和高度
        Display defaultDisplay = getWindow().getWindowManager().getDefaultDisplay();
        int screenWidth = defaultDisplay.getWidth();
        int screenHeight = defaultDisplay.getHeight();

        // 获取到对话框对象
        View decorView = getWindow().getDecorView();
        // 对话框参数对象
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) decorView.getLayoutParams();
        // 设置从屏幕的那个地方开始布局
        layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        // 设置对话框的宽度是屏幕宽度的0.8
        layoutParams.width = (int) (screenWidth * 0.8);    //宽度设置为屏幕的0.8
        //layoutParams.height = (int) (screenHeight*0.5);
        // 拿到设置宽度后的对话框宽度
        int DialogWidth = layoutParams.width;
        // 计算对话框距离屏幕左边的距离
        int x = screenWidth / 2 - DialogWidth / 2;
        // 把距离设置给对话框
        layoutParams.x = x;
        layoutParams.y = 100;
        // 设置透明度
        //layoutParams.alpha =0.7f;
        getWindowManager().updateViewLayout(decorView, layoutParams);
    }

}

