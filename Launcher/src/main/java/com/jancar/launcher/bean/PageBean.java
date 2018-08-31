package com.jancar.launcher.bean;

import java.util.ArrayList;
import java.util.List;

public class PageBean implements Cloneable{
    public int columns = 4;
    public int rows = 1;
    public int itemPadding = 10;
    public int itemWidth= 0;
    public int itemHeight = 0;
    public int x = 0;
    public int y= 0;
    public List<CellBean> cells;
    public PageBean clone() throws CloneNotSupportedException{
        PageBean newPageBean = (PageBean) super.clone();
        newPageBean.cells = new ArrayList<>();
        return newPageBean;
    }
}
