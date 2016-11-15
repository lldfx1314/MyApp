package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.FeedBackBean;
import com.anhubo.anhubo.bean.Pending_FeedbackBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.ImageTools;
import com.anhubo.anhubo.utils.Keys;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/11/14.
 */
public class Pending_FeedbackActivity extends BaseActivity {

    private static final int CAMERA = 0;
    private static final int PICTURE = 1;
    @InjectView(R.id.tv_issue_time)
    TextView tvIssueTime;
    @InjectView(R.id.tv_issue_detail)
    TextView tvIssueDetail;
    @InjectView(R.id.iv_issue1)
    ImageView ivIssue1;
    @InjectView(R.id.iv_issue2)
    ImageView ivIssue2;
    @InjectView(R.id.iv_issue3)
    ImageView ivIssue3;
    @InjectView(R.id.iv_pend_photo1)
    ImageView ivPendPhoto1;
    @InjectView(R.id.iv_pend_photo2)
    ImageView ivPendPhoto2;
    @InjectView(R.id.iv_pend_photo3)
    ImageView ivPendPhoto3;
    @InjectView(R.id.btn_noDeal)
    Button btnNoDeal;
    @InjectView(R.id.btn_complete_Deal)
    Button btnCompleteDeal;
    private String isId;
    private String str_photo;
    private boolean isClick;
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private File filePhoto01;
    private File file1;
    private File file2;
    private File file3;
    private File filePhoto02;

    @Override
    protected void initConfig() {
        super.initConfig();
        isId = getIntent().getStringExtra(Keys.IsId);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_pending_feedback;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("问题描述");
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        // 这里是完成的点击事件
        progressBar.setVisibility(View.VISIBLE);
        String url = Urls.Url_Check_Pending_FeedBack;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("is_id", isId); //这是uid,登录后改成真正的用户

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }



    @OnClick({R.id.iv_pend_photo1, R.id.iv_pend_photo2, R.id.iv_pend_photo3, R.id.btn_noDeal, R.id.btn_complete_Deal})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_pend_photo1:
                /**弹出拍照对话框*/
                str_photo = 1 + "";
                showDialog();
                break;
            case R.id.iv_pend_photo2:
                /**弹出拍照对话框*/
                str_photo = 2 + "";
                showDialog();
                break;
            case R.id.iv_pend_photo3:
                /**弹出拍照对话框*/
                str_photo = 3 + "";
                showDialog();
                break;
            case R.id.btn_noDeal:
                finish();
                break;
            case R.id.btn_complete_Deal:
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

    private void submit() {
        if (file1 == null || !file1.exists()) {
            ToastUtils.showToast(mActivity, "请拍第一张图片");
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
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();

        params.put("uid", uid);
        params.put("is_id", isId);

        String url = Urls.Url_PendFeedBack;


        OkHttpUtils.post()//
                .addFile("file", "file01.png", file1)//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());
    }

    class MyStringCallback1 extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {

            System.out.println("FeedbackActivity界面提交+++===失败");
        }

        @Override
        public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
            System.out.println("待反馈界面+++===" + response);
            /*FeedBackBean bean = new Gson().fromJson(response, FeedBackBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                userAddScore = bean.data.user_add_score;
                // 打开反馈成功界面
                Intent intent = new Intent(mActivity, FeedbackSuccessActivity.class);
                startActivity(intent);
            }*/
        }
    }


    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("Pending_FeedbackActivity+++===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("Pending_FeedbackActivity" + response);
            Pending_FeedbackBean bean = new Gson().fromJson(response, Pending_FeedbackBean.class);
            if (bean != null) {
                progressBar.setVisibility(View.GONE);
                String msg = bean.msg;
                int code = bean.code;
                String isContent = bean.data.is_content;
                String isTime = bean.data.is_time;
                List<String> isPic = bean.data.is_pic;

                tvIssueTime.setText("反馈时间： " + isTime);
                tvIssueDetail.setText(isContent);
                // 对图片做判断
                for (int i = 0; i < isPic.size(); i++) {


                    if (isPic.size() == 1) {
                        setHeaderIcon(ivIssue1, isPic.get(i).replace("anhubo.com", "115.28.56.139"));
                    } else if (isPic.size() == 2) {
                        setHeaderIcon(ivIssue1, isPic.get(i).replace("anhubo.com", "115.28.56.139"));
                        setHeaderIcon(ivIssue2, isPic.get(i).replace("anhubo.com", "115.28.56.139"));
                    } else if (isPic.size() == 3) {
                        setHeaderIcon(ivIssue1, isPic.get(i).replace("anhubo.com", "115.28.56.139"));
                        setHeaderIcon(ivIssue2, isPic.get(i).replace("anhubo.com", "115.28.56.139"));
                        setHeaderIcon(ivIssue3, isPic.get(i).replace("anhubo.com", "115.28.56.139"));
                    }

                }
            }
        }
    }

    @Override
    protected void onLoadDatas() {

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
                    ivPendPhoto1.setImageBitmap(bitmap);
                    // 图片一
                    file1 = filePhoto02;

                } else if (TextUtils.equals(str_photo, 2 + "")) {
                    ivPendPhoto2.setImageBitmap(bitmap);
                    //图片二
                    file2 = filePhoto02;

                } else if (TextUtils.equals(str_photo, 3 + "")) {
                    ivPendPhoto3.setImageBitmap(bitmap);

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
                ivPendPhoto1.setImageBitmap(bitmap);

                // 图片一
                file1 = filePhoto01;


            } else if (TextUtils.equals(str_photo, 2 + "")) {
                ivPendPhoto2.setImageBitmap(bitmap);

                // 图片二
                file2 = filePhoto01;

            } else if (TextUtils.equals(str_photo, 3 + "")) {
                ivPendPhoto3.setImageBitmap(bitmap);

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

    /**设置照片*/
    private void setHeaderIcon(final ImageView iv, String imgurl) {
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
                        iv.setImageBitmap(bitmap);
                    }
                });
    }
}
