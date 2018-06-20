package com.example.miic.sealManagement.common;

import com.example.miic.sealManagement.item.ApproveStatus;

/**
 * Created by admin on 2018/5/22.
 */

public class SealCommon {
    private String sealTypeStr="0";
    private String sealUseType = "0";

    public String getSealType(String str){
        switch(str){
            case "请选择":
                sealTypeStr ="0";
                break;
            case "中心公章":
                sealTypeStr = "1";
                break;
            case "电子信息系统推广办公室章":
                sealTypeStr = "2";
                break;
            case "兆软公章":
                sealTypeStr = "3";
                break;
            case "保卫章":
                sealTypeStr = "4";
                break;
            case "房产章":
                sealTypeStr = "5";
                break;
            default:
                break;
        }
        return sealTypeStr;
    }
    public String getSealTypeText(String str){
        switch(str){
            case "0":
                sealTypeStr ="请选择";
                break;
            case "1":
                sealTypeStr = "中心公章";
                break;
            case "2":
                sealTypeStr = "电子信息系统推广办公室章";
                break;
            case "3":
                sealTypeStr = "兆软公章";
                break;
            case "4":
                sealTypeStr = "保卫章";
                break;
            case "5":
                sealTypeStr = "房产章";
                break;
            default:
                break;
        }
        return sealTypeStr;
    }
    public String getSealUseType(String str){
        switch(str){
            case "请选择":
                sealTypeStr ="0";
                break;
            case "即时用印":
                sealTypeStr = "1";
                break;
            case "携印外出":
                sealTypeStr = "2";
                break;
            default:
                break;
        }
        return sealTypeStr;
    }
    public String getSealUseTypeText(String str){
        switch(str){
            case "0":
                sealTypeStr ="请选择";
                break;
            case "1":
                sealTypeStr = "即时用印";
                break;
            case "2":
                sealTypeStr = "携印外出";
                break;
            default:
                break;
        }
        return sealTypeStr;
    }
    public ApproveStatus getApproveStatus(String str){
        ApproveStatus result;
        switch (str) {
            case "0":
                result = ApproveStatus.ALL;
                break;
            case "1":
                result = ApproveStatus.NEEDSUBMIT;
                break;
            case "2":
                result = ApproveStatus.APPROVEING;
                break;
            case "3":
                result = ApproveStatus.COMPLETED;
                break;
            case "4":
                result = ApproveStatus.ACTIVE;
                break;
            case "5":
                result = ApproveStatus.DISAGREE;
                break;
            case "7":
                result = ApproveStatus.KEEPSEAL;
                break;
            case "8":
                result = ApproveStatus.FINISH;
                break;
            default:
                result = ApproveStatus.UNIDENTIFIED;
                break;
        }
        return result;
    }

}
