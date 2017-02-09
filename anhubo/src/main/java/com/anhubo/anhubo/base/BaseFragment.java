package com.anhubo.anhubo.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.interfaces.UiInterface;
import com.anhubo.anhubo.view.LoadProgressDialog;


/**
 * Created by Administrator on 2016/10/8.
 */
public abstract class BaseFragment extends Fragment  implements View.OnClickListener, UiInterface {


    private View rootView;
    protected Context mActivity;
    private View view;
    protected ImageButton iv_basepager_left;
    protected ImageView ivTopBarleftUnitMenu;
    protected ImageView ivTopBarleftBuildPen;
    protected ImageView ivTopBarRightUnitMsg;
    protected TextView tv_basepager_title;
    protected RelativeLayout llTop;
    protected FrameLayout fl_basepager_content;
    protected TextView tvTopBarRight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        if(view == null) {
            view = View.inflate(mActivity, R.layout.basepager, null);
            FrameLayout mainContentView = (FrameLayout) view.findViewById(R.id.fl_basepager_content);
            rootView = changeToView(getContentView());
            initTitleView();
            initTitleBar();
            initProgressBar();//初始化加载进度条
            initView();
            initListener();
            //所有Activity中共同的点击处理
            registerCommonClick();
            mainContentView.addView(rootView);
            initData();
        }else{
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }


        return view;
    }

    private void initProgressBar() {
    }

    /**加载Title布局*/
    private void initTitleView() {
        // 找到控件
        iv_basepager_left = (ImageButton) view.findViewById(R.id.ivTopBarLeft);//左上角返回按钮
        ivTopBarleftUnitMenu = (ImageView) view.findViewById(R.id.ivTopBarleft_unit_menu);//左上角菜单按钮
        ivTopBarRightUnitMsg = (ImageView) view.findViewById(R.id.ivTopBarRight_unit_msg);//右上角信息按钮
        tvTopBarRight = (TextView) view.findViewById(R.id.tvTopBarRight);//右上角列表
        ivTopBarleftBuildPen = (ImageView) view.findViewById(R.id.ivTopBarleft_build_pen);//左上角铅笔按钮
        tv_basepager_title = (TextView) view.findViewById(R.id.tvAddress);//标题
        llTop = (RelativeLayout) view.findViewById(R.id.ll_Top); // 顶部标题栏
        fl_basepager_content = (FrameLayout) view.findViewById(R.id.fl_basepager_content);//正文提示
        // 设置监听事件
        iv_basepager_left.setOnClickListener(this);
        ivTopBarleftUnitMenu.setOnClickListener(this);
        ivTopBarRightUnitMsg.setOnClickListener(this);
        ivTopBarleftBuildPen.setOnClickListener(this);


    }

    private void registerCommonClick() {
        View view = findView(R.id.back);
        if (view != null) {
            view.setOnClickListener(this);
        }
    }

    /**
     * 处理返回的是布局的id还是view
     */
    private View changeToView(Object viewOrId) {
        View contentView;
        if (viewOrId instanceof Integer) {
            int layoutId = (int) viewOrId;
            contentView = View.inflate(getActivity(), layoutId, null);
        } else {
            contentView = (View) viewOrId;
        }
        return contentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                //退栈
                getFragmentManager().popBackStack();
                break;
            default:
                processClick(view);
                break;
        }
    }


    /**
     * 查找View，省去强转操作
     */
    protected <T> T findView(int id) {
        @SuppressWarnings("unchecked")
        T view = (T) rootView.findViewById(id);
        return view;
    }

}
