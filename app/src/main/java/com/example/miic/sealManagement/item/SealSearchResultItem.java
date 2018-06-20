package com.example.miic.sealManagement.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.oa.common.Setting.stampToDate;

/**
 * Created by XuKe on 2018/4/8.
 */

public class SealSearchResultItem {
    private String sealApplyID;
    private String sealApplyTitle;
    private String applyTime;
    private String approveState;

    public SealSearchResultItem(JSONObject objectTemp ){
        try {
            this.sealApplyID = objectTemp.getString("ID");
            this.sealApplyTitle = objectTemp.getString("Content");
            //this.applyTime = stampToDate(objectTemp.getString("ApplyTime"));
            this.applyTime = objectTemp.getString("UsingTime");
            this.approveState = objectTemp.getString("Status");

        }catch (JSONException ex){
            Log.e("InfoPageNews",ex.getMessage());
        }

    }

    public String getSealApplyID() {
        return sealApplyID;
    }

    public void setSealApplyID(String sealApplyID) {
        this.sealApplyID = sealApplyID;
    }

    public String getSealApplyTitle() {
        return sealApplyTitle;
    }

    public void setSealApplyTitle(String sealApplyTitle) {
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
