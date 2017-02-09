package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.MsgPerfectAdapterOne;
import com.anhubo.anhubo.adapter.MsgPerfectAdapterSecond;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MsgPerfect_UsePro_Bean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by LUOLI on 2017/2/9.
 */
public class PlaceUsePropertyActivity extends BaseActivity{

    private static final String TAG = "PlaceUsePropertyActivity";
    public static List<String> mainList = new ArrayList<>();
    private List<MsgPerfect_UsePro_Bean.Data.Properties> list;
    private List<MsgPerfect_UsePro_Bean.Data.Properties.Property1_arr> property1ArrList;

    private List<String> list2TypeName = new ArrayList<>();
    private String[][] data;
    private ListView mainlist;
    private ListView morelist;
    private MsgPerfectAdapterSecond adapterSecond;
    private Dialog showDialog;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_place_use;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("场所使用性质");
        mainlist = (ListView)findViewById(R.id.lv_MsgPerfect_01);
        morelist = (ListView)findViewById(R.id.lv_MsgPerfect_02);
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        // 定义方法，获取数据
        getPlaceData();
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }

    // 场所使用性质
    private void getPlaceData() {
        showDialog = loadProgressDialog.show(mActivity, "正在加载...");
        String url = Urls.Url_GetUsePro;
        OkHttpUtils.post()//
                .url(url)
                .build()//
                .execute(new MyStringCallback2());
    }

    class MyStringCallback2 extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG, ":getPlaceData:", e);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
        }

        @Override
        public void onResponse(String response) {
            showDialog.dismiss();
            LogUtils.eNormal(TAG + ":getPlaceData:", response);
            MsgPerfect_UsePro_Bean bean = JsonUtil.json2Bean(response, MsgPerfect_UsePro_Bean.class);
            if (bean != null) {
                showData(bean);
                // 设置第一个适配器
                setAdapterOne();
            }
        }
    }


    private void setAdapterOne() {
        // 拿到数据后调用构造把数据传过去
        final MsgPerfectAdapterOne adapterOne = new MsgPerfectAdapterOne(mActivity, mainList);
        mainlist.setAdapter(adapterOne);
        adapterOne.setSelectItem(0);
        mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                initAdapter(data[position]);
                adapterOne.setSelectItem(position);
                adapterOne.notifyDataSetChanged();
            }
        });
        // 设置第二个适配器
        setAdapterSecond();
    }

    private void setAdapterSecond() {
        mainlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // 一定要设置这个属性，否则ListView不会刷新
        initAdapter(data[0]);

        morelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapterSecond.setSelectItem(position);
                TextView tv = (TextView) view.findViewById(R.id.moreitem_txt);
                // 获取到从第二例数组里面的字符串
                String string = tv.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra(Keys.PLACEUSE, string);
                setResult(5, intent);
                finish();


            }
        });
    }

    private void initAdapter(String[] array) {
        adapterSecond = new MsgPerfectAdapterSecond(mActivity, array);
        morelist.setAdapter(adapterSecond);
        adapterSecond.notifyDataSetChanged();
    }


    private void showData(MsgPerfect_UsePro_Bean bean) {
        // 先获取第一列数据
        list = bean.data.properties;
        // 每次遍历都清空一下集合
        mainList.clear();
        // 遍历list
        for (int i = 0; i < list.size(); i++) {
            MsgPerfect_UsePro_Bean.Data.Properties properties = list.get(i);
            String property2 = properties.property2;
            // 获取到第一列数据的集合
            mainList.add(property2);

            // 获取到第二列数据
            property1ArrList = properties.property1_arr;
            for (int m = 0; m < property1ArrList.size(); m++) {
                MsgPerfect_UsePro_Bean.Data.Properties.Property1_arr property1Arr = property1ArrList.get(m);
                // 拿到第二列的每一组的每个数据
                String property11 = property1Arr.property1;
                //添加进每组
                list2TypeName.add(property11);
            }
            // 每次添加完后为了便于区分,添加一个*来用于分割集合
            list2TypeName.add("#");
        }

        //list2TypeName集合添加完之后用#切割得到新数组
        String[] arr = list2TypeName.toString().split("#");
        // 创建一个二维数组
        data = new String[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];
            String string = s.substring(1, s.length()).trim();
            String[] newArr = string.split(",");
            data[i] = newArr;
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
