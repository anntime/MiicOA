package com.example.miic.sealManagement.item;

import static java.lang.Integer.parseInt;

public enum SealApproveStatus {

    Create("待审批",1),
    Submit("已提交",2),
    Agree("同意",3),
    Disagree("不同意",4),
    Retreat("退回",5),
    IllegalAgreeEnd("同意，已归档",6),
    IllegalDisagree("不同意，已归档",7),
    IllegalRetreat("退回，已归档",8),
    End("已完成，同意",10);

    // 成员变量
    private String name;
    private int index;
    // 构造方法
    private SealApproveStatus(String name, int index) {
        this.name = name;
        this.index = index;
    }
    // 普通方法
    public static String getName(int index) {
        for (SealApproveStatus c : SealApproveStatus.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
    // get set 方法
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public static SealApproveStatus parse(String str) {

        for (SealApproveStatus c : SealApproveStatus.values()) {
            if (c.getIndex() == parseInt(str)) {
                return c;
            }
        }
        return null;
    }
}