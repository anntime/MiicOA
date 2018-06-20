package com.example.miic.share.item;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.share.activity.MomentsDetailActivity;
import com.example.miic.share.activity.ShareActivity;
import com.example.miic.share.adapter.ShareMomentsCommentItemAdapter;
import com.example.miic.share.common.ShareSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.Setting.stampToTime;

/**
 * Created by XuKe on 2018/4/24.
 */

public class ShareMomentsItem implements Serializable {
    //信息id
    private String momentID;
    //发布人id
    private String createrID;
    private String createrName;
    //用户头像
    private String createrUrl;
    //文章标题
    private String momentTitle;
    private String momentTime;
    //摘要
    private String momentContent;
    //图片
    private List<String> accImgList;
    //详情
    private Boolean isLike;
    private String likeNum;
    private String commentNum;
    //详情页面所需内容
    private String detailContent;
    private String commentList;
    //评论信息
    private List<ShareMomentsCommentItem> shareMomentsCommentItemList;
    private ShareMomentsCommentItemAdapter commentItemAdapter;

    public ShareMomentsItem(JSONObject objectTemp ){
        try {
            this.momentID = objectTemp.getString("ID");
            this.createrID = objectTemp.getString("CreaterID");
            this.createrName = objectTemp.getString("CreaterName");
            this.createrUrl = objectTemp.getString("CreaterUserUrl");
            this.momentTitle = objectTemp.getString("Title");
            this.momentTime = stampToTime(objectTemp.getString("CreateTime"));
            //摘要
            this.momentContent = objectTemp.getString("Content");
            //附件：短文有图片，文件附件；长篇有文件附件
            this.accImgList = new ArrayList<>();
            JSONArray accTempArr = objectTemp.getJSONArray("AccInfos");
            List<String> tempList = new ArrayList<>();
            Log.i("九宫格图片1：",accTempArr.toString());
            if(accTempArr!=null){
                for (int i=0;i<accTempArr.length();i++){
                    JSONObject obj = (JSONObject) accTempArr.get(i);
                    if(obj.getString("FileType").equals("0")){
                        //附件是图片
                        tempList.add(ShareSetting.getService()+obj.getString("FilePath"));
                        Log.i("九宫格图片2：",obj.getString("FilePath"));
                    }
                }
                this.accImgList.addAll(tempList);
            }
            this.isLike = objectTemp.getBoolean("IsPraise");
            this.likeNum = objectTemp.getString("PraiseNum");
            this.commentNum = objectTemp.getString("CommentNum");
            //新闻详情
            this.detailContent = objectTemp.getString("DetailContent");
            this.commentList = objectTemp.getJSONArray("CommentList").toString();
            JSONArray tempArr = new JSONArray(this.commentList);
            List<ShareMomentsCommentItem> temp = new ArrayList<>();
            String authorID = createrID;
            for (int i=0;i<tempArr.length();i++){
                if(!tempArr.getJSONObject(i).isNull("ID")){
                    temp.add(new ShareMomentsCommentItem(tempArr.getJSONObject(i),authorID));
                }
            }
            this.shareMomentsCommentItemList=temp;
            this.commentItemAdapter = new ShareMomentsCommentItemAdapter(MyApplication.getContext(),this.shareMomentsCommentItemList);
        }catch (JSONException ex){
            Log.e("InfoPageNews",ex.getMessage());
        }

    }

    public String getMomentID(){
        return momentID;
    }
    public String getCreaterID(){
        return createrID;
    }
    public String getCreaterName(){
        return createrName;
    }
    public String getCreaterUrl(){
        return createrUrl;
    }
    public String getMomentTitle(){
        return momentTitle;
    }
    public String getMomentTime(){
        return momentTime;
    }
    public String getMomentContent(){
        return momentContent;
    }
    public Boolean getIsLike(){return isLike;}
    public String getLikeNum(){return likeNum;}
    public String getCommentNum(){return commentNum;}
    public String getDetailContent(){return detailContent;}
    public String getCommentList(){return commentList;}
    public List<ShareMomentsCommentItem> getShareMomentsCommentItemList(){return shareMomentsCommentItemList;}
    public ShareMomentsCommentItemAdapter getShareMomentsCommentItemAdapter(){return commentItemAdapter;}
    public List<String> getAccImgList(){return accImgList;}
    public void setIsLike(Boolean like){this.isLike=like;}
    public void setLikeNum(String num){this.likeNum=num;}
    public void setCommentNum(String num){this.commentNum=num;}
    public void setCommentList(String list){this.commentList = list;}
    public void setShareMomentsCommentItemList(List<ShareMomentsCommentItem> list){  this.shareMomentsCommentItemList = list;}
    public void setShareMomentsCommentItemAdapter(ShareMomentsCommentItemAdapter adapter){this.commentItemAdapter=adapter;}
    public void setAccImgList(List<String> list){this.accImgList=list;}
}
