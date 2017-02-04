package com.anhubo.anhubo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.CellListBean;
import com.anhubo.anhubo.ui.activity.unitDetial.CellListActivity;

import java.util.List;

/**
 * Created by LUOLI on 2017/1/3.
 */
public class CellListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private List<CellListBean.Data.Units> units;
    private ViewHolder holder;
    private OnBtnClickListener btnClickListener;

    public CellListAdapter(Context context, List<CellListBean.Data.Units> units, OnBtnClickListener btnClickListener) {
        this.mContext = context;
        this.units = units;
        this.btnClickListener = btnClickListener;
    }

    @Override
    public int getCount() {
        return units == null?0:units.size();
    }

    @Override
    public Object getItem(int position) {
        return units.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(mContext, R.layout.item_cell_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        CellListBean.Data.Units unit = this.units.get(position);
        holder.textView.setText(unit.unit_name);
        holder.button.setText("加入");
        holder.button.setOnClickListener(this);
        holder.button.setTag(position);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        btnClickListener.onbtnClick(v);
    }




    public interface OnBtnClickListener{
        void onbtnClick(View v);
    }


    static class ViewHolder{

        TextView textView;
        Button button;

        public ViewHolder(View view){
            textView = (TextView) view.findViewById(R.id.item_tv_unit_list);
            button =  (Button)  view.findViewById(R.id.item_btn_unit_list);
        }
    }
}
