package com.jancar.launcher.view.pageview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jancar.launcher.bean.CellBean;
import com.jancar.launcher.bean.PageBean;
import com.jancar.launcher.utils.FlyLog;
import com.jancar.launcher.view.cellview.CellViewFactory;
import com.jancar.launcher.view.cellview.ICellView;
import com.jancar.launcher.view.flyview.MirrorView;

import java.util.List;

public class SimplePageView extends FrameLayout implements IPage {
    private PageBean pageBean;
    private int width;
    private int height;
    private boolean isMirror = false;

    public SimplePageView(Context context) {
        this(context, null);
    }

    public SimplePageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimplePageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        FlyLog.d("width=%d,height=%d", width, height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public void setData(PageBean pageBean) {
        this.pageBean = pageBean;
        List<CellBean> appInfoList = pageBean.cells;
        if (appInfoList == null || appInfoList.isEmpty()) return;
        addAllItemView(appInfoList);
    }

    private void addAllItemView(List<CellBean> appInfoList) {
        if (appInfoList == null || appInfoList.isEmpty()) return;
        int sx = 0;
        int sy = 0;
        boolean autoSize = (pageBean.itemWidth != 0 && pageBean.itemHeight != 0);
        if (autoSize) {
            if (width != 0) {
                sx = (width - (pageBean.itemWidth + pageBean.itemPadding * 2) * pageBean.columns) / 2;
            }
            if (height != 0) {
                sy = (height - (pageBean.itemHeight + pageBean.itemPadding * 2) * pageBean.rows) / 2;
            }
        }
        FlyLog.d("sx=%d,sy=%d", sx, sy);
        for (int i = 0; i < appInfoList.size(); i++) {
            //多出的Cell不进行绘制
            if (autoSize) {
                if (i > pageBean.columns * pageBean.rows) break;
            }
            CellBean appInfo = appInfoList.get(i);
            ICellView iCellView = CellViewFactory.createView(getContext(), appInfo);
            LayoutParams lp;

            if (autoSize) {
                lp = new LayoutParams(pageBean.itemWidth, pageBean.itemHeight);
                lp.setMarginStart(sx + pageBean.x + (i % pageBean.columns) * (pageBean.itemWidth + pageBean.itemPadding * 2) + pageBean.itemPadding);
                lp.topMargin = sy + pageBean.y + (i / pageBean.columns) * (pageBean.itemHeight + pageBean.itemPadding * 2) + pageBean.itemPadding;

            } else {
                if (appInfo.textBottom <= 0) {
                    lp = new LayoutParams(appInfo.width, appInfo.height - appInfo.textBottom);
                } else {
                    lp = new LayoutParams(appInfo.width, appInfo.height);
                }
                lp.setMarginStart(appInfo.x);
                lp.topMargin = appInfo.y;
            }
            addView((View) iCellView, lp);

            //添加镜像
            if (isMirror) {
                LayoutParams lpMirror;
                if (autoSize) {
                    lpMirror = new LayoutParams(pageBean.itemWidth, MirrorView.MIRRORHIGHT);
                    lpMirror.setMarginStart(sx + pageBean.x + (i % pageBean.columns) * (pageBean.itemWidth + pageBean.itemPadding * 2) + pageBean.itemPadding);
                    lpMirror.topMargin = lp.topMargin + pageBean.itemHeight;
                } else {
                    lpMirror = new LayoutParams(appInfo.width, MirrorView.MIRRORHIGHT);
                    lpMirror.setMarginStart(appInfo.x);
                    lpMirror.topMargin = lp.topMargin + appInfo.height;
                }
                MirrorView mirrorView = new MirrorView(getContext());
                mirrorView.setScaleType(ImageView.ScaleType.FIT_XY);
                iCellView.setMirrorView(mirrorView);
                mirrorView.setRefHeight(MirrorView.MIRRORHIGHT);
                addView(mirrorView, lpMirror);
            }

            iCellView.notifyView();
        }
    }

    @Override
    public void setMirror(boolean flag) {
        isMirror = flag;
    }
}
