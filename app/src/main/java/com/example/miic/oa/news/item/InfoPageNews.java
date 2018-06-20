package com.example.miic.oa.news.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.oa.common.Setting.stampToDate;


/**
 * Created by XuKe on 2018/1/31.
 * 新闻类，里面定义了新闻列表需要填充的数据
 */

public class InfoPageNews {
    private String newsID;
    private String title;
    private String publishTime;
    private String praiseCount;
    private String commentCount;
    private String browseCount;
    private String publishType;
    private String canComment;

    public InfoPageNews(JSONObject objectTemp ){
        try {
            this.newsID = objectTemp.getString("ID");
            this.title = objectTemp.getString("Title");
            this.publishTime = stampToDate(objectTemp.getString("PublishTime"));
            this.praiseCount = objectTemp.getString("PraiseCount");
            this.commentCount = objectTemp.getString("CommentCount");
            this.browseCount = objectTemp.getString("BrowseCount");
            this.publishType = objectTemp.getString("PublishType");
            this.canComment = objectTemp.getString("CanComment");
        }catch (JSONException ex){
            Log.e("InfoPageNews",ex.getMessage());
        }

    }
    public String getNewsID(){
        return newsID;
    }
    public String getTitle(){
        return title;
    }
    public String getPublishTime(){
        return publishTime;
    }
    public String getPraiseCount(){
        return praiseCount;
    }
    public String getCommentCount(){
        return commentCount;
    }
    public String getCanComment(){
        return canComment;
    }
    public String getBrowseCount(){
        return browseCount;
    }

    public String getPublishType(){return publishType;}
    public void setPublishType(String publishType){this.publishType = publishType;}
}
