package com.example.miic.meetingManage.item;

/**
 * Created by XuKe on 2018/4/9.
 */

public class MeetingTimeLineItem {
    private String approveName;
    private String approveTime;
    private MeetingApproveStatus approveState;

    public MeetingTimeLineItem(String approveName, String approveTime, MeetingApproveStatus approveState) {
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

    public MeetingApproveStatus getApproveState() {
        return approveState;
    }

    public void setApproveState(MeetingApproveStatus approveState) {
        this.approveState = approveState;
    }
}
