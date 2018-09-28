package com.flyzebra.utils;

import android.content.Context;
import android.net.Uri;

public class FavoritesDBManager {
    private Uri WorkspaceScreensUri = Uri.parse("content://com.android.launcher3.settings/workspaceScreens");
    private Uri favoritesUri = Uri.parse("content://com.android.launcher3.settings/favorites");
    private Context mContext;

    private FavoritesDBManager(Context context) {
        this.mContext = context;
    }
}


