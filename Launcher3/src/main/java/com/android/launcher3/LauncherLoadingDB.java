package com.android.launcher3;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.TextUtils;

import com.android.flyzebra.FlyLog;
import com.android.flyzebra.LaunActivityUtil;
import com.android.launcher3.compat.UserHandleCompat;
import com.android.launcher3.compat.UserManagerCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import static com.android.launcher3.LauncherModel.deletePackageFromDatabase;

public class LauncherLoadingDB {
    private ILoadingDB iLoadingDB;
    private LauncherAppState launcherAppState;
    private List<AppInfo> allLauncherActivitys;
    private static final String CELLX = "cellx";
    private static final String CELLY = "celly";
    private static final String SCREEN = "screen";


    public void setOnListener(ILoadingDB iLoadingDB) {
        this.iLoadingDB = iLoadingDB;
    }

    public interface ILoadingDB {
        void loadingFinish(boolean isChanged);
    }

    public LauncherLoadingDB(LauncherAppState launcherAppStatep) {
        this.launcherAppState = launcherAppStatep;
    }


    public void start(final Context context) {
        FlyLog.d("start");
        LauncherAppState.getLauncherProvider().loadDefaultFavoritesIfNecessary();
        allLauncherActivitys = LaunActivityUtil.getAppInfos(null, context, launcherAppState.getIconCache());

        Collections.sort(allLauncherActivitys, new Comparator<AppInfo>() {
            public int compare(AppInfo p1, AppInfo p2) {
                try {
                    String str1 = p1.componentName.toString();
                    String str2 = p2.componentName.toString();
                    if (str1.contains("com.google")||str1.contains("com.android.vending")) {
                        return 1;
                    } else if (str2.contains("com.google")||str2.contains("com.android.vending")) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                    return 0;
                }
            }
        });

        iLoadingDB.loadingFinish(checkItems(context));
    }

    /**
     * 每次启动检测workspace显示的图标，对比本机安装应用进行增减
     */
    private boolean checkItems(Context mContext) {
        if (allLauncherActivitys != null && !allLauncherActivitys.isEmpty()) {
            SharedPreferences sp = mContext.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), Context.MODE_PRIVATE);
            return checkFavorites(mContext, allLauncherActivitys);
        }else{
            return false;
        }
    }

    private boolean checkFavorites(Context mContext, List<AppInfo> allLauncherActivitys) {
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
                if(TextUtils.isEmpty(intentDescription)){
                    continue;
                }
                try {
                    info.intent = Intent.parseUri(intentDescription, 0);
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                    continue;
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
                FlyLog.e("DELETE Activity=%s", workInfo.intent.toUri(0));
                //TODO:删除数据库数据，如果这一页只有这一个的情况未考虑
                cr.delete(LauncherSettings.Favorites.CONTENT_URI,
                        LauncherSettings.Favorites._ID + "=?",
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
        LauncherAppState app = LauncherAppState.getInstance();
        InvariantDeviceProfile profile = app.getInvariantDeviceProfile();
        int cellx = profile.numColumns - 1;
        int celly = profile.numRows - 1;
        if (!favoritesApps.isEmpty()) {
            screen = (int) favoritesApps.get(0).screenId;
            cellx = favoritesApps.get(0).cellX;
            celly = favoritesApps.get(0).cellY;
            for (AppInfo appInfo : favoritesApps) {
                if ((appInfo.screenId * 10000 + appInfo.cellY * 100 + appInfo.cellX) > (screen * 10000 + celly * 100 + cellx)) {
                    screen = (int) appInfo.screenId;
                    cellx = appInfo.cellX;
                    celly = appInfo.cellY;
                }
            }
        } else {
            try {
                ContentValues v = new ContentValues();
                v.put(LauncherSettings.WorkspaceScreens._ID, 1);
                v.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, 0);
                cr.insert(LauncherSettings.WorkspaceScreens.CONTENT_URI, v);
                FlyLog.d("insertfly-> screen=%d", screen);
            } catch (Exception e) {
                FlyLog.e(e.toString());
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

                if (lastPos.get(CELLX) == (profile.numColumns - 1)) {
                    lastPos.put(CELLX, 0);
                    if (lastPos.get(CELLY) == (profile.numRows - 1)) {
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

    private void insertToDatabase(Context context, final ItemInfo item, final long container, final long screenId, final int cellX, final int cellY) {
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
