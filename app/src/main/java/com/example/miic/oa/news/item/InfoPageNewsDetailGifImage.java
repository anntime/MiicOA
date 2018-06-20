package com.example.miic.oa.news.item;

/**
 * Created by XuKe on 2018/2/27.
 */

public class InfoPageNewsDetailGifImage {
    private String title;
    private String link;
    public InfoPageNewsDetailGifImage(String title, String link){
        this.title = title;
        this.link = link;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){this.title = title;}
    public String getLink(){
        return link;
    }
    public void setLink(String link){this.link = link;}
}
