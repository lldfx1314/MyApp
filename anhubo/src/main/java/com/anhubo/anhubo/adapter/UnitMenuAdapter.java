package com.anhubo.anhubo.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.StudyBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by Administrator on 2016/10/11.
 */
public class UnitMenuAdapter extends BaseAdapter {

    private final ArrayList<Object> datas;
    private Context mContext;
    ViewHolder hold;
    ViewHolder2 hold2;
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;


    public UnitMenuAdapter(Context mContext, ArrayList<Object> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public int getItemViewType(int position) {
        // 返回条目类型
        if (datas.get(position) instanceof String) {
            return TYPE_2;
        } else {
            return TYPE_1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
                    convertView = View.inflate(mContext, R.layout.item_unit_study, null);
                    hold = new ViewHolder(convertView);
                    convertView.setTag(hold);
                    break;
                case TYPE_2:
                    convertView = View.inflate(mContext, R.layout.item_unit_study2, null);
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

                StudyBean.Data.Records.Record_list record = (StudyBean.Data.Records.Record_list) datas.get(position);
                String typeIDStr = getTypeIDStr(record.type_id);
                hold.tvStudy.setText(record.user_name + " 完成了一次" + typeIDStr);
                hold.tvdeviceTime.setText(record.time_ext);
                if (TextUtils.equals(typeIDStr, "学习")) {
<<<<<<< HEAD
                    hold.tvdeviceName.setText("学习成绩:" + record.study_score+"分");
=======
                    hold.tvdeviceName.setText("学习成绩:" + record.study_score);
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
                } else if (TextUtils.equals(typeIDStr, "设备检查")) {
                    String deviceTypeName = record.device_type_name;
                    if (TextUtils.equals("", deviceTypeName)) {
                        hold.tvdeviceName.setText("设备名称:" + "设备已删除");
                    } else {
                        hold.tvdeviceName.setText("设备名称:" + record.device_type_name);
                    }
                }

                break;
            case TYPE_2:
                // 取时间记录
                String time = (String) datas.get(position);
                hold2.tvTimeRecord.setText(time);
                break;
            default:
                break;
        }


        return convertView;


    }

    public String getTypeIDStr(String id) {
        if (id.equals("1")) {
            return "学习";
        } else if (id.equals("2")) {
            return "设备检查";
        } else if (id.equals("3")) {
            return "演练";
        }
        return "";
    }


    class ViewHolder {
        TextView tvStudy;
        TextView tvdeviceTime;
        TextView tvdeviceName;

        public ViewHolder(View view) {
            tvStudy = (TextView) view.findViewById(R.id.tv_study);
            tvdeviceTime = (TextView) view.findViewById(R.id.tv_deviceTime);
            tvdeviceName = (TextView) view.findViewById(R.id.tv_deviceName);
        }
    }

    class ViewHolder2 {
        TextView tvTimeRecord;

        public ViewHolder2(View view) {
            tvTimeRecord = (TextView) view.findViewById(R.id.tv_timeRecord);
        }
    }
}
