package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.AdapterDotLine;
import com.anhubo.anhubo.adapter.EvacuateAdapter;
import com.anhubo.anhubo.adapter.EvacuateUserFloorAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.AreaBindingBean;
import com.anhubo.anhubo.bean.EvacuateBean;
import com.anhubo.anhubo.bean.EvacuateCardNumberBean;
import com.anhubo.anhubo.bean.EvacuateTempIdBean;
import com.anhubo.anhubo.bean.QuitEvacuateBean;
import com.anhubo.anhubo.entity.RxBus;
import com.anhubo.anhubo.entity.event.Rxbus_BindBuilding;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.evacuateLine.MyMapPointWithTitleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by LUOLI on 2017/2/23.
 */
public class EvacuateActivity extends BaseActivity {


    private static final String TAG = "EvacuateActivity";
    private static final int EVACUATE_QRCODE = 1;
    private static final int NOBUILDID = 2;
    @InjectView(R.id.evacuate_photoView)
    PhotoView photoView;
    @InjectView(R.id.recyclerview_evacuate_floor)
    RecyclerView recyclerviewFloor;
    @InjectView(R.id.tv_evacuate_floor)
    TextView tvFloor;
    @InjectView(R.id.iv_zoom_in)
    ImageView ivZoomIn;
    @InjectView(R.id.iv_zoom_out)
    ImageView ivZoomOut;
    @InjectView(R.id.evacuate_map_floor)
    RelativeLayout evacuateMapFloor;
    @InjectView(R.id.rl_evacuate)
    RelativeLayout rlEvacuate;
    @InjectView(R.id.tv_evacuate_hint)
    TextView tvEvacuateHint;
    @InjectView(R.id.btn_evacuate_qrcode)
    Button btnEvacuateQrcode;
    private ProgressDialog progressDialog;
    private String picture;
    private String resolution;
    private String underNum;
    private String upNum;
    private ArrayList<String> list;
    private ArrayList<String> qrIdList;
    private AdapterDotLine adapterDotLine;
    private EvacuateAdapter adapter;
    private List<AreaBindingBean.Data.Special_pic> specialPics;
    private ArrayList<MyMapPointWithTitleView> mapPoints;
    private int size;
    private String tempId = "";
    private String uid;
    private String userFloor = "";
    private ArrayList<Object> floorList;
    private ListView listView;
    private PopupWindow popupWindow;
    private String buildingId;
    private Subscription rxSubscription;

    @Override
    protected int getContentViewId() {
        return R.layout.evacuateline_activity;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("疏散");
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
        super.initEvents();
        list = new ArrayList<>();
        qrIdList = new ArrayList<>();
        mapPoints = new ArrayList<>();
        //先加载1层数据

        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        buildingId = SpUtils.getStringParam(mActivity, Keys.BULIDINGID);
        LogUtils.eNormal(TAG, "刚进页面+uid" + uid + "+buildingId:" + buildingId);
        if (TextUtils.isEmpty(buildingId)) {
            showDialog();
        } else {
            getData(1 + "");

        }
        // 禁止photoView缩放
        photoView.setOnTouchListener(null);
        // 显示提示文字
//        tvEvacuateHint.setText("用户扫描完用户所在楼层，1层和避难层所有疏散出口即为疏散成功,(避难层为:" + "0" + ")");
        recyclerviewFloor.setLayoutManager(new LinearLayoutManager(this));
        adapterDotLine = new AdapterDotLine(mActivity, list);
        recyclerviewFloor.setAdapter(adapterDotLine);
        adapterDotLine.setItemClickListener(new MyItemClickListener());
    }

    @Override
    protected void onLoadDatas() {
        iv_basepager_left.setOnClickListener(this);
        rxBusOnClickListener();
    }

