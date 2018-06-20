package com.example.miic.oa.news.item;

/**
 * Created by XuKe on 2018/2/27.
 */

public class InfoPageNewsDetailFileAcc {
    private String title;
    private String link;
    private String fileType;
    private int progressNum;
    private boolean isDownload;
    public InfoPageNewsDetailFileAcc(String title, String link,String fileType,int progressNum){
        this.title = title;
        this.link = link;
        this.fileType = fileType;
        this.progressNum = progressNum;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){this.title = title;}
    public String getLink(){
        return link;
    }
    public void setLink(String link){this.link = link;}
    public String getFileType(){
        return fileType;
    }
    public void setFileType(String fileType){this.fileType = fileType;}
    public int getProgress(){
        return progressNum;
    }
    public void setProgress(int progressNum){this.progressNum = progressNum;}
    public boolean getIsdownload(){
        return isDownload;
    }
    public void setIsdownload(boolean isDownload){this.isDownload = isDownload;}
}
