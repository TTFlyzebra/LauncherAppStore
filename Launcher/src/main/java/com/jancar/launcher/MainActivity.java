package com.jancar.launcher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jancar.launcher.bean.CellBean;
import com.jancar.launcher.bean.PageBean;
import com.jancar.launcher.bean.ThemeBean;
import com.jancar.launcher.utils.FlyLog;
import com.jancar.launcher.utils.GsonUtils;
import com.jancar.launcher.utils.SystemProperties;
import com.jancar.launcher.view.flyview.FlyDialog;
import com.jancar.launcher.view.pageanimtor.PageTransformerCube;
import com.jancar.launcher.view.pageanimtor.PageTransformerPage;
import com.jancar.launcher.view.pageview.SimplePageView;
import com.jancar.launcher.view.viewpager.LauncherView;
import com.jancar.launcher.view.viewpager.NavForViewPager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private LauncherView pageViews;
    private SimplePageView topView;
    private NavForViewPager navForViewPager;
    private USBReceiver receiver;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int addcount = 0;
    private boolean isLoad = false;
    private float screenWidth = 1024;
    private float screenHeigh = 600;
    private float screenScacle = 1.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
//        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeigh = dm.heightPixels;


        pageViews = (LauncherView) findViewById(R.id.ac_main_launcherview);
        topView = (SimplePageView) findViewById(R.id.ac_main_topview);

