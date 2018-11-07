package com.example.miic.contractManage.common;

import android.app.Activity;

/**
 * Created by admin on 2018/8/27.
 */

public class ContractCommon {
    public static Activity ContractManageActivity;


    public String getContractTypeOne(String str){
        String result ="0";
        switch(str){
            case "全部":
                result ="99";
                break;
            case "收款":
                result = "0";
                break;
            case "付款":
                result = "1";
                break;
            case "其他":
                result ="2";
                break;
        }
        return result;
    }
    public String getContractTypeTwo(String str){
        String result;
        switch(str){
            case "全部":
                result ="99";
                break;
            case "开发合同":
                result = "0";
                break;
            case "采购合同":
                result = "1";
                break;
            case "服务合同":
                result = "2";
                break;
            case "销售合同":
                result = "3";
                break;
            case "其他合同":
                result = "4";
                break;
            default:
                result ="99";
                break;
        }
        return result;
    }
    public String getIdentificationItem(String str){
        String result ="0";
        switch(str){
            case "全部":
                result ="0";
                break;
            case "中心":
                result = "1";
                break;
            case "兆软":
                result = "2";
                break;
        }
        return result;
    }

    public String getYesOrNo(String str){
        String YesOrNo ="";
        switch(str){
            case "1":
                YesOrNo = "1";
                break;
            case "2":
                YesOrNo = "2";
                break;
        }
        return YesOrNo;
    }
    public String getPayWay(String str){
        String result ="";
        switch(str){
            case "1":
                result = "现金";
                break;
            case "2":
                result = "转账支票";
                break;
            case "3":
                result = "汇款";
                break;
        }
        return result;
    }
    public String getScottareTypeStr(String str){
        String result ="免税";
        switch(str){
            case "1":
                result ="免税";
                break;
            case "2":
                result = "正常缴税";
                break;
        }
        return result;
    }
    public String getIdentificationItemStr(String str){
        String result ="全部";
        switch(str){
            case "0":
                result ="全部";
                break;
            case "1":
                result = "中心";
                break;
            case "2":
                result = "兆软";
                break;
        }
        return result;
    }
    public String getPrintTypeStr(String str){
        String result ="公章";
        switch(str){
            case "1":
                result ="公章";
                break;
            case "2":
                result = "合同专用章";
                break;
        }
        return result;
    }
    public String getContractTypeOneStr(String str){
        String result ="其他";
        switch(str){
            case "0":
                result ="收款";
                break;
            case "1":
                result = "付款";
                break;
            case "2":
                result = "其他";
                break;
        }
        return result;
    }
    public String getContractTypeTwoStr(String str){
        String result = "其他";
        switch(str){
            case "0":
                result ="开发";
                break;
            case "1":
                result = "采购";
                break;
            case "2":
                result = "服务";
                break;
            case "3":
                result = "销售";
                break;
            case "4":
                result = "其他";
                break;
        }
        return result;
    }
    public String getContractDraftTypeStr(String str){
        String result ="其他";
        switch(str){
            case "0":
                result ="我方起草";
                break;
            case "1":
                result = "对方起草";
                break;
            case "2":
                result = "中间方（中央采购）";
                break;
        }
        return result;
    }
    public String getContractApproveStatusStr(String str){
        String result ="全部";
        switch(str){
            case "0":
                result ="全部";
                break;
            case "1":
                result = "待审批";
                break;
            case "2":
                result = "审批中";
                break;
            case "3":
                result = "已完成";
                break;
        }
        return result;
    }
    public String getContractStatue(String str){
        String result="0";
        switch(str){
            case "全部":
                result ="0";
                break;
            case "待提交":
                result = "1";
                break;
            case "待审批":
                result = "1";
                break;
            case "审批中":
                result = "2";
                break;
            case "已完成":
                result = "3";
                break;
            case "待监印":
                result = "3";
                break;
            case "待归档":
                result = "7";
                break;
            case "执行中":
                result = "8";
                break;
            case "合同完成":
                result = "9";
                break;
            case "合同终止":
                result = "10";
                break;
            case "其他":
                result = "11";
                break;
        }
        return result;
    }
    public String getContractStatusStr(String status){
        String result;
        switch (status) {
            case "0":
                result = "全部";
                break;
            case "1":
                result = "待提交";
                break;
            case "2":
                result = "审批中";
                break;
            case "3":
                result = "待监印";
                break;
            case "4":
                result = "同意(已归档)";
                break;
            case "5":
                result="不同意(已归档)";
                break;
            case "7":
                result = "待归档";
                break;
            case "8":
                result = "执行中";
                break;
            case "9":
                result = "合同完成";
                break;
            case "10":
                result = "合同终止";
                break;
            case "11":
                result = "其他";
                break;
            default:
                result = "状态不明";
                break;
        }
        return result;
    }
    public int getContractStatusColorStr(String state){
        int result= 0xffCD5C5C;
        switch (state) {
            case "0":
                result = 0xffBA57ED;
                break;
            case "1":
                result = 0xff62B264;
                break;
            case "2":
                result = 0xffFFCC33;
                break;
            case "3":
                result = 0xffFF33CC;
                break;
            case "7":
                result = 0xff63b8ff;
                break;
            case "8":
                result = 0xffD2691E;
                break;
            case "9":
                result = 0xffC71585;
                break;
            case "10":
                result = 0xff63b8ff;
                break;
            case "11":
                result = 0xff6A5ACD;
                break;
            default:
                break;
        }
        return result;
    }
}
