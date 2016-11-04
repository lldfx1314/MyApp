package com.anhubo.anhubo.ui.activity.unitDetial;

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
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.FeedBackBean;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/11/4.
 */
public class FeedbackActivity extends BaseActivity {
    private static final int PICTURE = 1;
    private static final int CAMERA = 2;
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
    RelativeLayout rlFeedback;
    private InputMethodManager imm;
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private File filePhoto01;
    private File filePhoto02;
    private String str_photo;
    private boolean isClick;
    private String feedContent;
    private String deviceId;
    private File file1;
    private File file2;
    private File file3;
    private String uid;
    public static int userAddScore;

    @Override
    protected void initConfig() {
        super.initConfig();
        deviceId = getIntent().getStringExtra(Keys.DeviceId);
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
    protected void onLoadDatas() {

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
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
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
                if (!TextUtils.isEmpty(deviceId)) {
                    submit1();
                } else {
                    submit2();
                }

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
     * 无deviceid提交反馈
     */
    private void submit2() {
        /*// 获取
        // 图片1
        if (isClick) {
            // 照相
            file1 = filePhoto01;

        } else {
            //相册
            file1 = filePhoto02;
        }

        //图片2
        if (isClick) {
            // 照相
            file2 = filePhoto01;

        } else {
            //相册
            file2 = filePhoto02;
        }

        //图片3
        if (isClick) {
            // 照相
            file3 = filePhoto01;

        } else {
            //相册
            file3 = filePhoto02;
        }*/
        // 问题描述
        if (TextUtils.isEmpty(feedContent)) {
            ToastUtils.showToast(mActivity, "请输入问题");
            return;
        }

        if (file1 == null || !file1.exists()) {
            ToastUtils.showToast(mActivity, "请至少拍一张图片");
            return;
        }
        if (file1 == null || !file1.exists()) {
            ToastUtils.showToast(mActivity, "请拍第二张图片");
            return;
        }
        if (file1 == null || !file1.exists()) {
            ToastUtils.showToast(mActivity, "请拍第三张图片");
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put("uid", uid);
        params.put("issue_content", feedContent);

        String url = Urls.Url_FeedBack;


        OkHttpUtils.post()//
                .addFile("file1", "file01.png", file1)//
                .addFile("file2", "file02.png", file2)//
                .addFile("file3", "file03.png", file3)//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());
    }

    class MyStringCallback1 extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {

            System.out.println("FeedbackActivity界面+++===失败");
        }

        @Override
        public void onResponse(String response) {
            System.out.println("反馈界面+++===" + response);
            FeedBackBean bean = new Gson().fromJson(response, FeedBackBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                userAddScore = bean.data.user_add_score;
                // 打开反馈成功界面
                Intent intent = new Intent(mActivity, FeedbackSuccessActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * 有deviceid提交反馈
     */
    private void submit1() {

/*// 图片1
        if (isClick) {
            // 照相
            file1 = filePhoto01;

        } else {
            //相册
            file1 = filePhoto02;
        }

        //图片2
        if (isClick) {
            // 照相
            file2 = filePhoto01;

        } else {
            //相册
            file2 = filePhoto02;
        }

        //图片3
        if (isClick) {
            // 照相
            file3 = filePhoto01;

        } else {
            //相册
            file3 = filePhoto02;
        }*/


        // 问题描述
        if (TextUtils.isEmpty(feedContent)) {
            ToastUtils.showToast(mActivity, "请输入问题");
            return;
        }

        if (file1 == null || !file1.exists()) {
            ToastUtils.showToast(mActivity, "请至少拍一张图片");
            return;
        }
        if (file2 == null || !file2.exists()) {
            ToastUtils.showToast(mActivity, "请拍第二张图片");
            return;
        }
        if (file3 == null || !file3.exists()) {
            ToastUtils.showToast(mActivity, "请拍第三张图片");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("issue_content", feedContent);
        params.put("device_id", deviceId);


        String url = Urls.Url_FeedBack;

        OkHttpUtils.post()//
                .addFile("file", "file01.png", file1)//
                .addFile("file", "file02.png", file2)//
                .addFile("file", "file03.png", file3)//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {

            System.out.println("FeedbackActivity界面+++===失败");
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("反馈界面+++===" + response);
            FeedBackBean bean = new Gson().fromJson(response, FeedBackBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                userAddScore = bean.data.user_add_score;
                // 解开反馈成功界面
                Intent intent = new Intent(mActivity, FeedbackSuccessActivity.class);
                startActivity(intent);
            }
        }
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
                if (TextUtils.equals(str_photo, 1 + "")) {
                    ivFeedback1.setImageBitmap(bitmap);
                    // 图片一
                    file1 = filePhoto02;

                } else if (TextUtils.equals(str_photo, 2 + "")) {
                    ivFeedback2.setImageBitmap(bitmap);
                    //图片二
                    file2 = filePhoto02;

                } else if (TextUtils.equals(str_photo, 3 + "")) {
                    ivFeedback3.setImageBitmap(bitmap);

                    // 图片三
                    file3 = filePhoto02;

                }

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
            if (TextUtils.equals(str_photo, 1 + "")) {
                ivFeedback1.setImageBitmap(bitmap);

                // 图片一
                file1 = filePhoto01;


            } else if (TextUtils.equals(str_photo, 2 + "")) {
                ivFeedback2.setImageBitmap(bitmap);

                // 图片二
                file2 = filePhoto01;

            } else if (TextUtils.equals(str_photo, 3 + "")) {
                ivFeedback3.setImageBitmap(bitmap);

                // 图片三
                file3 = filePhoto01;

            }
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
     * 获取输入的内容
     */
    private void getInputData() {

        feedContent = etFeedback.getText().toString().trim();
    }

}
