package com.jancar.launcher.view.cellview;

import android.content.Context;

import com.jancar.launcher.bean.CellBean;

/**
 * 每页中cell控件生成类
 * Created by FlyZebra on 2016/6/15.
 */
public class CellViewFactory {
    /**
     * 根据传入的AppInfo构建对应的自定义pageItemView
     * @param context
     */
    public static ICellView createView(Context context, CellBean appInfo) {
        ICellView iCellView;
        switch (appInfo.type){
            case CellType.TYPE_APP_RADIO:
                iCellView = new RadioCellView(context);
                break;
            default:
                iCellView = new SimpeCellView(context);
                break;
        }
        iCellView.setData(appInfo);
        return iCellView;
    }

}
