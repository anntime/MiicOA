package com.example.miic.meetingRoomManage.item;

import static java.lang.Integer.parseInt;

/**
 * Created by admin on 2018/10/26.
 */

public enum RoomStatus {
    All("全部",0),
    CanUse("可用",1),
    CanNotUse("停用",2);

    // 成员变量
    private String name;
    private int index;
    // 构造方法
    private RoomStatus(String name, int index) {
        this.name = name;
        this.index = index;
    }
    // 普通方法
    public static String getName(int index) {
        for (RoomStatus c : RoomStatus.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
    // 普通方法
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
    public static RoomStatus parse(String str) {

        for (RoomStatus c : RoomStatus.values()) {
            if (c.getIndex() == parseInt(str)) {
                return c;
            }
        }
        return null;
    }
}
