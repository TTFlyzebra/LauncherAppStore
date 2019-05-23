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
     *
     * @param context
     */
    public static ICellView createView(Context context, CellBean appInfo) {
        ICellView iCellView;
        switch (appInfo.type) {
            case CellType.TYPE_BACKGROUND:
                iCellView = new StaticCellView(context);
                break;
            case CellType.TYPE_APP_RADIO:
                iCellView = new RadioCellView(context);
                break;
            case CellType.TYPE_APP_TIME:
                iCellView = new TimeCellView(context);
                break;
            case CellType.TYPE_APP_MEDIA:
                iCellView = new MediaInfoCellView(context);
                break;
            case CellType.TYPE_APP_MIRRORIMG:
                iCellView = new MirrorImageCellView(context);
                break;
            case CellType.TYPE_APP_SWITCH:
                iCellView = new SwitchCellView(context);
                break;
            case CellType.TYPE_APP_TIME1:
                iCellView = new Time1CellView(context);
                break;
            case CellType.TYPE_APP_MEDIA1:
                iCellView = new MediaInfoCellView1(context);
                break;
            case CellType.TYPE_APP_NORMAL:
            default:
                iCellView = new SimpeCellView(context);
                break;
        }
        iCellView.setData(appInfo);
        return iCellView;
    }

}
