/*
 * Copyright [2017] [Clayman Twinkle]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.miic.base.loadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * ClassName: LoadMoreListView
 *
 * @author kesar
 * @Description: 加载更多地listview
 * @date 2015-11-16
 */
public class LoadMoreListView extends ListView{
    private OnLoadMoreScrollListener mScrollListener;

    public LoadMoreListView(Context context) {
        super(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFooterView(View footerView) {
        this.mScrollListener = new OnLoadMoreScrollListener(this, footerView);
        setOnScrollListener(mScrollListener);
    }

    public void setFooterView(View footerView, OnLoadMoreScrollListener.OnLoadMoreStateListener onLoadMoreStateListener) {
        setFooterView(footerView);
        this.mScrollListener.setOnLoadMoreStateListener(onLoadMoreStateListener);
    }

    public void setOnLoadMoreStateListener(OnLoadMoreScrollListener.OnLoadMoreStateListener onLoadMoreStateListener) {
        if (mScrollListener != null) {
            this.mScrollListener.setOnLoadMoreStateListener(onLoadMoreStateListener);
        }
    }

    public void setEnd(boolean isEnd) {
        if (mScrollListener != null) {
            mScrollListener.setEnd(isEnd);
        }
    }
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}