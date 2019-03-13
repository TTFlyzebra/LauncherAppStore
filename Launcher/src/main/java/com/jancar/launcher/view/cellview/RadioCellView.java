package com.jancar.launcher.view.cellview;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jancar.launcher.R;
import com.jancar.launcher.utils.FlyLog;
import com.jancar.launcher.view.flyview.NumTextView;
import com.jancar.media.JacMediaController;

public class RadioCellView extends SimpeCellView {
    private NumTextView numTextView;
    private ImageView AMFM_ImageView;
    private ImageView KHZMHZ_ImageView;
    private ImageView imageView2;
    private JacMediaController controller;
    private String fmText = "";
    private String fmName = "";
    private String fmKz = "";
    private String showImageUrl = "";

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

        imageView2 = new ImageView(context);
        LayoutParams params4 = new LayoutParams(120, 120);
        params4.leftMargin = 46;
        params4.topMargin = 72;
        addView(imageView2, params4);

        imageView2.setVisibility(View.VISIBLE);
        AMFM_ImageView.setVisibility(View.GONE);
        KHZMHZ_ImageView.setVisibility(View.GONE);
        numTextView.setVisibility(View.GONE);


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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
                        fmText = "";
                        fmName = "";
                        fmKz = "";
                        FlyLog.e(e.toString());
                    }
                    if (TextUtils.isEmpty(fmName)) {
                        fmText = "";
                        fmName = "";
                        fmKz = "";
                    }
                }
                upWidgetView();
            }
        };
        controller.Connect();
        upWidgetView();
    }


    private void upWidgetView() {
        try {
            if (TextUtils.isEmpty(fmName) || TextUtils.isEmpty(fmText) || TextUtils.isEmpty(fmKz)) {
                imageView2.setVisibility(View.VISIBLE);
                AMFM_ImageView.setVisibility(View.GONE);
                KHZMHZ_ImageView.setVisibility(View.GONE);
                numTextView.setVisibility(View.GONE);
            } else {
                imageView2.setVisibility(View.GONE);
                boolean isFM = fmText.startsWith("FM");
                boolean isKHz = fmKz.endsWith("KHz");
                AMFM_ImageView.setImageResource(isFM ? R.drawable.radio_fm : R.drawable.radio_am);
                KHZMHZ_ImageView.setImageResource(isKHz ? R.drawable.radio_khz : R.drawable.radio_mhz);
                if (fmName.length() > 5) {
                    fmName = fmName.substring(0, 5);
                }
                numTextView.setText(fmName);
                AMFM_ImageView.setVisibility(View.VISIBLE);
                KHZMHZ_ImageView.setVisibility(View.VISIBLE);
                numTextView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        controller.release();
        super.onDetachedFromWindow();
    }


    @Override
    public void notifyView() {
        super.notifyView();
        if (imageView2 == null) return;

        Glide.with(getContext())
                .load(appInfo.focusImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView2);
    }

}
