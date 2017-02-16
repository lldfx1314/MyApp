package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.DeviceDetailsAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.CheckComplete_Bean;
import com.anhubo.anhubo.bean.ScanBean;
import com.anhubo.anhubo.entity.RxBus;
import com.anhubo.anhubo.entity.event.Exbus_AlterName;
import com.anhubo.anhubo.entity.event.Exbus_RefreshProgressbar;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.buildDetial.TestActivity;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.PopBirthHelper;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.ShowCheckDeviceDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import okhttp3.Call;
import rx.Subscription;
import rx.functions.Action1;

public class QrScanActivity extends BaseActivity implements QRCodeView.Delegate {
    private static final String TAG = QrScanActivity.class.getSimpleName();
    private static final int STARTSCAN = 1;
    @InjectView(R.id.zxingview)
    ZXingView mQRCodeView;
    @InjectView(R.id.proBar)
    ProgressBar proBar;
    @InjectView(R.id.btn_nfcScan)
    Button btnNfcScan;
    @InjectView(R.id.btn_completeCheck)
    Button btnCompleteCheck;
    @InjectView(R.id.btn_light)
    Button btnLight;
    @InjectView(R.id.tv_bigQrNumber)
    TextView tvBigQrNumber;
    @InjectView(R.id.tv_smallQrNumber)
    TextView tvSmallQrNumber;
    @InjectView(R.id.rl_qrNumber)
    RelativeLayout rlQrNumber;
    private String ndIntent;
    private String checkIntent;
    private String exerciseIntent;
    private String testIntent;
    private boolean isEnter = false;
    private String cardNumber;
    private String deviceId;
    private int deviceCheckedNum;
    private String devicesNum;
    private boolean isPermission;
    private Dialog dialog;
    private ListView ck_ListView;
    private TextView deviceName;
    private Button btnCheckMore;
    private Button btnCheckComplete;
    private TextView checkTime;
    private RelativeLayout checkFeedback;
    private TextView tvIssueDes;
    private DeviceDetailsAdapter adapter;
    private PopBirthHelper popBirthHelper;
    private String startTime;
    private String device_type_name;
    private int dateFlag;
    private List<String> require_list;
    private String isId;
    private String isContent;
    private boolean isGetDeviceInfo = false;
    // 选择完成后的有问题的集合
    private ArrayList<Integer> completeList;
    /* 用来记录存在问题的选项，0表示没问题，1表示有问题*/
    private int isProblem = 0;
    /*用来记录对应的position位是否被点击*/
    private Map<Integer, Boolean> map = new HashMap<>();
    private String uid;
    private String businessid;
    private View viewFeed;
    private View viewTime;
    private ScanBean scanBean;
    private ArrayList<String> listResult;
    private boolean isZero;
    private Dialog showDialog;
    private Dialog showDialog1;
    private Subscription rxSubscription;

