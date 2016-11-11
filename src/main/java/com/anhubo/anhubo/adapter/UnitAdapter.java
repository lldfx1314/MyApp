package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.Unit_PlanBean;
import com.anhubo.anhubo.ui.impl.UnitFragment;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by LUOLI on 2016/10/24.
 */
public class UnitAdapter extends BaseAdapter {
    private Context mContext;
    private List<Unit_PlanBean.Data.Certs> certs;
    ViewHolder hold;
    private String planName;
    private String status;
    private String planId;
    private String maxEachMoney;
    private String maxPlanEnsure;
    private String planMoneyLast;

    public UnitAdapter(Context context, List<Unit_PlanBean.Data.Certs> certs) {
        this.mContext = context;
        this.certs = certs;
    }

    @Override
    public int getCount() {
        return certs == null ? 0 : certs.size();
    }

    @Override
    public Object getItem(int position) {
        return certs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_plan, null);
            hold = new ViewHolder(convertView);
            convertView.setTag(hold);
        } else {
            hold = (ViewHolder) convertView.getTag();
        }
        //获取到一组信息
        Unit_PlanBean.Data.Certs cert = certs.get(position);
        planName = cert.plan_name;
        status = cert.status;
        planId = cert.plan_id;
        maxEachMoney = cert.max_each_money;
        maxPlanEnsure = cert.max_plan_ensure;
        planMoneyLast = cert.plan_money_last;
        // 把这些信息赋值给相应的控件
        if (TextUtils.equals("运行期", status)) {
            hold.ivHeighSecurity.setBackgroundResource(R.drawable.heigh_security);
            hold.ivHeighSharing.setBackgroundResource(R.drawable.heigh_sharing);
            hold.ivPrepayMoney.setBackgroundResource(R.drawable.prepay_money);

            hold.rlHeighSecurity.setBackgroundResource(R.drawable.rl_normal);
            hold.rlHeighSharing.setBackgroundResource(R.drawable.rl_normal);
            hold.rlPrepayMoney.setBackgroundResource(R.drawable.rl_normal);
        }

        hold.tvPlanName.setText(planName);
        hold.tvPlanStatus.setText(status);
        // 如果是"",就显示0
        if (TextUtils.equals(maxPlanEnsure, "")) {
            hold.tvHeighSecurity.setText(0 + "");
        }
        if (TextUtils.equals(maxEachMoney, "")) {
            hold.tvHeighSharing.setText(0 + "");
        }
        if (TextUtils.equals(planMoneyLast, "")) {
            hold.tvPrepayMoney.setText(0 + "");
        }
        if(!TextUtils.isEmpty(maxPlanEnsure)||!TextUtils.isEmpty(maxEachMoney)||!TextUtils.isEmpty(planMoneyLast)){
            Double heighSecurity = Double.parseDouble(maxPlanEnsure)/10000;
            Double heighSharing = Double.parseDouble(maxEachMoney)/10000;
            Double prepayMoney = Double.parseDouble(planMoneyLast)/10000;

            // 最高保障额
            if (heighSecurity < 1) {
                hold.tvHeighSecurity.setText(maxPlanEnsure);
            } else if (heighSecurity >= 1) {
                hold.tvHeighSecurity.setText(heighSecurity+"万");
            }


            // 最高分摊额
            if (heighSharing < 1) {
                hold.tvHeighSecurity.setText(maxEachMoney);
            } else if (heighSecurity >= 1) {
                hold.tvHeighSecurity.setText(heighSharing+"万");
            }
            // 预付保障金
            if (heighSecurity < 1) {
                hold.tvHeighSecurity.setText(planMoneyLast);
            } else if (heighSecurity >= 1) {
                hold.tvHeighSecurity.setText(prepayMoney+"万");
            }
        }




        return convertView;

    }


    static class ViewHolder {
        // 计划名和时期
        TextView tvPlanName;
        TextView tvPlanStatus;
        // 三个圆圈
        RelativeLayout rlHeighSecurity;
        RelativeLayout rlHeighSharing;
        RelativeLayout rlPrepayMoney;
        // 三个金额
        TextView tvHeighSecurity;
        TextView tvHeighSharing;
        TextView tvPrepayMoney;
        // 三个图标
        ImageView ivHeighSecurity;
        ImageView ivHeighSharing;
        ImageView ivPrepayMoney;

        public ViewHolder(View view) {
            // 计划名和时期
            tvPlanName = (TextView) view.findViewById(R.id.tv_planName);
            tvPlanStatus = (TextView) view.findViewById(R.id.tv_planStatus);

            // 三个圆圈
            rlHeighSecurity = (RelativeLayout) view.findViewById(R.id.rl_heigh_security);
            rlHeighSharing = (RelativeLayout) view.findViewById(R.id.rl_heigh_sharing);
            rlPrepayMoney = (RelativeLayout) view.findViewById(R.id.rl_prepay_money);
            // 三个金额
            tvHeighSecurity = (TextView) view.findViewById(R.id.tv_heigh_security);
            tvHeighSharing = (TextView) view.findViewById(R.id.tv_height_sharing);
            tvPrepayMoney = (TextView) view.findViewById(R.id.tv_prepay_money);
            // 三个图标
            ivHeighSecurity = (ImageView) view.findViewById(R.id.iv_heigh_security);
            ivHeighSharing = (ImageView) view.findViewById(R.id.iv_heigh_sharing);
            ivPrepayMoney = (ImageView) view.findViewById(R.id.iv_prepay_money);
        }
    }
}
