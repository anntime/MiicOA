package com.example.miic.oa.search.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.oa.common.Setting.stampToDate;


/**
 * Created by XuKe on 2018/1/31.
 * 新闻类，里面定义了新闻列表需要填充的数据
 */

public class SearchPageNews {
    private String newsID;
    private String title;
    private String publishTime;
    private String praiseCount;
    private String commentCount;
    private String browseCount;
    private String publishType;
    private String canComment;

    public SearchPageNews(JSONObject objectTemp){
        try{
            String newsID = objectTemp.getString("ID");
            String title = objectTemp.getString("Title");
            String columnName = objectTemp.getString("ColumnTitle");
            String publishTime =stampToDate(objectTemp.getString("PublishTime"));
            this.newsID = newsID;
            this.title = "["+columnName+"]  "+title;
            this.publishTime = publishTime;
            this.praiseCount = objectTemp.getString("PraiseCount");
            this.commentCount = objectTemp.getString("CommentCount");
            this.browseCount = objectTemp.getString("BrowseCount");
            this.publishType = objectTemp.getString("PublishType");
            this.canComment = objectTemp.getString("CanComment");
        }catch (JSONException ex){
            Log.i("SearchPageNews","json对象构造错误:"+ex.getMessage());
        }
    }
    public String getNewsID(){
        return newsID;
    }
    public void setNewsID(String newsID){this.newsID = newsID;}
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){this.newsID = title;}
    public String getPublishTime(){
        return publishTime;
    }
    public void setPublishTime(String publishTime){this.publishTime = publishTime;}
    public String getPublishType(){
        return publishType;
    }
    public void setPublishType(String publishType){this.publishType = publishType;}
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
}
