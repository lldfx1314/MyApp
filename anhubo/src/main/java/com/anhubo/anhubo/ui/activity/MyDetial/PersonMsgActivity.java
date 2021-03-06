package com.anhubo.anhubo.ui.activity.MyDetial;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MyAlterAgeBean;
import com.anhubo.anhubo.bean.MyAlterGenderBean;
import com.anhubo.anhubo.bean.MyAlterNameBean;
import com.anhubo.anhubo.bean.MyFragmentBean;
import com.anhubo.anhubo.bean.My_HeaderIconBean;
import com.anhubo.anhubo.bean.PersonMsgBindBean;
import com.anhubo.anhubo.entity.RxBus;
import com.anhubo.anhubo.entity.event.Exbus_AlterName;
import com.anhubo.anhubo.entity.event.Exbus_ShowIcon;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.impl.MyFragment;
import com.anhubo.anhubo.utils.DatePackerUtil;
import com.anhubo.anhubo.utils.ImageFactory;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.PopBirthHelper;
import com.anhubo.anhubo.utils.PopGenderHelper;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.gson.Gson;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.okhttp.Request;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by LUOLI on 2016/10/28.
 */
public class PersonMsgActivity extends BaseActivity {
    private static final int PICTURE = 0;
    private static final int CAMERA = 1;
    private static final int REQUESTCODE = 2;
    private static final String TAG = "PersonMsgActivity";
    private static final int CROP_PHOTO = 3;
    @InjectView(R.id.ll_psHeaderIcon)
    LinearLayout llPsHeaderIcon;
    @InjectView(R.id.ll_psUsername)
    LinearLayout llPsUsername;
    @InjectView(R.id.ll_psAge)
    LinearLayout llPsAge;
    @InjectView(R.id.ll_psGender)
    LinearLayout llPsGender;
    @InjectView(R.id.ll_psPhone)
    LinearLayout llPsPhone;
    @InjectView(R.id.ll_pspwd)
    LinearLayout llPspwd;
    @InjectView(R.id.ll_psCertification)
    LinearLayout llPsCertification;
    @InjectView(R.id.ll_psUnit)
    LinearLayout llPsUnit;
    @InjectView(R.id.ll_psWeChat)
    LinearLayout llPsWeChat;
    @InjectView(R.id.iv_headerIcon)
    ImageView ivHeaderIcon;
    @InjectView(R.id.et_my_username)
    EditText etMyUsername;
    @InjectView(R.id.tv_my_age)
    TextView tvMyAge;
    @InjectView(R.id.tv_my_gender)
    TextView tvMyGender;
    @InjectView(R.id.tv_my_pwd)
    TextView tvMyPwd;
    @InjectView(R.id.tv_my_unit)
    TextView tvMyUnit;
    @InjectView(R.id.tv_my_wechat)
    TextView tvMyWechat;
    @InjectView(R.id.tv_my_phone)
    TextView tvMyPhone;
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private boolean isShow;
    private String age;
    private String sex;
    private String img;
    private String name;
    private String businessName;
    private String phone;
    private String qqName;
    private String weiboName;
    private String weixinName;
    private String uid;
    private Handler handler = new Handler();
    private PopBirthHelper popBirthHelper;
    private InputMethodManager imm;
    private String newName;
    private PopGenderHelper popGenderHelper;
    private String newGender;
    private String newAge;
    private String buildingName;
    private UMShareAPI mShareAPI;
    private Dialog showDialog;
    private Subscription rxSubscription;
    private Uri imageUri;
    private File filePhoto01;

    @Override
    protected void initConfig() {
        super.initConfig();
        Intent intent = getIntent();
        MyFragmentBean bean = (MyFragmentBean) intent.getSerializableExtra(Keys.MYBEAN);
        if (bean != null) {
            age = bean.data.age;
            sex = bean.data.sex;
            img = bean.data.img;
            name = bean.data.name;
            businessName = bean.data.business_name;
            buildingName = bean.data.building_name;
            phone = bean.data.phone;
            qqName = bean.data.qq_name;
            weiboName = bean.data.weibo_name;
            weixinName = bean.data.weixin_name;
        }


    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_personmsg;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("个人信息");

    }


