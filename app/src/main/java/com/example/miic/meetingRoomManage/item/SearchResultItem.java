package com.example.miic.meetingRoomManage.item;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/10/9.
 */

public class SearchResultItem {
        private String meetingRoom;
        private String meetingCount;
        private List<ApplyListItem> applyList = new ArrayList<>();

        public SearchResultItem(String meetingRoom,String meetingCount){
            this.meetingRoom = meetingRoom;
            this.meetingCount = meetingCount;
        }
        public String getMeetingRoom() {
            return meetingRoom;
        }

        public void setMeetingRoom(String meetingRoom) {
            this.meetingRoom = meetingRoom;
        }

        public String getMeetingCount() {
            return meetingCount;
        }

        public void setMeetingCount(String meetingCount) {
            this.meetingCount = meetingCount;
        }

        public void addItem(ApplyListItem applyItem){
            applyList.add(applyItem);
        }
        /**
         *  获取Item内容
         *
         * @param pPosition
         * @return
         */
        public String getItem(int pPosition) {
            // Category排在第一位
            if (pPosition == 0) {
                return meetingRoom+"("+meetingCount+")";
            } else {
                ApplyListItem item = applyList.get(pPosition - 1);
               String str =  item.getId()+"&"+item.getStartTime()+"&"+item.getEndTime()+"&"+item.getDeptName()+"&"+item.getTitle()+"&"+item.getUserName();
                return str;
            }
        }

        /**
         * 当前类别Item总数。Category也需要占用一个Item
         * @return
         */
        public int getItemCount() {
            return applyList.size() + 1;
        }
}
