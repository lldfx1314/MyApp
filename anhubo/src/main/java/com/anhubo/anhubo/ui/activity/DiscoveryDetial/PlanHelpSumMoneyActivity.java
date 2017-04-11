package com.anhubo.anhubo.ui.activity.DiscoveryDetial;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.AdapterPlanHelpSumMoney;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.PlanHelpSumMoneyBean;
import com.anhubo.anhubo.bean.PlanHelpSumMoneyUpLoadBean;
import com.anhubo.anhubo.interfaces.InterClick;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.ImageFactory;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.RefreshListview;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2017/3/28.
 */
public class PlanHelpSumMoneyActivity extends BaseActivity implements InterClick {
    private static final String TAG = "PlanHelpSumMoneyActivity";
    private static final int CAMERA = 0;
    private static final int PICTURE = 1;
    private static final int CROP_PHOTO = 2;
    @InjectView(R.id.rl_plan_help_summoney)
    RefreshListview rlPlanHelpSummoney;
    @InjectView(R.id.btn_plan_help_summoney_comfirm)
    Button btnConfirm;
    private String uid;
    private String planId;
    private int pager = 0;
    private ArrayList<Object> datas;
    private AdapterPlanHelpSumMoney adapter;
    private TextView tvMoney;
    private LinearLayout llTakePhoto;
    private int page;
    private Dialog dialog;
    private Button btnTakephoto;
    private Button btnPhoto;
    private Dialog showDialog;
    private ImageView ivPhoto;
    private SpannableString ss;
    private Uri imageUri;
    private TextView tvStatementTime;
    private TimePickerView pvCustomTime;
    private String time;
    private LinearLayout llStatementTime;
    private String cTime;

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("互助金总额");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_plan_help_summoney;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
        super.initEvents();


        getLocalData();

        getData();
        setAdapters();
    }

    @Override
    protected void onLoadDatas() {
        btnConfirm.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_help_sunmoney_takePhoto:
                // 底部弹出对话框
                showDialog();
                break;
            case R.id.btn_plan_help_summoney_comfirm:
                upLoading();
                break;
            case R.id.btn_popDialog_takephoto:
                // 拍照
                camera();
                break;
            case R.id.btn_popDialog_photo:
                // 相册
                getPhoto();
                break;
            case R.id.ll_help_sum_money_statement_time:
                // 弹出自定义选择对账单日期的按钮
                if (pvCustomTime != null) {

                    pvCustomTime.show(); //弹出自定义时间选择器
                }
                break;
        }
    }

    /**
     * 初始化对账单时间选择的对话框
     */
    private void initTimePicker() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        if (!TextUtils.isEmpty(cTime)) {
            String[] split = cTime.split("-");
            int year1 = Integer.parseInt(split[0]);
            int month1 = Integer.parseInt(split[1])-1;
            startDate.set(year1, month1, 1);
        } else {
            startDate.set(2010, 1, 1);
        }

        Calendar endDate = Calendar.getInstance();
        int year = endDate.get(Calendar.YEAR);
        int month = endDate.get(Calendar.MONTH);
//        int day = endDate.get(Calendar.DAY_OF_MONTH);
        int day = 30;
//        LogUtils.eNormal(TAG,"哈哈：+"+day);
        endDate.set(year, month, day);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                time = getTime(date);
                StringBuilder builder = new StringBuilder(time.replace("-", "年"));
                String newTime = builder.append("月").toString();
                tvStatementTime.setText(newTime);
            }
        }).setType(TimePickerView.Type.YEAR_MONTH)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setTextColorCenter(DisplayUtil.getColor(R.color.hanzi8_black))
                .setTextColorOut(DisplayUtil.getColor(R.color.hanzi4_black))
