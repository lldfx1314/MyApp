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
public class FooterListview extends ListView {

    @InjectView(R.id.tv_footer)
    TextView tvFooter;


    private View footer;
    private int footerMeasuredHeight;

    public FooterListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        addFooter();
    }

    private void addFooter() {
        // 添加加载更多的脚布局
        footer = View.inflate(getContext(), R.layout.footer_emoployee, null);
        // 隐藏脚布局
        footer.measure(0, 0);
        footerMeasuredHeight = footer.getMeasuredHeight();
        footer.setPadding(0, -footerMeasuredHeight, 0, 0);
        this.addFooterView(footer, null, true);
        this.setFooterDividersEnabled(false);
        // 监听条目滑动事件
        setOnScrollListener(new MyOnScrollListener());
    }

    class MyOnScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //　当状态变化停止或惯性停止时
            if (scrollState == OnScrollListener.SCROLL_STATE_FLING || scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                // 判断当前展示的最后一个条目是否是Adapter中的最后一条
                if(getCount()-1 == getLastVisiblePosition()){
                    footer.setPadding(0, 0, 0, 0);

                    // 让脚布局自动显示出来
                    setSelection(getCount());
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }

}
