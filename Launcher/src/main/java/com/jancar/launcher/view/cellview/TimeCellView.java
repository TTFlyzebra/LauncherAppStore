package com.jancar.launcher.view.cellview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jancar.launcher.R;
import com.jancar.launcher.utils.FlyLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimeCellView extends StaticCellView {
    private TextView timeView, dateView, weekView;

    public TimeCellView(Context context) {
        super(context);
    }

    public TimeCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        addView(textLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        timeView = new TextView(context);
        timeView.setTextColor(0xFFFFFFFF);
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 64);
        timeView.setGravity(Gravity.CENTER);
        timeView.setPadding(0, 20, 0, 0);
        textLayout.addView(timeView, LayoutParams.MATCH_PARENT, 120);
        weekView = new TextView(context);
        weekView.setTextColor(0xFFFFFFFF);
        weekView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
        weekView.setGravity(Gravity.CENTER);
        textLayout.addView(weekView, LayoutParams.MATCH_PARENT, 50);
        dateView = new TextView(context);
        dateView.setTextColor(0xFFFFFFFF);
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
        dateView.setGravity(Gravity.CENTER);
        textLayout.addView(dateView, LayoutParams.MATCH_PARENT, 50);
        upView();
    }

    public void upView() {
        try {
            FlyLog.d("up time");
            timeView.setText(time);
            weekView.setText(week);
            dateView.setText(date);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
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

    private Timer mTimer;
    private String time = "";
    private String date = "";
    private String week = "";

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String tmpTime = getCurrentDate("HH:mm");
                    String tmpDate = getCurrentDate("yyyy-MM-dd");
                    String tmpWeek = getCurrentWeek();
                    if (!(tmpTime.equals(time) && tmpDate.equals(date) && tmpWeek.equals(week))) {
                        time = tmpTime;
                        date = tmpDate;
                        week = tmpWeek;
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                upView();
                            }
                        });
                    }
                }
            }, 0, 1000);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onDetachedFromWindow();
    }
}
