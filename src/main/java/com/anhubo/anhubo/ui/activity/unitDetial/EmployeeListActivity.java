package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.EmployeeListAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.EmployeeListBean;
import com.anhubo.anhubo.bean.Unit_Invate_WorkMateBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by LUOLI on 2017/1/11.
 */
public class EmployeeListActivity extends BaseActivity {
    private static final String TAG = "EmployeeListActivity";
    @InjectView(R.id.lv_employ)
    ListView lvEmploy;
    @InjectView(R.id.employee_num)
    TextView employeeNum;
    private String versionName;
    private Dialog showDialog;
    private String uid;
    private String businessId;
    private ArrayList<EmployeeListBean.Data.User_info> list;
    private EmployeeListAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_employeelist;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("员工列表");
        ivTopBarRight.setVisibility(View.VISIBLE);
        ivTopBarRight.setOnClickListener(this);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
        super.initEvents();
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);

        list = new ArrayList<>();
        adapter = new EmployeeListAdapter(mActivity, list);
        lvEmploy.setAdapter(adapter);

        // 请求数据
        getData();
    }

    /**
     * 请求数据
     */
    private void getData() {
        final Dialog showDialog = loadProgressDialog.show(mActivity, "正在加载...");
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("business_id", businessId);
        OkHttpUtils.post()
                .url(Urls.Url_EmployList)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        showDialog.dismiss();
                        LogUtils.e(TAG, ":getData", e);
                    }

                    @Override
                    public void onResponse(String response) {
                        showDialog.dismiss();
                        LogUtils.eNormal(TAG + ":getData", response);
                        // 处理数据
                        dealData(response);
                    }
                });
    }

    /**
     * 处理数据
     */
    private void dealData(String response) {
        if (!TextUtils.isEmpty(response)) {
            EmployeeListBean bean = JsonUtil.json2Bean(response, EmployeeListBean.class);
            int code = bean.code;
            String msg = bean.msg;
            EmployeeListBean.Data data = bean.data;
            String userNum = data.user_num;
            if (!TextUtils.isEmpty(userNum)) {
                int i = Integer.parseInt(userNum) - 1;
                employeeNum.setText(i+"人");
            }
            List<EmployeeListBean.Data.User_info> userInfo = data.user_info;
            list.addAll(userInfo);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivTopBarRight_add:
                dialog();
                break;
        }
    }

    private void dialog() {
        final AlertDialog alertDialog = new AlertDialog(mActivity);
        alertDialog
                .builder()
                .setTitle("提示")
                .setEditHint("请输入电话号码")
                .setCancelable(false)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String string = alertDialog.et_msg.getText().toString().trim();
                        if (!TextUtils.isEmpty(string)) {
                            boolean b = Utils.judgePhoneNumber(string);
                            if (!b) {
                                ToastUtils.showToast(mActivity, "号码输入不正确，请重新输入");
                                return;
                            } else {
                                // 拿着号码和uid请求网络
                                invateWorkMate(string);
                            }

                        }
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    /**
     * 邀请同事网络请求
     */
    private void invateWorkMate(String phone) {
        showDialog = loadProgressDialog.show(mActivity, "正在请求...");
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        String[] split = Utils.getAppInfo(mActivity).split("#");
        versionName = split[1];

        String url = Urls.Url_Unit_InvateWorkMate;
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("phone", phone);
        params.put("version", versionName);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback2());
    }


    class MyStringCallback2 extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG, "+:invateWorkMate", e);
        }

        @Override
        public void onResponse(String response) {
            showDialog.dismiss();
            LogUtils.eNormal(TAG + ":invateWorkMate", response);
            Unit_Invate_WorkMateBean bean = JsonUtil.json2Bean(response, Unit_Invate_WorkMateBean.class);
            int code = bean.code;
            String msg = bean.msg;
            String tableId = bean.data.table_id;
            if (code == 0 && !TextUtils.isEmpty(tableId)) {
                // 邀请成功，等待服务器给被邀请同事发消息就行了
                ToastUtils.showToast(mActivity, "邀请成功");
            } else if (code == 1) {
                ToastUtils.showToast(mActivity, msg);
            }

        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
