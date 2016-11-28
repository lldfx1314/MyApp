package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhubo.anhubo.R;

import java.util.ArrayList;

/**
 * Created by LUOLI on 2016/11/24.
 */
public class TestAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<String> listFather;
    private ArrayList<ArrayList<String>> listChild;
    public TestAdapter(Context context, ArrayList<String> listRequireTag, ArrayList<ArrayList<String>> listChild) {
        this.mContext = context;
        this.listFather = listRequireTag;
        this.listChild = listChild;
    }

    /**
     * 获取一级标签总数
     */
    @Override
    public int getGroupCount() {
        return listFather == null ? 0 : listFather.size();
    }

    /**
     * 获取一级标签下二级标签的总数
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        if (listChild.size() > 0) {

            //return listChild == null ? 0 : listChild.get(groupPosition).size();
            return listChild == null ? 0 : listChild.size();
        } else {
            return 0;
        }
    }

    /**
     * 获取一级标签内容
     */
    @Override
    public Object getGroup(int groupPosition) {
        return listFather.get(groupPosition);
    }

    /**
     * 获取一级标签下二级标签的内容
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChild.get(groupPosition).get(childPosition);
    }

    /**
     * 获取一级标签的ID
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 获取二级标签的ID
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 指定位置相应的组视图
     * 是否拥有一个稳定的id 用不到 默认
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * 对一级标签进行设置
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        View view = convertView;
        GroupHolder holder = null;
        if (view == null) {
            holder = new GroupHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.expandlist_group, null);
            holder.groupName = (TextView) view.findViewById(R.id.tv_group_name);
            view.setTag(holder);
        } else {
            holder = (GroupHolder) view.getTag();
        }

        holder.groupName.setText(listFather.get(groupPosition));

        return view;

    }

    /**
     * 对一级标签下的二级标签进行设置
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        View view = convertView;
        ChildHolder holder = null;
        if (view == null) {
            holder = new ChildHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.expandlist_child, null);
            holder.tvChild = (TextView) view.findViewById(R.id.tv_child);
            holder.ivChild = (ImageView) view.findViewById(R.id.iv_child);
            view.setTag(holder);
        } else {
            holder = (ChildHolder) view.getTag();
        }

        holder.tvChild.setText(listChild.get(groupPosition).get(childPosition));
       /* if (isLastChild) {
            holder.ivChild.setBackgroundResource(R.drawable.fuxuan_input01);
        } else {
            holder.ivChild.setBackgroundResource(R.drawable.fuxuan_input02);
        }*/
        return view;

    }

    /**
     * 当选择子节点的时候，调用该方法
     * 孩子是否可点击
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder {
        public TextView groupName;
    }

    class ChildHolder {
        public TextView tvChild;
        public ImageView ivChild;
    }
}
