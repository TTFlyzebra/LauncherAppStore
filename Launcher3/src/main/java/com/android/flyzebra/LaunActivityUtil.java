package com.android.flyzebra;

import android.content.Context;

import com.android.flyzebra.data.Const;
import com.android.launcher3.AppInfo;
import com.android.launcher3.IconCache;
import com.android.launcher3.compat.LauncherActivityInfoCompat;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserHandleCompat;
import com.android.launcher3.compat.UserManagerCompat;

import java.util.ArrayList;
import java.util.List;

public class LaunActivityUtil {

    public static List<AppInfo> getAppInfos(String packageName, Context context, IconCache iconCache) {
        List<AppInfo> retList = new ArrayList<>();
        LauncherAppsCompat mLauncherApps = LauncherAppsCompat.getInstance(context);
        if (mLauncherApps == null) return retList;
        UserManagerCompat mUserManager = UserManagerCompat.getInstance(context);
        if (mUserManager == null) return retList;
        List<UserHandleCompat> userHandles = mUserManager.getUserProfiles();
        if (userHandles == null) return retList;
        for (UserHandleCompat userHandle : userHandles) {
            List<LauncherActivityInfoCompat> addList = mLauncherApps.getActivityList(packageName, userHandle);
            if (addList != null) {
                for (LauncherActivityInfoCompat info : addList) {
                    boolean isFilter = false;
                    try {
                        String myPackName = info.getComponentName().getPackageName();;
                        for (String packName : Const.FILTER_PACKNAMES) {
                            if (packName.equals(myPackName)) {
                                FlyLog.i("filter packname, packName=%s", packName);
                                isFilter = false;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        FlyLog.e("filter mypackname error!");
                    }
                    if (!isFilter) {
                        retList.add((new AppInfo(context, info, userHandle, iconCache)));
                    }
                }
            }
        }
        return retList;
    }

}
