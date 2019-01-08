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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jancar.launcher.bean.CellBean;
import com.jancar.launcher.bean.PageBean;
import com.jancar.launcher.bean.TemplateBean;
import com.jancar.launcher.utils.FlyLog;
import com.jancar.launcher.utils.GsonUtils;
import com.jancar.launcher.utils.SystemProperties;
import com.jancar.launcher.view.flyview.FlyDialog;
import com.jancar.launcher.view.pageview.SimplePageView;
import com.jancar.launcher.view.viewpager.LauncherView;
import com.jancar.launcher.view.viewpager.NavForViewPager;
import com.jancar.launcher.view.viewpager.Switch3DPageTransformer;

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
    private LauncherView launcherView;
    private SimplePageView topView;
    private RelativeLayout pagesView;
    private NavForViewPager navForViewPager;
    private USBReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
//        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);

        launcherView = (LauncherView) findViewById(R.id.ac_main_launcherview);
        topView = (SimplePageView) findViewById(R.id.ac_main_topview);
        pagesView = (RelativeLayout) findViewById(R.id.ac_main_pages);


//        new ViewPagerScroller(launcherView.getContext()).initViewPagerScroll(launcherView);
        launcherView.setOffscreenPageLimit(10);
        navForViewPager = (NavForViewPager) findViewById(R.id.ac_main_navforviewpager);

        String template = SystemProperties.get(this, SystemProperties.Property.PERSIST_KEY_TEMPLATE_NAME, "AP1") + ".json";
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

    private void switchUI(String name) {
        String jsonStr = null;
        File file = new File(name);
        boolean isInFile = file.exists();
        if (isInFile) {
            jsonStr = getFileText(name, this);
        } else {
            jsonStr = getAssetFileText(name, this);
        }
        TemplateBean templateBean = GsonUtils.json2Object(jsonStr, TemplateBean.class);
        if (templateBean != null) {
            List<PageBean> pageBeans = templateBean.pageList;
            if (templateBean.x != 0 || templateBean.y != 0 || templateBean.width != 0 || templateBean.height != 0) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) pagesView.getLayoutParams();
                lp.setMargins(templateBean.x, templateBean.y, 0, 0);
                lp.setMarginStart(templateBean.x);
                lp.width = templateBean.width;
                lp.height = templateBean.height;
                pagesView.setLayoutParams(lp);
                for (PageBean pageBean : pageBeans) {
                    if (pageBean.cells == null || pageBean.cells.isEmpty()) continue;
                    for (CellBean cellBean : pageBean.cells) {
                        cellBean.x = cellBean.x - templateBean.x;
                        cellBean.y = cellBean.y - templateBean.y;
                    }
                }
            } else {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) pagesView.getLayoutParams();
                lp.setMargins(templateBean.x, templateBean.y, 0, 0);
                lp.setMarginStart(templateBean.x);
                lp.width = -1;
                lp.height = -1;
                pagesView.setLayoutParams(lp);
            }

            if (pageBeans != null && !pageBeans.isEmpty()) {

                if (isInFile) {
                    String rootPath = name.substring(0, name.lastIndexOf("/"));
                    for (PageBean page : pageBeans) {
                        for (CellBean cellBean : page.cells) {
                            if (!TextUtils.isEmpty(cellBean.defaultImageUrl)) {
                                cellBean.defaultImageUrl.replace("file:///android_asset", rootPath);
                            }
                        }
                    }
                }

                switch (templateBean.animtor) {
                    case 1:
                        launcherView.setPageTransformer(true, new Switch3DPageTransformer());
                        break;
                    default:
                        launcherView.setPageTransformer(true, null);
                        break;
                }

                launcherView.setData(templateBean);
                navForViewPager.setViewPager(launcherView);
            }

            topView.removeAllViews();
            if (templateBean.topPage != null && templateBean.topPage.cells != null && !templateBean.topPage.cells.isEmpty()) {
                topView.setData(templateBean.topPage);
            }

            //设置壁纸
            if (!TextUtils.isEmpty(templateBean.bkimg))
                setBackGround(templateBean.bkimg);
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
//                                    switchUI("AA3.json");
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

}
