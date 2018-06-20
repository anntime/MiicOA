package com.example.miic.contractManage.item;

/**
 * Created by XuKe on 2018/4/9.
 */

public class ContractTimeLineItem {
    private String approveName;
    private String approveTime;
    private ContractApproveStatus approveState;

    public ContractTimeLineItem(String approveName, String approveTime, ContractApproveStatus approveState) {
        this.approveName = approveName;
        this.approveTime = approveTime;
        this.approveState = approveState;
    }

    public String getApproveName() {
        return approveName;
    }

    public void setApproveName(String approveName) {
        this.approveName = approveName;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public ContractApproveStatus getApproveState() {
        return approveState;
    }

    public void setApproveState(ContractApproveStatus approveState) {
        this.approveState = approveState;
    }
}
