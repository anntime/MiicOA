package com.example.miic.sealManagement.item;

/**
 * Created by XuKe on 2018/4/9.
 */

public class TimeLineItem {
    private String approveName;
    private String approveTime;
    private SealApproveStatus approveState;
    //private String remark;

    public TimeLineItem(String approveName, String approveTime, SealApproveStatus approveState) {
        this.approveName = approveName;
        this.approveTime = approveTime;
        this.approveState = approveState;
        //this.remark = remark;
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

    public SealApproveStatus getApproveState() {
        return approveState;
    }

    public void setApproveState(SealApproveStatus approveState) {
        this.approveState = approveState;
    }
    //public String getRemark(){return remark;}
    //public void setRemark(String remark){
    //    this.remark = remark;
    //}
}
