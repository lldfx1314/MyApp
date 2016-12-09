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

    private Context mContext;
    private HashMap<String, ArrayList<StudyBean.Data.Records.Record_list>> recordMap;
    private ArrayList<String> listTime;
    private ArrayList<String> listTypeId;
    private ArrayList<String> listuserName;
    private ArrayList<String> listDeviceTypeName;
    private ArrayList<String> listStudyScore;
    private ArrayList<String> listTimeExt;
    private int pager;
    ViewHolder hold;
    ViewHolder2 hold2;
    private Map<String, String> map;
    private String string;
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;
    private int i = 0;

    public UnitMenuAdapter(Context context, HashMap<String, ArrayList<StudyBean.Data.Records.Record_list>> recordMap,
                           ArrayList<String> listTime, ArrayList<String> listTypeId, ArrayList<String> listuserName,
                           ArrayList<String> listDeviceTypeName, ArrayList<String> listStudyScore, ArrayList<String> listTimeExt,
                           int pager) {
        this.mContext = context;
        this.recordMap = recordMap;
        this.listTime = listTime;
        this.listTypeId = listTypeId;
        this.listuserName = listuserName;
        this.listDeviceTypeName = listDeviceTypeName;
        this.listStudyScore = listStudyScore;
        this.listTimeExt = listTimeExt;
        this.pager = pager;


        setMap(listTypeId);

    }


    private void setMap(ArrayList<String> listTypeId) {
        // 定义一个map集合，用来存放学习、检查或者演练
        map = new HashMap<>();

        //　遍历集合listTypeId
        for (int i = 0; i < listTypeId.size(); i++) {
            string = listTypeId.get(i);

            if (string != null) {
                if (string.equals(1 + "")) {
                    map.put(1 + "", "学习");
                } else if (string.equals(2 + "")) {
                    map.put(2 + "", "设备检查");
                } else if (string.equals(3 + "")) {
                    map.put(3 + "", "演练");
                }
            }
        }
    }


    @Override
    public int getItemViewType(int position) {

        //int p = position % 20;
        for (int m = 0; m < listTypeId.size(); m++) {
            String string = listTypeId.get(m);
            if (TextUtils.equals(string, "#")) {
                return TYPE_2;
            } else {
                return TYPE_1;
            }
        }
        return TYPE_2;
    }

    @Override
    public int getViewTypeCount() {

        return 2;

    }

    @Override
    public int getCount() {
        return listTypeId == null ? 0 : listTypeId.size();
    }

    @Override
    public Object getItem(int position) {
        return listTypeId.get(position);
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

                String s = "";
                for (int j = 0; j < listTypeId.size(); j++) {
                    String str = listTypeId.get(position);
                    if (!TextUtils.equals(str, "#")) {
                        s = map.get(str);
                    } else {

                    }
                }

                hold.tvStudy.setText(listuserName.get(position) + " 完成了一次" + s);
                hold.tvdeviceTime.setText(listTimeExt.get(position));
                if (TextUtils.equals(s, "学习")) {
                    hold.tvdeviceName.setText("学习成绩:" + listStudyScore.get(position));
                } else if (TextUtils.equals(s, "设备检查")) {
                    hold.tvdeviceName.setText("设备名称:" + listDeviceTypeName.get(position));
                }
                break;
            case TYPE_2:
                // 取时间记录
                hold2.tvTimeRecord.setText(listTime.get(i++));
                break;
            default:
                break;
        }


        return convertView;


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
