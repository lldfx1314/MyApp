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
    private  ArrayList<String> listUserName;
    private  ArrayList<String> listTypeId;
    ViewHolder hold;
    private Map<String, String> map;
    private String string;

    public UnitMenuAdapter(Context context, ArrayList<String> listTime, ArrayList<String> listUserName, ArrayList<String> listTypeId) {
        this.mContext = context;
        this.listTime = listTime;
        this.listUserName = listUserName;
        this.listTypeId = listTypeId;
        // 定义一个map集合，用来存放学习、检查或者演练
        map = new HashMap<>();
        
        //　遍历集合listTypeId
        for (int i = 0; i < listTypeId.size(); i++) {
            string = listTypeId.get(i);

            if(string !=null){
                if(string.equals(1+"")){
                    map.put(string,"学习");
                }else if(string.equals(2+"")){
                    map.put(string,"设备检查");
                }else if(string.equals(3+"")) {
                    map.put(string,"演练");
                }
            }
        }

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
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_unit_study, null);
            hold = new ViewHolder(convertView);
            convertView.setTag(hold);
        } else {
            hold = (ViewHolder) convertView.getTag();
        }
        String s = "";
        for (int i = 0; i < listTypeId.size(); i++) {
            String str = listTypeId.get(i);
            s = map.get(str);
        }
        hold.txt.setText(listUserName.get(position)+"完成了一次"+s);
        return convertView;

    }

    static class ViewHolder {
        TextView txt;
        public ViewHolder(View view) {
            txt = (TextView) view.findViewById(R.id.tv_study);
        }
    }
}
