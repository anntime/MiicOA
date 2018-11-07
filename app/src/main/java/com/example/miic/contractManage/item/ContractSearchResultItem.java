package com.example.miic.contractManage.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.oa.common.Setting.stampToDate;

/**
 * Created by XuKe on 2018/4/8.
 */

public class ContractSearchResultItem {
    private String contractID;
    private String contractTitle;
    private String contractTime;
    private String contractStatus;

    public ContractSearchResultItem(JSONObject objectTemp ){
        try {
            this.contractID = objectTemp.getString("ID");
            this.contractTitle = objectTemp.getString("ContractName");
            //this.applyTime = stampToDate(objectTemp.getString("ApplyTime"));
            if(objectTemp.getString("SignatoryTime").contains("Date")){
                this.contractTime = stampToDate(objectTemp.getString("SignatoryTime"));
            }else{
                this.contractTime = objectTemp.getString("SignatoryTime").split(" ")[0];
            }

            this.contractStatus = objectTemp.getString("Status");

        }catch (JSONException ex){
            Log.e("ContractSearch",ex.getMessage());
        }

    }

    public String getContractID() {
        return contractID;
    }

    public void setContractID(String contractID) {
        this.contractID = contractID;
    }

    public String getContractTitle() {
        return contractTitle;
    }

    public void setContractTitle(String contractTitle) {
        this.contractTitle = contractTitle;
    }

    public String getContractTime() {
        return contractTime;
    }

    public void setApplyTime(String contractTime) {
        this.contractTime = contractTime;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }
}
