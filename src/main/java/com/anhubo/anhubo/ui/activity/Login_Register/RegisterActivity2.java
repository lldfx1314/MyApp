package com.anhubo.anhubo.ui.activity.Login_Register;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Register_Com_Bean;
import com.anhubo.anhubo.protocol.RequestResultListener;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.HomeActivity;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.NetUtil;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;

import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/9/27.
 */
public class RegisterActivity2 extends BaseActivity {
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


    @OnClick({R.id.et_reg2_building, R.id.et_reg2_business, R.id.btn_register2})
    public void onClick(View view) {
        getInputData();
        switch (view.getId()) {
            case R.id.et_reg2_building://建筑
                // 建筑的点击事件，地图poi

                break;
            case R.id.et_reg2_business://单位名称
                // 单位的点击事件，地图poi

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
    /**注册完成的点击事件*/
    private void registerComPlete() {
        String url = Urls.Url_RegCom;
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("building_name", "腾讯众创空间");//建筑物名称
        params.put("floor_name", floorName);//楼层数
        params.put("area_name", areaName);//区域名称
        params.put("business_name", "安互保"); //单位名称
        MyRequestResultListener requestResultListener = new MyRequestResultListener();
        NetUtil.requestData(url,params, Register_Com_Bean.class, requestResultListener,0);
    }
    class MyRequestResultListener implements RequestResultListener<Register_Com_Bean> {



        @Override
        public void onRequestFinish(Register_Com_Bean bean, int what) {

            if(bean!=null){
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
                SpUtils.putParam(mActivity,Keys.UID,newUid);
                SpUtils.putParam(mActivity,Keys.BUSINESSID,businessId);
                SpUtils.putParam(mActivity,Keys.BULIDINGID,buildingId);
                SpUtils.putParam(mActivity,Keys.BUILDINGNAME,buildingName);
                SpUtils.putParam(mActivity,Keys.BUSINESSNAME,businessName);
                //跳转到主页面
                enterHome();
            }else {
                System.out.println("RegisterActivity2界面+++===没拿到bean对象");
            }
        }

        @Override
        public void showJsonStr(String str, int what) {
            //System.out.println("RegisterActivity2界面+++==="+str);
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
