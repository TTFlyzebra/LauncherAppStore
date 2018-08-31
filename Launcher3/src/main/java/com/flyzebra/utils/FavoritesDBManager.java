package com.flyzebra.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.android.launcher3.LauncherSettings;

public class FavoritesDBManager {
    private Context context;


    private FavoritesDBManager(Context mContext){
        ContentResolver cr = mContext.getContentResolver();
        Uri uri = LauncherSettings.WorkspaceScreens.CONTENT_URI;  //获取访问数据库的uri

    }
}


