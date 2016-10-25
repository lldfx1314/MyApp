package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anhubo.anhubo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/10/11.
 */
public class UnitMenuAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> listTime;
    private ArrayList<String> listUserName;
    private ArrayList<String> listTypeId;
    private int pager;
    ViewHolder hold;
    ViewHolder2 hold2;
    private Map<String, String> map;
    private String string;

    final int TYPE_1 = 0;
    final int TYPE_2 = 1;

    public UnitMenuAdapter(Context context, ArrayList<String> listTime, ArrayList<String> listUserName, ArrayList<String> listTypeId, int pager) {
        this.mContext = context;
        this.listTime = listTime;
        this.listUserName = listUserName;
        this.listTypeId = listTypeId;
        this.pager = pager;



    }

    private void setMap(ArrayList<String> listTypeId) {
        // 定义一个map集合，用来存放学习、检查或者演练
        map = new HashMap<>();

        //　遍历集合listTypeId
        for (int i = 0; i < listTypeId.size(); i++) {
            string = listTypeId.get(i);

            if (string != null) {
                if (string.equals(1 + "")) {
                    map.put(1+"", "学习");
                } else if (string.equals(2 + "")) {
                    map.put(2+"", "设备检查");
                } else if (string.equals(3 + "")) {
                    map.put(3+"", "演练");
                }
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        setMap(listTypeId);
        int p = position % 20;

        if (p == 0) {

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
        return listUserName == null ? 0 : listUserName.size();
    }

    @Override
    public Object getItem(int position) {
        return listUserName.get(position);
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
                    String str = listTypeId.get(j);
                    s = map.get(str);
                }
                hold.tvStudy.setText(listUserName.get(position) + "完成了一次" + s);
                break;
            case TYPE_2:

                for (int i = 0; i < listTime.size(); i++) {
                    if (i <= pager) {


                        hold2.tvTimeRecord.setText(listTime.get(i));
                        break;
                    }
                }
                break;
            default:
                break;
        }

        return convertView;


    }


    class ViewHolder {
        TextView tvStudy;

        public ViewHolder(View view) {
            tvStudy = (TextView) view.findViewById(R.id.tv_study);
        }
    }

    class ViewHolder2 {
        TextView tvTimeRecord;

        public ViewHolder2(View view) {
            tvTimeRecord = (TextView) view.findViewById(R.id.tv_timeRecord);
        }
    }
}
