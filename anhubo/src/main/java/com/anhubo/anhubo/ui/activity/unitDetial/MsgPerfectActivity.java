package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MsgPerfectBean;
import com.anhubo.anhubo.bean.MsgPerfectMemberBean;
import com.anhubo.anhubo.bean.MsgPerfectUseProBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.DatePackerUtil;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.PopOneHelper;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/17.
 */
public class MsgPerfectActivity extends BaseActivity {
    private static final int MSGPERFECT01 = 1;
    private static final int MSGPERFECT02 = 2;
    private static final int MSGPERFECT03 = 3;
    private static final int MSGPERFECT04 = 4;
    private static final String TAG = "MsgPerfectActivity";
    private static final int MSGPERFECT05 = 5;
    @InjectView(R.id.iv_msg_per01)
    ImageView ivMsgPer01;
    @InjectView(R.id.iv_msg_per02)
    ImageView ivMsgPer02;
    @InjectView(R.id.iv_msg_per03)
    ImageView ivMsgPer03;
    @InjectView(R.id.iv_msg_per04)
    ImageView ivMsgPer04;
    @InjectView(R.id.tv_msg_per05)
    TextView tvMsgPer05;
    @InjectView(R.id.ll_msg_per05)
    LinearLayout llMsgPer05;
    @InjectView(R.id.tv_msg_per06)
    TextView tvMsgPer06;
    private int employNum;
    private int threeQ;
    private int lower;
    private int notice;
    private int rent;
    private String property1;
    private PopOneHelper popOneHelper;
    private String businessId;
    private Dialog showDialogPlace;

