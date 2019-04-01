package com.jancar.launcher.bean;

import java.util.ArrayList;
import java.util.List;

public class PageBean implements Cloneable{
    public int columns = 0;
    public int rows = 0;
    public int itemPadding = 0;
    public int itemWidth= 0;
    public int itemHeight = 0;
    public int x = 0;
    public int y= 0;
    public List<CellBean> cellList;
    public PageBean clone() throws CloneNotSupportedException{
        PageBean newPageBean = (PageBean) super.clone();
        newPageBean.cellList = new ArrayList<>();
        return newPageBean;
    }
}
