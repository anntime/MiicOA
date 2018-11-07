package com.example.miic.contractManage.item;

import com.example.miic.common.MyApplication;
import com.example.miic.contractManage.utils.ResUtil;

/**
 * Created by admin on 2018/9/4.
 */

public class ContractGridView {
    private int columnIconSrc;
    private String columnIconName;
    private String columnIconID;
    private String type;

    public ContractGridView(String columnIconName, String columnIconID){
        this.columnIconName=columnIconName;
        this.columnIconID = columnIconID;
        switch (columnIconID){
            case "GoApproveList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_approve_list");
                break;
            case "GoContractExecuteList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_execute_list");
                break;
            case "GoContractPrintList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_print_list");
                break;
            case "GoContractSearchList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_search_list");
                break;
            case "GoContractFinishList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_finish_list");
                break;
            case "GoContractPlanList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_plan_list");
                break;
            case "GoContractManageList":
                this.columnIconSrc=ResUtil.getDrawableId(MyApplication.getContext(), "contract_manage_list");
                break;
            case "GoMyContractList":
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
