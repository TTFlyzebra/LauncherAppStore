package com.jancar.launcher.launcherview.cellview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jancar.launcher.R;
import com.jancar.launcher.launcherview.flyview.NumTextView;

public class RadioCellView extends SimpeCellView {
    private NumTextView numTextView;
    private ImageView AMFM_ImageView;
    private ImageView KHZMHZ_ImageView;

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
        AMFM_ImageView = new ImageView(context);
        LayoutParams params1 = new LayoutParams(-2,-2);
        params1.leftMargin = 10;
        params1.topMargin = 64;
        AMFM_ImageView.setImageResource(R.drawable.radio_am);
        addView(AMFM_ImageView,params1);

        numTextView = new NumTextView(context);
        LayoutParams params3 = new LayoutParams(-1,-1);
        params3.leftMargin = 0;
        params3.topMargin = 114;
        addView(numTextView,params3);

        KHZMHZ_ImageView = new ImageView(context);
        LayoutParams params2 = new LayoutParams(-2,-2);
        params2.leftMargin = 132;
        params2.topMargin = 176;
        KHZMHZ_ImageView.setImageResource(R.drawable.radio_khz);
        addView(KHZMHZ_ImageView,params2);

    }

    @Override
    public void notifyView() {
        super.notifyView();
        AMFM_ImageView.setImageResource(R.drawable.radio_fm);
        KHZMHZ_ImageView.setImageResource(R.drawable.radio_mhz);
        numTextView.setText("98.88");

    }
}
