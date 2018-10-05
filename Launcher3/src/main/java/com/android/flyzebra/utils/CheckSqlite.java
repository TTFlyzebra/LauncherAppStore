package com.android.flyzebra.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import com.android.launcher3.AppInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.UserHandleCompat;
import com.android.launcher3.compat.UserManagerCompat;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.android.launcher3.LauncherModel.deletePackageFromDatabase;

public class CheckSqlite {
    private ICheckSqlite iCheckSqlite;
    private LauncherAppState launcherAppState;
    private List<AppInfo> allLauncherActivitys;
    private static final String FIRST_CREATE_DB = "FIRST_CREATE_DB";
    private static final String CELLX = "cellx";
    private static final String CELLY = "celly";
    private static final String SCREEN = "screen";

    public void setOnListener(ICheckSqlite iCheckSqlite) {
        this.iCheckSqlite = iCheckSqlite;
    }

    public interface ICheckSqlite {
        void checkFinish();
    }

    public CheckSqlite(LauncherAppState launcherAppStatep){
        this.launcherAppState = launcherAppStatep;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    public void start(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                allLauncherActivitys = PMUtils.getAppInfos(null, context, launcherAppState.getIconCache());
                checkItems(context);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(iCheckSqlite!=null){
                            iCheckSqlite.checkFinish();
                        }
                    }
                });
            }
        }).start();

    }

    /**
     * 每次启动检测workspace显示的图标，对比本机安装应用进行增减
     */
    private synchronized void checkItems(Context mContext) {
        if (allLauncherActivitys != null && !allLauncherActivitys.isEmpty()) {
            SharedPreferences sp = mContext.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), Context.MODE_PRIVATE);
            if (sp.getBoolean(FIRST_CREATE_DB, true)) {
                FlyLog.d("FIRST_CREATE_DB, start copy all apps to workspace");
                ContentValues v = new ContentValues();
                v.put(LauncherSettings.WorkspaceScreens._ID, 1);
                v.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, 0);
                final ContentResolver cr = mContext.getContentResolver();
                cr.insert(LauncherSettings.WorkspaceScreens.CONTENT_URI, v);
                if (checkFavorites(mContext, allLauncherActivitys)) {
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(FIRST_CREATE_DB, false);
                editor.apply();
            } else {
//                //TODO: 对比列表删除数据
                FlyLog.d("checkFavorites() start!");
                if (checkFavorites(mContext, allLauncherActivitys)) {
                }
            }
        }
    }

    private synchronized boolean checkFavorites(Context mContext, List<AppInfo> allLauncherActivitys) {
        boolean bMastLoad = false;
        final ContentResolver cr = mContext.getContentResolver();
        Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI, null, null, null, null);
        ArrayList<AppInfo> favoritesApps = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                final int idIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites._ID);
                final int intentIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
                final int titleIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE);
//                final int containerIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
//                final int itemTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
//                final int appWidgetIdIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.APPWIDGET_ID);
//                final int appWidgetProviderIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.APPWIDGET_PROVIDER);
                final int screenIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);
                final int cellXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
                final int cellYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);
