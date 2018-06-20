package com.example.miic.qjManage.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XuKe on 2018/4/8.
 */

public class QjSearchResultItem {
    private String qjApplyID;
    private String qjApplyTitle;
    private String applyTime;
    private String approveState;

    public QjSearchResultItem(JSONObject objectTemp ){
        try {
            this.qjApplyID = objectTemp.getString("ID");
            this.qjApplyTitle = objectTemp.getString("Title");
            //this.applyTime = stampToDate(objectTemp.getString("ApplyTime"));
            this.applyTime = objectTemp.getString("ApplyTime");
            this.approveState = objectTemp.getString("ApproveState");

        }catch (JSONException ex){
            Log.e("InfoPageNews",ex.getMessage());
        }

    }

    public String getQjApplyID() {
        return qjApplyID;
    }

    public void setQjApplyID(String qjApplyID) {
        this.qjApplyID = qjApplyID;
    }

    public String getQjApplyTitle() {
        return qjApplyTitle;
    }

    public void setQjApplyTitle(String qjApplyTitle) {
        this.qjApplyTitle = qjApplyTitle;
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
