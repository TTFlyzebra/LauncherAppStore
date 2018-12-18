package com.jancar.launcher.view.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.jancar.launcher.bean.PageBean;
import com.jancar.launcher.view.pageview.SimplePageView;

import java.util.ArrayList;
import java.util.List;

public class LauncherView extends ViewPager implements ILauncher {
    private List<PageBean> pageList = new ArrayList<>();
    private MyPgaeAdapter myPgaeAdapter = new MyPgaeAdapter();

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
    public void setData(List<PageBean> mPageBeanList) {
        if (mPageBeanList.size() > 1) {
            pageList.add(mPageBeanList.get(mPageBeanList.size() - 1));
            pageList.addAll(mPageBeanList);
            pageList.add(mPageBeanList.get(0));
        } else {
            pageList.addAll(mPageBeanList);
        }

//        Gson gson2=new Gson();
//        String str=gson2.toJson(pageList);
//        FlyLog.e("pageList="+str);
//        File file = new File("/sdcard/data.json");
//        if(!file.exists()){
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            RandomAccessFile raf = new RandomAccessFile(file,"rwd");
//            raf.write(str.getBytes());
//            raf.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        setCurrentItem(1);
        myPgaeAdapter.notifyDataSetChanged();
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
            simplePageView.setData(pageList.get(position));
            container.addView(simplePageView);
            return simplePageView;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (position == 0 && pageList != null && pageList.size() > 1) {
                setCurrentItem(pageList.size() - 2, false);
            }
            if (position == pageList.size() - 1 && pageList != null && pageList.size() > 1) {
                setCurrentItem(1, false);
            }
        }

    }


}
