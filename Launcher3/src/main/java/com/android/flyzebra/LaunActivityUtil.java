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
                        String myPackName = info.getComponentName().getPackageName();
                        //按系统属性设置过滤
                        boolean isFindinPropList = false;
                        for (String packName : Const.FILTER_SYSPROP_PACKNAMES) {
                            if (packName.equals(myPackName)) {
                                isFindinPropList = true;
                                String systemProp = "persist.jancar.front.video";
                                isFilter = !SystemProperties.get(context, systemProp, "0").equals("1");
                                if(isFilter){
                                    FlyLog.i("filter sysprop packname, packName=%s", packName);
                                }
                                break;
                            }
                        }
                        //过滤黑名单
                        if (!isFindinPropList) {
                            for (String packName : Const.FILTER_PACKNAMES) {
                                if (packName.equals(myPackName)) {
                                    FlyLog.i("filter packname, packName=%s", packName);
                                    isFilter = true;
                                    break;
                                }
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
