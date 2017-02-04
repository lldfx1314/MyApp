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
<<<<<<< HEAD
    private String planId;
    private SpannableString ss;
    private String planEnsure;
    private String planEnsureProportion;
    private String planMoneyProportion;
    private String planMoney;
    private String time;
    private String score;
=======
    private String status;
    private String planId;
    private String maxEachMoney;
    private String maxPlanEnsure;
    private String planMoneyLast;
    private SpannableString ss;
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce

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
<<<<<<< HEAD
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
        //如果不过万，就在小数后保留2位，并且四舍五入
        DecimalFormat df2 = new DecimalFormat("###.00");

        // 上旬三色评分
        if (!TextUtils.isEmpty(score)) {
            if (score.contains(".")) {
                //　包含小数点,计算出小数点的索引
                int i = score.indexOf(".", 0);
                //截取
                String s = score.substring(0,i);
                hold.threeScore.setText(s);
            } else {
                hold.threeScore.setText(score);
            }
        }
        // 最高互助额
        if (!TextUtils.isEmpty(planEnsureProportion)) {
            // 比例
            hold.heighHelpProportion.setText(planEnsureProportion);
        }
        // 现金
        setShowDetial(planEnsure,hold.heighHelpMoney);

        // 单次分摊额
        if (!TextUtils.isEmpty(planMoneyProportion)) {
            // 比例
            hold.prepayPproportion.setText(planMoneyProportion);
        }
        // 现金
        setShowDetial(planMoney,hold.prepayShareMoney);

        return convertView;
    }

    private void setShowDetial(String string,TextView textView) {
        if (!TextUtils.isEmpty(string)) {
            Double heighSharing = Double.parseDouble(string) / 10000;

            if (heighSharing < 1) {
                textView.setText(string.substring(0, 4));
            } else if (heighSharing >= 1) {
                String str = String.valueOf(heighSharing);
                if (str.length() >= 3) {
                    String substring = str.substring(0, 3);
                    //　做判断，防止显示类似＂50.万＂这样的情况
                    if (substring.endsWith(".")) {
                        setWan(substring.substring(0, 2) + "万");
                    } else {
                        setWan(substring + "万");
                    }
                } else {
                    setWan(str + "万");

                }
                hold.heighHelpMoney.setHorizontallyScrolling(true);
                hold.heighHelpMoney.setText(ss);
            }
        }
    }

    /**
     * 设置万字大小
     */
    private void setWan(String string) {

        ss = new SpannableString(string);
        MyURLSpan myURLSpan = new MyURLSpan(string);
        ss.setSpan(myURLSpan, string.length() - 1, string.length(), SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
    }

=======
        status = cert.status;
        planId = cert.plan_id;
        maxPlanEnsure = cert.max_plan_ensure;
        maxEachMoney = cert.max_each_money;
        planMoneyLast = cert.plan_money_last;
        // 把这些信息赋值给相应的控件
        if (TextUtils.equals("运行期", status)) {
            // 设置运行期对应的样式
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
        //如果不过万，就在小数后保留2位，并且四舍五入
        DecimalFormat df2  = new DecimalFormat("###.00");
        // 最高保障额
        if (!TextUtils.isEmpty(maxPlanEnsure)) {

            Double heighSecurity = Double.parseDouble(maxPlanEnsure) / 10000;
            if (heighSecurity < 1) {
                // 不过万
                double parseDouble = Double.parseDouble(maxPlanEnsure.substring(0, 8));
                hold.tvHeighSecurity.setText(df2.format(parseDouble));
            } else if (heighSecurity >= 1) {
                // 过万
                String str = String.valueOf(heighSecurity);
                if (str.length() >= 7) {
                    setWan(str.substring(0, 7) + "万");
                    hold.tvHeighSecurity.setHorizontallyScrolling(true);
                    hold.tvHeighSecurity.setText(ss);
                } else {
                    setWan(str + "万");
                    hold.tvHeighSecurity.setHorizontallyScrolling(true);
                    hold.tvHeighSecurity.setText(ss);
                }
            }

        }
        // 最高分摊额
        if (!TextUtils.isEmpty(maxEachMoney)) {
            Double heighSharing = Double.parseDouble(maxEachMoney) / 10000;

            if (heighSharing < 1) {
                double parseDouble = Double.parseDouble(maxEachMoney.substring(0, 8));
                hold.tvHeighSharing.setText(df2.format(parseDouble));
            } else if (heighSharing >= 1) {
                String str = String.valueOf(heighSharing);
                if (str.length() >= 7) {
                    setWan(str.substring(0, 7) + "万");
                    hold.tvHeighSharing.setHorizontallyScrolling(true);
                    hold.tvHeighSharing.setText(ss);
                } else {
                    setWan(str + "万");
                    hold.tvHeighSharing.setHorizontallyScrolling(true);
                    hold.tvHeighSharing.setText(ss);
                }
            }
        }
        // 预付保障金
        if (!TextUtils.isEmpty(planMoneyLast)) {
            Double prepayMoney = Double.parseDouble(planMoneyLast) / 10000;

            if (prepayMoney < 1) {

                double parseDouble = Double.parseDouble(planMoneyLast.substring(0, 8));
                hold.tvPrepayMoney.setText(df2.format(parseDouble));
            } else if (prepayMoney >= 1) {

                String str = String.valueOf(prepayMoney);
                if (str.length() >= 7) {
                    setWan(str.substring(0, 7) + "万");
                    hold.tvPrepayMoney.setHorizontallyScrolling(true);
                    hold.tvPrepayMoney.setText(ss);
                } else {
                    setWan(str + "万");
                    hold.tvPrepayMoney.setHorizontallyScrolling(true);
                    hold.tvPrepayMoney.setText(ss);
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
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
    class MyURLSpan extends URLSpan {


        public MyURLSpan(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
<<<<<<< HEAD
            ds.setTextSize(DisplayUtil.sp2px(mContext, 8));
=======
            ds.setTextSize(DisplayUtil.sp2px(mContext,8));
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
        }
    }

    static class ViewHolder {
<<<<<<< HEAD


=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
        // 计划名和时期
        TextView tvPlanName;
        TextView tvPlanStatus;
        // 三个圆圈
        RelativeLayout rlHeighSecurity;
        RelativeLayout rlHeighSharing;
        RelativeLayout rlPrepayMoney;
<<<<<<< HEAD

        TextView threeScore;
        TextView heighHelpProportion;
        TextView heighHelpMoney;
        TextView prepayPproportion;
        TextView prepayShareMoney;
=======
        // 三个金额
        TextView tvHeighSecurity;
        TextView tvHeighSharing;
        TextView tvPrepayMoney;
        // 三个图标
        ImageView ivHeighSecurity;
        ImageView ivHeighSharing;
        ImageView ivPrepayMoney;
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce

        public ViewHolder(View view) {
            // 计划名和时期
            tvPlanName = (TextView) view.findViewById(R.id.tv_planName);
            tvPlanStatus = (TextView) view.findViewById(R.id.tv_planStatus);

            // 三个圆圈
            rlHeighSecurity = (RelativeLayout) view.findViewById(R.id.rl_heigh_security);
            rlHeighSharing = (RelativeLayout) view.findViewById(R.id.rl_heigh_sharing);
            rlPrepayMoney = (RelativeLayout) view.findViewById(R.id.rl_prepay_money);
            // 三个金额
<<<<<<< HEAD
            threeScore = (TextView) view.findViewById(R.id.three_score);
            heighHelpProportion = (TextView) view.findViewById(R.id.heigh_help_m_proportion);
            heighHelpMoney = (TextView) view.findViewById(R.id.heigh_help_m);
            prepayPproportion = (TextView) view.findViewById(R.id.prepay_m_proportion);
            prepayShareMoney = (TextView) view.findViewById(R.id.prepay_share_m);

=======
            tvHeighSecurity = (TextView) view.findViewById(R.id.tv_heigh_security);
            tvHeighSharing = (TextView) view.findViewById(R.id.tv_height_sharing);
            tvPrepayMoney = (TextView) view.findViewById(R.id.tv_prepay_money);
            // 三个图标
            ivHeighSecurity = (ImageView) view.findViewById(R.id.iv_heigh_security);
            ivHeighSharing = (ImageView) view.findViewById(R.id.iv_heigh_sharing);
            ivPrepayMoney = (ImageView) view.findViewById(R.id.iv_prepay_money);
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
        }
    }
}
