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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.EngineerAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.EngineerBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.ImageFactory;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
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
 * Created by LUOLI on 2016/11/1.
 */
public class EngineerActivity extends BaseActivity {
    private static final int PICTURE = 0;
    private static final int CAMERA = 1;
    private static final String TAG = "EngineerActivity";
    private static final int CROP_PHOTO = 2;
    @InjectView(R.id.et_engineer_name)
    EditText etEngineerName;
    @InjectView(R.id.ll_engineer_grade)
    TextView llEngineerGrade;
    @InjectView(R.id.tv_engineer_grade)
    TextView tvEngineerGrade;
    @InjectView(R.id.et_engineer_phone)
    EditText etEngineerPhone;
    @InjectView(R.id.iv_engineer1)
    ImageView ivEngineer1;
    @InjectView(R.id.iv_engineer2)
    ImageView ivEngineer2;
    @InjectView(R.id.btn_submit_engineer)
    Button btnSubmitEngineer;
    private String engineerName;
    private String engineerPhone;
    private String engineerGrade;
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private boolean isClick = false;//判断是正面还是反面
    private ListView listView;
    private ArrayList<String> list;
    private PopupWindow popupWindow;
    private String str;
    private Dialog showDialog;
    private Uri imageUri;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_engineer;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("消防工程师认证");
    }

    @Override
    protected void initEvents() {
        super.initEvents();

    }

    @Override
    protected void onLoadDatas() {

    }


    @OnClick({R.id.tv_engineer_grade, R.id.iv_engineer1, R.id.iv_engineer2, R.id.btn_submit_engineer})
    public void onClick(View view) {
        /**获取输入的内容*/
        getInputData();
        switch (view.getId()) {
            case R.id.tv_engineer_grade:
                // 评级弹出
                /********************************************/
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean immActive = imm.isActive();
                if (immActive) {
                    // 如果已经键盘弹出来，点击后让键盘隐藏
                    imm.hideSoftInputFromWindow(tvEngineerGrade.getWindowToken(), 0);
                }
                showPopupwindow();
                break;
            case R.id.iv_engineer1:
                // 证件正面 底部弹出对话框
                isClick = true;
                showDialog();
                break;
            case R.id.iv_engineer2:
                // 证件背面 底部弹出对话框
                isClick = false;
                showDialog();
                break;
            case R.id.btn_submit_engineer:
                // 姓名
                if (TextUtils.isEmpty(engineerName)) {
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("请输入您的姓名")
                            .setCancelable(true).show();
                    return;
                }
                // 评级
                if (TextUtils.isEmpty(str)) {
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("请选择证书等级")
                            .setCancelable(true).show();
                    return;
                }
                // 证书编号
                if (TextUtils.isEmpty(engineerPhone)) {
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("请输入证件遍号")
                            .setCancelable(true).show();
                    return;
                }
                /**提交证书编号*/
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
     * 评级弹出
     */
    private void showPopupwindow() {
        list = new ArrayList<>();
        String[] arr = new String[]{"建（构）筑物消防员初级", "建（构）筑物消防员中级", "建（构）筑物消防员高级", "建（构）筑物消防员技师",
                "建（构）筑物消防员高级技师", "注册消防工程师高级", "注册消防工程师一级", "注册消防工程师二级"};
        for (int i = 0; i < arr.length; i++) {
            list.add(arr[i]);
        }
        View view = View.inflate(mActivity, R.layout.engineer_dialog, null);

        listView = (ListView) view.findViewById(R.id.lv_engineer);
        setAdapter(view);
    }

    private void setAdapter(View view) {
        EngineerAdapter adapter = new EngineerAdapter(mActivity, list);
        listView.setAdapter(adapter);
        // 创建一个PopuWidow对象
        popupWindow = new PopupWindow(view, DisplayUtil.dp2px(mActivity, 350), DisplayUtil.dp2px(mActivity, 200));
        //控制键盘是否可以获得焦点
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
        int xPos = windowManager.getDefaultDisplay().getWidth() / 2
                - popupWindow.getWidth() / 2;

        popupWindow.showAsDropDown(llEngineerGrade);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {

                TextView tvEngineer = (TextView) view.findViewById(R.id.tv_engineer);
                str = tvEngineer.getText().toString().trim();
                tvEngineerGrade.setText(str);
                //关闭popupWindow
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            }
        });
    }

    private File file1 = null;
    private File file2 = null;

    /**
     * 提交证书编号
     */
    private void submit() {
        // 获取
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);

        if (file1 == null) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("亲，请拍取消防工程师正面照片")
                    .setCancelable(true).show();
            return;
        }
        if (file2 == null) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("亲，请拍取消防工程师背面照片")
                    .setCancelable(true).show();
            return;
        }
        showDialog = loadProgressDialog.show(mActivity, "正在提交...");
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("re_num", engineerPhone);
        params.put("name", engineerName);
        params.put("cer_hie", str);
        String url = Urls.Url_Engineer;

        OkHttpUtils.post()//
                .addFile("file1", "file01.png", file1)//
                .addFile("file2", "file02.png", file2)//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    private Handler handler = new Handler();

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Request request, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG, ":submit", e);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":submit", response);
            EngineerBean bean = JsonUtil.json2Bean(response, EngineerBean.class);
            if (bean != null) {
                showDialog.dismiss();
                int code = bean.code;
                final String msg = bean.msg;
                if (code != 0) {
                    ToastUtils.showToast(mActivity, "上传失败");
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(mActivity, "上传成功");

                            finish();
                        }
                    }, 500);
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

                        Bitmap photo = ImageFactory.ratio(bitmap, 1080, 720);
                        if (isClick) {
                            //显示第一张图片
                            ivEngineer1.setImageBitmap(photo);
                            File filePhoto01 = savePicture(photo, "photo01");
                            file1 = filePhoto01;
                        } else {
                            //显示第二张图片
                            ivEngineer2.setImageBitmap(photo);
                            File filePhoto02 = savePicture(photo, "photo02");
                            file2 = filePhoto02;
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
                    Bitmap photo = ImageFactory.ratio(bitmap, 1080, 720);
                    if (isClick) {
                        //显示第一张图片
                        ivEngineer1.setImageBitmap(photo);
                        File filePhoto01 = savePicture(photo, "photo01");
                        file1 = filePhoto01;
                    } else {
                        //显示第二张图片
                        ivEngineer2.setImageBitmap(photo);
                        File filePhoto02 = savePicture(photo, "photo02");
                        file2 = filePhoto02;
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
        intent.putExtra("outputX", 1080);
        intent.putExtra("outputY", 720);
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
                boolean delete = outputImage.delete();
                LogUtils.eNormal(TAG, "刪除老數據：" + delete);
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

        engineerName = etEngineerName.getText().toString().trim();
        engineerPhone = etEngineerPhone.getText().toString().trim();
    }
}
