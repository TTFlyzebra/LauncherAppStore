package com.jancar.launcher.view.flyview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jancar.launcher.R;
import com.jancar.launcher.utils.BlurDrawable;

import java.util.List;

/**
 * Created by FlyZebra on 2016/8/23.
 */
public class FlyDialog extends Dialog {
    private Context mContext;

    private RelativeLayout rlBlur;
    private boolean isBlur; // 是否显示高斯模糊
    private View view;

    private LinearLayout llTemplate;

    public FlyDialog(Context context) {
        this(context, R.style.DialogStyle);
    }

    public FlyDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        init();
    }

    private void init() {
        setContentView(R.layout.change_template_dialog);
        rlBlur = (RelativeLayout) findViewById(R.id.rl_blur);
        llTemplate = (LinearLayout) findViewById(R.id.ll_template_layout);
        Window window = getWindow();
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        window.setGravity(Gravity.CENTER);
    }

    public void setData(List<String> templates) {
        if (templates != null) {
            llTemplate.removeAllViews();
            for (String str : templates) {
                Button btn = new Button(mContext);
                btn.setSingleLine();
                btn.setEllipsize(TextUtils.TruncateAt.valueOf("MARQUEE"));
                String title = str.substring(str.lastIndexOf("/") + 1, str.lastIndexOf("."));
                btn.setText(title);
                btn.setTag(str);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(v);
                        }
                    }
                });
                btn.setTextSize(30);
                btn.setTextColor(Color.WHITE);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(220, 150);
                btn.setPadding(35, 0, 35, 0);
                btn.setLayoutParams(lp);
                btn.setBackgroundResource(R.drawable.tv_album_btn_bg);
                llTemplate.addView(btn);
            }
            if (isBlur) { // 如果显示高斯模糊就载入高斯模糊背景图
                BlurDrawable blurDrawable = new BlurDrawable((Activity) mContext);
                blurDrawable.setBlurRadius(9); //模糊半径12，越大图片越平均
                blurDrawable.setDownsampleFactor(2); //图片抽样率，这里把图片缩放小了8倍
                blurDrawable.setOverlayColor(Color.argb(150, 0x0, 0x0, 0x0)); //模糊后再覆盖的一层颜色
                //blurDrawable.setDrawOffset(0,0); //顶部View与底部View的相对坐标差，由于这里都是(0,0)起步，所以相对位置偏移为0
                rlBlur.setBackgroundDrawable(blurDrawable);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_M:
            case KeyEvent.KEYCODE_MENU: {
                if (rlBlur != null) {
                    rlBlur.setBackground(null);
                }
                dismiss();
                break;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setIsBlur(boolean isBlur) {
        this.isBlur = isBlur;
    }

    public void setView(View view) {
        this.view = view;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public void setOnItemClick(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
