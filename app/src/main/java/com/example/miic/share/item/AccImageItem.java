package com.example.miic.share.item;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static com.example.miic.oa.common.Setting.stampToTime;

/**
 * Created by XuKe on 2018/5/4.
 */
// {"result":true,"acc":{"FileName":"5","FilePath":"/file/PublishInfoAcc/Photo/688a9950-ae05-45c5-ab26-17537ed26917.jpg",
//"FileExt":"jpg","FileType":0,"TempPath":"/file/temp/PublishInfoAcc/Photo/688a9950-ae05-45c5-ab26-17537ed26917.jpg"}}

public class AccImageItem implements Serializable{
    private String FileName;
    private String FilePath;
    private String FileExt;
    private int FileType;
    private String TempPath;
    public AccImageItem(JSONObject AccImageObj){
        try{
            this.FileName = AccImageObj.getString("FileName");
            this.FilePath = AccImageObj.getString("FilePath");
            Log.i("AccImageItem",this.FilePath );
            this.FileExt = AccImageObj.getString("FileExt");
            this.FileType =  AccImageObj.getInt("FileType");
            this.TempPath = AccImageObj.getString("TempPath");
        }catch (JSONException ex){
            Log.i("AccImageItem","json对象构造错误");
        }
    }
    public String getFileName(){
        return FileName;
    }
    public void setFileName(String fileName){this.FileName = fileName;}
    public String getFilePath(){
        return FilePath;
    }
    public void setFilePath(String filePath){this.FilePath = filePath;}
    public String getFileExt(){
        return FileExt;
    }
    public void setFileExt(String fileExt){this.FileExt = fileExt;}
    public int getFileType(){
        return FileType;
    }
    public void setFileType(int fileType){this.FileType = fileType;}
    public String getTempPath(){return TempPath;}
    public void setTempPath(String tempPath){this.TempPath = tempPath;}
}