    @Override
    protected void initConfig() {
        super.initConfig();
        // 获取打开此Activity的意图对象携带的数据
        ndIntent = getIntent().getStringExtra(Keys.NEWDEVICE);
        checkIntent = getIntent().getStringExtra(Keys.CHECK);
        exerciseIntent = getIntent().getStringExtra(Keys.EXERCISE);
        testIntent = getIntent().getStringExtra(Keys.TEST);
        // 获取uid
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);

    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_qr_scan;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        // 设置标题栏提示内容
        setTopBarDesc("检查");
    }

    @Override
    protected void initViews() {
        // 设置扫描二维码的代理
        mQRCodeView.setDelegate(this);

        // 设置完成检查按钮显示的文字
        if (!TextUtils.isEmpty(ndIntent)) {
            setTopBarDesc("新增");
            btnCompleteCheck.setText("完成新增");
            proBar.setVisibility(View.GONE);
            rlQrNumber.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(checkIntent)) {
            setTopBarDesc("检查");
            tvTopBarRight.setVisibility(View.VISIBLE);
            btnCompleteCheck.setText("完成检查");
        } else if (!TextUtils.isEmpty(exerciseIntent)) {
            setTopBarDesc("演练");
            btnCompleteCheck.setText("完成演练");
            proBar.setVisibility(View.GONE);
            rlQrNumber.setVisibility(View.GONE);

        } else if (!TextUtils.isEmpty(testIntent)) {
            setTopBarDesc("测试");
            btnCompleteCheck.setText("完成测试");
            proBar.setVisibility(View.GONE);
            rlQrNumber.setVisibility(View.GONE);
        }
        tvTopBarRight.setOnClickListener(this);
        // 出厂时间的弹窗
        alterTime();
    }

    private void alterTime() {
        popBirthHelper = new PopBirthHelper(mActivity);
        popBirthHelper.setOnClickOkListener(new PopBirthHelper.OnClickOkListener() {
            @Override
            public void onClickOk(String time) {
                /*dialog.dismiss();
                mQRCodeView.startSpot();*/
                if (!TextUtils.isEmpty(time)) {

                    startTime = time;
                } else {
                    ToastUtils.showToast(mActivity, "您所选日期大于当前时间");
                }

            }


        });
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        listResult = new ArrayList<String>();
        // 获取进度条的信息
        getNum();
    }

    /**
     * 获取进度条的信息
     */
    private void getNum() {
        String url = Urls.Url_Get_Num;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("business_id", businessid);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback2());
    }


    class MyStringCallback2 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            LogUtils.e(TAG,":进度条getNum",e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG+":进度条getNum",response);
            CheckComplete_Bean bean = JsonUtil.json2Bean(response, CheckComplete_Bean.class);
            if (bean != null) {

                // 获取到数据
                deviceCheckedNum = bean.data.device_checked_num;
                devicesNum = bean.data.devices_num;

                //动态的设置进度条
                setProgressBar();

            }
        }
    }

    /**
     * 动态的设置进度条
     */
    private void setProgressBar() {
        int maxNum = Integer.parseInt(devicesNum);
        // 做一个处理，防止服务器返回当前进度大于总进度
        if (deviceCheckedNum > maxNum) {
            proBar.setProgress(maxNum);
            tvBigQrNumber.setText(devicesNum);
        } else {
            proBar.setProgress(deviceCheckedNum);
            tvBigQrNumber.setText(deviceCheckedNum + "");
        }
        proBar.setMax(maxNum);
        tvSmallQrNumber.setText(devicesNum);
    }

    @Override
    protected void onLoadDatas() {

        // RxBus
        rxBusOnClickListener();
    }
    // 订阅修改信息的事件
    private void rxBusOnClickListener() {
        // rxSubscription是一个Subscription的全局变量，这段代码可以在onCreate/onStart等生命周期内
        rxSubscription = RxBus.getDefault().toObservable(Exbus_RefreshProgressbar.class)
                .subscribe(new Action1<Exbus_RefreshProgressbar>() {
                               @Override
                               public void call(Exbus_RefreshProgressbar progressbar) {
                                   getNum();
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                // TODO: 处理异常
                            }
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mQRCodeView.onDestroy();
        // 解除订阅
        if(!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.startSpotAndShowRect();
    }


    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }


    /**
     * 震动
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        boolean isanhuboCard = result.startsWith("anhubo", 0);
        boolean isAHBCard = result.startsWith("AHB", 0);
        if (isanhuboCard || isAHBCard) {
            cardNumber = result;
            Log.i(TAG, "result:" + cardNumber);
            // 调用震动的方法
            vibrate();
            mQRCodeView.stopSpot();
            // 拿到数据后做相应的操作
            processData(cardNumber);
        } else {
            AlertDialog builder = new AlertDialog(mActivity).builder();
            builder
                    .setTitle("提示")
                    .setMsg("请使用安互保专用码")
                    .setPositiveButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mQRCodeView.startSpot();
                        }
                    })
                    .setCancelable(false).show();

        }


    }


    /**
     * 拿到数据后做相应的操作
     */
    private void processData(String cardNumber) {
        // 刚进来这个方法就把isEnter直反
        isEnter = false;
        if (!TextUtils.isEmpty(cardNumber)) {
            if (!TextUtils.isEmpty(ndIntent) && !isEnter) {
                /***********新增**************************/
                isEnter = true;
                //新增
                addNewDevice();
            } else if (!TextUtils.isEmpty(checkIntent) && !isEnter) {
                /***********检查**************************/
                isEnter = true;

                // 请求网络的方法
                getData();

            } else if (!TextUtils.isEmpty(exerciseIntent) && !isEnter) {
                /***********演练**************************/
                // 演练,获取DeviceId的最后一位数进行跳转到演练界面
                isEnter = true;
                String lastNumber = cardNumber.substring(cardNumber.length() - 1, cardNumber.length());
                AlertDialog builder = new AlertDialog(mActivity).builder();
                if (Integer.parseInt(lastNumber) <= 1) {
                    builder
                            .setTitle("提示")
                            .setMsg("演练结束")
                            .setCancelable(false).show();
                } else {
                    builder
                            .setTitle("提示")
                            .setMsg("您已到达" + lastNumber + "层")
                            .setCancelable(false).show();
                }
                /**走对话框消失后的回调*/
                builder.setListenerDialog(new AlertDialog.ClickListenerDialog() {
                    @Override
                    public void dismissDialog() {
                        // 打开二维码扫描
                        mQRCodeView.startSpot();
                    }
                });
            } else if (!TextUtils.isEmpty(testIntent) && !isEnter) {
                /***********测试**************************/
                isEnter = true;
                // 测试的方法
                enterTestActivity();
            }

        }
    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    /**
     * 这是检查的网络请求获取数据的方法，使用Post
     */
    private void getData() {
        showDialog = loadProgressDialog.show(mActivity, "正在加载...");
        String url = Urls.Url_Check;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", cardNumber);
        params.put("version", versionName);
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG,":getData",e);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
        }

        @Override
        public void onResponse(String response) {
            showDialog.dismiss();
            LogUtils.eNormal(TAG+":getData",response);
            scanBean = JsonUtil.json2Bean(response, ScanBean.class);
            if (scanBean != null) {
                // 获取到数据置为true
                isGetDeviceInfo = true;
                parseMessage(scanBean);
            }
        }
    }


    /**
     * 拿到数据后判断设备ID是否绑定而跳转到相应界面
     */
    private void parseMessage(ScanBean scanBean) {
        int isExist = scanBean.data.device_exist;//设备号是否在后台存在
        //设备ID
        deviceId = scanBean.data.device_id;
        // 获取数据
        showInfo(scanBean);

        if (isExist == 0) {
            // 跳转到新增页面
            if (TextUtils.equals(deviceId, cardNumber)) {
                Intent intent = new Intent(QrScanActivity.this, Add_Device_Activity.class);
                intent.putExtra(Keys.CARDNUMBER, cardNumber);
                startActivity(intent);

            }

        } else if (isExist == 1) {
            // 弹出对话框
            showCheckDeviceDialog();
        } else {
            ToastUtils.showToast(mActivity, scanBean.msg);
        }
    }

    /**
     * 拿到信息获取设备的相关信息
     */
    private void showInfo(ScanBean scanBean) {
        // 获取到设备名
        device_type_name = scanBean.data.device_type_name;
        // 获取到设备Id
        deviceId = scanBean.data.device_id;
        // 获取到时间标记
        dateFlag = scanBean.data.require_date_flag;
        // 拿到问题集合
        require_list = scanBean.data.require_list;
        // 拿到问题id
        isId = scanBean.data.is_id;
        // 拿到问题内容
        isContent = scanBean.data.is_content;
    }

    /**
     * 接受消息暂停和开始二维码扫描
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STARTSCAN:
                    mQRCodeView.startSpot();
                    break;
            }
        }
    };

    private void showCheckDeviceDialog() {
        // 创建一个对象
        View view = View.inflate(mActivity, R.layout.activity_check_device, null);
        //显示对话框
        ShowCheckDeviceDialog checkDeviceDialog = new ShowCheckDeviceDialog(mActivity, view);
        checkDeviceDialog.setListenerDialog(new ShowCheckDeviceDialog.ClickListenerDialog() {

            @Override
            public void popup() {
                // 初始化completeList
                completeList = new ArrayList<>();
                // 创建一个数组，五个数全是0
                int[] arr = new int[]{0, 0, 0, 0, 0};
                for (int i = 0; i < arr.length; i++) {
                    completeList.add(arr[i]);
                }
                // 为map集合设置默认的值，每个item默认没被点击过
                map.put(0, false);
                map.put(1, false);
                map.put(2, false);
                map.put(3, false);
                map.put(4, false);

                // 每次弹出对话框把集合里的元素全部置为0，防止扫描其他设备受影响
//                for (int i = 0; i < completeList.size(); i++) {
//                    completeList.set(i, 0);
//                }
                // 每次弹出对话框把listResult集合里的元素全部清空
                if (listResult != null) {
                    listResult.clear();
                }

            }

        });

        dialog = checkDeviceDialog.show();
        // 按返回键对话框下去后打开二维码扫描
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // 打开二维码扫描
                if (mQRCodeView != null) {
                    handler.sendEmptyMessage(STARTSCAN);
                }
            }
        });
        // 找控件
        ck_ListView = (ListView) view.findViewById(R.id.check_listView);
        deviceName = (TextView) view.findViewById(R.id.tv_check_device);
        btnCheckMore = (Button) view.findViewById(R.id.check_more);// 更多
        btnCheckComplete = (Button) view.findViewById(R.id.check_complete);//检查完成
        // 出厂日期
        checkTime = (TextView) view.findViewById(R.id.tv_check_time);
        // 出厂日期分割线
        viewTime = view.findViewById(R.id.view_time);
        //待处理反馈
        checkFeedback = (RelativeLayout) view.findViewById(R.id.rl_check_feedback);
        //待处理反馈分割线
        viewFeed = view.findViewById(R.id.view_feed);
        // 问题描述
        tvIssueDes = (TextView) view.findViewById(R.id.tv_issue_des);
        // 获取适配器对象
        adapter = new DeviceDetailsAdapter(this, require_list);
        // 处理出厂日期、待处理反馈
        checkFeedback.setOnClickListener((View.OnClickListener) mActivity);
        checkTime.setOnClickListener((View.OnClickListener) mActivity);
        // 事件处理
        event();
    }

    /**
     * 事件处理
     */
    private void event() {


        // 时间
        if (dateFlag == 1) {
            checkTime.setVisibility(View.VISIBLE);
            viewTime.setVisibility(View.VISIBLE);
        }
        // 问题描述
        if (!TextUtils.isEmpty(isContent)) {
            viewFeed.setVisibility(View.VISIBLE);
            checkFeedback.setVisibility(View.VISIBLE);
            tvIssueDes.setText(isContent);
        }


        // 显示 设备名称
        if (isGetDeviceInfo) {
            deviceName.setText(device_type_name);
        } else {
            deviceName.setText("没有数据");
            isGetDeviceInfo = false;
        }
        // 给listView设置适配器对象
        ck_ListView.setAdapter(adapter);
        // 设置listView的监听
        ck_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_check_device);
                // 获取当前position对应位置的点击记录
                Boolean isClick = map.get(position);
                if (!isClick) {
                    // 表示这个问题存在
                    imageView.setImageResource(R.drawable.fuxuan_input01);
                    // 存在问题就设置为1;
                    isProblem = 1;

                } else {
                    // 这个问题不存在
                    imageView.setImageResource(R.drawable.fuxuan_input02);
                    isProblem = 0;
                }

                // 将对应position位置改为对应的boolean值和值
                map.put(position, !isClick);
                completeList.set(position, isProblem);

            }

        });

        // 设置按钮的点击事件
        btnCheckMore.setOnClickListener(this);
        btnCheckComplete.setOnClickListener(this);

    }


    /**
     * 进入测试页
     */
    private void enterTestActivity() {
        // 跳转到测试页面
        Intent intent = new Intent(mActivity, TestActivity.class);
        intent.putExtra(Keys.CARDNUMBER, cardNumber);
        startActivity(intent);
    }

    /**
     * 这是新增设备的方法
     */
    private void addNewDevice() {
        // 跳转到新增页面
        Intent intent = new Intent(mActivity, Add_Device_Activity.class);
        intent.putExtra(Keys.CARDNUMBER, cardNumber);
        startActivity(intent);
    }

    /**
     * 打开相机失败的回调
     */
    @Override
    public void onScanQRCodeOpenCameraError() {
        isPermission = !isPermission;
        if (isPermission) {
            // Dialog对话框提示用户去设置界面打开权限
            dialog();
        }
        //Log.e(TAG, "打开相机出错");
    }

    /**
     * Dialog对话框提示用户去设置界面打开权限
     */
    protected void dialog() {

        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("前往系统设置的应用列表里打开安互保的相机权限？")
                .setCancelable(false)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 打开系统设置界面
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= 9) {
                            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                        } else if (Build.VERSION.SDK_INT <= 8) {
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                        }
                        startActivity(intent);

                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();


    }

    // 记录灯是否打开
    private boolean isLight = false;

    /**
     * 点击事件
     */
    @OnClick({R.id.btn_nfcScan, R.id.btn_completeCheck, R.id.btn_light})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_nfcScan:
                // 跳转到NFC扫描界面
                jumpNfcScan();
                break;
            case R.id.btn_completeCheck://完成检查
                finish();
                break;
            case R.id.tvTopBarRight:
                // 右上角列表
                startActivity(new Intent(mActivity, DeviceList.class));
                break;
            case R.id.btn_light:
                // 开关灯
                if (!isLight) {
                    mQRCodeView.openFlashlight();
                    btnLight.setBackgroundResource(R.drawable.light_close);
                } else {
                    mQRCodeView.closeFlashlight();
                    btnLight.setBackgroundResource(R.drawable.light_open);
                }
                isLight = !isLight;
                break;
            case R.id.check_more:
                // 更多的点击事件
                dialog.dismiss();
                Intent intent = new Intent(mActivity, FeedbackActivity.class);
                intent.putExtra(Keys.DeviceId, deviceId);
                startActivity(intent);
                break;
            case R.id.check_complete:
                // 检查完成的点击事件
                checkComplete();

                break;
            case R.id.tv_check_time:
                // 时间
                popBirthHelper.show(checkTime);
                break;
            case R.id.rl_check_feedback:
                // 待处理反馈
                dialog.dismiss();
                Intent intent1 = new Intent(mActivity, Pending_FeedbackActivity.class);
                intent1.putExtra(Keys.IsId, isId);
                startActivity(intent1);
                break;
        }
    }

    /**
     * 检查完成的点击事件，请求网络
     */
    private void checkComplete() {

        // 这里是完成的点击事件
        showDialog1 = loadProgressDialog.show(mActivity, "正在提交...");
        String url = Urls.Url_Check_Complete;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid); //这是uid,登录后改成真正的用户
        params.put("device_id", deviceId);
        params.put("device_result", completeList.toString());//选择后的集合
        params.put("business_id", businessid);//这是business_id,登录后改成真正的business_id
        if (!TextUtils.isEmpty(startTime)) {
            params.put("start_time", startTime);
        }

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());
    }


    class MyStringCallback1 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            showDialog1.dismiss();

            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();

            System.out.println("QrScanActivity+++完成===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
//            System.out.println("QrScanActivity界面+checkComplete++"+response);
            showDialog1.dismiss();
            CheckComplete_Bean bean = new Gson().fromJson(response, CheckComplete_Bean.class);
            if (bean != null) {

                dialog.dismiss();
                mQRCodeView.startSpot();
                // 获取到数据
                deviceCheckedNum = bean.data.device_checked_num;
                devicesNum = bean.data.devices_num;


                //动态的设置进度条
                setProgressBar();
                isZero = false;
                for (int i = 0; i < completeList.size(); i++) {
                    Integer integer = completeList.get(i);
                    if (integer == 1) {
                        isZero = true;
                    }
                }
                if (isZero) {
                    // 有问题，跳转到反馈界面
                    Intent intent = new Intent(mActivity, FeedbackActivity.class);
                    intent.putExtra(Keys.DeviceId, deviceId);
                    /******************把问题传递过去********************/
                    for (int i = 0; i < completeList.size(); i++) {
                        Integer integer = completeList.get(i);
                        if (integer == 1) {
                            String s = require_list.get(i);
                            listResult.add(s);
                        }
                    }
                    intent.putExtra(Keys.REQUIRE_LIST, listResult);
                    startActivity(intent);
                } else {
                    // 无问题，提示检查完成
                    ToastUtils.showToast(mActivity, "检查完成");

                }

            }
        }
    }

    /**
     * 跳转到NFC扫描界面
     */
    private void jumpNfcScan() {
        if (!TextUtils.isEmpty(ndIntent)) {
            // 新增
            Intent intent = new Intent(mActivity, NfcScanActivity.class);
            intent.putExtra(Keys.NEWDEVICE, "newDevice");
            startActivity(intent);

        } else if (!TextUtils.isEmpty(checkIntent)) {
            // 检查
            Intent intentCheck = new Intent(mActivity, NfcScanActivity.class);
            intentCheck.putExtra(Keys.CHECK, "Check");
            startActivity(intentCheck);

        } else if (!TextUtils.isEmpty(exerciseIntent)) {
            // 演练
            Intent intentExercise = new Intent(mActivity, NfcScanActivity.class);
            intentExercise.putExtra(Keys.EXERCISE, "Exercise");
            startActivity(intentExercise);
        } else if (!TextUtils.isEmpty(testIntent)) {
            // 测试
            Intent intentExercise = new Intent(mActivity, NfcScanActivity.class);
            intentExercise.putExtra(Keys.TEST, "test");
            startActivity(intentExercise);
        }


    }

}