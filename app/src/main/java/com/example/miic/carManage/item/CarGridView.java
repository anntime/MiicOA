package com.example.miic.carManage.item;

import com.example.miic.common.MyApplication;
import com.example.miic.carManage.utils.ResUtil;

/**
 * Created by admin on 2018/9/4.
 */

public class CarGridView {
    private int columnIconSrc;
    private String columnIconName;
    private String columnIconID;
    private String type;

    public CarGridView(String columnIconName, String columnIconID){
        this.columnIconName=columnIconName;
        this.columnIconID = columnIconID;
        switch (columnIconID){
            case "GoCarAdd":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_add_list");
                break;
            case "GoCarApproveList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_approve_list");
                break;
            case "GoMyCarList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "my_contract_list");
                break;
        }
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
    public String getColumnType() {
        return type;
    }

    public void setColumnType(String type) {
        this.type = type;
    }
}
