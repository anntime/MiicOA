package com.example.miic.share.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.common.MyApplication;
import com.example.miic.share.item.FriendContactItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by XuKe on 2018/5/8.
 */

public class FriendContactAdapter extends RecyclerView.Adapter<FriendContactAdapter.ContactsViewHolder> {
    private List<FriendContactItem> contacts;
    private int layoutId;

    public FriendContactAdapter(List<FriendContactItem> contacts, int layoutId) {
        this.contacts = contacts;
        this.layoutId = layoutId;
    }

    @Override
    public FriendContactAdapter.ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutId, null);
        return new FriendContactAdapter.ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendContactAdapter.ContactsViewHolder holder, int position) {
        FriendContactItem contact = contacts.get(position);
        if (position == 0 || !contacts.get(position-1).getIndex().equals(contact.getIndex())) {
            holder.tvIndex.setVisibility(View.VISIBLE);
            holder.tvIndex.setText(contact.getIndex());
        } else {
            holder.tvIndex.setVisibility(View.GONE);
        }
        holder.tvName.setText(contact.getName());
        if(!contact.getUrl().equals("")){
            Picasso.with(MyApplication.getContext()).load(contact.getUrl()).into(holder.ivAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {
        public TextView tvIndex;
        public ImageView ivAvatar;
        public TextView tvName;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            tvIndex = (TextView) itemView.findViewById(R.id.tv_index);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
