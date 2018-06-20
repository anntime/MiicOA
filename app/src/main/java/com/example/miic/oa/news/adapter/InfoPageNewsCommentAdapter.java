package com.example.miic.oa.news.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.oa.common.roundImage.RoundImageView;
import com.example.miic.oa.news.item.InfoPageNewsComment;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by XuKe on 2018/2/6.
 */

public class InfoPageNewsCommentAdapter extends ArrayAdapter<InfoPageNewsComment> {
    private  int resourceId;
    private Context mContext;
    public InfoPageNewsCommentAdapter(Context context, int textViewResourceId, List<InfoPageNewsComment> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
        mContext = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final InfoPageNewsComment newsComment = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

        RoundImageView fromCommenterUrl = (RoundImageView) view.findViewById(R.id.from_commenter_url);
        TextView fromCommenterName = (TextView) view.findViewById(R.id.from_commenter_name);
        TextView toCommenterName =(TextView) view.findViewById(R.id.to_commenter_name);
        TextView commentTime = (TextView) view.findViewById(R.id.comment_time);
        TextView commentContent = (TextView) view.findViewById(R.id.comment_content);
        if(!newsComment.getFromCommenterUrl().equals("")){
            Picasso.with(mContext).load(newsComment.getFromCommenterUrl()).into(fromCommenterUrl);
        }

        fromCommenterName.setText(newsComment.getFromCommenterName());
        toCommenterName.setText(newsComment.getToCommenterName());
        commentTime.setText(newsComment.getCommentTime());
        commentContent.setText(Html.fromHtml(newsComment.getCommentContent(), new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Log.d("项目图片测试_source:",   source);
                int id = Integer.parseInt(source);
                Drawable drawable = mContext.getResources().getDrawable(id);//.getDrawable(id, null);
                //64*64
//                drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/5 ,
//                        drawable.getIntrinsicHeight()/5);
                //24*24
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth()  ,
                        drawable.getIntrinsicHeight() );

                //drawable = zoomDrawable(drawable, 24, 24);
                Log.d("项目图片测试_source:",   drawable+"");
                return drawable;
            }
        }, null));

        return view;
    }
    public   Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // drawable转换成bitmap
        Bitmap oldbmp = drawableToBitmap(drawable);
        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        // 设置缩放比例
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(newbmp);  //BitmapDrawable(mContext.getResources(), newbmp);
    }
    public Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
}
