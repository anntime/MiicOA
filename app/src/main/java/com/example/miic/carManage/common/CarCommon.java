package com.example.miic.carManage.common;

import com.example.miic.carManage.item.CarApproveStatus;
import com.example.miic.sealManagement.item.ApproveStatus;
import android.app.Activity;

/**
 * Created by admin on 2018/5/22.
 */

public class CarCommon {
    public static Activity CarManageActivity;
    private String carNum="";
    private String carStatue = "0";
    private String carDeptId = "0";
    private String YesOrNo ="0";
    private String isWorkTime ="1";

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
    public String getCarNum(String str){
        switch(str){
            case "请选择":
                carNum ="";
                break;
            case "京HK8995":
                carNum = "921d31bc-e0bb-1d37-0a72-f6d213004046";
                break;
            case "京J55283":
                carNum = "33a3918b-6885-d86a-e618-fb94afe55c75";
                break;
            case "京N360F8":
                carNum = "01df6603-961b-21e8-cb0e-36e5dc675fb9";
                break;
            case "京N70BB6":
                carNum = "ea7b73d5-f9a8-bb7f-3f32-234d6f36a481";
                break;
            default:
                break;
        }
        return carNum;
    }

    public String getCarNumName(String str){
        switch(str){
            case "全部":
                carNum ="";
                break;
            case "京HK8995":
                carNum = "京HK8995";
                break;
            case "京J55283":
                carNum = "京J55283";
                break;
            case "京N360F8":
                carNum = "京N360F8";
                break;
            case "京N70BB6":
                carNum = "京N70BB6";
                break;
            default:
                break;
        }
        return carNum;
    }

    public String getCarStatue(String str){
        switch(str){
            case "全部":
                carStatue ="0";
                break;
            case "待提交":
                carStatue = "1";
                break;
            case "审批中":
                carStatue = "2";
                break;
            case "待还车":
                carStatue = "3";
                break;
            case "待核查":
                carStatue = "7";
                break;
            case "已完成":
                carStatue = "8";
                break;
            default:
                break;
        }
        return carStatue;
    }

    //审核列表页弹出查询窗口申请状态枚举
    public String getCarStatueA(String str){
        switch(str){
            case "全部":
                carStatue ="0";
                break;
            case "待审批":
                carStatue = "1";
                break;
            case "审批中":
                carStatue = "2";
                break;
            case "已完成":
                carStatue = "3";
                break;
            default:
                break;
        }
        return carStatue;
    }
    public String getCarDept(String str){
        switch(str){
            case "全部":
                carDeptId ="0";
                break;
            case "软件部":
                carDeptId = "21C7D0FB-C0F6-4766-8869-B7EA16174DEB";
                break;
            case "用户服务部":
                carDeptId = "BFCCE082-7F4A-413A-9DDA-1AC974B024B9";
                break;
            case "市场部":
                carDeptId = "9F58ECCF-08E8-4D62-802B-E85A70D3B80A";
                break;
            case "网管中心":
                carDeptId = "EEAB9764-F283-43B0-A51E-0B27EB51E029";
                break;
            case "IT口顾问":
                carDeptId = "b0851582-dab3-12aa-f771-45ca0439a093";
                break;
            case "中小企业研究处":
                carDeptId = "590AC4BA-FE49-4386-9F44-A316C3A0658E";
                break;
            case "统计处":
                carDeptId = "8F239F57-50E6-40DA-841D-43FBAE15D7E2";
                break;
            case "信息化推进处":
                carDeptId = "88B4A4E2-B6D4-411B-96AB-F19EDBA461F4";
                break;
            case "人事处":
                carDeptId = "8182EFC3-C4FA-474B-BC56-27929C67ED7A";
                break;
            case "财务处":
                carDeptId = "2D73D900-18E4-4B9F-8A40-56295B79CD2A";
                break;
            case "办公室":
                carDeptId = "3D73D900-18E4-4B9F-8A4A-56295B79CD21";
                break;
            default:
                break;
        }
        return carDeptId;
    }

    public String getIsWorkTime(String str){
        switch(str){
            case "是":
                isWorkTime ="1";
                break;
            case "否":
                isWorkTime = "2";
                break;
            default:
                break;
        }
        return isWorkTime;
    }

    public String getIsWorkTimeText(String str){
        switch(str){
            case "1":
                isWorkTime ="是";
                break;
            case "2":
                isWorkTime = "否";
                break;
            default:
                break;
        }
        return isWorkTime;
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
