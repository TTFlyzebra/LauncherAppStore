package com.jancar.launcher.view.cellview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jancar.launcher.R;
import com.jancar.launcher.bean.CellBean;
import com.jancar.launcher.utils.FlyLog;
import com.jancar.launcher.view.flyview.FlyImageView;
import com.jancar.launcher.view.flyview.MirrorView;
import com.jancar.media.JacMediaController;

import java.util.Locale;

public class MediaInfoCellView extends FrameLayout implements ICellView, View.OnClickListener {
    private CellBean appInfo;
    private FlyImageView imageView;
    private MirrorView mirrorView;
    private JacMediaController controller;
    private static final int SHOW_MUSIC = 0;
    private static final int SHOW_FM = 1;
    private int mSession = SHOW_MUSIC;
    private String mTitle = "";
    private long mCurrent;
    private long mDuration;
    private Bitmap mBitmap;
    private int playstate = 0;

    private String fmText = "FM1";
    private String fmName = "87.5";
    private String fmKz = "MHz";

    private View mViewMusic, mViewFm;
    private TextView fmTv01, fmTv02, fmTv03;
    private ImageView fmNext, fmPrev, musicPrev, musicNext;
    private TextView musicTv01, musicTvStart, musicTvEnd;
    private ProgressBar mProgressBar;
    private ImageView musicId3img;

    public MediaInfoCellView(Context context) {
        this(context, null);
    }

    public MediaInfoCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaInfoCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    public void initView(Context context) {
        imageView = new FlyImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        View view = inflate(context, R.layout.wg_media_widget, this);
        mViewMusic = view.findViewById(R.id.wg_music);
        mViewFm = view.findViewById(R.id.wg_radio);
        fmTv01 = (TextView) view.findViewById(R.id.wg_fm_tv01);
        fmTv02 = (TextView) view.findViewById(R.id.wg_fm_tv02);
        fmTv03 = (TextView) view.findViewById(R.id.wg_fm_tv03);
        fmPrev = (ImageView) view.findViewById(R.id.wg_fm_prev);
        fmNext = (ImageView) view.findViewById(R.id.wg_fm_next);
        musicPrev = (ImageView) view.findViewById(R.id.wg_music_prev);
        musicNext = (ImageView) view.findViewById(R.id.wg_music_next);
        fmPrev.setOnClickListener(this);
        fmNext.setOnClickListener(this);
        musicPrev.setOnClickListener(this);
        musicNext.setOnClickListener(this);
        musicTv01 = (TextView) view.findViewById(R.id.mediainfo_music_title);
        musicTvStart = (TextView) view.findViewById(R.id.mediainfo_music_starttime);
        musicTvEnd = (TextView) view.findViewById(R.id.mediainfo_music_endtime);
        mProgressBar = (ProgressBar) view.findViewById(R.id.mediainfo_music_progressbar);
        musicId3img = (ImageView) view.findViewById(R.id.mediainfo_id3img);
    }

    @Override
    public void setData(CellBean appInfo) {
        this.appInfo = appInfo;
    }

    @Override
    public void notifyView() {
        if (imageView == null) return;
        Glide.with(getContext()).load(appInfo.defaultImageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        controller = new JacMediaController(getContext().getApplicationContext()) {
            @Override
            public void onSession(String page) {
                FlyLog.d("onSession page=%s", page);
                switch (page) {
                    case "music":
                    case "a2dp":
                        mSession = SHOW_MUSIC;
                        break;
                    case "fm":
                        mSession = SHOW_FM;
                        break;
                    default:
                        mSession = SHOW_MUSIC;
                        break;

                }
                upWidgetView();
            }

            @Override
            public void onPlayUri(String uri) {
                FlyLog.d("onPlayUri=%s", uri);
            }

            @Override
            public void onPlayId(int currentId, int total) {
                FlyLog.d("onPlayId currentId=%d,total=%d", currentId, total);
            }

            @Override
            public void onPlayState(int state) {
                FlyLog.d("onPlayState=%s", state);
                if (playstate != state) {
                    playstate = state;
                    upWidgetView();
                }
            }

            @Override
            public void onProgress(long current, long duration) {
//                FlyLog.d("onProgress current=%d,duration=%d", current, duration);
                mSession = SHOW_MUSIC;
                mCurrent = current;
                mDuration = duration;
                upWidgetView();
            }

            @Override
            public void onRepeat(int repeat) {
                FlyLog.d("onRepeat repeat=%d", repeat);
            }

            @Override
            public void onFavor(boolean bFavor) {
                FlyLog.d("onFavor bFavor=" + bFavor);
            }

            @Override
            public void onID3(String title, String artist, String album, byte[] artWork) {
                FlyLog.d("onID3 title=%s,artist=%s,album=%s", title, artist, album);
                mSession = SHOW_MUSIC;
                mTitle = title;
                if (artWork == null) {
                    mBitmap = null;
                } else {
                    mBitmap = BitmapFactory.decodeByteArray(artWork, 0, artWork.length);
                }
                upWidgetView();
            }

            @Override
            public void onMediaEvent(String action, Bundle extras) {
                FlyLog.d("onMediaEvent action=%s,extras=" + extras, action);
                mSession = SHOW_FM;
                try {
                    int fmType = extras.getInt("Band");
                    fmText = fmType == 0 ? "FM1" : fmType == 1 ? "FM2" : fmType == 2 ? "FM3" : fmType == 3 ? "AM1" : "AM2";
                    fmKz = fmType < 3 ? "MHz" : "KHz";
                    fmName = extras.getString("name");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                upWidgetView();
            }
        };
        controller.Connect();
    }

    @Override
    protected void onDetachedFromWindow() {
        controller.DisConnect();
        super.onDetachedFromWindow();
    }

    private void upWidgetView() {
        switch (mSession) {
            case SHOW_FM:
                mViewMusic.setVisibility(View.GONE);
                mViewFm.setVisibility(View.VISIBLE);
                fmTv01.setText(fmText);
                fmTv02.setText(fmName);
                fmTv03.setText(fmKz);
                FlyLog.d("upWidgetView fm");
                break;
            case SHOW_MUSIC:
                mViewMusic.setVisibility(View.VISIBLE);
                mViewFm.setVisibility(View.GONE);
                //更新标题
                musicTv01.setText(mTitle);
                //更新时间
                String str1 = generateTime(mCurrent);
                String str2 = generateTime(mDuration);
                mProgressBar.setMax((int) (mDuration / 1000));
                mProgressBar.setProgress((int) (mCurrent / 1000));
                musicTvStart.setText(str1);
                musicTvEnd.setText(str2);

                //更新图片
                if (mBitmap == null) {
                    musicId3img.setImageResource(R.drawable.mediainfo_fm);
                } else {
                    musicId3img.setImageBitmap(mBitmap);
                }
                FlyLog.d("upWidgetView music");
                break;
        }

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.wg_fm_next:
            case R.id.wg_music_next:
                controller.requestNext();
                break;
            case R.id.wg_fm_prev:
                controller.requestPrev();
            case R.id.wg_music_prev:
                break;
        }
    }

    private String generateTime(long time) {
        time = Math.min(Math.max(time, 0), 359999000);
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds)
                : String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
    }
}
