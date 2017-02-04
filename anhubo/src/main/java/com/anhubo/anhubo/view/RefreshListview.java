package com.anhubo.anhubo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.anhubo.anhubo.R;


import butterknife.InjectView;
/**
 * Created by LUOLI on 2016/10/28.
 */
public class RefreshListview extends ListView {

    @InjectView(R.id.tv_footer)
    TextView tvFooter;



    private OnRefreshingListener mListener;
    private View footer;
    private int footerMeasuredHeight;

    public RefreshListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        addFooter();
    }

    private void addFooter() {
        // 添加加载更多的脚布局
        footer = View.inflate(getContext(), R.layout.footer, null);
        // 隐藏脚布局
        footer.measure(0, 0);
        footerMeasuredHeight = footer.getMeasuredHeight();
        footer.setPadding(0, -footerMeasuredHeight, 0, 0);
<<<<<<< HEAD
        this.addFooterView(footer,null, true);
        this.setFooterDividersEnabled(false);
=======
        this.addFooterView(footer);

>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
        // 监听Listview的条目滚动事件
        this.setOnScrollListener(new MyOnScrollListener());
    }



    // 对外暴露接口
    public interface OnRefreshingListener {
        // 加载更多业务方法
        void onLoadMore();
    }

    // 让外界设置监听
    public void setOnRefreshingListener(OnRefreshingListener listener) {
        this.mListener = listener;
    }




    private boolean isLoadMore = false;// 是否处于加载更多中

    // 恢复加载更多状态的方法
    public void loadMoreFinished() {
        footer.setPadding(0, -footerMeasuredHeight, 0, 0);
        isLoadMore = false;
    }

    class MyOnScrollListener implements OnScrollListener {

        // 当滚动状态发生变化时调用
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 当状态变化停止或惯性停止时
            if (OnScrollListener.SCROLL_STATE_IDLE == scrollState
                    || OnScrollListener.SCROLL_STATE_FLING == scrollState) {
                // 判断当前展示的最后一个条目是否是Adapter中的最后一条
                if (getLastVisiblePosition() == getCount() - 1 && !isLoadMore) {
                    isLoadMore = true;
                    //System.out.println("显示的是最后一条，开始加载更多");
                    footer.setPadding(0, 0, 0, 0);

                    // 让脚布局自动显示出来
                    setSelection(getCount());

                    // 当处于加载更多时，需要调用外界业务类传递进来的监听器的onLoadMore
                    if (mListener != null) {
                        mListener.onLoadMore();
                    }
                }
            }
        }

        // 滚动时调用
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

        }

    }
}
