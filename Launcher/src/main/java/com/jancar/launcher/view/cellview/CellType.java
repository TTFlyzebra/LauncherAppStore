package com.jancar.launcher.view.cellview;

public interface CellType {
    /**
     *背景图片
     */
    int TYPE_BACKGROUND = 0;
    /**
     * 普通应用
     */
    int TYPE_APP_NORMAL = 1;
    /**
     * 收音机应用
     */
    int TYPE_APP_RADIO = 2;
    /**
     * 时间
     */
    int TYPE_APP_TIME = 3;
    /**
     * 媒体小部件
     */
    int TYPE_APP_MEDIA = 4;
    /**
     * 可镜像的小部件
     */
    int TYPE_APP_MIRRORIMG = 5;

    int TYPE_APP_SWITCH = 9;

    /**
     * 时间1
     */
    int TYPE_APP_TIME1 = 10;
    /**
     * 媒体小部件1
     */
    int TYPE_APP_MEDIA1 = 11;
}
