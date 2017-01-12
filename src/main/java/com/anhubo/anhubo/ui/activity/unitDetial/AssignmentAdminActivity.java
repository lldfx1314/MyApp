package com.anhubo.anhubo.ui.activity.unitDetial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.AssignmentAdminAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.EmployeeListBean;
import com.anhubo.anhubo.interfaces.InterClick;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by LUOLI on 2017/1/12.
 */
public class AssignmentAdminActivity extends BaseActivity implements InterClick {


    private static final String TAG = "AssignmentAdminActivity";
    @InjectView(R.id.tv_ass_admin_num)
    TextView tvAssAdminNum;
    @InjectView(R.id.lv_ass_admin)
    ListView lvAssAdmin;
    private String uid;
    private String businessId;
    private ArrayList<EmployeeListBean.Data.User_info> list;
    private AssignmentAdminAdapter adapter;
    private Map<Integer, Boolean> booleanMap;
    /* 用来记录存在问题的选项，0表示没问题，1表示有问题*/
    private int isProblem = 0;
    // 选择完成后的有问题的集合
    private ArrayList<Integer> completeList;
    private Boolean isClick = false;

    @Override
    protected void initConfig() {
        super.initConfig();
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_assignmentadmin;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("选择管理员");
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        list = new ArrayList<>();
//        completeList = new ArrayList<>();
        adapter = new AssignmentAdminAdapter(mActivity, list, AssignmentAdminActivity.this);
        lvAssAdmin.setAdapter(adapter);
        Intent intent = getIntent();
        EmployeeListBean bean = (EmployeeListBean) intent.getSerializableExtra(Keys.EMPLOYEELISTBEAN);
        // 处理bean对象
        int count = dealBean(bean);
        // 创建一个数组
        int[] arr = new int[count];
//        for (int i = 0; i < arr.length; i++) {
//            completeList.add(arr[i]);
//            LogUtils.eNormal(TAG + ":", arr[i]);
//        }
        // 创建map
        booleanMap = creatMap(count);
        //监听条目的点击事件
        lvAssAdmin.setOnItemClickListener(onItemClickListener);
    }


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_employee_choose);
            // 获取当前position对应位置的点击记录
            if (!isClick) {
//                imageView.setImageResource(R.drawable.fuxuan_input01);

            }
            isClick = !isClick;
        }
    };

    private Map<Integer, Boolean> creatMap(int count) {
        HashMap<Integer, Boolean> map = new HashMap<>();
        for (int i = 0; i < count; i++) {
            map.put(i, false);
        }
        return map;
    }

    /**
     * 处理bean对象
     */
    private int dealBean(EmployeeListBean bean) {
        if (bean != null) {
            EmployeeListBean.Data data = bean.data;
            String userNum = data.user_num;

            List<EmployeeListBean.Data.User_info> userInfo = data.user_info;
            for (int i = 0; i < userInfo.size(); i++) {
                EmployeeListBean.Data.User_info info = userInfo.get(i);
                int userType = info.user_type;
                if (userType == 1) {
                    // 把管理员从列表里面删除
                    userInfo.remove(i);
                }

            }
            // 没有管理员，显示员工人数
            tvAssAdminNum.setText(userInfo.size() + "人");
            list.clear();
            list.addAll(userInfo);
            adapter.notifyDataSetChanged();
        }
        return list.size();
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }


    @Override
    public void onBtnClick(View v) {

    }
}
