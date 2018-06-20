package com.example.miic.oa.work.item;

/**
 * Created by XuKe on 2018/2/26.
 */

public class WorkPageGridView {
    private int columnIconSrc;
    private String columnIconName;
    private String columnIconID;

    public WorkPageGridView(int columnIconSrc, String columnIconName, String columnIconID){
        this.columnIconSrc=columnIconSrc;
        this.columnIconName=columnIconName;
        this.columnIconID = columnIconID;
    }

    public int getColumnIconSrc() {
        return columnIconSrc;
    }

    public void setColumnIconSrc(int columnIconSrc) {
        this.columnIconSrc = columnIconSrc;
    }

    public String getColumnIconName() {
        return columnIconName;
    }

    public void setColumnIconName(String columnIconName) {
        this.columnIconName = columnIconName;
    }

    public String getColumnIconID() {
        return columnIconID;
    }

    public void setColumnIconID(String columnIconID) {
        this.columnIconID = columnIconID;
    }
}
