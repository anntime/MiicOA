package com.example.miic.oa.main.item;

/**
 * Created by XuKe on 2018/1/31.
 * 新闻类，里面定义了新闻列表需要填充的数据
 */

public class MainPagePendingItem {
    private String newsID;
    private String title;
    private String publishTime;
    private int iconID;

    public MainPagePendingItem(String newsID, String title, String publishTime,int iconID){
        this.newsID = newsID;
        this.title = title;
        this.publishTime = publishTime;
        this.iconID = iconID;
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
    public int getIconID(){return iconID;}
    public void setIconID(int iconID){this.iconID = iconID;}
}
