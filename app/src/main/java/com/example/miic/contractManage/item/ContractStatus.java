package com.example.miic.contractManage.item;

import static java.lang.Integer.parseInt;

/**
 * Created by admin on 2018/9/12.
 */

public enum ContractStatus {

    All("全部",0),
    Init("待提交",1),
    Execute("审批中",2),
    Finish("待监印",3),
    IllegalFinish("同意(已归档)",4),
    IllegalDisagreeFinish("不同意(已归档)",5),
    File("待归档",7),
    Implement("执行中",8),
    ContractFinish("合同完成",9),
    ContractTermination("合同终止",10);

    // 成员变量
    private String name;
    private int index;
    // 构造方法
    private ContractStatus(String name, int index) {
        this.name = name;
        this.index = index;
    }
    // 普通方法
    public static String getName(int index) {
        for (ContractStatus c : ContractStatus.values()) {
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
    public static ContractStatus parse(String str) {

        for (ContractStatus c : ContractStatus.values()) {
            if (c.getIndex() == parseInt(str)) {
                return c;
            }
        }
        return null;
    }
}
