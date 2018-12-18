package com.jancar.launcher.bean;

public class CellBean implements Cloneable{
    public String jancar = "";
    public String packName = "com.android.launcher3";
    public String className = "com.android.launcher3.Launcher";
    public String action = "";
    public String intentFlag = "0x10200000";
    public String defaultImageUrl = "file:///android_asset/image/page01_default_1.png";
    public String focusImageUrl="file:///android_asset/image/page01_focus_1.png";
    public int type = 1;
    public Language textTitle;
    public int textSize = 26;
    public String textColor = "#FFFFFF";
    public int textLeft = 0;
    public int textRight = 0;
    public int textTop = 0;
    public int textBottom = 28;
    public int sort;
    public int x = 0;
    public int y = 0;
    public int width = 0;
    public int height = 0;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "CellBean{" +
                "jancar='" + jancar + '\'' +
                ", packName='" + packName + '\'' +
                ", className='" + className + '\'' +
                ", action='" + action + '\'' +
                ", intentFlag='" + intentFlag + '\'' +
                ", defaultImageUrl='" + defaultImageUrl + '\'' +
                ", focusImageUrl='" + focusImageUrl + '\'' +
                ", type=" + type +
                ", textTitle=" + textTitle +
                ", textSize=" + textSize +
                ", textColor='" + textColor + '\'' +
                ", textLeft=" + textLeft +
                ", textRight=" + textRight +
                ", textTop=" + textTop +
                ", textBottom=" + textBottom +
                ", sort=" + sort +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
