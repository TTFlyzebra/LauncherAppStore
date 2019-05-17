package com.jancar.launcher.view.cellview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jancar.JancarManager;
import com.jancar.launcher.R;
import com.jancar.launcher.bean.CellBean;
import com.jancar.launcher.utils.FlyLog;
import com.jancar.launcher.view.flyview.FlyImageView;
import com.jancar.launcher.view.flyview.MirrorView;
import com.jancar.media.JacMediaController;
import com.jancar.source.Page;

import java.util.Locale;

public class MediaInfoCellView extends FrameLayout implements ICellView, View.OnClickListener {
    private CellBean cellBean;
    private FlyImageView imageView;
    private MirrorView mirrorView;
    private JacMediaController controller;
    private String mSession = "music";
    private String mTitle = "";
    private long mCurrent;
    private long mDuration;
    private Bitmap mBitmap;
    private int playstate = 0;
    private String mLastSession = Page.PAGE_FM;

    private String fmText = "FM1";
    private String fmName = "87.5";
    private String fmKz = "MHz";

    private View mViewMusic, mViewFm;
    private TextView fmTv01, fmTv02, fmTv03;
    private ImageView fmNext, fmPrev, musicPrev, musicNext, musicPlay;
    private TextView musicTv01, musicTvStart, musicTvEnd;
    private ProgressBar mProgressBar;
    private ImageView musicId3img, fmimg;

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
        musicPlay = (ImageView) view.findViewById(R.id.wg_music_play);
        musicTv01 = (TextView) view.findViewById(R.id.mediainfo_music_title);
        musicTvStart = (TextView) view.findViewById(R.id.mediainfo_music_starttime);
        musicTvEnd = (TextView) view.findViewById(R.id.mediainfo_music_endtime);
        mProgressBar = (ProgressBar) view.findViewById(R.id.mediainfo_music_progressbar);
        musicId3img = (ImageView) view.findViewById(R.id.mediainfo_id3img);
        fmimg = (ImageView) view.findViewById(R.id.mediainfo_fmimg);

        fmPrev.setOnClickListener(this);
        fmNext.setOnClickListener(this);
        musicPrev.setOnClickListener(this);
        musicNext.setOnClickListener(this);
        musicPlay.setOnClickListener(this);

        fmTv02.setOnClickListener(this);
        musicTv01.setOnClickListener(this);
        musicId3img.setOnClickListener(this);
        fmimg.setOnClickListener(this);
    }

    @Override
    public void setData(CellBean appInfo) {
        this.cellBean = appInfo;
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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        controller = new JacMediaController(getContext().getApplicationContext()) {
            @Override
            public void onSession(String page) {
                FlyLog.d("onSession page=%s", page);
                if (!TextUtils.isEmpty(page)) {
                    mSession = page;
                    if (isJancarSession()) {
                        mLastSession = page;
                        upWidgetView();
                    }else{
                        mBitmap = null;
                    }
                }
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
                FlyLog.d("onProgress current=%d,duration=%d", current, duration);
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
        controller.release();
        super.onDetachedFromWindow();
    }

    private void upWidgetView() {
        FlyLog.d("last session=%s",mLastSession);
        switch (mLastSession) {
            case Page.PAGE_FM:
                mViewMusic.setVisibility(View.GONE);
                mViewFm.setVisibility(View.VISIBLE);
                fmTv01.setText(fmText);
                fmTv02.setText(fmName);
                fmTv03.setText(fmKz);
                FlyLog.d("upWidgetView fm");
                break;
            case Page.PAGE_A2DP:
            case Page.PAGE_MUSIC:
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
                musicPlay.setImageResource(playstate == 1 ? R.drawable.media_pause : R.drawable.media_play);
                //更新图片
                if (mBitmap == null) {
                    musicId3img.setImageResource(Page.PAGE_MUSIC.equals(mLastSession) ?
                            R.drawable.mediainfo_music_default : R.drawable.mediainfo_bt_default);
                } else {
                    musicId3img.setImageBitmap(mBitmap);
                }
                FlyLog.d("upWidgetView music");
                break;
        }
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wg_fm_next:
            case R.id.wg_music_next:
                if (!isJancarSession()) {
                    startAppByLastSession();
                } else {
                    playNext();
                }
                break;
            case R.id.wg_fm_prev:
            case R.id.wg_music_prev:
                if (!isJancarSession()) {
                    startAppByLastSession();
                } else {
                    playPrev();
                }
                break;
            case R.id.mediainfo_fmimg:
            case R.id.mediainfo_id3img:
            case R.id.mediainfo_music_title:
            case R.id.wg_fm_tv02:
                startAppByLastSession();
                break;
            case R.id.wg_music_play:
                if (!isJancarSession()) {
                    startAppByLastSession();
                } else {
                    playPasue();
                }
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

    private boolean isJancarSession() {
        return Page.PAGE_FM.equals(mSession) || Page.PAGE_MUSIC.equals(mSession) || Page.PAGE_A2DP.equals(mSession);
    }

    @SuppressLint("WrongConstant")
    private void startAppByLastSession() {
        try {
            FlyLog.d("start app mSession=" + mLastSession);
            ((JancarManager) getContext().getSystemService(JancarManager.JAC_SERVICE)).requestPage(mLastSession);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    private void playNext() {
        try {
            controller.requestNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playPrev() {
        try {
            controller.requestPrev();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playPasue() {
        try {
            controller.requestPPause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
