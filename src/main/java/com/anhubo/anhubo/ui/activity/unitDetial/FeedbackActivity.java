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
import android.widget.ScrollView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.FeedBackBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.Keys;
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
import java.util.ArrayList;
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
    RelativeLayout svFeedback;
    @InjectView(R.id.rl_feedback_tag)
    RelativeLayout rlFeedbackTag;
    private InputMethodManager imm;
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private String str_photo;
    private boolean isClick;
    private String feedContent = "";
    private String deviceId;
    private File file1;
    private File file2;
    private File file3;
    private String uid;
    public static int userAddScore;
    private ArrayList<String> listResult;
    private FlowLayout flowLayout;

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

        progressBar.setVisibility(View.VISIBLE);
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
        public void onError(Call call, Exception e) {
            progressBar.setVisibility(View.GONE);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
            System.out.println("FeedbackActivity界面+++===失败" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("反馈界面FeedbackActivity+++===" + response);
            FeedBackBean bean = new Gson().fromJson(response, FeedBackBean.class);
            if (bean != null) {
                progressBar.setVisibility(View.GONE);
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
        Bitmap photo = BitmapFactory.decodeFile(imagePath);
        try {
            if (photo != null) {
                if (TextUtils.equals(str_photo, 1 + "")) {
                    ivFeedback1.setImageBitmap(photo);
                    // 把本文件压缩后缓存到本地文件里面
                    savePicture(photo, "photo01");
                    File filePhoto02 = new File(Environment.getExternalStorageDirectory() + "/" + "photo01");
                    // 图片一
                    file1 = filePhoto02;

                } else if (TextUtils.equals(str_photo, 2 + "")) {
                    ivFeedback2.setImageBitmap(photo);
                    // 把本文件压缩后缓存到本地文件里面
                    savePicture(photo, "photo02");
                    File filePhoto02 = new File(Environment.getExternalStorageDirectory() + "/" + "photo02");
                    //图片二
                    file2 = filePhoto02;

                } else if (TextUtils.equals(str_photo, 3 + "")) {
                    ivFeedback3.setImageBitmap(photo);
                    // 把本文件压缩后缓存到本地文件里面
                    savePicture(photo, "photo03");
                    File filePhoto02 = new File(Environment.getExternalStorageDirectory() + "/" + "photo03");

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
        try {
            fout = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fout);

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
                // 把本文件压缩后缓存到本地文件里面
                savePicture(bitmap, "photo01");
                File filePhoto01 = new File(Environment.getExternalStorageDirectory() + "/" + "photo01");
                // 图片一
                file1 = filePhoto01;


            } else if (TextUtils.equals(str_photo, 2 + "")) {
                ivFeedback2.setImageBitmap(bitmap);
                // 把本文件压缩后缓存到本地文件里面
                savePicture(bitmap, "photo02");
                File filePhoto01 = new File(Environment.getExternalStorageDirectory() + "/" + "photo02");
                // 图片二
                file2 = filePhoto01;

            } else if (TextUtils.equals(str_photo, 3 + "")) {
                ivFeedback3.setImageBitmap(bitmap);
                // 把本文件压缩后缓存到本地文件里面
                savePicture(bitmap, "photo03");
                File filePhoto01 = new File(Environment.getExternalStorageDirectory() + "/" + "photo03");
                // 图片三
                file3 = filePhoto01;

            }
            return true;
        }
        return false;
    }

    /**
     * 保存图片到本应用下
     **/
    private void savePicture(Bitmap bitmap, String fileName) {

        FileOutputStream fos = null;
        try {//直接写入名称即可，没有会被自动创建；私有：只有本应用才能访问，重新写入内容会被覆盖
            //fos = mActivity.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStream stream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);// 把图片写入指定文件夹中

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
