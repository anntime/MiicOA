package com.example.miic.contractManage.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.oa.common.Setting.stampToDate;

/**
 * Created by admin on 2018/9/11.
 */

public class ContractListItem {
    private String contractID;
    private String contractTitle;
    private String contractTime;
    private String contractUndertakeName;
    private String contractStatus;

    public ContractListItem(JSONObject objectTemp ){
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
            this.contractUndertakeName = objectTemp.getString("CreaterName");

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
    public String getContractUndertakeName() {
        return contractUndertakeName;
    }

    public void setContractUndertakeName(String contractUndertakeName) {
        this.contractUndertakeName = contractUndertakeName;
    }
}
