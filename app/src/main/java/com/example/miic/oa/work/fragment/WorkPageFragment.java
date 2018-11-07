package com.example.miic.oa.work.fragment;


import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.miic.R;
import com.example.miic.oa.work.item.WorkPageGridView;
import com.example.miic.oa.work.item.WorkPageItem;
import com.example.miic.oa.work.adapter.WorkPageItemAdapter;
import com.example.miic.common.MyApplication;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("NewApi")
public class WorkPageFragment extends Fragment {

    private ListView columnListView;
    private List<WorkPageItem> columnItemList;


    private Context mContext;
    private View rootView;
    public WorkPageFragment() {
        // Required empty public constructor
        this.mContext = MyApplication.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_column_page, container, false);
        initDatas();
        columnListView = (ListView)rootView.findViewById(R.id.column_list_view);
        WorkPageItemAdapter columnItemAdapter=new WorkPageItemAdapter(getActivity(),columnItemList);
        columnListView.setAdapter(columnItemAdapter);
        return rootView;
    }
    //初始化
    private void initDatas() {
        //初始化icon
        columnItemList =new ArrayList<>();
        List<WorkPageItem>  columnItemListTemp=new ArrayList<WorkPageItem>();
        List<WorkPageGridView> columnGridViewList1 = new ArrayList<WorkPageGridView>();
        columnGridViewList1.add(new WorkPageGridView(R.drawable.pinboard,"用印管理","sealManage"));
        columnGridViewList1.add(new WorkPageGridView(R.drawable.blogger,"合同管理","contractManage"));
        columnGridViewList1.add(new WorkPageGridView(R.drawable.car,"车辆管理","carManage"));
        columnGridViewList1.add(new WorkPageGridView(R.drawable.readability,"会议室管理","meetingRoomManage"));
         columnItemList.add(new WorkPageItem("日常管理",columnGridViewList1,"001"));
        List<WorkPageGridView> columnGridViewList2 = new ArrayList<WorkPageGridView>();
        columnGridViewList2.add(new WorkPageGridView(R.drawable.location,"请假管理","qjManage"));
        columnGridViewList2.add(new WorkPageGridView(R.drawable.stackoverflow," 待办事宜","IconID"));
        columnGridViewList2.add(new WorkPageGridView(R.drawable.myspace,"已办事宜","IconID"));
        columnGridViewList2.add(new WorkPageGridView(R.drawable.foresquare," 办结事宜\n","IconID"));
        columnItemList.add(new WorkPageItem("工作流程",columnGridViewList2,"002"));
        List<WorkPageGridView> columnGridViewList3 = new ArrayList<WorkPageGridView>();
        columnGridViewList3.add(new WorkPageGridView(R.drawable.imessage,"员工圈子","share"));
        columnItemList.add(new WorkPageItem("沟通交流",columnGridViewList3,"003"));
        List<WorkPageGridView> columnGridViewList4 = new ArrayList<WorkPageGridView>();
        columnGridViewList4.add(new WorkPageGridView(R.drawable.podcast,"在岗职工信息","IconID"));
        columnGridViewList4.add(new WorkPageGridView(R.drawable.whatsapp,"中心干部手机","IconID"));
        columnItemList.add(new WorkPageItem("常用信息",columnGridViewList4,"001"));
    }

}
