package com.example.miic.oa.work.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.work.item.WorkPageItem;

import java.util.List;

/**
 * Created by XuKe on 2018/2/9.
 */

public class WorkPageItemAdapter extends BaseAdapter {
    private Context context;
    private List<WorkPageItem> list;

    public WorkPageItemAdapter(Context context, List<WorkPageItem> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();//注意此处
    }

    @Override
    public WorkPageItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        final WorkPageItem columnItem = getItem(position);
        if(convertView==null){
            view=LayoutInflater.from(context).inflate(R.layout.column_child_item,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        if(columnItem.getColumnTitle()!=""){
            holder.mTextView.setText(columnItem.getColumnTitle());
        }
        WorkPageGridViewAdapter columnGridViewAdapter = new WorkPageGridViewAdapter(context,columnItem.getColumnGridView());
        holder.mGridView.setAdapter(columnGridViewAdapter);
        holder.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //获得点击的图标的ID，然后将id传递到下一页，
                String clickIconID = columnItem.getColumnID();
                Toast.makeText(MyApplication.getContext(),"待开发中……",Toast.LENGTH_SHORT).show();
//                Log.i("点击图标的ID",clickIconID);
//
//                if(clickIconID=="IM"){
////                    final String target = "testpro2"; //消息接收者ID
////                    final String appkey = "23015524"; //消息接收者appKey
////                    Intent intent = mIMKit.getChattingActivityIntent(target, appkey);
////                    context.startActivity(intent);
//                }else if(clickIconID=="address"){
//                    Intent intent=add Intent(context,AddressListActivity.class);
//                    context.startActivity(intent);
//                }else{
//                    Intent intent=add Intent(context,WorkChildActivity.class);
//                    intent.putExtra("clickIconID",clickIconID);
//                    context.startActivity(intent);
//                }


            }
        });
        return view;
    }
    class ViewHolder{
        TextView mTextView;
        GridView mGridView;
        public ViewHolder(View view){
            mTextView=(TextView)view.findViewById(R.id.cloumn_title);
            mGridView = (GridView)view.findViewById(R.id.cloumn_gridview);
        }
    }

}