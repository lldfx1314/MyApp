package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.PlanHelpSumMoneyBean;
import com.anhubo.anhubo.interfaces.InterClick;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/03/28.
 */
public class AdapterPlanHelpSumMoney extends BaseAdapter implements View.OnClickListener {

    private final ArrayList<Object> datas;
    private Context mContext;
    private InterClick mCallback;
    ViewHolder hold;
    ViewHolder2 hold2;
    final int TYPE_1 = 1;
    final int TYPE_2 = 2;


    public AdapterPlanHelpSumMoney(Context mContext, ArrayList<Object> datas, InterClick mCallback) {
        this.mContext = mContext;
        this.datas = datas;
        this.mCallback = mCallback;
    }

    @Override
    public int getItemViewType(int position) {
        // 返回条目类型
        if (datas.get(position) instanceof String) {
            return TYPE_1;
        } else {
            return TYPE_2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }


    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_1:
                    convertView = View.inflate(mContext, R.layout.item_plan_sum_money_time, null);
                    hold = new ViewHolder(convertView);
                    convertView.setTag(hold);
                    break;
                case TYPE_2:
                    convertView = View.inflate(mContext, R.layout.item_plan_sum_money_time2, null);
                    hold2 = new ViewHolder2(convertView);
                    convertView.setTag(hold2);
                    break;

            }
        } else {

            switch (type) {
                case TYPE_1:
                    hold = (ViewHolder) convertView.getTag();
                    break;
                case TYPE_2:
                    hold2 = (ViewHolder2) convertView.getTag();
                    break;

            }
        }

        //设置资源
        switch (type) {

            case TYPE_1:
                // 取时间记录
                String time = (String) datas.get(position);
                hold.tvTimeYear.setText(time);
                break;
            case TYPE_2:
                PlanHelpSumMoneyBean.Data.Pics.Pic pic = (PlanHelpSumMoneyBean.Data.Pics.Pic) datas.get(position);
                String uploadDate = pic.upload_date;
                String status = pic.status;
                String picUrl = pic.pic_url;
                // 填充数据
                hold2.tvTime.setText(uploadDate);

                //设置标记
                hold2.ivPhoto.setTag(R.id.image_tag, position);
                if (TextUtils.equals(status, 0 + "")) {
                    // 未审核
                    hold2.tvStatus.setText("未审核");
                    hold2.tvStatus.setTextColor(Color.parseColor("#5e84ff"));
                    hold2.tvStatus.setBackgroundResource(R.drawable.shap_helpmoney_blue01);
                } else if (TextUtils.equals(status, 1 + "")) {
                    // 已经审核
                    hold2.tvStatus.setText("已审核");
                    hold2.tvStatus.setTextColor(Color.parseColor("#ffffff"));
                    hold2.tvStatus.setBackgroundResource(R.drawable.shap_helpmoney_blue02);
                }
                // 获取标记 防止错位
                int tag = (int) hold2.ivPhoto.getTag(R.id.image_tag);
                if (!TextUtils.isEmpty(picUrl)) {
                    if (TextUtils.equals(tag + "", position + "")) {
                        setMap(picUrl, hold2.ivPhoto);
                    }
                } else {
                    // 设置默认显示图片
                }
                hold2.ivPhoto.setOnClickListener(this);
                break;
            default:
                break;
        }
        return convertView;


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_item_helpmoney_photo:
                if (mCallback != null) {
                    mCallback.onBtnClick(v);
                }
                break;
            default:
                break;
        }
    }


    class ViewHolder {
        TextView tvTimeYear;


        public ViewHolder(View view) {
            tvTimeYear = (TextView) view.findViewById(R.id.tv_item_plan_summoney_year_time);

        }
    }

    class ViewHolder2 {

        TextView tvTime;
        TextView tvStatus;
        ImageView ivPhoto;

        public ViewHolder2(View view) {
            tvTime = (TextView) view.findViewById(R.id.tv_item_check_time);
            tvStatus = (TextView) view.findViewById(R.id.tv_item_check_status);
            ivPhoto = (ImageView) view.findViewById(R.id.iv_item_helpmoney_photo);
        }
    }

    /**
     * 设置图片显示
     */
    private void setMap(final String imgurl, ImageView imageView) {

        Glide.with(mContext)//activty
                .load(imgurl)
                .asBitmap()
                .thumbnail(0.1f)
                .into(imageView);
    }
}
