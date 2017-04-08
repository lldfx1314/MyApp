package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.PlanMemberBean;
import com.anhubo.anhubo.utils.PopBirthHelper;

import java.util.ArrayList;

/**
 * Created by LUOLI on 2017/3/27.
 */
public class AdapterPlanNumberUnFull extends RecyclerView.Adapter<AdapterPlanNumberUnFull.MyViewHolder> implements View.OnClickListener{
    private Context mContext;
    private ArrayList<PlanMemberBean.Data.Unfilled_units> mList;

    public AdapterPlanNumberUnFull(Context context, ArrayList<PlanMemberBean.Data.Unfilled_units> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_plan_number_unfull, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PlanMemberBean.Data.Unfilled_units unfilledUnits = mList.get(position);
        String unitName = unfilledUnits.unit_name;
        String personNum = unfilledUnits.person_num;
        String maxNum = unfilledUnits.max_num;
        String unitId = unfilledUnits.unit_id;
        holder.tvPalnName.setText(unitName);
        holder.tvPalnNum.setText(personNum+"/"+maxNum);
        holder.rlItem.setOnClickListener(this);
        holder.rlItem.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if(itemClickListener!=null){
            itemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }
    public NoPlanItemClickListener itemClickListener;

    public void setItemClickListener(NoPlanItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public interface NoPlanItemClickListener {

        /**
         * Item里选择点名称的点击事件
         */
        void onItemClick(View view, int position);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlItem;
        TextView tvPalnName;
        TextView tvPalnNum;

        public MyViewHolder(View view) {
            super(view);
            rlItem = (RelativeLayout) view.findViewById(R.id.rl_item_plan_number_unfull);
            tvPalnName = (TextView) view.findViewById(R.id.tv_item_plan_member_nofull_name);
            tvPalnNum = (TextView) view.findViewById(R.id.tv_item_plan_member_nofull_num);
        }
    }
}
