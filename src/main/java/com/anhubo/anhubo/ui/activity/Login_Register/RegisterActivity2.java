package com.anhubo.anhubo.ui.activity.Login_Register;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Register_Com_Bean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.HomeActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.BuildingActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.BusinessActivity;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/27.
 */
public class RegisterActivity2 extends BaseActivity {
    private static final int REQUESTCODE1 = 1;
    private static final int REQUESTCODE2 = 2;
    @InjectView(R.id.et_reg2_building)
    EditText etReg2JianZhu;
    @InjectView(R.id.et_reg2_floorName)
    EditText etReg2floorName;
    @InjectView(R.id.et_reg2_area)
    EditText etReg2QuYu;
    @InjectView(R.id.et_reg2_business)// 单位名称
            EditText etReg2DanWei;
    @InjectView(R.id.btn_register2)
    Button btnRegister2;
    private String floorName;
    private String areaName;
    private String code;
    private String msg;
    private String uid;
    private String buildingName;
    private String buildingId;
    private String businessName;
    private String businessId;

    @Override
    protected void initConfig() {
        super.initConfig();

    }

    @Override
    protected int getContentViewId() {
        return R.layout.register_activity2;
    }

    @Override
    protected void initViews() {

    }


    @Override
    protected void onLoadDatas() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case REQUESTCODE1:
                    if (resultCode == 1) {
                        String stringExtra = data.getStringExtra(Keys.STR);
                        if (!TextUtils.isEmpty(stringExtra)) {
                            etReg2JianZhu.setText(stringExtra);
                        }
                    }
                    break;
                case REQUESTCODE2:
                    if (resultCode == 2) {
                        String stringExtra = data.getStringExtra(Keys.STR);
                        if (!TextUtils.isEmpty(stringExtra)) {
                            etReg2DanWei.setText(stringExtra);
                        }
                    }
                    break;
            }
        }
    }

    @OnClick({R.id.et_reg2_building, R.id.et_reg2_business, R.id.btn_register2})
    public void onClick(View view) {
        getInputData();
        switch (view.getId()) {
            case R.id.et_reg2_building://建筑
                // 建筑的点击事件，地图poi
                Intent intent = new Intent(mActivity, BuildingActivity.class);
                startActivityForResult(intent, REQUESTCODE1);
                break;
            case R.id.et_reg2_business://单位名称
                // 单位的点击事件，地图poi
                Intent intent2 = new Intent(mActivity, BusinessActivity.class);
                startActivityForResult(intent2, REQUESTCODE2);
                break;
            case R.id.btn_register2://完成的点击事件
                /*if (TextUtils.isEmpty(floorName)) {
                    ToastUtils.showToast(mActivity, "请选择建筑");
                    return;
                }*/
                if (TextUtils.isEmpty(floorName)) {
                    ToastUtils.showToast(mActivity, "请输入楼层");
                    return;
                }
                /*if (TextUtils.isEmpty(floorName)) {
                    ToastUtils.showToast(mActivity, "请选择单位");
                    return;
                }*/
                registerComPlete();
                break;
        }
    }

    /**
     * 注册完成的点击事件
     */
    private void registerComPlete() {
        String url = Urls.Url_RegCom;
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("building_name", "腾讯众创空间");//建筑物名称
        params.put("floor_name", floorName);//楼层数
        params.put("area_name", areaName);//区域名称
        params.put("business_name", "安互保"); //单位名称
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("RegisterActivity2+++===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            Register_Com_Bean bean = new Gson().fromJson(response, Register_Com_Bean.class);
            if (bean != null) {
                // 拿到数据，开始存储数据，并跳转到主界面
                code = bean.code;
                msg = bean.msg;
                Register_Com_Bean.Data data = bean.data;
                String newUid = data.uid;
                buildingName = data.building_name;
                buildingId = data.building_id;
                businessName = data.business_name;
                businessId = data.business_id;
                // 定义方法把数据存在本地，最好把方法写在工具类里面，便于复用
                SpUtils.putParam(mActivity, Keys.UID, newUid);
                SpUtils.putParam(mActivity, Keys.BUSINESSID, businessId);
                SpUtils.putParam(mActivity, Keys.BULIDINGID, buildingId);
                SpUtils.putParam(mActivity, Keys.BUILDINGNAME, buildingName);
                SpUtils.putParam(mActivity, Keys.BUSINESSNAME, businessName);
                //跳转到主页面
                enterHome();
            } else {
                System.out.println("RegisterActivity2界面+++===没拿到bean对象");
            }
        }
    }


    private void enterHome() {
        Intent intent = new Intent(RegisterActivity2.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // 发送一条广播，登录完成后关闭登录的所有界面
        mActivity.sendBroadcast(new Intent(INTENT_FINISH));
    }

    /**
     * 获取输入的内容
     */
    private void getInputData() {
        // 在这儿先获取到uid
        uid = getIntent().getStringExtra(Keys.UID);
        //获取输入的楼层
        floorName = etReg2floorName.getText().toString().trim();
        //第二次区域
        areaName = etReg2QuYu.getText().toString().trim();
    }
}
