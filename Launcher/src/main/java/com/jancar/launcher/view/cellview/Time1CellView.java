package com.jancar.launcher.view.cellview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jancar.launcher.R;
import com.jancar.launcher.bean.CellBean;
import com.jancar.launcher.utils.FlyLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.provider.Settings.Global.AUTO_TIME;

public class Time1CellView extends StaticCellView {
    private TextView ampmView, timeView, dateView, weekView;
    private LinearLayout rootLayout;
    private boolean bTime24 = true;
    private String ampm = "";
    private String time = "";
    private String date = "";
    private String week = "";
    private IntentFilter intentFilter;
    private TimeChangeReceiver timeChangeReceiver;
    private int screenWidth;
    private int screenHeigh;
    private float screenScacle = 1.0f;

    public Time1CellView(Context context) {
        super(context);
    }

    public Time1CellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Time1CellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeigh = dm.heightPixels;
        //如果设置的有效区域无效，设置有效区域为全屏
        float wScale = screenWidth / (float)1024;
        float hScale = screenHeigh / (float)600;
        screenScacle = Math.min(wScale, hScale);
        super.initView(context);
        try {
            intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
            intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
            intentFilter.addAction(AUTO_TIME);
            timeChangeReceiver = new TimeChangeReceiver();
            bTime24 = Settings.System.getString(getContext().getContentResolver(), Settings.System.TIME_12_24).equals("24");
            FlyLog.d("first bTime24=" + bTime24);
        } catch (Exception e) {
            FlyLog.d(e.toString());
        }
        rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER);
        addView(rootLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        FrameLayout ll = new FrameLayout(context);
        rootLayout.addView(ll);

        ampmView = new TextView(context);
        ampmView.setGravity(Gravity.CENTER_VERTICAL);
        ll.addView(ampmView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        timeView = new TextView(context);
        timeView.setGravity(Gravity.CENTER);
        ll.addView(timeView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        LinearLayout ll1 = new LinearLayout(context);
        rootLayout.addView(ll1);

        dateView = new TextView(context);
        dateView.setGravity(Gravity.LEFT|Gravity.CENTER);
        ll1.addView(dateView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        weekView = new TextView(context);
        weekView.setGravity(Gravity.RIGHT|Gravity.CENTER);
        ll1.addView(weekView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        upView();
    }

    public void upView() {
        try {
            FlyLog.d("up time bTime24=" + bTime24);
            ampm = getCurrentDate("a");
            time = getCurrentDate(bTime24 ? "HH:mm" : "hh:mm");
            date = getCurrentDate("yyyy-MM-dd");
            week = getCurrentWeek();
            if (bTime24) {
                ampmView.setVisibility(INVISIBLE);
            } else {
                ampmView.setVisibility(VISIBLE);
                ampmView.setText(ampm);
            }
            timeView.setText(time);
            weekView.setText(week);
            dateView.setText(date);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    @Override
    public void setData(CellBean mCellBean) {
        this.cellBean = mCellBean;

        int color = 0xFFFFFFFF;
        try {
            color = Color.parseColor(mCellBean.textColor);
        } catch (Exception e) {
            color = 0xFFFFFFFF;
        }
        FrameLayout.LayoutParams alp = (FrameLayout.LayoutParams) ampmView.getLayoutParams();
        alp.setMargins(10, 20, 0, 0);
        ampmView.setLayoutParams(alp);
        FrameLayout.LayoutParams tlp = (FrameLayout.LayoutParams) timeView.getLayoutParams();
        tlp.setMargins(52, 0, 0, 0);
        timeView.setLayoutParams(tlp);
        LinearLayout.LayoutParams wlp = (LinearLayout.LayoutParams) weekView.getLayoutParams();
        wlp.setMargins(15, (int) (4*screenScacle), 0, 0);
        weekView.setLayoutParams(wlp);
        LinearLayout.LayoutParams dlp = (LinearLayout.LayoutParams) dateView.getLayoutParams();
        dlp.setMargins(15, (int) (4*screenScacle), 0, 0);
        dateView.setLayoutParams(dlp);

        ampmView.setTextColor(color);
        ampmView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCellBean.textSize * 48 / 128*screenScacle);
        timeView.setTextColor(color);
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCellBean.textSize);
        weekView.setTextColor(color);
        weekView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCellBean.textSize * 32 / 64*screenScacle);
        dateView.setTextColor(color);
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCellBean.textSize * 28 / 64*screenScacle);
    }

    private static String getCurrentDate(String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

    private String[] weeks = new String[7];

    private String getCurrentWeek() {
        weeks = new String[]{getContext().getString(R.string.tv_str_sunday),
                getContext().getString(R.string.tv_str_monday),
                getContext().getString(R.string.tv_str_tuesday),
                getContext().getString(R.string.tv_str_wednesday),
                getContext().getString(R.string.tv_str_thursday),
                getContext().getString(R.string.tv_str_friday),
                getContext().getString(R.string.tv_str_saturday)};
        Date date = new Date(System.currentTimeMillis());
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);
        return weeks[mCalendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            getContext().registerReceiver(timeChangeReceiver, intentFilter);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        upView();
        try {
            getContext().unregisterReceiver(timeChangeReceiver);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
        super.onDetachedFromWindow();
    }

    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                case Intent.ACTION_TIME_CHANGED:
                case Intent.ACTION_TIMEZONE_CHANGED:
                    bTime24 = Settings.System.getString(getContext().getContentResolver(), Settings.System.TIME_12_24).equals("24");
                    upView();
                    break;
            }
        }
    }
}
