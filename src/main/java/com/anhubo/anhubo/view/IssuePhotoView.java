package com.anhubo.anhubo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.anhubo.anhubo.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;

/**
 * Created by LUOLI on 2016/11/16.
 */
public class IssuePhotoView extends RelativeLayout {

    private Context mContext;
    public ImageView ivIssue1;
    public ImageView ivIssue2;
    public ImageView ivIssue3;

    public IssuePhotoView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        View view = View.inflate(mContext, R.layout.issue_photo_view, null);
        ivIssue1 = (ImageView) view.findViewById(R.id.iv_issue1);
        ivIssue2 = (ImageView) view.findViewById(R.id.iv_issue2);
        ivIssue3 = (ImageView) view.findViewById(R.id.iv_issue3);

    }
    public void setHeaderIcon(final ImageView iv, String imgurl) {
        OkHttpUtils
                .get()//
                .url(imgurl)//
                .tag(this)//
                .build()//
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                        System.out.println("MyFragment获取头像+++===" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        iv.setImageBitmap(bitmap);
                    }
                });
    }
}
