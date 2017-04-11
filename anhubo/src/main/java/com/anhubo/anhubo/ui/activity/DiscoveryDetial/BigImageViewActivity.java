package com.anhubo.anhubo.ui.activity.DiscoveryDetial;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.anhubo.anhubo.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by LUOLI on 2017/3/29.
 */
public class BigImageViewActivity extends AppCompatActivity {

    private static final String TAG = "BigImageViewActivity";
    @InjectView(R.id.iv_bigImageView)
    PhotoView ivBigImageView;
    private String imageUrl;
    private PhotoViewAttacher attacher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_imageview);
        ButterKnife.inject(this);
        getIntentData();
    }

    private void getIntentData() {
        imageUrl = getIntent().getStringExtra("images");
//
        setMap(imageUrl, ivBigImageView);

    }

    class MyOnPhotoTapListener implements PhotoViewAttacher.OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            finish();
            overridePendingTransition(0, R.anim.big_image_quit);
        }

        @Override
        public void onOutsidePhotoTap() {
            finish();
            overridePendingTransition(0, R.anim.big_image_quit);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.big_image_quit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        attacher.cleanup();
    }

    /**
     * 设置图片显示
     */
    private void setMap(final String imgurl, PhotoView imageView) {

        Glide.with(this)//activty
                .load(imgurl)
                .asBitmap()
                .placeholder(R.drawable.buffer_icon)
                .thumbnail(0.1f)
                .animate(animationObject)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        //设置显示图片的控件充满父窗体
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivBigImageView.getLayoutParams();
                        layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                        layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                        ivBigImageView.setLayoutParams(layoutParams);
                        // 监听完在初始化PhotoViewAttacher，以解决先加载占位图造成缩放时图片宽高突然变得很大的现象
                        attacher = new PhotoViewAttacher(ivBigImageView);
                        attacher.setOnPhotoTapListener(new MyOnPhotoTapListener());
                        return false;
                    }
                })
                .into(imageView);
    }

    ViewPropertyAnimation.Animator animationObject = new ViewPropertyAnimation.Animator() {
        @Override
        public void animate(View view) {
            // if it's a custom view class, cast it here
            // then find subviews and do the animations
            // here, we just use the entire view for the fade animation
            view.setAlpha(0f);

            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            fadeAnim.setDuration(1500);
            fadeAnim.start();
        }
    };

}
