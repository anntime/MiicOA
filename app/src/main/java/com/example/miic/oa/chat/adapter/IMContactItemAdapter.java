package com.example.miic.oa.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.oa.chat.item.IMContactItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by XuKe on 2018/3/19.
 */

public class IMContactItemAdapter extends BaseAdapter {

    private Context context;
    private List<IMContactItem> contactItemList;

    public IMContactItemAdapter(Context context, List<IMContactItem> contactItemList) {
        this.contactItemList = contactItemList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return contactItemList.size();
    }

    @Override
    public IMContactItem getItem(int i) {
        return contactItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.contacts_people_item,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        IMContactItem contactItem = contactItemList.get(i);
        viewHolder.name.setText(contactItem.getUserName());
        if(!contactItem.getUserUrl().equals("")){
            Picasso.with(context).load(contactItem.getUserUrl()).into(viewHolder.mImageView);
        }
        viewHolder.email.setText(contactItem.getUserEmail());
        viewHolder.tel.setText(contactItem.getUserTel());

        return view;
    }
    class ViewHolder{
        TextView name;
        ImageView mImageView;
        TextView tel;
        TextView email;
        public ViewHolder(View view){
            name=(TextView)view.findViewById(R.id.tv_name);
            mImageView = (ImageView)view.findViewById(R.id.iv_avatar);
            tel = (TextView)view.findViewById(R.id.tv_phone);
            email = (TextView)view.findViewById(R.id.tv_email);
        }
    }
}
