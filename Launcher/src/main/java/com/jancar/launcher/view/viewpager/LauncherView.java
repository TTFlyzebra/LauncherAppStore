package com.jancar.launcher.view.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.jancar.launcher.bean.PageBean;
import com.jancar.launcher.bean.TemplateBean;
import com.jancar.launcher.utils.CommondUtils;
import com.jancar.launcher.utils.FlyLog;
import com.jancar.launcher.view.pageview.SimplePageView;

import java.util.ArrayList;
import java.util.List;

public class LauncherView extends ViewPager implements ILauncher {
    private List<PageBean> pageList = new ArrayList<>();
    private TemplateBean templateBean;
    private MyPgaeAdapter myPgaeAdapter = new MyPgaeAdapter();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable runSetWallTask = new Runnable() {
        @Override
        public void run() {
            FlyLog.d("runSetWallTask ");
            CommondUtils.execStartPackage(
                    getContext(),
                    "com.android.launcher3",
                    "com.android.launcher3.WallpaperPickerActivity"
            );
        }
    };

    public LauncherView(Context context) {
        this(context, null);
    }

    public LauncherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setAdapter(myPgaeAdapter);
    }

    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer) {
        super.setPageTransformer(reverseDrawingOrder, transformer);
    }

    @Override
    public void setData(TemplateBean templateBean) {
        if (templateBean == null || templateBean.pageList == null || templateBean.pageList.isEmpty()) {
            return;
        }
        this.templateBean = templateBean;
        List<PageBean> mPageBeanList = templateBean.pageList;
        pageList.clear();
        if (mPageBeanList.size() > 1) {
            pageList.add(mPageBeanList.get(mPageBeanList.size() - 1));
            pageList.addAll(mPageBeanList);
            pageList.add(mPageBeanList.get(0));
            myPgaeAdapter.notifyDataSetChanged();
            setCurrentItem(1);
        } else {
            pageList.addAll(mPageBeanList);
            myPgaeAdapter.notifyDataSetChanged();
        }

    }

    public class MyPgaeAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pageList == null ? 0 : pageList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SimplePageView simplePageView = new SimplePageView(getContext());
            simplePageView.setTag(position);
            simplePageView.setMirror(templateBean.mirror != 0);
            simplePageView.setData(pageList.get(position));
            container.addView(simplePageView);
            return simplePageView;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (position == 0 && pageList != null && pageList.size() > 1) {
                setCurrentItem(pageList.size() - 2, false);
                for (int i = 0; i < getCount(); i++) {
                    View view = getChildAt(i);
                    view.setTranslationX(0);
                    view.setRotation(0);
                }
            }
            if (position == pageList.size() - 1 && pageList != null && pageList.size() > 1) {
                setCurrentItem(1, false);
                for (int i = 0; i < getCount(); i++) {
                    View view = getChildAt(i);
                    view.setTranslationX(0);
                    view.setRotation(0);
                }
            }
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            FlyLog.d(ev.toString());
        }
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                FlyLog.d("ACTION_DOWN");
                mHandler.removeCallbacks(runSetWallTask);
                mHandler.postDelayed(runSetWallTask,1000);
                break;
            case MotionEvent.ACTION_UP:
                FlyLog.d("ACTION_UP");
                mHandler.removeCallbacks(runSetWallTask);
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }
}