    @OnClick({R.id.iv_zoom_in, R.id.iv_zoom_out, R.id.btn_evacuate_qrcode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_evacuate_qrcode:
                //跳转到疏散界面
                startActivityForResult(new Intent(mActivity, EvacuateQrcodeActivity.class), EVACUATE_QRCODE);
                break;
            case R.id.ivTopBarLeft:
                popDialog();

                break;
        }
    }

    /**
     * 監聽
     */
    class MyItemClickListener implements AdapterDotLine.ItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            // 获取选择的楼层
            String string = list.get(position);
            String pic = picture;
            String res = resolution;
            if (specialPics != null && specialPics.size() > 0) {

                for (int i = 0; i < specialPics.size(); i++) {
                    String floor = specialPics.get(i).floor;
                    String picSpec = specialPics.get(i).pic;
                    String newRes = specialPics.get(i).resolution;
                    res = newRes;
                    if (string.startsWith("B")) {
                        LogUtils.eNormal(TAG, "替换前" + string);
                        string = string.replace("B", "-");
                        LogUtils.eNormal(TAG, "替换前" + string);
                    }
                    // 如果当前切换的楼层是特殊楼层，就重新加载地图界面
                    if (TextUtils.equals(floor, string)) {
                        // 地下层数
                        pic = picSpec;
                        break;
                    } else if (TextUtils.equals(floor, string)) {
                        //地上特殊层
                        pic = picSpec;
                        break;
                    }
                }
            }
            LogUtils.eNormal(TAG, "获取楼层的" + string);
            getCurrentDot(string, "");
            setMap(pic, photoView, res);

