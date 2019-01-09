package com.jancar.launcher.view.cellview;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jancar.launcher.R;
import com.jancar.launcher.utils.FlyLog;
import com.jancar.launcher.utils.SPUtil;
import com.jancar.launcher.view.flyview.NumTextView;
import com.jancar.media.JacMediaController;

public class RadioCellView extends SimpeCellView {
    private NumTextView numTextView;
    private ImageView AMFM_ImageView;
    private ImageView KHZMHZ_ImageView;
    private JacMediaController controller;
    private String fmText = "FM1";
    private String fmName = "87.5";
    private String fmKz = "MHz";
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public RadioCellView(Context context) {
        super(context);
    }

    public RadioCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        FlyLog.d("RadioManager init()");
        AMFM_ImageView = new ImageView(context);
        LayoutParams params1 = new LayoutParams(-2, -2);
        params1.leftMargin = 10;
        params1.topMargin = 64;
        AMFM_ImageView.setImageResource(R.drawable.radio_am);
        addView(AMFM_ImageView, params1);

        numTextView = new NumTextView(context);
        LayoutParams params3 = new LayoutParams(-1, -1);
        params3.leftMargin = 0;
        params3.topMargin = 114;
        addView(numTextView, params3);

        KHZMHZ_ImageView = new ImageView(context);
        LayoutParams params2 = new LayoutParams(-2, -2);
        params2.leftMargin = 132;
        params2.topMargin = 176;
        KHZMHZ_ImageView.setImageResource(R.drawable.radio_khz);
        addView(KHZMHZ_ImageView, params2);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        String temStr = (String) SPUtil.get(getContext(), "FM_CHANNEL", "");
        String strs[] = temStr.split("##");
        if (strs.length == 3) {
            fmText = strs[0];
            fmKz = strs[1];
            fmName = strs[2];
        }
        controller = new JacMediaController(getContext().getApplicationContext()) {
            @Override
            public void onSession(String page) {
            }

            @Override
            public void onPlayUri(String uri) {
            }

            @Override
            public void onPlayId(int currentId, int total) {
            }

            @Override
            public void onPlayState(int state) {
            }

            @Override
            public void onProgress(long current, long duration) {
            }

            @Override
            public void onRepeat(int repeat) {
            }

            @Override
            public void onFavor(boolean bFavor) {
            }

            @Override
            public void onID3(String title, String artist, String album, byte[] artWork) {
            }

            @Override
            public void onMediaEvent(String action, Bundle extras) {
                FlyLog.d("onMediaEvent action=%s,extras=" + extras, action);
                if (extras != null) {
                    try {
                        int fmType = extras.getInt("Band");
                        fmText = fmType == 0 ? "FM1" : fmType == 1 ? "FM2" : fmType == 2 ? "FM3" : fmType == 3 ? "AM1" : "AM2";
                        fmKz = fmType < 3 ? "MHz" : "KHz";
                        fmName = extras.getString("name");
                    } catch (Exception e) {
                        fmText = "FM1";
                        fmName = "87.5";
                        fmKz = "MHz";
                        FlyLog.e(e.toString());
                    }
                    if (TextUtils.isEmpty(fmName)) {
                        fmText = "FM1";
                        fmName = "87.5";
                        fmKz = "MHz";
                    }
                }
                upWidgetView();
                mHandler.removeCallbacks(saveFMtask);
                mHandler.postDelayed(saveFMtask, 2000);
            }
        };
        controller.Connect();
        upWidgetView();
    }

    private void upWidgetView() {
        try {
            boolean isFM = fmText.startsWith("FM");
            boolean isKHz = fmKz.endsWith("KHz");
            AMFM_ImageView.setImageResource(isFM ? R.drawable.radio_fm : R.drawable.radio_am);
            KHZMHZ_ImageView.setImageResource(isKHz ? R.drawable.radio_khz : R.drawable.radio_mhz);
            numTextView.setText(fmName);
        }catch (Exception e){
            FlyLog.e(e.toString());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        SPUtil.set(getContext(), "FM_CHANNEL", fmText + "##" + fmKz + "##" + fmName);
        controller.release();
        super.onDetachedFromWindow();
    }

    private Runnable saveFMtask = new Runnable() {
        @Override
        public void run() {
            SPUtil.set(getContext(), "FM_CHANNEL", fmText + "##" + fmKz + "##" + fmName);
        }
    };

    @Override
    public void notifyView() {
        super.notifyView();
    }

}
