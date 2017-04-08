package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.UnitListBean;

import java.util.ArrayList;

/**
 * Created by LUOLI on 2017/3/27.
 */
public class AdapterPlanUnitList extends BaseAdapter/*RecyclerView.Adapter<AdapterPlanUnitList.MyViewHolder> implements View.OnClickListener*/ {
    private Context mContext;
    private ArrayList<UnitListBean.Data.Units> mList;
    private MyViewHolder hold;

    public AdapterPlanUnitList(Context context, ArrayList<UnitListBean.Data.Units> list) {
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
            convertView = View.inflate(mContext, R.layout.item_plan_number_unfull, null);

            hold = new MyViewHolder(convertView);
            convertView.setTag(hold);
        } else {
            hold = (MyViewHolder) convertView.getTag();
        }
        UnitListBean.Data.Units units = mList.get(position);
        String unitName = units.unit_name;
        String personNum = units.person_num;
        String maxNum = units.max_num;
        String unitId = units.unit_id;
        String type = units.type;
        if (TextUtils.equals(type, 1 + "")) {
            hold.tvPalnNum.setVisibility(View.VISIBLE);
            hold.tvPalnNum.setText(personNum + "/" + maxNum);
            hold.ivIcon.setBackgroundResource(R.drawable.paln_member_add);
        } else if (TextUtils.equals(type, 2 + "")) {
            hold.tvPalnNum.setVisibility(View.GONE);
            hold.ivIcon.setBackgroundResource(R.drawable.paln_member_eye);
        }
        hold.tvPalnName.setText(unitName);

        return convertView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlItem;
        TextView tvPalnName;
        TextView tvPalnNum;
        ImageView ivIcon;

        public MyViewHolder(View view) {
            super(view);
            rlItem = (RelativeLayout) view.findViewById(R.id.rl_item_plan_number_unfull);
            tvPalnName = (TextView) view.findViewById(R.id.tv_item_plan_member_nofull_name);
            tvPalnNum = (TextView) view.findViewById(R.id.tv_item_plan_member_nofull_num);
            tvPalnNum = (TextView) view.findViewById(R.id.tv_item_plan_member_nofull_num);
            ivIcon = (ImageView) view.findViewById(R.id.iv_item_plan_member_nofull_icon);
        }
    }
}
