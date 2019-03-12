package com.jancar.launcher.view.viewpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.jancar.launcher.R;


/**
 * ViewPager轮播用的导航条用来指示ViewPager当前显示的页面ViewPager
 * Created by FlyZebra on 2016/3/1.
 */
public class NavForViewPager extends View {
    //    private final String TAG = "com.flyzebra";
    private Paint paint;
    private int width;
    private int height;
    //总页数
    private int sumItem = 0;
    //当前页
    private int currentItem = 5;

    private int circleWidth = 16;
    private Bitmap nav_on;
    private Bitmap nav_off;

    public NavForViewPager(Context context) {
        this(context, null);
    }

    public NavForViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavForViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        nav_off = BitmapFactory.decodeResource(getResources(), R.drawable.nav_off);
        nav_on = BitmapFactory.decodeResource(getResources(), R.drawable.nav_on);
    }

    public void setViewPager(final ViewPager viewPager) {
        setCurrentItem(viewPager.getCurrentItem());
        setSumItem(viewPager.getAdapter().getCount());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int item = viewPager.getCurrentItem();
                if (sumItem > 1) {
                    if (item == sumItem - 1) {
                        item = sumItem - 2;
                    } else if (item == 0) {
                        item = 1;
                    }
                }
                setCurrentItem(item);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > 0 && height > 0) {
            this.width = width;
            this.height = height;
        }
    }

    public void setSumItem(int sumItem) {
        if (sumItem > 1) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
        this.sumItem = sumItem;
        postInvalidate();
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sumItem > 0) {
            float x = width / 2 - (sumItem * circleWidth * 2 - circleWidth) / 2;
            for (int i = 0; i < sumItem; i++) {
                if (i == currentItem) {
                    if (nav_on == null) {
                        nav_on = BitmapFactory.decodeResource(getResources(), R.drawable.nav_on);
                    }
                    if (sumItem > 1 && i > 0 && i < sumItem - 1) {
                        canvas.drawBitmap(nav_on, x + i * circleWidth * 2, circleWidth, paint);
                    }
                } else {
                    if (nav_off == null) {
                        nav_off = BitmapFactory.decodeResource(getResources(), R.drawable.nav_off);
                    }
                    if (sumItem > 1 && i > 0 && i < sumItem - 1) {
                        canvas.drawBitmap(nav_off, x + i * circleWidth * 2, circleWidth, paint);
                    }
                }
            }
        }

    }

    private void initPaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(0xFFFFFFFF);
            paint.setStyle(Paint.Style.FILL);
        }
    }
}
