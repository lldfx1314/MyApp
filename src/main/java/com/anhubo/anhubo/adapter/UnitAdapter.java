package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.Unit_PlanBean;
import com.anhubo.anhubo.utils.DisplayUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by LUOLI on 2016/10/24.
 */
public class UnitAdapter extends BaseAdapter {
    private Context mContext;
    private List<Unit_PlanBean.Data.Certs> certs;
    ViewHolder hold;
    private String planName;
    private String planId;
    private SpannableString ss;
    private String planEnsure;
    private String planEnsureProportion;
    private String planMoneyProportion;
    private String planMoney;
    private String time;
    private String score;

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
        // 获取详细信息

        planName = cert.plan_name;
        planEnsure = cert.plan_ensure;
        planId = cert.plan_id;
        planEnsureProportion = cert.plan_ensure_proportion;
        planMoneyProportion = cert.plan_money_proportion;
        planMoney = cert.plan_money;
        time = cert.time;
        score = cert.score;
        // 把这些信息赋值给相应的控件
        if (!TextUtils.equals("运行期", time)) {
            // 设置非运行期对应的样式
            hold.rlHeighSecurity.setBackgroundResource(R.drawable.rl_gray);
            hold.rlHeighSharing.setBackgroundResource(R.drawable.rl_gray);
            hold.rlPrepayMoney.setBackgroundResource(R.drawable.rl_gray);
        }

        hold.tvPlanName.setText(planName);
        hold.tvPlanStatus.setText(time);
        // 如果是"",就显示0
//        if (TextUtils.equals(maxPlanEnsure, "")) {
//            hold.tvHeighSecurity.setText(0 + "");
//        }
//        if (TextUtils.equals(maxEachMoney, "")) {
//            hold.tvHeighSharing.setText(0 + "");
//        }
//        if (TextUtils.equals(planMoneyLast, "")) {
//            hold.tvPrepayMoney.setText(0 + "");
//        }
        //如果不过万，就在小数后保留2位，并且四舍五入
        DecimalFormat df2  = new DecimalFormat("###.00");
        // 上旬三色评分
        if (!TextUtils.isEmpty(score)) {
            hold.threeScore.setText(score);
        }
        // 最高互助额
        if(!TextUtils.isEmpty(planEnsureProportion)){
            hold.heighHelpProportion.setText(planEnsureProportion);
        }

        if (!TextUtils.isEmpty(planEnsure)) {
            Double heighSharing = Double.parseDouble(planEnsure) / 10000;

            if (heighSharing < 1) {
//                double parseDouble = Double.parseDouble(planEnsure.substring(0, 4));
                hold.heighHelpMoney.setText(planEnsure.substring(0, 4));
            } else if (heighSharing >= 1) {
                String str = String.valueOf(heighSharing);
                if (str.length() >= 3) {
                    setWan(str.substring(0, 3) + "万");
                    hold.heighHelpMoney.setHorizontallyScrolling(true);
                    hold.heighHelpMoney.setText(ss);
                } else {
                    setWan(str + "万");
                    hold.heighHelpMoney.setHorizontallyScrolling(true);
                    hold.heighHelpMoney.setText(ss);
                }
            }
        }
        // 预付分摊额
        if(!TextUtils.isEmpty(planMoneyProportion)){
            hold.prepayPproportion.setText(planMoneyProportion);
        }
        if (!TextUtils.isEmpty(planMoney)) {
            Double prepayMoney = Double.parseDouble(planMoney) / 10000;

            if (prepayMoney < 1) {

//                double parseDouble = Double.parseDouble(planMoney.substring(0, 4));
                hold.prepayShareMoney.setText(planMoney.substring(0, 4));
            } else if (prepayMoney >= 1) {

                String str = String.valueOf(prepayMoney);
                if (str.length() >= 3) {
                    setWan(str.substring(0, 3) + "万");
                    hold.prepayShareMoney.setHorizontallyScrolling(true);
                    hold.prepayShareMoney.setText(ss);
                } else {
                    setWan(str + "万");
                    hold.prepayShareMoney.setHorizontallyScrolling(true);
                    hold.prepayShareMoney.setText(ss);
                }

            }
        }
        return convertView;
    }
    /**设置万字大小*/
    private void setWan(String string){

        ss = new SpannableString(string);
        MyURLSpan myURLSpan = new MyURLSpan(string);
        ss.setSpan(myURLSpan, string.length()-1, string.length(), SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
    }
    class MyURLSpan extends URLSpan {


        public MyURLSpan(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setTextSize(DisplayUtil.sp2px(mContext,8));
        }
    }

    static class ViewHolder {


        // 计划名和时期
        TextView tvPlanName;
        TextView tvPlanStatus;
        // 三个圆圈
        RelativeLayout rlHeighSecurity;
        RelativeLayout rlHeighSharing;
        RelativeLayout rlPrepayMoney;

        TextView threeScore;
        TextView heighHelpProportion;
        TextView heighHelpMoney;
        TextView prepayPproportion;
        TextView prepayShareMoney;

        public ViewHolder(View view) {
            // 计划名和时期
            tvPlanName = (TextView) view.findViewById(R.id.tv_planName);
            tvPlanStatus = (TextView) view.findViewById(R.id.tv_planStatus);

            // 三个圆圈
            rlHeighSecurity = (RelativeLayout) view.findViewById(R.id.rl_heigh_security);
            rlHeighSharing = (RelativeLayout) view.findViewById(R.id.rl_heigh_sharing);
            rlPrepayMoney = (RelativeLayout) view.findViewById(R.id.rl_prepay_money);
            // 三个金额
            threeScore = (TextView) view.findViewById(R.id.three_score);
            heighHelpProportion = (TextView) view.findViewById(R.id.heigh_help_m_proportion);
            heighHelpMoney = (TextView) view.findViewById(R.id.heigh_help_m);
            prepayPproportion = (TextView) view.findViewById(R.id.prepay_m_proportion);
            prepayShareMoney = (TextView) view.findViewById(R.id.prepay_share_m);

        }
    }
}
