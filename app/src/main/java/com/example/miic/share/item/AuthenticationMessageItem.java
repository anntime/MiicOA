package com.example.miic.share.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.oa.common.Setting.stampToTime;

/**
 * Created by XuKe on 2018/5/9.
 */

public class AuthenticationMessageItem {
    private String ApplicationID;
    private String userID;
    private String userName;
    private String userUrl;
    private String applyTime;
    private String applyMessage;
    public AuthenticationMessageItem(JSONObject obj){
        try {
            this.ApplicationID = obj.getString("ID");
            this.userID = obj.getString("UserID");
            this.userName = obj.getString("UserName");
            this.applyMessage = obj.getString("Remark");
            this.applyTime = obj.getString("ApplicationTime");
            this.userUrl = obj.getString("UserUrl");
        } catch (JSONException e) {
            Log.i("AuthenticationMessage",e.getMessage().toString());
            e.printStackTrace();
        }
    }

    public String getID(){
        return ApplicationID;
    }
    public String getUserName(){
        return userName;
    }
    public String getUserID(){
        return userID;
    }
    public String getUserUrl(){
        return userUrl;
    }
    public String getApplyTime(){
        return applyTime;
    }
    public String getApplyMessage(){
        return applyMessage;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setUserUrl(String userUrl){
        this.userUrl = userUrl;
    }
    public void setApplyTime(String applyTime){
        this.applyTime = applyTime;
    }
    public void setApplyMessage(String applyMessage){
        this.applyMessage = applyMessage;
    }
}
