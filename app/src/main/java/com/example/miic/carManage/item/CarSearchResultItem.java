package com.example.miic.carManage.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.oa.common.Setting.stampToDate;

/**
 * Created by XuKe on 2018/4/8.
 */

public class CarSearchResultItem {
    private String carApplyID;
    private String carTravelWay;
    private String applyTime;
    private String approveState;

    public CarSearchResultItem(JSONObject objectTemp ){
        try {
            this.carApplyID = objectTemp.getString("ID");
            this.carTravelWay = objectTemp.getString("TravelWay");
            //this.applyTime = stampToDate(objectTemp.getString("ApplyTime"));
            this.applyTime = stampToDate(objectTemp.getString("BeginTime"));
            this.approveState = objectTemp.getString("Status");

        }catch (JSONException ex){
            Log.e("InfoPageNews",ex.getMessage());
        }

    }

    public String getCarApplyID() {
        return carApplyID;
    }

    public void setCarApplyID(String carApplyID) {
        this.carApplyID = carApplyID;
    }

    public String getCarTravelWay() {
        return carTravelWay;
    }

    public void setCarTravelWay(String carTravelWay) {
        this.carTravelWay = carTravelWay;
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
