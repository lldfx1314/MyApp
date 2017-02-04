package com.anhubo.anhubo.ui.impl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.bean.MyFragmentBean;
<<<<<<< HEAD
import com.anhubo.anhubo.entity.RxBus;
import com.anhubo.anhubo.entity.event.Exbus_AlterName;
import com.anhubo.anhubo.entity.event.Exbus_ShowIcon;
=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.Login_Register.Login_Message;
import com.anhubo.anhubo.ui.activity.MyDetial.InvateActivity;
import com.anhubo.anhubo.ui.activity.MyDetial.OrderManagerActivity;
import com.anhubo.anhubo.ui.activity.MyDetial.PersonMsgActivity;
import com.anhubo.anhubo.ui.activity.MyDetial.SettingActivity;
<<<<<<< HEAD
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
=======
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.google.gson.Gson;
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
<<<<<<< HEAD
import rx.Subscription;
import rx.functions.Action1;
=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce

/**
 * Created by Administrator on 2016/10/8.
 */
public class MyFragment extends BaseFragment {


<<<<<<< HEAD
    private static final String TAG = "MyFragment";
=======
    private static final int REQUESTCODE = 0;
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
    private LinearLayout llMyDetial;
    private LinearLayout llInvate;
    private LinearLayout llOrderManager;
    private LinearLayout llSetting;
    private TextView tvLogOut;
    private CircleImageView image;
<<<<<<< HEAD
    public static GlideDrawable mBitmap;
=======
    public static Bitmap mBitmap;
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
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
<<<<<<< HEAD
    private Subscription rxSubscription;
=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce

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
        tvLogOut = findView(R.id.tv_logOut);
    }

    @Override
    public void initListener() {
        // 设置监听
        llMyDetial.setOnClickListener(this);
        llInvate.setOnClickListener(this);
        llOrderManager.setOnClickListener(this);
        llSetting.setOnClickListener(this);
        tvLogOut.setOnClickListener(this);
<<<<<<< HEAD
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
=======
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 每次界面可见的时候请求网络获取数据
        getDataInternet(isVisibleToUser);
    }

    private void getDataInternet(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            /**我的界面第一次请求网络*/
            getData();
        }
    }

    @Override
    public void initData() {
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
    }

    /**
     * 我的界面第一次请求网络
     */
    private void getData() {
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        String url = Urls.Url_My_GetUserInfo;
<<<<<<< HEAD
=======

>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

<<<<<<< HEAD
            LogUtils.e(TAG, ":getData:", e);
=======
            System.out.println("MyFragment+++===界面失败" + e.getMessage());
            //ToastUtils.showToast(mActivity, "网络有问题，请检查");
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
            String response = SpUtils.getStringParam(mActivity, "headIcon");
            setData(response);
        }

        @Override
        public void onResponse(String response) {
<<<<<<< HEAD
            LogUtils.eNormal(TAG + ":getData:", response);
            SpUtils.putParam(mActivity, "headIcon", response);
=======
            //System.out.println("MyFragment界面+++="+response);
            SpUtils.putParam(mActivity, "headIcon", response);

>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
            setData(response);

        }
    }

    /**
     * 设置头像的显示内容
     */
    private void setData(String response) {
<<<<<<< HEAD
        bean = JsonUtil.json2Bean(response, MyFragmentBean.class);
=======
        bean = new Gson().fromJson(response, MyFragmentBean.class);
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
        if (bean != null) {
            age = bean.data.age;
            sex = bean.data.sex;
            img = bean.data.img;
            name = bean.data.name;
        }
        /**设置头像、姓名、年龄、性别的显示内容*/
<<<<<<< HEAD
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
=======
        // 头像
        if (!TextUtils.isEmpty(img)) {
            // 用户自己设置过头像，就显示自己的头像
            setHeaderIcon(img);
        }

        //姓名、年龄、性别
        tvMyName.setText(name);
        tvMyAge.setText(age);
        tvMyGebder.setText(sex);
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
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
            case R.id.tv_logOut:
<<<<<<< HEAD
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

=======
                logOut();
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
                break;
            default:
                break;
        }

    }

<<<<<<< HEAD
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

=======
    /***/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null && requestCode == REQUESTCODE) {
            switch (resultCode) {
                case 1:// 保存头像后返回的url，显示更改后的头像
                    String imgurl = intent.getStringExtra(Keys.HEADERICON);
                    if (!TextUtils.isEmpty(imgurl)) {
                        setHeaderIcon(imgurl);
                    }
                    break;
                case 2:// 更改姓名后返回的url，显示更改后的姓名
                    String newName = intent.getStringExtra(Keys.NEWNAME);
                    if (!TextUtils.isEmpty(newName)) {
                        name_new = newName;

                        tvMyName.setText(newName);
                    }
                    break;
                case 3:// 更改年龄后返回的url，显示更改后的年龄
                    String newAge = intent.getStringExtra(Keys.NEWAGE);
                    if (!TextUtils.isEmpty(newAge)) {
                        age_new = newAge;

                        tvMyAge.setText(newAge);
                    }
                    break;

                case 4:// 更改性别后返回的url，显示更改后的性别
                    String newGender = intent.getStringExtra(Keys.NEWGENDER);
                    if (!TextUtils.isEmpty(newGender)) {
                        gender_new = newGender;
                        tvMyGebder.setText(newGender);
                    }
                    break;
                case 5:// 更改头像后返回的微信url，显示更改后的微信头像
                    String headericon_weixin = intent.getStringExtra(Keys.HEADERICON_WEIXIN);
                    // 这里得把微信的头像地址保存起来
                    if (!TextUtils.isEmpty(headericon_weixin)) {
                        setHeaderIcon(headericon_weixin);
                    }
                    break;
            }
        }

    }

    /**
     * 设置头像的方法
     */
    private void setHeaderIcon(String imgurl) {
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

                        System.out.println("MyFragment设置头像头像+++===" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mBitmap = bitmap;
                        image.setImageBitmap(bitmap);
                    }
                });
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
    }

    /**
     * 进入个人信息界面
     */
    private void enterPersonMsg() {
        Intent intent = new Intent(mActivity, PersonMsgActivity.class);
        intent.putExtra(Keys.MYBEAN, bean);
<<<<<<< HEAD
        startActivity(intent);
=======
        startActivityForResult(intent, REQUESTCODE);
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
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
