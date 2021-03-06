package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.EmployeeListBean;
import com.anhubo.anhubo.bean.EmployeeOperate;
import com.anhubo.anhubo.interfaces.InterClick;
import com.anhubo.anhubo.utils.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import com.squareup.okhttp.Request;

/**
 * Created by LUOLI on 2017/1/11.
 */
public class AssignmentAdminAdapter extends BaseAdapter implements View.OnClickListener {
    private static final String TAG = "AssignmentAdminAdapter";
    private Context mContext;
    private ArrayList<EmployeeListBean.Data.User_info> mList;
    private ViewHolder hold;
    private InterClick mCallback;
    private int selectedPosition = -1;

    public AssignmentAdminAdapter(Context context, ArrayList<EmployeeListBean.Data.User_info> list, InterClick callback) {
        this.mContext = context;
        this.mList = list;
        this.mCallback = callback;
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
        int status = userInfo.status;
        int userType = userInfo.user_type;
//        LogUtils.eNormal(TAG+":haha",userInfo.username);
        // 显示头像、姓名、分割线
        if (userType == 0) {
            // 把除管理员之外的员工显示在列表上
            String picPath = userInfo.pic_path;
            hold.ivIcon.setTag(R.id.image_tag,picPath);
            String tag = (String) hold.ivIcon.getTag(R.id.image_tag);
            if (!TextUtils.isEmpty(picPath)) {
                if (TextUtils.equals(tag , picPath)) {
                    // 防止图片错位
                    setHeaderIcon(hold.ivIcon, picPath);
                }
            } else {
                hold.ivIcon.setImageResource(R.drawable.newicon);
            }
            hold.tvName.setText(userInfo.username);
            // 显示选择小圈
            hold.ivEmployeeChoose.setVisibility(View.VISIBLE);


            // 显示分割线
            hold.viewEmployee.setVisibility(View.VISIBLE);
            // 显示选中条目的效果
            if (selectedPosition == position) {
                hold.ivEmployeeChoose.setImageResource(R.drawable.fuxuan_input01);
            } else {
                hold.ivEmployeeChoose.setImageResource(R.drawable.fuxuan_input02);
            }
        }

        hold.btnEmployee.setOnClickListener(this);
        hold.btnEmployee.setTag(position);


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

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    static class ViewHolder {
        CircleImageView ivIcon;
        TextView tvName;
        Button btnEmployee;
        ImageView ivEmployeeChoose;
        View viewEmployee;

        public ViewHolder(View view) {
            ivIcon = (CircleImageView) view.findViewById(R.id.iv_employee_icon);
            tvName = (TextView) view.findViewById(R.id.tv_employee_name);
            btnEmployee = (Button) view.findViewById(R.id.btn_employee);
            ivEmployeeChoose = (ImageView) view.findViewById(R.id.iv_employee_choose);
            viewEmployee = view.findViewById(R.id.view_employee);
        }
    }

    /**
     * 设置头像的方法
     */
    private void setHeaderIcon(final CircleImageView ivIcon, String imgurl) {

        Glide.with(mContext)
                .load(imgurl)
                .centerCrop()
                .crossFade(800)
                .error(R.drawable.newicon)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .into(ivIcon);

    }
}
