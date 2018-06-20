package com.example.miic.share.item;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.base.Pinyin.getCnASCII;
import static com.example.miic.base.Pinyin.getPinYinHeadChar;
import static com.example.miic.base.Pinyin.getPingYin;
import static java.lang.Integer.parseInt;

/**
 * Created by admin on 2018/5/14.
 */

public class SearchUserItem {
    private String userID;
    private String name;
    private String url;
    private boolean isMyFriend;

    public SearchUserItem(JSONObject object) {
        try {
            this.name = object.getString("UserName");
            this.url =  object.getString("UserUrl");
            this.userID = object.getString("ID");
            this.isMyFriend = object.getBoolean("IsMyFriend");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserID() {
        return userID;
    }
    public String getName() {
        return name;
    }
    public String getUrl() { return url; }
    public boolean getIsMyFriend(){return isMyFriend;}
}
