package com.anhubo.anhubo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Alter_MateUnitBean;
import com.anhubo.anhubo.bean.ExtrasBean;
import com.anhubo.anhubo.bean.UploadRegistration_Id_Bean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.unitDetial.AreaBindingActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.BusinessActivity;
import com.anhubo.anhubo.ui.impl.BuildFragment;
import com.anhubo.anhubo.ui.impl.FindFragment;
import com.anhubo.anhubo.ui.impl.MyFragment;
import com.anhubo.anhubo.ui.impl.UnitFragment;
import com.anhubo.anhubo.utils.FragmentSwitchTool;
import com.anhubo.anhubo.utils.JPushManager;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * 这是首页的主界面
 */

public class HomeActivity extends BaseActivity {

    private static final int UNIT_REGISTER = 0;
    private static final String TAG = "HomeActivity";

    private LinearLayout llChat, llFriends, llContacts, llSettings;
    private ImageView ivChat, ivFriends, ivContacts, ivSettings;
    private TextView tvChat, tvFriends, tvContacts, tvSettings;
    private FragmentSwitchTool tool;
    private ArrayList<Fragment> list;
    private long exitTime = 0;
    public static boolean isForeground = false;
    private View view;

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

        String businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
        if (TextUtils.isEmpty(businessId)) {
            view = View.inflate(mActivity, R.layout.home_zhezhao, null);
            addContentView(view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            View rlZheZhao = view.findViewById(R.id.rl_unit_zhezhao);
            rlZheZhao.setOnClickListener(null);
            TextView tvZhezhao = (TextView) view.findViewById(R.id.tv_unit_zhezhao);
            // 设置下划线
            Utils.setUnderline(tvZhezhao);
            tvZhezhao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, BusinessActivity.class);
                    intent.putExtra(Keys.UNIT_ZHEZHAO, "zhezhao");
                    startActivityForResult(intent, UNIT_REGISTER);
                }
            });
        }

        initView();
        tool = new FragmentSwitchTool(getSupportFragmentManager(), R.id.flContainer);
        tool.setClickableViews(llChat, llFriends, llContacts, llSettings);
        tool.addSelectedViews(new View[]{ivChat, tvChat}).addSelectedViews(new View[]{ivFriends, tvFriends})
                .addSelectedViews(new View[]{ivContacts, tvContacts}).addSelectedViews(new View[]{ivSettings, tvSettings});
        tool.setFragments(UnitFragment.class, BuildFragment.class, FindFragment.class, MyFragment.class);

        tool.changeTag(llChat);


    }
    private void initView() {
        llChat = (LinearLayout) findViewById(R.id.llChat);
        llFriends = (LinearLayout) findViewById(R.id.llFriends);
        llContacts = (LinearLayout) findViewById(R.id.llContacts);
        llSettings = (LinearLayout) findViewById(R.id.llSettings);

        ivChat = (ImageView) findViewById(R.id.ivChat);
        ivFriends = (ImageView) findViewById(R.id.ivFriends);
        ivContacts = (ImageView) findViewById(R.id.ivContacts);
        ivSettings = (ImageView) findViewById(R.id.ivSettings);

        tvChat = (TextView) findViewById(R.id.tvChat);
        tvFriends = (TextView) findViewById(R.id.tvFriends);
        tvContacts = (TextView) findViewById(R.id.tvContacts);
        tvSettings = (TextView) findViewById(R.id.tvSettings);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case UNIT_REGISTER:
                    if (resultCode == 1) {
                        if (view != null) {
                            // 隐藏遮罩层
                            view.setVisibility(View.GONE);
                        }
                        if (updateFragmentUIFromActivity != null) {
                            updateFragmentUIFromActivity.UIChange();
                        }
                    }
                    break;
            }
        }
    }

    public void setUpdateFragmentUIFromActivity(UpdateFragmentUIFromActivity updateFragmentUIFromActivity) {
        this.updateFragmentUIFromActivity = updateFragmentUIFromActivity;
    }

    public UpdateFragmentUIFromActivity updateFragmentUIFromActivity;

    public interface UpdateFragmentUIFromActivity {
        void UIChange();

        void changeUnit(String string, String str);
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
            // 清理掉区域绑定的缓存
            SpUtils.putParam(mActivity, AreaBindingActivity.FLOOR, null);
            SpUtils.putParam(mActivity, AreaBindingActivity.MESSAGE, null);
//            SpUtils.putParam(mActivity, AreaBindingActivity.DOT_MESSAGE, null);
            String floorOld = SpUtils.getStringParam(mActivity, AreaBindingActivity.FLOOR);
            if (TextUtils.isEmpty(floorOld)) {
                LogUtils.eNormal(TAG, "缓存清理了");
            } else {
                LogUtils.eNormal(TAG, "缓存没清理");
            }
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
                        // 弹窗提示修改单位
                        dialog(messge, tableId);
                    }
                }

            } else if (MESSAGE_REGISTRATION_ID.equals(intent.getAction())) {
                String registration_id = intent.getStringExtra(REGISTRATION_ID);
                LogUtils.eNormal(TAG, "registration_id***MyReceiver*****++" + registration_id);
                String uid = SpUtils.getStringParam(mActivity, Keys.UID);
                LogUtils.eNormal(TAG, "uid***MyReceiver*****++" + uid);
                String url = Urls.Url_Registration_Id;
                HashMap<String, String> params = new HashMap<>();
                // 把这个registration_id上传到服务器
                params.put("uid", uid);
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
        public void onError(Request request, Exception e) {
            LogUtils.e(TAG, ":Registration_Id上传", e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":Registration_Id上传", response);
            UploadRegistration_Id_Bean bean = JsonUtil.json2Bean(response, UploadRegistration_Id_Bean.class);
            int code = bean.code;
            String msg = bean.msg;
            if (code == 0) {
                LogUtils.eNormal(TAG + ":", "Registration_Id上传成功");
            }
        }

    }

    // 弹窗提示修改单位
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
        public void onError(Request request, Exception e) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
            LogUtils.e(TAG, ":alterUnit", e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":alterUnit", response);
            Alter_MateUnitBean bean = JsonUtil.json2Bean(response, Alter_MateUnitBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                Alter_MateUnitBean.Data data = bean.data;
                String businessId = data.business_id;
                String businessName = data.business_name;
                if (code == 0) {
                    SpUtils.putParam(mActivity, Keys.BUSINESSID, businessId);
                    SpUtils.putParam(mActivity, Keys.BUSINESSNAME, businessName);
                    if (updateFragmentUIFromActivity != null) {
                        if (view != null) {
                            // 如果是在遮罩层弹窗，同意更改单位后如果遮罩层显示则隐藏
                            view.setVisibility(View.GONE);
                        }
                        updateFragmentUIFromActivity.changeUnit(businessName, businessId);
                    }
                }
            }
        }
    }
}
