package com.jancar.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.jancar.launcher.bean.CellBean;
import com.jancar.launcher.bean.PageBean;
import com.jancar.launcher.launcherview.viewpager.LauncherView;
import com.jancar.launcher.launcherview.viewpager.NavForViewPager;
import com.jancar.launcher.utils.GsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends Activity {
    private LauncherView launcherView;
    private NavForViewPager naviForViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        launcherView = (LauncherView) findViewById(R.id.ac_main_launcherview);
        naviForViewPager = (NavForViewPager) findViewById(R.id.ac_main_navforviewpager);
        String jsonStr = getAssetFileText("data.json",this);
        PageBean pageBean = GsonUtils.json2Object(jsonStr,PageBean.class);

        if(pageBean!=null&&pageBean.cells!=null) {
            //排序
            Collections.sort(pageBean.cells, new Comparator<CellBean>() {
                @Override
                public int compare(CellBean lhs, CellBean rhs) {
                    return lhs.sort - rhs.sort;
                }
            });
            launcherView.setData(pageBean);
            naviForViewPager.setViewPager(launcherView);
        }
    }

    @Override
    public void onBackPressed() {
//        BlurDrawable blurDrawable = new BlurDrawable(this);
//        blurDrawable.setBlurRadius(9); //模糊半径12，越大图片越平均
//        blurDrawable.setDownsampleFactor(2); //图片抽样率，这里把图片缩放小了8倍
//        blurDrawable.setOverlayColor(Color.argb(150, 0x0, 0x0, 0x0)); //模糊后再覆盖的一层颜色
//        blurDrawable.setDrawOffset(0,0); //顶部View与底部View的相对坐标差，由于这里都是(0,0)起步，所以相对位置偏移为0
//        launcherView.setBackgroundDrawable(blurDrawable);
//        super.onBackPressed();
        return;
    }
    public static String getAssetFileText(String fileName,Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
