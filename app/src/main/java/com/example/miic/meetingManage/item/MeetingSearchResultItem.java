package com.example.miic.meetingManage.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.oa.common.Setting.stampToDate;

/**
 * Created by XuKe on 2018/4/8.
 */

public class MeetingSearchResultItem {
    private String sealApplyID;
    private String sealApplyTitle;
    private String applyTime;
    private String approveState;

    public MeetingSearchResultItem(JSONObject objectTemp ){
        try {
            this.sealApplyID = objectTemp.getString("ID");
            this.sealApplyTitle = objectTemp.getString("Title");
            //this.applyTime = stampToDate(objectTemp.getString("ApplyTime"));
            this.applyTime = objectTemp.getString("ApplyTime");
            this.approveState = objectTemp.getString("ApproveState");

        }catch (JSONException ex){
            Log.e("InfoPageNews",ex.getMessage());
        }

    }

    public String getMeetingApplyID() {
        return sealApplyID;
    }

    public void setMeetingApplyID(String sealApplyID) {
        this.sealApplyID = sealApplyID;
    }

    public String getMeetingApplyTitle() {
        return sealApplyTitle;
    }

    public void setMeetingApplyTitle(String sealApplyTitle) {
        this.sealApplyTitle = sealApplyTitle;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getApproveState() {
        return approveState;
    }

    public void setApproveState(String approveState) {
        this.approveState = approveState;
    }
}
