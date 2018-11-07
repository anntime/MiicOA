package com.example.miic.oa.main.item;

import android.util.Log;

import com.example.miic.R;
import com.example.miic.oa.common.EnumMessageType;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.miic.oa.common.Setting.stampToTime;
import static java.lang.Integer.parseInt;

/**
 * Created by XuKe on 2018/1/31.
 * 新闻类，里面定义了新闻列表需要填充的数据
 */

public class MainPagePendingItem {
    private String messageID;
    private int messageType;
    private String title;
    private String publishTime;
    private String messageTip;
    private int iconID;
    private String contentID;

    public MainPagePendingItem(JSONObject jsonObj){
                try{
                    this.messageID = jsonObj.getString("ID");
                    this.messageType = parseInt(jsonObj.getString("MessageType"));
                    this.title = jsonObj.getString("Title");
                    this.publishTime = stampToTime(jsonObj.getString("MessageTime"));
                    this.contentID = jsonObj.getString("ContentID");
                    this.messageTip = jsonObj.getString("MessageTip");
                    switch (this.messageType){
                        //评论
                        case 0:
                            this.iconID = R.drawable.comment;
                            break;
                        //@评论
                        case 1:
                            this.iconID = R.drawable.comment;
                            break;
                        //赞
                        case 2:
                            this.iconID =  R.drawable.heart;
                            break;
                        //踩
                        case 3:
                            this.iconID =  R.drawable.heart;
                            break;
                        //欢迎
                        case 4:
                            this.iconID =  R.drawable.heart;
                            break;
                        //新闻审批
                        case 6:
                            this.iconID =  R.drawable.pen;
                            break;
                        //用印审批
                        case 7:
                            this.iconID = R.drawable.pen;
                            break;
                        //合同审批
                        case 8:
                            this.iconID = R.drawable.pen;
                            break;
                        //用车审批
                        case 9:
                            this.iconID = R.drawable.pen;
                            break;
                    }

                }catch (JSONException ex){
                    Log.i("MainPagePendingItem","解析错误");
                }

    }
    public String getMessageID(){
        return messageID;
    }
    public void setMessageID(String messageID){this.messageID = messageID;}
    public String getContentID(){
        return contentID;
    }
    public String getMessageTip(){
        return messageTip;
    }
    public void setContentID(String contentID){this.contentID = contentID;}
    public int getMessageType(){
        return messageType;
    }
    public String getMessageTypeStr(){
        return EnumMessageType.getName(messageType);
    }
    public void setMessageType(int messageType){this.messageType = messageType;}
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){this.title = title;}
    public String getPublishTime(){
        return publishTime;
    }
    public void setPublishTime(String publishTime){this.publishTime = publishTime;}
    public int getIconID(){return iconID;}
    public void setIconID(int iconID){this.iconID = iconID;}
}
