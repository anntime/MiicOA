package com.example.miic.oa.basic.item;

import com.example.miic.oa.basic.item.ContactItem;

import java.util.List;

/**
 * Created by XuKe on 2018/3/19.
 */

public class ContactsPageListView {
    private String deptName;
    private List<ContactItem> contactsList;

    public ContactsPageListView(String deptName,List<ContactItem> contactsList){
        this.contactsList = contactsList;
        this.deptName = deptName;
    }
    public void setDeptName(String deptName){this.deptName = deptName;}
    public String getDeptName(){return deptName;}
    public void setContactsList(List<ContactItem> contactsList){this.contactsList = contactsList;}
    public List<ContactItem> getContactsList(){return contactsList;}
}
