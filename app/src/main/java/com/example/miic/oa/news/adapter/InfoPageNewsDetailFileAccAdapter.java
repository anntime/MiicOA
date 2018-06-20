package com.example.miic.oa.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.base.progressbar.DownLoadProgressBar;
import com.example.miic.oa.news.item.InfoPageNewsDetailFileAcc;

import java.util.List;

/**
 * Created by XuKe on 2018/2/27.
 */

public class InfoPageNewsDetailFileAccAdapter extends BaseAdapter {
    private Context context;
    private List<InfoPageNewsDetailFileAcc> list;
    public InfoPageNewsDetailFileAccAdapter(Context context, List<InfoPageNewsDetailFileAcc> objects){
        this.context = context;
        this.list = objects;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public InfoPageNewsDetailFileAcc getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        InfoPageNewsDetailFileAcc newsDetailFileAcc = getItem(position);
        View view;
        ViewHolder holder;
        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.news_detail_file_item,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        holder.title.setText(newsDetailFileAcc.getTitle());
        holder.progress.setProgress(newsDetailFileAcc.getProgress());
        holder.fileType.setImageResource(setFileTypeImage(newsDetailFileAcc.getFileType()));

        if(newsDetailFileAcc.getIsdownload()) {
            holder.progress.setVisibility(View.VISIBLE);
            holder.progress.setProgress(newsDetailFileAcc.getProgress());
        }else {
            holder.progress.setVisibility(View.INVISIBLE);
        }

        return view;
    }
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }
    class ViewHolder{
        private TextView title;
        private DownLoadProgressBar progress;
        private ImageView fileType;
        public ViewHolder( View view){
            title = (TextView) view.findViewById(R.id.news_detail_acc_file);
            progress = (DownLoadProgressBar) view.findViewById(R.id.pb_mp);
            fileType = (ImageView)view.findViewById(R.id.news_detail_acc_file_type);
        }
    }
    private int setFileTypeImage(String fileType){

//                Word: 1,
//                Excel: 2,
//                PowerPoint: 3,
//                Pdf: 4,
//                Text: 5,
//                Accessory: 6

        int result = R.drawable.access;
        switch (fileType) {
            case "1":
                result = R.drawable.word;
                break;
            case "2":
                result = R.drawable.excel;
                break;
            case "3":
                result = R.drawable.ppt;
                break;
            case "4":
                result = R.drawable.pdf;
                break;
            case "5":
                result = R.drawable.file;
                break;
            case "6":
                result = R.drawable.zip;
                break;
            default:
                result = R.drawable.access;
                break;
        }
        return result;
    }
}
