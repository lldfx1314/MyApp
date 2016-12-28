package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Peeding_FeedBackBean;
import com.anhubo.anhubo.bean.Pending_FeedbackBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/11/14.
 */
public class Pending_FeedbackActivity extends BaseActivity {

    private static final int CAMERA = 1;
    private static final int PICTURE = 0;
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
    @InjectView(R.id.tv_issue_photo)
    TextView tvIssuePhoto;

    @InjectView(R.id.ll_issue_photo)
    LinearLayout llIssuePhoto;
    private String isId;
    private String str_photo;
    private boolean isClick;
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private File file1;
    private File file2;
    private File file3;
    private String uid;

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
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        // 这里是完成的点击事件
        progressBar.setVisibility(View.VISIBLE);
        String url = Urls.Url_Check_Pending_FeedBack;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("is_id", isId);

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
                // 提交
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
                .setMsg("请至少拍一张修复后的设备照片")
                .setCancelable(false)
                .show();
    }


    /**
     * 提交
     */
    private void submit() {
        if (file1 == null && file2 == null && file3 == null) {
            dialog();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("is_id", isId);

        String url = Urls.Url_PendFeedBack;

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
                .execute(new MyStringCallback1());
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    private Handler handler = new Handler();

    class MyStringCallback1 extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {
            progressBar.setVisibility(View.GONE);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(false).show();
            System.out.println("FeedbackActivity界面提交+++===失败" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            progressBar.setVisibility(View.GONE);

            //System.out.println("待反馈界面Pending_FeedbackActivity+++===" + response);

            Peeding_FeedBackBean bean = new Gson().fromJson(response, Peeding_FeedBackBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                // 返回到上个页面
                if (code == 0) {

                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("反馈已处理完成")
                            .setCancelable(false)
                            .setPositiveButton("确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    handler.postDelayed(new MyRunnable(),500);
                                }
                            }).show();


                }
            }
        }
    }
    /**执行推出界面操作*/
    class MyRunnable implements Runnable {

        @Override
        public void run() {
            finish();
        }
    }

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            progressBar.setVisibility(View.GONE);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(false).show();
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
                int size = isPic.size();
                if (size > 0) {


                    if (size == 1) {
                        for (int i = 0; i < isPic.size(); i++) {
                            setHeaderIcon(ivIssue1, isPic.get(i));
                        }
                    } else if (size == 2) {
                        for (int i = 0; i < isPic.size(); i++) {
                            if (i == 0) {
                                setHeaderIcon(ivIssue1, isPic.get(i));
                            } else if (i == 1) {
                                setHeaderIcon(ivIssue2, isPic.get(i));
                            }
                        }
                    } else if (size == 3) {
                        for (int i = 0; i < isPic.size(); i++) {
                            if (i == 0) {
                                setHeaderIcon(ivIssue1, isPic.get(i));
                            } else if (i == 1) {
                                setHeaderIcon(ivIssue2, isPic.get(i));
                            } else if (i == 2) {
                                setHeaderIcon(ivIssue3, isPic.get(i));
                            }
                        }
                    }
                } else {
                    // 动态改变空间的高度
                    tvIssuePhoto.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0));
                    llIssuePhoto.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0));
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
        Bitmap photo = BitmapFactory.decodeFile(imagePath);
        try {


            if (photo != null) {
                if (TextUtils.equals(str_photo, 1 + "")) {
                    ivPendPhoto1.setImageBitmap(photo);// 把本文件压缩后缓存到本地文件里面
                    savePicture(photo, "photo01");
                    File filePhoto02 = new File(Environment.getExternalStorageDirectory() + "/" + "photo01");

                    // 图片一
                    file1 = filePhoto02;

                } else if (TextUtils.equals(str_photo, 2 + "")) {
                    ivPendPhoto2.setImageBitmap(photo);
                    // 把本文件压缩后缓存到本地文件里面
                    savePicture(photo, "photo02");
                    File filePhoto02 = new File(Environment.getExternalStorageDirectory() + "/" + "photo02");
                    //图片二
                    file2 = filePhoto02;

                } else if (TextUtils.equals(str_photo, 3 + "")) {
                    ivPendPhoto3.setImageBitmap(photo);
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
                ivPendPhoto1.setImageBitmap(bitmap);
                // 图片一
                // 把本文件压缩后缓存到本地文件里面
                savePicture(bitmap, "photo01");
                File filePhoto01 = new File(Environment.getExternalStorageDirectory() + "/" + "photo01");
                file1 = filePhoto01;


            } else if (TextUtils.equals(str_photo, 2 + "")) {
                ivPendPhoto2.setImageBitmap(bitmap);
                // 把本文件压缩后缓存到本地文件里面
                savePicture(bitmap, "photo02");
                File filePhoto01 = new File(Environment.getExternalStorageDirectory() + "/" + "photo02");
                // 图片二
                file2 = filePhoto01;

            } else if (TextUtils.equals(str_photo, 3 + "")) {
                ivPendPhoto3.setImageBitmap(bitmap);
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
     * 设置照片
     */
    private void setHeaderIcon(final ImageView iv, String imgurl) {
        OkHttpUtils
                .get()//
                .url(imgurl)//
                .tag(this)//
                .build()//
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                        System.out.println("Pending_FeedbackActivity设置图片+++===" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        iv.setImageBitmap(bitmap);
                    }
                });
    }

}
