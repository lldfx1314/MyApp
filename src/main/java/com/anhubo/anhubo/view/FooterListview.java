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
    }

}
