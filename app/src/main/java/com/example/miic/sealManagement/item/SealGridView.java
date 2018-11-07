package com.example.miic.sealManagement.item;

import com.example.miic.carManage.utils.ResUtil;
import com.example.miic.common.MyApplication;

/**
 * Created by admin on 2018/9/4.
 */

public class SealGridView {
    private int columnIconSrc;
    private String columnIconName;
    private String columnIconID;
    private String type;

    public SealGridView(String columnIconName, String columnIconID){
        this.columnIconName=columnIconName;
        this.columnIconID = columnIconID;
        switch (columnIconID){
            case "GoSealAdd":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_add_list");
                break;
            case "GoSealApproveList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_approve_list");
                break;
            case "GoMySealList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "my_contract_list");
                break;
            case "GoSealPrintList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_print_list");
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
