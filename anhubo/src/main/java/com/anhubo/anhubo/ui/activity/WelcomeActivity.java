package com.anhubo.anhubo.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Check_UpDateBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.Login_Register.Login_Message;
import com.anhubo.anhubo.ui.activity.Login_Register.RegisterActivity2;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/9.
 */
public class WelcomeActivity extends BaseActivity {

    private static final int DOWN_ERROR = 1;
    private static final int UPDATA_CLIENT = 2;
    private static final int NET_ERROR = 3;
    private static final int ENTER_MAIN = 4;
    private static final int SELECT_UPDATA_CLIENT = 5;
    private String uid;
    private String bulidingid;
    private String businessid;
    private String oldversionName;
    private String versionName;
    private String url;
    private String newVersion;
    private String cancel_update_versionName;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        bulidingid = SpUtils.getStringParam(mActivity, Keys.BULIDINGID);
        businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
        oldversionName = SpUtils.getStringParam(mActivity, Keys.VERSIONNAME);
        String[] split = Utils.getAppInfo(mActivity).split("#");
        versionName = split[1];
        
//        取出上次取消更新后保存的版本号
        cancel_update_versionName = SpUtils.getStringParam(mActivity, Keys.CANCEL_UPDATE_VERSION, null);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 检查是否有版本升级
                checkUpdate();
            }
        }, 2000);

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
    private Message msg = Message.obtain();

    class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {

            System.out.println("WelcomeActivity界面+++版本升级===没拿到数据" + e.getMessage());
            // 网络出故障，直接进后面界面
            Toast.makeText(mActivity,"网络有问题，请检查",Toast.LENGTH_SHORT).show();
            msg.what = NET_ERROR;
            handler.sendMessage(msg);
        }

        @Override
        public void onResponse(String response) {
            System.out.println("WelcomeActivity界面+++版本升级" + response);
            Check_UpDateBean bean = new Gson().fromJson(response, Check_UpDateBean.class);
            if (bean != null) {
                int code = bean.code;
                newVersion = bean.data.new_version;
                String type = bean.data.type;
                url = bean.data.url;
                if (code == 0) {
                    if (!TextUtils.isEmpty(newVersion)) {

                        if(!TextUtils.isEmpty(cancel_update_versionName)&&TextUtils.equals(newVersion,cancel_update_versionName)){
                            // 保存的上次更新不为空且两次更新版本号相同，则代表用户上次取消了更新，本次就不提示，直接进入下一步
                            msg.what = ENTER_MAIN;
                            handler.sendMessage(msg);
                        }else {
                            // 有更新，根据type判断是必须更新还是非必须更新
                            if (TextUtils.equals(type, 0 + "")) {
                                // 0,强制更新
                                msg.what = UPDATA_CLIENT;
                                handler.sendMessage(msg);
                            } else if (TextUtils.equals(type, 1 + "")) {
                                // 1,选择更新
                                msg.what = SELECT_UPDATA_CLIENT;
                                handler.sendMessage(msg);
                            } else {
                                // 2,什么都不管
                                msg.what = ENTER_MAIN;
                                handler.sendMessage(msg);
                            }
                        }
                    } else {
                        msg.what = ENTER_MAIN;
                        handler.sendMessage(msg);
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
                    //对话框通知用户升级程序  强制
                    dialog_force();
                    break;
                case SELECT_UPDATA_CLIENT:
                    //对话框通知用户升级程序  选择
                    dialog_select();
                    break;
                case ENTER_MAIN:
                    // 不更新，进入主界面
                    enterMain();
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    enterMain();
                    break;
                case NET_ERROR:
                    //网络错误，下载apk失败
                    enterMain();
                    break;
            }
        }
    };

    /**
     * Dialog对话框提示用户更新 强制
     */
    protected void dialog_force() {

        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("发现需要升级的版本,现在去更新!")
                .setCancelable(false)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downLoadApk();
//                        Toast.makeText(mActivity, "正在下载。。。", Toast.LENGTH_SHORT).show();
//                        enterMain();
                    }
                }).show();


    }

    /**
     * Dialog对话框提示用户更新 选择
     */
    protected void dialog_select() {

        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("发现需要升级的版本,现在去更新!")
                .setCancelable(false)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downLoadApk();
//                        Toast.makeText(mActivity, "正在下载。。。", Toast.LENGTH_SHORT).show();
//                        enterMain();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 取消更新后，下次进来后如果还是相同版本应该不提醒用户了
                        SpUtils.putParam(mActivity,Keys.CANCEL_UPDATE_VERSION,newVersion);
                        enterMain();
                    }
                })
                .show();


    }

    /**
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final ProgressDialog pd;    //进度条对话框
        pd = new  ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.setCancelable(false);
        pd.show();
        new Thread() {
            @Override
            public void run() {
                Message msg1 = Message.obtain();
                try {
                    File file = getFileFromServer(url, pd);
                    if (file != null) {
                        installApk(file);
                    } else {
                        msg1.what = DOWN_ERROR;
                        handler.sendMessage(msg1);
                        Toast.makeText(mActivity,"下载失败...",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    msg1.what = DOWN_ERROR;
                    handler.sendMessage(msg1);
                    e.printStackTrace();
                    Toast.makeText(mActivity,"下载失败...",Toast.LENGTH_SHORT).show();
                    System.out.println("下载失败..." + e.getMessage());
                }finally {
                    pd.dismiss();
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
//        防止用户下载完进入安装界面后取消安装返回本应用界面后出现应用无任何操作，因此进入安装界面后就把本界面finish掉
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    public File getFileFromServer(String path, ProgressDialog pd) {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = null;
            try {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10 * 1000);
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                int contentLength = conn.getContentLength();
                pd.setMax(contentLength/1024/1024);
//                System.out.println("最大值+" + contentLength/1024/1024);
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
//                    System.out.println("进度+" + total/1024/1024);
                    pd.setProgress(total/1024/1024);
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

        if (TextUtils.isEmpty(oldversionName)) {
//            首次安装，进入引导页
            enterGuide();
        } else if (!TextUtils.equals(versionName, oldversionName)) {
//            新版本，进入引导页
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