//        new ViewPagerScroller(launcherView.getContext()).initViewPagerScroll(launcherView);
        pageViews.setOffscreenPageLimit(10);
        navForViewPager = (NavForViewPager) findViewById(R.id.ac_main_navforviewpager);

        String template = SystemProperties.get(this, SystemProperties.Property.PERSIST_KEY_TEMPLATE_NAME, "FLY") + ".json";
        switchUI(template);

        receiver = new USBReceiver();

        IntentFilter f = new IntentFilter();
        f.addAction(Intent.ACTION_MEDIA_MOUNTED);
        f.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        f.addDataScheme("file");
        registerReceiver(receiver, f);
    }

    public byte[] getBytesByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        return;
    }

    public static String getAssetFileText(String fileName, Context context) {
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


    public static String getFileText(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream ins = null;
        BufferedReader bf = null;
        try {
            File file = new File(fileName);
            ins = new FileInputStream(file);
            bf = new BufferedReader(new InputStreamReader(ins));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    private void switchUI(final String name) {
        String jsonStr = null;
        File file = new File(name);
        final boolean isInFile = file.exists();
        if (isInFile) {
            jsonStr = getFileText(name, this);
        } else {
            jsonStr = getAssetFileText(name, this);
        }
        final ThemeBean themeBean = GsonUtils.json2Object(jsonStr, ThemeBean.class);
        if (themeBean != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isLoad) {
                        loadView(themeBean, isInFile, name);
                    }
                }
            }, 10000);
            try {
                final PageBean pageBean = themeBean.pageList.get(0);
                for (int j = 0; j < pageBean.cellList.size(); j++) {
                    CellBean cellBean = pageBean.cellList.get(j);
                    Glide.with(this)
                            .load(cellBean.defaultImageUrl)
                            .asBitmap()
                            .override(cellBean.width, cellBean.height)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                    addcount++;
                                    if (addcount == pageBean.cellList.size() && !isLoad) {
                                        loadView(themeBean, isInFile, name);
                                    }
                                }
                            });
                }
            } catch (Exception e) {
                FlyLog.e(e.toString());
                loadView(themeBean, isInFile, name);
            }
        }
    }


    private void loadView(ThemeBean themeBean, boolean isInFile, String name) {
        isLoad = true;
        matchResolution(themeBean);
        List<PageBean> pageBeans = themeBean.pageList;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(themeBean.right - themeBean.left, themeBean.bottom - themeBean.top);
        lp.setMarginStart(themeBean.left);
        lp.topMargin = themeBean.top;

        pageViews.setLayoutParams(lp);

        if (pageBeans != null && !pageBeans.isEmpty()) {

            if (isInFile) {
                String rootPath = name.substring(0, name.lastIndexOf("/"));
                for (PageBean page : pageBeans) {
                    for (CellBean cellBean : page.cellList) {
                        if (!TextUtils.isEmpty(cellBean.defaultImageUrl)) {
                            cellBean.defaultImageUrl.replace("file:///android_asset", rootPath);
                        }
                    }
                }
            }

            switch (themeBean.animType) {
                case 1:
                    pageViews.setPageTransformer(true, new PageTransformerCube());
                    break;
                case 2:
                    pageViews.setPageTransformer(true, new PageTransformerPage());
                    break;
                default:
                    pageViews.setPageTransformer(true, null);
                    break;
            }

            pageViews.setData(themeBean);
            navForViewPager.setViewPager(pageViews);
        }

        topView.removeAllViews();
        if (themeBean.topPage != null && themeBean.topPage.cellList != null && !themeBean.topPage.cellList.isEmpty()) {
            topView.setData(themeBean.topPage);
        }
    }

    private void setBackGround(String bkimg) {
        Glide.with(this).load(bkimg).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
                getWindow().getDecorView().setBackground(glideDrawable);
            }
        });

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FlyLog.d(event.toString());
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            findUSBTemplate("/storage/udisk2");
        }
        return super.dispatchKeyEvent(event);
    }

    public class USBReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case Intent.ACTION_MEDIA_MOUNTED:
                        if ("1".equals(SystemProperties.get(MainActivity.this, SystemProperties.Property.PERSIST_KEY_TEMPLATE_ON, "1"))) {
                            FlyLog.d("Intent.ACTION_MEDIA_MOUNTED");
                            FlyLog.d(intent.toUri(0));
                            final Uri uri = intent.getData();
                            if (uri == null) return;
                            if (!uri.getScheme().equals("file")) return;
                            String path = uri.getPath();
                            findUSBTemplate(path);
                        }
                        break;
                    case Intent.ACTION_MEDIA_UNMOUNTED:
                        if ("1".equals(SystemProperties.get(MainActivity.this, SystemProperties.Property.PERSIST_KEY_TEMPLATE_ON, "1"))) {
                            FlyLog.d("Intent.ACTION_MEDIA_UNMOUNTED");
                            FlyLog.d(intent.toUri(0));
                            switchUI("AP1.json");
                        }
                        break;
                }
            }
        }
    }

    private void findUSBTemplate(String path) {
        if (path == null) return;
        FlyLog.d("MEDIA_MOUNTED path=%s", path);
        String str = path + File.separator + "Launcher";
        File file = new File(str);
        if (file.exists() && file.isDirectory()) {
            File f[] = file.listFiles();
            if (f != null && f.length > 0) {
                List<String> list = new ArrayList<>();
                for (File tmeFile : f) {
                    String fileName = tmeFile.getAbsolutePath();
                    if (fileName.endsWith(".json")) {
                        list.add(fileName);
                    }
                }

                if (!list.isEmpty()) {
                    showSwitchDialog(list);
                }
            }
        }
    }

    private void showSwitchDialog(List<String> list) {
        final FlyDialog flyDialog = new FlyDialog(this);
        flyDialog.setData(list);
        flyDialog.setIsBlur(true);
        flyDialog.show();
        flyDialog.setOnItemClick(new FlyDialog.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                String path = (String) view.getTag();
                switchUI(path);
                flyDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * 匹配屏幕分辨率
     */
    private void matchResolution(ThemeBean mThemeBean) {
        //如果设置的分辨率无效，设置为系统获取的分辨率和有效区域。
        if (mThemeBean.screenWidth <= 0 || mThemeBean.screenHeight <= 0) {
            mThemeBean.screenWidth = (int) screenWidth;
            mThemeBean.screenHeight = (int) screenHeigh;
            mThemeBean.left = 0;
            mThemeBean.top = 0;
            mThemeBean.right = (int) screenWidth;
            mThemeBean.bottom = (int) screenHeigh;
            return;
        }
        if (mThemeBean.right <= mThemeBean.left || mThemeBean.bottom <= mThemeBean.top) {
            mThemeBean.left = 0;
            mThemeBean.top = 0;
            mThemeBean.right = (int) screenWidth;
            mThemeBean.bottom = (int) screenHeigh;
        }

        //如果设置的有效区域无效，设置有效区域为全屏
        float wScale = screenWidth / (float) mThemeBean.screenWidth;
        float hScale = screenHeigh / (float) mThemeBean.screenHeight;
        if (wScale == 1 && hScale == 1) {
            if (mThemeBean.left != 0 || mThemeBean.top != 0) {
                for (PageBean pageBean : mThemeBean.pageList) {
                    for (CellBean cellBean : pageBean.cellList) {
                        //有效显示区域FitCenter，只显示位于指定区域中的内容
                        cellBean.x = cellBean.x - mThemeBean.left;
                        cellBean.y = cellBean.y - mThemeBean.top;
                    }
                }
            }
            return;
        }

        screenScacle = Math.min(wScale, hScale);
        int moveX = (int) ((screenWidth - mThemeBean.screenWidth * screenScacle) / 2);
        int moveY = (int) ((screenHeigh - mThemeBean.screenHeight * screenScacle) / 2);

        mThemeBean.left = (int) (mThemeBean.left * screenScacle) + moveX;
        mThemeBean.top = (int) (mThemeBean.top * screenScacle) + moveY;
        mThemeBean.right = (int) (mThemeBean.right * screenScacle) + moveX;
        mThemeBean.bottom = (int) (mThemeBean.bottom * screenScacle) + moveY;

        if (mThemeBean.pageList != null) {
            for (PageBean pageBean : mThemeBean.pageList) {
                for (CellBean cellBean : pageBean.cellList) {
                    //有效显示区域FitCenter，只显示位于指定区域中的内容
                    cellBean.x = (int) (cellBean.x * screenScacle) + moveX - mThemeBean.left;
                    cellBean.y = (int) (cellBean.y * screenScacle) + moveY - mThemeBean.top;
                    cellBean.width = (int) (cellBean.width * screenScacle);
                    cellBean.height = (int) (cellBean.height * screenScacle);
                    cellBean.textSize = (int) (cellBean.textSize * screenScacle);
                    cellBean.textLeft = (int) (cellBean.textLeft * screenScacle);
                    cellBean.textTop = (int) (cellBean.textTop * screenScacle);
                    cellBean.textRight = (int) (cellBean.textRight * screenScacle);
                    cellBean.textBottom = (int) (cellBean.textBottom * screenScacle);
                }
            }
            if (mThemeBean.topPage != null && mThemeBean.topPage.cellList != null) {
                for (CellBean cellBean : mThemeBean.topPage.cellList) {
                    cellBean.x = (int) (cellBean.x * screenScacle) + moveX;
                    cellBean.y = (int) (cellBean.y * screenScacle) + moveY;
                    cellBean.width = (int) (cellBean.width * screenScacle);
                    cellBean.height = (int) (cellBean.height * screenScacle);
                    cellBean.textSize = (int) (cellBean.textSize * screenScacle);
                    cellBean.textLeft = (int) (cellBean.textLeft * screenScacle);
                    cellBean.textTop = (int) (cellBean.textTop * screenScacle);
                    cellBean.textRight = (int) (cellBean.textRight * screenScacle);
                    cellBean.textBottom = (int) (cellBean.textBottom * screenScacle);
                }
            }
        }
    }
}
