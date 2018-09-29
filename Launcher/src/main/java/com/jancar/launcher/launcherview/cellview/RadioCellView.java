package com.jancar.launcher.launcherview.cellview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jancar.BaseManager;
import com.jancar.launcher.R;
import com.jancar.launcher.launcherview.flyview.NumTextView;
import com.jancar.launcher.utils.FlyLog;
import com.jancar.radio.RadioManager;

public class RadioCellView extends SimpeCellView implements
        BaseManager.ConnectListener,
        RadioManager.RadioListener {
    private NumTextView numTextView;
    private ImageView AMFM_ImageView;
    private ImageView KHZMHZ_ImageView;

    private RadioManager radioManager;

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
        radioManager = new RadioManager(context, this, this, "com.jancar.media");
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
    public void notifyView() {
        super.notifyView();
        AMFM_ImageView.setImageResource(R.drawable.radio_fm);
        KHZMHZ_ImageView.setImageResource(R.drawable.radio_mhz);
        numTextView.setText("98.88");

    }

    @Override
    public void onServiceConnected() {
        FlyLog.d();
    }

    @Override
    public void onServiceDisconnected() {
        FlyLog.d();
    }

    @Override
    public void onFreqChanged(int i) {
        FlyLog.d("radio i=%d",i);
    }

    @Override
    public void onScanResult(int i, int i1) {
        FlyLog.d();
    }

    @Override
    public void onScanStart(boolean b) {
        FlyLog.d();
    }

    @Override
    public void onScanEnd(boolean b) {
        FlyLog.d();
    }

    @Override
    public void onScanAbort(boolean b) {
        FlyLog.d();
    }

    @Override
    public void onSignalUpdate(int i, int i1) {
        FlyLog.d();
    }

    @Override
    public void suspend() {
        FlyLog.d();
    }

    @Override
    public void resume() {
        FlyLog.d();
    }

    @Override
    public void pause() {
        FlyLog.d();
    }

    @Override
    public void play() {
        FlyLog.d();
    }

    @Override
    public void playPause() {
        FlyLog.d();
    }

    @Override
    public void stop() {
        FlyLog.d();
    }

    @Override
    public void next() {
        FlyLog.d();
    }

    @Override
    public void prev() {
        FlyLog.d();
    }

    @Override
    public void quitApp() {
        FlyLog.d();
    }

    @Override
    public void select(int i) {
        FlyLog.d();
    }

    @Override
    public void setFavour(boolean b) {
        FlyLog.d();
    }

    @Override
    public void onRdsPsChanged(int i, int i1, String s) {
        FlyLog.d();
    }

    @Override
    public void onRdsRtChanged(int i, int i1, String s) {
        FlyLog.d();
    }

    @Override
    public void onRdsMaskChanged(int i, int i1, int i2, int i3, int i4) {
        FlyLog.d();
    }

    @Override
    public void scanUp() {
        FlyLog.d();
    }

    @Override
    public void scanDown() {
        FlyLog.d();
    }

    @Override
    public void scanAll() {
        FlyLog.d();
    }

    @Override
    public void requestRadioFocus() {
        FlyLog.d();
    }

    @Override
    public void abandonRadioFocus() {
        FlyLog.d();
    }
}
