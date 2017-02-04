package com.anhubo.anhubo.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class BaseListAdapter<T> extends BaseAdapter {
    protected ArrayList<T> datas;

    public BaseListAdapter(ArrayList<T> datas) {
        this.datas = datas;
    }

    /**
     * 获取Adapter中的数据，datas不可能会是null，只有可能是datas.isEmpty()
     */
    public ArrayList<T> getDatas() {
        if (datas == null) {
            datas = new ArrayList<T>();
        }
        return datas;
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

    public View getView(int position, View convertView, ViewGroup parent) {
        Object holder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(),
                    getItemLayoutId(position), null);
            holder = createViewHolder(position, convertView);

            convertView.setTag(holder);
        } else {
            holder = convertView.getTag();
        }
        T data = datas.get(position);
        showData(position, holder, data);
        return convertView;
    }

    /**
     * 返回一个用于创建item条目的布局id
     *
     * @param position 要生成item的位置
     */
    public abstract int getItemLayoutId(int position);

    /**
     * 创建ViewHolder，并且把convertView中的控件查找到保存到ViewHolder
     *
     * @param position
     * @param convertView
     * @return
     */
    public abstract Object createViewHolder(int position, View convertView);

    /**
     * 显示数据到viewHolder中的控件中
     *
     * @param position
     * @param viewHolder
     * @param data
     */
    public abstract void showData(int position, Object viewHolder, T data);

}