            adapterDotLine.setSelectedPosition(position);
            adapterDotLine.notifyDataSetChanged();
        }
    }

    private void rxBusOnClickListener() {
        // rxSubscription是一个Subscription的全局变量，这段代码可以在onCreate/onStart等生命周期内
        rxSubscription = RxBus.getDefault().toObservable(Rxbus_BindBuilding.class)
                .subscribe(new Action1<Rxbus_BindBuilding>() {
                               @Override
                               public void call(Rxbus_BindBuilding building) {
                                   buildingId = SpUtils.getStringParam(mActivity, Keys.BULIDINGID);
                                   LogUtils.eNormal(TAG, "收到消息+" + buildingId);
                                   getData(1 + "");
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
        // 解除订阅
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }

    /**
     * 获取地图图片和点集合以及位置
     */
    private void getData(final String floor) {
        showProgressDialog();
        Map<String, String> params = new HashMap<>();
        params.put("building_id", buildingId);
        OkHttpUtils.post()
                .url(Urls.Url_AreaBinding)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        LogUtils.e(TAG, "getData:", e);
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtils.eNormal(TAG, "getData:" + response);
                        closeProgressDialog();
                        if (!TextUtils.isEmpty(response)) {
                            dealWith(response, floor);
                        }
                    }
                });
    }

    /**
     * 处理返回数据
     */
    private void dealWith(String response, String floor) {
        AreaBindingBean bean = JsonUtil.json2Bean(response, AreaBindingBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            if (code == 0) {
                AreaBindingBean.Data data = bean.data;
                // 通用层图片地址
                picture = data.pic;
                //通用图片的分辨率
                resolution = data.resolution;
                //地下的楼层数

                underNum = data.under_num;
                //地上的楼层数
                upNum = data.up_num;
                // 添加楼层数，显示楼层，以便于切换
                addFloor(underNum, upNum);

                // 特殊楼层信息
                specialPics = data.special_pic;
                // 弹窗提示用户选择用户所在楼层
                if (TextUtils.isEmpty(SpUtils.getStringParam(mActivity, Keys.USERFLOOR))) {

                    showPopupwindow();
                }

                getTempId(floor);
            } else if (code == 1) {
                // 说明没有在后台上传图片，返回到上一页
                new AlertDialog(mActivity).builder().setTitle("提示").setMsg(msg).setCancelable(false).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 300);
                    }
                }).show();
            } else if (code == 222) {
                showDialog();

            }

        }
    }

    /**
     * 提示用户去选择建筑
     */
    private void showDialog() {
        // 说明没有在后台上传图片，返回到上一页
        new AlertDialog(mActivity).builder().setTitle("提示").setMsg("您还没有选择建筑，点击确定去选择建筑").setCancelable(false)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mActivity, BuildingActivity.class);
                        intent.putExtra(Keys.NOBUILDID, "nobuildId");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 300);
                    }
                }).show();
    }

    /**
     * 弹窗提示用户选择用户所在楼层
     */
    private void showPopupwindow() {
        floorList = new ArrayList<>();
        // 添加地面以上楼层
        int[] upArr = new int[Integer.parseInt(upNum)];
        for (int i = 0; i < upArr.length; i++) {
            floorList.add(String.valueOf(upArr.length - i));
        }
        // 添加地面以下楼层
        int[] underArr = new int[Integer.parseInt(underNum)];
        for (int i = 0; i < underArr.length; i++) {
            floorList.add("B" + String.valueOf(i + 1));
        }

        View view = View.inflate(mActivity, R.layout.evacuate_dialog, null);

        listView = (ListView) view.findViewById(R.id.lv_evavuate);
        setAdapter(view);
    }

    private void setAdapter(View view) {
        EvacuateUserFloorAdapter adapter = new EvacuateUserFloorAdapter(mActivity, list);
        listView.setAdapter(adapter);
        // 创建一个PopuWidow对象
        popupWindow = new PopupWindow(view, DisplayUtil.dp2px(mActivity, 350), DisplayUtil.dp2px(mActivity, 200));
        //控制键盘是否可以获得焦点
//        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.setTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
        int xPos = windowManager.getDefaultDisplay().getWidth() / 2
                - popupWindow.getWidth() / 2;

//        popupWindow.showAsDropDown(tvArebindingFloor);
        popupWindow.showAtLocation(rlEvacuate, Gravity.CENTER, 0, 0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {

                TextView textView = (TextView) view.findViewById(R.id.tv_evacuate);
                String userFloor = textView.getText().toString().trim();
                SpUtils.putParam(mActivity, Keys.USERFLOOR, userFloor);
                ToastUtils.showToast(EvacuateActivity.this, "用户所在楼层：" + userFloor);

                // 判断当前切换的楼层是否是特殊楼层

                //关闭popupWindow
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            }
        });
    }

    /**
     * 获取疏散Id
     */
    private void getTempId(final String floor) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        OkHttpUtils.post()
                .url(Urls.Url_Evacuate_GetTempId)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        LogUtils.e(TAG, "getTempId:", e);
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtils.eNormal(TAG, "getTempId:" + response);
                        if (!TextUtils.isEmpty(response)) {
                            EvacuateTempIdBean bean = JsonUtil.json2Bean(response, EvacuateTempIdBean.class);
                            int code = bean.code;
                            String msg = bean.msg;
                            if (code == 0) {
                                tempId = bean.data.temp_id;
                                setFloorMap(floor, "");
                            } else {
                                ToastUtils.showToast(mActivity, msg);
                            }
                        }
                    }
                });
    }

    /**
     * 显示楼层地图以及获取点
     */
    private void setFloorMap(String floor, String location) {
        //获取点的信息
        getCurrentDot(floor, location);
        String pic = picture;
        String res = resolution;
        StringBuilder builder = new StringBuilder();
        if (specialPics != null && specialPics.size() > 0) {
            // 遍历特殊层
            for (int i = 0; i < specialPics.size(); i++) {
                String newFloor = specialPics.get(i).floor;
                String picSpec = specialPics.get(i).pic;
                String newRes = specialPics.get(i).resolution;
                // 重新赋值分辨率
                res = newRes;
                // 如果当前切换的楼层是特殊楼层，就重新加载地图界面
                if (TextUtils.equals(newFloor, floor)) {
                    // 地下层数
                    pic = picSpec;
                    break;
                } else if (TextUtils.equals(newFloor, floor)) {
                    //地上特殊层
                    pic = picSpec;

                    break;
                }
                if (!newFloor.contains("-")) {
                    builder.append(newFloor + "层 ");
                }
            }
        }
        // 更改疏散提示
        String string = builder.toString().trim();
//        LogUtils.eNormal(TAG, "哈哈：" + string);
        if (TextUtils.isEmpty(string)) {
            tvEvacuateHint.setText("用户扫描完用户所在楼层，1层和避难层所有疏散出口即为疏散成功(当前建筑无避难层)");

        } else {

            tvEvacuateHint.setText("用户扫描完用户所在楼层，1层和避难层所有疏散出口即为疏散成功(避难层为:" + string + ")");
        }
        if (floor.startsWith("-")) {
            floor = floor.replace("-", "B");
        }
        tvFloor.setText(floor);
        setMap(pic, photoView, res);
    }

    /**
     * 获取点的个数
     */
    private void getCurrentDot(final String floor, final String location) {
        showProgressDialog();
        Map<String, String> params = new HashMap<>();
        params.put("building_id", buildingId);
        params.put("floor", floor);
        params.put("temp_id", tempId);
        OkHttpUtils.post()
                .url(Urls.Url_Evacuate_Dot)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        LogUtils.e(TAG, "getCurrentDot:", e);
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtils.eNormal(TAG, "getCurrentDot:" + response);
                        closeProgressDialog();
                        if (!TextUtils.isEmpty(response)) {
                            dealDotWith(response, location);
                        }
                    }
                });
    }

    private HashMap<Integer, MyMapPointWithTitleView> hashMap = new HashMap();
    private ArrayList<MyMapPointWithTitleView> pointList = new ArrayList();
    private ArrayList<String> locationList = new ArrayList();

    private void dealDotWith(String response, String location) {
        EvacuateBean bean = JsonUtil.json2Bean(response, EvacuateBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            if (code == 0) {
                EvacuateBean.Data data = bean.data;
                String qrNum = data.qr_num;
                List<EvacuateBean.Data.Qrcodes> qrcodes = data.qrcodes;

                MyMapPointWithTitleView myMapPointWithTitleView = null;
                // 遍历集合，，把点添加到地图上
                // 切换楼层后清理之前楼层的点
                clearMapPoints();
                if (qrcodes != null && qrcodes.size() > 0) {
                    size = qrcodes.size();
                    for (int i = 0; i < size; i++) {
                        EvacuateBean.Data.Qrcodes qrcode = qrcodes.get(i);

                        String location1 = qrcode.location;
                        String areaName = qrcode.area_name;
                        String status = qrcode.status;
                        LogUtils.eNormal(TAG, "status:" + status);
                        // 把对应的点显示在地图上
                        String[] split = location1.split(",");
                        double x = Double.parseDouble(split[0]);
                        double y = Double.parseDouble(split[1]);
                        myMapPointWithTitleView = new MyMapPointWithTitleView(mActivity);
                        hashMap.put(i, myMapPointWithTitleView);
                        myMapPointWithTitleView.setFirstX(x);
                        myMapPointWithTitleView.setFirstY(y);
                        myMapPointWithTitleView.setTitle(i + "");
                        if (TextUtils.equals(status, 0 + "")) {

                            myMapPointWithTitleView.setPointIcon(MyMapPointWithTitleView.NO_SELECT);
                        } else {
                            myMapPointWithTitleView.setPointIcon(MyMapPointWithTitleView.SELECT);

                        }
                        addMapPoints(myMapPointWithTitleView);
                    }
                    changeImageView(location);
                }
            } else {
                ToastUtils.showToast(mActivity, msg);
            }

        }
    }

    /**
     * 拿到扫描后点对应的信息进行对应的更改显示的图片
     */
    private void changeImageView(String location) {
        MyMapPointWithTitleView myMapPointWithTitleView;
        if (!TextUtils.isEmpty(location)) {
            locationList.add(location);
            // 把对应的点显示在地图上
            String[] split = location.split(",");
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);
            for (int i = 0; i < size; i++) {
                myMapPointWithTitleView = mapPoints.get(i);
                double firstX = myMapPointWithTitleView.getFirstX();
                double firstY = myMapPointWithTitleView.getFirstY();
                if (firstX == x && firstY == y) {
                    pointList.add(myMapPointWithTitleView);
                    break;
                }
            }
        }
