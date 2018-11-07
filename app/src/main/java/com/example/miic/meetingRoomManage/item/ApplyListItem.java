package com.example.miic.meetingRoomManage.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2018/10/9.
 */

public class ApplyListItem {
    private String id;
    private String startTime;
    private String endTime;
    private String deptName;
    private String title;
    private String userName;

    public ApplyListItem(JSONObject objectTemp) {
        try {
            this.id = objectTemp.getString("ID");
            this.startTime = objectTemp.getString("startTime");
            this.endTime = objectTemp.getString("endTime");
            this.deptName = objectTemp.getString("deptName");
            this.title = objectTemp.getString("title");
            this.userName = objectTemp.getString("userName");
        } catch (JSONException ex) {
            Log.e("ApplyListItem", ex.getMessage());
        }
    }
    public String getId(){return id;}
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getUserName(){return this.userName;}
}
