package com.anhubo.anhubo.ui.activity.MyDetial;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.EngineerAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.EngineerBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.ImageTools;
import com.anhubo.anhubo.utils.Keys;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/11/1.
 */
public class EngineerActivity extends BaseActivity {
    private static final int PICTURE = 0;
    private static final int CAMERA = 1;
    @InjectView(R.id.et_engineer_name)
    EditText etEngineerName;
    @InjectView(R.id.tv_engineer_grade)
    TextView tvEngineerGrade;
    @InjectView(R.id.et_engineer_phone)
    EditText etEngineerPhone;
    @InjectView(R.id.iv_engineer1)
    ImageView ivEngineer1;
    @InjectView(R.id.iv_engineer2)
    ImageView ivEngineer2;
    @InjectView(R.id.tv_submit_engineer)
    TextView tvSubmitEngineer;
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


    @OnClick({R.id.tv_engineer_grade, R.id.iv_engineer1, R.id.iv_engineer2, R.id.tv_submit_engineer})
    public void onClick(View view) {
        /**获取输入的内容*/
        getInputData();
        switch (view.getId()) {
            case R.id.tv_engineer_grade:
                // 评级弹出
                /********************************************/
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
            case R.id.tv_submit_engineer:
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
                takePhoto();
                break;
            case R.id.btn_popDialog_photo:
                // 相册
                getPhoto();
                break;
        }
    }

    /**评级弹出*/
    private void showPopupwindow() {
        list = new ArrayList<>();
        String[] arr = new String[]{"建（构）筑物消防员初级","建（构）筑物消防员中级","建（构）筑物消防员高级","建（构）筑物消防员技师",
                "建（构）筑物消防员高级技师","注册消防工程师高级","注册消防工程师一级","注册消防工程师二级"};
        for (int i = 0; i < arr.length; i++) {
            list.add(arr[i]);
        }
        View view = View.inflate(mActivity, R.layout.engineer_dialog, null);

        listView = (ListView) view.findViewById(R.id.lv_engineer);
        setAdapter(view);
    }

    private void setAdapter(View view) {
        EngineerAdapter adapter = new EngineerAdapter(mActivity,list);
        listView.setAdapter(adapter);
        // 创建一个PopuWidow对象
        popupWindow = new PopupWindow(view, DisplayUtil.dp2px(mActivity,270), DisplayUtil.dp2px(mActivity,130));
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

        popupWindow.showAsDropDown(tvEngineerGrade);

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
        progressBar.setVisibility(View.VISIBLE);
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

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();

            System.out.println("IdCardActivity+++===界面失败" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println(response);
            EngineerBean bean = new Gson().fromJson(response, EngineerBean.class);
            if (bean != null) {
                progressBar.setVisibility(View.GONE);
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
        Bitmap photo = BitmapFactory.decodeFile(imagePath);
        try {

            if (photo != null) {
                if (isClick) {
                    //显示图片1
                    ivEngineer1.setImageBitmap(photo);
                    // 给图片一赋值
                    // 把本文件压缩后缓存到本地文件里面
                    savePicture(photo,"photo01");
                    File filePhoto02 = new File(Environment.getExternalStorageDirectory() + "/" + "photo01");
                    file1 = filePhoto02;
                } else {
                    //显示图片2
                    ivEngineer2.setImageBitmap(photo);
//                    给图片二赋值
                    // 把本文件压缩后缓存到本地文件里面
                    savePicture(photo,"photo02");
                    File filePhoto02 = new File(Environment.getExternalStorageDirectory() + "/" + "photo02");
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
        if (isClick) {
            //显示图片一
            ivEngineer1.setImageBitmap(bitmap);
            // 把本文件压缩后缓存到本地文件里面
            savePicture(bitmap,"photo01");
            File filePhoto01 = new File(Environment.getExternalStorageDirectory() + "/" + "photo01");
            // 给图片1赋值
            file1 = filePhoto01;
        } else {
            //显示图片二
            ivEngineer2.setImageBitmap(bitmap);
            // 把本文件压缩后缓存到本地文件里面
            savePicture(bitmap,"photo02");
            File filePhoto01 = new File(Environment.getExternalStorageDirectory() + "/" + "photo02");
            // 给图片1赋值
            file2 = filePhoto01;
        }
    }
    /**
     * 保存图片到本应用下
     **/
    private void savePicture(Bitmap bitmap,String fileName) {

        FileOutputStream fos = null;
        try {//直接写入名称即可，没有会被自动创建；私有：只有本应用才能访问，重新写入内容会被覆盖
            //fos = mActivity.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStream stream = new FileOutputStream(Environment.getExternalStorageDirectory() +"/"+fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);// 把图片写入指定文件夹中

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
