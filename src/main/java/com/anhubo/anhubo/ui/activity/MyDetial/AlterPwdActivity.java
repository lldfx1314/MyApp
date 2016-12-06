package com.anhubo.anhubo.ui.activity.MyDetial;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MyAlterPwdBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/10/31.
 */
public class AlterPwdActivity extends BaseActivity {

    @InjectView(R.id.et_alter_oldPwd)
    EditText etAlterOldPwd;
    @InjectView(R.id.et_alter_newPwd1)
    EditText etAlterNewPwd1;
    @InjectView(R.id.et_alter_newPwd2)
    EditText etAlterNewPwd2;
    @InjectView(R.id.tv_sure_alter)
    TextView tvSureAlter;
    private String uid;
    private String oldPwd;
    private String newPwd1;
    private String newPwd2;

    @Override
    protected void initConfig() {
        super.initConfig();
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_alterpwd;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("修改密码");
    }

    @Override
    protected void initEvents() {
        super.initEvents();

    }

    @Override
    protected void onLoadDatas() {

    }

    @OnClick(R.id.tv_sure_alter)
    public void onClick(View v) {
        /**获取输入的内容*/
        getInputData();
        switch (v.getId()) {
            case R.id.tv_sure_alter:
                // 对密码做判断
                if (!TextUtils.isEmpty(oldPwd)) {
                    boolean isoldPwd = Utils.isRightPwd(oldPwd);
                    if (!isoldPwd) {
                        new AlertDialog(mActivity).builder()
                                .setTitle("提示")
                                .setMsg("原密码输入不合法")
                                .setCancelable(true).show();
                        //ToastUtils.showToast(mActivity, "原密码输入不合法");
                        return;
                    }
                } else {
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("请输入原密码")
                            .setCancelable(true).show();
                    //ToastUtils.showToast(mActivity, "请输入原密码");
                    return;
                }

                if (!TextUtils.isEmpty(newPwd1)) {
                    boolean isnewPwd1 = Utils.isRightPwd(newPwd1);
                    if (!isnewPwd1) {
                        new AlertDialog(mActivity).builder()
                                .setTitle("提示")
                                .setMsg("请输入8-16位数字和字母的组合")
                                .setCancelable(true).show();
                        //ToastUtils.showToast(mActivity, "请输入8-16位数字和字母的组合");
                        return;
                    }
                } else {
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("请输入新密码")
                            .setCancelable(true).show();
                    //ToastUtils.showToast(mActivity, "请输入新密码");
                    return;
                }

                if (!TextUtils.isEmpty(newPwd2)) {
                    if (!TextUtils.equals(newPwd1, newPwd2)) {
                        new AlertDialog(mActivity).builder()
                                .setTitle("提示")
                                .setMsg("两次输入不一致")
                                .setCancelable(true).show();
                        //ToastUtils.showToast(mActivity, "两次输入不一致");
                        return;
                    }
                } else {
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("请再次输入新密码")
                            .setCancelable(true).show();
                    //ToastUtils.showToast(mActivity, "请再次输入新密码");
                    return;
                }
                /**走网络，修改密码*/
                alterPwd(oldPwd,newPwd2);
                break;
            default:
                break;
        }

    }
    /**走网络，修改密码*/
    private void alterPwd(String oldPwd, String newPwd2) {
        // 走网络，提交性别
        progressBar.setVisibility(View.VISIBLE);
        String url = Urls.Url_My_AlterPwd;
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("password", newPwd2);
        params.put("pass_old", oldPwd);
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }
    private Handler handler = new Handler();

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    class MyStringCallback extends StringCallback{
        @Override
        public void onError(Call call, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");

            System.out.println("AlterPwdActivity+++界面修改密码===" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println(response);
            MyAlterPwdBean bean = new Gson().fromJson(response, MyAlterPwdBean.class);
            if(bean!=null){
                progressBar.setVisibility(View.GONE);
                int code = bean.code;
                String msg = bean.msg;
                if(code != 0){
                    // 没修改成功
                    ToastUtils.showToast(mActivity,msg);
                }else{
                    // 修改成功
                    ToastUtils.showToast(mActivity,"密码修改成功");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },2000);
                }
            }
        }
    }

    /**
     * 获取输入的内容
     */
    private void getInputData() {
        oldPwd = etAlterOldPwd.getText().toString().trim();
        newPwd1 = etAlterNewPwd1.getText().toString().trim();
        newPwd2 = etAlterNewPwd2.getText().toString().trim();
    }
}