//        LogUtils.eNormal(TAG, "Location的长度:" + locationList.size() + "+点集合的长度+" + pointList.size());
        for (int i = 0; i < locationList.size(); i++) {
            MyMapPointWithTitleView pointView = pointList.get(i);
            String str = locationList.get(i);
            String[] split = str.split(",");
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);
            pointView.setFirstX(x);
            pointView.setFirstY(y);
            pointView.setPointIcon(MyMapPointWithTitleView.SELECT);
        }
    }

    /**
     * 切换楼层时干掉点
     */
    public void changeMapPoints() {
        for (int i = 0; i < mapPoints.size(); i++) {
            MyMapPointWithTitleView pointView = mapPoints.get(i);
            String title = pointView.getTitle();
        }
    }

    /**
     * 遍历添加大点 并显示
     */
    public void addMapPoints(MyMapPointWithTitleView myMapPointWithTitleView) {
        // 添加到集合里面
        mapPoints.add(myMapPointWithTitleView);
        // 设置点显示的位置
        myMapPointWithTitleView.setFirstXShow((float) (myMapPointWithTitleView.getFirstX()));
        myMapPointWithTitleView.setFirstYShow((float) (myMapPointWithTitleView.getFirstY()));
        // 把点添加到视图里面
        evacuateMapFloor.addView(myMapPointWithTitleView);
    }


    /**
     * 切换楼层时干掉点
     */
    public void clearMapPoints() {
        for (int i = 0; i < mapPoints.size(); i++) {
            evacuateMapFloor.removeView(mapPoints.get(i));
//            LogUtils.eNormal(TAG, "清理啦啦");
        }
        this.mapPoints.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EVACUATE_QRCODE && resultCode == 1) {
            String cardNumber = data.getStringExtra(EvacuateQrcodeActivity.CARDNUMBER);
            if (!TextUtils.isEmpty(cardNumber)) {
                //扫描后拿到信息请求网络，获取坐标，让对应点的图片变亮
                undate(cardNumber);
            }
        }
    }

    private void undate(String cardNumber) {
        showProgressDialog();
        Map<String, String> params = new HashMap<>();
        params.put("area_qrcode", cardNumber);
        params.put("building_id", buildingId);
        params.put("temp_id", tempId);
        OkHttpUtils.post()
                .url(Urls.Url_Evacuate_Card)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        LogUtils.e(TAG, "undate:", e);
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtils.eNormal(TAG, "undate:" + response);
                        closeProgressDialog();
                        if (!TextUtils.isEmpty(response)) {
                            if (!TextUtils.isEmpty(response)) {

                                dealCardNumberWith(response);
                            }
                        }
                    }
                });
    }

    private void dealCardNumberWith(String response) {
        EvacuateCardNumberBean bean = JsonUtil.json2Bean(response, EvacuateCardNumberBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            if (code == 0) {
                EvacuateCardNumberBean.Data data = bean.data;
                String location = data.location;
                String areaName = data.area_name;
                String qrId = data.qr_id;
                //　把区域二维码对应的Id添加到集合里面
                qrIdList.add(qrId);
                String status = data.status;
                if (TextUtils.equals(status, 1 + "")) {
                    ToastUtils.showToast(mActivity, "当前二维码已经扫过了");
                }
                String floor = data.floor;
                String trim = tvFloor.getText().toString().trim();
                if (trim.contains("B")) {
                    trim = trim.replace("B", "-");
                }
                if (!TextUtils.isEmpty(floor)) {
                    if (!TextUtils.equals(trim, floor)) {

                        // 拿到楼层判断，若楼层不一样，以显示切换后地图
//                    LogUtils.eNormal(TAG,"切换楼层");
                        setFloorMap(floor, location);
                    } else {
                        // 楼层一样就直接替换对应点的图标
//                    LogUtils.eNormal(TAG,"不切换楼层");
                        changeImageView(location);
                    }

                }
            } else if (code == 111) {
                // 当前建筑不存在该二维码
                ToastUtils.showToast(mActivity, msg);
            } else {
                // 该二维码尚未添加过
                ToastUtils.showToast(mActivity, msg);
            }

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            popDialog();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 弹窗提示
     */
    private void popDialog() {
        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("确定要退出疏散吗？")
                .setCancelable(false)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tellQuitEvacuate();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }

    /**
     * 告诉服务器，用户退出疏散界面
     */

    private void tellQuitEvacuate() {
        // 获取缓存的用户所在楼层
        userFloor = SpUtils.getStringParam(mActivity, Keys.USERFLOOR);
        if (userFloor == null) {
            userFloor = "";
        }
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("building_id", buildingId);
        params.put("floor", userFloor);
        LogUtils.eNormal(TAG, "userFloor:" + userFloor);
        params.put("temp_id", tempId);
        LogUtils.eNormal(TAG, "tempId:" + tempId);
        OkHttpUtils.post()
                .url(Urls.Url_EvacuateQuit)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        LogUtils.e(TAG, "tellQuitEvacuate:", e);
                        //关闭popupWindow,防止WindowManager: android.view.WindowLeaked问题
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                            popupWindow = null;
                        }
                        finish();
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtils.eNormal(TAG, "tellQuitEvacuate:" + response);
                        QuitEvacuateBean bean = JsonUtil.json2Bean(response, QuitEvacuateBean.class);
                        int code = bean.code;
                        //关闭popupWindow,防止WindowManager: android.view.WindowLeaked问题
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                            popupWindow = null;
                        }
                        finish();
                    }
                });
    }


    /**
     * 显示楼层数
     */
    private void addFloor(String underNum, String upNum) {
        // 添加地面以上楼层
        int[] upArr = new int[Integer.parseInt(upNum)];
        for (int i = 0; i < upArr.length; i++) {
            list.add(String.valueOf(upArr.length - i));
        }
        // 添加地面以下楼层
        int[] underArr = new int[Integer.parseInt(underNum)];
        for (int i = 0; i < underArr.length; i++) {
            list.add("B" + String.valueOf(i + 1));
        }

        adapterDotLine.notifyDataSetChanged();
    }

    /**
     * 设置地图的方法
     */
    private void setMap(final String imgurl, final PhotoView photoView, String resolution) {
        resolution = resolution.replace("*", ",");
        LogUtils.eNormal(TAG, "分辨率resolution:" + resolution);
        String[] split = resolution.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        LogUtils.eNormal(TAG, "分辨率x:" + x + "+Y:" + y);

        Glide.with(this)//activty
                .load(imgurl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(/*DisplayUtil.dp2px(mActivity, x), DisplayUtil.dp2px(mActivity, y)*/Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {

                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        // Do something with bitmap here.
                        int height = bitmap.getHeight();//获取bitmap信息，可赋值给外部变量操作，也可在此时行操作。
                        int width = bitmap.getWidth();
                        LogUtils.eNormal(TAG, "图片宽==width:" + width + "+图片高==height:" + height);
                        photoView.setImageBitmap(bitmap);
                    }

                });


//        Glide.with(this)
//                .load(imgurl)
//                .crossFade()
//                .override(x, y)
//                .centerCrop()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(new GlideDrawableImageViewTarget(photoView) {
//                    @Override
//                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
//                        super.onResourceReady(drawable, anim);
//                        //在这里添加一些图片加载完成的操作
//                        LogUtils.eNormal(TAG, "加载完成");
//                    }
//                });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
