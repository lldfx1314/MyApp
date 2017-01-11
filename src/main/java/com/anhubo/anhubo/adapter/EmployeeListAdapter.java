package com.anhubo.anhubo.adapter;

import android.app.Activity;
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
import com.anhubo.anhubo.utils.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Created by LUOLI on 2017/1/11.
 */
public class EmployeeListAdapter extends BaseAdapter {
    private static final String TAG = "EmployeeListAdapter";
    private Context mContext;
    private ArrayList<EmployeeListBean.Data.User_info> mList;
    private ViewHolder hold;

    public EmployeeListAdapter(Context context, ArrayList<EmployeeListBean.Data.User_info> list) {
        this.mContext = context;
        this.mList = list;
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
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_employee, null);
            hold = new ViewHolder(convertView);
            convertView.setTag(hold);
        } else {
            hold = (ViewHolder) convertView.getTag();
        }
        EmployeeListBean.Data.User_info userInfo = mList.get(position);
        String picPath = userInfo.pic_path;
        if (!TextUtils.isEmpty(picPath)) {
            setHeaderIcon(hold.ivIcon, picPath);
        }
        hold.tvName.setText(userInfo.username);
        hold.viewEmployee.setVisibility(View.VISIBLE);
        return convertView;

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
