package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.EvacuateUserFloorAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.AreaBindingBean;
import com.anhubo.anhubo.bean.AreaBindingCompleteBean;
import com.anhubo.anhubo.bean.AreaBindingDotBean;
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
import rx.Subscription;
import rx.functions.Action1;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by LUOLI on 2017/2/23.
 */
public class AreaBindingActivity extends BaseActivity implements View.OnLongClickListener {

    private static final String TAG = "AreaBindingActivity";
    public static final String FLOOR = "floor";
    public static final String MESSAGE = "message";
    @InjectView(R.id.recyclerview_floor)
    RecyclerView recyclerviewFloor;
    @InjectView(R.id.rl_areabind)
    RelativeLayout rlAreabind;
    @InjectView(R.id.rl_map)
    RelativeLayout rlMap;
    @InjectView(R.id.iv_areabinding_zoom_in)
    ImageView ivZoomIn;
    @InjectView(R.id.iv_areabinding_zoom_out)
    ImageView ivZoomOut;
    @InjectView(R.id.map_floor)
    RelativeLayout mapFloor;
    @InjectView(R.id.tv_arebinding_build)
    TextView tvArebindingBuild;
    @InjectView(R.id.tv_arebinding_floor)
    TextView tvArebindingFloor;
    @InjectView(R.id.ll_floor)
    LinearLayout llFloor;
    @InjectView(R.id.tv_arebinding_areName)
    TextView tvArebindingAreName;
    @InjectView(R.id.btn_complete_Bind)
    Button btnCompleteBind;
    @InjectView(R.id.tv_arebinding_dots)
    TextView tvArebindingDots;

    @InjectView(R.id.photoView)
    PhotoView photoView;

    // 地图上 标记 的 点
    private ArrayList<MyMapPointWithTitleView> mapPoints;
    private ArrayList<MyMapPointWithTitleView> mapPointList;

    private ProgressDialog progressDialog;
    private String buildingId;
    private PopupWindow popupWindow;
    private ListView listView;
    private ArrayList<String> list;
    private String underNum;
    private String upNum;
    private List<AreaBindingBean.Data.Special_pic> specialPics;
    private String picture;
    private String qrNum;
    private String buildingName;
    private String resolution;
    private String resolutionSpec;
    private String picSpec;
    private String floorOld;
    private int size;

    private String cardNumber;
    private String myLocation = "";
    private Subscription rxSubscription;
    private List<AreaBindingDotBean.Data.Qrcodes> qrcodes;
    private boolean isShow;
    private float newX;
    private float newY;

    @Override
    protected void initConfig() {
        super.initConfig();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.areabinding_activity;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("区域绑定");
    }

