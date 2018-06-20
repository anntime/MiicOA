package com.example.miic.oa.work.item;

/**
 * Created by XuKe on 2018/2/9.
 */

public class WorkPageIcon {
    private int iconSrc;
    private String iconName;
    private String iconID;

    public WorkPageIcon(int iconSrc, String iconName, String iconID){
        this.iconSrc=iconSrc;
        this.iconName=iconName;
        this.iconID = iconID;
    }

    public int getIconSrc() {
        return iconSrc;
    }

    public void setIconSrc(int iconSrc) {
        this.iconSrc = iconSrc;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getIconID() {
        return iconID;
    }

    public void setIconID(String iconID) {
        this.iconID = iconID;
    }
}
