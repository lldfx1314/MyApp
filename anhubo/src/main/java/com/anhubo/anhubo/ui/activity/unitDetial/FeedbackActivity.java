package com.anhubo.anhubo.ui.activity.unitDetial;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.FeedBackBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.ImageFactory;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.FlowLayout;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.okhttp.Request;

/**
 * Created by LUOLI on 2016/11/4.
 */
public class FeedbackActivity extends BaseActivity {
    private static final int PICTURE = 1;
    private static final int CAMERA = 2;
    public static final String FEEDBACK_FINISH = "feedback_finish";
    private static final int CROP_PHOTO = 3;
    private static final String TAG = "FeedbackActivity";
    @InjectView(R.id.et_feedback)
    EditText etFeedback;
    @InjectView(R.id.iv_feedback1)
    ImageView ivFeedback1;
    @InjectView(R.id.iv_feedback2)
    ImageView ivFeedback2;
    @InjectView(R.id.iv_feedback3)
    ImageView ivFeedback3;
    @InjectView(R.id.tv_submit_feedback)
    TextView tvSubmitFeedback;
    @InjectView(R.id.rl_feedback)
    RelativeLayout svFeedback;
    @InjectView(R.id.rl_feedback_tag)
    RelativeLayout rlFeedbackTag;
    private InputMethodManager imm;
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private String str_photo;
    private String feedContent = "";
    private String deviceId;
    private File file1;
    private File file2;
    private File file3;
    private String uid;
    public static int userAddScore;
    private ArrayList<String> listResult;
    private FlowLayout flowLayout;
    private Dialog showDialog;
    private Uri imageUri;

    @Override
    protected void initConfig() {
        super.initConfig();
        deviceId = getIntent().getStringExtra(Keys.DeviceId);
        listResult = (ArrayList<String>) getIntent().getSerializableExtra(Keys.REQUIRE_LIST);


    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("反馈");
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        ScrollView scrollView = new ScrollView(mActivity);
        flowLayout = new FlowLayout(mActivity);
        flowLayout.setPadding(6, 6, 6, 6);
        scrollView.addView(flowLayout);
        rlFeedbackTag.addView(scrollView);
        if (listResult != null) {
            for (String string : listResult) {
                TextView textView = DisplayUtil.createTextView(mActivity);
                textView.setText(string);
                flowLayout.addView(textView);
            }
        }
        // 初始化结束的广播监听
        initFinishReceiver();
    }

    /**
     * 初始化结束的广播监听
     */
    private void initFinishReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(FEEDBACK_FINISH);
        registerReceiver(finishReceiver, filter);
    }

    /**
     * 广播接收
     */
    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //收到广播后finishing
            if (FEEDBACK_FINISH.equals(intent.getAction())) {
                finish();
            }
        }
    };


    @Override
    protected void onLoadDatas() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishReceiver);
    }

    @OnClick({R.id.iv_feedback1, R.id.rl_feedback, R.id.iv_feedback2, R.id.iv_feedback3, R.id.tv_submit_feedback})
    public void onClick(View view) {
        /**获取输入的内容*/
        getInputData();
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        switch (view.getId()) {
            case R.id.rl_feedback:// 问题输入框父控件的点击
                // 如果点击输入框父控件先弹出键盘,让焦点在输入框上
                etFeedback.requestFocus();// 获取焦点
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.iv_feedback1:// 图片1
                /**弹出拍照对话框*/
                str_photo = 1 + "";
                showDialog();

                break;
            case R.id.iv_feedback2:// 图片2
                /**弹出拍照对话框*/
                str_photo = 2 + "";
                showDialog();

                break;
            case R.id.iv_feedback3:// 图片3
                /**弹出拍照对话框*/
                str_photo = 3 + "";
                showDialog();

                break;
            case R.id.tv_submit_feedback:// 提交按钮
                /**提交反馈*/
                submit();
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
     * 弹窗提示
     */
    private void dialog() {
        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("请填写问题描述")
                .setCancelable(true)
                .show();
    }

    /**
     * 提交反馈
     */
    private void submit() {

        if (TextUtils.isEmpty(feedContent)) {
            dialog();
            return;
        }

        showDialog = loadProgressDialog.show(mActivity, "正在提交...");
        String url = Urls.Url_FeedBack;
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("issue_content", feedContent);
        if (!TextUtils.isEmpty(deviceId)) {
            params.put("device_id", deviceId);
        }

        PostFormBuilder post = OkHttpUtils.post();
        if (file1 != null) {
            post.addFile("file1", "file01.png", file1);
        }
        if (file2 != null) {
            post.addFile("file2", "file02.png", file2);
        }
        if (file3 != null) {
            post.addFile("file3", "file03.png", file3);
        }
        post.url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    class MyStringCallback extends StringCallback {

        @Override
        public void onError(Request request, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG,"submit:",e);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG,"submit:"+response);
            FeedBackBean bean = JsonUtil.json2Bean(response, FeedBackBean.class);
            if (bean != null) {
                showDialog.dismiss();
                int code = bean.code;
                String msg = bean.msg;
                userAddScore = bean.data.user_add_score;
                // 解开反馈成功界面
                Intent intent = new Intent(mActivity, FeedbackSuccessActivity.class);
                startActivity(intent);
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
//                    ToastUtils.showToast(mActivity, "You denied the permission");
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

        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case CAMERA:
                try {
                    //　启动相机裁剪
                    startCameraCrop();

                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CROP_PHOTO://相机裁剪成功
                try {
                    //图片解析成Bitmap对象
                    Bitmap bitmap = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(imageUri));
                    if (bitmap != null) {
                        Bitmap photo = ImageFactory.ratio(bitmap, 800, 800);

                        if (TextUtils.equals(str_photo, 1 + "")) {
                            ivFeedback1.setImageBitmap(photo);// 把本文件压缩后缓存到本地文件里面
                            File filePhoto01 = savePicture(photo, "photo01");
                            // 图片一
                            file1 = filePhoto01;

                        } else if (TextUtils.equals(str_photo, 2 + "")) {
                            ivFeedback2.setImageBitmap(photo);
                            // 把本文件压缩后缓存到本地文件里面
                            File filePhoto02 = savePicture(photo, "photo02");
                            //图片二
                            file2 = filePhoto02;

                        } else if (TextUtils.equals(str_photo, 3 + "")) {
                            ivFeedback3.setImageBitmap(photo);
                            // 把本文件压缩后缓存到本地文件里面
                            File filePhoto02 = savePicture(photo, "photo03");
                            // 图片三
                            file3 = filePhoto02;

                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case PICTURE:// 相册
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
                    if (TextUtils.equals(str_photo, 1 + "")) {
                        ivFeedback1.setImageBitmap(photo);// 把本文件压缩后缓存到本地文件里面
                        File filePhoto01 = savePicture(photo, "photo01");
                        // 图片一
                        file1 = filePhoto01;

                    } else if (TextUtils.equals(str_photo, 2 + "")) {
                        ivFeedback2.setImageBitmap(photo);
                        // 把本文件压缩后缓存到本地文件里面
                        File filePhoto02 = savePicture(photo, "photo02");
                        //图片二
                        file2 = filePhoto02;

                    } else if (TextUtils.equals(str_photo, 3 + "")) {
                        ivFeedback3.setImageBitmap(photo);
                        // 把本文件压缩后缓存到本地文件里面
                        File filePhoto02 = savePicture(photo, "photo03");
                        // 图片三
                        file3 = filePhoto02;

                    }

                }
                break;
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
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
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
     * 获取输入的内容
     */
    private void getInputData() {

        feedContent = etFeedback.getText().toString().trim();
    }


}
