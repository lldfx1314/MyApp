package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Add_Device_Bean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by LUOLI on 2016/9/14.
 */
public class Add_Device_Activity extends BaseActivity implements View.OnClickListener {

    private static final int CAMERA = 1;
    private static final int REQUEST_CODE = 2;
    private static final int REQUESTCODE1 = 3;
    private static final int REQUESTCODE2 = 4;
    private Button complete;
    private LinearLayout takePhoto;
    private EditText device_Name;
    private ImageView iv;
    private EditText device_Area;
    private EditText device_Place;
    private EditText device_Build;
    private EditText device_Unit;
    private String deviceName;
    private String build;
    private String unit;
    private String area;
    private String devicePlace;
    private String cardnumber;
    private String uid;
    private String deviceId;
    private String buildingname;
    private String businessname;
    private File newFile;

    @Override
    protected void initConfig() {
        // 获取开启此Activity的intent对象携带的数据
        Intent intent = getIntent();
        cardnumber = intent.getStringExtra(Keys.CARDNUMBER);
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        businessname = SpUtils.getStringParam(mActivity, Keys.BUSINESSNAME);
        buildingname = SpUtils.getStringParam(mActivity, Keys.BUILDINGNAME);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_device;
    }


    @Override
    protected void initViews() {
        // 所属建筑
        device_Build = (EditText) findViewById(R.id.et_add_build);
        // 单位
        device_Unit = (EditText) findViewById(R.id.et_add_unit);
        // 所在区域
        device_Area = (EditText) findViewById(R.id.et_add_area);
        // 所属位置
        device_Place = (EditText) findViewById(R.id.et_add_name);
        // 设备名称
        device_Name = (EditText) findViewById(R.id.et_add_type);
        //　拍照验证
        takePhoto = (LinearLayout) findViewById(R.id.ll_add_photo);
        // 完成添加Button
        complete = (Button) findViewById(R.id.add_complete_btn);
        //　显示图片的控件
        iv = (ImageView) findViewById(R.id.iv);

        //设置初始值
        device_Build.setText(buildingname);
        device_Unit.setText(businessname);


        // 设置点击事件
        device_Build.setOnClickListener(this);
        device_Unit.setOnClickListener(this);
        device_Name.setOnClickListener(this);
        complete.setOnClickListener(this);
        takePhoto.setOnClickListener(this);

        // 设置显示内容
        setTopBarDesc("新增设备");
    }

    @Override
    protected void onLoadDatas() {


    }

