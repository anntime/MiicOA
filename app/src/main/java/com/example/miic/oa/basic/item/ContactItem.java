package com.example.miic.oa.basic.item;

import android.widget.Toast;

import com.example.miic.common.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XuKe on 2018/3/19.
 */

public class ContactItem {
    private String userUrl;
    private String userName;
    private String userID;
    private String userTel;
    private String userEmail;

    public ContactItem(JSONObject userInfo) {
        try{
            this.userID = userInfo.getString("UserID");
            this.userName = userInfo.getString("UserName");
            this.userUrl = userInfo.getString("UserUrl");
            this.userTel = userInfo.getString("Tel");
            this.userEmail = userInfo.getString("Email");
        }catch (JSONException ex){
            Toast.makeText(MyApplication.getContext(),"JSON 解析错误",Toast.LENGTH_SHORT).show();
        }
    }


    public void setUserUrl(String userUrl){this.userUrl = userUrl;}
    public String getUserUrl(){return userUrl;}

    public void setUserName(String userName){this.userName = userName;}
    public String getUserName(){return userName;}

    public void setUserID(String userID){this.userID = userID;}
    public String getUserID(){return userID;}

    public void setUserTel(String userTel){this.userTel = userTel;}
    public String getUserTel(){return userTel;}

    public void setUserEmail(String userEmail){this.userEmail = userEmail;}
    public String getUserEmail(){return userEmail;}
}
