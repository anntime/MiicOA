package com.example.miic.share.item;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by XuKe on 2018/4/27.
 */

public class ShareMomentsCommentItem implements Serializable {
    private String commentID;
    private String fromCommenterName;
    private String fromCommenterID;
    private String toCommenterName;
    private String toCommenterID;
    private String authorID;
    private String commentContent;

    public ShareMomentsCommentItem(JSONObject MomentCommentJson,String authorID){
        try{
            this.commentID = MomentCommentJson.getString("ID");
            this.fromCommenterName = MomentCommentJson.getString("FromCommenterName");
            this.fromCommenterID =  MomentCommentJson.getString("FromCommenterID");
            this.commentContent = MomentCommentJson.getString("Content");
            this.toCommenterName = MomentCommentJson.getString("ToCommenterName");
            this.toCommenterID = MomentCommentJson.getString("ToCommenterID");
            this.authorID = authorID;
        }catch (JSONException ex){
            Log.i("ShareActivity","json对象构造错误");
        }
    }
    public String getCommentID(){
        return commentID;
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
    public String getCommentContent(){
        return commentContent;
    }
    public String getAuthorID(){return authorID;}

}
