package com.anhubo.anhubo.ui.activity.MyDetial;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MyAlterGenderBean;
import com.anhubo.anhubo.bean.MyAlterNameBean;
import com.anhubo.anhubo.bean.MyFragmentBean;
import com.anhubo.anhubo.bean.My_HeaderIconBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.impl.MyFragment;
import com.anhubo.anhubo.utils.DatePackerUtil;
import com.anhubo.anhubo.utils.ImageTools;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.PopBirthHelper;
import com.anhubo.anhubo.utils.PopGenderHelper;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/10/28.
 */
public class PersonMsgActivity extends BaseActivity {
    private static final int PICTURE = 0;
    private static final int CAMERA = 1;
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
    private boolean isClick = false;
    private File filePhoto01;
    private File filePhoto02;
    private Bitmap bitmap2;
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
    protected void initEvents() {
        super.initEvents();
        // 屏蔽键盘的弹起
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // 获取到MyFragment里的Bitmap对象

        // 给每个控件先设置初始内容
        setInitialData();

        Bitmap mBitmap = MyFragment.mBitmap;
        if (mBitmap != null) {
            ivHeaderIcon.setImageBitmap(mBitmap);
        }
        /**获取我的界面传过来的姓名，显示*/
        String name1 = MyFragment.name1;
        if (!TextUtils.isEmpty(name1)) {
            etMyUsername.setText(name1);
        }
        /**年龄弹窗的显示*/
        alterAge();
        /**性别弹窗*/
        alterGender();
    }
    /**年龄弹窗的显示*/
    private void alterAge() {
        popBirthHelper = new PopBirthHelper(mActivity);
        popBirthHelper.setOnClickOkListener(new PopBirthHelper.OnClickOkListener() {
            @Override
            public void onClickOk(String time) {
                //ToastUtils.showLongToast(mActivity,birthday);

                tvMyAge.setText(time);
            }
        });
    }
    /**性别弹窗*/
    private void alterGender() {
        popGenderHelper = new PopGenderHelper(this);
        popGenderHelper.setListItem(DatePackerUtil.getListGender());
        popGenderHelper.setOnClickOkListener(new PopGenderHelper.OnClickOkListener() {

            @Override
            public void onClickOk(String str) {
                tvMyGender.setText(str);
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
    /**上传性别*/
    class MyStringCallback4 extends StringCallback{
        @Override
        public void onError(Call call, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("PersonMsgActivity+++界面上传性别"+response);
            MyAlterGenderBean bean = new Gson().fromJson(response, MyAlterGenderBean.class);
            if(bean!=null){
                int code = bean.code;
                String msg = bean.msg;
                if(code != 0){
                    ToastUtils.showToast(mActivity,msg);
                }else{
                    ToastUtils.showToast(mActivity,"性别修改成功");
                }
            }
        }
    }

    /**
     * 给每个控件先设置初始内容
     */
    private void setInitialData() {
        if (!TextUtils.isEmpty(img)) {
            //设置头像显示,这种方式效率较低，体验不好
            //setHeaderIcon(img);
        }
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
        if (!TextUtils.isEmpty(businessName)) {
            tvMyUnit.setText(businessName);
        }
        if (!TextUtils.isEmpty(weixinName)) {
            tvMyWechat.setText(weixinName);
        }
    }

    @Override
    protected void onLoadDatas() {

    }

    @OnClick({R.id.ll_psHeaderIcon, R.id.ll_psUsername, R.id.ll_psAge, R.id.ll_psGender, R.id.ll_psPhone, R.id.ll_pspwd, R.id.ll_psCertification, R.id.ll_psUnit, R.id.ll_psWeChat})
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
                /**修改用户名*/
                alterName();
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

                break;
            case R.id.ll_psCertification:
                // 实名认证

                break;
            case R.id.ll_psUnit:
                // 所属单位
                break;
            case R.id.ll_psWeChat:
                //微信
                break;
            case R.id.btn_popDialog_takephoto:
                // 拍照
                isClick = true;
                takePhoto();
                break;
            case R.id.btn_popDialog_photo:
                // 相册
                isClick = false;
                getPhoto();
                break;
        }
    }


    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            uploadName();
        }
    };

