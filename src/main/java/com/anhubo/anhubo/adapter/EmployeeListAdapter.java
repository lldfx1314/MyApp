package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.EmployeeListBean;
import com.anhubo.anhubo.bean.EmployeeOperate;
import com.anhubo.anhubo.interfaces.InterClick;
import com.anhubo.anhubo.utils.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Created by LUOLI on 2017/1/11.
 */
public class EmployeeListAdapter extends BaseAdapter implements View.OnClickListener {
    private static final String TAG = "EmployeeListAdapter";
    private Context mContext;
    private ArrayList<EmployeeListBean.Data.User_info> mList;
    private ViewHolder hold;
    private InterClick mCallback;
    private boolean isAdm;
    private EmployeeOperate operate;

    public EmployeeListAdapter(Context context, ArrayList<EmployeeListBean.Data.User_info> list, InterClick callback, boolean isAdm) {
        this.mContext = context;
        this.mList = list;
        this.mCallback = callback;
        this.isAdm = isAdm;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 对员工的操作记录
        operate = new EmployeeOperate();
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_employee, null);
            hold = new ViewHolder(convertView);
            convertView.setTag(hold);
        } else {
            hold = (ViewHolder) convertView.getTag();
        }
        EmployeeListBean.Data.User_info userInfo = mList.get(position);
        int status = userInfo.status;
        int userType = userInfo.user_type;
//        LogUtils.eNormal(TAG+":haha",userInfo.username);
        // 显示头像、姓名、分割线
        if (userType == 0) {
            // 把除管理员之外的员工显示在列表上
            String picPath = userInfo.pic_path;
            hold.ivIcon.setTag(position);
            int tag = (int) hold.ivIcon.getTag();

            if (!TextUtils.isEmpty(picPath)) {
                if (tag == position) {

                    setHeaderIcon(hold.ivIcon, picPath);
                }
            } else {
                hold.ivIcon.setImageResource(R.drawable.newicon);
            }


            hold.tvName.setText(userInfo.username);
            // 显示分割线
            hold.viewEmployee.setVisibility(View.VISIBLE);
        }
            if (userType == 0 && status == 1) {
//                LogUtils.eNormal(TAG,"哈哈 退出");
                // 是员工，并且是员工本人
                hold.btnEmployee.setVisibility(View.VISIBLE);
                hold.btnEmployee.setText("退出");
                operate.operate = "quit";
            }else{
//                LogUtils.eNormal(TAG,"哈哈 不要啊");
                hold.btnEmployee.setVisibility(View.GONE);
            }

        // 是管理员，其他成员显示删除按钮
        if (isAdm) {
            // 删除暂缓
//            hold.btnEmployee.setVisibility(View.VISIBLE);
            hold.btnEmployee.setText("删除");
            operate.operate = "del";
        }
        hold.btnEmployee.setOnClickListener(this);
        operate.position = position;
        hold.btnEmployee.setTag(operate);


        return convertView;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_employee:
                if (mCallback != null) {
                    mCallback.onBtnClick(v);
                }
                break;
            default:
                break;
        }
    }

    static class ViewHolder {
        CircleImageView ivIcon;
        TextView tvName;
        Button btnEmployee;
        View viewEmployee;

        public ViewHolder(View view) {
            ivIcon = (CircleImageView) view.findViewById(R.id.iv_employee_icon);
            tvName = (TextView) view.findViewById(R.id.tv_employee_name);
            btnEmployee = (Button) view.findViewById(R.id.btn_employee);
            viewEmployee = view.findViewById(R.id.view_employee);
        }
    }

    /**
     * 设置头像的方法
     */
    private void setHeaderIcon(final CircleImageView ivIcon, String imgurl) {
        OkHttpUtils
                .get()//
                .url(imgurl)//
                .tag(this)//
                .build()//
                .connTimeOut(15000)
                .readTimeOut(15000)
                .writeTimeOut(15000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        LogUtils.e(TAG, ":setHeaderIcon:", e);
                    }

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        ivIcon.setImageBitmap(bitmap);
                    }
                });
    }
}
