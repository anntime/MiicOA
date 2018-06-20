package com.example.miic.share.item;

import android.widget.Toast;

import com.example.miic.common.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.base.Pinyin.getCnASCII;
import static com.example.miic.base.Pinyin.getPinYinHeadChar;
import static com.example.miic.base.Pinyin.getPingYin;
import static java.lang.Integer.parseInt;

/**
 * Created by XuKe on 2018/5/8.
 */

public class FriendContactItem implements Comparable<FriendContactItem>{
    private String index;
    private String userID;
    private String name;
    private String url;

    public FriendContactItem(JSONObject object) {
        try {
            this.name = object.getString("AddresserName");
            this.url =  object.getString("AddresserUrl");
            this.index = getPinYinHeadChar(getPingYin(this.name)).toUpperCase().substring(0,1);
            this.userID = object.getString("AddresserID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getIndex() {
        return index;
    }
    public String getUserID() {
        return userID;
    }
    public String getName() {
        return name;
    }
    public String getUrl() { return url; }
    @Override
    public int compareTo(FriendContactItem o) {
        int i = parseInt(getCnASCII(this.getIndex())) - parseInt(getCnASCII(o.getIndex()));//先按照年龄排序

        return i;
    }
}
