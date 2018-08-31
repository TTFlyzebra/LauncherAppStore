package com.jancar.launcher.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.os.Build;
import android.os.UserHandle;
import android.os.UserManager;

import java.util.ArrayList;
import java.util.List;

public class PackNameUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static List<UserHandle> getUserProfiles(Context context) {
        UserManager mUserManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        return mUserManager == null ? null : mUserManager.getUserProfiles();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static List<LauncherActivityInfo> getAllLauncgerActivitys(Context context) {
        List<LauncherActivityInfo> retLst = new ArrayList<>();
        LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        if (launcherApps == null) return retLst;
        List<UserHandle> userHandles = getUserProfiles(context);
        if(userHandles==null) return retLst;
        for (UserHandle userHandle : userHandles) {
            List<LauncherActivityInfo> addList = launcherApps.getActivityList(null, userHandle);
            if (addList != null && !addList.isEmpty()) {
                retLst.addAll(addList);
            }
        }
        return retLst;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static List<LauncherActivityInfo> getLauncgerActivitys(String packageName, Context context) {
        List<LauncherActivityInfo> retLst = new ArrayList<>();
        LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        if (launcherApps == null) return retLst;
        List<UserHandle> userHandles = getUserProfiles(context);
        if(userHandles==null) return retLst;
        for (UserHandle userHandle : userHandles) {
            List<LauncherActivityInfo> addList = launcherApps.getActivityList(packageName, userHandle);
            if (addList != null && !addList.isEmpty()) {
                retLst.addAll(addList);
            }
        }
        return retLst;
    }

}
