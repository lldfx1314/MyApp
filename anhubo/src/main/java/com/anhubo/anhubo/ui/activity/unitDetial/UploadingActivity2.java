package com.anhubo.anhubo.ui.activity.unitDetial;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MsgPerfectLowerBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.ImageFactory;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/10/17.
 */
public class UploadingActivity2 extends BaseActivity {

    private static final int CAMERA = 0;
    private static final int PICTURE = 1;
    private static final String TAG = "UploadingActivity2";
    private static final int CROP_PHOTO = 2;
    @InjectView(R.id.ll_card01)
    LinearLayout llCard01;
    @InjectView(R.id.iv_showCardFront02)
    ImageView ivShowCardFront02;
    @InjectView(R.id.ll_card02)
    LinearLayout llCard02;
    @InjectView(R.id.iv_showCardBehind02)
    ImageView ivShowCardBehind02;
    @InjectView(R.id.btn_unloading02)
    Button btnUnloading02;
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private boolean isClick = false;//判断是正面还是反面
    private Dialog showDialog;
    private Uri imageUri;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_uploading2;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("法人身份证");

    }

    @Override
    protected void onLoadDatas() {

    }

    @OnClick({R.id.ll_card01, R.id.ll_card02, R.id.btn_unloading02})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_card01:
                // 身份证正面 底部弹出对话框
                isClick = true;
                showDialog();
                break;
            case R.id.ll_card02:
                // 身份证背面 底部弹出对话框
                isClick = false;
                showDialog();

                break;
            case R.id.btn_unloading02:
                // 走网络
                upLoading();
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

    File file1 = null;
    File file2 = null;

    /**
     * 拿到拍到的照片去上传
     */

    private void upLoading() {

        // 获取
        String businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);

        if (file1 == null) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("亲，必须拍取法人身份证正面照片")
                    .setCancelable(true).show();
            return;
        }
        if (file2 == null) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("亲，必须拍取法人身份证背面照片")
                    .setCancelable(true).show();
            return;
        }
        showDialog = loadProgressDialog.show(mActivity, "正在上传...");
        String url = Urls.Url_UpLoading02;
        Map<String, String> params = new HashMap<>();
        params.put("business_id", businessid);

        OkHttpUtils.post()//
                .addFile("file1", "file01.png", file1)//
                .addFile("file2", "file02.png", file2)//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());


    }

    private Handler handler = new Handler();


    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Request request, Exception e) {
            showDialog.dismiss();
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(false).show();
            LogUtils.e(TAG, ":upLoading:", e);
        }

        @Override
        public void onResponse(String response) {
            showDialog.dismiss();
            LogUtils.eNormal(TAG + ":upLoading:", response);
            MsgPerfectLowerBean lowerBean = JsonUtil.json2Bean(response, MsgPerfectLowerBean.class);
            int code = lowerBean.code;
            final String msg = lowerBean.msg;
            if (code != 0) {
                ToastUtils.showToast(mActivity, "上传失败");
            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ToastUtils.showToast(mActivity, "上传成功");
                        Intent intent = new Intent();
                        intent.putExtra(Keys.ISCLICK2, true);
                        setResult(2, intent);
                        finish();
                    }
                }, 500);
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
                            llCard01.setVisibility(View.GONE);
                            //显示图片
                            ivShowCardFront02.setImageBitmap(photo);
                            File filePhoto01 = savePicture(photo, "photo01");
                            file1 = filePhoto01;
                        } else {
                            llCard02.setVisibility(View.GONE);
                            //显示图片
                            ivShowCardBehind02.setImageBitmap(photo);
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
                        llCard01.setVisibility(View.GONE);
                        //显示图片
                        ivShowCardFront02.setImageBitmap(photo);
                        File filePhoto01 = savePicture(photo, "photo01");
                        file1 = filePhoto01;
                    } else {
                        llCard02.setVisibility(View.GONE);
                        //显示图片
                        ivShowCardBehind02.setImageBitmap(photo);
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

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

}
