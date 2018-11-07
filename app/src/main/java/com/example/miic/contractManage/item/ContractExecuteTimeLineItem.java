package com.example.miic.contractManage.item;

/**
 * Created by admin on 2018/9/12.
 */

public class ContractExecuteTimeLineItem {
    private String amount;
    private String approveTime;
    private String payWay;

    public ContractExecuteTimeLineItem(String amount, String approveTime, String payWay) {
        this.amount = amount;
        this.approveTime = approveTime;
        this.payWay = payWay;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }
}
