package com.example.miic.sealManagement.common;

import android.app.Activity;

import com.example.miic.sealManagement.item.ApproveStatus;

/**
 * Created by admin on 2018/5/22.
 */

public class SealCommon {
    public static Activity SealManageActivity;
    private String sealTypeStr="0";
    private String sealUseType = "0";
    private String sealStatueStr = "0";
    private String YesOrNo ="0";

    public String getYesOrNo(String str){
        switch(str){
            case "Yes":
                YesOrNo = "1";
                break;
            case "No":
                YesOrNo = "2";
                break;
            default:
                break;
        }
        return YesOrNo;
    }
    public String getSealType(String str){
        switch(str){
            case "全部":
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
                sealTypeStr ="全部";
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
    public String getSealStatue(String str){
        switch(str){
            case "全部":
                sealStatueStr ="0";
                break;
            case "编辑中":
                sealStatueStr = "1";
                break;
            case "审批中":
                sealStatueStr = "2";
                break;
            case "待监印":
                sealStatueStr = "10";
                break;
            case "已完成":
                sealStatueStr = "8";
                break;
            case "其他":
                sealStatueStr = "11";
                break;
            default:
                break;
        }
        return sealStatueStr;
    }

    public String getSealStatueA(String str){
        switch(str){
            case "全部":
                sealStatueStr ="0";
                break;
            case "待审批":
                sealStatueStr = "1";
                break;
            case "审批中":
                sealStatueStr = "2";
                break;
            case "已完成":
                sealStatueStr = "3";
                break;
            default:
                break;
        }
        return sealStatueStr;
    }

    public String getSealStatueP(String str){
        switch(str){
            case "全部":
                sealStatueStr ="0";
                break;
            case "待监印":
                sealStatueStr = "3";
                break;
            case "已完成":
                sealStatueStr = "2";
                break;
            default:
                break;
        }
        return sealStatueStr;
    }
    public String getSealStatueText(String str){
        switch(str){
            case "0":
                sealStatueStr ="全部";
                break;
            case "1":
                sealStatueStr = "编辑中";
                break;
            case "2":
                sealStatueStr = "审批中";
                break;
            case "10":
                sealStatueStr = "待监印";
                break;
            case "8":
                sealStatueStr = "已完成";
                break;
            case "11":
                sealStatueStr = "其他";
                break;
            default:
                break;
        }
        return sealStatueStr;
    }
    public String getSealUseType(String str){
        switch(str){
            case "全部":
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
                sealTypeStr ="全部";
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
