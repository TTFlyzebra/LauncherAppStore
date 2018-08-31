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
    public String textTitle = "手机互联";
    public int textSize = 26;
    public String textColor = "#FFFFFF";
    public int textLeft = 0;
    public int textRight = 0;
    public int textTop = 0;
    public int textBottom = 28;
    public int sort;
    public int width = 212;
    public int height = 317;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
