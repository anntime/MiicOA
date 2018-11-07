package com.example.miic.contractManage.item;

/**
 * Created by admin on 2018/9/7.
 */

public class ContractApprovalAccFileItem {
    private String title;
    private String link;
    private int progressNum;
    private boolean isDownload;
    public ContractApprovalAccFileItem(String title, String link,int progressNum){
        this.title = title;
        this.link = link;
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
    public int getProgress(){
        return progressNum;
    }
    public void setProgress(int progressNum){this.progressNum = progressNum;}
    public boolean getIsdownload(){
        return isDownload;
    }
    public void setIsdownload(boolean isDownload){this.isDownload = isDownload;}
}
