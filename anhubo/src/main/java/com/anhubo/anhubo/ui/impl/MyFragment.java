package com.anhubo.anhubo.ui.impl;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.bean.MyFragmentBean;
import com.anhubo.anhubo.entity.RxBus;
import com.anhubo.anhubo.entity.event.Exbus_AlterName;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.Login_Register.Login_Message;
import com.anhubo.anhubo.ui.activity.MyDetial.InvateActivity;
import com.anhubo.anhubo.ui.activity.MyDetial.OrderManagerActivity;
import com.anhubo.anhubo.ui.activity.MyDetial.PersonMsgActivity;
import com.anhubo.anhubo.ui.activity.MyDetial.SettingActivity;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/10/8.
 */
public class MyFragment extends BaseFragment {


    private static final String TAG = "MyFragment";
    private LinearLayout llMyDetial;
    private LinearLayout llInvate;
    private LinearLayout llOrderManager;
    private LinearLayout llSetting;
    private Button btnLogOut;
    private CircleImageView image;
    public static GlideDrawable mBitmap;
    private String img;
    private String age;
    private String sex;
    private String name;
    private TextView tvMyName;
    private TextView tvMyGebder;
    private TextView tvMyAge;
    private MyFragmentBean bean;
    public static String name_new;
    public static String gender_new;
    public static String age_new;
    private Subscription rxSubscription;

    @Override
    public void initTitleBar() {
        //设置菜单键隐藏
        iv_basepager_left.setVisibility(View.GONE);
//        tv_basepager_title.setText("我的界面");
    }

    @Override
    public Object getContentView() {
        return R.layout.fragment_my;
    }

    @Override
    public void initView() {
        llTop.setVisibility(View.GONE);
        // 找控件
        image = findView(R.id.profile_image);
        tvMyName = findView(R.id.tv_my_name);
        tvMyGebder = findView(R.id.tv_my_gebder);
        tvMyAge = findView(R.id.tv_my_age);
        llMyDetial = findView(R.id.ll_My_Detial);
        llInvate = findView(R.id.ll_invate);
        llOrderManager = findView(R.id.ll_orderManager);
        llSetting = findView(R.id.ll_setting);
        btnLogOut = findView(R.id.btn_logOut);
    }

    @Override
    public void initListener() {
        // 设置监听
        llMyDetial.setOnClickListener(this);
        llInvate.setOnClickListener(this);
        llOrderManager.setOnClickListener(this);
        llSetting.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        // RxBus
        rxBusOnClickListener();
    }
    // 订阅修改信息的事件
    private void rxBusOnClickListener() {
        // rxSubscription是一个Subscription的全局变量，这段代码可以在onCreate/onStart等生命周期内
        rxSubscription = RxBus.getDefault().toObservable(Exbus_AlterName.class)
                .subscribe(new Action1<Exbus_AlterName>() {
                               @Override
                               public void call(Exbus_AlterName alterName) {
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


    @Override
    public void initData() {
        /**我的界面第一次请求网络*/
        getData();
    }

    /**
     * 我的界面第一次请求网络
     */
    private void getData() {
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        String url = Urls.Url_My_GetUserInfo;
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            LogUtils.e(TAG, ":getData:", e);
            String response = SpUtils.getStringParam(mActivity, "headIcon");
            setData(response);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":getData:", response);
            SpUtils.putParam(mActivity, "headIcon", response);
            setData(response);

        }
    }

    /**
     * 设置头像的显示内容
     */
    private void setData(String response) {
        bean = JsonUtil.json2Bean(response, MyFragmentBean.class);
        if (bean != null) {
            age = bean.data.age;
            sex = bean.data.sex;
            img = bean.data.img;
            name = bean.data.name;
        }
        /**设置头像、姓名、年龄、性别的显示内容*/
        if (!TextUtils.isEmpty(img)) {
            setHeaderIcon(img);
        }
        if (!TextUtils.isEmpty(name)) {
            tvMyName.setText(name);
        }
        if (!TextUtils.isEmpty(age)) {
            tvMyAge.setText(age);
        }
        if (!TextUtils.isEmpty(sex)) {
            tvMyGebder.setText(sex);
        }
    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.ll_My_Detial:
                enterPersonMsg();
                break;
            case R.id.ll_invate:
                // 邀请
                enterInvate();
                break;
            case R.id.ll_orderManager:
                // 订单管理
                enterOrder();
                break;
            case R.id.ll_setting:
                //设置
                enterSetting();
                break;
            case R.id.btn_logOut:
                new AlertDialog(mActivity).builder()
                        .setTitle("提示")
                        .setMsg("您确定要退出登录吗？")
                        .setPositiveButton("退出", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                logOut();
                            }
                        })
                        .setNegativeButton("", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setCancelable(false).show();

                break;
            default:
                break;
        }

    }

    /**
     * 设置头像的方法
     */
    private void setHeaderIcon(final String imgurl) {

        Glide.with(this)
                .load(imgurl)
                .centerCrop()
                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .into(new GlideDrawableImageViewTarget(image) {
            @Override
            public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                super.onResourceReady(drawable, anim);
                //在这里添加一些图片加载完成的操作
                mBitmap = drawable;
            }
        });

    }

    /**
     * 进入个人信息界面
     */
    private void enterPersonMsg() {
        Intent intent = new Intent(mActivity, PersonMsgActivity.class);
        intent.putExtra(Keys.MYBEAN, bean);
        startActivity(intent);
    }

    /**
     * 进入设置界面
     */
    private void enterSetting() {
        startActivity(new Intent(mActivity, SettingActivity.class));
    }

    /**
     * 进入订单管理界面
     */
    private void enterOrder() {
        Intent intent = new Intent(mActivity, OrderManagerActivity.class);
        startActivity(intent);
    }

    /**
     * 进入邀请界面
     */
    private void enterInvate() {
        Intent intent = new Intent(mActivity, InvateActivity.class);
        startActivity(intent);
    }

    /**
     * 登出方法
     */
    private void logOut() {
        // 清除消息
        SpUtils.putParam(mActivity, Keys.UID, null);
        SpUtils.putParam(mActivity, Keys.BUSINESSID, null);
        SpUtils.putParam(mActivity, Keys.BULIDINGID, null);
        SpUtils.putParam(mActivity, Keys.BUILDINGNAME, null);
        SpUtils.putParam(mActivity, Keys.BUSINESSNAME, null);
        /**清除微信头像*/
        SpUtils.putParam(mActivity, Keys.HEADERICON_WEIXIN, null);

        /**修改过单位*/
        SpUtils.putParam(mActivity, Keys.ISALTERUNIT, false);
        SpUtils.putParam(mActivity, Keys.NEWBUSINESSNAME, null);
        /**保存版本号*/
        SpUtils.putParam(mActivity, Keys.VERSIONNAME, null);
        /**学习和检查的小圆点*/
        SpUtils.putParam(mActivity, Keys.STUDY_TIME, null);
        SpUtils.putParam(mActivity, Keys.CHECK_TIME, null);


        //跳转到登录页面
        startActivity(new Intent(mActivity, Login_Message.class));
        getActivity().finish();
    }
}
