package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.EmployeeListAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.AssignmentAdminBean;
import com.anhubo.anhubo.bean.EmployeeListBean;
import com.anhubo.anhubo.bean.EmployeeOperate;
import com.anhubo.anhubo.bean.Unit_Invate_WorkMateBean;
import com.anhubo.anhubo.entity.RxBus;
import com.anhubo.anhubo.entity.event.Exbus_EmployeeList;
import com.anhubo.anhubo.interfaces.InterClick;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.HomeActivity;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.FooterListview;
import com.bumptech.glide.Glide;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by LUOLI on 2017/1/11.
 */
public class EmployeeListActivity extends BaseActivity implements InterClick {
    private static final String TAG = "EmployeeListActivity";
    @InjectView(R.id.rl_employee_adm)
    RelativeLayout rlEmployeeAdm;
    @InjectView(R.id.tv_employee_adm)
    TextView tvEmployeeAdm;
    @InjectView(R.id.lv_employ)
    FooterListview lvEmploy;
    @InjectView(R.id.tv_employee)
    TextView tvEmployee;
    @InjectView(R.id.employee_num)
    TextView employeeNum;
    @InjectView(R.id.btn_employee_invate)
    Button btnEmployeeInvate;
    private Dialog showDialog;
    private String businessId;
    private ArrayList<EmployeeListBean.Data.User_info> list;
    private EmployeeListAdapter adapter;
    private CircleImageView ivIcon;
    private TextView tvName;
    private Button btnEmployee;
    private View viewEmployee;
    private String uid;
    private boolean isAdm;
    private boolean isHaveAdm;
    private EmployeeListBean bean;
    private SpannableString ss;
    private Subscription rxSubscription;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_employeelist;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("员工列表");
//        ivTopBarRight.setVisibility(View.VISIBLE);
//        ivTopBarRight.setOnClickListener(this);
    }

    @Override
    protected void initViews() {
        ivIcon = (CircleImageView) findViewById(R.id.iv_employee_icon);
        tvName = (TextView) findViewById(R.id.tv_employee_name);
        btnEmployee = (Button) findViewById(R.id.btn_employee);
        viewEmployee = findViewById(R.id.view_employee);
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
        list = new ArrayList<>();
        // 请求数据
        getData();
    }

    @Override
    protected void onLoadDatas() {
        btnEmployee.setOnClickListener(this);
        btnEmployeeInvate.setOnClickListener(this);
        // RxBus
        rxBusOnClickListener();
    }
    // 订阅修改信息的事件
    private void rxBusOnClickListener() {
        // rxSubscription是一个Subscription的全局变量，这段代码可以在onCreate/onStart等生命周期内
        rxSubscription = RxBus.getDefault().toObservable(Exbus_EmployeeList.class)
                .subscribe(new Action1<Exbus_EmployeeList>() {
                               @Override
                               public void call(Exbus_EmployeeList employeeList) {
                                   getData();
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                // TODO: 处理异常
                            }
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 解除订阅
        if(!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
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
                    public void onError(Request request, Exception e) {
                        showDialog.dismiss();
                        LogUtils.e(TAG, ":getData", e);
                        new AlertDialog(mActivity).builder()
                                .setTitle("提示")
                                .setMsg("网络有问题，请检查")
                                .setCancelable(true).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        showDialog.dismiss();
                        LogUtils.eNormal(TAG + ":getData", response);
                        tvEmployee.setVisibility(View.VISIBLE);// 显示员工标记
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
            bean = JsonUtil.json2Bean(response, EmployeeListBean.class);
            int code = bean.code;
            String msg = bean.msg;
            EmployeeListBean.Data data = bean.data;
            String userNum = data.user_num;

            List<EmployeeListBean.Data.User_info> userInfo = data.user_info;
            for (int i = 0; i < userInfo.size(); i++) {
                EmployeeListBean.Data.User_info info = userInfo.get(i);
                int userType = info.user_type;
                int status = info.status;
                String picPath = info.pic_path;
                String username = info.username;
                if (userType == 1) {
                    // 有管理员,显示管理员信息
                    isHaveAdm = true;
                    // 显示管理员布局
                    tvEmployeeAdm.setVisibility(View.VISIBLE);
                    rlEmployeeAdm.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(picPath)) {
                        setHeaderIcon(ivIcon, picPath);
                    } else {
                        ivIcon.setImageResource(R.drawable.newicon);
                    }
                    tvName.setText(username);
                    // 显示的员工人数-1
                    if (!TextUtils.isEmpty(userNum)) {
                        int m = Integer.parseInt(userNum) - 1;
                        setHanZiColor(m + "人");
                        employeeNum.setHorizontallyScrolling(true);
                        employeeNum.setText(ss);
                    }

                    if (status == 1) {
                        if (Integer.parseInt(userNum) > 1) {
                            // 是管理员本人，并且人数大于1，显示转让按钮
                            isAdm = true;
                            btnEmployee.setVisibility(View.VISIBLE);
                            btnEmployee.setText("转让");
                        } else {
                            isAdm = false;
                            btnEmployee.setVisibility(View.GONE);
                        }
                    } else {
                        isAdm = false;
                        btnEmployee.setVisibility(View.GONE);
                    }
                    // 把管理员从列表里面删除
                    userInfo.remove(i);
                }

            }
            if (!isHaveAdm) {
                // 没有管理员，显示员工人数
                if (!TextUtils.isEmpty(userNum)) {
                    setHanZiColor(userNum + "人");
                    employeeNum.setHorizontallyScrolling(true);
                    employeeNum.setText(ss);
                }
            }
            list.clear();
            list.addAll(userInfo);
            adapter = new EmployeeListAdapter(mActivity, list, EmployeeListActivity.this, isAdm);
            lvEmploy.setAdapter(adapter);
        }
    }

    /**
     * 设置人字数字的颜色
     */
    private void setHanZiColor(String string) {

        ss = new SpannableString(string);
        MyURLSpan myURLSpan = new MyURLSpan(string);
        ss.setSpan(myURLSpan, 0, string.length()-1, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    class MyURLSpan extends URLSpan {


        public MyURLSpan(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.parseColor("#7393f4"));
//            ds.setTextSize(DisplayUtil.sp2px(mActivity, 8));
        }
    }
    /**
     * 转让管理员
     */
    private void assignmentAdministrator() {
        Intent intent = new Intent(mActivity, AssignmentAdminActivity.class);
        intent.putExtra(Keys.EMPLOYEELISTBEAN, bean);
        startActivity(intent);
    }

    /**
     * 这个方法是实现InterClick接口
     * listView条目里按钮的点击事件
     */
    @Override
    public void onBtnClick(View v) {
        EmployeeOperate operateg = (EmployeeOperate) v.getTag();
        int mPosition = operateg.position;
        String mOperate = operateg.operate;
        if (TextUtils.equals(mOperate, "del")) {
            // 弹窗提示用户是否删除用户
            dialogDel(mPosition);
        } else if (TextUtils.equals(mOperate, "quit")) {
            // 弹窗提示用户是否退出按钮
            dialogQuit(mPosition);
        }

    }

    private void dialogDel(int mPosition) {
        ToastUtils.showToast(mActivity, "删除+" + mPosition);
    }

    private void dialogQuit(final int mPosition) {
        // 退出企业
        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("确认退出当前企业 ")
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        quitBusiness(mPosition);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setCancelable(false).show();

    }

    /**
     * 退出企业
     */
    private void quitBusiness(int mPosition) {
        final Dialog showDialog = loadProgressDialog.show(mActivity, "正在退出...");
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("business_id", businessId);
        OkHttpUtils.post()
                .url(Urls.Url_Quit_Business)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        showDialog.dismiss();
                        LogUtils.e(TAG, ":quitBusiness", e);
                        new AlertDialog(mActivity).builder()
                                .setTitle("提示")
                                .setMsg("网络有问题，请检查")
                                .setCancelable(true).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        showDialog.dismiss();
                        LogUtils.eNormal(TAG + ":quitBusiness", response);
                        AssignmentAdminBean bean = JsonUtil.json2Bean(response, AssignmentAdminBean.class);
                        if (bean != null) {
                            int code = bean.code;
                            if (code == 0) {
                                SpUtils.putParam(mActivity, Keys.BUSINESSID, null);
                                SpUtils.putParam(mActivity, Keys.BUSINESSNAME, null);
                                SpUtils.putParam(mActivity, Keys.BUILDINGNAME, null);
                                /**是否修改过单位，都置于false*/
                                SpUtils.putParam(mActivity, Keys.ISALTERUNIT, false);
                                // 跳转到HomeActivity里面
                                startActivity(new Intent(mActivity, HomeActivity.class));
                                // 发送一条广播,关掉之前所有界面
                                mActivity.sendBroadcast(new Intent(INTENT_FINISH));
                            } else {
                                ToastUtils.showToast(mActivity, "退出失败");
                            }
                        }
                    }
                });
    }

    /**
     * 设置头像的方法
     */
    private void setHeaderIcon(final CircleImageView ivIcon, String imgurl) {
        Glide
                .with(mActivity)
                .load(imgurl)
                .centerCrop().crossFade(800).into(ivIcon);
    }

    @Override
    public void onClick(View v) {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        switch (v.getId()) {
            case R.id.btn_employee_invate:
                // 邀请同事
                dialog();
                break;
            case R.id.btn_employee:
                // 转让管理员
                assignmentAdministrator();
                break;
        }
    }

    /**
     * 邀请同事
     */
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
        public void onError(Request request, Exception e) {
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
