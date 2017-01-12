package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.EmployeeListAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.EmployeeListBean;
import com.anhubo.anhubo.bean.EmployeeOperate;
import com.anhubo.anhubo.bean.Unit_Invate_WorkMateBean;
import com.anhubo.anhubo.interfaces.InterClick;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Created by LUOLI on 2017/1/11.
 */
public class EmployeeListActivity extends BaseActivity implements InterClick {
    private static final String TAG = "EmployeeListActivity";
    @InjectView(R.id.rl_employee_adm)
    RelativeLayout rlEmployeeAdm;
    @InjectView(R.id.lv_employ)
    ListView lvEmploy;
    @InjectView(R.id.employee_num)
    TextView employeeNum;
    private String versionName;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                if(userType == 1){
                    // 有管理员,显示管理员信息
                    isHaveAdm = true;
                    rlEmployeeAdm.setVisibility(View.VISIBLE);
                    setHeaderIcon(ivIcon,picPath);
                    tvName.setText(username);
                    // 显示的员工人数-1
                    if (!TextUtils.isEmpty(userNum)) {
                        int m = Integer.parseInt(userNum) - 1;
                        employeeNum.setText(m + "人");
                    }
                    if(status == 1){
                        // 是管理员本人，显示转让按钮
                        isAdm = true;
                        btnEmployee.setVisibility(View.VISIBLE);
                        btnEmployee.setText("转让");
                    }
                    // 把管理员从列表里面删除
                    userInfo.remove(i);
                }

            }
            if(!isHaveAdm){
                // 没有管理员，显示员工人数
                if (!TextUtils.isEmpty(userNum)) {
                    employeeNum.setText(userNum + "人");
                }
            }
            list.clear();
            list.addAll(userInfo);
            adapter = new EmployeeListAdapter(mActivity, list,EmployeeListActivity.this, isAdm);
            lvEmploy.setAdapter(adapter);
        }
    }

    @Override
    protected void onLoadDatas() {
        btnEmployee.setOnClickListener(this);
    }

    /**转让管理员*/
    private void assignmentAdministrator() {
        Intent intent = new Intent(mActivity, AssignmentAdminActivity.class);
        intent.putExtra(Keys.EMPLOYEELISTBEAN, bean);
        startActivity(intent);
    }
    /**listView条目的点击事件*/
    @Override
    public void onBtnClick(View v) {
        EmployeeOperate operateg = (EmployeeOperate)v.getTag();
        int mPosition = operateg.position;
        String mOperate = operateg.operate;
        if(TextUtils.equals(mOperate,"del")){
            // 弹窗提示用户是否删除用户
            dialogDel(mPosition);
        }else if(TextUtils.equals(mOperate,"quit")){
            // 弹窗提示用户是否退出按钮
            dialogQuit(mPosition);
        }

    }

    private void dialogDel(int mPosition) {
        ToastUtils.showToast(mActivity,"删除+"+mPosition);
    }

    private void dialogQuit(int mPosition) {
        ToastUtils.showToast(mActivity,"退出+"+mPosition);
    }

    /**
     * 设置头像的方法
     */
    private void setHeaderIcon(final CircleImageView ivIcon, String imgurl) {
        OkHttpUtils
                .get()//
                .url(imgurl)//
                .tag(this)//
                .build()//
                .connTimeOut(15000)
                .readTimeOut(15000)
                .writeTimeOut(15000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        LogUtils.e(TAG, ":setHeaderIcon:", e);
                    }

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        ivIcon.setImageBitmap(bitmap);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        uid =  SpUtils.getStringParam(mActivity, Keys.UID);
        switch (v.getId()) {
            case R.id.ivTopBarRight_add:
                dialog();
                break;
            case R.id.btn_employee:
                // 转让管理员
                assignmentAdministrator();
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