    @Override
    protected void onLoadDatas() {
    }


    @Override
    protected void initEvents() {
        super.initEvents();

        //设置etMyUsername的点击事件
        etMyUsername.setOnTouchListener(new View.OnTouchListener() {
            //按住和松开的标识
            int touch_flag = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch_flag++;
                if (touch_flag == 2) {
                    alterName();
                }
                return false;
            }
        });

        // 给每个控件先设置初始内容
        setInitialData();

        GlideDrawable mBitmap = MyFragment.mBitmap;
        if (mBitmap != null) {
            ivHeaderIcon.setImageDrawable(mBitmap);
        }
        /**获取我的界面传过来的姓名，显示*/
        String name_new = MyFragment.name_new;
        if (!TextUtils.isEmpty(name_new)) {
            etMyUsername.setText(name_new);
        }
        /**获取我的界面传过来的年龄，显示*/
        String age_new = MyFragment.age_new;
        if (!TextUtils.isEmpty(age_new)) {
            tvMyAge.setText(age_new);
        }
        /**获取我的界面传过来的性别，显示*/
        String gender_new = MyFragment.gender_new;
        if (!TextUtils.isEmpty(gender_new)) {
            tvMyGender.setText(gender_new);
        }
        /**年龄弹窗的显示*/
        alterAge();
        /**性别弹窗*/
        alterGender();
    }

    @OnClick({R.id.ll_psHeaderIcon, R.id.ll_psUsername, R.id.et_my_username, R.id.ll_psAge, R.id.ll_psGender, R.id.ll_psPhone, R.id.ll_pspwd, R.id.ll_psCertification, R.id.ll_psUnit, R.id.ll_psWeChat})
    public void onClick(View view) {
        // 获取uid
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        //System.out.println("111uid是+++===" + uid);
        switch (view.getId()) {
            case R.id.ll_psHeaderIcon:
                /**弹出拍照对话框*/
                showDialog();
                break;
            case R.id.ll_psUsername:
                /**修改用户名*/
                alterName();
                break;
            case R.id.et_my_username:
                break;
            case R.id.ll_psAge:
                /**年龄弹窗*/
                popBirthHelper.show(llPsAge);
                break;
            case R.id.ll_psGender:
                /**性别弹窗*/
                popGenderHelper.show(llPsGender);
                break;
            case R.id.ll_psPhone:
                //手机号码
                break;
            case R.id.ll_pspwd:
                // 密码修改
                alterPwd();
                break;
            case R.id.ll_psCertification:
                // 实名认证
                certification();
                break;
            case R.id.ll_psUnit:
                // 所属单位
                alterUnit();
                break;
            case R.id.ll_psWeChat:
                //微信
                mShareAPI = UMShareAPI.get(mActivity);
                boolean authorize = mShareAPI.isAuthorize(mActivity, SHARE_MEDIA.WEIXIN);
//                if (!authorize) {
                mShareAPI.doOauthVerify(mActivity, SHARE_MEDIA.WEIXIN, umAuthListener);//授权
//                }else{
////                    ToastUtils.showToast(mActivity,"别点了，已经授权了");
//                }

                break;
            case R.id.btn_popDialog_takephoto:
                // 拍照
                camera();
                break;
            case R.id.btn_popDialog_photo:
                // 相册
                getPhoto();
                break;
        }
    }


    /**
     * 微信授权
     */

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            // 授权成功
            mShareAPI.getPlatformInfo(mActivity, SHARE_MEDIA.WEIXIN, umGetUserInfoListener);//获取用户信息
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            ToastUtils.showToast(mActivity, "绑定失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            ToastUtils.showToast(mActivity, "取消绑定");
        }
    };

    private String unionid;
    private String profileImageUrl;
    private String screenName;
    /**
     * 获取微信详细信息
     */
    private UMAuthListener umGetUserInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            if (map != null) {
                //转换为set

                Set<String> keySet = map.keySet();

                //遍历循环，得到里面的key值----用户名，头像....

                for (String string : keySet) {
                    //打印下
                    //System.out.println("==========11111111111=="+string);
                    // 打印完获取到的信息在下面
                   /* unionid profile_image_url country screen_name access_token city gender province
                    language expires_in refresh_token openid*/
                    //我需要的 uid unionid 头像 profile_image_url   姓名  screen_name
                }
                unionid = map.get("unionid");
                profileImageUrl = map.get("profile_image_url");
                screenName = map.get("screen_name");
            }
            // 绑定微信
            bindWeixin();


        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

        }
    };

    //绑定微信
    private void bindWeixin() {
        /**获取微信信息后走绑定接口*/
        String url = Urls.Url_BindWEIXIN;
        // 封装请求参数
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("uid", uid);
        params.put("third_type", 2 + "");// 2代表微信
        params.put("unique_name", screenName);
        params.put("unique_id", unionid);
        params.put("pic_url", profileImageUrl);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback5());
    }

    class MyStringCallback5 extends StringCallback {
        @Override
        public void onError(Request request, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");
            LogUtils.e(TAG, ":bindWeixin:", e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":bindWeixin:", response);
            PersonMsgBindBean bean = new Gson().fromJson(response, PersonMsgBindBean.class);
            if (bean != null) {
                int code = bean.code;
                if (code == 0) {
                    if (TextUtils.isEmpty(img)) {
                        // 代表用户没设置过自己的头像，因此显示自己的微信头像// 我的界面头像重新刷一遍
                        setHeaderIcon(profileImageUrl);
                        // 通知我的界面请求数据更新界面
                        RxBus.getDefault().post(new Exbus_AlterName());
                    }
                    // 显示微信名
                    tvMyWechat.setText(screenName);
                }

            }
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    /**
     * ****************************************************************************
     * 单位修改
     */
    private void alterUnit() {
        Intent intent = new Intent(mActivity, AlterUnitActivity.class);
        startActivityForResult(intent, REQUESTCODE);

    }

    /**
     * ****************************************************************************
     * 实名认证
     */
    private void certification() {
        Intent intent = new Intent(mActivity, CertificationActivity.class);
        startActivity(intent);

    }

    /**
     * ****************************************************************************
     * 密码修改
     */
    private void alterPwd() {
        Intent intent = new Intent(mActivity, AlterPwdActivity.class);
        startActivity(intent);
    }

    /**
     * ****************************************************************************
     * 性别弹窗
     */
    private void alterGender() {
        popGenderHelper = new PopGenderHelper(this);
        popGenderHelper.setListItem(DatePackerUtil.getListGender());
        popGenderHelper.setOnClickOkListener(new PopGenderHelper.OnClickOkListener() {

            @Override
            public void onClickOk(String str) {
                tvMyGender.setText(str);
                newGender = str;
                // 走网络，提交性别
                String url = Urls.Url_My_Gender;
                HashMap<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("sex", str);
                OkHttpUtils.post()//
                        .url(url)//
                        .params(params)//
                        .build()//
                        .execute(new MyStringCallback4());
            }
        });
    }

    /**
     * 上传性别
     */
    class MyStringCallback4 extends StringCallback {
        @Override
        public void onError(Request request, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");
            LogUtils.e(TAG, ":alterGender:", e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":alterGender:", response);
            MyAlterGenderBean bean = JsonUtil.json2Bean(response, MyAlterGenderBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, msg);
                } else {
                    // 通知我的界面请求数据更新界面
                    RxBus.getDefault().post(new Exbus_AlterName());
                    ToastUtils.showToast(mActivity, "性别修改成功");
                }
            }
        }
    }

    /**
     * ****************************************************************************
     * 年龄弹窗的显示
     */
    private void alterAge() {
        popBirthHelper = new PopBirthHelper(mActivity);
        popBirthHelper.setOnClickOkListener(new PopBirthHelper.OnClickOkListener() {
            @Override
            public void onClickOk(String time) {

                try {

                    //此处是获得的年龄
                    //由出生日期获得年龄***
                    newAge = String.valueOf(getAge(parse(time)));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Integer.parseInt(newAge) >= 0) {
                    tvMyAge.setText(newAge);
                    // 拿到年龄,上传到网络
                    uploadAge(time);
                } else {
                    //new ConfirmPopWindow(mActivity).showDialog(llPsAge,"年龄","您所选日期大于当前时间");
                    ToastUtils.showToast(mActivity, "您所选日期大于当前时间");
                }

            }


        });
    }

    /**
     * 把年龄上传到网络
     *
     * @param time
     */
    private void uploadAge(String time) {
        // 走网络，提交性别
        String url = Urls.Url_My_Age;
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("birthday", time);
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback3());
    }

    class MyStringCallback3 extends StringCallback {
        @Override
        public void onError(Request request, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");
            LogUtils.e(TAG, ":uploadAge:", e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":uploadAge:", response);
            MyAlterAgeBean bean = JsonUtil.json2Bean(response, MyAlterAgeBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, "年龄修改失败");
                } else {

                    // 通知我的界面请求数据更新界面
                    RxBus.getDefault().post(new Exbus_AlterName());
                    ToastUtils.showToast(mActivity, "年龄修改成功");
                }
            }
        }
    }

    //出生日期字符串转化成Date对象
    public Date parse(String strDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.parse(strDate);
    }


    //由出生日期获得年龄
    public int getAge(Date birthDay) {
        //获取当前系统时间
        Calendar cal = Calendar.getInstance();
        //如果出生日期大于当前时间，则抛出异常

        try {

            //取出系统当前时间的年、月、日部分
            int yearNow = cal.get(Calendar.YEAR);
            int monthNow = cal.get(Calendar.MONTH);
            int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

            //将日期设置为出生日期
            cal.setTime(birthDay);
            //取出出生日期的年、月、日部分
            int yearBirth = cal.get(Calendar.YEAR);
            int monthBirth = cal.get(Calendar.MONTH);
            int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            //当前年份与出生年份相减，初步计算年龄
            int age = yearNow - yearBirth;
            //当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
            if (monthNow <= monthBirth) {
                //如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
                if (monthNow == monthBirth) {
                    if (dayOfMonthNow < dayOfMonthBirth) age--;
                } else {
                    age--;
                }
            }
            return age;
        } catch (Exception e) {
            if (cal.before(birthDay)) {
                ToastUtils.showToast(mActivity, "您所选日期大于当前时间");
            }
            return 0;
        }
    }


    /**
     * 给每个控件先设置初始内容
     */
    private void setInitialData() {
        // 记录是否修改过单位，以便于从我的界面进来后决定是否修改单位显示内容，因为修改完返回到我的界面是不请求网络的，此时要是进入PersonMsgActivity界面，单位显示的必然是修改之前的单位
        boolean isalterUnit = SpUtils.getBooleanParam(mActivity, Keys.ISALTERUNIT, false);
        String newBusinessName = SpUtils.getStringParam(mActivity, Keys.NEWBUSINESSNAME);

        if (!TextUtils.isEmpty(name)) {
            etMyUsername.setText(name);
        }
        if (!TextUtils.isEmpty(age)) {
            tvMyAge.setText(age);
        }
        if (!TextUtils.isEmpty(sex)) {
            tvMyGender.setText(sex);
        }
        if (!TextUtils.isEmpty(phone)) {
            tvMyPhone.setText(phone);
        }
        if (!isalterUnit) {
            // 没修改过，显示之前的单位
            if (!TextUtils.isEmpty(businessName)) {
                tvMyUnit.setText(businessName);
            }
        } else {
            // 修改过，显示修改的单位
            if (!TextUtils.isEmpty(newBusinessName)) {
                tvMyUnit.setText(newBusinessName);
            }
        }

        if (!TextUtils.isEmpty(weixinName)) {
            tvMyWechat.setText(weixinName);
        }
    }


    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            // 上传更改后的姓名
            uploadName();
        }
    };

    /**
     * ****************************************************************************
     * 修改用户名
     */
    private void alterName() {
        // 先弹出键盘,让焦点在输入框上

        etMyUsername.requestFocus(); // 获取焦点
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        if (imm.isActive()) {
            // 获取焦点,先设置光标遇到最后，然后监听输入框的动态变化
            etMyUsername.setSelection(etMyUsername.length());
            listenetMyUsername();
        }
    }

    /**
     * 监听输入框的动态变化
     */
    private void listenetMyUsername() {
        etMyUsername.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                newName = s.toString();
                //延迟2s，如果不再输入字符，则执行该线程的run方法，取消输入框的焦点
                handler.postDelayed(delayRun, 1000);
            }
        });
    }

    /**
     * 上传名字
     */
    private void uploadName() {
        String url = Urls.Url_My_Name;
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("name", newName);
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback2());
    }

    /**
     * 上传用户
     */
    class MyStringCallback2 extends StringCallback {
        @Override
        public void onError(Request request, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");
            LogUtils.e(TAG, ":uploadName:", e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":uploadName:", response);
            MyAlterNameBean bean = JsonUtil.json2Bean(response, MyAlterNameBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, msg);
                } else {
                    // 通知我的界面请求数据更新界面
                    RxBus.getDefault().post(new Exbus_AlterName());
                    LogUtils.eNormal(TAG + ":uploadName:", "姓名修改成功");
                }
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (null != data) {
//            switch (requestCode) {
//                case CAMERA:
//                    if (resultCode == Activity.RESULT_OK) {
//
////                        isShow = showPhoto01(data);
//                    }
//                    break;
//                case PICTURE:
//                    if (resultCode == Activity.RESULT_OK) {
//                        isShow = showPhoto02(data);
//                    }
//                    break;
//                case REQUESTCODE:
//                    if (resultCode == 1) {
//                        String newBusinessName = data.getStringExtra(Keys.BUSINESSNAME);
//                        if (!TextUtils.isEmpty(newBusinessName)) {
//                            SpUtils.putParam(mActivity, Keys.ISALTERUNIT, true);
//                            SpUtils.putParam(mActivity, Keys.NEWBUSINESSNAME, newBusinessName);
//                            tvMyUnit.setText(newBusinessName);
//                        }
//                    }
//
//                    break;
//            }
//        }
//        if (isShow) {
//
//            // 说明图片已经显示，上传头像到网络
//            upLoadIcon();
//            isShow = false;
//        } else {
//            // 图片没显示
//            isShow = false;
//        }
//
//    }

    /**
     * 拿到拍到的照片去上传
     */
    private File newFile = null;

    private void upLoadIcon() {

        // 获取
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        if (newFile == null) {
            return;
        }
//        showDialog = loadProgressDialog.show(mActivity, "正在上传...");
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        String url = Urls.Url_UpLoadingHeaderIcon;

        OkHttpUtils.post()//
                .addFile("file", "file01.png", newFile)//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());
    }

    /**
     * 上传头像
     */
    class MyStringCallback1 extends StringCallback {
        @Override
        public void onError(Request request, Exception e) {
//            showDialog.dismiss();
            LogUtils.e(TAG, ":upLoadIcon:", e);
            ToastUtils.showToast(mActivity, "网络有问题，请检查");

        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":upLoadIcon:", response);
            My_HeaderIconBean bean = JsonUtil.json2Bean(response, My_HeaderIconBean.class);
            if (bean != null) {
                int code = bean.code;
                final String img1 = bean.data.img;
                String msg = bean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, msg);
                } else {
                    // 通知我的界面请求数据更新界面
                    RxBus.getDefault().post(new Exbus_AlterName());
                    ToastUtils.showToast(mActivity, "保存成功");

                }
            }

        }
    }

    /**
     * 打开相机
     */
    private void camera() {
        takePhoto();
        dialog.dismiss();
    }

    /**
     * 获取相册照片
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, PICTURE); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

    }


    /**
     * 打开相册
     */
    private void getPhoto() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
        dialog.dismiss();
    }

    private void takePhoto() {
        //图片名称 时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File outputImage = new File(path, filename + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将File对象转换为Uri并启动照相程序
        if (Build.VERSION.SDK_INT < 24) {

            imageUri = Uri.fromFile(outputImage);
        } else {
            imageUri = FileProvider.getUriForFile(mActivity, "com.luoli.cameraalbumtest.fileprovider", outputImage);
        }
        Intent tTntent = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
        tTntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
        startActivityForResult(tTntent, CAMERA); //启动照相

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE:
                if (resultCode == 1) {
                    String newBusinessName = data.getStringExtra(Keys.BUSINESSNAME);
                    if (!TextUtils.isEmpty(newBusinessName)) {
                        SpUtils.putParam(mActivity, Keys.ISALTERUNIT, true);
                        SpUtils.putParam(mActivity, Keys.NEWBUSINESSNAME, newBusinessName);
                        tvMyUnit.setText(newBusinessName);
                    }
                }

                break;
            case CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        //　启动相机裁剪
                        startCameraCrop();

                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CROP_PHOTO://相机裁剪成功
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        //图片解析成Bitmap对象
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(imageUri));
                        if (bitmap != null) {
                            Bitmap photo = ImageFactory.ratio(bitmap, 800, 800);

                            //显示图片
                            ivHeaderIcon.setImageBitmap(photo);
                            // 把本文件压缩后缓存到本地文件里面
                            filePhoto01 = savePicture(photo, "photo01");
                            newFile = filePhoto01;
                            isShow = true;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICTURE:// 相册
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    Bitmap bitmap = null;
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        bitmap = handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        bitmap = handleImageBeforeKitKat(data);
                    }
                    if (bitmap != null) {
                        Bitmap photo = ImageFactory.ratio(bitmap, 800, 800);
                        //显示图片
                        ivHeaderIcon.setImageBitmap(photo);
                        // 把本文件压缩后缓存到本地文件里面
                        filePhoto01 = savePicture(photo, "photo02");
                        newFile = filePhoto01;
                        isShow = true;
                    }
                }
                break;
        }
        if (isShow) {

            // 说明图片已经显示，上传头像到网络
            upLoadIcon();
            isShow = false;
        } else {
            // 图片没显示
            isShow = false;
        }

    }

    /**
     * 相机裁剪
     */
    private void startCameraCrop() {
        // 启动剪裁功能
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("scale", true);
        //设置宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //设置裁剪图片宽高
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //广播刷新相册
        Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentBc.setData(imageUri);
        this.sendBroadcast(intentBc);
        startActivityForResult(intent, CROP_PHOTO); //设置裁剪参数显示图片至ImageView
    }

    @TargetApi(19)
    private Bitmap handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        Bitmap bitmap = displayImage(imagePath);// 根据图片路径显示图片
        return bitmap;
    }

    private Bitmap handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        Bitmap bitmap = displayImage(imagePath);
        return bitmap;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private Bitmap displayImage(String imagePath) {
        Bitmap bitmap = null;
        if (imagePath != null) {
            bitmap = BitmapFactory.decodeFile(imagePath);
//            ivPhoto.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
//            ToastUtils.showToast(mActivity, "failed to get image");
        }
        return bitmap;
    }


    /**
     * 为了减小体积 把图片压缩保存到手机上（清晰度改动不大，基本不受影响）
     **/
    private File savePicture(Bitmap bitmap, String fileName) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File outputImage = new File(path, fileName + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
            OutputStream stream = new FileOutputStream(outputImage);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);// 把图片写入指定文件夹中
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return outputImage;
    }

    /**
     * 弹出对话框
     */
    private void showDialog() {
        // 创建一个对象
        View view = View.inflate(mActivity, R.layout.dialog_layout, null);
        View btnCancel = view.findViewById(R.id.btn_popDialog_cancel);//取消按钮
        //显示对话框
        ShowBottonDialog showBottonDialog = new ShowBottonDialog(mActivity, view, btnCancel);
        dialog = showBottonDialog.show();
        //拍照按钮
        btnTakephoto = (Button) view.findViewById(R.id.btn_popDialog_takephoto);
        //相册获取
        btnPhoto = (Button) view.findViewById(R.id.btn_popDialog_photo);
        // 设置监听
        btnTakephoto.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);


    }

    /**
     * 设置头像的方法
     */
    private void setHeaderIcon(String imgurl) {
        Glide
                .with(mActivity)
                .load(imgurl)
                .centerCrop().crossFade().into(ivHeaderIcon);
    }

}
