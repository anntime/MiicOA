package com.example.miic.share.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.oa.common.Setting.stampToTime;

/**
 * Created by XuKe on 2018/4/26.
 */

public class MomentCommentItem {
    private String commentID;
    private String momentID;
    private String fromCommenterUrl;
    private String fromCommenterName;
    private String fromCommenterID;
    private String toCommenterName;
    private String toCommenterID;
    private String commentTime;
    private String commentContent;
    private String authorID;

    public MomentCommentItem(JSONObject MomentCommentJson,String authorID){
        try{
            this.commentID = MomentCommentJson.getString("ID");
//            this.momentID = MomentCommentJson.getString("MomentID");
            this.fromCommenterUrl = MomentCommentJson.getString("FromCommenterUrl");
            this.fromCommenterName = MomentCommentJson.getString("FromCommenterName");
            this.fromCommenterID =  MomentCommentJson.getString("FromCommenterID");
            this.commentTime = stampToTime(MomentCommentJson.getString("CommentTime"));
            this.commentContent = MomentCommentJson.getString("Content");
            this.toCommenterName = MomentCommentJson.getString("ToCommenterName");
            this.toCommenterID = MomentCommentJson.getString("ToCommenterID");
            this.authorID = authorID;
        }catch (JSONException ex){
            Log.i("MomentDetailActivity","json对象构造错误");
        }
    }
    public String getCommentID(){
        return commentID;
    }
    public String getMomentID(){
        return momentID;
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
    public String getAuthorID(){return authorID;}
    public void setAuthorID(String ID){this.authorID = ID;}
}