//                final int spanXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANX);
//                final int spanYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANY);
//                final int rankIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.RANK);
//                final int restoredIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.RESTORED);
//                final int profileIdIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.PROFILE_ID);
//                final int optionsIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.OPTIONS);
                AppInfo info = new AppInfo();
                info.id = c.getInt(idIndex);
                String intentDescription = c.getString(intentIndex);
                try {
                    info.intent = Intent.parseUri(intentDescription, 0);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                info.screenId = c.getInt(screenIndex);
                info.cellX = c.getInt(cellXIndex);
                info.cellY = c.getInt(cellYIndex);
                info.spanX = 1;
                info.spanY = 1;
                info.title = c.getString(titleIndex);
                favoritesApps.add(info);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        //先删除
        int sum = favoritesApps.size();
        for (int i = sum - 1; i >= 0; i--) {
            AppInfo workInfo = favoritesApps.get(i);
            boolean isFind = false;
            for (AppInfo appInfo : allLauncherActivitys) {
                String it1 = workInfo.intent.toUri(0);
                String it2 = appInfo.intent.toUri(0);
                if (it1.equals(it2)) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                FlyLog.d("DELETE Activity=%s", workInfo.intent.toUri(0));
                //TODO:删除数据库数据，如果这一页只有这一个的情况未考虑
                cr.delete(LauncherSettings.Favorites.CONTENT_URI,
                        LauncherSettings.Favorites._ID+"=?",
                        new String[]{String.valueOf(workInfo.id)});
                for (UserHandleCompat user : UserManagerCompat.getInstance(mContext).getUserProfiles()) {
                    deletePackageFromDatabase(mContext, workInfo.intent.getPackage(), user);
                }
                //删除worksapceDB数据
                bMastLoad = true;
                favoritesApps.remove(i);
            }
        }

        /**
         * 查找workspace上的最后一个图标位置
         */
        Hashtable<String, Integer> lastPos = new Hashtable<>();
        int screen = 0;
        int cellx = 5;
        int celly = 1;
        if (!favoritesApps.isEmpty()) {
            screen = (int) favoritesApps.get(0).screenId;
            cellx = favoritesApps.get(0).cellX;
            celly = favoritesApps.get(0).cellY;
        }
        for (AppInfo appInfo : favoritesApps) {
            if ((appInfo.screenId * 10000 + appInfo.cellY * 100 + appInfo.cellX) > (screen * 10000 + celly * 100 + cellx)) {
                screen = (int) appInfo.screenId;
                cellx = appInfo.cellX;
                celly = appInfo.cellY;
            }
        }

        lastPos.put(SCREEN, screen);
        lastPos.put(CELLX, cellx);
        lastPos.put(CELLY, celly);

        for (AppInfo appInfo : allLauncherActivitys) {
            boolean isFind = false;
            int sum1 = favoritesApps.size();
            for (int i = sum1 - 1; i >= 0; i--) {
                String it1 = favoritesApps.get(i).intent.toUri(0);
                String it2 = appInfo.intent.toUri(0);
                if (it1.equals(it2)) {
                    isFind = true;
                    break;
                }
            }
            if (isFind) {
                try {
                    FlyLog.d("already add packname=%s!", appInfo.componentName.getClassName());
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                }
            } else {
                try {
                    FlyLog.d("add packclassName=%s!", appInfo.componentName.getClassName());
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                }

                if (lastPos.get(CELLX) == 5) {
                    lastPos.put(CELLX, 0);
                    if (lastPos.get(CELLY) == 1) {
                        lastPos.put(CELLY, 0);
                        lastPos.put(SCREEN, lastPos.get(SCREEN) + 1);
                        //TODO：页面加1
                        ContentValues v = new ContentValues();
                        v.put(LauncherSettings.WorkspaceScreens._ID, lastPos.get(SCREEN));
                        v.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, lastPos.get(SCREEN) - 1);
                        cr.insert(LauncherSettings.WorkspaceScreens.CONTENT_URI, v);
                        FlyLog.d("insertfly-> screen=%d", lastPos.get(SCREEN));
                    } else {
                        lastPos.put(CELLY, lastPos.get(CELLY) + 1);
                    }
                } else {
                    lastPos.put(CELLX, lastPos.get(CELLX) + 1);
                }
                ShortcutInfo shortcutInfo = new ShortcutInfo(appInfo);
                insertToDatabase(mContext, shortcutInfo, -100, lastPos.get(SCREEN), lastPos.get(CELLX), lastPos.get(CELLY));
                bMastLoad = true;
            }
        }
        return bMastLoad;
    }

    public synchronized static void insertToDatabase(Context context, final ItemInfo item, final long container, final long screenId, final int cellX, final int cellY) {
        FlyLog.d("insertfly-> screen=%d,cellx=%d,celly=%d,title=%s", screenId, cellX, cellY, item.title);
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        item.screenId = screenId;
        final ContentValues values = new ContentValues();
        final ContentResolver cr = context.getContentResolver();
        item.onAddToDatabase(context, values);
        item.id = LauncherAppState.getLauncherProvider().generateNewItemId();
        values.put(LauncherSettings.Favorites._ID, item.id);
        cr.insert(LauncherSettings.Favorites.CONTENT_URI, values);
    }

}
