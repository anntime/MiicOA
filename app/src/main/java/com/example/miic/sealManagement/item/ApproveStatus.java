package com.example.miic.sealManagement.item;

import static java.lang.Integer.parseInt;

/**
 * Created by HP-HP on 07-06-2016.
 */
public enum ApproveStatus {

    ALL("全部",0),
    NEEDSUBMIT("待提交",1),
    APPROVEING("审批中",2),
    COMPLETED("同意(已完成)",3),
    ACTIVE("同意(已归档)",4),
    DISAGREE("不同意(已归档)",5),
    KEEPSEAL("监印中",7),
    FINISH("已完成",8),
    UNIDENTIFIED("状态不明",9);


    // 成员变量
    private String name;
    private int index;
    // 构造方法
    private ApproveStatus(String name, int index) {
        this.name = name;
        this.index = index;
    }
    // 普通方法
    public static String getName(int index) {
        for (ApproveStatus c : ApproveStatus.values()) {
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
}


