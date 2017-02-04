package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.AssignmentAdminAdapter;
import com.anhubo.anhubo.bean.AssignmentAdminBean;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.EmployeeListBean;
import com.anhubo.anhubo.interfaces.InterClick;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.FooterListview;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2017/1/12.
 */
public class AssignmentAdminActivity extends BaseActivity implements InterClick {


    private static final String TAG = "AssignmentAdminActivity";
    @InjectView(R.id.tv_ass_admin_num)
    TextView tvAssAdminNum;
    @InjectView(R.id.lv_ass_admin)
    FooterListview lvAssAdmin;
    @InjectView(R.id.btn_ass_admin_ok)
    Button btnAssAdminOk;
    private String uid;
    private String businessId;
    private ArrayList<EmployeeListBean.Data.User_info> list;
    private AssignmentAdminAdapter adapter;
    private String newUid;
    private String username;
    private SpannableString ss;

    @Override
    protected void initConfig() {
        super.initConfig();
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_assignmentadmin;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("选择管理员");
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        list = new ArrayList<>();
        adapter = new AssignmentAdminAdapter(mActivity, list, AssignmentAdminActivity.this);
        lvAssAdmin.setAdapter(adapter);
        Intent intent = getIntent();
        EmployeeListBean bean = (EmployeeListBean) intent.getSerializableExtra(Keys.EMPLOYEELISTBEAN);
        // 处理bean对象
        int count = dealBean(bean);
        // 创建map
        creatMap(count);
        //监听条目的点击事件
        lvAssAdmin.setOnItemClickListener(onItemClickListener);
    }


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            EmployeeListBean.Data.User_info info = list.get(position);
            username = info.username;
            // 获取新的uid
            newUid = info.uid;

            LogUtils.eNormal(TAG + ":新的uid", newUid);
            adapter.setSelectedPosition(position);
            adapter.notifyDataSetInvalidated();
        }
    };

    private Map<Integer, Boolean> creatMap(int count) {
        HashMap<Integer, Boolean> map = new HashMap<>();
        for (int i = 0; i < count; i++) {
            map.put(i, false);
        }
        return map;
    }

    /**
     * 处理bean对象
     */
    private int dealBean(EmployeeListBean bean) {
        if (bean != null) {
            EmployeeListBean.Data data = bean.data;
            String userNum = data.user_num;

            List<EmployeeListBean.Data.User_info> userInfo = data.user_info;
            for (int i = 0; i < userInfo.size(); i++) {
                EmployeeListBean.Data.User_info info = userInfo.get(i);
                int userType = info.user_type;
                if (userType == 1) {
                    // 把管理员从列表里面删除
                    userInfo.remove(i);
                }

            }
            // 没有管理员，显示员工人数
//            tvAssAdminNum.setText(userInfo.size() + "人");
            setHanZiColor(userInfo.size() + "人");
            tvAssAdminNum.setHorizontallyScrolling(true);
            tvAssAdminNum.setText(ss);
            list.clear();
            list.addAll(userInfo);
            adapter.notifyDataSetChanged();
        }
        return list.size();
    }

    /**
     * 设置人字颜色
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

    @Override
    protected void initViews() {

    }

    @Override
    protected void onLoadDatas() {
//        btnAssAdminOk.setOnClickListener(this);
    }

    @OnClick(R.id.btn_ass_admin_ok)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ass_admin_ok:
                // 确认提交按钮
                confirm();
//                ToastUtils.showToast(mActivity, "uid+" + uid + "+newUId+" + newUid + "+bu+" + businessId);
                break;
        }
    }

    Handler handler = new Handler();

    /**
     * 确认提交按钮
     */
    private void confirm() {
        if (TextUtils.isEmpty(newUid)) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("请先选择员工")
                    .setCancelable(true).show();
            return;
        }
        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("确认将 " + username + " 设置为管理员吗？")
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submit();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setCancelable(false).show();

    }

    private void submit() {
        final Dialog showDialog = loadProgressDialog.show(mActivity, "正在提交...");
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("business_id", businessId);
        params.put("new_uid", newUid);
        OkHttpUtils.post().url(Urls.Url_Assi_Admin)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        showDialog.dismiss();
                        LogUtils.e(TAG, ":confirm", e);
                        new AlertDialog(mActivity).builder()
                                .setTitle("提示")
                                .setMsg("网络有问题，请检查")
                                .setCancelable(true).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        showDialog.dismiss();
                        LogUtils.eNormal(TAG + ":confirm", response);
                        AssignmentAdminBean bean = JsonUtil.json2Bean(response, AssignmentAdminBean.class);
                        if (bean != null) {
                            int code = bean.code;
                            if (code == 0) {
                                ToastUtils.showToast(mActivity, "转让成功");
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 500);
                            } else {
                                ToastUtils.showToast(mActivity, "转让失败");
                            }
                        }
                    }
                });
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }


    @Override
    public void onBtnClick(View v) {

    }


}
