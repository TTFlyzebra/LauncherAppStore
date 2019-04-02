package com.jancar.launcher.view.cellview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jancar.launcher.bean.CellBean;
import com.jancar.launcher.view.flyview.FlyImageView;
import com.jancar.launcher.view.flyview.MirrorView;

public class StaticCellView extends FrameLayout implements ICellView{
    protected CellBean cellBean;
    private FlyImageView imageView;
    private MirrorView mirrorView;

    public StaticCellView(Context context) {
        this(context, null);
    }

    public StaticCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StaticCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    public void initView(Context context) {
        imageView = new FlyImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public void setData(CellBean cellBean) {
        this.cellBean = cellBean;
    }

    @Override
    public void notifyView() {
        if (imageView == null) return;
        Glide.with(getContext())
                .load(cellBean.defaultImageUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(final Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                imageView.setImageBitmap(bitmap);
                if (mirrorView != null) {
                    setDrawingCacheEnabled(true);
                    Bitmap bmp = getDrawingCache();
                    mirrorView.showImage(bmp);
                }
            }
        });
    }

    @Override
    public void runAction() {
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
    }

    @Override
    public void setMirrorView(MirrorView mirrorView) {
        this.mirrorView = mirrorView;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
