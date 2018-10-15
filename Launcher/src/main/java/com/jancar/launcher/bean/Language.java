package com.jancar.launcher.bean;

import android.text.TextUtils;

import java.util.Locale;

public class Language {
    public String zh_CN;
    public String zh_TW;
    public String zh_HK;
    public String en_US;

    @Override
    public String toString() {
        return "Language{" +
                "zh_CN='" + zh_CN + '\'' +
                ", zh_TW='" + zh_TW + '\'' +
                ", zh_HK='" + zh_HK + '\'' +
                ", en_US='" + en_US + '\'' +
                '}';
    }

    public String getText() {
        String text = "";
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        String type = language + "-" + country;
        switch (type) {
            case "zh-CN":
                text = zh_CN;
                break;
            case "zh-TW":
            case "zh-HK":
                if(TextUtils.isEmpty(zh_TW)){
                    text = zh_HK;
                }else{
                    text = zh_HK;
                }
                break;
            case "en-":
            case "en-US":
                text = en_US;
                break;
            default:
                text = en_US;
        }
        return TextUtils.isEmpty(text)?"":text;
    }
}
