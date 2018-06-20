package com.example.miic.carManage.item;

/**
 * Created by XuKe on 2018/4/9.
 */

public class CarTimeLineItem {
    private String approveName;
    private String approveTime;
    private CarApproveStatus approveState;

    public CarTimeLineItem(String approveName, String approveTime, CarApproveStatus approveState) {
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

    public CarApproveStatus getApproveState() {
        return approveState;
    }

    public void setApproveState(CarApproveStatus approveState) {
        this.approveState = approveState;
    }
}
