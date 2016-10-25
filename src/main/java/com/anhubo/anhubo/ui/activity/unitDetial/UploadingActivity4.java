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
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MsgPerfectRentingBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.ImageTools;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.PopBirthHelper;
import com.anhubo.anhubo.utils.PopDateHelper;
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
public class UploadingActivity4 extends BaseActivity {

    private static final int PICTURE = 0;
    private static final int CAMERA = 1;
    @InjectView(R.id.ll_msgPerRenting01)
    LinearLayout llMsgPerRenting01;
    @InjectView(R.id.ll_msgPerRenting02)
    LinearLayout llMsgPerRenting02;
    @InjectView(R.id.ll_takePhoto04)
    LinearLayout llTakePhoto04;
    @InjectView(R.id.iv_showPhoto04)
    ImageView ivShowPhoto04;
    @InjectView(R.id.btn_unloading04)
    Button btnUnloading04;
    @InjectView(R.id.tv_startTime)
    TextView tvStartTime;
    @InjectView(R.id.tv_Time)
    TextView tvTime;
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private String imgName;
    private PopBirthHelper popBirthHelper;
    private PopDateHelper popDateHelper;
    private boolean isClick;
    private File filePhoto01;
    private File filePhoto02;
    private String newTime;
    private String timeLong;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_uploading4;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("租房合同");
        popBirthHelper = new PopBirthHelper(mActivity);
        popBirthHelper.setOnClickOkListener(new PopBirthHelper.OnClickOkListener() {
            @Override
            public void onClickOk(String time) {
                //ToastUtils.showLongToast(mActivity,birthday);
                tvStartTime.setVisibility(View.VISIBLE);
                tvStartTime.setText(time);
                newTime = time;
            }
        });

        popDateHelper = new PopDateHelper(this);
        popDateHelper.setOnClickOkListener(new PopDateHelper.OnClickOkListener() {



            @Override
            public void onClickOk(String year, String month) {
                tvTime.setVisibility(View.VISIBLE);
                timeLong = year + "年" + month + "月";
                tvTime.setText(timeLong);
            }
        });

    }

    @Override
    protected void onLoadDatas() {

    }


    @OnClick({R.id.ll_msgPerRenting01, R.id.ll_msgPerRenting02, R.id.ll_takePhoto04, R.id.btn_unloading04})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_msgPerRenting01:
                popBirthHelper.show(llMsgPerRenting01);
                break;
            case R.id.ll_msgPerRenting02:
                popDateHelper.show(llMsgPerRenting02);
                break;
            case R.id.ll_takePhoto04:
                // 底部弹出对话框
                showDialog();
                break;
            case R.id.btn_unloading04:
                // 提交按钮，走网络
                upLoading();
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
     * 拿到拍到的照片去上传
     */


    private void upLoading() {
        // 获取
        String businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
        File file = null;
        if (isClick) {
            file = filePhoto01;
        } else {
            file = filePhoto02;
        }
        if (!file.exists()) {
            ToastUtils.showLongToast(mActivity, "图片不存在");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("business_id", businessid);
        params.put("rent_time", timeLong);
        params.put("rent_start_time", newTime);
        String url = Urls.Url_UpLoading04;

        OkHttpUtils.post()//
                .addFile("file", "file02.png", file)//
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

            System.out.println("UploadingActivity4+++===界面失败" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            System.out.println("UploadingActivity4-------"+response);
            MsgPerfectRentingBean rentingBean = new Gson().fromJson(response, MsgPerfectRentingBean.class);
            int code = rentingBean.code;
            final String msg = rentingBean.msg;
            if (code != 0) {

                ToastUtils.showToast(mActivity, "上传失败");
            } else {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(mActivity, "上传成功");
                        Intent intent = new Intent();
                        intent.putExtra(Keys.ISCLICK4, true);
                        setResult(4, intent);
                        finish();
                    }
                }, 2000);
            }
        }
    }

    private void getPhoto() {

        Intent intent = new Intent();
        intent.setType("image/*");  // 开启Pictures画面Type设定为image
        intent.setAction(Intent.ACTION_GET_CONTENT); //使用Intent.ACTION_GET_CONTENT这个Action
        startActivityForResult(intent, PICTURE); //取得相片后返回到本画面
        dialog.dismiss();

    }

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

    private void showPhoto02(Intent data) {
        ContentResolver resolver = getContentResolver();
        //照片的原始资源地址
        Uri originalUri = data.getData();
        //System.out.println(originalUri.toString());  //" content://media/external/images/media/15838 "

        //将原始路径转换成图片的路径
        String selectedImagePath = uri2filePath(originalUri);
        filePhoto02 = new File(selectedImagePath);
        try {
            //使用ContentProvider通过URI获取原始图片
            Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);

            imgName = createPhotoFileName();
            //写一个方法将此文件保存到本应用下面啦
            savePicture(imgName, photo);

            if (photo != null) {
                //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                Bitmap bitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / 5, photo.getHeight() / 5);

                llTakePhoto04.setVisibility(View.GONE);
                //显示图片
                ivShowPhoto04.setImageBitmap(bitmap);
            }
            //ToastUtils.showLongToast(mActivity,"已保存本应用的files文件夹下");
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
        filePhoto01 = new File(filename);
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
        llTakePhoto04.setVisibility(View.GONE);
        //显示图片
        ivShowPhoto04.setImageBitmap(bitmap);
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
