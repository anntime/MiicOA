package com.example.miic.oa.news.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XuKe on 2018/2/6.
 */

public class InfoPageNewsComment {
    private String commentID;
    private String newsID;
    private String fromCommenterUrl;
    private String fromCommenterName;
    private String fromCommenterID;
    private String toCommenterName;
    private String toCommenterID;
    private String commentTime;
    private String commentContent;

    public InfoPageNewsComment(JSONObject newsCommentJson){
        try{
            this.commentID = newsCommentJson.getString("ID");
            this.newsID = newsCommentJson.getString("NewsID");
            this.fromCommenterUrl = newsCommentJson.getString("FromCommenterUrl");
            this.fromCommenterName = newsCommentJson.getString("FromCommenterName");
            this.fromCommenterID =  newsCommentJson.getString("FromCommenterID");
            this.commentTime = newsCommentJson.getString("CommentTime");

            this.commentContent = newsCommentJson.getString("Content");
            this.toCommenterName = newsCommentJson.getString("ToCommenterName");
            this.toCommenterID = newsCommentJson.getString("ToCommenterID");
        }catch (JSONException ex){
            Log.i("NewsCommentActivity","json对象构造错误");
        }
    }
    public String getCommentID(){
        return commentID;
    }
    public String getNewsID(){
        return newsID;
    }
    public String getFromCommenterUrl(){
        return fromCommenterUrl;
    }
    public String getFromCommenterName(){
        return fromCommenterName;
    }
    public String getFromCommenterID(){
        return fromCommenterID;
    }
    public String getToCommenterName(){
        return toCommenterName;
    }
    public String getToCommenterID(){
        return toCommenterID;
    }
    public String getCommentTime(){
        return commentTime;
    }
    public String getCommentContent(){
        return commentContent;
    }
}