    @Override
    protected void initViews() {
        mapPoints = new ArrayList<>();
        mapPointList = new ArrayList<>();
        /**约定当图片缩放后把已经打好的点和刚才打好的点进行隐藏，当缩放比小于1时进行显示，下面的按钮点击缩放时也是如此*/
        photoView.setOnScaleChangeListener(new PhotoViewAttacher.OnScaleChangeListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                // 清理打好的点
                clearMapPoints();
                isShow = false;
                // 清理刚才打好的点
                clearMapPoint();
                // 当图片缩放时设置加减按钮是否可以点击
                ivZoomIn.setBackgroundResource(R.drawable.zoom_in);
                ivZoomOut.setBackgroundResource(R.drawable.zoom_out);
                ivZoomIn.setOnClickListener(AreaBindingActivity.this);
                ivZoomOut.setOnClickListener(AreaBindingActivity.this);
                float scale = photoView.getScale();
                if (scale >= 3.0f) {
                    ivZoomIn.setBackgroundResource(R.drawable.zoom_in_gray);
                    ivZoomIn.setOnClickListener(null);
                } else if (scale < 1.0f) {

                    ivZoomOut.setBackgroundResource(R.drawable.zoom_out_gray);
                    ivZoomOut.setOnClickListener(null);
                    // 展示打好的点
                    isShow = showPoints();
                    // 添加一点
                    if (newX != 0 && newY != 0) {
                        MyMapPointWithTitleView myMapPointWithTitleView = new MyMapPointWithTitleView(mActivity, newX, newY, MyMapPointWithTitleView.NORMAL_POINT, false, "第一个点");
                        // 监听长按点可以删除刚才已经打好的点
                        myMapPointWithTitleView.setClearPoint(new MyClearPoint());
                        addMapPoint(myMapPointWithTitleView);
                    }
                }
            }
        });

        photoView.setLongPressListener(new PhotoViewAttacher.OnLongPressListener() {
            @Override
            public void onLongPress(View v, MotionEvent event) {
                // 已经有的点没显示，则显示
                if (!isShow) {
                    isShow = showPoints();

                }
                // 添加一点
                addPoint(event);
            }
        });
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        tvArebindingDots.setText("长按扎点设置为疏散点\r\n建议设置疏散出口为疏散点");
        buildingId = SpUtils.getStringParam(mActivity, Keys.BULIDINGID);
        cardNumber = getIntent().getStringExtra(Keys.CARDNUMBER);
        floorOld = SpUtils.getStringParam(mActivity, FLOOR);
        if (floorOld != null) {

            String message = SpUtils.getStringParam(mActivity, MESSAGE);
//            String dot_message = SpUtils.getStringParam(mActivity, DOT_MESSAGE);
            dealWith(message);
//            dealWithDot(dot_message, floorOld);
        } else {
            // 第一次进来
            if (TextUtils.isEmpty(buildingId)) {
                showDialog();
            } else {
                getData(buildingId);

            }
        }

    }


    @Override
    protected void onLoadDatas() {
        btnCompleteBind.setOnClickListener(this);
        tvArebindingFloor.setOnClickListener(this);
        ivZoomIn.setOnClickListener(this);
        ivZoomOut.setOnClickListener(this);
        // RxBus
        rxBusOnClickListener();
    }


    @Override
    public void onClick(View v) {
        float scale = photoView.getScale();
        // 获取区域名称
        String areaName = tvArebindingAreName.getText().toString().trim();
        String floor = tvArebindingFloor.getText().toString().trim();
        if (floor.contains("B")) {
            floor = floor.replace("B", "-");
        }
        LogUtils.eNormal(TAG, "缩放比是:" + scale);
        switch (v.getId()) {

            case R.id.tv_arebinding_floor:
                showPopupwindow();
                break;
            case R.id.iv_areabinding_zoom_in:
                ivZoomOut.setBackgroundResource(R.drawable.zoom_out);
                ivZoomOut.setOnClickListener(this);
                photoView.setScale(scale + 0.5f);
                if (scale > 2.5f && scale < 3.0f) {
                    scale = 3.0f;
                    photoView.setScale(scale);
                } else if (scale >= 3.0f) {
                    ivZoomIn.setBackgroundResource(R.drawable.zoom_in_gray);
                    ivZoomIn.setOnClickListener(this);
                }
                // 清理打好的点
                clearMapPoints();
                isShow = false;
                // 清理刚才打好的点
                clearMapPoint();
//                moveMapPoints(scale);
                break;
            case R.id.iv_areabinding_zoom_out:

                ivZoomIn.setBackgroundResource(R.drawable.zoom_in);
                ivZoomIn.setOnClickListener(this);
                photoView.setScale(scale - 0.5f);
                if (scale < 1.5f && scale > 1.0f) {
                    scale = 1.0f;
                    photoView.setScale(scale);
                } else if (scale <= 1.0f) {
                    ivZoomOut.setBackgroundResource(R.drawable.zoom_out_gray);
                    ivZoomOut.setOnClickListener(this);
                    // 展示打好的点 已经有的点若没显示，则显示
                    if (!isShow) {
                        isShow = showPoints();
                    }
                    // 添加一点
                    if (newX != 0 && newY != 0) {
                        MyMapPointWithTitleView myMapPointWithTitleView = new MyMapPointWithTitleView(mActivity, newX, newY, MyMapPointWithTitleView.NORMAL_POINT, false, "第一个点");
                        // 监听长按点可以删除刚才已经打好的点
                        myMapPointWithTitleView.setClearPoint(new MyClearPoint());
                        addMapPoint(myMapPointWithTitleView);
                    }
                }
//                moveMapPoints(scale);
                break;
            case R.id.btn_complete_Bind:
                // 绑定
                completeBind(cardNumber, areaName, floor);
                break;
        }

    }

    private void rxBusOnClickListener() {
        // rxSubscription是一个Subscription的全局变量，这段代码可以在onCreate/onStart等生命周期内
        rxSubscription = RxBus.getDefault().toObservable(Rxbus_BindBuilding.class)
                .subscribe(new Action1<Rxbus_BindBuilding>() {
                               @Override
                               public void call(Rxbus_BindBuilding building) {

                                   buildingId = SpUtils.getStringParam(mActivity, Keys.BULIDINGID);
                                   LogUtils.eNormal(TAG, "rxBusOnClickListener收到消息+" + buildingId);
                                   getData(buildingId);
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
     * 添加一点
     */
    private void addPoint(MotionEvent e) {
        float scale = photoView.getScale();
        float x = e.getX();
        float y = e.getY();
        newX = x;
        newY = y;
        myLocation = x + "," + y;
        LogUtils.eNormal(TAG, "打点" + x + "++" + y);
        //添加之前把之前的先移除
        clearMapPoint();
        MyMapPointWithTitleView myMapPointWithTitleView = new MyMapPointWithTitleView(mActivity, x, y, MyMapPointWithTitleView.NORMAL_POINT, false, "第一个点");
        // 监听长按点可以删除刚才已经打好的点
        myMapPointWithTitleView.setClearPoint(new MyClearPoint());
        addMapPoint(myMapPointWithTitleView);
    }

    /**
     * 展示已经有的点
     */
    private boolean showPoints() {
        boolean isShowPoints = false;
        if (qrcodes != null && size > 0) {

            for (int i = 0; i < size; i++) {
                AreaBindingDotBean.Data.Qrcodes qrcode = qrcodes.get(i);

                String location = qrcode.location;
                // 把对应的点显示在地图上
                String[] split = location.split(",");
                double x = Double.parseDouble(split[0]);
                double y = Double.parseDouble(split[1]);
                addMapPoints(new MyMapPointWithTitleView(mActivity, x, y, MyMapPointWithTitleView.NORMAL_POINT, false, ""));
                isShowPoints = true;
            }
        }
        return isShowPoints;
    }

    /**
     * 监听长按打好的点
     */
    class MyClearPoint implements MyMapPointWithTitleView.ClearPoint {

        @Override
        public void clearPoint() {
            myLocation = "";
            clearMapPoint();
        }
    }

    /**
     * 遍历添加大于两个的点 并显示
     */
    public void addMapPoints(MyMapPointWithTitleView myMapPointWithTitleView) {
        // 添加到集合里面
        mapPoints.add(myMapPointWithTitleView);
        // 设置点显示的位置
        myMapPointWithTitleView.setFirstXShow((float) (myMapPointWithTitleView.getFirstX()));
        myMapPointWithTitleView.setFirstYShow((float) (myMapPointWithTitleView.getFirstY()));
        // 把点添加到视图里面
        mapFloor.addView(myMapPointWithTitleView);
    }

    /**
     * 根据 当前 缩放 比例
     * 移动 点 的 位置
     * 用于 放大缩小 按钮
     */
    public void moveMapPoints(float scale) {
        for (int i = 0; i < mapPoints.size(); i++) {
            MyMapPointWithTitleView point = mapPoints.get(i);
            // 设置 点 移动后的位置
            point.setFirstXShow((float) (point.getFirstX() * scale));
            point.setFirstYShow((float) (point.getFirstY() * scale));
        }
    }

    /**
     * 移走长按的点
     */
    public void clearMapPoint() {
        for (int i = 0; i < mapPointList.size(); i++) {
            mapFloor.removeView(mapPointList.get(i));
        }
        this.mapPointList.clear();
    }

    /**
     * 清空 大于两个的点
     */
    public void clearMapPoints() {
        for (int i = 0; i < mapPoints.size(); i++) {
            mapFloor.removeView(mapPoints.get(i));
//            LogUtils.eNormal(TAG, "清理啦啦");
        }
        this.mapPoints.clear();
    }

    /**
     * 长按添加一个点 并显示
     */
//    private boolean isAddPoint = false;
    public void addMapPoint(MyMapPointWithTitleView myMapPointWithTitleView) {
//        isAddPoint = false;
        // 添加到集合里面
        mapPointList.add(myMapPointWithTitleView);
        // 设置点显示的位置
        myMapPointWithTitleView.setFirstXShow((float) (myMapPointWithTitleView.getFirstX()));
        myMapPointWithTitleView.setFirstYShow((float) (myMapPointWithTitleView.getFirstY()));
        // 把点添加到视图里面
        mapFloor.addView(myMapPointWithTitleView);
        // 打完点后设置地图没法放大缩小，以及拖拽，直接重写PhotoView的setOnTouchListener方法
        final GestureDetector gestureDetector = new GestureDetector(mActivity, new LongPress());
//        photoView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });
        //打完点设置标记，，用于设置放大缩小按钮无法点击
//        isAddPoint = true;
//        changeButton(isAddPoint);
    }

    private void changeButton(boolean isAddPoint) {
        if (isAddPoint) {
            // 添加完点，设置地图不可被点击缩放
            ivZoomIn.setBackgroundResource(R.drawable.zoom_in_gray);
            ivZoomOut.setBackgroundResource(R.drawable.zoom_out_gray);
            ivZoomIn.setOnClickListener(null);
            ivZoomOut.setOnClickListener(null);

        } else {
            // 没添加点，设置地图可以被点击缩放
            ivZoomIn.setBackgroundResource(R.drawable.zoom_in);
            ivZoomOut.setBackgroundResource(R.drawable.zoom_out);
            ivZoomIn.setOnClickListener(this);
            ivZoomOut.setOnClickListener(this);
        }
    }

    /**
     * 长按的手势监听
     */
    private class LongPress extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            // 添加一点
            addPoint(e);
        }

    }


    /**
     * 获取地图图片和点集合以及位置
     */
    private void getData(String buildingId) {
        showProgressDialog();

        Map<String, String> params = new HashMap<>();
        params.put("building_id", buildingId);
        LogUtils.eNormal(TAG, "buildingId:" + buildingId);
        OkHttpUtils.post()
                .url(Urls.Url_AreaBinding)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        closeProgressDialog();
                        LogUtils.e(TAG, "getData:", e);
                    }

                    @Override
                    public void onResponse(String response) {
                        closeProgressDialog();
                        LogUtils.eNormal(TAG, "getData:" + response);
                        dealWith(response);
                        saveMessage(response);
                    }
                });
    }

    /**
     * 处理获取 到的楼层信息
     */
    private void dealWith(String response) {

        AreaBindingBean bean = JsonUtil.json2Bean(response, AreaBindingBean.class);
        int code = bean.code;
        String msg = bean.msg;
        if (code == 0) {
            AreaBindingBean.Data data = bean.data;
            buildingName = data.building_name;
            // 通用层图片地址
            picture = data.pic;
            //通用图片的分辨率
            resolution = data.resolution;
            //地下的楼层数

            underNum = data.under_num;
            //地上的楼层数
            upNum = data.up_num;
            // 特殊楼层信息
            specialPics = data.special_pic;

            // 建筑
            tvArebindingBuild.setText(buildingName);
            if (!TextUtils.isEmpty(floorOld)) {
                // 更新当前层的点数
                getCurrentDot(floorOld);
                // 如果有缓存图片显示缓存
                setFloor(floorOld);
                String pic = picture;
                String res = resolution;
//            LogUtils.eNormal(TAG,"初始值"+pic);
                if (specialPics != null && specialPics.size() > 0) {
                    for (int i = 0; i < specialPics.size(); i++) {
                        String floor = specialPics.get(i).floor;
                        picSpec = specialPics.get(i).pic;
                        String newRes = specialPics.get(i).resolution;
                        res = newRes;
                        String s = "";
                        if (floorOld.startsWith("B")) {
                            s = floorOld.replace("B", "-");
                        }
                        // 如果当前切换的楼层是特殊楼层，就重新加载地图界面
                        if (TextUtils.equals(floor, s)) {
                            // 地下层数
                            pic = picSpec;
                            break;
                        } else if (TextUtils.equals(floor, floorOld)) {
                            //地上特殊层
                            pic = picSpec;
                            break;
                        }
                    }
                }
                setMap(pic, photoView, res);
            } else {
                // // 没有缓存图片显示1层图片 获取1层的点的数据
                boolean b = setFloor(1 + "");
                if (b) {
                    // 获取切换后楼层的点个数
                    getCurrentDot(1 + "");
                }
                String picBegin = picture;
                //判断1层是否是特殊层
                if (specialPics != null && specialPics.size() > 0) {
                    for (int i = 0; i < specialPics.size(); i++) {

                        picSpec = specialPics.get(i).pic;
                        if (TextUtils.equals(1 + "", specialPics.get(i).floor)) {
                            // 是特殊层 设置显示特殊层图片
                            picBegin = picSpec;
                            break;
                        }
                    }
                }
                setMap(picBegin, photoView, resolution);
            }
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
            // 提示用户去选择建筑
            showDialog();
        }

    }

    /**
     * 提示用户去选择建筑
     */
    private void showDialog() {
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
     * 获取切换后楼层的点个数
     */
    private void getCurrentDot(final String string) {
        showProgressDialog();
        Map<String, String> params = new HashMap<>();
        params.put("building_id", buildingId);
        params.put("floor", string);
        OkHttpUtils.post()
                .url(Urls.Url_GetCurrentDot)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        closeProgressDialog();
                        LogUtils.e(TAG, "getcurrentDot:", e);
                    }

                    @Override
                    public void onResponse(String response) {
                        closeProgressDialog();
                        LogUtils.eNormal(TAG, "getcurrentDot:" + response);
                        dealWithDot(response, string);
                    }
                });
    }

    /**
     * 处理区域二维码对应的点
     * i 是当前所对应的楼层数
     */
    private void dealWithDot(String response, String floor) {
        AreaBindingDotBean bean = JsonUtil.json2Bean(response, AreaBindingDotBean.class);
        int code = bean.code;
        String msg = bean.msg;
        if (code == 0) {
            AreaBindingDotBean.Data data = bean.data;
            qrNum = data.qr_num;
            qrcodes = data.area_qrs;
            String newFloor = floor;
            if (qrcodes != null) {
                size = qrcodes.size();
                //显示区域名称
                if (floor.startsWith("-")) {
                    newFloor = floor.replace("-", "B");
                }
                tvArebindingAreName.setText(newFloor + " - " + String.valueOf(size + 1));
                // 切换楼层结束，把上一楼层点清理掉
                // 清理长按大的点
                clearMapPoint();
                // 清理已经打好的点
                clearMapPoints();
                // 展示之前已经打好的点
                isShow = showPoints();
//                if (qrcodes != null && size > 0) {
//
//                    for (int i = 0; i < size; i++) {
//                        AreaBindingDotBean.Data.Qrcodes qrcode = qrcodes.get(i);
//
//                        String location = qrcode.location;
//                        // 把对应的点显示在地图上
//                        String[] split = location.split(",");
//                        double x = Double.parseDouble(split[0]);
//                        double y = Double.parseDouble(split[1]);
//                        LogUtils.eNormal(TAG, "第一个元素：" + x + "第二个元素：" + y);
//                        addMapPoints(new MyMapPointWithTitleView(mActivity, x, y, MyMapPointWithTitleView.NORMAL_POINT, false, ""));
//                    }
//                }
                // 保存缓存数据
                saveDotMessage(response, floor);
            } else {
                clearMapPoints();
                tvArebindingAreName.setText(newFloor + " - " + 1);
            }
        } else {
            ToastUtils.showToast(mActivity, msg);
        }

    }

    /**
     * 保存第二次网络请求点的数据
     */
    private void saveDotMessage(String string, String floor) {
//        SpUtils.putParam(mActivity, DOT_MESSAGE, string);
        SpUtils.putParam(mActivity, FLOOR, floor);
    }

    /**
     * 保存第一次网络请求数据
     */
    private void saveMessage(String string) {
        SpUtils.putParam(mActivity, MESSAGE, string);
    }


    /**
     * 设置显示的楼层数
     */
    private boolean setFloor(String string) {
        String trim = tvArebindingFloor.getText().toString().trim();
        if (!TextUtils.equals(trim, string) && tvArebindingFloor != null) {
            String newFloor = string;
            if (string.startsWith("-")) {
                newFloor = string.replace("-", "B");
            }
            tvArebindingFloor.setText(newFloor);
            return true;
        }

        return false;
    }

    /**
     * 设置地图的方法
     */
    private void setMap(final String imgurl, final PhotoView photoView, String resolution) {
        LogUtils.eNormal(TAG, "加载url+" + imgurl);
        resolution = resolution.replace("*", ",");
        LogUtils.eNormal(TAG, "分辨率resolution:" + resolution);
        String[] split = resolution.split(",");
        final int x = Integer.parseInt(split[0]);
        final int y = Integer.parseInt(split[1]);
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

        /*Glide.with(this)
                .load(imgurl)
                .centerCrop()
                .override(x, y)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new GlideDrawableImageViewTarget(photoView) {
                    @Override
                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                        //在这里添加一些图片加载完成的操作
                        LogUtils.eNormal(TAG, "加载完成");

                        ViewTreeObserver vto2 = photoView.getViewTreeObserver();
                        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                photoView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                int height = photoView.getHeight();
                                int width = photoView.getWidth();
                                LogUtils.eNormal(TAG, "分辨率==width:" + width + "+height:" + height);
                                int yScale = y/ height;
                                int xScale = x/width;
                                LogUtils.eNormal(TAG, "分辨率==xScale:" + xScale + "+yScale:" + yScale);
                            }
                        });

                    }
                });*/
    }


    private Handler handler = new Handler();

    private void completeBind(String cardNumber, String areaName, String floor) {
        if (TextUtils.isEmpty(myLocation)) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("请绑定区域二维码")
                    .setCancelable(false)
                    .show();
            return;
        }
        showProgressDialog();
        Map<String, String> params = new HashMap<>();
        params.put("building_id", buildingId);
        LogUtils.eNormal(TAG, "buildingId是：" + buildingId);
        params.put("area_qrcode", cardNumber);
        LogUtils.eNormal(TAG, "二維碼是：" + cardNumber);
        params.put("area_name", areaName);
        LogUtils.eNormal(TAG, "区域名称是：" + areaName);
        params.put("floor", floor);
        LogUtils.eNormal(TAG, "楼层是：" + floor);
        params.put("location", myLocation);
        LogUtils.eNormal(TAG, "location：" + myLocation);
        OkHttpUtils.post()
                .url(Urls.Url_Complete_Bind)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        closeProgressDialog();
                        LogUtils.e(TAG, "completeBind:", e);
                    }

                    @Override
                    public void onResponse(String response) {
                        closeProgressDialog();
                        LogUtils.eNormal(TAG, "completeBind:" + response);
                        AreaBindingCompleteBean bean = JsonUtil.json2Bean(response, AreaBindingCompleteBean.class);
                        if (bean != null) {
                            int code = bean.code;
                            String msg = bean.msg;
                            LogUtils.eNormal(TAG, "code值:" + code);
                            if (code == 0) {
                                ToastUtils.showToast(mActivity, "绑定成功");
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 500);
                            } else {
                                ToastUtils.showToast(mActivity, msg);
                            }
                        }
                    }

                });
    }

    /**
     * 彈窗
     */
    private void showPopupwindow() {
        list = new ArrayList<>();
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

        View view = View.inflate(mActivity, R.layout.evacuate_dialog, null);

        listView = (ListView) view.findViewById(R.id.lv_evavuate);
        setAdapter(view);
    }

    private void setAdapter(View view) {
        EvacuateUserFloorAdapter adapter = new EvacuateUserFloorAdapter(mActivity, list);
        listView.setAdapter(adapter);
        // 创建一个PopuWidow对象
        popupWindow = new PopupWindow(view, DisplayUtil.dp2px(mActivity, 350), DisplayUtil.dp2px(mActivity, 150));
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

//        popupWindow.showAsDropDown(tvArebindingFloor);
//        popupWindow.showAtLocation(rlAreabind, Gravity.BOTTOM, 0, DisplayUtil.dp2px(mActivity,60));
        popupWindow.showAtLocation(rlAreabind, Gravity.CENTER_VERTICAL, 0, DisplayUtil.dp2px(mActivity, 185));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {

                TextView textView = (TextView) view.findViewById(R.id.tv_evacuate);
                String str = textView.getText().toString().trim();
                String pic = picture;
                String res = resolution;
                // 判断当前切换的楼层是否是特殊楼层
                if (specialPics != null && specialPics.size() > 0) {
                    for (int i = 0; i < specialPics.size(); i++) {
                        AreaBindingBean.Data.Special_pic specialPic = specialPics.get(i);
                        resolutionSpec = specialPic.resolution;
                        picSpec = specialPic.pic;
                        String floor = specialPic.floor;
                        String newRes = specialPic.resolution;
                        res = newRes;
                        if (str.startsWith("B")) {
                            LogUtils.eNormal(TAG, "替换前" + str);
                            str = str.replace("B", "-");
                            LogUtils.eNormal(TAG, "替换后" + str);
                        }
                        // 如果当前切换的楼层是特殊楼层，就重新加载地图界面
                        if (TextUtils.equals(floor, str)) {
                            // 地下层数
                            pic = picSpec;
                            break;
                        } else if (TextUtils.equals(floor, str)) {
                            //地上特殊层
                            pic = picSpec;
                            break;
                        }
                    }
                }
                setMap(pic, photoView, res);
                boolean b = setFloor(str);
                if (b) {
                    // 获取切换后楼层的点个数
                    LogUtils.eNormal(TAG, "获取楼层的" + str);
                    getCurrentDot(str);
                }
                //关闭popupWindow
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            }
        });
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
    public boolean onLongClick(View v) {

        return false;
    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

}