    /**
     * 修改用户名
     */
    private void alterName() {
        // 先弹出键盘,让焦点在输入框上
        etMyUsername.requestFocus();// 获取焦点
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

        //监听输入框的动态变化

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
                //延迟5s，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun, 5000);
            }
        });

    }

    /**上传名字*/
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
    /**上传用户*/
    class MyStringCallback2 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("PersonMsgActivity+界面修改用户名" + response);
            MyAlterNameBean bean = new Gson().fromJson(response, MyAlterNameBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, msg);
                }else {
                    ToastUtils.showToast(mActivity, "修改成功");
                    // 每次更改成功后要通知我的界面也要改变显示内容
                    Intent intent = new Intent();
                    intent.putExtra(Keys.NEWNAME, newName);
                    setResult(2, intent);
                    // 失去焦点
                    //etMyUsername.clearFocus();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && null != data) {
            switch (requestCode) {
                case CAMERA:
                    isShow = showPhoto01(data);
                    break;
                case PICTURE:
                    isShow = showPhoto02(data);
                    break;
            }
        }
        if (isShow) {
            // 说明图片已经显示，上传头像到网络
            upLoading();
            isShow = false;
        } else {
            // 图片没显示
            isShow = false;
        }

    }

    /**
     * 拿到拍到的照片去上传
     */
    private void upLoading() {
        // 获取
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        File file = null;
        if (isClick) {
            file = filePhoto01;//相机照片
        } else {
            file = filePhoto02;//相册照片
        }
        if (file == null || !file.exists()) {
            ToastUtils.showLongToast(mActivity, "请先拍照或者获取图库图片");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);


        String url = Urls.Url_UpLoadingHeaderIcon;

        OkHttpUtils.post()//
                .addFile("file", "file01.png", file)//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());
    }
    /**上传头像*/
    class MyStringCallback1 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            System.out.println("PersonMsgActivity+++===界面失败" + e.getMessage());

            ToastUtils.showToast(mActivity, "网络有问题，请检查");

        }

        @Override
        public void onResponse(String response) {
            //System.out.println("PersonMsgActivity个人信息页面+" + response);
            My_HeaderIconBean bean = new Gson().fromJson(response, My_HeaderIconBean.class);
            if (bean != null) {
                int code = bean.code;
                final String img1 = bean.data.img;
                String msg = bean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, msg);
                } else {
                    // code = 0，保存成功
                    ToastUtils.showToast(mActivity, "保存成功");
                    Intent intent = new Intent();
                    intent.putExtra(Keys.HEADERICON, img1);
                    setResult(1, intent);

                }
            }

        }
    }

    /**
     * 展示相册图片
     */
    private boolean showPhoto02(Intent data) {

        Uri selectedImage = data.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        String imagePath = c.getString(columnIndex);
        filePhoto02 = new File(imagePath);
        Bitmap photo = BitmapFactory.decodeFile(imagePath);
        try {

            /*imgName = createPhotoFileName();
            //写一个方法将此文件保存到本应用下面啦
            savePicture(imgName, photo);*/


            if (photo != null) {
                //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                Bitmap bitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / 5, photo.getHeight() / 5);

                //显示图片
                ivHeaderIcon.setImageBitmap(bitmap);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            c.close();
        }
        return false;
    }

    /**
     * 展示照相机图片
     */
    private boolean showPhoto01(Intent data) {
        String sdState = Environment.getExternalStorageState();
        if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showLongToast(mActivity, "sd card unmount");
            return false;
        }
        new DateFormat();
        String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        Bundle bundle = data.getExtras();
        //获取相机返回的数据，并转换为图片格式
        Bitmap bitmap = (Bitmap) bundle.get("data");
        FileOutputStream fout = null;
        File file = new File("/sdcard/photo_anhubo/");
        file.mkdirs();
        String filename = file.getPath() + name;
        filePhoto01 = new File(filename);//图片的文件
        try {
            fout = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fout.flush();
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            //显示图片
            ivHeaderIcon.setImageBitmap(bitmap);
            return true;
        }
        return false;
    }

    /**
     * 打开相册获取图片
     */
    private void getPhoto() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICTURE);

        dialog.dismiss();

    }

    /**
     * 打开相机拍照
     */
    private void takePhoto() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA);
        dialog.dismiss();

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

                        System.out.println("MyFragment获取头像+++===" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        ivHeaderIcon.setImageBitmap(bitmap);
                    }
                });
    }

}
