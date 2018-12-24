package com.jancar.launcher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.jancar.launcher.bean.PageBean;
import com.jancar.launcher.utils.FlyLog;
import com.jancar.launcher.utils.GsonUtils;
import com.jancar.launcher.view.flyview.FlyDialog;
import com.jancar.launcher.view.viewpager.LauncherView;
import com.jancar.launcher.view.viewpager.NavForViewPager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private LauncherView launcherView;
    private NavForViewPager navForViewPager;
    private USBReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
//        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);

        launcherView = (LauncherView) findViewById(R.id.ac_main_launcherview);

//        new ViewPagerScroller(launcherView.getContext()).initViewPagerScroll(launcherView);
        launcherView.setOffscreenPageLimit(10);
        navForViewPager = (NavForViewPager) findViewById(R.id.ac_main_navforviewpager);
//        String jsonStr = getAssetFileText("AA2.json", this);
//        List<PageBean> pageBean = GsonUtils.json2ListObject(jsonStr, PageBean.class);
//
//        if (pageBean != null && !pageBean.isEmpty()) {
//            launcherView.setData(pageBean);
//            navForViewPager.setViewPager(launcherView);
//        }
        switchUI("AP1.json");

        receiver = new USBReceiver();

        IntentFilter f = new IntentFilter();
        f.addAction(Intent.ACTION_MEDIA_MOUNTED);
        f.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        f.addDataScheme("file");
        registerReceiver(receiver, f);


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
        }finally {
            if(bf!=null){
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(ins!=null){
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
        if(file.exists()){
            jsonStr = getFileText(name,this);
        }else{
            jsonStr = getAssetFileText(name,this);
        }
        List<PageBean> pageBean = GsonUtils.json2ListObject(jsonStr, PageBean.class);
        if (pageBean != null && !pageBean.isEmpty()) {
            launcherView.setData(pageBean);
            navForViewPager.setViewPager(launcherView);
        }
    }


    public class USBReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case Intent.ACTION_MEDIA_MOUNTED:
                        FlyLog.d("Intent.ACTION_MEDIA_MOUNTED");
                        FlyLog.d(intent.toUri(0));
                        final Uri uri = intent.getData();
                        if (uri == null) return;
                        if (!uri.getScheme().equals("file")) return;
                        String path = uri.getPath();
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
                                    if(fileName.endsWith(".json")){
                                        list.add(fileName);
                                    }
                                }

                                if(!list.isEmpty()){
//                                    switchUI("AA3.json");
                                    showSwitchDialog(list);
                                }
                            }
                        }
                        break;
                    case Intent.ACTION_MEDIA_UNMOUNTED:
                        FlyLog.d("Intent.ACTION_MEDIA_UNMOUNTED");
                        FlyLog.d(intent.toUri(0));
                        switchUI("AP1.json");
                        break;
                }
            }
        }
    }

    private void showSwitchDialog(List<String>  list) {
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
