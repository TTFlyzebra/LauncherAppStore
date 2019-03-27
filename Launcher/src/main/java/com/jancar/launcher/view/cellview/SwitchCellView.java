package com.jancar.launcher.view.cellview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jancar.JancarManager;
import com.jancar.launcher.bean.CellBean;
import com.jancar.launcher.utils.CommondUtils;
import com.jancar.launcher.utils.FlyLog;
import com.jancar.launcher.utils.SystemProperties;
import com.jancar.launcher.view.flyview.FlyImageView;
import com.jancar.launcher.view.flyview.FlyTextView;
import com.jancar.launcher.view.flyview.MirrorView;

import static com.jancar.launcher.utils.SystemProperties.Property.SWITCH_APP_CELL;

public class SwitchCellView extends FrameLayout implements ICellView, View.OnTouchListener, View.OnClickListener {
    protected CellBean mCellBean;
    protected FlyImageView imageView;
    protected MirrorView mirrorView;
    protected TextView textView;
    protected Handler mHandler = new Handler();
    protected JancarManager jancarManager;
    private boolean isSwitch = false;


    public SwitchCellView(Context context) {
        this(context, null);
    }

    public SwitchCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("WrongConstant")
    public SwitchCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            jancarManager = (JancarManager) context.getSystemService("jancar_manager");
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
        initView(context);
        focusChange(false);
    }

    @Override
    public void initView(Context context) {
        imageView = new FlyImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        textView = new FlyTextView(context);
        addView(textView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setOnClickListener(this);
        setOnTouchListener(this);
    }

    @Override
    public void setData(CellBean appInfo) {
        isSwitch = !("no".equals(SystemProperties.get(getContext(), SWITCH_APP_CELL, "no")));
        FlyLog.d("switch = "+isSwitch);
        this.mCellBean = appInfo;
        if (appInfo.width > 0 || appInfo.height > 0) {
            LayoutParams params = (LayoutParams) imageView.getLayoutParams();
            params.width = appInfo.width;
            params.height = appInfo.height;
            imageView.setLayoutParams(params);
        }

        textView.setGravity(Gravity.CENTER);
        try {
            textView.setTextColor(Color.parseColor(appInfo.textColor));
        } catch (Exception e) {
            textView.setTextColor(0xffffffff);
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, appInfo.textSize);
        LayoutParams params2 = (LayoutParams) textView.getLayoutParams();
        params2.gravity = Gravity.BOTTOM;
        params2.leftMargin = appInfo.textLeft;
        params2.topMargin = appInfo.textTop;
        params2.rightMargin = appInfo.textRight;
        params2.bottomMargin = Math.max(0, appInfo.textBottom);
        params2.height = (int) (appInfo.textSize * 2.5f);
        textView.setLayoutParams(params2);
        textView.setGravity(Gravity.CENTER);
        textView.setLines(2);
    }

    @Override
    public void notifyView() {
        if (textView != null && mCellBean != null && mCellBean.textTitle != null) {
            if(isSwitch){
                textView.setText("Chrome");
            }else{
                textView.setText(mCellBean.textTitle.getText());
            }
        }
        if (imageView == null) return;
        String imgurl = mCellBean.defaultImageUrl;
        if (isSwitch) {
            imgurl = mCellBean.focusImageUrl;
        }
        Glide.with(getContext())
                .load(imgurl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(bitmap);
                        if (mirrorView != null) {
                            setDrawingCacheEnabled(true);
                            Bitmap bmp = getDrawingCache();
                            if (bmp == null) {
                                measure(MeasureSpec.makeMeasureSpec(mCellBean.width, MeasureSpec.EXACTLY),
                                        MeasureSpec.makeMeasureSpec(mCellBean.height, MeasureSpec.EXACTLY));
                                layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
                                buildDrawingCache();
                                bmp = getDrawingCache();
                            }
                            mirrorView.showImage(bmp);
                        }
                    }
                });


    }

    /**
     * 启动优先级，包名+类名>Action>包名
     */
    @Override
    public void runAction() {
        if (!TextUtils.isEmpty(mCellBean.jancar) && jancarManager != null) {
            if (!isSwitch) {
                if (jancarManager.requestPage(mCellBean.jancar)) {
                    FlyLog.d("start app by jancarManager id=%s", mCellBean.jancar);
                    return;
                } else {
                    FlyLog.d("start app by jancarManager failed!");
                }
            }
        }
        String packName = mCellBean.packName;
        String className = mCellBean.className;
        String action = mCellBean.action;
        try {
            String packs[] = packName.split("#");
            String classs[] = className.split("#");
            if (!isSwitch) {
                packName = packs[0];
                className = classs[0];
            } else {
                packName = packs[1];
                className = classs[1];
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
        if (CommondUtils.execStartPackage(getContext(), packName, className))
            return;
        if (CommondUtils.execStartActivity(getContext(), action)) return;
        if (!CommondUtils.execStartPackage(getContext(), packName)) {
//            Toast.makeText(getContext(), getContext().getResources().getString(R.string.startAppFailed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setMirrorView(MirrorView mirrorView) {
        this.mirrorView = mirrorView;
    }

    private Runnable show = new Runnable() {
        @Override
        public void run() {
            focusChange(false);
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                focusChange(true);
                break;
            case MotionEvent.ACTION_MOVE:
                focusChange(isTouchPointInView(v, (int) event.getRawX(), (int) event.getRawY()));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                focusChange(false);
                break;
        }
        return false;
    }

    private void focusChange(boolean flag) {
        if (flag) {
            mHandler.removeCallbacks(show);
            mHandler.postDelayed(show, 300);
//            imageView.setAlpha(clickAlpha);
            imageView.setColorFilter(0x3FFFFFFF);
        } else {
//            imageView.setAlpha(normalAlphe);
            imageView.clearColorFilter();
        }
    }

    @Override
    public void onClick(View v) {
        runAction();
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    private boolean isTouchPointInView(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }
}
