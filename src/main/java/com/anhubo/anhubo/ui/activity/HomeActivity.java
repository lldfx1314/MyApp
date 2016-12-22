package com.anhubo.anhubo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.HomeAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Alter_MateUnitBean;
import com.anhubo.anhubo.bean.ExtrasBean;
import com.anhubo.anhubo.bean.UploadRegistration_Id_Bean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.impl.BuildFragment;
import com.anhubo.anhubo.ui.impl.FindFragment;
import com.anhubo.anhubo.ui.impl.MyFragment;
import com.anhubo.anhubo.ui.impl.UnitFragment;
import com.anhubo.anhubo.utils.JPushManager;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.NoScrollViewPager;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

/**
 * 这是首页的主界面
 */

public class HomeActivity extends BaseActivity {

    @InjectView(R.id.viewpager)
    NoScrollViewPager viewpager;

    @InjectView(R.id.rg_home_bottom)
    RadioGroup rgHomeBottom;
    private ArrayList<Fragment> list;
    private long exitTime = 0;
    public static boolean isForeground = false;

    @Override
    protected void initConfig() {
        super.initConfig();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initViews() {
        // 极光推送
        JPushManager.newInstence(mActivity).initJPush();
        registerMessageReceiver();     // 注册广播  用来处理接收到消息后弹出对话框提示用户
    }

    @Override
    protected void onResume() {
        isForeground = true;
        JPushInterface.onResume(this);
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        JPushInterface.onPause(this);
        super.onPause();
    }

    @Override
    protected void initEvents() {


        // 初始化集合
        list = new ArrayList();
        list.add(new UnitFragment());
        list.add(new BuildFragment());
        list.add(new FindFragment());
        list.add(new MyFragment());
        // 设置适配器
        viewpager.setAdapter(new HomeAdapter(getSupportFragmentManager(), list));
        // 关联底部RadioButton的五个button和ViewPager的关联
        rgHomeBottom.setOnCheckedChangeListener(new onCheckedChangeListener());
        //默认选中首页
        rgHomeBottom.check(R.id.rb_bottom_unit);//参数是默认的ID
        viewpager.setOffscreenPageLimit(0);

    }



    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    class onCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_bottom_unit:
                    viewpager.setCurrentItem(0, false);//false表示点击时没有滑动效果
                    break;
                case R.id.rb_bottom_build:
                    viewpager.setCurrentItem(1, false);
                    break;
                case R.id.rb_bottom_find:
                    viewpager.setCurrentItem(2, false);
                    break;
                case R.id.rb_bottom_my:
                    viewpager.setCurrentItem(3, false);
                    break;

            }
        }
    }


    /**
     * 设置再点击一次退出应用
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //dialog();
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.showLongToast(mActivity, "再按一次退出程序");

            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public static final String MESSAGE_REGISTRATION_ID = "MESSAGE_REGISTRATION_ID";
    public static final String REGISTRATION_ID = "registration_id";

    /**
     * 注册广播
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        filter.addAction(MESSAGE_REGISTRATION_ID);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                // 弹窗提示用户收到推送的消息
                ExtrasBean bean = new Gson().fromJson(extras, ExtrasBean.class);
                String tableId = bean.table_id;
                if (tableId != null) {
                    if (!TextUtils.isEmpty(messge) && !TextUtils.isEmpty(tableId)) {
                        dialog(messge, tableId);
                    }
                }

            }else if(MESSAGE_REGISTRATION_ID.equals(intent.getAction())){
                String registration_id = intent.getStringExtra(REGISTRATION_ID);
                String deviceId = Utils.getDeviceId(mActivity);
                System.out.println("registration_id***MyReceiver*****++" + registration_id);
                System.out.println("deviceId***MyReceiver*****++" + deviceId);
                String uid = SpUtils.getStringParam(mActivity, Keys.UID);
                System.out.println("uid***MyReceiver*****++" + uid);
                String url = Urls.Url_Registration_Id;
                HashMap<String, String> params = new HashMap<>();
                // 把这个registration_id上传到服务器
                params.put("uid", uid);
                params.put("device_token", deviceId);
                params.put("registration_id", registration_id);

                OkHttpUtils.post()//
                        .url(url)//
                        .params(params)//
                        .build()//
                        .execute(new MyStringCallback1());

            }
        }
    }
    /**
     * 上传Registration_Id
     */
    class MyStringCallback1 extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {

            System.out.println("WelcomeActivity界面+++上传Registration_Id===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("上传Registration_Id+" + response);
            UploadRegistration_Id_Bean bean = new Gson().fromJson(response, UploadRegistration_Id_Bean.class);
            int code = bean.code;
            String msg = bean.msg;
            if (code == 0) {
                //System.out.println("上传Registration_Id+++msg上传成功+++" + msg);
            }
        }

    }

    private void dialog(String messge, final String tableId) {
        new AlertDialog(mActivity).builder()
                .setTitle("通知")
                .setMsg(messge)
                .setCancelable(false)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //同事修改单位
                        alterUnit(tableId);
                    }
                }).setNegativeButton("忽略", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    /**
     * 同事修改单位
     */
    private void alterUnit(String tableId) {
        String url = Urls.Url_Unit_AlterUnit;
        Map<String, String> params = new HashMap<>();
        params.put("table_id", tableId);
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    /**
     * 同事修改单位
     */
    class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
            System.out.println("HomeActivity，界面同事修改单位+" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("同事修改单位+" + response);
            Alter_MateUnitBean bean = new Gson().fromJson(response, Alter_MateUnitBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                Alter_MateUnitBean.Data data = bean.data;
                String businessId = data.business_id;
                String businessName = data.business_name;
                String buildingId = data.building_id;
                String buildingName = data.building_name;
                SpUtils.putParam(mActivity, Keys.BUSINESSID, businessId);
                SpUtils.putParam(mActivity, Keys.BUSINESSNAME, businessName);
                SpUtils.putParam(mActivity, Keys.BULIDINGID, buildingId);
                SpUtils.putParam(mActivity, Keys.BUILDINGNAME, buildingName);
            }
        }
    }
}
