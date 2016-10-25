package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MsgPerfectLowerBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.ImageTools;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/17.
 */
public class UploadingActivity2 extends BaseActivity {

    private static final int CAMERA = 0;
    private static final int PICTURE = 1;
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
    private String imgName;
    private boolean isClick = false;//判断是正面还是反面
    private boolean isClick1 = false;// 判断是拍照还是相册
    private File filePhoto02;
    private File filePhoto01;

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
                isClick1 = true;
                takePhoto();
                break;
            case R.id.btn_popDialog_photo:
                // 相册
                isClick1 = false;
                getPhoto();
                break;
        }
    }

    /**
     * 拿到拍到的照片去上传
     */

    private void upLoading() {
        // 获取
        String businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
        File file1 = null;
        File file2 = null;

        // 正面
        if (isClick1) {
            // 照相
            file1 = filePhoto01;

        } else {
            //相册
            file1 = filePhoto02;
        }
        isClick = !isClick;

        /**正面和反面个走个的*/

        //反面
        if (isClick1) {
            // 照相
            file2 = filePhoto01;

        } else {
            //相册
            file2 = filePhoto02;
        }
        isClick = !isClick;

        if (file1==null||file2==null||!file1.exists()||!file2.exists()) {
            ToastUtils.showLongToast(mActivity, "请先拍照或者获取图库图片");
            return;
        }
        /*if (!file2.exists()) {

            ToastUtils.showLongToast(mActivity, "图片不存在");
        }*/
        Map<String, String> params = new HashMap<>();
        params.put("business_id", businessid);
        String url = Urls.Url_UpLoading02;

        OkHttpUtils.post()//
                .addFile("file", "file01.png", file1)//
                .addFile("file", "file02.png", file2)//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());


    }

    private Handler handler = new Handler();

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");

            System.out.println("UploadingActivity2+++===界面失败" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            MsgPerfectLowerBean lowerBean = new Gson().fromJson(response, MsgPerfectLowerBean.class);
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
                }, 2000);
            }
        }
    }

    /**
     * 从相册获取
     */
    private void getPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");  // 开启Pictures画面Type设定为image
        intent.setAction(Intent.ACTION_GET_CONTENT); //使用Intent.ACTION_GET_CONTENT这个Action
        startActivityForResult(intent, PICTURE); //取得相片后返回到本画面
        dialog.dismiss();
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA);
        dialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && null != data) {
            switch (requestCode) {
                case CAMERA:
                    showPhoto01(data);
                    break;
                case PICTURE:
                    showPhoto02(data);
                    break;
            }
        }

    }

    /**
     * 显示相册照片
     */
    private void showPhoto02(Intent data) {
        ContentResolver resolver = getContentResolver();
        //照片的原始资源地址
        Uri originalUri = data.getData();
        //System.out.println(originalUri.toString());  //" content://media/external/images/media/15838 "

        //                  //将原始路径转换成图片的路径
        String selectedImagePath = uri2filePath(originalUri);
        filePhoto02 = new File(selectedImagePath);
        //                  System.out.println(selectedImagePath);  //" /mnt/sdcard/DCIM/Camera/IMG_20130603_185143.jpg "
        try {
            //使用ContentProvider通过URI获取原始图片
            Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);

            imgName = createPhotoFileName();
            //写一个方法将此文件保存到本应用下面啦
            savePicture(imgName, photo);

            if (photo != null) {
                //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                Bitmap bitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / 5, photo.getHeight() / 5);
                if (isClick) {
                    llCard01.setVisibility(View.GONE);
                    //显示图片
                    ivShowCardFront02.setImageBitmap(bitmap);
                } else {
                    llCard02.setVisibility(View.GONE);
                    //显示图片
                    ivShowCardBehind02.setImageBitmap(bitmap);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件路径
     **/
    public String uri2filePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }

    /**
     * 显示照相机照片
     */
    private void showPhoto01(Intent data) {
        String sdState = Environment.getExternalStorageState();
        if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showLongToast(mActivity, "sd card unmount");
            return;
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
        if (isClick) {
            llCard01.setVisibility(View.GONE);
            //显示图片
            ivShowCardFront02.setImageBitmap(bitmap);
        } else {
            llCard02.setVisibility(View.GONE);
            //显示图片
            ivShowCardBehind02.setImageBitmap(bitmap);
        }
    }

    /**
     * 保存图片到本应用下
     **/
    private void savePicture(String fileName, Bitmap bitmap) {

        FileOutputStream fos = null;
        try {//直接写入名称即可，没有会被自动创建；私有：只有本应用才能访问，重新内容写入会被覆盖
            fos = mActivity.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 把图片写入指定文件夹中

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                    fos = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建图片不同的文件名
     **/
    private String createPhotoFileName() {
        String fileName = "";
        Date date = new Date(System.currentTimeMillis());  //系统当前时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        fileName = dateFormat.format(date) + ".jpg";
        return fileName;
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


}
