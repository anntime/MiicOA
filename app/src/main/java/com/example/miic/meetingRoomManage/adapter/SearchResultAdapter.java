package com.example.miic.meetingRoomManage.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.contractManage.item.ContractListItem;
import com.example.miic.contractManage.item.ContractSearchResultItem;
import com.example.miic.meetingRoomManage.item.ApplyListItem;
import com.example.miic.meetingRoomManage.item.SearchResultItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.LvHeightUtil.setListViewHeightBasedOnChildren;

/**
 * Created by admin on 2018/10/11.
 */

public class SearchResultAdapter extends BaseAdapter {
    private static final int TYPE_CATEGORY_ITEM = 0;
    private static final int TYPE_ITEM = 1;

    private List<SearchResultItem> mListData;
    private LayoutInflater mInflater;
    public SearchResultAdapter(Context context, List<SearchResultItem> pData) {
        mListData = pData;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int count = 0;

        if (null != mListData) {
            //  所有分类中item的总和是ListVIew  Item的总个数
            for (SearchResultItem searchResultItem : mListData) {
                count += searchResultItem.getItemCount();
            }
        }

        return count;
    }
    @Override
    public Object getItem(int position) {

        // 异常情况处理
        if (null == mListData || position <  0|| position > getCount()) {
            return null;
        }

        // 同一分类内，第一个元素的索引值
        int categroyFirstIndex = 0;

        for (SearchResultItem category : mListData) {
            int size = category.getItemCount();
            // 在当前分类中的索引值
            int categoryIndex = position - categroyFirstIndex;
            // item在当前分类内
            if (categoryIndex < size) {
                return  category.getItem( categoryIndex );
            }
            // 索引移动到当前分类结尾，即下一个分类第一个元素索引
            categroyFirstIndex += size;
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        // 异常情况处理
        if (null == mListData || position <  0|| position > getCount()) {
            return TYPE_ITEM;
        }


        int categroyFirstIndex = 0;

        for (SearchResultItem category : mListData) {
            int size = category.getItemCount();
            // 在当前分类中的索引值
            int categoryIndex = position - categroyFirstIndex;
            if (categoryIndex == 0) {
                return TYPE_CATEGORY_ITEM;
            }

            categroyFirstIndex += size;
        }

        return TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_CATEGORY_ITEM:
                if (null == convertView) {
                    convertView = mInflater.inflate(R.layout.meeting_room_search_result_item, null);
                }

                TextView textView = (TextView) convertView.findViewById(R.id.meeting_room);
                String  itemValue = (String) getItem(position);
                textView.setText( itemValue );
                break;

            case TYPE_ITEM:
                ViewHolder viewHolder = null;
                if (null == convertView) {

                    convertView = mInflater.inflate(R.layout.meeting_room_apply_list_item, null);

                    viewHolder = new ViewHolder();
                    viewHolder.useTime = (TextView) convertView.findViewById(R.id.use_time);
                    viewHolder.useDept = (TextView) convertView.findViewById(R.id.dept_name);
                    viewHolder.topic = (TextView) convertView.findViewById(R.id.title);
                    viewHolder.cancelBtn = (TextView)convertView.findViewById(R.id.cancel_btn);

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                // 绑定数据
                final String item = (String) getItem(position);
                Log.i("数据：",item);
                viewHolder.useTime.setText(item.split("&")[1]+" ~ "+item.split("&")[2]);
                viewHolder.useDept.setText(item.split("&")[3]);
                viewHolder.topic.setText(item.split("&")[4]);
                //判断当前登录的人跟会议室申请的人是不是一个，决定是不是要显示这个取消按钮!!!!!!!!!!!!!!!!!!!!!!!!
                if(MyApplication.getUserName().equals(item.split("&")[5])){
                    viewHolder.cancelBtn.setVisibility(View.VISIBLE);
                    viewHolder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //点击取消事件
                            String clickID = item.split("&")[0];
                            onMyClickListener.onMyClick(position, clickID);
                        }
                    });

                }else{
                    viewHolder.cancelBtn.setVisibility(View.GONE);
                }
                break;
        }

        return convertView;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != TYPE_CATEGORY_ITEM;
    }


    private class ViewHolder {
        TextView useTime;
        TextView useDept;
        TextView topic;
        TextView cancelBtn;
    }
    public interface onItemMyClickListener {
        /**
         *监听接口
         * @param position:位置
         * @param listID： 会议预约id
         */
        void onMyClick(int position, String listID);
    }

    private onItemMyClickListener onMyClickListener;

    public void setOnItemMyClickListener(onItemMyClickListener onMyClickListener) {
        this.onMyClickListener = onMyClickListener;
    }
}
