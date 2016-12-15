package com.anhubo.anhubo.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Check_UpDateBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.Login_Register.Login_Message;
import com.anhubo.anhubo.ui.activity.Login_Register.RegisterActivity2;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.UpdateManger;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Administrator on 2016/10/9.
 */
public class WelcomeActivity extends BaseActivity {

    private static final int DOWN_ERROR = 1;
    private static final int UPDATA_CLIENT = 2;
    private String uid;
    private String bulidingid;
    private String businessid;
    private String oldversionName;
    private String versionName;
    private String url;
    private boolean isCheck = false;
    private UpdateManger mUpdateManger;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(this);
        super.onResume();
    }


    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }

    @Override
    protected void initEvents() {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        bulidingid = SpUtils.getStringParam(mActivity, Keys.BULIDINGID);
        businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
        oldversionName = SpUtils.getStringParam(mActivity, Keys.VERSIONNAME);
        String[] split = Utils.getAppInfo(mActivity).split("#");
        versionName = split[1];

        // 检查是否有版本升级
        checkUpdate();
//        mUpdateManger = new UpdateManger(mActivity);
//        mUpdateManger.checkUpdateInfo();
    }

    @Override
    protected void onLoadDatas() {
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {

        String url = Urls.Url_Check_Update;
        Map<String, String> params = new HashMap<>();
        params.put("version", versionName);
        params.put("system", "AND");

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    /**
     * 检查更新
     */
    class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {
            System.out.println("UnitFragment界面+++版本升级===没拿到数据" + e.getMessage());
            // 网络出故障，直接进后面界面
            enterMain();
        }

        @Override
        public void onResponse(String response) {
            System.out.println("版本升级" + response);
            Check_UpDateBean bean = new Gson().fromJson(response, Check_UpDateBean.class);
            if (bean != null) {
                int code = bean.code;
                String newVersion = bean.data.new_version;
                String type = bean.data.type;
//                url = bean.data.url;
                url = "http://softfile.3g.qq.com:8080/msoft/179/24659/43549/qq_hd_mini_1.4.apk";
                if (code == 0) {
                    if (!TextUtils.isEmpty(newVersion)) {
                        // 有更新，根据type判断是必须更新还是非必须更新
                        if (TextUtils.equals(type, 0 + "")) {
                            // 强制更新
                            Message msg1 = new Message();
                            msg1.what = UPDATA_CLIENT;
                            handler.sendMessage(msg1);
                        }
                    } else {
                        enterMain();
                    }
                }
            }


        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_CLIENT:
                    //对话框通知用户升级程序
                    dialog();

                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    ToastUtils.showToast(mActivity, "下载新版本失败");
                    enterMain();
                    break;
            }
        }
    };

    /**
     * Dialog对话框提示用户更新
     */
    protected void dialog() {

        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("发现需要升级的版本,现在去更新!")
                .setCancelable(false)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 弹出进度条的对话框，进行一步下载
                        System.out.println("正在下载。。。");
                        downLoadApk();
                    }
                }).show();


    }

    /**
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final ProgressDialog pd;    //进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
        pd.setProgress(0);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(url, pd);
                    //File file = downloadFile(url, pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWN_ERROR;
                    handler.sendMessage(msg);
                    pd.dismiss(); //结束掉进度条对话框
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    public File downloadFile(String url, final ProgressDialog pd) {
        final File[] newFile = {null};
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "gson-2.2.1.jar"){

                    @Override
                    public void onBefore(Request request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void inProgress(float progress, long total) {
                        pd.setProgress((int) (100 * progress));
                        System.out.println((int) (100 * progress));
                        System.out.println("总大小"+(int) total);
                        pd.setMax((int) total);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        //Log.e(TAG, "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File file) {
                        //Log.e(TAG, "onResponse :" + file.getAbsolutePath());
                        newFile[0] = file;
                    }
                });
        return newFile[0];
    }

    public File getFileFromServer(String path, ProgressDialog pd) {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = null;
            try {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                conn.setConnectTimeout(10*1000);
                conn.setRequestMethod("GET");
                int code = conn.getResponseCode();
                //获取到文件的大小

//                System.out.println("总大小+++" + conn.getContentLength());

                pd.setMax(conn.getContentLength()/ 1024/1024);
                InputStream is = conn.getInputStream();
                file = new File(Environment.getExternalStorageDirectory(), "/anhubao.apk");
                //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
                if (file.exists()) {
                    file.delete();
                }
                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                int total = 0;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    total += len;
                    //获取当前下载量
                    pd.setProgress(total/ 1024/1024);
//                    System.out.println("进度+++" + total / 1024/1024);
                }
                fos.close();
                bis.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return file;
        } else {
            return null;
        }
    }

    private void enterMain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(oldversionName)) {
                    enterGuide();
                } else if (!TextUtils.equals(versionName, oldversionName)) {
                    enterGuide();
                } else {
                    if (!TextUtils.isEmpty(uid)) {
                        if (!TextUtils.isEmpty(bulidingid) || !TextUtils.isEmpty(businessid)) {
                            //跳转到主页面
                            enterHome();
                        } else {
                            // 跳到注册第二个界面
                            enterRegister2();
                        }

                    } else {
                        // 无uid，跳到登录界面
                        enterLogin();

                    }
                }
            }
        }, 2000);
    }

    private void enterGuide() {
        startActivity(new Intent(WelcomeActivity.this, GuideActivity.class));
        finish();
    }

    private void enterRegister2() {
        Intent intent = new Intent(WelcomeActivity.this, RegisterActivity2.class);
        intent.putExtra(Keys.UID, uid);
        startActivity(intent);
    }

    private void enterLogin() {
        startActivity(new Intent(WelcomeActivity.this, Login_Message.class));
        finish();
    }

    private void enterHome() {
        startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