    /**
     * 返回本界面执行的方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null) {
            switch (requestCode) {
                case REQUEST_CODE:
                    String str = intent.getStringExtra(Keys.STR);
                    if (!TextUtils.isEmpty(str)) {
                        device_Name.setText(str);
                    }
                    break;
                case CAMERA:
                    // 从相机界面返回
                    if (resultCode == Activity.RESULT_OK) {
                        showPhoto01(intent);
                    }
                    break;
                case REQUESTCODE1:
                    if (resultCode == 1) {
                        String stringExtra = intent.getStringExtra(Keys.STR);
                        if (!TextUtils.isEmpty(stringExtra)) {
                            device_Build.setText(stringExtra);
                        }
                    }
                    break;
                case REQUESTCODE2:
                    if (resultCode == 2) {
                        String stringExtra = intent.getStringExtra(Keys.STR);
                        if (!TextUtils.isEmpty(stringExtra)) {
                            device_Unit.setText(stringExtra);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 从相机界面返回
     */
    private void showPhoto01(Intent intent) {
        String sdState = Environment.getExternalStorageState();
        if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showLongToast(mActivity, "sd card unmount");
            return;
        }
        new DateFormat();
        String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        Bundle bundle = intent.getExtras();
        //获取相机返回的数据，并转换为图片格式
        Bitmap bitmap = (Bitmap) bundle.get("data");
        FileOutputStream fout = null;
        File file = new File("/sdcard/photo_anhubo/");
        file.mkdirs();
        String filename = file.getPath() + name;
        //filePhoto = new File(filename);//图片的文件对象
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
        //显示图片
        iv.setImageBitmap(bitmap);
        //file1 = filePhoto;
        // 把本文件压缩后缓存到本地文件里面
        savePicture(bitmap, "photo01");
        File filePhoto01 = new File(Environment.getExternalStorageDirectory() + "/" + "photo01");
        newFile = filePhoto01;
    }

    /**
     * 保存图片到本应用下
     **/
    private void savePicture(Bitmap bitmap, String fileName) {

        FileOutputStream fos = null;
        try {//直接写入名称即可，没有会被自动创建；私有：只有本应用才能访问，重新写入内容会被覆盖
            //fos = mActivity.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStream stream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + fileName);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_add_build://所属建筑
                // 建筑的点击事件，地图poi
                Intent intent = new Intent(mActivity, BuildingActivity.class);
                startActivityForResult(intent, REQUESTCODE1);
                break;
            case R.id.et_add_unit://所属单位
                // 单位的点击事件，地图poi
                Intent intent2 = new Intent(mActivity, BusinessActivity.class);
                startActivityForResult(intent2, REQUESTCODE2);
                break;
            case R.id.et_add_type://设备名称的点击事件
                startActivityForResult(new Intent(Add_Device_Activity.this, DeviceName_Activity.class), REQUEST_CODE);
                break;
            case R.id.ll_add_photo://添加拍照的点击事件
                // 定义一个方法进行展示拍到的图片
                takePhoto();
                break;
            case R.id.add_complete_btn://添加完成的点击事件
                addComplete();
                break;
        }
    }

    /**
     * 处理拍照的相关事项
     */
    private void takePhoto() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA);
    }

    /**
     * 点击添加完成的界面的按钮的点击事件
     */
    private void addComplete() {
        // 点击完成的时候获取输入框内的内容，调用接口，使用post方法把数据添加到后台
        getInputData();


        if (TextUtils.isEmpty(build)) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("请选择建筑")
                    .setCancelable(true).show();
            //ToastUtils.showToast(mActivity, "请选择建筑");
            return;
        }
        if (TextUtils.isEmpty(unit)) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("请选择单位")
                    .setCancelable(true).show();
            //ToastUtils.showToast(mActivity, "请选择单位");
            return;
        }
        if (TextUtils.isEmpty(area)) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("区域不能为空")
                    .setCancelable(true).show();
            //ToastUtils.showToast(mActivity, "区域不能为空");
            return;
        }
        if (TextUtils.isEmpty(devicePlace)) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("设备位置不能为空")
                    .setCancelable(true).show();
            //ToastUtils.showToast(mActivity, "设备位置不能为空");
            return;
        }
        if (TextUtils.isEmpty(deviceName)) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("亲，请拍取设备照片")
                    .setCancelable(true).show();
            //ToastUtils.showToast(mActivity, "设备名称不能为空");
            return;
        }


        if (newFile == null) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("亲，请拍取设备照片")
                    .setCancelable(true).show();
            //ToastUtils.showToast(mActivity, "请拍取设备照片");
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            String url = Urls.Url_Add;
            Map<String, String> params = new HashMap<>();
            params.put("uid", uid);
            params.put("qrcode", cardnumber);
            params.put("building_name", build);// 所属建筑
            params.put("business_name", unit);//所属单位
            params.put("area_name", area);//所属区域
            params.put("name", devicePlace);// 设备位置
            params.put("type", deviceName);// 设备名称
            OkHttpUtils.post()//
                    .addFile("file", "file01.png", newFile)//
                    .url(url)//
                    .params(params)//
                    .build()//
                    .execute(new MyStringCallback());
        }


    }

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            System.out.println("Add_Device_Activity+++===获取数据失败+++===" + e.getMessage());
            progressBar.setVisibility(View.GONE);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(false).show();
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("添加界面+++==="+response);
            if (!TextUtils.isEmpty(response)) {
                Add_Device_Bean addDeviceBean = new Gson().fromJson(response, Add_Device_Bean.class);
                if (addDeviceBean != null) {

                    progressBar.setVisibility(View.GONE);
                    deviceId = addDeviceBean.data.device_id;
                    ToastUtils.showToast(mActivity, "添加成功");
                    finish();
                } else {
                    System.out.println("Add_Device_Activity+++===没获取bean对象");
                }
            }

        }
    }


    /**
     * 获取输入的内容
     */
    private void getInputData() {
        build = device_Build.getText().toString().trim();//所属建筑
        unit = device_Unit.getText().toString().trim();//所属单位
        area = device_Area.getText().toString().trim();//所属区域
        devicePlace = device_Place.getText().toString().trim();//设备位置
        deviceName = device_Name.getText().toString().trim();//设备名称
    }
}