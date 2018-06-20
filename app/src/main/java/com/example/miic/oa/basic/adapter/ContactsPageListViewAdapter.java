package com.example.miic.oa.basic.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.oa.basic.item.ContactItem;
import com.example.miic.oa.basic.item.ContactsPageListView;

import java.util.List;

import static com.example.miic.oa.common.LvHeightUtil.setListViewHeightBasedOnChildren;

/**
 * Created by XuKe on 2018/3/19.
 */

public class ContactsPageListViewAdapter extends BaseAdapter {
    private Context context;
    private List<ContactsPageListView> contactsPageListViewList;

    public  ContactsPageListViewAdapter(Context context,List<ContactsPageListView> contactsPageListViewList){
        this.contactsPageListViewList = contactsPageListViewList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return contactsPageListViewList.size();
    }

    @Override
    public ContactsPageListView getItem(int i) {
        return contactsPageListViewList.get(i);
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
            view = LayoutInflater.from(context).inflate(R.layout.item_contacts_container,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        ContactsPageListView contactsListView = contactsPageListViewList.get(i);
        viewHolder.deptName.setText(contactsListView.getDeptName());

        final List<ContactItem> contactItemList = contactsListView.getContactsList();
        ContactItemAdapter contactItemAdapter = new ContactItemAdapter(context,contactItemList);
        viewHolder.peopleContainer.setAdapter(contactItemAdapter);
        setListViewHeightBasedOnChildren(viewHolder.peopleContainer);
        viewHolder.peopleContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("ContactsPageAdapter","点击联系人"+contactItemList.get(i).getUserName());
            }
        });



        return view;
    }
    class ViewHolder{
        TextView deptName;
        ListView peopleContainer;
        public ViewHolder(View view){
            deptName=(TextView)view.findViewById(R.id.dept_name);
            peopleContainer = (ListView)view.findViewById(R.id.people_container);
        }
    }
}
