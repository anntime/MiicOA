package com.example.miic.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.common.MyApplication;
import com.example.miic.share.activity.MomentsCommentActivity;
import com.example.miic.share.activity.MomentsDetailActivity;
import com.example.miic.share.activity.ShareActivity;
import com.example.miic.share.common.ShareSetting;
import com.example.miic.share.item.MomentCommentItem;
import com.example.miic.share.item.ShareMomentsCommentItem;
import com.example.miic.share.item.ShareMomentsItem;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.LvHeightUtil.setListViewHeightBasedOnChildren;

/**
 * Created by XuKe on 2018/4/24.
 */

public class ShareMomentsItemAdapter extends BaseAdapter {
    private Context context;
    private List<ShareMomentsItem> dataList;

    public ShareMomentsItemAdapter(Context context, List<ShareMomentsItem> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public ShareMomentsItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        final ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.moments_item,parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        final ShareMomentsItem momentsEntity = dataList.get(position);
        String imgPath = momentsEntity.getCreaterUrl();
        Picasso.with(context).load(imgPath).into(holder.imgIv);
        holder.nameTv.setText(momentsEntity.getCreaterName());
        holder.titTv.setText(momentsEntity.getMomentTitle());
        holder.conTv.setHtml(momentsEntity.getMomentContent(), new HtmlHttpImageGetter(holder.conTv, ShareSetting.getService()));
        holder.timeTv.setText(momentsEntity.getMomentTime());
        holder.commentNumTv.setText(momentsEntity.getCommentNum());
        holder.likeNumTv.setText(momentsEntity.getLikeNum());
        //九宫格图片显示
        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
        List<String> imageDetails = momentsEntity.getAccImgList();
        if (imageDetails != null) {
            for (String imageDetail : imageDetails) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(imageDetail);
                info.setBigImageUrl(imageDetail);
                Log.i("图片地址：",imageDetail);
                imageInfo.add(info);
            }
        }
        holder.nineGridView.setAdapter(new NineGridViewClickAdapter(context, imageInfo));
        final Logger httpLogger = Logger.getLogger(HttpURLConnection.class.getName());
        httpLogger.setLevel(Level.OFF);
        //点赞与否
        Boolean isLike = momentsEntity.getIsLike();
        if (isLike==true) {
            holder.likeIv.setImageResource(R.drawable.red_heart);
        } else {
            holder.likeIv.setImageResource(R.drawable.heart);
        }
        //点赞点击事件
        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyClickListener.onMyClick(0, position,0);
            }
        });
        //评论点击事件
        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyClickListener.onMyClick(1, position,0);
            }
        });
        //打开详情
        holder.conLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyClickListener.onMyClick(2, position,0);
            }
        });
        holder.conTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyClickListener.onMyClick(2, position,0);
            }
        });
        //评论显示
        List<ShareMomentsCommentItem> commentItemList = momentsEntity.getShareMomentsCommentItemList();
        holder.commentListView.setAdapter(momentsEntity.getShareMomentsCommentItemAdapter());
        holder.commentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("listView点击",adapterView.getCount()+"");
                Log.i("listView点击",adapterView.getAdapter().getItem(i).getClass().toString());
                ShareMomentsCommentItem comment = (ShareMomentsCommentItem)adapterView.getAdapter().getItem(i);
                //判断自己的评论还是别人的评论
                String commenterID = comment.getFromCommenterID();
                String userID=MyApplication.getUserID();
                if(commenterID.equals(userID)){
                    //是自己发表的评论，可以删除（下面弹出框：删除与取消）
                    onMyClickListener.onMyClick(3, position,i);
                }else{
                    //不是自己发表的评论，可以回复评论，
                    onMyClickListener.onMyClick(4, position,i);
                }
            }
        });
        momentsEntity.getShareMomentsCommentItemAdapter().setOnItemMyClickListener(
                new ShareMomentsCommentItemAdapter.onItemMyClickListener() {
                    @Override
                    public void onMyClick(int type, int i, List<ShareMomentsCommentItem> dataList) {
                        if (type == 0) {
                            if(i<dataList.size()){
                                //判断自己的评论还是别人的评论
                                String commenterID = dataList.get(i).getFromCommenterID();
                                String userID=MyApplication.getUserID();
                                if(commenterID.equals(userID)){
                                    //是自己发表的评论，可以删除（下面弹出框：删除与取消）
                                    onMyClickListener.onMyClick(3, position,i);
                                }else{
                                    //不是自己发表的评论，可以回复评论，
                                    onMyClickListener.onMyClick(4, position,i);
                                }
                            }

                        }
                    }
        });
        if(commentItemList.size()==0){
            holder.commentContainer.setVisibility(View.GONE);
        }else {
            holder.commentContainer.setVisibility(View.VISIBLE);
            holder.commentListView.requestFocus();
            setListViewHeightBasedOnChildren(holder.commentListView);
            momentsEntity.getShareMomentsCommentItemAdapter().notifyDataSetChanged();
        }


        return view;
    }


    public interface onItemMyClickListener {
        /**
         *监听接口：点赞、评论点击事件
         * @param type:事件类型
         * @param momentListID：朋友圈列表的位置id
         * @param commentListID：朋友圈评论列表的位置id
         */
        void onMyClick(int type, int momentListID,int commentListID);
    }

    private onItemMyClickListener onMyClickListener;

    public void setOnItemMyClickListener(onItemMyClickListener onMyClickListener) {
        this.onMyClickListener = onMyClickListener;
    }



    class ViewHolder {
        ImageView imgIv;
        TextView nameTv;
        TextView titTv;
        HtmlTextView conTv;
        // WebView webView;
        LinearLayout conLayout;
        TextView timeTv;
        TextView likeNumTv;
        TextView commentNumTv;
        ImageView likeIv;
        ImageView commentIv;
        LinearLayout likeLayout;
        LinearLayout commentLayout;
        //九宫格
        NineGridView nineGridView;
        //评论
        LinearLayout commentContainer;
        ListView commentListView;

        public ViewHolder(View view) {
            imgIv = (ImageView) view.findViewById(R.id.friend_url);
            nameTv = (TextView) view.findViewById(R.id.friend_name);
            titTv = (TextView) view.findViewById(R.id.moments_title);
            conTv = (HtmlTextView) view.findViewById(R.id.moments_con);
            conLayout = (LinearLayout) view.findViewById(R.id.con_layout);
            timeTv = (TextView) view.findViewById(R.id.moments_time);
            likeNumTv = (TextView) view.findViewById(R.id.like_num_tv);
            commentNumTv = (TextView) view.findViewById(R.id.comment_num_tv);
            likeIv = (ImageView) view.findViewById(R.id.like_iv);
            commentIv = (ImageView) view.findViewById(R.id.comment_iv);
            likeLayout = (LinearLayout) view.findViewById(R.id.like_layout);
            nineGridView = (NineGridView) view.findViewById(R.id.nineGrid);
            commentLayout = (LinearLayout) view.findViewById(R.id.comment_layout);
            commentContainer = (LinearLayout)view.findViewById(R.id.comment_container);
            commentListView = (ListView)view.findViewById(R.id.comment_list_view);

        }
    }

    /**
     * 更新评论数、点赞数，点赞状态
     *
     * @param listView
     * @param itemIndex
     */
    public void updateView(LoadMoreListView listView, int itemIndex, int headerCount) {
        //得到第一个可显示控件的位置
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
//        if (itemIndex >= visibleFirstPosi && itemIndex <= visibleLastPosi) {
            View view = listView.getChildAt(itemIndex + headerCount - visibleFirstPosi);
            ViewHolder holder = (ViewHolder) view.getTag();
            ShareMomentsItem momentsEntity = dataList.get(itemIndex);
            Boolean isLike = momentsEntity.getIsLike();
            //更新点赞状态
            if (isLike==true) {
                holder.likeIv.setImageResource(R.drawable.red_heart);
            } else {
                holder.likeIv.setImageResource(R.drawable.heart);
            }
            //更新评论数，点赞数
            holder.commentNumTv.setText(momentsEntity.getCommentNum());
            holder.likeNumTv.setText(momentsEntity.getLikeNum());
            setListViewHeightBasedOnChildren(holder.commentListView);


//        }
    }
}
