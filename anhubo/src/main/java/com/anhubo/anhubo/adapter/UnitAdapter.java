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
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by LUOLI on 2016/10/24.
 */
public class UnitAdapter extends BaseAdapter {
    private static final String TAG = "UnitAdapter";
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
        //如果不过万，就在小数后保留2位，并且四舍五入
        DecimalFormat df2 = new DecimalFormat("###.00");

        // 上旬三色评分
        if (!TextUtils.isEmpty(score)) {
            if (score.contains(".")) {
                //　包含小数点,计算出小数点的索引
                int i = score.indexOf(".", 0);
                //截取
                String s = score.substring(0, i);
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
        setShowDetial(planEnsure, hold.heighHelpMoney);
        // 单次分摊额
        if (!TextUtils.isEmpty(planMoneyProportion)) {
            // 比例
            hold.prepayPproportion.setText(planMoneyProportion);
        }
        // 现金
        setShowDetial(planMoney, hold.prepayShareMoney);

        return convertView;
    }

    private void setShowDetial(String string, TextView textView) {
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
                    } else if (substring.endsWith(".0")) {
                        setWan(substring.substring(0, 1) + "万");
                    }
                } else {
                    setWan(str + "万");

                }
                textView.setHorizontallyScrolling(true);
                textView.setText(ss);
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

    class MyURLSpan extends URLSpan {


        public MyURLSpan(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setTextSize(DisplayUtil.sp2px(mContext, 8));
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