//                .isDialog(true)
//                .isCyclic(true)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView tvCancel = (TextView) v.findViewById(R.id.tv_cancel);
                        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
                        tvTitle.setText("选择对账单时间");
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData(tvSubmit);
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setDividerColor(DisplayUtil.getColor(R.color.rl_gray))
                .build();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }

    private void setAdapters() {
        datas = new ArrayList<>();
        adapter = new AdapterPlanHelpSumMoney(mActivity, datas, PlanHelpSumMoneyActivity.this);
        // 填充头布局
        View view = View.inflate(mActivity, R.layout.header_help_sunmoney, null);
        tvMoney = (TextView) view.findViewById(R.id.tv_help_sum_money);
        tvStatementTime = (TextView) view.findViewById(R.id.tv_help_sum_money_statement_time);
        llStatementTime = (LinearLayout) view.findViewById(R.id.ll_help_sum_money_statement_time);
        llTakePhoto = (LinearLayout) view.findViewById(R.id.ll_help_sunmoney_takePhoto);
        ivPhoto = (ImageView) view.findViewById(R.id.iv_help_sunmoney_photo);

        rlPlanHelpSummoney.addHeaderView(view);
        rlPlanHelpSummoney.setAdapter(adapter);

        rlPlanHelpSummoney.setOnRefreshingListener(new MyOnRefreshingListener());
        // 拍照按钮监听
        llTakePhoto.setOnClickListener(this);
        llStatementTime.setOnClickListener(this);
    }

    private boolean isLoadMore;//记录是否加载更多

    @Override
    public void onBtnClick(View v) {
        int position = (int) v.getTag(R.id.image_tag);
        PlanHelpSumMoneyBean.Data.Pics.Pic pic = (PlanHelpSumMoneyBean.Data.Pics.Pic) datas.get(position);
        String picUrl = pic.pic_url;
        // 打开另一界面显示大图
        Intent intent = new Intent(mActivity, BigImageViewActivity.class);
        intent.putExtra("images", picUrl);
        startActivity(intent);
        overridePendingTransition(R.anim.big_image_enter, 0);
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
                rlPlanHelpSummoney.layoutParams();
            }
        }
    }

    private void getData() {
        dialog = loadProgressDialog.show(mActivity, "正在加载...");
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
//        LogUtils.eNormal(TAG, "uid+" + uid);
        params.put("plan_id", planId);
//        LogUtils.eNormal(TAG, "plan_id+" + planId);
        params.put("page", String.valueOf(pager++));
//        LogUtils.eNormal(TAG, "pager+" + pager);
        OkHttpUtils.post()
                .url(Urls.Url_PlanHelpSumMoney)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        dialog.dismiss();
                        LogUtils.e(TAG, "getData:", e);
                        new AlertDialog(mActivity).builder().setTitle("提示").setMsg("网络错误").setPositiveButton("返回", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isLoadMore) {
                                    rlPlanHelpSummoney.loadMoreFinished();
                                }
                                finish();
                            }
                        }).setCancelable(false).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        LogUtils.eNormal(TAG, "getData:" + response);
                        if (!TextUtils.isEmpty(response)) {
                            dealWith(response);
                        }
                    }
                });
    }

    private void dealWith(String response) {
        PlanHelpSumMoneyBean bean = JsonUtil.json2Bean(response, PlanHelpSumMoneyBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            PlanHelpSumMoneyBean.Data data = bean.data;
            page = data.page;
            String sumMoney = data.sum_money;
            cTime = data.c_time;
            if (!TextUtils.isEmpty(sumMoney)) {
                // 填充头布局里互助金总额
//                tvMoney.setText(sumMoney);
                setShowDetial(sumMoney, tvMoney);
            }
            isLoadMore = false;
            // 恢复加载更多状态
            rlPlanHelpSummoney.loadMoreFinished();
            if (code == 0) {
                List<PlanHelpSumMoneyBean.Data.Pics> pics = data.pics;
                if (pics != null) {
                    for (int i = 0; i < pics.size(); i++) {
                        PlanHelpSumMoneyBean.Data.Pics pic = pics.get(i);
                        String year = pic.year;
                        List<PlanHelpSumMoneyBean.Data.Pics.Pic> picList = pic.pic;
                        datas.add(year);
                        datas.addAll(picList);
                    }

                    adapter.notifyDataSetChanged();
                }
            } else {
                ToastUtils.showToast(mActivity, msg);
            }
        } else {
            isLoadMore = false;
            // 恢复加载更多状态
            rlPlanHelpSummoney.loadMoreFinished();
        }
        initTimePicker();
    }

    private void getLocalData() {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        planId = getIntent().getStringExtra(Keys.PLANID);
    }

    /**
     * 拿到拍到的照片去上传
     */
    private File newFile = null;

    private void upLoading() {
        String billTime = tvStatementTime.getText().toString().trim();
        if (TextUtils.isEmpty(billTime) && TextUtils.isEmpty(time)) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("请选取对账单对应日期")
                    .setCancelable(false).show();
            return;
        }
        if (newFile == null) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("请拍取对账单照片")
                    .setCancelable(false).show();
            return;
        }

        showDialog = loadProgressDialog.show(mActivity, "正在上传...");
        String url = Urls.Url_UpLoadingHelpSumMoney;
        Map<String, String> params = new HashMap<>();
        params.put("plan_id", planId);
        params.put("uid", uid);
        params.put("bill_time", time);

        OkHttpUtils.post()//
                .addFile("file", "file01.png", newFile)//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        LogUtils.e(TAG, ":upLoading:", e);
                        showDialog.dismiss();
                        new AlertDialog(mActivity).builder()
                                .setTitle("提示")
                                .setMsg("网络有问题，请检查")
                                .setCancelable(true).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        showDialog.dismiss();
                        LogUtils.eNormal(TAG + ":upLoading:", response);
                        PlanHelpSumMoneyUpLoadBean bean = JsonUtil.json2Bean(response, PlanHelpSumMoneyUpLoadBean.class);
                        if (bean != null) {
                            int code = bean.code;
                            final String msg = bean.msg;
                            if (code != 0) {
                                new AlertDialog(mActivity).builder()
                                        .setTitle("提示").setMsg("上传失败").setCancelable(false).show();
                            } else {
                                new AlertDialog(mActivity).builder()
                                        .setTitle("提示").setMsg("上传成功").setCancelable(false).setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        datas.clear();
                                        pager = 0;
                                        // 重新刷数据
                                        getData();
                                        // 把之前显示 的图片关掉，把拍照按钮回复
                                        llTakePhoto.setVisibility(View.VISIBLE);

                                        //让显示的图片不显示
                                        ivPhoto.setImageBitmap(null);
                                        //让时间置空
                                        tvStatementTime.setText(null);
                                        time = null;
                                    }
                                }).show();
                            }
                        }
                    }
                });
    }

    /**
     * 打开相册
     */
    private void getPhoto() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
        dialog.dismiss();
    }


    /**
     * 打开相机
     */
    private void camera() {
        takePhoto();
        dialog.dismiss();
    }

    /**
     * 获取相册照片
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, PICTURE); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
//                    ToastUtils.showToast(mActivity, "You denied the permission");
                }
                break;
            default:
        }

    }

    /**
     * 打开相机拍照
     */
    private void takePhoto() {
        //图片名称 时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File outputImage = new File(path, filename + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将File对象转换为Uri并启动照相程序
        if (Build.VERSION.SDK_INT < 24) {

            imageUri = Uri.fromFile(outputImage);
        } else {
            imageUri = FileProvider.getUriForFile(mActivity, "com.luoli.cameraalbumtest.fileprovider", outputImage);
        }
        Intent tTntent = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
        tTntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
        startActivityForResult(tTntent, CAMERA); //启动照相
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case CAMERA:// 相机
                try {
                    //　启动相机裁剪
                    startCameraCrop();

                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CROP_PHOTO://相机裁剪成功
                try {
                    //图片解析成Bitmap对象
                    Bitmap bitmap = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(imageUri));
                    if (bitmap != null) {
                        Bitmap photo = ImageFactory.ratio(bitmap, ivPhoto.getWidth(), ivPhoto.getHeight());
                        llTakePhoto.setVisibility(View.GONE);

                        //显示图片
                        ivPhoto.setImageBitmap(photo);
                        // 把本文件压缩后缓存到本地文件里面
                        File filePhoto01 = savePicture(photo, "photo01");
                        newFile = filePhoto01;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case PICTURE:// 相册
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    Bitmap bitmap = null;
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        bitmap = handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        bitmap = handleImageBeforeKitKat(data);
                    }
                    if (bitmap != null) {
                        Bitmap photo = ImageFactory.ratio(bitmap, ivPhoto.getWidth(), ivPhoto.getHeight());
                        llTakePhoto.setVisibility(View.GONE);
                        //显示图片
                        ivPhoto.setImageBitmap(photo);
                        // 把本文件压缩后缓存到本地文件里面
                        File filePhoto02 = savePicture(photo, "photo02");
                        newFile = filePhoto02;
                    }
                }
                break;
        }
    }

    /**
     * 相机裁剪
     */
    private void startCameraCrop() {
        // 启动剪裁功能
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("scale", true);
        //设置宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1.2);
        //设置裁剪图片宽高
        intent.putExtra("outputX", ivPhoto.getWidth());
        intent.putExtra("outputY", ivPhoto.getWidth());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //广播刷新相册
        Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentBc.setData(imageUri);
        this.sendBroadcast(intentBc);
        startActivityForResult(intent, CROP_PHOTO); //设置裁剪参数显示图片至ImageView
    }

    @TargetApi(19)
    private Bitmap handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        Bitmap bitmap = displayImage(imagePath);// 根据图片路径显示图片
        return bitmap;
    }

    private Bitmap handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        Bitmap bitmap = displayImage(imagePath);
        return bitmap;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private Bitmap displayImage(String imagePath) {
        Bitmap bitmap = null;
        if (imagePath != null) {
            bitmap = BitmapFactory.decodeFile(imagePath);
//            ivPhoto.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
//            ToastUtils.showToast(mActivity, "failed to get image");
        }
        return bitmap;
    }


    /**
     * 为了减小体积 把图片压缩保存到手机上（清晰度改动不大，基本不受影响）
     **/
    private File savePicture(Bitmap bitmap, String fileName) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File outputImage = new File(path, fileName + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
            OutputStream stream = new FileOutputStream(outputImage);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);// 把图片写入指定文件夹中
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return outputImage;
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

    private void setShowDetial(String string, TextView textView) {
        if (!TextUtils.isEmpty(string)) {

            Double heighSharing = Double.parseDouble(string) / 10000;

            if (heighSharing < 1) {
                if (string.endsWith(".0")) {
                    string = string.replace(".0", "");
                }
                textView.setText(string);
            } else if (heighSharing >= 1) {
                String str = String.valueOf(heighSharing);
                if (str.length() >= 3) {
                    String substring = str.substring(0, 3);
                    //　做判断，防止显示类似＂50.万＂这样的情况
                    if (substring.endsWith(".")) {
                        setWan(substring.substring(0, 2) + "万");
                    } else if (substring.endsWith(".0")) {
                        setWan(substring.substring(0, 1) + "万");
                    }
                } else {
                    setWan(str + "万");

                }
                textView.setHorizontallyScrolling(true);
                textView.setText(ss);
            }
        }
    }

    /**
     * 设置万字大小
     */
    private void setWan(String string) {

        ss = new SpannableString(string);
        MyURLSpan myURLSpan = new MyURLSpan(string);
        ss.setSpan(myURLSpan, string.length() - 1, string.length(), SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    class MyURLSpan extends URLSpan {


        public MyURLSpan(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setTextSize(DisplayUtil.sp2px(mActivity, 15));
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

}
