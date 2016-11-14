package com.anhubo.anhubo.ui.activity.MyDetial;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.IdCardBean;
import com.anhubo.anhubo.bean.MsgPerfectLowerBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.ImageTools;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/11/1.
 */
public class IdCardActivity extends BaseActivity {
    private static final int CAMERA = 0;
    private static final int PICTURE = 1;
    @InjectView(R.id.et_idcard_name)
    EditText etIdcardName;
    @InjectView(R.id.et_idcard_phone)
    EditText etIdcardPhone;
    @InjectView(R.id.iv_card1)
    ImageView ivCard1;
    @InjectView(R.id.iv_card2)
    ImageView ivCard2;
    @InjectView(R.id.tv_submit_idcard)
    TextView tvSubmitIdcard;
    private String name;
    private String idcardPhone;
    private boolean isClick = false;//判断是正面还是反面
    private boolean isClick1 = false;// 判断是拍照还是相册
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private File filePhoto01;
    private File filePhoto02;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_idcard;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("身份证认证");
    }

    @Override
    protected void initEvents() {
        super.initEvents();

    }

    @Override
    protected void onLoadDatas() {

    }

    @OnClick({R.id.iv_card1, R.id.iv_card2, R.id.tv_submit_idcard})
    public void onClick(View view) {
        /**获取输入的内容*/
        getInputData();

        switch (view.getId()) {
            case R.id.iv_card1:
                // 身份证正面 底部弹出对话框
                isClick = true;
                showDialog();
                break;
            case R.id.iv_card2:
                // 身份证背面 底部弹出对话框
                isClick = false;
                showDialog();
                break;
            case R.id.tv_submit_idcard:
                // 对输入的信息做判断
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToast(mActivity, "请输入姓名");
                    return;
                }
                // 身份证认证
                if (!TextUtils.isEmpty(idcardPhone)) {
                    boolean iscardPhone = Utils.isRightIdcard(idcardPhone);
                    if (!iscardPhone) {
                        ToastUtils.showToast(mActivity, "请输入正确的身份证号码");
                        return;
                    }
                } else {
                    ToastUtils.showToast(mActivity, "请输入身份证号码");
                    return;
                }

                /**身份证号码*/
                submit();
                break;
            case R.id.btn_popDialog_takephoto:
                // 拍照
                takePhoto();
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
     * 身份证号码
     */
    private void submit() {
        // 获取
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);

        if (file1 == null || !file1.exists()) {
            ToastUtils.showLongToast(mActivity, "请先拍照或者获取图库图片");
            return;
        }
        if (file2 == null || !file2.exists()) {
            ToastUtils.showLongToast(mActivity, "请先拍照或者获取图库图片");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("card_num", idcardPhone);
        params.put("true_name", name);
        String url = Urls.Url_IdCard;

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
        public void onError(Call call, Exception e) {
            ToastUtils.showToast(mActivity, "网络有问题，请检查");

            System.out.println("IdCardActivity+++===界面失败" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println(response);
            IdCardBean bean = new Gson().fromJson(response, IdCardBean.class);
            if (bean != null) {
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
                    }, 2000);
                }
            }

        }
    }

    /**
     * 从相册获取
     */
    private void getPhoto() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICTURE);

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
                if (isClick) {
                    //显示正面图片
                    ivCard1.setImageBitmap(bitmap);
                    // 给正面图片赋值
                    file1 = filePhoto02;
                } else {
                    //显示反面图片
                    ivCard2.setImageBitmap(bitmap);
                    // 给背面图片赋值
                    file2 = filePhoto02;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            c.close();
        }

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
            //显示正面图片
            ivCard1.setImageBitmap(bitmap);
            // 给正面图片赋值
            file1 = filePhoto01;
        } else {
            //显示反面图片
            ivCard2.setImageBitmap(bitmap);
            // 给背面图片赋值
            file2 = filePhoto01;
        }
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
        name = etIdcardName.getText().toString().trim();
        idcardPhone = etIdcardPhone.getText().toString().trim();
    }
}
