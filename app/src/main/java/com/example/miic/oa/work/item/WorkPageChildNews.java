package com.example.miic.oa.work.item;

/**
 * Created by XuKe on 2018/1/31.
 * 新闻类，里面定义了新闻列表需要填充的数据
 */

public class WorkPageChildNews {
    private String newsID;
    private String title;
    private String publishTime;
    private String praiseCount;
    private String commentCount;
    private String browseCount;

    public WorkPageChildNews(String newsID, String title, String publishTime, String praiseCount, String commentCount, String browseCount){
        this.newsID = newsID;
        this.title = title;
        this.publishTime = publishTime;
        this.praiseCount =praiseCount;
        this.commentCount = commentCount;
        this.browseCount = browseCount;
    }
    public String getNewsID(){
        return newsID;
    }
    public void setNewsID(String newsID){this.newsID = newsID;}
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){this.title = title;}
    public String getPublishTime(){
        return publishTime;
    }
    public void setPublishTime(String publishTime){this.publishTime = publishTime;}
    public String getPraiseCount(){
        return praiseCount;
    }
    public void setPraiseCount(String praiseCount){
        this.praiseCount = praiseCount;
    }
    public String getCommentCount(){
        return commentCount;
    }
    public void setCommentCount(String commentCount){this.commentCount = commentCount;}
    public String getBrowseCount(){
        return browseCount;
    }
    public void setBrowseCount(String browseCount){
        this.browseCount = browseCount;
    }

}
