package com.example.miic.oa.common;
import static java.lang.Integer.parseInt;

/**
 * Created by admin on 2018/9/28.
 */

public enum EnumMessageType {

    Comment("评论",0),
    CommentAt("评论@",1),
    Praise("赞",2),
    Tread("踩",3),
    InitWelcome("欢迎",4),
    NewsApprove("新闻审批",6),
    SealApprove("用印审批",7),
    ContractApprove("合同审批",8),
    CarApprove("用车审批",9);

    // 成员变量
    private String name;
    private int index;
    // 构造方法
    private EnumMessageType(String name, int index) {
        this.name = name;
        this.index = index;
    }
    // 普通方法
    public static String getName(int index) {
        for (EnumMessageType c : EnumMessageType.values()) {
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
    public static EnumMessageType parse(String str) {
        for (EnumMessageType c : EnumMessageType.values()) {
            if (c.getIndex() == parseInt(str)) {
                return c;
            }
        }
        return null;
    }
}
