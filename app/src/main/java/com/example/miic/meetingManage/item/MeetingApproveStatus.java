package com.example.miic.meetingManage.item;

/**
 * Created by HP-HP on 07-06-2016.
 */
public enum MeetingApproveStatus {

    COMPLETED("已完成",1),
    ACTIVE("待审核",2),
    INACTIVE("待审核",3);

    // 成员变量
    private String name;
    private int index;
    // 构造方法
    private MeetingApproveStatus(String name, int index) {
        this.name = name;
        this.index = index;
    }
    // 普通方法
    public static String getName(int index) {
        for (MeetingApproveStatus c : MeetingApproveStatus.values()) {
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
