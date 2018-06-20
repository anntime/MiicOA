package com.example.miic.oa.chat.item;

import java.util.List;

/**
 * Created by XuKe on 2018/3/19.
 */

public class IMChatPageListView {
    private String deptName;
    private List<IMContactItem> contactsList;

    public IMChatPageListView(String deptName, List<IMContactItem> contactsList){
        this.contactsList = contactsList;
        this.deptName = deptName;
    }
    public void setDeptName(String deptName){this.deptName = deptName;}
    public String getDeptName(){return deptName;}
    public void setContactsList(List<IMContactItem> contactsList){this.contactsList = contactsList;}
    public List<IMContactItem> getContactsList(){return contactsList;}
}