    @Override
    protected void initConfig() {
        super.initConfig();
        // 获取business_id
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID).trim();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_msgperfect;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("完善信息");
        // 修改员工数
        alterEmploy();
    }

    // 修改员工数
    private void alterEmploy() {
        popOneHelper = new PopOneHelper(this);
        popOneHelper.setListItem(DatePackerUtil.getList());
        popOneHelper.setOnClickOkListener(new PopOneHelper.OnClickOkListener() {
            @Override
            public void onClickOk(String str) {
                tvMsgPer05.setText(str);
                // 走网络，提交员工数
                String url = Urls.Url_MsgPerfect_Member;
                HashMap<String, String> params = new HashMap<>();
                params.put("business_id", businessId);
                params.put("employ_num", str);
                OkHttpUtils.post()//
                        .url(url)//
                        .params(params)//
                        .build()//
                        .execute(new MyStringCallback1());

            }
        });
    }

    /**
     * 员工数的网络强求
     */
    class MyStringCallback1 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(false).show();
            LogUtils.e(TAG, ":alterEmploy:", e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":alterEmploy:", response);
            MsgPerfectMemberBean bean = JsonUtil.json2Bean(response, MsgPerfectMemberBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                if (code == 0) {
                    ToastUtils.showToast(mActivity, msg);
                }

            }
        }
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        // 刚进来走网络获取数据
        getData();

    }

    //　获取初始数据
    private void getData() {
        String url = Urls.Url_MsgPerfect;
        HashMap<String, String> params = new HashMap<>();
        String businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
        params.put("business_id", businessId);
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    /**
     * 首次加载网络
     */

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            LogUtils.e(TAG, ":getData:", e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":getData:", response);
            MsgPerfectBean bean = JsonUtil.json2Bean(response, MsgPerfectBean.class);
            if (bean != null) {

                // 营业执照
                threeQ = bean.data.three_q;
                //法人
                lower = bean.data.lower;
                //消防审批
                notice = bean.data.notice;
                //租房合同
                rent = bean.data.rent;
                // 员工数
                employNum = bean.data.employ_num;//员工数
                // 场所性质
                property1 = bean.data.property1;
                isShowView();
            }
        }
    }

    @Override
    protected void onLoadDatas() {

    }


    private void isShowView() {

        if (threeQ == 1) {
            ivMsgPer01.setVisibility(View.VISIBLE);
        } else {
            ivMsgPer01.setVisibility(View.GONE);
        }
        if (lower == 1) {
            ivMsgPer02.setVisibility(View.VISIBLE);
        } else {
            ivMsgPer02.setVisibility(View.GONE);
        }
        if (notice == 1) {
            ivMsgPer03.setVisibility(View.VISIBLE);
        } else {
            ivMsgPer03.setVisibility(View.GONE);
        }
        if (rent == 1) {
            ivMsgPer04.setVisibility(View.VISIBLE);
        } else {
            ivMsgPer04.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(employNum + "")) {
            tvMsgPer05.setVisibility(View.VISIBLE);
            tvMsgPer05.setText(employNum + "");
        } else {
            tvMsgPer05.setText(0);
        }

        if (!TextUtils.isEmpty(property1)) {
            tvMsgPer06.setVisibility(View.VISIBLE);
            tvMsgPer06.setText(property1);
        } else {
            tvMsgPer06.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (null != intent) {
            switch (requestCode) {
                case MSGPERFECT01:
                    if (resultCode == 1) {
                        boolean result1_boolean = intent.getBooleanExtra(Keys.ISCLICK1, false);
                        if (result1_boolean) {
                            ivMsgPer01.setVisibility(View.VISIBLE);
                        } else {
                            ivMsgPer01.setVisibility(View.GONE);

                        }
                    }

                    break;
                case MSGPERFECT02:
                    if (resultCode == 2) {
                        boolean result2_boolean = intent.getBooleanExtra(Keys.ISCLICK2, false);
                        if (result2_boolean) {
                            ivMsgPer02.setVisibility(View.VISIBLE);
                        } else {
                            ivMsgPer02.setVisibility(View.GONE);

                        }
                    }

                    break;
                case MSGPERFECT03:
                    if (resultCode == 3) {
                        boolean result3_boolean = intent.getBooleanExtra(Keys.ISCLICK3, false);
                        if (result3_boolean) {
                            ivMsgPer03.setVisibility(View.VISIBLE);
                        } else {
                            ivMsgPer03.setVisibility(View.GONE);

                        }
                    }

                    break;
                case MSGPERFECT04:
                    if (resultCode == 4) {
                        boolean result4_boolean = intent.getBooleanExtra(Keys.ISCLICK4, false);
                        if (result4_boolean) {
                            ivMsgPer04.setVisibility(View.VISIBLE);
                        } else {
                            ivMsgPer04.setVisibility(View.GONE);

                        }
                    }

                    break;
                case MSGPERFECT05:
                    if (resultCode == 5) {
                        String string = intent.getStringExtra(Keys.PLACEUSE);
                        if (!TextUtils.isEmpty(string)) {

                            tvMsgPer06.setVisibility(View.VISIBLE);
                            tvMsgPer06.setText(string);
                            uploadPlaceUse(string);
                        }
                    }

                    break;
            }

        }

    }



    // 上传场所使用性质
    private void uploadPlaceUse(String string) {
        showDialogPlace = loadProgressDialog.show(mActivity, "正在加载...");
        // 拿着数据去走网络传到服务器
        Map<String, String> params = new HashMap<>();
        params.put("business_id", businessId);
        params.put("property1", string);

        String url = Urls.Url_UpLoading06;
        OkHttpUtils.post()//
                .url(url)
                .params(params)//
                .build()//
                .execute(new MyStringCallback3());
    }

    /**
     * 场所使用性质
     */
    class MyStringCallback3 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            showDialogPlace.dismiss();
            ToastUtils.showToast(mActivity, "网络有问题，请检查");
            LogUtils.e(TAG, ":uploadPlaceUse", e);
        }

        @Override
        public void onResponse(String response) {
            showDialogPlace.dismiss();
            LogUtils.eNormal(TAG + ":uploadPlaceUse", response);
            MsgPerfectUseProBean bean = JsonUtil.json2Bean(response, MsgPerfectUseProBean.class);
            int code = bean.code;
            if (code != 0) {
                ToastUtils.showToast(mActivity, "上传失败");
                tvMsgPer06.setVisibility(View.GONE);

            } else {
                ToastUtils.showToast(mActivity, "上传成功");
            }
        }
    }


    @OnClick({R.id.ll_msg_per01, R.id.ll_msg_per02, R.id.ll_msg_per03, R.id.ll_msg_per04, R.id.ll_msg_per05, R.id.ll_msg_per06})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_msg_per01:
                startActivityForResult(new Intent(mActivity, UploadingActivity1.class), MSGPERFECT01);
                break;
            case R.id.ll_msg_per02:
                startActivityForResult(new Intent(mActivity, UploadingActivity2.class), MSGPERFECT02);
                break;
            case R.id.ll_msg_per03:
                startActivityForResult(new Intent(mActivity, UploadingActivity3.class), MSGPERFECT03);
                break;
            case R.id.ll_msg_per04:
                startActivityForResult(new Intent(mActivity, UploadingActivity4.class), MSGPERFECT04);
                break;
            case R.id.ll_msg_per05:
                popOneHelper.show(llMsgPer05);
                break;
            case R.id.ll_msg_per06:
                startActivityForResult(new Intent(mActivity, PlaceUsePropertyActivity.class), MSGPERFECT05);
                break;
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
