package com.example.miic.oa.work.item;

import java.util.List;

/**
 * Created by XuKe on 2018/2/9.
 */

public class WorkPageItem {
    private String columnTitle;
    private List<WorkPageGridView> columnList;
    private String columnID;

    public WorkPageItem(String columnTitle, List<WorkPageGridView> columnList, String columnID){
        this.columnTitle=columnTitle;
        this.columnList=columnList;
        this.columnID = columnID;

    }

    public String getColumnTitle() {
        return columnTitle;
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    public List<WorkPageGridView> getColumnGridView() {
        return columnList;
    }

    public void setColumnGridView(List<WorkPageGridView> columnList) {
        this.columnList = columnList;
    }

    public String getColumnID() {
        return columnID;
    }

    public void setColumnID(String columnID) {
        this.columnID = columnID;
    }


}
