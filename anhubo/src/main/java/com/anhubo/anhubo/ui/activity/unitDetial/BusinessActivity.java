package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.LatLng;
import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.Business_Location_Adapter;
import com.anhubo.anhubo.bean.LocationBean;
import com.anhubo.anhubo.bean.Unit_RegisterBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.LoadProgressDialog;
import com.anhubo.anhubo.view.RefreshListview;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by LUOLI on 2016/11/21.
 */
public class BusinessActivity extends AppCompatActivity implements View.OnSystemUiVisibilityChangeListener {

    private static final String TAG = "BusinessActivity";
    MapView mMapView = null;
    private RefreshListview lvBusiness;
    private ImageView ivTopBarRight;
    private TextView tvToptitle;
    private RelativeLayout llTop;
    private AMapLocationClient mlocationClient;
    private double latitude;
    private double longitude;
    private int pager = 0;
    private int page;

    private boolean isLoadMore = false;
    private Business_Location_Adapter adapter;
    private AMap aMap;
    private UiSettings settings;
    //声明mListener对象，定位监听器
    private LocationSource.OnLocationChangedListener mListener = null;
    private ArrayList<String> listBusiness;
    private ArrayList<String> listBusinessPoi;
    private LoadProgressDialog loadProgressDialog;
    private Dialog showDialog;
    private Dialog showDialog1;
    private String businessName;
    private String zhezhao;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**设置浸入式状态栏*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        setStatusBarTransparent();
        super.onCreate(savedInstanceState);
        //setBar();
        setContentView(R.layout.activity_business);
        // 设置title上的返回键的点击事件
        initDefaultViews();
        // 加载标题栏的布局
        initTitleView();// 找到标题栏布局
        initTitleBar();//设置标题栏的具体事件
        initViews();
        initEvents();

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
    }

    private void initViews() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map_business);
        lvBusiness = (RefreshListview) findViewById(R.id.lv_business);

        zhezhao = getIntent().getStringExtra(Keys.UNIT_ZHEZHAO);

        // 监听listview的滑动监听
        lvBusiness.setOnRefreshingListener(new MyOnRefreshingListener());
        lvBusiness.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String poi = listBusinessPoi.get(position);
                TextView tv = (TextView) view.findViewById(R.id.tv_location);
                String str = tv.getText().toString().trim();
                // 判断是从哪个界面点进来的，便于做相应操作
                if (!TextUtils.isEmpty(zhezhao)) {
                    // 从遮罩点击进来的，直接注册单位
                    unitRegister(str, poi);
                } else {
                    // 返回修改界面
                    returnAddActivity(str, poi);
                }

            }
        });
    }

    /**
     * 单位注册
     */
    private void unitRegister(String string, String poi) {
        businessName = string;
        Map<String, String> params = new HashMap<>();
        String uid = SpUtils.getStringParam(this, Keys.UID);
        params.put("uid", uid);
        params.put("business_name", string);
        params.put("latitude", String.valueOf(latitude));
        params.put("longitude", String.valueOf(longitude));
        if (!TextUtils.isEmpty(poi)) {
            params.put("poi_id", poi);
        }
        String url = Urls.Url_Unit_Register;
        showDialog1 = loadProgressDialog.show(this, "正在提交...");
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        showDialog1.dismiss();

                        new AlertDialog(BusinessActivity.this).builder()
                                .setTitle("提示")
                                .setMsg("网络有问题，请检查")
                                .setCancelable(true).show();
                        LogUtils.e(TAG, "单位注册", e);
                    }

                    @Override
                    public void onResponse(String response) {
                        showDialog1.dismiss();
                        LogUtils.eNormal(TAG + "+unitRegister", response);
                        Unit_RegisterBean bean = JsonUtil.json2Bean(response, Unit_RegisterBean.class);
                        if (bean != null) {
                            int code = bean.code;
                            String businessId = bean.data.business_id;
                            if (code == 0) {
                                SpUtils.putParam(BusinessActivity.this, Keys.BUSINESSID, businessId);
                                SpUtils.putParam(BusinessActivity.this, Keys.BUSINESSNAME, businessName);
                                Intent intent = new Intent();
                                intent.putExtra(Keys.BUSINESSID, businessId);
                                intent.putExtra(Keys.BUSINESSNAME, businessName);
                                setResult(1, intent);
                                finish();
                            }

                        }
                    }
                });
    }

    private void returnAddActivity(String str, String poi) {
        Intent intent = new Intent();
        intent.putExtra(Keys.STR, str);
        intent.putExtra(Keys.BUSINESS_POI, poi);
        setResult(2, intent);
        finish();
    }

    private void initEvents() {
        listBusiness = new ArrayList<String>();
        listBusinessPoi = new ArrayList<String>();


    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
        // 设置地图的图标
        setIcon();
        // 开始定位
        getlocationData();
        //添加单位
        addBusiness();
    }


    private void addBusiness() {
        ivTopBarRight.setVisibility(View.VISIBLE);
        ivTopBarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
    }

    private void dialog() {
        final AlertDialog alertDialog = new AlertDialog(BusinessActivity.this);
        alertDialog
                .builder()
                .setTitle("提示")
                .setEditHint("请输入您所在的单位")
                .setCancelable(false)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String string = alertDialog.et_msg.getText().toString().trim();
                        if (!TextUtils.isEmpty(string)) {
                            // 判断是从哪个界面点进来的，便于做相应操作
                            if (!TextUtils.isEmpty(zhezhao)) {
                                // 从遮罩点击进来的，直接注册单位
                                unitRegister(string, "");
                            } else {
                                // 返回修改界面
                                returnAddActivity(string, "");
                            }
                        }
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    /**
     * 设置地图的图标
     */
    private void setIcon() {
        if (aMap == null) {

            aMap = mMapView.getMap();
            aMap.setLocationSource(mLocationLiSource);//设置了定位的监听,这里要实现LocationSource接口
            //UiSettings 主要是对地图上的控件的管理，比如指南针、logo位置（不能隐藏）.....
            settings = aMap.getUiSettings();
            // 是否显示定位按钮
            settings.setMyLocationButtonEnabled(true);
            //添加指南针
            //settings.setCompassEnabled(true);

            //aMap.getCameraPosition(); 方法可以获取地图的旋转角度

            //管理缩放控件 不显示
            settings.setZoomControlsEnabled(false);
            //设置logo位置，左下，底部居中，右下
            settings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
            //设置显示地图的默认比例尺
            settings.setScaleControlsEnabled(true);
            //每像素代表几米
            //float scale = aMap.getScalePerPixel();

            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase

        }
    }

    /**
     * 获取经纬度
     */
    private void getlocationData() {

        mlocationClient = new AMapLocationClient(this);
        //设置定位监听
        mlocationClient.setLocationListener(mLocationListener);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        //设置定位间隔,单位毫秒,默认为2000ms
        //mLocationOption.setInterval(2000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();

    }


    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    //地图放大级别
    private float zoomlevel = 16f;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    //获取纬度
                    latitude = amapLocation.getLatitude();
                    //获取经度
                    longitude = amapLocation.getLongitude();
                    // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                    if (isFirstLoc) {

                        //设置缩放级别（缩放级别为4-20级）
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomlevel));
                        //将地图移动到定位点
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                        //点击定位按钮 能够将地图的中心移动到定位点
                        mListener.onLocationChanged(amapLocation);

                        isFirstLoc = false;
                    }
                    // 拿到经纬度请求网络

                    loadProgressDialog = LoadProgressDialog.newInstance();
                    showDialog = loadProgressDialog.show(BusinessActivity.this, "正在加载...");
                    getData();


                } else if (amapLocation.getErrorCode() == 12) {
                    // 没有定位权限
                    dialogLocation();
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    System.out.println(("AmapError,location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo()));
                }
            }
        }
    };

    /**
     * Dialog对话框提示用户去设置界面打开权限
     */
    protected void dialogLocation() {

        new AlertDialog(BusinessActivity.this).builder()
                .setTitle("提示")
                .setMsg("前往系统设置的应用列表里打开安互保的定位权限？")
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

    /**
     * 定位的监听
     */
    public LocationSource mLocationLiSource = new LocationSource() {

        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            mListener = onLocationChangedListener;
        }

        @Override
        public void deactivate() {
            mListener = null;
        }
    };


    /**
     * 拿到经纬度请求网络
     */
    private void getData() {
        String location = String.valueOf(latitude) + "," + String.valueOf(longitude);
        String url = Urls.Location;
        Map<String, String> params = new HashMap<>();
        params.put("location", location);
        params.put("page", pager + "");
        pager++;
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
            new AlertDialog(BusinessActivity.this).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isLoadMore){
                                lvBusiness.loadMoreFinished();
                            }
                        }
                    })
                    .setCancelable(false).show();
            LogUtils.e(TAG, ":getData定位:", e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":getData定位:", response);
            LocationBean bean = JsonUtil.json2Bean(response, LocationBean.class);
            if (bean != null) {
                showDialog.dismiss();
                processData(bean);
                isLoadMore = false;
                // 恢复加载更多状态
                lvBusiness.loadMoreFinished();
            } else {
                isLoadMore = false;
                // 恢复加载更多状态
                lvBusiness.loadMoreFinished();
            }
        }
    }


    class MyOnRefreshingListener implements RefreshListview.OnRefreshingListener {

        @Override
        public void onLoadMore() {


            // 判断是否有更多数据，moreurl是否为空
            if (pager <= page) {
                // 加载更多业务
                isLoadMore = true;
                getData();
            } else {
                // 恢复Listview的加载更多状态
                lvBusiness.loadMoreFinished();
                if (count++ == 0) {
                    ToastUtils.showToast(BusinessActivity.this, "没有更多数据了");
                }
            }
        }
    }

    private void processData(LocationBean bean) {
        int code = bean.code;
        String msg = bean.msg;
        page = bean.data.page;
        List<LocationBean.Data.Business> business = bean.data.business;

        if (!business.isEmpty()) {
            for (int i = 0; i < business.size(); i++) {
                LocationBean.Data.Business busines = business.get(i);
                String s = busines.name;
                listBusiness.add(s);
                String poiId = busines.poi_id;
                listBusinessPoi.add(poiId);
            }
        }
        if (!isLoadMore) {
            // 给listView设置适配器
            adapter = new Business_Location_Adapter(this, listBusiness);
            lvBusiness.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置标题栏
     */
    private void initTitleBar() {
        setTopBarDesc("单位");
    }

    private final void initDefaultViews() {
        setTopBarLeftView(R.drawable.left, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 设置顶部左侧按钮图片以及点击的监听事件
     *
     * @param res
     */
    protected final void setTopBarLeftView(int res, View.OnClickListener listener) {
        ImageView ivTopBarLeft = (ImageView) findViewById(R.id.ivTopBarLeft);
        if (ivTopBarLeft != null) {
            ivTopBarLeft.setImageResource(res);
            ivTopBarLeft.setOnClickListener(listener);
        }
    }

    /**
     * 加载Title布局
     */
    private void initTitleView() {
        // 找到顶部控件
        ivTopBarRight = (ImageView) findViewById(R.id.ivTopBarRight_add);//右上角加号
        tvToptitle = (TextView) findViewById(R.id.tvAddress);//标题
        llTop = (RelativeLayout) findViewById(R.id.ll_Top); // 顶部标题栏

    }


    /**
     * @param str 设置顶部状态栏显示文字
     */
    protected final void setTopBarDesc(String str) {
        tvToptitle.setTextColor(getResources().getColor(R.color.backgroud_white));
        tvToptitle.setText(str);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 设置浸入式状态栏
     */
    private void setStatusBarTransparent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //托盘重叠显示在Activity上
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(uiOptions);
            decorView.setOnSystemUiVisibilityChangeListener(this);
            // 设置托盘透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //Log.d("CP_Common","VERSION.SDK_INT =" + VERSION.SDK_INT);
        } else {
            //Log.d("CP_Common", "SDK 小于19不设置状态栏透明效果");
        }

    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

}